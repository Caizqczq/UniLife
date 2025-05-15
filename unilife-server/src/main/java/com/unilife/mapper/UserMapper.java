package com.unilife.mapper;

import com.unilife.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserMapper {
    void insert(User user);
    User findByEmail(String email);
    User findByUsername(String username);
    void updateLoginInfo(@Param("userId") Long userId,
                         @Param("ipLocation") String ipLocation,
                         @Param("loginTime") Date loginTime);
    void UpdateIPLocation(@Param("email") String email,@Param("loginIp") String loginIp);

    // 用户信息管理相关方法
    User getUserById(Long id);
    void updateUserProfile(User user);
    void updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatarUrl);
    void updateEmail(@Param("id") Long id, @Param("email") String email);
}
