package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建帖子的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostDTO {
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子内容
     */
    private String content;
    
    /**
     * 分类ID
     */
    private Long categoryId;
}