package com.xlc.spartacus.article.service.impl;

import com.xlc.spartacus.article.mapper.ArticleLikeMapper;
import com.xlc.spartacus.article.pojo.ArticleLike;
import com.xlc.spartacus.article.pojo.LikeStatus;
import com.xlc.spartacus.article.service.LikeService;
import com.xlc.spartacus.article.utils.RedisKeyUtils;
import com.xlc.spartacus.common.core.common.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service 
public class LikeServiceImpl implements LikeService {
    private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
 
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ArticleLikeMapper articleLikeMapper;


    @Override
    public void setLikeData(Long articleId, Integer userSequence, Boolean bit) {
        try {
            String key = RedisKeyUtils.getLikeKey(articleId);
            redisTemplate.opsForValue().setBit(key, userSequence, bit);
        } catch (Exception e) {
            logger.error("加载点赞数据发生异常！", e);
        }
    }

    @Override
    public CommonResponse like(Long articleId, Integer userSequence) {
        try {
            String key = RedisKeyUtils.getLikeKey(articleId);
            redisTemplate.opsForValue().setBit(key, userSequence, true);
            return CommonResponse.success();
        } catch (Exception e) {
            logger.error("点赞发生异常！", e);
        }
        return CommonResponse.failed();
    }
 
    @Override
    public CommonResponse unLike(Long articleId, Integer userSequence) {
        try {
            String key = RedisKeyUtils.getLikeKey(articleId);
            redisTemplate.opsForValue().setBit(key, userSequence, false);
            return CommonResponse.success();
        } catch (Exception e) {
            logger.error("取消点赞发生异常！", e);
        }
        return CommonResponse.failed();
    }

    @Override
    public CommonResponse checkOneLikeMember(ArticleLike articleLike) {
        try {
            String key = RedisKeyUtils.getLikeKey(articleLike.getArticleId());
            Boolean likeStatus = redisTemplate.opsForValue().getBit(key, articleLike.getUserSequence());
            articleLike.setStatus(likeStatus ? LikeStatus.LIKE.getCode() : LikeStatus.UNLIKE.getCode());
            return CommonResponse.success(articleLike);
        } catch (Exception e) {
            logger.error("判断是否点赞发生异常！", e);
            return CommonResponse.failed();
        }
    }

    @Override
    public CommonResponse checkMulLikeMember(List<ArticleLike> articleLikes) {
        try {
            articleLikes.forEach(al -> {
                String key = RedisKeyUtils.getLikeKey(al.getArticleId());
                Boolean likeStatus = redisTemplate.opsForValue().getBit(key, al.getUserSequence());
                al.setStatus(likeStatus ? LikeStatus.LIKE.getCode() : LikeStatus.UNLIKE.getCode());
            });
            return CommonResponse.success(articleLikes);
        } catch (Exception e) {
            logger.error("判断是否点赞发生异常！", e);
            return CommonResponse.failed();
        }
    }

    @Override
    public Integer getLikeCount(Long articleId) {
        try {
            String key = RedisKeyUtils.getLikeKey(articleId);
            if(redisTemplate.hasKey(key)) {
                Long count = (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
                return Integer.parseInt(count.toString());
            }
        } catch (Exception e) {
            logger.error("获取点赞数量发生异常！", e);
        }
        return 0;
    }
 
}