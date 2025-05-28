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
              <el-button @click="createTestSchedule" type="warning" plain>
                <el-icon><Calendar /></el-icon>
                创建测试日程
              </el-button>
            </div>
          </div>
          
          <!-- 学期选择 -->
          <div class="semester-selector">
            <el-select v-model="currentSemester" placeholder="选择学期" size="default" @change="handleSemesterChange">
              <el-option 
                v-for="(config, key) in semesterConfig" 
                :key="key" 
                :label="config.name" 
                :value="key" 
              />
            </el-select>
            
            <div class="week-selector">
              <el-button @click="previousWeek" circle size="small">
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
              
              <el-select 
                v-model="currentWeek" 
                placeholder="选择周数" 
                size="small"
                style="width: 120px;"
              >
                <el-option 
                  v-for="week in getCurrentSemesterConfig().totalWeeks" 
                  :key="week" 
                  :label="`第 ${week} 周`" 
                  :value="week"
                />
              </el-select>
              
              <el-button @click="nextWeek" circle size="small">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
              
              <el-button @click="goToCurrentWeek" size="small" type="primary" plain>
                <el-icon><Calendar /></el-icon>
                回到今天
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
              :class="{ 
                today: getDayDate(index).isToday,
                'current-day': getDayDate(index).isToday 
              }"
            >
              <div class="day-name">{{ day }}</div>
              <div class="day-date" :class="{ 'today-date': getDayDate(index).isToday }">
                {{ getDayDate(index).month }}/{{ getDayDate(index).date }}
                <span v-if="getDayDate(index).isToday" class="today-indicator">今天</span>
              </div>
            </div>
          </div>
          
          <div class="table-body">
            <div 
              v-for="(timeSlot, timeIndex) in timeSlots" 
              :key="timeIndex"
              class="time-row"
            >
              <div class="time-cell">
                <div class="time-period">{{ timeSlot }}</div>
              </div>
              
              <div 
                v-for="dayIndex in 7" 
                :key="dayIndex"
                class="course-cell"
                @click="addCourseToSlot(timeIndex, dayIndex - 1)"
              >
                <!-- 显示所有项目（课程和日程） -->
                <div v-if="getItemsForSlot(timeIndex, dayIndex - 1).length > 0" class="slot-items">
                  <div 
                    v-for="(item, itemIndex) in getItemsForSlot(timeIndex, dayIndex - 1)" 
                    :key="itemIndex"
                    :class="item.type === 'course' ? 'course-item' : 'schedule-item'"
                    :style="{ backgroundColor: item.data.color }"
                    @click.stop="item.type === 'course' ? editCourse(item.data) : editSchedule(item.data)"
                  >
                    <!-- 课程内容 -->
                    <template v-if="item.type === 'course'">
                      <div class="course-name">{{ item.data.name }}</div>
                      <div class="course-location">{{ item.data.location }}</div>
                      <div class="course-teacher">{{ item.data.teacher }}</div>
                    </template>
                    
                    <!-- 日程内容 -->
                    <template v-else>
                      <div class="schedule-name">{{ item.data.title }}</div>
                      <div class="schedule-location">{{ item.data.location }}</div>
                      <div class="schedule-time" v-if="item.data.isAllDay !== 1">
                        {{ formatTime(item.data.startTime) }}
                      </div>
                      <div class="schedule-time" v-else>全天</div>
                    </template>
                  </div>
                </div>
                
                <!-- 空白时间段 -->
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
            <el-select 
              v-model="courseForm.startTime" 
              placeholder="选择开始时间" 
              style="width: 100%"
            >
              <el-option 
                v-for="option in startTimeOptions" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="结束时间" prop="endTime">
            <el-select 
              v-model="courseForm.endTime" 
              placeholder="选择结束时间" 
              style="width: 100%"
            >
              <el-option 
                v-for="option in endTimeOptions" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value"
              />
            </el-select>
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
const currentSemester = ref('2024-2025-1')
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
const scheduleForm = reactive<{
  title: string
  description: string
  location: string
  startTime: string | Date
  endTime: string | Date
  date: string
  isAllDay: boolean
  reminder: number
  color: string
}>({
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
  '08:00-08:50',   // 第1节课
  '08:50-09:40',   // 第2节课  
  '09:50-10:40',   // 第3节课
  '10:40-11:30',   // 第4节课
  '11:30-12:20',   // 第5节课
  '14:05-14:55',   // 第6节课
  '14:55-15:45',   // 第7节课
  '15:45-16:35',   // 第8节课
  '16:40-17:30',   // 第9节课
  '17:30-18:20',   // 第10节课
  '18:30-19:20',   // 第11节课
  '19:20-20:10',   // 第12节课
  '20:10-21:00'    // 第13节课
]

// 课程时间选项
const courseTimeOptions = [
  { label: '第1节课 08:00-08:50', value: '08:00', endValue: '08:50' },
  { label: '第2节课 08:50-09:40', value: '08:50', endValue: '09:40' },
  { label: '第3节课 09:50-10:40', value: '09:50', endValue: '10:40' },
  { label: '第4节课 10:40-11:30', value: '10:40', endValue: '11:30' },
  { label: '第5节课 11:30-12:20', value: '11:30', endValue: '12:20' },
  { label: '第6节课 14:05-14:55', value: '14:05', endValue: '14:55' },
  { label: '第7节课 14:55-15:45', value: '14:55', endValue: '15:45' },
  { label: '第8节课 15:45-16:35', value: '15:45', endValue: '16:35' },
  { label: '第9节课 16:40-17:30', value: '16:40', endValue: '17:30' },
  { label: '第10节课 17:30-18:20', value: '17:30', endValue: '18:20' },
  { label: '第11节课 18:30-19:20', value: '18:30', endValue: '19:20' },
  { label: '第12节课 19:20-20:10', value: '19:20', endValue: '20:10' },
  { label: '第13节课 20:10-21:00', value: '20:10', endValue: '21:00' }
]

// 开始时间选项（显示开始时间）
const startTimeOptions = courseTimeOptions.map(option => ({
  label: `${option.value} (${option.label.split(' ')[0]}开始)`,
  value: option.value + ':00'  // 添加秒数，符合后端HH:mm:ss格式
}))

// 结束时间选项（显示结束时间）
const endTimeOptions = courseTimeOptions.map(option => ({
  label: `${option.endValue} (${option.label.split(' ')[0]}结束)`,
  value: option.endValue + ':00'  // 添加秒数，符合后端HH:mm:ss格式
}))

// 课程颜色预设
const courseColors = [
  '#409EFF', // 蓝色
  '#67C23A', // 绿色
  '#E6A23C', // 橙色
  '#F56C6C', // 红色
  '#909399', // 灰色
  '#9C27B0', // 紫色
  '#FF5722', // 深橙色
  '#4CAF50', // 深绿色
  '#2196F3', // 深蓝色
  '#FF9800', // 琥珀色
  '#795548', // 棕色
  '#607D8B'  // 蓝灰色
]

// 生成随机颜色
const getRandomColor = () => {
  return courseColors[Math.floor(Math.random() * courseColors.length)]
}

// 计算属性
const todayCourses = computed(() => {
  const today = new Date().getDay()
  const dayOfWeek = today === 0 ? 7 : today // 将周日转换为7
  return courses.value.filter(course => course.dayOfWeek === dayOfWeek)
})

const todaySchedules = computed(() => {
  // 获取真实的今天日期
  const today = new Date()
  const todayStr = today.toISOString().split('T')[0]
  
  console.log('=== 今日日程调试信息 ===')
  console.log('今天日期:', todayStr)
  console.log('所有日程数据:', schedules.value)
  
  const filteredSchedules = schedules.value.filter(schedule => {
    const startDate = new Date(schedule.startTime).toISOString().split('T')[0]
    const endDate = new Date(schedule.endTime).toISOString().split('T')[0]
    
    // 检查今天是否在日程的时间范围内（包括跨天日程）
    const isInRange = todayStr >= startDate && todayStr <= endDate
    
    console.log('日程:', schedule.title)
    console.log('开始日期:', startDate, '结束日期:', endDate)
    console.log('今天是否在范围内:', isInRange)
    
    return isInRange
  })
  
  console.log('筛选后的今日日程:', filteredSchedules)
  console.log('=== 调试信息结束 ===')
  
  return filteredSchedules
})

// 方法
// 学期配置
const semesterConfig = {
  '2024-2025-1': {
    name: '2024-2025第一学期',
    startDate: new Date(2024, 8, 2), // 2024年9月2日（周一）
    endDate: new Date(2025, 0, 19),   // 2025年1月19日
    totalWeeks: 20
  },
  '2024-2025-2': {
    name: '2024-2025第二学期', 
    startDate: new Date(2025, 1, 24), // 2025年2月24日（周一）
    endDate: new Date(2025, 5, 29),   // 2025年6月29日
    totalWeeks: 18
  },
  '2023-2024-1': {
    name: '2023-2024第一学期',
    startDate: new Date(2023, 8, 4), // 2023年9月4日（周一）
    endDate: new Date(2024, 0, 21),   // 2024年1月21日
    totalWeeks: 20
  },
  '2023-2024-2': {
    name: '2023-2024第二学期',
    startDate: new Date(2024, 1, 26), // 2024年2月26日（周一）
    endDate: new Date(2024, 5, 30),   // 2024年6月30日
    totalWeeks: 18
  },
  '2022-2023-1': {
    name: '2022-2023第一学期',
    startDate: new Date(2022, 8, 5), // 2022年9月5日（周一）
    endDate: new Date(2023, 0, 15),   // 2023年1月15日
    totalWeeks: 20
  },
  '2022-2023-2': {
    name: '2022-2023第二学期',
    startDate: new Date(2023, 1, 27), // 2023年2月27日（周一）
    endDate: new Date(2023, 6, 2),    // 2023年7月2日
    totalWeeks: 18
  }
}

// 获取当前应该显示的学期
const getCurrentSemester = () => {
  const now = new Date()
  
  // 按时间顺序检查学期，找到当前日期所在的学期
  const sortedSemesters = Object.entries(semesterConfig).sort((a, b) => {
    return b[1].startDate.getTime() - a[1].startDate.getTime() // 按开始时间倒序排列
  })
  
  for (const [semesterKey, config] of sortedSemesters) {
    if (now >= config.startDate && now <= config.endDate) {
      return semesterKey
    }
  }
  
  // 如果当前时间不在任何学期范围内，找最接近的学期
  let closestSemester = '2024-2025-1'
  let minDistance = Infinity
  
  for (const [semesterKey, config] of Object.entries(semesterConfig)) {
    // 计算到学期开始时间的距离
    const distanceToStart = Math.abs(now.getTime() - config.startDate.getTime())
    // 计算到学期结束时间的距离
    const distanceToEnd = Math.abs(now.getTime() - config.endDate.getTime())
    // 取较小的距离
    const distance = Math.min(distanceToStart, distanceToEnd)
    
    if (distance < minDistance) {
      minDistance = distance
      closestSemester = semesterKey
    }
  }
  
  console.log('当前时间不在任何学期范围内，选择最接近的学期:', closestSemester)
  return closestSemester
}

// 获取指定学期的当前周数
const getCurrentWeekInSemester = (semester: string) => {
  const config = semesterConfig[semester as keyof typeof semesterConfig]
  if (!config) return 1
  
  const now = new Date()
  
  // 如果当前时间不在学期范围内，返回第1周
  if (now < config.startDate || now > config.endDate) {
    return 1
  }
  
  // 计算从学期开始到现在的周数
  const diffTime = now.getTime() - config.startDate.getTime()
  const diffWeeks = Math.floor(diffTime / (7 * 24 * 60 * 60 * 1000)) + 1
  
  return Math.max(1, Math.min(diffWeeks, config.totalWeeks))
}

// 获取指定学期和周数的周一日期
const getWeekMondayDate = (semester: string, week: number) => {
  const config = semesterConfig[semester as keyof typeof semesterConfig]
  if (!config) return new Date()
  
  const mondayDate = new Date(config.startDate)
  mondayDate.setDate(config.startDate.getDate() + (week - 1) * 7)
  
  return mondayDate
}

// 获取当前学期配置
const getCurrentSemesterConfig = () => {
  return semesterConfig[currentSemester.value as keyof typeof semesterConfig] || semesterConfig['2024-2025-1']
}

// 处理学期切换
const handleSemesterChange = (newSemester: string) => {
  console.log('学期切换到:', newSemester)
  
  // 重置周数到第1周
  currentWeek.value = 1
  
  // 重新加载课程
  loadCourses()
}

const isToday = (dayIndex: number) => {
  const now = new Date()
  const currentSemesterKey = getCurrentSemester()
  
  // 只有在当前学期才显示"今天"
  if (currentSemester.value !== currentSemesterKey) {
    return false
  }
  
  // 只有在当前周才显示"今天"
  const currentWeekInSemester = getCurrentWeekInSemester(currentSemester.value)
  if (currentWeek.value !== currentWeekInSemester) {
    return false
  }
  
  const today = now.getDay()
  const targetDay = dayIndex + 1
  return today === 0 ? targetDay === 7 : today === targetDay
}

const getDayDate = (dayIndex: number) => {
  const monday = getWeekMondayDate(currentSemester.value, currentWeek.value)
  const targetDate = new Date(monday)
  targetDate.setDate(monday.getDate() + dayIndex)
  
  // 检查是否是今天
  const now = new Date()
  const isCurrentDay = (
    targetDate.getFullYear() === now.getFullYear() &&
    targetDate.getMonth() === now.getMonth() &&
    targetDate.getDate() === now.getDate()
  )
  
  return {
    date: targetDate.getDate(),
    month: targetDate.getMonth() + 1,
    isToday: isCurrentDay,
    fullDate: targetDate
  }
}

const getCourseForSlot = (timeIndex: number, dayIndex: number) => {
  return courses.value.find(course => {
    // dayIndex现在是0-6，需要转换为1-7来匹配数据库
    if (course.dayOfWeek !== dayIndex + 1) return false
    
    // 检查当前周是否在课程的周数范围内
    if (currentWeek.value < course.startWeek || currentWeek.value > course.endWeek) {
      return false
    }
    
    // 获取当前时间段的开始和结束时间
    const slotTimes = timeSlots[timeIndex].split('-')
    const slotStart = slotTimes[0] // 例如: "08:00"
    const slotEnd = slotTimes[1]   // 例如: "08:50"
    
    // 获取课程的开始和结束时间(移除秒数部分进行比较)
    const courseStart = course.startTime.substring(0, 5) // "08:00:00" -> "08:00"
    const courseEnd = course.endTime.substring(0, 5)     // "09:40:00" -> "09:40"
    
    // 判断当前时间段是否在课程的时间范围内
    // 课程开始时间 <= 时间段开始时间 && 时间段结束时间 <= 课程结束时间
    return courseStart <= slotStart && slotEnd <= courseEnd
  })
}

// 获取指定时间段和日期的日程
const getScheduleForSlot = (timeIndex: number, dayIndex: number) => {
  // 获取指定日期
  const targetDate = getDayDate(dayIndex)
  const targetDateStr = `${targetDate.fullDate.getFullYear()}-${String(targetDate.fullDate.getMonth() + 1).padStart(2, '0')}-${String(targetDate.fullDate.getDate()).padStart(2, '0')}`
  
  // 获取当前时间段的开始和结束时间
  const slotTimes = timeSlots[timeIndex].split('-')
  const slotStart = slotTimes[0] // 例如: "08:00"
  const slotEnd = slotTimes[1]   // 例如: "08:50"
  
  return schedules.value.find(schedule => {
    // 检查指定日期是否在日程的时间范围内（支持跨天日程）
    const startDate = new Date(schedule.startTime).toISOString().split('T')[0]
    const endDate = new Date(schedule.endTime).toISOString().split('T')[0]
    
    // 检查目标日期是否在日程的日期范围内
    const isDateInRange = targetDateStr >= startDate && targetDateStr <= endDate
    if (!isDateInRange) return false
    
    // 如果是全天日程，在第一个时间段显示
    if (schedule.isAllDay === 1) {
      return timeIndex === 0
    }
    
    // 对于跨天的非全天日程，需要特殊处理时间检查
    if (startDate !== endDate) {
      // 跨天日程：如果是开始日期，检查时间段是否在开始时间之后
      if (targetDateStr === startDate) {
        const scheduleStart = new Date(schedule.startTime).toTimeString().substring(0, 5)
        return slotStart >= scheduleStart
      }
      // 跨天日程：如果是结束日期，检查时间段是否在结束时间之前
      else if (targetDateStr === endDate) {
        const scheduleEnd = new Date(schedule.endTime).toTimeString().substring(0, 5)
        return slotEnd <= scheduleEnd
      }
      // 跨天日程：如果是中间日期，整天都显示
      else {
        return true
      }
    } else {
      // 同一天的日程：检查时间段是否重叠
      const scheduleStart = new Date(schedule.startTime).toTimeString().substring(0, 5) // "08:00"
      const scheduleEnd = new Date(schedule.endTime).toTimeString().substring(0, 5)     // "09:40"
      
      // 判断时间段是否重叠
      return scheduleStart < slotEnd && scheduleEnd > slotStart
    }
  })
}

// 获取指定时间段的所有项目（课程和日程）
const getItemsForSlot = (timeIndex: number, dayIndex: number) => {
  const items: Array<{type: 'course' | 'schedule', data: any}> = []
  
  // 获取课程
  const course = getCourseForSlot(timeIndex, dayIndex)
  if (course) {
    items.push({ type: 'course', data: course })
  }
  
  // 获取日程
  const schedule = getScheduleForSlot(timeIndex, dayIndex)
  if (schedule) {
    items.push({ type: 'schedule', data: schedule })
  }
  
  return items
}

const previousWeek = () => {
  if (currentWeek.value > 1) {
    currentWeek.value--
  }
}

const nextWeek = () => {
  const maxWeeks = getCurrentSemesterConfig().totalWeeks
  if (currentWeek.value < maxWeeks) {
    currentWeek.value++
  }
}

// 回到当前周
const goToCurrentWeek = () => {
  // 获取当前真实日期对应的学期
  const currentRealSemester = getCurrentSemester()
  const currentRealWeek = getCurrentWeekInSemester(currentRealSemester)
  
  // 检查是否已经在当前学期和周数
  if (currentSemester.value === currentRealSemester && currentWeek.value === currentRealWeek) {
    const config = getCurrentSemesterConfig()
    ElMessage.info(`当前已经是${config.name}第${currentRealWeek}周`)
    return
  }
  
  // 如果当前选择的学期不是真实的当前学期，则切换学期
  if (currentSemester.value !== currentRealSemester) {
    console.log('切换到当前学期:', currentRealSemester)
    currentSemester.value = currentRealSemester
    // 学期切换会触发handleSemesterChange，重新加载课程
    loadCourses()
  }
  
  // 设置到当前学期的当前周
  currentWeek.value = currentRealWeek
  
  console.log('回到今天 - 学期:', currentRealSemester, '周数:', currentRealWeek)
  
  // 显示提示信息
  const config = getCurrentSemesterConfig()
  ElMessage.success(`已切换到${config.name}第${currentRealWeek}周`)
}

const addCourseToSlot = (timeIndex: number, dayIndex: number) => {
  // 预设时间和星期
  courseForm.dayOfWeek = dayIndex + 1
  if (timeIndex < courseTimeOptions.length) {
    courseForm.startTime = courseTimeOptions[timeIndex].value + ':00'
    // 不自动设置结束时间，让用户自己选择
    courseForm.endTime = ''
  }
  // 预设随机颜色
  courseForm.color = getRandomColor()
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
      // 全天日程：使用选择的日期，时间设为00:00:00到23:59:59
      const dateStr = scheduleForm.date
      startTime = `${dateStr}T00:00:00`
      endTime = `${dateStr}T23:59:59`
    } else {
      // 非全天日程：处理日期时间格式
      startTime = scheduleForm.startTime
      endTime = scheduleForm.endTime
      
      // 如果是Date对象，转换为ISO字符串格式
      if (startTime && typeof startTime === 'object') {
        startTime = (startTime as any).toISOString().slice(0, 19)
      }
      if (endTime && typeof endTime === 'object') {
        endTime = (endTime as any).toISOString().slice(0, 19)
      }
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
    
    console.log('保存日程数据:', scheduleData)
    
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
  courseForm.startTime = course.startTime.includes(':') && course.startTime.split(':').length === 3 
    ? course.startTime 
    : course.startTime + ':00'
  courseForm.endTime = course.endTime.includes(':') && course.endTime.split(':').length === 3 
    ? course.endTime 
    : course.endTime + ':00'
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
    // 全天日程：只设置日期
    scheduleForm.date = new Date(schedule.startTime).toISOString().split('T')[0]
    scheduleForm.startTime = ''
    scheduleForm.endTime = ''
  } else {
    // 非全天日程：设置开始和结束时间
    scheduleForm.date = ''
    scheduleForm.startTime = new Date(schedule.startTime)
    scheduleForm.endTime = new Date(schedule.endTime)
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
  courseForm.color = getRandomColor()
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
  scheduleForm.color = getRandomColor()
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

// 监听当前周数变化，触发课程表重新渲染
watch(currentWeek, () => {
  console.log('当前周数变化为:', currentWeek.value)
  // 由于getCourseForSlot方法中使用了currentWeek.value，
  // Vue的响应式系统会自动重新渲染课程表
})

// 页面加载时获取数据
onMounted(async () => {
  console.log('课程表页面加载完成')
  
  // 自动选择当前学期
  currentSemester.value = getCurrentSemester()
  console.log('自动选择学期:', currentSemester.value)
  
  // 自动定位到当前周
  currentWeek.value = getCurrentWeekInSemester(currentSemester.value)
  console.log('自动定位到当前周:', currentWeek.value)
  
  console.log('开始加载课程和日程数据...')
  await loadCourses()
  await loadSchedules()
  
  console.log('数据加载完成，当前日程数据:', schedules.value)
  console.log('今日日程计算结果:', todaySchedules.value)
})

const createTestSchedule = async () => {
  console.log('开始创建测试日程')
  
  const today = new Date()
  const todayStr = today.toISOString().split('T')[0]
  
  const testScheduleData = {
    title: '测试日程',
    description: '这是一个测试日程，用于验证今日日程显示功能',
    location: '测试地点',
    startTime: `${todayStr}T10:00:00`,
    endTime: `${todayStr}T11:00:00`,
    isAllDay: 0,
    reminder: 30,
    color: '#67C23A'
  }
  
  console.log('测试日程数据:', testScheduleData)
  
  try {
    const response = await createSchedule(testScheduleData) as any as ApiResponse<{ scheduleId: number }>
    
    if (response.code === 200) {
      ElMessage.success('测试日程创建成功！')
      await loadSchedules() // 重新加载日程列表
      console.log('重新加载后的日程数据:', schedules.value)
      console.log('重新计算的今日日程:', todaySchedules.value)
    } else {
      ElMessage.error(response.message || '创建测试日程失败')
      console.error('创建测试日程失败:', response)
    }
  } catch (error) {
    console.error('创建测试日程出错:', error)
    ElMessage.error('创建测试日程出错')
  }
}
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

.day-column.current-day {
  background: linear-gradient(135deg, var(--primary-100), var(--primary-200));
  border: 2px solid var(--primary-400);
  border-radius: 8px 8px 0 0;
}

.day-name {
  font-size: 14px;
  margin-bottom: 4px;
}

.day-date {
  font-size: 12px;
  opacity: 0.8;
  position: relative;
}

.day-date.today-date {
  opacity: 1;
  font-weight: 700;
  color: var(--primary-700);
}

.today-indicator {
  display: block;
  font-size: 10px;
  color: var(--primary-600);
  background: var(--primary-200);
  padding: 1px 4px;
  border-radius: 4px;
  margin-top: 2px;
  font-weight: 600;
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

.slot-items {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.slot-items .course-item,
.slot-items .schedule-item {
  flex: 1;
  min-height: 0;
  padding: 4px 6px;
}

.slot-items .course-name,
.slot-items .schedule-name {
  font-size: 10px;
  margin-bottom: 1px;
}

.slot-items .course-location,
.slot-items .course-teacher,
.slot-items .schedule-location,
.slot-items .schedule-time {
  font-size: 8px;
  line-height: 1.1;
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

.schedule-item {
  height: 100%;
  padding: 8px;
  border-radius: 8px;
  border-left: 4px dashed rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: var(--transition-base);
  opacity: 0.9;
}

.schedule-item:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
  opacity: 1;
}

.course-name,
.schedule-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 2px;
}

.course-location,
.course-teacher,
.schedule-location,
.schedule-time {
  font-size: 10px;
  color: var(--gray-600);
  line-height: 1.2;
}
</style> 