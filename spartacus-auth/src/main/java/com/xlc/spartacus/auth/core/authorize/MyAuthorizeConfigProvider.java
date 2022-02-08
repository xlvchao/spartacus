package com.xlc.spartacus.auth.core.authorize;

import com.xlc.spartacus.auth.core.properties.SecurityConstants;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

/**
 * 核心模块的授权配置提供器，安全模块涉及的url的授权配置在这里
 *
 * @author xlc, since 2021
 */
@Component
public class MyAuthorizeConfigProvider implements AuthorizeConfigProvider {

	@Override
	public boolean config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		config.antMatchers(
				SecurityConstants.DEFAULT_VALIDATE_CODE_IMAGE_URL,
				SecurityConstants.DEFAULT_VALIDATE_CODE_SMS_URL,
				SecurityConstants.DEFAULT_GET_PASS_RSA_PUBLIC_KEY_URL,
				SecurityConstants.DEFAULT_GET_AUTH_RSA_PUBLIC_KEY_URL,
				SecurityConstants.AUTH_LOG_OUT_URL
		).permitAll();

		return false;
	}

}
