<script setup lang="ts">
defineProps<{
  post: {
    title: string
    tags: string[]
    excerpt: string
    link: string
  }
}>()
</script>

<template>
  <router-link :to="post.link" class="post-card">
    <h3>{{ post.title }}</h3>
    <div class="tags">
      <el-tag v-for="(tag, i) in post.tags" :key="i" type="info">{{ tag }}</el-tag>
    </div>
    <p>{{ post.excerpt }}</p>
  </router-link>
</template>

<style lang="scss" scoped>
.post-card {
  display: block;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  color: inherit;
  text-decoration: none;

  &:hover {
    transform: translateY(-2px);
  }

  .tags {
    margin: 8px 0;
  }
}
</style>
