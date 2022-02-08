package com.xlc.spartacus.auth.core.code;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * 验证码处理器抽象实现
 *
 * @author xlc, since 2021
 */
public abstract class AbstractValidateCodeProcessor<C extends ValidateCode> implements ValidateCodeProcessor {
	
	@Autowired
	@Qualifier("redisValidateCodeRepository")
	private ValidateCodeRepository validateCodeRepository;
	
	/**
	 * 收集系统中所有的 {@link ValidateCodeGenerator} 接口的实现。
	 */
	@Autowired
	private Map<String, ValidateCodeGenerator> validateCodeGenerators;



	@Override
	public void create(ServletWebRequest request) throws Exception {
		C validateCode = generate(request);
		save(request, validateCode);
		send(request, validateCode);
	}

	
	/**
	 * 生成校验码
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private C generate(ServletWebRequest request) {
		String type = getValidateCodeType(request).toString().toLowerCase();
		String generatorName = type + ValidateCodeGenerator.class.getSimpleName();
		ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(generatorName);
		if (validateCodeGenerator == null) {
			throw new ValidateCodeException("验证码生成器" + generatorName + "不存在");
		}
		return (C) validateCodeGenerator.generate(request);
	}

	
	/**
	 * 保存校验码
	 * 
	 * @param request
	 * @param validateCode
	 */
	private void save(ServletWebRequest request, C validateCode) {
		//这里必须重新new，否则遇到包含图片的验证码时，图片无法序列化到redis类型的session中
		ValidateCode code = new ValidateCode(validateCode.getCode(), validateCode.getExpireTime());
		validateCodeRepository.save(request, code, getValidateCodeType(request));
	}
	

	/**
	 * 发送校验码，由子类实现
	 * 
	 * @param request
	 * @param validateCode
	 * @throws Exception
	 */
	protected abstract void send(ServletWebRequest request, C validateCode) throws Exception;

	
	/**
	 * 根据请求的url获取校验码的类型
	 * 
	 * @param request
	 * @return
	 */
	private ValidateCodeType getValidateCodeType(ServletWebRequest request) {
		String type = StringUtils.substringBefore(getClass().getSimpleName(), "ValidateCodeProcessor");
		return ValidateCodeType.valueOf(type.toUpperCase());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void validate(ServletWebRequest request) {
		String codeInRequest;
		ValidateCodeType codeType = getValidateCodeType(request);
		C codeInStore = (C) validateCodeRepository.get(request, codeType);
		try {
			codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), codeType.getParamNameOnValidate());
		} catch (ServletRequestBindingException e) {
			throw new ValidateCodeException("获取验证码的值失败");
		}
		if (StringUtils.isBlank(codeInRequest)) {
			throw new ValidateCodeException(codeType + "验证码的值不能为空");
		}
		if (codeInStore == null) {
			throw new ValidateCodeException(codeType + "验证码不存在");
		}
		if (codeInStore.isExpried()) {
			validateCodeRepository.remove(request, codeType);
			throw new ValidateCodeException(codeType + "验证码已过期");
		}
		if (!StringUtils.equalsIgnoreCase(codeInStore.getCode(), codeInRequest)) {
			throw new ValidateCodeException(codeType + "验证码不匹配");
		}
		validateCodeRepository.remove(request, codeType);
	}

}
