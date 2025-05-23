package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.LoginDTO;
import com.unilife.model.dto.LoginEmailDTO;
import com.unilife.model.dto.RegisterDTO;
import com.unilife.model.dto.UpdateEmailDTO;
import com.unilife.model.dto.UpdatePasswordDTO;
import com.unilife.model.dto.UpdateProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    Result register(RegisterDTO registerDTO, HttpServletRequest request);

    Result login(LoginDTO loginDTO, HttpServletRequest request);

    Result sendVerificationCode(String email, HttpServletRequest request);

    Result loginWithEmail(LoginEmailDTO loginEmailDTO, HttpServletRequest request);

    // 用户信息管理相关方法
    Result getUserProfile(Long userId);

    Result updateUserProfile(Long userId, UpdateProfileDTO profileDTO);

    Result updatePassword(Long userId, UpdatePasswordDTO passwordDTO);

    Result updateAvatar(Long userId, MultipartFile file);

    Result updateEmail(Long userId, UpdateEmailDTO emailDTO);

    // 用户统计数据
    Result getUserStats(Long userId);

    // 用户最近帖子
    Result getUserRecentPosts(Long userId, Integer limit);
}
