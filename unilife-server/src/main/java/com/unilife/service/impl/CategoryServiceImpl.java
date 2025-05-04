package com.unilife.service.impl;

import com.unilife.common.result.Result;
import com.unilife.mapper.CategoryMapper;
import com.unilife.model.entity.Category;
import com.unilife.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<?> getCategoryDetail(Long categoryId) {
        // 获取分类
        Category category = categoryMapper.getById(categoryId);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }

        return Result.success(category);
    }

    @Override
    public Result<?> getCategoryList(Byte status) {
        // 获取分类列表
        List<Category> categories = categoryMapper.getList(status);
        
        // 获取分类总数
        Integer count = categoryMapper.getCount(status);
        
        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", count);
        data.put("list", categories);
        
        return Result.success(data);
    }

    @Override
    public Result<?> createCategory(Category category) {
        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus((byte) 1);
        }
        
        // 保存分类
        categoryMapper.insert(category);
        
        Map<String, Object> data = new HashMap<>();
        data.put("categoryId", category.getId());
        
        return Result.success(data, "创建成功");
    }

    @Override
    public Result updateCategory(Long categoryId, Category category) {
        // 获取分类
        Category existingCategory = categoryMapper.getById(categoryId);
        if (existingCategory == null) {
            return Result.error(404, "分类不存在");
        }
        
        // 更新分类
        category.setId(categoryId);
        categoryMapper.update(category);
        
        return Result.success(null, "更新成功");
    }

    @Override
    public Result deleteCategory(Long categoryId) {
        // 获取分类
        Category category = categoryMapper.getById(categoryId);
        if (category == null) {
            return Result.error(404, "分类不存在");
        }
        
        // 删除分类（逻辑删除）
        categoryMapper.delete(categoryId);
        
        return Result.success(null, "删除成功");
    }
}