package com.xlc.spartacus.gateway.constant;

/**
 * 常量
 *
 * @author xlc, since 2021
 */
public final class GatewayConstant {

    public static final String AUTHORITY_PREFIX = "ROLE_";

    public static final String AUTHORITY_CLAIM_NAME = "authorities";

    public static final String REDIS_RESOURCE_ROLES_MAP = "AUTH:RESOURCE_ROLES_MAP";

    // apollo命名空间
    public static final String NAMESPACE_APPLICATION = "application";
    public static final String NAMESPACE_RESOURCE_ROLES = "resourceRoles";

    public static final String REDIS_RESOURCE_ROLES = "gateway:resource:roles";

    public static final String ROLE_SUFFIX_DEFAULT = "DEFAULT"; //默认角色
    public static final String ROLE_SUFFIX_USER = "USER";
    public static final String ROLE_SUFFIX_ADMIN = "ADMIN";

    //具有验签功能的解码器
    public static final String RSA_JWT_TOKEN_DECODE_URL = "/jwt/decode/{token}";

    //心跳检测
    public static final String RSA_JWT_TOKEN_HEARTBEAT_URL = "/jwt/heartBeat/{token}";

    /**
     * url白名单key
     */
    public static final String SECURITY_WHITE_URL_KEY = "spartacus.gateway.security.white.urls";

}
