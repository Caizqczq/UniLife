package com.unilife.mapper;

import com.unilife.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insert(User user);
    User FindByEmail(@Param("email") String email, @Param("password") String password);
    User getUserByEmail(String email);
    void UpdateIPLocation(@Param("email") String email,@Param("loginIp") String loginIp);
    User FindByOnlyEmail(@Param("email") String email);
}
