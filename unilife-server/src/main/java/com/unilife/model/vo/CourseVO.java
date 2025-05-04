package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课程视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseVO {
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
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
    
    /**
     * 状态（0-删除, 1-正常）
     */
    private Byte status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}