<template>
  <div class="forum-home">
    <!-- 左侧主内容区 -->
    <div class="left-section card">
      <!-- 上半部分 -->
      <div class="top-section">
        <div class="carousel-wrapper">
          <Carousel />
        </div>
        <div class="hot-topic-wrapper">
          <h2>📍 今日热点</h2>
          <HotTopic
            v-for="(item, index) in hotTopics"
            :key="index"
            :title="item.title"
            :link="item.link"
          />
        </div>
      </div>
      <el-divider content-position="right">欢迎来到UniLife</el-divider>
      <!-- 下半部分：帖子 -->
      <div class="posts-section">
        <h2>帖子</h2>
        <PostCard
          v-for="(post, index) in posts"
          :key="index"
          :post="post"
        />
      </div>
    </div>

    <!-- 右侧：今日行程 -->
    <div class="right-section">
      <h2>📅 今日行程</h2>
      <ScheduleCard />
    </div>
  </div>
</template>

<script setup lang="ts">
import Carousel from '@/components/Carousel.vue'
import PostCard from '@/components/PostCard.vue'
import HotTopic from '@/components/HotTopic.vue'
import ScheduleCard from '@/components/ScheduleCard.vue'

const hotTopics = [
  { title: '这是一个标题，一个热门帖子的标题', link: '/post/1' },
  { title: '这是一个标题，一个热门帖子的标题', link: '/post/2' },
  { title: '这是一个标题，一个热门帖子的标题', link: '/post/3' }
]

const posts = [
  {
    id: 1,
    title: '蚂蚁金服设计平台简介',
    tags: ['Ant Design', '设计语言', '蚂蚁金服'],
    excerpt: '段落示意：这是帖子的部分具体内容……',
    link: '/post/1'
  },
  {
    id: 2,
    title: '蚂蚁金服设计平台简介',
    tags: ['Ant Design', '设计语言', '蚂蚁金服'],
    excerpt: '段落示意：这是帖子的部分具体内容……',
    link: '/post/2'
  },
  {
    id: 3,
    title: '蚂蚁金服设计平台简介',
    tags: ['Ant Design', '设计语言', '蚂蚁金服'],
    excerpt: '段落示意：这是帖子的部分具体内容……',
    link: '/post/3'
  },
  {
    id: 4,
    title: '蚂蚁金服设计平台简介',
    tags: ['Ant Design', '设计语言', '蚂蚁金服'],
    excerpt: '段落示意：这是帖子的部分具体内容……',
    link: '/post/4'
  },
  {
    id: 5,
    title: '蚂蚁金服设计平台简介',
    tags: ['Ant Design', '设计语言', '蚂蚁金服'],
    excerpt: '段落示意：这是帖子的部分具体内容……',
    link: '/post/5'
  }
]
</script>

<style scoped lang="scss">
.forum-home {
  display: flex;
  width:92%;
  padding-top:750px;
  gap: 40px; // 🔧 左右两栏之间间距
  

  .left-section {
    flex: 3;
    display: flex;
    flex-direction: column;
    margin-right: 350px;
    gap: 30px; // 🔧 帖子区块和上半部分间隔加大
    background: linear-gradient(to bottom right, #f7f1ff, #ffffff);
  }

  .top-section {
    display: flex;
    gap: 24px; // 🔧 跑马灯和热点之间间距
    height: 320px;
    background-color:transparent;

    .carousel-wrapper {
      flex: 2;
      border-radius: 8px;
      overflow: hidden;
    }

    .hot-topic-wrapper {
      flex: 1;
      padding: 16px;
      background-color: #fef6ff;
      border-radius: 8px;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
  }

  .posts-section {
    display: flex;
    flex-direction: column;
    gap: 20px;

    h2 {
      margin-bottom: 12px;
    }
  }

  .right-section {
    flex: 1;
    position:fixed;
    margin-left:75%;
    height: 830px;
    min-width: 350px;
    padding: 16px;
    background-color: #f9f7ff;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    background: linear-gradient(to bottom right, #f7f1ff, #ffffff);

    h2 {
      margin-bottom: 16px;
    }
  }
}
</style>