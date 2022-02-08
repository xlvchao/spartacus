package com.xlc.spartacus.monitor.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.exception.BaseException;
import com.xlc.spartacus.monitor.mapper.ScheduleTaskMapper;
import com.xlc.spartacus.monitor.service.ScheduleTaskService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 定时任务执行详情 接口实现
 *
 * @author xlc, since 2021
 */
@Service
@DS("xxljob")
public class ScheduleTaskServiceImpl implements ScheduleTaskService {
	
	private static Logger logger = Logger.getLogger(ScheduleTaskServiceImpl.class);
	
	@Resource
	private ScheduleTaskMapper scheduleTaskMapper;


	@Override
	public CommonResponse getRecentTaskExeRecords() throws BaseException {
		try {
			return CommonResponse.success(scheduleTaskMapper.getRecentTaskExeRecords());
		} catch (Exception e) {
			logger.error("获取最近定时任务记录出错！", e);
			return CommonResponse.failed("获取最近定时任务记录出错！");
		}
	}

}
