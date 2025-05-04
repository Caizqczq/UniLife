package com.unilife.mapper;

import com.unilife.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类数据访问层
 */
@Mapper
public interface CategoryMapper {
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类实体
     */
    Category getById(Long id);
    
    /**
     * 获取所有分类
     * @param status 状态（可为null，表示获取所有状态）
     * @return 分类列表
     */
    List<Category> getList(@Param("status") Byte status);
    
    /**
     * 插入分类
     * @param category 分类实体
     */
    void insert(Category category);
    
    /**
     * 更新分类
     * @param category 分类实体
     */
    void update(Category category);
    
    /**
     * 删除分类（逻辑删除）
     * @param id 分类ID
     */
    void delete(Long id);
    
    /**
     * 获取分类总数
     * @param status 状态（可为null）
     * @return 分类总数
     */
    Integer getCount(@Param("status") Byte status);
}