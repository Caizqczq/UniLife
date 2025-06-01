package com.unilife.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.entity.User;
import com.unilife.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        try {
            // 获取token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                writeErrorResponse(response, 401, "未登录或token格式错误");
                return false;
            }

            token = token.substring(7); // 移除 "Bearer " 前缀

            // 验证token
            if (!jwtUtil.verifyToken(token)) {
                writeErrorResponse(response, 401, "token无效或已过期");
                return false;
            }

            // 获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                writeErrorResponse(response, 401, "无法获取用户信息");
                return false;
            }

            // 查询用户信息
            User user = userMapper.getUserById(userId);
            if (user == null) {
                writeErrorResponse(response, 401, "用户不存在");
                return false;
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                writeErrorResponse(response, 403, "账号已被禁用");
                return false;
            }

            // 检查是否为管理员
            if (user.getRole() != 2) {
                writeErrorResponse(response, 403, "权限不足，需要管理员权限");
                return false;
            }

            // 将用户信息存储到request中，供后续使用
            request.setAttribute("currentUser", user);
            return true;

        } catch (Exception e) {
            log.error("管理员权限验证失败", e);
            writeErrorResponse(response, 500, "权限验证失败");
            return false;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200); // HTTP状态码设为200，错误信息在响应体中
        response.setContentType("application/json;charset=UTF-8");
        
        Result result = Result.error(code, message);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(result);
        
        response.getWriter().write(jsonResponse);
    }
} 