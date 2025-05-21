<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEditing ? '编辑日程' : '添加日程'"
    width="500px"
    @closed="handleClosed"
  >
    <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入日程标题"></el-input>
      </el-form-item>
      <el-form-item label="全天" prop="isAllDay">
        <el-switch v-model="form.isAllDay" :active-value="1" :inactive-value="0"></el-switch>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-if="form.isAllDay === 1"
          v-model="form.startDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        ></el-date-picker>
        <el-date-picker
          v-else
          v-model="form.startTime"
          type="datetime"
          placeholder="选择日期时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:00"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-if="form.isAllDay === 1"
          v-model="form.endDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledEndDate"
        ></el-date-picker>
        <el-date-picker
          v-else
          v-model="form.endTime"
          type="datetime"
          placeholder="选择日期时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:00"
          :disabled-date="disabledEndDate"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="地点" prop="location">
        <el-input v-model="form.location" placeholder="请输入地点"></el-input>
      </el-form-item>
      <el-form-item label="提醒" prop="reminder">
        <el-select v-model="form.reminder" placeholder="请选择提醒时间">
          <el-option label="不提醒" :value="0"></el-option>
          <el-option label="5分钟前" :value="5"></el-option>
          <el-option label="15分钟前" :value="15"></el-option>
          <el-option label="30分钟前" :value="30"></el-option>
          <el-option label="1小时前" :value="60"></el-option>
          <el-option label="2小时前" :value="120"></el-option>
          <el-option label="1天前" :value="1440"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="颜色" prop="color">
        <el-color-picker v-model="form.color"></el-color-picker>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入日程描述"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          保存
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, defineProps, defineEmits } from 'vue';
import { ElMessage, FormInstance } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isEditing: {
    type: Boolean,
    default: false
  },
  schedule: {
    type: Object,
    default: () => ({})
  },
  initialDate: {
    type: Date,
    default: () => new Date()
  },
  initialHour: {
    type: Number,
    default: undefined
  }
});

const emit = defineEmits(['update:visible', 'submit', 'delete']);

// 对话框状态
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 表单引用
const formRef = ref<FormInstance>();
const submitting = ref(false);

// 表单数据
const form = reactive({
  id: undefined as number | undefined,
  title: '',
  isAllDay: 0,
  startTime: '',
  endTime: '',
  startDate: '',
  endDate: '',
  location: '',
  reminder: 0,
  color: '#409EFF',
  description: ''
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入日程标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
};

// 监听props变化
watch(() => props.visible, (val) => {
  if (val) {
    initForm();
  }
});

// 初始化表单
const initForm = () => {
  if (props.isEditing && props.schedule) {
    // 编辑模式
    form.id = props.schedule.id;
    form.title = props.schedule.title;
    form.isAllDay = props.schedule.isAllDay;
    form.location = props.schedule.location || '';
    form.reminder = props.schedule.reminder || 0;
    form.color = props.schedule.color || '#409EFF';
    form.description = props.schedule.description || '';
    
    if (form.isAllDay === 1) {
      form.startDate = dayjs(props.schedule.startTime).format('YYYY-MM-DD');
      form.endDate = dayjs(props.schedule.endTime).format('YYYY-MM-DD');
    } else {
      form.startTime = dayjs(props.schedule.startTime).format('YYYY-MM-DDTHH:mm:00');
      form.endTime = dayjs(props.schedule.endTime).format('YYYY-MM-DDTHH:mm:00');
    }
  } else {
    // 添加模式
    form.id = undefined;
    form.title = '';
    form.isAllDay = 0;
    form.location = '';
    form.reminder = 0;
    form.color = getRandomColor();
    form.description = '';
    
    // 设置初始日期和时间
    const initialDate = props.initialDate || new Date();
    const initialHour = props.initialHour !== undefined ? props.initialHour : initialDate.getHours();
    
    const startDate = dayjs(initialDate);
    const endDate = dayjs(initialDate).add(1, 'hour');
    
    form.startDate = startDate.format('YYYY-MM-DD');
    form.endDate = startDate.format('YYYY-MM-DD');
    
    form.startTime = startDate.hour(initialHour).minute(0).format('YYYY-MM-DDTHH:mm:00');
    form.endTime = startDate.hour(initialHour + 1).minute(0).format('YYYY-MM-DDTHH:mm:00');
  }
};

// 禁用结束日期早于开始日期
const disabledEndDate = (time: Date) => {
  if (form.isAllDay === 1) {
    return dayjs(time).isBefore(dayjs(form.startDate));
  } else {
    return dayjs(time).isBefore(dayjs(form.startTime));
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      
      try {
        // 构建提交数据
        const data: any = {
          title: form.title,
          isAllDay: form.isAllDay,
          location: form.location,
          reminder: form.reminder,
          color: form.color,
          description: form.description
        };
        
        // 处理日期时间
        if (form.isAllDay === 1) {
          data.startTime = `${form.startDate}T00:00:00`;
          data.endTime = `${form.endDate}T23:59:59`;
        } else {
          data.startTime = form.startTime;
          data.endTime = form.endTime;
        }
        
        // 提交数据
        emit('submit', {
          id: form.id,
          ...data
        });
        
        dialogVisible.value = false;
      } catch (error) {
        console.error('提交日程失败:', error);
        ElMessage.error('提交日程失败');
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 处理对话框关闭
const handleClosed = () => {
  formRef.value?.resetFields();
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
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
