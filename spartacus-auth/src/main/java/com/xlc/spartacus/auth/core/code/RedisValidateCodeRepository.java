package com.xlc.spartacus.auth.core.code;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.concurrent.TimeUnit;

/**
 * 将验证码存放redis中
 *
 * @author xlc, since 2021
 */
@Component("redisValidateCodeRepository")
public class RedisValidateCodeRepository implements ValidateCodeRepository {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	

	@Override
	public ValidateCode get(ServletWebRequest request, ValidateCodeType validateCodeType) {
		Object value = redisTemplate.opsForValue().get(buildKey(request, validateCodeType));
		return value == null ? null : (ValidateCode)value;
	}

	@Override
	public void save(ServletWebRequest request, ValidateCode code, ValidateCodeType validateCodeType) {
		redisTemplate.opsForValue().set(buildKey(request, validateCodeType), code, 30, TimeUnit.SECONDS);
	}

	@Override
	public void remove(ServletWebRequest request, ValidateCodeType validateCodeType) {
		System.out.println("删除验证码" + buildKey(request, validateCodeType));
		redisTemplate.delete(buildKey(request, validateCodeType));
	}

	private String buildKey(ServletWebRequest request, ValidateCodeType validateCodeType) {
		String deviceId = request.getHeader("deviceId");
		if(StringUtils.isBlank(deviceId)) {
			throw new ValidateCodeException("请在请求头中携带deviceId参数");
		}
		return "code:" + validateCodeType.toString().toLowerCase() + ":" + deviceId;
	}

}
