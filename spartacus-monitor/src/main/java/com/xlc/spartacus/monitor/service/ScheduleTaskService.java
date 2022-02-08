package com.xlc.spartacus.monitor.service;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.exception.BaseException;

/**
 * 定时任务执行详情 接口定义
 *
 * @author xlc, since 2021
 */
public interface ScheduleTaskService {


    /**
     * 获取最近的定时任务执行记录
	 *
	 * @return
     * @throws BaseException
	 */
	CommonResponse getRecentTaskExeRecords() throws BaseException;
	
}
