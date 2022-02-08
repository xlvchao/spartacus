package com.xlc.spartacus.gateway.config;

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

    // 百度地图
    @Value("${spartacus.baiduMap.ak}")
    private String baiduMapAk;
    @Value("${spartacus.baiduMap.url}")
    private String baiduMapUrl;
}
