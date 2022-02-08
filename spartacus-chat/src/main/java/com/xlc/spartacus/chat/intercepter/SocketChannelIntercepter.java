package com.xlc.spartacus.chat.intercepter;

import com.xlc.spartacus.chat.service.CommonUtilService;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.common.Response;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompConversionException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 频道拦截器
 *
 * 在消息成功发送到频道之前，执行一些操作（消息经由Controller层发送过来的）
 *
 * @author xlc, since 2021
 */
@Component
public class SocketChannelIntercepter extends ChannelInterceptorAdapter{

	private MessageConverter converter = new SimpleMessageConverter();

	@Resource
	private CommonUtilService commonUtilService;

	@Resource
	private RedisTemplate redisTemplate;


	/**
	 * 在完成发送之后进行调用，不管是否有异常发生，一般用于资源清理
	 */
	@Override
	public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
		super.afterSendCompletion(message, channel, sent, ex);
	}

	
	/**
	 * 在消息被推送到频道 之前调用
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

		/*//路径匹配器
		PathMatcher pathMatcher = new AntPathMatcher();

		//获取订阅地址
		String subscribeTopic = headerAccessor.getDestination();

		//权限校验-校验消息发送接口
		if (SimpMessageType.MESSAGE.equals(headerAccessor.getMessageType()) && pathMatcher.match("/queue/**", subscribeTopic)
																			&& !pathMatcher.match("/queue/onlineUser", subscribeTopic)) {
			//获取token
			List<String> accessTokenList = headerAccessor.toNativeHeaderMap().get("accessToken");
			String accessToken = accessTokenList != null && accessTokenList.size()>0 ? accessTokenList.get(0) : null;
			System.out.println("发送前accessToken： " + accessToken);

			//获取sessionId
			List<String> sessionIdList = headerAccessor.toNativeHeaderMap().get("sessionId");
			String sessionId = sessionIdList != null && sessionIdList.size()>0 ? sessionIdList.get(0) : null;
			System.out.println("发送前sessionId： " + sessionId);

			//校验token
			CommonResult checkResult = commonUtilService.checkTokenValid(accessToken);
			if(!Result.SUCCESS.getCode().equals(checkResult.getCode())) {
				Message msg = converter.toMessage(JSON.toJSONString(ChatMessage.unAuthorized()).getBytes(), message.getHeaders());
				return super.preSend(msg, channel);
			}
		}*/

		//权限校验-校验创建连接时是否已登录
		if (SimpMessageType.CONNECT.equals(headerAccessor.getMessageType())) {
			//获取token
			String accessToken = headerAccessor.getFirstNativeHeader("accessToken");
			System.out.println("创建连接accessToken： " + accessToken);

			//校验token
			CommonResponse checkResult = commonUtilService.checkTokenValid(accessToken);
			if(!Response.SUCCESS.getCode().equals(checkResult.getCode())) {
				throw new StompConversionException("连接失败，请登录后再试试吧！");
			}
		}

		return super.preSend(message, channel);
	}


	/**
	 * 在消息被推送到频道 之后调用
	 */
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);//消息头访问器

		if(headerAccessor.getCommand() == null) return; //避免非stomp消息类型，例如心跳检测

		if(headerAccessor.getSessionAttributes() != null) {
			String sessionId = (String)headerAccessor.getSessionAttributes().get("sessionId");
			switch (headerAccessor.getCommand()) {
				case CONNECT:
					break;
				case DISCONNECT:
					break;
				case SUBSCRIBE:
					break;
				case UNSUBSCRIBE:
					break;
				default:
					break;
			}
		}
	}

}
