package com.xlc.spartacus.article.controller;

import com.xlc.spartacus.article.pojo.ArticleLike;
import com.xlc.spartacus.article.service.LikeService;
import com.xlc.spartacus.common.core.common.CommonResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 接口层
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/articleLike")
public class ArticleLikeController {
	
	@Resource
	private LikeService likeService;


	@RequestMapping("/like")
	public CommonResponse like(Long articleId, Integer userSequence) {
		return likeService.like(articleId, userSequence);
	}

	@RequestMapping("/unLike")
	public CommonResponse unLike(Long articleId, Integer userSequence) {
		return likeService.unLike(articleId, userSequence);
	}

	@RequestMapping("/checkOneLikeMember")
	public CommonResponse checkOneLikeMember(ArticleLike articleLike) {
		return likeService.checkOneLikeMember(articleLike);
	}

	@RequestMapping(value = "/checkMulLikeMember", method = RequestMethod.POST)
	public CommonResponse checkMulLikeMember(@RequestBody List<ArticleLike> articleLikes) {
		return likeService.checkMulLikeMember(articleLikes);
	}

}
