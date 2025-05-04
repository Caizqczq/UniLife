package com.unilife.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子点赞数据访问层
 */
@Mapper
public interface PostLikeMapper {
    /**
     * 检查用户是否已点赞帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    Boolean isLiked(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * 添加点赞记录
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void insert(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * 删除点赞记录
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void delete(@Param("postId") Long postId, @Param("userId") Long userId);
    
    /**
     * 获取帖子点赞数
     * @param postId 帖子ID
     * @return 点赞数
     */
    Integer getCountByPostId(@Param("postId") Long postId);
    
    /**
     * 获取用户点赞的帖子ID列表
     * @param userId 用户ID
     * @return 帖子ID列表
     */
    java.util.List<Long> getLikedPostIdsByUserId(@Param("userId") Long userId);
}