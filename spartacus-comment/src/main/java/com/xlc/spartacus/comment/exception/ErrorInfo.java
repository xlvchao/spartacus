package com.xlc.spartacus.comment.exception;

/**
 * 异常信息
 *
 * @author xlc, since 2021
 */
public class ErrorInfo {

    private Integer code;
    private String msg;
    
    private String data;
    private String url;
    
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

}