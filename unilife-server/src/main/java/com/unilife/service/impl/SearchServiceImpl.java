package com.unilife.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.unilife.mapper.PostMapper;
import com.unilife.mapper.ResourceMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.SearchDTO;
import com.unilife.model.entity.Post;
import com.unilife.model.entity.Resource;
import com.unilife.model.entity.User;
import com.unilife.model.vo.SearchResultVO;
import com.unilife.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final PostMapper postMapper;
    private final ResourceMapper resourceMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    
    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";
    private static final String SEARCH_HISTORY_KEY = "search:history:";
    
    @Override
    public SearchResultVO search(SearchDTO searchDTO) {
        long startTime = System.currentTimeMillis();
        
        // 记录搜索关键词
        recordSearchKeyword(searchDTO.getKeyword());
        
        List<SearchResultVO.SearchItem> allItems = new ArrayList<>();
        
        // 搜索帖子
        if ("all".equals(searchDTO.getType()) || "post".equals(searchDTO.getType())) {
            SearchResultVO postResults = searchPosts(searchDTO);
            allItems.addAll(postResults.getItems());
        }
        
        // 搜索资源
        if ("all".equals(searchDTO.getType()) || "resource".equals(searchDTO.getType())) {
            SearchResultVO resourceResults = searchResources(searchDTO);
            allItems.addAll(resourceResults.getItems());
        }
        
        // 搜索用户
        if ("all".equals(searchDTO.getType()) || "user".equals(searchDTO.getType())) {
            SearchResultVO userResults = searchUsers(searchDTO);
            allItems.addAll(userResults.getItems());
        }
        
        // 排序和分页
        allItems = sortAndPage(allItems, searchDTO);
        
        long endTime = System.currentTimeMillis();
        
        SearchResultVO result = new SearchResultVO();
        result.setItems(allItems);
        result.setTotal((long) allItems.size());
        result.setPage(searchDTO.getPage());
        result.setSize(searchDTO.getSize());
        result.setKeyword(searchDTO.getKeyword());
        result.setSearchTime(endTime - startTime);
        
        return result;
    }
    
    @Override
    public SearchResultVO searchPosts(SearchDTO searchDTO) {
        PageHelper.startPage(searchDTO.getPage(), searchDTO.getSize());
        
        List<Post> posts = postMapper.searchPosts(
            searchDTO.getKeyword(),
            searchDTO.getCategoryId(),
            searchDTO.getSortBy()
        );
        
        PageInfo<Post> pageInfo = new PageInfo<>(posts);
        
        List<SearchResultVO.SearchItem> items = posts.stream()
            .map(this::convertPostToSearchItem)
            .collect(Collectors.toList());
        
        SearchResultVO result = new SearchResultVO();
        result.setItems(items);
        result.setTotal(pageInfo.getTotal());
        result.setPage(searchDTO.getPage());
        result.setSize(searchDTO.getSize());
        result.setKeyword(searchDTO.getKeyword());
        
        return result;
    }
    
    @Override
    public SearchResultVO searchResources(SearchDTO searchDTO) {
        PageHelper.startPage(searchDTO.getPage(), searchDTO.getSize());
        
        List<Resource> resources = resourceMapper.searchResources(
            searchDTO.getKeyword(),
            searchDTO.getCategoryId(),
            searchDTO.getSortBy()
        );
        
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);
        
        List<SearchResultVO.SearchItem> items = resources.stream()
            .map(this::convertResourceToSearchItem)
            .collect(Collectors.toList());
        
        SearchResultVO result = new SearchResultVO();
        result.setItems(items);
        result.setTotal(pageInfo.getTotal());
        result.setPage(searchDTO.getPage());
        result.setSize(searchDTO.getSize());
        result.setKeyword(searchDTO.getKeyword());
        
        return result;
    }
    
    @Override
    public SearchResultVO searchUsers(SearchDTO searchDTO) {
        PageHelper.startPage(searchDTO.getPage(), searchDTO.getSize());
        
        List<User> users = userMapper.searchUsers(searchDTO.getKeyword());
        
        PageInfo<User> pageInfo = new PageInfo<>(users);
        
        List<SearchResultVO.SearchItem> items = users.stream()
            .map(this::convertUserToSearchItem)
            .collect(Collectors.toList());
        
        SearchResultVO result = new SearchResultVO();
        result.setItems(items);
        result.setTotal(pageInfo.getTotal());
        result.setPage(searchDTO.getPage());
        result.setSize(searchDTO.getSize());
        result.setKeyword(searchDTO.getKeyword());
        
        return result;
    }
    
    @Override
    public List<String> getSuggestions(String keyword) {
        if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
            return new ArrayList<>();
        }
        
        // 从热门搜索词中匹配
        Set<String> hotKeywords = redisTemplate.opsForZSet()
            .reverseRange(HOT_KEYWORDS_KEY, 0, 19);
        
        if (hotKeywords != null) {
            return hotKeywords.stream()
                .filter(k -> k.contains(keyword))
                .limit(5)
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public List<String> getHotKeywords() {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
            .reverseRangeWithScores(HOT_KEYWORDS_KEY, 0, 9);
        
        if (tuples != null) {
            return tuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
    
    private void recordSearchKeyword(String keyword) {
        if (StringUtils.hasText(keyword)) {
            // 增加关键词热度
            redisTemplate.opsForZSet().incrementScore(HOT_KEYWORDS_KEY, keyword, 1);
            // 设置过期时间
            redisTemplate.expire(HOT_KEYWORDS_KEY, 30, TimeUnit.DAYS);
        }
    }
    
    private List<SearchResultVO.SearchItem> sortAndPage(List<SearchResultVO.SearchItem> items, SearchDTO searchDTO) {
        // 根据排序方式排序
        switch (searchDTO.getSortBy()) {
            case "time":
                items.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                break;
            case "popularity":
                items.sort((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()));
                break;
            default: // relevance
                // 相关性排序可以基于关键词匹配度等
                break;
        }
        
        // 简单分页
        int start = (searchDTO.getPage() - 1) * searchDTO.getSize();
        int end = Math.min(start + searchDTO.getSize(), items.size());
        
        if (start >= items.size()) {
            return new ArrayList<>();
        }
        
        return items.subList(start, end);
    }
    
    private SearchResultVO.SearchItem convertPostToSearchItem(Post post) {
        SearchResultVO.SearchItem item = new SearchResultVO.SearchItem();
        item.setId(post.getId());
        item.setTitle(post.getTitle());
        item.setSummary(getSummary(post.getContent()));
        item.setType("post");
        
        // 通过userId查询用户信息
        User user = userMapper.getUserById(post.getUserId());
        item.setAuthor(user != null ? user.getNickname() : "未知用户");
        item.setAvatar(user != null ? user.getAvatar() : null);
        
        // 分类名称暂时设为空，需要通过categoryId查询
        item.setCategoryName("未知分类");
        item.setCreatedAt(post.getCreatedAt());
        item.setLikeCount(post.getLikeCount());
        item.setViewCount(post.getViewCount());
        return item;
    }
    
    private SearchResultVO.SearchItem convertResourceToSearchItem(Resource resource) {
        SearchResultVO.SearchItem item = new SearchResultVO.SearchItem();
        item.setId(resource.getId());
        item.setTitle(resource.getTitle());
        item.setSummary(resource.getDescription());
        item.setType("resource");
        
        // 通过userId查询用户信息
        User user = userMapper.getUserById(resource.getUserId());
        item.setAuthor(user != null ? user.getNickname() : "未知用户");
        item.setAvatar(user != null ? user.getAvatar() : null);
        
        // 分类名称暂时设为空，需要通过categoryId查询
        item.setCategoryName("未知分类");
        item.setCreatedAt(resource.getCreatedAt());
        item.setLikeCount(resource.getLikeCount());
        item.setViewCount(resource.getDownloadCount());
        return item;
    }
    
    private SearchResultVO.SearchItem convertUserToSearchItem(User user) {
        SearchResultVO.SearchItem item = new SearchResultVO.SearchItem();
        item.setId(user.getId());
        item.setTitle(user.getNickname());
        item.setSummary(user.getBio());
        item.setType("user");
        item.setAuthor(user.getUsername());
        item.setAvatar(user.getAvatar());
        item.setCategoryName(user.getDepartment());
        item.setCreatedAt(user.getCreatedAt());
        item.setLikeCount(user.getPoints());
        item.setViewCount(0);
        return item;
    }
    
    private String getSummary(String content) {
        if (content == null) return "";
        if (content.length() <= 100) return content;
        return content.substring(0, 100) + "...";
    }
} 