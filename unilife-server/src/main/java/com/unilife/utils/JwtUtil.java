package com.unilife.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
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
        payload.put("created",now.getTime());
        return JWTUtil.createToken(payload,secret.getBytes());
    }

    public boolean verifyToken(String token) {
        try{
            JWTUtil.verify(token,secret.getBytes());
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public Long getUserIdFromToken(String token) {
        try {
            return (Long)JWTUtil.parseToken(token).getPayload("userId");
        }catch (Exception e){
            return null;
        }
    }

}
