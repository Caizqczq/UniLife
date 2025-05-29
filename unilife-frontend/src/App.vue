<template>
  <div id="app">
    <router-view />
    <!-- Stagewise工具栏 - 仅在开发模式下显示 -->
    <component :is="stagewiseComponent" v-if="isDevelopment && stagewiseComponent" :config="stagewiseConfig" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, type Component } from 'vue'

// 检查是否为开发环境
const isDevelopment = import.meta.env.DEV

// Stagewise组件引用
const stagewiseComponent = ref<Component | null>(null)

// Stagewise配置
const stagewiseConfig = {
  plugins: []
}

// 仅在开发模式下动态加载stagewise组件
if (isDevelopment) {
  onMounted(async () => {
    try {
      const { StagewiseToolbar } = await import('@stagewise/toolbar-vue')
      stagewiseComponent.value = StagewiseToolbar as Component
    } catch (error) {
      console.warn('Stagewise toolbar not available:', error)
    }
  })
}
</script>

<style>
#app {
  min-height: 100vh;
  background-color: #f5f5f5;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
</style>
