package com.xlc.spartacus.monitor.controller;

import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.monitor.service.CloudApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 云市场调用 接口
 *
 * @author xlc, since 2021
 */
@RestController
public class CloudApiController {
	
	@Resource
	private CloudApiService cloudApiService;

	@RequestMapping("/wheather/queryCityWeather")
	public BaseResponse queryCityWeather(@RequestParam String cityname) {
		return cloudApiService.queryCityWeather(cityname);
	}

}
