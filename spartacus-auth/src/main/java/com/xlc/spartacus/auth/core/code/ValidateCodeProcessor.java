package com.xlc.spartacus.auth.core.code;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 校验码处理器 定义
 *
 * @author xlc, since 2021
 */
public interface ValidateCodeProcessor {


	/**
	 * 创建校验码、保存及发送
	 * 
	 * @param request
	 * @throws Exception
	 */
	void create(ServletWebRequest request) throws Exception;

	/**
	 * 校验验证码
	 * 
	 * @param servletWebRequest
	 * @throws Exception
	 */
	void validate(ServletWebRequest servletWebRequest);

}
