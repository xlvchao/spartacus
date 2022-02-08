package com.xlc.spartacus.datasyner.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.xlc.spartacus.datasyner.constant.DatasynerConstant;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 聊天 消息监听器
 *
 * @author xlc, since 2021
 */
@Component
public class ChatMessageMQListener {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageMQListener.class);

    @Resource
    private ElasticsearchService elasticsearchService;

    /**
     * 注意：如果已经手动通过web控制台创建了topic和queue，那么请务必保持这里的参数设置与控制台的一致！
     */
    @RabbitHandler
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = DatasynerConstant.CHAT_MESSAGE_QUEUE_NAME, durable = "true", autoDelete = "false", arguments = {@Argument(name = "x-queue-type", value = "classic")}),
            key = DatasynerConstant.CHAT_MESSAGE_ROUTING_KEY,
            exchange = @Exchange(value = DatasynerConstant.CHAT_MESSAGE_EXCHANGE_NAME, durable = "true", autoDelete = "false", type = ExchangeTypes.TOPIC)
        )
    )
    public void handleCosMessage(Message message, Channel channel) throws IOException {
        long msgId = message.getMessageProperties().getDeliveryTag();
        String msg = new String(message.getBody());

        try {
            logger.info("开始同步聊天数据：{}-{}", msgId, msg);

            JSONObject jsonObject = JSON.parseObject(msg);
            String id = jsonObject.getLong("id").toString();
            Map doc = JSON.parseObject(jsonObject.toJSONString(), Map.class);

            boolean flag = elasticsearchService.add(DatasynerConstant.CHAT_MESSAGE_INDEX_NAME, id, doc);

            if(flag) {
                // 第二个参数返回true，表示需要将这条消息投递给其他的消费者重新消费
                channel.basicAck(msgId,false);
            } else {
                // 第三个参数true，表示这个消息会重新进入队列
                channel.basicNack(msgId,false,true);
            }
            logger.info("同步聊天数据结束：{}-{}", msgId, flag);

        } catch (Exception e) {
            channel.basicNack(msgId,false,true);
            logger.error("同步聊天数据发生异常！", e);
        }
    }

}