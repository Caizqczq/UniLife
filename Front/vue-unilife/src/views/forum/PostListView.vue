<template>
  <div class="forum-page">
    <!-- 顶部横幅 -->
    <section class="forum-hero">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">UniLife 论坛</h1>
          <p class="hero-subtitle">分享知识，交流想法，共同成长</p>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">{{ postStore.totalPosts || 0 }}</span>
            <span class="stat-label">帖子总数</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ postStore.categories?.length || 0 }}</span>
            <span class="stat-label">分类数量</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 搜索和筛选区域 -->
    <section class="search-section">
      <div class="search-container">
        <div class="search-main">
          <div class="search-input-wrapper">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索帖子标题或内容..."
              @keyup.enter="handleSearch"
              @clear="clearSearch"
              clearable
              size="large"
              class="search-input"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
              <template #append>
                <el-button 
                  @click="handleSearch" 
                  :loading="searchLoading"
                  type="primary"
                  class="search-btn"
                >
                  搜索
                </el-button>
              </template>
            </el-input>
          </div>
          
          <div class="filter-controls">
            <el-select 
              v-model="selectedCategoryComputed" 
              placeholder="选择分类" 
              clearable
              @clear="clearCategorySelection"
              size="large"
              class="category-select"
            >
              <template #prefix>
                <el-icon><FolderOpened /></el-icon>
              </template>
              <el-option label="全部分类" value=""></el-option>
              <el-option
                v-for="category in postStore.categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              ></el-option>
            </el-select>
            
            <el-button 
              type="primary" 
              :icon="Edit"
              @click="createNewPost"
              size="large"
              class="create-post-btn"
            >
              发布帖子
            </el-button>
          </div>
        </div>
        
        <!-- 状态提示 -->
        <div class="status-alerts">
          <el-alert 
            v-if="postStore.loadingCategories" 
            title="正在加载分类..." 
            type="info" 
            :closable="false" 
            show-icon 
            class="status-alert"
          />
          <el-alert 
            v-if="postStore.errorCategories" 
            :title="`分类加载失败: ${postStore.errorCategories}`" 
            type="error" 
            :closable="false" 
            show-icon 
            class="status-alert"
          />
        </div>
      </div>
    </section>

    <!-- 帖子列表区域 -->
    <section class="posts-section">
      <!-- 加载状态 -->
      <div v-if="postStore.loading && postStore.posts.length === 0" class="loading-container">
        <div class="posts-skeleton">
          <div v-for="i in 5" :key="i" class="skeleton-card">
            <el-skeleton animated>
              <template #template>
                <div class="skeleton-header">
                  <el-skeleton-item variant="text" style="width: 60%; height: 24px;" />
                  <el-skeleton-item variant="text" style="width: 80px; height: 16px;" />
                </div>
                <el-skeleton-item variant="text" style="width: 100%; height: 16px; margin: 12px 0;" />
                <el-skeleton-item variant="text" style="width: 80%; height: 16px;" />
                <div class="skeleton-footer">
                  <el-skeleton-item variant="text" style="width: 120px; height: 14px;" />
                  <el-skeleton-item variant="text" style="width: 60px; height: 14px;" />
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="postStore.error && postStore.posts.length === 0" class="error-container">
        <el-empty description="加载失败，请稍后重试">
          <el-button type="primary" @click="postStore.fetchPosts({ pageNum: 1 })">
            重新加载
          </el-button>
        </el-empty>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!postStore.loading && postStore.posts.length === 0 && !postStore.error" class="empty-container">
        <el-empty description="暂无帖子，成为第一个发帖的人吧！">
          <el-button type="primary" @click="createNewPost">
            发布帖子
          </el-button>
        </el-empty>
      </div>

      <!-- 帖子列表 -->
      <div v-else class="posts-container">
        <div class="posts-grid">
          <article 
            v-for="(post, index) in postStore.posts" 
            :key="post.id" 
            class="post-card animate-fade-in"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="navigateToPostDetail(post.id)"
          >
            <!-- 帖子头部 -->
            <header class="post-header">
              <div class="post-category">
                <el-icon><FolderOpened /></el-icon>
                <span>{{ post.categoryName || '未分类' }}</span>
              </div>
              <div class="post-date">
                <el-icon><Clock /></el-icon>
                <span>{{ formatDate(post.createdAt) }}</span>
              </div>
            </header>

            <!-- 帖子内容 -->
            <div class="post-content">
              <h2 class="post-title">{{ post.title }}</h2>
              <p class="post-summary">{{ post.summary || '暂无摘要' }}</p>
            </div>

            <!-- 帖子底部 -->
            <footer class="post-footer">
              <div class="post-author">
                <el-avatar :size="32" class="author-avatar">
                  {{ post.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="author-name">{{ post.nickname || '匿名用户' }}</span>
              </div>
              
              <div class="post-stats">
                <div class="stat-item">
                  <el-icon class="stat-icon"><View /></el-icon>
                  <span class="stat-text">{{ post.viewCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon"><Pointer /></el-icon>
                  <span class="stat-text">{{ post.likeCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon class="stat-icon"><ChatDotRound /></el-icon>
                  <span class="stat-text">{{ post.commentCount || 0 }}</span>
                </div>
              </div>
            </footer>

            <!-- 点赞按钮 -->
            <div class="post-actions">
              <el-button 
                v-if="userStore.isLoggedIn"
                :type="post.isLiked ? 'primary' : ''"
                :loading="likingPostId === post.id"
                @click.stop="toggleLike(post)"
                class="like-btn"
                round
              >
                <el-icon><Pointer /></el-icon>
                <span>{{ post.isLiked ? '已点赞' : '点赞' }}</span>
              </el-button>
              <el-button 
                v-else 
                @click.stop="goLogin"
                class="like-btn"
                round
              >
                <el-icon><Pointer /></el-icon>
                <span>点赞</span>
              </el-button>
            </div>
          </article>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="postStore.totalPages > 1">
          <el-pagination
            background
            layout="sizes, prev, pager, next, jumper, ->, total"
            :total="postStore.totalPosts"
            :page-size="postStore.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            :current-page="postStore.currentPage"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
            class="pagination"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { usePostStore } from '@/stores/postStore';
import { useUserStore } from '@/stores';
import { ElMessage, ElIcon, ElSkeleton, ElAlert, ElPagination, ElEmpty, ElSelect, ElOption, ElButton, ElInput, ElAvatar } from 'element-plus';
import { User, FolderOpened, View, Pointer, ChatDotRound, Clock, Edit, Search } from '@element-plus/icons-vue';

const router = useRouter();
const postStore = usePostStore();
const userStore = useUserStore();

// 搜索相关变量
const searchKeyword = ref('');
const searchLoading = ref(false);

// 点赞状态
const likingPostId = ref<number | null>(null);

// 分类选择计算属性
const selectedCategoryComputed = computed({
  get: () => postStore.selectedCategoryId ?? "",
  set: (value) => {
    postStore.selectCategory(value === "" ? null : value);
  }
});

// 搜索帖子
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  
  searchLoading.value = true;
  try {
    router.push(`/search?keyword=${encodeURIComponent(searchKeyword.value)}&type=post`);
  } catch (error) {
    console.error('搜索失败:', error);
    ElMessage.error('搜索失败，请稍后重试');
  } finally {
    searchLoading.value = false;
  }
};

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = '';
};

// 清除分类选择
const clearCategorySelection = () => {
  postStore.selectCategory(null);
  postStore.fetchPosts({ pageNum: 1 });
};

// 格式化日期
const formatDate = (dateString?: string) => {
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

// 导航到帖子详情
const navigateToPostDetail = (postId: number) => {
  router.push({ name: 'PostDetail', params: { id: postId.toString() } });
};

// 创建新帖子
const createNewPost = () => {
  if (userStore.isLoggedIn) {
    router.push({ name: 'CreatePost' });
  } else {
    ElMessage.warning('请先登录');
    router.push({
      path: '/login',
      query: { redirect: '/create-post' }
    });
  }
};

// 点赞/取消点赞帖子
const toggleLike = async (post: any) => {
  likingPostId.value = post.id;
  try {
    await postStore.likePost(post.id);
  } finally {
    likingPostId.value = null;
  }
};

// 跳转到登录页面
const goLogin = () => {
  ElMessage.warning('请先登录');
  router.push({
    path: '/login',
    query: { redirect: router.currentRoute.value.fullPath }
  });
};

// 分页处理
const handleCurrentChange = (page: number) => {
  postStore.fetchPosts({ pageNum: page });
};

const handleSizeChange = (size: number) => {
  postStore.fetchPosts({ pageNum: 1, pageSize: size });
};

onMounted(async () => {
  await postStore.fetchCategories();
  postStore.fetchPosts({ pageNum: postStore.currentPage, pageSize: postStore.pageSize }); 
});
</script>

<style scoped>
.forum-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 顶部横幅 */
.forum-hero {
  background: linear-gradient(135deg, var(--primary-600) 0%, var(--primary-400) 100%);
  color: white;
  padding: var(--space-10) 0;
  position: relative;
  overflow: hidden;
}

.forum-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.hero-content {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 2;
}

.hero-text {
  flex: 1;
}

.hero-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--space-3) 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
  font-size: var(--font-size-xl);
  margin: 0;
  opacity: 0.9;
  font-weight: var(--font-weight-normal);
}

.hero-stats {
  display: flex;
  gap: var(--space-8);
}

.hero-stats .stat-item {
  text-align: center;
}

.hero-stats .stat-number {
  display: block;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  line-height: 1;
  margin-bottom: var(--space-1);
}

.hero-stats .stat-label {
  font-size: var(--font-size-sm);
  opacity: 0.8;
}

/* 搜索区域 */
.search-section {
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 72px;
  z-index: var(--z-sticky);
}

.search-container {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: var(--space-6);
}

.search-main {
  display: flex;
  gap: var(--space-4);
  align-items: center;
}

.search-input-wrapper {
  flex: 1;
  max-width: 600px;
}

.search-input {
  box-shadow: var(--shadow-sm);
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-xl);
  padding: var(--space-2) var(--space-4);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: all var(--duration-150) var(--ease-out);
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: var(--shadow-md);
  border-color: var(--primary-300);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(139, 77, 255, 0.1), var(--shadow-md);
  border-color: var(--primary-500);
}

.search-btn {
  border-radius: var(--radius-lg);
  padding: var(--space-3) var(--space-5);
  font-weight: var(--font-weight-medium);
}

.filter-controls {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.category-select {
  min-width: 200px;
}

.category-select :deep(.el-input__wrapper) {
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.create-post-btn {
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-weight: var(--font-weight-medium);
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-150) var(--ease-out);
}

.create-post-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.status-alerts {
  margin-top: var(--space-4);
}

.status-alert {
  border-radius: var(--radius-lg);
}

/* 帖子列表区域 */
.posts-section {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: var(--space-8) var(--space-6);
}

.loading-container,
.error-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.posts-skeleton {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.skeleton-card {
  background: var(--bg-elevated);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--border-light);
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-4);
}

/* 帖子卡片网格 */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.post-card {
  background: var(--bg-elevated);
  border: 2px solid var(--border-light);
  border-radius: var(--radius-xl);
  padding: 0;
  cursor: pointer;
  transition: all var(--duration-200) var(--ease-out);
  position: relative;
  overflow: hidden;
  height: fit-content;
  box-shadow: var(--shadow-md);
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-500), var(--primary-300));
  transform: scaleX(0);
  transition: transform var(--duration-300) var(--ease-out);
}

.post-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, transparent 0%, rgba(139, 77, 255, 0.03) 100%);
  opacity: 0;
  transition: opacity var(--duration-200) var(--ease-out);
}

.post-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-2xl);
  border-color: var(--primary-200);
}

.post-card:hover::before {
  transform: scaleX(1);
}

.post-card:hover::after {
  opacity: 1;
}

/* 帖子头部 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5) var(--space-6) var(--space-3) var(--space-6);
  background: var(--neutral-50);
  border-bottom: 1px solid var(--border-light);
  position: relative;
  z-index: 2;
}

.post-category {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  background: linear-gradient(135deg, var(--primary-500), var(--primary-400));
  color: white;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  box-shadow: var(--shadow-sm);
}

.post-date {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-light);
  font-size: var(--font-size-xs);
  background: var(--neutral-100);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
}

/* 帖子内容 */
.post-content {
  padding: var(--space-5) var(--space-6);
  position: relative;
  z-index: 2;
  background: var(--bg-elevated);
}

.post-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
  line-height: var(--line-height-snug);
  transition: color var(--duration-150) var(--ease-out);
}

.post-card:hover .post-title {
  color: var(--primary-600);
}

.post-summary {
  color: var(--text-secondary);
  line-height: var(--line-height-relaxed);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  position: relative;
}

.post-summary::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  width: 30px;
  height: 20px;
  background: linear-gradient(90deg, transparent, var(--bg-elevated));
}

/* 帖子底部 */
.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-6);
  background: var(--neutral-25);
  border-top: 1px solid var(--border-light);
  position: relative;
  z-index: 2;
}

.post-author {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  background: white;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xs);
}

.author-avatar {
  box-shadow: var(--shadow-sm);
  border: 2px solid var(--primary-100);
}

.author-name {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
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
  font-size: var(--font-size-xs);
  background: white;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: all var(--duration-150) var(--ease-out);
}

.stat-item:hover {
  background: var(--primary-50);
  color: var(--primary-600);
}

.stat-icon {
  font-size: var(--font-size-sm);
}

/* 帖子操作 */
.post-actions {
  display: flex;
  justify-content: center;
  padding: var(--space-4) var(--space-6) var(--space-5) var(--space-6);
  background: var(--bg-elevated);
  position: relative;
  z-index: 2;
}

.like-btn {
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  transition: all var(--duration-150) var(--ease-out);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.like-btn:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-md);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: var(--space-8);
}

.pagination {
  background: var(--bg-elevated);
  padding: var(--space-4);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .posts-grid {
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: var(--space-5);
  }
  
  .hero-content {
    flex-direction: column;
    gap: var(--space-6);
    text-align: center;
  }
  
  .hero-stats {
    gap: var(--space-6);
  }
}

@media (max-width: 768px) {
  .forum-hero {
    padding: var(--space-8) 0;
  }
  
  .hero-title {
    font-size: var(--font-size-3xl);
  }
  
  .hero-subtitle {
    font-size: var(--font-size-lg);
  }
  
  .search-main {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .search-input-wrapper {
    max-width: none;
  }
  
  .filter-controls {
    width: 100%;
    justify-content: space-between;
  }
  
  .category-select {
    flex: 1;
    margin-right: var(--space-3);
  }
  
  .posts-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
  
  .hero-stats {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .hero-stats .stat-item {
    display: flex;
    align-items: center;
    gap: var(--space-3);
  }
  
  .hero-stats .stat-number {
    margin-bottom: 0;
  }
}

@media (max-width: 480px) {
  .posts-section {
    padding: var(--space-6) var(--space-4);
  }
  
  .search-container {
    padding: var(--space-4);
  }
  
  .post-card {
    padding: var(--space-4);
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .category-select {
    margin-right: 0;
    margin-bottom: var(--space-3);
  }
}
</style>
