<template>
  <div class="post-detail-container">
    <!-- 使用通用顶部导航栏组件 -->
    <TopNavbar />

    <!-- 主要内容区域 -->
    <div class="post-detail-main">
      <div class="post-detail-content">
        <!-- 返回按钮 -->
        <div class="back-section">
          <el-button @click="goBack" circle>
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <span class="breadcrumb">论坛 / 帖子详情</span>
        </div>

        <!-- 帖子内容 -->
        <div class="post-content-section card-light" v-loading="loading">
          <div class="post-header">
            <div class="post-meta">
              <el-tag type="primary">{{ post.categoryName }}</el-tag>
              <span class="post-time">{{ post.createdAt }}</span>
            </div>
            
            <div class="post-actions">
              <el-button text @click="toggleLike">
                <el-icon><Star /></el-icon>
                {{ post.isLiked ? '已点赞' : '点赞' }} ({{ post.likeCount }})
              </el-button>
              <el-button text>
                <el-icon><Share /></el-icon>
                分享
              </el-button>
              <el-dropdown>
                <el-button text>
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>举报</el-dropdown-item>
                    <el-dropdown-item v-if="isAuthor">编辑</el-dropdown-item>
                    <el-dropdown-item v-if="isAuthor" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <h1 class="post-title">{{ post.title }}</h1>

          <div class="author-section">
            <div class="author-info">
              <el-avatar :size="50" :src="post.avatar">
                {{ post.nickname?.charAt(0) }}
              </el-avatar>
              <div class="author-details">
                <div class="author-name">{{ post.nickname }}</div>
                <div class="author-stats">发帖 {{ authorStats.postCount }} · 获赞 {{ authorStats.likeCount }}</div>
              </div>
            </div>
            <el-button type="primary" plain size="small" class="follow-btn">关注</el-button>
          </div>

          <div class="post-content">
            <div class="markdown-content">
              <MdPreview 
                :model-value="post.content"
                preview-theme="default"
                code-theme="atom"
                :show-code-row-number="true"
                class="post-markdown"
              />
            </div>
          </div>

          <div class="post-footer">
            <div class="post-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ post.viewCount }} 浏览
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ post.commentCount }} 评论
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ post.likeCount }} 点赞
              </span>
            </div>
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="comments-section card-light">
          <div class="comments-header">
            <h3>评论 ({{ post.commentCount || 0 }})</h3>
            <el-select v-model="commentSort" size="small" style="width: 120px">
              <el-option label="最新" value="latest" />
              <el-option label="最热" value="hot" />
            </el-select>
          </div>

          <!-- 发表评论 -->
          <div class="comment-form">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
              class="comment-input"
            />
            <div class="comment-actions">
              <el-button type="primary" @click="submitComment" :loading="commenting">
                {{ commenting ? '发表中...' : '发表评论' }}
              </el-button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <!-- 空状态 -->
            <div v-if="comments.length === 0" class="empty-comments">
              <el-empty description="暂无评论" :image-size="80">
                <template #description>
                  <p>暂无评论，快来发表第一个评论吧！</p>
                </template>
              </el-empty>
            </div>
            
            <div 
              v-for="comment in comments" 
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar :size="40" :src="comment.avatar">
                  {{ comment.nickname?.charAt(0) }}
                </el-avatar>
              </div>
              
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.nickname }}</span>
                  <span class="comment-time">{{ comment.createdAt }}</span>
                </div>
                
                <div class="comment-text">{{ comment.content }}</div>
                
                <div class="comment-footer">
                  <el-button text size="small" @click="replyToComment(comment)">
                    <el-icon><ChatDotRound /></el-icon>
                    回复
                  </el-button>
                  <el-button text size="small" @click="likeComment(comment)">
                    <el-icon><Star /></el-icon>
                    {{ comment.likeCount }}
                  </el-button>
                </div>

                <!-- 回复列表 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                  <div 
                    v-for="reply in comment.replies" 
                    :key="reply.id"
                    class="reply-item"
                  >
                    <el-avatar :size="32" :src="reply.avatar">
                      {{ reply.nickname?.charAt(0) }}
                    </el-avatar>
                    <div class="reply-content">
                      <div class="reply-header">
                        <span class="reply-author">{{ reply.nickname }}</span>
                        <span class="reply-time">{{ reply.createdAt }}</span>
                      </div>
                      <div class="reply-text">{{ reply.content }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, 
  Star, 
  Share, 
  MoreFilled, 
  View, 
  ChatDotRound 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getPostDetail, likePost as likePostAPI, getComments, createComment as createCommentAPI, likeComment as likeCommentAPI } from '@/api/forum'
import type { Post, ApiResponse } from '@/types'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import TopNavbar from '@/components/TopNavbar.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 响应式数据
const newComment = ref('')
const commentSort = ref('latest')
const loading = ref(false)
const commenting = ref(false)

// 真实数据
const post = ref<Post>({
  id: 0,
  title: '',
  content: '',
  summary: '',
  userId: 0,
  nickname: '',
  avatar: '',
  categoryId: 0,
  categoryName: '',
  viewCount: 0,
  likeCount: 0,
  commentCount: 0,
  isLiked: false,
  createdAt: '',
  updatedAt: ''
})
const comments = ref<any[]>([])

// 作者统计信息
const authorStats = ref({
  postCount: 12,
  likeCount: 234
})

// 计算属性
const isAuthor = computed(() => {
  return userStore.user?.id === post.value.userId
})

// 方法
const goBack = () => {
  router.go(-1)
}

const toggleLike = async () => {
  if (!post.value) return
  
  try {
    const response = await likePostAPI(post.value.id) as any as ApiResponse<null>
    if (response.code === 200) {
      post.value.isLiked = !post.value.isLiked
      post.value.likeCount += post.value.isLiked ? 1 : -1
      ElMessage.success(post.value.isLiked ? '点赞成功' : '取消点赞')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    commenting.value = true
    const response = await createCommentAPI({
      postId: post.value.id,
      content: newComment.value,
      parentId: null
    }) as any as ApiResponse<{ commentId: number }>
    
    if (response.code === 200) {
      newComment.value = ''
      ElMessage.success('评论发表成功')
      // 重新加载评论
      loadComments()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '评论发表失败')
  } finally {
    commenting.value = false
  }
}

const replyToComment = (comment: any) => {
  ElMessage.info('回复功能开发中...')
}

const likeComment = async (comment: any) => {
  try {
    const response = await likeCommentAPI(comment.id) as any as ApiResponse<null>
    if (response.code === 200) {
      comment.isLiked = !comment.isLiked
      comment.likeCount += comment.isLiked ? 1 : -1
      ElMessage.success(comment.isLiked ? '点赞成功' : '取消点赞')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 加载帖子详情
const loadPost = async () => {
  const postId = Number(route.params.id)
  if (!postId) {
    ElMessage.error('帖子ID无效')
    router.push('/forum')
    return
  }
  
  try {
    loading.value = true
    const response = await getPostDetail(postId) as any as ApiResponse<Post>
    if (response.code === 200) {
      post.value = response.data
      
      // 调试信息：打印帖子详情的点赞状态
      console.log('加载的帖子详情:', {
        id: post.value.id,
        title: post.value.title,
        isLiked: post.value.isLiked,
        likeCount: post.value.likeCount
      })
    } else {
      ElMessage.error(response.message || '加载帖子失败')
      router.push('/forum')
    }
  } catch (error: any) {
    console.error('加载帖子失败:', error)
    ElMessage.error(error.response?.data?.message || '加载帖子失败')
    router.push('/forum')
  } finally {
    loading.value = false
  }
}

// 加载评论列表
const loadComments = async () => {
  if (!post.value.id) return
  
  try {
    const response = await getComments(post.value.id) as any as ApiResponse<{
      total: number
      list: any[]
    }>
    
    if (response.code === 200) {
      comments.value = response.data.list || []
    } else {
      console.error('加载评论失败:', response.message)
    }
  } catch (error: any) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  }
}

onMounted(async () => {
  console.log('帖子详情页面加载完成', route.params.id)
  await loadPost()
  if (post.value.id) {
    await loadComments()
  }
})
</script>

<style scoped>
.post-detail-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 主要内容区域 */
.post-detail-main {
  padding: 32px 24px;
}

.post-detail-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 返回按钮区域 */
.back-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.breadcrumb {
  color: var(--gray-600);
  font-size: 14px;
}

/* 帖子内容区域 */
.post-content-section {
  padding: 32px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.post-time {
  color: var(--gray-500);
  font-size: 14px;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.post-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 24px;
  line-height: 1.3;
}

.author-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: var(--primary-50);
  border-radius: 16px;
  margin-bottom: 32px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 700;
  color: var(--gray-800);
  font-size: 16px;
}

.author-stats {
  color: var(--gray-600);
  font-size: 14px;
}

/* 关注按钮优化样式 */
.follow-btn {
  background: transparent !important;
  border: 2px solid var(--primary-300) !important;
  color: var(--primary-600) !important;
  padding: 8px 16px !important;
  border-radius: 20px !important;
  font-weight: 500 !important;
  transition: var(--transition-base) !important;
  min-width: 80px !important;
}

.follow-btn:hover {
  background: var(--primary-50) !important;
  border-color: var(--primary-400) !important;
  color: var(--primary-700) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 2px 8px rgba(168, 85, 247, 0.2) !important;
}

.follow-btn:active {
  transform: translateY(0) !important;
}

/* 已关注状态样式 */
.follow-btn.following {
  background: var(--primary-100) !important;
  border-color: var(--primary-400) !important;
  color: var(--primary-700) !important;
}

.follow-btn.following:hover {
  background: var(--gray-100) !important;
  border-color: var(--gray-400) !important;
  color: var(--gray-600) !important;
}

.post-content {
  margin-bottom: 32px;
}

.markdown-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.post-markdown {
  font-size: 16px !important;
  line-height: 1.8 !important;
  color: var(--gray-700) !important;
}

/* Markdown内容样式优化 */
.post-markdown h1,
.post-markdown h2,
.post-markdown h3,
.post-markdown h4,
.post-markdown h5,
.post-markdown h6 {
  color: var(--gray-800) !important;
  font-weight: 600 !important;
  margin: 24px 0 16px 0 !important;
}

.post-markdown h1 {
  font-size: 28px !important;
  border-bottom: 2px solid var(--primary-200) !important;
  padding-bottom: 8px !important;
}

.post-markdown h2 {
  font-size: 24px !important;
  border-bottom: 1px solid var(--gray-200) !important;
  padding-bottom: 6px !important;
}

.post-markdown h3 {
  font-size: 20px !important;
}

.post-markdown p {
  margin-bottom: 16px !important;
  color: var(--gray-700) !important;
}

.post-markdown blockquote {
  border-left: 4px solid var(--primary-400) !important;
  background: var(--primary-50) !important;
  padding: 16px 20px !important;
  margin: 16px 0 !important;
  border-radius: 6px !important;
}

.post-markdown code {
  background: var(--gray-100) !important;
  padding: 2px 6px !important;
  border-radius: 4px !important;
  font-size: 14px !important;
  color: var(--primary-700) !important;
}

.post-markdown pre {
  background: var(--gray-900) !important;
  border-radius: 8px !important;
  padding: 16px !important;
  margin: 16px 0 !important;
  overflow-x: auto !important;
}

.post-markdown ul,
.post-markdown ol {
  margin: 16px 0 !important;
  padding-left: 24px !important;
}

.post-markdown li {
  margin-bottom: 8px !important;
  color: var(--gray-700) !important;
}

.post-markdown table {
  width: 100% !important;
  border-collapse: collapse !important;
  margin: 16px 0 !important;
  border-radius: 8px !important;
  overflow: hidden !important;
  box-shadow: var(--shadow-sm) !important;
}

.post-markdown th,
.post-markdown td {
  padding: 12px 16px !important;
  text-align: left !important;
  border-bottom: 1px solid var(--gray-200) !important;
}

.post-markdown th {
  background: var(--primary-50) !important;
  font-weight: 600 !important;
  color: var(--gray-800) !important;
}

.post-markdown a {
  color: var(--primary-600) !important;
  text-decoration: none !important;
  border-bottom: 1px solid transparent !important;
  transition: var(--transition-base) !important;
}

.post-markdown a:hover {
  color: var(--primary-700) !important;
  border-bottom-color: var(--primary-400) !important;
}

.post-markdown img {
  max-width: 100% !important;
  height: auto !important;
  border-radius: 8px !important;
  margin: 16px 0 !important;
  box-shadow: var(--shadow-md) !important;
}

.post-footer {
  padding-top: 24px;
  border-top: 1px solid var(--gray-200);
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--gray-500);
  font-size: 14px;
}

/* 评论区域 */
.comments-section {
  padding: 32px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.comments-header h3 {
  color: var(--gray-800);
  font-size: 18px;
  font-weight: 700;
}

.comment-form {
  margin-bottom: 32px;
}

.comment-input {
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.empty-comments {
  text-align: center;
  padding: 48px 24px;
  color: var(--gray-500);
}

.comment-item {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: var(--gray-800);
}

.comment-time {
  color: var(--gray-500);
  font-size: 12px;
}

.comment-text {
  color: var(--gray-700);
  line-height: 1.6;
  margin-bottom: 12px;
}

.comment-footer {
  display: flex;
  gap: 16px;
}

.replies-list {
  margin-top: 16px;
  padding-left: 16px;
  border-left: 2px solid var(--gray-200);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reply-item {
  display: flex;
  gap: 12px;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.reply-author {
  font-weight: 600;
  color: var(--gray-800);
  font-size: 14px;
}

.reply-time {
  color: var(--gray-500);
  font-size: 12px;
}

.reply-text {
  color: var(--gray-700);
  line-height: 1.6;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-detail-main {
    padding: 16px;
  }
  
  .post-content-section,
  .comments-section {
    padding: 24px 16px;
  }
  
  .post-title {
    font-size: 1.5rem;
  }
  
  .author-section {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .comment-item {
    gap: 12px;
  }
}
</style> 