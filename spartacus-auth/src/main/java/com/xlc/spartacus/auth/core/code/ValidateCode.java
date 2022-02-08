package com.xlc.spartacus.auth.core.code;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码定义
 *
 * @author xlc, since 2021
 */
public class ValidateCode implements Serializable{ //实现序列化
	
	private static final long serialVersionUID = 1L;

	private String code;
	
	private LocalDateTime expireTime;

	public ValidateCode() {

	}
	
	public ValidateCode(String code, LocalDateTime expireTime) {
		this.code = code;
		this.expireTime = expireTime;
	}

	public ValidateCode(String code, int expireIn){
		this.code = code;
		this.expireTime = this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
	}
	
	public boolean isExpried() {
		return LocalDateTime.now().isAfter(expireTime);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(LocalDateTime expireTime) {
		this.expireTime = expireTime;
	}
	
}
