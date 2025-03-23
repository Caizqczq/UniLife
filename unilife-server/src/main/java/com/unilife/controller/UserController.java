package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.EmailDTO;
import com.unilife.model.dto.LogDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册")
    @PostMapping("register")
    public Result register(@RequestBody LoginDTO loginDTO) {
        return userService.register(loginDTO);
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LogDTO logDTO) { return userService.login(logDTO); }

    @ApiOperation(value = "获取邮箱验证码")
    @PostMapping("code")
    public Result getCode(@RequestBody EmailDTO emailDto){
        String email=emailDto.getEmail();
        log.debug("收到的原始邮箱: {}", email);
        return userService.sendVerificationCode(email);
    }

    @ApiOperation(value = "邮箱验证码登录")
    @PostMapping("login/code")
    public Result loginWithEmailCode(@RequestBody LoginEmailDTO loginEmailDTO){
        return userService.loginWithEmail(loginEmailDTO);
    }

}
