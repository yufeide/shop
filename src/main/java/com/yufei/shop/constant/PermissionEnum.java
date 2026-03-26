package com.yufei.shop.constant;

/**
 * 权限枚举类
 * 对应 RBAC 权限表的 permission_key，统一定义所有系统权限
 * key：权限唯一标识（与角色枚举中关联的权限一致，也对应权限表字段）
 * name：权限名称（用于展示、日志、权限管理页面）
 */
public enum PermissionEnum {

    // ------------------- 用户相关权限 -------------------
    USER_INFO_QUERY("user:info:query", "查看个人信息权限"),
    USER_INFO_UPDATE("user:info:update", "修改个人信息权限"),
    USER_LIST_QUERY("user:list:query", "查看所有用户列表权限"),
    USER_DELETE("user:delete", "删除用户权限"), // 新增超级管理员权限

    // ------------------- 订单相关权限 -------------------
    ORDER_LIST_QUERY("order:list:query", "查看订单列表权限"),
    ORDER_INFO_UPDATE("order:info:update", "修改订单状态权限"),
    ORDER_DELETE("order:delete", "删除订单权限"),

    // ------------------- 商品相关权限 -------------------
    GOODS_LIST_QUERY("goods:list:query", "查看商品列表权限"),
    GOODS_INFO_ADD("goods:info:add", "新增商品权限"),
    GOODS_INFO_UPDATE("goods:info:update", "修改商品权限"),
    GOODS_INFO_DELETE("goods:info:delete", "删除商品权限");

    // 权限唯一标识（对应 RBAC 权限表的 permission_key，也是角色枚举中关联的权限值）
    private final String key;
    // 权限名称（用于前端展示、系统日志、权限管理）
    private final String name;

    // 构造方法（枚举类构造方法必须私有）
    PermissionEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }

    // ------------ 常用工具方法 ------------
    /**
     * 根据权限唯一标识（key），获取对应的权限枚举对象
     * 用于权限校验、权限管理时，快速匹配权限
     */
    public static PermissionEnum getByKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        for (PermissionEnum permission : PermissionEnum.values()) {
            if (permission.key.equals(key)) {
                return permission;
            }
        }
        return null;
    }

    // getter方法（枚举字段私有，通过getter获取）
    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}

