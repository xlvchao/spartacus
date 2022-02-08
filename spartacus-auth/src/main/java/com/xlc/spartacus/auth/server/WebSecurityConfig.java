package com.xlc.spartacus.auth.server;

import com.xlc.spartacus.auth.core.authorize.AuthorizeConfigManager;
import com.xlc.spartacus.auth.core.code.ValidateCodeSecurityConfig;
import com.xlc.spartacus.auth.core.code.image.authentication.ImageValidateCodeSecurityConfig;
import com.xlc.spartacus.auth.core.code.sms.authentication.SmsValidateCodeSecurityConfig;
import com.xlc.spartacus.auth.core.social.authentication.OpenIdAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * Spring Security配置
 *
 * @author xlc, since 2021
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启Spring方法级安全
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;

    @Autowired
    private ImageValidateCodeSecurityConfig imageValidateCodeSecurityConfig;

    @Autowired
    private SmsValidateCodeSecurityConfig smsValidateCodeSecurityConfig;

    @Autowired
    private SpringSocialConfigurer mySocialSecurityConfig;

    @Autowired
    private OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;

    /**
     * springboot2.x之后需要手动创建，AuthorizationServer里需要
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .successHandler(myAuthenticationSuccessHandler) // 登陆成功处理器
                .failureHandler(myAuthenticationFailureHandler); // 登陆失败处理器

        http.apply(validateCodeSecurityConfig)//验证码校验配置
                .and()
                .apply(imageValidateCodeSecurityConfig) //图片验证码
                .and()
                .apply(smsValidateCodeSecurityConfig) //短信验证码
                .and()
                .apply(mySocialSecurityConfig) //导入配置
                .and()
                .apply(openIdAuthenticationSecurityConfig); //导入配置

        http.csrf().disable(); //禁用csrf校验

        //授权配置（资源配置）
        authorizeConfigManager.config(http.authorizeRequests());
    }

}
