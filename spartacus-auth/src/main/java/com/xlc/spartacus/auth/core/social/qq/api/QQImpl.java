package com.xlc.spartacus.auth.core.social.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

/**
 * QQ接口层实现
 *
 * @author xlc, since 2021
 */
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	//两个路径、三个常量（其中accessToken父类中已预留）
	private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";
	
	private static final String URL_GET_USERINFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
	
	private String appId;
	
	private String openId;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	public QQImpl(String accessToken, String appId) {
		//默认父类的restTemplate发请求时token会被放到header里，这里改成普通查询参数
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		
		this.appId = appId;
		
		String url = String.format(URL_GET_OPENID, accessToken);
		String result = getRestTemplate().getForObject(url, String.class);
		
		logger.info(result);
		
		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
	}
	
	
	@Override
	public QQUserInfo getUserInfo() {
		
		String url = String.format(URL_GET_USERINFO, appId, openId);
		String result = getRestTemplate().getForObject(url, String.class);
		
//		logger.info(result);
		
		QQUserInfo userInfo = null;
		try {
			userInfo = objectMapper.readValue(result, QQUserInfo.class);
			userInfo.setOpenId(openId);
			return userInfo;
		} catch (Exception e) {
			throw new RuntimeException("获取用户信息失败", e);
		}
	}

}
