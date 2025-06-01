package com.unilife.config;

import com.unilife.interceptor.AdminInterceptor;
import com.unilife.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理员权限拦截器 - 优先级最高
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .order(1);
        
        // JWT拦截器
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns(
                        // 用户登录注册相关
                        "/users/login",
                        "/users/register",
                        "/users/code",
                        "/users/login/code",
                        
                        // 静态资源访问
                        "/api/files/**",
                        
                        // 管理员接口（由AdminInterceptor处理）
                        "/admin/**",
                        
                        // Swagger文档相关
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/favicon.ico",
                        "/knife4j/**"
                        )
                .order(2);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        String uploadPath = new File("uploads").getAbsolutePath();
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + uploadPath + File.separator);
        
        // 直接映射到uploads/resources目录
        registry.addResourceHandler("/api/resources/**")
                .addResourceLocations("file:" + new File("uploads/resources").getAbsolutePath() + File.separator);
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
