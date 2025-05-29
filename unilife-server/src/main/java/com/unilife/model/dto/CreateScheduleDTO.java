package com.unilife.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 创建日程的数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateScheduleDTO {
    /**
     * 日程标题
     */
    private String title;
    
    /**
     * 日程描述
     */
    private String description;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 是否全天（0-否, 1-是）
     */
    private Byte isAllDay;
    
    /**
     * 提醒时间（分钟）
     */
    private Integer reminder;
    
    /**
     * 显示颜色
     */
    private String color;
}