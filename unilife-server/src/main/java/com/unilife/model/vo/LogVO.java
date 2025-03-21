package com.unilife.model.vo;

import com.unilife.model.entity.User;
import lombok.Data;

@Data
public class LogVO {
    private Integer id;
    private String username;
    private String nickname;
    private String avatar;
    private Byte role;
    private Byte isVerified;
    private Byte status;

    public LogVO(Integer id,String username,String nickname,String avatar,Byte role,Byte isVerified,Byte status)
    {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
        this.isVerified = isVerified;
    }

}

