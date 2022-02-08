package com.xlc.spartacus.chat.constant;

/**
 * 常量
 *
 * @author xlc, since 2021
 */
public class ChatConstant {

    //当前在线用户
    public static final String CHAT_ONLINE_USERS = "CHAT:ONLINE_USERS";

    //登录过的所有用户
    public static final String CHAT_HISTORY_USERS = "CHAT:HISTORY_USERS";


    /**
     * rabbitmq topic & queue & routting
     */
    public static final String CHAT_MESSAGE_EXCHANGE_NAME = "chat";
    public static final String CHAT_MESSAGE_QUEUE_NAME = "chat.message";
    public static final String CHAT_MESSAGE_ROUTING_KEY = "chat.message";


    public static final String CHAT_MESSAGE_INDEX_NAME = "chat_message_index";
}
