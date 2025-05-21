<template>
  <div class="resource-list-container">
    <div class="resource-header">
      <h1 class="page-title">学习资源共享</h1>
      <div class="resource-actions">
        <el-button type="primary" @click="handleUpload" v-if="isLoggedIn">
          <el-icon><Upload /></el-icon>上传资源
        </el-button>
      </div>
    </div>

    <div class="resource-filters">
      <el-card shadow="never" class="filter-card">
        <div class="filter-row">
          <div class="filter-item">
            <span class="filter-label">分类：</span>
            <el-radio-group v-model="filters.categoryId" @change="handleFilterChange">
              <el-radio-button :label="null">全部</el-radio-button>
              <el-radio-button v-for="category in categories" :key="category.id" :label="category.id">
                {{ category.name }}
              </el-radio-button>
            </el-radio-group>
          </div>
          <div class="filter-item">
            <el-input
              v-model="filters.keyword"
              placeholder="搜索资源"
              class="search-input"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            >
              <template #suffix>
                <el-icon class="search-icon" @click="handleSearch"><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </el-card>
    </div>

    <div class="resource-content">
      <el-empty v-if="resources.length === 0" description="暂无资源" />

      <el-row :gutter="20" v-else>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="resource in resources" :key="resource.id" class="resource-col">
          <el-card class="resource-card" shadow="hover" @click="viewResourceDetail(resource.id)">
            <div class="resource-icon">
              <el-icon v-if="resource.fileType.includes('pdf')" size="40"><Document /></el-icon>
              <el-icon v-else-if="resource.fileType.includes('image')" size="40"><Picture /></el-icon>
              <el-icon v-else-if="resource.fileType.includes('word')" size="40"><Reading /></el-icon>
              <el-icon v-else-if="resource.fileType.includes('excel')" size="40"><Grid /></el-icon>
              <el-icon v-else-if="resource.fileType.includes('powerpoint')" size="40"><Promotion /></el-icon>
              <el-icon v-else-if="resource.fileType.includes('zip') || resource.fileType.includes('rar')" size="40"><Folder /></el-icon>
              <el-icon v-else size="40"><Files /></el-icon>
            </div>
            <h3 class="resource-title">{{ resource.title }}</h3>
            <p class="resource-description" v-if="resource.description">{{ resource.description }}</p>
            <div class="resource-meta">
              <span class="resource-category">{{ resource.categoryName }}</span>
              <span class="resource-size">{{ formatFileSize(resource.fileSize) }}</span>
            </div>
            <div class="resource-footer">
              <div class="resource-uploader">
                <el-avatar :size="24" :src="resource.avatar"></el-avatar>
                <span>{{ resource.nickname }}</span>
              </div>
              <div class="resource-stats">
                <span class="resource-downloads">
                  <el-icon><Download /></el-icon> {{ resource.downloadCount }}
                </span>
                <span class="resource-likes">
                  <el-icon><Star /></el-icon> {{ resource.likeCount }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 上传资源对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传资源"
      width="500px"
    >
      <el-form :model="uploadForm" label-width="80px" :rules="uploadRules" ref="uploadFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入资源标题"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="uploadForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="文件" prop="file">
          <el-upload
            class="resource-upload"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploading">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import { resourceApi } from '@/api';
import { useUserStore } from '@/stores';
import { Document, Picture, Reading, Grid, Promotion, Folder, Files, Upload, Download, Star, Search } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 资源列表数据
const resources = ref<any[]>([]);
const categories = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);
const loading = ref(false);

// 筛选条件
const filters = reactive({
  categoryId: null as number | null,
  keyword: ''
});

// 上传资源相关
const uploadDialogVisible = ref(false);
const uploadFormRef = ref<FormInstance>();
const uploading = ref(false);
const uploadForm = reactive({
  title: '',
  description: '',
  categoryId: undefined as number | undefined,
  file: null as File | null
});

const uploadRules = {
  title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  file: [{ required: true, message: '请上传文件', trigger: 'change' }]
};

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchResources(),
    fetchCategories()
  ]);
});

// 获取资源列表
const fetchResources = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...filters
    };

    const res = await resourceApi.getResources(params);
    if (res.code === 200) {
      resources.value = res.data.list;
      total.value = res.data.total;
    }
  } catch (error) {
    console.error('获取资源列表失败:', error);
    ElMessage.error('获取资源列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await resourceApi.getResourceCategories();
    if (res.code === 200) {
      categories.value = res.data.list;
    }
  } catch (error) {
    console.error('获取分类列表失败:', error);
  }
};

// 处理筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1;
  fetchResources();
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchResources();
};

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchResources();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchResources();
};

// 查看资源详情
const viewResourceDetail = (id: number) => {
  router.push(`/resource/${id}`);
};

// 处理上传资源
const handleUpload = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  uploadDialogVisible.value = true;
};

// 处理文件变化
const handleFileChange = (file: any) => {
  uploadForm.file = file.raw;
};

// 处理文件移除
const handleFileRemove = () => {
  uploadForm.file = null;
};

// 提交上传
const submitUpload = async () => {
  if (!uploadFormRef.value) return;

  await uploadFormRef.value.validate(async (valid) => {
    if (valid && uploadForm.file) {
      uploading.value = true;

      try {
        const formData = new FormData();
        formData.append('file', uploadForm.file);
        formData.append('title', uploadForm.title);
        formData.append('categoryId', String(uploadForm.categoryId));
        formData.append('description', uploadForm.description || '');

        const res = await resourceApi.uploadResource(formData);

        if (res.code === 200) {
          ElMessage.success('资源上传成功');
          uploadDialogVisible.value = false;
          resetUploadForm();
          fetchResources();
        }
      } catch (error) {
        console.error('上传资源失败:', error);
        ElMessage.error('上传资源失败');
      } finally {
        uploading.value = false;
      }
    }
  });
};

// 重置上传表单
const resetUploadForm = () => {
  uploadForm.title = '';
  uploadForm.description = '';
  uploadForm.categoryId = undefined;
  uploadForm.file = null;
  if (uploadFormRef.value) {
    uploadFormRef.value.resetFields();
  }
};

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB';
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }
};
</script>

<style scoped>
.resource-list-container {
  padding: 20px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.resource-filters {
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

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  margin-right: 10px;
  font-weight: 500;
}

.search-input {
  width: 250px;
}

.search-icon {
  cursor: pointer;
}

.resource-content {
  margin-top: 20px;
}

.resource-col {
  margin-bottom: 20px;
}

.resource-card {
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s;
}

.resource-card:hover {
  transform: translateY(-5px);
}

.resource-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  color: var(--el-color-primary);
}

.resource-title {
  font-size: 16px;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-description {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
  font-size: 13px;
}

.resource-category {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  padding: 2px 8px;
  border-radius: 4px;
}

.resource-size {
  color: #999;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  font-size: 13px;
}

.resource-uploader {
  display: flex;
  align-items: center;
  gap: 5px;
}

.resource-stats {
  display: flex;
  gap: 10px;
}

.resource-downloads, .resource-likes {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.resource-upload {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
