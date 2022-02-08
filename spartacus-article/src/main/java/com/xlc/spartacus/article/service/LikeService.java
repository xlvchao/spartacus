package com.xlc.spartacus.article.service;

import com.xlc.spartacus.article.pojo.ArticleLike;
import com.xlc.spartacus.common.core.common.CommonResponse;

import java.util.List;

public interface LikeService {

    /**
     * 加载点赞数据
     * @param articleId
     * @param userSequence
     */
    void setLikeData(Long articleId, Integer userSequence, Boolean bit);
 
    /**
     * 点赞，状态为1
     * @param articleId
     * @param userSequence
     */
    CommonResponse like(Long articleId, Integer userSequence);

    /**
     * 取消点赞，状态为0
     * @param articleId
     * @param userSequence
     */
    CommonResponse unLike(Long articleId, Integer userSequence);

    /**
     * 用户是否点赞了
     * @param articleLike
     */
    CommonResponse checkOneLikeMember(ArticleLike articleLike);

    /**
     * 用户是否点赞了
     * @param articleLikes
     * @return
     */
    CommonResponse checkMulLikeMember(List<ArticleLike> articleLikes);

    /**
     * 获取该文章的点赞数
     * @param articleId
     */
    Integer getLikeCount(Long articleId);
}