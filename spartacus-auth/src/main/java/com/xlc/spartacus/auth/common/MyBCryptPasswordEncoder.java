package com.xlc.spartacus.auth.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

/**
 * 自定义加、解密
 *
 * @author xlc, since 2021
 */
public class MyBCryptPasswordEncoder extends BCryptPasswordEncoder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
	

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || encodedPassword.length() == 0) {
			logger.warn("Empty encoded password");
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
			logger.warn("Encoded password does not look like BCrypt");
			return false;
		}
		return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	}

}
