package com.xlc.spartacus.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.AliHttpUtils;
import com.xlc.spartacus.common.core.utils.CoorTransformer;
import com.xlc.spartacus.common.core.utils.HttpUtils;
import com.xlc.spartacus.monitor.config.CommonProperties;
import com.xlc.spartacus.monitor.service.CloudApiService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云市场api 接口实现
 *
 * @author xlc, since 2021
 */
@Component
public class CloudApiServiceImpl implements CloudApiService {

	private static Logger logger = Logger.getLogger(CloudApiServiceImpl.class);
	
	@Resource
    private CommonProperties commonProperties;


	@Override
	public double[] getCoorByIp(String ip) throws BaseException {
		try {
			String url = commonProperties.getBaiduMapUrl() + "&ip="+ ip + "&ak=" + commonProperties.getBaiduMapAk();
			String result = HttpUtils.get(url);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger("status") == 0) {
				String lon_gcj02 = jsonObject.getJSONObject("content").getJSONObject("point").getString("x");
				String lat_gcj02 = jsonObject.getJSONObject("content").getJSONObject("point").getString("y");
				double[] point_wgs84 = CoorTransformer.gcj02towgs84(Double.valueOf(lon_gcj02), Double.valueOf(lat_gcj02));
				return point_wgs84;
			}

		} catch (Exception e) {
			logger.error("queryCityWeather->查询天气失败！", e);
		}

		return new double[2];
	}

	@Override
	public BaseResponse queryCityWeather(String area) throws BaseException {
		try {
			String host = commonProperties.getAliyunWeatherUrl();
			String path = "/day15";
			String method = "GET";
			String appcode = commonProperties.getAliyunAppCode();
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "APPCODE " + appcode);
			Map<String, String> querys = new HashMap<String, String>();
			querys.put("area", area);

			HttpResponse response = AliHttpUtils.doGet(host, path, method, headers, querys);
			if (response.getStatusLine().getStatusCode() == 200) {
				String respString = EntityUtils.toString(response.getEntity(), "UTF-8");
				JSONObject respJson = JSONObject.parseObject(respString);
				if(respJson.getInteger("showapi_res_code") == 0) {
					JSONObject showapi_res_body = respJson.getJSONObject("showapi_res_body");
					if(showapi_res_body.getInteger("ret_code") == 0) {
						List<Map<String, Object>> dayList = JSON.parseObject(showapi_res_body.get("dayList").toString(), List.class);
						return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, showapi_res_body.get("dayList"));
					}
				}
			}
		} catch (Exception e) {
			logger.error("queryCityWeather->查询天气失败！", e);
		}

		return new BaseResponse(RespConstant.CODE_1, RespConstant.MSG_1);
	}

}