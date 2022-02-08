package com.xlc.spartacus.system.service;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.system.pojo.FriendLink;
import com.xlc.spartacus.system.pojo.Notice;
import com.xlc.spartacus.system.pojo.Profile;
import com.xlc.spartacus.system.pojo.UserConnection;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 系统服务接口 定义
 *
 * @author xlc, since 2021
 */
public interface SystemService {

	/**
	 * 加载博主基本信息
	 *
	 * @return
	 */
	CommonResponse loadBlogProfile();

	/**
	 * 更新博主基本信息
	 *
	 * @param profile
	 * @return
	 */
	CommonResponse updateBlogProfile(@ModelAttribute Profile profile);
	
	/**
	 * 基于ES的搜索引擎根据任意内容搜索文章
	 *  
	 * @return
	 */
	CommonResponse searchLoginRecord(String username, String userType, String ip, String city, String client, String loginTimeStart, String loginTimeEnd, int currentPage, int pageSize);

	/**
	 * 批量删除登陆记录
	 *
	 * @param ids
	 * @return
	 */
    CommonResponse batchDeleteLoginRecord(long[] ids);

	/**
	 * 分页查询登陆记录
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	CommonResponse queryLoginRecord(Integer currentPage, Integer pageSize);

	/**
	 * 根据id删除登录记录
	 *
	 * @param id
	 * @return
	 */
    CommonResponse deleteLoginRecord(long id);

	/**
	 * 分页查询通知
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
    CommonResponse queryNotice(Integer currentPage, Integer pageSize);

	/**
	 * 添加 或 修改 通知公告
	 *
	 * @param notice
	 * @return
	 */
	CommonResponse addOrEditNotice(Notice notice);

	/**
	 * 删除
	 *
	 * @param id
	 * @return
	 */
	CommonResponse deleteNotice(long id);

	/**
	 * 批量修改
	 *
	 * @param field
	 * @param value
	 * @param ids
	 * @return
	 */
    CommonResponse batchSetNotice(String field, Integer value, List<Long> ids);

	/**
	 * 批量删除通知公告
	 *
	 * @param ids
	 * @return
	 */
	CommonResponse batchDeleteNotice(long[] ids);

	/**
	 * 根据条件返回全部可用（isEnabled=1）通知
	 *
	 * @param isForSite
	 * @param isForChat
	 * @return
	 */
	CommonResponse findNoticeByCriteria(Integer isForSite, Integer isForChat);

	/**
	 * 分页 搜索通知
	 *
	 * @param isForSite
	 * @param isForChat
	 * @param isEnabled
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
    CommonResponse searchNotice(Integer isForSite, Integer isForChat, Integer isEnabled, Integer currentPage, Integer pageSize);

	/**
	 * 分页 查询 友情链接
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	CommonResponse queryFriendLink(Integer currentPage, Integer pageSize);

	/**
	 * 添加 或 修改 友情链接
	 *
	 * @param friendLink
	 * @return
	 */
	CommonResponse addOrEditFriendLink(FriendLink friendLink);

	/**
	 * 删除 友情链接
	 *
	 * @param id
	 * @return
	 */
    CommonResponse deleteFriendLink(long id);

	/**
	 * 批量 设置 有效/无效
	 *
	 * @param isValid
	 * @param ids
	 * @return
	 */
    CommonResponse batchSetFriendLink(Integer isValid, List<Long> ids);

	/**
	 * 批量 友情链接
	 *
	 * @param ids
	 * @return
	 */
	CommonResponse batchDeleteFriendLink(long[] ids);

	/**
	 * 搜索 符合条件 的 友情链接
	 *
	 * @param siteName
	 * @param siteAddress
	 * @param isValid
	 * @return
	 */
    CommonResponse searchFriendLink(String siteName, String siteAddress, Integer isValid, Integer currentPage, Integer pageSize);

	/**
	 * 添加 系统用户
	 *
	 * @param user
	 * @return
	 */
	CommonResponse addSystemUser(UserConnection user);

	/**
	 * 分页查询 系统用户
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
    CommonResponse querySystemUser(Integer currentPage, Integer pageSize);

	/**
	 * 修改系统用户
	 *
	 * @param user
	 * @return
	 */
	CommonResponse editSystemUser(UserConnection user);

	/**
	 * 删除 系统用户
	 *
	 * @param userId
	 * @return
	 */
	CommonResponse deleteSystemUser(String userId);

	/**
	 * 批量删除系统用户
	 *
	 * @param userIds
	 * @return
	 */
	CommonResponse batchDeleteSystemUser(String[] userIds);

	/**
	 * 批量设置 状态
	 *
	 * @param status
	 * @param userIds
	 * @return
	 */
	CommonResponse batchSetSystemUserStatus(Integer status, List<Long> userIds);

	/**
	 * 分页 搜索 系统账户
	 *
	 * @param userId
	 * @param status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	CommonResponse searchSystemUser(String userId, Integer status, Integer currentPage, Integer pageSize);

}
