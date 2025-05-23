<template>
  <div class="course-table-container">
    <div class="course-header">
      <h1 class="page-title">课程表</h1>
      <div class="course-actions">
        <el-button type="primary" @click="handleAddCourse" v-if="isLoggedIn">
          <el-icon><Plus /></el-icon>添加课程
        </el-button>
      </div>
    </div>

    <div class="course-filters">
      <el-card shadow="never" class="filter-card">
        <div class="filter-row">
          <div class="semester-selector">
            <span class="filter-label">学期：</span>
            <el-select v-model="currentSemester" placeholder="选择学期" style="min-width: 220px;" @change="handleSemesterChange">
              <el-option label="2023-2024学年第一学期" value="2023-1"></el-option>
              <el-option label="2023-2024学年第二学期" value="2023-2"></el-option>
            </el-select>
          </div>
          <div class="week-selector">
            <span class="filter-label">周次：</span>
            <el-select v-model="currentWeek" placeholder="选择周次">
              <el-option v-for="week in 20" :key="week" :label="`第${week}周`" :value="week"></el-option>
            </el-select>
          </div>
          <div class="view-selector">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="week">周视图</el-radio-button>
              <el-radio-button label="day">日视图</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 周视图 -->
    <div v-if="viewMode === 'week'" class="week-view">
      <el-card shadow="hover" class="timetable-card">
        <div class="timetable">
          <!-- 时间列 -->
          <div class="time-column">
            <div class="header-cell"></div>
            <div class="time-cell" v-for="time in timeSlots" :key="time.id">
              {{ time.label }}
            </div>
          </div>
          
          <!-- 星期列 -->
          <div v-for="day in 7" :key="day" class="day-column">
            <div class="header-cell">{{ getDayLabel(day) }}</div>
            <div 
              class="course-cell" 
              v-for="time in timeSlots" 
              :key="time.id"
              @click="handleCellClick(day, time.id)"
            >
              <div 
                v-for="course in getCoursesForTimeSlot(day, time.id)" 
                :key="course.id"
                class="course-item"
                :style="{ backgroundColor: course.color || '#409EFF' }"
                @click.stop="handleCourseClick(course)"
              >
                <div class="course-name">{{ course.name }}</div>
                <div class="course-info">{{ course.location }}</div>
                <div class="course-info">{{ course.teacher }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 日视图 -->
    <div v-else class="day-view">
      <el-card shadow="hover" class="day-card">
        <div class="day-header">
          <el-button-group>
            <el-button @click="previousDay">
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <el-button>{{ getDayLabel(currentDay) }}</el-button>
            <el-button @click="nextDay">
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </el-button-group>
        </div>
        
        <div class="day-timetable">
          <div class="time-column">
            <div class="time-cell" v-for="time in timeSlots" :key="time.id">
              {{ time.label }}
            </div>
          </div>
          
          <div class="day-courses-column">
            <div 
              class="course-cell" 
              v-for="time in timeSlots" 
              :key="time.id"
              @click="handleCellClick(currentDay, time.id)"
            >
              <div 
                v-for="course in getCoursesForTimeSlot(currentDay, time.id)" 
                :key="course.id"
                class="course-item day-course-item"
                :style="{ backgroundColor: course.color || '#409EFF' }"
                @click.stop="handleCourseClick(course)"
              >
                <div class="course-name">{{ course.name }}</div>
                <div class="course-info">{{ course.location }}</div>
                <div class="course-info">{{ course.teacher }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="courseDialogVisible"
      :title="isEditing ? '编辑课程' : '添加课程'"
      width="500px"
    >
      <el-form :model="courseForm" label-width="80px" :rules="courseRules" ref="courseFormRef">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="教师" prop="teacher">
          <el-input v-model="courseForm.teacher" placeholder="请输入教师姓名"></el-input>
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="courseForm.location" placeholder="请输入上课地点"></el-input>
        </el-form-item>
        <el-form-item label="星期" prop="dayOfWeek">
          <el-select v-model="courseForm.dayOfWeek" placeholder="请选择星期">
            <el-option v-for="day in 7" :key="day" :label="getDayLabel(day)" :value="day"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-select
            v-model="courseForm.startTime"
            placeholder="请选择开始时间"
            start="08:00"
            step="00:30"
            end="22:00"
          ></el-time-select>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-select
            v-model="courseForm.endTime"
            placeholder="请选择结束时间"
            start="08:00"
            step="00:30"
            end="22:00"
            :min-time="courseForm.startTime"
          ></el-time-select>
        </el-form-item>
        <el-form-item label="开始周次" prop="startWeek">
          <el-input-number v-model="courseForm.startWeek" :min="1" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="结束周次" prop="endWeek">
          <el-input-number v-model="courseForm.endWeek" :min="courseForm.startWeek" :max="20"></el-input-number>
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-color-picker v-model="courseForm.color"></el-color-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCourse" :loading="submitting">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="courseDetailVisible"
      title="课程详情"
      width="400px"
    >
      <div v-if="selectedCourse" class="course-detail">
        <h3 class="detail-title">{{ selectedCourse.name }}</h3>
        <div class="detail-item">
          <span class="detail-label">教师：</span>
          <span>{{ selectedCourse.teacher || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">地点：</span>
          <span>{{ selectedCourse.location || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">时间：</span>
          <span>{{ getDayLabel(selectedCourse.dayOfWeek) }} {{ selectedCourse.startTime }} - {{ selectedCourse.endTime }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">周次：</span>
          <span>第{{ selectedCourse.startWeek }}周 - 第{{ selectedCourse.endWeek }}周</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleEditCourse" v-if="isOwner">编辑</el-button>
          <el-button type="danger" @click="handleDeleteCourse" v-if="isOwner">删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { scheduleApi } from '@/api';
import { useUserStore } from '@/stores';
import { Plus, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';

const userStore = useUserStore();
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 课程表数据
const courses = ref<any[]>([]);
const loading = ref(false);
const currentSemester = ref('2023-1');
const currentWeek = ref(1);
const viewMode = ref('week');
const currentDay = ref(1); // 1-7 表示周一到周日

// 时间段定义
const timeSlots = [
  { id: 1, label: '第1节 8:00-8:45' },
  { id: 2, label: '第2节 8:55-9:40' },
  { id: 3, label: '第3节 10:00-10:45' },
  { id: 4, label: '第4节 10:55-11:40' },
  { id: 5, label: '第5节 13:30-14:15' },
  { id: 6, label: '第6节 14:25-15:10' },
  { id: 7, label: '第7节 15:30-16:15' },
  { id: 8, label: '第8节 16:25-17:10' },
  { id: 9, label: '第9节 18:30-19:15' },
  { id: 10, label: '第10节 19:25-20:10' },
  { id: 11, label: '第11节 20:20-21:05' },
  { id: 12, label: '第12节 21:15-22:00' }
];

// 课程表单
const courseDialogVisible = ref(false);
const courseFormRef = ref<FormInstance>();
const submitting = ref(false);
const isEditing = ref(false);
const courseForm = reactive({
  id: undefined as number | undefined,
  name: '',
  teacher: '',
  location: '',
  dayOfWeek: 1,
  startTime: '',
  endTime: '',
  startWeek: 1,
  endWeek: 16,
  semester: '2023-1', // 默认学期
  color: '#409EFF'
});

const courseRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  startWeek: [{ required: true, message: '请输入开始周次', trigger: 'blur' }],
  endWeek: [{ required: true, message: '请输入结束周次', trigger: 'blur' }]
};

// 课程详情
const courseDetailVisible = ref(false);
const selectedCourse = ref<any>(null);
const isOwner = computed(() => {
  if (!selectedCourse.value || !userStore.userInfo) return false;
  return selectedCourse.value.userId === userStore.userInfo.id;
});

// 初始化
onMounted(async () => {
  await fetchCourses();
});

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true;
  try {
    let res;
    if (currentSemester.value) {
      // 按学期查询
      res = await scheduleApi.getCoursesBySemester(currentSemester.value);
    } else {
      // 查询所有课程
      res = await scheduleApi.getAllCourses();
    }
    
    if (res.code === 200) {
      courses.value = res.data.list;
    }
  } catch (error) {
    console.error('获取课程列表失败:', error);
    ElMessage.error('获取课程列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取星期标签
const getDayLabel = (day: number) => {
  const days = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  return days[day] || '';
};

// 获取指定时间段的课程
const getCoursesForTimeSlot = (day: number, timeSlotId: number) => {
  return courses.value.filter(course => {
    // 检查星期是否匹配
    if (course.dayOfWeek !== day) return false;
    
    // 检查周次是否匹配
    if (currentWeek.value < course.startWeek || currentWeek.value > course.endWeek) return false;
    
    // 检查时间段是否匹配
    const courseStartHour = parseInt(course.startTime.split(':')[0]);
    const courseStartMinute = parseInt(course.startTime.split(':')[1]);
    const courseEndHour = parseInt(course.endTime.split(':')[0]);
    const courseEndMinute = parseInt(course.endTime.split(':')[1]);
    
    const slotStartHour = parseInt(timeSlots[timeSlotId - 1].label.split(' ')[1].split('-')[0].split(':')[0]);
    const slotStartMinute = parseInt(timeSlots[timeSlotId - 1].label.split(' ')[1].split('-')[0].split(':')[1]);
    const slotEndHour = parseInt(timeSlots[timeSlotId - 1].label.split(' ')[1].split('-')[1].split(':')[0]);
    const slotEndMinute = parseInt(timeSlots[timeSlotId - 1].label.split(' ')[1].split('-')[1].split(':')[1]);
    
    const courseStartTime = courseStartHour * 60 + courseStartMinute;
    const courseEndTime = courseEndHour * 60 + courseEndMinute;
    const slotStartTime = slotStartHour * 60 + slotStartMinute;
    const slotEndTime = slotEndHour * 60 + slotEndMinute;
    
    // 如果课程时间与时间段有重叠，则返回true
    return (courseStartTime <= slotEndTime && courseEndTime >= slotStartTime);
  });
};

// 处理单元格点击
const handleCellClick = (day: number, timeSlotId: number) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    return;
  }
  
  // 设置表单初始值
  isEditing.value = false;
  courseForm.id = undefined;
  courseForm.name = '';
  courseForm.teacher = '';
  courseForm.location = '';
  courseForm.dayOfWeek = day;
  
  // 设置时间
  const timeSlot = timeSlots[timeSlotId - 1];
  const timeRange = timeSlot.label.split(' ')[1].split('-');
  courseForm.startTime = timeRange[0];
  courseForm.endTime = timeRange[1];
  
  courseForm.startWeek = currentWeek.value;
  courseForm.endWeek = currentWeek.value + 15 > 20 ? 20 : currentWeek.value + 15;
  courseForm.semester = currentSemester.value; // 设置当前学期
  courseForm.color = getRandomColor();
  
  courseDialogVisible.value = true;
};

// 处理课程点击
const handleCourseClick = (course: any) => {
  selectedCourse.value = course;
  courseDetailVisible.value = true;
};

// 处理添加课程
const handleAddCourse = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    return;
  }
  
  isEditing.value = false;
  courseForm.id = undefined;
  courseForm.name = '';
  courseForm.teacher = '';
  courseForm.location = '';
  courseForm.dayOfWeek = 1;
  courseForm.startTime = '08:00';
  courseForm.endTime = '09:40';
  courseForm.startWeek = 1;
  courseForm.endWeek = 16;
  courseForm.semester = currentSemester.value; // 设置当前学期
  courseForm.color = getRandomColor();
  
  courseDialogVisible.value = true;
};

// 处理编辑课程
const handleEditCourse = () => {
  if (!selectedCourse.value) return;
  
  isEditing.value = true;
  courseForm.id = selectedCourse.value.id;
  courseForm.name = selectedCourse.value.name;
  courseForm.teacher = selectedCourse.value.teacher || '';
  courseForm.location = selectedCourse.value.location || '';
  courseForm.dayOfWeek = selectedCourse.value.dayOfWeek;
  courseForm.startTime = selectedCourse.value.startTime;
  courseForm.endTime = selectedCourse.value.endTime;
  courseForm.startWeek = selectedCourse.value.startWeek;
  courseForm.endWeek = selectedCourse.value.endWeek;
  courseForm.color = selectedCourse.value.color || '#409EFF';
  
  courseDetailVisible.value = false;
  courseDialogVisible.value = true;
};

// 处理删除课程
const handleDeleteCourse = () => {
  if (!selectedCourse.value) return;
  
  ElMessageBox.confirm(
    '确定要删除该课程吗？此操作不可恢复',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await scheduleApi.deleteCourse(selectedCourse.value.id);
      
      if (res.code === 200) {
        ElMessage.success('课程删除成功');
        courseDetailVisible.value = false;
        fetchCourses();
      }
    } catch (error) {
      console.error('删除课程失败:', error);
      ElMessage.error('删除课程失败');
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 提交课程表单
const submitCourse = async () => {
  if (!courseFormRef.value) return;
  
  await courseFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      
      try {
        // 格式化时间为 HH:mm:ss 格式
        const formatTime = (time: string) => {
          if (!time) return '';
          
          // 处理各种时间格式
          let formattedTime = time.trim();
          
          // 如果已经是 HH:mm:ss 格式，直接返回
          if (/^\d{2}:\d{2}:\d{2}$/.test(formattedTime)) {
            return formattedTime;
          }
          
          // 如果是 H:mm 或 HH:mm 格式，补充秒数
          if (/^\d{1,2}:\d{2}$/.test(formattedTime)) {
            const parts = formattedTime.split(':');
            const hours = parts[0].padStart(2, '0'); // 确保小时是两位数
            const minutes = parts[1];
            return `${hours}:${minutes}:00`;
          }
          
          // 如果是 H:mm:ss 格式，补充小时前导零
          if (/^\d{1}:\d{2}:\d{2}$/.test(formattedTime)) {
            const parts = formattedTime.split(':');
            const hours = parts[0].padStart(2, '0');
            return `${hours}:${parts[1]}:${parts[2]}`;
          }
          
          // 其他情况，尝试解析
          try {
            const parts = formattedTime.split(':');
            if (parts.length >= 2) {
              const hours = parts[0].padStart(2, '0');
              const minutes = parts[1].padStart(2, '0');
              const seconds = parts[2] ? parts[2].padStart(2, '0') : '00';
              return `${hours}:${minutes}:${seconds}`;
            }
          } catch (e) {
            console.error('时间格式化失败:', time, e);
          }
          
          return time; // 如果无法格式化，返回原值
        };

        // 检查时间冲突
        const conflictParams = {
          dayOfWeek: courseForm.dayOfWeek,
          startTime: formatTime(courseForm.startTime),
          endTime: formatTime(courseForm.endTime),
          excludeCourseId: courseForm.id
        };
        
        const conflictRes = await scheduleApi.checkCourseConflict(conflictParams);
        
        if (conflictRes.code === 200 && conflictRes.data.hasConflict) {
          ElMessageBox.confirm(
            `检测到时间冲突，有${conflictRes.data.conflictCount}门课程与当前时间段冲突，是否继续？`,
            '时间冲突',
            {
              confirmButtonText: '继续',
              cancelButtonText: '取消',
              type: 'warning'
            }
          ).then(() => {
            saveCourse();
          }).catch(() => {
            submitting.value = false;
          });
        } else {
          saveCourse();
        }
      } catch (error) {
        console.error('检查时间冲突失败:', error);
        ElMessage.error('检查时间冲突失败');
        submitting.value = false;
      }
    }
  });
};

// 保存课程
const saveCourse = async () => {
  try {
    // 格式化时间为 HH:mm:ss 格式
    const formatTime = (time: string) => {
      if (!time) return '';
      
      // 处理各种时间格式
      let formattedTime = time.trim();
      
      // 如果已经是 HH:mm:ss 格式，直接返回
      if (/^\d{2}:\d{2}:\d{2}$/.test(formattedTime)) {
        return formattedTime;
      }
      
      // 如果是 H:mm 或 HH:mm 格式，补充秒数
      if (/^\d{1,2}:\d{2}$/.test(formattedTime)) {
        const parts = formattedTime.split(':');
        const hours = parts[0].padStart(2, '0'); // 确保小时是两位数
        const minutes = parts[1];
        return `${hours}:${minutes}:00`;
      }
      
      // 如果是 H:mm:ss 格式，补充小时前导零
      if (/^\d{1}:\d{2}:\d{2}$/.test(formattedTime)) {
        const parts = formattedTime.split(':');
        const hours = parts[0].padStart(2, '0');
        return `${hours}:${parts[1]}:${parts[2]}`;
      }
      
      // 其他情况，尝试解析
      try {
        const parts = formattedTime.split(':');
        if (parts.length >= 2) {
          const hours = parts[0].padStart(2, '0');
          const minutes = parts[1].padStart(2, '0');
          const seconds = parts[2] ? parts[2].padStart(2, '0') : '00';
          return `${hours}:${minutes}:${seconds}`;
        }
      } catch (e) {
        console.error('时间格式化失败:', time, e);
      }
      
      return time; // 如果无法格式化，返回原值
    };

    // 准备请求数据
    const courseData = {
      name: courseForm.name,
      teacher: courseForm.teacher || undefined,
      location: courseForm.location || undefined,
      dayOfWeek: courseForm.dayOfWeek,
      startTime: formatTime(courseForm.startTime),
      endTime: formatTime(courseForm.endTime),
      startWeek: courseForm.startWeek,
      endWeek: courseForm.endWeek,
      semester: courseForm.semester,
      color: courseForm.color || '#409EFF'
    };

    // 调试日志
    console.log('原始时间数据:', {
      startTime: courseForm.startTime,
      endTime: courseForm.endTime
    });
    console.log('格式化后时间:', {
      startTime: courseData.startTime,
      endTime: courseData.endTime
    });
    console.log('完整发送数据:', courseData);

    // 如果是空字符串，则完全移除这些字段
    if (!courseForm.teacher) {
      delete (courseData as any).teacher;
    }
    if (!courseForm.location) {
      delete (courseData as any).location;
    }

    let res;
    if (isEditing.value && courseForm.id) {
      // 更新课程
      res = await scheduleApi.updateCourse(courseForm.id, courseData);
    } else {
      // 创建课程
      res = await scheduleApi.createCourse(courseData);
    }
    
    if (res.code === 200) {
      ElMessage.success(isEditing.value ? '课程更新成功' : '课程添加成功');
      courseDialogVisible.value = false;
      fetchCourses();
    }
  } catch (error) {
    console.error(isEditing.value ? '更新课程失败:' : '添加课程失败:', error);
    ElMessage.error(isEditing.value ? '更新课程失败' : '添加课程失败');
  } finally {
    submitting.value = false;
  }
};

// 切换到前一天
const previousDay = () => {
  currentDay.value = currentDay.value === 1 ? 7 : currentDay.value - 1;
};

// 切换到后一天
const nextDay = () => {
  currentDay.value = currentDay.value === 7 ? 1 : currentDay.value + 1;
};

// 获取随机颜色
const getRandomColor = () => {
  const colors = [
    '#409EFF', // 蓝色
    '#67C23A', // 绿色
    '#E6A23C', // 黄色
    '#F56C6C', // 红色
    '#909399', // 灰色
    '#9966CC', // 紫色
    '#FF9900', // 橙色
    '#19CAAD', // 青色
    '#8CC7B5', // 浅绿
    '#A0EEE1', // 浅青
    '#BEE7E9', // 浅蓝
    '#BEEDC7', // 浅绿
    '#D6D5B7', // 浅黄
    '#D1BA74', // 浅棕
    '#E6CEAC', // 浅橙
    '#ECAD9E'  // 浅红
  ];
  return colors[Math.floor(Math.random() * colors.length)];
};

// 学期选择变化处理
const handleSemesterChange = () => {
  fetchCourses();
};
</script>

<style scoped>
.course-table-container {
  padding: 20px;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.course-filters {
  margin-bottom: 20px;
}

.filter-card {
  border-radius: 8px;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-label {
  margin-right: 10px;
  font-weight: 500;
}

/* 周视图样式 */
.week-view {
  margin-top: 20px;
}

.timetable-card {
  border-radius: 8px;
}

.timetable {
  display: flex;
  min-height: 800px;
}

.time-column, .day-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eee;
}

.time-column {
  flex: 0 0 120px;
}

.header-cell {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  background-color: #f5f7fa;
  border-bottom: 1px solid #eee;
}

.time-cell {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #eee;
  font-size: 12px;
}

.course-cell {
  height: 60px;
  border-bottom: 1px solid #eee;
  position: relative;
  cursor: pointer;
}

.course-item {
  position: absolute;
  top: 2px;
  left: 2px;
  right: 2px;
  bottom: 2px;
  border-radius: 4px;
  padding: 4px;
  color: white;
  font-size: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.course-item:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.course-name {
  font-weight: bold;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-info {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 10px;
}

/* 日视图样式 */
.day-view {
  margin-top: 20px;
}

.day-card {
  border-radius: 8px;
}

.day-header {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.day-timetable {
  display: flex;
  min-height: 800px;
}

.day-courses-column {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.day-course-item {
  padding: 8px;
}

/* 课程详情样式 */
.course-detail {
  padding: 10px;
}

.detail-title {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
}

.detail-item {
  margin-bottom: 10px;
  display: flex;
}

.detail-label {
  font-weight: 500;
  width: 60px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
