package com.xlc.spartacus.gateway.server;

import com.xlc.spartacus.gateway.authorization.AuthorizationManager;
import com.xlc.spartacus.gateway.component.RestAccessDeniedHandler;
import com.xlc.spartacus.gateway.component.RestAuthenticationEntryPoint;
import com.xlc.spartacus.gateway.constant.GatewayConstant;
import com.xlc.spartacus.gateway.filter.WhiteUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 资源服务器
 *
 * @author xlc, since 2021
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServer {
    @Autowired
    private final AuthorizationManager authorizationManager;

    @Value("${spartacus.gateway.security.white.urls}")
    private String[] whiteUrls;

    @Autowired
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private final WhiteUrlsRemoveJwtFilter whiteUrlsRemoveJwtFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());

        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);

        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(whiteUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);


        http.authorizeExchange()
                .pathMatchers(whiteUrls).permitAll()//白名单配置
                .anyExchange().access(authorizationManager)//鉴权管理器配置
                .and().exceptionHandling() //统一处理Security的两大类异常：AccessDeniedException、AuthenticationException
                .accessDeniedHandler(restAccessDeniedHandler)//未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint)//未认证
                .and().csrf().disable()
                .headers().frameOptions().disable(); //允许使用iframe嵌入网页

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(GatewayConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(GatewayConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
