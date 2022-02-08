package com.xlc.spartacus.article.mapper;

import com.xlc.spartacus.article.pojo.Article;
import com.xlc.spartacus.article.pojo.ArticleLike;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * mapper层
 *
 * @author xlc, since 2021
 */
@Mapper
public interface ArticleMapper {

    Integer updateArticleScanNumber(Article article);

    Integer getArticleScanNumber(Long id);

    List<Long> getAllArticleId();

    Integer save(Article article);

    Integer update(Article article);

    Integer updateLikeNumber(Article article);

    List<Article> findByStatus(Integer status);

    Article findById(Long id);

    void deleteById(Long id);

    List<Article> findByCategory(Integer isTop, String cname);

    List<Article> findByCriteria(Integer isLikeMost, Integer isReadMost);

}