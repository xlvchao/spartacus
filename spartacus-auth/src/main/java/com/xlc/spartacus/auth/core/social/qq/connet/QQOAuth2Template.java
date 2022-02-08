package com.xlc.spartacus.auth.core.social.qq.connet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * 1、原生的OAuth2Template里面的useParametersForClientAuthentication是false，即发请求时不带上client_id、client_secret，但是QQ官方需要带上
 *
 * 2、原生的OAuth2Template里面的RestTemplate的MessageConverters包含的Converter无法处理text/html这种content-type，所以这里要自定义处理
 *
 * 3、原生的OAuth2Template里面的postForAccessGrant方法的请求参数是map格式，但是QQ官方是直接拼接字符串，所以这里要自定义处理
 *
 * @author xlc, since 2021
 */
public class QQOAuth2Template extends OAuth2Template {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		//带上client_id、client_secret
		setUseParametersForClientAuthentication(true);
	}
	
	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		//添加处理content-type为text/html的converter
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}
	
	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		//请求方式按QQ的逻辑做一个映射
		String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		
		logger.info("获取accessToke的响应："+responseStr);
		
		String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(responseStr, "&");
		
		String accessToken = StringUtils.substringAfterLast(items[0], "=");
		Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
		String refreshToken = StringUtils.substringAfterLast(items[2], "=");
		
		return new AccessGrant(accessToken, null, refreshToken, expiresIn);
	}
	
}
