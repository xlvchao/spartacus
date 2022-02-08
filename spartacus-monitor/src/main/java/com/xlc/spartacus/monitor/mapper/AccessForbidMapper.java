package com.xlc.spartacus.monitor.mapper;

import com.xlc.spartacus.monitor.pojo.AccessForbid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface AccessForbidMapper {

    Integer countByIp(String ip);

    Integer deleteByIp(String ip);

    void batchDelete(@Param("ips")List<String> ips);

    Integer save(AccessForbid forbid);

    List<AccessForbid> findAll();

    List<String> findAllIp();
}