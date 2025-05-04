package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 创建课程的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseDTO {
    /**
     * 课程名称
     */
    private String name;
    
    /**
     * 教师姓名
     */
    private String teacher;
    
    /**
     * 上课地点
     */
    private String location;
    
    /**
     * 星期几（1-7）
     */
    private Byte dayOfWeek;
    
    /**
     * 开始时间
     */
    private LocalTime startTime;
    
    /**
     * 结束时间
     */
    private LocalTime endTime;
    
    /**
     * 开始周次
     */
    private Byte startWeek;
    
    /**
     * 结束周次
     */
    private Byte endWeek;
    
    /**
     * 显示颜色
     */
    private String color;
}