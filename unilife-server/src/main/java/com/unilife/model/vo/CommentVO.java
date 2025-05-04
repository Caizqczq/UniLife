package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVO {
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 帖子ID
     */
    private Long postId;
    
    /**
     * 评论用户ID
     */
    private Long userId;
    
    /**
     * 评论用户昵称
     */
    private String nickname;
    
    /**
     * 评论用户头像
     */
    private String avatar;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（回复某条评论）
     */
    private Long parentId;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 当前用户是否点赞
     */
    private Boolean isLiked;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 回复列表
     */
    private List<CommentVO> replies;
}