package com.xlc.spartacus.article.utils;

public class RedisKeyUtils {
 
    //保存文章的点赞数据
    public static final String ARTICLE_LIKE_DATA_KEY_PREFIX = "ARTICLE_LIKE_DATA";

    //保存文章的浏览量
    public static final String ARTICLE_SCAN_NUMBER_KEY_PREFIX = "ARTICLE_SCAN_COUNT";

 
    public static String getLikeKey(Long articleId) {
        StringBuilder builder = new StringBuilder();
        builder.append(ARTICLE_LIKE_DATA_KEY_PREFIX);
        builder.append(":");
        builder.append(articleId);
        return builder.toString();
    }

    public static String getScanNumberKey(Long articleId) {
        StringBuilder builder = new StringBuilder();
        builder.append(ARTICLE_SCAN_NUMBER_KEY_PREFIX);
        builder.append(":");
        builder.append(articleId);
        return builder.toString();
    }
} 