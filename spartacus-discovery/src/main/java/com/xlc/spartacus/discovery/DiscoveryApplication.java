package com.xlc.spartacus.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableEurekaServer
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}
