package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

//import org.springframework.boot.autoconfigure.social.SocialProperties;
import com.xlc.spartacus.auth.core.social.base.SocialProperties;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class WeixinProperties extends SocialProperties {
	
	/**
	 * 第三方id，用来决定发起第三方登录的url，默认是 weixin。
	 */
	private String providerId = "weixin";

}
