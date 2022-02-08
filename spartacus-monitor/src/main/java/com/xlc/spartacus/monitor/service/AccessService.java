package com.xlc.spartacus.monitor.service;

import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.monitor.pojo.AccessForbid;
import com.xlc.spartacus.monitor.pojo.Result;

import java.util.*;

/**
 * 访问统计 接口定义
 *
 * @author xlc, since 2021
 */
public interface AccessService {
	
	
	/**
    * 获取浏览量、访客、IP、评论等统计信息
    *  
    * @return
    * @throws BaseException
    */
	BaseResponse getStatistics() throws BaseException;
	
	/**
    * 分页获取访问记录
    *  
    * @param flag day/month/year/all
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BaseException
    */
	BaseResponse getScanRecords(String flag, Integer currentPage, Integer pageSize) throws BaseException;
	
	
	/**
    * 分页获取高频访问IP的信息
    *  
    * @param flag
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BaseException
    */
	BaseResponse getHighFrequencyAccesses(String flag, Integer currentPage, Integer pageSize) throws BaseException;
	
	/**
    * 封禁IP
    *  
    * @return
    * @throws BaseException
    */
	BaseResponse forbid(AccessForbid forbid) throws BaseException;
	
	/**
    * 解封IP
    *  
    * @param ip
    * @return
    * @throws BaseException
    */
	BaseResponse unForbid(String ip) throws BaseException;

	
	/**
    * 分页获取封禁列表
    *  
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BaseException
    */
	BaseResponse getForbids(String flag, Integer currentPage, Integer pageSize) throws BaseException;


	/**
    * 批量解封
    *  
    * @param jsonParams
    * @return
    * @throws BaseException
    */
	BaseResponse batchUnForbid(String jsonParams) throws BaseException;


	/**
	 * 根据某个IP的访问时间列表，分时段分析是否人机
	 *
	 * @param dates
	 * @return
	 */
	default Map<String, Object> analysis(List<Date> dates, int interval, int window) {
		Map<String, Object> map = new HashMap<>();
		Result[] results = new Result[12];
		for(int i =0; i< results.length; i++) {
			results[i] = new Result();
		}

		for (Date date : dates) {
			int start = 0, end = 1;
			String startTime = "", endTime = "";
			for (int i = 0; i < results.length; i++) {
				if (start < 10) {
					startTime = "0" + start + ":00:00";
					endTime = "0" + end + ":59:59";
				} else {
					startTime = start + ":00:00";
					endTime = end + ":59:59";
				}
				if (CommonUtils.isInPeriod(date, startTime, endTime)) {
					results[i].setCount(results[i].getCount() + 1);
					results[i].getDateList().add(date);
				}
				start += 2;
				end += 2;
			}
		}

		int start = 0, end = 1;
		for (int i = 0; i < results.length; i++) {
			float avgInterval = 0.00f;
			if(results[i].getDateList() != null && results[i].getDateList().size() > 0) {
				Object[] array = results[i].getDateList().toArray();
				float sum = 0.0f;
				for (int j = 0; j < array.length - 1; j++) {
					Date date1 = (Date) array[j];
					Date date2 = (Date) array[j+1];
					if(date2.compareTo(date1) != 0) {
						sum += Math.abs(date2.getTime() - date1.getTime());
					}
				}
				avgInterval =  sum / array.length / 1000;
			}
			results[i].setAvgInterval(avgInterval);

			boolean isRobot = results[i].getCount() > window && results[i].getAvgInterval() < interval;
			results[i].setIsRobot(isRobot);

			if (start < 10) {
				map.put("0" + start + ":00:00-0" + end + ":59:59", results[i]);
			} else {
				map.put(start + ":00:00-" + end + ":59:59", results[i]);
			}
			start += 2;
			end += 2;
		}
		return map;
	}


	/**
	 * 对统计SQL的结果集进行强制转换，否则会出问题
	 *
	 * @param list
	 * @return
	 */
	default HashMap<Object, Object> processDay(List<HashMap<Object, Object>> list) {
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=0; i<=24; i++) {
			map.put(i, 0);
		}
		for(HashMap<Object, Object> it : list) {
			map.put(Integer.parseInt(it.get("date").toString()), Integer.parseInt(it.get("count").toString()));
		}
		return map;
	}

	default HashMap<Object, Object> processMonth(List<HashMap<Object, Object>> list, boolean isLastMonth) {
		Calendar cal = Calendar.getInstance();
		if(isLastMonth) {
			cal.add(Calendar.MONTH, -1);//上个月
		}
		cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
		cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int days = cal.get(Calendar.DATE);
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=1; i<=days; i++) {
			map.put(i, 0);
		}
		for(HashMap<Object, Object> it : list) {
			map.put(Integer.parseInt(it.get("date").toString()), Integer.parseInt(it.get("count").toString()));
		}
		return map;
	}

	default HashMap<Object, Object> processYear(List<HashMap<Object, Object>> list) {
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=1; i<=12; i++) {
			map.put(i, 0);
		}
		for(HashMap<Object, Object> it : list) {
			map.put(Integer.parseInt(it.get("date").toString()), Integer.parseInt(it.get("count").toString()));
		}
		return map;
	}
	
}
