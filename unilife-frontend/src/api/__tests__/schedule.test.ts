import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import * as scheduleApi from '../schedule'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

describe('Schedule API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getSchedules', () => {
    it('should fetch schedules successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: [
            {
              id: 1,
              title: '高等数学',
              description: '线性代数课程',
              startTime: '2024-01-15T09:00:00',
              endTime: '2024-01-15T10:30:00',
              location: '教学楼A101',
              type: 'COURSE',
              repeatType: 'WEEKLY',
              repeatEnd: '2024-06-15T10:30:00',
              userId: 1,
              createdAt: '2024-01-01T10:00:00'
            },
            {
              id: 2,
              title: '团队会议',
              description: '项目进度讨论',
              startTime: '2024-01-16T14:00:00',
              endTime: '2024-01-16T15:00:00',
              location: '会议室B201',
              type: 'MEETING',
              repeatType: 'NONE',
              userId: 1,
              createdAt: '2024-01-01T11:00:00'
            }
          ]
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getSchedules()

      expect(mockedAxios.get).toHaveBeenCalledWith('/schedules')
      expect(result.data.data).toHaveLength(2)
      expect(result.data.data[0].title).toBe('高等数学')
      expect(result.data.data[1].type).toBe('MEETING')
    })

    it('should handle empty schedule list', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: []
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getSchedules()

      expect(result.data.data).toHaveLength(0)
    })
  })

  describe('getSchedulesByRange', () => {
    it('should fetch schedules by time range successfully', async () => {
      const startTime = '2024-01-01T00:00:00'
      const endTime = '2024-01-31T23:59:59'

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: [
            {
              id: 1,
              title: '期末考试',
              description: '高等数学期末考试',
              startTime: '2024-01-20T09:00:00',
              endTime: '2024-01-20T11:00:00',
              location: '考试教室C301',
              type: 'EXAM',
              repeatType: 'NONE',
              userId: 1
            }
          ]
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getSchedulesByRange(startTime, endTime)

      expect(mockedAxios.get).toHaveBeenCalledWith('/schedules/range', {
        params: {
          startTime,
          endTime
        }
      })
      expect(result.data.data).toHaveLength(1)
      expect(result.data.data[0].type).toBe('EXAM')
    })

    it('should handle invalid time range', async () => {
      const startTime = '2024-01-31T23:59:59'
      const endTime = '2024-01-01T00:00:00'

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '结束时间不能早于开始时间'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getSchedulesByRange(startTime, endTime)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('结束时间不能早于开始时间')
    })
  })

  describe('getScheduleDetail', () => {
    it('should fetch schedule detail successfully', async () => {
      const mockSchedule = {
        id: 1,
        title: '数据结构与算法',
        description: '数据结构课程，包含树、图等内容',
        startTime: '2024-01-15T14:00:00',
        endTime: '2024-01-15T15:30:00',
        location: '计算机楼201',
        type: 'COURSE',
        repeatType: 'WEEKLY',
        repeatEnd: '2024-06-15T15:30:00',
        userId: 1,
        createdAt: '2024-01-01T10:00:00',
        updatedAt: '2024-01-01T10:00:00'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: mockSchedule
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getScheduleDetail(1)

      expect(mockedAxios.get).toHaveBeenCalledWith('/schedules/1')
      expect(result.data.data.title).toBe('数据结构与算法')
      expect(result.data.data.location).toBe('计算机楼201')
    })

    it('should handle schedule not found', async () => {
      const mockResponse = {
        data: {
          code: 404,
          success: false,
          message: '日程不存在'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getScheduleDetail(999)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('日程不存在')
    })

    it('should handle unauthorized access', async () => {
      const mockResponse = {
        data: {
          code: 403,
          success: false,
          message: '无权限查看此日程'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getScheduleDetail(1)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('无权限查看此日程')
    })
  })

  describe('createSchedule', () => {
    it('should create schedule successfully', async () => {
      const scheduleData = {
        title: '新课程',
        description: '新课程描述',
        startTime: '2024-01-16T09:00:00',
        endTime: '2024-01-16T10:30:00',
        location: '教学楼D101',
        type: 'COURSE',
        repeatType: 'WEEKLY',
        repeatEnd: '2024-06-16T10:30:00'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { scheduleId: 123 },
          message: '日程创建成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await scheduleApi.createSchedule(scheduleData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/schedules', scheduleData)
      expect(result.data.data.scheduleId).toBe(123)
      expect(result.data.message).toBe('日程创建成功')
    })

    it('should handle validation errors', async () => {
      const invalidScheduleData = {
        title: '',
        description: '描述',
        startTime: '2024-01-16T09:00:00',
        endTime: '2024-01-16T08:00:00', // 结束时间早于开始时间
        location: '教学楼D101',
        type: 'COURSE',
        repeatType: 'WEEKLY'
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '结束时间不能早于开始时间'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await scheduleApi.createSchedule(invalidScheduleData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('结束时间不能早于开始时间')
    })

    it('should handle time conflict', async () => {
      const conflictingScheduleData = {
        title: '冲突课程',
        description: '与现有课程时间冲突',
        startTime: '2024-01-16T09:30:00',
        endTime: '2024-01-16T11:00:00',
        location: '教学楼D102',
        type: 'COURSE',
        repeatType: 'WEEKLY'
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '时间冲突，与现有日程重叠'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await scheduleApi.createSchedule(conflictingScheduleData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('时间冲突，与现有日程重叠')
    })
  })

  describe('updateSchedule', () => {
    it('should update schedule successfully', async () => {
      const updateData = {
        title: '更新后的课程',
        description: '更新后的描述',
        startTime: '2024-01-16T10:00:00',
        endTime: '2024-01-16T11:30:00',
        location: '教学楼E101',
        type: 'COURSE',
        repeatType: 'WEEKLY',
        repeatEnd: '2024-06-16T11:30:00'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '日程更新成功'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await scheduleApi.updateSchedule(1, updateData)

      expect(mockedAxios.put).toHaveBeenCalledWith('/schedules/1', updateData)
      expect(result.data.message).toBe('日程更新成功')
    })

    it('should handle unauthorized update', async () => {
      const updateData = {
        title: '尝试更新别人的课程',
        description: '无权限更新',
        startTime: '2024-01-16T10:00:00',
        endTime: '2024-01-16T11:30:00',
        location: '教学楼E101',
        type: 'COURSE'
      }

      const mockResponse = {
        data: {
          code: 403,
          success: false,
          message: '无权限修改此日程'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await scheduleApi.updateSchedule(1, updateData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('无权限修改此日程')
    })
  })

  describe('deleteSchedule', () => {
    it('should delete schedule successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '日程删除成功'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await scheduleApi.deleteSchedule(1)

      expect(mockedAxios.delete).toHaveBeenCalledWith('/schedules/1')
      expect(result.data.message).toBe('日程删除成功')
    })

    it('should handle unauthorized delete', async () => {
      const mockResponse = {
        data: {
          code: 403,
          success: false,
          message: '无权限删除此日程'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await scheduleApi.deleteSchedule(1)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('无权限删除此日程')
    })
  })

  describe('checkConflict', () => {
    it('should check for no conflicts', async () => {
      const startTime = '2024-01-16T13:00:00'
      const endTime = '2024-01-16T14:30:00'

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '无时间冲突'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.checkConflict(startTime, endTime)

      expect(mockedAxios.get).toHaveBeenCalledWith('/schedules/check-conflict', {
        params: {
          startTime,
          endTime
        }
      })
      expect(result.data.message).toBe('无时间冲突')
    })

    it('should check for conflicts with exclusion', async () => {
      const startTime = '2024-01-16T13:00:00'
      const endTime = '2024-01-16T14:30:00'
      const excludeScheduleId = 5

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '无时间冲突'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.checkConflict(startTime, endTime, excludeScheduleId)

      expect(mockedAxios.get).toHaveBeenCalledWith('/schedules/check-conflict', {
        params: {
          startTime,
          endTime,
          excludeScheduleId
        }
      })
      expect(result.data.message).toBe('无时间冲突')
    })

    it('should detect time conflicts', async () => {
      const startTime = '2024-01-16T09:30:00'
      const endTime = '2024-01-16T11:00:00'

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '时间冲突，与现有日程重叠'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.checkConflict(startTime, endTime)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('时间冲突，与现有日程重叠')
    })
  })

  describe('getCourses', () => {
    it('should fetch courses successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: [
            {
              id: 1,
              name: '高等数学',
              code: 'MATH101',
              teacher: '张教授',
              credits: 4,
              semester: '2024春季',
              description: '高等数学基础课程'
            },
            {
              id: 2,
              name: '数据结构',
              code: 'CS201',
              teacher: '李教授',
              credits: 3,
              semester: '2024春季',
              description: '计算机科学基础课程'
            }
          ]
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await scheduleApi.getCourses()

      expect(mockedAxios.get).toHaveBeenCalledWith('/courses')
      expect(result.data.data).toHaveLength(2)
      expect(result.data.data[0].name).toBe('高等数学')
      expect(result.data.data[1].credits).toBe(3)
    })
  })

  describe('createCourse', () => {
    it('should create course successfully', async () => {
      const courseData = {
        name: '操作系统',
        code: 'CS301',
        teacher: '王教授',
        credits: 3,
        semester: '2024春季',
        description: '操作系统原理与实践'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { courseId: 456 },
          message: '课程创建成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await scheduleApi.createCourse(courseData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/courses', courseData)
      expect(result.data.data.courseId).toBe(456)
      expect(result.data.message).toBe('课程创建成功')
    })

    it('should handle duplicate course code', async () => {
      const duplicateCourseData = {
        name: '重复课程',
        code: 'MATH101', // 已存在的课程代码
        teacher: '赵教授',
        credits: 2,
        semester: '2024春季',
        description: '重复的课程代码'
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '课程代码已存在'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await scheduleApi.createCourse(duplicateCourseData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('课程代码已存在')
    })
  })

  describe('updateCourse', () => {
    it('should update course successfully', async () => {
      const updateData = {
        name: '高等数学(更新)',
        code: 'MATH101',
        teacher: '张教授',
        credits: 4,
        semester: '2024春季',
        description: '更新后的高等数学课程'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '课程更新成功'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await scheduleApi.updateCourse(1, updateData)

      expect(mockedAxios.put).toHaveBeenCalledWith('/courses/1', updateData)
      expect(result.data.message).toBe('课程更新成功')
    })
  })

  describe('deleteCourse', () => {
    it('should delete course successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '课程删除成功'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await scheduleApi.deleteCourse(1)

      expect(mockedAxios.delete).toHaveBeenCalledWith('/courses/1')
      expect(result.data.message).toBe('课程删除成功')
    })

    it('should handle course in use error', async () => {
      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '课程正在使用中，无法删除'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await scheduleApi.deleteCourse(1)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('课程正在使用中，无法删除')
    })
  })
}) 