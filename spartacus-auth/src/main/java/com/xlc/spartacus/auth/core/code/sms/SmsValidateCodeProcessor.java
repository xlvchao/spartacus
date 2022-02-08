package com.xlc.spartacus.auth.core.code.sms;

import com.xlc.spartacus.auth.core.code.AbstractValidateCodeProcessor;
import com.xlc.spartacus.auth.core.code.ValidateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 短信验证码处理器
 *
 * @author xlc, since 2021
 */
@Component("smsValidateCodeProcessor")
public class SmsValidateCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {

	/**
	 * 短信验证码发送器
	 */
	@Autowired
	private SmsValidateCodeSender smsValidateCodeSender;


	@Override
	protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
		String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), "mobile");
		smsValidateCodeSender.send(mobile, validateCode.getCode());
	}

}
