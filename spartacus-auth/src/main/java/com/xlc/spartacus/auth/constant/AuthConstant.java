package com.xlc.spartacus.auth.constant;

/**
 * 常量
 *
 * @author xlc, since 2021
 */
public class AuthConstant {

    /**
     * 登陆
     */
    public static final String LOGIN_SUCCESS = "登录成功!";
    public static final String USERNAME_PASSWORD_ERROR = "用户名或密码错误!";
    public static final String CREDENTIALS_EXPIRED = "该账户的登录凭证已过期，请重新登录!";
    public static final String ACCOUNT_DISABLED = "该账户已被禁用，请联系管理员!";
    public static final String ACCOUNT_LOCKED = "该账号已被锁定，请联系管理员!";
    public static final String ACCOUNT_EXPIRED = "该账号已过期，请联系管理员!";
    public static final String PERMISSION_DENIED = "没有访问权限，请联系管理员!";

    /**
     * redis
     */
    public static final String REDIS_SMSCODE_SEND_MOBILE_MAP = "AUTH:SMSCODE_SEND_MOBILE_MAP";

    /**
     * apollo命名空间
     */
    public static final String NAMESPACE_APPLICATION = "application";
    public static final String NAMESPACE_RESOURCE_ROLES = "resourceRoles";

}
