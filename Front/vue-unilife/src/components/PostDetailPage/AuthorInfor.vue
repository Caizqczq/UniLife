<template>
   <div class="author-box">
    <img class="avatar" :src="author.avatar" @click="toProfile" />
    <div class="nickname">{{ author.name }}</div>
    <div class="stats">
      <span>xx 关注</span>
      <span>xx 粉丝</span>
      <span>xx 帖子</span>
    </div>
    <div class="other-posts">
      <h4>这是帖主的其他帖子</h4>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  author: {
    id: number,
    name: string,
    avatar: string,
    bio: string,
  },
}>()

function toProfile() {
    window.location.href = '/not-found'
}
</script>

<style scoped lang="scss">
.author-box {
  text-align: center;

  .avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    margin-bottom: 8px;
    cursor: pointer;
  }

  .nickname {
    font-weight: bold;
    margin-bottom: 8px;
  }

  .stats {
    display: flex;
    justify-content: space-around;
    margin-bottom: 16px;
    font-size: 14px;
  }

  .other-posts {
    background: #fff;
    border-radius: 8px;
    padding: 10px;
    font-size: 14px;
    text-align: left;
  }
}
</style>