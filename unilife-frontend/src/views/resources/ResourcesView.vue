<template>
  <div class="resources-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar glass-light">
      <div class="nav-container">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">
            <div class="logo-circle">
              <i class="el-icon-star-filled"></i>
            </div>
            <span class="brand-name gradient-text">UniLife</span>
          </router-link>
        </div>
        
        <div class="nav-menu">
          <router-link to="/forum" class="nav-item">论坛</router-link>
          <router-link to="/resources" class="nav-item active">资源</router-link>
          <router-link to="/schedule" class="nav-item">课程表</router-link>
          <router-link to="/tasks" class="nav-item">日程管理</router-link>
        </div>
        
        <div class="nav-actions">
          <div class="user-info">
            <el-avatar :size="36" :src="userStore.user?.avatar">
              {{ userStore.user?.nickname?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.user?.nickname }}</span>
          </div>
          <el-dropdown @command="handleCommand">
            <el-button circle>
              <el-icon><Setting /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="resources-main">
      <div class="resources-content">
        <!-- 侧边栏 -->
        <aside class="resources-sidebar">
          <!-- 上传按钮 -->
          <div class="upload-section">
            <el-button 
              type="primary" 
              size="large" 
              class="upload-btn animate-glow"
              @click="showUploadDialog = true"
            >
              <el-icon><Upload /></el-icon>
              上传资源
            </el-button>
          </div>

          <!-- 资源分类 -->
          <div class="categories-section card-light">
            <h3 class="section-title">资源分类</h3>
            <div class="categories-list">
              <div 
                class="category-item"
                :class="{ active: selectedCategory === null }"
                @click="selectCategory(null)"
              >
                <el-icon class="category-icon">
                  <Folder />
                </el-icon>
                <span class="category-name">全部资源</span>
                <span class="resource-count">{{ totalResources }}</span>
              </div>
              <div 
                v-for="category in categories" 
                :key="category.id"
                class="category-item"
                :class="{ active: selectedCategory === category.id }"
                @click="selectCategory(category.id)"
              >
                <el-icon class="category-icon">
                  <Folder />
                </el-icon>
                <span class="category-name">{{ category.name }}</span>
                <span class="resource-count">{{ category.count || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 文件类型筛选 -->
          <div class="file-types-section card-light">
            <h3 class="section-title">文件类型</h3>
            <div class="file-types-list">
              <el-tag 
                v-for="type in fileTypes" 
                :key="type.name"
                class="file-type-tag"
                :class="{ active: selectedFileType === type.name }"
                @click="selectFileType(type.name)"
              >
                <el-icon>
                  <Document />
                </el-icon>
                {{ type.label }}
              </el-tag>
            </div>
          </div>
        </aside>

        <!-- 资源列表区域 -->
        <main class="resources-area">
          <!-- 搜索和筛选栏 -->
          <div class="search-section card-light">
            <div class="search-bar">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索资源标题或描述..."
                size="large"
                class="search-input"
                @keyup.enter="loadResources"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #append>
                  <el-button @click="loadResources">搜索</el-button>
                </template>
              </el-input>
            </div>
            
            <div class="filter-options">
              <el-select v-model="sortBy" placeholder="排序方式" size="default" @change="loadResources">
                <el-option label="最新上传" value="latest" />
                <el-option label="下载最多" value="downloads" />
                <el-option label="点赞最多" value="likes" />
                <el-option label="文件大小" value="size" />
              </el-select>
              
              <el-button @click="refreshResources" circle>
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-loading="loading" class="loading-container">
          <!-- 资源列表 -->
            <div class="resources-list" v-if="!loading">
            <div 
                v-for="resource in resources" 
              :key="resource.id"
              class="resource-card card-light animate-fade-in-up"
                @click="goToResourceDetail(resource.id)"
            >
              <div class="resource-header">
                <div class="file-icon">
                  <el-icon :size="32" :color="getFileTypeColor(resource.fileType)">
                    <Document />
                  </el-icon>
                </div>
                
                <div class="resource-info">
                  <h3 class="resource-title">{{ resource.title }}</h3>
                  <p class="resource-description">{{ resource.description }}</p>
                  
                  <div class="resource-meta">
                    <span class="file-size">{{ formatFileSize(resource.fileSize) }}</span>
                    <span class="file-type">{{ getFileTypeLabel(resource.fileType) }}</span>
                      <span class="upload-time">{{ formatDate(resource.createdAt) }}</span>
                  </div>
                </div>
                
                  <div class="resource-actions" @click.stop>
                    <el-button type="primary" @click="downloadResource(resource)" :loading="resource.downloading">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                    <el-button 
                      :text="!resource.isLiked" 
                      :type="resource.isLiked ? 'danger' : 'default'"
                      @click="toggleLike(resource)"
                      :loading="resource.liking"
                    >
                    <el-icon><Star /></el-icon>
                    {{ resource.likeCount }}
                  </el-button>
                </div>
              </div>

              <div class="resource-footer">
                <div class="uploader-info">
                  <el-avatar :size="24" :src="resource.avatar">
                    {{ resource.nickname?.charAt(0) }}
                  </el-avatar>
                  <span class="uploader-name">{{ resource.nickname }}</span>
                </div>
                
                <div class="resource-stats">
                  <span class="stat-item">
                    <el-icon><Download /></el-icon>
                    {{ resource.downloadCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ resource.likeCount }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
              <div v-if="resources.length === 0 && !loading" class="empty-state">
              <el-empty description="暂无资源">
                <el-button type="primary" @click="showUploadDialog = true">
                  上传第一个资源
                </el-button>
              </el-empty>
              </div>
            </div>

            <!-- 分页 -->
            <div v-if="totalPages > 1" class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="totalResources"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadResources"
                @current-change="loadResources"
              />
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 上传资源对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传资源"
      width="600px"
      class="upload-dialog"
    >
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-position="top">
        <el-form-item label="资源标题" prop="title">
          <el-input 
            v-model="uploadForm.title" 
            placeholder="请输入资源标题" 
            size="large" 
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="资源分类" prop="categoryId">
          <el-select 
            v-model="uploadForm.categoryId" 
            placeholder="请选择分类" 
            size="large" 
            style="width: 100%"
          >
            <el-option 
              v-for="category in categories" 
              :key="category.id"
              :label="category.name" 
              :value="category.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="资源描述" prop="description">
          <el-input 
            v-model="uploadForm.description"
            type="textarea" 
            :rows="3" 
            placeholder="请输入资源描述..." 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="uploadRef"
            class="upload-area"
            drag
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.zip,.rar"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击选择文件</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、PPT、Excel、压缩包等格式，文件大小不超过 50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleUploadResource"
          :loading="uploading"
        >
          {{ uploading ? '上传中...' : '上传' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadInstance, UploadProps } from 'element-plus'
import { 
  Upload, 
  Search, 
  Refresh, 
  Download, 
  Star, 
  Setting,
  Folder,
  Document,
  UploadFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { 
  getResources, 
  uploadResource, 
  downloadResource as downloadResourceAPI,
  likeResource,
  type Resource as BaseResource
} from '@/api/resources'
import { getCategories } from '@/api/forum'
import type { ApiResponse, Category } from '@/types'

// 扩展Resource接口，添加UI状态
interface ExtendedResource extends BaseResource {
  downloading?: boolean
  liking?: boolean
}

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const uploading = ref(false)
const showUploadDialog = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const selectedFileType = ref<string | null>(null)
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(10)
const totalResources = ref(0)
const totalPages = ref(0)

// 数据列表
const resources = ref<ExtendedResource[]>([])
const categories = ref<any[]>([])

// 上传表单
const uploadFormRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const uploadForm = reactive({
  title: '',
  description: '',
  categoryId: null as number | null,
  file: null as File | null
})

// 表单验证规则
const uploadRules: FormRules = {
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择资源分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入资源描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  file: [
    { required: true, message: '请选择要上传的文件', trigger: 'change' }
  ]
}

// 文件类型
const fileTypes = ref([
  { name: 'pdf', label: 'PDF' },
  { name: 'doc', label: 'Word' },
  { name: 'ppt', label: 'PPT' },
  { name: 'excel', label: 'Excel' },
  { name: 'zip', label: '压缩包' }
])

// 方法
const selectCategory = (categoryId: number | null) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  currentPage.value = 1
  loadResources()
}

const selectFileType = (fileType: string | null) => {
  selectedFileType.value = selectedFileType.value === fileType ? null : fileType
  currentPage.value = 1
  loadResources()
}

const refreshResources = () => {
  currentPage.value = 1
  loadResources()
  ElMessage.success('刷新成功')
}

// 跳转到资源详情页面
const goToResourceDetail = (resourceId: number) => {
  router.push(`/resources/${resourceId}`)
}

// 加载资源列表
const loadResources = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      category: selectedCategory.value || undefined,
      keyword: searchKeyword.value || undefined
    }
    
    console.log('正在请求资源列表，参数:', params)
    console.log('请求URL: /resources')
    
    const response = await getResources(params) as any as ApiResponse<{
      total: number
      list: BaseResource[]
      pages: number
    }>
    
    console.log('资源列表API响应:', response)
    
    if (response.code === 200) {
      resources.value = response.data.list.map(item => ({
        ...item,
        downloading: false,
        liking: false
      }))
      totalResources.value = response.data.total
      totalPages.value = response.data.pages
      
      console.log('资源列表加载成功:', {
        total: totalResources.value,
        pages: totalPages.value,
        list: resources.value
      })
    } else {
      console.error('资源列表API返回错误:', response)
      ElMessage.error(response.message || '获取资源列表失败')
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    console.log('正在请求分类列表...')
    const response = await getCategories() as any as ApiResponse<{
      total: number
      list: Category[]
    }>
    console.log('分类列表API响应:', response)
    console.log(response.code)
    
    if (response.code === 200) {
      categories.value = response.data.list
      console.log('分类列表加载成功:', categories.value)
    } else {
      console.error('分类列表API返回错误:', response)
      ElMessage.error(response.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 下载资源
const downloadResource = async (resource: ExtendedResource) => {
  try {
    resource.downloading = true
    const response = await downloadResourceAPI(resource.id) as any as ApiResponse<{
      fileUrl: string
      fileName: string
      fileType: string
    }>
    
    console.log('下载资源API响应:', response)
    
    if (response.code === 200) {
      const downloadUrl = response.data.fileUrl
      // 创建下载链接
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = response.data.fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      // 更新下载次数
      resource.downloadCount++
      ElMessage.success('下载开始')
    } else {
      ElMessage.error(response.message || '下载失败')
}
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  } finally {
    resource.downloading = false
  }
}

// 点赞/取消点赞
const toggleLike = async (resource: ExtendedResource) => {
  try {
    resource.liking = true
    const response = await likeResource(resource.id) as any as ApiResponse<null>
    
    console.log('点赞API响应:', response)
    
    if (response.code === 200) {
  resource.isLiked = !resource.isLiked
  resource.likeCount += resource.isLiked ? 1 : -1
  ElMessage.success(resource.isLiked ? '点赞成功' : '取消点赞')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    resource.liking = false
  }
}

// 文件选择处理
const handleFileChange: UploadProps['onChange'] = (uploadFile) => {
  if (uploadFile.raw) {
    // 检查文件大小（50MB限制）
    if (uploadFile.raw.size > 50 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过 50MB')
      uploadRef.value?.clearFiles()
      return
    }
    uploadForm.file = uploadFile.raw
  }
}

// 文件移除处理
const handleFileRemove = () => {
  uploadForm.file = null
}

// 上传资源
const handleUploadResource = async () => {
  if (!uploadFormRef.value) return
  
  try {
    await uploadFormRef.value.validate()
    
    if (!uploadForm.file) {
      ElMessage.error('请选择要上传的文件')
      return
    }
    
    uploading.value = true
    
    const response = await uploadResource({
      file: uploadForm.file,
      title: uploadForm.title,
      description: uploadForm.description,
      categoryId: uploadForm.categoryId!
    }) as any as ApiResponse<{ resourceId: number }>
    
    console.log('上传资源API响应:', response)
    
    if (response.code === 200) {
  ElMessage.success('资源上传成功！')
      showUploadDialog.value = false
      resetUploadForm()
      loadResources() // 重新加载资源列表
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 重置上传表单
const resetUploadForm = () => {
  uploadForm.title = ''
  uploadForm.description = ''
  uploadForm.categoryId = null
  uploadForm.file = null
  uploadRef.value?.clearFiles()
  uploadFormRef.value?.clearValidate()
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 获取文件类型标签
const getFileTypeLabel = (mimeType: string) => {
  const typeMap: { [key: string]: string } = {
    'application/pdf': 'PDF',
    'application/msword': 'Word',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word',
    'application/vnd.ms-powerpoint': 'PPT',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'PPT',
    'application/vnd.ms-excel': 'Excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel',
    'application/zip': 'ZIP',
    'application/x-rar-compressed': 'RAR'
  }
  return typeMap[mimeType] || '文件'
}

// 获取文件类型颜色
const getFileTypeColor = (mimeType: string) => {
  const colorMap: { [key: string]: string } = {
    'application/pdf': '#ff4757',
    'application/msword': '#2e86de',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': '#2e86de',
    'application/vnd.ms-powerpoint': '#ff6348',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': '#ff6348',
    'application/vnd.ms-excel': '#2ed573',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': '#2ed573',
    'application/zip': '#a4b0be',
    'application/x-rar-compressed': '#a4b0be'
  }
  return colorMap[mimeType] || '#6c5ce7'
}

// 用户操作处理
const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 监听对话框关闭，重置表单
watch(showUploadDialog, (newVal) => {
  if (!newVal) {
    resetUploadForm()
  }
})

// 页面加载时获取数据
onMounted(async () => {
  console.log('资源页面加载完成')
  await loadCategories()
  await loadResources()
})
</script>

<style scoped>
.resources-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 导航栏样式 - 复用之前的样式 */
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 16px 0;
  border-bottom: 1px solid var(--gray-200);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo-circle {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: var(--shadow-light);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-item {
  text-decoration: none;
  color: var(--gray-600);
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 12px;
  transition: var(--transition-base);
}

.nav-item:hover,
.nav-item.active {
  color: var(--primary-600);
  background: var(--primary-50);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: var(--gray-700);
}

/* 主要内容区域 */
.resources-main {
  padding: 32px 24px;
}

.resources-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 32px;
}

/* 侧边栏样式 */
.resources-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.upload-section {
  position: sticky;
  top: 100px;
}

.upload-btn {
  width: 100%;
  height: 52px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.section-title {
  color: var(--gray-800);
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 16px;
}

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: var(--transition-base);
}

.category-item:hover {
  background: var(--primary-50);
}

.category-item.active {
  background: var(--primary-100);
  color: var(--primary-700);
}

.category-icon {
  color: var(--primary-500);
}

.category-name {
  flex: 1;
  font-weight: 500;
}

.resource-count {
  font-size: 12px;
  color: var(--gray-500);
  background: var(--gray-100);
  padding: 2px 8px;
  border-radius: 12px;
}

.file-types-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.file-type-tag {
  cursor: pointer;
  transition: var(--transition-base);
  display: flex;
  align-items: center;
  gap: 4px;
}

.file-type-tag:hover {
  transform: translateY(-1px);
}

.file-type-tag.active {
  background: var(--primary-100);
  color: var(--primary-700);
}

/* 资源区域样式 */
.resources-area {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-section {
  padding: 24px;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-bar {
  flex: 1;
}

.filter-options {
  display: flex;
  gap: 12px;
  align-items: center;
}

.resources-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.resource-card {
  padding: 24px;
  transition: var(--transition-base);
  cursor: pointer;
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-purple);
}

.resource-header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.file-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gray-50);
  border-radius: 12px;
}

.resource-info {
  flex: 1;
}

.resource-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.resource-description {
  color: var(--gray-600);
  line-height: 1.6;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.resource-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--gray-500);
}

.resource-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--gray-200);
}

.uploader-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.uploader-name {
  font-size: 14px;
  color: var(--gray-600);
  font-weight: 500;
}

.resource-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--gray-500);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}

/* 表单行样式 */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* 上传对话框样式 */
.upload-dialog :deep(.el-upload-dragger) {
  border: 2px dashed var(--primary-300);
  border-radius: 12px;
  background: var(--primary-50);
  transition: var(--transition-base);
}

.upload-dialog :deep(.el-upload-dragger:hover) {
  border-color: var(--primary-400);
  background: var(--primary-100);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .resources-content {
    grid-template-columns: 1fr;
  }
  
  .resources-sidebar {
    order: 2;
  }
  
  .resources-area {
    order: 1;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .resources-main {
    padding: 16px;
  }
  
  .resources-content {
    gap: 16px;
  }
  
  .search-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-options {
    justify-content: center;
  }
  
  .resource-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .resource-actions {
    flex-direction: row;
    align-items: center;
  }
}
</style> 