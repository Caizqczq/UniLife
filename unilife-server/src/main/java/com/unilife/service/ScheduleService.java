package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateScheduleDTO;

import java.time.LocalDateTime;

/**
 * 日程服务接口
 */
public interface ScheduleService {
    /**
     * 创建日程
     * @param userId 用户ID
     * @param createScheduleDTO 创建日程DTO
     * @return 结果
     */
    Result createSchedule(Long userId, CreateScheduleDTO createScheduleDTO);
    
    /**
     * 获取日程详情
     * @param scheduleId 日程ID
     * @param userId 用户ID
     * @return 结果
     */
    Result getScheduleDetail(Long scheduleId, Long userId);
    
    /**
     * 获取用户的所有日程
     * @param userId 用户ID
     * @return 结果
     */
    Result getScheduleList(Long userId);
    
    /**
     * 获取用户在指定时间范围内的日程
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    Result getScheduleListByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 更新日程
     * @param scheduleId 日程ID
     * @param userId 用户ID
     * @param createScheduleDTO 更新日程DTO
     * @return 结果
     */
    Result updateSchedule(Long scheduleId, Long userId, CreateScheduleDTO createScheduleDTO);
    
    /**
     * 删除日程
     * @param scheduleId 日程ID
     * @param userId 用户ID
     * @return 结果
     */
    Result deleteSchedule(Long scheduleId, Long userId);
    
    /**
     * 检查日程时间冲突
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeScheduleId 排除的日程ID（用于更新时排除自身）
     * @return 结果
     */
    Result checkScheduleConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId);
    
    /**
     * 处理日程提醒
     * 此方法由定时任务调用，用于发送日程提醒
     * @return 结果
     */
    Result processScheduleReminders();
}