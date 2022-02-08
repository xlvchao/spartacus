package com.xlc.spartacus.auth.component;

import com.xlc.spartacus.auth.config.CommonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.annotation.Resource;
import java.security.KeyPair;

/**
 * tokenStore的特点：
 * tokenStore通常情况为自定义实现，一般放置在缓存或者数据库中。此处可以利用自定义tokenStore来实现多种需求，如：
 *
 * 同已用户每次获取token，获取到的都是同一个token，只有token失效后才会获取新token。
 * 同一用户每次获取token都生成一个完成周期的token并且保证每次生成的token都能够使用（多点登录）。
 * 同一用户每次获取token都保证只有最后一个token能够使用，之前的token都设为无效（单点token）。
 *
 * @author xlc, since 2021
 */
@Configuration
public class TokenStoreConfig {

    @Resource
    private CommonProperties commonProperties;

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair()); //使用RSA秘钥对生成的token进行签名
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), commonProperties.getKeyFactoryPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", commonProperties.getKeyPassword().toCharArray());
    }

}
