package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.mapper.CommentMapper;
import com.unilife.mapper.PostMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateCommentDTO;
import com.unilife.model.entity.Comment;
import com.unilife.model.entity.Post;
import com.unilife.model.entity.User;
import com.unilife.model.vo.CommentVO;
import com.unilife.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result createComment(Long userId, CreateCommentDTO createCommentDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查帖子是否存在
        Post post = postMapper.getById(createCommentDTO.getPostId());
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 如果是回复评论，检查父评论是否存在
        if (createCommentDTO.getParentId() != null) {
            Comment parentComment = commentMapper.getById(createCommentDTO.getParentId());
            if (parentComment == null) {
                return Result.error(404, "父评论不存在");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setPostId(createCommentDTO.getPostId());
        comment.setUserId(userId);
        comment.setContent(createCommentDTO.getContent());
        comment.setParentId(createCommentDTO.getParentId());
        comment.setLikeCount(0);
        comment.setStatus((byte) 1);

        // 保存评论
        commentMapper.insert(comment);

        // 增加帖子评论数
        postMapper.incrementCommentCount(createCommentDTO.getPostId());

        Map<String, Object> data = new HashMap<>();
        data.put("commentId", comment.getId());

        return Result.success(data, "评论成功");
    }

    @Override
    public Result getCommentsByPostId(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postMapper.getById(postId);
        if (post == null) {
            return Result.error(404, "帖子不存在");
        }

        // 获取一级评论
        List<Comment> topLevelComments = commentMapper.getTopLevelCommentsByPostId(postId);

        // 转换为VO
        List<CommentVO> commentVOs = topLevelComments.stream().map(comment -> {
            // 获取评论用户信息
            User user = userMapper.getUserById(comment.getUserId());

            // 获取回复列表
            List<Comment> replies = commentMapper.getRepliesByParentId(comment.getId());
            List<CommentVO> replyVOs = replies.stream().map(reply -> {
                User replyUser = userMapper.getUserById(reply.getUserId());
                return CommentVO.builder()
                        .id(reply.getId())
                        .postId(reply.getPostId())
                        .userId(reply.getUserId())
                        .nickname(replyUser != null ? replyUser.getNickname() : "未知用户")
                        .avatar(replyUser != null ? replyUser.getAvatar() : null)
                        .content(reply.getContent())
                        .parentId(reply.getParentId())
                        .likeCount(reply.getLikeCount())
                        .isLiked(false) // 实际开发中应该查询用户是否点赞
                        .createdAt(reply.getCreatedAt())
                        .replies(new ArrayList<>())
                        .build();
            }).collect(Collectors.toList());

            return CommentVO.builder()
                    .id(comment.getId())
                    .postId(comment.getPostId())
                    .userId(comment.getUserId())
                    .nickname(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : null)
                    .content(comment.getContent())
                    .parentId(comment.getParentId())
                    .likeCount(comment.getLikeCount())
                    .isLiked(false) // 实际开发中应该查询用户是否点赞
                    .createdAt(comment.getCreatedAt())
                    .replies(replyVOs)
                    .build();
        }).collect(Collectors.toList());

        // 获取评论总数
        Integer count = commentMapper.getCountByPostId(postId);

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", count);
        data.put("list", commentVOs);

        return Result.success(data);
    }

    @Override
    @Transactional
    public Result deleteComment(Long commentId, Long userId) {
        // 获取评论
        Comment comment = commentMapper.getById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }

        // 检查是否有权限删除
        if (!comment.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除此评论");
        }

        // 删除评论（逻辑删除）
        commentMapper.delete(commentId);

        // 减少帖子评论数
        postMapper.decrementCommentCount(comment.getPostId());

        return Result.success(null, "删除成功");
    }

    @Override
    public Result likeComment(Long commentId, Long userId) {
        // 获取评论
        Comment comment = commentMapper.getById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }

        // 检查用户是否已点赞
        // 注意：这里需要创建一个评论点赞表和相应的Mapper，实际开发中需要先创建
        boolean isLiked = false; // commentLikeMapper.isLiked(commentId, userId);

        if (isLiked) {
            // 取消点赞
            // commentLikeMapper.delete(commentId, userId);
            commentMapper.decrementLikeCount(commentId);
            return Result.success(null, "取消点赞成功");
        } else {
            // 添加点赞
            // commentLikeMapper.insert(commentId, userId);
            commentMapper.incrementLikeCount(commentId);
            return Result.success(null, "点赞成功");
        }
    }
}