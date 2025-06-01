package com.unilife.mapper;

import com.unilife.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {
    void insert(User user);
    User findByEmail(String email);
    User findByUsername(String username);
    void updateLoginInfo(@Param("userId") Long userId,
                         @Param("ipLocation") String ipLocation,
                         @Param("loginTime") Date loginTime);
    void UpdateIPLocation(@Param("email") String email,@Param("loginIp") String loginIp);

    // 用户信息管理相关方法
    User getUserById(Long id);
    void updateUserProfile(User user);
    void updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatarUrl);
    void updateEmail(@Param("id") Long id, @Param("email") String email);
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    List<User> searchUsers(@Param("keyword") String keyword);
    
    /**
     * 软删除用户（设置status为0）
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 批量删除用户的帖子（软删除）
     * @param userId 用户ID
     */
    void deleteUserPosts(Long userId);
    
    /**
     * 批量删除用户的评论（软删除）
     * @param userId 用户ID
     */
    void deleteUserComments(Long userId);
    
    /**
     * 批量删除用户的资源（软删除）
     * @param userId 用户ID
     */
    void deleteUserResources(Long userId);
    
    /**
     * 批量删除用户的课程
     * @param userId 用户ID
     */
    void deleteUserCourses(Long userId);
    
    /**
     * 批量删除用户的日程
     * @param userId 用户ID
     */
    void deleteUserSchedules(Long userId);
    
    /**
     * 删除用户的所有点赞记录
     * @param userId 用户ID
     */
    void deleteUserLikes(Long userId);
    
    // ========== 管理员后台相关方法 ==========
    
    /**
     * 获取用户总数
     */
    int getTotalCount();
    
    /**
     * 获取活跃用户数（最近30天登录）
     */
    int getActiveUserCount();
    
    /**
     * 获取今日新增用户数
     */
    int getNewUserCountToday();
    
    /**
     * 管理员获取用户列表（支持筛选和分页）
     */
    List<User> getAdminUserList(@Param("offset") int offset, 
                               @Param("size") int size,
                               @Param("keyword") String keyword,
                               @Param("role") Integer role,
                               @Param("status") Integer status);
    
    /**
     * 管理员获取用户总数（支持筛选）
     */
    int getAdminUserCount(@Param("keyword") String keyword,
                         @Param("role") Integer role,
                         @Param("status") Integer status);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 更新用户角色
     */
    void updateUserRole(@Param("userId") Long userId, @Param("role") Integer role);
}
