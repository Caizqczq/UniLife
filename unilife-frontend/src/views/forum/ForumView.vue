<template>
  <div class="forum-container">
    <!-- 顶部导航栏 -->
    <nav class="forum-navbar glass-light">
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
          <router-link to="/forum" class="nav-item active">论坛</router-link>
          <router-link to="/resources" class="nav-item">资源</router-link>
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
    <div class="forum-main">
      <div class="forum-content">
        <!-- 侧边栏 -->
        <aside class="forum-sidebar">
          <!-- 发布按钮 -->
          <div class="create-post-section">
            <el-button 
              type="primary" 
              size="large" 
              class="create-post-btn animate-glow"
              @click="showCreatePost = true"
            >
              <el-icon><Edit /></el-icon>
              发布帖子
            </el-button>
          </div>

          <!-- 分类列表 -->
          <div class="categories-section card-light">
            <h3 class="section-title">讨论分类</h3>
            <div class="categories-list">
              <div 
                v-for="category in categories" 
                :key="category.id"
                class="category-item"
                :class="{ active: selectedCategory === category.id }"
                @click="selectCategory(category.id)"
              >
                <el-icon class="category-icon">
                  <School />
                </el-icon>
                <span class="category-name">{{ category.name }}</span>
                <span class="post-count">0</span>
              </div>
            </div>
          </div>

          <!-- 热门标签 -->
          <div class="tags-section card-light">
            <h3 class="section-title">热门标签</h3>
            <div class="tags-list">
              <el-tag 
                v-for="tag in hotTags" 
                :key="tag"
                class="hot-tag"
                @click="searchPosts(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </aside>

        <!-- 帖子列表区域 -->
        <main class="posts-area">
          <!-- 搜索和筛选栏 -->
          <div class="search-section card-light">
            <div class="search-bar">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索帖子标题或内容..."
                size="large"
                class="search-input"
                @keyup.enter="searchPosts()"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #append>
                  <el-button @click="searchPosts()">搜索</el-button>
                </template>
              </el-input>
            </div>
            
            <div class="filter-options">
              <el-select v-model="sortBy" placeholder="排序方式" size="default" @change="handleSortChange">
                <el-option label="最新发布" value="latest" />
                <el-option label="最多点赞" value="likes" />
                <el-option label="最多回复" value="comments" />
                <el-option label="最多浏览" value="views" />
              </el-select>
              
              <el-button @click="refreshPosts" circle>
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- 帖子列表 -->
          <div class="posts-list" v-loading="loading">
            <div 
              v-for="post in posts" 
              :key="post.id"
              class="post-card card-light animate-fade-in-up"
              @click="viewPost(post.id)"
            >
              <div class="post-header">
                <div class="author-info">
                  <el-avatar :size="40" :src="post.avatar">
                    {{ post.nickname?.charAt(0) }}
                  </el-avatar>
                  <div class="author-details">
                    <div class="author-name">{{ post.nickname }}</div>
                    <div class="post-time">{{ post.createdAt }}</div>
                  </div>
                </div>
                
                <div class="category-badge">
                  <el-tag type="primary">
                    {{ post.categoryName }}
                  </el-tag>
                </div>
              </div>

              <div class="post-content">
                <h3 class="post-title">{{ post.title }}</h3>
                <p class="post-summary">{{ post.summary }}</p>
              </div>

              <div class="post-footer">
                <div class="post-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ post.viewCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ post.commentCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ post.likeCount }}
                  </span>
                </div>
                
                <div class="post-actions">
                  <el-button 
                    text 
                    :type="post.isLiked ? 'primary' : 'default'"
                    @click.stop="toggleLike(post)"
                  >
                    <el-icon><Star /></el-icon>
                    {{ post.isLiked ? '已点赞' : '点赞' }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="posts.length === 0 && !loading" class="empty-state">
              <el-empty description="暂无帖子">
                <el-button type="primary" @click="showCreatePost = true">
                  发布第一个帖子
                </el-button>
              </el-empty>
            </div>

            <!-- 分页 -->
            <div v-if="posts.length > 0" class="pagination-section">
              <el-pagination
                v-model:current-page="pagination.page"
                v-model:page-size="pagination.size"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 发布帖子对话框 -->
    <el-dialog
      v-model="showCreatePost"
      title="发布新帖子"
      width="800px"
      class="create-post-dialog"
      :close-on-click-modal="false"
    >
      <div class="create-post-form">
        <el-form label-position="top" :model="postForm" ref="postFormRef">
          <div class="form-row">
            <el-form-item 
              label="帖子标题" 
              prop="title"
              :rules="[{ required: true, message: '请输入帖子标题', trigger: 'blur' }]"
              class="title-field"
            >
              <el-input 
                v-model="postForm.title"
                placeholder="写一个吸引人的标题..." 
                size="large"
                maxlength="100"
                show-word-limit
                clearable
              />
            </el-form-item>
            <el-form-item 
              label="分类" 
              prop="categoryId"
              :rules="[{ required: true, message: '请选择分类', trigger: 'change' }]"
              class="category-field"
            >
              <el-select 
                v-model="postForm.categoryId"
                placeholder="选择分类" 
                size="large" 
                style="width: 100%"
                clearable
              >
                <el-option 
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name" 
                  :value="category.id" 
                />
              </el-select>
            </el-form-item>
          </div>
          
          <el-form-item 
            label="帖子内容" 
            prop="content"
            :rules="[{ required: true, message: '请输入帖子内容', trigger: 'blur' }]"
            class="content-field"
          >
            <div class="markdown-editor-container">
              <MdEditor 
                v-model="postForm.content"
                :height="400"
                :preview="true"
                placeholder="支持Markdown语法，让你的内容更生动..."
                theme="light"
                preview-theme="default"
                code-theme="atom"
              />
            </div>
          </el-form-item>
          
          <div class="form-tips">
            <div class="tip-item">
              <el-icon><InfoFilled /></el-icon>
              <span>支持Markdown语法：**粗体**、*斜体*、`代码`、[链接](url)等</span>
            </div>
          </div>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCreatePost = false" :disabled="posting" size="large">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="handleCreatePost" 
            :loading="posting"
            size="large"
            class="publish-btn"
          >
            <el-icon v-if="!posting"><EditPen /></el-icon>
            {{ posting ? '发布中...' : '发布帖子' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Edit, 
  Search, 
  Refresh, 
  View, 
  ChatDotRound, 
  Star, 
  Setting,
  School,
  EditPen,
  InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getPosts, getCategories, createPost, likePost } from '@/api/forum'
import type { Post, Category, ApiResponse } from '@/types'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const showCreatePost = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const sortBy = ref('latest')
const loading = ref(false)
const posting = ref(false)
const postFormRef = ref()

// 真实数据
const categories = ref<Category[]>([])
const posts = ref<Post[]>([])
const pagination = ref({
  page: 1,
  size: 10,
  total: 0,
  pages: 0
})

// 发帖表单数据
const postForm = reactive({
  title: '',
  content: '',
  categoryId: null as number | null
})

// 热门标签
const hotTags = ref(['学习交流', '生活分享', '技术讨论', '考试', '实习', '社团活动'])

// 方法
const selectCategory = (categoryId: number | null) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  pagination.value.page = 1 // 重置到第一页
  loadPosts()
}

const searchPosts = (keyword?: string) => {
  if (keyword) {
    searchKeyword.value = keyword
  }
  pagination.value.page = 1 // 重置到第一页
  loadPosts()
}

const refreshPosts = () => {
  pagination.value.page = 1
  loadPosts()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.page = 1
  loadPosts()
}

const handleCurrentChange = (page: number) => {
  pagination.value.page = page
  loadPosts()
}

const handleSortChange = () => {
  pagination.value.page = 1
  loadPosts()
}

const viewPost = (postId: number) => {
  router.push(`/forum/post/${postId}`)
}

const toggleLike = async (post: Post) => {
  try {
    const response = await likePost(post.id) as any as ApiResponse<null>
    if (response.code === 200) {
      // 切换点赞状态
      const wasLiked = post.isLiked
      post.isLiked = !wasLiked
      post.likeCount += post.isLiked ? 1 : -1
      ElMessage.success(post.isLiked ? '点赞成功' : '取消点赞')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleCreatePost = async () => {
  // 表单验证
  if (!postFormRef.value) return
  
  try {
    const valid = await postFormRef.value.validate()
    if (!valid) return
  } catch (error) {
    console.log('表单验证失败:', error)
    return
  }

  if (!postForm.title.trim()) {
    ElMessage.warning('请输入帖子标题')
    return
  }
  if (!postForm.categoryId) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!postForm.content.trim()) {
    ElMessage.warning('请输入帖子内容')
    return
  }

  try {
    posting.value = true
    const response = await createPost({
      title: postForm.title.trim(),
      content: postForm.content.trim(),
      categoryId: postForm.categoryId
    }) as any as ApiResponse<{ postId: number }>
    
    if (response.code === 200) {
      showCreatePost.value = false
      ElMessage.success('发布成功！')
      // 重置表单
      postForm.title = ''
      postForm.content = ''
      postForm.categoryId = null
      // 清除验证状态
      if (postFormRef.value) {
        postFormRef.value.resetFields()
      }
      // 刷新帖子列表
      loadPosts()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    posting.value = false
  }
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 加载帖子列表
const loadPosts = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.value.page,
      size: pagination.value.size,
      sort: sortBy.value
    }
    
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim()
    }
    
    const response = await getPosts(params) as any as ApiResponse<{
      total: number
      list: Post[]
      pages: number
    }>
    
    if (response.code === 200) {
      posts.value = response.data.list || []
      pagination.value.total = response.data.total || 0
      pagination.value.pages = response.data.pages || 0
      
      // 调试信息：打印帖子的点赞状态
      console.log('加载的帖子数据:', posts.value.map(p => ({
        id: p.id,
        title: p.title,
        isLiked: p.isLiked,
        likeCount: p.likeCount
      })))
    } else {
      ElMessage.error(response.message || '加载帖子失败')
    }
  } catch (error: any) {
    console.error('加载帖子失败:', error)
    ElMessage.error(error.response?.data?.message || '加载帖子失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getCategories({ status: 1 }) as any as ApiResponse<{
      total: number
      list: Category[]
    }>
    
    if (response.code === 200) {
      categories.value = response.data.list || []
    } else {
      console.error('加载分类失败:', response.message)
    }
  } catch (error: any) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

onMounted(() => {
  console.log('论坛页面加载完成')
  loadCategories()
  loadPosts()
})
</script>

<style scoped>
.forum-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 导航栏样式 */
.forum-navbar {
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
.forum-main {
  padding: 32px 24px;
}

.forum-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 32px;
}

/* 侧边栏样式 */
.forum-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.create-post-section {
  position: sticky;
  top: 100px;
}

.create-post-btn {
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

.post-count {
  font-size: 12px;
  color: var(--gray-500);
  background: var(--gray-100);
  padding: 2px 8px;
  border-radius: 12px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-tag {
  cursor: pointer;
  transition: var(--transition-base);
}

.hot-tag:hover {
  transform: translateY(-1px);
}

/* 帖子区域样式 */
.posts-area {
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

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  padding: 24px;
  cursor: pointer;
  transition: var(--transition-base);
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-purple);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 600;
  color: var(--gray-800);
}

.post-time {
  font-size: 12px;
  color: var(--gray-500);
}

.post-content {
  margin-bottom: 16px;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 8px;
  line-height: 1.4;
}

.post-summary {
  color: var(--gray-600);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-stats {
  display: flex;
  gap: 16px;
}

/* 分页样式 */
.pagination-section {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--gray-500);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .forum-content {
    grid-template-columns: 1fr;
  }
  
  .forum-sidebar {
    order: 2;
  }
  
  .posts-area {
    order: 1;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .forum-main {
    padding: 16px;
  }
  
  .forum-content {
    gap: 16px;
  }
  
  .search-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-options {
    justify-content: center;
  }
}

/* 点赞按钮优化样式 */
.post-actions .el-button {
  border: none !important;
  background: transparent !important;
  color: var(--gray-500) !important;
  font-weight: 500 !important;
  transition: var(--transition-base) !important;
  padding: 8px 12px !important;
  border-radius: 8px !important;
}

.post-actions .el-button:hover {
  background: rgba(168, 85, 247, 0.1) !important;
  color: var(--primary-600) !important;
  transform: translateY(-1px) !important;
}

.post-actions .el-button.el-button--primary {
  background: rgba(168, 85, 247, 0.15) !important;
  color: var(--primary-600) !important;
  font-weight: 600 !important;
}

.post-actions .el-button.el-button--primary:hover {
  background: rgba(168, 85, 247, 0.2) !important;
  color: var(--primary-700) !important;
}

.post-actions .el-button .el-icon {
  margin-right: 4px !important;
}

.create-post-dialog {
  border-radius: 16px !important;
}

.create-post-dialog .el-dialog__header {
  padding: 24px 24px 16px !important;
  background: var(--gradient-light) !important;
  border-bottom: 1px solid var(--gray-200) !important;
}

.create-post-dialog .el-dialog__title {
  font-size: 20px !important;
  font-weight: 700 !important;
  color: var(--gray-800) !important;
}

.create-post-dialog .el-dialog__body {
  padding: 24px !important;
  background: var(--gray-50) !important;
}

.create-post-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 200px;
  gap: 16px;
  align-items: start;
}

.title-field {
  grid-column: 1;
}

.category-field {
  grid-column: 2;
}

.content-field .el-form-item__label {
  font-size: 16px !important;
  font-weight: 600 !important;
  color: var(--gray-800) !important;
  margin-bottom: 12px !important;
}

.markdown-editor-container {
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid var(--gray-200);
  transition: var(--transition-base);
}

.markdown-editor-container:hover {
  border-color: var(--primary-300);
}

.markdown-editor-container:focus-within {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.1);
}

.form-tips {
  background: rgba(168, 85, 247, 0.05);
  border: 1px solid rgba(168, 85, 247, 0.2);
  border-radius: 12px;
  padding: 16px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--primary-700);
  font-size: 14px;
}

.tip-item .el-icon {
  color: var(--primary-500);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 24px !important;
  background: var(--gray-50) !important;
  border-top: 1px solid var(--gray-200) !important;
}

.publish-btn {
  background: var(--gradient-primary) !important;
  border: none !important;
  font-weight: 600 !important;
  padding: 12px 24px !important;
  border-radius: 12px !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

.publish-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--shadow-purple) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .create-post-dialog {
    width: 95% !important;
    margin: 20px auto !important;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .title-field,
  .category-field {
    grid-column: 1;
  }
}
</style> 