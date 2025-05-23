<template>
  <div class="post-detail-view">
    <el-button @click="goBack" class="back-button" :icon="ArrowLeft">返回列表</el-button>

    <el-skeleton :rows="8" animated v-if="postStore.loading && !postStore.currentPost" />

    <el-alert
      v-if="postStore.error && !postStore.currentPost"
      :title="`获取帖子详情失败: ${postStore.error}`"
      type="error"
      show-icon
      :closable="false"
    />

    <el-card v-if="postStore.currentPost" class="post-content-card">
      <template #header>
        <h1>{{ postStore.currentPost.title }}</h1>
        <div class="post-meta-detail">
          <span>作者: {{ postStore.currentPost.nickname }}</span>
          <span>分类: {{ postStore.currentPost.categoryName }}</span>
          <span>发布于: {{ formatDate(postStore.currentPost.createdAt) }}</span>
          <span v-if="postStore.currentPost.updatedAt && postStore.currentPost.updatedAt !== postStore.currentPost.createdAt">
            更新于: {{ formatDate(postStore.currentPost.updatedAt) }}
          </span>
        </div>
      </template>
      
      <div class="post-body" v-html="postStore.currentPost.content"></div>

      <el-divider />
      <div class="post-stats-actions">
        <div class="post-stats">
          <span><el-icon><View /></el-icon> {{ postStore.currentPost.viewCount }} 次浏览</span>
          <span><el-icon><Pointer /></el-icon> {{ postStore.currentPost.likeCount }} 个点赞</span>
          <span><el-icon><ChatDotRound /></el-icon> {{ postStore.currentPost.commentCount }} 条评论</span>
        </div>
        <div class="post-actions">
          <el-button 
            v-if="userStore.isLoggedIn"
            :type="postStore.currentPost.isLiked ? 'primary' : ''"
            :loading="likingPost"
            @click="toggleLike"
          >
            <el-icon><Pointer /></el-icon>
            {{ postStore.currentPost.isLiked ? '已点赞' : '点赞' }}
          </el-button>
          <el-button v-else @click="goLogin">
            <el-icon><Pointer /></el-icon>
            点赞
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 评论区组件 -->
    <el-card class="comments-section" v-if="postStore.currentPost">
      <CommentSection :post-id="postStore.currentPost.id" />
    </el-card>

  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { usePostStore } from '@/stores/postStore';
import { useUserStore } from '@/stores';
import { ElMessage, ElIcon, ElButton, ElCard, ElSkeleton, ElAlert, ElDivider } from 'element-plus';
import { View, Pointer, ChatDotRound, ArrowLeft } from '@element-plus/icons-vue';
import CommentSection from '@/components/CommentSection.vue';

const route = useRoute();
const router = useRouter();
const postStore = usePostStore();
const userStore = useUserStore();

// 点赞状态
const likingPost = ref(false);

const formatDate = (dateString?: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

const goBack = () => {
  router.push('/'); // 返回到论坛首页
};

// 点赞/取消点赞帖子
const toggleLike = async () => {
  if (!postStore.currentPost) return;
  
  likingPost.value = true;
  try {
    await postStore.likePost(postStore.currentPost.id);
  } finally {
    likingPost.value = false;
  }
};

// 跳转到登录页面
const goLogin = () => {
  router.push({
    path: '/login',
    query: { redirect: route.fullPath }
  });
};

const loadPostDetails = (id: string | number) => {
  const postId = Number(id);
  if (isNaN(postId)) {
    ElMessage.error('无效的帖子ID');
    router.push('/forum'); // Redirect if ID is invalid
    return;
  }
  postStore.fetchPostDetail(postId);
};

// Fetch post details when the component is mounted and when the route param changes
onMounted(() => {
  if (route.params.id) {
    loadPostDetails(route.params.id as string);
  }
});

watch(
  () => route.params.id,
  (newId) => {
    if (newId && newId !== postStore.currentPost?.id?.toString()) {
      loadPostDetails(newId as string);
    }
  }
);

</script>

<style scoped>
.post-detail-view {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.back-button {
  margin-bottom: 20px;
}

.post-content-card {
  margin-bottom: 20px;
}

h1 {
  font-size: 2em;
  margin-bottom: 10px;
}

.post-meta-detail {
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.post-body {
  line-height: 1.8;
  color: var(--el-text-color-primary);
  /* Add more styling if content is rich text (e.g., from a WYSIWYG editor) */
}

.post-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

.post-stats-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.post-stats {
  display: flex;
  gap: 20px;
  align-items: center;
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.post-actions {
  display: flex;
  gap: 10px;
}

.comments-section {
  margin-top: 30px;
}
</style>
