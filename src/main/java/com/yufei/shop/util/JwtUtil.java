package com.yufei.shop.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT通用工具类
 * 核心功能：生成Token、解析Token中的用户ID、验证Token有效性
 * 与JWTInterceptor拦截器配套使用，密钥/过期时间等配置保持一致
 */
public class JwtUtil {

    // JWT密钥（需与拦截器中的密钥完全一致，建议抽取到配置类中统一管理）
     private static final String JWT_SECRET = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2";
     private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    // Token过期时间：单位毫秒（示例：2小时，可根据业务调整）
    private static final long JWT_EXPIRE_TIME = 2 * 60 * 60 * 1000L;

    /**
     * 生成JWT Token
     * @param userId 要存入Token的用户ID（Long类型，与拦截器解析类型一致）
     * @return 生成的Token字符串（前端存储，后续请求携带）
     */
    public static String generateToken(Long userId) {
        // 1. 设置Token的过期时间
        Date expireDate = new Date(System.currentTimeMillis() + JWT_EXPIRE_TIME);

        // 2. 构建Token的负载（Claims）：存储自定义信息（如用户ID）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // 与拦截器中解析的key保持一致

        // 3. 生成Token
        return Jwts.builder()
                .setClaims(claims) // 设置负载
                .setExpiration(expireDate) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 设置签名算法+密钥
                .compact(); // 生成最终Token字符串
    }

    /**
     * 解析Token，提取用户ID（仅解析，不验证有效性，拦截器中已做完整验证）
     * @param token 待解析的Token（已去掉Bearer前缀）
     * @return 用户ID（Long类型）
     * @throws Exception 解析失败时抛出异常
     */
    public static Long getUserIdFromToken(String token) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    /**
     * 验证Token是否有效（单独验证，拦截器中已整合此逻辑）
     * @param token 待验证的Token（已去掉Bearer前缀）
     * @return true=有效，false=无效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 任意异常都代表Token无效（过期、签名错误、格式错误等）
            return false;
        }
    }
}
