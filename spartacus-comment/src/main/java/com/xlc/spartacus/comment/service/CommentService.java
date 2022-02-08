package com.xlc.spartacus.comment.service;

import com.xlc.spartacus.comment.pojo.Comment;
import com.xlc.spartacus.comment.pojo.CommentForbid;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;

import javax.servlet.http.HttpServletRequest;


/**
 * 评论服务
 *
 * @author xlc, since 2021
 */
public interface CommentService {
	

	/**
	 * 插入一条评论
	 *  
	 * @param comment
	 * @return
	 * @throws BaseException
	 */
	BaseResponse submitComment(Comment comment, HttpServletRequest request) throws BaseException;

	/**
	 * 插入一条评论
	 *
	 * @param comment
	 * @return
	 * @throws BaseException
	 */
	BaseResponse adminSubmitComment(Comment comment, HttpServletRequest request) throws BaseException;


	/**
	 * 管理员回复一条评论，管理员回复这条评论、被回复的评论，都直接通过
	 *  
	 * @param comment
	 * @return
	 * @throws BaseException
	 */
	BaseResponse adminReplyComment(Comment comment, HttpServletRequest request) throws BaseException;

	/**
	 * 获取最近的20条评论
	 *  
	 * @return
	 * @throws BaseException
	 */
	BaseResponse getRecentComments() throws BaseException;
	
	/**
	 * 分页返回评论数据
	 *  
	 * @param status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	BaseResponse findByStatus(Integer status, Integer currentPage, Integer pageSize) throws BaseException;
	
	/**
	 * 根据文章ID和状态 分页返回评论数据
	 *  
	 * @param status
	 * @return
	 * @throws BaseException
	 */
	BaseResponse findByArticleIdAndStatus(Long articleId, Integer status, Integer currentPage, Integer pageSize) throws BaseException;

	
	/**
	 * 更新评论状态，发生异常时整个事务回滚
	 *  
	 * @return
	 * @throws BaseException
	 */
    BaseResponse updateStatus(Long articleId, Long commentId, Long frontId, Integer level, Integer oldStatus, Integer newStatus) throws BaseException;
	
    /**
	 * 批量更新评论状态，发生异常时整个事务回滚
	 *  
	 * @return
	 * @throws BaseException
	 */
    BaseResponse batchUpdateStatus(String jsonParams) throws BaseException;
	
	
    /**
	 * 删除评论及其追评，发生异常时整个事务回滚（逻辑删除）
	 *
	 * @param commentId
	 * @return
	 * @throws BaseException
	 */
	BaseResponse delete(Long articleId, Long commentId, Integer status, Integer level) throws BaseException;

	/**
	 * 批量删除评论及其追评，发生异常时整个事务回滚（逻辑删除）
	 *  
	 * @return
	 * @throws BaseException
	 */
	BaseResponse batchDelete(String jsonParams) throws BaseException;

	/**
	 * 加入评论黑名单
	 *  
	 * @return
	 * @throws BaseException
	 */
	BaseResponse forbid(CommentForbid forbid) throws BaseException;

	/**
	 * 分页获取评论黑名单
	 *  
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BaseException
	 */
	BaseResponse getForbids(Integer forbidType, Integer currentPage, Integer pageSize) throws BaseException;

	/**
	 * 移除评论黑名单
	 *  
	 * @return
	 * @throws BaseException
	 */
	BaseResponse unForbid(CommentForbid forbid) throws BaseException;
	
	
	/**
	 * 批量解禁评论黑名单，发生异常时整个事务回滚
	 *  
	 * @param jsonParams
	 * @return
	 * @throws BaseException
	 */
	BaseResponse batchUnForbid(String jsonParams) throws BaseException;
	
}
