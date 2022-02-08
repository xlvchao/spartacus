package com.xlc.spartacus.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.gateway.task.AsyncTask;
import com.xlc.spartacus.gateway.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 前端在没有点击退出登录的情况下，关闭页面或者关闭浏览器之后，token在服务端只有30秒保活时间，然而token在客户端并不会删除，此时会有安全性问题
 *
 * 因此，针对服务端token失效、客户端保留了token的情况，为了防止恶意用户，此处再加一层校验token校验
 *
 * GlobalFilter作为gateway路由全局过滤器，负责把好路由最后一道关，做最后的统一处理（参考文章：https://www.jianshu.com/p/3561332886f1）
 *
 * @author xlc, since 2021
 */
@Component
public class HeaderCheckGlobalFilter implements GlobalFilter, Ordered {

    private static Logger logger = LoggerFactory.getLogger(HeaderCheckGlobalFilter.class);

    @Value("${spartacus.gateway.security.white.urls}")
    private String[] whiteUrls;

    @Value("${spartacus.gateway.access.count.urls}")
    private String[] accessCountUrls;

    @Resource
    private AsyncTask asyncTask;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String uriPath = request.getURI().getPath();
        PathMatcher pathMatcher = new AntPathMatcher();
        System.out.println(String.format("uri3: %s", uriPath));

        //1、校验请求接口是否属于白名单
        boolean isNotWhiteUrl = true;
        for (String url : whiteUrls) {
            if (pathMatcher.match(url, uriPath)) {
                isNotWhiteUrl = false;
                break;
            }
        }
        if(isNotWhiteUrl) { //非白名单接口需要校验token是否有效
            String token = request.getHeaders().getFirst("Authorization");
            if(StringUtils.isBlank(token)) {
                return response.writeWith(Mono.just(genRejectResponeBuffer(response, "无效的token！")));
            }

            token = token.replace("bearer ", "");
            if(!redisTemplate.hasKey("CURRENT_ONLINE_USER:" + token)) {
                return response.writeWith(Mono.just(genRejectResponeBuffer(response, "无效的token！")));
            }
        }

        // 2、校验是否携带了关键请求头
        boolean isAccessCountUrl = false;
        if(!UrlUtils.isStaticResource(uriPath)) {
            for (String url : accessCountUrls) {
                if (pathMatcher.match(url, uriPath)) {
                    isAccessCountUrl = true;
                    break;
                }
            }
        }
        if(isAccessCountUrl) {
            String clientId = request.getHeaders().getFirst("clientId");
            if(!("spartacus-sunday".equals(clientId) || "spartacus-friday".equals(clientId))) {
                return response.writeWith(Mono.just(genRejectResponeBuffer(response, "未携带有效的clientId！")));
            }
            //异步记录访问信息
            if("spartacus-sunday".equals(clientId)) {
                asyncTask.saveAccessInfo(request);
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
        return 1;
    }

}
