package com.xlc.spartacus.auth.service;

import com.xlc.spartacus.auth.domain.User;
import com.xlc.spartacus.auth.domain.UserConnection;
import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户查询接口
 *
 * @author xlc, since 2021
 */
@Component("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService, SocialUserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserConnectionMapper userConnectionMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("表单登录用户名：" + username);
        Long start = System.currentTimeMillis();
        UserConnection user = userConnectionMapper.findUserConnectionByUsername(username);
        Long end = System.currentTimeMillis();
        logger.info(String.format("登录耗时：%s秒", (end-start)/1000));

        return user==null ? null : new User(username, user.getSecret(), user.getImageUrl(), user.getStatus() == 0, true, true,  true, AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        logger.info("社交登录用户ID：" + userId);
        return new SocialUser(userId, "", true, true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }

}
