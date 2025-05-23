package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateCourseDTO;

/**
 * 课程服务接口
 */
public interface CourseService {
    /**
     * 创建课程
     * @param userId 用户ID
     * @param createCourseDTO 创建课程DTO
     * @return 结果
     */
    Result createCourse(Long userId, CreateCourseDTO createCourseDTO);
    
    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 结果
     */
    Result getCourseDetail(Long courseId, Long userId);
    
    /**
     * 获取用户的所有课程
     * @param userId 用户ID
     * @return 结果
     */
    Result getCourseList(Long userId);
    
    /**
     * 获取用户在指定星期几的课程
     * @param userId 用户ID
     * @param dayOfWeek 星期几（1-7）
     * @return 结果
     */
    Result getCourseListByDayOfWeek(Long userId, Byte dayOfWeek);
    
    /**
     * 获取用户在指定学期的课程
     * @param userId 用户ID
     * @param semester 学期（如：2023-1）
     * @return 结果
     */
    Result getCourseListBySemester(Long userId, String semester);
    
    /**
     * 更新课程
     * @param courseId 课程ID
     * @param userId 用户ID
     * @param createCourseDTO 更新课程DTO
     * @return 结果
     */
    Result updateCourse(Long courseId, Long userId, CreateCourseDTO createCourseDTO);
    
    /**
     * 删除课程
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 结果
     */
    Result deleteCourse(Long courseId, Long userId);
    
    /**
     * 检查课程时间冲突
     * @param userId 用户ID
     * @param dayOfWeek 星期几（1-7）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeCourseId 排除的课程ID（用于更新时排除自身）
     * @return 结果
     */
    Result checkCourseConflict(Long userId, Byte dayOfWeek, String startTime, String endTime, Long excludeCourseId);
}