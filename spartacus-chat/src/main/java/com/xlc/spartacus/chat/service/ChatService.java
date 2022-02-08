package com.xlc.spartacus.chat.service;

import com.xlc.spartacus.chat.constant.ChatConstant;
import com.xlc.spartacus.chat.mapper.UserContactMapper;
import com.xlc.spartacus.chat.model.ChatMessage;
import com.xlc.spartacus.chat.model.UserContact;
import com.xlc.spartacus.chat.task.AsyncTask;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.common.es.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 简单消息模板，用来推送消息
 *
 * @author xlc, since 2021
 */
@Component
public class ChatService {
	private Logger logger = LoggerFactory.getLogger(ChatService.class);

	@Resource
	private SimpMessagingTemplate messagingTemplate;

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private UserContactMapper userContactMapper;

	@Resource
	private AsyncTask task;

	@Resource
	private ElasticsearchService elasticsearchService;


	/**
	 * 查询最近100条聊天记录
	 *
	 * @param fromProviderUserId
	 * @param toProviderUserId
	 * @return
	 */
	public BaseResponse searchMessage(String belongProviderUserId, String fromProviderUserId, String toProviderUserId) {
		try {
			logger.info("开始查询最近100条聊天记录：" + CommonUtils.concat(fromProviderUserId, toProviderUserId));
			Map<String, Object> mustMatchs = new HashMap<>();
			mustMatchs.put("belongProviderUserId", belongProviderUserId);
			mustMatchs.put("chatId", CommonUtils.genUniqueId(fromProviderUserId, toProviderUserId));
			Page page = elasticsearchService.searchMatch(ChatConstant.CHAT_MESSAGE_INDEX_NAME, null, null, mustMatchs, null, "sendTime", null, 1, 100);
			List<Map<String, Object>> records = (List<Map<String, Object>>) page.getRecords();

			page.setRecords(records);
			logger.info("查询最近100条聊天记录成功：" + page);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

		} catch (Exception e) {
			logger.error("查询最近100条聊天记录发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "查询最近100条聊天记录发生异常！");
		}
	}

	/**
	 * 查询联系人
	 *
	 * @param providerUserId
	 * @return
	 */
	public BaseResponse queryContacts(String providerUserId) {
		try {
			logger.info(String.format("开始查询联系人:%s", providerUserId));
			List<Map> contacts = userContactMapper.queryContacts(providerUserId);
			System.out.println("contacts = " + contacts);
			logger.info(String.format("查询联系人成功:%s", providerUserId), contacts);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, contacts);

		} catch (Exception e) {
			logger.error("查询联系人发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "查询联系人发生异常！");
		}
	}

	/**
	 * 保存联系人
	 *
	 * @param userContact
	 * @return
	 */
	public BaseResponse saveContact(UserContact userContact) {
		try {
			logger.info(String.format("开始保存联系人:%s-%s", userContact.getProviderUserId(), userContact.getContactProviderUserId()));
			userContact.setId(Snowflake.generateId());
			userContactMapper.saveContact(userContact);
			logger.info(String.format("保存联系人成功:%s-%s", userContact.getProviderUserId(), userContact.getContactProviderUserId()));
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("保存联系人发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "保存联系人发生异常！");
		}
	}

	/**
	 * 异步推送到MQ
	 *
	 * @param chatMessage
	 * @return
	 */
	public BaseResponse saveMessage(ChatMessage chatMessage) {
		try {
			logger.info("开始异步推送到MQ", chatMessage);
			chatMessage.setId(Snowflake.generateId());
			task.pushChatMessage(chatMessage);
			logger.info("异步推送到MQ成功");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("异步推送到MQ成功发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "异步推送到MQ成功发生异常！");
		}
	}

	/**
	 * 推送订阅消息-群聊
	 *
	 * @param chatMessage
	 */
	public void sendGroupChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		chatMessage.setType(1);
		chatMessage.setChatId(chatMessage.getToProviderUserId());

		List<String> accessTokenList = headerAccessor.toNativeHeaderMap().get("accessToken");
		String accessToken = accessTokenList != null && accessTokenList.size()>0 ? accessTokenList.get(0) : null;

		String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");

		Map<String, Object> headers = new HashMap<>();
		headers.put("sessionId", sessionId);
		headers.put("accessToken", accessToken);

		//给所有登陆过的用户都发送一遍
		Set<Object> users = redisTemplate.opsForSet().members(ChatConstant.CHAT_HISTORY_USERS);
		users.forEach(providerUserId -> {
			String destination = "/queue/" + providerUserId;
			messagingTemplate.convertAndSend(destination, chatMessage, headers);
		});
	}

	/**
	 * 推送订阅消息-私聊
	 *
	 * @param chatMessage
	 */
	public void sendPrivateChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		chatMessage.setType(2);
		chatMessage.setChatId(CommonUtils.genUniqueId(chatMessage.getFromProviderUserId(), chatMessage.getToProviderUserId()));

		List<String> accessTokenList = headerAccessor.toNativeHeaderMap().get("accessToken");
		String accessToken = accessTokenList != null && accessTokenList.size()>0 ? accessTokenList.get(0) : null;

		String sessionId = (String) headerAccessor.getSessionAttributes().get("sessionId");

		Map<String, Object> headers = new HashMap<>();
		headers.put("sessionId", sessionId);
		headers.put("accessToken", accessToken);

		//发给对方
		String destination1 = "/queue/" + chatMessage.getTo();
		messagingTemplate.convertAndSend(destination1, chatMessage, headers);

		//发给自己
		String destination2 = "/queue/" + chatMessage.getFrom();
		messagingTemplate.convertAndSend(destination2, chatMessage, headers);
	}

	/**
	 * 推送在线用户
	 *
	 * @param onlineUsers
	 */
	public void sendOnlineUser(List<Map<String, Object>> onlineUsers) {
		messagingTemplate.convertAndSend("/topic/onlineUser", onlineUsers);
	}

}
