<template>
  <div class="resource-detail-page">
    <el-skeleton :rows="10" animated v-if="loading" />

    <el-card v-if="!loading && resource" class="resource-card">
      <template #header>
        <div class="header-content">
          <h1>{{ resource.title }}</h1>
          <div class="actions" v-if="isUploader">
            <el-button type="primary" plain size="small" :icon="Edit" @click="goToEditPage">Edit</el-button>
            <el-popconfirm
              title="Are you sure you want to delete this resource?"
              confirm-button-text="Yes, Delete"
              cancel-button-text="No"
              @confirm="handleDeleteResource"
            >
              <template #reference>
                <el-button type="danger" plain size="small" :icon="Delete" :loading="deleting">Delete</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border class="resource-info-table">
        <el-descriptions-item label="Uploader">
          <el-avatar :size="24" :src="resource.user?.avatar || defaultAvatar" style="margin-right: 8px;" />
          <span>{{ resource.user?.nickname || resource.user?.username || 'Unknown' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="Upload Date">{{ formatDate(resource.createdAt) }}</el-descriptions-item>
        
        <el-descriptions-item label="Category" v-if="resource.categoryName">
            <el-tag size="small">{{ resource.categoryName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="File Name">{{ resource.fileName }}</el-descriptions-item>
        
        <el-descriptions-item label="File Type">
            <el-tag effect="plain" size="small"><el-icon><Document /></el-icon> {{ resource.fileType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="File Size">
            <el-tag effect="plain" size="small"><el-icon><Coin /></el-icon> {{ formatFileSize(resource.fileSize) }}</el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="Downloads">
            <el-icon><DownloadIcon /></el-icon> {{ resource.downloadCount }}
        </el-descriptions-item>
        <el-descriptions-item label="Likes">
            <el-icon><PointerIcon /></el-icon> {{ localLikeCount }}
        </el-descriptions-item>
        
        <el-descriptions-item label="Description" :span="2" v-if="resource.description">
          <div class="description-content">{{ resource.description }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <div class="page-actions">
        <el-button 
          type="success" 
          size="large" 
          :icon="DownloadIcon" 
          @click="handleDownloadResource"
          :loading="downloading"
        >
          Download File
        </el-button>
        <el-button
          size="large"
          @click="toggleLike"
          :loading="likeLoading"
          :icon="PointerIcon"
          :type="isLikedByCurrentUser ? 'danger' : 'info'"
          plain
        >
          {{ isLikedByCurrentUser ? 'Unlike' : 'Like' }} ({{ localLikeCount }})
        </el-button>
      </div>
    </el-card>

    <el-alert
        v-if="!loading && error"
        :title="error"
        type="error"
        show-icon
        :closable="false"
        style="margin-top: 20px;"
    />
    <el-empty v-if="!loading && !resource && !error" description="Resource not found." style="margin-top: 20px;" />

  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElCard, ElAvatar, ElButton, ElIcon, ElSkeleton, ElPopconfirm, ElDescriptions, ElDescriptionsItem, ElTag, ElAlert, ElEmpty } from 'element-plus';
import { Calendar, Download as DownloadIcon, Pointer as PointerIcon, Edit, Delete, Document, Coin } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user';

interface ResourceUser {
  id: number;
  username: string;
  nickname?: string;
  avatar?: string;
}

interface Resource {
  id: number;
  title: string;
  description: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  userId: number;
  user?: ResourceUser;
  categoryId: number | null;
  categoryName?: string; // Assuming API provides this
  downloadCount: number;
  likeCount: number;
  isLiked?: boolean; // Important: API should provide this for current user
  createdAt: string;
  updatedAt: string;
}

export default defineComponent({
  name: 'ResourceDetailPage',
  components: {
    ElCard, ElAvatar, ElButton, ElIcon, ElSkeleton, ElPopconfirm, ElDescriptions, ElDescriptionsItem, ElTag, ElAlert, ElEmpty,
    Calendar, DownloadIcon, PointerIcon, Edit, Delete, Document, Coin,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const authStore = useUserStore();

    const resource = ref<Resource | null>(null);
    const loading = ref(true);
    const error = ref<string | null>(null);
    const likeLoading = ref(false);
    const deleting = ref(false);
    const downloading = ref(false);

    const localLikeCount = ref(0);
    const isLikedByCurrentUser = ref(false);

    const defaultAvatar = '/img/default-avatar.png'; 
    const resourceId = computed(() => Number(route.params.resourceId as string));

    const isUploader = computed(() => {
      return authStore.user && resource.value && authStore.user.id === resource.value.userId;
    });

    const fetchResourceDetails = async () => {
      loading.value = true;
      error.value = null;
      resource.value = null;
      try {
        const response = await axios.get(`/api/resources/${resourceId.value}`);
        if (response.data && response.data.code === 200 && response.data.data) {
          resource.value = response.data.data;
          localLikeCount.value = resource.value?.likeCount || 0;
          isLikedByCurrentUser.value = resource.value?.isLiked || false;
        } else {
          throw new Error(response.data.message || 'Resource not found or failed to load.');
        }
      } catch (err: any) {
        console.error("Fetch resource details error:", err);
        error.value = err.response?.data?.message || err.message || 'Could not load the resource.';
         if (err.response?.status === 404) {
            error.value = "Resource not found.";
        }
      } finally {
        loading.value = false;
      }
    };

    const handleDownloadResource = async () => {
      if (!resource.value) return;
      downloading.value = true;
      try {
        const response = await axios.get(`/api/resources/download/${resource.value.id}`, {
          responseType: 'blob',
        });

        const contentType = response.headers['content-type'];
        const contentDisposition = response.headers['content-disposition'];
        let fileName = resource.value.fileName; // Default to stored filename

        if (contentDisposition) {
            const fileNameMatch = contentDisposition.match(/filename\*?=['"]?(?:UTF-\d'')?([^;\r\n"']*)['"]?;?/i);
            if (fileNameMatch && fileNameMatch[1]) {
                fileName = decodeURIComponent(fileNameMatch[1]);
            } else {
                  const plainFilenameMatch = contentDisposition.match(/filename="?([^"]+)"?/i);
                  if(plainFilenameMatch && plainFilenameMatch[1]) fileName = plainFilenameMatch[1];
            }
        }
        
        const blob = new Blob([response.data], { type: contentType || 'application/octet-stream' });
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(link.href);
        ElMessage.success(`Downloading ${fileName}`);
        
        // Increment download count locally and potentially on server if not done by download endpoint
        if (resource.value) resource.value.downloadCount++;

      } catch (err: any) {
        ElMessage.error(err.response?.data?.message || 'Download failed.');
        console.error("Download error:", err);
      } finally {
        downloading.value = false;
      }
    };

    const toggleLike = async () => {
      if (!authStore.isLoggedIn) {
        ElMessage.warning('Please login to like resources.');
        router.push({ name: 'Login', query: { redirect: route.fullPath } });
        return;
      }
      if (!resource.value) return;
      likeLoading.value = true;
      try {
        let response;
        if (isLikedByCurrentUser.value) {
          response = await axios.delete(`/api/resources/${resource.value.id}/like`);
          if (response.data && response.data.code === 200) {
            isLikedByCurrentUser.value = false;
            localLikeCount.value = Math.max(0, localLikeCount.value - 1);
          }
        } else {
          response = await axios.post(`/api/resources/${resource.value.id}/like`);
          if (response.data && response.data.code === 200) {
            isLikedByCurrentUser.value = true;
            localLikeCount.value++;
          }
        }
         if (response.data.code !== 200) {
            ElMessage.error(response.data.message || `Action failed.`);
        } else {
            ElMessage.success(`Resource ${isLikedByCurrentUser.value ? 'liked' : 'unliked'}!`);
        }
      } catch (err: any) {
        ElMessage.error(err.response?.data?.message || 'Error updating like status.');
      } finally {
        likeLoading.value = false;
      }
    };

    const goToEditPage = () => {
      // Assuming an edit page route exists: /resources/edit/:resourceId
      if (resource.value) {
        router.push({ name: 'ResourceEdit', params: { resourceId: resource.value.id } });
      }
    };

    const handleDeleteResource = async () => {
      if (!resource.value) return;
      deleting.value = true;
      try {
        const response = await axios.delete(`/api/resources/${resource.value.id}`);
        if (response.data && response.data.code === 200) {
          ElMessage.success('Resource deleted successfully.');
          router.push({ name: 'ResourceList' }); 
        } else {
          ElMessage.error(response.data.message || 'Failed to delete resource.');
        }
      } catch (err: any) {
        ElMessage.error(err.response?.data?.message || 'Error deleting resource.');
      } finally {
        deleting.value = false;
      }
    };
    
    const formatDate = (dateString: string) => {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    const formatFileSize = (bytes: number, decimals = 2) => {
        if (!+bytes) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
    };


    onMounted(() => {
      fetchResourceDetails();
    });

    watch(() => route.params.resourceId, (newId, oldId) => {
      if (newId && newId !== oldId) {
        fetchResourceDetails();
      }
    });

    return {
      resource, loading, error, isUploader, defaultAvatar, formatDate, formatFileSize,
      handleDownloadResource, downloading,
      toggleLike, likeLoading, localLikeCount, isLikedByCurrentUser,
      goToEditPage, handleDeleteResource, deleting,
      // Icons
      Calendar, DownloadIcon, PointerIcon, Edit, Delete, Document, Coin,
    };
  },
});
</script>

<style scoped>
.resource-detail-page {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}
.resource-card {
  border-radius: 8px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.header-content h1 {
  font-size: 2em;
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
  word-break: break-word;
  flex-grow: 1;
  padding-right: 15px;
}
.actions .el-button {
  margin-left: 10px;
}

.resource-info-table {
  margin-top: 15px;
}
.el-descriptions-item__label {
  font-weight: bold;
}
.description-content {
  white-space: pre-wrap; /* Respects newlines in description */
  line-height: 1.6;
  color: #555;
}

.page-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 15px;
  justify-content: center; /* Center buttons */
}
.page-actions .el-button {
    min-width: 150px; /* Give buttons some width */
}
</style>
