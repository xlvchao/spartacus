package com.xlc.spartacus.monitor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.monitor.config.CommonProperties;
import com.xlc.spartacus.monitor.mapper.AccessForbidMapper;
import com.xlc.spartacus.monitor.mapper.AccessMapper;
import com.xlc.spartacus.monitor.pojo.Access;
import com.xlc.spartacus.monitor.pojo.AccessForbid;
import com.xlc.spartacus.monitor.pojo.HighFrequencyAccess;
import com.xlc.spartacus.monitor.service.AccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 访问统计 接口实现
 *
 * @author xlc, since 2021
 */
@Service
@DS("spartacus")
public class AccessServiceImpl implements AccessService {
	
	private static Logger logger = LoggerFactory.getLogger(AccessServiceImpl.class);
	
	@Resource
	private AccessForbidMapper accessForbidMapper;

	@Resource
	private AccessMapper accessMapper;

	@Resource
	private CommonProperties commonProperties;

	@Resource
	private RedisTemplate redisTemplate;
	
	
	@Override
	public BaseResponse getStatistics() throws BaseException {
	    try {
			logger.info("开始获取统计信息！");
	    	HashMap<String, Object> statistics = new HashMap<>();
	    	statistics.put("totalScan", accessMapper.getTotalScanCount());
	    	statistics.put("todayScan", accessMapper.getTodayScanCount());
	    	statistics.put("yestodayScan", accessMapper.getYestodayScanCount());
	    	statistics.put("todayScanDetails", processDay(accessMapper.getTodayScanDetails()));
	    	statistics.put("monthScan", accessMapper.getMonthScanCount());
	    	statistics.put("lastMonthScan", accessMapper.getLastMonthScanCount());
	    	statistics.put("monthScanDetails", processMonth(accessMapper.getMonthScanDetails(), false));
	    	statistics.put("yearScan", accessMapper.getYearScanCount());
	    	statistics.put("lastYearScan", accessMapper.getLastYearScanCount());
	    	statistics.put("yearScanDetails", processYear(accessMapper.getYearScanDetails()));
	    	
	    	statistics.put("totalVisitor", accessMapper.getTotalVisitorCount());
	    	statistics.put("todayVisitor", accessMapper.getTodayVisitorCount());
	    	statistics.put("yestodayVisitor", accessMapper.getYestodayVisitorCount());
	    	statistics.put("todayVisitorDetails", processDay(accessMapper.getTodayVisitorDetails()));
	    	statistics.put("monthVisitor", accessMapper.getMonthVisitorCount());
	    	statistics.put("lastMonthVisitor", accessMapper.getLastMonthVisitorCount());
	    	statistics.put("monthVisitorDetails", processMonth(accessMapper.getMonthVisitorDetails(), false));
	    	statistics.put("yearVisitor", accessMapper.getYearVisitorCount());
	    	statistics.put("lastYearVisitor", accessMapper.getLastYearVisitorCount());
	    	statistics.put("yearVisitorDetails", processYear(accessMapper.getYearVisitorDetails()));
	    	
	    	statistics.put("totalIP", accessMapper.getTotalIPCount());
	    	statistics.put("todayIP", accessMapper.getTodayIPCount());
	    	statistics.put("yestodayIP", accessMapper.getYestodayIPCount());
	    	statistics.put("todayIPDetails", processDay(accessMapper.getTodayIPDetails()));
	    	statistics.put("monthIP", accessMapper.getMonthIPCount());
	    	statistics.put("lastMonthIP", accessMapper.getLastMonthIPCount());
	    	statistics.put("monthIPDetails", processMonth(accessMapper.getMonthIPDetails(), false));
	    	statistics.put("yearIP", accessMapper.getYearIPCount());
	    	statistics.put("lastYearIP", accessMapper.getLastYearIPCount());
	    	statistics.put("yearIPDetails", processYear(accessMapper.getYearIPDetails()));
	    	
	    	statistics.put("totalComment", accessMapper.getTotalCommentCount());
	    	statistics.put("todayComment", accessMapper.getTodayCommentCount());
	    	statistics.put("yestodayComment", accessMapper.getYestodayCommentCount());
	    	statistics.put("todayCommentDetails", processDay(accessMapper.getTodayCommentDetails()));
	    	statistics.put("monthComment", accessMapper.getMonthCommentCount());
	    	statistics.put("lastMonthComment", accessMapper.getLastMonthCommentCount());
	    	statistics.put("monthCommentDetails", processMonth(accessMapper.getMonthCommentDetails(), false));
	    	statistics.put("yearComment", accessMapper.getYearCommentCount());
	    	statistics.put("lastYearComment", accessMapper.getLastYearCommentCount());
	    	statistics.put("yearCommentDetails", processYear(accessMapper.getYearCommentDetails()));

			logger.info("获取统计信息成功：" + statistics);
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, statistics);

	    } catch (Exception e) {
	    	logger.error("获取统计信息发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取统计信息发生异常！");
	    }
	}

	@Override
	public BaseResponse getScanRecords(String flag, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取访问记录：" + flag);

	        List<Access> records = null;
			PageHelper.startPage(currentPage, pageSize);
			if(flag.equals("day")) {
				records = accessMapper.getTodayScan();
			} else if(flag.equals("month")) {
				records = accessMapper.getMonthScan();
			} else if(flag.equals("year")) {
				records = accessMapper.getYearScan();
			} else if(flag.equals("total")) {
				records = accessMapper.getTotalScan();
			}
			PageInfo<Access> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
							.currentPage(currentPage)
							.pageSize(pageSize)
							.total((int) pageInfo.getTotal())
							.totalPages(pageInfo.getPages())
							.records(pageInfo.getList())
							.build();

			logger.info("获取访问记录成功：" + page);
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

	    } catch (Exception e) {
	    	logger.error("获取浏览记录发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取浏览记录发生异常！");
	    }
	}


	@Override
	public BaseResponse getHighFrequencyAccesses(String flag, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取高频访问记录：" + flag);

	        List<HighFrequencyAccess> records = null;
			PageHelper.startPage(currentPage, pageSize);
			if(flag.equals("day")) {
				records = accessMapper.getTodayHighFrequencyAccessess(commonProperties.getDayAccessThreshold());
				records.forEach(it -> it.setAnalysis(analysis(accessMapper.getTodayHighFrequencyAccessessDateList(it.getIp()), commonProperties.getAccessAvgInterval(), commonProperties.getAccessWindow())));
			} else if(flag.equals("month")) {
				records = accessMapper.getMonthHighFrequencyAccessess(commonProperties.getMonthAccessThreshold());
				records.forEach(it -> it.setAnalysis(analysis(accessMapper.getMonthHighFrequencyAccessessDateList(it.getIp()), commonProperties.getAccessAvgInterval(), commonProperties.getAccessWindow())));
			} else if(flag.equals("year")) {
				records = accessMapper.getYearHighFrequencyAccessess(commonProperties.getYearAccessThreshold());
				records.forEach(it -> it.setAnalysis(analysis(accessMapper.getYearHighFrequencyAccessessDateList(it.getIp()), commonProperties.getAccessAvgInterval(), commonProperties.getAccessWindow())));
			} else if(flag.equals("total")) {
				records = accessMapper.getTotalHighFrequencyAccessess(commonProperties.getTotalAccessThreshold());
				records.forEach(it -> it.setAnalysis(analysis(accessMapper.getTotalHighFrequencyAccessessDateList(it.getIp()), commonProperties.getAccessAvgInterval(), commonProperties.getAccessWindow())));
			}
			PageInfo<HighFrequencyAccess> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
							.currentPage(currentPage)
							.pageSize(pageSize)
							.total((int) pageInfo.getTotal())
							.totalPages(pageInfo.getPages())
							.records(pageInfo.getList())
							.build();

			logger.info("获取高频访问记录成功：" + page);
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

	    } catch (Exception e) {
	    	logger.error("获取高频访问记录发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取高频访问记录发生异常！");
	    }
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse forbid(AccessForbid forbid) throws BaseException {
		try {
			logger.info("开始执行访问封禁：" + forbid);
			redisTemplate.opsForSet().add("FORBIDDEN_ACCESS_IP_SET", forbid.getIp());
			forbid.setId(Snowflake.generateId());
			forbid.setDayCount(accessMapper.getTodayAccessCountByIp(forbid.getIp()));
			forbid.setMonthCount(accessMapper.getMonthAccessCountByIp(forbid.getIp()));
			forbid.setYearCount(accessMapper.getYearAccessCountByIp(forbid.getIp()));
			forbid.setTotalCount(accessMapper.getTotalAccessCountByIp(forbid.getIp()));
			forbid.setOperateTime(new Date());
			accessForbidMapper.save(forbid);
			logger.info("访问封禁执行成功！");
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);
	    } catch (Exception e) {
	    	logger.error("执行访问封禁发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "执行访问封禁发生异常！");
	    }
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse unForbid(String ip) throws BaseException {
		try {
			logger.info("开始执行访问解禁：" + ip);
			redisTemplate.opsForSet().remove("FORBIDDEN_ACCESS_IP_SET", ip);
			accessForbidMapper.deleteByIp(ip);
			logger.info("访问解禁执行成功！");
	    	return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

	    } catch (Exception e) {
	    	logger.error("执行访问解禁发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "执行访问解禁发生异常！");
	    }
	}


	@Override
	public BaseResponse getForbids(String flag, Integer currentPage, Integer pageSize) throws BaseException {
		try {
			logger.info("开始获取封禁列表！");

			PageHelper.startPage(currentPage, pageSize).setOrderBy("operate_time desc");
			List<AccessForbid> forbidList = accessForbidMapper.findAll();
			PageInfo<AccessForbid> pageInfo = new PageInfo<>(forbidList);
			Page page = Page.builder()
							.currentPage(currentPage)
							.pageSize(pageSize)
							.total((int) pageInfo.getTotal())
							.totalPages(pageInfo.getPages())
							.records(pageInfo.getList())
							.build();

	        logger.info("获取封禁列表成功：" + page);
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0, page);

		} catch (Exception e) {
			logger.error("获取封禁列表发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "获取封禁列表发生异常！");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public BaseResponse batchUnForbid(String jsonParams) throws BaseException {
		try {
			logger.info("开始批量解禁访问黑名单：" + jsonParams);

			List<String> ips = new ArrayList<>();
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				String ip = jsonArray.getJSONObject(i).getString("ip");
				ips.add(ip);
			}
			redisTemplate.opsForSet().remove("FORBIDDEN_ACCESS_IP_SET", ips.toArray());
			accessForbidMapper.batchDelete(ips);

			logger.info("批量解禁访问黑名单成功！");
			return new BaseResponse(RespConstant.CODE_0, RespConstant.MSG_0);

		} catch (Exception e) {
			logger.error("批量解禁访问黑名单发生异常！", e);
			return new BaseResponse(RespConstant.CODE_1, "批量解禁访问黑名单发生异常！");
		}
	}

}
