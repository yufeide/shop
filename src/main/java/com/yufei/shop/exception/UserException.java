package com.yufei.shop.exception;


import lombok.Data;

@Data
public class UserException extends RuntimeException{

    // 自定义错误码
    private String code;

    // 构造方法
    public UserException(String code, String message) {
        super(message);
        this.code = code;
    }

    // 快捷构造（默认400码）
    public UserException(String message) {
        this("400", message);
    }
}
