package com.xlc.spartacus.comment.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/**
 * 实体
 *
 * @author xlc, since 2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Comment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@JsonSerialize(using= ToStringSerializer.class)
	private Long refId;// 父评论（一级评论）id

	@JsonSerialize(using= ToStringSerializer.class)
	private Long frontId;// 子评论（二级评论）链上的上一条

	@JsonSerialize(using= ToStringSerializer.class)
	private Long rearId;// 子评论（二级评论）链上的下一条

	@JsonSerialize(using= ToStringSerializer.class)
	private Long articleId; // 对应文章的id

    private String articleTitle; //文章标题
	
    private String content;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    private Integer level = 1; //默认是1即一级评论，2的话表示二级评论

//	@Transient //不序列化
	private TreeMap<Long, Comment> subComments; //二级评论列表
	
    private Integer status = 0; //审核状态，0是待审核，1是通过，2是已删除
	
    private String ip;
	
    private String ipCity;
	
	//社交用户信息
	private String providerId; //账号类型，qq/weixin
	
	private String providerUserId; //QQ、微信共用
	
    private String nickname;

    private String headImg;

//	@Transient //不序列化
	private Integer pageStatus;//审核状态，0是待审核页，1是已通过页，2是已删除页
}
