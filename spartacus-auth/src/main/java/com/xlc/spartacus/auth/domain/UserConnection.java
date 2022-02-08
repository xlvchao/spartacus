package com.xlc.spartacus.auth.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户实体类
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserConnection implements Serializable {
	private static final long serialVersionUID = 1L;

    private String userId; //username

	private String providerId;

	private String providerUserId; //username

	private Integer status; //0有效，1失效

	private String roles;

	private Integer rank;

	private String displayName;

	private String profileUrl;

	private String imageUrl; //headImage

	private String secret; //password

	private String accessToken;

	private String refreshToken;

	private Long expireTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //格式化前端传过来的时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8") //返回给前端的时间格式
	private Date addTime;

}
