package com.unilife.utils;

import com.unilife.model.dto.*;
import com.unilife.model.entity.*;

import java.time.LocalDateTime;

/**
 * 测试数据构建工具类
 * 提供各种实体和DTO的测试数据构建方法
 */
public class TestDataBuilder {

    /**
     * 构建测试用户
     */
    public static User buildTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setNickname("测试用户");
        user.setPassword("$2a$10$encrypted_password");
        user.setAvatar("avatar.jpg");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * 构建测试分类
     */
    public static Category buildTestCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("测试分类");
        category.setDescription("测试分类描述");
        category.setIcon("test-icon");
        category.setSort(1);
        category.setStatus(1);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    /**
     * 构建测试帖子
     */
    public static Post buildTestPost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("测试帖子");
        post.setContent("测试帖子内容");
        post.setUserId(1L);
        post.setCategoryId(1L);
        post.setLikeCount(0);
        post.setViewCount(0);
        post.setCommentCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    /**
     * 构建测试资源
     */
    public static Resource buildTestResource() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setTitle("测试资源");
        resource.setDescription("测试资源描述");
        resource.setFileName("test.pdf");
        resource.setFileUrl("http://example.com/test.pdf");
        resource.setFileSize(1024L);
        resource.setFileType("pdf");
        resource.setUserId(1L);
        resource.setCategoryId(1L);
        resource.setDownloadCount(0);
        resource.setLikeCount(0);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        return resource;
    }

    /**
     * 构建创建帖子DTO
     */
    public static CreatePostDTO buildCreatePostDTO() {
        CreatePostDTO dto = new CreatePostDTO();
        dto.setTitle("新帖子标题");
        dto.setContent("新帖子内容");
        dto.setCategoryId(1L);
        return dto;
    }

    /**
     * 构建创建用户DTO
     */
    public static CreateUserDTO buildCreateUserDTO() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setNickname("新用户");
        dto.setPassword("password123");
        return dto;
    }

    /**
     * 构建带有指定ID的用户
     */
    public static User buildTestUser(Long id) {
        User user = buildTestUser();
        user.setId(id);
        return user;
    }
} 