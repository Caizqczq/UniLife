package com.unilife.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日程视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleVO {
    /**
     * 日程ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
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