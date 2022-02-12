package com.xlc.spartacus.resource.service;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.resource.constant.ResourceConstant;
import com.xlc.spartacus.resource.pojo.NoticeMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 发送服务
 *
 * @author xlc, since 2021
 */
@Service
public class WebSocketService {

	@Resource
	private SimpMessagingTemplate template;

	
	public void sendFileReadStatusChangeNotice(NoticeMessage message) {
		template.convertAndSend("/topic/fileReadStatusChangeNotice", JSON.toJSONString(message));
	}

}
