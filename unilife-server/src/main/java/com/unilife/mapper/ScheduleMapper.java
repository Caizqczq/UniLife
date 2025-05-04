package com.unilife.mapper;

import com.unilife.model.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程数据访问层
 */
@Mapper
public interface ScheduleMapper {
    /**
     * 插入日程
     * @param schedule 日程实体
     */
    void insert(Schedule schedule);
    
    /**
     * 根据ID获取日程
     * @param id 日程ID
     * @return 日程实体
     */
    Schedule getById(Long id);
    
    /**
     * 获取用户的所有日程
     * @param userId 用户ID
     * @return 日程列表
     */
    List<Schedule> getListByUserId(Long userId);
    
    /**
     * 获取用户在指定时间范围内的日程
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日程列表
     */
    List<Schedule> getListByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 更新日程
     * @param schedule 日程实体
     */
    void update(Schedule schedule);
    
    /**
     * 删除日程（逻辑删除）
     * @param id 日程ID
     * @param userId 用户ID（确保只能删除自己的日程）
     */
    void delete(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 检查用户在指定时间段是否有日程冲突
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeScheduleId 排除的日程ID（用于更新时排除自身）
     * @return 冲突的日程数量
     */
    Integer checkConflict(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeScheduleId") Long excludeScheduleId);
    
    /**
     * 获取需要提醒的日程
     * @param currentTime 当前时间
     * @param reminderMinutes 提醒时间（分钟）
     * @return 需要提醒的日程列表
     */
    List<Schedule> getSchedulesToRemind(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("reminderMinutes") Integer reminderMinutes);
}