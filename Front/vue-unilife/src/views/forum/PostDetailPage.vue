<template>
  <div class="post-detail-page">
    <el-skeleton :rows="12" animated v-if="loading" />

    <el-card v-if="!loading && post" class="post-card">
      <template #header>
        <div class="post-header-title">
          <h1>{{ post.title }}</h1>
          <div class="author-actions">
            <el-button
              v-if="isAuthor"
              type="primary"
              plain
              size="small"
              :icon="Edit"
              @click="goToEditPage"
            >
              Edit
            </el-button>
            <el-popconfirm
              v-if="isAuthor"
              title="Are you sure you want to delete this post?"
              confirm-button-text="Yes, Delete"
              cancel-button-text="No"
              @confirm="deletePost"
            >
              <template #reference>
                <el-button type="danger" plain size="small" :icon="Delete" :loading="deleting">
                  Delete
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
        <div class="post-meta">
          <div class="author-info">
            <el-avatar :size="30" :src="post.user?.avatar || defaultAvatar" class="author-avatar" />
            <span class="author-name">{{ post.user?.nickname || post.user?.username || 'Unknown Author' }}</span>
          </div>
          <span class="meta-item"><el-icon><Calendar /></el-icon> {{ formatDate(post.createdAt) }}</span>
          <span class="meta-item"><el-icon><View /></el-icon> {{ post.viewCount || 0 }} views</span>
        </div>
      </template>

      <div class="post-content" v-html="purifiedPostContent"></div>

      <div class="post-actions">
        <el-button
          type="danger"
          plain
          @click="togglePostLike"
          :loading="likeLoading"
          :icon="Pointer"
          :class="{ 'liked': isLikedByCurrentUser }"
        >
          {{ isLikedByCurrentUser ? 'Unlike' : 'Like' }} ({{ localLikeCount }})
        </el-button>
        <!-- Add other actions like share, report if needed -->
      </div>
    </el-card>

    <el-alert
        v-if="!loading && error"
        :title="error"
        type="error"
        show-icon
        :closable="false"
        style="margin-top: 20px;"
    />
    
    <el-empty v-if="!loading && !post && !error" description="Post not found." style="margin-top: 20px;" />

    <CommentSection v-if="!loading && post" :post-id="post.id" class="comment-section-container" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElCard, ElAvatar, ElButton, ElIcon, ElSkeleton, ElPopconfirm, ElAlert, ElEmpty } from 'element-plus';
import { Calendar, View, Pointer, Edit, Delete } from '@element-plus/icons-vue';
import CommentSection from '../../components/forum/CommentSection.vue';
import { useUserStore } from '../../stores/user';
import DOMPurify from 'dompurify';

interface PostUser {
  id: number;
  username: string;
  nickname?: string;
  avatar?: string;
}

interface Post {
  id: number;
  title: string;
  content: string;
  categoryId: number;
  categoryName?: string;
  userId: number;
  user?: PostUser;
  viewCount: number;
  likeCount: number;
  isLiked?: boolean; // Important: Assuming API provides this for the current user
  createdAt: string;
  updatedAt: string;
}

export default defineComponent({
  name: 'PostDetailPage',
  components: {
    CommentSection,
    ElCard, ElAvatar, ElButton, ElIcon, ElSkeleton, ElPopconfirm, ElAlert, ElEmpty,
    Calendar, View, Pointer, Edit, Delete,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const authStore = useUserStore();

    const post = ref<Post | null>(null);
    const loading = ref(true);
    const error = ref<string | null>(null);
    const likeLoading = ref(false);
    const deleting = ref(false);

    const localLikeCount = ref(0); // To manage like count reactively
    const isLikedByCurrentUser = ref(false); // To manage like status reactively

    const defaultAvatar = '/img/default-avatar.png'; // Path to your default avatar

    const postId = computed(() => Number(route.params.postId as string));

    const isAuthor = computed(() => {
      return authStore.user && post.value && authStore.user.id === post.value.userId;
    });

    const purifiedPostContent = computed(() => {
      if (post.value && post.value.content) {
        return DOMPurify.sanitize(post.value.content, { USE_PROFILES: { html: true } });
      }
      return '';
    });

    const fetchPostDetails = async () => {
      loading.value = true;
      error.value = null;
      post.value = null;
      try {
        // Increment view count first (fire and forget, or handle error if critical)
        // Backend should ideally handle view increment on GET /api/posts/{postId} itself to avoid race conditions
        // and ensure it's counted accurately. If client must do it:
        try {
          await axios.post(`/api/posts/${postId.value}/view`);
        } catch (viewError) {
            console.warn("Could not increment view count or already counted:", viewError);
        }


        const response = await axios.get(`/api/posts/${postId.value}`);
        if (response.data && response.data.code === 200 && response.data.data) {
          post.value = response.data.data;
          localLikeCount.value = post.value?.likeCount || 0;
          // The GET /api/posts/{postId} response MUST include an `isLiked` field (boolean)
          // for the current authenticated user for this to work correctly.
          isLikedByCurrentUser.value = post.value?.isLiked || false;
        } else {
          throw new Error(response.data.message || 'Post not found or failed to load.');
        }
      } catch (err: any) {
        console.error("Fetch post details error:", err);
        error.value = err.response?.data?.message || err.message || 'Could not load the post.';
        if (err.response?.status === 404) {
            error.value = "Post not found.";
        }
      } finally {
        loading.value = false;
      }
    };

    const togglePostLike = async () => {
      if (!authStore.isLoggedIn) {
        ElMessage.warning('Please login to like posts.');
        router.push({ name: 'Login', query: { redirect: route.fullPath } });
        return;
      }
      if (!post.value) return;

      likeLoading.value = true;
      try {
        let response;
        if (isLikedByCurrentUser.value) {
          // Currently liked, so unlike
          response = await axios.delete(`/api/posts/${post.value.id}/like`);
          if (response.data && response.data.code === 200) {
            isLikedByCurrentUser.value = false;
            localLikeCount.value = Math.max(0, localLikeCount.value - 1);
            ElMessage.success('Post unliked!');
          }
        } else {
          // Currently not liked, so like
          response = await axios.post(`/api/posts/${post.value.id}/like`);
           if (response.data && response.data.code === 200) {
            isLikedByCurrentUser.value = true;
            localLikeCount.value++;
            ElMessage.success('Post liked!');
          }
        }
         if (!response || response.data.code !== 200) {
           ElMessage.error(response?.data?.message || `Failed to ${isLikedByCurrentUser.value ? 'like' : 'unlike'} post.`);
         }

      } catch (err: any) {
        ElMessage.error(err.response?.data?.message || 'Error updating like status.');
        console.error("Toggle like error:", err);
      } finally {
        likeLoading.value = false;
      }
    };

    const goToEditPage = () => {
      if (post.value) {
        router.push({ name: 'PostEdit', params: { postId: post.value.id } });
      }
    };

    const deletePost = async () => {
      if (!post.value) return;
      deleting.value = true;
      try {
        const response = await axios.delete(`/api/posts/${post.value.id}`);
        if (response.data && response.data.code === 200) {
          ElMessage.success('Post deleted successfully.');
          router.push({ name: 'PostList' }); // Or wherever appropriate after deletion
        } else {
           ElMessage.error(response.data.message || 'Failed to delete post.');
        }
      } catch (err: any) {
        ElMessage.error(err.response?.data?.message || 'Error deleting post.');
        console.error("Delete post error:", err);
      } finally {
        deleting.value = false;
      }
    };

    const formatDate = (dateString: string) => {
      if (!dateString) return 'N/A';
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    onMounted(() => {
      fetchPostDetails();
    });

    // Watch for route changes if navigating between posts directly
    watch(() => route.params.postId, (newPostId, oldPostId) => {
      if (newPostId && newPostId !== oldPostId) {
        fetchPostDetails();
      }
    });

    return {
      post,
      loading,
      error,
      isAuthor,
      defaultAvatar,
      formatDate,
      purifiedPostContent,
      goToEditPage,
      deletePost,
      deleting,
      togglePostLike,
      likeLoading,
      localLikeCount,
      isLikedByCurrentUser,
      // Icons
      Calendar, View, Pointer, Edit, Delete,
    };
  },
});
</script>

<style scoped>
.post-detail-page {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}

.post-card {
  border-radius: 8px;
}

.post-header-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.post-header-title h1 {
  font-size: 2em;
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
  word-break: break-word; /* Break long titles */
  flex-grow: 1;
  padding-right: 15px; /* Space before buttons */
}
.author-actions .el-button {
    margin-left: 10px;
}


.post-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap; /* Allow meta items to wrap on smaller screens */
  gap: 15px; /* Spacing between meta items */
  color: #606266;
  font-size: 0.9em;
  margin-bottom: 20px; /* Space before content */
}
.author-info {
  display: flex;
  align-items: center;
}
.author-avatar {
  margin-right: 8px;
}
.author-name {
  font-weight: 500;
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.post-content {
  margin-top: 20px;
  margin-bottom: 30px;
  font-size: 1.1em;
  line-height: 1.8;
  color: #333;
  word-wrap: break-word; /* Ensure long words do not overflow */
}

/* Styles for HTML content from rich text editor */
.post-content :deep(p) {
  margin-bottom: 1em;
}
.post-content :deep(h1),
.post-content :deep(h2),
.post-content :deep(h3),
.post-content :deep(h4),
.post-content :deep(h5) {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
}
.post-content :deep(ul),
.post-content :deep(ol) {
  margin-bottom: 1em;
  padding-left: 1.5em;
}
.post-content :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid #e0e0e0;
  background-color: #f9f9f9;
  color: #666;
}
.post-content :deep(pre) {
  background-color: #f5f5f5;
  padding: 1em;
  border-radius: 4px;
  overflow-x: auto; /* Allow horizontal scrolling for code blocks */
  margin-bottom: 1em;
}
.post-content :deep(code) {
  font-family: 'Courier New', Courier, monospace;
  background-color: #f0f0f0;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-size: 0.95em;
}
.post-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
  border-radius: 0;
  font-size: inherit;
}
.post-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 1em 0;
}
.post-content :deep(a) {
  color: var(--el-color-primary);
  text-decoration: none;
}
.post-content :deep(a:hover) {
  text-decoration: underline;
}


.post-actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 10px;
}
.post-actions .el-button.liked {
  background-color: var(--el-color-danger-light-8);
  border-color: var(--el-color-danger-light-5);
  color: var(--el-color-danger);
}

.comment-section-container {
  margin-top: 30px;
}
</style>
