package com.xlc.spartacus.auth.task;

import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 异步任务
 *
 * @author xlc, since 2021
 */
@Component
public class AsyncTask {
	private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

	@Resource
	private UserConnectionMapper userConnectionMapper;


	/**
	 * 保存登录记录
	 *
	 * @param loginInfo
	 */
	@Async("myAsync")
	public void asyncRecordLoginInfo(Map<String, Object> loginInfo) {
		userConnectionMapper.recordLoginInfo(loginInfo);
	}

}