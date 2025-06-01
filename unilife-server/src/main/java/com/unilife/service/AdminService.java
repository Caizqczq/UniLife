package com.unilife.service;

import com.unilife.common.result.Result;

import java.util.Map;

public interface AdminService {
    
    /**
     * 获取系统统计数据
     */
    Result getSystemStats();
    
    /**
     * 获取用户列表
     */
    Result getUserList(Integer page, Integer size, String keyword, Integer role, Integer status);
    
    /**
     * 更新用户状态
     */
    Result updateUserStatus(Long userId, Integer status);
    
    /**
     * 更新用户角色
     */
    Result updateUserRole(Long userId, Integer role);
    
    /**
     * 删除用户
     */
    Result deleteUser(Long userId);
    
    /**
     * 获取帖子列表
     */
    Result getPostList(Integer page, Integer size, String keyword, Long categoryId, Integer status);
    
    /**
     * 更新帖子状态
     */
    Result updatePostStatus(Long postId, Integer status);
    
    /**
     * 删除帖子
     */
    Result deletePost(Long postId);
    
    /**
     * 获取评论列表
     */
    Result getCommentList(Integer page, Integer size, String keyword, Long postId, Integer status);
    
    /**
     * 删除评论
     */
    Result deleteComment(Long commentId);
    
    /**
     * 获取分类列表
     */
    Result getCategoryList();
    
    /**
     * 创建分类
     */
    Result createCategory(Map<String, Object> request);
    
    /**
     * 更新分类
     */
    Result updateCategory(Long categoryId, Map<String, Object> request);
    
    /**
     * 删除分类
     */
    Result deleteCategory(Long categoryId);
    
    /**
     * 获取资源列表
     */
    Result getResourceList(Integer page, Integer size, String keyword, Long categoryId, Integer status);
    
    /**
     * 删除资源
     */
    Result deleteResource(Long resourceId);
} 