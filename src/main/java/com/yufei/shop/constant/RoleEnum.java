package com.yufei.shop.constant;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限角色关联枚举类
 * 对应 user 表的 role 字段（tinyint 类型），关联角色与权限枚举，适配 RBAC 模型
 * code：对应数据库 user.role 字段存储值（tinyint）
 * name：角色名称（用于展示、日志输出）
 * permissions：角色拥有的权限枚举集合（关联 PermissionEnum，替代原字符串集合）
 */
public enum RoleEnum {

    // 普通用户：code=0，对应数据库role字段值0，拥有基础查询权限
    NORMAL_USER(0, "普通用户", new HashSet<PermissionEnum>() {{
        add(PermissionEnum.USER_INFO_QUERY); // 关联权限枚举
        add(PermissionEnum.ORDER_LIST_QUERY);
        add(PermissionEnum.GOODS_LIST_QUERY);
    }}),

    // 管理员：code=1，对应数据库role字段值1，拥有核心管理权限
    ADMIN(1, "管理员", new HashSet<PermissionEnum>() {{
        add(PermissionEnum.USER_INFO_QUERY);
        add(PermissionEnum.USER_INFO_UPDATE);
        add(PermissionEnum.USER_LIST_QUERY);
        add(PermissionEnum.ORDER_LIST_QUERY);
        add(PermissionEnum.ORDER_INFO_UPDATE);
        add(PermissionEnum.GOODS_LIST_QUERY);
        add(PermissionEnum.GOODS_INFO_ADD);
        add(PermissionEnum.GOODS_INFO_UPDATE);
    }}),

    // 访客：code=2，对应数据库role字段值2，仅拥有浏览权限
    VISITOR(2, "访客", new HashSet<PermissionEnum>() {{
        add(PermissionEnum.GOODS_LIST_QUERY);
    }}),

    // 新增：超级管理员（可选，按需添加），拥有所有权限
    SUPER_ADMIN(3, "超级管理员", new HashSet<PermissionEnum>(Set.of(PermissionEnum.values())));

    // 角色对应的数据库存储值（与user表role字段一一对应，tinyint类型）
    private final Integer code;
    // 角色名称（用于前端展示、系统日志）
    private final String name;
    // 角色拥有的权限枚举集合（关联权限枚举，替代原字符串集合）
    private final Set<PermissionEnum> permissions;

    // 构造方法（枚举类构造方法必须私有）
    RoleEnum(Integer code, String name, Set<PermissionEnum> permissions) {
        this.code = code;
        this.name = name;
        this.permissions = permissions;
    }

    // ------------ 常用工具方法（优化，适配权限枚举）------------
    /**
     * 根据数据库role字段值（code），获取对应的枚举对象
     */
    public static RoleEnum getByCode(Integer code) {
        if (code == null) {
            return null; // 可根据业务返回默认角色（如VISITOR）
        }
        for (RoleEnum role : RoleEnum.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 验证某个角色是否拥有指定权限（通过权限枚举校验，更规范）
     */
    public boolean hasPermission(PermissionEnum permission) {
        if (permission == null) {
            return false;
        }
        return this.permissions.contains(permission);
    }

    /**
     * 验证某个角色是否拥有指定权限（兼容原字符串key，避免修改大量代码）
     */
    public boolean hasPermission(String permissionKey) {
        if (permissionKey == null || permissionKey.isEmpty()) {
            return false;
        }
        // 将权限枚举集合转为key集合，匹配传入的permissionKey
        Set<String> permissionKeys = this.permissions.stream()
                .map(PermissionEnum::getKey)
                .collect(Collectors.toSet());
        return permissionKeys.contains(permissionKey);
    }

    /**
     * 获取角色拥有的所有权限key（用于权限校验、返回前端）
     */
    public Set<String> getPermissionKeys() {
        return this.permissions.stream()
                .map(PermissionEnum::getKey)
                .collect(Collectors.toSet());
    }

    // getter方法
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Set<PermissionEnum> getPermissions() {
        return permissions;
    }
}
