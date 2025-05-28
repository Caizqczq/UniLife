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
            <el-select v-model="currentSemester" placeholder="选择学期" size="default">
              <el-option label="2024春季学期" value="2024-1" />
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
        <div class="schedule-table card-light">
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
                <div class="time-period">{{ timeSlot.period }}</div>
                <div class="time-range">{{ timeSlot.time }}</div>
              </div>
              
              <div 
                v-for="(day, dayIndex) in 7" 
                :key="dayIndex"
                class="course-cell"
                @click="addCourseToSlot(timeIndex, dayIndex)"
              >
                <div 
                  v-if="getCourseForSlot(timeIndex, dayIndex)"
                  class="course-item"
                  :style="{ backgroundColor: getCourseForSlot(timeIndex, dayIndex)?.color }"
                  @click.stop="editCourse(getCourseForSlot(timeIndex, dayIndex))"
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
                <div class="schedule-time">{{ schedule.startTime }}</div>
                <div class="schedule-info">
                  <div class="schedule-title">{{ schedule.title }}</div>
                  <div class="schedule-location">{{ schedule.location }}</div>
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
      title="添加课程"
      width="500px"
    >
      <el-form label-position="top">
        <el-form-item label="课程名称">
          <el-input placeholder="请输入课程名称" size="large" />
        </el-form-item>
        
        <el-form-item label="任课教师">
          <el-input placeholder="请输入教师姓名" size="large" />
        </el-form-item>
        
        <el-form-item label="上课地点">
          <el-input placeholder="请输入上课地点" size="large" />
        </el-form-item>
        
        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 16px;">
          <el-form-item label="星期">
            <el-select placeholder="选择星期" size="large">
              <el-option label="星期一" value="1" />
              <el-option label="星期二" value="2" />
              <el-option label="星期三" value="3" />
              <el-option label="星期四" value="4" />
              <el-option label="星期五" value="5" />
              <el-option label="星期六" value="6" />
              <el-option label="星期日" value="0" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="开始时间">
            <el-time-picker
              placeholder="选择时间"
              format="HH:mm"
              value-format="HH:mm"
              size="large"
            />
          </el-form-item>
          
          <el-form-item label="结束时间">
            <el-time-picker
              placeholder="选择时间"
              format="HH:mm"
              value-format="HH:mm"
              size="large"
            />
          </el-form-item>
        </div>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddCourse = false">取消</el-button>
        <el-button type="primary" @click="handleAddCourse">添加</el-button>
      </template>
    </el-dialog>

    <!-- 添加日程对话框 -->
    <el-dialog
      v-model="showAddSchedule"
      title="添加日程"
      width="500px"
    >
      <el-form label-position="top">
        <el-form-item label="日程标题">
          <el-input placeholder="请输入日程标题" size="large" />
        </el-form-item>
        
        <el-form-item label="日期时间">
          <el-date-picker
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            size="large"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="地点">
          <el-input placeholder="请输入地点（可选）" size="large" />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input type="textarea" :rows="3" placeholder="请输入备注信息..." />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddSchedule = false">取消</el-button>
        <el-button type="primary" @click="handleAddSchedule">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus,
  Calendar,
  Setting,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const showAddCourse = ref(false)
const showAddSchedule = ref(false)
const currentSemester = ref('2024-1')
const currentWeek = ref(1)

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const timeSlots = ref([
  { period: '第1节', time: '08:00-08:45' },
  { period: '第2节', time: '08:55-09:40' },
  { period: '第3节', time: '10:00-10:45' },
  { period: '第4节', time: '10:55-11:40' },
  { period: '第5节', time: '14:00-14:45' },
  { period: '第6节', time: '14:55-15:40' },
  { period: '第7节', time: '16:00-16:45' },
  { period: '第8节', time: '16:55-17:40' },
  { period: '第9节', time: '19:00-19:45' },
  { period: '第10节', time: '19:55-20:40' }
])

// 模拟课程数据
const courses = ref([
  {
    id: 1,
    name: '高等数学',
    teacher: '张教授',
    location: 'A101',
    dayOfWeek: 1,
    startTime: '08:00',
    endTime: '09:40',
    timeSlots: [0, 1],
    color: '#e3f2fd'
  },
  {
    id: 2,
    name: '数据结构',
    teacher: '李教授',
    location: 'B203',
    dayOfWeek: 2,
    startTime: '10:00',
    endTime: '11:40',
    timeSlots: [2, 3],
    color: '#f3e5f5'
  },
  {
    id: 3,
    name: '计算机网络',
    teacher: '王教授',
    location: 'C305',
    dayOfWeek: 3,
    startTime: '14:00',
    endTime: '15:40',
    timeSlots: [4, 5],
    color: '#e8f5e8'
  }
])

// 今日课程
const todayCourses = ref([
  {
    id: 1,
    name: '高等数学',
    location: 'A101',
    teacher: '张教授',
    startTime: '08:00',
    endTime: '09:40'
  },
  {
    id: 2,
    name: '英语听力',
    location: 'B205',
    teacher: 'Smith',
    startTime: '14:00',
    endTime: '15:40'
  }
])

// 今日日程
const todaySchedules = ref([
  {
    id: 1,
    title: '小组讨论',
    location: '图书馆201',
    startTime: '16:00'
  },
  {
    id: 2,
    title: '社团活动',
    location: '学生活动中心',
    startTime: '19:00'
  }
])

// 方法
const isToday = (dayIndex: number) => {
  const today = new Date().getDay()
  return dayIndex === (today === 0 ? 6 : today - 1)
}

const getDayDate = (dayIndex: number) => {
  const today = new Date()
  const currentDay = today.getDay() === 0 ? 6 : today.getDay() - 1
  const targetDate = new Date(today)
  targetDate.setDate(today.getDate() + (dayIndex - currentDay))
  return targetDate.getDate()
}

const getCourseForSlot = (timeIndex: number, dayIndex: number) => {
  return courses.value.find(course => 
    course.dayOfWeek === dayIndex + 1 && 
    course.timeSlots.includes(timeIndex)
  )
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
  showAddCourse.value = true
}

const editCourse = (course: any) => {
  ElMessage.info('编辑课程功能开发中...')
}

const handleAddCourse = () => {
  showAddCourse.value = false
  ElMessage.success('课程添加成功！')
}

const handleAddSchedule = () => {
  showAddSchedule.value = false
  ElMessage.success('日程添加成功！')
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

onMounted(() => {
  console.log('课程表页面加载完成')
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
</style> 