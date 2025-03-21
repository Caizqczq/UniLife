package com.unilife.model.vo;

import lombok.Data;

@Data
public class LoginVO {
    private Integer id;
    private String username;
    private String nickname;

    public LoginVO(Integer id, String username, String nickname)
    {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }
}
