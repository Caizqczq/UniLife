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

        log.info("JwtInterceptor preHandle");
        String token = request.getHeader("Authorization");

        if(StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }

        boolean verified = jwtUtil.verifyToken(token);
        if (!verified) {
            response.setStatus(401);
            return false;
        }

        //从token中获取userid并存入threadlocal
        Long userId = jwtUtil.getUserIdFromToken(token);
        BaseContext.setId(userId);


        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeId();
    }
}
