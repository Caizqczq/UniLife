package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateResourceDTO;
import com.unilife.service.ResourceService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "资源管理")
@RestController
@RequestMapping("/resources")
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Operation(summary = "上传资源")
    @PostMapping
    public Result<?> uploadResource(@RequestParam("file") MultipartFile file, 
                                   @RequestParam("title") String title,
                                   @RequestParam("description") String description,
                                   @RequestParam("categoryId") Long categoryId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        // 创建DTO
        CreateResourceDTO createResourceDTO = new CreateResourceDTO();
        createResourceDTO.setTitle(title);
        createResourceDTO.setDescription(description);
        createResourceDTO.setCategoryId(categoryId);
        
        return resourceService.uploadResource(userId, createResourceDTO, file);
    }

    @Operation(summary = "获取资源详情")
    @GetMapping("/{id}")
    public Result<?> getResourceDetail(@PathVariable("id") Long resourceId) {
        // 从当前上下文获取用户ID，可能为null（未登录用户）
        Long userId = BaseContext.getId();
        return resourceService.getResourceDetail(resourceId, userId);
    }

    @Operation(summary = "获取资源列表")
    @GetMapping
    public Result<?> getResourceList(
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "user", required = false) Long uploaderUserId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        // 从当前上下文获取用户ID，可能为null（未登录用户）
        Long currentUserId = BaseContext.getId();
        return resourceService.getResourceList(categoryId, uploaderUserId, keyword, page, size, currentUserId);
    }

    @Operation(summary = "更新资源")
    @PutMapping("/{id}")
    public Result<?> updateResource(
            @PathVariable("id") Long resourceId,
            @RequestBody CreateResourceDTO createResourceDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return resourceService.updateResource(resourceId, userId, createResourceDTO);
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/{id}")
    public Result<?> deleteResource(@PathVariable("id") Long resourceId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return resourceService.deleteResource(resourceId, userId);
    }

    @Operation(summary = "下载资源")
    @GetMapping("/{id}/download")
    public Result<?> downloadResource(@PathVariable("id") Long resourceId) {
        // 从当前上下文获取用户ID，可能为null（未登录用户）
        Long userId = BaseContext.getId();
        return resourceService.downloadResource(resourceId, userId);
    }

    @Operation(summary = "点赞/取消点赞资源")
    @PostMapping("/{id}/like")
    public Result<?> likeResource(@PathVariable("id") Long resourceId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return resourceService.likeResource(resourceId, userId);
    }

    @Operation(summary = "获取用户上传的资源列表")
    @GetMapping("/user/{userId}")
    public Result<?> getUserResources(
            @PathVariable("userId") Long targetUserId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return resourceService.getUserResources(targetUserId, page, size);
    }

    @Operation(summary = "获取当前用户上传的资源列表")
    @GetMapping("/my")
    public Result<?> getMyResources(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return resourceService.getUserResources(userId, page, size);
    }
}