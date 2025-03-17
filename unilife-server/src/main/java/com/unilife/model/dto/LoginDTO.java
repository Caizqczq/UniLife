package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String studentId;
    private String department;
    private String major;
    private String grade;
}
