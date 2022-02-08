package com.xlc.spartacus.article.service.impl;

import com.xlc.spartacus.article.exception.GlobalException;
import com.xlc.spartacus.article.mapper.CategoryMapper;
import com.xlc.spartacus.article.pojo.Category;
import com.xlc.spartacus.article.service.CategoryService;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * service层
 *
 * @author xlc, since 2021
 */
@Component
public class CategoryServiceImpl implements CategoryService {
	
	private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	

	@Resource
	private CategoryMapper categoryMapper;


	@Override
	public BaseResponse findAll() throws GlobalException {
		try {
			List<Category> categories = categoryMapper.findAll();
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, categories);
		} catch (Exception e) {
			logger.error("获取文章分类失败！", e);
			return new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1);
		}
	}

}
