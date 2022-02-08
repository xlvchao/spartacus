package com.xlc.spartacus.comment.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.comment.config.CommonProperties;
import com.xlc.spartacus.comment.mapper.CommentForbidMapper;
import com.xlc.spartacus.comment.mapper.CommentMapper;
import com.xlc.spartacus.comment.pojo.Comment;
import com.xlc.spartacus.comment.pojo.CommentForbid;
import com.xlc.spartacus.comment.service.CommentService;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.HttpUtils;
import com.xlc.spartacus.common.core.utils.IPUtils;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * 评论服务 实现类
 *
 * @author xlc, since 2021
 */
@Service
public class CommentServiceImpl implements CommentService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Resource
	CommonProperties commonProperties;
	
	@Resource
	CommentMapper commentMapper;
	
	@Resource
	CommentForbidMapper commentForbidMapper;

	@Resource
	RedisTemplate redisTemplate;



	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse submitComment(Comment comment, HttpServletRequest request) throws BaseException {
		try {
			logger.info("开始提交评论：" + comment);
			comment.setId(Snowflake.generateId());
			comment.setStatus(0);
			comment.setPublishTime(new Date());

			///////
			getIpAddressInfos(comment, request);
			///////

			if(CommonUtils.isNull(comment.getRefId())) { //一级评论
				comment.setLevel(1);
			} else { //二级评论
				comment.setLevel(2);
			}

			commentMapper.save(comment);
			logger.info("提交评论成功：" + comment);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, comment.getId()); //返回评论的id

		} catch (Exception e) {
			logger.error("提交评论发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "提交评论发生异常！");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse adminSubmitComment(Comment comment, HttpServletRequest request) throws BaseException {
		try {
			logger.info("管理员开始提交评论：" + comment);
			comment.setId(Snowflake.generateId());
			comment.setStatus(1);
			comment.setPublishTime(new Date());

			///////
			getIpAddressInfos(comment, request);
			///////

			int count = 0;
			if(CommonUtils.isNull(comment.getRefId())) { //一级评论
				count = 1;

			} else { //二级评论
				comment.setLevel(2);
				if(comment.getPageStatus() != null && comment.getPageStatus() == 0) {
					commentMapper.updateStatusById(comment.getFrontId(), 1);
					count = 2;
				} else if(comment.getPageStatus() != null && comment.getPageStatus() == 1) {
					count = 1;
				}
			}

			commentMapper.save(comment);
			commentMapper.modifyCommentNumber(comment.getArticleId(), count);
			logger.info("管理员提交评论成功：" + comment);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, comment.getId()); //返回评论的id

		} catch (Exception e) {
			logger.error("管理员提交评论发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "管理员提交评论发生异常！");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse adminReplyComment(Comment comment, HttpServletRequest request) throws BaseException {
		try {
			logger.info("管理员开始回复评论：" + comment);
			Integer count = 0;
			comment.setId(Snowflake.generateId());
			comment.setStatus(1);
			comment.setLevel(2);

			///////
			getIpAddressInfos(comment, request);
			///////

			commentMapper.save(comment);
			count++;

			Optional<Comment> repoComment = Optional.ofNullable(commentMapper.findById(comment.getFrontId()));
			if(repoComment.isPresent()) {
				commentMapper.updateStatusById(comment.getFrontId(), 1);
				count++;
			}

			commentMapper.modifyCommentNumber(comment.getArticleId(), count);
			logger.info("管理员回复评论成功：" + comment);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, comment.getId()); //返回评论的id

		} catch (Exception e) {
			logger.error("管理员回复评论发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "管理员回复评论发生异常！");
		}
	}


	@Override
	public BaseResponse getRecentComments() throws BaseException {
		try {
			logger.info("开始获取最近评论！");
			List<Comment> list = commentMapper.getRecentComments();
			logger.info("获取最近评论成功：" + list);
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, list);

	    } catch (Exception e) {
	    	logger.error("获取最近评论发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取最近评论发生异常！");
	    }
	}


	@Override
	public BaseResponse findByStatus(Integer status, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取评论：" + status);
			PageHelper.startPage(currentPage, pageSize).setOrderBy("publish_time desc");
			List<Comment> commentList = commentMapper.findByStatus(status);
			PageInfo<Comment> pageInfo = new PageInfo<>(commentList);

	        Map<Object, Object> pageMap = new HashMap<>();
	        pageMap.put("forbiddenIps", commentForbidMapper.getForbiddenIpList());
	        pageMap.put("forbiddenIds", commentForbidMapper.getForbiddenProviderUserIdList());
	        pageMap.put("comments", pageInfo.getList());
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", pageInfo.getPages());
	        pageMap.put("total", pageInfo.getTotal());
	        logger.info("获取评论成功：" + pageMap);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, pageMap);

		} catch (Exception e) {
			logger.error("获取评论发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取评论发生异常！");
		}
	}


	@Override
	public BaseResponse findByArticleIdAndStatus(Long articleId, Integer status, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取评论数据：" + articleId + "," + status);
			PageHelper.startPage(currentPage, pageSize);
			Comment condition = Comment.builder().articleId(articleId).status(status).level(1).build();
			List<Comment> commentList = commentMapper.findByCondition(condition);
			PageInfo<Comment> page = new PageInfo<>(commentList);

			Map<Long, Comment> commentMap = new LinkedHashMap<>();
			for(Comment comment1 : page.getList()) {
				Comment condition1 = Comment.builder().refId(comment1.getId()).status(status).level(2).build();
				List<Comment> subComments = commentMapper.findByCondition(condition1);
				TreeMap<Long, Comment> subCommentMap = new TreeMap<Long, Comment>(); //默认根据key升序排序
				for(Comment comment2 : subComments) {
					subCommentMap.put(comment2.getId(), comment2);
				}
				comment1.setSubComments(subCommentMap);
				commentMap.put(comment1.getId(), comment1);
			}

			HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("comments", commentMap);
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getPages());
	        pageMap.put("total", page.getTotal());
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, pageMap);

		} catch (Exception e) {
			logger.error("获取评论数据发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取评论数据发生异常！");
		}
	}


	@Override
    @Transactional
    public BaseResponse updateStatus(Long articleId, Long commentId, Long frontId, Integer level, Integer oldStatus, Integer newStatus) throws BaseException {
		try {
			logger.info("开始更新评论状态：" + CommonUtils.concat(articleId, commentId, frontId, level, oldStatus, newStatus));
			// 检查追评对象状态是否'已通过'
			if(level == 2 && newStatus == 1) {
				Integer status = commentMapper.getStatusById(frontId);
				if(status != 1) {
					return new BaseResponse(RespConstant.CODE_1, "请先'通过'前置评论，再'通过'当前评论！");
				}
			}

			int count = commentMapper.updateStatusById(commentId, newStatus);

			if(oldStatus != 1 && newStatus == 1) {
				commentMapper.modifyCommentNumber(articleId, count);
			} else if(oldStatus == 1 && newStatus != 1){
				commentMapper.modifyCommentNumber(articleId, count * -1);
			}
			logger.info("评论状态更新成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("更新评论状态发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "更新评论状态发生异常！");
		}
    }


	@Override
    @Transactional
    public BaseResponse batchUpdateStatus(String jsonParams) throws BaseException {
		try {
			logger.info("开始批量更新评论状态：" + jsonParams);
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				Long articleId = jsonArray.getJSONObject(i).getLong("articleId");
				Long commentId = jsonArray.getJSONObject(i).getLong("commentId");
				Integer oldStatus = jsonArray.getJSONObject(i).getInteger("oldStatus");
				Integer newStatus = jsonArray.getJSONObject(i).getInteger("newStatus");
				int count = commentMapper.updateStatusById(commentId, newStatus);
				if(oldStatus != 1 && newStatus == 1) {
					commentMapper.modifyCommentNumber(articleId, count);
				} else if(oldStatus == 1 && newStatus != 1){
					commentMapper.modifyCommentNumber(articleId, count * -1);
				}
			}			
			logger.info("批量更新评论状态成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("批量更新评论状态发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "批量更新评论状态发生异常！");
		}
    }


	@Override
	@Transactional
	public BaseResponse delete(Long articleId, Long commentId, Integer status, Integer level) throws BaseException {
		try {
			logger.info("开始删除评论：" + CommonUtils.concat(articleId, status, level));
			int count = 0;
			if(level == 1) {
				count += commentMapper.countByRefIdAndStatus(commentId, 1);
				commentMapper.updateStatusByRefId(commentId, 2);
			} else if(level == 2) {
				PriorityQueue<Long> queue = new PriorityQueue<>();
				queue.addAll(commentMapper.getLinkedCommentIdListById(commentId));
				while(!queue.isEmpty()) {
					Long id = queue.poll();
					queue.addAll(commentMapper.getLinkedCommentIdListById(id));
					if(commentMapper.findById(id).getStatus() == 1) {
						count++;
					}
					commentMapper.updateStatusById(id, 2);
				}
			}
			if(status == 1) {
				count++;
			}

			// 删除当前评论
			commentMapper.updateStatusById(commentId, 2);
			// 更新文章评论数量
			commentMapper.modifyCommentNumber(articleId, count * -1);

			logger.info("删除评论成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("删除评论发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "删除评论发生异常！");
		}
	}


	@Override
	@Transactional
	public BaseResponse batchDelete(String jsonParams) throws BaseException {
		try {
			logger.info("开始批量删除评论：" + jsonParams);
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				Long commentId = jsonArray.getJSONObject(i).getLong("commentId");
				Integer status = jsonArray.getJSONObject(i).getInteger("status");
				Integer level = jsonArray.getJSONObject(i).getInteger("level");
				Long articleId = jsonArray.getJSONObject(i).getLong("articleId");
				
				int count = 0;
				if(level == 1) {
					count += commentMapper.countByRefIdAndStatus(commentId, 1);
					commentMapper.updateStatusByRefId(commentId, 2);
				} else if(level == 2) {
					PriorityQueue<Long> queue = new PriorityQueue<>();
					queue.addAll(commentMapper.getLinkedCommentIdListById(commentId));
					while(!queue.isEmpty()) {
						Long id = queue.poll();
						queue.addAll(commentMapper.getLinkedCommentIdListById(id));
						if(commentMapper.findById(id).getStatus() == 1) {
							count++;
						}
						commentMapper.updateStatusById(id, 2);
					}
				}
				if(status == 1) {
					count++;
				}

				// 删除当前评论
				commentMapper.updateStatusById(commentId, 2);
				// 批量删除时，如果待删除1级评论下面有2级评论，但是在后台2级评论先做删除操作，会出现假删问题，会报错
				// 更新文章评论数量
				commentMapper.modifyCommentNumber(articleId, count * -1);
			}
			logger.info("批量删除评论成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("批量删除评论发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "批量删除评论发生异常！");
		}
	}


	@Override
	@Transactional
	public BaseResponse forbid(CommentForbid forbid) throws BaseException {
		try {
			logger.info("开始加入评论黑名单：" + forbid);
			if(forbid.getForbidType() == 1) {
				redisTemplate.opsForSet().add("FORBIDDEN_COMMENT_IP_SET", forbid.getIp());
			} else if(forbid.getForbidType() == 0) {
				redisTemplate.opsForSet().add("FORBIDDEN_COMMENT_USERID_SET", forbid.getProviderUserId());
			}

			forbid.setId(Snowflake.generateId());
			commentForbidMapper.save(forbid);
			logger.info("加入评论黑名单成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("加入评论黑名单发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "加入评论黑名单发生异常！");
		}
	}


	@Override
	public BaseResponse getForbids(Integer forbidType, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取评论黑名单：" + forbidType);

			PageHelper.startPage(currentPage, pageSize).setOrderBy("operate_time desc");
	        List<CommentForbid> forbidList = null;
	        if(forbidType != null) {
				forbidList = commentForbidMapper.findByForbidType(forbidType);
	        } else {
				forbidList = commentForbidMapper.findAll();
	        }
			PageInfo<CommentForbid> page = new PageInfo<>(forbidList);

	        HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("forbids", page.getList());
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getPages());
	        pageMap.put("total", page.getTotal());
	        logger.info("获取评论黑名单成功：" + pageMap);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, pageMap);

		} catch (Exception e) {
			logger.error("获取评论黑名单发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取评论黑名单发生异常！");
		}
	}


	@Override
	@Transactional
	public BaseResponse unForbid(CommentForbid forbid) throws BaseException {
		try {
			logger.info("开始解封评论黑名单：" + forbid);
			if(forbid.getForbidType() == 0) {
				redisTemplate.opsForSet().remove("FORBIDDEN_COMMENT_USERID_SET", forbid.getProviderUserId());
				commentForbidMapper.deleteByProviderUserId(forbid.getProviderUserId());
			} else if(forbid.getForbidType() == 1) {
				redisTemplate.opsForSet().remove("FORBIDDEN_COMMENT_IP_SET", forbid.getIp());
				commentForbidMapper.deleteByIp(forbid.getIp());
			}
			logger.info("解封评论黑名单成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("解封评论黑名单发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "解封评论黑名单发生异常！");
		}
	}


	@Override
	@Transactional
	public BaseResponse batchUnForbid(String jsonParams) throws BaseException {
		try {
			logger.info("开始批量解封评论黑名单：" + jsonParams);
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			List<String> ips = new ArrayList<>();
			List<String> userIds = new ArrayList<>();
			for(int i=0; i< jsonArray.size(); i++) {
				Integer forbidType = jsonArray.getJSONObject(i).getInteger("forbidType");
				String ip = jsonArray.getJSONObject(i).getString("ip");
				String providerUserId = jsonArray.getJSONObject(i).getString("providerUserId");
				if(forbidType == 0) {
					userIds.add(providerUserId);
				} else if(forbidType == 1) {
					ips.add(ip);
				}

				if(ips.size() > 0) {
					redisTemplate.opsForSet().remove("FORBIDDEN_COMMENT_IP_SET", ips.toArray());
					commentForbidMapper.batchDeleteByIp(ips);
				}
				if(userIds.size() > 0) {
					redisTemplate.opsForSet().remove("FORBIDDEN_COMMENT_USERID_SET", userIds.toArray());
					commentForbidMapper.batchDeleteByProviderUserId(userIds);
				}

			}
			logger.info("批量解封评论黑名单成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("批量解封评论黑名单发生异常！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
			return new BaseResponse(RespConstant.CODE_1, "批量解封评论黑名单发生异常！");
		}
	}


	/**
	 * 获取IP地址的详细信息
	 *
	 * @param comment
	 * @param request
	 * @throws Exception
	 */
	private void getIpAddressInfos(Comment comment, HttpServletRequest request) throws Exception {
		String ip = IPUtils.getIp(request);
		comment.setIp(ip);
		String url = commonProperties.getBaiduMapUrl() + "&ak=" + commonProperties.getBaiduMapAk() + "&ip=" + ip;
		String result = HttpUtils.get(url, null);
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.getInteger("status") == 0) {
			String province = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("province");
			String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
			comment.setIpCity(province + "," + city);
		}
	}

}
