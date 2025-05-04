package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceVO {
    /**
     * 资源ID
     */
    private Long id;
    
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 上传用户ID
     */
    private Long userId;
    
    /**
     * 上传用户昵称
     */
    private String nickname;
    
    /**
     * 上传用户头像
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
     * 下载次数
     */
    private Integer downloadCount;
    
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
     * 更新时间
     */
    private LocalDateTime updatedAt;
}