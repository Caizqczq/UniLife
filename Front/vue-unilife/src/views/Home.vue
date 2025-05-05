<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useUserStore } from '../stores';

const userStore = useUserStore();

// 用户帖子数据（模拟数据）
const userPosts = ref([
  {
    id: 1,
    title: '大学生活经验分享',
    content: '分享一些大学生活的经验和技巧...',
    createTime: '2023-05-01',
    likes: 25,
    comments: 10
  },
  {
    id: 2,
    title: '考研复习计划',
    content: '分享我的考研复习计划和方法...',
    createTime: '2023-05-15',
    likes: 42,
    comments: 18
  },
  {
    id: 3,
    title: '校园活动推荐',
    content: '推荐几个值得参加的校园活动...',
    createTime: '2023-06-01',
    likes: 15,
    comments: 5
  }
]);

// 用户统计数据
const userStats = ref({
  totalPosts: 3,
  totalLikes: 82,
  totalComments: 33,
  totalViews: 256
});

onMounted(async () => {
  // 如果没有用户信息，获取用户信息
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo();
  }
  
  // 这里可以添加获取用户帖子和统计数据的API调用
});
</script>

<template>
  <div class="home-page">
    <div class="page-header">
      <h1>个人主页</h1>
      <p>欢迎回来，{{ userStore.userInfo?.username || '用户' }}</p>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userStats.totalPosts }}</div>
          <div class="stat-label">发布帖子</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userStats.totalLikes }}</div>
          <div class="stat-label">获得点赞</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><chat-dot-round /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userStats.totalComments }}</div>
          <div class="stat-label">收到评论</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><view /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userStats.totalViews }}</div>
          <div class="stat-label">帖子浏览</div>
        </div>
      </div>
    </div>
    
    <!-- 最近帖子 -->
    <div class="recent-posts">
      <div class="section-header">
        <h2>最近发布的帖子</h2>
        <button class="btn btn-primary">发布新帖</button>
      </div>
      
      <div class="posts-list">
        <div v-if="userPosts.length === 0" class="empty-state">
          <p>你还没有发布过帖子</p>
          <button class="btn btn-primary">立即发布</button>
        </div>
        
        <div v-else class="post-card" v-for="post in userPosts" :key="post.id">
          <div class="post-header">
            <h3 class="post-title">{{ post.title }}</h3>
            <span class="post-date">{{ post.createTime }}</span>
          </div>
          
          <p class="post-content">{{ post.content }}</p>
          
          <div class="post-footer">
            <div class="post-stats">
              <div class="post-stat">
                <el-icon><star /></el-icon>
                <span>{{ post.likes }}</span>
              </div>
              
              <div class="post-stat">
                <el-icon><chat-dot-round /></el-icon>
                <span>{{ post.comments }}</span>
              </div>
            </div>
            
            <div class="post-actions">
              <button class="btn btn-text">编辑</button>
              <button class="btn btn-text">删除</button>
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
