package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.unilife.common.result.Result;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.entity.User;
import com.unilife.model.vo.LoginVO;
import com.unilife.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result register(LoginDTO loginDTO) {
        User user = new User();
        BeanUtil.copyProperties(loginDTO,user);
        userMapper.insert(user);
        LoginVO loginVO = new LoginVO();
        return Result.success(loginVO);
    }
}
