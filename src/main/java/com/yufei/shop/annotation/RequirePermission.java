package com.yufei.shop.annotation;


import com.yufei.shop.constant.PermissionEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 所需权限标识（对应RBAC中Permission表的permission_key）
     * 示例：sys:user:list（查询用户）、sys:user:add（新增用户）
     */
    String[] value();  // 标识未来可以在注解中传的参数类型

    /**
     * 权限校验逻辑：true=所有权限都需满足（且），false=满足任一权限即可（或）
     * 默认：满足任一即可
     */
    boolean requireAll() default false;  // 标识未来可以在注解中传的参数类型
}
