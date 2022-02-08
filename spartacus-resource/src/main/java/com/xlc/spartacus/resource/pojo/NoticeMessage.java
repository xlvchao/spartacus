package com.xlc.spartacus.resource.pojo;

import lombok.Data;

@Data
public class NoticeMessage<T> {

	private String from;

	private String to;

	private long timestamp;

	private T payload;
	
}
