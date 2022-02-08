package com.xlc.spartacus.sunday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SundayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SundayApplication.class, args);
    }

}
