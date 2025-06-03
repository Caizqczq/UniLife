package com.unilife.interceptor;

import cn.hutool.core.util.StrUtil;
import com.unilife.utils.BaseContext;
import com.unilife.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;



@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("OPTIONS请求，跳过token验证");
            return true; // 直接允许通过，不检查 token
        }

        String authHeader = request.getHeader("Authorization");

        if(StrUtil.isBlank(authHeader)){
            log.warn("请求头中缺少Authorization字段 - Path: {}", requestPath);
            response.setStatus(401);
            return false;
        }

        // 处理Bearer token格式
        String token = authHeader;
        if(authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
        }
        boolean verified = jwtUtil.verifyToken(token);
        if (!verified) {
            log.warn("Token验证失败 - Path: {}", requestPath);
            response.setStatus(401);
            return false;
        }

        //从token中获取userid并存入threadlocal
        Long userId = jwtUtil.getUserIdFromToken(token);
        if(userId == null) {
            log.warn("无法从Token获取用户ID - Path: {}", requestPath);
            response.setStatus(401);
            return false;
        }
        
        log.debug("Token验证成功，用户ID: {} - Path: {}", userId, requestPath);
        BaseContext.setId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeId();
    }
}
