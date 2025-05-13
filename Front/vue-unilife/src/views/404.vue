<template>
  <div class="not-found-container">
    <el-icon class="not-found-icon"><Warning /></el-icon>
    <h1>404 - 页面未找到</h1>
    <p>你访问的页面不存在，可能是链接失效或地址错误。</p>
    <button class = "btn btn-primary" @click="goHome">返回首页</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Warning } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/') // 你也可以改成 '/log' 或其他主页路径
}
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.not-found-icon {
  font-size: 64px;
  color: #b19cd9;
  margin-bottom: 20px;
}

h1 {
  margin-bottom: 10px;
}

p {
  margin-bottom: 30px;
}
</style>
