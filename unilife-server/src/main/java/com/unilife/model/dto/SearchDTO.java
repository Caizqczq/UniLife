package com.unilife.model.dto;

import lombok.Data;

/**
 * 搜索请求DTO
 */
@Data
public class SearchDTO {
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 搜索类型：all-全部, post-帖子, resource-资源, user-用户
     */
    private String type = "all";
    
    /**
     * 分类ID（可选）
     */
    private Long categoryId;
    
    /**
     * 排序方式：time-时间, relevance-相关性, popularity-热门度
     */
    private String sortBy = "relevance";
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
} 