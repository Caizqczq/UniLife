package com.unilife.controller;

import com.unilife.common.result.Result;
import com.unilife.model.dto.CreateCourseDTO;
import com.unilife.service.CourseService;
import com.unilife.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "课程管理")
@RestController
@RequestMapping("/courses")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "创建课程")
    @PostMapping
    public Result<?> createCourse(@RequestBody CreateCourseDTO createCourseDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.createCourse(userId, createCourseDTO);
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public Result<?> getCourseDetail(@PathVariable("id") Long courseId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.getCourseDetail(courseId, userId);
    }

    @Operation(summary = "获取用户的所有课程")
    @GetMapping
    public Result<?> getCourseList() {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.getCourseList(userId);
    }

    @Operation(summary = "获取用户在指定星期几的课程")
    @GetMapping("/day/{dayOfWeek}")
    public Result<?> getCourseListByDayOfWeek(@PathVariable("dayOfWeek") Byte dayOfWeek) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.getCourseListByDayOfWeek(userId, dayOfWeek);
    }

    @Operation(summary = "获取用户在指定学期的课程")
    @GetMapping("/semester/{semester}")
    public Result<?> getCourseListBySemester(@PathVariable("semester") String semester) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.getCourseListBySemester(userId, semester);
    }

    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public Result<?> updateCourse(
            @PathVariable("id") Long courseId,
            @RequestBody CreateCourseDTO createCourseDTO) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.updateCourse(courseId, userId, createCourseDTO);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<?> deleteCourse(@PathVariable("id") Long courseId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.deleteCourse(courseId, userId);
    }

    @Operation(summary = "检查课程时间冲突")
    @GetMapping("/check-conflict")
    public Result<?> checkCourseConflict(
            @RequestParam("dayOfWeek") Byte dayOfWeek,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam(value = "excludeCourseId", required = false) Long excludeCourseId) {
        // 从当前上下文获取用户ID
        Long userId = BaseContext.getId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return courseService.checkCourseConflict(userId, dayOfWeek, startTime, endTime, excludeCourseId);
    }
}