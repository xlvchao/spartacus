package com.xlc.spartacus.monitor.controller;

import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.monitor.exception.GlobalException;
import com.xlc.spartacus.monitor.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器详情获取 接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/server")
public class ServerController {
	
	@Autowired
	ServerService serverService;

	@RequestMapping("/getServerHosts")
	public BaseResponse getServerHosts() throws GlobalException {
	    return serverService.getServerHosts();
	}
	
	@RequestMapping("/getServerStatus")
	public BaseResponse getServerStatus(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
	    return serverService.getServerStatus(baseRequest.getIp(), baseRequest.getPort(), baseRequest.getDevice());
	}
	
	@RequestMapping("/getNetIoCountDetails")
	public BaseResponse getNetIoCountDetails(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
	    return serverService.getNetIODetails(baseRequest.getIp());
	}
	
}
