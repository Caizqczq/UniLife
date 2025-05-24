package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreatePostDTO;
import com.unilife.model.dto.UpdatePostDTO;

/**
 * 帖子服务接口
 */
public interface PostService {
    /**
     * 创建帖子
     * @param userId 用户ID
     * @param createPostDTO 创建帖子DTO
     * @return 结果
     */
    Result createPost(Long userId, CreatePostDTO createPostDTO);
    
    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param userId 当前用户ID，可为null
     * @return 结果
     */
    Result getPostDetail(Long postId, Long userId);
    
    /**
     * 获取帖子列表
     * @param categoryId 分类ID，可为null
     * @param keyword 搜索关键词，可为null
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序方式（latest-最新，hot-热门）
     * @return 结果
     */
    Result getPostList(Long categoryId, String keyword, Integer page, Integer size, String sort);
    
    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param updatePostDTO 更新帖子DTO
     * @return 结果
     */
    Result updatePost(Long postId, Long userId, UpdatePostDTO updatePostDTO);
    
    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    Result deletePost(Long postId, Long userId);
    
    /**
     * 点赞/取消点赞帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    Result likePost(Long postId, Long userId);
    
    /**
     * 获取用户的帖子列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序方式（latest-最新，hot-热门）
     * @return 结果
     */
    Result getUserPosts(Long userId, Integer page, Integer size, String sort);
}