package com.yufei.shop.handler;


import com.yufei.shop.entity.Result;
import com.yufei.shop.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // 自定义业务异常
    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(UserException e) {
        // 业务异常无需打印栈（预期内的异常）
        return Result.fail(e.getCode(), e.getMessage());
    }
}
