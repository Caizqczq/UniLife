package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.entity.Category;

/**
 * 分类服务接口
 */
public interface CategoryService {
    /**
     * 获取分类详情
     * @param categoryId 分类ID
     * @return 结果
     */
    Result getCategoryDetail(Long categoryId);
    
    /**
     * 获取分类列表
     * @param status 状态（可为null，表示获取所有状态）
     * @return 结果
     */
    Result getCategoryList(Byte status);
    
    /**
     * 创建分类
     * @param category 分类实体
     * @return 结果
     */
    Result createCategory(Category category);
    
    /**
     * 更新分类
     * @param categoryId 分类ID
     * @param category 分类实体
     * @return 结果
     */
    Result updateCategory(Long categoryId, Category category);
    
    /**
     * 删除分类
     * @param categoryId 分类ID
     * @return 结果
     */
    Result deleteCategory(Long categoryId);
}