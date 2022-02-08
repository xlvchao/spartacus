package com.xlc.spartacus.auth.mapper;

import com.xlc.spartacus.auth.domain.UserConnection;
import com.xlc.spartacus.auth.domain.UserSequence;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * mapper层
 *
 * @author xlc, since 2021
 */
@Mapper
@Component(value = "userConnectionMapper")
public interface UserConnectionMapper {

    void insertUserSequence(UserSequence userSequence);

    Integer getNewUserSequence();

    UserSequence findUserSequence(String userId);

    void recordLoginInfo(Map<String, Object> userInfo);

    void insertUserConnection(UserConnection user);

    UserConnection findUserConnectionByUsername(String username);
}