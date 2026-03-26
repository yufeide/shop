package com.yufei.shop.service;

import com.yufei.shop.constant.RoleEnum;
import com.yufei.shop.mapper.UserRoleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RbacPermissionService {


    private final UserRoleMapper userRoleMapper;

    /**
     * 优化后：根据用户ID，获取其拥有的所有权限key（通过双枚举联动，无需查询权限表）
     */
    public Set<String> getUserPermissions(Long userId) {
        Set<String> permissionSet = new HashSet<>();
        // 1. 查询用户关联的角色code
        List<Integer> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);
        if (roleCodes.isEmpty()) {
            return permissionSet;
        }
        // 2. 通过角色枚举获取权限key，合并集合
        for (Integer roleCode : roleCodes) {
            RoleEnum roleEnum = RoleEnum.getByCode(roleCode);
            if (roleEnum != null) {
                // 直接获取角色的权限key集合（通过角色枚举的工具方法）
                permissionSet.addAll(roleEnum.getPermissionKeys());
            }
        }
        return permissionSet;
    }
}

