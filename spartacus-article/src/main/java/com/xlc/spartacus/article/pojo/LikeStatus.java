package com.xlc.spartacus.article.pojo;

import lombok.Getter;
 
/** 
 * 用户点赞的状态 
 */ 
@Getter 
public enum LikeStatus {
    LIKE(1, "点赞"),
    UNLIKE(0, "取消点赞/未点赞");

    private Integer code; 
 
    private String msg; 
 
    LikeStatus(Integer code, String msg) {
        this.code = code; 
        this.msg = msg; 
    } 
} 