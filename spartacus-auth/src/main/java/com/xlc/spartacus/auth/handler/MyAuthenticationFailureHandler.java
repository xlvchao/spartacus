package com.xlc.spartacus.auth.handler;

import com.xlc.spartacus.auth.common.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 失败处理器，登录失败给页面返回提示信息
 *
 * @author xlc, since 2021
 */
@Component("myAuthenticationFailureHandler")
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ObjectMapper objectMapper;
	

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
			AuthenticationException exception) throws IOException, ServletException {
		
		logger.info("登录失败"+exception.getMessage());
		
//		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(CommonResponse.failed(exception.getMessage())));
	}

}
