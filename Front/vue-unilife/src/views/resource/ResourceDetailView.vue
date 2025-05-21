<template>
  <div class="resource-detail-container">
    <el-card class="resource-detail-card" v-loading="loading">
      <template v-if="resource">
        <div class="resource-header">
          <div class="resource-title-section">
            <h1 class="resource-title">{{ resource.title }}</h1>
            <el-tag size="small" type="primary">{{ resource.categoryName }}</el-tag>
          </div>
          <div class="resource-actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon> 下载
            </el-button>
            <el-button 
              :type="resource.isLiked ? 'danger' : 'default'" 
              @click="handleLike"
            >
              <el-icon><Star /></el-icon> {{ resource.isLiked ? '已点赞' : '点赞' }} ({{ resource.likeCount }})
            </el-button>
            <el-dropdown v-if="isOwner" trigger="click">
              <el-button>
                <el-icon><More /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleEdit">编辑</el-dropdown-item>
                  <el-dropdown-item @click="handleDelete" style="color: var(--el-color-danger)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <el-divider />

        <div class="resource-info">
          <div class="resource-meta">
            <div class="resource-uploader">
              <el-avatar :size="40" :src="resource.avatar"></el-avatar>
              <div class="uploader-info">
                <div class="uploader-name">{{ resource.nickname }}</div>
                <div class="upload-time">上传于 {{ formatDate(resource.createdAt) }}</div>
              </div>
            </div>
            <div class="resource-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>下载次数: {{ resource.downloadCount }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>点赞数: {{ resource.likeCount }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Files /></el-icon>
                <span>文件大小: {{ formatFileSize(resource.fileSize) }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Document /></el-icon>
                <span>文件类型: {{ formatFileType(resource.fileType) }}</span>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="resource-description">
            <h3>资源描述</h3>
            <div class="description-content">
              {{ resource.description || '暂无描述' }}
            </div>
          </div>

          <el-divider />

          <div class="resource-preview" v-if="canPreview">
            <h3>预览</h3>
            <div class="preview-container">
              <img v-if="isImage" :src="resource.fileUrl" alt="资源预览" class="preview-image" />
              <div v-else class="no-preview">
                <el-icon size="48"><Document /></el-icon>
                <p>该文件类型不支持在线预览</p>
              </div>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="资源不存在或已被删除" />
    </el-card>

    <!-- 编辑资源对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑资源信息"
      width="500px"
    >
      <el-form :model="editForm" label-width="80px" :rules="editRules" ref="editFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入资源标题"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类">
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
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitting">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import { resourceApi } from '@/api';
import { useUserStore } from '@/stores';
import { Download, Star, More, View, Files, Document } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const resourceId = computed(() => Number(route.params.id));

// 资源数据
const resource = ref<any>(null);
const loading = ref(true);
const categories = ref<any[]>([]);

// 编辑资源相关
const editDialogVisible = ref(false);
const editFormRef = ref<FormInstance>();
const submitting = ref(false);
const editForm = reactive({
  title: '',
  description: '',
  categoryId: undefined as number | undefined
});

const editRules = {
  title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
};

// 计算属性
const isOwner = computed(() => {
  if (!resource.value || !userStore.user) return false;
  return resource.value.userId === userStore.user.id;
});

const isImage = computed(() => {
  if (!resource.value) return false;
  return resource.value.fileType.includes('image');
});

const canPreview = computed(() => {
  if (!resource.value) return false;
  return isImage.value;
});

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchResourceDetail(),
    fetchCategories()
  ]);
});

// 获取资源详情
const fetchResourceDetail = async () => {
  loading.value = true;
  try {
    const res = await resourceApi.getResourceDetail(resourceId.value);
    if (res.code === 200) {
      resource.value = res.data;
    }
  } catch (error) {
    console.error('获取资源详情失败:', error);
    ElMessage.error('获取资源详情失败');
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

// 处理下载
const handleDownload = async () => {
  try {
    const res = await resourceApi.downloadResource(resourceId.value);
    if (res.code === 200) {
      // 创建下载链接
      const link = document.createElement('a');
      link.href = res.data.fileUrl;
      link.download = res.data.fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // 刷新资源详情，更新下载次数
      fetchResourceDetail();
    }
  } catch (error) {
    console.error('下载资源失败:', error);
    ElMessage.error('下载资源失败');
  }
};

// 处理点赞
const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  try {
    const res = await resourceApi.likeResource(resourceId.value);
    if (res.code === 200) {
      // 更新点赞状态
      resource.value.isLiked = !resource.value.isLiked;
      resource.value.likeCount += resource.value.isLiked ? 1 : -1;
      ElMessage.success(resource.value.isLiked ? '点赞成功' : '取消点赞成功');
    }
  } catch (error) {
    console.error('点赞操作失败:', error);
    ElMessage.error('点赞操作失败');
  }
};

// 处理编辑
const handleEdit = () => {
  editForm.title = resource.value.title;
  editForm.description = resource.value.description || '';
  editForm.categoryId = resource.value.categoryId;
  editDialogVisible.value = true;
};

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return;
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      
      try {
        const res = await resourceApi.updateResource(resourceId.value, editForm);
        
        if (res.code === 200) {
          ElMessage.success('资源信息更新成功');
          editDialogVisible.value = false;
          fetchResourceDetail();
        }
      } catch (error) {
        console.error('更新资源信息失败:', error);
        ElMessage.error('更新资源信息失败');
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 处理删除
const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除该资源吗？此操作不可恢复',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await resourceApi.deleteResource(resourceId.value);
      
      if (res.code === 200) {
        ElMessage.success('资源删除成功');
        router.push('/resources');
      }
    } catch (error) {
      console.error('删除资源失败:', error);
      ElMessage.error('删除资源失败');
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
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

// 格式化文件类型
const formatFileType = (fileType: string) => {
  const typeMap: Record<string, string> = {
    'application/pdf': 'PDF文档',
    'application/msword': 'Word文档',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word文档',
    'application/vnd.ms-excel': 'Excel表格',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel表格',
    'application/vnd.ms-powerpoint': 'PowerPoint演示文稿',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'PowerPoint演示文稿',
    'application/zip': 'ZIP压缩文件',
    'application/x-rar-compressed': 'RAR压缩文件',
    'image/jpeg': 'JPEG图片',
    'image/png': 'PNG图片',
    'image/gif': 'GIF图片',
    'text/plain': '文本文件'
  };
  
  return typeMap[fileType] || fileType;
};
</script>

<style scoped>
.resource-detail-container {
  padding: 20px;
}

.resource-detail-card {
  border-radius: 8px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.resource-title-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-title {
  font-size: 24px;
  margin: 0;
}

.resource-actions {
  display: flex;
  gap: 10px;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.resource-uploader {
  display: flex;
  align-items: center;
  gap: 15px;
}

.uploader-info {
  display: flex;
  flex-direction: column;
}

.uploader-name {
  font-weight: 500;
  font-size: 16px;
}

.upload-time {
  color: #999;
  font-size: 14px;
  margin-top: 5px;
}

.resource-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
}

.resource-description {
  margin: 20px 0;
}

.description-content {
  white-space: pre-line;
  line-height: 1.6;
  color: #333;
}

.resource-preview {
  margin: 20px 0;
}

.preview-container {
  margin-top: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.preview-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
}

.no-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
