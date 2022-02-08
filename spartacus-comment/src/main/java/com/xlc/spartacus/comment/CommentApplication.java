package com.xlc.spartacus.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
@EnableAsync
@EnableTransactionManagement // 开启注解事务管理
public class CommentApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommentApplication.class, args);
	}

}
