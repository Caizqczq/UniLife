<template>
  <div class="profile-page">
    <el-card v-if="!loading && userData" class="profile-card">
      <template #header>
        <div class="card-header">
          <el-avatar :size="100" :src="userData.avatar || defaultAvatar" />
          <h2 class="username">{{ userData.username }}</h2>
          <p class="nickname">({{ userData.nickname || 'No nickname' }})</p>
        </div>
      </template>

      <el-descriptions title="User Information" :column="2" border>
        <el-descriptions-item label="Email">
          <el-tag type="info">{{ userData.email }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Gender">
          {{ formatGender(userData.gender) }}
        </el-descriptions-item>
        <el-descriptions-item label="Student ID" v-if="userData.studentId">
          {{ userData.studentId }}
        </el-descriptions-item>
         <el-descriptions-item label="Department" v-if="userData.department">
          {{ userData.department }}
        </el-descriptions-item>
        <el-descriptions-item label="Major" v-if="userData.major">
          {{ userData.major }}
        </el-descriptions-item>
        <el-descriptions-item label="Grade" v-if="userData.grade">
          {{ userData.grade }}
        </el-descriptions-item>
         <el-descriptions-item label="Bio" :span="2" v-if="userData.bio">
          <div class="bio-content">{{ userData.bio }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="isCurrentUser" class="actions">
        <router-link to="/user/settings">
          <el-button type="primary" icon="Edit">Edit Profile</el-button>
        </router-link>
      </div>
    </el-card>

    <el-skeleton :rows="10" animated v-if="loading" />

    <el-empty description="User not found or profile data is unavailable." v-if="!loading && !userData && error" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import { ElMessage, ElCard, ElAvatar, ElDescriptions, ElDescriptionsItem, ElTag, ElButton, ElSkeleton, ElEmpty } from 'element-plus';
import { Edit } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user'; // Assuming a user store exists

interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  email: string;
  avatar: string;
  bio: string;
  gender: number | null; // 0: prefer not to say, 1: male, 2: female, 3: other
  studentId: string;
  department: string;
  major: string;
  grade: string;
}

export default defineComponent({
  name: 'ProfilePage',
  components: {
    ElCard,
    ElAvatar,
    ElDescriptions,
    ElDescriptionsItem,
    ElTag,
    ElButton,
    ElSkeleton,
    ElEmpty,
    Edit,
  },
  props: {
    userId: { // For viewing other users' profiles via route prop e.g., /profile/123
      type: [String, Number],
      default: null,
    },
  },
  setup(props) {
    const route = useRoute();
    const router = useRouter();
    const userStore = useUserStore();
    const userData = ref<UserProfile | null>(null);
    const loading = ref(true);
    const error = ref<string | null>(null);

    const defaultAvatar = '/path/to/default/avatar.png'; // Replace with your actual default avatar path

    const currentRouteUserId = computed(() => props.userId || route.params.userId);
    const isCurrentUser = computed(() => {
      if (!userData.value || !userStore.user) return false;
      return userData.value.id === userStore.user.id;
    });


    const fetchUserProfile = async () => {
      loading.value = true;
      error.value = null;
      userData.value = null;
      const targetUserId = currentRouteUserId.value;

      try {
        let response;
        if (targetUserId) {
          // Viewing a specific user's profile
          response = await axios.get(`/api/user/profile/${targetUserId}`);
        } else {
          // Viewing the logged-in user's own profile
          response = await axios.get('/api/user/profile');
        }

        if (response.data && response.data.code === 200 && response.data.data) {
          userData.value = response.data.data;
          // If this is the current user's profile being fetched, ensure the store is up-to-date
          if (!targetUserId || (userStore.user && userStore.user.id === response.data.data.id)) {
            userStore.setUser(response.data.data);
          }
        } else {
          throw new Error(response.data.message || 'Failed to fetch profile data.');
        }
      } catch (err: any) {
        console.error("Fetch profile error:", err);
        error.value = err.response?.data?.message || err.message || 'Error fetching profile. Please try again.';
        ElMessage.error(error.value);
        // If a specific user ID was given and not found, or other error, redirect or show message
        if (targetUserId && err.response?.status === 404) {
            ElMessage.error(`User with ID ${targetUserId} not found.`);
            // Optionally redirect to a 404 page or back
            // router.push('/not-found');
        }
      } finally {
        loading.value = false;
      }
    };

    const formatGender = (gender: number | null) => {
      switch (gender) {
        case 1: return 'Male';
        case 2: return 'Female';
        case 3: return 'Other';
        case 0: return 'Prefer not to say';
        default: return 'Not specified';
      }
    };

    onMounted(() => {
      fetchUserProfile();
    });

    // Watch for changes in route params if the component is reused for different user profiles
    watch(() => route.params.userId, (newUserId, oldUserId) => {
      if (newUserId && newUserId !== oldUserId) {
        fetchUserProfile();
      }
    });
     // Watch for prop changes if userId prop is used
    watch(() => props.userId, (newUserId, oldUserId) => {
      if (newUserId && newUserId !== oldUserId) {
        fetchUserProfile();
      }
    });


    return {
      userData,
      loading,
      error,
      isCurrentUser,
      formatGender,
      defaultAvatar,
      Edit, // Make icon available in template
    };
  },
});
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.profile-card {
  border-radius: 8px;
}

.card-header {
  text-align: center;
  padding-bottom: 20px;
}

.username {
  margin-top: 10px;
  margin-bottom: 5px;
  font-size: 24px;
  font-weight: bold;
}

.nickname {
  font-size: 16px;
  color: #606266;
  margin-bottom: 20px;
}

.el-descriptions {
  margin-top: 20px;
}

.el-descriptions-item__label {
  font-weight: bold;
}

.bio-content {
  white-space: pre-wrap; /* To respect newlines in bio */
  line-height: 1.6;
}

.actions {
  margin-top: 30px;
  text-align: center;
}

.el-skeleton {
    padding: 20px;
}
</style>
