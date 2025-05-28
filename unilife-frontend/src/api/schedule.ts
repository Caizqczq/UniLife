import api from './index'
import type { ApiResponse } from '@/types'

// 课程类型定义
export interface Course {
  id: number
  userId: number
  name: string
  teacher: string
  location: string
  dayOfWeek: number
  startTime: string
  endTime: string
  startWeek: number
  endWeek: number
  semester: string
  color: string
  status: number
  createdAt: string
  updatedAt: string
}

// 日程类型定义
export interface Schedule {
  id: number
  userId: number
  title: string
  description: string
  startTime: string
  endTime: string
  location: string
  isAllDay: number
  reminder: number
  color: string
  status: number
  createdAt: string
  updatedAt: string
}

// 课程管理 API

// 创建课程
export const createCourse = (data: {
  name: string
  teacher: string
  location: string
  dayOfWeek: number
  startTime: string
  endTime: string
  startWeek: number
  endWeek: number
  semester: string
  color: string
}) => {
  return api.post<ApiResponse<{
    courseId: number
  }>>('/courses', data)
}

// 获取课程详情
export const getCourseDetail = (id: number) => {
  return api.get<ApiResponse<Course>>(`/courses/${id}`)
}

// 获取用户的所有课程
export const getCourses = () => {
  return api.get<ApiResponse<{
    total: number
    list: Course[]
  }>>('/courses')
}

// 获取用户在指定星期几的课程
export const getCoursesByDay = (dayOfWeek: number) => {
  return api.get<ApiResponse<{
    total: number
    list: Course[]
  }>>(`/courses/day/${dayOfWeek}`)
}

// 获取用户在指定学期的课程
export const getCoursesBySemester = (semester: string) => {
  return api.get<ApiResponse<{
    total: number
    list: Course[]
  }>>(`/courses/semester/${semester}`)
}

// 更新课程
export const updateCourse = (id: number, data: {
  name: string
  teacher: string
  location: string
  dayOfWeek: number
  startTime: string
  endTime: string
  startWeek: number
  endWeek: number
  color: string
}) => {
  return api.put<ApiResponse<null>>(`/courses/${id}`, data)
}

// 删除课程
export const deleteCourse = (id: number) => {
  return api.delete<ApiResponse<null>>(`/courses/${id}`)
}

// 检查课程时间冲突
export const checkCourseConflict = (params: {
  dayOfWeek: number
  startTime: string
  endTime: string
  excludeCourseId?: number
}) => {
  return api.get<ApiResponse<{
    hasConflict: boolean
    conflictCount: number
  }>>('/courses/check-conflict', { params })
}

// 日程管理 API

// 创建日程
export const createSchedule = (data: {
  title: string
  description: string
  startTime: string
  endTime: string
  location: string
  isAllDay: number
  reminder: number
  color: string
}) => {
  return api.post<ApiResponse<{
    scheduleId: number
  }>>('/schedules', data)
}

// 获取日程详情
export const getScheduleDetail = (id: number) => {
  return api.get<ApiResponse<Schedule>>(`/schedules/${id}`)
}

// 获取用户的所有日程
export const getSchedules = () => {
  return api.get<ApiResponse<{
    total: number
    list: Schedule[]
  }>>('/schedules')
}

// 获取用户在指定时间范围内的日程
export const getSchedulesByRange = (params: {
  startTime: string
  endTime: string
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Schedule[]
  }>>('/schedules/range', { params })
}

// 更新日程
export const updateSchedule = (id: number, data: {
  title: string
  description: string
  startTime: string
  endTime: string
  location: string
  isAllDay: number
  reminder: number
  color: string
}) => {
  return api.put<ApiResponse<null>>(`/schedules/${id}`, data)
}

// 删除日程
export const deleteSchedule = (id: number) => {
  return api.delete<ApiResponse<null>>(`/schedules/${id}`)
}

// 检查日程时间冲突
export const checkScheduleConflict = (params: {
  startTime: string
  endTime: string
  excludeScheduleId?: number
}) => {
  return api.get<ApiResponse<{
    hasConflict: boolean
    conflictCount: number
  }>>('/schedules/check-conflict', { params })
}

// 处理日程提醒
export const processReminders = () => {
  return api.post<ApiResponse<{
    total: number
    success: number
  }>>('/schedules/process-reminders')
} 