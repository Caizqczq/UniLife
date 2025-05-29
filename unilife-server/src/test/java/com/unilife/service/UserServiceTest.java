package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateUserDTO;
import com.unilife.model.dto.UpdateUserDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.entity.User;
import com.unilife.service.impl.UserServiceImpl;
import com.unilife.utils.JwtUtil;
import com.unilife.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setNickname("测试用户");
        testUser.setPassword("$2a$10$encrypted_password"); // 模拟加密后的密码
        testUser.setAvatar("avatar.jpg");
        testUser.setStatus(1);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("newuser");
        createUserDTO.setEmail("newuser@example.com");
        createUserDTO.setNickname("新用户");
        createUserDTO.setPassword("password123");

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setNickname("更新后的昵称");
        updateUserDTO.setAvatar("new_avatar.jpg");

        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        // Mock 依赖方法
        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.findByEmail("newuser@example.com")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.encode("password123"))
                .thenReturn("$2a$10$encrypted_password");

            // 执行测试
            Result<?> result = userService.register(createUserDTO);

            // 验证结果
            assertTrue(result.isSuccess());
            assertEquals("注册成功", result.getMessage());

            // 验证方法调用
            verify(userMapper).findByUsername("newuser");
            verify(userMapper).findByEmail("newuser@example.com");
            verify(userMapper).insert(any(User.class));
        }
    }

    @Test
    void testRegister_UsernameExists() {
        // Mock 用户名已存在
        when(userMapper.findByUsername("newuser")).thenReturn(testUser);

        // 执行测试
        Result<?> result = userService.register(createUserDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("用户名已存在", result.getMessage());

        // 验证不会尝试插入用户
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        // Mock 邮箱已存在
        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(userMapper.findByEmail("newuser@example.com")).thenReturn(testUser);

        // 执行测试
        Result<?> result = userService.register(createUserDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("邮箱已存在", result.getMessage());
    }

    @Test
    void testLogin_Success() {
        // Mock 依赖方法
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class);
             MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            
            passwordUtil.when(() -> PasswordUtil.matches("password123", "$2a$10$encrypted_password"))
                .thenReturn(true);
            jwtUtil.when(() -> JwtUtil.generateToken(1L))
                .thenReturn("mock_jwt_token");

            // 执行测试
            Result<?> result = userService.login(loginDTO);

            // 验证结果
            assertTrue(result.isSuccess());
            assertEquals("登录成功", result.getMessage());
            assertNotNull(result.getData());

            // 验证方法调用
            verify(userMapper).findByUsername("testuser");
            verify(userMapper).updateLastLoginTime(1L);
        }
    }

    @Test
    void testLogin_UserNotFound() {
        // Mock 用户不存在
        when(userMapper.findByUsername("testuser")).thenReturn(null);

        // 执行测试
        Result<?> result = userService.login(loginDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
    }

    @Test
    void testLogin_PasswordIncorrect() {
        // Mock 密码错误
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.matches("password123", "$2a$10$encrypted_password"))
                .thenReturn(false);

            // 执行测试
            Result<?> result = userService.login(loginDTO);

            // 验证结果
            assertFalse(result.isSuccess());
            assertEquals(401, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
        }
    }

    @Test
    void testLogin_UserDisabled() {
        // Mock 用户被禁用
        testUser.setStatus(0);
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.matches("password123", "$2a$10$encrypted_password"))
                .thenReturn(true);

            // 执行测试
            Result<?> result = userService.login(loginDTO);

            // 验证结果
            assertFalse(result.isSuccess());
            assertEquals(403, result.getCode());
            assertEquals("账户已被禁用", result.getMessage());
        }
    }

    @Test
    void testGetUserInfo_Success() {
        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);

        // 执行测试
        Result<?> result = userService.getUserInfo(1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        // 验证方法调用
        verify(userMapper).findById(1L);
    }

    @Test
    void testGetUserInfo_UserNotFound() {
        // Mock 用户不存在
        when(userMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = userService.getUserInfo(1L);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    void testUpdateUserInfo_Success() {
        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(userMapper.update(any(User.class))).thenReturn(1);

        // 执行测试
        Result<?> result = userService.updateUserInfo(1L, updateUserDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("用户信息更新成功", result.getMessage());

        // 验证方法调用
        verify(userMapper).update(any(User.class));
    }

    @Test
    void testSendEmailVerificationCode_Success() {
        String email = "test@example.com";
        String verificationCode = "123456";

        // Mock Redis操作
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));

        // 执行测试
        Result<?> result = userService.sendEmailVerificationCode(email);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("验证码发送成功", result.getMessage());

        // 验证邮件发送
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testVerifyEmailCode_Success() {
        String email = "test@example.com";
        String code = "123456";

        // Mock Redis操作
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));
        when(redisTemplate.opsForValue().get("email_code:" + email)).thenReturn(code);

        // 执行测试
        Result<?> result = userService.verifyEmailCode(email, code);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("验证码验证成功", result.getMessage());

        // 验证删除验证码
        verify(redisTemplate).delete("email_code:" + email);
    }

    @Test
    void testVerifyEmailCode_CodeExpired() {
        String email = "test@example.com";
        String code = "123456";

        // Mock 验证码不存在（已过期）
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));
        when(redisTemplate.opsForValue().get("email_code:" + email)).thenReturn(null);

        // 执行测试
        Result<?> result = userService.verifyEmailCode(email, code);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("验证码已过期", result.getMessage());
    }

    @Test
    void testVerifyEmailCode_CodeIncorrect() {
        String email = "test@example.com";
        String code = "123456";
        String wrongCode = "654321";

        // Mock 验证码错误
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));
        when(redisTemplate.opsForValue().get("email_code:" + email)).thenReturn(wrongCode);

        // 执行测试
        Result<?> result = userService.verifyEmailCode(email, code);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("验证码错误", result.getMessage());
    }

    @Test
    void testResetPassword_Success() {
        String email = "test@example.com";
        String newPassword = "newpassword123";

        // Mock 依赖方法
        when(userMapper.findByEmail(email)).thenReturn(testUser);
        when(userMapper.updatePassword(eq(1L), anyString())).thenReturn(1);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.encode(newPassword))
                .thenReturn("$2a$10$new_encrypted_password");

            // 执行测试
            Result<?> result = userService.resetPassword(email, newPassword);

            // 验证结果
            assertTrue(result.isSuccess());
            assertEquals("密码重置成功", result.getMessage());

            // 验证方法调用
            verify(userMapper).updatePassword(eq(1L), eq("$2a$10$new_encrypted_password"));
        }
    }

    @Test
    void testGetUserList_Success() {
        // Mock 用户列表
        List<User> users = Arrays.asList(testUser);
        when(userMapper.findByConditions(any(), any(), anyInt(), anyInt())).thenReturn(users);
        when(userMapper.countByConditions(any(), any())).thenReturn(1);

        // 执行测试
        Result<?> result = userService.getUserList("测试", 1, 1, 10);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        // 验证方法调用
        verify(userMapper).findByConditions(any(), any(), anyInt(), anyInt());
        verify(userMapper).countByConditions(any(), any());
    }

    @Test
    void testChangePassword_Success() {
        String oldPassword = "oldpassword";
        String newPassword = "newpassword123";

        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(userMapper.updatePassword(eq(1L), anyString())).thenReturn(1);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.matches(oldPassword, "$2a$10$encrypted_password"))
                .thenReturn(true);
            passwordUtil.when(() -> PasswordUtil.encode(newPassword))
                .thenReturn("$2a$10$new_encrypted_password");

            // 执行测试
            Result<?> result = userService.changePassword(1L, oldPassword, newPassword);

            // 验证结果
            assertTrue(result.isSuccess());
            assertEquals("密码修改成功", result.getMessage());

            // 验证方法调用
            verify(userMapper).updatePassword(eq(1L), eq("$2a$10$new_encrypted_password"));
        }
    }

    @Test
    void testChangePassword_OldPasswordIncorrect() {
        String oldPassword = "wrongpassword";
        String newPassword = "newpassword123";

        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);

        try (MockedStatic<PasswordUtil> passwordUtil = mockStatic(PasswordUtil.class)) {
            passwordUtil.when(() -> PasswordUtil.matches(oldPassword, "$2a$10$encrypted_password"))
                .thenReturn(false);

            // 执行测试
            Result<?> result = userService.changePassword(1L, oldPassword, newPassword);

            // 验证结果
            assertFalse(result.isSuccess());
            assertEquals(400, result.getCode());
            assertEquals("原密码错误", result.getMessage());

            // 验证不会更新密码
            verify(userMapper, never()).updatePassword(anyLong(), anyString());
        }
    }
} 