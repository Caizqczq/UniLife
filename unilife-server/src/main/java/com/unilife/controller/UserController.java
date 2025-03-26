package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.EmailDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import com.unilife.model.vo.LoginVO;
import com.unilife.model.vo.RegisterVO;
import com.unilife.service.UserService;
import com.unilife.utils.BaseContext;
import com.unilife.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "用户注册")
    @PostMapping("register")
    public Result register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        return userService.register(registerDTO,request);
    }

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginDTO loginDTO,HttpServletRequest request) {
        Result login = userService.login(loginDTO,request);
        //登陆成功后生成jwt令牌
        LoginVO vo=(LoginVO) login.getData();
        if (vo == null) {
            return login;
        }
        Long id = vo.getId();
        String token = jwtUtil.generateToken(id);
        vo.setToken(token);

        //Threadlocal保存当前用户id
        BaseContext.setId(id);
        return Result.success(vo);
    }

    @ApiOperation(value = "获取邮箱验证码")
    @PostMapping("code")
    public Result getCode(@RequestBody EmailDTO emailDto,HttpServletRequest request) {
        String email=emailDto.getEmail();
        log.debug("收到的原始邮箱: {}", email);
        return userService.sendVerificationCode(email,request);
    }

    @ApiOperation(value = "邮箱验证码登录")
    @PostMapping("login/code")
    public Result loginWithEmailCode(@RequestBody LoginEmailDTO loginEmailDTO,HttpServletRequest request) {
        return userService.loginWithEmail(loginEmailDTO,request);
    }

}
