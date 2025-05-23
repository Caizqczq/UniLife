package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.EmailDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import com.unilife.model.dto.UpdateEmailDTO;
import com.unilife.model.dto.UpdatePasswordDTO;
import com.unilife.model.dto.UpdateProfileDTO;
import com.unilife.model.vo.LoginVO;
import com.unilife.service.UserService;
import com.unilife.utils.BaseContext;
import com.unilife.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "用户注册")
    @PostMapping("register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        return userService.register(registerDTO, request);
    }

    @Operation(summary = "用户登录")
    @PostMapping("login")
    public Result<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        Result<?> login = userService.login(loginDTO, request);
        LoginVO vo = (LoginVO) login.getData();
        if (vo == null) {
            return login;
        }
        Long id = vo.getId();
        String token = jwtUtil.generateToken(id);
        vo.setToken(token);
        BaseContext.setId(id);
        return Result.success(vo);
    }

    @Operation(summary = "获取邮箱验证码")
    @PostMapping("code")
    public Result<?> getCode(@RequestBody EmailDTO emailDto, HttpServletRequest request) {
        String email = emailDto.getEmail();
        log.debug("收到的原始邮箱: {}", email);
        return userService.sendVerificationCode(email, request);
    }

    @Operation(summary = "邮箱验证码登录")
    @PostMapping("login/code")
    public Result<?> loginWithEmailCode(@RequestBody LoginEmailDTO loginEmailDTO, HttpServletRequest request) {
        Result<?> login = userService.loginWithEmail(loginEmailDTO, request);
        LoginVO vo = (LoginVO) login.getData();
        if (vo == null) {
            return login;
        }
        Long id = vo.getId();
        String token = jwtUtil.generateToken(id);
        vo.setToken(token);
        BaseContext.setId(id);
        return Result.success(vo);
    }

    // 用户信息管理相关API

    @Operation(summary = "获取用户个人信息")
    @GetMapping("info")
    public Result<?> getUserProfile() {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.getUserProfile(userId);
    }

    @Operation(summary = "更新用户个人信息")
    @PutMapping("profile")
    public Result<?> updateUserProfile(@RequestBody UpdateProfileDTO profileDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.updateUserProfile(userId, profileDTO);
    }

    @Operation(summary = "修改用户密码")
    @PutMapping("password")
    public Result<?> updatePassword(@RequestBody UpdatePasswordDTO passwordDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.updatePassword(userId, passwordDTO);
    }

    @Operation(summary = "上传用户头像")
    @PostMapping("avatar")
    public Result<?> updateAvatar(@RequestParam("file") MultipartFile file) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.updateAvatar(userId, file);
    }

    @Operation(summary = "更新用户邮箱")
    @PutMapping("email")
    public Result<?> updateEmail(@RequestBody UpdateEmailDTO emailDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.updateEmail(userId, emailDTO);
    }

    @Operation(summary = "获取用户统计数据")
    @GetMapping("stats")
    public Result<?> getUserStats() {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.getUserStats(userId);
    }

    @Operation(summary = "获取用户最近帖子")
    @GetMapping("recent-posts")
    public Result<?> getUserRecentPosts(@RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return userService.getUserRecentPosts(userId, limit);
    }
}
