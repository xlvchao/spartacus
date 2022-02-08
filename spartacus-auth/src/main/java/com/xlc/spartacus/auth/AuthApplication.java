package com.xlc.spartacus.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
