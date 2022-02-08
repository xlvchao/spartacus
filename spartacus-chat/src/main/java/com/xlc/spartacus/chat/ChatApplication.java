package com.xlc.spartacus.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 程序入口
 *
 * @author xlc, since 2021
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.xlc.spartacus"})
public class ChatApplication {

	public static void main(String [] args){
		SpringApplication.run(ChatApplication.class);
	}
	
	
}
