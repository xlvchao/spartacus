package com.xlc.spartacus.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * 跨域配置
 *
 * @author xlc, since 2021
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);//是否允许携带cookie
        corsConfiguration.addAllowedOrigin("*"); //允许跨域访问的域名，可填写具体域名，*代表允许所有访问
        corsConfiguration.addAllowedMethod("*");//允许访问类型：get post 等，*代表所有类型
        corsConfiguration.addAllowedHeader("*");//允许携带任何请求头
        //第一次是浏览器使用OPTIONS方法发起一个预检请求，每1800秒才会有一次
        //第二次才是真正的异步请求，第一次的预检请求获知服务器是否允许该跨域请求
        //如果允许，才发起第二次真实的请求；如果不允许，则拦截第二次请求。
        corsConfiguration.setMaxAge(1800L);

        //配置源对象
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        //cors 过滤器对象  注意！CorsWebFilter不要导错包
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

}