<template>
  <div class="schedule-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar glass-light">
      <div class="nav-container">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">
            <div class="logo-circle">
              <i class="el-icon-star-filled"></i>
            </div>
            <span class="brand-name gradient-text">UniLife</span>
          </router-link>
        </div>
        
        <div class="nav-menu">
          <router-link to="/forum" class="nav-item">论坛</router-link>
          <router-link to="/resources" class="nav-item">资源</router-link>
          <router-link to="/schedule" class="nav-item active">课程表</router-link>
        </div>
        
        <div class="nav-actions">
          <div class="user-info">
            <el-avatar :size="36" :src="userStore.user?.avatar">
              {{ userStore.user?.nickname?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.user?.nickname }}</span>
          </div>
          <el-dropdown @command="handleCommand">
            <el-button circle>
              <el-icon><Setting /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="schedule-main">
      <div class="schedule-content">
        <!-- 页面标题和操作 -->
        <div class="page-header card-light">
          <div class="header-content">
            <div class="title-section">
              <h1 class="page-title">我的课程表</h1>
              <p class="page-subtitle">管理你的课程安排和日程</p>
            </div>
            
            <div class="header-actions">
              <el-button type="primary" @click="showAddCourse = true">
                <el-icon><Plus /></el-icon>
                添加课程
              </el-button>
              <el-button @click="showAddSchedule = true">
                <el-icon><Calendar /></el-icon>
                添加日程
              </el-button>
            </div>
          </div>
          
          <!-- 学期选择 -->
          <div class="semester-selector">
            <el-select v-model="currentSemester" placeholder="选择学期" size="default" @change="loadCourses">
              <el-option label="2024春季学期" value="2024-1" />
              <el-option label="2024秋季学期" value="2024-2" />
              <el-option label="2023秋季学期" value="2023-2" />
              <el-option label="2023春季学期" value="2023-1" />
            </el-select>
            
            <div class="week-selector">
              <el-button @click="previousWeek" circle size="small">
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
              <span class="current-week">第 {{ currentWeek }} 周</span>
              <el-button @click="nextWeek" circle size="small">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 课程表 -->
        <div v-loading="loading" class="schedule-table card-light">
          <div class="table-header">
            <div class="time-column">时间</div>
            <div 
              v-for="(day, index) in weekDays" 
              :key="index"
              class="day-column"
              :class="{ today: isToday(index) }"
            >
              <div class="day-name">{{ day }}</div>
              <div class="day-date">{{ getDayDate(index) }}</div>
            </div>
          </div>
          
          <div class="table-body">
            <div 
              v-for="(timeSlot, timeIndex) in timeSlots" 
              :key="timeIndex"
              class="time-row"
            >
              <div class="time-cell">
                <div class="time-period">第{{ timeIndex + 1 }}节</div>
                <div class="time-range">{{ timeSlot }}</div>
              </div>
              
              <div 
                v-for="dayIndex in 7" 
                :key="dayIndex"
                class="course-cell"
                @click="addCourseToSlot(timeIndex, dayIndex)"
              >
                <div 
                  v-if="getCourseForSlot(timeIndex, dayIndex)"
                  class="course-item"
                  :style="{ backgroundColor: getCourseForSlot(timeIndex, dayIndex)?.color }"
                  @click.stop="editCourse(getCourseForSlot(timeIndex, dayIndex)!)"
                >
                  <div class="course-name">{{ getCourseForSlot(timeIndex, dayIndex)?.name }}</div>
                  <div class="course-location">{{ getCourseForSlot(timeIndex, dayIndex)?.location }}</div>
                  <div class="course-teacher">{{ getCourseForSlot(timeIndex, dayIndex)?.teacher }}</div>
                </div>
                
                <div v-else class="empty-slot">
                  <el-icon class="add-icon"><Plus /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 今日课程和日程 -->
        <div class="today-section">
          <div class="today-courses card-light">
            <h3 class="section-title">今日课程</h3>
            <div v-if="todayCourses.length > 0" class="courses-list">
              <div 
                v-for="course in todayCourses" 
                :key="course.id"
                class="course-card"
              >
                <div class="course-time">{{ course.startTime }} - {{ course.endTime }}</div>
                <div class="course-info">
                  <div class="course-name">{{ course.name }}</div>
                  <div class="course-details">{{ course.location }} · {{ course.teacher }}</div>
                </div>
                <div class="course-actions">
                  <el-button size="small" @click="editCourse(course)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteCourseConfirm(course)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-message">
              今天没有课程安排
            </div>
          </div>

          <div class="today-schedules card-light">
            <h3 class="section-title">今日日程</h3>
            <div v-if="todaySchedules.length > 0" class="schedules-list">
              <div 
                v-for="schedule in todaySchedules" 
                :key="schedule.id"
                class="schedule-card"
              >
                <div class="schedule-time">{{ formatTime(schedule.startTime) }}</div>
                <div class="schedule-info">
                  <div class="schedule-title">{{ schedule.title }}</div>
                  <div class="schedule-location">{{ schedule.location }}</div>
                </div>
                <div class="schedule-actions">
                  <el-button size="small" @click="editSchedule(schedule)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteScheduleConfirm(schedule)">删除</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-message">
              今天没有特别日程
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加课程对话框 -->
    <el-dialog
      v-model="showAddCourse"
      :title="editingCourse ? '编辑课程' : '添加课程'"
      width="500px"
    >
      <el-form :model="courseForm" :rules="courseRules" ref="courseFormRef" label-position="top">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" size="large" />
        </el-form-item>
        
        <el-form-item label="任课教师" prop="teacher">
          <el-input v-model="courseForm.teacher" placeholder="请输入教师姓名" size="large" />
        </el-form-item>
        
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="courseForm.location" placeholder="请输入上课地点" size="large" />
        </el-form-item>
        
        <div class="form-row">
          <el-form-item label="星期" prop="dayOfWeek">
            <el-select v-model="courseForm.dayOfWeek" placeholder="选择星期" style="width: 100%">
              <el-option v-for="(day, index) in weekDays" :key="index" :label="day" :value="index + 1" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="颜色" prop="color">
            <el-color-picker v-model="courseForm.color" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="开始时间" prop="startTime">
            <el-time-picker
              v-model="courseForm.startTime"
              format="HH:mm"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="结束时间" prop="endTime">
            <el-time-picker
              v-model="courseForm.endTime"
              format="HH:mm"
              placeholder="选择结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="开始周" prop="startWeek">
            <el-input-number 
              v-model="courseForm.startWeek" 
              :min="1" 
              :max="20" 
              placeholder="开始周"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="结束周" prop="endWeek">
            <el-input-number 
              v-model="courseForm.endWeek" 
              :min="1" 
              :max="20" 
              placeholder="结束周"
              style="width: 100%"
            />
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <el-button @click="cancelEditCourse">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSaveCourse"
          :loading="savingCourse"
        >
          {{ editingCourse ? '更新' : '保存' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 添加日程对话框 -->
    <el-dialog
      v-model="showAddSchedule"
      :title="editingSchedule ? '编辑日程' : '添加日程'"
      width="500px"
    >
      <el-form :model="scheduleForm" :rules="scheduleRules" ref="scheduleFormRef" label-position="top">
        <el-form-item label="日程标题" prop="title">
          <el-input v-model="scheduleForm.title" placeholder="请输入日程标题" size="large" />
        </el-form-item>
        
        <el-form-item label="日程描述" prop="description">
          <el-input 
            v-model="scheduleForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入日程描述..."
          />
        </el-form-item>
        
        <el-form-item label="地点" prop="location">
          <el-input v-model="scheduleForm.location" placeholder="请输入地点" size="large" />
        </el-form-item>
        
        <el-form-item label="是否全天">
          <el-switch v-model="scheduleForm.isAllDay" @change="handleAllDayChange" />
        </el-form-item>
        
        <div v-if="!scheduleForm.isAllDay" class="form-row">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="scheduleForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              format="YYYY-MM-DD HH:mm"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="scheduleForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              format="YYYY-MM-DD HH:mm"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div v-else class="form-row">
          <el-form-item label="日期" prop="date">
            <el-date-picker
              v-model="scheduleForm.date"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="提醒时间" prop="reminder">
            <el-select v-model="scheduleForm.reminder" placeholder="选择提醒时间" style="width: 100%">
              <el-option label="不提醒" :value="0" />
              <el-option label="5分钟前" :value="5" />
              <el-option label="15分钟前" :value="15" />
              <el-option label="30分钟前" :value="30" />
              <el-option label="1小时前" :value="60" />
              <el-option label="1天前" :value="1440" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="颜色" prop="color">
            <el-color-picker v-model="scheduleForm.color" />
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <el-button @click="cancelEditSchedule">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSaveSchedule"
          :loading="savingSchedule"
        >
          {{ editingSchedule ? '更新' : '保存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  Plus, 
  Calendar, 
  Setting,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getCourses,
  getCoursesBySemester,
  getCoursesByDay,
  createCourse,
  updateCourse,
  deleteCourse,
  type Course
} from '@/api/schedule'
import {
  getSchedules,
  getSchedulesByRange,
  createSchedule,
  updateSchedule,
  deleteSchedule,
  type Schedule
} from '@/api/schedule'
import type { ApiResponse } from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const savingCourse = ref(false)
const savingSchedule = ref(false)
const showAddCourse = ref(false)
const showAddSchedule = ref(false)
const currentSemester = ref('2024-1')
const currentWeek = ref(1)

// 编辑状态
const editingCourse = ref<Course | null>(null)
const editingSchedule = ref<Schedule | null>(null)

// 数据列表
const courses = ref<Course[]>([])
const schedules = ref<Schedule[]>([])

// 表单引用
const courseFormRef = ref<FormInstance>()
const scheduleFormRef = ref<FormInstance>()

// 课程表单
const courseForm = reactive({
  name: '',
  teacher: '',
  location: '',
  dayOfWeek: null as number | null,
  startTime: '',
  endTime: '',
  startWeek: 1,
  endWeek: 16,
  color: '#409EFF'
})

// 日程表单
const scheduleForm = reactive({
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  date: '',
  isAllDay: false,
  reminder: 30,
  color: '#67C23A'
})

// 表单验证规则
const courseRules: FormRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  teacher: [
    { required: true, message: '请输入教师姓名', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
  ],
  dayOfWeek: [
    { required: true, message: '请选择星期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

const scheduleRules: FormRules = {
  title: [
    { required: true, message: '请输入日程标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入日程描述', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入地点', trigger: 'blur' }
  ]
}

// 常量数据
const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const timeSlots = [
  '08:00-09:40',
  '10:00-11:40', 
  '14:00-15:40',
  '16:00-17:40',
  '19:00-20:40'
]

// 计算属性
const todayCourses = computed(() => {
  const today = new Date().getDay()
  const dayOfWeek = today === 0 ? 7 : today // 将周日转换为7
  return courses.value.filter(course => course.dayOfWeek === dayOfWeek)
})

const todaySchedules = computed(() => {
  const today = new Date()
  const todayStr = today.toISOString().split('T')[0]
  return schedules.value.filter(schedule => {
    const scheduleDate = new Date(schedule.startTime).toISOString().split('T')[0]
    return scheduleDate === todayStr
  })
})

// 方法
const isToday = (dayIndex: number) => {
  const today = new Date().getDay()
  const targetDay = dayIndex + 1
  return today === 0 ? targetDay === 7 : today === targetDay
}

const getDayDate = (dayIndex: number) => {
  const today = new Date()
  const currentDayOfWeek = today.getDay()
  const monday = new Date(today)
  monday.setDate(today.getDate() - (currentDayOfWeek === 0 ? 6 : currentDayOfWeek - 1))
  
  const targetDate = new Date(monday)
  targetDate.setDate(monday.getDate() + dayIndex)
  
  return targetDate.getDate()
}

const getCourseForSlot = (timeIndex: number, dayIndex: number) => {
  return courses.value.find(course => {
    if (course.dayOfWeek !== dayIndex + 1) return false
    
    // 简化时间匹配逻辑
    const courseStart = course.startTime
    const courseEnd = course.endTime
    const slotTime = timeSlots[timeIndex]
    
    // 这里需要根据实际需求进行时间匹配
    return slotTime.includes(courseStart.substring(0, 5))
  })
}

const previousWeek = () => {
  if (currentWeek.value > 1) {
    currentWeek.value--
  }
}

const nextWeek = () => {
  if (currentWeek.value < 20) {
    currentWeek.value++
  }
}

const addCourseToSlot = (timeIndex: number, dayIndex: number) => {
  // 预设时间和星期
  courseForm.dayOfWeek = dayIndex + 1
  const timeSlot = timeSlots[timeIndex].split('-')
  courseForm.startTime = timeSlot[0]
  courseForm.endTime = timeSlot[1]
  showAddCourse.value = true
}

// 课程相关方法
const loadCourses = async () => {
  try {
    loading.value = true
    const response = await getCoursesBySemester(currentSemester.value) as any as ApiResponse<{
      total: number
      list: Course[]
      pages: number
    }>
    
    console.log('课程列表API响应:', response)
    
    if (response.code === 200) {
      courses.value = response.data.list
      console.log('课程列表加载成功:', courses.value)
    } else {
      console.error('课程列表API返回错误:', response)
      ElMessage.error(response.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const loadSchedules = async () => {
  try {
    const response = await getSchedules() as any as ApiResponse<{
      total: number
      list: Schedule[]
      pages: number
    }>
    
    console.log('日程列表API响应:', response)
    
    if (response.code === 200) {
      schedules.value = response.data.list
      console.log('日程列表加载成功:', schedules.value)
    } else {
      console.error('日程列表API返回错误:', response)
      ElMessage.error(response.message || '获取日程列表失败')
    }
  } catch (error) {
    console.error('获取日程列表失败:', error)
    ElMessage.error('获取日程列表失败')
  }
}

const handleSaveCourse = async () => {
  if (!courseFormRef.value) return
  
  try {
    await courseFormRef.value.validate()
    savingCourse.value = true
    
    const courseData = {
      name: courseForm.name,
      teacher: courseForm.teacher,
      location: courseForm.location,
      dayOfWeek: courseForm.dayOfWeek!,
      startTime: courseForm.startTime,
      endTime: courseForm.endTime,
      startWeek: courseForm.startWeek,
      endWeek: courseForm.endWeek,
      semester: currentSemester.value,
      color: courseForm.color
    }
    
    let response
    if (editingCourse.value) {
      response = await updateCourse(editingCourse.value.id, courseData) as any as ApiResponse<null>
    } else {
      response = await createCourse(courseData) as any as ApiResponse<{ courseId: number }>
    }
    
    if (response.code === 200) {
      ElMessage.success(editingCourse.value ? '课程更新成功！' : '课程添加成功！')
      showAddCourse.value = false
      resetCourseForm()
      loadCourses() // 重新加载课程列表
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存课程失败:', error)
    ElMessage.error('保存失败')
  } finally {
    savingCourse.value = false
  }
}

const handleSaveSchedule = async () => {
  if (!scheduleFormRef.value) return
  
  try {
    await scheduleFormRef.value.validate()
    savingSchedule.value = true
    
    let startTime, endTime
    if (scheduleForm.isAllDay) {
      startTime = `${scheduleForm.date}T00:00:00`
      endTime = `${scheduleForm.date}T23:59:59`
    } else {
      startTime = scheduleForm.startTime
      endTime = scheduleForm.endTime
    }
    
    const scheduleData = {
      title: scheduleForm.title,
      description: scheduleForm.description,
      location: scheduleForm.location,
      startTime,
      endTime,
      isAllDay: scheduleForm.isAllDay ? 1 : 0,
      reminder: scheduleForm.reminder,
      color: scheduleForm.color
    }
    
    let response
    if (editingSchedule.value) {
      response = await updateSchedule(editingSchedule.value.id, scheduleData) as any as ApiResponse<null>
    } else {
      response = await createSchedule(scheduleData) as any as ApiResponse<{ scheduleId: number }>
    }
    
    if (response.code === 200) {
      ElMessage.success(editingSchedule.value ? '日程更新成功！' : '日程添加成功！')
      showAddSchedule.value = false
      resetScheduleForm()
      loadSchedules() // 重新加载日程列表
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存日程失败:', error)
    ElMessage.error('保存失败')
  } finally {
    savingSchedule.value = false
  }
}

const editCourse = (course: Course) => {
  editingCourse.value = course
  courseForm.name = course.name
  courseForm.teacher = course.teacher
  courseForm.location = course.location
  courseForm.dayOfWeek = course.dayOfWeek
  courseForm.startTime = course.startTime
  courseForm.endTime = course.endTime
  courseForm.startWeek = course.startWeek
  courseForm.endWeek = course.endWeek
  courseForm.color = course.color
  showAddCourse.value = true
}

const editSchedule = (schedule: Schedule) => {
  editingSchedule.value = schedule
  scheduleForm.title = schedule.title
  scheduleForm.description = schedule.description
  scheduleForm.location = schedule.location
  scheduleForm.isAllDay = schedule.isAllDay === 1
  scheduleForm.reminder = schedule.reminder
  scheduleForm.color = schedule.color
  
  if (schedule.isAllDay === 1) {
    scheduleForm.date = new Date(schedule.startTime).toISOString().split('T')[0]
  } else {
    scheduleForm.startTime = schedule.startTime
    scheduleForm.endTime = schedule.endTime
  }
  
  showAddSchedule.value = true
}

const deleteCourseConfirm = async (course: Course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await deleteCourse(course.id) as any as ApiResponse<null>
    if (response.code === 200) {
      ElMessage.success('课程删除成功')
      loadCourses()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const deleteScheduleConfirm = async (schedule: Schedule) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除日程"${schedule.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await deleteSchedule(schedule.id) as any as ApiResponse<null>
    if (response.code === 200) {
      ElMessage.success('日程删除成功')
      loadSchedules()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除日程失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const cancelEditCourse = () => {
  showAddCourse.value = false
  resetCourseForm()
}

const cancelEditSchedule = () => {
  showAddSchedule.value = false
  resetScheduleForm()
}

const resetCourseForm = () => {
  editingCourse.value = null
  courseForm.name = ''
  courseForm.teacher = ''
  courseForm.location = ''
  courseForm.dayOfWeek = null
  courseForm.startTime = ''
  courseForm.endTime = ''
  courseForm.startWeek = 1
  courseForm.endWeek = 16
  courseForm.color = '#409EFF'
  courseFormRef.value?.clearValidate()
}

const resetScheduleForm = () => {
  editingSchedule.value = null
  scheduleForm.title = ''
  scheduleForm.description = ''
  scheduleForm.location = ''
  scheduleForm.startTime = ''
  scheduleForm.endTime = ''
  scheduleForm.date = ''
  scheduleForm.isAllDay = false
  scheduleForm.reminder = 30
  scheduleForm.color = '#67C23A'
  scheduleFormRef.value?.clearValidate()
}

const handleAllDayChange = (value: boolean) => {
  if (value) {
    scheduleForm.startTime = ''
    scheduleForm.endTime = ''
  } else {
    scheduleForm.date = ''
  }
}

const formatTime = (dateTimeString: string) => {
  const date = new Date(dateTimeString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 监听对话框关闭，重置表单
watch(showAddCourse, (newVal) => {
  if (!newVal) {
    resetCourseForm()
  }
})

watch(showAddSchedule, (newVal) => {
  if (!newVal) {
    resetScheduleForm()
  }
})

// 页面加载时获取数据
onMounted(async () => {
  console.log('课程表页面加载完成')
  await loadCourses()
  await loadSchedules()
  
  // 设置当前周（简化计算）
  const now = new Date()
  const startOfYear = new Date(now.getFullYear(), 0, 1)
  const weekNumber = Math.ceil((now.getTime() - startOfYear.getTime()) / (7 * 24 * 60 * 60 * 1000))
  currentWeek.value = Math.min(weekNumber, 20)
})
</script>

<style scoped>
.schedule-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 导航栏样式 - 复用之前的样式 */
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 16px 0;
  border-bottom: 1px solid var(--gray-200);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo-circle {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: var(--shadow-light);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-item {
  text-decoration: none;
  color: var(--gray-600);
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 12px;
  transition: var(--transition-base);
}

.nav-item:hover,
.nav-item.active {
  color: var(--primary-600);
  background: var(--primary-50);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: var(--gray-700);
}

/* 主要内容区域 */
.schedule-main {
  padding: 32px 24px;
}

.schedule-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 页面标题 */
.page-header {
  padding: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.title-section h1 {
  font-size: 2rem;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0 0 8px 0;
}

.page-subtitle {
  color: var(--gray-600);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.semester-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.week-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.current-week {
  font-weight: 600;
  color: var(--gray-700);
  min-width: 80px;
  text-align: center;
}

/* 课程表样式 */
.schedule-table {
  padding: 0;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 120px repeat(7, 1fr);
  background: var(--primary-50);
  border-bottom: 2px solid var(--primary-200);
}

.time-column,
.day-column {
  padding: 16px 12px;
  text-align: center;
  font-weight: 600;
  color: var(--gray-700);
}

.day-column.today {
  background: var(--primary-100);
  color: var(--primary-700);
}

.day-name {
  font-size: 14px;
  margin-bottom: 4px;
}

.day-date {
  font-size: 12px;
  opacity: 0.8;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.time-row {
  display: grid;
  grid-template-columns: 120px repeat(7, 1fr);
  border-bottom: 1px solid var(--gray-200);
}

.time-cell {
  padding: 12px;
  text-align: center;
  background: var(--gray-50);
  border-right: 1px solid var(--gray-200);
}

.time-period {
  font-size: 12px;
  color: var(--gray-600);
  margin-bottom: 4px;
}

.time-range {
  font-size: 11px;
  color: var(--gray-500);
}

.course-cell {
  padding: 4px;
  border-right: 1px solid var(--gray-200);
  min-height: 60px;
  cursor: pointer;
  transition: var(--transition-base);
}

.course-cell:hover {
  background: var(--primary-50);
}

.course-item {
  height: 100%;
  padding: 8px;
  border-radius: 8px;
  border-left: 4px solid rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: var(--transition-base);
}

.course-item:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.course-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 2px;
}

.course-location,
.course-teacher {
  font-size: 10px;
  color: var(--gray-600);
  line-height: 1.2;
}

.empty-slot {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: var(--transition-base);
}

.course-cell:hover .empty-slot {
  opacity: 1;
}

.add-icon {
  color: var(--primary-400);
  font-size: 16px;
}

/* 今日安排 */
.today-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.today-courses,
.today-schedules {
  padding: 24px;
}

.section-title {
  color: var(--gray-800);
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 16px 0;
}

.courses-list,
.schedules-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-card,
.schedule-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--primary-50);
  border-radius: 12px;
  border-left: 4px solid var(--primary-400);
}

.course-time,
.schedule-time {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-600);
  min-width: 80px;
}

.course-info,
.schedule-info {
  flex: 1;
}

.course-name,
.schedule-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 2px;
}

.course-details,
.schedule-location {
  font-size: 12px;
  color: var(--gray-600);
}

.empty-message {
  text-align: center;
  color: var(--gray-500);
  font-size: 14px;
  padding: 32px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .today-section {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .semester-selector {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .schedule-main {
    padding: 16px;
  }
  
  .schedule-content {
    gap: 16px;
  }
  
  .page-header {
    padding: 24px 16px;
  }
  
  .table-header,
  .time-row {
    grid-template-columns: 80px repeat(7, 1fr);
  }
  
  .time-column,
  .day-column {
    padding: 8px 4px;
    font-size: 12px;
  }
  
  .course-cell {
    min-height: 50px;
  }
  
  .course-name {
    font-size: 10px;
  }
  
  .course-location,
  .course-teacher {
    font-size: 9px;
  }
}

/* 表单行样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
</style> 