package com.vsst.common.handler;

import com.vsst.common.response.Result;
import com.vsst.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @aescription: 全局异常处理
* @author: Ziqiang Lee
* @date: 2020/12/28
*/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 全局异常处理，不管什么异常都可以处理
     * @param e 异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        log.error(e.getMessage());
        return Result.error();
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public Result error(ArithmeticException e){
        log.error(e.getMessage());
        return Result.error().code(ResultCode.ARITHMETIC_EXCEPTION.getCode())
                .message(ResultCode.ARITHMETIC_EXCEPTION.getMessage());
    }
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result error(BusinessException e){
        log.error(e.getMessage());
        return Result.error().code(e.getCode())
                .message(e.getMessage());
    }
}
