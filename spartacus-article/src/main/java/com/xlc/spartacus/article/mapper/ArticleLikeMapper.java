package com.xlc.spartacus.article.mapper;

import com.xlc.spartacus.article.pojo.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper层
 *
 * @author xlc, since 2021
 */
@Mapper
public interface ArticleLikeMapper {

    Integer batchSinkLikeData(@Param("articleLikes") List<ArticleLike> articleLikes);

    List<ArticleLike> findLikeData();
}