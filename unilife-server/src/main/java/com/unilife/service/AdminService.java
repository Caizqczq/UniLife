package com.unilife.service;

import com.unilife.common.result.Result;

import java.util.Map;

public interface AdminService {
    
    /**
     * 获取系统统计数据
     */
    Result getSystemStats();
    
    /**
     * 获取用户列表
     */
    Result getUserList(Integer page, Integer size, String keyword, Integer role, Integer status);
    
    /**
     * 更新用户状态
     */
    Result updateUserStatus(Long userId, Integer status);
    
    /**
     * 更新用户角色
     */
    Result updateUserRole(Long userId, Integer role);
    
    /**
     * 删除用户
     */
    Result deleteUser(Long userId);
    
    /**
     * 获取帖子列表
     */
    Result getPostList(Integer page, Integer size, String keyword, Long categoryId, Integer status);
    
    /**
     * 更新帖子状态
     */
    Result updatePostStatus(Long postId, Integer status);
    
    /**
     * 删除帖子
     */
    Result deletePost(Long postId);
    
    /**
     * 永久删除帖子
     */
    Result permanentDeletePost(Long postId);
    
    /**
     * 获取评论列表
     */
    Result getCommentList(Integer page, Integer size, String keyword, Long postId, Integer status);
    
    /**
     * 删除评论
     */
    Result deleteComment(Long commentId);
    
    /**
     * 获取分类列表
     */
    Result getCategoryList();
    
    /**
     * 创建分类
     */
    Result createCategory(Map<String, Object> request);
    
    /**
     * 更新分类
     */
    Result updateCategory(Long categoryId, Map<String, Object> request);
    
    /**
     * 删除分类
     */
    Result deleteCategory(Long categoryId);
    
    /**
     * 获取资源列表
     */
    Result getResourceList(Integer page, Integer size, String keyword, Long categoryId, Integer status);
    
    /**
     * 删除资源
     */
    Result deleteResource(Long resourceId);
    
    /**
     * 获取系统监控信息
     */
    Result getSystemStatus();
    
    /**
     * 获取系统日志
     */
    Result getSystemLogs(Integer page, Integer size, String level, String keyword, String startDate, String endDate);
    
    /**
     * 获取系统设置
     */
    Result getSystemSettings();
    
    /**
     * 更新系统设置
     */
    Result updateSystemSettings(Map<String, Object> settings);
    
    /**
     * 获取系统公告列表
     */
    Result getAnnouncements();
    
    /**
     * 创建系统公告
     */
    Result createAnnouncement(Map<String, Object> announcement);
    
    /**
     * 更新系统公告
     */
    Result updateAnnouncement(Long id, Map<String, Object> announcement);
    
    /**
     * 删除系统公告
     */
    Result deleteAnnouncement(Long id);
    
    /**
     * 获取系统通知
     */
    Result getNotifications();
    
    /**
     * 标记通知已读
     */
    Result markNotificationAsRead(Long id);
    
    /**
     * 测试邮件发送
     */
    Result testEmail(Map<String, String> request);
    
    /**
     * 获取数据统计
     */
    Result getStatistics();
    
    /**
     * 数据备份
     */
    Result backupData();
    
    // ========== 课表管理相关方法 ==========
    
    /**
     * 获取课程列表
     */
    Result getCourseList(Integer page, Integer size, String keyword, Long userId, String semester, Integer status);
    
    /**
     * 获取课程详情
     */
    Result getCourseDetail(Long courseId);
    
    /**
     * 删除课程
     */
    Result deleteCourse(Long courseId);
    
    /**
     * 获取用户的课表
     */
    Result getUserSchedule(Long userId, String semester);
} 