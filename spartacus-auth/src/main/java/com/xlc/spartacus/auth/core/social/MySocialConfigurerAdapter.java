package com.xlc.spartacus.auth.core.social;

import com.xlc.spartacus.auth.core.properties.SecurityProperties;
import com.xlc.spartacus.auth.core.social.support.MySpringSocialConfigurer;
import com.xlc.spartacus.auth.core.social.support.SocialAuthenticationFilterPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * 这里出现了一个问题：扫码注册后，再次扫码还是要跳转的注册页面
 *
 * 断点调试后发现，一定要加 @Order(1)，否则SocialAuthenticationProvider中的toUserId会
 *
 * 默认调用用InMemoryUsersConnectionRepository，而不使用这里实例化的的JdbcUsersConnectionRepository
 *
 * @author xlc, since 2021
 */
@Order(1)
@Configuration
@EnableSocial
public class MySocialConfigurerAdapter extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired(required = false) //并不一定会提供
	private ConnectionSignUp connectionSignUp;

	@Autowired(required = false)
	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;
	
	
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix("tb_");
		if(connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}
	

	/**
	 * 实例化一个自定义的过滤器配置（MySpringSocialConfigurer）
	 *  
	 * @return
	 */
	@Bean
	public SpringSocialConfigurer mySocialSecurityConfig() {
		String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
		MySpringSocialConfigurer configurer = new MySpringSocialConfigurer(filterProcessesUrl);
		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
		configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}

}
