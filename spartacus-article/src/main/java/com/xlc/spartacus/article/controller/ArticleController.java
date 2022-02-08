package com.xlc.spartacus.article.controller;

import com.xlc.spartacus.article.aspect.ArticleScan;
import com.xlc.spartacus.article.exception.GlobalException;
import com.xlc.spartacus.article.pojo.Article;
import com.xlc.spartacus.article.service.ArticleService;
import com.xlc.spartacus.article.service.CategoryService;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 接口层
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
	
	@Resource
	private CategoryService categoryService;

	@Resource
	private ArticleService articleService;


	@ArticleScan
	@RequestMapping("/getArticle/{id}")
	public BaseResponse getArticle(@PathVariable Long id, HttpServletRequest request) throws GlobalException {
		return articleService.getArticle(id);
	}

	@RequestMapping("/findByCriteria")
	public BaseResponse findByCriteria(Integer isLikeMost, Integer isReadMost) throws GlobalException {
		return articleService.findByCriteria(isLikeMost, isReadMost);
	}

	@RequestMapping("/findByCategory")
	public BaseResponse findByCategory(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return articleService.findByCategory(baseRequest.getCname(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/getArchive")
	public BaseResponse getArchive(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return articleService.findByStatus(0, baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/findByStatus")
	public BaseResponse findByStatus(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return articleService.findByStatus(baseRequest.getStatus(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/searchByKeywords")
	public BaseResponse searchByKeywords(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return articleService.search(baseRequest.getSearchText(), 0, baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/search")
	public BaseResponse search(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return articleService.search(baseRequest.getSearchText(), baseRequest.getStatus(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/getCategory", method = RequestMethod.GET)
	public BaseResponse getCategory() throws GlobalException {
		return categoryService.findAll();
    }
	
	@RequestMapping(value = "/release", method = RequestMethod.POST)
	public BaseResponse release(@ModelAttribute BaseRequest articleReq) throws GlobalException {
		return articleService.release(articleReq);
	}

    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public BaseResponse draft(@ModelAttribute BaseRequest articleReq) throws GlobalException {
        return articleService.draft(articleReq);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse update(@ModelAttribute BaseRequest articleReq) throws GlobalException {
        return articleService.update(articleReq);
    }

	@RequestMapping(value = "/saveAboutMe", method = RequestMethod.POST)
	public BaseResponse saveAboutMe(@ModelAttribute Article article) throws GlobalException {
		return articleService.saveAboutMe(article);
	}
    
}
