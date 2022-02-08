package com.xlc.spartacus.auth.core.properties;

//import org.springframework.boot.autoconfigure.social.SocialProperties;
import com.xlc.spartacus.auth.core.social.base.SocialProperties;
import lombok.Data;

/**
 * 配置
 *
 * @author xlc, since 2021
 */
@Data
public class QQProperties extends SocialProperties {
	
	private String providerId = "qq";

}
