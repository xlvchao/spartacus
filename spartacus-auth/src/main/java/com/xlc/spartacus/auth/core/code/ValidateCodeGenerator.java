package com.xlc.spartacus.auth.core.code;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码生成器定义
 *
 * @author xlc, since 2021
 */
public interface ValidateCodeGenerator {

	ValidateCode generate(ServletWebRequest request);
	
}
