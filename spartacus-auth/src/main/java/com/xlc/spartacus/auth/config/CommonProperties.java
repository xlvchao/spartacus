package com.xlc.spartacus.auth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 配置加载
 *
 * @author xlc, since 2021
 */
@Data
@Component
public class CommonProperties {
    //手机用户默认头像
    @Value("${spartacus.phoneUser.headimage}")
    private String phoneHeadImage;

    // 百度地图
    @Value("${spartacus.baiduMap.ak}")
    private String baiduMapAk;
    @Value("${spartacus.baiduMap.url}")
    private String baiduMapUrl;

    @Value("${spartacus.jwt.keyFactoryPassword}")
    private String keyFactoryPassword;
    @Value("${spartacus.jwt.keyPassword}")
    private String keyPassword;
}
