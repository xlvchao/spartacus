package com.xlc.spartacus.monitor.xxljob.jobhandler;

import com.xlc.spartacus.monitor.mapper.AccessForbidMapper;
import com.xlc.spartacus.monitor.pojo.Access;
import com.xlc.spartacus.monitor.pojo.AccessForbid;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 *
 * @author xlc, since 2021
 */
@Component
public class MonitorJobHandler {
    private static Logger logger = LoggerFactory.getLogger(MonitorJobHandler.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private AccessForbidMapper accessForbidMapper;


    @XxlJob(value = "synAccessForbidDataJobHandler")
    public void synAccessForbidDataJobHandler() throws Exception {
        try{
            XxlJobHelper.log("开始同步禁止访问数据");
            logger.info("开始同步禁止访问数据");

            List<String> ips = accessForbidMapper.findAllIp();
            redisTemplate.opsForSet().add("FORBIDDEN_ACCESS_IP_SET", ips.toArray());

            logger.info("同步禁止访问数据完成！");
            XxlJobHelper.log("同步禁止访问数据完成！");

            XxlJobHelper.handleSuccess();
        } catch (Exception e) {
            logger.error("同步禁止访问数据异常！", e);

            XxlJobHelper.log("同步禁止访问数据异常！");
            XxlJobHelper.handleFail();

            if (e instanceof InterruptedException) { //中断异常一定要抛出，否则调度中心的终止job功能将失效！
                throw e;
            }
        }
    }

}
