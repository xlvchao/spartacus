package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class SocialProperties {
	
	private String filterProcessesUrl = "/auth";

	private QQProperties qq = new QQProperties();

	private WeixinProperties weixin = new WeixinProperties();
	
}
