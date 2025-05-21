package com.unilife.config;

import com.unilife.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns(
                        // 用户登录注册相关
                        "/users/login",
                        "/users/register",
                        "/users/code",
                        "/users/login/code",
                        
                        // 论坛相关 - 允许未登录用户访问帖子列表和帖子详情
                        "/posts",                // 帖子列表
                        "/posts/*/",            // 帖子详情
                        "/posts/{id}",           // 帖子详情（另一种匹配方式）
                        "/categories",           // 分类列表
                        
                        // Swagger文档相关
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/favicon.ico",
                        "/knife4j/**"
                        );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 允许所有来源，生产环境建议限制为特定域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
