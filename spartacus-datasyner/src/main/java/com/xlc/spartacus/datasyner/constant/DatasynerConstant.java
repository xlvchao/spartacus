package com.xlc.spartacus.datasyner.constant;

/**
 * 常量定义
 *
 * @author xlc, since 2021
 */
public interface DatasynerConstant {

	/**
	 * rabbitmq topic & queue & routting
	 */
	String DATA_SYN_EXCHANGE_NAME = "spartacus";

	//cos
	String DATA_SYN_COS_QUEUE_NAME = "spartacus.tb_cos_resource";

	String DATA_SYN_COS_ROUTING_KEY = "spartacus.tb_cos_resource";

	//article
	String DATA_SYN_ART_QUEUE_NAME = "spartacus.tb_article";

	String DATA_SYN_ART_ROUTING_KEY = "spartacus.tb_article";

	//chat
	String CHAT_MESSAGE_EXCHANGE_NAME = "chat";
	String CHAT_MESSAGE_QUEUE_NAME = "chat.message";
	String CHAT_MESSAGE_ROUTING_KEY = "chat.message";

	//login
	String DATA_SYN_LOGIN_RECORD_QUEUE_NAME = "spartacus.tb_login_record";

	String DATA_SYN_LOGIN_RECORD_ROUTING_KEY = "spartacus.tb_login_record";


	/**
	 * maxwell数据变更类型
	 */
	String TYPE_BOOTSTRAP_START = "bootstrap-start";

	String TYPE_BOOTSTRAP_INSERT = "bootstrap-insert";

	String TYPE_INSERT = "insert";

	String TYPE_UPDATE = "update";

	String TYPE_DELETE = "delete";

	String TYPE_BOOTSTRAP_COMPLETE = "bootstrap-complete";


	/**
	 * ES索引
	 */
	String ARTICLE_INDEX_NAME = "article_index";

	String COS_RESOURCE_INDEX_NAME = "cos_resource_index";

	String CHAT_MESSAGE_INDEX_NAME = "chat_message_index";

	String LOGIN_RECORD_INDEX_NAME = "login_record_index";
	
}