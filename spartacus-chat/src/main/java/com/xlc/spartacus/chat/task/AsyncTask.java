package com.xlc.spartacus.chat.task;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.chat.constant.ChatConstant;
import com.xlc.spartacus.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步处理
 *
 * @author xlc, since 2021
 */
@Component
public class AsyncTask {
	private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

	@Resource
	private RabbitTemplate rabbitTemplate;


	@Async("myAsync")
	public void pushChatMessage(ChatMessage message) {
		rabbitTemplate.convertAndSend(ChatConstant.CHAT_MESSAGE_EXCHANGE_NAME, ChatConstant.CHAT_MESSAGE_ROUTING_KEY, JSON.toJSONString(message));
	}

}