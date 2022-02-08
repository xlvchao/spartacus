package com.xlc.spartacus.auth.core.social.qq.config;

import com.xlc.spartacus.auth.core.properties.QQProperties;
import com.xlc.spartacus.auth.core.properties.SecurityProperties;
import com.xlc.spartacus.auth.core.social.base.SocialAutoConfigurerAdapter;
import com.xlc.spartacus.auth.core.social.qq.connet.QQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

/**
 * 配置QQ链接工厂
 *
 * @author xlc, since 2021
 */
@Configuration
@ConditionalOnProperty(prefix = "spartacus.security.social.qq", name = "app-id")
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;

	
	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		QQProperties qqConfig = securityProperties.getSocial().getQq();
		return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(), qqConfig.getAppSecret());
	}

}
