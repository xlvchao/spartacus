package com.xlc.spartacus.auth.core.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置
 * 让SecurityProperties.java这个读取器生效
 *
 * @author xlc, since 2021
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityCoreConfig {

}
