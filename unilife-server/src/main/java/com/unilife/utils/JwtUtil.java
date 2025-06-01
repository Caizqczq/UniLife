package com.unilife.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    public long expiration;

    public String generateToken(Long id) {
        DateTime now = DateTime.now();
        DateTime expireTime = new DateTime(now.getTime() + expiration * 1000);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", id);
        payload.put("created", now.getTime());
        payload.put("exp", expireTime.getTime() / 1000); // JWT标准过期时间字段（秒）
        return JWTUtil.createToken(payload, secret.getBytes());
    }

    public boolean verifyToken(String token) {
        try {
            // 验证token签名
            JWTUtil.verify(token, secret.getBytes());
            
            // 验证过期时间
            JWT jwt = JWTUtil.parseToken(token);
            Object expObj = jwt.getPayload("exp");
            if (expObj != null) {
                long exp = Long.parseLong(expObj.toString());
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime > exp) {
                    log.debug("Token已过期: exp={}, current={}", exp, currentTime);
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    public Long getUserIdFromToken(String token) {
        try {
            // 先验证token是否有效
            if (!verifyToken(token)) {
                return null;
            }
            return Long.valueOf(JWTUtil.parseToken(token).getPayload("userId").toString());
        } catch (Exception e) {
            log.debug("从Token获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
}
