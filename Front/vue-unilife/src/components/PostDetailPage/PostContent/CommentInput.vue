<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <img class="avatar" src="@/assets/images/默认头像.jpg" @click="goToProfile" />
      <input v-model="newComment" placeholder="发布友善的评论" />
      <button @click="submitComment">
        <i class="icon-send">📨</i>
      </button>
    </div>

    <!-- 评论操作栏 -->
    <div class="action-bar">
      <i class="icon">👍</i>
      <i class="icon">📤</i>
      <i class="icon">⭐</i>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <CommentList
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CommentList from './CommentList.vue'
import Avatar from '@/assets/images/默认头像.jpg';

interface Comment {
  id: number
  user: string
  avatar: string
  content: string
  date: string
}

const newComment = ref('')
const comments = ref<Comment[]>([
  {
    id: 1,
    user: '其他用户昵称',
    avatar: Avatar,
    content: '这是一条评论……',
    date: '日期时间 IP',
  },
  {
    id: 2,
    user: '用户B',
    avatar: Avatar,
    content: '第二条评论',
    date: '日期时间 IP',
  },
])

function goToProfile() {
  window.location.href = '/not-found'
}

function submitComment() {
  if (!newComment.value.trim()) return
  comments.value.push({
    id: Date.now(),
    user: '你',
    avatar: Avatar,
    content: newComment.value,
    date: '刚刚',
  })
  newComment.value = ''
}

</script>

<style scoped lang="scss">
.comment-section {
  margin-top: 24px;

  .comment-input {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 12px;

    .avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      cursor: pointer;
    }

    input {
      flex: 1;
      padding: 8px 12px;
      border: 1px solid #ccc;
      border-radius: 6px;
    }

    button {
      color: white;
      border: none;
      border-radius: 6px;
      padding: 6px 10px;
      cursor: pointer;
    }
  }

  .action-bar {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-bottom: 16px;

    .icon {
      cursor: pointer;
      font-size: 20px;
      &:hover {
        
      }
    }
  }

  .comment-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
}
</style>