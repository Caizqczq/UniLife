<template>
  <div class="comment-item">
    <img class="avatar" :src="comment.avatar" @click="toProfile" />
    <div class="content-box">
      <div class="user">{{ comment.user }}</div>
      <div class="content">{{ comment.content }}</div>
      <div class="meta">
        <span class="date">{{ comment.date }}</span>
        <span class="actions">
          <i class="icon" title="点赞">👍</i>
          <i class="icon" title="回复">💬</i>
          <i class="icon" title="举报">🚩</i>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  comment: {
    id: number
    user: string
    avatar: string
    content: string
    date: string
  }
}>()

function toProfile() {
  window.location.href = '/not-found'
}
</script>

<style scoped lang="scss">

.comment-item {
  display: flex;
  gap: 10px;
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 6px;
  background: #fff;

  .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    cursor: pointer;
  }

  .content-box {
    flex: 1;

    .user {
      font-weight: bold;
      margin-bottom: 4px;
    }

    .content {
      margin-bottom: 6px;
      font-size: 14px;
    }

    .meta {
      display: flex;
      justify-content: space-between;
      font-size: 12px;

      .actions {
        display: flex;
        gap: 10px;

        .icon {
          cursor: pointer;
          &:hover {
            
          }
        }
      }
    }
  }
}
</style>
