<template>
  <div class="schedule-container">
    <div class="schedule-header">
      <h1 class="page-title">日程管理</h1>
      <div class="schedule-actions">
        <el-button type="primary" @click="handleAddSchedule" v-if="isLoggedIn">
          <el-icon><Plus /></el-icon>添加日程
        </el-button>
      </div>
    </div>

    <div class="schedule-content">
      <el-row :gutter="20">
        <!-- 日历视图 -->
        <el-col :span="16">
          <el-card shadow="hover" class="calendar-card">
            <div class="calendar-header">
              <div class="month-selector">
                <el-button-group>
                  <el-button @click="prevMonth">
                    <el-icon><ArrowLeft /></el-icon>
                  </el-button>
                  <el-button>{{ currentYearMonth }}</el-button>
                  <el-button @click="nextMonth">
                    <el-icon><ArrowRight /></el-icon>
                  </el-button>
                </el-button-group>
              </div>
              <div class="view-selector">
                <el-radio-group v-model="calendarView" size="small">
                  <el-radio-button label="month">月视图</el-radio-button>
                  <el-radio-button label="week">周视图</el-radio-button>
                  <el-radio-button label="day">日视图</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="calendar-body">
              <!-- 月视图 -->
              <div v-if="calendarView === 'month'" class="month-view">
                <!-- 星期标题 -->
                <div class="week-header">
                  <div v-for="day in weekDays" :key="day" class="week-day">{{ day }}</div>
                </div>

                <!-- 日期网格 -->
                <div class="month-grid">
                  <div
                    v-for="(date, index) in calendarDays"
                    :key="index"
                    class="date-cell"
                    :class="{
                      'other-month': date.otherMonth,
                      'today': isToday(date.date),
                      'has-events': hasEvents(date.date)
                    }"
                    @click="selectDate(date.date)"
                  >
                    <div class="date-number">{{ date.day }}</div>
                    <div class="date-events">
                      <div
                        v-for="event in getEventsForDate(date.date)"
                        :key="event.id"
                        class="event-dot"
                        :style="{ backgroundColor: event.color || '#409EFF' }"
                        :title="event.title"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 周视图 -->
              <div v-else-if="calendarView === 'week'" class="week-view">
                <!-- 星期标题 -->
                <div class="week-header">
                  <div class="time-column-header"></div>
                  <div
                    v-for="(date, index) in weekDates"
                    :key="index"
                    class="week-day-header"
                    :class="{ 'today': isToday(date) }"
                  >
                    <div>{{ weekDays[index] }}</div>
                    <div>{{ formatDate(date, 'MM-DD') }}</div>
                  </div>
                </div>

                <!-- 时间网格 -->
                <div class="week-grid">
                  <div class="time-column">
                    <div
                      v-for="hour in 24"
                      :key="hour"
                      class="time-cell"
                    >
                      {{ formatHour(hour - 1) }}
                    </div>
                  </div>

                  <div
                    v-for="(date, dayIndex) in weekDates"
                    :key="dayIndex"
                    class="day-column"
                  >
                    <div
                      v-for="hour in 24"
                      :key="hour"
                      class="hour-cell"
                      @click="handleAddScheduleAt(date, hour - 1)"
                    >
                      <div
                        v-for="event in getEventsForDateAndHour(date, hour - 1)"
                        :key="event.id"
                        class="event-item"
                        :style="{
                          backgroundColor: event.color || '#409EFF',
                          top: calculateEventTop(event, hour - 1) + 'px',
                          height: calculateEventHeight(event) + 'px'
                        }"
                        @click.stop="handleEventClick(event)"
                      >
                        <div class="event-title">{{ event.title }}</div>
                        <div class="event-time">{{ formatEventTime(event) }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 日视图 -->
              <div v-else class="day-view">
                <!-- 日期标题 -->
                <div class="day-header">
                  <div class="time-column-header"></div>
                  <div
                    class="day-date-header"
                    :class="{ 'today': isToday(selectedDate) }"
                  >
                    <div>{{ formatDate(selectedDate, 'YYYY年MM月DD日') }}</div>
                    <div>{{ weekDays[new Date(selectedDate).getDay()] }}</div>
                  </div>
                </div>

                <!-- 时间网格 -->
                <div class="day-grid">
                  <div class="time-column">
                    <div
                      v-for="hour in 24"
                      :key="hour"
                      class="time-cell"
                    >
                      {{ formatHour(hour - 1) }}
                    </div>
                  </div>

                  <div class="day-events-column">
                    <div
                      v-for="hour in 24"
                      :key="hour"
                      class="hour-cell"
                      @click="handleAddScheduleAt(selectedDate, hour - 1)"
                    >
                      <div
                        v-for="event in getEventsForDateAndHour(selectedDate, hour - 1)"
                        :key="event.id"
                        class="event-item day-event-item"
                        :style="{
                          backgroundColor: event.color || '#409EFF',
                          top: calculateEventTop(event, hour - 1) + 'px',
                          height: calculateEventHeight(event) + 'px'
                        }"
                        @click.stop="handleEventClick(event)"
                      >
                        <div class="event-title">{{ event.title }}</div>
                        <div class="event-time">{{ formatEventTime(event) }}</div>
                        <div v-if="event.location" class="event-location">
                          <el-icon><Location /></el-icon> {{ event.location }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 日程列表 -->
        <el-col :span="8">
          <el-card shadow="hover" class="events-card">
            <template #header>
              <div class="events-header">
                <span>{{ formatDate(selectedDate, 'YYYY年MM月DD日') }} 的日程</span>
                <el-button
                  type="primary"
                  size="small"
                  @click="handleAddScheduleAt(selectedDate)"
                  v-if="isLoggedIn"
                >
                  <el-icon><Plus /></el-icon>添加
                </el-button>
              </div>
            </template>

            <div class="events-list">
              <el-empty v-if="selectedDateEvents.length === 0" description="暂无日程" />

              <el-timeline v-else>
                <el-timeline-item
                  v-for="event in selectedDateEvents"
                  :key="event.id"
                  :color="event.color || '#409EFF'"
                  :timestamp="formatEventTime(event)"
                >
                  <el-card class="event-card" @click="handleEventClick(event)">
                    <h4>{{ event.title }}</h4>
                    <p v-if="event.location">
                      <el-icon><Location /></el-icon> {{ event.location }}
                    </p>
                    <p v-if="event.description" class="event-description">
                      {{ event.description }}
                    </p>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>

  <!-- 日程对话框 -->
  <ScheduleDialog
    v-model:visible="scheduleDialogVisible"
    :is-editing="isEditingSchedule"
    :schedule="currentSchedule"
    :initial-date="dialogInitialDate"
    :initial-hour="dialogInitialHour"
    @submit="handleScheduleSubmit"
  />

  <!-- 日程详情对话框 -->
  <ScheduleDetailDialog
    v-model:visible="scheduleDetailVisible"
    :schedule="currentSchedule"
    @edit="handleEditSchedule"
    @delete="handleDeleteSchedule"
  />
</template>

<style scoped>
.schedule-container {
  padding: 20px;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.schedule-content {
  margin-top: 20px;
}

.calendar-card, .events-card {
  border-radius: 8px;
  height: 100%;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 月视图样式 */
.month-view {
  min-height: 600px;
}

.week-header {
  display: flex;
  border-bottom: 1px solid #eee;
}

.week-day {
  flex: 1;
  text-align: center;
  padding: 10px;
  font-weight: bold;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: minmax(100px, auto);
  gap: 1px;
  background-color: #eee;
}

.date-cell {
  background-color: white;
  padding: 5px;
  min-height: 100px;
  cursor: pointer;
}

.date-cell:hover {
  background-color: #f5f7fa;
}

.date-cell.other-month {
  color: #c0c4cc;
}

.date-cell.today {
  background-color: #ecf5ff;
}

.date-cell.has-events .date-number {
  font-weight: bold;
  color: var(--el-color-primary);
}

.date-number {
  font-size: 14px;
  margin-bottom: 5px;
}

.date-events {
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
}

.event-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

/* 周视图和日视图样式 */
.week-view, .day-view {
  min-height: 600px;
}

.week-day-header, .day-date-header {
  flex: 1;
  text-align: center;
  padding: 10px;
  font-weight: bold;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.week-day-header.today, .day-date-header.today {
  background-color: #ecf5ff;
  color: var(--el-color-primary);
}

.time-column-header {
  width: 60px;
}

.week-grid, .day-grid {
  display: flex;
  height: 600px;
  overflow-y: auto;
}

.time-column {
  width: 60px;
  flex-shrink: 0;
  border-right: 1px solid #eee;
}

.time-cell {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #eee;
  font-size: 12px;
}

.day-column, .day-events-column {
  flex: 1;
  position: relative;
  border-right: 1px solid #eee;
}

.hour-cell {
  height: 60px;
  border-bottom: 1px solid #eee;
  position: relative;
}

.event-item {
  position: absolute;
  left: 2px;
  right: 2px;
  border-radius: 4px;
  padding: 4px;
  color: white;
  font-size: 12px;
  overflow: hidden;
  cursor: pointer;
  z-index: 1;
  transition: all 0.3s;
}

.event-item:hover {
  transform: scale(1.02);
  z-index: 2;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.event-title {
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-time, .event-location {
  font-size: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  gap: 2px;
}

.day-event-item {
  padding: 8px;
}

/* 日程列表样式 */
.events-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.events-list {
  max-height: 600px;
  overflow-y: auto;
}

.event-card {
  cursor: pointer;
  transition: all 0.3s;
}

.event-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.event-description {
  color: #606266;
  font-size: 12px;
  margin-top: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import { scheduleApi } from '@/api';
import { useUserStore } from '@/stores';
import { Plus, ArrowLeft, ArrowRight, Location } from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import ScheduleDialog from './components/ScheduleDialog.vue';
import ScheduleDetailDialog from './components/ScheduleDetailDialog.vue';

const userStore = useUserStore();
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 日历数据
const calendarView = ref('month');
const currentDate = ref(new Date());
const selectedDate = ref(new Date());
const events = ref<any[]>([]);
const loading = ref(false);

// 星期几标签
const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

// 计算属性
const currentYearMonth = computed(() => {
  return dayjs(currentDate.value).format('YYYY年MM月');
});

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();

  // 获取当月第一天是星期几
  const firstDay = new Date(year, month, 1).getDay();

  // 获取当月天数
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  // 获取上个月天数
  const daysInPrevMonth = new Date(year, month, 0).getDate();

  const days = [];

  // 添加上个月的日期
  for (let i = firstDay - 1; i >= 0; i--) {
    const prevMonthDay = daysInPrevMonth - i;
    const date = new Date(year, month - 1, prevMonthDay);
    days.push({
      day: prevMonthDay,
      date,
      otherMonth: true
    });
  }

  // 添加当月的日期
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month, i);
    days.push({
      day: i,
      date,
      otherMonth: false
    });
  }

  // 添加下个月的日期，补满42个格子（6行7列）
  const remainingDays = 42 - days.length;
  for (let i = 1; i <= remainingDays; i++) {
    const date = new Date(year, month + 1, i);
    days.push({
      day: i,
      date,
      otherMonth: true
    });
  }

  return days;
});

const weekDates = computed(() => {
  const date = new Date(selectedDate.value);
  const day = date.getDay(); // 0-6, 0 is Sunday

  // 计算本周的第一天（周日）
  date.setDate(date.getDate() - day);

  // 生成本周的7天日期
  const dates = [];
  for (let i = 0; i < 7; i++) {
    const weekDate = new Date(date);
    weekDate.setDate(date.getDate() + i);
    dates.push(weekDate);
  }

  return dates;
});

const selectedDateEvents = computed(() => {
  return events.value.filter(event => {
    const eventDate = new Date(event.startTime);
    return isSameDay(eventDate, selectedDate.value);
  }).sort((a, b) => {
    return new Date(a.startTime).getTime() - new Date(b.startTime).getTime();
  });
});

// 初始化
onMounted(async () => {
  await fetchEvents();
});

// 获取日程列表
const fetchEvents = async () => {
  loading.value = true;
  try {
    const res = await scheduleApi.getAllSchedules();
    if (res.code === 200) {
      events.value = res.data.list;
    }
  } catch (error) {
    console.error('获取日程列表失败:', error);
    ElMessage.error('获取日程列表失败');
  } finally {
    loading.value = false;
  }
};

// 日期操作
const prevMonth = () => {
  const date = new Date(currentDate.value);
  date.setMonth(date.getMonth() - 1);
  currentDate.value = date;
};

const nextMonth = () => {
  const date = new Date(currentDate.value);
  date.setMonth(date.getMonth() + 1);
  currentDate.value = date;
};

const selectDate = (date: Date) => {
  selectedDate.value = date;
};

// 日期工具函数
const isToday = (date: Date) => {
  const today = new Date();
  return isSameDay(date, today);
};

const isSameDay = (date1: Date, date2: Date) => {
  return date1.getFullYear() === date2.getFullYear() &&
         date1.getMonth() === date2.getMonth() &&
         date1.getDate() === date2.getDate();
};

const formatDate = (date: Date, format: string = 'YYYY-MM-DD') => {
  return dayjs(date).format(format);
};

const formatHour = (hour: number) => {
  return `${hour.toString().padStart(2, '0')}:00`;
};

const formatEventTime = (event: any) => {
  const startTime = dayjs(event.startTime);
  const endTime = dayjs(event.endTime);

  if (event.isAllDay) {
    return '全天';
  }

  return `${startTime.format('HH:mm')} - ${endTime.format('HH:mm')}`;
};

// 事件操作
const hasEvents = (date: Date) => {
  return events.value.some(event => {
    const eventDate = new Date(event.startTime);
    return isSameDay(eventDate, date);
  });
};

const getEventsForDate = (date: Date) => {
  return events.value.filter(event => {
    const eventDate = new Date(event.startTime);
    return isSameDay(eventDate, date);
  });
};

const getEventsForDateAndHour = (date: Date, hour: number) => {
  return events.value.filter(event => {
    const eventStartDate = new Date(event.startTime);
    const eventEndDate = new Date(event.endTime);

    // 检查日期是否匹配
    if (!isSameDay(eventStartDate, date)) {
      return false;
    }

    // 检查时间是否匹配
    const eventStartHour = eventStartDate.getHours();
    const eventEndHour = eventEndDate.getHours();

    // 如果是全天事件，则显示在所有小时
    if (event.isAllDay) {
      return true;
    }

    // 如果事件跨越当前小时，则显示
    return (eventStartHour <= hour && eventEndHour > hour) ||
           (eventStartHour === hour);
  });
};

const calculateEventTop = (event: any, hour: number) => {
  const eventStartDate = new Date(event.startTime);
  const eventStartHour = eventStartDate.getHours();
  const eventStartMinute = eventStartDate.getMinutes();

  // 如果是全天事件，则显示在小时的顶部
  if (event.isAllDay) {
    return 0;
  }

  // 如果事件开始时间在当前小时内，则计算相对位置
  if (eventStartHour === hour) {
    return (eventStartMinute / 60) * 60; // 60px是小时格子的高度
  }

  return 0;
};

const calculateEventHeight = (event: any) => {
  const eventStartDate = new Date(event.startTime);
  const eventEndDate = new Date(event.endTime);

  // 如果是全天事件，则高度为小时格子的高度
  if (event.isAllDay) {
    return 30;
  }

  // 计算事件持续时间（分钟）
  const durationMinutes = (eventEndDate.getTime() - eventStartDate.getTime()) / (1000 * 60);

  // 将分钟转换为像素高度（60px是小时格子的高度）
  return (durationMinutes / 60) * 60;
};

// 日程对话框相关
const scheduleDialogVisible = ref(false);
const scheduleDetailVisible = ref(false);
const isEditingSchedule = ref(false);
const currentSchedule = ref<any>(null);
const dialogInitialDate = ref<Date | null>(null);
const dialogInitialHour = ref<number | undefined>(undefined);

// 处理添加日程
const handleAddSchedule = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    return;
  }

  // 打开添加日程对话框，使用当前选中的日期
  handleAddScheduleAt(selectedDate.value);
};

// 在指定日期和时间添加日程
const handleAddScheduleAt = (date: Date, hour?: number) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    return;
  }

  isEditingSchedule.value = false;
  currentSchedule.value = null;
  dialogInitialDate.value = date;
  dialogInitialHour.value = hour;
  scheduleDialogVisible.value = true;
};

// 处理点击事件
const handleEventClick = (event: any) => {
  currentSchedule.value = event;
  scheduleDetailVisible.value = true;
};

// 处理编辑日程
const handleEditSchedule = (schedule: any) => {
  isEditingSchedule.value = true;
  currentSchedule.value = schedule;
  scheduleDialogVisible.value = true;
};

// 处理删除日程
const handleDeleteSchedule = async (id: number) => {
  try {
    const res = await scheduleApi.deleteSchedule(id);

    if (res.code === 200) {
      ElMessage.success('日程删除成功');
      fetchEvents();
    }
  } catch (error) {
    console.error('删除日程失败:', error);
    ElMessage.error('删除日程失败');
  }
};

// 处理提交日程
const handleScheduleSubmit = async (data: any) => {
  try {
    let res;

    if (isEditingSchedule.value && data.id) {
      // 更新日程
      res = await scheduleApi.updateSchedule(data.id, {
        title: data.title,
        description: data.description,
        startTime: data.startTime,
        endTime: data.endTime,
        location: data.location,
        isAllDay: data.isAllDay,
        reminder: data.reminder,
        color: data.color
      });

      if (res.code === 200) {
        ElMessage.success('日程更新成功');
      }
    } else {
      // 创建日程
      res = await scheduleApi.createSchedule({
        title: data.title,
        description: data.description,
        startTime: data.startTime,
        endTime: data.endTime,
        location: data.location,
        isAllDay: data.isAllDay,
        reminder: data.reminder,
        color: data.color
      });

      if (res.code === 200) {
        ElMessage.success('日程创建成功');
      }
    }

    // 刷新日程列表
    fetchEvents();
  } catch (error) {
    console.error('提交日程失败:', error);
    ElMessage.error('提交日程失败');
  }
};
</script>
