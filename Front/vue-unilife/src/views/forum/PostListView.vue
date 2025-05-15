<template>
  <div class="post-list-view">
    <el-card class="filter-card" shadow="never">
      <div class="filter-controls">
        <div class="filter-left">
          <el-select 
            v-model="selectedCategoryComputed" 
            placeholder="选择分类" 
            clearable
            @clear="clearCategorySelection"
            style="width: 240px;"
          >
            <el-option label="全部分类" :value="null"></el-option>
            <el-option
              v-for="category in postStore.categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
          <el-alert v-if="postStore.loadingCategories" title="正在加载分类..." type="info" :closable="false" show-icon class="status-alert"></el-alert>
          <el-alert v-if="postStore.errorCategories" :title="`分类加载失败: ${postStore.errorCategories}`" type="error" :closable="false" show-icon class="status-alert"></el-alert>
        </div>
        
        <div class="filter-right">
          <el-button 
            type="primary" 
            :icon="Edit"
            @click="createNewPost"
          >
            发布帖子
          </el-button>
        </div>
      </div>
    </el-card>

    <el-skeleton :rows="5" animated v-if="postStore.loading && postStore.posts.length === 0" />

    <el-alert
      v-if="postStore.error && postStore.posts.length === 0"
      :title="`获取帖子列表失败: ${postStore.error}`"
      type="error"
      show-icon
      :closable="false"
    />

    <div v-if="!postStore.loading && postStore.posts.length === 0 && !postStore.error" class="empty-state">
      <el-empty description="暂无帖子，敬请期待！"></el-empty>
    </div>

    <el-row :gutter="20" v-if="postStore.posts.length > 0">
      <el-col :span="24" v-for="post in postStore.posts" :key="post.id" class="post-item-col">
        <el-card class="post-item-card" shadow="hover" @click="navigateToPostDetail(post.id)">
          <template #header>
            <div class="post-title">{{ post.title }}</div>
          </template>
          <div class="post-summary">{{ post.summary || '暂无摘要' }}</div>
          <el-divider></el-divider>
          <div class="post-meta">
            <span><el-icon><User /></el-icon> {{ post.nickname }}</span>
            <span><el-icon><FolderOpened /></el-icon> {{ post.categoryName }}</span>
            <span><el-icon><View /></el-icon> {{ post.viewCount }}</span>
            <span><el-icon><Pointer /></el-icon> {{ post.likeCount }}</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ post.commentCount }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatDate(post.createdAt) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="pagination-container" v-if="postStore.totalPages > 1">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :total="postStore.totalPosts"
        :page-size="postStore.pageSize"
        :current-page="postStore.currentPage"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { usePostStore } from '@/stores/postStore';
import { useUserStore } from '@/stores';
import { ElMessage, ElIcon, ElCard, ElSkeleton, ElAlert, ElRow, ElCol, ElDivider, ElPagination, ElEmpty, ElSelect, ElOption, ElButton } from 'element-plus';
import { User, FolderOpened, View, Pointer, ChatDotRound, Clock, Edit } from '@element-plus/icons-vue';

const router = useRouter();
const postStore = usePostStore();
const userStore = useUserStore();

// Computed property to two-way bind el-select with store's selectedCategoryId
// and trigger store action on change.
const selectedCategoryComputed = computed({
  get: () => postStore.selectedCategoryId,
  set: (value) => {
    // 将分类值传递给store的selectCategory方法
    console.log('选择分类:', value);
    postStore.selectCategory(value);
  }
});

const clearCategorySelection = () => {
  // 手动强制清除分类并重新获取帖子
  postStore.selectCategory(null);
  postStore.fetchPosts({ pageNum: 1 });
  console.log('清除分类选择');
};

const formatDate = (dateString?: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

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

const handleCurrentChange = (page: number) => {
  postStore.fetchPosts({ pageNum: page });
};

onMounted(async () => {
  await postStore.fetchCategories(); // Fetch categories first
  // Fetch posts, it will use the default selectedCategoryId (null) from store initially
  // or if persisted from a previous session if store has persistence.
  postStore.fetchPosts({ pageNum: postStore.currentPage, pageSize: postStore.pageSize }); 
});

</script>

<style scoped>
.post-list-view {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 20px;
  padding: 10px; /* Reduced padding for a more compact look */
}

.filter-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.filter-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.status-alert {
  flex-grow: 1;
  margin-left: 20px;
}

.post-item-col {
  margin-bottom: 20px;
}

.post-item-card {
  cursor: pointer;
  transition: box-shadow 0.3s ease-in-out;
}

.post-item-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.post-title {
  font-size: 1.4em;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.post-summary {
  font-size: 0.95em;
  color: var(--el-text-color-regular);
  line-height: 1.6;
  min-height: 40px; /* Ensure some space even if summary is short */
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 20px; /* row-gap column-gap */
  font-size: 0.85em;
  color: var(--el-text-color-secondary);
  align-items: center;
}

.post-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px; /* Give some height to the empty state */
}
</style>
