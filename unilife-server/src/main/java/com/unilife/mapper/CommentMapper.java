package com.unilife.mapper;

import com.unilife.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论数据访问层
 */
@Mapper
public interface CommentMapper {
    /**
     * 插入评论
     * @param comment 评论实体
     */
    void insert(Comment comment);
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论实体
     */
    Comment getById(Long id);
    
    /**
     * 获取帖子的一级评论列表（不包含回复）
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Comment> getTopLevelCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * 获取评论的回复列表
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<Comment> getRepliesByParentId(@Param("parentId") Long parentId);
    
    /**
     * 获取帖子的评论总数
     * @param postId 帖子ID
     * @return 评论总数
     */
    Integer getCountByPostId(@Param("postId") Long postId);
    
    /**
     * 更新评论
     * @param comment 评论实体
     */
    void update(Comment comment);
    
    /**
     * 删除评论（逻辑删除）
     * @param id 评论ID
     */
    void delete(Long id);
    
    /**
     * 增加点赞次数
     * @param id 评论ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞次数
     * @param id 评论ID
     */
    void decrementLikeCount(Long id);
    
    // ========== 管理员后台相关方法 ==========
    
    /**
     * 获取评论总数
     */
    int getTotalCount();
    
    /**
     * 获取今日新增评论数
     */
    int getNewCommentCountToday();
    
    /**
     * 根据ID获取评论（管理员用）
     */
    Comment getCommentById(Long id);
    
    /**
     * 管理员获取评论列表（支持筛选和分页）
     */
    List<Comment> getAdminCommentList(@Param("offset") int offset,
                                     @Param("size") int size,
                                     @Param("keyword") String keyword,
                                     @Param("postId") Long postId,
                                     @Param("status") Integer status);
    
    /**
     * 管理员获取评论总数（支持筛选）
     */
    int getAdminCommentCount(@Param("keyword") String keyword,
                            @Param("postId") Long postId,
                            @Param("status") Integer status);
    
    /**
     * 删除评论（管理员用）
     */
    void deleteComment(Long commentId);
}