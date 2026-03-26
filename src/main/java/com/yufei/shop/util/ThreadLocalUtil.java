package com.yufei.shop.util;


import org.springframework.stereotype.Component;

/**
 * 通用ThreadLocal工具类
 * 核心功能：安全存储/获取/清除线程上下文数据，避免内存泄漏、数据污染
 * 适配场景：存储JWT解析的用户ID、请求ID、租户ID等线程级上下文数据
 */
@Component
public class ThreadLocalUtil {

    // 核心ThreadLocal容器：存储线程上下文数据（键值对形式，支持多类型数据）
    private static final ThreadLocal<java.util.Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 私有构造方法：禁止实例化工具类
     */
    private ThreadLocalUtil() {
    }

    // ------------------- 基础操作：存储/获取/删除单个数据 -------------------
    /**
     * 存储线程上下文数据
     * @param key   数据key（如"currentUserId"）
     * @param value 数据值（如用户ID、请求ID等）
     */
    public static void set(String key, Object value) {
        // 初始化Map：若当前线程无Map，先创建空Map
        java.util.Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new java.util.HashMap<>();
            THREAD_LOCAL.set(map);
        }
        // 存入数据
        map.put(key, value);
    }

    /**
     * 获取线程上下文数据（通用类型）
     * @param key 数据key
     * @return 数据值（需手动强转，或使用重载方法）
     */
    public static Object get(String key) {
        java.util.Map<String, Object> map = THREAD_LOCAL.get();
        return map == null ? null : map.get(key);
    }

    /**
     * 重载：获取指定类型的线程上下文数据（避免手动强转，更安全）
     * @param key   数据key
     * @param clazz 目标类型（如Long.class、String.class）
     * @return 强转后的目标类型数据
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        // 类型校验：避免强转异常
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            throw new IllegalArgumentException("数据类型不匹配：key=" + key + "，期望类型=" + clazz.getName() + "，实际类型=" + value.getClass().getName());
        }
    }

    /**
     * 删除指定key的线程上下文数据
     * @param key 数据key
     */
    public static void remove(String key) {
        java.util.Map<String, Object> map = THREAD_LOCAL.get();
        if (map != null) {
            map.remove(key);
            // 若Map为空，直接清除ThreadLocal（减少内存占用）
            if (map.isEmpty()) {
                THREAD_LOCAL.remove();
            }
        }
    }

    // ------------------- 快捷操作：适配你的业务场景（存储用户ID） -------------------
    /**
     * 快捷存储用户ID（适配JWT拦截器解析的用户ID）
     * @param userId 用户ID（Long类型）
     */
    public static void setCurrentUserId(Long userId) {
        set("currentUserId", userId);
    }

    /**
     * 快捷获取用户ID（无需手动强转，直接返回Long类型）
     * @return 当前线程的用户ID
     */
    public static Long getCurrentUserId() {
        return get("currentUserId", Long.class);
    }

    /**
     * 快捷删除用户ID
     */
    public static void removeCurrentUserId() {
        remove("currentUserId");
    }

    // ------------------- 核心操作：清除所有线程上下文数据（必须！） -------------------
    /**
     * 清除当前线程的所有上下文数据（关键：避免内存泄漏、线程复用数据污染）
     * 建议：在拦截器afterCompletion、AOP最终通知中调用
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
