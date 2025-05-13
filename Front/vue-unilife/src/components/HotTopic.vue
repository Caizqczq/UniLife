<script setup lang="ts">
defineProps<{ title: string; link: string }>()
</script>

<template>
  <router-link :to="link" class="hot-topic-item">
    {{ title }}
  </router-link>
</template>


<style lang="scss" scoped>
.hot-topic-item {
  display: block;
  background-color: #fbefff;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 8px;
  color: #333;
  text-decoration: none;

  &:hover {
    background-color: #e4d4ff;
  }
}
</style>
