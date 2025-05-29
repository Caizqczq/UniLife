<template>
  <div class="task-container">
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
          <router-link to="/schedule" class="nav-item">课程表</router-link>
          <router-link to="/tasks" class="nav-item active">日程管理</router-link>
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
    <div class="task-main">
      <div class="task-content">
        <!-- 页面标题和操作 -->
        <div class="page-header card-light">
          <div class="header-content">
            <div class="title-section">
              <h1 class="page-title">日程管理</h1>
              <p class="page-subtitle">管理你的个人日程和任务安排</p>
            </div>
            
            <div class="header-actions">
              <el-button type="primary" @click="showAddSchedule = true">
                <el-icon><Plus /></el-icon>
                新建日程
              </el-button>
              <el-button @click="refreshSchedules">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </div>
          
          <!-- 视图切换和日期选择 -->
          <div class="view-controls">
            <div class="view-tabs">
              <el-radio-group v-model="currentView" @change="handleViewChange">
                <el-radio-button label="today">今日日程</el-radio-button>
                <el-radio-button label="list">列表视图</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </div>

        <!-- 今日日程视图 -->
        <div v-if="currentView === 'today'" class="today-view">
          <div class="today-schedules card-light">
            <div class="today-header">
              <h2 class="today-title">
                <el-icon><Calendar /></el-icon>
                今日日程
                <span class="today-date">{{ formatDate(new Date()) }}</span>
              </h2>
              <el-button type="primary" size="small" @click="showAddSchedule = true">
                <el-icon><Plus /></el-icon>
                添加日程
              </el-button>
            </div>
            
            <div v-loading="loading" class="schedules-timeline">
              <div v-if="todaySchedules.length > 0" class="timeline-items">
                <div 
                  v-for="schedule in todaySchedules" 
                  :key="schedule.id"
                  class="timeline-item"
                  :style="{ borderLeftColor: schedule.color }"
                >
                  <div class="timeline-time">
                    <template v-if="schedule.isAllDay === 1">
                      <span class="all-day-badge">全天</span>
                    </template>
                    <template v-else>
                      <div class="time-start">{{ formatTime(schedule.startTime) }}</div>
                      <div class="time-end">{{ formatTime(schedule.endTime) }}</div>
                    </template>
                  </div>
                  
                  <div class="timeline-content">
                    <div class="schedule-title">{{ schedule.title }}</div>
                    <div class="schedule-location" v-if="schedule.location">
                      <el-icon><Location /></el-icon>
                      {{ schedule.location }}
                    </div>
                    <div class="schedule-description" v-if="schedule.description">
                      {{ schedule.description }}
                    </div>
                  </div>
                  
                  <div class="timeline-actions">
                    <el-button size="small" @click="editSchedule(schedule)" type="primary" text>
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button size="small" @click="deleteScheduleConfirm(schedule)" type="danger" text>
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-today">
                <el-icon class="empty-icon"><Calendar /></el-icon>
                <div class="empty-text">今天暂无日程安排</div>
                <el-button type="primary" @click="showAddSchedule = true">添加第一个日程</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else-if="currentView === 'list'" class="list-view">
          <div class="list-container card-light">
            <div class="list-header">
              <div class="list-title">所有日程</div>
              <div class="list-filters">
                <el-select v-model="filterType" placeholder="筛选类型" size="small" style="width: 120px;">
                  <el-option label="全部" value="all" />
                  <el-option label="全天" value="allday" />
                  <el-option label="定时" value="timed" />
                </el-select>
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  size="small"
                  @change="handleDateRangeChange"
                />
              </div>
            </div>
            
            <div v-loading="loading" class="schedule-list">
              <div v-if="filteredSchedules.length > 0" class="list-items">
                <div 
                  v-for="schedule in filteredSchedules" 
                  :key="schedule.id"
                  class="list-item"
                  :style="{ borderLeftColor: schedule.color }"
                >
                  <div class="item-date">
                    <div class="date-day">{{ formatDay(schedule.startTime) }}</div>
                    <div class="date-month">{{ formatMonth(schedule.startTime) }}</div>
                  </div>
                  
                  <div class="item-content">
                    <div class="item-title">{{ schedule.title }}</div>
                    <div class="item-meta">
                      <span class="item-time">
                        <template v-if="schedule.isAllDay === 1">
                          <el-tag size="small" type="success">全天</el-tag>
                        </template>
                        <template v-else>
                          {{ formatTime(schedule.startTime) }} - {{ formatTime(schedule.endTime) }}
                        </template>
                      </span>
                      <span v-if="schedule.location" class="item-location">
                        <el-icon><Location /></el-icon>
                        {{ schedule.location }}
                      </span>
                    </div>
                    <div v-if="schedule.description" class="item-description">
                      {{ schedule.description }}
                    </div>
                  </div>
                  
                  <div class="item-actions">
                    <el-button size="small" @click="editSchedule(schedule)" type="primary" text>
                      编辑
                    </el-button>
                    <el-button size="small" @click="deleteScheduleConfirm(schedule)" type="danger" text>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-list">
                <el-icon class="empty-icon"><Calendar /></el-icon>
                <div class="empty-text">暂无日程安排</div>
                <el-button type="primary" @click="showAddSchedule = true">创建第一个日程</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑日程对话框 -->
    <el-dialog
      v-model="showAddSchedule"
      :title="editingSchedule ? '编辑日程' : '新建日程'"
      width="500px"
      @closed="resetScheduleForm"
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
        
        <el-form-item label="全天事件">
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
          <el-form-item label="提醒设置" prop="reminder">
            <el-select v-model="scheduleForm.reminder" placeholder="选择提醒时间" style="width: 100%">
              <el-option label="不提醒" :value="0" />
              <el-option label="事件开始时" :value="0" />
              <el-option label="5分钟前" :value="5" />
              <el-option label="15分钟前" :value="15" />
              <el-option label="30分钟前" :value="30" />
              <el-option label="1小时前" :value="60" />
              <el-option label="2小时前" :value="120" />
              <el-option label="1天前" :value="1440" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="标签颜色" prop="color">
            <el-color-picker v-model="scheduleForm.color" size="large" />
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
  ArrowRight,
  Refresh,
  Edit,
  Delete,
  Location
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
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
const savingSchedule = ref(false)
const showAddSchedule = ref(false)
const currentView = ref('today') // 'today', 'list'
const dateRange = ref<[Date, Date] | null>(null)
const filterType = ref('all')

// 编辑状态
const editingSchedule = ref<Schedule | null>(null)

// 数据列表
const schedules = ref<Schedule[]>([])

// 表单引用
const scheduleFormRef = ref<FormInstance>()

// 日程表单
const scheduleForm = reactive<{
  title: string
  description: string
  location: string
  startTime: string | Date
  endTime: string | Date
  date: string | Date
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
  color: '#409EFF'
})

// 表单验证规则
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
const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

// 计算属性
const todaySchedules = computed(() => {
  const today = new Date()
  // 使用本地时间格式，避免时区问题
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  const todayStr = `${year}-${month}-${day}`
  
  console.log('=== 今日日程详细调试 ===')
  console.log('浏览器当前时间:', today)
  console.log('计算的今天日期字符串:', todayStr)
  console.log('所有日程数量:', schedules.value.length)
  
  if (schedules.value.length > 0) {
    console.log('所有日程数据:', schedules.value)
  }
  
  const filtered = schedules.value.filter(schedule => {
    // 处理日程的开始和结束时间，提取日期部分
    let startDate, endDate
    
    if (schedule.startTime && schedule.endTime) {
      // 从时间字符串中提取日期部分（YYYY-MM-DD）
      startDate = schedule.startTime.split('T')[0] || schedule.startTime.split(' ')[0]
      endDate = schedule.endTime.split('T')[0] || schedule.endTime.split(' ')[0]
      
      console.log(`检查日程 "${schedule.title}":`)
      console.log(`  开始日期: ${startDate}`)
      console.log(`  结束日期: ${endDate}`)
      console.log(`  今天日期: ${todayStr}`)
      console.log(`  条件1 (今天 >= 开始): ${todayStr} >= ${startDate} = ${todayStr >= startDate}`)
      console.log(`  条件2 (今天 <= 结束): ${todayStr} <= ${endDate} = ${todayStr <= endDate}`)
      
      // 检查今天是否在日程的日期范围内
      const isInRange = todayStr >= startDate && todayStr <= endDate
      console.log(`  最终结果: ${isInRange}`)
      
      return isInRange
    }
    
    console.log(`日程 "${schedule.title}" 缺少时间信息，跳过`)
    return false
  })
  
  console.log('筛选后的今日日程数量:', filtered.length)
  console.log('筛选后的今日日程:', filtered)
  console.log('=== 调试结束 ===')
  
  return filtered.sort((a, b) => {
    if (a.isAllDay === 1 && b.isAllDay !== 1) return -1
    if (b.isAllDay === 1 && a.isAllDay !== 1) return 1
    return new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
  })
})

const filteredSchedules = computed(() => {
  let filtered = [...schedules.value]
  
  // 按类型筛选
  if (filterType.value === 'allday') {
    filtered = filtered.filter(s => s.isAllDay === 1)
  } else if (filterType.value === 'timed') {
    filtered = filtered.filter(s => s.isAllDay !== 1)
  }
  
  // 按日期范围筛选
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    const [start, end] = dateRange.value
    filtered = filtered.filter(schedule => {
      const scheduleDate = new Date(schedule.startTime)
      return scheduleDate >= start && scheduleDate <= end
    })
  }
  
  // 按开始时间排序
  return filtered.sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
})

// 方法
const loadSchedules = async () => {
  try {
    loading.value = true
    const response = await getSchedules() as any as ApiResponse<{
      total: number
      list: Schedule[]
      pages: number
    }>
    
    if (response.code === 200) {
      schedules.value = response.data.list
      console.log('日程列表加载成功:', schedules.value)
    } else {
      ElMessage.error(response.message || '获取日程列表失败')
    }
  } catch (error) {
    console.error('获取日程列表失败:', error)
    ElMessage.error('获取日程列表失败')
  } finally {
    loading.value = false
  }
}

const refreshSchedules = () => {
  loadSchedules()
}

const handleViewChange = (view: string) => {
  console.log('切换视图:', view)
}

const handleDateRangeChange = () => {
  console.log('日期范围变化:', dateRange.value)
}

const handleSaveSchedule = async () => {
  if (!scheduleFormRef.value) return
  
  try {
    await scheduleFormRef.value.validate()
    savingSchedule.value = true
    
    let startTime, endTime
    if (scheduleForm.isAllDay) {
      // 全天日程：使用选择的日期，时间设为00:00:00到23:59:59
      let dateStr: string
      if (scheduleForm.date instanceof Date) {
        // 使用本地时间，避免时区问题
        const year = scheduleForm.date.getFullYear()
        const month = String(scheduleForm.date.getMonth() + 1).padStart(2, '0')
        const day = String(scheduleForm.date.getDate()).padStart(2, '0')
        dateStr = `${year}-${month}-${day}`
      } else {
        dateStr = scheduleForm.date
      }
      startTime = `${dateStr} 00:00:00`
      endTime = `${dateStr} 23:59:59`
    } else {
      // 非全天日程：处理日期时间格式
      startTime = scheduleForm.startTime
      endTime = scheduleForm.endTime
      
      // 如果是Date对象，转换为数据库兼容格式（YYYY-MM-DD HH:mm:ss）
      if (startTime && typeof startTime === 'object') {
        const date = startTime as Date
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        startTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      if (endTime && typeof endTime === 'object') {
        const date = endTime as Date
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        endTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
    }
    
    console.log('准备保存的日程数据:', {
      title: scheduleForm.title,
      startTime,
      endTime,
      isAllDay: scheduleForm.isAllDay ? 1 : 0
    })
    
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
      loadSchedules()
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
    scheduleForm.date = new Date(schedule.startTime)
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

const cancelEditSchedule = () => {
  showAddSchedule.value = false
  resetScheduleForm()
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
  scheduleForm.color = '#409EFF'
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

const formatDate = (date: Date) => {
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
}

const formatTime = (dateTimeString: string) => {
  const date = new Date(dateTimeString)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDay = (dateTimeString: string) => {
  const date = new Date(dateTimeString)
  return date.getDate().toString().padStart(2, '0')
}

const formatMonth = (dateTimeString: string) => {
  const date = new Date(dateTimeString)
  return (date.getMonth() + 1).toString() + '月'
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 页面加载时获取数据
onMounted(async () => {
  console.log('日程管理页面加载完成')
  await loadSchedules()
})
</script>

<style scoped>
.task-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 导航栏样式 */
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
.task-main {
  padding: 32px 24px;
}

.task-content {
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

.view-controls {
  display: flex;
  justify-content: center;
  align-items: center;
}

.view-tabs {
  display: flex;
  gap: 12px;
}

/* 今日日程视图 */
.today-view {
  margin-top: 24px;
}

.today-schedules {
  padding: 32px;
}

.today-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.today-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0;
}

.today-date {
  font-size: 16px;
  font-weight: 400;
  color: var(--gray-600);
}

.schedules-timeline {
  max-height: 600px;
  overflow-y: auto;
}

.timeline-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: var(--gray-50);
  border-radius: 12px;
  border-left: 4px solid var(--primary-400);
  transition: var(--transition-base);
}

.timeline-item:hover {
  background: var(--primary-50);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.timeline-time {
  min-width: 80px;
  text-align: center;
}

.all-day-badge {
  background: var(--success-100);
  color: var(--success-700);
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.time-start, .time-end {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-700);
}

.time-end {
  font-size: 12px;
  color: var(--gray-500);
  margin-top: 2px;
}

.timeline-content {
  flex: 1;
}

.schedule-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 8px;
}

.schedule-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--gray-600);
  margin-bottom: 8px;
}

.schedule-description {
  font-size: 14px;
  color: var(--gray-600);
  line-height: 1.5;
}

.timeline-actions {
  display: flex;
  gap: 8px;
}

.empty-today {
  text-align: center;
  padding: 80px 20px;
  color: var(--gray-500);
}

.empty-icon {
  font-size: 64px;
  color: var(--gray-300);
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  margin-bottom: 24px;
}

/* 列表视图 */
.list-view {
  margin-top: 24px;
}

.list-container {
  padding: 32px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.list-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--gray-800);
}

.list-filters {
  display: flex;
  gap: 12px;
}

.schedule-list {
  max-height: 600px;
  overflow-y: auto;
}

.list-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: var(--gray-50);
  border-radius: 12px;
  border-left: 4px solid var(--primary-400);
  transition: var(--transition-base);
}

.list-item:hover {
  background: var(--primary-50);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.item-date {
  min-width: 60px;
  text-align: center;
}

.date-day {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-600);
  line-height: 1;
}

.date-month {
  font-size: 12px;
  color: var(--gray-500);
  margin-top: 4px;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 8px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}

.item-time {
  font-size: 14px;
  color: var(--gray-600);
}

.item-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--gray-600);
}

.item-description {
  font-size: 14px;
  color: var(--gray-600);
  line-height: 1.5;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.empty-list {
  text-align: center;
  padding: 80px 20px;
  color: var(--gray-500);
}

/* 表单样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .view-controls {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .today-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .list-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .task-main {
    padding: 16px;
  }
  
  .task-content {
    gap: 16px;
  }
  
  .page-header {
    padding: 24px 16px;
  }
  
  .today-schedules,
  .list-container {
    padding: 24px 16px;
  }
  
  .timeline-item,
  .list-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style> 