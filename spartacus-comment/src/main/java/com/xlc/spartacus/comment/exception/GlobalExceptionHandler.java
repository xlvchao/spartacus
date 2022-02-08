package com.xlc.spartacus.comment.exception;

import javax.servlet.http.HttpServletRequest;

import com.xlc.spartacus.common.core.constant.RespConstant;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理全局异常
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

