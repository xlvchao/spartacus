package com.xlc.spartacus.friday;

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
public class FridayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridayApplication.class, args);
    }

}
