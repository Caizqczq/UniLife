package com.unilife.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 上传用户ID
     */
    private Long userId;

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
     * 分类ID
     */
    private Long categoryId;

    /**
     * 下载次数
     */
    private Integer downloadCount = 0;

    /**
     * 点赞次数
     */
    private Integer likeCount = 0;

    /**
     * 状态（0-删除, 1-正常）
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