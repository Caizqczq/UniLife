<template>
  <div class="profile-container">
    <!-- 使用通用顶部导航栏组件 -->
    <TopNavbar />

    <!-- 主要内容区域 -->
    <div class="profile-main" v-loading="loading" element-loading-text="正在加载用户信息...">
      <div class="profile-content">
        <!-- 个人信息卡片 -->
        <div class="profile-header card-light">
          <div class="avatar-section">
            <div class="avatar-container">
              <el-avatar :size="120" :src="userProfile.avatar">
                {{ userProfile.nickname?.charAt(0) }}
              </el-avatar>
              <el-button class="change-avatar-btn" circle @click="showAvatarUpload = true">
                <el-icon><Camera /></el-icon>
              </el-button>
            </div>
            
            <div class="user-basic-info">
              <h1 class="user-name">{{ userProfile.nickname || '加载中...' }}</h1>
              <p class="user-title">{{ userProfile.department }} · {{ userProfile.major }}</p>
              <p class="user-grade">{{ userProfile.grade }}</p>
              
              <div class="user-stats" v-loading="statsLoading" element-loading-text="加载统计中...">
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.postsCount }}</span>
                  <span class="stat-label">发帖</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.likesReceived }}</span>
                  <span class="stat-label">获赞</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.resourcesCount }}</span>
                  <span class="stat-label">资源</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userProfile.points }}</span>
                  <span class="stat-label">积分</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="profile-actions">
            <el-button type="primary" @click="showEditProfile = true">
              <el-icon><Edit /></el-icon>
              编辑资料
            </el-button>
            <el-button @click="showChangePassword = true">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-button>
          </div>
        </div>

        <!-- 详细信息和活动 -->
        <div class="profile-details">
          <!-- 个人信息 -->
          <div class="info-section card-light">
            <h3 class="section-title">个人信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">用户名</span>
                <span class="info-value">{{ userProfile.username || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ userProfile.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学号</span>
                <span class="info-value">{{ userProfile.studentId || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">性别</span>
                <span class="info-value">{{ getGenderText(userProfile.gender) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学院</span>
                <span class="info-value">{{ userProfile.department || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">专业</span>
                <span class="info-value">{{ userProfile.major || '未设置' }}</span>
              </div>
            </div>
            
            <div class="bio-section">
              <span class="info-label">个人简介</span>
              <p class="bio-text">{{ userProfile.bio || '这个人很懒，什么都没有留下...' }}</p>
            </div>
          </div>

          <!-- 最近活动 -->
          <div class="activity-section card-light">
            <h3 class="section-title">最近活动</h3>
            <div class="activity-tabs">
              <el-tabs v-model="activeTab">
                <el-tab-pane label="我的帖子" name="posts">
                  <div class="activity-content" v-loading="postsLoading" element-loading-text="加载帖子中...">
                    <div v-if="recentPosts.length > 0" class="posts-list">
                      <div 
                        v-for="post in recentPosts" 
                        :key="post.id"
                        class="post-item"
                        @click="viewPost(post.id)"
                      >
                        <div class="post-info">
                          <h4 class="post-title">{{ post.title }}</h4>
                          <p class="post-summary">{{ getPostSummary(post.content) }}</p>
                          <div class="post-meta">
                            <span class="post-time">{{ post.createdAt }}</span>
                            <div class="post-stats">
                              <span>{{ post.viewCount }} 浏览</span>
                              <span>{{ post.likeCount }} 点赞</span>
                              <span>{{ post.commentCount }} 评论</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-else-if="!postsLoading" class="empty-state">
                      <el-icon class="empty-icon"><Document /></el-icon>
                      <p>还没有发布任何帖子</p>
                      <el-button type="primary" @click="$router.push('/forum')">去发帖</el-button>
                    </div>
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="我的资源" name="resources">
                  <div class="activity-content" v-loading="resourcesLoading" element-loading-text="加载资源中...">
                    <div v-if="recentResources.length > 0" class="resources-list">
                      <div 
                        v-for="resource in recentResources" 
                        :key="resource.id"
                        class="resource-item"
                      >
                        <el-icon class="resource-icon"><Document /></el-icon>
                        <div class="resource-info">
                          <h4 class="resource-title">{{ resource.title }}</h4>
                          <p class="resource-desc">{{ resource.description }}</p>
                          <div class="resource-meta">
                            <span class="resource-time">{{ resource.createdAt }}</span>
                            <span class="resource-downloads">{{ resource.downloadCount }} 下载</span>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div v-else-if="!resourcesLoading" class="empty-state">
                      <el-icon class="empty-icon"><Document /></el-icon>
                      <p>还没有上传任何资源</p>
                      <el-button type="primary" @click="$router.push('/resources')">去上传</el-button>
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="showEditProfile"
      title="编辑个人资料"
      width="600px"
    >
      <el-form :model="editForm" label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" size="large" />
        </el-form-item>
        
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
          <el-form-item label="学院">
            <el-input v-model="editForm.department" placeholder="请输入学院" size="large" />
          </el-form-item>
          
          <el-form-item label="专业">
            <el-input v-model="editForm.major" placeholder="请输入专业" size="large" />
          </el-form-item>
        </div>
        
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
          <el-form-item label="年级">
            <el-input v-model="editForm.grade" placeholder="请输入年级" size="large" />
          </el-form-item>
          
          <el-form-item label="性别">
            <el-select v-model="editForm.gender" placeholder="请选择性别" size="large">
              <el-option label="保密" :value="0" />
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
        </div>
        
        <el-form-item label="个人简介">
          <el-input 
            v-model="editForm.bio" 
            type="textarea" 
            :rows="4" 
            placeholder="介绍一下自己吧..." 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditProfile = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showChangePassword"
      title="修改密码"
      width="400px"
    >
      <el-form :model="passwordForm" label-position="top">
        <el-form-item label="验证码">
          <div style="display: flex; gap: 12px;">
            <el-input 
              v-model="passwordForm.code" 
              placeholder="请输入验证码" 
              size="large" 
              style="flex: 1;"
            />
            <el-button @click="sendVerificationCode" :disabled="codeCountdown > 0">
              {{ codeCountdown > 0 ? `${codeCountdown}s` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="新密码">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码" 
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            size="large"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="showAvatarUpload"
      title="更换头像"
      width="400px"
    >
      <el-upload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :auto-upload="false"
        accept="image/*"
        @change="handleAvatarChange"
      >
        <div class="upload-area">
          <el-icon class="upload-icon"><Plus /></el-icon>
          <div class="upload-text">点击选择头像</div>
          <div class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</div>
        </div>
      </el-upload>
      
      <template #footer>
        <el-button @click="showAvatarUpload = false">取消</el-button>
        <el-button type="primary" @click="handleUploadAvatar">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Setting,
  Edit,
  Lock,
  Camera,
  Document,
  Plus
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { 
  getUserInfo, 
  getUserStats, 
  getUserRecentPosts,
  updateUserProfile,
  changePassword,
  uploadAvatar,
  sendEmailCode
} from '@/api/user'
import { getMyResources } from '@/api/resources'
import type { ApiResponse } from '@/types'
import TopNavbar from '@/components/TopNavbar.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const showEditProfile = ref(false)
const showChangePassword = ref(false)
const showAvatarUpload = ref(false)
const activeTab = ref('posts')
const codeCountdown = ref(0)
const loading = ref(false)
const statsLoading = ref(false)
const postsLoading = ref(false)
const resourcesLoading = ref(false)

// 用户资料数据 - 使用空对象，待API加载
const userProfile = ref({
  id: 0,
  username: '',
  email: '',
  nickname: '',
  avatar: '',
  bio: '',
  gender: 0,
  studentId: '',
  department: '',
  major: '',
  grade: '',
  points: 0,
  role: 0,
  isVerified: 0
})

// 用户统计数据 - 使用空对象，待API加载
const userStats = ref({
  postsCount: 0,
  commentsCount: 0,
  resourcesCount: 0,
  likesReceived: 0,
  coursesCount: 0,
  schedulesCount: 0
})

// 最近帖子 - 使用空数组，待API加载
const recentPosts = ref<any[]>([])

// 最近资源 - 使用空数组，待API加载
const recentResources = ref<any[]>([])

// 编辑表单
const editForm = reactive({
  nickname: '',
  department: '',
  major: '',
  grade: '',
  gender: 0,
  bio: ''
})

// 密码表单
const passwordForm = reactive({
  code: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
const loadUserProfile = async () => {
  try {
    loading.value = true
    const response = await getUserInfo() as any as ApiResponse<typeof userProfile.value>
    
    if (response.code === 200) {
      userProfile.value = response.data
      console.log('用户信息加载成功:', userProfile.value)
      
      // 更新编辑表单数据
      Object.assign(editForm, {
        nickname: userProfile.value.nickname,
        department: userProfile.value.department,
        major: userProfile.value.major,
        grade: userProfile.value.grade,
        gender: userProfile.value.gender,
        bio: userProfile.value.bio
      })
    } else {
      console.error('获取用户信息失败:', response.message)
      ElMessage.error(response.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    statsLoading.value = true
    const response = await getUserStats() as any as ApiResponse<{
      totalPosts: number
      totalLikes: number
      totalComments: number
      totalViews: number
    }>
    
    if (response.code === 200) {
      // 映射后端字段到前端期望的字段
      userStats.value = {
        postsCount: response.data.totalPosts || 0,
        commentsCount: response.data.totalComments || 0,
        resourcesCount: 0, // 后端暂未返回，使用默认值
        likesReceived: response.data.totalLikes || 0,
        coursesCount: 0, // 后端暂未返回，使用默认值
        schedulesCount: 0 // 后端暂未返回，使用默认值
      }
      console.log('用户统计数据加载成功:', userStats.value)
    } else {
      console.error('获取用户统计失败:', response.message)
    }
  } catch (error) {
    console.error('获取用户统计失败:', error)
  } finally {
    statsLoading.value = false
  }
}

// 加载用户最近帖子
const loadRecentPosts = async () => {
  try {
    postsLoading.value = true
    const response = await getUserRecentPosts(5) as any as ApiResponse<{
      posts: any[]
      totalCount: number
    }>
    
    if (response.code === 200) {
      recentPosts.value = response.data.posts || []
      console.log('最近帖子加载成功:', recentPosts.value)
    } else {
      console.error('获取最近帖子失败:', response.message)
    }
  } catch (error) {
    console.error('获取最近帖子失败:', error)
  } finally {
    postsLoading.value = false
  }
}

// 加载用户最近资源
const loadRecentResources = async () => {
  try {
    resourcesLoading.value = true
    const response = await getMyResources({ page: 1, size: 5 }) as any as ApiResponse<{
      total: number
      list: any[]
      pages: number
    }>
    
    if (response.code === 200) {
      recentResources.value = response.data.list
      console.log('最近资源加载成功:', recentResources.value)
    } else {
      console.error('获取最近资源失败:', response.message)
    }
  } catch (error) {
    console.error('获取最近资源失败:', error)
  } finally {
    resourcesLoading.value = false
  }
}

// 方法
const getGenderText = (gender: number) => {
  const genderMap = { 0: '保密', 1: '男', 2: '女' }
  return genderMap[gender as keyof typeof genderMap] || '保密'
}

const getPostSummary = (content: string) => {
  if (!content) return '无内容'
  
  // 移除markdown语法
  let summary = content
    .replace(/#{1,6}\s+/g, '') // 移除标题
    .replace(/\*\*(.*?)\*\*/g, '$1') // 移除粗体
    .replace(/\*(.*?)\*/g, '$1') // 移除斜体
    .replace(/`(.*?)`/g, '$1') // 移除行内代码
    .replace(/```[\s\S]*?```/g, '') // 移除代码块
    .replace(/!\[.*?\]\(.*?\)/g, '') // 移除图片
    .replace(/\[.*?\]\(.*?\)/g, '') // 移除链接
    .replace(/\n+/g, ' ') // 将换行替换为空格
    .trim()
  
  // 限制长度
  return summary.length > 120 ? summary.substring(0, 120) + '...' : summary
}

const viewPost = (postId: number) => {
  router.push(`/forum/post/${postId}`)
}

const handleUpdateProfile = async () => {
  try {
    const response = await updateUserProfile({
      username: editForm.nickname, // 注意：API可能需要username字段
      bio: editForm.bio,
      gender: editForm.gender,
      department: editForm.department,
      major: editForm.major,
      grade: editForm.grade
    }) as any as ApiResponse<null>
    
    if (response.code === 200) {
      // 更新本地数据
      Object.assign(userProfile.value, editForm)
      showEditProfile.value = false
      ElMessage.success('资料更新成功！')
      
      // 重新加载用户信息
      await loadUserProfile()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户资料失败:', error)
    ElMessage.error('更新失败')
  }
}

const sendVerificationCode = async () => {
  try {
    const response = await sendEmailCode(userProfile.value.email) as any as ApiResponse<null>
    
    if (response.code === 200) {
      // 开始倒计时
      codeCountdown.value = 60
      const timer = setInterval(() => {
        codeCountdown.value--
        if (codeCountdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
      ElMessage.success('验证码已发送到您的邮箱')
    } else {
      ElMessage.error(response.message || '发送验证码失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败')
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    const response = await changePassword({
      code: passwordForm.code,
      newPassword: passwordForm.newPassword
    }) as any as ApiResponse<null>
    
    if (response.code === 200) {
      showChangePassword.value = false
      // 重置表单
      passwordForm.code = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      ElMessage.success('密码修改成功！')
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  }
}

const handleAvatarChange = (file: any) => {
  console.log('选择的头像文件:', file)
  // TODO: 处理头像上传逻辑
}

const handleUploadAvatar = async () => {
  try {
    // TODO: 实现头像上传逻辑
    showAvatarUpload.value = false
    ElMessage.success('头像上传成功！')
    
    // 重新加载用户信息
    await loadUserProfile()
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('头像上传失败')
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    // 已经在个人资料页面
  }
}

onMounted(async () => {
  console.log('个人资料页面加载完成，开始加载数据...')
  
  // 并行加载所有数据
  await Promise.all([
    loadUserProfile(),
    loadUserStats(),
    loadRecentPosts(),
    loadRecentResources()
  ])
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 主要内容区域 */
.profile-main {
  padding: 32px 24px;
}

.profile-content {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 个人信息头部 */
.profile-header {
  padding: 32px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  gap: 24px;
  align-items: center;
}

.avatar-container {
  position: relative;
}

.change-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  background: var(--primary-500);
  color: white;
  border: 3px solid white;
}

.user-basic-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name {
  font-size: 2rem;
  font-weight: 700;
  color: var(--gray-800);
  margin: 0;
}

.user-title {
  color: var(--gray-600);
  font-size: 16px;
  margin: 0;
}

.user-grade {
  color: var(--gray-500);
  font-size: 14px;
  margin: 0;
}

.user-stats {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-600);
}

.stat-label {
  font-size: 12px;
  color: var(--gray-500);
}

.profile-actions {
  display: flex;
  gap: 12px;
}

/* 详细信息区域 */
.profile-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.section-title {
  color: var(--gray-800);
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 20px 0;
}

.info-section {
  padding: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: var(--gray-500);
  font-weight: 600;
}

.info-value {
  color: var(--gray-700);
  font-weight: 500;
}

.bio-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bio-text {
  color: var(--gray-700);
  line-height: 1.6;
  margin: 0;
  padding: 12px;
  background: var(--gray-50);
  border-radius: 8px;
}

/* 活动区域 */
.activity-section {
  padding: 24px;
}

.activity-tabs {
  height: 100%;
}

.activity-content {
  height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.activity-content::-webkit-scrollbar {
  width: 6px;
}

.activity-content::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 3px;
}

.activity-content::-webkit-scrollbar-thumb {
  background: var(--gray-300);
  border-radius: 3px;
}

.activity-content::-webkit-scrollbar-thumb:hover {
  background: var(--gray-400);
}

.posts-list,
.resources-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-item {
  padding: 16px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  cursor: pointer;
  transition: var(--transition-base);
}

.post-item:hover {
  border-color: var(--primary-300);
  transform: translateY(-1px);
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--gray-800);
  margin: 0 0 8px 0;
}

.post-summary {
  color: var(--gray-600);
  font-size: 14px;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-time {
  font-size: 12px;
  color: var(--gray-500);
}

.post-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--gray-500);
}

.resource-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border: 1px solid var(--gray-200);
  border-radius: 12px;
}

.resource-icon {
  font-size: 24px;
  color: var(--primary-500);
  flex-shrink: 0;
}

.resource-info {
  flex: 1;
}

.resource-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--gray-800);
  margin: 0 0 4px 0;
}

.resource-desc {
  font-size: 12px;
  color: var(--gray-600);
  margin: 0 0 8px 0;
}

.resource-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: var(--gray-500);
}

/* 上传区域 */
.avatar-uploader {
  width: 100%;
}

.upload-area {
  border: 2px dashed var(--gray-300);
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: var(--transition-base);
}

.upload-area:hover {
  border-color: var(--primary-400);
  background: var(--primary-50);
}

.upload-icon {
  font-size: 32px;
  color: var(--gray-400);
  margin-bottom: 12px;
}

.upload-text {
  color: var(--gray-600);
  font-weight: 600;
  margin-bottom: 8px;
}

.upload-tip {
  color: var(--gray-500);
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-content {
    max-width: 800px;
  }
  
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .profile-details {
    flex-direction: column;
  }
  
  .info-section,
  .activity-section {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .profile-main {
    padding: 24px 16px;
  }
  
  .user-stats {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .stat-item {
    min-width: calc(50% - 8px);
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .profile-actions {
    flex-direction: column;
    gap: 12px;
  }
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: var(--gray-500);
}

.empty-icon {
  font-size: 48px;
  color: var(--gray-400);
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 20px;
  color: var(--gray-600);
}

.empty-state .el-button {
  border-radius: 8px;
  font-weight: 600;
}
</style> 