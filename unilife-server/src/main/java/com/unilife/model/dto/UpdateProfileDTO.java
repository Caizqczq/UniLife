package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户个人资料的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    private String nickname;
    private String bio;
    private Byte gender;
    private String department;
    private String major;
    private String grade;
}