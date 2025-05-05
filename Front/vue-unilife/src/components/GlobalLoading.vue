<script setup lang="ts">
import { computed } from 'vue';
import { useUIStore } from '../stores';

const uiStore = useUIStore();

// 计算是否显示加载状态
const isVisible = computed(() => uiStore.isLoading);
const loadingText = computed(() => uiStore.loadingText);
</script>

<template>
  <transition name="fade">
    <div v-if="isVisible" class="loading-overlay">
      <div class="loading-container">
        <div class="loading-spinner"></div>
        <div class="loading-text">{{ loadingText }}</div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-container {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--secondary-color);
  border-top: 4px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

.loading-text {
  color: var(--text-primary);
  font-size: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
