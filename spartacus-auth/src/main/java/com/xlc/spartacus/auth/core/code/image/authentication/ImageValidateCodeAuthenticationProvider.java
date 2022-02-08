package com.xlc.spartacus.auth.core.code.image.authentication;

import com.xlc.spartacus.auth.common.RSAUtils;
import com.xlc.spartacus.auth.constant.AuthConstant;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.util.Collection;

/**
 * 用户名/密码/验证码时，认证器
 *
 * @author xlc, since 2021
 */
@Data
public class ImageValidateCodeAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();// 这个获取表单输入中的用户名
        String password = (String) authentication.getCredentials();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException(AuthConstant.USERNAME_PASSWORD_ERROR);
        }

        System.out.println("解密前：" + password);
        // 根据模和私钥指数获取私钥，解密获得明文密码
        String modulus = (String) redisTemplate.opsForValue().get(username + "_modulus");
        String private_exponent = (String) redisTemplate.opsForValue().get(username + "_private_exponent");
        RSAPrivateKey privateKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        try {
            password = RSAUtils.decrypttoStr(privateKey, password);
            redisTemplate.delete(username + "_modulus");
            redisTemplate.delete(username + "_private_exponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("解密后：" + password);

        // 取出数据库中的密码，二者进行比较
        String encodePassword = userDetails.getPassword();
        if (!passwordEncoder.matches(password, encodePassword)) {
            throw new UsernameNotFoundException(AuthConstant.USERNAME_PASSWORD_ERROR);
        }

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        return new UsernamePasswordAuthenticationToken(userDetails, encodePassword, authorities);

    }

    /**
     * Token是ImageCodeAuthenticationToken，AuthenticationManager才会调用ImageCodeAuthenticationProvider
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return ImageValidateCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}