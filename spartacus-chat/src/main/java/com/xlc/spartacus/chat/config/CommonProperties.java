package com.xlc.spartacus.chat.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置加载
 *
 * @author xlc, since 2021
 */
@Data
@Component
public class CommonProperties {
    @Value("${spartacus.broker.relayHost}")
    private String relayHost;
    @Value("${spartacus.broker.relayPort}")
    private Integer relayPort;
	@Value("${spartacus.broker.clientLogin}")
    private String clientLogin;
    @Value("${spartacus.broker.clientPasscode}")
    private String clientPasscode;
    @Value("${spartacus.broker.systemLogin}")
    private String systemLogin;
    @Value("${spartacus.broker.systemPasscode}")
    private String systemPasscode;
    @Value("${spartacus.broker.virtualHost}")
    private String virtualHost;

}
