package com.xlc.spartacus.article.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户点赞表 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLike {
 
    //被点赞文章id
    private Long articleId;

    //用户序列号
    private Integer userSequence;

    //点赞的状态：默认未点赞
    private Integer status = LikeStatus.UNLIKE.getCode();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss") //fastJson序列化时的格式
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format="yyyy-MM-dd HH:mm:ss") //fastJson序列化时的格式
    private Date updateTime;
}