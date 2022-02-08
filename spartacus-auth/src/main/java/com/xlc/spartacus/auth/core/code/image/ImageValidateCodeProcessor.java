package com.xlc.spartacus.auth.core.code.image;

import com.xlc.spartacus.auth.core.code.AbstractValidateCodeProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;

/**
 * 图片验证码处理器
 *
 * @author xlc, since 2021
 */
@Component("imageValidateCodeProcessor")
public class ImageValidateCodeProcessor extends AbstractValidateCodeProcessor<ImageValidateCode> {

	/**
	 * 发送图形验证码，将其写到响应流中
	 */
	@Override
	protected void send(ServletWebRequest request, ImageValidateCode imageValidateCode) throws Exception {
		System.out.println("图片验证码:" + imageValidateCode.getCode());
		ImageIO.write(imageValidateCode.getImage(), "JPEG", request.getResponse().getOutputStream());
	}

}
