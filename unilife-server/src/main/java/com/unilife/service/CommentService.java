package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateCommentDTO;

/**
 * 评论服务接口
 */
public interface CommentService {
    /**
     * 创建评论
     * @param userId 用户ID
     * @param createCommentDTO 创建评论DTO
     * @return 结果
     */
    Result createComment(Long userId, CreateCommentDTO createCommentDTO);
    
    /**
     * 获取帖子评论列表
     * @param postId 帖子ID
     * @param userId 当前用户ID，可为null
     * @return 结果
     */
    Result getCommentsByPostId(Long postId, Long userId);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    Result deleteComment(Long commentId, Long userId);
    
    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    Result likeComment(Long commentId, Long userId);
}