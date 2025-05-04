package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建评论的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDTO {
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（回复某条评论）
     * 如果是直接评论帖子，则为null
     */
    private Long parentId;
}