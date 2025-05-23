<template>
  <div class="comment-section">
    <div class="comment-section-header">
      <h3>评论区 ({{ commentTotal }})</h3>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-form" v-if="userStore.isLoggedIn">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <div class="comment-form-actions">
        <el-button 
          type="primary" 
          @click="submitComment"
          :loading="submittingComment"
          :disabled="!commentContent.trim()"
        >
          发表评论
        </el-button>
      </div>
    </div>

    <div class="login-prompt" v-else>
      <el-alert
        title="请登录后发表评论"
        type="info"
        show-icon
        :closable="false"
      >
        <template #default>
          <el-button type="primary" size="small" @click="goLogin">立即登录</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <el-skeleton :rows="3" animated v-if="loading && comments.length === 0" />
      
      <el-alert
        v-if="error && comments.length === 0"
        :title="`加载评论失败: ${error}`"
        type="error"
        show-icon
        :closable="false"
      />

      <div v-if="!loading && comments.length === 0 && !error" class="empty-comments">
        <el-empty description="暂无评论，来发表第一条评论吧！" :image-size="100"></el-empty>
      </div>

      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-main">
          <div class="comment-avatar">
            <el-avatar :src="comment.avatar" :size="40">
              {{ comment.nickname.charAt(0) }}
            </el-avatar>
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-nickname">{{ comment.nickname }}</span>
              <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button 
                text 
                :type="comment.isLiked ? 'primary' : ''"
                @click="toggleCommentLike(comment)"
                :loading="comment.id === likingCommentId"
              >
                <el-icon><Pointer /></el-icon>
                {{ comment.likeCount || 0 }}
              </el-button>
              <el-button text @click="showReplyForm(comment)">
                <el-icon><ChatDotRound /></el-icon>
                回复
              </el-button>
              <el-button 
                v-if="userStore.isLoggedIn && userStore.userInfo && comment.userId === userStore.userInfo.id"
                text 
                type="danger"
                @click="deleteComment(comment)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 回复表单 -->
        <div v-if="replyingTo === comment.id" class="reply-form">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="`回复 ${comment.nickname}...`"
            maxlength="500"
            show-word-limit
          />
          <div class="reply-form-actions">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button 
              type="primary" 
              size="small"
              @click="submitReply(comment)"
              :loading="submittingReply"
              :disabled="!replyContent.trim()"
            >
              回复
            </el-button>
          </div>
        </div>

        <!-- 回复列表 -->
        <div v-if="comment.replies && comment.replies.length > 0" class="replies">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="comment-avatar">
              <el-avatar :src="reply.avatar" :size="32">
                {{ reply.nickname.charAt(0) }}
              </el-avatar>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-nickname">{{ reply.nickname }}</span>
                <span class="comment-time">{{ formatDate(reply.createdAt) }}</span>
              </div>
              <div class="comment-text">{{ reply.content }}</div>
              <div class="comment-actions">
                <el-button 
                  text 
                  :type="reply.isLiked ? 'primary' : ''"
                  @click="toggleCommentLike(reply)"
                  :loading="reply.id === likingCommentId"
                >
                  <el-icon><Pointer /></el-icon>
                  {{ reply.likeCount || 0 }}
                </el-button>
                <el-button 
                  v-if="userStore.isLoggedIn && userStore.userInfo && reply.userId === userStore.userInfo.id"
                  text 
                  type="danger"
                  @click="deleteComment(reply)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores';
import commentApi from '@/api/comment';
import type { CommentItem } from '@/api/comment';
import { ElMessage, ElMessageBox, ElIcon, ElButton, ElInput, ElAlert, ElEmpty, ElSkeleton, ElAvatar } from 'element-plus';
import { Pointer, ChatDotRound, Delete } from '@element-plus/icons-vue';

const props = defineProps<{
  postId: number;
}>();

const router = useRouter();
const userStore = useUserStore();

// 评论数据
const comments = ref<CommentItem[]>([]);
const commentTotal = ref(0);
const loading = ref(false);
const error = ref<string | null>(null);

// 评论表单
const commentContent = ref('');
const submittingComment = ref(false);

// 回复表单
const replyingTo = ref<number | null>(null);
const replyContent = ref('');
const submittingReply = ref(false);

// 点赞状态
const likingCommentId = ref<number | null>(null);

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
  
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

// 获取评论列表
const fetchComments = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await commentApi.getCommentsByPostId(props.postId);
    if (response && response.code === 200 && response.data) {
      comments.value = response.data.list;
      commentTotal.value = response.data.total;
    } else {
      throw new Error('获取评论失败');
    }
  } catch (err: any) {
    const errorMsg = err.message || '获取评论失败';
    error.value = errorMsg;
    ElMessage.error(errorMsg);
  } finally {
    loading.value = false;
  }
};

// 发表评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  submittingComment.value = true;
  try {
    const response = await commentApi.createComment({
      postId: props.postId,
      content: commentContent.value.trim()
    });

    if (response && response.code === 200) {
      ElMessage.success('评论发表成功');
      commentContent.value = '';
      await fetchComments(); // 重新获取评论列表
    } else {
      throw new Error('发表评论失败');
    }
  } catch (err: any) {
    if (err.response && err.response.status === 401) {
      ElMessage.warning('请先登录');
      goLogin();
    } else {
      ElMessage.error(err.message || '发表评论失败');
    }
  } finally {
    submittingComment.value = false;
  }
};

// 显示回复表单
const showReplyForm = (comment: CommentItem) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    goLogin();
    return;
  }
  replyingTo.value = comment.id;
  replyContent.value = '';
};

// 取消回复
const cancelReply = () => {
  replyingTo.value = null;
  replyContent.value = '';
};

// 提交回复
const submitReply = async (parentComment: CommentItem) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }

  submittingReply.value = true;
  try {
    const response = await commentApi.createComment({
      postId: props.postId,
      content: replyContent.value.trim(),
      parentId: parentComment.id
    });

    if (response && response.code === 200) {
      ElMessage.success('回复发表成功');
      cancelReply();
      await fetchComments(); // 重新获取评论列表
    } else {
      throw new Error('发表回复失败');
    }
  } catch (err: any) {
    if (err.response && err.response.status === 401) {
      ElMessage.warning('请先登录');
      goLogin();
    } else {
      ElMessage.error(err.message || '发表回复失败');
    }
  } finally {
    submittingReply.value = false;
  }
};

// 点赞/取消点赞评论
const toggleCommentLike = async (comment: CommentItem) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    goLogin();
    return;
  }

  likingCommentId.value = comment.id;
  try {
    const response = await commentApi.likeComment(comment.id);
    if (response && response.code === 200) {
      // 更新评论的点赞状态
      if (comment.isLiked) {
        comment.likeCount -= 1;
        comment.isLiked = false;
        ElMessage.success('取消点赞成功');
      } else {
        comment.likeCount += 1;
        comment.isLiked = true;
        ElMessage.success('点赞成功');
      }
    } else {
      throw new Error('操作失败');
    }
  } catch (err: any) {
    if (err.response && err.response.status === 401) {
      ElMessage.warning('请先登录');
      goLogin();
    } else {
      ElMessage.error(err.message || '操作失败');
    }
  } finally {
    likingCommentId.value = null;
  }
};

// 删除评论
const deleteComment = async (comment: CommentItem) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '删除确认', {
      type: 'warning'
    });

    const response = await commentApi.deleteComment(comment.id);
    if (response && response.code === 200) {
      ElMessage.success('删除成功');
      await fetchComments(); // 重新获取评论列表
    } else {
      throw new Error('删除失败');
    }
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '删除失败');
    }
  }
};

// 跳转到登录页面
const goLogin = () => {
  router.push({
    path: '/login',
    query: { redirect: router.currentRoute.value.fullPath }
  });
};

// 监听 postId 变化
watch(() => props.postId, (newPostId) => {
  if (newPostId) {
    fetchComments();
  }
});

onMounted(() => {
  if (props.postId) {
    fetchComments();
  }
});
</script>

<style scoped>
.comment-section {
  margin-top: 20px;
}

.comment-section-header {
  margin-bottom: 20px;
}

.comment-section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background: var(--el-bg-color-page);
  border-radius: 8px;
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.login-prompt {
  margin-bottom: 30px;
}

.comment-list {
  min-height: 200px;
}

.empty-comments {
  text-align: center;
  padding: 40px 0;
}

.comment-item {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-nickname {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.comment-time {
  font-size: 0.85em;
  color: var(--el-text-color-secondary);
}

.comment-text {
  line-height: 1.6;
  color: var(--el-text-color-regular);
  margin-bottom: 10px;
  word-wrap: break-word;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.reply-form {
  margin-top: 16px;
  margin-left: 52px;
  padding: 16px;
  background: var(--el-bg-color-page);
  border-radius: 6px;
}

.reply-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
}

.replies {
  margin-top: 16px;
  margin-left: 52px;
  padding-left: 20px;
  border-left: 2px solid var(--el-border-color-lighter);
}

.reply-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.reply-item:last-child {
  margin-bottom: 0;
}
</style> 