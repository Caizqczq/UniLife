<template>
  <div class="schedule-management-page">
    <el-card class="schedule-card">
      <template #header>
        <div class="card-header">
          <h2>My Schedules & Events</h2>
          <el-button type="primary" :icon="Plus" @click="openAddScheduleModal">Add Schedule</el-button>
        </div>
      </template>

      <div class="filter-controls">
        <el-radio-group v-model="displayMode" size="small" style="margin-bottom: 15px;">
          <el-radio-button label="list">List View</el-radio-button>
          <el-radio-button label="calendar">Calendar View (Basic)</el-radio-button>
        </el-radio-group>
        
        <el-date-picker
          v-if="displayMode === 'calendar'"
          v-model="calendarMonth"
          type="month"
          placeholder="Select month"
          @change="handleMonthChange"
          style="margin-left: 15px; width: 150px;"
          format="YYYY-MM"
          value-format="YYYY-MM"
        />
      </div>


      <el-skeleton :rows="8" animated v-if="loading" />

      <div v-if="!loading && schedules.length === 0" class="empty-schedules">
        <el-empty description="No schedules found. Create one to get started!" />
      </div>

      <!-- List View -->
      <div v-if="!loading && schedules.length > 0 && displayMode === 'list'" class="schedule-list">
        <el-table :data="pagedSchedules" style="width: 100%" stripe>
          <el-table-column prop="title" label="Title" sortable width="200">
            <template #default="scope">
              <el-tag :color="scope.row.color || '#409EFF'" effect="dark" size="small" style="margin-right: 5px; border-radius: 50%; width:10px; height:10px; padding:0; border:none;"/>
              {{ scope.row.title }}
            </template>
          </el-table-column>
          <el-table-column label="Time" sortable width="280">
            <template #default="scope">
              <div v-if="scope.row.isAllDay">
                All Day ({{ formatDateShort(scope.row.startTime) }}
                <span v-if="!isSameDay(scope.row.startTime, scope.row.endTime)"> - {{ formatDateShort(scope.row.endTime) }}</span>)
              </div>
              <div v-else>
                {{ formatDateTime(scope.row.startTime) }} - {{ formatDateTime(scope.row.endTime) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="Location" sortable />
          <el-table-column prop="reminderMinutes" label="Reminder" width="150">
            <template #default="scope">
              {{ formatReminder(scope.row.reminderMinutes) }}
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="150" align="center">
            <template #default="scope">
              <el-button size="small" :icon="Edit" @click="openEditScheduleModal(scope.row)" circle title="Edit"/>
              <el-popconfirm
                title="Are you sure to delete this schedule?"
                confirm-button-text="Yes"
                cancel-button-text="No"
                @confirm="handleDeleteSchedule(scope.row.id!)"
              >
                <template #reference>
                  <el-button size="small" type="danger" :icon="Delete" circle title="Delete" :loading="deletingId === scope.row.id" />
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
            v-if="schedules.length > listPageSize"
            layout="prev, pager, next, total"
            :total="schedules.length"
            :page-size="listPageSize"
            :current-page="listCurrentPage"
            @current-change="handleListPageChange"
            class="list-pagination"
        />
      </div>
      
      <!-- Basic Calendar View Placeholder -->
      <div v-if="!loading && displayMode === 'calendar'" class="calendar-view-basic">
         <el-calendar v-model="calendarSelectedDate" ref="calendarRef">
            <template #date-cell="{ data }">
                <div class="calendar-day-cell" :class="{ 'is-selected': data.isSelected }">
                    <p class="day-number">{{ data.day.split('-').slice(2).join('-') }}</p>
                    <div v-for="event in getEventsForDate(data.day)" :key="event.id" class="calendar-event-item">
                        <el-tooltip :content="`${event.title} (${event.isAllDay ? 'All Day' : formatTime(event.startTime) + '-' + formatTime(event.endTime)})`" placement="top">
                             <div class="event-dot" :style="{ backgroundColor: event.color || '#409EFF' }" @click="openEditScheduleModal(event)"></div>
                        </el-tooltip>
                    </div>
                </div>
            </template>
         </el-calendar>
      </div>


    </el-card>

    <ScheduleEditModal
      :visible="scheduleModalVisible"
      :schedule-to-edit="scheduleToEdit"
      @close="closeScheduleModal"
      @schedule-saved="handleScheduleSaved"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage, ElButton, ElCard, ElTable, ElTableColumn, ElPagination, ElPopconfirm, ElSkeleton, ElEmpty, ElRadioGroup, ElRadioButton, ElDatePicker, ElCalendar, ElTooltip, ElTag } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import ScheduleEditModal, { type Schedule } from '../../components/schedule/ScheduleEditModal.vue'; // Adjusted path
import { dayjs } from 'element-plus';

export default defineComponent({
  name: 'ScheduleManagementPage',
  components: {
    ElButton, ElCard, ElTable, ElTableColumn, ElPagination, ElPopconfirm, ElSkeleton, ElEmpty, ElRadioGroup, ElRadioButton, ElDatePicker, ElCalendar, ElTooltip, ElTag,
    ScheduleEditModal,
    Plus, Edit, Delete,
  },
  setup() {
    const schedules = ref<Schedule[]>([]);
    const loading = ref(true);
    const deletingId = ref<number | null>(null);

    const scheduleModalVisible = ref(false);
    const scheduleToEdit = ref<Schedule | null>(null);

    const displayMode = ref<'list' | 'calendar'>('list');
    const calendarMonth = ref(dayjs().format('YYYY-MM')); // For month selection in calendar view
    const calendarSelectedDate = ref(new Date()); // For el-calendar's current date
    const calendarRef = ref<InstanceType<typeof ElCalendar> | null>(null);

    // List view pagination
    const listCurrentPage = ref(1);
    const listPageSize = ref(10);
    const pagedSchedules = computed(() => {
        const start = (listCurrentPage.value - 1) * listPageSize.value;
        const end = start + listPageSize.value;
        return schedules.value.slice(start, end);
    });
    const handleListPageChange = (page: number) => {
        listCurrentPage.value = page;
    };


    const fetchSchedules = async () => {
      loading.value = true;
      try {
        const response = await axios.get('/api/schedules');
        if (response.data && response.data.code === 200) {
          schedules.value = (response.data.data || []).map((s: any) => ({
            ...s,
            // Ensure dates are in a consistent format if needed, though API should return ISO strings
            startTime: s.startTime,
            endTime: s.endTime,
          })).sort((a, b) => dayjs(a.startTime).valueOf() - dayjs(b.startTime).valueOf()); // Sort by start time
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch schedules.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching schedules.');
        console.error("Fetch schedules error:", error);
      } finally {
        loading.value = false;
      }
    };
    
    const getEventsForDate = (dateStr: string): Schedule[] => { // dateStr is YYYY-MM-DD
        return schedules.value.filter(schedule => {
            const scheduleStartDate = dayjs(schedule.startTime).format('YYYY-MM-DD');
            const scheduleEndDate = dayjs(schedule.endTime).format('YYYY-MM-DD');
            if (schedule.isAllDay) {
                return dayjs(dateStr).isSameOrAfter(scheduleStartDate, 'day') && dayjs(dateStr).isSameOrBefore(scheduleEndDate, 'day');
            }
            return scheduleStartDate === dateStr;
        });
    };
    
    const handleMonthChange = (newMonth: string) => {
        calendarMonth.value = newMonth;
        // el-calendar updates its view based on its v-model (calendarSelectedDate)
        // So, we need to set calendarSelectedDate to a date within the newMonth
        if (calendarRef.value) {
            calendarSelectedDate.value = dayjs(newMonth + '-01').toDate();
        }
    };


    const openAddScheduleModal = () => {
      scheduleToEdit.value = null;
      scheduleModalVisible.value = true;
    };

    const openEditScheduleModal = (schedule: Schedule) => {
      scheduleToEdit.value = { ...schedule };
      scheduleModalVisible.value = true;
    };

    const closeScheduleModal = () => {
      scheduleModalVisible.value = false;
      scheduleToEdit.value = null;
    };

    const handleScheduleSaved = () => {
      fetchSchedules(); // Re-fetch schedules after saving
      closeScheduleModal();
    };

    const handleDeleteSchedule = async (id: number) => {
      deletingId.value = id;
      try {
        const response = await axios.delete(`/api/schedules/${id}`);
        if (response.data && response.data.code === 200) {
          ElMessage.success('Schedule deleted successfully.');
          fetchSchedules(); // Re-fetch
        } else {
          ElMessage.error(response.data.message || 'Failed to delete schedule.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error deleting schedule.');
      } finally {
        deletingId.value = null;
      }
    };

    const formatDateTime = (dateTimeStr: string) => {
      return dayjs(dateTimeStr).format('YYYY-MM-DD HH:mm');
    };
    const formatDateShort = (dateTimeStr: string) => {
      return dayjs(dateTimeStr).format('MMM DD, YYYY');
    };
    const formatTime = (dateTimeStr: string) => {
      return dayjs(dateTimeStr).format('HH:mm');
    };
     const isSameDay = (date1: string, date2: string) => {
        return dayjs(date1).isSame(dayjs(date2), 'day');
    };

    const formatReminder = (minutes: number | null | undefined) => {
      if (minutes === null || typeof minutes === 'undefined' || minutes === 0) return 'No reminder';
      if (minutes === 1440) return '1 day before';
      if (minutes >= 60) return `${minutes / 60} hour(s) before`;
      return `${minutes} minutes before`;
    };

    onMounted(() => {
      fetchSchedules();
    });

    return {
      schedules, loading, deletingId,
      scheduleModalVisible, scheduleToEdit,
      openAddScheduleModal, openEditScheduleModal, closeScheduleModal, handleScheduleSaved, handleDeleteSchedule,
      formatDateTime, formatDateShort, formatReminder, formatTime, isSameDay,
      displayMode, calendarMonth, calendarSelectedDate, calendarRef,
      getEventsForDate, handleMonthChange,
      pagedSchedules, listCurrentPage, listPageSize, handleListPageChange,
      Plus, Edit, Delete,
    };
  },
});
</script>

<style scoped>
.schedule-management-page {
  padding: 20px;
}
.schedule-card {
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
.empty-schedules {
  padding: 40px 0;
  text-align: center;
}
.filter-controls {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
}

.list-pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.calendar-view-basic {
    margin-top: 20px;
}
.calendar-day-cell {
    padding: 4px;
    height: 100%; /* Fill cell height */
    display: flex;
    flex-direction: column;
    align-items: flex-start; /* Align day number to top left */
}
.calendar-day-cell p.day-number {
    margin: 0 0 4px 0;
    font-size: 0.9em;
}
.calendar-event-item {
    margin-bottom: 2px;
    width: 100%;
}
.event-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    display: inline-block;
    cursor: pointer;
}
.el-calendar :deep(.el-calendar__body) {
    padding-bottom: 12px; /* Adjust if needed */
}
.el-calendar :deep(.el-calendar-day) {
    height: auto; /* Let cell content determine height */
    min-height: 80px; /* Minimum height for day cells */
}
</style>
