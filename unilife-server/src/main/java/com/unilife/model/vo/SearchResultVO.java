package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索结果VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultVO {
    
    /**
     * 搜索结果项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchItem {
        
        /**
         * 项目ID
         */
        private Long id;
        
        /**
         * 标题
         */
        private String title;
        
        /**
         * 内容摘要
         */
        private String summary;
        
        /**
         * 类型：post-帖子, resource-资源, user-用户
         */
        private String type;
        
        /**
         * 作者/用户信息
         */
        private String author;
        
        /**
         * 头像
         */
        private String avatar;
        
        /**
         * 分类名称
         */
        private String categoryName;
        
        /**
         * 创建时间
         */
        private LocalDateTime createdAt;
        
        /**
         * 点赞数
         */
        private Integer likeCount;
        
        /**
         * 浏览数/下载数
         */
        private Integer viewCount;
        
        /**
         * 高亮的关键词片段
         */
        private List<String> highlights;
    }
    
    /**
     * 搜索结果列表
     */
    private List<SearchItem> items;
    
    /**
     * 总数量
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer size;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 搜索耗时（毫秒）
     */
    private Long searchTime;
} 