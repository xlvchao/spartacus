package com.xlc.spartacus.comment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

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
