package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.LogDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.entity.User;
import com.unilife.model.vo.LogVO;
import com.unilife.model.vo.LoginVO;
import com.unilife.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result register(LoginDTO loginDTO) {
        User user = new User();
        BeanUtil.copyProperties(loginDTO,user);
        userMapper.insert(user);
        LoginVO loginVO = new LoginVO(Math.toIntExact(user.getId()),user.getUsername(),user.getNickname());
        return Result.success(loginVO);
    }

    @Override
    public Result login(LogDTO logDTO) {
        User user = new User();
        BeanUtil.copyProperties(logDTO,user);//将登录的前端传来的消息拷贝给这个user
        User getuser = userMapper.FindByEmail(user.getEmail(),user.getPassword());
        if(getuser == null)
        {
            return Result.error(logDTO,"用户不存在，登录失败!");
        }
        if(!user.getPassword().equals(getuser.getPassword()))
        {
            return Result.error(logDTO,"密码错误，登录失败!");
        }
        LogVO logVO = new LogVO(Math.toIntExact(getuser.getId()), getuser.getUsername(), getuser.getNickname(),
                getuser.getAvatar(), getuser.getRole(), getuser.getIsVerified(), getuser.getStatus());
        return Result.success(logVO);
    }
}
