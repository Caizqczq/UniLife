<template>
  <div class="comment-section">
    <el-divider />
    <h3>Comments ({{ totalComments }})</h3>

    <div v-if="authStore.isLoggedIn" class="add-comment-box">
      <el-input
        v-model="newCommentContent"
        type="textarea"
        :rows="3"
        placeholder="Write a comment..."
        maxlength="1000"
        show-word-limit
      />
      <el-button
        type="primary"
        @click="handleAddComment(null)"
        :loading="addingComment"
        style="margin-top: 10px;"
        :disabled="!newCommentContent.trim()"
      >
        Post Comment
      </el-button>
    </div>
    <div v-else class="login-prompt">
      <p>Please <router-link to="/login">login</router-link> to post comments.</p>
    </div>

    <el-skeleton :rows="5" animated v-if="loadingComments" />

    <div v-if="!loadingComments && comments.length === 0 && !errorLoadingComments" class="no-comments">
      <el-empty description="No comments yet. Be the first to comment!" />
    </div>
    
    <div v-if="errorLoadingComments" class="error-comments">
       <el-alert title="Error loading comments" type="error" :description="errorLoadingComments" show-icon :closable="false" />
    </div>


    <div class="comment-list" v-if="!loadingComments && comments.length > 0">
      <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :post-id="postId"
        @comment-deleted="handleCommentDeleted"
        @comment-liked="handleCommentLiked"
        @reply-added="handleReplyAdded"
        @reply-to="handleReplyTo"
      />
    </div>

    <el-pagination
      v-if="!loadingComments && totalComments > commentsPageSize"
      class="pagination"
      :current-page="currentCommentsPage"
      :page-size="commentsPageSize"
      :total="totalComments"
      layout="prev, pager, next"
      @current-change="handleCommentsPageChange"
    />

    <!-- Reply Dialog (simplified, could be inline) -->
    <el-dialog v-model="replyDialogVisible" :title="'Reply to ' + (replyingToComment?.user?.nickname || replyingToComment?.user?.username || 'Commenter')" width="500px">
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="3"
        :placeholder="'Replying to ' + (replyingToComment?.user?.nickname || replyingToComment?.user?.username)"
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitReply" :loading="addingReply" :disabled="!replyContent.trim()">
          Post Reply
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, PropType, watch } from 'vue';
import axios from 'axios';
import { ElMessage, ElButton, ElInput, ElDivider, ElPagination, ElSkeleton, ElEmpty, ElDialog, ElAlert } from 'element-plus';
import { useUserStore } from '../../stores/user'; // For checking login status and user ID
import CommentItem from './CommentItem.vue'; // Assuming CommentItem is created separately

interface CommentUser {
  id: number;
  username: string;
  nickname?: string;
  avatar?: string;
}
export interface Comment {
  id: number;
  postId: number;
  userId: number;
  user?: CommentUser;
  content: string;
  parentId: number | null;
  likeCount: number;
  isLiked?: boolean; // Added to track if current user liked this comment
  createdAt: string;
  updatedAt: string;
  replies?: Comment[]; // For nested comments
}

export default defineComponent({
  name: 'CommentSection',
  components: {
    CommentItem, // Register CommentItem
    ElButton, ElInput, ElDivider, ElPagination, ElSkeleton, ElEmpty, ElDialog, ElAlert,
  },
  props: {
    postId: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const authStore = useUserStore();
    const comments = ref<Comment[]>([]);
    const loadingComments = ref(true);
    const errorLoadingComments = ref<string | null>(null);
    const newCommentContent = ref('');
    const addingComment = ref(false);
    const totalComments = ref(0);
    const currentCommentsPage = ref(1);
    const commentsPageSize = ref(10); // Or whatever your API default/preference is

    const replyDialogVisible = ref(false);
    const replyingToComment = ref<Comment | null>(null);
    const replyContent = ref('');
    const addingReply = ref(false);

    const fetchComments = async (page = 1) => {
      loadingComments.value = true;
      errorLoadingComments.value = null;
      try {
        const response = await axios.get(`/api/comments/post/${props.postId}`, {
          params: {
            page: page,
            size: commentsPageSize.value,
          },
        });
        if (response.data && response.data.code === 200 && response.data.data) {
          // Assuming API returns comments with replies nested or needs to be structured
          // For now, assuming a flat list or backend handles nesting structure if parentId is used.
          // The provided API GET /api/comments/post/{postId} does not explicitly state how replies are handled.
          // We will assume it returns all comments for the post, and we can structure them client-side if needed,
          // or CommentItem handles its own replies if the API provides 'children' or similar.
          // For this implementation, we'll assume a flat list for direct comments to the post.
          // Replies to comments will be handled by CommentItem or a nested call if needed.
          comments.value = response.data.data.records || response.data.data || [];
          totalComments.value = response.data.data.total || comments.value.length;
          currentCommentsPage.value = page;

          // If your API returns user like status for each comment, process it here
          // e.g., comments.value.forEach(c => c.isLiked = checkUserLike(c.id));
        } else {
          throw new Error(response.data.message || 'Failed to fetch comments');
        }
      } catch (error: any) {
        console.error("Fetch comments error:", error);
        errorLoadingComments.value = error.response?.data?.message || error.message || 'Could not load comments.';
        comments.value = [];
        totalComments.value = 0;
      } finally {
        loadingComments.value = false;
      }
    };

    const handleAddComment = async (parentId: number | null = null, content?: string) => {
      const commentText = content || newCommentContent.value;
      if (!commentText.trim()) {
        ElMessage.warning('Comment cannot be empty.');
        return;
      }
      
      const currentAddingFlag = parentId ? addingReply : addingComment;
      currentAddingFlag.value = true;

      try {
        const payload: { postId: number; content: string; parentId?: number } = {
          postId: props.postId,
          content: commentText.trim(),
        };
        if (parentId) {
          payload.parentId = parentId;
        }

        const response = await axios.post('/api/comments', payload);

        if (response.data && response.data.code === 200) {
          ElMessage.success(parentId ? 'Reply posted successfully!' : 'Comment posted successfully!');
          if (parentId) {
            // If it's a reply, we might need to update the parent comment's replies list
            // This depends on how CommentItem and the data structure work.
            // For now, simply re-fetch all comments to see the new reply.
            // A more optimized way would be to add it directly to the parent in comments.value
            fetchComments(currentCommentsPage.value);
            replyDialogVisible.value = false;
            replyContent.value = '';
          } else {
            // If it's a new top-level comment, add it to the list or re-fetch
            // For simplicity, re-fetch the current page of comments.
             // Or, if the API returns the created comment, prepend it:
            // comments.value.unshift(response.data.data); totalComments.value++;
            fetchComments(1); // Go to first page to see new comment
            newCommentContent.value = '';
          }
        } else {
          ElMessage.error(response.data.message || 'Failed to post comment.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error posting comment.');
        console.error("Add comment error:", error);
      } finally {
        currentAddingFlag.value = false;
      }
    };

    const handleCommentsPageChange = (page: number) => {
      fetchComments(page);
    };

    const handleCommentDeleted = (commentId: number) => {
      comments.value = comments.value.filter(c => c.id !== commentId);
      totalComments.value--;
      // Potentially re-fetch if counts per page are affected significantly
      // or if a reply was deleted, to update parent.
      ElMessage.success('Comment deleted.');
       if (comments.value.length === 0 && totalComments.value > 0) {
        fetchComments(Math.max(1, currentCommentsPage.value -1)); // fetch previous page if current is empty
      }
    };
    
    const handleCommentLiked = (commentId: number, isLiked: boolean, newLikeCount: number) => {
        const comment = findComment(comments.value, commentId);
        if (comment) {
            comment.isLiked = isLiked;
            comment.likeCount = newLikeCount;
        }
    };

    const findComment = (commentList: Comment[], commentId: number): Comment | null => {
        for (const comment of commentList) {
            if (comment.id === commentId) return comment;
            if (comment.replies) {
                const foundInReply = findComment(comment.replies, commentId);
                if (foundInReply) return foundInReply;
            }
        }
        return null;
    };


    const handleReplyTo = (comment: Comment) => {
      replyingToComment.value = comment;
      replyContent.value = ''; // Clear previous reply content
      replyDialogVisible.value = true;
    };

    const submitReply = () => {
      if (replyingToComment.value) {
        handleAddComment(replyingToComment.value.id, replyContent.value);
      }
    };
    
    const handleReplyAdded = (/* parentCommentId: number, newReply: Comment */) => {
        // This event is emitted by CommentItem.vue after a reply is successfully added *through CommentItem itself*.
        // If CommentSection handles all reply submissions (like via the dialog), this might not be needed here,
        // or it's used to refresh/update the view.
        // For now, re-fetching comments to show the new reply.
        fetchComments(currentCommentsPage.value);
    };


    onMounted(() => {
      fetchComments();
    });
    
    // Watch for postId changes, if the component can be reused for different posts without remounting
    watch(() => props.postId, (newPostId, oldPostId) => {
        if (newPostId !== oldPostId) {
            fetchComments(1); // Reset to first page for new post
        }
    });

    return {
      authStore,
      comments,
      loadingComments,
      errorLoadingComments,
      newCommentContent,
      addingComment,
      totalComments,
      currentCommentsPage,
      commentsPageSize,
      handleAddComment,
      handleCommentsPageChange,
      handleCommentDeleted,
      handleCommentLiked,
      handleReplyAdded,
      // Reply dialog
      replyDialogVisible,
      replyingToComment,
      replyContent,
      addingReply,
      handleReplyTo,
      submitReply,
    };
  },
});
</script>

<style scoped>
.comment-section {
  margin-top: 30px;
}
.comment-section h3 {
  margin-bottom: 15px;
  font-size: 1.4em;
  color: #303133;
}
.add-comment-box {
  margin-bottom: 25px;
}
.login-prompt {
  margin-bottom: 25px;
  padding: 15px;
  background-color: #f4f4f5;
  border-radius: 4px;
  text-align: center;
}
.login-prompt p {
  margin: 0;
  color: #909399;
}
.login-prompt a {
  color: var(--el-color-primary);
  text-decoration: none;
}
.login-prompt a:hover {
  text-decoration: underline;
}
.comment-list {
  margin-top: 20px;
}
.no-comments {
  padding: 20px 0;
}
.error-comments {
    padding: 20px 0;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
