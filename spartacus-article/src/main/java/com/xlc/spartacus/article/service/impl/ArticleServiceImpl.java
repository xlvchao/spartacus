package com.xlc.spartacus.article.service.impl;

import com.xlc.spartacus.article.constant.ArticleConstant;
import com.xlc.spartacus.article.exception.GlobalException;
import com.xlc.spartacus.article.mapper.ArticleMapper;
import com.xlc.spartacus.article.pojo.Article;
import com.xlc.spartacus.article.service.ArticleService;
import com.xlc.spartacus.article.service.LikeService;
import com.xlc.spartacus.article.utils.RedisKeyUtils;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.core.utils.Converts;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * service层
 *
 * @author xlc, since 2021
 */
@Component
public class ArticleServiceImpl implements ArticleService {
	
	private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

	@Resource
    private ArticleMapper articleMapper;

	@Resource
	private ElasticsearchService elasticsearchService;

	@Resource
	private LikeService likeService;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;


	@Override
	public BaseResponse getArticle(Long id) throws GlobalException {
		try {
			logger.info("开始获取文章：" + id);
			Article article = articleMapper.findById(id);
			article.setLikeNumber(likeService.getLikeCount(id));
			Long newScanNumber = redisTemplate.opsForHyperLogLog().size(RedisKeyUtils.getScanNumberKey(article.getId()));
			article.setScanNumber(article.getScanNumber()+newScanNumber.intValue());
			logger.info("文章获取成功：" + article);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, article);

		} catch (Exception e) {
			logger.error("获取文章发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取文章发生异常！");
		}
	}


	@Override
	public BaseResponse findByCriteria(Integer isLikeMost, Integer isReadMost) throws GlobalException {
		try {
			logger.info(String.format("开始根据条件获取文章，isLikeMost：{}, isReadMost：{}", isLikeMost, isReadMost));
			List<Article> articleList = articleMapper.findByCriteria(isLikeMost, isReadMost);
			articleList.forEach(a -> {
				a.setLikeNumber(likeService.getLikeCount(a.getId()));
				Long newScanNumber = redisTemplate.opsForHyperLogLog().size(RedisKeyUtils.getScanNumberKey(a.getId()));
				a.setScanNumber(a.getScanNumber()+newScanNumber.intValue());});
			logger.info("根据条件获取文章成功：" + articleList);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, articleList);

		} catch (Exception e) {
			logger.error("根据条件获取文章发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "根据条件获取文章发生异常！");
		}
	}


	@Override
	public BaseResponse findByCategory(String cname, Integer currentPage, Integer pageSize) throws GlobalException {
		try {
			logger.info("开始获取已发表文章列表");
			List<Article> pageArticleList = new ArrayList<>();

			if(currentPage == 1) {
				PageHelper.startPage(1, 100).setOrderBy("publish_time desc"); //约定置顶文章不可能超过100篇
				List<Article> topArticleList = articleMapper.findByCategory(1, null);
				pageArticleList.addAll(topArticleList);
			}

			PageHelper.startPage(currentPage, pageSize).setOrderBy("publish_time desc");
			List<Article> articleList = articleMapper.findByCategory(0, cname);
			PageInfo<Article> pageInfo = new PageInfo<>(articleList);
			pageArticleList.addAll(articleList);

			pageArticleList.forEach(a -> {
				a.setLikeNumber(likeService.getLikeCount(a.getId()));
				Long newScanNumber = redisTemplate.opsForHyperLogLog().size(RedisKeyUtils.getScanNumberKey(a.getId()));
				a.setScanNumber(a.getScanNumber()+newScanNumber.intValue());});

			// 把返回给前段的Page替换成PageInfo，后面有空再优化吧
			Page pageData = Page.builder()
					.currentPage(pageInfo.getPageNum()).pageSize(pageSize).totalPages(pageInfo.getPages())
					.total((int) pageInfo.getTotal()).records(pageArticleList)
					.build();

			logger.info("获取已发表文章列表成功：" + pageData);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, pageData);

		} catch (Exception e) {
			logger.error("获取已发表文章列表发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取已发表文章列表发生异常！");
		}
	}


	@Override
	public BaseResponse findByStatus(Integer status, Integer currentPage, Integer pageSize) throws GlobalException {
		try {
			logger.info("开始获取文章列表：" + status);

			PageHelper.startPage(currentPage, pageSize).setOrderBy("publish_time desc");
			List<Article> articleList = articleMapper.findByStatus(status);

			articleList.forEach(a -> {
				a.setLikeNumber(likeService.getLikeCount(a.getId()));
				Long newScanNumber = redisTemplate.opsForHyperLogLog().size(RedisKeyUtils.getScanNumberKey(a.getId()));
				a.setScanNumber(a.getScanNumber()+newScanNumber.intValue());});
			PageInfo<Article> pageInfo = new PageInfo<>(articleList);

			// 把返回给前段的Page替换成PageInfo，后面有空再优化吧
			Page pageData = Page.builder()
							.currentPage(pageInfo.getPageNum()).pageSize(pageSize).totalPages(pageInfo.getPages())
							.total((int) pageInfo.getTotal()).records(pageInfo.getList())
							.build();

			logger.info("获取文章列表成功：" + pageData);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, status, pageData);

		} catch (Exception e) {
			logger.error("获取文章列表发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取文章列表发生异常！");
		}
	}


	@Override
	public BaseResponse search(String searchText, Integer status, int currentPage, int pageSize) throws GlobalException {
		try {
			logger.info("开始搜索文章：" + searchText);
			String matchFields = "title,author,labels,brief";
			String highlightFields = "title,author,labels,brief";
			Map<String, Object> mustMatchs = new HashMap<>();
			mustMatchs.put("status", status);

			Page page = elasticsearchService.searchMatch(ArticleConstant.ARTICLE_INDEX_NAME, searchText, matchFields, mustMatchs, null, "publish_time", highlightFields, currentPage, pageSize);
			List<Map<String, Object>> records = (List<Map<String, Object>>) page.getRecords();

			records.forEach(a -> {
				Long id = Long.parseLong(a.get("id").toString());
				a.put("like_number", likeService.getLikeCount(id));
				Long newScanNumber = redisTemplate.opsForHyperLogLog().size(RedisKeyUtils.getScanNumberKey(id));
				a.put("scan_number", (int)a.get("scan_number") + newScanNumber.intValue());});
			page.setRecords(Converts.convertSnakeToCamel(records));

			logger.info("搜索文章成功：" + page);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, status, page);

		} catch (Exception e) {
			logger.error("搜索文章发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "搜索文章发生异常！");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse release(BaseRequest articleReq) throws GlobalException {
		try {
			logger.info("开始发布文章：" + articleReq);
			Article article = new Article(articleReq);
			article.setId(Snowflake.generateId());
			article.setStatus(0);
			articleMapper.save(article);
			logger.info("文章发布成功：" + articleReq.getTitle());
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("发布文章发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "发布文章发生异常！");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BaseResponse draft(BaseRequest articleReq) throws GlobalException {
		try {
			logger.info("开始保存草稿：" + articleReq);
			Article article = new Article(articleReq);
			article.setId(Snowflake.generateId());
			article.setStatus(1);
			articleMapper.save(article);
			logger.info("草稿保存成功：" + articleReq);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("保存草稿发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "保存草稿发生异常！");
		}
    }


	@Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public BaseResponse update(BaseRequest articleRequest) throws GlobalException {
		try {
			logger.info("开始更新文章：" + articleRequest);
			Article article = new Article();
			article.setId(articleRequest.getId());
			Optional<Article> repositoryArticle = Optional.ofNullable(articleMapper.findById(articleRequest.getId()));
			if(repositoryArticle.isPresent()) {
				article = repositoryArticle.get();
				updateProperty(article, articleRequest);
				articleMapper.update(article);
			}
			logger.info("文章更新成功：" + article);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("更新文章发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "更新文章发生异常！");
		}
    }

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse saveAboutMe(Article article) throws GlobalException {
		try {
			logger.info("开始保存关于我：" + article);
			articleMapper.deleteById(111111111111111111L);
			article.setId(111111111111111111L);
			article.setStatus(4);
			article.setTitle("关于我");
			article.setLabels("关于我");
			article.setAuthor("spartacus");
			article.setPublishTime(new Date());
			article.setCname("关于我");
			article.setFromWhere("原创");
			articleMapper.save(article);
			logger.info("保存关于我成功：" + article.getTitle());
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("保存关于我发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "保存关于我发生异常！");
		}
	}

}
