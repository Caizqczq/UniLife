<template>
  <el-dialog
    :model-value="visible"
    :title="isEditMode ? 'Edit Schedule' : 'Add New Schedule'"
    width="600px"
    @close="handleCloseDialog"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="scheduleFormRef"
      :model="scheduleData"
      :rules="scheduleRules"
      label-position="top"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="Title" prop="title">
        <el-input v-model="scheduleData.title" placeholder="e.g., Team Meeting" maxlength="100" show-word-limit />
      </el-form-item>

      <el-form-item label="Description" prop="description">
        <el-input
          v-model="scheduleData.description"
          type="textarea"
          :rows="3"
          placeholder="Enter schedule details"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Start Time" prop="startTime">
            <el-date-picker
              v-model="scheduleData.startTime"
              type="datetime"
              placeholder="Select start date and time"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss" 
              style="width: 100%;"
              :disabled="scheduleData.isAllDay"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="End Time" prop="endTime">
            <el-date-picker
              v-model="scheduleData.endTime"
              type="datetime"
              placeholder="Select end date and time"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DDTHH:mm:ss"
              style="width: 100%;"
              :disabled="scheduleData.isAllDay"
            />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item prop="isAllDay">
         <el-checkbox v-model="scheduleData.isAllDay" label="All-day event" @change="handleAllDayChange"/>
      </el-form-item>
      
      <div v-if="scheduleData.isAllDay">
           <el-row :gutter="20">
            <el-col :span="12">
            <el-form-item label="All-day Start Date" prop="allDayStartDate">
                <el-date-picker
                v-model="scheduleData.allDayStartDate"
                type="date"
                placeholder="Select start date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
                />
            </el-form-item>
            </el-col>
            <el-col :span="12">
            <el-form-item label="All-day End Date" prop="allDayEndDate">
                <el-date-picker
                v-model="scheduleData.allDayEndDate"
                type="date"
                placeholder="Select end date (optional)"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%;"
                />
            </el-form-item>
            </el-col>
        </el-row>
      </div>


      <el-form-item label="Location" prop="location">
        <el-input v-model="scheduleData.location" placeholder="e.g., Conference Room A" maxlength="100" show-word-limit />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Reminder" prop="reminderMinutes">
            <el-select v-model="scheduleData.reminderMinutes" placeholder="Set reminder" style="width: 100%;" clearable>
              <el-option label="No reminder" :value="0" />
              <el-option label="5 minutes before" :value="5" />
              <el-option label="15 minutes before" :value="15" />
              <el-option label="30 minutes before" :value="30" />
              <el-option label="1 hour before" :value="60" />
              <el-option label="1 day before" :value="1440" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Color Tag" prop="color">
            <el-color-picker v-model="scheduleData.color" show-alpha />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="handleCloseDialog">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ isEditMode ? 'Save Changes' : 'Create Schedule' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts">
import { defineComponent, ref, watch, PropType, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElRow, ElCol, ElDatePicker, ElCheckbox, ElColorPicker } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { dayjs } from 'element-plus'; // Import dayjs for date manipulation

// This should match the structure expected/returned by the backend
export interface Schedule {
  id?: number;
  title: string;
  description?: string;
  startTime: string; // ISO 8601 format string or ensure conversion
  endTime: string;   // ISO 8601 format string
  isAllDay: boolean;
  location?: string;
  reminderMinutes?: number | null; // Minutes before event
  color?: string | null;
  // These are for UI handling of all-day events, might not be part of core Schedule model sent to backend directly
  allDayStartDate?: string | null; // YYYY-MM-DD
  allDayEndDate?: string | null;   // YYYY-MM-DD
}


export default defineComponent({
  name: 'ScheduleEditModal',
  components: {
    ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElRow, ElCol, ElDatePicker, ElCheckbox, ElColorPicker,
  },
  props: {
    visible: {
      type: Boolean,
      required: true,
    },
    scheduleToEdit: {
      type: Object as PropType<Schedule | null>,
      default: null,
    },
  },
  emits: ['close', 'schedule-saved'],
  setup(props, { emit }) {
    const scheduleFormRef = ref<FormInstance>();
    const defaultColor = '#409EFF';
    const scheduleData = ref<Schedule>({
      title: '',
      description: '',
      startTime: '', 
      endTime: '',
      isAllDay: false,
      allDayStartDate: null,
      allDayEndDate: null,
      location: '',
      reminderMinutes: null,
      color: defaultColor,
    });
    const submitting = ref(false);

    const isEditMode = computed(() => !!(props.scheduleToEdit && props.scheduleToEdit.id));

    const validateTimes = (rule: any, value: any, callback: any) => {
        if (!scheduleData.value.isAllDay) {
            if (!scheduleData.value.startTime) return callback(new Error('Start time is required'));
            if (!scheduleData.value.endTime) return callback(new Error('End time is required'));
            if (dayjs(scheduleData.value.startTime).isAfter(dayjs(scheduleData.value.endTime))) {
                 return callback(new Error('End time must be after start time'));
            }
        } else { // All day event
            if (!scheduleData.value.allDayStartDate) return callback(new Error('Start date is required for all-day event'));
            if (scheduleData.value.allDayEndDate && dayjs(scheduleData.value.allDayStartDate).isAfter(dayjs(scheduleData.value.allDayEndDate))) {
                return callback(new Error('End date must be after or same as start date for all-day event'));
            }
        }
        callback();
    };


    const scheduleRules: FormRules = {
      title: [
        { required: true, message: 'Please enter a title', trigger: 'blur' },
        { max: 100, message: 'Title is too long', trigger: 'blur' },
      ],
      description: [{ max: 500, message: 'Description is too long', trigger: 'blur' }],
      startTime: [{ validator: validateTimes, trigger: 'change' }],
      endTime: [{ validator: validateTimes, trigger: 'change' }],
      allDayStartDate: [{ validator: validateTimes, trigger: 'change' }],
      allDayEndDate: [{ validator: validateTimes, trigger: 'change' }],
      location: [{ max: 100, message: 'Location is too long', trigger: 'blur' }],
      color: [{ required: true, message: 'Please select a color', trigger: 'change' }],
    };
    
    const resetForm = () => {
        scheduleData.value = {
            title: '',
            description: '',
            startTime: '',
            endTime: '',
            isAllDay: false,
            allDayStartDate: null,
            allDayEndDate: null,
            location: '',
            reminderMinutes: null,
            color: defaultColor,
        };
        if (scheduleFormRef.value) {
            scheduleFormRef.value.resetFields();
        }
    };

    watch(() => props.visible, (newVal) => {
      if (newVal) {
        nextTick(() => { // Ensure form is rendered before trying to interact
            if (props.scheduleToEdit) {
                // Editing existing schedule
                scheduleData.value = { ...props.scheduleToEdit };
                if (props.scheduleToEdit.isAllDay) {
                    scheduleData.value.allDayStartDate = dayjs(props.scheduleToEdit.startTime).format('YYYY-MM-DD');
                    // Backend might store endTime as end of day or start of next day for all-day.
                    // If endTime is start of next day, subtract one day for display.
                    // Or if API returns separate allDayStartDate/allDayEndDate, use those.
                    // For simplicity, if endTime is significantly different from startTime on all-day, assume it's a multi-day all-day event.
                    if (props.scheduleToEdit.endTime && !dayjs(props.scheduleToEdit.startTime).isSame(dayjs(props.scheduleToEdit.endTime), 'day')) {
                         scheduleData.value.allDayEndDate = dayjs(props.scheduleToEdit.endTime).format('YYYY-MM-DD');
                    } else {
                        scheduleData.value.allDayEndDate = scheduleData.value.allDayStartDate; // Default to same day if not specified
                    }
                } else {
                     // Ensure startTime and endTime are in a format recognized by el-date-picker (e.g. Date object or ISO string)
                    scheduleData.value.startTime = props.scheduleToEdit.startTime; // Assuming it's already correct format
                    scheduleData.value.endTime = props.scheduleToEdit.endTime;
                }

            } else {
                // Adding new schedule, reset to defaults
                resetForm();
                // Set default start/end times e.g. next hour block
                const now = dayjs();
                const defaultStart = now.startOf('hour').add(1, 'hour');
                const defaultEnd = defaultStart.add(1, 'hour');
                scheduleData.value.startTime = defaultStart.format('YYYY-MM-DDTHH:mm:ss');
                scheduleData.value.endTime = defaultEnd.format('YYYY-MM-DDTHH:mm:ss');
            }
        });
      }
    });

    const handleAllDayChange = (isAllDay: boolean) => {
        if (isAllDay) {
            // When switching to all-day, set default all-day dates based on current startTime or today
            const baseDate = scheduleData.value.startTime ? dayjs(scheduleData.value.startTime) : dayjs();
            scheduleData.value.allDayStartDate = baseDate.format('YYYY-MM-DD');
            scheduleData.value.allDayEndDate = baseDate.format('YYYY-MM-DD'); // Default to single all-day event
            // Clear time parts
            scheduleData.value.startTime = '';
            scheduleData.value.endTime = '';
        } else {
            // When switching from all-day, set default times if allDayStartDate was set
            const baseDate = scheduleData.value.allDayStartDate ? dayjs(scheduleData.value.allDayStartDate) : dayjs();
            scheduleData.value.startTime = baseDate.hour(8).minute(0).second(0).format('YYYY-MM-DDTHH:mm:ss'); // Default to 8 AM
            scheduleData.value.endTime = baseDate.hour(9).minute(0).second(0).format('YYYY-MM-DDTHH:mm:ss');   // Default to 9 AM
        }
    };


    const handleSubmit = async () => {
      if (!scheduleFormRef.value) return;
      await scheduleFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          let payload: Omit<Schedule, 'allDayStartDate' | 'allDayEndDate'> = { // Omit UI-specific fields
            title: scheduleData.value.title,
            description: scheduleData.value.description,
            isAllDay: scheduleData.value.isAllDay,
            location: scheduleData.value.location,
            reminderMinutes: scheduleData.value.reminderMinutes,
            color: scheduleData.value.color,
            startTime: '', // Will be set below
            endTime: '',   // Will be set below
          };
          if (scheduleData.value.id) { // For edits
            payload.id = scheduleData.value.id;
          }

          if (scheduleData.value.isAllDay) {
            payload.startTime = dayjs(scheduleData.value.allDayStartDate).startOf('day').toISOString();
            // For all-day events, endTime is typically the start of the next day, or end of the selected end day
            payload.endTime = scheduleData.value.allDayEndDate 
                                ? dayjs(scheduleData.value.allDayEndDate).endOf('day').toISOString() 
                                : dayjs(scheduleData.value.allDayStartDate).endOf('day').toISOString();
          } else {
            payload.startTime = dayjs(scheduleData.value.startTime).toISOString();
            payload.endTime = dayjs(scheduleData.value.endTime).toISOString();
          }
          
          try {
            const response = isEditMode.value
              ? await axios.put(`/api/schedules/${payload.id}`, payload)
              : await axios.post('/api/schedules', payload);

            if (response.data && response.data.code === 200) {
              ElMessage.success(isEditMode.value ? 'Schedule updated successfully!' : 'Schedule created successfully!');
              emit('schedule-saved');
              handleCloseDialog();
            } else {
              ElMessage.error(response.data.message || `Failed to ${isEditMode.value ? 'update' : 'create'} schedule.`);
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || `Error ${isEditMode.value ? 'updating' : 'creating'} schedule.`);
          } finally {
            submitting.value = false;
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };

    const handleCloseDialog = () => {
      resetForm();
      emit('close');
    };

    return {
      scheduleFormRef, scheduleData, scheduleRules, isEditMode, submitting,
      handleSubmit, handleCloseDialog, handleAllDayChange,
    };
  },
});
</script>

<style scoped>
.el-form-item {
  margin-bottom: 20px;
}
.el-color-picker {
    margin-top: 5px; /* Align with other inputs if needed */
}
</style>
