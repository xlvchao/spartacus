package com.xlc.spartacus.article.exception;

/**
 * 自定义异常
 *
 * @author xlc, since 2021
 */
public class GlobalException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public GlobalException(String message) {
        super(message);
    }

}
