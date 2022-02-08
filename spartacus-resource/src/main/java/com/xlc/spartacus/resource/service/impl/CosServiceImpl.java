package com.xlc.spartacus.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.Converts;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.common.core.utils.ZipUtils;
import com.xlc.spartacus.resource.constant.ResourceConstant;
import com.xlc.spartacus.resource.config.CommonProperties;
import com.xlc.spartacus.resource.exception.GlobalException;
import com.xlc.spartacus.resource.mapper.CosResourceMapper;
import com.xlc.spartacus.resource.pojo.CosResource;
import com.xlc.spartacus.resource.pojo.Profile;
import com.xlc.spartacus.resource.pojo.WordFrequency;
import com.xlc.spartacus.resource.service.CosService;
import com.xlc.spartacus.resource.task.AsyncTask;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * cos接口 实现
 *
 * @author xlc, since 2021
 */
@Component
public class CosServiceImpl implements CosService {
	private static Logger logger = LoggerFactory.getLogger(CosServiceImpl.class);

	@Lazy
	@Resource
	AsyncTask task;
	
	@Resource
    private CommonProperties commonProperties;
	
	@Resource
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Resource
	private CosResourceMapper cosResourceMapper;
	
	@Resource
	private ElasticsearchService elasticsearchService;

	@Resource
	private COSClient cosClient;


	@Override
	public BaseResponse search(String searchText, Integer cosType, String rootDirPath, int currentPage, int pageSize) {
		try {
			logger.info("开始搜索资源：" + CommonUtils.concat(searchText, cosType, rootDirPath));
			Map<String, Object> mustMatchs = new HashMap<>();
			mustMatchs.put("cos_type", cosType);
			mustMatchs.put("root_dir_path", rootDirPath);
			String highlightFields = "file_name";
			String matchFields = "file_name,tags";
			Page page = elasticsearchService.searchMatch(ResourceConstant.COS_RESOURCE_INDEX_NAME, searchText, matchFields, mustMatchs, null, "last_modified", highlightFields, currentPage, pageSize);
			List<Map<String, Object>> records = (List<Map<String, Object>>) page.getRecords();
			records.forEach(r ->  r.put("url", commonProperties.getBaseUrl() + "/" + r.get("key")));

			page.setRecords(Converts.convertSnakeToCamel(records));
			logger.info("资源搜索成功：" + page);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

		} catch (Exception e) {
			logger.error("搜索资源发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "搜索资源发生异常！");
		}
	}

	@Override
	public BaseResponse rename(String key, String newFileName, String eventId) {
		try {
			logger.info("开始资源重命名：" + CommonUtils.concat(key, newFileName, eventId));
			task.rename(key, newFileName, eventId);
			logger.info("资源重命名成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("资源重命名发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "资源重命名发生异常！");
		}
	}

	@Override
	public BaseResponse batchMove(String keysStr, String destDirPath, String eventId) {
		try {
			logger.info("开始批量移动资源：" + CommonUtils.concat(keysStr, destDirPath, eventId));
			task.batchMove(keysStr, destDirPath, eventId);
			logger.info("批量移动资源成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("批量移动资源发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "批量移动资源发生异常！");
		}
	}

	@Override
	public BaseResponse batchDownload(String keysStr) {
		try {
			logger.info("开始批量下载资源：" + keysStr);
			String[] keys = keysStr.split(",");
			List<Map<String, byte[]>> bytesList = new ArrayList<>();
			for(String key : keys) {
				try {
					//获取图片流
					GetObjectRequest getObjectRequest = new GetObjectRequest(commonProperties.getBucketName(), key);
					COSObject cosObject = cosClient.getObject(getObjectRequest);
					ObjectMetadata objectMetadata = cosObject.getObjectMetadata();
					COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

					//转换成base64字符串
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buffer = new byte[4096];
					int n = 0;
					while (-1 != (n = cosObjectInput.read(buffer))) {
						output.write(buffer, 0, n);
					}

					String fileName = key.substring(key.lastIndexOf("/") + 1);
					Map<String, byte[]> map = new HashMap<>();
					map.put(fileName, output.toByteArray());
					bytesList.add(map);

				} catch (Exception e) {
					logger.error("下载单个资源时发生异常：" + key, e);
					throw new GlobalException("下载单个资源时发生异常：" + key);
				}
			}

			//发给前端
			String fileName = Snowflake.generateId() +".zip";
			Map<String, Object> map = new HashMap<>();
			map.put("base64", ZipUtils.batchZipByteArrayOutputStream(bytesList));
			map.put("contentType", "application/x-zip-compressed");
			map.put("fileName", fileName);

			logger.info("批量下载资源成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, map);

		} catch (Exception e) {
			String msg = "批量下载资源发生异常！";
			if(e instanceof GlobalException) {
				msg = e.getMessage();
			}
			logger.error(msg, e);
			return new BaseResponse(RespConstant.CODE_1, msg);

		}
	}

	@Override
	public BaseResponse download(String key) {
		try {
			logger.info("开始下载资源：" + key);
			//获取图片流
			GetObjectRequest getObjectRequest = new GetObjectRequest(commonProperties.getBucketName(), key);
			COSObject cosObject = cosClient.getObject(getObjectRequest);
			ObjectMetadata objectMetadata = cosObject.getObjectMetadata();
			COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

			//转换成base64字符串
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = cosObjectInput.read(buffer))) {
				output.write(buffer, 0, n);
			}
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(output.toByteArray());

			//发给前端
			String fileName = key.substring(key.lastIndexOf("/") + 1);
			Map<String, Object> map = new HashMap<>();
			map.put("base64", base64);
			map.put("contentType", objectMetadata.getContentType());
			map.put("fileName", fileName);

			logger.info("下载资源成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, map);

		} catch (Exception e) {
			logger.error("下载资源时发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "下载资源时发生异常！");

		}
	}

	@Override
	public BaseResponse listTags() {
		try {
			logger.info("开始获取标签词频统计列表！");
			List<WordFrequency> tagsList = new ArrayList<>();
			if(redisTemplate.hasKey(ResourceConstant.COS_RESOURCE_TAGS)) {
				tagsList = JSON.parseArray(redisTemplate.opsForValue().get(ResourceConstant.COS_RESOURCE_TAGS).toString(), WordFrequency.class);
			} else {
				List<String> allTags = cosResourceMapper.findAllTags();
				Map<String, Integer> tagWeightMap = new HashMap<>();
				for (String tags : allTags) {
					for (String tag : tags.split(",")) {
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
			}

			logger.info("获取标签词频统计列表成功：" + tagsList);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, tagsList);
		} catch (Exception e) {
			logger.error("获取标签词频统计列表发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取标签词频统计列表发生异常！");
		}
	}

	@Override
	@Transactional
	public BaseResponse webUploader(String parentDirPath, Long parentId, String fileName, String tags, byte[] fileBytes) {
        try {
			logger.info("开始上传资源：" + CommonUtils.concat(parentDirPath, parentId, fileName, tags));
			String key = null;
			CosResource cosResource = new CosResource();
			Long id = Snowflake.generateId();
			int aclFlag = 2;
			boolean flag = false;
			String contentType = null;

			String bucketName = commonProperties.getBucketName();

        	//COS
        	try {
				Integer bucketAclFlag = cosClient.getBucketAcl(bucketName).getGrantsAsList().size();

	            // 上传到COS后相对bucket的路径，对象key包含多级目录也没事，如果包含的目录不存在，会自动创建
		        key = parentDirPath + id + "-" + fileName;
	            // 获取文件流
	            InputStream inputStream = new ByteArrayInputStream(fileBytes);
	            // 生成元数据
	            ObjectMetadata objectMetadata = new ObjectMetadata();
	            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
	            objectMetadata.setContentLength(fileBytes.length);
	            // 生成存储对象
	            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
	            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia), 近线(nearline)
	            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
	            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
				contentType = putObjectResult.getMetadata().getContentType();

	            //设置访问权限
				AccessControlList accessCtrlList = cosClient.getObjectAcl(bucketName, parentDirPath);
				if (accessCtrlList.isExistDefaultAcl()) { //继承权限，继承的是存储桶的权限
					aclFlag = bucketAclFlag;
				} else {
					aclFlag = accessCtrlList.getGrantsAsList().size();
				}

				cosClient.setObjectAcl(bucketName, key, ResourceConstant.ACL_MAP.get(aclFlag));
				//COS不报错即是成功
			} catch (Exception e) {
        		logger.error("上传资源第1步发生异常：上传资源至COS！", e);
				task.deleteCosObject(bucketName, key);
				return new BaseResponse(RespConstant.CODE_1, "上传资源第1步发生异常：上传资源至COS！");
			}

            //MYSQL
        	try {
	            cosResource.setId(id);
	            cosResource.setParentId(parentId);
	            cosResource.setRegion(commonProperties.getBucketRegion());
	            cosResource.setBucketName(commonProperties.getBucketName());
	            cosResource.setFileName(fileName);
	            cosResource.setKey(key);
	            cosResource.setStatus(0);
	            cosResource.setContentType(contentType);
				cosResource.setCosType(0);
				//cosResource.setParentDirPath(parentDirPath);
				cosResource.setRootDirPath(parentDirPath.substring(0, parentDirPath.indexOf("/")+1));
	            cosResource.setTags(tags.replace("，",","));
	            cosResource.setAclFlag(aclFlag);
	            cosResource.setLastModified(new Date());
	            cosResourceMapper.save(cosResource);
        	} catch (Exception e) {
				logger.error("上传资源第2步发生异常：同步资源信息至Mysql！", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(commonProperties.getBucketName(), key);
				return new BaseResponse(RespConstant.CODE_1, "上传资源第2步发生异常：同步资源信息至Mysql！");
			}

			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);
        } catch (Exception e) {
			logger.error("上传资源发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "上传资源发生异常！");
		}
    }

	@Override
	public BaseResponse listObjects(boolean isRecursive, String dirPath, Integer currentPage, Integer pageSize, String tag) {
		try {
			logger.info("开始获取资源列表：" + CommonUtils.concat(isRecursive, dirPath, tag, currentPage, pageSize));

			Map<String, Object> param = new HashMap<>();
			param.put("dirPath", dirPath);
			param.put("tag", tag);

			List<CosResource> records = null;
			PageHelper.startPage(currentPage, pageSize);
			if(isRecursive) {
				if(StringUtils.isNotBlank(tag)) {
					records = cosResourceMapper.getRecursiveCosResourcesByTag(param);
				} else {
					records = cosResourceMapper.getRecursiveCosResources(param);
				}
			} else {
				if(StringUtils.isNotBlank(tag)) {
					records = cosResourceMapper.getDirectCosResourcesByTag(param);
				} else {
					records = cosResourceMapper.getDirectCosResources(param);
				}
			}
			records.forEach(r -> r.setUrl(commonProperties.getBaseUrl() + "/" + r.getKey()));
			PageInfo<CosResource> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("获取资源列表成功：" + page);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

		} catch (Exception e) {
			logger.error("获取资源列表发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取资源列表发生异常！");
		}
	}

	@Override
	public BaseResponse batchSetObjectAcl(String keysStr, Integer aclFlag, String eventId) {
		try {
			logger.info("开始异步执行批量设置权限命令：" + CommonUtils.concat(keysStr, aclFlag, eventId));
			task.batchSetObjectAcl(keysStr, aclFlag, eventId);
			logger.info("异步执行批量设置权限命令成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("异步执行批量设置权限命令发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "异步执行批量设置权限命令发生异常！");
		}
	}

	@Override
	@Transactional
	public BaseResponse createDirectory(String parentDirPath, Long parentId, String newDirName) {
		try {
			logger.info("开始新建COS虚拟目录：" + CommonUtils.concat(parentDirPath, parentId, newDirName));
			CosResource cosResource = new CosResource();
			String key = parentDirPath + newDirName + "/";
			Long id = Snowflake.generateId();
			boolean flag = false;
			Integer aclFlag = 1;

			String bucketName = commonProperties.getBucketName();
			String bucketRegion = commonProperties.getBucketRegion();

			Integer bucketAclFlag = cosClient.getBucketAcl(bucketName).getGrantsAsList().size();

			//COS
			try {
				// 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
				InputStream input = new ByteArrayInputStream(new byte[0]);
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentLength(0);
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input, objectMetadata);
				PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
				String etag = putObjectResult.getETag();

				//设置访问权限
				AccessControlList accessCtrlList = cosClient.getObjectAcl(bucketName, parentDirPath);
				if (accessCtrlList.isExistDefaultAcl()) {
					aclFlag = bucketAclFlag;
				} else {
					aclFlag = accessCtrlList.getGrantsAsList().size();
				}
				cosClient.setObjectAcl(bucketName, key, ResourceConstant.ACL_MAP.get(aclFlag));

			} catch (Exception e) {
				logger.error("新建目录第1步发生异常：COS端远程创建新目录！", e);
				return new BaseResponse(RespConstant.CODE_1, "新建目录第1步发生异常：COS端远程创建新目录！");
			}

			//MYSQL
			try {
				cosResource.setId(id);
				cosResource.setParentId(parentId);
				cosResource.setKey(key);
				cosResource.setFileName(newDirName);
				cosResource.setRegion(bucketRegion);
				cosResource.setBucketName(bucketName);
				cosResource.setStatus(0);
				cosResource.setCosType(1); //目录
				cosResource.setAclFlag(aclFlag);
				cosResource.setLastModified(new Date());
				cosResourceMapper.save(cosResource);
			} catch (Exception e) {
				logger.error("新建目录第2步发生异常：同步数据至MYSQL！", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(commonProperties.getBucketName(), key);
				return new BaseResponse(RespConstant.CODE_1, "新建目录第2步发生异常：同步数据至MYSQL！");
			}

			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);
		} catch (Exception e) {
			logger.error("新建COS虚拟目录发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "新建COS虚拟目录发生异常！");
		}
	}

	@Override
	public BaseResponse deleteDirectory(String targetDirPath, String eventId) {
		try {
			logger.info("开始异步执行删除COS目录对象：" + CommonUtils.concat(targetDirPath, eventId));
			task.deleteCosDirectory(targetDirPath, eventId);
			logger.info("异步执行删除COS目录对象成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("异步执行删除COS目录对象发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "异步执行删除COS目录对象发生异常！");
		}
	}

	@Override
	public BaseResponse batchDelete(String keysStr, String eventId) {
		try {
			logger.info("开始异步批量删除COS对象：" + CommonUtils.concat(keysStr, eventId));
			task.batchDelete(keysStr, eventId);
			logger.info("异步批量删除COS对象成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("异步批量删除COS对象发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "异步批量删除COS对象发生异常！");
		}
	}

	@Override
	public BaseResponse getDirectoryTree(String rootDirPath) {
 		try {
			logger.info("开始生成COS目录树：" + rootDirPath);
			List<CosResource> nodeList = null;
			if(CommonUtils.isNull(rootDirPath)) {
				nodeList = cosResourceMapper.findAllByCosType(1);
			} else {
				Map<String, Object> param = new HashMap<>();
				param.put("key", rootDirPath);
				param.put("cosType", 1);
				nodeList = cosResourceMapper.findAllByKeyStartsWithAndCosType(param);
			}

			logger.info("生成COS目录树成功：" + nodeList);
 			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, nodeList);

        } catch (Exception e) {
            logger.error("生成COS目录树发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "生成COS目录树发生异常！");
        }
    }

	@Override
	public BaseResponse fileUpload(String fileName, byte[] fileBytes, String baseDirPath) {
		String key = null;
		String imgUrl = null;

		//COS
        try {
			logger.info(String.format("开始上传文件，路径：%s，文件名：%s", baseDirPath, fileName));
            // 上传到COS后相对bucket的路径，对象key包含多级目录也没事，如果包含的目录不存在，会自动创建
			if(StringUtils.isBlank(baseDirPath)) {
				baseDirPath = commonProperties.getArticleUploadPath();
			}
	        String dirKey = baseDirPath + CommonUtils.getDateString("yyyy-MM-dd") + "/";
	        key = dirKey + Snowflake.generateId() + "-" + fileName;
            
            // 获取文件流
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            // 生成元数据
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(fileBytes.length);
            // 生成存储对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(commonProperties.getBucketName(), key, inputStream, objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia), 近线(nearline) 
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        	imgUrl = commonProperties.getBaseUrl() + "/" + key;

        } catch (Exception e) {
			logger.error("上传文件第1步发生异常：上传文件至COS！", e);
			return new BaseResponse(RespConstant.CODE_1, "上传文件第1步发生异常：上传文件至COS！", commonProperties.getDefaultUrl());
        }

		//MYSQL
		try {
			if(commonProperties.getLogoUploadPath().equals(baseDirPath)) {
				HashMap<String, Object> logo = new HashMap<>();
				logo.put("id", Snowflake.generateId());
				logo.put("headImg", imgUrl);
				logo.put("addTime", new Date());
				cosResourceMapper.saveLogo(logo);

			} else {
				CosResource cosResource = new CosResource();
				cosResource.setId(Snowflake.generateId());
				cosResource.setRegion(commonProperties.getBucketRegion());
				cosResource.setBucketName(commonProperties.getBucketName());
				cosResource.setFileName(fileName);
				cosResource.setKey(key);
				cosResource.setCosType(0);
				cosResource.setStatus(0);
				cosResource.setAclFlag(2);
				cosResource.setLastModified(new Date());
				cosResourceMapper.save(cosResource);
			}

		} catch (Exception e) {
			logger.error("上传文件第2步发生异常：同步文件信息至Mysql！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			task.deleteCosObject(commonProperties.getBucketName(), key);
			return new BaseResponse(RespConstant.CODE_1, "上传文件第2步发生异常：同步文件信息至Mysql！", commonProperties.getDefaultUrl());
		}

		logger.info("上传文件成功：" + imgUrl);
		return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, imgUrl);
    }

	@Override
	public boolean isExist(String key) {
        try {
            String bucketName = commonProperties.getBucketName();
            // 获取 COS 文件属性
    		ObjectMetadata objectMetadata = cosClient.getObjectMetadata(bucketName, key);
    		return true;
        } catch (CosServiceException e) {
        	return false;
        }
	}

	@Override
	public BaseResponse loadHistoryLogos() {
		try {
			logger.info("开始查询历史头像");
			List<Profile> historyLogos = cosResourceMapper.loadHistoryLogos();
			logger.info("历史头像查询成功：" + historyLogos);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, historyLogos);

		} catch (Exception e) {
			logger.error("历史头像查询发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "历史头像查询发生异常！");
		}
	}

}
