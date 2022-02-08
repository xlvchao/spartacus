package com.xlc.spartacus.chat.mapper;

import com.xlc.spartacus.chat.model.UserContact;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * mapper层
 *
 * @author xlc, since 2021
 */
@Mapper
public interface UserContactMapper {

    Integer saveContact(UserContact userContact);

    List<Map> queryContacts(String providerUserId);
}