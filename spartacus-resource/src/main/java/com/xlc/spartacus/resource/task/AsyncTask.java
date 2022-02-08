package com.xlc.spartacus.resource.task;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.resource.constant.ResourceConstant;
import com.xlc.spartacus.resource.config.BeanConfig;
import com.xlc.spartacus.resource.config.CommonProperties;
import com.xlc.spartacus.resource.mapper.CosResourceMapper;
import com.xlc.spartacus.resource.pojo.CosResource;
import com.xlc.spartacus.resource.pojo.NoticeMessage;
import com.xlc.spartacus.resource.pojo.WordFrequency;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.exception.MultiObjectDeleteException.DeleteError;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.DeleteObjectsRequest.KeyVersion;
import com.qcloud.cos.model.DeleteObjectsResult.DeletedObject;
import com.xlc.spartacus.resource.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 异步任务
 *
 * @author xlc, since 2021
 */
@Component
public class AsyncTask {
	private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

	@Resource
	BeanConfig beanConfig;

	@Resource
	private CommonProperties commonProperties;

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	@Resource
	private CosResourceMapper cosResourceMapper;

	@Resource
	PlatformTransactionManager dataSourceTransactionManager;

	@Resource
	private COSClient cosClient;

	@Resource
	private WebSocketService webSocketService;


	@Async("myAsync")
	public void sendFileReadStatusChangeNotice(BaseResponse payload) {
		NoticeMessage<BaseResponse> noticeMessage = new NoticeMessage<>();
		noticeMessage.setFrom("spartacus-resource");
		noticeMessage.setTo("spartacus-friday");
		noticeMessage.setPayload(payload);
		noticeMessage.setTimestamp(System.currentTimeMillis());

		try {
			webSocketService.sendFileReadStatusChangeNotice(noticeMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/////////////////////ES////////////////////////
	@Async("myAsync")
	public void syncDataFromCosToMysql() {
		logger.info("COS-MYSQL全量同步数据开始！");

		try {
			// 0、删除MYSQL（太粗暴了，有待优化）
			cosResourceMapper.deleteAll();

			String bucketName = commonProperties.getBucketName();
			String bucketRegion = commonProperties.getBucketRegion();

			Integer bucketAclFlag = cosClient.getBucketAcl(bucketName).getGrantsAsList().size();

			ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
			listObjectsRequest.setBucketName(bucketName);

			for(String rootDirPath : commonProperties.getResourceRootDirs().split(",")) {
				List<CosResource> dirList = new ArrayList<>();
				Stack<CosResource> dirStack = new Stack<>();

				// 1、拿到所有目录（包括递归子目录）
				CosResource resource = new CosResource();
				resource.setId(Snowflake.generateId());
				resource.setParentId(null);
				resource.setKey(rootDirPath);
				resource.setRootDirPath(null);
//				resource.setParentDirPath(null);
				resource.setFileName(rootDirPath.substring(0, rootDirPath.length() - 1));
				resource.setRegion(bucketRegion);
				resource.setBucketName(bucketName);
				resource.setStatus(0);
				resource.setCosType(1); //目录

				AccessControlList accessCtrlList = cosClient.getObjectAcl(bucketName, rootDirPath);
				if (accessCtrlList.isExistDefaultAcl()) { //继承权限，继承的是存储桶的权限
					resource.setAclFlag(bucketAclFlag);
				} else {
					resource.setAclFlag(accessCtrlList.getGrantsAsList().size());
				}

				resource.setLastModified(new Date());

				dirList.add(resource);
				dirStack.push(resource);

				while (!dirStack.isEmpty()) {
					CosResource pop = dirStack.pop();
					String prefix = pop.getKey();
					Long parentId = pop.getId();

					listObjectsRequest.setPrefix(prefix);
					listObjectsRequest.setDelimiter("/");
					listObjectsRequest.setMaxKeys(1000);

					ObjectListing objectListing = null;
					do {
						objectListing = cosClient.listObjects(listObjectsRequest);
						// common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
						List<String> commonPrefixs = objectListing.getCommonPrefixes();
						for (String subDirPath : commonPrefixs) {
							CosResource resource1 = new CosResource();
							resource1.setId(Snowflake.generateId());
							resource1.setParentId(parentId);
							resource1.setKey(subDirPath);

							resource1.setRootDirPath(rootDirPath);
//							resource.setParentDirPath(prefix);

							String[] paths = subDirPath.split("/");
							String fileName = paths[paths.length - 1];
							resource1.setFileName(fileName);

							resource1.setRegion(bucketRegion);
							resource1.setBucketName(bucketName);
							resource1.setStatus(0);
							resource1.setCosType(1);

							AccessControlList accessCtrlList1 = cosClient.getObjectAcl(bucketName, subDirPath);
							if (accessCtrlList1.isExistDefaultAcl()) { //继承权限，继承的是存储桶的权限
								resource1.setAclFlag(bucketAclFlag);
							} else {
								resource1.setAclFlag(accessCtrlList1.getGrantsAsList().size());
							}

							resource1.setLastModified(new Date());

							dirList.add(resource1);
							dirStack.push(resource1);
						}

						String nextMarker = objectListing.getNextMarker();
						listObjectsRequest.setMarker(nextMarker);
					} while (objectListing.isTruncated());
				}

				// 2、拿到各个目录下所有直接cos实例对象(所有的object)
				for (CosResource dir : dirList) {
					String prefix = dir.getKey();
					Long parentId = dir.getId();

					listObjectsRequest.setPrefix(prefix);
					listObjectsRequest.setMaxKeys(1000);

					List<CosResource> dbObjList = new ArrayList<>();

					ObjectListing objectListing = null;
					do {
						objectListing = cosClient.listObjects(listObjectsRequest);
						List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
						for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
							String key = cosObjectSummary.getKey();

							if(!key.endsWith("/")) {
								CosResource resource1 = new CosResource();
								resource1.setId(Snowflake.generateId());
								resource1.setParentId(parentId);
								resource1.setKey(key);
								resource1.setRootDirPath(rootDirPath);
//								resource.setParentDirPath(prefix);

								resource1.setFileName(key.substring(key.lastIndexOf("/") + 1));
								resource1.setRegion(bucketRegion);
								resource1.setBucketName(bucketName);
								resource1.setStatus(0);
								resource1.setCosType(0);
								resource1.setRootDirPath(rootDirPath);
								resource1.setContentType(cosClient.getObjectMetadata(bucketName, key).getContentType());

								AccessControlList accessCtrlList1 = cosClient.getObjectAcl(bucketName, key);
								if (accessCtrlList1.isExistDefaultAcl()) { //继承权限，继承的是存储桶的权限
									resource1.setAclFlag(bucketAclFlag);
								} else {
									resource1.setAclFlag(accessCtrlList1.getGrantsAsList().size());
								}

								resource1.setLastModified(new Date());

								dbObjList.add(resource1);
							}
						}
						String nextMarker = objectListing.getNextMarker();
						listObjectsRequest.setMarker(nextMarker);
					} while (objectListing.isTruncated());

					dbObjList.add(dir);

					//设置直接上级父目录
					dbObjList.forEach(item -> {
						if(!commonProperties.getResourceRootDirs().contains(item.getKey())) {
							String key = item.getKey();
							if(key.endsWith("/")) {
								item.setParentDirPath(key.substring(0, key.lastIndexOf("/", key.length()-2)));
							} else {
								item.setParentDirPath(key.substring(0, key.lastIndexOf("/")+1));
							}
						} else {
							item.setParentDirPath(null);
						}
					});

					// 3、同步到MYSQL
					cosResourceMapper.saveAll(dbObjList);
				}
			}

			logger.info("COS-MYSQL全量同步数据完成！");

		} catch (Exception e) {
			logger.error("COS-MYSQL全量同步数据发生异常！", e);
		}
	}



    /////////////////////COS///////////////////////
	@Async("myAsync")
	public void rename(String key, String newFileName, String eventId) {
		try {
			String bucketName = commonProperties.getBucketName();

			//1、复制
			String srcKey = key;
			String destKey = key.substring(0, key.lastIndexOf("/") + 1) + newFileName;
			try {
				CopyObjectResult copyObjectResult = cosClient.copyObject(bucketName, srcKey, bucketName, destKey);
				String eTrag = copyObjectResult.getETag();
			} catch (Exception e) {
				logger.error("rename->重命名失败，请确认以下COS对象的key（修改成："+destKey+"）是否修改成功：" + key, e);
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1, key));
				return;
			}

			//2、cos
			DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
			ArrayList<KeyVersion> keyList = new ArrayList<>();
			keyList.add(new KeyVersion(key));
			deleteObjectsRequest.setKeys(keyList);

			List<DeletedObject> deleteObjects = new ArrayList<>();
			List<DeleteError> deleteErrors = new ArrayList<>();
			List<String> deletedErrorKeys = new ArrayList<>();
			try {
				DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
				deleteObjects = deleteObjectsResult.getDeletedObjects();
			} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
				deleteObjects = mde.getDeletedObjects();
				deleteErrors = mde.getErrors();
				deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
				logger.error("batchMove->批量删除失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
			} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
				logger.error("batchMove->批量删除失败，可能是连接问题！", e);
			}

			//3、筛选出成功删除的key
			List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());

			//4、mysql
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
			try {
				Map<String, Object> param = new HashMap<>();
				param.put("key", srcKey);
				param.put("newKey", destKey);
				param.put("newFileName", newFileName);
				cosResourceMapper.renameByKey(param);
				dataSourceTransactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error("rename->重命名失败，请确认以下COS对象的key（修改成："+destKey+"）是否修改成功：" + key, e);
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_2, RespConstant.MSG_2, key));
				return;
			}

		} catch (Exception e) {
			logger.error("rename->重命名失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1, key));
			return;

		}
		this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0));
	}

	@Async("myAsync")
	public void batchMove(String keysStr, String destDirPath, String eventId) {
		try {
			String bucketName = commonProperties.getBucketName();

			//1、COS-复制
			List<String> keys = Arrays.asList(keysStr.split(","));
			List<String> copiedKeys = new ArrayList<>();
			List<Map<String, Object>> srcDestList = new ArrayList<>();

			String targetRootDirPath = destDirPath.substring(0, destDirPath.indexOf("/") + 1);

			for(String key : keys) {
				String srcKey = key;
				String destKey = destDirPath + key.substring(key.lastIndexOf("/") + 1);
				if(!srcKey.equals(destKey)) {
					try {
						CopyObjectResult copyObjectResult = cosClient.copyObject(bucketName, srcKey, bucketName, destKey);
						String eTrag = copyObjectResult.getETag();

						copiedKeys.add(srcKey);

						// 准备修改MYSQL的参数
						Map<String, Object> map = new HashMap<>();
						map.put("key", srcKey);
						map.put("newKey", destKey);
						map.put("newRootDirPath", targetRootDirPath);
						map.put("newParentDirPath", destDirPath);
						srcDestList.add(map);
					} catch (CosServiceException e) {
						e.printStackTrace();
					} catch (CosClientException e) {
						e.printStackTrace();
					}
				}
			}

			//2、COS-删除
			DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
			ArrayList<KeyVersion> keyList = new ArrayList<>();
			copiedKeys.forEach(key -> keyList.add(new KeyVersion(key)));
			deleteObjectsRequest.setKeys(keyList);

			List<DeletedObject> deleteObjects = new ArrayList<>();
			List<DeleteError> deleteErrors = new ArrayList<>();
			List<String> deletedErrorKeys = new ArrayList<>();
			try {
				DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
				deleteObjects = deleteObjectsResult.getDeletedObjects();
			} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
				deleteObjects = mde.getDeletedObjects();
				deleteErrors = mde.getErrors();
				deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
				logger.error("batchMove->批量删除失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
			} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
				logger.error("batchMove->批量删除失败，可能是连接问题！", e);
			}

			//3、加一层保险，确保COS云端删除干净
			List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
			for(int i=0; i< srcDestList.size(); i++) {
				if(!deletedKeys.contains(srcDestList.get(i).get("key"))) {
					cosClient.deleteObject(bucketName, (String) srcDestList.get(i).get("key"));
//					srcDestList.remove(i);
				}
			}

			//4、mysql
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
			try {
				cosResourceMapper.batchMove(srcDestList);
				dataSourceTransactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error("batchMove->批量修改mysql中数据失败，请确认以下COS对象的key前缀（修改成："+destDirPath+"）是否修改成功：" + copiedKeys, e);
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_2, RespConstant.MSG_2, copiedKeys));
				return;
			}

		} catch (Exception e) {
			logger.error("batchMove->批量移动对象失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
			return;
		}
		this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0));
	}

	@Async("myAsync")
	public void batchSetObjectAcl(String keysStr, Integer aclFlag, String eventId) {
		String bucketName = null;
		boolean flag = false;
		try {
			List<String> keys = Arrays.asList(keysStr.split(","));
			List<String> updateKeys = new ArrayList<>();

			//cos
			try {
				bucketName = commonProperties.getBucketName();
				for (String key : keys) {
					cosClient.setObjectAcl(bucketName, key, ResourceConstant.ACL_MAP.get(aclFlag));
					updateKeys.add(key);
				}
			} catch (Exception e) {
				logger.error("修改对象权限第1步就失败了：修改COS对象权限失败！", e);
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
				return;
			}

			//mysql
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
			try {
				Map<String, Object> param = new HashMap<>();
				param.put("aclFlag", aclFlag);
				param.put("updateKeys", updateKeys);
				cosResourceMapper.batchSetAclFlag(param);
				dataSourceTransactionManager.commit(transactionStatus); //立马提交，同步到数据库中
			} catch (Exception e) {
				logger.error("修改对象权限第2步就失败了：同步信息到mysql失败！", e);
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_2, RespConstant.MSG_2));
				return;
			}

		} catch (Exception e) {
			this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
			return;
		}
		this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0));
	}

	@Async("myAsync")
	public void batchDelete(String keysStr, String eventId) {
		try {
			String bucketName = commonProperties.getBucketName();

			List<String> keys = Arrays.asList(keysStr.split(","));
			List<CosResource> cosResources = cosResourceMapper.findAllByKeyIn(keys);

			if(cosResources != null && cosResources.size() > 0) {
				//cos
				//设置要删除的key列表, 最多一次删除1000个
				DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
				ArrayList<KeyVersion> keyList = new ArrayList<>();
				cosResources.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
				deleteObjectsRequest.setKeys(keyList);

				List<DeletedObject> deleteObjects = new ArrayList<>();
				List<DeleteError> deleteErrors = new ArrayList<>();
				List<String> deletedErrorKeys = new ArrayList<>();
				try {
					DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
					deleteObjects = deleteObjectsResult.getDeletedObjects();
				} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
					deleteObjects = mde.getDeletedObjects();
					deleteErrors = mde.getErrors();
					deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
					logger.error("deleteCosObjects->批量删除对象第1步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
				} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
					logger.error("deleteCosObjects->批量删除对象第1步就失败了，可能是连接问题！", e);
				}

				//mysql
				List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
				List<Long> ids1 = cosResources.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
				//手动开启事务，不要用注解
				TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
				try {
					if(ids1 != null && ids1.size() > 0) cosResourceMapper.deleteCosResourcesByIdIn(ids1);
					dataSourceTransactionManager.commit(transactionStatus);
				} catch (Exception e) {
					logger.error("deleteCosObjects->批量删除对象第2步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_2, RespConstant.MSG_2, deletedKeys));
					return;
				}
			}

		} catch (Exception e) {
			logger.error("deleteCosObjects->批量删除对象失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
			return;
		}
		this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0));
	}

	@Async("myAsync")
	public void refreshTagsWordFrequencyCount() {
		List<WordFrequency> tagsList = new ArrayList<>();
		try {
			redisTemplate.delete("tag-cloud");
			List<String> allTags = cosResourceMapper.findAllTags();
			Map<String, Integer> tagWeightMap = new HashMap<>();
			for (String everyTags : allTags) {
				String[] tags = everyTags.split(",");
				for (String tag : tags) {
					if(tagWeightMap.containsKey(tag)) {
						tagWeightMap.put(tag, tagWeightMap.get(tag) + 1);
					} else {
						tagWeightMap.put(tag, 1);
					}
				}
			}
			//装载、排序、截取前30个
			tagsList = tagWeightMap.entrySet().stream().map(en -> WordFrequency.builder().text(en.getKey()).weight(en.getValue()).build()).collect(Collectors.toList());
			Collections.sort(tagsList, (o1, o2) -> o2.getWeight() - o1.getWeight());
			tagsList = tagsList.subList(0, tagsList.size() >= 30 ? 30 : tagsList.size());
			if(tagsList.size()>0) {
				redisTemplate.opsForValue().set(ResourceConstant.COS_RESOURCE_TAGS, JSON.toJSONString(tagsList));
			}

		} catch (Exception e) {
			logger.error("refreshTagsWordFrequencyCount->刷新标签的词频统计结果失败：", e);
		}
	}

	@Async("myAsync")
	public void deleteCosObject(String bucketName, String key) {
		try {
			cosClient.deleteObject(bucketName, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async("myAsync")
	public void deleteCosDirectory(String targetDirPath, String eventId) {
		try {
			String bucketName = commonProperties.getBucketName();

			//1、先删除文件对象
			int pageSize = 500;

			int currentPage = 1;
			Map<String, Object> param = new HashMap<>();
			param.put("key", targetDirPath);
			param.put("cosType", 0);
			param.put("startIndex", (currentPage-1) * pageSize);
			param.put("pageSize", pageSize);
			int total = cosResourceMapper.countByKeyStartsWithAndCosType(param);

			do {
				List<CosResource> resources = cosResourceMapper.findByKeyStartsWithAndCosType(param);

				if(resources != null && !resources.isEmpty()) {
					//cos
					//设置要删除的key列表, 最多一次删除1000个
					DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
					ArrayList<KeyVersion> keyList = new ArrayList<>();
					resources.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
					deleteObjectsRequest.setKeys(keyList);

					List<DeletedObject> deleteObjects = new ArrayList<>();
					List<DeleteError> deleteErrors = new ArrayList<>();
					List<String> deletedErrorKeys = new ArrayList<>();
					try {
						DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
						deleteObjects = deleteObjectsResult.getDeletedObjects();
					} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
						deleteObjects = mde.getDeletedObjects();
						deleteErrors = mde.getErrors();
						deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
						logger.error("deleteDirectory->删除目录第1步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
					} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
						logger.error("deleteDirectory->删除目录第1步就失败了，删除COS目录失败，可能是连接问题！", e);
					}

					//mysql
					List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
					List<Long> ids1 = resources.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
					//手动开启事务，不要用注解
					TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
					try {
						if(ids1 != null && ids1.size() > 0) cosResourceMapper.deleteCosResourcesByIdIn(ids1);
						dataSourceTransactionManager.commit(transactionStatus);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第2步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}
				}
			} while (currentPage++ * pageSize < total);


			//2、再删除虚拟目录
			currentPage = 1;
			param.put("key", targetDirPath);
			param.put("cosType", 1);
			param.put("startIndex", (currentPage-1) * pageSize);
			param.put("pageSize", pageSize);
			total = cosResourceMapper.countByKeyStartsWithAndCosType(param);

			do {
				List<CosResource> resources = cosResourceMapper.findByKeyStartsWithAndCosType(param);

				if(resources != null && !resources.isEmpty()) {
					//用于装载目录路径，且排序（子目录在前，父目录在后）
					Set<CosResource> pathSet = new TreeSet<>((o1, o2) -> {
						int diff = o2.getKey().split("/").length - o1.getKey().split("/").length;
						return diff == 0 ? o2.getKey().compareTo(o1.getKey()) : diff;
					});

					//cos
					//设置要删除的key列表, 最多一次删除1000个
					DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
					ArrayList<KeyVersion> keyList = new ArrayList<>();
					pathSet.addAll(resources);
					pathSet.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
					deleteObjectsRequest.setKeys(keyList);

					List<DeletedObject> deleteObjects = new ArrayList<>();
					List<DeleteError> deleteErrors = new ArrayList<>();
					List<String> deletedErrorKeys = new ArrayList<>();
					try {
						DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
						deleteObjects = deleteObjectsResult.getDeletedObjects();
					} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
						deleteObjects = mde.getDeletedObjects();
						deleteErrors = mde.getErrors();
						deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
						logger.error("deleteDirectory->删除目录第4步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
					} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
						logger.error("deleteDirectory->删除目录第4步就失败了，删除COS目录失败，可能是连接问题！", e);
					}

					//mysql
					List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
					List<Long> ids1 = pathSet.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
					//手动开启事务，不要用注解
					TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
					try {
						if(ids1 != null && ids1.size() > 0) cosResourceMapper.deleteCosResourcesByIdIn(ids1);
						dataSourceTransactionManager.commit(transactionStatus);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第5步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}

					//es
					/*List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
					try {
						if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第6步就失败了，删除ES中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}*/
				}
			} while (currentPage++ * pageSize < total);

			try {
				boolean exist1 = cosClient.doesObjectExist(bucketName, targetDirPath);
				boolean exist2 = cosResourceMapper.findFirstByKeyStartsWith(targetDirPath) != null;
				if(!exist1 && !exist2) {
					logger.info("deleteDirectory->删除成功：" + CommonUtils.concat(targetDirPath,exist1,exist2/*,exist3*/));
					this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0));
				} else {
					logger.info("deleteDirectory->删除失败："+ CommonUtils.concat(targetDirPath,exist1,exist2/*,exist3*/));
					this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
				}
			} catch (Exception e) {
				this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
				throw new Exception();
			}

		} catch (Exception e) {
			logger.error("deleteDirectory->删除目录失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.sendFileReadStatusChangeNotice(new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1));
		}
	}

}