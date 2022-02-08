package com.xlc.spartacus.auth.core.social.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 自定义MySpringSocialConfigurer，覆盖postProcess方法，然后即可使用配置的QQ登录地址（即QQ提供商的回调地址）
 *
 * SocialAuthenticationFilter中QQ默认的访问地址前缀是/auth
 *
 * 继承默认的社交登录配置，加入自定义的后处理逻辑
 *
 * @author xlc, since 2021
 */
public class MySpringSocialConfigurer extends SpringSocialConfigurer {

	@Getter	@Setter
	private String filterProcessesUrl;

	@Getter @Setter
	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	public MySpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.config.annotation.SecurityConfigurerAdapter#postProcess(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		if (socialAuthenticationFilterPostProcessor != null) {
			socialAuthenticationFilterPostProcessor.process(filter);
		}
		return (T) filter;
	}

}
