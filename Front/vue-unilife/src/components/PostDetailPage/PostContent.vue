<template>
  <div class="post-content">
    <!-- 顶部信息 -->
    <div class="post-header">
        <img class="avatar" :src="post.author.avatar" @click="toProfile" />
        <div class="info">
            <div class="nickname">{{ post.author.name }}</div>
            <div class="meta">发布日期 | IP 属地</div>
      </div>
    <button class="follow-btn btn-primary" @click="toProfile">+关注</button>
    </div>

    <el-divider />

    <!-- markdown内容 -->
    <MarkdownContent :content="post.content" />

    <!-- 评论区 -->
    <CommentInput />

  </div>
</template>

<script setup lang="ts">
import { string } from 'yup';
import MarkdownContent from './PostContent/MarkdownContent.vue'
import CommentInput from './PostContent/CommentInput.vue'
import CommentList from './PostContent/CommentList.vue'


defineProps<{
  post: {
    id:number,
    title:string,
    content:string,
    author: {
        id: number,
        name: string,
        avatar: string,
        bio: string,
    },
    tags:string[],
  },

  
}>()

function toProfile() {
    window.location.href = '/not-found'
}
</script>

<style scoped lang="scss">
.post-content{
    .post-header {
        display:flex;
        min-height:70px;
        align-items: center;
        margin-bottom: 30px;

        .avatar {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            cursor: pointer;
        }

    .info {
      margin-left: 15px;

      .nickname {
        font-size: 25px;
        font-weight: bold;
      }

      .meta {
        font-size: 15px;

      }
    }

    .follow-btn {
        width:100px;
        height:50px;
        margin-left: auto;
        margin-right:50px;
      }
    }
}
</style>