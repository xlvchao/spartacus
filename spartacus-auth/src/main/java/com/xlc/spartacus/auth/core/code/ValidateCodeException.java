package com.xlc.spartacus.auth.core.code;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码校验异常
 *
 * @author xlc, since 2021
 */
public class ValidateCodeException extends AuthenticationException {

	private static final long serialVersionUID = -7285211528095468156L;

	public ValidateCodeException(String msg) {
		super(msg);
	}

}
