package com.xlc.spartacus.chat.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * 取消订阅 监听器
 *
 * @author xlc, since 2021
 */
@Component
public class UnSubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent>{

	@Override
	public void onApplicationEvent(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		System.out.println("【UnSubscribeEventListener监听器事件 类型】"+headerAccessor.getCommand().getMessageType());
		System.out.println("【UnSubscribeEventListener监听器事件 sessionId】"+headerAccessor.getSessionAttributes().get("sessionId"));
	}
}
