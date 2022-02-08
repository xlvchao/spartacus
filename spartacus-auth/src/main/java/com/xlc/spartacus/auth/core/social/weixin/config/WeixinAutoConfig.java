package com.xlc.spartacus.auth.core.social.weixin.config;

import com.xlc.spartacus.auth.core.properties.SecurityProperties;
import com.xlc.spartacus.auth.core.properties.WeixinProperties;
import com.xlc.spartacus.auth.core.social.base.SocialAutoConfigurerAdapter;
import com.xlc.spartacus.auth.core.social.weixin.connect.WeixinConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

//import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;

/**
 * 微信登录配置
 *
 * @author xlc, since 2021
 */
@Configuration
@ConditionalOnProperty(prefix = "spartacus.security.social.weixin", name = "app-id")
public class WeixinAutoConfig extends SocialAutoConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;


	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		WeixinProperties weixinConfig = securityProperties.getSocial().getWeixin();
		return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
				weixinConfig.getAppSecret());
	}
	
}
