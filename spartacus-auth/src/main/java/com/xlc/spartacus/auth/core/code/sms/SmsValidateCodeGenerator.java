package com.xlc.spartacus.auth.core.code.sms;

import com.xlc.spartacus.auth.core.code.ValidateCode;
import com.xlc.spartacus.auth.core.code.ValidateCodeGenerator;
import com.xlc.spartacus.auth.core.properties.SecurityProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 短信验证码生成器
 *
 * @author xlc, since 2021
 */
@Component("smsValidateCodeGenerator")
public class SmsValidateCodeGenerator implements ValidateCodeGenerator {

	@Autowired
	private SecurityProperties securityProperties;
	
	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}
	
	
	@Override
	public ValidateCode generate(ServletWebRequest request) {
		String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSms().getLength());
		return new ValidateCode(code, securityProperties.getCode().getSms().getExpireIn());
	}

}
