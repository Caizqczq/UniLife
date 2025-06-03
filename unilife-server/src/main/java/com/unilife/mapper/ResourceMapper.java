package com.unilife.mapper;

import com.unilife.model.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源数据访问层
 */
@Mapper
public interface ResourceMapper {
    /**
     * 插入资源
     * @param resource 资源实体
     */
    void insert(Resource resource);
    
    /**
     * 根据ID获取资源
     * @param id 资源ID
     * @return 资源实体
     */
    Resource getById(Long id);
    
    /**
     * 获取资源列表
     * @param categoryId 分类ID，可为null
     * @param userId 用户ID，可为null
     * @param keyword 关键词，可为null
     * @return 资源列表
     */
    List<Resource> getList(@Param("categoryId") Long categoryId, 
                          @Param("userId") Long userId, 
                          @Param("keyword") String keyword);
    
    /**
     * 获取资源总数
     * @param categoryId 分类ID，可为null
     * @param userId 用户ID，可为null
     * @param keyword 关键词，可为null
     * @return 资源总数
     */
    Integer getCount(@Param("categoryId") Long categoryId, 
                    @Param("userId") Long userId, 
                    @Param("keyword") String keyword);
    
    /**
     * 更新资源
     * @param resource 资源实体
     */
    void update(Resource resource);
    
    /**
     * 删除资源（逻辑删除）
     * @param id 资源ID
     */
    void delete(Long id);
    
    /**
     * 增加下载次数
     * @param id 资源ID
     */
    void incrementDownloadCount(Long id);
    
    /**
     * 增加点赞次数
     * @param id 资源ID
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞次数
     * @param id 资源ID
     */
    void decrementLikeCount(Long id);
    
    /**
     * 获取用户上传的资源数量
     * @param userId 用户ID
     * @return 资源数量
     */
    Integer getCountByUserId(Long userId);
    
    /**
     * 获取分类下的资源数量
     * @param categoryId 分类ID
     * @return 资源数量
     */
    Integer getCountByCategoryId(Long categoryId);
    
    /**
     * 搜索资源
     * @param keyword 搜索关键词
     * @param categoryId 分类ID，可为null
     * @param sortBy 排序方式
     * @return 资源列表
     */
    List<Resource> searchResources(@Param("keyword") String keyword, 
                                  @Param("categoryId") Long categoryId, 
                                  @Param("sortBy") String sortBy);
    
    // ========== 管理员后台相关方法 ==========
    
    /**
     * 获取资源总数
     */
    int getTotalCount();
    
    /**
     * 获取今日新增资源数
     */
    int getNewResourceCountToday();
    
    /**
     * 根据ID获取资源（管理员用）
     */
    Resource getResourceById(Long id);
    
    /**
     * 管理员获取资源列表（支持筛选和分页）
     */
    List<Resource> getAdminResourceList(@Param("offset") int offset,
                                       @Param("size") int size,
                                       @Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId,
                                       @Param("status") Integer status);
    
    /**
     * 管理员获取资源总数（支持筛选）
     */
    int getAdminResourceCount(@Param("keyword") String keyword,
                             @Param("categoryId") Long categoryId,
                             @Param("status") Integer status);
    
    /**
     * 删除资源（管理员用）
     */
    void deleteResource(Long resourceId);
    
    /**
     * 物理删除资源（永久删除）
     */
    void permanentDeleteResource(Long resourceId);
}