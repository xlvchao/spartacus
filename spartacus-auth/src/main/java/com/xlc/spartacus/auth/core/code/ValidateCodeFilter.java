package com.xlc.spartacus.auth.core.code;

import com.xlc.spartacus.auth.constant.AuthConstant;
import com.xlc.spartacus.auth.core.properties.SecurityConstants;
import com.xlc.spartacus.auth.core.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 验证码过滤器
 *
 * @author xlc, since 2021
 */
@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

	/**
	 * 验证码校验失败处理器
	 */
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;
	/**
	 * 系统配置信息
	 */
	@Autowired
	private SecurityProperties securityProperties;
	/**
	 * 系统中的校验码处理器
	 */
	@Autowired
	private ValidateCodeProcessorHolder validateCodeProcessorHolder;
	/**
	 * 存放所有需要校验验证码的url
	 */
	private Map<String, ValidateCodeType> urlMap = new HashMap<>();
	/**
	 * 验证请求url与配置的url是否匹配的工具类
	 */
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;


	/**
	 * 初始化要拦截的url配置信息
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();

		urlMap.put(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM, ValidateCodeType.IMAGE);
		addUrlToMap(securityProperties.getCode().getImage().getUrl(), ValidateCodeType.IMAGE);

		urlMap.put(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE, ValidateCodeType.SMS);
		addUrlToMap(securityProperties.getCode().getSms().getUrl(), ValidateCodeType.SMS);
	}

	/**
	 * 将系统中配置的需要校验验证码的URL根据校验的类型放入map
	 *
	 * @param urlString
	 * @param type
	 */
	protected void addUrlToMap(String urlString, ValidateCodeType type) {
		if (StringUtils.isNotBlank(urlString)) {
			String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString, ",");
			for (String url : urls) {
				urlMap.put(url, type);
			}
		}
	}

	/**
	 * 获取校验码的类型，如果当前请求不需要校验，则返回null
	 *
	 * @param request
	 * @return
	 */
	private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
		ValidateCodeType result = null;
		if (!StringUtils.equalsIgnoreCase(request.getMethod(), "get")) {
			Set<String> urls = urlMap.keySet();
			for (String url : urls) {
				if (pathMatcher.match(url, request.getRequestURI())) {
					result = urlMap.get(url);
				}
			}
		}
		return result;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String mobile = null;
		ValidateCodeType type = getValidateCodeType(request);

		if (type != null) {
			logger.info("校验请求(" + request.getRequestURI() + ")中的验证码类型：" + type);
			try {
				if(ValidateCodeType.SMS.equals(type)) {
					mobile = ServletRequestUtils.getRequiredStringParameter(request, "mobile");
					if(!redisTemplate.opsForHash().hasKey(AuthConstant.REDIS_SMSCODE_SEND_MOBILE_MAP, mobile)) {
						throw new ValidateCodeException("接受验证码的手机号与登陆手机号不一致！");
					}
				}

				validateCodeProcessorHolder.findValidateCodeProcessor(type).validate(new ServletWebRequest(request, response));
				logger.info("验证码校验通过");

			} catch (ValidateCodeException exception) {
				authenticationFailureHandler.onAuthenticationFailure(request, response, exception);
				return;

			} finally {
				if(ValidateCodeType.SMS.equals(type)) {
					if(redisTemplate.opsForHash().hasKey(AuthConstant.REDIS_SMSCODE_SEND_MOBILE_MAP, mobile)) {
						redisTemplate.opsForHash().delete(AuthConstant.REDIS_SMSCODE_SEND_MOBILE_MAP, mobile);
					}
				}
			}
		}
		chain.doFilter(request, response);
	}
	
	
}
