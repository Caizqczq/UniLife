<template>
  <div class="resource-detail-container">
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
    <div class="resource-detail-main">
      <div class="resource-detail-content" v-loading="loading">
        <!-- 返回按钮 -->
        <div class="back-section">
          <el-button @click="goBack" type="primary" text>
            <el-icon><ArrowLeft /></el-icon>
            返回资源列表
          </el-button>
        </div>

        <!-- 资源详情卡片 -->
        <div v-if="resource" class="resource-detail-card card-light">
          <!-- 资源头部信息 -->
          <div class="resource-header">
            <div class="file-icon-large">
              <el-icon :size="64" :color="getFileTypeColor(resource.fileType)">
                <Document />
              </el-icon>
            </div>
            
            <div class="resource-main-info">
              <h1 class="resource-title">{{ resource.title }}</h1>
              <div class="resource-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  上传者：{{ resource.nickname }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  上传时间：{{ formatDate(resource.createdAt) }}
                </span>
                <span class="meta-item">
                  <el-icon><Folder /></el-icon>
                  分类：{{ resource.categoryName }}
                </span>
                <span class="meta-item">
                  <el-icon><Document /></el-icon>
                  文件类型：{{ getFileTypeLabel(resource.fileType) }}
                </span>
                <span class="meta-item">
                  <el-icon><Download /></el-icon>
                  文件大小：{{ formatFileSize(resource.fileSize) }}
                </span>
              </div>
            </div>
            
            <div class="resource-actions">
              <el-button type="primary" size="large" @click="downloadResource" :loading="downloading">
                <el-icon><Download /></el-icon>
                下载资源
              </el-button>
              <el-button 
                :type="resource.isLiked ? 'danger' : 'default'" 
                size="large"
                @click="toggleLike"
                :loading="liking"
              >
                <el-icon><Star /></el-icon>
                {{ resource.isLiked ? '已点赞' : '点赞' }}
                ({{ resource.likeCount }})
              </el-button>
            </div>
          </div>

          <!-- 资源描述 -->
          <div class="resource-description">
            <h3>资源描述</h3>
            <p class="description-text">{{ resource.description }}</p>
          </div>

          <!-- 统计信息 -->
          <div class="resource-stats">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><Download /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ resource.downloadCount }}</div>
                <div class="stat-label">下载次数</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ resource.likeCount }}</div>
                <div class="stat-label">点赞数量</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ resource.downloadCount * 2 + resource.likeCount }}</div>
                <div class="stat-label">浏览次数</div>
              </div>
            </div>
          </div>

          <!-- 上传者信息 -->
          <div class="uploader-info-card">
            <h3>上传者信息</h3>
            <div class="uploader-profile">
              <el-avatar :size="48" :src="resource.avatar">
                {{ resource.nickname?.charAt(0) }}
              </el-avatar>
              <div class="uploader-details">
                <div class="uploader-name">{{ resource.nickname }}</div>
                <div class="uploader-id">用户ID: {{ resource.userId }}</div>
                <div class="uploader-upload-time">上传于 {{ formatDate(resource.createdAt) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="资源不存在或已被删除">
            <el-button type="primary" @click="goBack">
              返回资源列表
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, 
  Document, 
  Download, 
  Star, 
  Setting,
  User,
  Calendar,
  Folder,
  View
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { 
  getResourceDetail, 
  downloadResource as downloadResourceAPI,
  likeResource,
  type Resource as BaseResource
} from '@/api/resources'
import type { ApiResponse } from '@/types'

// 扩展Resource接口，添加UI状态
interface ExtendedResource extends BaseResource {
  downloading?: boolean
  liking?: boolean
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const downloading = ref(false)
const liking = ref(false)
const resource = ref<ExtendedResource | null>(null)

// 方法
const goBack = () => {
  router.push('/resources')
}

const loadResourceDetail = async () => {
  try {
    loading.value = true
    const resourceId = Number(route.params.id)
    
    if (!resourceId) {
      ElMessage.error('资源ID无效')
      goBack()
      return
    }
    
    const response = await getResourceDetail(resourceId) as any as ApiResponse<BaseResource>
    
    if (response.code === 200) {
      resource.value = {
        ...response.data,
        downloading: false,
        liking: false
      }
    } else {
      ElMessage.error(response.message || '获取资源详情失败')
      goBack()
    }
  } catch (error) {
    console.error('获取资源详情失败:', error)
    ElMessage.error('获取资源详情失败')
    goBack()
  } finally {
    loading.value = false
  }
}

const downloadResource = async () => {
  if (!resource.value) return
  
  try {
    downloading.value = true
    const response = await downloadResourceAPI(resource.value.id) as any as ApiResponse<{
      fileUrl: string
      fileName: string
      fileType: string
    }>
    
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
      resource.value.downloadCount++
      ElMessage.success('下载开始')
    } else {
      ElMessage.error(response.message || '下载失败')
    }
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败')
  } finally {
    downloading.value = false
  }
}

const toggleLike = async () => {
  if (!resource.value) return
  
  try {
    liking.value = true
    const response = await likeResource(resource.value.id) as any as ApiResponse<null>
    
    if (response.code === 200) {
      resource.value.isLiked = !resource.value.isLiked
      resource.value.likeCount += resource.value.isLiked ? 1 : -1
      ElMessage.success(resource.value.isLiked ? '点赞成功' : '取消点赞')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    liking.value = false
  }
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

// 页面加载时获取数据
onMounted(() => {
  loadResourceDetail()
})
</script>

<style scoped>
.resource-detail-container {
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
.resource-detail-main {
  padding: 32px 24px;
}

.resource-detail-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.back-section {
  display: flex;
  align-items: center;
}

.resource-detail-card {
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.resource-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--gray-200);
}

.file-icon-large {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gray-50);
  border-radius: 16px;
}

.resource-main-info {
  flex: 1;
}

.resource-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.resource-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--gray-600);
}

.resource-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.resource-description {
  padding-bottom: 24px;
  border-bottom: 1px solid var(--gray-200);
}

.resource-description h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--gray-800);
  margin: 0 0 16px 0;
}

.description-text {
  font-size: 16px;
  line-height: 1.6;
  color: var(--gray-700);
  margin: 0;
}

.resource-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--gray-200);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--gray-50);
  border-radius: 12px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-100);
  color: var(--primary-600);
  border-radius: 10px;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: var(--gray-800);
}

.stat-label {
  font-size: 12px;
  color: var(--gray-500);
}

.uploader-info-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--gray-800);
  margin: 0 0 16px 0;
}

.uploader-profile {
  display: flex;
  align-items: center;
  gap: 16px;
}

.uploader-details {
  flex: 1;
}

.uploader-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: 4px;
}

.uploader-id,
.uploader-upload-time {
  font-size: 14px;
  color: var(--gray-500);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .resource-detail-main {
    padding: 16px;
  }
  
  .resource-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .resource-actions {
    flex-direction: row;
    align-items: center;
    width: 100%;
  }
  
  .resource-actions button {
    flex: 1;
  }
  
  .resource-stats {
    grid-template-columns: 1fr;
  }
}
</style> 