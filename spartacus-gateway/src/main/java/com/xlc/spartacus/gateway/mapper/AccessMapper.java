package com.xlc.spartacus.gateway.mapper;

import com.xlc.spartacus.gateway.pojo.Access;
import org.apache.ibatis.annotations.Mapper;


/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface AccessMapper {

    Integer saveAccessInfo(Access access);

}