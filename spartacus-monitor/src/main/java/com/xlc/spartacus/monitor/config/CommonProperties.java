package com.xlc.spartacus.monitor.config;

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

    //阿里云市场-全国天气预报
    @Value("${spartacus.aliyun.appCode}")
    private String aliyunAppCode;
    @Value("${spartacus.aliyun.weatherUrl}")
    private String aliyunWeatherUrl;

    //高频访问IP访问次数控制
    @Value("${spartacus.dayAccessThreshold}")
    private Integer dayAccessThreshold;
    @Value("${spartacus.monthAccessThreshold}")
    private Integer monthAccessThreshold;
    @Value("${spartacus.yearAccessThreshold}")
    private Integer yearAccessThreshold;
    @Value("${spartacus.totalAccessThreshold}")
    private Integer totalAccessThreshold;
    
    //高频访问IP访问次数控制
    @Value("${spartacus.accessWindow}")
    private Integer accessWindow;
    @Value("${spartacus.accessAvgInterval}")
    private Integer accessAvgInterval;
    
}
