package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户邮箱的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailDTO {
    private String email;
    private String code;
}