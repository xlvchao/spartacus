package com.xlc.spartacus.gateway.task;

import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.common.core.utils.HttpUtils;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.gateway.config.CommonProperties;
import com.xlc.spartacus.gateway.mapper.AccessMapper;
import com.xlc.spartacus.gateway.pojo.Access;
import com.xlc.spartacus.gateway.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 异步任务
 *
 * @author xlc, since 2021
 */
@Component
public class AsyncTask {
	private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

	@Resource
	AccessMapper accessMapper;

	@Resource
	private CommonProperties commonProperties;

	@Async("myAsync")
	public void saveAccessInfo(ServerHttpRequest request) {
		System.out.println("开始记录");
		accessMapper.saveAccessInfo(packAccessInfo(request));
		System.out.println("记录完成");
	}

	private Access packAccessInfo(ServerHttpRequest request) {
		Access access = new Access();
		access.setId(Snowflake.generateId());
		try {
			String clientId = request.getHeaders().getFirst("clientId");
			access.setClientId(clientId);

			String clientCurrentId = request.getHeaders().getFirst("clientCurrentId");
			access.setClientCurrentId(clientCurrentId);

			String uriPath = request.getURI().getPath();
			access.setUrl(uriPath);

			access.setAccessTime(new Date());

			String ip = IPUtils.getIp(request);
			access.setIp(ip);

			String url = commonProperties.getBaiduMapUrl() + "&ak=" + commonProperties.getBaiduMapAk() + "&ip=" + ip;
			JSONObject jsonObject = JSONObject.parseObject(HttpUtils.get(url));
			if (jsonObject.getInteger("status") == 0) {
				String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
				access.setIpCity(city);
			}
		} catch (Exception e) {
			logger.error("打包访问信息发生异常！", e);
		}

		return access;
	}

}