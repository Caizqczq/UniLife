package com.unilife.model.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    private String password;

    private String nickname;

    private String avatar;

    private String bio;

    private Byte gender;

    private String studentId;

    private String department;

    private String major;

    private String grade;

    private Integer points = 0;

    private Byte role = 0;

    private Byte status = 1;

    private Byte isVerified = 0;

    private String loginIp;

    private LocalDateTime loginTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
