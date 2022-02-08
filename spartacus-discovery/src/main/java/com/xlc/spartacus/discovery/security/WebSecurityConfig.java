package com.xlc.spartacus.discovery.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable(); //关闭csrf
//        http.authorizeRequests().anyRequest().authenticated().and().httpBasic(); //开启认证

        super.configure(http);//加这句是为了访问eureka控制台和/actuator时能做安全控制
        http.csrf().disable();//关闭csrf
    }

}