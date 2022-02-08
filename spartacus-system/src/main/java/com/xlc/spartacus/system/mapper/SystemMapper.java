package com.xlc.spartacus.system.mapper;

import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.system.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface SystemMapper {

    void batchDeleteLoginRecord(long[] ids);

    List<LoginRecord> queryLoginRecord();

    void deleteLoginRecord(long id);

    void updateNotice(Notice notice);

    void addNotice(Notice notice);

    List<Notice> queryNotice();

    void deleteNotice(long id);

    void batchSetNotice(@Param("field") String field, @Param("value") Integer value, @Param("ids") List<Long> ids);

    void batchDeleteNotice(long[] ids);

    List<Notice> findNoticeByCriteria(@Param("isForSite")Integer isForSite, @Param("isForChat")Integer isForChat);

    List<Notice> searchNotice(@Param("isForSite")Integer isForSite, @Param("isForChat")Integer isForChat, @Param("isEnabled")Integer isEnabled);

    List<FriendLink> queryFriendLink();

    void updateFriendLink(FriendLink friendLink);

    void addFriendLink(FriendLink friendLink);

    void deleteFriendLink(long id);

    void batchSetFriendLink(@Param("isValid") Integer isValid, @Param("ids") List<Long> ids);

    void batchDeleteFriendLink(long[] ids);

    List<FriendLink> searchFriendLink(@Param("siteName")String siteName, @Param("siteAddress")String siteAddress, @Param("isValid")Integer isValid);

    void addSystemUser(UserConnection user);

    void updateSystemUser(UserConnection user);

    UserConnection findSystemUserByUsername(String username);

    List<UserConnection> querySystemUser();

    void deleteSystemUser(String userId);

    void batchDeleteSystemUser(String[] userIds);

    void batchSetSystemUserStatus(@Param("status")Integer status, @Param("userIds")List<Long> userIds);

    List<UserConnection> searchSystemUser(@Param("userId")String userId, @Param("status")Integer status);

    void updateBlogProfile(Profile profile);

    Profile loadBlogProfile();
}