package com.xlc.spartacus.article.service;

import com.xlc.spartacus.article.exception.GlobalException;
import com.xlc.spartacus.article.pojo.Article;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;

/**
 * 接口定义层
 *
 * @author xlc, since 2021
 */
public interface ArticleService {

	/**
	 * 根据id获取文章简介
	 *
	 * @param id
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse getArticle(Long id) throws GlobalException;

	/**
	 * 根据条件获取20篇文章
	 *
	 * @param isLikeMost 喜欢最多
	 * @param isReadMost 阅读最多
	 * @return
	 */
	BaseResponse findByCriteria(Integer isLikeMost, Integer isReadMost) throws GlobalException;

	/**
	 * 获取已发表文章
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse findByCategory(String cname, Integer currentPage, Integer pageSize) throws GlobalException;


	/**
	 * 分页返回文章基本信息列表
	 *
	 * @param status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse findByStatus(Integer status, Integer currentPage, Integer pageSize) throws GlobalException;


	/**
	 * 基于ES的搜索引擎根据任意内容搜索文章
	 *
	 * @param searchText
	 * @param status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse search(String searchText, Integer status, int currentPage, int pageSize) throws GlobalException;


	/**
	 * 发布一条文章
	 *
	 * @param articleReq
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse release(BaseRequest articleReq) throws GlobalException;

	/**
	 * 保存关于我
	 *
	 * @param article
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse saveAboutMe(Article article) throws GlobalException;


	/**
	 * 存草稿一条文章
	 *
	 * @param articleReq
	 * @return
	 * @throws GlobalException
	 */
    BaseResponse draft(BaseRequest articleReq) throws GlobalException;


	/**
	 * 更新文章，发生异常时整个事务回滚
	 *
	 * @param articleReq
	 * @return
	 * @throws GlobalException
	 */
    BaseResponse update(BaseRequest articleReq) throws GlobalException;


	/**
	 * 把请求对象各个字段的值赋给通过懒加载得到的持久化对象
	 *
	 * @param article
	 * @param articleReq
	 */
	default void updateProperty(Article article, BaseRequest articleReq) {
		if(!CommonUtils.isNull(articleReq.getTitle())) {
			article.setTitle(articleReq.getTitle());
		}
		if(!CommonUtils.isNull(articleReq.getAuthor())) {
			article.setAuthor(articleReq.getAuthor());
		}
		if(!CommonUtils.isNull(articleReq.getLabels())) {
			article.setLabels(articleReq.getLabels());
		}
		if(!CommonUtils.isNull(articleReq.getPublishTime())) {
			article.setPublishTime(articleReq.getPublishTime());
		}
		if(!CommonUtils.isNull(articleReq.getCname())) {
			article.setCname(articleReq.getCname());
		}
		if(!CommonUtils.isNull(articleReq.getFromWhere())) {
			article.setFromWhere(articleReq.getFromWhere());
		}
		if(!CommonUtils.isNull(articleReq.getStatus())) {
			article.setStatus(articleReq.getStatus());
		}
		if(!CommonUtils.isNull(articleReq.getIsTop())) {
			article.setIsTop(articleReq.getIsTop());
		}
		if(!CommonUtils.isNull(articleReq.getContent())) {
			article.setContent(articleReq.getContent().trim());
		}
		if(!CommonUtils.isNull(articleReq.getContent())) {
			article.setPictures(CommonUtils.getPictures(articleReq.getContent().trim()));
		}
		if(!CommonUtils.isNull(articleReq.getBrief())) {
			article.setBrief(articleReq.getBrief());
		} else if(!CommonUtils.isNull(articleReq.getContent())) {
			article.setBrief(CommonUtils.getBrief(articleReq.getContent().trim()));
		}
		if(!CommonUtils.isNull(articleReq.getPublishTime())) {
			article.setYear(CommonUtils.getDateString("yyyy", articleReq.getPublishTime()));
			article.setMonthDay(CommonUtils.getDateString("MM.dd", articleReq.getPublishTime()));
		}
    }

}
