package com.xlc.spartacus.common.es.config;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 链接保活任务
 *
 * 访问量大的网站不需要
 *
 * @author xlc, since 2021
 */
@Component
public class KeepAliveTask {

    @Resource
    private RestHighLevelClient client;

    @Scheduled(cron = "0 0/1 * * * *")
    public void keepAlive() {
        try {
            client.info(RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
