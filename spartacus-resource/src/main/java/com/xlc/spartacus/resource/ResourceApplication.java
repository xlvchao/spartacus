package com.xlc.spartacus.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
@EnableAsync
@EnableTransactionManagement // 开启注解事务管理
public class ResourceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ResourceApplication.class, args);
	}

}
