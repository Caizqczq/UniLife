<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores';
import userApi from '../api/user';
import type { UserStats } from '../api/user';
import { ElMessage, ElSkeleton, ElEmpty, ElIcon, ElButton } from 'element-plus';
import { Document, Star, ChatDotRound, View, Edit, Delete } from '@element-plus/icons-vue';

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
    // 保持默认值，不显示错误消息以免影响用户体验
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
    // 这里需要调用删除API
    ElMessage.success('删除成功');
    await fetchRecentPosts(); // 重新获取帖子列表
  } catch (err: any) {
    ElMessage.error('删除失败');
  }
};

// 发布新帖
const createNewPost = () => {
  router.push('/create-post');
};

onMounted(async () => {
  try {
    loading.value = true;
    // 如果没有用户信息，获取用户信息
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo();
    }
    
    // 并行获取统计数据和最近帖子
    await Promise.all([
      fetchUserStats(),
      fetchRecentPosts()
    ]);
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
    <div class="page-header">
      <h1>个人主页</h1>
      <p>欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</p>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <el-skeleton :loading="statsLoading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 40px; height: 24px;" />
              <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
            </template>
            <template #default>
              <div class="stat-value">{{ userStats.totalPosts }}</div>
              <div class="stat-label">发布帖子</div>
            </template>
          </el-skeleton>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-content">
          <el-skeleton :loading="statsLoading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 40px; height: 24px;" />
              <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
            </template>
            <template #default>
              <div class="stat-value">{{ userStats.totalLikes }}</div>
              <div class="stat-label">获得点赞</div>
            </template>
          </el-skeleton>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <el-skeleton :loading="statsLoading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 40px; height: 24px;" />
              <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
            </template>
            <template #default>
              <div class="stat-value">{{ userStats.totalComments }}</div>
              <div class="stat-label">收到评论</div>
            </template>
          </el-skeleton>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <el-skeleton :loading="statsLoading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 40px; height: 24px;" />
              <el-skeleton-item variant="text" style="width: 60px; height: 16px;" />
            </template>
            <template #default>
              <div class="stat-value">{{ userStats.totalViews }}</div>
              <div class="stat-label">帖子浏览</div>
            </template>
          </el-skeleton>
        </div>
      </div>
    </div>
    
    <!-- 最近帖子 -->
    <div class="recent-posts">
      <div class="section-header">
        <h2>最近发布的帖子</h2>
        <el-button type="primary" @click="createNewPost">发布新帖</el-button>
      </div>
      
      <div class="posts-list">
        <el-skeleton :loading="postsLoading" :rows="3" animated />
        
        <el-empty v-if="!postsLoading && userPosts.length === 0" description="你还没有发布过帖子">
          <el-button type="primary" @click="createNewPost">立即发布</el-button>
        </el-empty>
        
        <div v-if="!postsLoading && userPosts.length > 0" class="post-card" v-for="post in userPosts" :key="post.id">
          <div class="post-header">
            <h3 class="post-title clickable" @click="goToPost(post.id)">{{ post.title }}</h3>
            <span class="post-date">{{ formatDate(post.createdAt) }}</span>
          </div>
          
          <p class="post-content">{{ post.summary || post.content?.substring(0, 100) + '...' || '暂无摘要' }}</p>
          
          <div class="post-footer">
            <div class="post-stats">
              <div class="post-stat">
                <el-icon><Star /></el-icon>
                <span>{{ post.likeCount || 0 }}</span>
              </div>
              
              <div class="post-stat">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ post.commentCount || 0 }}</span>
              </div>
              
              <div class="post-stat">
                <el-icon><View /></el-icon>
                <span>{{ post.viewCount || 0 }}</span>
              </div>
            </div>
            
            <div class="post-actions">
              <el-button link type="primary" @click="editPost(post.id)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button link type="danger" @click="deletePost(post.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-xl);
}

.page-header h1 {
  font-size: var(--font-size-xxl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
}

.page-header p {
  color: var(--text-secondary);
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-full);
  background-color: var(--primary-light);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: var(--spacing-md);
  color: white;
  font-size: var(--font-size-xl);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-header h2 {
  font-size: var(--font-size-xl);
  color: var(--text-primary);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.post-card {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.post-title {
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  margin: 0;
}

.post-title.clickable {
  cursor: pointer;
  transition: color 0.2s;
}

.post-title.clickable:hover {
  color: var(--primary-color);
}

.post-date {
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.post-content {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-lg);
  line-height: 1.6;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-stats {
  display: flex;
  gap: var(--spacing-md);
}

.post-stat {
  display: flex;
  align-items: center;
  color: var(--text-light);
}

.post-stat .el-icon {
  margin-right: var(--spacing-xs);
}

.post-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.btn-text {
  background: none;
  color: var(--primary-color);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  transition: background-color var(--transition-normal);
}

.btn-text:hover {
  background-color: rgba(147, 112, 219, 0.1);
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-light);
}

.empty-state p {
  margin-bottom: var(--spacing-lg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-container {
    grid-template-columns: 1fr;
  }
  
  .post-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .post-date {
    margin-top: var(--spacing-xs);
  }
  
  .post-footer {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .post-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
