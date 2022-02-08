package com.xlc.spartacus.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.HttpUtils;
import com.xlc.spartacus.monitor.mapper.NetIOMapper;
import com.xlc.spartacus.monitor.service.ServerService;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器详情 接口实现
 *
 * @author xlc, since 2021
 */
@Service
public class ServerServiceImpl implements ServerService {
	
	private static Logger logger = Logger.getLogger(ServerServiceImpl.class);
	
	@Resource
	private NetIOMapper netIOMapper;
	
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	
	
	@Override
	public BaseResponse getServerHosts() throws BaseException {
		try {
			logger.info("开始获取集群服务器主机信息！");
			Map<Object, Object> hosts = stringRedisTemplate.opsForHash().entries("monitor:server:hosts");
			logger.info("获取集群服务器主机信息成功：" + hosts);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, hosts);

	    } catch (Exception e) {
	    	logger.error("获取集群服务器主机信息发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取集群服务器主机信息发生异常！");
	    }
	}


	@Override
	public BaseResponse getServerStatus(String ip, Integer port, String device) throws BaseException {
		try {
			logger.info("开始获取集服务器设备状态信息：" + ip + "," + port + "," + device);
			String url = "http://" + ip + ":" + port + "/" + device;
			JSONObject jsonObject = JSONObject.parseObject(HttpUtils.get(url));
			logger.info("获取到的集服务器设备状态信息：" + jsonObject.toJSONString());

			if(jsonObject.containsKey("code") && jsonObject.getInteger("code") == 0) {
				return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, jsonObject.get("data"));
			} else {
				return new BaseResponse(RespConstant.CODE_1, "获取集服务器设备状态信息失败！");
			}

	    } catch (Exception e) {
	    	logger.error("获取集服务器设备状态信息发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取集服务器设备状态信息发生异常！");
	    }
	}


	@Override
	public BaseResponse getNetIODetails(String ip) throws BaseException {
		try {
			logger.info("开始获取网卡流量统计信息：" + ip);
			Map<Object, Object> countMap = new HashMap<>();
			Map<Object, Object> dayMap = processDay(netIOMapper.getTodayNetIoDetails(ip));
			Map<Object, Object> monthMap = processMonth(netIOMapper.getMonthNetIoDetails(ip));
			Map<Object, Object> yearMap = processYear(netIOMapper.getYearNetIoDetails(ip));
			
			countMap.put("day", dayMap);
			countMap.put("month", monthMap);
			countMap.put("year", yearMap);
			logger.info("获取网卡流量统计信息成功：" + countMap);
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, countMap);

	    } catch (Exception e) {
	    	logger.error("获取网卡流量统计信息发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取网卡流量统计信息发生异常！");
	    }
	}

}
