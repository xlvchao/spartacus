package com.xlc.spartacus.chat.listener;

import com.xlc.spartacus.chat.constant.ChatConstant;
import com.xlc.spartacus.chat.service.CommonUtilService;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.common.Response;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 创建连接 监听器
 *
 * @author xlc, since 2021
 */
@Component
public class ConnectEventListener implements ApplicationListener<SessionConnectEvent>{

	@Resource
	private CommonUtilService commonUtilService;

	@Resource
	private RedisTemplate redisTemplate;

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());

		//获取sessionId
		String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");

		//获取token
		List<String> accessTokenList = headerAccessor.toNativeHeaderMap().get("accessToken");
		String accessToken = accessTokenList != null && accessTokenList.size()>0 ? accessTokenList.get(0) : null;

		//权限校验
		CommonResponse checkResult = commonUtilService.checkTokenValid(accessToken);
		if(Response.SUCCESS.getCode().equals(checkResult.getCode())) {
			//缓存当前在线用户
			redisTemplate.opsForHash().putIfAbsent(ChatConstant.CHAT_ONLINE_USERS, sessionId, checkResult.getData());

			//缓存连接过的用户
			Map<String, Object> userInfo = (Map<String, Object>) checkResult.getData();
			redisTemplate.opsForSet().add(ChatConstant.CHAT_HISTORY_USERS, userInfo.get("providerUserId"));
		}

	}
}
