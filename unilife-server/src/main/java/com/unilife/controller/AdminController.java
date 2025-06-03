package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员接口", description = "后台管理相关接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "获取系统统计数据")
    @GetMapping("/stats")
    public Result getSystemStats() {
        return adminService.getSystemStats();
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/users")
    public Result getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        return adminService.getUserList(page, size, keyword, role, status);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/users/{userId}/status")
    public Result updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return adminService.updateUserStatus(userId, status);
    }

    @Operation(summary = "更新用户角色")
    @PutMapping("/users/{userId}/role")
    public Result updateUserRole(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        Integer role = request.get("role");
        return adminService.updateUserRole(userId, role);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/users/{userId}")
    public Result deleteUser(@PathVariable Long userId) {
        return adminService.deleteUser(userId);
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/posts")
    public Result getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return adminService.getPostList(page, size, keyword, categoryId, status);
    }

    @Operation(summary = "更新帖子状态")
    @PutMapping("/posts/{postId}/status")
    public Result updatePostStatus(@PathVariable Long postId, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return adminService.updatePostStatus(postId, status);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/posts/{postId}")
    public Result deletePost(@PathVariable Long postId) {
        return adminService.deletePost(postId);
    }

    @Operation(summary = "永久删除帖子")
    @DeleteMapping("/posts/{postId}/permanent")
    public Result permanentDeletePost(@PathVariable Long postId) {
        return adminService.permanentDeletePost(postId);
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/comments")
    public Result getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Integer status) {
        return adminService.getCommentList(page, size, keyword, postId, status);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comments/{commentId}")
    public Result deleteComment(@PathVariable Long commentId) {
        return adminService.deleteComment(commentId);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/categories")
    public Result getCategoryList() {
        return adminService.getCategoryList();
    }

    @Operation(summary = "创建分类")
    @PostMapping("/categories")
    public Result createCategory(@RequestBody Map<String, Object> request) {
        return adminService.createCategory(request);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/categories/{categoryId}")
    public Result updateCategory(@PathVariable Long categoryId, @RequestBody Map<String, Object> request) {
        return adminService.updateCategory(categoryId, request);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/categories/{categoryId}")
    public Result deleteCategory(@PathVariable Long categoryId) {
        return adminService.deleteCategory(categoryId);
    }

    @Operation(summary = "获取资源列表")
    @GetMapping("/resources")
    public Result getResourceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        return adminService.getResourceList(page, size, keyword, categoryId, status);
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/resources/{resourceId}")
    public Result deleteResource(@PathVariable Long resourceId) {
        return adminService.deleteResource(resourceId);
    }

    @Operation(summary = "获取系统监控信息")
    @GetMapping("/monitor/status")
    public Result getSystemStatus() {
        return adminService.getSystemStatus();
    }

    @Operation(summary = "获取系统日志")
    @GetMapping("/logs")
    public Result getSystemLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return adminService.getSystemLogs(page, size, level, keyword, startDate, endDate);
    }

    @Operation(summary = "获取系统设置")
    @GetMapping("/settings")
    public Result getSystemSettings() {
        return adminService.getSystemSettings();
    }

    @Operation(summary = "更新系统设置")
    @PostMapping("/settings")
    public Result updateSystemSettings(@RequestBody Map<String, Object> settings) {
        return adminService.updateSystemSettings(settings);
    }

    @Operation(summary = "获取系统公告列表")
    @GetMapping("/announcements")
    public Result getAnnouncements() {
        return adminService.getAnnouncements();
    }

    @Operation(summary = "创建系统公告")
    @PostMapping("/announcements")
    public Result createAnnouncement(@RequestBody Map<String, Object> announcement) {
        return adminService.createAnnouncement(announcement);
    }

    @Operation(summary = "更新系统公告")
    @PutMapping("/announcements/{id}")
    public Result updateAnnouncement(@PathVariable Long id, @RequestBody Map<String, Object> announcement) {
        return adminService.updateAnnouncement(id, announcement);
    }

    @Operation(summary = "删除系统公告")
    @DeleteMapping("/announcements/{id}")
    public Result deleteAnnouncement(@PathVariable Long id) {
        return adminService.deleteAnnouncement(id);
    }

    @Operation(summary = "获取系统通知")
    @GetMapping("/notifications")
    public Result getNotifications() {
        return adminService.getNotifications();
    }

    @Operation(summary = "标记通知已读")
    @PostMapping("/notifications/{id}/read")
    public Result markNotificationAsRead(@PathVariable Long id) {
        return adminService.markNotificationAsRead(id);
    }

    @Operation(summary = "测试邮件发送")
    @PostMapping("/settings/email/test")
    public Result testEmail(@RequestBody Map<String, String> request) {
        return adminService.testEmail(request);
    }

    @Operation(summary = "获取数据统计")
    @GetMapping("/statistics")
    public Result getStatistics() {
        return adminService.getStatistics();
    }

    @Operation(summary = "数据备份")
    @PostMapping("/backup")
    public Result backupData() {
        return adminService.backupData();
    }

    // ========== 课表管理相关接口 ==========

    @Operation(summary = "获取课程列表")
    @GetMapping("/courses")
    public Result getCourseList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer status) {
        return adminService.getCourseList(page, size, keyword, userId, semester, status);
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/courses/{courseId}")
    public Result getCourseDetail(@PathVariable Long courseId) {
        return adminService.getCourseDetail(courseId);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/courses/{courseId}")
    public Result deleteCourse(@PathVariable Long courseId) {
        return adminService.deleteCourse(courseId);
    }

    @Operation(summary = "获取用户课表")
    @GetMapping("/users/{userId}/schedule")
    public Result getUserSchedule(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "2024-2025-2") String semester) {
        return adminService.getUserSchedule(userId, semester);
    }
} 