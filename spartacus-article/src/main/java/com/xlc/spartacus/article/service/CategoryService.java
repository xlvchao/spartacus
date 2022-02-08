package com.xlc.spartacus.article.service;

import com.xlc.spartacus.article.exception.GlobalException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;

/**
 * 接口定义层
 *
 * @author xlc, since 2021
 */
public interface CategoryService {


	/**
	 * 返回所有分类
	 *
	 * @return
	 * @throws GlobalException
	 */
	BaseResponse findAll() throws GlobalException;

}
