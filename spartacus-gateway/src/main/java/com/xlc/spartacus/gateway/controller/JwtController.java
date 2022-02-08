package com.xlc.spartacus.gateway.controller;

import com.xlc.spartacus.common.core.common.CommonResponse;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.xlc.spartacus.gateway.constant.GatewayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 接口
 *
 * @author xlc, since 2021
 */
@RestController
public class JwtController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JwtDecoder myJwtDecoder; // 验签、解码

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @RequestMapping(value = GatewayConstant.RSA_JWT_TOKEN_DECODE_URL, method = RequestMethod.GET)
    public CommonResponse decode(@PathVariable String token) {
        try {
            String keySuffix = DigestUtils.md5DigestAsHex(token.getBytes());
            if(redisTemplate.hasKey("CURRENT_ONLINE_USER:" + token)) {
                //redisTemplate.opsForValue().set("CURRENT_ONLINE_USER:" + token, CommonUtils.getDateString(), 5, TimeUnit.MINUTES);
                redisTemplate.expire("CURRENT_ONLINE_USER:" + token, 5, TimeUnit.MINUTES);
                return CommonResponse.success(myJwtDecoder.decode(token));
            } else {
                return CommonResponse.failed("Token无效！");
            }

        } catch (JwtValidationException e) {
            logger.error("Token过期！", e);
            return CommonResponse.failed("Token过期！");
        } catch (BadJwtException e) {
            logger.error("无效签名！", e);
            return CommonResponse.failed("无效签名！");
        } catch (Exception e) {
            logger.error("验签出错！", e);
            return CommonResponse.failed("验签出错！");
        }
    }

}
