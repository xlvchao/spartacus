package com.xlc.spartacus.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.cloud.sleuth.instrument.redis.TraceRedisAutoConfiguration;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"}, exclude = {TraceRedisAutoConfiguration.class})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
