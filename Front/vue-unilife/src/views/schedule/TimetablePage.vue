<template>
  <div class="timetable-page">
    <el-card class="timetable-card">
      <template #header>
        <div class="card-header">
          <h2>My Weekly Timetable</h2>
          <div class="actions">
            <el-select v-model="currentWeek" placeholder="Select Week" @change="handleWeekChange" style="width: 150px; margin-right:15px;">
              <el-option v-for="week in availableWeeks" :key="week" :label="`Week ${week}`" :value="week" />
            </el-select>
            <router-link to="/schedule/course/create">
              <el-button type="primary" :icon="Plus">Add Course</el-button>
            </router-link>
          </div>
        </div>
      </template>

      <el-skeleton :rows="10" animated v-if="loading" />

      <div v-if="!loading && courses.length === 0" class="empty-timetable">
        <el-empty description="No courses scheduled for this week. Add some courses to get started!" />
      </div>

      <div v-if="!loading && courses.length > 0" class="timetable-grid">
        <!-- Header Row for Days -->
        <div class="day-header-cell time-column-header">Time</div>
        <div v-for="day in days" :key="day.value" class="day-header-cell">
          {{ day.label }}
        </div>

        <!-- Time Slots and Course Cells -->
        <template v-for="timeSlot in timeSlots" :key="timeSlot.slot">
          <div class="time-slot-cell">{{ timeSlot.label }}</div>
          <div v-for="day in days" :key="`${day.value}-${timeSlot.slot}`" class="grid-cell">
            <div
              v-for="course in getCoursesForCell(day.value, timeSlot.start, timeSlot.end)"
              :key="course.id"
              class="course-block"
              :style="{ backgroundColor: course.color || '#409EFF', borderColor: darkenColor(course.color || '#409EFF') }"
              @click="viewCourseDetails(course)"
              v-tooltip="`${course.name}\n${course.location}\n${course.teacher}\n${course.startTime.substring(0,5)} - ${course.endTime.substring(0,5)}`"
            >
              <div class="course-name">{{ course.name }}</div>
              <div class="course-location">{{ course.location }}</div>
              <div class="course-time">{{ course.startTime.substring(0,5) }} - {{ course.endTime.substring(0,5) }}</div>
            </div>
          </div>
        </template>
      </div>
    </el-card>

    <!-- Course Detail Modal (Simplified, could be a dialog) -->
     <el-dialog v-model="courseDetailVisible" title="Course Details" width="500px">
        <div v-if="selectedCourse" class="course-details-content">
            <p><strong>Name:</strong> {{ selectedCourse.name }}</p>
            <p><strong>Teacher:</strong> {{ selectedCourse.teacher || 'N/A' }}</p>
            <p><strong>Location:</strong> {{ selectedCourse.location || 'N/A' }}</p>
            <p><strong>Time:</strong> {{ selectedCourse.startTime.substring(0,5) }} - {{ selectedCourse.endTime.substring(0,5) }} ({{ getDayLabel(selectedCourse.dayOfWeek) }})</p>
            <p><strong>Weeks:</strong> Week {{ selectedCourse.startWeek }} to Week {{ selectedCourse.endWeek }}</p>
            <p v-if="selectedCourse.color"><strong>Color:</strong> <el-tag :color="selectedCourse.color" effect="dark" size="small" style="border-radius: 50%; width: 20px; height: 20px; border: none;"></el-tag></p>
        </div>
        <template #footer>
            <el-button @click="courseDetailVisible = false">Close</el-button>
            <el-button type="primary" @click="editCourse(selectedCourse!)" :icon="Edit">Edit Course</el-button>
        </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElButton, ElCard, ElSelect, ElOption, ElSkeleton, ElEmpty, ElDialog, ElTag } from 'element-plus';
import { Plus, Edit } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router'; // For navigation
import { Tooltip as vTooltip } from 'floating-vue'; // Using floating-vue for tooltips
import 'floating-vue/dist/style.css';


interface Course {
  id: number;
  name: string;
  teacher: string;
  location: string;
  dayOfWeek: number; // 1-7
  startTime: string; // HH:mm:ss
  endTime: string;   // HH:mm:ss
  startWeek: number;
  endWeek: number;
  color: string | null;
}

interface TimeSlot {
  slot: number; // e.g., 1 for 08:00-09:00
  label: string; // e.g., "08:00 - 09:00"
  start: string; // HH:mm:ss
  end: string;   // HH:mm:ss
}

export default defineComponent({
  name: 'TimetablePage',
  components: {
    ElButton, ElCard, ElSelect, ElOption, ElSkeleton, ElEmpty, ElDialog, ElTag,
    Plus, Edit,
  },
  directives: {
    tooltip: vTooltip,
  },
  setup() {
    const router = useRouter();
    const courses = ref<Course[]>([]);
    const loading = ref(true);
    const currentWeek = ref(1); // Default to week 1, can be dynamic
    const availableWeeks = computed(() => Array.from({ length: 20 }, (_, i) => i + 1)); // Weeks 1-20

    const courseDetailVisible = ref(false);
    const selectedCourse = ref<Course | null>(null);

    const days = ref([
      { label: 'Monday', value: 1 }, { label: 'Tuesday', value: 2 }, { label: 'Wednesday', value: 3 },
      { label: 'Thursday', value: 4 }, { label: 'Friday', value: 5 },
      { label: 'Saturday', value: 6 }, { label: 'Sunday', value: 7 },
    ]);

    // Define time slots for the timetable rows
    const timeSlots = computed<TimeSlot[]>(() => {
      const slots: TimeSlot[] = [];
      for (let i = 8; i < 22; i++) { // 8 AM to 10 PM
        const startHour = i.toString().padStart(2, '0');
        const endHour = (i + 1).toString().padStart(2, '0');
        slots.push({
          slot: i - 7, // 1-indexed slot
          label: `${startHour}:00`,
          start: `${startHour}:00:00`,
          end: `${endHour}:00:00`, // Courses end at the start of the next hour block for simplicity here
        });
      }
      return slots;
    });
    
    const getDayLabel = (dayValue: number) => {
        return days.value.find(d => d.value === dayValue)?.label || 'Unknown Day';
    };


    const fetchCourses = async () => {
      loading.value = true;
      try {
        const response = await axios.get('/api/courses'); // API for logged-in user's courses
        if (response.data && response.data.code === 200) {
          courses.value = response.data.data || [];
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch courses.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching courses.');
        console.error("Fetch courses error:", error);
      } finally {
        loading.value = false;
      }
    };

    const getCoursesForCell = (day: number, slotStartTime: string, slotEndTime: string): Course[] => {
        return courses.value.filter(course => {
            // Check if course is in the current selected week
            if (currentWeek.value < course.startWeek || currentWeek.value > course.endWeek) {
                return false;
            }
            // Check day
            if (course.dayOfWeek !== day) {
                return false;
            }
            // Check time overlap: course starts before slot ends AND course ends after slot starts
            // This simple check assumes courses align with hour blocks. More complex logic needed for partial overlaps.
            // For this grid, we check if the course's start time falls within the slot's hour.
            return course.startTime >= slotStartTime && course.startTime < slotEndTime;
        });
    };


    const viewCourseDetails = (course: Course) => {
      selectedCourse.value = course;
      courseDetailVisible.value = true;
    };

    const editCourse = (course: Course) => {
        if (!course) return;
        router.push({ name: 'CourseEdit', params: { courseId: course.id } });
        courseDetailVisible.value = false; // Close dialog
    };

    const handleWeekChange = (newWeek: number) => {
      currentWeek.value = newWeek;
      // No need to re-fetch courses unless API supports week-specific filtering
      // The filtering is done client-side by getCoursesForCell
    };

    const darkenColor = (color: string, amount = -20) => {
        if (!color) return '#3a8ee6'; // Default darkened color if input is invalid
        try {
            let usePound = false;
            if (color[0] === "#") {
                color = color.slice(1);
                usePound = true;
            }
            const num = parseInt(color, 16);
            let r = (num >> 16) + amount;
            if (r > 255) r = 255;
            else if (r < 0) r = 0;
            let b = ((num >> 8) & 0x00FF) + amount;
            if (b > 255) b = 255;
            else if (b < 0) b = 0;
            let g = (num & 0x0000FF) + amount;
            if (g > 255) g = 255;
            else if (g < 0) g = 0;
            const newColor = (g | (b << 8) | (r << 16)).toString(16).padStart(6, '0');
            return (usePound ? "#" : "") + newColor;
        } catch (e) {
            return '#3a8ee6'; // Fallback for invalid color string
        }
    };


    onMounted(() => {
      fetchCourses();
      // Potentially set currentWeek based on actual current date if needed
    });

    return {
      courses, loading, currentWeek, availableWeeks, days, timeSlots,
      courseDetailVisible, selectedCourse,
      fetchCourses, getCoursesForCell, viewCourseDetails, editCourse, handleWeekChange, darkenColor, getDayLabel,
      Plus, Edit, // Icons
    };
  },
});
</script>

<style scoped>
.timetable-page {
  padding: 20px;
}
.timetable-card {
  border-radius: 8px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header h2 {
  margin: 0;
  font-size: 1.8em;
}
.actions .el-button {
  margin-left: 10px;
}
.empty-timetable {
  padding: 40px 0;
  text-align: center;
}

.timetable-grid {
  display: grid;
  grid-template-columns: 60px repeat(7, 1fr); /* Time column + 7 days */
  border: 1px solid #ebeef5;
  overflow-x: auto; /* For smaller screens */
}
.day-header-cell, .time-slot-cell, .grid-cell {
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  padding: 8px;
  text-align: center;
  min-height: 60px; /* Minimum height for cells */
}
.day-header-cell:last-child, .time-slot-cell:last-child, .grid-cell:last-child {
  border-right: none;
}
.day-header-cell {
  background-color: #f5f7fa;
  font-weight: bold;
  position: sticky; /* Sticky day headers */
  top: 0;
  z-index: 2;
}
.time-column-header {
    position: sticky;
    left:0;
    z-index: 3 !important; /* Ensure time header is above day headers if they overlap */
}
.time-slot-cell {
  background-color: #f5f7fa;
  font-size: 0.8em;
  color: #606266;
  position: sticky; /* Sticky time slots */
  left: 0;
  z-index: 1;
}
.grid-cell {
  position: relative; /* For positioning course blocks */
  padding: 2px; /* Minimal padding for course blocks to fit */
}

.course-block {
  border-radius: 4px;
  padding: 4px 6px;
  color: white;
  font-size: 0.8em;
  margin-bottom: 2px; /* Space between courses in the same slot */
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border: 1px solid; /* Border color set by darkenColor */
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.course-block:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}
.course-name {
  font-weight: bold;
  margin-bottom: 2px;
}
.course-location, .course-time {
  font-size: 0.9em;
  opacity: 0.9;
}

/* Tooltip styling via floating-vue */
:deep(.v-popper__inner) {
  background-color: #303133 !important;
  color: #fff !important;
  padding: 8px 12px !important;
  border-radius: 4px !important;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1) !important;
  font-size: 13px !important;
  white-space: pre-line; /* Respect newlines in tooltip content */
}
:deep(.v-popper__arrow-outer), :deep(.v-popper__arrow-inner) {
  border-color: #303133 !important;
}

.course-details-content p {
    margin: 8px 0;
    font-size: 1em;
    line-height: 1.6;
}
.course-details-content strong {
    color: #555;
}
</style>
