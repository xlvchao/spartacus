package com.xlc.spartacus.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 白名单路径访问时需要移除JWT请求头（不管有没有携带，最好都做一下移除）
 * 如果不移除，携带过期或错误的token的白名单请求，会被security后续自带的filter拦截、校验，无法直接通过
 *
 * WebFilter作为Web请求的第一道关口（在所有GlobalFilter之前），负责处理所有客户端（参考文章：https://www.jianshu.com/p/3561332886f1）
 *
 * @author xlc, since 2021
 */
@Component
public class WhiteUrlsRemoveJwtFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(HeaderCheckGlobalFilter.class);

    @Value("${spartacus.gateway.security.white.urls}")
    private String[] whiteUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        PathMatcher pathMatcher = new AntPathMatcher();

        String uriPath = request.getURI().getPath();
        String token = request.getHeaders().getFirst("Authorization");
        logger.info("uri and token: {}, {}", uriPath, token);
        System.out.println(String.format("uri1: %s", uriPath));

        //白名单路径直接移除JWT请求头
        for (String url : whiteUrls) {
            if (pathMatcher.match(url, uriPath)) {
                request = exchange.getRequest().mutate().header("Authorization", "").build();
                exchange = exchange.mutate().request(request).build();
                break;
            }
        }

        return chain.filter(exchange);
    }

}
