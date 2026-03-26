package com.yufei.shop.Aspect;

import com.yufei.shop.annotation.RequirePermission;
import com.yufei.shop.service.RbacPermissionService;
import com.yufei.shop.util.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 权限校验切面：拦截@RequirePermission注解，结合RBAC模型校验用户权限
 */
@Aspect
@Component // 交给Spring管理，确保切面生效
public class PermissionAspect {

    @Resource
    private RbacPermissionService rbacPermissionService;
    @Resource
    private ThreadLocalUtil threadLocalUtil;


    /**
     * 切入点：拦截所有带有@RequirePermission注解的方法
     */
    @Pointcut("@annotation(com.yufei.shop.annotation.RequirePermission)")
    public void permissionPointcut() {}

    /**
     * 前置通知：在接口方法执行前执行权限校验
     */
    @Before("permissionPointcut()")
    public void doPermissionCheck(JoinPoint joinPoint) {
        // 1. 获取当前登录用户ID（根据项目实际认证方式调整，如Token解析、Session获取）
        Long userId = (Long) threadLocalUtil.get("userId");
        if (userId == null) {
            throw new RuntimeException("用户未登录，请先登录");
        }

        // 2. 获取接口方法上的@RequirePermission注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            return; // 理论上不会触发，切入点已拦截带注解的方法
        }

        // 3. 获取注解要求的权限标识数组
        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions.length == 0) {
            throw new IllegalArgumentException("接口权限注解未配置权限标识");
        }

        // 4. 获取当前用户拥有的所有权限标识（从RBAC模型中查询）
        Set<String> userPermissions = rbacPermissionService.getUserPermissions(userId);

        // 5. 执行权限校验（根据注解的requireAll参数，判断是“且”还是“或”逻辑）
        boolean hasPermission = false;
        if (requirePermission.requireAll()) {
            // 需满足所有权限（且逻辑）：注解中的所有权限，用户都必须拥有
            hasPermission = userPermissions.containsAll(List.of(requiredPermissions));
        } else {
            // 满足任一权限即可（或逻辑）：注解中的权限，用户拥有一个就可以
            for (String requiredPermission : requiredPermissions) {
                if (userPermissions.contains(requiredPermission)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        // 6. 校验不通过，抛出权限不足异常（全局异常处理器捕获，返回统一响应）
        if (!hasPermission) {
            throw new RuntimeException("权限不足，无法访问该接口");
        }
    }
}
