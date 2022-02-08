package com.xlc.spartacus.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
@EnableTransactionManagement // 开启注解事务管理
public class SystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(SystemApplication.class, args);
	}

}
