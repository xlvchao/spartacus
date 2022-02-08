package com.xlc.spartacus.resource.exception;

import com.xlc.spartacus.common.core.constant.RespConstant;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author xlc, since 2021
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public ErrorInfo jsonErrorHandler(HttpServletRequest req, GlobalException e) throws Exception {
        ErrorInfo error = new ErrorInfo();
        error.setCode(RespConstant.CODE_500);
        error.setMsg(RespConstant.MSG_500);
        error.setData(e.getMessage());
        error.setUrl(req.getRequestURL().toString());
        return error;
    }

}

