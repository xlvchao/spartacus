package com.xlc.spartacus.datasyner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
public class DatasynerApplication {
	public static void main(String[] args) {
		SpringApplication.run(DatasynerApplication.class, args);
	}

}
