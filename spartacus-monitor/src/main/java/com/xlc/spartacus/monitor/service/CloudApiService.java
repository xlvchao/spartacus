package com.xlc.spartacus.monitor.service;

import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.monitor.exception.GlobalException;

/**
 * 云市场api 接口定义
 *
 * @author xlc, since 2021
 */
public  interface CloudApiService {


	/**
	 * 根据IP获取 WGS84 格式的 经度(lon)、纬度(lat)
	 *
	 * @param ip
	 * @return
	 * @throws GlobalException
	 */
	double[] getCoorByIp(String ip) throws BaseException;


	/**
	 * 根据 城市名（不带'市'字） 查询天气信息
	 *
	 * 阿里云市场
	 *
	 * @return
	 */
	BaseResponse queryCityWeather(String area) throws BaseException;
	
}