package com.xlc.spartacus.auth.core.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认的授权配置管理器
 *
 * @author xlc, since 2021
 */
@Component
public class MyAuthorizeConfigManager implements AuthorizeConfigManager {

	@Autowired
	private List<AuthorizeConfigProvider> authorizeConfigProviders;

	/* (non-Javadoc)
	 * @see com.spartacus.security.core.authorize.AuthorizeConfigManager#config(org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry)
	 */
	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		for (AuthorizeConfigProvider authorizeConfigProvider : authorizeConfigProviders) {
			authorizeConfigProvider.config(config);
		}
		
		config.anyRequest().authenticated();
	}

}
