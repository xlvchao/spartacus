package com.xlc.spartacus.article.xxljob.jobhandler;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xlc.spartacus.article.mapper.ArticleLikeMapper;
import com.xlc.spartacus.article.mapper.ArticleMapper;
import com.xlc.spartacus.article.pojo.Article;
import com.xlc.spartacus.article.pojo.ArticleLike;
import com.xlc.spartacus.article.pojo.LikeStatus;
import com.xlc.spartacus.article.service.LikeService;
import com.xlc.spartacus.article.utils.RedisKeyUtils;
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
public class ArticleJobHandler {
    private static Logger logger = LoggerFactory.getLogger(ArticleJobHandler.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ArticleLikeMapper articleLikeMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private LikeService likeService;


    @XxlJob(value = "sinkLikeDataJobHandler")
    public void sinkLikeDataJobHandler() throws Exception {
        try{
            XxlJobHelper.log("开始下沉点赞数据");
            logger.info("开始下沉点赞数据");

            Set<String> keys = redisTemplate.keys(RedisKeyUtils.ARTICLE_LIKE_DATA_KEY_PREFIX.concat("*"));
            keys.forEach(key -> {
                Long articleId = Long.parseLong(key.substring(key.lastIndexOf(":") + 1));
                Long bitMapLength = (Long) redisTemplate.execute((RedisCallback<Long>) con -> {
                    byte[] valueBytes = con.get(key.getBytes());
                    return Long.valueOf(valueBytes.length * 8);
                });

                //TODO 1、点赞数据落库
                List<ArticleLike> list = new ArrayList<>();
                for (int i = 0; i < bitMapLength; i++) {
                    Boolean likeStatus = redisTemplate.opsForValue().getBit(key, i);
                    ArticleLike articleLike = ArticleLike.builder()
                            .articleId(articleId)
                            .userSequence(i)
                            .status(likeStatus ? LikeStatus.LIKE.getCode() : LikeStatus.UNLIKE.getCode())
                            .build();
                    list.add(articleLike);
                    while (list.size() == 200) {
                        articleLikeMapper.batchSinkLikeData(list);
                        list.clear();
                    }
                }
                articleLikeMapper.batchSinkLikeData(list);

                //TODO 2、更新文章点赞数量
                Integer count = likeService.getLikeCount(articleId);
                articleMapper.updateLikeNumber(Article.builder().id(articleId).likeNumber(count).build());
            });

            logger.info("下沉点赞数据完成！");
            XxlJobHelper.log("下沉点赞数据完成！");

            XxlJobHelper.handleSuccess();
        } catch (Exception e) {
            logger.error("下沉点赞数据异常！", e);

            XxlJobHelper.log("下沉点赞数据异常！");
            XxlJobHelper.handleFail();

            if (e instanceof InterruptedException) { //中断异常一定要抛出，否则调度中心的终止job功能将失效！
                throw e;
            }
        }
    }

    @XxlJob(value = "loadLikeDataJobHandler")
    public void loadLikeDataJobHandler() throws Exception {
        try{
            XxlJobHelper.log("开始加载点赞数据");
            logger.info("开始加载点赞数据");

            Set<String> keys = redisTemplate.keys(RedisKeyUtils.ARTICLE_LIKE_DATA_KEY_PREFIX.concat("*"));
            redisTemplate.delete(keys);

            int pageNum = 1;
            PageInfo<ArticleLike> pageInfo = null;
            do {
                pageInfo = PageHelper.startPage(pageNum, 200).doSelectPageInfo(() -> articleLikeMapper.findLikeData());
                pageInfo.getList().forEach(like -> {
                    likeService.setLikeData(like.getArticleId(), like.getUserSequence(), like.getStatus() == 1 ? true : false);
                });
            } while (pageInfo != null && pageInfo.getPages() >= (++pageNum));

            logger.info("加载点赞数据完成！");
            XxlJobHelper.log("加载点赞数据完成！");

            XxlJobHelper.handleSuccess();
        } catch (Exception e) {
            logger.error("加载点赞数据异常！", e);

            XxlJobHelper.log("加载点赞数据异常！");
            XxlJobHelper.handleFail();

            if (e instanceof InterruptedException) { //中断异常一定要抛出，否则调度中心的终止job功能将失效！
                throw e;
            }
        }
    }

    @XxlJob(value = "sinkScanNumberJobHandler")
    public void sinkScanNumberJobHandler() throws Exception {
        try{
            XxlJobHelper.log("开始下沉文章浏览量");
            logger.info("开始下沉文章浏览量");

            List<Long> ids = articleMapper.getAllArticleId();
            ids.forEach(articleId ->{
                //获取每一篇文章新增的浏览量，刷到数据库中
                String key = RedisKeyUtils.getScanNumberKey(articleId);
                Long newScanNumber = redisTemplate.opsForHyperLogLog().size(key);
                if(newScanNumber > 0) {
                    Integer curScanNumber = articleMapper.getArticleScanNumber(articleId);
                    Long sumScanNumber = newScanNumber + curScanNumber;
                    Article article = Article.builder().id(articleId).scanNumber(sumScanNumber.intValue()).build();
                    Integer result = articleMapper.updateArticleScanNumber(article);
                    if (result != 0) {
                        logger.info("文章：{}更新后的浏览量为：{}", articleId, sumScanNumber);
                        redisTemplate.delete(key);
                    }
                }
            });

            logger.info("下沉文章浏览量完成！");
            XxlJobHelper.log("下沉文章浏览量完成！");

            XxlJobHelper.handleSuccess();
        } catch (Exception e) {
            logger.error("下沉文章浏览量异常！", e);

            XxlJobHelper.log("下沉文章浏览量异常！");
            XxlJobHelper.handleFail();

            if (e instanceof InterruptedException) { //中断异常一定要抛出，否则调度中心的终止job功能将失效！
                throw e;
            }
        }
    }

}
