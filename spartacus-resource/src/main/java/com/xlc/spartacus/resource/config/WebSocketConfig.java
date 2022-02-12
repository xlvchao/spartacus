package com.xlc.spartacus.resource.config;

import com.xlc.spartacus.resource.intercepter.SocketChannelIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.annotation.Resource;


/**
 * websocket配置
 *
 * @author xlc, since 2021
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{

	@Resource
	private CommonProperties commonProperties;

	@Resource
	private SocketChannelIntercepter socketChannelIntercepter;

	/**
	 * 注册端点，发布或者订阅消息的时候需要连接此端点
	 * setAllowedOrigins 非必须，*表示允许其他域进行连接
	 * withSockJS  表示开始sockejs支持
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/endpoint").setAllowedOrigins("*").withSockJS();
	}

	/**
	 * 配置消息代理(中介)
	 * enableSimpleBroker 服务端推送给客户端的路径前缀(客户端需要订阅此地址)
	 * setApplicationDestinationPrefixes  客户端发送数据给服务器端的一个前缀
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//基于rabbitmq的stomp消息代理
		registry.enableStompBrokerRelay("/topic/")
				.setRelayHost(commonProperties.getRelayHost())
				.setRelayPort(commonProperties.getRelayPort())
				.setClientLogin(commonProperties.getClientLogin())
				.setClientPasscode(commonProperties.getClientPasscode())
				.setSystemLogin(commonProperties.getSystemLogin())
				.setSystemPasscode(commonProperties.getSystemPasscode())
				.setVirtualHost(commonProperties.getVirtualHost());

		//registry.setApplicationDestinationPrefixes("/send");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(socketChannelIntercepter);
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.interceptors(socketChannelIntercepter);
	}


}


