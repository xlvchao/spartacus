package com.xlc.spartacus.monitor.controller;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.monitor.service.ScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务执行情况获取 接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/task")
public class ScheduleTaskController {
	
	@Autowired
	ScheduleTaskService scheduleTaskService;

	@RequestMapping("/getRecentTaskExeRecords")
	public CommonResponse getRecentTaskExeRecords() {
		try {
			return scheduleTaskService.getRecentTaskExeRecords();
		} catch (Exception e) {
			return CommonResponse.failed("获取最近登录记录出错！");
		}
	}
	
}
