package com.xlc.spartacus.auth.core.code.image.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 图片验证码登录方式安全配置
 *
 * @author xlc, since 2021
 */
@Component("imageValidateCodeSecurityConfig")
public class ImageValidateCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired
	private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler myAuthenticationFailureHandler;

	@Autowired
	private UserDetailsService myUserDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;


	@Override
	public void configure(HttpSecurity http) throws Exception {

		ImageValidateCodeAuthenticationFilter imageValidateCodeAuthenticationFilter = new ImageValidateCodeAuthenticationFilter();
		imageValidateCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		imageValidateCodeAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
		imageValidateCodeAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);

		ImageValidateCodeAuthenticationProvider imageValidateCodeAuthenticationProvider = new ImageValidateCodeAuthenticationProvider();
		imageValidateCodeAuthenticationProvider.setUserDetailsService(myUserDetailsService);
		imageValidateCodeAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		imageValidateCodeAuthenticationProvider.setRedisTemplate(redisTemplate);

		http.authenticationProvider(imageValidateCodeAuthenticationProvider)
				.addFilterAfter(imageValidateCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
}
