<template>
  <div class="profile-container">
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
          <router-link to="/resources" class="nav-item">资源</router-link>
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
    <div class="profile-main">
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
              <h1 class="user-name">{{ userProfile.nickname }}</h1>
              <p class="user-title">{{ userProfile.department }} · {{ userProfile.major }}</p>
              <p class="user-grade">{{ userProfile.grade }}</p>
              
              <div class="user-stats">
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
                <span class="info-value">{{ userProfile.username }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ userProfile.email }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学号</span>
                <span class="info-value">{{ userProfile.studentId }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">性别</span>
                <span class="info-value">{{ getGenderText(userProfile.gender) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学院</span>
                <span class="info-value">{{ userProfile.department }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">专业</span>
                <span class="info-value">{{ userProfile.major }}</span>
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
                  <div class="posts-list">
                    <div 
                      v-for="post in recentPosts" 
                      :key="post.id"
                      class="post-item"
                      @click="viewPost(post.id)"
                    >
                      <div class="post-info">
                        <h4 class="post-title">{{ post.title }}</h4>
                        <p class="post-summary">{{ post.content }}</p>
                        <div class="post-meta">
                          <span class="post-time">{{ post.createTime }}</span>
                          <div class="post-stats">
                            <span>{{ post.viewsCount }} 浏览</span>
                            <span>{{ post.likesCount }} 点赞</span>
                            <span>{{ post.commentsCount }} 评论</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="我的资源" name="resources">
                  <div class="resources-list">
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

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const showEditProfile = ref(false)
const showChangePassword = ref(false)
const showAvatarUpload = ref(false)
const activeTab = ref('posts')
const codeCountdown = ref(0)

// 用户资料数据
const userProfile = ref({
  id: 12345,
  username: 'student123',
  email: 'student@school.edu',
  nickname: '测试用户',
  avatar: '',
  bio: '这是一个热爱学习的大学生，喜欢分享知识和经验。',
  gender: 1,
  studentId: '20220101001',
  department: '计算机学院',
  major: '软件工程',
  grade: '2023级',
  points: 1250,
  role: 0,
  isVerified: 1
})

// 用户统计数据
const userStats = ref({
  postsCount: 25,
  commentsCount: 150,
  resourcesCount: 10,
  likesReceived: 300,
  coursesCount: 8,
  schedulesCount: 15
})

// 最近帖子
const recentPosts = ref([
  {
    id: 1,
    title: '分享一些高效学习方法',
    content: '经过一学期的摸索，总结了一些比较有效的学习方法...',
    createTime: '2024-01-15 10:30',
    viewsCount: 234,
    likesCount: 89,
    commentsCount: 23
  },
  {
    id: 2,
    title: '大学生活时间管理心得',
    content: '作为一名大二学生，想和大家分享一些时间管理的经验...',
    createTime: '2024-01-12 16:20',
    viewsCount: 156,
    likesCount: 45,
    commentsCount: 12
  }
])

// 最近资源
const recentResources = ref([
  {
    id: 1,
    title: '数据结构课件整理',
    description: '完整的数据结构课程课件，包含所有章节',
    createdAt: '2024-01-10 14:30',
    downloadCount: 89
  },
  {
    id: 2,
    title: '高等数学期末复习资料',
    description: '精心整理的高数复习重点和练习题',
    createdAt: '2024-01-08 09:15',
    downloadCount: 156
  }
])

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

// 方法
const getGenderText = (gender: number) => {
  const genderMap = { 0: '保密', 1: '男', 2: '女' }
  return genderMap[gender as keyof typeof genderMap] || '保密'
}

const viewPost = (postId: number) => {
  router.push(`/forum/post/${postId}`)
}

const handleUpdateProfile = () => {
  // 更新用户资料
  Object.assign(userProfile.value, editForm)
  showEditProfile.value = false
  ElMessage.success('资料更新成功！')
}

const sendVerificationCode = () => {
  // 发送验证码
  codeCountdown.value = 60
  const timer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
  ElMessage.success('验证码已发送到您的邮箱')
}

const handleChangePassword = () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  showChangePassword.value = false
  ElMessage.success('密码修改成功！')
}

const handleAvatarChange = (file: any) => {
  console.log('选择的头像文件:', file)
}

const handleUploadAvatar = () => {
  showAvatarUpload.value = false
  ElMessage.success('头像上传成功！')
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    // 已经在个人资料页面
  }
}

onMounted(() => {
  // 初始化编辑表单
  Object.assign(editForm, {
    nickname: userProfile.value.nickname,
    department: userProfile.value.department,
    major: userProfile.value.major,
    grade: userProfile.value.grade,
    gender: userProfile.value.gender,
    bio: userProfile.value.bio
  })
  
  console.log('个人资料页面加载完成')
})
</script>

<style scoped>
.profile-container {
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

.nav-item:hover {
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
  .profile-details {
    grid-template-columns: 1fr;
  }
  
  .profile-header {
    flex-direction: column;
    gap: 24px;
    align-items: center;
    text-align: center;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .profile-main {
    padding: 16px;
  }
  
  .profile-header {
    padding: 24px 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .profile-actions {
    width: 100%;
    justify-content: center;
  }
}
</style> 