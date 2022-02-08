package com.xlc.spartacus.auth.core.code;


import com.xlc.spartacus.auth.core.properties.SecurityConstants;

/**
 * 验证码类型定义
 *
 * @author xlc, since 2021
 */
public enum ValidateCodeType {
	
	/**
	 * 短信验证码
	 */
	SMS {
		@Override
		public String getParamNameOnValidate() {
			return SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_SMS;
		}
	},
	
	/**
	 * 图片验证码
	 */
	IMAGE {
		@Override
		public String getParamNameOnValidate() {
			return SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_IMAGE;
		}
	};
	
	/**
	 * 校验时从请求中获取的参数的名字
	 */
	public abstract String getParamNameOnValidate();

}
