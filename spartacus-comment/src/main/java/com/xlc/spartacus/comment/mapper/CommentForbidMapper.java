package com.xlc.spartacus.comment.mapper;

import com.xlc.spartacus.comment.pojo.CommentForbid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface CommentForbidMapper {

    Integer save(CommentForbid forbid);

    List<String> getForbiddenIpList();

    List<String> getForbiddenProviderUserIdList();

    List<CommentForbid> findAll();

    List<CommentForbid> findByForbidType(Integer forbidType);

    Integer deleteByIp(@Param("ip")String ip);

    Integer batchDeleteByIp(@Param("ips")List<String> ips);

    Integer deleteByProviderUserId(@Param("userId")String userId);

    Integer batchDeleteByProviderUserId(@Param("userIds")List<String> userIds);
}