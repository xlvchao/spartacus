package com.xlc.spartacus.chat.model;

import com.xlc.spartacus.common.core.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息实体
 *
 * @author xlc, since 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

	private Long id;

	//当前这条信息的所属人
	private String belongProviderUserId;

	//该条消息所属的两个聊天对象唯一标识符
	private String chatId;

	private Integer code = 200;
	
	private String from;
	
	private String to;

	//yyyy-MM-dd HH:mm:ss
	private String sendTime = CommonUtils.getDateString("yyyy-MM-dd HH:mm:ss");

	private String content;

	private Integer type; //消息类型，1是群聊消息，2是私聊消息

	private String fromNickname;
	private String fromHeadimage;
	private String fromProviderId;
	private String fromProviderUserId;

	private String toNickname;
	private String toHeadimage;
	private String toProviderId;
	private String toProviderUserId;

	public static ChatMessage unAuthorized() {
		return ChatMessage.builder()
				.code(401)
				.content("您没有权限，请登录后重试！")
				.build();
	}
}
