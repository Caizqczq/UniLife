package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateScheduleDTO;
import com.unilife.service.ScheduleService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "日程管理")
@RestController
@RequestMapping("/schedules")
@Slf4j
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(summary = "创建日程")
    @PostMapping
    public Result<?> createSchedule(@RequestBody CreateScheduleDTO createScheduleDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.createSchedule(userId, createScheduleDTO);
    }

    @Operation(summary = "获取日程详情")
    @GetMapping("/{id}")
    public Result<?> getScheduleDetail(@PathVariable("id") Long scheduleId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.getScheduleDetail(scheduleId, userId);
    }

    @Operation(summary = "获取用户的所有日程")
    @GetMapping
    public Result<?> getScheduleList() {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.getScheduleList(userId);
    }

    @Operation(summary = "获取用户在指定时间范围内的日程")
    @GetMapping("/range")
    public Result<?> getScheduleListByTimeRange(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.getScheduleListByTimeRange(userId, startTime, endTime);
    }

    @Operation(summary = "更新日程")
    @PutMapping("/{id}")
    public Result<?> updateSchedule(
            @PathVariable("id") Long scheduleId,
            @RequestBody CreateScheduleDTO createScheduleDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.updateSchedule(scheduleId, userId, createScheduleDTO);
    }

    @Operation(summary = "删除日程")
    @DeleteMapping("/{id}")
    public Result<?> deleteSchedule(@PathVariable("id") Long scheduleId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.deleteSchedule(scheduleId, userId);
    }

    @Operation(summary = "检查日程时间冲突")
    @GetMapping("/check-conflict")
    public Result<?> checkScheduleConflict(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(value = "excludeScheduleId", required = false) Long excludeScheduleId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return scheduleService.checkScheduleConflict(userId, startTime, endTime, excludeScheduleId);
    }

    @Operation(summary = "处理日程提醒")
    @PostMapping("/process-reminders")
    public Result<?> processScheduleReminders() {
        // 此接口通常由系统定时任务调用，不需要用户认证
        // 实际项目中应该添加适当的安全措施，如API密钥验证
        return scheduleService.processScheduleReminders();
    }
}