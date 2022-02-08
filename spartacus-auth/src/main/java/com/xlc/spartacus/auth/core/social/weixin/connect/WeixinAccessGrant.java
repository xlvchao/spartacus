package com.xlc.spartacus.auth.core.social.weixin.connect;

import org.springframework.social.oauth2.AccessGrant;

/**
 * 与标准OAuth2协议不同，微信在获取access_token时会同时返回openId，不像QQ，没有单独的通过accessToken换取openId的服务，
 *
 * 所以在这里继承了标准AccessGrant，添加了openId字段，作为对微信access_token信息的封装。
 *
 * @author xlc, since 2021
 */
public class WeixinAccessGrant extends AccessGrant {
	
	private static final long serialVersionUID = -7243374526633186782L;
	
	private String openId;
	
	public WeixinAccessGrant() {
		super("");
	}

	public WeixinAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
		super(accessToken, scope, refreshToken, expiresIn);
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
}
