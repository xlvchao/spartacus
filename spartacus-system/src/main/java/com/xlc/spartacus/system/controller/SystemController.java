package com.xlc.spartacus.system.controller;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.system.pojo.FriendLink;
import com.xlc.spartacus.system.pojo.Notice;
import com.xlc.spartacus.system.pojo.Profile;
import com.xlc.spartacus.system.pojo.UserConnection;
import com.xlc.spartacus.system.service.SystemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/system")
public class SystemController {
	
	@Resource
	private SystemService systemService;


	@RequestMapping(value="/loadBlogProfile", method = RequestMethod.GET)
	public CommonResponse loadBlogProfile() {
		return systemService.loadBlogProfile();
	}

	@RequestMapping(value="/updateBlogProfile", method = RequestMethod.POST)
	public CommonResponse updateBlogProfile(@ModelAttribute Profile profile) {
		return systemService.updateBlogProfile(profile);
	}

	@RequestMapping(value="/searchSystemUser", method = RequestMethod.POST)
	public CommonResponse searchSystemUser(@ModelAttribute BaseRequest baseRequest) {
		return systemService.searchSystemUser(baseRequest.getUserId(), baseRequest.getStatus(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/batchDeleteSystemUser", method = RequestMethod.POST)
	public CommonResponse batchDeleteSystemUser(@RequestBody String[] userIds) {
		return systemService.batchDeleteSystemUser(userIds);
	}

	@RequestMapping(value = "/batchSetSystemUserStatus", method = RequestMethod.POST)
	public CommonResponse batchSetSystemUserStatus(@RequestBody Map<String, Object> params) {
		return systemService.batchSetSystemUserStatus((Integer)params.get("status"), (List<Long>)params.get("userIds"));
	}

	@RequestMapping("/deleteSystemUser")
	public CommonResponse deleteSystemUser(@RequestParam String userId) {
		return systemService.deleteSystemUser(userId);
	}

	@RequestMapping("/editSystemUser")
	public CommonResponse editSystemUser(@ModelAttribute UserConnection user) {
		return systemService.editSystemUser(user);
	}

	@RequestMapping("/querySystemUser")
	public CommonResponse querySystemUser(@ModelAttribute BaseRequest baseRequest) {
		return systemService.querySystemUser(baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value ="/addSystemUser", method = RequestMethod.POST)
	public CommonResponse addSystemUser(@ModelAttribute UserConnection user) {
		return systemService.addSystemUser(user);
	}

	@RequestMapping("/searchFriendLink")
	public CommonResponse searchFriendLink(@ModelAttribute BaseRequest baseRequest) {
		return systemService.searchFriendLink(baseRequest.getSiteName(), baseRequest.getSiteAddress(), baseRequest.getIsValid(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/batchDeleteFriendLink", method = RequestMethod.POST)
	public CommonResponse batchDeleteFriendLink(@RequestBody long[] ids) {
		return systemService.batchDeleteFriendLink(ids);
	}

	@RequestMapping(value = "/batchSetFriendLink", method = RequestMethod.POST)
	public CommonResponse batchSetFriendLink(@RequestBody Map<String, Object> params) {
		return systemService.batchSetFriendLink((Integer)params.get("isValid"), (List<Long>)params.get("ids"));
	}

	@RequestMapping(value = "/deleteFriendLink", method = RequestMethod.POST)
	public CommonResponse deleteFriendLink(@RequestParam long id) {
		return systemService.deleteFriendLink(id);
	}

	@RequestMapping("/addOrEditFriendLink")
	public CommonResponse addOrEditFriendLink(@ModelAttribute FriendLink friendLink) {
		return systemService.addOrEditFriendLink(friendLink);
	}

	@RequestMapping("/queryFriendLink")
	public CommonResponse queryFriendLink(@ModelAttribute BaseRequest baseRequest) {
		return systemService.queryFriendLink(baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/findNoticeByCriteria")
	public CommonResponse findNoticeByCriteria(@ModelAttribute BaseRequest baseRequest) {
		return systemService.findNoticeByCriteria(baseRequest.getIsForSite(), baseRequest.getIsForChat());
	}

	@RequestMapping("/searchNotice")
	public CommonResponse searchNotice(@ModelAttribute BaseRequest baseRequest) {
		return systemService.searchNotice(baseRequest.getIsForSite(), baseRequest.getIsForChat(), baseRequest.getIsEnabled(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/batchDeleteNotice", method = RequestMethod.POST)
	public CommonResponse batchDeleteNotice(@RequestBody long[] ids) {
		return systemService.batchDeleteNotice(ids);
	}

	@RequestMapping(value = "/batchSetNotice", method = RequestMethod.POST)
	public CommonResponse batchSetNotice(@RequestBody Map<String, Object> params) {
		return systemService.batchSetNotice((String)params.get("field"), (Integer)params.get("value"), (List<Long>)params.get("ids"));
	}

	@RequestMapping(value = "/deleteNotice", method = RequestMethod.POST)
	public CommonResponse deleteNotice(@RequestParam long id) {
		return systemService.deleteNotice(id);
	}

	@RequestMapping("/addOrEditNotice")
	public CommonResponse addOrEditNotice(@ModelAttribute Notice notice) {
		return systemService.addOrEditNotice(notice);
	}

	@RequestMapping("/queryNotice")
	public CommonResponse queryNotice(@ModelAttribute BaseRequest baseRequest) {
		return systemService.queryNotice(baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping(value = "/batchDeleteLoginRecord", method = RequestMethod.POST)
	public CommonResponse batchDeleteLoginRecord(@RequestBody long[] ids) {
		return systemService.batchDeleteLoginRecord(ids);
	}

	@RequestMapping(value = "/deleteLoginRecord", method = RequestMethod.POST)
	public CommonResponse deleteLoginRecord(@RequestParam long id) {
		return systemService.deleteLoginRecord(id);
	}

	@RequestMapping("/searchLoginRecord")
	public CommonResponse searchLoginRecord(@ModelAttribute BaseRequest baseRequest) {
		return systemService.searchLoginRecord(baseRequest.getUsername(), baseRequest.getUserType(),baseRequest.getIp(),baseRequest.getCity(),baseRequest.getClient(),baseRequest.getLoginTimeStart(),baseRequest.getLoginTimeEnd(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/queryLoginRecord")
	public CommonResponse queryLoginRecord(@ModelAttribute BaseRequest baseRequest) {
		return systemService.queryLoginRecord(baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}


}
