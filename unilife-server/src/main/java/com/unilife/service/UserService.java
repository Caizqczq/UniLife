package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.LoginDTO;


public interface UserService {
    Result register(LoginDTO loginDTO);
}
