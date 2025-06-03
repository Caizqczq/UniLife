package com.unilife.mapper;

import com.unilife.model.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程数据访问层
 */
@Mapper
public interface CourseMapper {
    /**
     * 插入课程
     * @param course 课程实体
     */
    void insert(Course course);
    
    /**
     * 根据ID获取课程
     * @param id 课程ID
     * @return 课程实体
     */
    Course getById(Long id);
    
    /**
     * 获取用户的所有课程
     * @param userId 用户ID
     * @return 课程列表
     */
    List<Course> getListByUserId(Long userId);
    
    /**
     * 更新课程
     * @param course 课程实体
     */
    void update(Course course);
    
    /**
     * 删除课程（逻辑删除）
     * @param id 课程ID
     * @param userId 用户ID（确保只能删除自己的课程）
     */
    void delete(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 获取用户在指定星期几的课程
     * @param userId 用户ID
     * @param dayOfWeek 星期几（1-7）
     * @return 课程列表
     */
    List<Course> getListByUserIdAndDayOfWeek(@Param("userId") Long userId, @Param("dayOfWeek") Byte dayOfWeek);
    
    /**
     * 获取用户在指定学期的课程
     * @param userId 用户ID
     * @param semester 学期（如：2023-1）
     * @return 课程列表
     */
    List<Course> getListByUserIdAndSemester(@Param("userId") Long userId, @Param("semester") String semester);
    
    /**
     * 检查用户在指定时间段是否有课程冲突
     * @param userId 用户ID
     * @param dayOfWeek 星期几（1-7）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeCourseId 排除的课程ID（用于更新时排除自身）
     * @return 冲突的课程数量
     */
    Integer checkConflict(@Param("userId") Long userId, 
                         @Param("dayOfWeek") Byte dayOfWeek, 
                         @Param("startTime") String startTime, 
                         @Param("endTime") String endTime,
                         @Param("excludeCourseId") Long excludeCourseId);
    
    // ========== 管理员后台相关方法 ==========
    
    /**
     * 获取课程总数
     */
    int getTotalCount();
    
    /**
     * 获取今日新增课程数
     */
    int getNewCourseCountToday();
    
    /**
     * 根据ID获取课程（管理员用）
     */
    Course getCourseById(Long id);
    
    /**
     * 管理员获取课程列表（支持筛选和分页）
     */
    List<Course> getAdminCourseList(@Param("offset") int offset,
                                   @Param("size") int size,
                                   @Param("keyword") String keyword,
                                   @Param("userId") Long userId,
                                   @Param("semester") String semester,
                                   @Param("status") Integer status);
    
    /**
     * 管理员获取课程总数（支持筛选）
     */
    int getAdminCourseCount(@Param("keyword") String keyword,
                           @Param("userId") Long userId,
                           @Param("semester") String semester,
                           @Param("status") Integer status);
    
    /**
     * 管理员删除课程
     */
    void deleteCourse(Long courseId);
    
    /**
     * 物理删除课程（永久删除）
     */
    void permanentDeleteCourse(Long courseId);
}