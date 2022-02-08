package com.xlc.spartacus.chat.intercepter;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * http握手拦截器，可以通过这个类的方法获取resuest,和response
 *
 * @author xlc, since 2021
 */
@Component
public class HttpHandShakeIntercepter extends HttpSessionHandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {

		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			attributes.put("sessionId", sessionId);
		}
		
		return true;
	}

	
	
	@Override
	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
		}

	}

}
