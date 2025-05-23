package com.unilife.service;

import com.unilife.model.dto.SearchDTO;
import com.unilife.model.vo.SearchResultVO;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 综合搜索
     * @param searchDTO 搜索参数
     * @return 搜索结果
     */
    SearchResultVO search(SearchDTO searchDTO);
    
    /**
     * 搜索帖子
     * @param searchDTO 搜索参数
     * @return 搜索结果
     */
    SearchResultVO searchPosts(SearchDTO searchDTO);
    
    /**
     * 搜索资源
     * @param searchDTO 搜索参数
     * @return 搜索结果
     */
    SearchResultVO searchResources(SearchDTO searchDTO);
    
    /**
     * 搜索用户
     * @param searchDTO 搜索参数
     * @return 搜索结果
     */
    SearchResultVO searchUsers(SearchDTO searchDTO);
    
    /**
     * 获取搜索建议
     * @param keyword 关键词
     * @return 建议列表
     */
    List<String> getSuggestions(String keyword);
    
    /**
     * 获取热门搜索词
     * @return 热门搜索词列表
     */
    List<String> getHotKeywords();
} 