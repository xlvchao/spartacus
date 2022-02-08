package com.xlc.spartacus.gateway.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.xlc.spartacus.gateway.constant.GatewayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * apollo配置变动监听器
 *
 * @author xlc, since 2021
 */
@Configuration
public class ApolloConfigChangeListener implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private ApolloConfigHelper apolloConfigHelper;


    @Override
    public void run(String... args) throws Exception {
        //初始化角色资源
        redisTemplate.opsForValue().set(GatewayConstant.REDIS_RESOURCE_ROLES, apolloConfigHelper.getPropertyMap(GatewayConstant.NAMESPACE_RESOURCE_ROLES));
        logger.info("初始化角色资源到redis中成功！");

        //配置更新时
        Config config = ConfigService.getConfig(GatewayConstant.NAMESPACE_RESOURCE_ROLES);
        config.addChangeListener(changeEvent -> {
            redisTemplate.opsForValue().set(GatewayConstant.REDIS_RESOURCE_ROLES, apolloConfigHelper.getPropertyMap(GatewayConstant.NAMESPACE_RESOURCE_ROLES));
            logger.info("角色资源更新成功！");
        });
    }
}