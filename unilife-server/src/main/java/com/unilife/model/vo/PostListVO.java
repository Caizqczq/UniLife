package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 帖子列表项视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListVO {
    /**
     * 帖子ID
     */
    private Long id;
    
    /**
     * 帖子标题
     */
    private String title;
    
    /**
     * 帖子摘要
     */
    private String summary;
    
    /**
     * 发布用户ID
     */
    private Long userId;
    
    /**
     * 发布用户昵称
     */
    private String nickname;
    
    /**
     * 发布用户头像
     */
    private String avatar;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 评论次数
     */
    private Integer commentCount;
    
    /**
     * 帖子状态（0-删除, 1-正常, 2-置顶）
     */
    private Byte status;
    
    /**
     * 当前用户是否点赞
     */
    private Boolean isLiked;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}