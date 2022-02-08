package com.xlc.spartacus.auth.controller;

import com.xlc.spartacus.auth.common.CommonResponse;
import com.xlc.spartacus.auth.common.RSAUtils;
import com.xlc.spartacus.auth.core.properties.SecurityConstants;
import com.xlc.spartacus.auth.domain.UserConnection;
import com.xlc.spartacus.auth.mapper.UserConnectionMapper;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口层
 *
 * @author xlc, since 2021
 */
@RestController
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserConnectionMapper userConnectionMapper;


    @RequestMapping(value = SecurityConstants.DEFAULT_GET_AUTH_RSA_PUBLIC_KEY_URL, method = RequestMethod.GET)
    public Map<String, Object> getRsaPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }

    @RequestMapping(value = SecurityConstants.DEFAULT_GET_PASS_RSA_PUBLIC_KEY_URL, method = RequestMethod.POST)
    public CommonResponse getRsaPublicKey(String username) {
        try {
            //生成公钥和私钥
            Map<String,Object> keyMap = RSAUtils.createKey();
            RSAPublicKey publicKey = (RSAPublicKey) keyMap.get("publicKey");
            RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("privateKey");

            //页面通过模和公钥指数获取公钥对字符串进行加密，注意必须转为16进制
            String modulus = publicKey.getModulus().toString(16); //模
            String public_exponent = publicKey.getPublicExponent().toString(16); //公钥指数
            String private_exponent = privateKey.getPrivateExponent().toString(); //私钥指数

            //后台的模和私钥指数不需要转16进制
            redisTemplate.opsForValue().set(username + "_modulus", publicKey.getModulus().toString());
            redisTemplate.opsForValue().set(username + "_private_exponent", private_exponent);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("modulus", modulus);
            map.put("public_exponent", public_exponent);
            return CommonResponse.success(map);

        } catch (Exception e) {
            logger.error("获取公钥出错！", e);
            return CommonResponse.failed("获取公钥出错！");
        }
    }

    @RequestMapping(value = SecurityConstants.AUTH_LOG_OUT_URL, method = RequestMethod.GET)
    public void logout(@PathVariable String token) {
        try {
            String keySuffix = DigestUtils.md5DigestAsHex(token.getBytes());
            redisTemplate.delete("CURRENT_ONLINE_USER:" + token);
            logger.info("删除缓存登录信息成功！");
        } catch (Exception e) {
            logger.error("删除缓存登录信息异常！", e);
        }
    }

}
