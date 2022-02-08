package com.xlc.spartacus.auth.core.code.sms;

import com.xlc.spartacus.auth.common.SmsUtils;
import com.xlc.spartacus.auth.constant.AuthConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 短信验证码发送器
 *
 * @author xlc, since 2021
 */
@Component("smsValidateCodeSender")
public class SmsValidateCodeSender {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private SmsUtils smsUtils;


	public void send(String mobile, String code) {
		logger.info("向手机"+mobile+"发送短信验证码: "+code);
		System.out.println("向手机"+mobile+"发送短信验证码: "+code);
		smsUtils.sendSmsCode(mobile, code);

		redisTemplate.opsForHash().put(AuthConstant.REDIS_SMSCODE_SEND_MOBILE_MAP, mobile, 1);
		redisTemplate.expire(AuthConstant.REDIS_SMSCODE_SEND_MOBILE_MAP, 30, TimeUnit.SECONDS);
	}

}
