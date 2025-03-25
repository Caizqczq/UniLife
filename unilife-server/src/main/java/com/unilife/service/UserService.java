package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;


public interface UserService {
    Result register(RegisterDTO registerDTO);
    Result login(LoginDTO loginDTO);

    Result sendVerificationCode(String email);

    Result loginWithEmail(LoginEmailDTO loginEmailDTO);
}
