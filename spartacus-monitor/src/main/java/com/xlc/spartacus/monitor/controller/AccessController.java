package com.xlc.spartacus.monitor.controller;

import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.monitor.exception.GlobalException;
import com.xlc.spartacus.monitor.pojo.AccessForbid;
import com.xlc.spartacus.monitor.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问详情获取 接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/access")
public class AccessController {
	
	@Autowired
	AccessService accessService;
	
	
	@RequestMapping("/getStatistics")
	public BaseResponse getStatistics() throws GlobalException {
	    return accessService.getStatistics();
	}
	
	@RequestMapping("/getScanRecords")
	public BaseResponse getScanRecords(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
	    return accessService.getScanRecords(baseRequest.getFlag(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}
	
	@RequestMapping("/getHighFrequencyAccessess")
	public BaseResponse getHighFrequencyAccessess(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
	    return accessService.getHighFrequencyAccesses(baseRequest.getFlag(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}
	
	@RequestMapping("/forbid")
	public BaseResponse forbid(@ModelAttribute AccessForbid forbid) throws GlobalException {
	    return accessService.forbid(forbid);
	}
	
	@RequestMapping("/unForbid")
	public BaseResponse unForbid(String ip) throws GlobalException {
	    return accessService.unForbid(ip);
	}
	
	@RequestMapping(value = "/getForbids", method = RequestMethod.POST)
	public BaseResponse getForbids(String flag, Integer currentPage, Integer pageSize) throws GlobalException {
		return accessService.getForbids(flag, currentPage, pageSize);
	}
	
	@RequestMapping(value = "/batchUnForbid", method = RequestMethod.POST)
    public BaseResponse batchUnForbid(String jsonParams) throws GlobalException {
        return accessService.batchUnForbid(jsonParams);
    }
}
