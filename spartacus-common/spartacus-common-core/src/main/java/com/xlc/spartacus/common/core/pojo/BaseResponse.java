package com.xlc.spartacus.common.core.pojo;

/**
 * 通用返回实体
 *
 * @author xlc, since 2021
 */
public class BaseResponse {
	private Integer code = 0;
	private String message = "success";
	
	private Integer status = 0;
	private Object data;
	
	public BaseResponse() {
		super();
	}
	public BaseResponse(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public BaseResponse(Integer code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public BaseResponse(Integer code, String message, Integer status, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.status = status;
		this.data = data;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "BaseResponse [code=" + code + ", message=" + message + ", status=" + status + ", data=" + data + "]";
	}
	
}
