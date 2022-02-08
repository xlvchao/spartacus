package com.xlc.spartacus.auth.service;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * 实现ConnectionSignUp接口后，Spring Social会直接插入数据
 *
 * 若配置了DemoConnectionSignUp，就会默认将qq/wx用户注册为一个新用户，而不再跳转到注册或绑定页面
 *
 * 根据社交用户信息默认创建用户并返回用户唯一标识，这样就跳过社交登录注册或绑定页面，这里使用OpenId作为userId
 *
 * @author xlc, since 2021
 */
@Component
public class MyConnectionSignUp implements ConnectionSignUp {
	
	@Override
	public String execute(Connection<?> connection) {
        return connection.getKey().getProviderUserId();
	}

}
