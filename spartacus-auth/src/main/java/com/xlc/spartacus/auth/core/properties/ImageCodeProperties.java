package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class ImageCodeProperties {
	
	private int length = 4;
	private int expireIn = 60;
	
	private int width = 67;
	private int height = 23;
	
	private String url; //哪些url需要校验验证码，多个以英文逗号分隔
	
}
