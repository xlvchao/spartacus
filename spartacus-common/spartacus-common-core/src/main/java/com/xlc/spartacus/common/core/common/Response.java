package com.xlc.spartacus.common.core.common;

/**
 * 常用返回枚举
 *
 * @author xlc, since 2021
 */
public enum Response {
    SUCCESS(200, "操作成功！"),
    FAILED(500, "操作失败！"),
    VALIDATE_FAILED(404, "参数检验失败！"),
    UNAUTHORIZED(401, "未登录或token已过期！"),
    FORBIDDEN(403, "没有权限！");

    private Integer code;
    private String message;

    Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
