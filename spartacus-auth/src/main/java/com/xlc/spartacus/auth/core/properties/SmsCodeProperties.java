package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class SmsCodeProperties {
	
	private int length = 6;
	private int expireIn = 60;

	private String url; //哪些url需要校验验证码，多个以英文逗号分隔

}
