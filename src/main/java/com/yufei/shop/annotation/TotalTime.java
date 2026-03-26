package com.yufei.shop.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TotalTime {

    /**
     * 可选：接口描述（方便日志识别）
     * @return
     */
    String value() default "";
}
