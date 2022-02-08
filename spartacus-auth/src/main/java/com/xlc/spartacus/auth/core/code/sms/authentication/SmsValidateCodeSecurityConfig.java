package com.xlc.spartacus.auth.core.code.sms.authentication;

import com.xlc.spartacus.auth.config.CommonProperties;
import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信验证码安全配置
 *
 * @author xlc, since 2021
 */
@Component("smsValidateCodeSecurityConfig")
public class SmsValidateCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	
	@Autowired
	private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
	
	@Autowired
	private AuthenticationFailureHandler myAuthenticationFailureHandler;
	
	@Autowired
	private UserDetailsService myUserDetailsService;

	@Autowired
	private UserConnectionMapper userConnectionMapper;

	@Autowired
	private CommonProperties commonProperties;


	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		SmsValidateCodeAuthenticationFilter smsValidateCodeAuthenticationFilter = new SmsValidateCodeAuthenticationFilter();
		smsValidateCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		smsValidateCodeAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
		smsValidateCodeAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);

		SmsValidateCodeAuthenticationProvider smsValidateCodeAuthenticationProvider = new SmsValidateCodeAuthenticationProvider();
		smsValidateCodeAuthenticationProvider.setUserDetailsService(myUserDetailsService);
		smsValidateCodeAuthenticationProvider.setUserConnectionMapper(userConnectionMapper);
		smsValidateCodeAuthenticationProvider.setCommonProperties(commonProperties);
		
		/*
		 * 配置自定的两个组件：
		 * 	1、smsCodeAuthenticationProvider会被加到AuthenticationManager的集合里
		 * 	2、smsCodeAuthenticationFilter加载到过滤器链上
		 */
		http.authenticationProvider(smsValidateCodeAuthenticationProvider)
			.addFilterAfter(smsValidateCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
