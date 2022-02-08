package com.xlc.spartacus.chat.listener;

import com.xlc.spartacus.chat.constant.ChatConstant;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.Resource;


/**
 * 断开链接 监听器
 *
 * @author xlc, since 2021
 */
@Component
public class DisConnectEventListener implements ApplicationListener<SessionDisconnectEvent>{

	@Resource
	private RedisTemplate redisTemplate;

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());

		//获取sessionId
		String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");

		System.out.println("断开连接：" + sessionId);

		//从redis的在线用户map删除已下线用户
		redisTemplate.opsForHash().delete(ChatConstant.CHAT_ONLINE_USERS, sessionId);
	}

}
