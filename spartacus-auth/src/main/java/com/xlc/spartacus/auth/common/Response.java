package com.xlc.spartacus.auth.common;

/**
 * 常用返回码
 *
 * @author xlc, since 2021
 */
public enum Response {
    SUCCESS(200, "操作成功！"),
    FAILED(500, "操作失败！"),
    VALIDATE_FAILED(404, "参数检验失败！"),
    UNAUTHORIZED(401, "凭证失效，请重新登陆！"),
    FORBIDDEN(403, "没有权限！");

    private Integer code;
    private String message;

    Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
