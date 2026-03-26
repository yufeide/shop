package com.yufei.shop.interceptor;

import ch.qos.logback.classic.Logger;
import com.yufei.shop.util.JwtUtil;
import com.yufei.shop.util.ThreadLocalUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 通用JWT拦截器
 * 核心功能：解析请求头中的JWT Token，验证Token有效性，提取用户ID存入请求上下文
 */
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private ThreadLocalUtil threadLocalUtil;


    // 拦截器前置处理（接口执行前执行，核心校验逻辑）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头中获取JWT Token（约定请求头Key为Authorization，格式：Bearer Token值）
        String token = request.getHeader("Authorization");
        log.info("token={}",token);
        // 2. 校验Token是否存在
        if (token == null || token.isEmpty()) {
            // Token不存在或格式错误，返回401未授权
            returnJson(response, "{\"code\":401,\"msg\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }

//        // 3. 截取Token（去掉前缀Bearer ，注意空格）
//        token = token.substring(7);

        try {
            // 4. 验证Token有效性（签名、过期时间）
            // 复用工具类解析用户ID（简化拦截器代码）
            Long userId = JwtUtil.getUserIdFromToken(token);
            threadLocalUtil.set("userId",userId);
            request.setAttribute("currentUserId", userId);
            return true;

        } catch (Exception e) {
            // 8. Token校验失败（过期、签名错误、非法Token等），返回401
            String errorMsg = "Token无效或已过期，请重新登录";
            if (e instanceof ExpiredJwtException) {
                errorMsg = "Token已过期，请重新登录";
            } else if (e instanceof SignatureException) {
                errorMsg = "Token签名错误，非法请求";
            } else if (e instanceof MalformedJwtException) {
                errorMsg = "Token格式错误，非法请求";
            }
            returnJson(response, "{\"code\":401,\"msg\":\"" + errorMsg + "\",\"data\":null}");
            return false;
        }
    }

    // 拦截器后置处理（接口执行后执行，可做日志记录等，非必需）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可选：记录Token解析日志、用户访问日志等
        Long userId = (Long) request.getAttribute("currentUserId");
        if (userId != null) {
            System.out.println("用户ID：" + userId + "，访问接口：" + request.getRequestURI());
        }
    }

    // 整个请求完成后执行（非必需）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 可选：清理资源、记录异常日志等
    }

    // 工具方法：向响应中写入JSON格式的错误信息
    private void returnJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(json);
        writer.flush();
        writer.close();
    }
}

