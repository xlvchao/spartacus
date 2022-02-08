package com.xlc.spartacus.common.core.common;

/**
 * 通用返回
 *
 * @author xlc, since 2021
 */
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private T data;

    protected CommonResponse() {
    }

    protected CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    protected CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<T>(Response.SUCCESS.getCode(), Response.SUCCESS.getMessage(), null);
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<T>(Response.SUCCESS.getCode(), Response.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<T>(Response.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResponse<T> failed() {
        return new CommonResponse<T>(Response.FAILED.getCode(), Response.FAILED.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResponse<T> failed(String message) {
        return new CommonResponse<T>(Response.FAILED.getCode(), message, null);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResponse<T> validateFailed() {
        return new CommonResponse<T>(Response.VALIDATE_FAILED.getCode(), Response.VALIDATE_FAILED.getMessage(), null);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResponse<T> validateFailed(String message) {
        return new CommonResponse<T>(Response.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResponse<T> unauthorized(T data) {
        return new CommonResponse<T>(Response.UNAUTHORIZED.getCode(), Response.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResponse<T> forbidden(T data) {
        return new CommonResponse<T>(Response.FORBIDDEN.getCode(), Response.FORBIDDEN.getMessage(), data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
