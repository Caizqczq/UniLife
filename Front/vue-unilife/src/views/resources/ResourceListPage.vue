<template>
  <div class="resource-list-page">
    <el-container>
      <el-aside width="250px" class="sidebar">
        <h3>Categories</h3>
        <el-menu
          :default-active="selectedCategoryId || 'all'"
          class="category-menu"
          @select="handleCategorySelect"
        >
          <el-menu-item index="all">
            <el-icon><Files /></el-icon>
            <span>All Resources</span>
          </el-menu-item>
          <el-menu-item
            v-for="category in categories"
            :key="category.id"
            :index="category.id.toString()"
          >
            <el-icon><Folder /></el-icon>
            <span>{{ category.name }}</span>
          </el-menu-item>
        </el-menu>
        <el-divider />
        <h3>Sort By</h3>
         <el-radio-group v-model="sortBy" @change="handleSortChange" class="sort-options">
          <el-radio-button label="latest">Latest</el-radio-button>
          <el-radio-button label="downloads">Most Downloads</el-radio-button>
          <el-radio-button label="likes">Most Liked</el-radio-button>
        </el-radio-group>
      </el-aside>

      <el-main>
        <div class="toolbar">
          <h2>{{ pageTitle }}</h2>
          <router-link to="/resources/upload">
            <el-button type="primary" :icon="UploadFilled">Upload Resource</el-button>
          </router-link>
        </div>

        <el-skeleton :rows="10" animated v-if="loading" />

        <div v-if="!loading && resources.length === 0" class="empty-state">
          <el-empty description="No resources found matching your criteria." />
        </div>

        <div v-if="!loading && resources.length > 0" class="resource-list">
          <el-card v-for="resource in resources" :key="resource.id" class="resource-card" shadow="hover">
            <template #header>
              <div class="resource-header">
                <router-link :to="`/resources/${resource.id}`" class="resource-title-link">
                  <h3 class="resource-title">{{ resource.title }}</h3>
                </router-link>
                <span class="resource-uploader">
                  Uploaded by: {{ resource.user?.nickname || resource.user?.username || 'Unknown' }}
                </span>
              </div>
            </template>
            <div class="resource-description">{{ truncateText(resource.description, 100) }}</div>
            <div class="resource-meta">
              <el-tag size="small" effect="plain" class="file-type-tag">
                <el-icon><Document /></el-icon> {{ resource.fileType || 'N/A' }}
              </el-tag>
              <el-tag size="small" effect="plain" class="file-size-tag">
                <el-icon><Coin /></el-icon> {{ formatFileSize(resource.fileSize) }}
              </el-tag>
              <span><el-icon><Download /></el-icon> {{ resource.downloadCount || 0 }}</span>
              <span><el-icon><Pointer /></el-icon> {{ resource.likeCount || 0 }}</span>
              <span class="resource-date"><el-icon><Calendar /></el-icon> {{ formatDate(resource.createdAt) }}</span>
            </div>
            <div class="resource-actions">
                <router-link :to="`/resources/${resource.id}`">
                     <el-button type="primary" plain size="small">View Details</el-button>
                </router-link>
                <el-button type="success" plain size="small" :icon="Download" @click="directDownloadResource(resource.id, resource.title)">Download</el-button>
            </div>
          </el-card>
        </div>

        <el-pagination
          v-if="!loading && totalResources > pageSize"
          class="pagination"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalResources"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import { ElMessage, ElButton, ElCard, ElContainer, ElAside, ElMain, ElMenu, ElMenuItem, ElIcon, ElPagination, ElSkeleton, ElEmpty, ElDivider, ElRadioGroup, ElRadioButton, ElTag } from 'element-plus';
import { Files, Folder, UploadFilled, Download, Pointer, Calendar, Document, Coin } from '@element-plus/icons-vue';
import { useRouter, useRoute } from 'vue-router';

interface ResourceUser {
  id: number;
  username: string;
  nickname?: string;
}
interface Resource {
  id: number;
  title: string;
  description: string;
  fileName: string;
  fileType: string;
  fileSize: number; // in bytes
  userId: number;
  user?: ResourceUser;
  categoryId: number | null;
  categoryName?: string;
  downloadCount: number;
  likeCount: number;
  isLiked?: boolean;
  createdAt: string;
  updatedAt: string;
}

interface Category {
  id: number;
  name: string;
  description?: string;
}

export default defineComponent({
  name: 'ResourceListPage',
  components: {
    ElButton, ElCard, ElContainer, ElAside, ElMain, ElMenu, ElMenuItem, ElIcon, ElPagination, ElSkeleton, ElEmpty, ElDivider, ElRadioGroup, ElRadioButton, ElTag,
    Files, Folder, UploadFilled, Download, Pointer, Calendar, Document, Coin,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();

    const resources = ref<Resource[]>([]);
    const categories = ref<Category[]>([]); // Assuming generic categories for now
    const loading = ref(true);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const totalResources = ref(0);
    const selectedCategoryId = ref<string | null>(null);
    const sortBy = ref('latest'); // 'latest', 'downloads', 'likes'

    const pageTitle = computed(() => {
      if (selectedCategoryId.value && selectedCategoryId.value !== 'all') {
        const category = categories.value.find(c => c.id.toString() === selectedCategoryId.value);
        return category ? `${category.name} Resources` : 'Resources';
      }
      return 'All Resources';
    });

    const fetchCategories = async () => {
      // Assuming /api/categories is the endpoint for resource categories as well.
      // If not, this URL needs to be changed.
      try {
        const response = await axios.get('/api/categories');
        if (response.data && response.data.code === 200) {
          categories.value = response.data.data || [];
        } else {
          ElMessage.warning(response.data.message || 'Could not load categories.');
        }
      } catch (error: any) {
        console.error("Fetch categories error:", error);
        // Non-critical, so don't block page load, maybe just log or show small warning
      }
    };

    const fetchResources = async () => {
      loading.value = true;
      const params: any = {
        page: currentPage.value,
        size: pageSize.value,
        sort: sortBy.value, // Assuming API supports sort by 'latest', 'downloads', 'likes'
      };
      
      let url = '/api/resources';
      if (selectedCategoryId.value && selectedCategoryId.value !== 'all') {
        // If API is /api/resources/category/{id}
         url = `/api/resources/category/${selectedCategoryId.value}`;
        // If API is /api/resources?categoryId={id}
        // params.categoryId = selectedCategoryId.value;
      }


      try {
        const response = await axios.get(url, { params });
        if (response.data && response.data.code === 200 && response.data.data) {
          resources.value = response.data.data.records || response.data.data || [];
          totalResources.value = response.data.data.total || resources.value.length;
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch resources.');
          resources.value = [];
          totalResources.value = 0;
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching resources.');
        console.error("Fetch resources error:", error);
        resources.value = [];
        totalResources.value = 0;
      } finally {
        loading.value = false;
      }
    };
    
    const directDownloadResource = async (resourceId: number, resourceTitle: string = 'download') => {
        try {
            const response = await axios.get(`/api/resources/download/${resourceId}`, {
                responseType: 'blob', // Important for file downloads
            });

            const contentType = response.headers['content-type'];
            const contentDisposition = response.headers['content-disposition'];
            let fileName = resourceTitle;

            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename\*?=['"]?(?:UTF-\d'')?([^;\r\n"']*)['"]?;?/i);
                if (fileNameMatch && fileNameMatch[1]) {
                    fileName = decodeURIComponent(fileNameMatch[1]);
                } else {
                     const plainFilenameMatch = contentDisposition.match(/filename="?([^"]+)"?/i);
                     if(plainFilenameMatch && plainFilenameMatch[1]) fileName = plainFilenameMatch[1];
                }
            }
            
            // Fallback if filename couldn't be extracted
            if (fileName === 'download' && resources.value.find(r => r.id === resourceId)?.fileName) {
                fileName = resources.value.find(r => r.id === resourceId)!.fileName;
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
            
            // Optionally, increment download count on client-side if API doesn't do it on download
            const resource = resources.value.find(r => r.id === resourceId);
            if (resource) resource.downloadCount++;

        } catch (error: any) {
            console.error('Download error:', error);
            ElMessage.error(error.response?.data?.message || 'Download failed. Please try again.');
        }
    };


    const handleCategorySelect = (index: string) => {
      selectedCategoryId.value = index === 'all' ? null : index;
      currentPage.value = 1;
      router.push({ query: { ...route.query, category: index === 'all' ? undefined : index, page: undefined, sort: sortBy.value } });
    };

    const handleSortChange = (newSortBy: string | number | boolean) => {
      sortBy.value = newSortBy as string;
      currentPage.value = 1;
      router.push({ query: { ...route.query, sort: sortBy.value, page: undefined, category: selectedCategoryId.value } });
    };

    const handlePageChange = (page: number) => {
      currentPage.value = page;
      router.push({ query: { ...route.query, page: page === 1 ? undefined : page, sort: sortBy.value, category: selectedCategoryId.value } });
    };

    const formatDate = (dateString: string) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    };

    const formatFileSize = (bytes: number, decimals = 2) => {
        if (!+bytes) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
    };
    
    const truncateText = (text: string, maxLength: number) => {
      if (!text) return '';
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    };

    onMounted(() => {
      selectedCategoryId.value = route.query.category as string || null;
      sortBy.value = route.query.sort as string || 'latest';
      currentPage.value = parseInt(route.query.page as string) || 1;

      fetchCategories();
      fetchResources();
    });

    watch(
      () => route.query,
      (newQuery) => {
        const newPage = parseInt(newQuery.page as string) || 1;
        const newCategory = newQuery.category as string || null;
        const newSort = newQuery.sort as string || 'latest';

        let needsFetch = false;
        if (newPage !== currentPage.value) { currentPage.value = newPage; needsFetch = true; }
        if (newCategory !== selectedCategoryId.value) { selectedCategoryId.value = newCategory; if(currentPage.value !==1) {currentPage.value =1;} else needsFetch = true;}
        if (newSort !== sortBy.value) { sortBy.value = newSort; if(currentPage.value !==1) {currentPage.value =1;} else needsFetch = true;}
        
        if (needsFetch) fetchResources();
      },
      { deep: true }
    );

    return {
      resources, categories, loading, currentPage, pageSize, totalResources, selectedCategoryId, sortBy, pageTitle,
      handleCategorySelect, handleSortChange, handlePageChange, formatDate, formatFileSize, truncateText, directDownloadResource,
      Files, Folder, UploadFilled, Download, Pointer, Calendar, Document, Coin, // Icons
    };
  },
});
</script>

<style scoped>
.resource-list-page {
  padding: 20px;
}
.sidebar {
  padding-right: 20px;
  border-right: 1px solid #e4e7ed;
}
.sidebar h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
  color: #303133;
}
.category-menu .el-menu-item {
  height: 40px;
  line-height: 40px;
}
.category-menu .el-menu-item.is-active {
  font-weight: bold;
  color: var(--el-color-primary);
}
.sort-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
}
.sort-options .el-radio-button :deep(.el-radio-button__inner) {
    width: 100%;
    text-align: left;
    border-radius: 4px !important;
    border: 1px solid #dcdfe6 !important;
    margin-bottom: -1px;
}
.sort-options .el-radio-button.is-active :deep(.el-radio-button__inner) {
    background-color: var(--el-color-primary-light-9);
    border-color: var(--el-color-primary) !important;
    color: var(--el-color-primary);
    box-shadow: -1px 0 0 0 var(--el-color-primary) !important;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar h2 { margin: 0; }

.resource-card {
  margin-bottom: 20px;
}
.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.resource-title-link {
  text-decoration: none;
  color: inherit;
}
.resource-title {
  margin: 0;
  font-size: 1.3em;
  color: #303133;
}
.resource-title-link:hover .resource-title {
  color: var(--el-color-primary);
}
.resource-uploader {
  font-size: 0.85em;
  color: #606266;
}
.resource-description {
  color: #606266;
  font-size: 0.95em;
  line-height: 1.5;
  margin: 10px 0;
}
.resource-meta {
  display: flex;
  flex-wrap: wrap; /* Allow items to wrap */
  gap: 10px; /* Spacing between meta items */
  align-items: center;
  font-size: 0.85em;
  color: #909399;
  margin-bottom: 15px;
}
.resource-meta span, .resource-meta .el-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.resource-date {
  margin-left: auto; /* Pushes date to the right if space allows */
}
.resource-actions {
    margin-top: 10px;
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
.empty-state {
  margin-top: 40px;
  text-align: center;
}
</style>
