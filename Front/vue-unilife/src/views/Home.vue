<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores';
import userApi from '../api/user';
import type { UserStats } from '../api/user';
import { ElMessage, ElSkeleton, ElEmpty, ElIcon, ElButton, ElCard, ElDivider, ElAvatar, ElBadge } from 'element-plus';
import { Document, Star, ChatDotRound, View, Edit, Delete, Plus, TrendCharts, Trophy, Timer } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 数据状态
const loading = ref(true);
const statsLoading = ref(true);
const postsLoading = ref(true);
const error = ref<string | null>(null);

// 用户帖子数据
const userPosts = ref<any[]>([]);

// 用户统计数据
const userStats = ref<UserStats>({
  totalPosts: 0,
  totalLikes: 0,
  totalComments: 0,
  totalViews: 0
});

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    statsLoading.value = true;
    const response = await userApi.getUserStats();
    if (response && response.code === 200 && response.data) {
      userStats.value = response.data;
    }
  } catch (err: any) {
    console.error('获取统计数据失败:', err);
  } finally {
    statsLoading.value = false;
  }
};

// 获取用户最近帖子
const fetchRecentPosts = async () => {
  try {
    postsLoading.value = true;
    const response = await userApi.getUserRecentPosts(5);
    if (response && response.code === 200 && response.data) {
      userPosts.value = response.data.list;
    }
  } catch (err: any) {
    console.error('获取最近帖子失败:', err);
    userPosts.value = [];
  } finally {
    postsLoading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) return '今天';
  if (days === 1) return '昨天';
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
};

// 跳转到帖子详情
const goToPost = (postId: number) => {
  router.push(`/post/${postId}`);
};

// 编辑帖子
const editPost = (postId: number) => {
  router.push(`/edit-post/${postId}`);
};

// 删除帖子
const deletePost = async (postId: number) => {
  try {
    ElMessage.success('删除成功');
    await fetchRecentPosts();
  } catch (err: any) {
    ElMessage.error('删除失败');
  }
};

// 发布新帖
const createNewPost = () => {
  router.push('/create-post');
};

// 获取用户首字母
const getUserInitial = () => {
  return userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0) || 'U';
};

onMounted(async () => {
  try {
    loading.value = true;
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo();
    }
    await Promise.all([fetchUserStats(), fetchRecentPosts()]);
  } catch (err: any) {
    error.value = '加载数据失败';
    ElMessage.error('加载数据失败，请稍后重试');
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="home-page">
    <!-- 欢迎区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="user-welcome">
          <el-avatar 
            :size="80" 
            :src="userStore.userInfo?.avatar"
            class="user-avatar animate-scale-in"
          >
            {{ getUserInitial() }}
          </el-avatar>
          <div class="welcome-text">
            <h1 class="welcome-title animate-fade-in">
              欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}！
            </h1>
            <p class="welcome-subtitle animate-slide-up">
              今天也要加油哦～ 继续你的学习之旅吧
            </p>
          </div>
        </div>
        
        <div class="quick-actions animate-slide-up">
          <el-button 
            type="primary" 
            :icon="Plus" 
            size="large"
            @click="createNewPost"
            class="action-button"
          >
            发布帖子
          </el-button>
          <el-button 
            :icon="ChatDotRound" 
            size="large"
            @click="router.push('/forum')"
            class="action-button"
          >
            浏览论坛
          </el-button>
        </div>
      </div>
      
      <!-- 装饰性背景 -->
      <div class="hero-decoration">
        <div class="decoration-circle decoration-circle--1"></div>
        <div class="decoration-circle decoration-circle--2"></div>
        <div class="decoration-circle decoration-circle--3"></div>
      </div>
    </section>

    <!-- 统计数据 -->
    <section class="stats-section">
      <h2 class="section-title">数据概览</h2>
      <div class="stats-grid">
        <div class="stat-card animate-fade-in" style="animation-delay: 0.1s">
          <div class="stat-header">
            <div class="stat-icon stat-icon--primary">
              <el-icon><Document /></el-icon>
            </div>
            <el-badge value="new" v-if="userStats.totalPosts > 0" class="stat-badge">
              <span></span>
            </el-badge>
          </div>
          <div class="stat-content">
            <el-skeleton :loading="statsLoading" animated>
              <template #template>
                <el-skeleton-item variant="text" style="width: 40px; height: 32px;" />
                <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
              </template>
              <template #default>
                <div class="stat-value">{{ userStats.totalPosts }}</div>
                <div class="stat-label">发布帖子</div>
              </template>
            </el-skeleton>
          </div>
          <div class="stat-trend">
            <el-icon class="trend-icon trend-up"><TrendCharts /></el-icon>
            <span class="trend-text">+12%</span>
          </div>
        </div>
        
        <div class="stat-card animate-fade-in" style="animation-delay: 0.2s">
          <div class="stat-header">
            <div class="stat-icon stat-icon--success">
              <el-icon><Star /></el-icon>
            </div>
          </div>
          <div class="stat-content">
            <el-skeleton :loading="statsLoading" animated>
              <template #template>
                <el-skeleton-item variant="text" style="width: 40px; height: 32px;" />
                <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
              </template>
              <template #default>
                <div class="stat-value">{{ userStats.totalLikes }}</div>
                <div class="stat-label">获得点赞</div>
              </template>
            </el-skeleton>
          </div>
          <div class="stat-trend">
            <el-icon class="trend-icon trend-up"><TrendCharts /></el-icon>
            <span class="trend-text">+24%</span>
          </div>
        </div>
        
        <div class="stat-card animate-fade-in" style="animation-delay: 0.3s">
          <div class="stat-header">
            <div class="stat-icon stat-icon--warning">
              <el-icon><ChatDotRound /></el-icon>
            </div>
          </div>
          <div class="stat-content">
            <el-skeleton :loading="statsLoading" animated>
              <template #template>
                <el-skeleton-item variant="text" style="width: 40px; height: 32px;" />
                <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
              </template>
              <template #default>
                <div class="stat-value">{{ userStats.totalComments }}</div>
                <div class="stat-label">收到评论</div>
              </template>
            </el-skeleton>
          </div>
          <div class="stat-trend">
            <el-icon class="trend-icon trend-up"><TrendCharts /></el-icon>
            <span class="trend-text">+8%</span>
          </div>
        </div>
        
        <div class="stat-card animate-fade-in" style="animation-delay: 0.4s">
          <div class="stat-header">
            <div class="stat-icon stat-icon--info">
              <el-icon><View /></el-icon>
            </div>
          </div>
          <div class="stat-content">
            <el-skeleton :loading="statsLoading" animated>
              <template #template>
                <el-skeleton-item variant="text" style="width: 40px; height: 32px;" />
                <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
              </template>
              <template #default>
                <div class="stat-value">{{ userStats.totalViews }}</div>
                <div class="stat-label">帖子浏览</div>
              </template>
            </el-skeleton>
          </div>
          <div class="stat-trend">
            <el-icon class="trend-icon trend-up"><TrendCharts /></el-icon>
            <span class="trend-text">+18%</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 最近帖子 -->
    <section class="posts-section">
      <div class="section-header">
        <div class="header-content">
          <h2 class="section-title">最近动态</h2>
          <p class="section-subtitle">你最近发布的帖子和互动</p>
        </div>
        <el-button 
          type="primary" 
          :icon="Plus"
          @click="createNewPost"
          class="header-action"
        >
          发布新帖
        </el-button>
      </div>
      
      <div class="posts-container">
        <el-skeleton :loading="postsLoading" :rows="3" animated class="posts-skeleton" />
        
        <el-empty 
          v-if="!postsLoading && userPosts.length === 0" 
          description="还没有发布过帖子"
          class="posts-empty"
        >
          <el-button type="primary" @click="createNewPost">立即发布</el-button>
        </el-empty>
        
        <div v-if="!postsLoading && userPosts.length > 0" class="posts-list">
          <article 
            v-for="(post, index) in userPosts" 
            :key="post.id" 
            class="post-card animate-slide-up"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="post-content" @click="goToPost(post.id)">
              <div class="post-header">
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="post-meta">
                  <el-icon class="meta-icon"><Timer /></el-icon>
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                </div>
              </div>
              
              <p class="post-summary">
                {{ post.summary || post.content?.substring(0, 120) + '...' || '暂无摘要' }}
              </p>
              
              <div class="post-stats">
                <div class="stat-item">
                  <el-icon class="stat-icon"><Star /></el-icon>
                  <span class="stat-number">{{ post.likeCount || 0 }}</span>
                </div>
                
                <div class="stat-item">
                  <el-icon class="stat-icon"><ChatDotRound /></el-icon>
                  <span class="stat-number">{{ post.commentCount || 0 }}</span>
                </div>
                
                <div class="stat-item">
                  <el-icon class="stat-icon"><View /></el-icon>
                  <span class="stat-number">{{ post.viewCount || 0 }}</span>
                </div>
              </div>
            </div>
            
            <div class="post-actions">
              <el-button 
                link 
                type="primary" 
                :icon="Edit"
                @click.stop="editPost(post.id)"
                class="action-btn"
              >
                编辑
              </el-button>
              <el-button 
                link 
                type="danger" 
                :icon="Delete"
                @click.stop="deletePost(post.id)"
                class="action-btn"
              >
                删除
              </el-button>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: var(--space-6);
}

/* 欢迎区域 */
.hero-section {
  position: relative;
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--primary-100) 100%);
  border-radius: var(--radius-2xl);
  padding: var(--space-10) var(--space-8);
  margin-bottom: var(--space-8);
  overflow: hidden;
}

.hero-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: var(--content-max-width);
  margin: 0 auto;
}

.user-welcome {
  display: flex;
  align-items: center;
  gap: var(--space-6);
}

.user-avatar {
  box-shadow: var(--shadow-xl);
  border: 4px solid white;
}

.welcome-text {
  flex: 1;
}

.welcome-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  background: linear-gradient(135deg, var(--primary-700), var(--primary-500));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin: 0;
}

.quick-actions {
  display: flex;
  gap: var(--space-4);
}

.action-button {
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-200) var(--ease-out);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* 装饰性背景 */
.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  background: linear-gradient(135deg, var(--primary-300), var(--primary-200));
  border-radius: var(--radius-full);
  opacity: 0.3;
}

.decoration-circle--1 {
  width: 120px;
  height: 120px;
  top: -60px;
  right: 10%;
  animation: float 6s ease-in-out infinite;
}

.decoration-circle--2 {
  width: 80px;
  height: 80px;
  bottom: -40px;
  left: 20%;
  animation: float 4s ease-in-out infinite reverse;
}

.decoration-circle--3 {
  width: 160px;
  height: 160px;
  top: 50%;
  right: -80px;
  animation: float 8s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 区域标题 */
.section-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.section-subtitle {
  color: var(--text-secondary);
  margin: 0;
}

/* 统计数据区域 */
.stats-section {
  margin-bottom: var(--space-8);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-6);
  margin-top: var(--space-6);
}

.stat-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  position: relative;
  transition: all var(--duration-200) var(--ease-out);
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-500), var(--primary-300));
  transform: scaleX(0);
  transition: transform var(--duration-300) var(--ease-out);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-xl);
  color: white;
}

.stat-icon--primary { background: linear-gradient(135deg, var(--primary-600), var(--primary-400)); }
.stat-icon--success { background: linear-gradient(135deg, var(--success-600), var(--success-500)); }
.stat-icon--warning { background: linear-gradient(135deg, var(--warning-600), var(--warning-500)); }
.stat-icon--info { background: linear-gradient(135deg, #3b82f6, #60a5fa); }

.stat-content {
  margin-bottom: var(--space-3);
}

.stat-value {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.trend-icon {
  font-size: var(--font-size-sm);
}

.trend-up {
  color: var(--success-500);
}

.trend-text {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: var(--success-600);
}

/* 帖子区域 */
.posts-section {
  margin-bottom: var(--space-8);
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: var(--space-6);
}

.header-content {
  flex: 1;
}

.header-action {
  box-shadow: var(--shadow-sm);
}

.posts-container {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--border-light);
}

.posts-skeleton,
.posts-empty {
  padding: var(--space-8);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.post-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  transition: all var(--duration-200) var(--ease-out);
  position: relative;
  overflow: hidden;
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-500);
  transform: scaleY(0);
  transition: transform var(--duration-200) var(--ease-out);
}

.post-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateX(4px);
}

.post-card:hover::before {
  transform: scaleY(1);
}

.post-content {
  cursor: pointer;
  margin-bottom: var(--space-4);
}

.post-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.post-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin: 0;
  line-height: var(--line-height-snug);
  transition: color var(--duration-150) var(--ease-out);
}

.post-content:hover .post-title {
  color: var(--primary-600);
}

.post-meta {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.meta-icon {
  font-size: var(--font-size-xs);
}

.post-summary {
  color: var(--text-secondary);
  line-height: var(--line-height-relaxed);
  margin: 0 0 var(--space-4) 0;
}

.post-stats {
  display: flex;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-light);
  font-size: var(--font-size-sm);
}

.stat-icon {
  font-size: var(--font-size-sm);
}

.post-actions {
  display: flex;
  gap: var(--space-2);
  justify-content: flex-end;
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
}

.action-btn {
  font-size: var(--font-size-sm);
  padding: var(--space-1) var(--space-2);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-content {
    flex-direction: column;
    gap: var(--space-6);
    text-align: center;
  }
  
  .user-welcome {
    flex-direction: column;
    gap: var(--space-4);
  }
}

@media (max-width: 768px) {
  .home-page {
    padding: var(--space-4);
  }
  
  .hero-section {
    padding: var(--space-6);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
  
  .section-header {
    flex-direction: column;
    gap: var(--space-4);
    align-items: stretch;
  }
  
  .quick-actions {
    flex-direction: column;
  }
  
  .post-header {
    flex-direction: column;
    gap: var(--space-2);
  }
  
  .post-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .posts-container {
    padding: var(--space-4);
  }
  
  .welcome-title {
    font-size: var(--font-size-2xl);
  }
}
</style>

    font-size: var(--font-size-2xl);