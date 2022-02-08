package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class ValidateCodeProperties {
	
	private ImageCodeProperties image = new ImageCodeProperties();
	
	private SmsCodeProperties sms = new SmsCodeProperties();

}
