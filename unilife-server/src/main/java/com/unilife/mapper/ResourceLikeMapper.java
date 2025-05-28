package com.unilife.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 资源点赞数据访问层
 */
@Mapper
public interface ResourceLikeMapper {
    /**
     * 检查用户是否已点赞资源
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLiked(@Param("resourceId") Long resourceId, @Param("userId") Long userId);
    
    /**
     * 添加点赞记录
     * @param resourceId 资源ID
     * @param userId 用户ID
     */
    void insert(@Param("resourceId") Long resourceId, @Param("userId") Long userId);
    
    /**
     * 删除点赞记录
     * @param resourceId 资源ID
     * @param userId 用户ID
     */
    void delete(@Param("resourceId") Long resourceId, @Param("userId") Long userId);
    
    /**
     * 获取资源的点赞用户数量
     * @param resourceId 资源ID
     * @return 点赞数量
     */
    int getLikeCount(@Param("resourceId") Long resourceId);
} 