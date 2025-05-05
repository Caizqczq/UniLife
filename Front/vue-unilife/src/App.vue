<script setup lang="ts">
import { onMounted } from 'vue';
import { useUserStore, useUIStore } from './stores';
import GlobalLoading from './components/GlobalLoading.vue';

const userStore = useUserStore();
const uiStore = useUIStore();

onMounted(() => {
  // 初始化UI状态
  uiStore.initialize();

  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo();
  }
});
</script>

<template>
  <div id="app">
    <router-view />
    <GlobalLoading />
  </div>
</template>

<style>
/* 全局样式已移至styles/global.css */
#app {
  width: 100%;
  height: 100vh;
  overflow-x: hidden;
}
</style>
