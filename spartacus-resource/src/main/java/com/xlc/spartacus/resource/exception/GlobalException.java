package com.xlc.spartacus.resource.exception;

/**
 * 全局异常
 *
 * @author xlc, since 2021
 */
public class GlobalException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public GlobalException(String message) {
        super(message);
    }

}
