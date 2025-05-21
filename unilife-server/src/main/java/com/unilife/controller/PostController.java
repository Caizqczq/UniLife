package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreatePostDTO;
import com.unilife.model.dto.UpdatePostDTO;
import com.unilife.service.PostService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "帖子管理")
@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "创建帖子")
    @PostMapping
    public Result<?> createPost(@RequestBody CreatePostDTO createPostDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return postService.createPost(userId, createPostDTO);
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<?> getPostDetail(@PathVariable("id") Long postId) {
        // 从当前上下文获取用户ID，可能为null（未登录用户）
        Long userId = BaseContext.getId();
        return postService.getPostDetail(postId, userId);
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping
    public Result<?> getPostList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort) {
        return postService.getPostList(categoryId, page, size, sort);
    }

    @Operation(summary = "更新帖子")
    @PutMapping("/{id}")
    public Result<?> updatePost(
            @PathVariable("id") Long postId,
            @RequestBody UpdatePostDTO updatePostDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return postService.updatePost(postId, userId, updatePostDTO);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<?> deletePost(@PathVariable("id") Long postId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return postService.deletePost(postId, userId);
    }

    @Operation(summary = "点赞/取消点赞帖子")
    @PostMapping("/{id}/like")
    public Result<?> likePost(@PathVariable("id") Long postId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return postService.likePost(postId, userId);
    }
    
    @Operation(summary = "获取用户的帖子列表")
    @GetMapping("/user/{userId}")
    public Result<?> getUserPosts(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort) {
        return postService.getUserPosts(userId, page, size, sort);
    }
}