package com.xlc.spartacus.chat.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * 订阅 监听器
 *
 * @author xlc, since 2021
 */
@Component
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent>{

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		System.out.println("【SubscribeEventListener监听器事件 类型】"+headerAccessor.getCommand().getMessageType());
		System.out.println("【SubscribeEventListener监听器事件 sessionId】"+headerAccessor.getSessionAttributes().get("sessionId"));
	}

}
