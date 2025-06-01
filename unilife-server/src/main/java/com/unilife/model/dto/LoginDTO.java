package com.unilife.model.dto;
//这个才是登录的DTO

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String account;  // 支持用户名或邮箱
    private String password;
}
