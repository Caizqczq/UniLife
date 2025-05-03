package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;


public interface UserService {
    Result register(RegisterDTO registerDTO, HttpServletRequest request);

    Result login(LoginDTO loginDTO, HttpServletRequest request);

    Result sendVerificationCode(String email,HttpServletRequest request);

    Result loginWithEmail(LoginEmailDTO loginEmailDTO,HttpServletRequest request);
}
