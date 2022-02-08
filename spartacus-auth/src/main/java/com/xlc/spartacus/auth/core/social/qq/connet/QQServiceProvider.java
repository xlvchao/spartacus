package com.xlc.spartacus.auth.core.social.qq.connet;

import com.xlc.spartacus.auth.core.social.qq.api.QQ;
import com.xlc.spartacus.auth.core.social.qq.api.QQImpl;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/** 
 * QQ认证服务器
 *
 * @author xlc, since 2021
 */
public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {

	private String appId;
	
	//将用户导向认证服务器，获取授权码
	private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	//申请令牌
	private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
	
	public QQServiceProvider(String appId, String appSecret) {
		super(new QQOAuth2Template(appId, appSecret, URL_AUTHORIZE, URL_ACCESS_TOKEN));
		this.appId = appId;
	}
	
	@Override
	public QQ getApi(String accessToken) {
		return new QQImpl(accessToken, appId);
	}

}
