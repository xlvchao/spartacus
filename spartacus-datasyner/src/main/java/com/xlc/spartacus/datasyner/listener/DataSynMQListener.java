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
 * 数据同步 消息监听器
 *
 * @author xlc, since 2021
 */
@Component
public class DataSynMQListener {
    private static final Logger logger = LoggerFactory.getLogger(DataSynMQListener.class);

    @Resource
    private ElasticsearchService elasticsearchService;

    /**
     * 注意：如果已经手动通过web控制台创建了topic和queue，那么请务必保持这里的参数设置与控制台的一致！
     */
    @RabbitHandler
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = DatasynerConstant.DATA_SYN_COS_QUEUE_NAME, durable = "true", autoDelete = "false", arguments = {@Argument(name = "x-queue-type", value = "classic")}),
            key = DatasynerConstant.DATA_SYN_COS_ROUTING_KEY,
            exchange = @Exchange(value = DatasynerConstant.DATA_SYN_EXCHANGE_NAME, durable = "true", autoDelete = "false", type = ExchangeTypes.TOPIC)
        )
    )
    public void handleCosMessage(Message message, Channel channel) throws IOException {
        long msgId = message.getMessageProperties().getDeliveryTag();
        String msg = new String(message.getBody());

        /*ParserConfig parserConfig = new ParserConfig(); // 生产环境中，parserConfig要做singleton处理，要不然会存在性能问题
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        MqCosResource object = JSON.parseObject(String.valueOf(message.getBody()), MqCosResource.class, parserConfig);
        System.out.println(object);*/

        try {
            logger.info("开始同步cos数据：{}-{}", msgId, msg);
            boolean flag = synDataToES(DatasynerConstant.COS_RESOURCE_INDEX_NAME, msg);
            if(flag) {
                channel.basicAck(msgId,false);
            } else {
                channel.basicNack(msgId,false,true);
            }
            logger.info("同步cos数据结束：{}-{}", msgId, flag);

        } catch (Exception e) {
            channel.basicNack(msgId,false,true);
            logger.error("同步cos数据发生异常！", e);
        }

//        // 签收
//        // 第二个参数返回true，表示需要将这条消息投递给其他的消费者重新消费
//        channel.basicAck(msgId,false);
//        //拒收
//        // 第三个参数true，表示这个消息会重新进入队列
//        channel.basicNack(msgId,false,true);
    }


    /**
     * 注意：如果已经手动通过web控制台创建了topic和queue，那么请务必保持这里的参数设置与控制台的一致！
     */
    @RabbitHandler
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = DatasynerConstant.DATA_SYN_ART_QUEUE_NAME, durable = "true", autoDelete = "false", arguments = {@Argument(name = "x-queue-type", value = "classic")}),
            key = DatasynerConstant.DATA_SYN_ART_ROUTING_KEY,
            exchange = @Exchange(value = DatasynerConstant.DATA_SYN_EXCHANGE_NAME, durable = "true", autoDelete = "false", type = ExchangeTypes.TOPIC)
        )
    )
    public void handleArticleMessage(Message message, Channel channel) throws IOException {
        long msgId = message.getMessageProperties().getDeliveryTag();
        String msg = new String(message.getBody());

        try {
            logger.info("开始同步文章数据：{}-{}", msgId, msg);
            boolean flag = synDataToES(DatasynerConstant.ARTICLE_INDEX_NAME, msg);
            if(flag) {
                channel.basicAck(msgId,false);
            } else {
                channel.basicNack(msgId,false,true);
            }
            logger.info("同步文章数据结束：{}-{}", msgId, flag);

        } catch (Exception e) {
            channel.basicNack(msgId,false,true);
            logger.error("同步文章数据发生异常！", e);
        }

    }

    @RabbitHandler
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = DatasynerConstant.DATA_SYN_LOGIN_RECORD_QUEUE_NAME, durable = "true", autoDelete = "false", arguments = {@Argument(name = "x-queue-type", value = "classic")}),
                    key = DatasynerConstant.DATA_SYN_LOGIN_RECORD_ROUTING_KEY,
                    exchange = @Exchange(value = DatasynerConstant.DATA_SYN_EXCHANGE_NAME, durable = "true", autoDelete = "false", type = ExchangeTypes.TOPIC)
            )
    )
    public void handleLoginMessage(Message message, Channel channel) throws IOException {
        long msgId = message.getMessageProperties().getDeliveryTag();
        String msg = new String(message.getBody());

        try {
            logger.info("开始同步登陆数据：{}-{}", msgId, msg);
            boolean flag = synDataToES(DatasynerConstant.LOGIN_RECORD_INDEX_NAME, msg);
            if(flag) {
                channel.basicAck(msgId,false);
            } else {
                channel.basicNack(msgId,false,true);
            }
            logger.info("同步登陆数据结束：{}-{}", msgId, flag);

        } catch (Exception e) {
            channel.basicNack(msgId,false,true);
            logger.error("同步登陆数据发生异常！", e);
        }

    }

    /**
     * 同步数据到ES
     *
     * @param json
     * @param index
     * @return
     * @throws IOException
     */
    private boolean synDataToES(String index, String json) throws Exception {
        JSONObject jsonObject = JSON.parseObject(json);
        String type = jsonObject.getString("type");
        JSONObject data = jsonObject.getJSONObject("data");

        if (DatasynerConstant.TYPE_BOOTSTRAP_START.equals(type) || DatasynerConstant.TYPE_BOOTSTRAP_COMPLETE.equals(type)) {
            return true;

        } else if (DatasynerConstant.TYPE_BOOTSTRAP_INSERT.equals(type) || DatasynerConstant.TYPE_INSERT.equals(type)) {
            String id = data.getLong("id").toString();
            Map doc = JSON.parseObject(data.toJSONString(), Map.class);
            return elasticsearchService.add(index, id, doc);

        } else if (DatasynerConstant.TYPE_UPDATE.equals(type)) {
            String id = data.getLong("id").toString();
            Map doc = JSON.parseObject(data.toJSONString(), Map.class);
            return elasticsearchService.update(index, id, doc);

        } else if (DatasynerConstant.TYPE_DELETE.equals(type)) {
            String id = data.getLong("id").toString();
            return elasticsearchService.delete(index, id);

        } else {
            return false;
        }
    }

}