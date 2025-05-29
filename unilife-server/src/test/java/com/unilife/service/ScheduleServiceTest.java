package com.unilife.service;

import com.unilife.common.result.Result;
import com.unilife.mapper.ScheduleMapper;
import com.unilife.mapper.UserMapper;
import com.unilife.model.dto.CreateScheduleDTO;
import com.unilife.model.entity.Schedule;
import com.unilife.model.entity.User;
import com.unilife.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ScheduleServiceTest {

    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private User testUser;
    private Schedule testSchedule;
    private CreateScheduleDTO createScheduleDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setNickname("测试用户");
        testUser.setAvatar("avatar.jpg");

        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setTitle("测试课程");
        testSchedule.setDescription("测试课程描述");
        testSchedule.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        testSchedule.setEndTime(LocalDateTime.of(2024, 1, 15, 10, 30));
        testSchedule.setLocation("教学楼A101");
        testSchedule.setType("COURSE");
        testSchedule.setRepeatType("WEEKLY");
        testSchedule.setRepeatEnd(LocalDateTime.of(2024, 6, 15, 10, 30));
        testSchedule.setUserId(1L);
        testSchedule.setCreatedAt(LocalDateTime.now());
        testSchedule.setUpdatedAt(LocalDateTime.now());

        createScheduleDTO = new CreateScheduleDTO();
        createScheduleDTO.setTitle("新课程");
        createScheduleDTO.setDescription("新课程描述");
        createScheduleDTO.setStartTime(LocalDateTime.of(2024, 1, 16, 14, 0));
        createScheduleDTO.setEndTime(LocalDateTime.of(2024, 1, 16, 15, 30));
        createScheduleDTO.setLocation("教学楼B201");
        createScheduleDTO.setType("COURSE");
        createScheduleDTO.setRepeatType("WEEKLY");
        createScheduleDTO.setRepeatEnd(LocalDateTime.of(2024, 6, 16, 15, 30));
    }

    @Test
    void testCreateSchedule_Success() {
        // Mock 依赖方法
        when(userMapper.findById(1L)).thenReturn(testUser);
        when(scheduleMapper.findConflictingSchedules(eq(1L), any(), any(), any())).thenReturn(Arrays.asList());
        when(scheduleMapper.insert(any(Schedule.class))).thenReturn(1);

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("日程创建成功", result.getMessage());

        // 验证方法调用
        verify(userMapper).findById(1L);
        verify(scheduleMapper).findConflictingSchedules(eq(1L), any(), any(), any());
        verify(scheduleMapper).insert(any(Schedule.class));
    }

    @Test
    void testCreateSchedule_UserNotFound() {
        // Mock 用户不存在
        when(userMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("用户不存在", result.getMessage());

        // 验证不会尝试创建日程
        verify(scheduleMapper, never()).insert(any(Schedule.class));
    }

    @Test
    void testCreateSchedule_TimeConflict() {
        // Mock 时间冲突
        Schedule conflictingSchedule = new Schedule();
        conflictingSchedule.setId(2L);
        conflictingSchedule.setTitle("冲突课程");
        conflictingSchedule.setStartTime(LocalDateTime.of(2024, 1, 16, 14, 30));
        conflictingSchedule.setEndTime(LocalDateTime.of(2024, 1, 16, 16, 0));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(scheduleMapper.findConflictingSchedules(eq(1L), any(), any(), any()))
            .thenReturn(Arrays.asList(conflictingSchedule));

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertTrue(result.getMessage().contains("时间冲突"));
    }

    @Test
    void testCreateSchedule_InvalidTimeRange() {
        // 测试结束时间早于开始时间
        createScheduleDTO.setStartTime(LocalDateTime.of(2024, 1, 16, 16, 0));
        createScheduleDTO.setEndTime(LocalDateTime.of(2024, 1, 16, 14, 0));

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertEquals("结束时间不能早于开始时间", result.getMessage());
    }

    @Test
    void testGetScheduleDetail_Success() {
        // Mock 依赖方法
        when(scheduleMapper.findById(1L)).thenReturn(testSchedule);

        // 执行测试
        Result<?> result = scheduleService.getScheduleDetail(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testGetScheduleDetail_NotFound() {
        // Mock 日程不存在
        when(scheduleMapper.findById(1L)).thenReturn(null);

        // 执行测试
        Result<?> result = scheduleService.getScheduleDetail(1L, 1L);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("日程不存在", result.getMessage());
    }

    @Test
    void testGetScheduleDetail_Unauthorized() {
        // Mock 其他用户的日程
        testSchedule.setUserId(2L);
        when(scheduleMapper.findById(1L)).thenReturn(testSchedule);

        // 执行测试
        Result<?> result = scheduleService.getScheduleDetail(1L, 1L);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        assertEquals("无权限查看此日程", result.getMessage());
    }

    @Test
    void testGetScheduleList_Success() {
        // Mock 日程列表
        List<Schedule> schedules = Arrays.asList(testSchedule);
        when(scheduleMapper.findByUserId(1L)).thenReturn(schedules);

        // 执行测试
        Result<?> result = scheduleService.getScheduleList(1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        // 验证方法调用
        verify(scheduleMapper).findByUserId(1L);
    }

    @Test
    void testGetScheduleListByTimeRange_Success() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        // Mock 时间范围内的日程列表
        List<Schedule> schedules = Arrays.asList(testSchedule);
        when(scheduleMapper.findByUserIdAndTimeRange(1L, startTime, endTime)).thenReturn(schedules);

        // 执行测试
        Result<?> result = scheduleService.getScheduleListByTimeRange(1L, startTime, endTime);

        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        // 验证方法调用
        verify(scheduleMapper).findByUserIdAndTimeRange(1L, startTime, endTime);
    }

    @Test
    void testUpdateSchedule_Success() {
        // Mock 依赖方法
        when(scheduleMapper.findById(1L)).thenReturn(testSchedule);
        when(scheduleMapper.findConflictingSchedules(eq(1L), any(), any(), eq(1L))).thenReturn(Arrays.asList());
        when(scheduleMapper.update(any(Schedule.class))).thenReturn(1);

        // 执行测试
        Result<?> result = scheduleService.updateSchedule(1L, 1L, createScheduleDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("日程更新成功", result.getMessage());

        // 验证方法调用
        verify(scheduleMapper).update(any(Schedule.class));
    }

    @Test
    void testUpdateSchedule_Unauthorized() {
        // Mock 其他用户的日程
        testSchedule.setUserId(2L);
        when(scheduleMapper.findById(1L)).thenReturn(testSchedule);

        // 执行测试
        Result<?> result = scheduleService.updateSchedule(1L, 1L, createScheduleDTO);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        assertEquals("无权限修改此日程", result.getMessage());
    }

    @Test
    void testDeleteSchedule_Success() {
        // Mock 依赖方法
        when(scheduleMapper.findById(1L)).thenReturn(testSchedule);
        when(scheduleMapper.delete(1L)).thenReturn(1);

        // 执行测试
        Result<?> result = scheduleService.deleteSchedule(1L, 1L);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("日程删除成功", result.getMessage());

        // 验证方法调用
        verify(scheduleMapper).delete(1L);
    }

    @Test
    void testCheckScheduleConflict_NoConflict() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 15, 30);
        
        // Mock 无冲突
        when(scheduleMapper.findConflictingSchedules(eq(1L), eq(startTime), eq(endTime), any()))
            .thenReturn(Arrays.asList());

        // 执行测试
        Result<?> result = scheduleService.checkScheduleConflict(1L, startTime, endTime, null);

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("无时间冲突", result.getMessage());
    }

    @Test
    void testCheckScheduleConflict_HasConflict() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 15, 30);
        
        // Mock 有冲突
        when(scheduleMapper.findConflictingSchedules(eq(1L), eq(startTime), eq(endTime), any()))
            .thenReturn(Arrays.asList(testSchedule));

        // 执行测试
        Result<?> result = scheduleService.checkScheduleConflict(1L, startTime, endTime, null);

        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
        assertTrue(result.getMessage().contains("时间冲突"));
    }

    @Test
    void testProcessScheduleReminders_Success() {
        // Mock 需要提醒的日程
        List<Schedule> upcomingSchedules = Arrays.asList(testSchedule);
        when(scheduleMapper.findUpcomingSchedules(any())).thenReturn(upcomingSchedules);

        // 执行测试
        Result<?> result = scheduleService.processScheduleReminders();

        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals("提醒处理完成", result.getMessage());

        // 验证方法调用
        verify(scheduleMapper).findUpcomingSchedules(any());
    }

    @Test
    void testCreateSchedule_WeeklyRepeat() {
        // 测试周重复日程
        createScheduleDTO.setRepeatType("WEEKLY");
        createScheduleDTO.setRepeatEnd(LocalDateTime.of(2024, 3, 16, 15, 30));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(scheduleMapper.findConflictingSchedules(eq(1L), any(), any(), any())).thenReturn(Arrays.asList());
        when(scheduleMapper.insert(any(Schedule.class))).thenReturn(1);

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        
        // 验证会创建多个重复的日程实例
        verify(scheduleMapper, atLeast(1)).insert(any(Schedule.class));
    }

    @Test
    void testCreateSchedule_DailyRepeat() {
        // 测试日重复日程
        createScheduleDTO.setRepeatType("DAILY");
        createScheduleDTO.setRepeatEnd(LocalDateTime.of(2024, 1, 20, 15, 30));

        when(userMapper.findById(1L)).thenReturn(testUser);
        when(scheduleMapper.findConflictingSchedules(eq(1L), any(), any(), any())).thenReturn(Arrays.asList());
        when(scheduleMapper.insert(any(Schedule.class))).thenReturn(1);

        // 执行测试
        Result<?> result = scheduleService.createSchedule(1L, createScheduleDTO);

        // 验证结果
        assertTrue(result.isSuccess());
        
        // 验证会创建多个重复的日程实例
        verify(scheduleMapper, atLeast(1)).insert(any(Schedule.class));
    }
} 