package com.xlc.spartacus.auth.core.social.weixin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Weixin API调用模板， scope为Request的Spring bean, 根据当前用户的accessToken创建。
 *
 * @author xlc, since 2021
 */
public class WeixinImpl extends AbstractOAuth2ApiBinding implements Weixin {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private static final String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?openid=";
	
	/**
	 * @param accessToken
	 */
	public WeixinImpl(String accessToken) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
	}
	
	
	/**
	 * 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
	 */
	@Override
	protected List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		messageConverters.remove(0);
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return messageConverters;
	}

	
	/**
	 * 获取微信用户信息。
	 */
	@Override
	public WeixinUserInfo getUserInfo(String openId) {
		String url = URL_GET_USER_INFO + openId;
		String response = getRestTemplate().getForObject(url, String.class);
		if(StringUtils.contains(response, "errcode")) {
			return null;
		}
		WeixinUserInfo userInfo = null;
		try {
			userInfo = objectMapper.readValue(response, WeixinUserInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

}
