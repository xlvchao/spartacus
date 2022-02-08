package com.xlc.spartacus.auth.core.social.weixin.api;

/**
 * 微信API调用接口
 *
 * @author xlc, since 2021
 */
public interface Weixin {

	WeixinUserInfo getUserInfo(String openId);
	
}
