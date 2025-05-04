package com.unilife.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.unilife.common.result.Result;
import com.unilife.mapper.ScheduleMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateScheduleDTO;
import com.unilife.model.entity.Schedule;
import com.unilife.model.entity.User;
import com.unilife.model.vo.ScheduleVO;
import com.unilife.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public Result createSchedule(Long userId, CreateScheduleDTO createScheduleDTO) {
        // 检查用户是否存在
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查日程时间冲突
        Integer conflictCount = scheduleMapper.checkConflict(userId, 
                createScheduleDTO.getStartTime(), createScheduleDTO.getEndTime(), null);
        if (conflictCount > 0) {
            return Result.error(400, "日程时间冲突，该时间段已有其他日程");
        }

        // 创建日程
        Schedule schedule = new Schedule();
        BeanUtil.copyProperties(createScheduleDTO, schedule);
        schedule.setUserId(userId);
        schedule.setStatus((byte) 1);

        // 保存日程
        scheduleMapper.insert(schedule);

        Map<String, Object> data = new HashMap<>();
        data.put("scheduleId", schedule.getId());

        return Result.success(data, "创建日程成功");
    }

    @Override
    public Result getScheduleDetail(Long scheduleId, Long userId) {
        // 获取日程
        Schedule schedule = scheduleMapper.getById(scheduleId);
        if (schedule == null) {
            return Result.error(404, "日程不存在");
        }

        // 检查权限（只能查看自己的日程）
        if (!schedule.getUserId().equals(userId)) {
            return Result.error(403, "无权限查看此日程");
        }

        // 转换为VO
        ScheduleVO scheduleVO = new ScheduleVO();
        BeanUtil.copyProperties(schedule, scheduleVO);

        return Result.success(scheduleVO);
    }

    @Override
    public Result getScheduleList(Long userId) {
        // 获取用户的所有日程
        List<Schedule> schedules = scheduleMapper.getListByUserId(userId);

        // 转换为VO
        List<ScheduleVO> scheduleVOs = schedules.stream().map(schedule -> {
            ScheduleVO scheduleVO = new ScheduleVO();
            BeanUtil.copyProperties(schedule, scheduleVO);
            return scheduleVO;
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", scheduleVOs.size());
        data.put("list", scheduleVOs);

        return Result.success(data);
    }

    @Override
    public Result getScheduleListByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 获取用户在指定时间范围内的日程
        List<Schedule> schedules = scheduleMapper.getListByUserIdAndTimeRange(userId, startTime, endTime);

        // 转换为VO
        List<ScheduleVO> scheduleVOs = schedules.stream().map(schedule -> {
            ScheduleVO scheduleVO = new ScheduleVO();
            BeanUtil.copyProperties(schedule, scheduleVO);
            return scheduleVO;
        }).collect(Collectors.toList());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", scheduleVOs.size());
        data.put("list", scheduleVOs);

        return Result.success(data);
    }

    @Override
    @Transactional
    public Result updateSchedule(Long scheduleId, Long userId, CreateScheduleDTO createScheduleDTO) {
        // 获取日程
        Schedule schedule = scheduleMapper.getById(scheduleId);
        if (schedule == null) {
            return Result.error(404, "日程不存在");
        }

        // 检查权限（只能更新自己的日程）
        if (!schedule.getUserId().equals(userId)) {
            return Result.error(403, "无权限更新此日程");
        }

        // 检查日程时间冲突
        Integer conflictCount = scheduleMapper.checkConflict(userId, 
                createScheduleDTO.getStartTime(), createScheduleDTO.getEndTime(), scheduleId);
        if (conflictCount > 0) {
            return Result.error(400, "日程时间冲突，该时间段已有其他日程");
        }

        // 更新日程
        BeanUtil.copyProperties(createScheduleDTO, schedule);

        // 保存更新
        scheduleMapper.update(schedule);

        return Result.success(null, "更新日程成功");
    }

    @Override
    @Transactional
    public Result deleteSchedule(Long scheduleId, Long userId) {
        // 获取日程
        Schedule schedule = scheduleMapper.getById(scheduleId);
        if (schedule == null) {
            return Result.error(404, "日程不存在");
        }

        // 检查权限（只能删除自己的日程）
        if (!schedule.getUserId().equals(userId)) {
            return Result.error(403, "无权限删除此日程");
        }

        // 删除日程（逻辑删除）
        scheduleMapper.delete(scheduleId, userId);

        return Result.success(null, "删除日程成功");
    }

    @Override
    public Result checkScheduleConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId) {
        // 检查日程时间冲突
        Integer conflictCount = scheduleMapper.checkConflict(userId, startTime, endTime, excludeScheduleId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("hasConflict", conflictCount > 0);
        data.put("conflictCount", conflictCount);
        
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result processScheduleReminders() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        
        // 获取需要提醒的日程（提醒时间在当前时间前后5分钟内）
        List<Schedule> schedulesToRemind = scheduleMapper.getSchedulesToRemind(now, 5);
        
        // 发送提醒邮件
        int successCount = 0;
        for (Schedule schedule : schedulesToRemind) {
            User user = userMapper.getUserById(schedule.getUserId());
            if (user != null && user.getEmail() != null) {
                boolean sent = sendReminderEmail(user, schedule);
                if (sent) {
                    successCount++;
                }
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", schedulesToRemind.size());
        data.put("success", successCount);
        
        return Result.success(data, "处理日程提醒完成");
    }
    
    /**
     * 发送日程提醒邮件
     * @param user 用户
     * @param schedule 日程
     * @return 是否发送成功
     */
    private boolean sendReminderEmail(User user, Schedule schedule) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(user.getEmail());
            helper.setSubject("UniLife - 日程提醒");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String startTimeStr = schedule.getStartTime().format(formatter);
            
            String content = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 5px;\">" +
                    "<h2 style=\"color: #333;\">日程提醒</h2>" +
                    "<p>您好，" + user.getNickname() + "！</p>" +
                    "<p>您有一个即将开始的日程：</p>" +
                    "<div style=\"background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 15px 0;\">" +
                    "<h3 style=\"margin-top: 0;\">" + schedule.getTitle() + "</h3>" +
                    "<p><strong>时间：</strong>" + startTimeStr + "</p>" +
                    (schedule.getLocation() != null ? "<p><strong>地点：</strong>" + schedule.getLocation() + "</p>" : "") +
                    (schedule.getDescription() != null ? "<p><strong>描述：</strong>" + schedule.getDescription() + "</p>" : "") +
                    "</div>" +
                    "<p>请合理安排您的时间。</p>" +
                    "<p style=\"margin-top: 30px; font-size: 12px; color: #888;\">" +
                    "这是一封自动生成的邮件，请勿直接回复。" +
                    "</p></div>";
            
            helper.setText(content, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            log.error("发送日程提醒邮件失败", e);
            return false;
        }
    }
}