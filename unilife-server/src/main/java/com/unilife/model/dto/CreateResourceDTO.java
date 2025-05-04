package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建资源的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateResourceDTO {
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 文件类型（由后端根据上传文件自动判断）
     * 此字段在前端可选，后端会根据实际上传文件覆盖
     */
    private String fileType;
}