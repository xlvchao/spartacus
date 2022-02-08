package com.xlc.spartacus.auth.core.code.sms.authentication;

import com.xlc.spartacus.auth.config.CommonProperties;
import com.xlc.spartacus.auth.domain.User;
import com.xlc.spartacus.auth.domain.UserConnection;
import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 处理逻辑写在这里
 *
 * @author xlc, since 2021
 */
@Data
public class SmsValidateCodeAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailsService;

	private UserConnectionMapper userConnectionMapper;

	private CommonProperties commonProperties;

	/**
	 * 处理逻辑
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		SmsValidateCodeAuthenticationToken authenticationToken = (SmsValidateCodeAuthenticationToken) authentication;

		String mobile = (String) authenticationToken.getPrincipal();
		System.out.println("mobile=====" + mobile);

		//根据手机号拿用户信息
		UserDetails user = userDetailsService.loadUserByUsername(mobile);
		if (user == null) {
			user =  new User(mobile, "", commonProperties.getPhoneHeadImage(),
					true, true, true, true,
					AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));

			//落表，否则后面刷新token时，查不到数据
			UserConnection userInfo = new UserConnection();
			userInfo.setUserId(mobile);
			userInfo.setProviderId("spartacus");
			userInfo.setProviderUserId(mobile);
			userInfo.setStatus(0);
			userInfo.setSecret("");
			userInfo.setDisplayName(mobile);
			userInfo.setImageUrl(commonProperties.getPhoneHeadImage());
			userInfo.setRoles("ROLE_USER");
			userConnectionMapper.insertUserConnection(userInfo);
		}


		SmsValidateCodeAuthenticationToken authenticationResult = new SmsValidateCodeAuthenticationToken(user, user.getAuthorities());
		
		authenticationResult.setDetails(authenticationToken.getDetails());

		return authenticationResult;
	}

	/**
	 * Token是SmsAuthenticationToken，AuthenticationManager才会调用SmsAuthenticationProvider
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return SmsValidateCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
