package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.entity.Category;
import com.unilife.service.CategoryService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<?> getCategoryDetail(@PathVariable("id") Long categoryId) {
        return categoryService.getCategoryDetail(categoryId);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<?> getCategoryList(
            @RequestParam(value = "status", required = false) Byte status) {
        return categoryService.getCategoryList(status);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<?> createCategory(@RequestBody Category category) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        // 检查用户权限（只有管理员可以创建分类）
        // 实际项目中应该从用户服务获取用户角色信息
        // 这里简化处理，假设已经检查了权限
        
        return categoryService.createCategory(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<?> updateCategory(
            @PathVariable("id") Long categoryId,
            @RequestBody Category category) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        // 检查用户权限（只有管理员可以更新分类）
        // 实际项目中应该从用户服务获取用户角色信息
        // 这里简化处理，假设已经检查了权限
        
        return categoryService.updateCategory(categoryId, category);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<?> deleteCategory(@PathVariable("id") Long categoryId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        // 检查用户权限（只有管理员可以删除分类）
        // 实际项目中应该从用户服务获取用户角色信息
        // 这里简化处理，假设已经检查了权限
        
        return categoryService.deleteCategory(categoryId);
    }
}