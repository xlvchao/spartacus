package com.xlc.spartacus.auth.core.code;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 保存验证码的接口
 *
 * @author xlc, since 2021
 */
public interface ValidateCodeRepository {
 
	/**
	 * 保存验证码
	 * @param request
	 * @param code
	 * @param validateCodeType
	 */
	void save(ServletWebRequest request, ValidateCode code, ValidateCodeType validateCodeType);
	/**
	 * 获取验证码
	 * @param request
	 * @param validateCodeType
	 * @return
	 */
	ValidateCode get(ServletWebRequest request, ValidateCodeType validateCodeType);
	/**
	 * 移除验证码
	 * @param request
	 */
	void remove(ServletWebRequest request, ValidateCodeType validateCodeType);
 
}
