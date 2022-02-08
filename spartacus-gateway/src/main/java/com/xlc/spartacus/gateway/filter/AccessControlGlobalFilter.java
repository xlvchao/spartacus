package com.xlc.spartacus.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.gateway.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 *
 * 检查是否有访问权限、评论权限
 *
 * GlobalFilter作为gateway路由全局过滤器，负责把好路由最后一道关，做最后的统一处理（参考文章：https://www.jianshu.com/p/3561332886f1）
 *
 * @author xlc, since 2021
 */
@Component
public class AccessControlGlobalFilter implements GlobalFilter, Ordered {
    private static Logger logger = LoggerFactory.getLogger(AccessControlGlobalFilter.class);

    @Resource
    private RedisTemplate<String,Object> redisTemplate;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uriPath = request.getURI().getPath();
        System.out.println(String.format("uri2: %s", uriPath));

        String ip = IPUtils.getIp(request);

        PathMatcher pathMatcher = new AntPathMatcher();

        //1、检查是否被禁止访问
        Boolean isForbiddenAccess  = redisTemplate.opsForSet().isMember("FORBIDDEN_ACCESS_IP_SET", ip);
        if(isForbiddenAccess) {
            return response.writeWith(Mono.just(genRejectResponeBuffer(response, "您已被禁止访问！")));
        }

        //2、检查是否被禁止评论
        if (pathMatcher.match("/spartacus-comment/comment/submitComment", uriPath)) {
            String userId = request.getHeaders().getFirst("userId");
            Boolean isForbiddenComment  = redisTemplate.opsForSet().isMember("FORBIDDEN_COMMENT_IP_SET", ip) || redisTemplate.opsForSet().isMember("FORBIDDEN_COMMENT_USERID_SET", userId);
            if(isForbiddenComment) {
                return response.writeWith(Mono.just(genRejectResponeBuffer(response, "您已被禁止评论！")));
            }
        }

        return chain.filter(exchange);
    }

    public DataBuffer genRejectResponeBuffer(ServerHttpResponse response, String msg) {
        byte[] bits = JSON.toJSONString(CommonResponse.failed(msg)).getBytes(StandardCharsets.UTF_8);
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return buffer ;
    }


    @Override
    public int getOrder() {
        return 0;
    }



}
