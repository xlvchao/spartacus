package com.xlc.spartacus.auth.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlc.spartacus.auth.domain.User;
import com.xlc.spartacus.auth.domain.UserSequence;
import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT内容增强器
 *
 * @author xlc, since 2021
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private UserConnectionMapper userConnectionMapper;

    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Object principal = authentication.getPrincipal();

        //本地、短信
        if(principal instanceof User) {
            Map<String, Object> info = new HashMap<>();
            User user = (User) authentication.getPrincipal();
            info.put("nickname", user.getUsername());
            info.put("headimage", user.getHeadImage());
            info.put("providerId", "spartacus");
            info.put("providerUserId", user.getUsername());
//            info.put("authorities", user.getAuthorities());

            info.put("userSequence", getSequence(user.getUsername()));

            List<Object> authorities = new ArrayList<>();
            user.getAuthorities().forEach(item -> authorities.add(item.getAuthority()));
            info.put("authorities", authorities);

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        }

        //QQ、微信
        if(principal instanceof SocialUser) {
            Map<String, Object> info = new HashMap<>();
            JSONObject authJson = JSON.parseObject(objectMapper.writeValueAsString(authentication));
            JSONObject connection = authJson.getJSONObject("userAuthentication").getJSONObject("connection");

            info.put("nickname", connection.get("displayName"));
            info.put("headimage", connection.get("imageUrl"));
            info.put("providerId", connection.getJSONObject("key").get("providerId"));
            info.put("providerUserId", connection.getJSONObject("key").get("providerUserId"));
//            info.put("authorities", authJson.getJSONObject("authorities"));

            info.put("userSequence", getSequence((String) connection.getJSONObject("key").get("providerUserId")));

            List<Object> authorities = new ArrayList<>();
            authJson.getJSONArray("authorities").forEach(item -> authorities.add(((JSONObject)item).get("authority")));
            info.put("authorities", authorities);

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        }

        return accessToken;
    }

    private Integer getSequence(String userId) {
        Integer sequence = -1; //默认值-1
        UserSequence userSequence = userConnectionMapper.findUserSequence(userId);
        if(userSequence == null) {
            RLock rLock = redissonClient.getLock("getSequence-lock");
            try {
                // 5s拿不到锁就认为获取锁失败，10s即锁失效时间
                boolean tryLock = rLock.tryLock(5, 10, TimeUnit.SECONDS);
                if(tryLock) {
                    sequence = userConnectionMapper.getNewUserSequence();
                    userConnectionMapper.insertUserSequence(new UserSequence(userId, sequence));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                rLock.unlock(); //无论如何都要记得释放锁
            }
        } else {
            sequence = userSequence.getUserSequence();
        }
        return sequence;
    }

}
