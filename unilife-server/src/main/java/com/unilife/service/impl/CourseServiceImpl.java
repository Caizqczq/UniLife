package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.unilife.common.result.Result;
import com.unilife.mapper.CourseMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateCourseDTO;
import com.unilife.model.entity.Course;
import com.unilife.model.entity.User;
import com.unilife.model.vo.CourseVO;
import com.unilife.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    @Transactional
    public Result createCourse(Long userId, CreateCourseDTO createCourseDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查课程时间冲突
        String startTimeStr = createCourseDTO.getStartTime().format(TIME_FORMATTER);
        String endTimeStr = createCourseDTO.getEndTime().format(TIME_FORMATTER);
        Integer conflictCount = courseMapper.checkConflict(userId, createCourseDTO.getDayOfWeek(), 
                startTimeStr, endTimeStr, null);
        if (conflictCount > 0) {
            return Result.error(400, "课程时间冲突，该时间段已有其他课程");
        }

        // 创建课程
        Course course = new Course();
        BeanUtil.copyProperties(createCourseDTO, course);
        course.setUserId(userId);
        course.setStatus((byte) 1);

        // 保存课程
        courseMapper.insert(course);

        Map<String, Object> data = new HashMap<>();
        data.put("courseId", course.getId());

        return Result.success(data, "创建课程成功");
    }

    @Override
    public Result getCourseDetail(Long courseId, Long userId) {
        // 获取课程
        Course course = courseMapper.getById(courseId);
        if (course == null) {
            return Result.error(404, "课程不存在");
        }

        // 检查权限（只能查看自己的课程）
        if (!course.getUserId().equals(userId)) {
            return Result.error(403, "无权限查看此课程");
        }

        // 转换为VO
        CourseVO courseVO = new CourseVO();
        BeanUtil.copyProperties(course, courseVO);

        return Result.success(courseVO);
    }

    @Override
    public Result getCourseList(Long userId) {
        // 获取用户的所有课程
        List<Course> courses = courseMapper.getListByUserId(userId);

        // 转换为VO
        List<CourseVO> courseVOs = courses.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtil.copyProperties(course, courseVO);
            return courseVO;
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", courseVOs.size());
        data.put("list", courseVOs);

        return Result.success(data);
    }

    @Override
    public Result getCourseListByDayOfWeek(Long userId, Byte dayOfWeek) {
        // 获取用户在指定星期几的课程
        List<Course> courses = courseMapper.getListByUserIdAndDayOfWeek(userId, dayOfWeek);

        // 转换为VO
        List<CourseVO> courseVOs = courses.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtil.copyProperties(course, courseVO);
            return courseVO;
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", courseVOs.size());
        data.put("list", courseVOs);

        return Result.success(data);
    }

    @Override
    @Transactional
    public Result updateCourse(Long courseId, Long userId, CreateCourseDTO createCourseDTO) {
        // 获取课程
        Course course = courseMapper.getById(courseId);
        if (course == null) {
            return Result.error(404, "课程不存在");
        }

        // 检查权限（只能更新自己的课程）
        if (!course.getUserId().equals(userId)) {
            return Result.error(403, "无权限更新此课程");
        }

        // 检查课程时间冲突
        String startTimeStr = createCourseDTO.getStartTime().format(TIME_FORMATTER);
        String endTimeStr = createCourseDTO.getEndTime().format(TIME_FORMATTER);
        Integer conflictCount = courseMapper.checkConflict(userId, createCourseDTO.getDayOfWeek(), 
                startTimeStr, endTimeStr, courseId);
        if (conflictCount > 0) {
            return Result.error(400, "课程时间冲突，该时间段已有其他课程");
        }

        // 更新课程
        BeanUtil.copyProperties(createCourseDTO, course);

        // 保存更新
        courseMapper.update(course);

        return Result.success(null, "更新课程成功");
    }

    @Override
    @Transactional
    public Result deleteCourse(Long courseId, Long userId) {
        // 获取课程
        Course course = courseMapper.getById(courseId);
        if (course == null) {
            return Result.error(404, "课程不存在");
        }

        // 检查权限（只能删除自己的课程）
        if (!course.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除此课程");
        }

        // 删除课程（逻辑删除）
        courseMapper.delete(courseId, userId);

        return Result.success(null, "删除课程成功");
    }

    @Override
    public Result checkCourseConflict(Long userId, Byte dayOfWeek, String startTime, String endTime, Long excludeCourseId) {
        // 检查课程时间冲突
        Integer conflictCount = courseMapper.checkConflict(userId, dayOfWeek, startTime, endTime, excludeCourseId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("hasConflict", conflictCount > 0);
        data.put("conflictCount", conflictCount);
        
        return Result.success(data);
    }
}