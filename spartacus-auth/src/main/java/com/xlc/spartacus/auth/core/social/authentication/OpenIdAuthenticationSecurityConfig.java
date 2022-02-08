/**
 * 
 */
package com.xlc.spartacus.auth.core.social.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 安全配置
 *
 * @author xlc, since 2021
 */
@Component
public class OpenIdAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	
	@Autowired
	private AuthenticationSuccessHandler spartacusAuthenticationSuccessHandler;
	
	@Autowired
	private AuthenticationFailureHandler spartacusAuthenticationFailureHandler;
	
	@Autowired
	private SocialUserDetailsService myUserDetailsService;
	
	@Autowired
	private UsersConnectionRepository usersConnectionRepository;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		OpenIdAuthenticationFilter OpenIdAuthenticationFilter = new OpenIdAuthenticationFilter();
		OpenIdAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		OpenIdAuthenticationFilter.setAuthenticationSuccessHandler(spartacusAuthenticationSuccessHandler);
		OpenIdAuthenticationFilter.setAuthenticationFailureHandler(spartacusAuthenticationFailureHandler);
		
		OpenIdAuthenticationProvider OpenIdAuthenticationProvider = new OpenIdAuthenticationProvider();
		OpenIdAuthenticationProvider.setUserDetailsService(myUserDetailsService);
		OpenIdAuthenticationProvider.setUsersConnectionRepository(usersConnectionRepository);
		
		http.authenticationProvider(OpenIdAuthenticationProvider)
			.addFilterAfter(OpenIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
	}

}
