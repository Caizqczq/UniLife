<template>
  <div class="course-edit-page">
    <el-card class="edit-card">
      <template #header>
        <h2>{{ isEditMode ? 'Edit Course' : 'Add New Course' }}</h2>
      </template>

      <el-form
        ref="courseFormRef"
        :model="courseData"
        :rules="courseRules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Course Name" prop="name">
              <el-input v-model="courseData.name" placeholder="e.g., Advanced Calculus" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Teacher" prop="teacher">
              <el-input v-model="courseData.teacher" placeholder="e.g., Prof. Smith" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="Location" prop="location">
          <el-input v-model="courseData.location" placeholder="e.g., Room 301, Main Building" maxlength="100" show-word-limit />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="Day of the Week" prop="dayOfWeek">
              <el-select v-model="courseData.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <el-option v-for="day in daysOfWeek" :key="day.value" :label="day.label" :value="day.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="Start Time" prop="startTime">
              <el-time-picker v-model="courseData.startTime" placeholder="HH:mm" format="HH:mm" value-format="HH:mm:ss" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="End Time" prop="endTime">
              <el-time-picker v-model="courseData.endTime" placeholder="HH:mm" format="HH:mm" value-format="HH:mm:ss" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="Start Week" prop="startWeek">
              <el-input-number v-model="courseData.startWeek" :min="1" :max="25" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="End Week" prop="endWeek">
              <el-input-number v-model="courseData.endWeek" :min="1" :max="25" style="width: 100%;" />
            </el-form-item>
          </el-col>
           <el-col :span="8">
            <el-form-item label="Color" prop="color">
              <el-color-picker v-model="courseData.color" show-alpha />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item class="form-actions">
          <el-button @click="handleCancel">Cancel</el-button>
          <el-button type="primary" native-type="submit" :loading="submitting">
            {{ isEditMode ? 'Save Changes' : 'Add Course' }}
          </el-button>
           <el-popconfirm
              v-if="isEditMode"
              title="Are you sure you want to delete this course?"
              confirm-button-text="Yes, Delete"
              cancel-button-text="No"
              @confirm="handleDelete"
              style="margin-left: 10px;"
            >
              <template #reference>
                <el-button type="danger" plain :loading="deleting">Delete Course</el-button>
              </template>
            </el-popconfirm>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard, ElRow, ElCol, ElTimePicker, ElInputNumber, ElColorPicker, ElPopconfirm } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

interface CourseFormData {
  id?: number;
  name: string;
  teacher: string;
  location: string;
  dayOfWeek: number | null; // 1 (Mon) - 7 (Sun)
  startTime: string; // HH:mm:ss
  endTime: string;   // HH:mm:ss
  startWeek: number;
  endWeek: number;
  color: string | null;
}

export default defineComponent({
  name: 'CourseEditPage',
  components: {
    ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard, ElRow, ElCol, ElTimePicker, ElInputNumber, ElColorPicker, ElPopconfirm,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const courseFormRef = ref<FormInstance>();
    const courseData = ref<CourseFormData>({
      name: '',
      teacher: '',
      location: '',
      dayOfWeek: null,
      startTime: '',
      endTime: '',
      startWeek: 1,
      endWeek: 16, // Default common semester length
      color: '#409EFF', // Default color
    });
    const submitting = ref(false);
    const deleting = ref(false);

    const courseId = computed(() => route.params.courseId ? Number(route.params.courseId as string) : null);
    const isEditMode = computed(() => !!courseId.value);

    const daysOfWeek = ref([
      { label: 'Monday', value: 1 }, { label: 'Tuesday', value: 2 }, { label: 'Wednesday', value: 3 },
      { label: 'Thursday', value: 4 }, { label: 'Friday', value: 5 }, { label: 'Saturday', value: 6 },
      { label: 'Sunday', value: 7 },
    ]);

    const validateTime = (rule: any, value: any, callback: any) => {
        if (courseData.value.startTime && courseData.value.endTime && courseData.value.startTime >= courseData.value.endTime) {
            callback(new Error('End time must be after start time'));
        } else {
            callback();
        }
    };
    const validateWeek = (rule: any, value: any, callback: any) => {
        if (courseData.value.startWeek && courseData.value.endWeek && courseData.value.startWeek > courseData.value.endWeek) {
            callback(new Error('End week must be after or same as start week'));
        } else {
            callback();
        }
    };


    const courseRules: FormRules = {
      name: [
        { required: true, message: 'Please enter course name', trigger: 'blur' },
        { max: 100, message: 'Course name too long', trigger: 'blur' },
      ],
      teacher: [{ max: 50, message: 'Teacher name too long', trigger: 'blur' }],
      location: [{ max: 100, message: 'Location too long', trigger: 'blur' }],
      dayOfWeek: [{ required: true, message: 'Please select day of the week', trigger: 'change' }],
      startTime: [
        { required: true, message: 'Please select start time', trigger: 'change' },
        { validator: validateTime, trigger: 'change' }
      ],
      endTime: [
        { required: true, message: 'Please select end time', trigger: 'change' },
        { validator: validateTime, trigger: 'change' }
      ],
      startWeek: [
        { required: true, message: 'Please enter start week', trigger: 'blur' },
        { type: 'number', min: 1, max: 52, message: 'Invalid week number', trigger: 'blur'},
        { validator: validateWeek, trigger: 'blur' }
      ],
      endWeek: [
        { required: true, message: 'Please enter end week', trigger: 'blur' },
        { type: 'number', min: 1, max: 52, message: 'Invalid week number', trigger: 'blur'},
        { validator: validateWeek, trigger: 'blur' }
      ],
      color: [{ required: true, message: 'Please select a color', trigger: 'change' }],
    };

    const fetchCourseDataForEdit = async () => {
      if (!isEditMode.value || !courseId.value) return;
      submitting.value = true; 
      try {
        const response = await axios.get(`/api/courses/${courseId.value}`);
        if (response.data && response.data.code === 200 && response.data.data) {
          const fetchedCourse = response.data.data;
          courseData.value = { ...fetchedCourse };
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch course data.');
          router.push({ name: 'TimetablePage' }); // Or appropriate fallback
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching course data.');
        router.push({ name: 'TimetablePage' });
      } finally {
        submitting.value = false;
      }
    };

    const handleSubmit = async () => {
      if (!courseFormRef.value) return;
      await courseFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          try {
            const payload = { ...courseData.value };
            // Ensure IDs are not sent on create, and are sent on update
            if (!isEditMode.value) delete payload.id;


            const response = isEditMode.value && courseId.value
              ? await axios.put(`/api/courses/${courseId.value}`, payload)
              : await axios.post('/api/courses', payload);

            if (response.data && response.data.code === 200) {
              ElMessage.success(isEditMode.value ? 'Course updated successfully!' : 'Course added successfully!');
              router.push({ name: 'TimetablePage' }); // Navigate to timetable view
            } else {
              // Handle backend validation errors, e.g., course conflicts
              ElMessage.error(response.data.message || `Failed to ${isEditMode.value ? 'update' : 'add'} course.`);
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || `Error ${isEditMode.value ? 'updating' : 'adding'} course.`);
          } finally {
            submitting.value = false;
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };
    
    const handleDelete = async () => {
        if (!isEditMode.value || !courseId.value) return;
        deleting.value = true;
        try {
            const response = await axios.delete(`/api/courses/${courseId.value}`);
            if (response.data && response.data.code === 200) {
                ElMessage.success('Course deleted successfully!');
                router.push({ name: 'TimetablePage' });
            } else {
                ElMessage.error(response.data.message || 'Failed to delete course.');
            }
        } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error deleting course.');
        } finally {
            deleting.value = false;
        }
    };

    const handleCancel = () => {
      router.push({ name: 'TimetablePage' }); // Or router.back()
    };

    onMounted(() => {
      if (isEditMode.value) {
        fetchCourseDataForEdit();
      }
    });

    return {
      courseFormRef, courseData, courseRules, daysOfWeek,
      isEditMode, submitting, deleting,
      handleSubmit, handleCancel, handleDelete,
    };
  },
});
</script>

<style scoped>
.course-edit-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}
.edit-card {
  border-radius: 8px;
}
.edit-card h2 {
  margin: 0;
  font-size: 1.8em;
  color: #303133;
}
.el-form-item {
  margin-bottom: 22px;
}
.el-form-item label {
  font-weight: 600;
}
.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end; /* Align buttons to the right */
}
.form-actions .el-button {
  margin-left: 10px;
}
.el-color-picker {
    margin-top: 5px; /* Align with input number */
}
</style>
