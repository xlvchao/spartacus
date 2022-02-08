package com.xlc.spartacus.article.mapper;

import com.xlc.spartacus.article.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * mapper层
 *
 * @author xlc, since 2021
 */
@Mapper
public interface CategoryMapper {

    List<Category> findAll();
}