<template>
  <div class="my-resources-container">
    <div class="page-header">
      <h1 class="page-title">我的资源</h1>
      <el-button type="primary" @click="handleUpload">
        <el-icon><Upload /></el-icon>上传资源
      </el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="我上传的资源" name="uploaded">
        <el-table
          v-loading="loading"
          :data="resources"
          style="width: 100%"
          empty-text="暂无资源"
        >
          <el-table-column prop="title" label="资源标题" min-width="200">
            <template #default="scope">
              <div class="resource-title-cell">
                <el-icon v-if="getFileIcon(scope.row.fileType)" :size="20">
                  <component :is="getFileIcon(scope.row.fileType)" />
                </el-icon>
                <router-link :to="`/resource/${scope.row.id}`" class="resource-link">
                  {{ scope.row.title }}
                </router-link>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="fileSize" label="大小" width="120">
            <template #default="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="downloadCount" label="下载次数" width="100" />
          <el-table-column prop="likeCount" label="点赞数" width="100" />
          <el-table-column prop="createdAt" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" @click="handleDownload(scope.row.id)">下载</el-button>
              <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

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
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import { resourceApi } from '@/api';
import { Upload, Document, Picture, Files, Folder, Grid, Reading, Promotion } from '@element-plus/icons-vue';

const router = useRouter();

// 资源列表数据
const resources = ref<any[]>([]);
const categories = ref<any[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const activeTab = ref('uploaded');

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

// 编辑资源相关
const editDialogVisible = ref(false);
const editFormRef = ref<FormInstance>();
const submitting = ref(false);
const currentEditId = ref<number | null>(null);
const editForm = reactive({
  title: '',
  description: '',
  categoryId: undefined as number | undefined
});

const editRules = {
  title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
};

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchMyResources(),
    fetchCategories()
  ]);
});

// 获取我的资源列表
const fetchMyResources = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };

    const res = await resourceApi.getMyResources(params);
    if (res.code === 200) {
      resources.value = res.data.list;
      total.value = res.data.total;
    }
  } catch (error) {
    console.error('获取我的资源列表失败:', error);
    ElMessage.error('获取我的资源列表失败');
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

// 处理标签页切换
const handleTabChange = () => {
  fetchMyResources();
};

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchMyResources();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchMyResources();
};

// 处理上传资源
const handleUpload = () => {
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
          fetchMyResources();
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

// 处理编辑
const handleEdit = (resource: any) => {
  currentEditId.value = resource.id;
  editForm.title = resource.title;
  editForm.description = resource.description || '';
  editForm.categoryId = resource.categoryId;
  editDialogVisible.value = true;
};

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value || !currentEditId.value) return;

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;

      try {
        const res = await resourceApi.updateResource(currentEditId.value, editForm);

        if (res.code === 200) {
          ElMessage.success('资源信息更新成功');
          editDialogVisible.value = false;
          fetchMyResources();
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

// 处理下载
const handleDownload = async (id: number) => {
  try {
    const res = await resourceApi.downloadResource(id);
    if (res.code === 200) {
      // 创建下载链接
      const link = document.createElement('a');
      link.href = res.data.fileUrl;
      link.download = res.data.fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // 刷新资源列表，更新下载次数
      fetchMyResources();
    }
  } catch (error) {
    console.error('下载资源失败:', error);
    ElMessage.error('下载资源失败');
  }
};

// 处理删除
const handleDelete = (id: number) => {
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
      const res = await resourceApi.deleteResource(id);

      if (res.code === 200) {
        ElMessage.success('资源删除成功');
        fetchMyResources();
      }
    } catch (error) {
      console.error('删除资源失败:', error);
      ElMessage.error('删除资源失败');
    }
  }).catch(() => {
    // 用户取消删除
  });
};

// 获取文件图标
const getFileIcon = (fileType: string) => {
  if (fileType.includes('pdf')) {
    return Document;
  } else if (fileType.includes('image')) {
    return Picture;
  } else if (fileType.includes('word')) {
    return Reading;
  } else if (fileType.includes('excel')) {
    return Grid;
  } else if (fileType.includes('powerpoint')) {
    return Promotion;
  } else if (fileType.includes('zip') || fileType.includes('rar')) {
    return Folder;
  } else {
    return Files;
  }
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
</script>

<style scoped>
.my-resources-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.resource-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resource-link {
  color: var(--el-color-primary);
  text-decoration: none;
}

.resource-link:hover {
  text-decoration: underline;
}

.pagination-container {
  margin-top: 20px;
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
