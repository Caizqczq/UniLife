package com.unilife.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=qwertyuiopasdfghjklzxcvbnm",
    "jwt.expiration=2" // 2秒过期，用于测试
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testTokenExpiration() throws InterruptedException {
        Long userId = 123L;
        
        // 生成token
        String token = jwtUtil.generateToken(userId);
        assertNotNull(token);
        
        // 立即验证，应该有效
        assertTrue(jwtUtil.verifyToken(token));
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
        
        // 等待3秒，token应该过期
        Thread.sleep(3000);
        
        // 验证token已过期
        assertFalse(jwtUtil.verifyToken(token));
        assertNull(jwtUtil.getUserIdFromToken(token));
    }
    
    @Test
    public void testValidToken() {
        Long userId = 456L;
        
        // 生成token
        String token = jwtUtil.generateToken(userId);
        assertNotNull(token);
        
        // 验证token有效
        assertTrue(jwtUtil.verifyToken(token));
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
    }
    
    @Test
    public void testInvalidToken() {
        // 测试无效token
        assertFalse(jwtUtil.verifyToken("invalid.token.here"));
        assertNull(jwtUtil.getUserIdFromToken("invalid.token.here"));
        
        // 测试空token
        assertFalse(jwtUtil.verifyToken(""));
        assertFalse(jwtUtil.verifyToken(null));
    }
} 