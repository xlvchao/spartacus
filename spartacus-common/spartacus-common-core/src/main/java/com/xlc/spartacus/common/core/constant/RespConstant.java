package com.xlc.spartacus.common.core.constant;

/**
 * 响应码
 *
 * @author xlc, since 2021
 */
public interface RespConstant {
	
	Integer CODE_0 = 0; //成功code
	String MSG_0 = "success"; //成功msg
	
	Integer CODE_1 = 1; //失败code
	String MSG_1 = "failed"; //失败msg

	Integer CODE_2 = 2; //失败code
	String MSG_2 = "mysql"; //失败msg

	Integer CODE_3 = 3; //失败code
	String MSG_3 = "es"; //失败msg
	
	Integer CODE_NULL = -1; 
	String MSG_NULL = "no record";
	
	Integer CODE_101 = 101; //缺少必填参数
	String MSG_101 = "no params"; //缺少必填参数
	
	Integer CODE_102 = 102; //参数不合法
	String MSG_102 = "invalid params"; //参数不合法
	
	Integer CODE_500 = 500; //失败code
	String MSG_500 = "server inner error"; //失败msg
	
	Integer CODE_501 = 501; //失败code
	String MSG_501 = "unknown error"; //失败msg
}
