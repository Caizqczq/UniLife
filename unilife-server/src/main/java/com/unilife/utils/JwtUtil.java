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
        payload.put("iat", now.getTime() / 1000); // 签发时间（秒）
        payload.put("exp", expireTime.getTime() / 1000); // JWT标准过期时间字段（秒）
        String token = JWTUtil.createToken(payload, secret.getBytes());
        return token;
    }

    public boolean verifyToken(String token) {
        try {
            // 验证token签名
            JWTUtil.verify(token, secret.getBytes());
            
            // 验证过期时间
            JWT jwt = JWTUtil.parseToken(token);
            Object expObj = jwt.getPayload("exp");
            Object iatObj = jwt.getPayload("iat");
            
            if (expObj != null) {
                long exp = Long.parseLong(expObj.toString());
                long currentTime = System.currentTimeMillis() / 1000;
                
                if (currentTime > exp) {
                    log.warn("Token已过期: exp={} ({}), current={} ({})", 
                            exp, new DateTime(exp * 1000),
                            currentTime, new DateTime(currentTime * 1000));
                    return false;
                }
            } else {
                log.warn("Token中没有过期时间字段");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    public Long getUserIdFromToken(String token) {
        try {
            // 先验证token是否有效
            if (!verifyToken(token)) {
                log.warn("Token验证失败，无法获取用户ID");
                return null;
            }
            Long userId = Long.valueOf(JWTUtil.parseToken(token).getPayload("userId").toString());
            log.debug("从Token获取用户ID: {}", userId);
            return userId;
        } catch (Exception e) {
            log.warn("从Token获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
}
