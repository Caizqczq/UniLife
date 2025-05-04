package com.unilife.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 帖子实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 发布用户ID
     */
    private Long userId;

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

    /**
     * 浏览次数
     */
    private Integer viewCount = 0;

    /**
     * 点赞次数
     */
    private Integer likeCount = 0;

    /**
     * 评论次数
     */
    private Integer commentCount = 0;

    /**
     * 状态（0-删除, 1-正常, 2-置顶）
     */
    private Byte status = 1;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}