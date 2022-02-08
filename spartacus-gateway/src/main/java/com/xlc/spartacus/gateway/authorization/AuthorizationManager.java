package com.xlc.spartacus.gateway.authorization;

import com.xlc.spartacus.gateway.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 *
 * @author xlc, since 2021
 */
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

//    @Autowired
//    private ApolloConfigHelper apolloConfigHelper;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        URI uri = authorizationContext.getExchange().getRequest().getURI();

        //先从Redis中拿资源-角色关系，拿不到再去apollo拉，然后回填redis
        /*String roles = Convert.toStr(redisTemplate.opsForHash().get(GatewayConstants.REDIS_RESOURCE_ROLES_MAP, uri.getPath()));
        if(StringUtils.isBlank(roles)) {
            roles = apolloConfigHelper.getStringProperty(GatewayConstants.NAMESPACE_RESOURCE_ROLES, uri.getPath(), GatewayConstants.ROLE_DEFAULT);
            redisTemplate.opsForHash().put(GatewayConstants.REDIS_RESOURCE_ROLES_MAP, uri.getPath(), roles);
        }*/

        String roles = GatewayConstant.ROLE_SUFFIX_DEFAULT;
        Map<String, String> resourceRoles = (Map<String, String>) redisTemplate.opsForValue().get(GatewayConstant.REDIS_RESOURCE_ROLES);
        PathMatcher pathMatcher = new AntPathMatcher();
        for(Map.Entry<String, String> en : resourceRoles.entrySet()) {
            if (pathMatcher.match(en.getKey(), uri.getPath())) {
                roles = en.getValue();
                break;
            }
        }

        List<String> authorities = Arrays.stream(roles.split(",")).map(item -> item = GatewayConstant.AUTHORITY_PREFIX + item).collect(Collectors.toList());

        //认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
