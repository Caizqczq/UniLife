<script setup lang="ts">
import { useUserStore } from '../stores';
import { onMounted } from 'vue';

const userStore = useUserStore();

onMounted(async () => {
  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn) {
    await userStore.fetchUserInfo();
  }
});
</script>

<template>
  <div class="base-layout">
    <router-view />
  </div>
</template>

<style scoped>
.base-layout {
  width: 100%;
  height: 100vh;
  overflow-x: hidden;
}
</style>
