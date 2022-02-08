package com.xlc.spartacus.chat.config;

import com.alibaba.fastjson.JSON;
import com.xlc.spartacus.chat.constant.ChatConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * bean配置
 *
 * @author xlc, since 2021
 */
@Configuration
public class BeanConfig {

    private Logger logger = LoggerFactory.getLogger(BeanConfig.class);

    @Bean("myAsync")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("myAsync-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setMessageConverter(new SerializerMessageConverter());

        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(!ack) {
                    logger.info("生产者到交换机发送失败，即将重试：ack:{}, cause:{}", ack, cause);

                    String msg = new String(correlationData.getReturnedMessage().getBody());
                    template.convertAndSend(ChatConstant.CHAT_MESSAGE_EXCHANGE_NAME, ChatConstant.CHAT_MESSAGE_ROUTING_KEY, JSON.toJSONString(msg));
                }
            }
        });

        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("交换机到队列路由失败，即将重试：replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，messageBody:{} messageProperties:{}",
                        replyCode, replyText, exchange, routingKey, new String(message.getBody()), message.getMessageProperties());

                String msg = new String(message.getBody());
                template.convertAndSend(ChatConstant.CHAT_MESSAGE_EXCHANGE_NAME, ChatConstant.CHAT_MESSAGE_ROUTING_KEY, JSON.toJSONString(msg));
            }
        });

        return template;
    }

}