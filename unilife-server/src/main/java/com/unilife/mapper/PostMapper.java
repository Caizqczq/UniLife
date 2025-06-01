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
    
    /**
     * 搜索帖子
     * @param keyword 搜索关键词
     * @param categoryId 分类ID，可为null
     * @param sortBy 排序方式
     * @return 帖子列表
     */
    List<Post> searchPosts(@Param("keyword") String keyword, 
                          @Param("categoryId") Long categoryId, 
                          @Param("sortBy") String sortBy);
    
    // ========== 管理员后台相关方法 ==========
    
    /**
     * 获取帖子总数
     */
    int getTotalCount();
    
    /**
     * 获取今日新增帖子数
     */
    int getNewPostCountToday();
    
    /**
     * 根据ID获取帖子（管理员用）
     */
    Post getPostById(Long id);
    
    /**
     * 管理员获取帖子列表（支持筛选和分页）
     */
    List<Post> getAdminPostList(@Param("offset") int offset,
                               @Param("size") int size,
                               @Param("keyword") String keyword,
                               @Param("categoryId") Long categoryId,
                               @Param("status") Integer status);
    
    /**
     * 管理员获取帖子总数（支持筛选）
     */
    int getAdminPostCount(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("status") Integer status);
    
    /**
     * 更新帖子状态
     */
    void updatePostStatus(@Param("postId") Long postId, @Param("status") Integer status);
    
    /**
     * 删除帖子（管理员用）
     */
    void deletePost(Long postId);
    
    /**
     * 获取指定分类下的帖子数量
     */
    int getCountByCategoryId(Long categoryId);
}