package com.xlc.spartacus.system.service.impl;

import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.constant.RespConstant;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import com.xlc.spartacus.common.core.pojo.Page;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.common.core.utils.Converts;
import com.xlc.spartacus.common.core.utils.Snowflake;
import com.xlc.spartacus.common.es.ElasticsearchService;
import com.xlc.spartacus.system.constant.SystemConstant;
import com.xlc.spartacus.system.mapper.SystemMapper;
import com.xlc.spartacus.system.pojo.*;
import com.xlc.spartacus.system.service.SystemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统服务接口 实现
 *
 * @author xlc, since 2021
 */
@Component
public class SystemServiceImpl implements SystemService {
	
	private Logger logger = LoggerFactory.getLogger(SystemServiceImpl.class);

	@Resource
	private ElasticsearchService elasticsearchService;

	@Resource
	private SystemMapper systemMapper;

	@Resource
	PasswordEncoder passwordEncoder;


	@Override
	public CommonResponse loadBlogProfile() {
		try {
			Profile profile = systemMapper.loadBlogProfile();
			return CommonResponse.success(profile);

		} catch (Exception e) {
			logger.error("加载博主基本信息异常！", e);
			return CommonResponse.failed("加载博主基本信息异常！");
		}
	}


	@Override
	public CommonResponse updateBlogProfile(Profile profile) {
		try {
			systemMapper.updateBlogProfile(profile);
			return CommonResponse.success("更新博主基本信息成功！");

		} catch (Exception e) {
			logger.error("更新博主基本信息异常！", e);
			return CommonResponse.failed("更新博主基本信息异常！");
		}
	}


	@Override
	public CommonResponse searchSystemUser(String userId, Integer status, Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始搜索系统账户：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<UserConnection> records = systemMapper.searchSystemUser(userId, status);
			PageInfo<UserConnection> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("搜索系统账户成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("搜索系统账户发生异常！", e);
			return CommonResponse.failed("搜索系统账户发生异常！");
		}
	}

	@Override
	public CommonResponse batchDeleteSystemUser(String[] userIds) {
		try {
			logger.info("开始批量删除系统账号", userIds);
			systemMapper.batchDeleteSystemUser(userIds);
			logger.info("批量删除系统账号成功!");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量删除系统账号发生异常！", e);
			return CommonResponse.failed("批量删除系统账号发生异常！");
		}
	}

	@Override
	public CommonResponse batchSetSystemUserStatus(Integer status, List<Long> userIds) {
		try {
			logger.info("开始批量修改系统账号状态: " + CommonUtils.concat(status, userIds));
			systemMapper.batchSetSystemUserStatus(status, userIds);
			logger.info("批量修改系统账号状态成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量修改系统账号状态发生异常！", e);
			return CommonResponse.failed("批量修改系统账号状态发生异常！");
		}
	}

	@Override
	public CommonResponse deleteSystemUser(String userId) {
		try {
			logger.info("开始删除系统用户：" + userId);
			systemMapper.deleteSystemUser(userId);
			logger.info("删除系统用户成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("删除系统用户发生异常！", e);
			return CommonResponse.failed("删除系统用户发生异常！");
		}
	}

	@Override
	public CommonResponse editSystemUser(UserConnection user) {
		try {
			UserConnection storeUser = systemMapper.findSystemUserByUsername(user.getUserId());
			if(storeUser != null) {
				if(StringUtils.isNotBlank(user.getSecret())) {
					user.setSecret(passwordEncoder.encode(user.getSecret()));
				}
				systemMapper.updateSystemUser(user);
				return CommonResponse.success("更新成功！");
			}
			return CommonResponse.failed("账号不存在，更新失败！");

		} catch (Exception e) {
			logger.error("更新账户信息异常！", e);
			return CommonResponse.failed("更新账户信息异常！");
		}
	}

	@Override
	public CommonResponse addSystemUser(UserConnection user) {
		try {
			UserConnection localUser = systemMapper.findSystemUserByUsername(user.getUserId());
			if(localUser != null) {
				return CommonResponse.failed("已存在相同的用户名，添加失败！");
			}

			user.setProviderId("spartacus");
			user.setProviderUserId(user.getUserId());
			user.setSecret(passwordEncoder.encode(user.getSecret()));
			user.setAddTime(new Date());
			systemMapper.addSystemUser(user);
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("添加系统用户发生异常！", e);
			return CommonResponse.failed("添加系统用户发生异常！");
		}
	}

	@Override
	public CommonResponse querySystemUser(Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始获取系统用户：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<UserConnection> records = systemMapper.querySystemUser();
			PageInfo<UserConnection> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("获取系统用户成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("获取系统用户发生异常！", e);
			return CommonResponse.failed("获取系统用户发生异常！");
		}
	}

	@Override
	public CommonResponse searchFriendLink(String siteName, String siteAddress, Integer isValid, Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始搜索友情链接：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<FriendLink> records = systemMapper.searchFriendLink(siteName, siteAddress, isValid);
			PageInfo<FriendLink> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("搜索友情链接成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("搜索友情链接发生异常！", e);
			return CommonResponse.failed("搜索友情链接发生异常！");
		}
	}

	@Override
	public CommonResponse batchDeleteFriendLink(long[] ids) {
		try {
			logger.info("开始批量删除友情链接", ids);
			systemMapper.batchDeleteFriendLink(ids);
			logger.info("批量删除友情链接成功!");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量删除友情链接发生异常！", e);
			return CommonResponse.failed("批量删除友情链接发生异常！");
		}
	}

	@Override
	public CommonResponse batchSetFriendLink(Integer isValid, List<Long> ids) {
		try {
			logger.info("开始批量修改友情链接有效状态: " + CommonUtils.concat(isValid, ids));
			systemMapper.batchSetFriendLink(isValid, ids);
			logger.info("批量修改友情链接有效状态成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量修改友情链接有效状态发生异常！", e);
			return CommonResponse.failed("批量修改友情链接有效状态发生异常！");
		}
	}

	@Override
	public CommonResponse deleteFriendLink(long id) {
		try {
			logger.info("开始删除友情链接：" + id);
			systemMapper.deleteFriendLink(id);
			logger.info("删除友情链接成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("删除友情链接发生异常！", e);
			return CommonResponse.failed("删除友情链接发生异常！");
		}
	}

	@Override
	public CommonResponse addOrEditFriendLink(FriendLink friendLink) {
		try {
			logger.info("开始添加/修改友情链接：" + friendLink);
			if(friendLink.getId() != null) {
				systemMapper.updateFriendLink(friendLink);
			} else {
				friendLink.setId(Snowflake.generateId());
				friendLink.setAddTime(new Date());
				systemMapper.addFriendLink(friendLink);
			}
			logger.info("添加/修改友情链接成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("添加/修改友情链接发生异常！", e);
			return CommonResponse.failed("添加/修改友情链接发生异常！");
		}
	}

	@Override
	public CommonResponse queryFriendLink(Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始获取友情链接：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<FriendLink> records = systemMapper.queryFriendLink();
			PageInfo<FriendLink> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("获取友情链接成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("获取友情链接发生异常！", e);
			return CommonResponse.failed("获取友情链接发生异常！");
		}
	}

	@Override
	public CommonResponse batchSetNotice(String field, Integer value, List<Long> ids) {
		try {
			logger.info("开始批量修改通知公告: " + CommonUtils.concat(field, value, ids));
			systemMapper.batchSetNotice(field, value, ids);
			logger.info("批量修改通知公告成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量修改通知公告发生异常！", e);
			return CommonResponse.failed("批量修改通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse batchDeleteNotice(long[] ids) {
		try {
			logger.info("开始批量删除通知公告", ids);
			systemMapper.batchDeleteNotice(ids);
			logger.info("批量删除通知公告成功!");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量删除通知公告发生异常！", e);
			return CommonResponse.failed("批量删除通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse findNoticeByCriteria(Integer isForSite, Integer isForChat) {
		try {
			logger.info("开始根据条件搜索通知公告：" + CommonUtils.concat(isForSite, isForChat));
			List<Notice> records = systemMapper.findNoticeByCriteria(isForSite, isForChat);
			logger.info("根据条件搜索通知公告成功：" + records);
			return CommonResponse.success(records);

		} catch (Exception e) {
			logger.error("根据条件搜索通知公告发生异常！", e);
			return CommonResponse.failed("根据条件搜索通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse searchNotice(Integer isForSite, Integer isForChat, Integer isEnabled, Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始搜索通知公告：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<Notice> records = systemMapper.searchNotice(isForSite, isForChat, isEnabled);
			PageInfo<Notice> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("搜索通知公告成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("搜索通知公告发生异常！", e);
			return CommonResponse.failed("搜索通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse deleteNotice(long id) {
		try {
			logger.info("开始删除通知公告：" + id);
			systemMapper.deleteNotice(id);
			logger.info("删除通知公告成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("删除通知公告发生异常！", e);
			return CommonResponse.failed("删除通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse addOrEditNotice(Notice notice) {
		try {
			logger.info("开始添加/修改通知公告：" + notice);
			if(notice.getId() != null) {
				systemMapper.updateNotice(notice);
			} else {
				notice.setId(Snowflake.generateId());
				notice.setCreateTime(new Date());
				systemMapper.addNotice(notice);
			}
			logger.info("添加/修改通知公告成功！");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("添加/修改通知公告发生异常！", e);
			return CommonResponse.failed("添加/修改通知公告发生异常！");
		}
	}

	@Override
	public CommonResponse queryNotice(Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始获取通知公告：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<Notice> records = systemMapper.queryNotice();
			PageInfo<Notice> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("获取通知公告成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("获取通知公告发生异常！", e);
			return CommonResponse.failed("获取通知公告发生异常！");
		}
	}


	@Override
	public CommonResponse searchLoginRecord(String username, String userType, String ip, String city, String client, String loginTimeStart, String loginTimeEnd, int currentPage, int pageSize) {
		try {
			logger.info("开始查询登陆记录");

			//完全匹配索引字段
			Map<String, Object> mustMatchs = new HashMap<>();
			if(StringUtils.isNotBlank(username)) {
				mustMatchs.put("username", username);
			}
			if(StringUtils.isNotBlank(ip)) {
				mustMatchs.put("ip", ip);
			}
			if(StringUtils.isNotBlank(city)) {
				mustMatchs.put("city", city);
			}
			if(StringUtils.isNotBlank(client)) {
				mustMatchs.put("client", client);
			}
			if(StringUtils.isNotBlank(userType)) {
				mustMatchs.put("user_type", userType);
			}

			//范围匹配索引字段
			Map<String, Map<String, Object>> rangeMatchs = new HashMap<>();
			if(StringUtils.isNotBlank(loginTimeStart) && StringUtils.isNotBlank(loginTimeEnd)) {
				Map<String, Object> rangeProps = new HashMap<>();
				rangeProps.put("gte", loginTimeStart);
				rangeProps.put("lte", loginTimeEnd);
				rangeProps.put("format", "yyyy-MM-dd HH:mm:ss");
				rangeMatchs.put("login_time", rangeProps);
			}

			Page page = elasticsearchService.searchMatch(SystemConstant.LOGIN_RECORD_INDEX_NAME, null, null, mustMatchs, rangeMatchs, "login_time", null, currentPage, pageSize);
			List<Map<String, Object>> records = (List<Map<String, Object>>) page.getRecords();
			page.setRecords(Converts.convertSnakeToCamel(records));

			logger.info("查询登陆记录成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("查询登陆记录发生异常！", e);
			return CommonResponse.failed("查询登陆记录发生异常！");
		}
	}

	@Override
	public CommonResponse batchDeleteLoginRecord(long[] ids) {
		try {
			logger.info("开始批量删除登陆记录", ids);
			systemMapper.batchDeleteLoginRecord(ids);
			logger.info("批量删除登陆记录成功!");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("批量删除登陆记录发生异常！", e);
			return CommonResponse.failed("批量删除登陆记录发生异常！");
		}
	}

	@Override
	public CommonResponse deleteLoginRecord(long id) {
		try {
			logger.info("开始删除登陆记录", id);
			systemMapper.deleteLoginRecord(id);
			logger.info("删除登陆记录成功!");
			return CommonResponse.success();

		} catch (Exception e) {
			logger.error("删除登陆记录发生异常！", e);
			return CommonResponse.failed("删除登陆记录发生异常！");
		}
	}

	@Override
	public CommonResponse queryLoginRecord(Integer currentPage, Integer pageSize) {
		try {
			logger.info("开始获取登陆记录：" + CommonUtils.concat(currentPage, pageSize));

			PageHelper.startPage(currentPage, pageSize);
			List<LoginRecord> records = systemMapper.queryLoginRecord();
			PageInfo<LoginRecord> pageInfo = new PageInfo<>(records);

			Page page = Page.builder()
					.currentPage(currentPage)
					.pageSize(pageSize)
					.total((int) pageInfo.getTotal())
					.totalPages(pageInfo.getPages())
					.records(pageInfo.getList())
					.build();

			logger.info("获取登陆记录成功：" + page);
			return CommonResponse.success(page);

		} catch (Exception e) {
			logger.error("获取登陆记录发生异常！", e);
			return CommonResponse.failed("获取登陆记录发生异常！");
		}
	}

}
