package com.xlc.spartacus.auth.core.code.image;


import com.xlc.spartacus.auth.core.code.ValidateCode;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 图片验证码实体
 *
 * @author xlc, since 2021
 */
public class ImageValidateCode extends ValidateCode {
	
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image;
	
	public ImageValidateCode(BufferedImage image, String code, int expireIn){
		super(code, expireIn);
		this.image = image;
	}
	
	public ImageValidateCode(BufferedImage image, String code, LocalDateTime expireTime){
		super(code, expireTime);
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
