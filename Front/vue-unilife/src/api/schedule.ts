import { get, post, put, del } from './request';

// 课程类型定义
export interface CourseItem {
  id: number;
  userId: number;
  name: string;
  teacher?: string;
  location?: string;
  dayOfWeek: number; // 1-7 表示周一到周日
  startTime: string; // 格式: "08:00:00"
  endTime: string;   // 格式: "09:40:00"
  startWeek: number; // 开始周次
  endWeek: number;   // 结束周次
  color?: string;    // 颜色，如 "#4CAF50"
  status: number;    // 状态（0-删除, 1-正常）
  createdAt: string;
  updatedAt: string;
}

// 日程类型定义
export interface ScheduleItem {
  id: number;
  userId: number;
  title: string;
  description?: string;
  startTime: string; // 格式: "2023-05-10T14:00:00"
  endTime: string;   // 格式: "2023-05-10T16:00:00"
  location?: string;
  isAllDay: number;  // 是否全天（0-否, 1-是）
  reminder?: number; // 提醒时间（分钟）
  color?: string;    // 颜色，如 "#FF5722"
  status: number;    // 状态（0-删除, 1-正常）
  createdAt: string;
  updatedAt: string;
}

export interface CreateCourseParams {
  name: string;
  teacher?: string;
  location?: string;
  dayOfWeek: number;
  startTime: string;
  endTime: string;
  startWeek: number;
  endWeek: number;
  color?: string;
}

export interface UpdateCourseParams extends Partial<CreateCourseParams> {}

export interface CreateScheduleParams {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  location?: string;
  isAllDay?: number;
  reminder?: number;
  color?: string;
}

export interface UpdateScheduleParams extends Partial<CreateScheduleParams> {}

export interface ConflictCheckResult {
  hasConflict: boolean;
  conflictCount: number;
}

// 课程表与日程管理API
export default {
  // 课程相关API
  
  // 创建课程
  createCourse(data: CreateCourseParams) {
    return post<{ code: number; data: { courseId: number } }>('/courses', data);
  },
  
  // 获取课程详情
  getCourseDetail(id: number) {
    return get<{ code: number; data: CourseItem }>(`/courses/${id}`);
  },
  
  // 获取用户的所有课程
  getAllCourses() {
    return get<{ code: number; data: { total: number; list: CourseItem[] } }>('/courses');
  },
  
  // 获取用户在指定星期几的课程
  getCoursesByDay(dayOfWeek: number) {
    return get<{ code: number; data: { total: number; list: CourseItem[] } }>(`/courses/day/${dayOfWeek}`);
  },
  
  // 更新课程
  updateCourse(id: number, data: UpdateCourseParams) {
    return put<{ code: number; message: string }>(`/courses/${id}`, data);
  },
  
  // 删除课程
  deleteCourse(id: number) {
    return del<{ code: number; message: string }>(`/courses/${id}`);
  },
  
  // 检查课程时间冲突
  checkCourseConflict(params: { dayOfWeek: number; startTime: string; endTime: string; excludeCourseId?: number }) {
    return get<{ code: number; data: ConflictCheckResult }>('/courses/check-conflict', params);
  },
  
  // 日程相关API
  
  // 创建日程
  createSchedule(data: CreateScheduleParams) {
    return post<{ code: number; data: { scheduleId: number } }>('/schedules', data);
  },
  
  // 获取日程详情
  getScheduleDetail(id: number) {
    return get<{ code: number; data: ScheduleItem }>(`/schedules/${id}`);
  },
  
  // 获取用户的所有日程
  getAllSchedules() {
    return get<{ code: number; data: { total: number; list: ScheduleItem[] } }>('/schedules');
  },
  
  // 获取用户在指定时间范围内的日程
  getSchedulesByRange(params: { startTime: string; endTime: string }) {
    return get<{ code: number; data: { total: number; list: ScheduleItem[] } }>('/schedules/range', params);
  },
  
  // 更新日程
  updateSchedule(id: number, data: UpdateScheduleParams) {
    return put<{ code: number; message: string }>(`/schedules/${id}`, data);
  },
  
  // 删除日程
  deleteSchedule(id: number) {
    return del<{ code: number; message: string }>(`/schedules/${id}`);
  },
  
  // 检查日程时间冲突
  checkScheduleConflict(params: { startTime: string; endTime: string; excludeScheduleId?: number }) {
    return get<{ code: number; data: ConflictCheckResult }>('/schedules/check-conflict', params);
  }
};
