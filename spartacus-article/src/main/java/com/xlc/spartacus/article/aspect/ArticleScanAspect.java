package com.xlc.spartacus.article.aspect;

import com.xlc.spartacus.article.service.impl.ArticleServiceImpl;
import com.xlc.spartacus.article.utils.RedisKeyUtils;
import com.xlc.spartacus.common.core.utils.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class ArticleScanAspect {
    private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.xlc.spartacus.article.aspect.ArticleScan)")
    public void PageViewAspect() {

    }

    /**
     * 切入处理
     */
    @Around("PageViewAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long articleId = (Long)args[0];
        HttpServletRequest request = (HttpServletRequest)args[1];
        String ip = IPUtils.getIp(request);

        Object obj = null;
        try {
            logger.info("客户端ip：{}，访问的文章：{}", ip, articleId);

            //刷新浏览量
            String key = RedisKeyUtils.getScanNumberKey(articleId);
            Long num = redisTemplate.opsForHyperLogLog().add(key, ip);
            if (num == 0) {
                Long newScanNumber = redisTemplate.opsForHyperLogLog().size(key);
                logger.info("客户端ip：{}已经访问过文章{}了，当前新增访问量：{}", ip, articleId, newScanNumber);
            }

            obj = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("统计文章：{}的访问量发生异常，当前客户端ip：{}", articleId, ip, e);
        }
        return obj;
    }

}
