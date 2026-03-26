package com.yufei.shop.config;

import com.yufei.shop.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类：注册JWT拦截器，指定拦截/放行规则
 * 核心：让JWTInterceptor生效，并控制哪些接口需要校验Token
 */
@Configuration // 必须加该注解，Spring才能识别为配置类
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器核心方法
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 注册JWT拦截器
        registry.addInterceptor(new JWTInterceptor())
                // 2. 设置需要拦截的接口路径（支持Ant风格通配符）
                .addPathPatterns("/api/**") // 拦截所有/api开头的接口（需登录）
                // 3. 设置不需要拦截的接口路径（放行登录、注册、静态资源等）
                .excludePathPatterns(
                        "/api/user/login",    // 登录接口（无需Token）
                        "/api/register", // 注册接口（无需Token）
                        "/api/verify/code", // 验证码接口（无需Token）
                        "/static/**",    // 静态资源（如前端页面、图片等）
                        "/swagger-ui/**",// Swagger文档（开发环境放行）
                        "/v3/api-docs/**",// Swagger接口文档（开发环境放行）
                        "/doc.html"
                );
    }
}
