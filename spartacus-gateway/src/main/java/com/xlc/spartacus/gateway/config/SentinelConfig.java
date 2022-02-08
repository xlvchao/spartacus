package com.xlc.spartacus.gateway.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * 网关限流、降级配置
 *
 * 参考：https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81
 *
 * @author xlc, since 2021
 */
@Configuration
public class SentinelConfig implements CommandLineRunner {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler mySentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter mySentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * API分组
     *
     * @return {@link null}
     * @date 2021/3/27 16:44
     */
    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("spartacus-auth-rote")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/spartacus-auth/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api2 = new ApiDefinition("spartacus-article-rote")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/spartacus-article/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api3 = new ApiDefinition("spartacus-comment-rote")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/spartacus-comment/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        ApiDefinition api4 = new ApiDefinition("spartacus-chat-rote")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/spartacus-chat/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(api1);
        definitions.add(api2);
        definitions.add(api3);
        definitions.add(api4);
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    /**
     * 流控规则
     *
     * @return {@link null}
     * @date 2021/3/27 16:41
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("spartacus-auth-rote")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(5)
                .setIntervalSec(5)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
                .setBurst(5)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
        );

        rules.add(new GatewayFlowRule("spartacus-article-rote")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(10)
                .setIntervalSec(5)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
                .setBurst(10)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
        );

        rules.add(new GatewayFlowRule("spartacus-comment-rote")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(10)
                .setIntervalSec(5)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
                .setBurst(10)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
        );

        rules.add(new GatewayFlowRule("spartacus-chat-rote")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                .setCount(10)
                .setIntervalSec(5)
                .setParamItem(new GatewayParamFlowItem()
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
                )
                .setBurst(10)
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
        );

        GatewayRuleManager.loadRules(rules);
    }


    private void initGatewayRulesBak() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        // SentinelGatewayFilter从Eureka拉取服务而后准换成的网关ID名就长这鸟样：'长串前缀_' + '服务名称大写'
        // 因此这里要想网关流控规则生效，必须这种格式，而不能直接使用配置的spring.cloud.gateway.routes[i].id
        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-AUTH")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-COMMON")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-COMMENT")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(30) //QPS
                .setIntervalSec(30) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-ARTICLE")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(10) //QPS
                .setIntervalSec(10) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-RESOURCE")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-MONITOR")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-FRIDAY")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        rules.add(new GatewayFlowRule("ReactiveCompositeDiscoveryClient_SPARTACUS-CHAT")
                .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_ROUTE_ID)
                .setCount(5) //QPS
                .setIntervalSec(5) //统计时间窗口
                .setParamItem(new GatewayParamFlowItem() //参数限流策略
                        .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP) //以客户端IP作为限流因子
                )
        );

        GatewayRuleManager.loadRules(rules);
    }

    private void initBlockHandler() {
        GatewayCallbackManager.setBlockHandler(new MyBlockRequestHandler());
    }

    @Override
    public void run(String... args) throws Exception {
        initCustomizedApis();
        initGatewayRules();
        initBlockHandler();
    }
}