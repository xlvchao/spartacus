package com.xlc.spartacus.common.core.exception;

/**
 * 基础异常
 *
 * @author xlc, since 2021
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}