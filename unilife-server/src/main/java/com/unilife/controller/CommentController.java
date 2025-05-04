package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateCommentDTO;
import com.unilife.service.CommentService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理")
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<?> createComment(@RequestBody CreateCommentDTO createCommentDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return commentService.createComment(userId, createCommentDTO);
    }

    @Operation(summary = "获取帖子评论列表")
    @GetMapping("/post/{postId}")
    public Result<?> getCommentsByPostId(@PathVariable("postId") Long postId) {
        // 从当前上下文获取用户ID，可能为null（未登录用户）
        Long userId = BaseContext.getId();
        return commentService.getCommentsByPostId(postId, userId);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<?> deleteComment(@PathVariable("id") Long commentId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return commentService.deleteComment(commentId, userId);
    }

    @Operation(summary = "点赞/取消点赞评论")
    @PostMapping("/{id}/like")
    public Result<?> likeComment(@PathVariable("id") Long commentId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return commentService.likeComment(commentId, userId);
    }
}