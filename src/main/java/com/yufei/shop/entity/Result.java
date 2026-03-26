package com.yufei.shop.entity;


import lombok.Data;

@Data
public class Result<T> {

    private String msg;
    private T data;
    private String code;

    // 常用状态码/提示语常量（私有静态，仅内部使用）
    private static final String SUCCESS_CODE = "200";
    private static final String ERROR_CODE = "500";
    private static final String SUCCESS_MSG = "操作成功";
    private static final String ERROR_MSG = "操作失败";

    // -------------------------- 静态返回方法（核心） --------------------------
    // 1. 无数据的成功返回（默认提示）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        return result;
    }

    // 2. 带自定义提示的成功返回（无数据）
    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        return result;
    }

    // 3. 带数据的成功返回（默认提示）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        result.setData(data);
        return result;
    }

    // 4. 带数据+自定义提示的成功返回（最常用）
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // 5. 无数据的失败返回（默认提示）
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ERROR_CODE);
        result.setMsg(ERROR_MSG);
        return result;
    }

    // 6. 带自定义提示的失败返回（无数据）
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ERROR_CODE);
        result.setMsg(msg);
        return result;
    }

    // 7. 自定义状态码+提示的失败返回（灵活扩展）
    public static <T> Result<T> fail(String code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}

