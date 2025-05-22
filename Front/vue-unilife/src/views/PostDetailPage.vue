<template>
    <div class = "post-detail-page">
    <!-- 左侧分类 包含分类 -->
        <SidebarCategory class="sidebar card" />

    <!-- 中间内容 帖子的具体内容-->
        <PostContent :post="post" class="content card" />

    <!-- 右侧作者信息 -->
        <AuthorInfo class="author-info card" :author="post.author" />
    </div>
</template>

<script setup lang = "ts">
import SidebarCategory from '@/components/PostDetailPage/SidebarCategory.vue'
import PostContent from '@/components/PostDetailPage/PostContent.vue'
import AuthorInfo from '@/components/PostDetailPage/AuthorInfor.vue'
import { useRoute } from 'vue-router'
import {ref, onMounted} from 'vue'
import Avatar from '@/assets/images/默认头像.jpg'

const route = useRoute()
const postId = ref<number>(parseInt(route.params.id as string))

const post = ref({
  id: postId.value,
  title: '帖子标题示例',
  content: '这是帖子的详细内容，支持HTML或markdown',
  author: {
    id: 1,
    name: '张三',
    avatar: Avatar,
    bio: '一名热爱分享的大学生'
  },
  tags: ['Vue3', '论坛开发', '学习笔记']
})

// 可选：模拟异步获取
onMounted(() => {
  // TODO: fetch(`/api/post/${postId.value}`) 替换为真实接口
})

</script>

<style scoped lang="scss">
.post-detail-page {
  display:flex;
  padding-top:550px;
  width:92%;
  gap:16px;

  .sidebar{
    flex:1;
    position:sticky;
    top:20px;
    height: fit-content;
    border-radius: 8px;
    padding: 16px;
    flex-shrink: 0;
  }

  .content{
    flex: 8;
    padding: 16px;
    border-radius: 8px;
  }

  .author-info{
    flex:2;
    position:sticky;
    flex-shrink:0;
    height:fit-content;
    border-radius: 8px;
    padding: 16px;
  }
}
</style>