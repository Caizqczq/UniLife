package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Byte role;
    private Byte isVerified;
    private Byte status;
    private String token;
    private String LoginIp;
}

