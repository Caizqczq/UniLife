<template>
  <el-dialog
    v-model="dialogVisible"
    title="日程详情"
    width="400px"
  >
    <div v-if="schedule" class="schedule-detail">
      <h3 class="detail-title" :style="{ color: schedule.color || '#409EFF' }">{{ schedule.title }}</h3>
      
      <div class="detail-item">
        <span class="detail-label">时间：</span>
        <span>{{ formatScheduleTime(schedule) }}</span>
      </div>
      
      <div class="detail-item" v-if="schedule.location">
        <span class="detail-label">地点：</span>
        <span>{{ schedule.location }}</span>
      </div>
      
      <div class="detail-item" v-if="schedule.reminder">
        <span class="detail-label">提醒：</span>
        <span>{{ formatReminder(schedule.reminder) }}</span>
      </div>
      
      <div class="detail-item" v-if="schedule.description">
        <span class="detail-label">描述：</span>
        <div class="detail-description">{{ schedule.description }}</div>
      </div>
    </div>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEdit" v-if="isOwner">编辑</el-button>
        <el-button type="danger" @click="handleDelete" v-if="isOwner">删除</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits } from 'vue';
import { ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import { useUserStore } from '@/stores';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  schedule: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible', 'edit', 'delete']);

// 对话框状态
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 用户权限
const userStore = useUserStore();
const isOwner = computed(() => {
  if (!props.schedule || !userStore.user) return false;
  return props.schedule.userId === userStore.user.id;
});

// 格式化日程时间
const formatScheduleTime = (schedule: any) => {
  if (!schedule) return '';
  
  const startTime = dayjs(schedule.startTime);
  const endTime = dayjs(schedule.endTime);
  
  if (schedule.isAllDay === 1) {
    // 如果开始日期和结束日期相同，则只显示一个日期
    if (startTime.format('YYYY-MM-DD') === endTime.format('YYYY-MM-DD')) {
      return `${startTime.format('YYYY年MM月DD日')} 全天`;
    }
    return `${startTime.format('YYYY年MM月DD日')} - ${endTime.format('YYYY年MM月DD日')} 全天`;
  }
  
  // 如果开始日期和结束日期相同，则只显示一个日期
  if (startTime.format('YYYY-MM-DD') === endTime.format('YYYY-MM-DD')) {
    return `${startTime.format('YYYY年MM月DD日 HH:mm')} - ${endTime.format('HH:mm')}`;
  }
  
  return `${startTime.format('YYYY年MM月DD日 HH:mm')} - ${endTime.format('YYYY年MM月DD日 HH:mm')}`;
};

// 格式化提醒时间
const formatReminder = (minutes: number) => {
  if (minutes === 0) return '不提醒';
  if (minutes === 5) return '5分钟前';
  if (minutes === 15) return '15分钟前';
  if (minutes === 30) return '30分钟前';
  if (minutes === 60) return '1小时前';
  if (minutes === 120) return '2小时前';
  if (minutes === 1440) return '1天前';
  return `${minutes}分钟前`;
};

// 处理编辑
const handleEdit = () => {
  emit('edit', props.schedule);
  dialogVisible.value = false;
};

// 处理删除
const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除该日程吗？此操作不可恢复',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    emit('delete', props.schedule.id);
    dialogVisible.value = false;
  }).catch(() => {
    // 用户取消删除
  });
};
</script>

<style scoped>
.schedule-detail {
  padding: 10px;
}

.detail-title {
  font-size: 18px;
  margin-bottom: 15px;
}

.detail-item {
  margin-bottom: 10px;
  display: flex;
}

.detail-label {
  font-weight: 500;
  width: 60px;
  flex-shrink: 0;
}

.detail-description {
  white-space: pre-line;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
