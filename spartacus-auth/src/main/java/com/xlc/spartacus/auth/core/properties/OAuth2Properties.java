package com.xlc.spartacus.auth.core.properties;

import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class OAuth2Properties {
	
	private String jwtSigningKey = "spartacus";
	
	private OAuth2ClientProperties[] clients = {};
 
}
