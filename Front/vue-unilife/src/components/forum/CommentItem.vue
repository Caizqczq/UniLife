<template>
  <el-card class="comment-item" :class="{'is-reply': isReply}" shadow="never">
    <div class="comment-header">
      <el-avatar :size="isReply ? 30 : 40" :src="comment.user?.avatar || defaultAvatar" class="comment-avatar" />
      <div class="comment-author-info">
        <span class="comment-author-name">{{ comment.user?.nickname || comment.user?.username || 'Anonymous' }}</span>
        <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
      </div>
      <div class="comment-actions">
        <el-button
          type="text"
          size="small"
          @click="toggleLike"
          :class="{ 'liked': localIsLiked }"
          :loading="likeLoading"
          :icon="Pointer"
        >
          {{ localLikeCount }} Like{{ localLikeCount !== 1 ? 's' : '' }}
        </el-button>
        <el-button type="text" size="small" @click="handleReply" :icon="ChatDotRound" v-if="authStore.isLoggedIn">Reply</el-button>
        <el-popconfirm
          v-if="isAuthor"
          title="Are you sure you want to delete this comment?"
          confirm-button-text="Yes"
          cancel-button-text="No"
          @confirm="deleteComment"
        >
          <template #reference>
            <el-button type="text" size="small" :icon="Delete" :loading="deleteLoading" class="delete-btn">Delete</el-button>
          </template>
        </el-popconfirm>
      </div>
    </div>
    <div class="comment-content" v-html="purifiedContent"></div>

    <!-- Replies Section -->
    <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies">
      <CommentItem
        v-for="reply in comment.replies"
        :key="reply.id"
        :comment="reply"
        :post-id="postId"
        :is-reply="true"
        @comment-deleted="handleChildCommentAction"
        @comment-liked="handleChildCommentAction"
        @reply-added="handleChildCommentAction"
        @reply-to="emitReplyTo"
      />
    </div>
     <!-- Simplified reply box directly under comment -->
    <div v-if="showReplyBox" class="reply-input-box">
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="2"
        :placeholder="'Replying to ' + (comment.user?.nickname || comment.user?.username)"
        maxlength="500"
        show-word-limit
      />
      <div style="text-align: right; margin-top: 8px;">
        <el-button size="small" @click="showReplyBox = false">Cancel</el-button>
        <el-button type="primary" size="small" @click="submitReply" :loading="addingReply" :disabled="!replyContent.trim()">
          Post Reply
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts">
import { defineComponent, ref, computed, PropType, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElAvatar, ElButton, ElCard, ElPopconfirm, ElInput } from 'element-plus';
import { Pointer, ChatDotRound, Delete } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user';
import type { Comment } from './CommentSection.vue'; // Import the Comment interface
import DOMPurify from 'dompurify';

export default defineComponent({
  name: 'CommentItem',
  components: { ElAvatar, ElButton, ElCard, ElPopconfirm, ElInput, Pointer, ChatDotRound, Delete },
  props: {
    comment: {
      type: Object as PropType<Comment>,
      required: true,
    },
    postId: {
      type: Number,
      required: true,
    },
    isReply: { // To style replies differently if needed
      type: Boolean,
      default: false,
    },
  },
  emits: ['comment-deleted', 'comment-liked', 'reply-added', 'reply-to'],
  setup(props, { emit }) {
    const authStore = useUserStore();
    const likeLoading = ref(false);
    const deleteLoading = ref(false);
    const localLikeCount = ref(props.comment.likeCount);
    const localIsLiked = ref(props.comment.isLiked || false); // Initialize with prop

    const showReplyBox = ref(false);
    const replyContent = ref('');
    const addingReply = ref(false);

    const defaultAvatar = '/img/default-avatar.png'; // Provide a path to a default avatar

    const isAuthor = computed(() => {
      return authStore.user && authStore.user.id === props.comment.userId;
    });

    const purifiedContent = computed(() => {
      return DOMPurify.sanitize(props.comment.content, { USE_PROFILES: { html: true } });
    });

    const formatDate = (dateString: string) => {
      const date = new Date(dateString);
      return date.toLocaleString(); // Or more specific formatting
    };

    const toggleLike = async () => {
      if (!authStore.isLoggedIn) {
        ElMessage.warning('Please login to like comments.');
        return;
      }
      likeLoading.value = true;
      try {
        let response;
        if (localIsLiked.value) {
          response = await axios.delete(`/api/comments/${props.comment.id}/like`);
          if (response.data && response.data.code === 200) {
            localIsLiked.value = false;
            localLikeCount.value--;
          }
        } else {
          response = await axios.post(`/api/comments/${props.comment.id}/like`);
           if (response.data && response.data.code === 200) {
            localIsLiked.value = true;
            localLikeCount.value++;
          }
        }
        if (response.data && response.data.code === 200) {
            emit('comment-liked', props.comment.id, localIsLiked.value, localLikeCount.value);
        } else {
            ElMessage.error(response.data.message || `Failed to ${localIsLiked.value ? 'unlike' : 'like'} comment.`);
            // Revert optimistic update if API call failed
            if(localIsLiked.value) localLikeCount.value --; else localLikeCount.value ++;
            localIsLiked.value = !localIsLiked.value;
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error updating like status.');
        // Revert optimistic update on error
        if(localIsLiked.value) localLikeCount.value --; else localLikeCount.value ++;
        localIsLiked.value = !localIsLiked.value;
      } finally {
        likeLoading.value = false;
      }
    };

    const deleteComment = async () => {
      deleteLoading.value = true;
      try {
        const response = await axios.delete(`/api/comments/${props.comment.id}`);
        if (response.data && response.data.code === 200) {
          ElMessage.success('Comment deleted successfully.');
          emit('comment-deleted', props.comment.id);
        } else {
           ElMessage.error(response.data.message || 'Failed to delete comment.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error deleting comment.');
      } finally {
        deleteLoading.value = false;
      }
    };
    
    const handleReply = () => {
      // Option 1: Emit event for parent (CommentSection) to handle with a dialog
      // emit('reply-to', props.comment);

      // Option 2: Show an inline reply box directly under this comment
      showReplyBox.value = !showReplyBox.value; // Toggle display
      replyContent.value = ''; // Clear content when showing
    };

    const submitReply = async () => {
        if (!replyContent.value.trim()) {
            ElMessage.warning('Reply cannot be empty.');
            return;
        }
        addingReply.value = true;
        try {
            const response = await axios.post('/api/comments', {
                postId: props.postId,
                content: replyContent.value.trim(),
                parentId: props.comment.id,
            });
            if (response.data && response.data.code === 200) {
                ElMessage.success('Reply posted successfully!');
                // The new reply (response.data.data) could be added to props.comment.replies
                // or simply emit an event for the parent to refresh.
                emit('reply-added', props.comment.id, response.data.data);
                showReplyBox.value = false;
                replyContent.value = '';
            } else {
                ElMessage.error(response.data.message || 'Failed to post reply.');
            }
        } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error posting reply.');
        } finally {
            addingReply.value = false;
        }
    };
    
    const handleChildCommentAction = () => {
        // If a child comment (a reply to this comment's reply) is deleted/liked,
        // this component might need to refresh its state or propagate the event upwards.
        // For simplicity, if CommentSection re-fetches on these actions, this might not be needed.
        // Or, more specifically, tell CommentSection that *something* happened to trigger a refresh.
        emit('reply-added', props.comment.id, null); // Using reply-added as a generic "refresh children" signal
    };

    const emitReplyTo = (comment: Comment) => {
        // Propagate event from nested replies upwards
        emit('reply-to', comment);
    };
    
    // Watch for external changes to like status (e.g., if user logs in/out and status is re-fetched)
    // This is more complex and depends on how comment data is managed globally.
    // For now, relying on initial prop and local updates.
    onMounted(() => {
        // Check if the API GET /api/comments/post/{postId} returns `isLiked` for the current user.
        // If so, initialize localIsLiked based on that.
        // The current `Comment` interface in `CommentSection.vue` has an optional `isLiked`.
        // This means `props.comment.isLiked` should be the source of truth initially.
        localIsLiked.value = !!props.comment.isLiked;

        // Fetch initial like status if not provided by parent (less ideal due to N+1 potential)
        // This should ideally be part of the main comment fetch in CommentSection.
        // if (authStore.isLoggedIn && typeof props.comment.isLiked === 'undefined') {
        //   fetchLikeStatus(); // You would need a `GET /api/comments/{commentId}/like/status` endpoint
        // }
    });


    return {
      authStore,
      likeLoading,
      deleteLoading,
      localLikeCount,
      localIsLiked,
      isAuthor,
      defaultAvatar,
      formatDate,
      toggleLike,
      deleteComment,
      handleReply,
      purifiedContent,
      Pointer, ChatDotRound, Delete, // Icons
      // Inline reply box
      showReplyBox,
      replyContent,
      addingReply,
      submitReply,
      // For handling events from nested replies
      handleChildCommentAction,
      emitReplyTo,
    };
  },
});
</script>

<style scoped>
.comment-item {
  margin-bottom: 15px;
  border-left: 3px solid transparent; /* Default border */
}
.comment-item.is-reply {
  margin-left: 40px; /* Indent replies */
  border-left-color: #e9ebef; /* Slightly different border for replies */
  background-color: #f9fafc;
}
.comment-item :deep(.el-card__body) { /* Accessing el-card's inner body padding */
    padding: 15px;
}


.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.comment-avatar {
  margin-right: 10px;
  flex-shrink: 0;
}
.comment-author-info {
  flex-grow: 1;
}
.comment-author-name {
  font-weight: bold;
  color: #303133;
  margin-right: 8px;
  font-size: 0.95em;
}
.comment-date {
  font-size: 0.8em;
  color: #909399;
}
.comment-actions {
  margin-left: auto; /* Pushes actions to the right */
  display: flex;
  align-items: center;
}
.comment-actions .el-button {
  padding: 5px 8px; /* Smaller padding for action buttons */
  margin-left: 8px;
}
.comment-actions .el-button.liked {
  color: var(--el-color-primary);
}
.comment-actions .delete-btn:hover {
    color: var(--el-color-danger);
}

.comment-content {
  font-size: 0.9em;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap; /* Respects newlines and spaces */
  word-wrap: break-word;
}
.comment-content :deep(p:last-child) {
    margin-bottom: 0;
}


.comment-replies {
  margin-top: 15px;
  padding-left: 10px; /* Slight indent for replies container */
  /* border-left: 2px solid #f0f2f5; */ /* Visual cue for reply thread */
}

.reply-input-box {
    margin-top: 10px;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
}
</style>
