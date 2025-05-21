package com.unilife.mapper;

import com.unilife.model.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子数据访问层
 */
@Mapper
public interface PostMapper {
    /**
     * 插入帖子
     * @param post 帖子实体
     */
    void insert(Post post);
    
    /**
     * 根据ID获取帖子
     * @param id 帖子ID
     * @return 帖子实体
     */
    Post getById(Long id);

    /**
     * 按分类获取帖子列表
     * @param categoryId 分类ID，可以为null（表示获取所有分类）
     * @return 帖子列表
     */
    List<Post> getListByCategory(@Param("categoryId") Long categoryId,@Param("sort")String sort);
    
    /**
     * 获取帖子总数
     * @param categoryId 分类ID，可为null
     * @return 帖子总数
     */
    Integer getCount(@Param("categoryId") Long categoryId);
    
    /**
     * 更新帖子
     * @param post 帖子实体
     */
    void update(Post post);
    
    /**
     * 删除帖子（逻辑删除）
     * @param id 帖子ID
     */
    void delete(Long id);
    
    /**
     * 增加浏览次数
     * @param id 帖子ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加点赞次数
     * @param id 帖子ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞次数
     * @param id 帖子ID
     */
    void decrementLikeCount(Long id);
    
    /**
     * 增加评论次数
     * @param id 帖子ID
     */
    void incrementCommentCount(Long id);
    
    /**
     * 减少评论次数
     * @param id 帖子ID
     */
    void decrementCommentCount(Long id);
    
    /**
     * 获取指定用户的帖子列表
     * @param userId 用户ID
     * @param sort 排序方式
     * @return 帖子列表
     */
    List<Post> getListByUserId(@Param("userId") Long userId, @Param("sort") String sort);
    
    /**
     * 获取指定用户的帖子总数
     * @param userId 用户ID
     * @return 帖子总数
     */
    Integer getCountByUserId(@Param("userId") Long userId);
}