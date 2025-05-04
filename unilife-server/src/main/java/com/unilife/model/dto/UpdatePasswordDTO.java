package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户密码的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDTO {
    private String code;
    private String newPassword;
}