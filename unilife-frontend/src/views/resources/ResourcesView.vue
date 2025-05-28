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
                <span class="resource-count">{{ category.count }}</span>
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
                @keyup.enter="searchResources()"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #append>
                  <el-button @click="searchResources()">搜索</el-button>
                </template>
              </el-input>
            </div>
            
            <div class="filter-options">
              <el-select v-model="sortBy" placeholder="排序方式" size="default">
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

          <!-- 资源列表 -->
          <div class="resources-list">
            <div 
              v-for="resource in mockResources" 
              :key="resource.id"
              class="resource-card card-light animate-fade-in-up"
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
                    <span class="upload-time">{{ resource.createdAt }}</span>
                  </div>
                </div>
                
                <div class="resource-actions">
                  <el-button type="primary" @click="downloadResource(resource)">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                  <el-button text @click="toggleLike(resource)">
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
            <div v-if="mockResources.length === 0" class="empty-state">
              <el-empty description="暂无资源">
                <el-button type="primary" @click="showUploadDialog = true">
                  上传第一个资源
                </el-button>
              </el-empty>
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
      <el-form label-position="top">
        <el-form-item label="资源标题">
          <el-input placeholder="请输入资源标题" size="large" />
        </el-form-item>
        
        <el-form-item label="资源分类">
          <el-select placeholder="请选择分类" size="large" style="width: 100%">
            <el-option label="学习资料" value="1" />
            <el-option label="课件PPT" value="2" />
            <el-option label="实验报告" value="3" />
            <el-option label="其他资源" value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="资源描述">
          <el-input type="textarea" :rows="3" placeholder="请输入资源描述..." />
        </el-form-item>
        
        <el-form-item label="上传文件">
          <el-upload
            class="upload-area"
            drag
            action="#"
            :auto-upload="false"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.zip,.rar"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
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
        <el-button type="primary" @click="handleUploadResource">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
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

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const showUploadDialog = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const selectedFileType = ref<string | null>(null)
const sortBy = ref('latest')

// 资源分类
const categories = ref([
  { id: 1, name: '学习资料', count: 156 },
  { id: 2, name: '课件PPT', count: 89 },
  { id: 3, name: '实验报告', count: 67 },
  { id: 4, name: '考试资料', count: 134 },
  { id: 5, name: '其他资源', count: 45 }
])

// 文件类型
const fileTypes = ref([
  { name: 'pdf', label: 'PDF' },
  { name: 'doc', label: 'Word' },
  { name: 'ppt', label: 'PPT' },
  { name: 'excel', label: 'Excel' },
  { name: 'zip', label: '压缩包' }
])

// 模拟资源数据
const mockResources = ref([
  {
    id: 1,
    title: '高等数学期末复习资料',
    description: '包含重点知识点总结、历年真题和详细解答，适合期末复习使用',
    fileType: 'application/pdf',
    fileSize: 2048000,
    nickname: '数学小王子',
    avatar: '',
    downloadCount: 234,
    likeCount: 89,
    isLiked: false,
    createdAt: '2024-01-15 14:30'
  },
  {
    id: 2,
    title: '数据结构课件完整版',
    description: '完整的数据结构课程PPT，包含所有章节内容和代码示例',
    fileType: 'application/vnd.ms-powerpoint',
    fileSize: 15360000,
    nickname: '编程达人',
    avatar: '',
    downloadCount: 156,
    likeCount: 67,
    isLiked: false,
    createdAt: '2024-01-14 10:20'
  },
  {
    id: 3,
    title: '计算机网络实验报告模板',
    description: '标准的实验报告格式，包含实验目的、步骤、结果分析等部分',
    fileType: 'application/msword',
    fileSize: 512000,
    nickname: '实验小助手',
    avatar: '',
    downloadCount: 98,
    likeCount: 34,
    isLiked: false,
    createdAt: '2024-01-13 16:45'
  }
])

// 方法
const selectCategory = (categoryId: number | null) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
}

const selectFileType = (fileType: string | null) => {
  selectedFileType.value = selectedFileType.value === fileType ? null : fileType
}

const searchResources = () => {
  ElMessage.info('搜索功能开发中...')
}

const refreshResources = () => {
  ElMessage.success('刷新成功')
}

const downloadResource = (resource: any) => {
  ElMessage.success(`开始下载：${resource.title}`)
}

const toggleLike = (resource: any) => {
  resource.isLiked = !resource.isLiked
  resource.likeCount += resource.isLiked ? 1 : -1
  ElMessage.success(resource.isLiked ? '点赞成功' : '取消点赞')
}

const handleUploadResource = () => {
  showUploadDialog.value = false
  ElMessage.success('资源上传成功！')
}

const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getFileTypeLabel = (mimeType: string) => {
  const typeMap: { [key: string]: string } = {
    'application/pdf': 'PDF',
    'application/msword': 'Word',
    'application/vnd.ms-powerpoint': 'PPT',
    'application/vnd.ms-excel': 'Excel',
    'application/zip': 'ZIP'
  }
  return typeMap[mimeType] || '文件'
}

const getFileTypeColor = (mimeType: string) => {
  const colorMap: { [key: string]: string } = {
    'application/pdf': '#ff4757',
    'application/msword': '#2e86de',
    'application/vnd.ms-powerpoint': '#ff6348',
    'application/vnd.ms-excel': '#2ed573',
    'application/zip': '#a4b0be'
  }
  return colorMap[mimeType] || '#6c5ce7'
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

onMounted(() => {
  console.log('资源页面加载完成')
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