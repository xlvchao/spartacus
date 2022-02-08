package com.xlc.spartacus.auth.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置
 * 读取application.properties里面所有spartacus.security开头的配置项
 *
 * @author xlc, since 2021
 */
@Data
@ConfigurationProperties(prefix = "spartacus.security")
public class SecurityProperties {
	
	private BrowserProperties browser = new BrowserProperties();
	
	private ValidateCodeProperties code = new ValidateCodeProperties();

	private SocialProperties social = new SocialProperties();

	private OAuth2Properties oauth2 = new OAuth2Properties();

}

