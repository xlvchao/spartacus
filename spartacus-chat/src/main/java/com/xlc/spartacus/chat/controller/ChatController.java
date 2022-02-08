package com.xlc.spartacus.chat.controller;

import com.xlc.spartacus.chat.model.ChatMessage;
import com.xlc.spartacus.chat.model.UserContact;
import com.xlc.spartacus.chat.service.ChatService;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 接口
 *
 * @author xlc, since 2021
 */
@RestController
public class ChatController {

	@Resource
	private ChatService chatService;


	@RequestMapping("/searchMessage")
	public BaseResponse searchMessage(@ModelAttribute BaseRequest baseRequest) {
		return chatService.searchMessage(baseRequest.getBelongProviderUserId(), baseRequest.getFromProviderUserId(), baseRequest.getToProviderUserId());
	}

	@RequestMapping("/saveContact")
	public BaseResponse saveContact(@ModelAttribute UserContact userContact) {
		return chatService.saveContact(userContact);
	}

	@RequestMapping("/queryContacts")
	public BaseResponse queryContacts(String providerUserId) {
		return chatService.queryContacts(providerUserId);
	}

	@RequestMapping("/saveMessage")
	public BaseResponse saveMessage(@ModelAttribute ChatMessage chatMessage) {
		return chatService.saveMessage(chatMessage);
	}

	@MessageMapping({"/sendGroupChatMessage"})
	public void sendGroupChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
		chatService.sendGroupChatMessage(chatMessage, headerAccessor);
	}

	@MessageMapping({"/sendPrivateChatMessage"})
	public void sendPrivateChatMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
		chatService.sendPrivateChatMessage(chatMessage, headerAccessor);
	}

}
