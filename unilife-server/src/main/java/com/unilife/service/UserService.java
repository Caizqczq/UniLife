package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.LogDTO;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.entity.User;


public interface UserService {
    Result register(LoginDTO loginDTO);
    Result login(LogDTO logDTO);

    Result sendVerificationCode(String email);
}
