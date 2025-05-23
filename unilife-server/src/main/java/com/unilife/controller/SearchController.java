package com.unilife.controller;


import com.unilife.common.result.Result;
import com.unilife.model.dto.SearchDTO;
import com.unilife.model.vo.SearchResultVO;
import com.unilife.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "搜索管理", description = "搜索相关接口")
public class SearchController {
    
    private final SearchService searchService;
    
    @GetMapping
    @Operation(summary = "综合搜索", description = "根据关键词搜索帖子、资源和用户")
    public Result<SearchResultVO> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "搜索类型") @RequestParam(defaultValue = "all") String type,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "relevance") String sortBy,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setType(type);
        searchDTO.setCategoryId(categoryId);
        searchDTO.setSortBy(sortBy);
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        
        SearchResultVO result = searchService.search(searchDTO);
        return Result.success(result);
    }
    
    @GetMapping("/posts")
    @Operation(summary = "搜索帖子", description = "搜索论坛帖子")
    public Result<SearchResultVO> searchPosts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "relevance") String sortBy,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setType("post");
        searchDTO.setCategoryId(categoryId);
        searchDTO.setSortBy(sortBy);
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        
        SearchResultVO result = searchService.searchPosts(searchDTO);
        return Result.success(result);
    }
    
    @GetMapping("/resources")
    @Operation(summary = "搜索资源", description = "搜索学习资源")
    public Result<SearchResultVO> searchResources(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "排序方式") @RequestParam(defaultValue = "relevance") String sortBy,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setType("resource");
        searchDTO.setCategoryId(categoryId);
        searchDTO.setSortBy(sortBy);
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        
        SearchResultVO result = searchService.searchResources(searchDTO);
        return Result.success(result);
    }
    
    @GetMapping("/users")
    @Operation(summary = "搜索用户", description = "搜索用户")
    public Result<SearchResultVO> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setKeyword(keyword);
        searchDTO.setType("user");
        searchDTO.setPage(page);
        searchDTO.setSize(size);
        
        SearchResultVO result = searchService.searchUsers(searchDTO);
        return Result.success(result);
    }
    
    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议", description = "根据关键词获取搜索建议")
    public Result<List<String>> getSuggestions(
            @Parameter(description = "关键词") @RequestParam String keyword) {
        List<String> suggestions = searchService.getSuggestions(keyword);
        return Result.success(suggestions);
    }
    
    @GetMapping("/hot-keywords")
    @Operation(summary = "获取热门搜索词", description = "获取热门搜索关键词列表")
    public Result<List<String>> getHotKeywords() {
        List<String> hotKeywords = searchService.getHotKeywords();
        return Result.success(hotKeywords);
    }
} 