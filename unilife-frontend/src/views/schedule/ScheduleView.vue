<template>
  <div class="schedule-container">
    <!-- 使用通用顶部导航栏组件 -->
    <TopNavbar />

    <!-- 主要内容区域 -->
    <div class="schedule-main">
      <div class="schedule-content">
        <!-- 主要内容区域 -->
        <div class="schedule-main-content">
          <!-- 页面标题和操作 -->
          <div class="page-header card-light">
            <div class="header-content">
              <div class="title-section">
                <h1 class="page-title">我的课程表</h1>
                <p class="page-subtitle">管理你的课程安排</p>
              </div>
              
              <div class="header-actions">
                <el-button type="primary" @click="showAddCourse = true">
                  <el-icon><Plus /></el-icon>
                  添加课程
                </el-button>
                <el-button @click="loadCourses">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>
            </div>
            
            <!-- 学期选择和周次导航 -->
            <div class="semester-selector">
              <div class="semester-info">
                <el-select v-model="currentSemester" placeholder="选择学期" size="default" @change="handleSemesterChange" class="semester-select">
                  <el-option 
                    v-for="(config, key) in semesterConfig" 
                    :key="key" 
                    :label="config.name" 
                    :value="key" 
                  />
                </el-select>
              </div>
              
              <div class="week-navigation">
                <div class="week-controls">
                  <el-button @click="previousWeek" circle size="small" class="nav-btn">
                    <el-icon><ArrowLeft /></el-icon>
                  </el-button>
                  
                  <div class="week-info">
                    <span class="week-label">第</span>
                    <el-select 
                      v-model="currentWeek" 
                      placeholder="选择周数" 
                      size="small"
                      class="week-select"
                    >
                      <el-option 
                        v-for="week in getCurrentSemesterConfig().totalWeeks" 
                        :key="week" 
                        :label="week" 
                        :value="week"
                      />
                    </el-select>
                    <span class="week-label">周</span>
                  </div>
                  
                  <el-button @click="nextWeek" circle size="small" class="nav-btn">
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </div>
                
                <el-button @click="goToCurrentWeek" size="small" class="today-btn">
                  <el-icon><Calendar /></el-icon>
                  今天
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
                  <div class="time-period">{{ timeIndex + 1 }}</div>
                  <div class="time-range">{{ getTimeRange(timeIndex) }}</div>
                </div>
                
                <div 
                  v-for="dayIndex in 7" 
                  :key="dayIndex"
                  class="course-cell"
                  @click="addCourseToSlot(timeIndex, dayIndex - 1)"
                >
                  <!-- 显示课程 -->
                  <div v-if="getCourseForSlot(timeIndex, dayIndex - 1)" 
                       class="course-item" 
                       :style="getCourseStyle(getCourseForSlot(timeIndex, dayIndex - 1))"
                       @click.stop="editCourse(getCourseForSlot(timeIndex, dayIndex - 1)!)">
                    <div class="course-name">{{ getCourseForSlot(timeIndex, dayIndex - 1)?.name }}</div>
                    <div class="course-location">{{ getCourseForSlot(timeIndex, dayIndex - 1)?.location }}</div>
                    <div class="course-teacher">{{ getCourseForSlot(timeIndex, dayIndex - 1)?.teacher }}</div>
                  </div>
                  
                  <!-- 空白时间段 -->
                  <div v-else class="empty-slot">
                    <el-icon class="add-icon"><Plus /></el-icon>
                    <span class="add-text">点击添加</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧边栏 - 今日课程 -->
        <div class="schedule-sidebar">
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
        </div>
      </div>
    </div>

    <!-- 添加课程对话框 -->
    <el-dialog
      v-model="showAddCourse"
      :title="editingCourse ? '编辑课程' : '添加课程'"
      width="500px"
      @closed="resetCourseForm"
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
  ArrowRight,
  Refresh
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
import type { ApiResponse } from '@/types'
import TopNavbar from '@/components/TopNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const savingCourse = ref(false)
const showAddCourse = ref(false)
const currentSemester = ref('2024-2025-1')
const currentWeek = ref(1)

// 编辑状态
const editingCourse = ref<Course | null>(null)

// 数据列表
const courses = ref<Course[]>([])

// 表单引用
const courseFormRef = ref<FormInstance>()

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

// 开始时间选项
const startTimeOptions = courseTimeOptions.map(option => ({
  label: `${option.value} (${option.label.split(' ')[0]}开始)`,
  value: option.value + ':00'
}))

// 结束时间选项
const endTimeOptions = courseTimeOptions.map(option => ({
  label: `${option.endValue} (${option.label.split(' ')[0]}结束)`,
  value: option.endValue + ':00'
}))

// 课程颜色预设
const courseColors = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#9C27B0',
  '#FF5722', '#4CAF50', '#2196F3', '#FF9800', '#795548', '#607D8B'
]

// 生成随机颜色
const getRandomColor = () => {
  return courseColors[Math.floor(Math.random() * courseColors.length)]
}

// 计算属性
const todayCourses = computed(() => {
  const today = new Date().getDay()
  const dayOfWeek = today === 0 ? 7 : today
  return courses.value.filter(course => course.dayOfWeek === dayOfWeek)
})

// 学期配置
const semesterConfig = {
  '2024-2025-1': {
    name: '2024-2025第一学期',
    startDate: new Date(2024, 8, 2),
    endDate: new Date(2025, 0, 19),
    totalWeeks: 20
  },
  '2024-2025-2': {
    name: '2024-2025第二学期', 
    startDate: new Date(2025, 1, 24),
    endDate: new Date(2025, 5, 29),
    totalWeeks: 18
  },
  '2023-2024-1': {
    name: '2023-2024第一学期',
    startDate: new Date(2023, 8, 4),
    endDate: new Date(2024, 0, 21),
    totalWeeks: 20
  },
  '2023-2024-2': {
    name: '2023-2024第二学期',
    startDate: new Date(2024, 1, 26),
    endDate: new Date(2024, 5, 30),
    totalWeeks: 18
  }
}

// 方法
const getCurrentSemester = () => {
  const now = new Date()
  for (const [semesterKey, config] of Object.entries(semesterConfig)) {
    if (now >= config.startDate && now <= config.endDate) {
      return semesterKey
    }
  }
  return '2024-2025-1'
}

const getCurrentWeekInSemester = (semester: string) => {
  const config = semesterConfig[semester as keyof typeof semesterConfig]
  if (!config) return 1
  
  const now = new Date()
  if (now < config.startDate || now > config.endDate) {
    return 1
  }
  
  const diffTime = now.getTime() - config.startDate.getTime()
  const diffWeeks = Math.floor(diffTime / (7 * 24 * 60 * 60 * 1000)) + 1
  return Math.max(1, Math.min(diffWeeks, config.totalWeeks))
}

const getWeekMondayDate = (semester: string, week: number) => {
  const config = semesterConfig[semester as keyof typeof semesterConfig]
  if (!config) return new Date()
  
  const mondayDate = new Date(config.startDate)
  mondayDate.setDate(config.startDate.getDate() + (week - 1) * 7)
  return mondayDate
}

const getCurrentSemesterConfig = () => {
  return semesterConfig[currentSemester.value as keyof typeof semesterConfig] || semesterConfig['2024-2025-1']
}

const handleSemesterChange = (newSemester: string) => {
  console.log('学期切换到:', newSemester)
  currentWeek.value = 1
  loadCourses()
}

const getDayDate = (dayIndex: number) => {
  const monday = getWeekMondayDate(currentSemester.value, currentWeek.value)
  const targetDate = new Date(monday)
  targetDate.setDate(monday.getDate() + dayIndex)
  
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
    if (course.dayOfWeek !== dayIndex + 1) return false
    if (currentWeek.value < course.startWeek || currentWeek.value > course.endWeek) {
      return false
    }
    
    const slotTimes = timeSlots[timeIndex].split('-')
    const slotStart = slotTimes[0]
    const slotEnd = slotTimes[1]
    
    const courseStart = course.startTime.substring(0, 5)
    const courseEnd = course.endTime.substring(0, 5)
    
    return courseStart <= slotStart && slotEnd <= courseEnd
  })
}

const getTimeRange = (timeIndex: number) => {
  if (timeIndex < timeSlots.length) {
    return timeSlots[timeIndex]
  }
  return ''
}

const getCourseStyle = (course: Course | undefined) => {
  if (!course?.color) {
    return {
      '--course-color': '#6366f1',
      '--course-color-light': '#818cf8'
    }
  }
  
  // 根据主色生成稍浅的颜色
  const lighterColor = adjustColorBrightness(course.color, 20)
  
  return {
    '--course-color': course.color,
    '--course-color-light': lighterColor
  }
}

// 辅助函数：调整颜色亮度
const adjustColorBrightness = (color: string, percent: number) => {
  const hex = color.replace('#', '')
  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)
  
  const newR = Math.min(255, Math.max(0, r + (r * percent / 100)))
  const newG = Math.min(255, Math.max(0, g + (g * percent / 100)))
  const newB = Math.min(255, Math.max(0, b + (b * percent / 100)))
  
  return `#${Math.round(newR).toString(16).padStart(2, '0')}${Math.round(newG).toString(16).padStart(2, '0')}${Math.round(newB).toString(16).padStart(2, '0')}`
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

const goToCurrentWeek = () => {
  const currentRealSemester = getCurrentSemester()
  const currentRealWeek = getCurrentWeekInSemester(currentRealSemester)
  
  if (currentSemester.value === currentRealSemester && currentWeek.value === currentRealWeek) {
    const config = getCurrentSemesterConfig()
    ElMessage.info(`当前已经是${config.name}第${currentRealWeek}周`)
    return
  }
  
  if (currentSemester.value !== currentRealSemester) {
    currentSemester.value = currentRealSemester
    loadCourses()
  }
  
  currentWeek.value = currentRealWeek
  const config = getCurrentSemesterConfig()
  ElMessage.success(`已切换到${config.name}第${currentRealWeek}周`)
}

const addCourseToSlot = (timeIndex: number, dayIndex: number) => {
  courseForm.dayOfWeek = dayIndex + 1
  if (timeIndex < courseTimeOptions.length) {
    courseForm.startTime = courseTimeOptions[timeIndex].value + ':00'
    courseForm.endTime = ''
  }
  courseForm.color = getRandomColor()
  showAddCourse.value = true
}

const loadCourses = async () => {
  try {
    loading.value = true
    const response = await getCoursesBySemester(currentSemester.value) as any as ApiResponse<{
      total: number
      list: Course[]
      pages: number
    }>
    
    if (response.code === 200) {
      courses.value = response.data.list
      console.log('课程列表加载成功:', courses.value)
    } else {
      ElMessage.error(response.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
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
      loadCourses()
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

const cancelEditCourse = () => {
  showAddCourse.value = false
  resetCourseForm()
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

// 监听当前周数变化
watch(currentWeek, () => {
  console.log('当前周数变化为:', currentWeek.value)
})

// 页面加载时获取数据
onMounted(async () => {
  console.log('课程表页面加载完成')
  
  currentSemester.value = getCurrentSemester()
  currentWeek.value = getCurrentWeekInSemester(currentSemester.value)
  
  await loadCourses()
})
</script>

<style scoped>
.schedule-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 主要内容区域 */
.schedule-main {
  padding: 16px;
  height: calc(100vh - 88px);
  overflow: hidden;
}

.schedule-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 16px;
}

.schedule-main-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-height: 0;
}

.schedule-sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 页面标题 */
.page-header {
  padding: 6px 12px;
  border-radius: 6px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.title-section h1 {
  font-size: 1rem;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0;
}

.page-subtitle {
  display: none;
}

.header-actions {
  display: flex;
  gap: 6px;
}

.semester-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.semester-selector:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.semester-info {
  flex: 1;
}

.semester-select {
  width: 100%;
}

.semester-select .el-input__wrapper {
  border-radius: 12px !important;
  border: 1px solid #e2e8f0 !important;
  transition: all 0.2s ease !important;
}

.semester-select .el-input__wrapper:hover {
  border-color: #94a3b8 !important;
}

.week-navigation {
  display: flex;
  align-items: center;
  gap: 16px;
}

.week-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.nav-btn {
  background: white !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
  width: 32px !important;
  height: 32px !important;
}

.nav-btn:hover {
  background: #f1f5f9 !important;
  border-color: #94a3b8 !important;
  color: #334155 !important;
  transform: scale(1.05);
}

.nav-btn .el-icon {
  color: #64748b !important;
}

.week-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.week-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-700);
}

.week-select {
  width: 80px;
}

.week-select .el-input__wrapper {
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
  background: white !important;
  transition: all 0.2s ease !important;
}

.week-select .el-input__wrapper:hover {
  border-color: #94a3b8 !important;
}

.today-btn {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0) !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
  border-radius: 10px !important;
  padding: 8px 16px !important;
}

.today-btn:hover {
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1) !important;
  border-color: #94a3b8 !important;
  color: #334155 !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.today-btn .el-icon {
  color: #3b82f6 !important;
}

/* 课程表样式 */
.schedule-table {
  padding: 0;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: var(--shadow-light);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-bottom: 1px solid var(--gray-200);
  flex-shrink: 0;
  z-index: 10;
}

.time-column {
  padding: 8px 4px;
  text-align: center;
  font-weight: 700;
  color: var(--gray-600);
  font-size: 11px;
  background: #f8fafc;
  border-right: 1px solid var(--gray-200);
}

.day-column {
  padding: 6px 4px;
  text-align: center;
  font-weight: 600;
  color: var(--gray-700);
  border-right: 1px solid var(--gray-200);
  transition: var(--transition-base);
}

.day-column.today {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
}

.day-column.current-day {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1d4ed8;
  position: relative;
}

.day-column.current-day::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #3b82f6;
  border-radius: 2px;
}

.day-name {
  font-size: 12px;
  margin-bottom: 2px;
  font-weight: 600;
}

.day-date {
  font-size: 10px;
  opacity: 0.8;
  position: relative;
}

.day-date.today-date {
  opacity: 1;
  font-weight: 700;
  color: #1d4ed8;
}

.today-indicator {
  display: inline-block;
  font-size: 8px;
  color: #ffffff;
  background: #3b82f6;
  padding: 1px 4px;
  border-radius: 6px;
  margin-left: 2px;
  font-weight: 600;
}

.table-body {
  display: flex;
  flex-direction: column;
  background: white;
  flex: 1;
}

.time-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  border-bottom: 1px solid #f1f5f9;
  flex: 1;
  min-height: 35px;
}

.time-row:last-child {
  border-bottom: none;
}

.time-cell {
  padding: 6px 3px;
  text-align: center;
  background: #fafbfc;
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.time-period {
  font-size: 12px;
  font-weight: 700;
  color: var(--gray-700);
  line-height: 1;
  margin-bottom: 1px;
}

.time-range {
  font-size: 7px;
  color: var(--gray-500);
  margin-top: 1px;
}

.course-cell {
  padding: 2px;
  border-right: 1px solid #f1f5f9;
  cursor: pointer;
  transition: var(--transition-base);
  position: relative;
  display: flex;
  align-items: stretch;
  justify-content: stretch;
}

.course-cell:hover {
  background: rgba(59, 130, 246, 0.05);
}

.course-item {
  width: 100%;
  height: 100%;
  padding: 6px 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: var(--transition-base);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, var(--course-color, #6366f1), var(--course-color-light, #818cf8));
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.course-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: rgba(255, 255, 255, 0.6);
}

.course-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.course-name {
  font-size: 11px;
  font-weight: 700;
  color: white;
  margin-bottom: 2px;
  line-height: 1.1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  text-align: center;
  word-break: break-word;
}

.course-location {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 1px;
  display: block;
  text-align: center;
  word-break: break-word;
}

.course-location::before {
  content: '📍 ';
  font-size: 8px;
}

.course-teacher {
  font-size: 9px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.1;
  text-align: center;
  word-break: break-word;
}

.empty-slot {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: var(--transition-base);
  border: 2px dashed transparent;
  border-radius: 6px;
  color: #94a3b8;
  font-size: 12px;
  text-align: center;
  position: relative;
}

.course-cell:hover .empty-slot {
  opacity: 1;
  border-color: #cbd5e1;
  background: rgba(59, 130, 246, 0.05);
}

.empty-slot .add-text {
  font-size: 10px;
  color: #64748b;
  margin-top: 2px;
  opacity: 0;
  transition: var(--transition-base);
}

.course-cell:hover .empty-slot .add-text {
  opacity: 1;
}

.add-icon {
  color: #94a3b8;
  font-size: 16px;
}

/* 今日课程 */
.today-section {
  width: 100%;
}

.today-courses {
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
  height: 100%;
  overflow-y: auto;
}

.section-title {
  color: var(--gray-800);
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '📅';
  font-size: 18px;
}

.courses-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: var(--transition-base);
}

.course-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.course-time {
  font-size: 12px;
  font-weight: 700;
  color: #3b82f6;
  background: #dbeafe;
  padding: 4px 8px;
  border-radius: 6px;
  text-align: center;
  align-self: flex-start;
}

.course-info .course-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 4px;
}

.course-details {
  font-size: 12px;
  color: var(--gray-600);
}

.course-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-message {
  text-align: center;
  color: var(--gray-500);
  font-size: 14px;
  padding: 40px 20px;
  background: #f8fafc;
  border-radius: 8px;
  border: 2px dashed #cbd5e1;
}

/* 表单行样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .schedule-content {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .schedule-sidebar {
    order: -1;
    height: auto;
  }
  
  .today-courses {
    height: auto;
    overflow-y: visible;
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
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style> 