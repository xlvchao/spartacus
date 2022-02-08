package com.xlc.spartacus.monitor.service;

import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.monitor.pojo.NetIO;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务器详情 接口定义
 *
 * @author xlc, since 2021
 */
public interface ServerService {


	/**
    * 获取集群服务器主机的IP、端口号
    *  
    * @return
    * @throws BaseException
    */
	BaseResponse getServerHosts() throws BaseException;

	
	/**
	* 获取服务器设备信息
	*
	* @param ip
	* @param device 只能取值cpu、memory、swap、disk
	* @return
	* @throws BaseException
	*/
	BaseResponse getServerStatus(String ip, Integer port, String device) throws BaseException;


	/**
    * 获取网卡流量统计信息
    *  
    * @return
    * @throws BaseException
    */
	BaseResponse getNetIODetails(String ip) throws BaseException;


	default Map<Object, Object> processDay(List<NetIO> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();
		System.out.println(list);
		for(int i=0; i <= 24; i++) {
			if(i < 10) {
				inMap.put("0" + i + ":00", 0);
				outMap.put("0" + i + ":00", 0);
			} else {
				inMap.put(i + ":00", 0);
				outMap.put(i + ":00", 0);
			}
		}

		//标记是否有第二天0点0分0秒的数据
		boolean hasTomorrowZeroClockNetIoData = false;
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(int i = 0; i < list.size(); i++) {
			String time = CommonUtils.getDateString("HH:mm", list.get(i).getInsertDate());
			inMap.put(time, list.get(i).getIoIn());
			outMap.put(time, list.get(i).getIoOut());

			if(time.equals("00:00")) {
				if(fmt.format(cal.getTime()).equals(fmt.format(list.get(i).getInsertDate()))) {
					hasTomorrowZeroClockNetIoData = true;
				}
			}
		}

		if(hasTomorrowZeroClockNetIoData) {
			inMap.put("24:00", inMap.get("00:00"));
			outMap.put("24:00", outMap.get("00:00"));
		}
		inMap.put("00:00", 0f);
		outMap.put("00:00", 0f);
		map.put("ioIn", inMap);
		map.put("ioOut", outMap);
		return map;
	}

	default Map<Object, Object> processMonth(List<NetIO> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
		cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int days = cal.get(Calendar.DATE);
		for(int i=1; i<=days; i++) {
			inMap.put(i, 0);
			outMap.put(i, 0);
		}
		for(NetIO vo : list) {
			inMap.put(CommonUtils.getWhichDay(vo.getInsertDate()), vo.getIoIn());
			outMap.put(CommonUtils.getWhichDay(vo.getInsertDate()), vo.getIoOut());
		}

		map.put("ioIn", inMap);
		map.put("ioOut", outMap);
		return map;
	}

	default Map<Object, Object> processYear(List<NetIO> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();

		for(int i=1; i<=12; i++) {
			inMap.put(i, 0);
			outMap.put(i, 0);
		}
		for(NetIO vo : list) {
			inMap.put(CommonUtils.getWhichMonth(vo.getInsertDate()), vo.getIoIn());
			outMap.put(CommonUtils.getWhichMonth(vo.getInsertDate()), vo.getIoOut());
		}

		map.put("ioIn", inMap);
		map.put("ioOut", outMap);
		return map;
	}

}
