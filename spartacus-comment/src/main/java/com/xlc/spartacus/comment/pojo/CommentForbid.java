package com.xlc.spartacus.comment.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
public class CommentForbid implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	private Integer forbidType = 0; //0表示禁的是id，1表示禁的是ip
	
	//ip地址信息
	private String ip;
	
	private String ipCity;
	
	//社交信息
	private String providerId; //账号类型，qq/weixin
	
	private String providerUserId; //QQ、微信共用
	
    private String nickname;

    private String headImg;
	
	
    private String reason;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operateTime;
	
}
