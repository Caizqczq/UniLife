<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push('/');
};

const goBack = () => {
  router.back();
};
</script>

<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1>404</h1>
      <h2>页面不存在</h2>
      <p>抱歉，您访问的页面不存在或已被移除</p>
      <div class="not-found-actions">
        <button class="btn btn-primary" @click="goHome">返回首页</button>
        <button class="btn btn-secondary" @click="goBack">返回上一页</button>
      </div>
    </div>
    <div class="not-found-image">
      <img src="/images/默认头像.jpg" alt="404">
    </div>
  </div>
</template>

<style scoped>
.not-found {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--bg-gradient);
  padding: var(--spacing-lg);
}

.not-found-content {
  text-align: center;
  margin-right: var(--spacing-xl);
}

.not-found-content h1 {
  font-size: 120px;
  color: var(--primary-color);
  margin: 0;
  line-height: 1;
}

.not-found-content h2 {
  font-size: var(--font-size-xxl);
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}

.not-found-content p {
  font-size: var(--font-size-lg);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
}

.not-found-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
}

.not-found-image {
  max-width: 400px;
}

.not-found-image img {
  width: 100%;
  height: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .not-found {
    flex-direction: column;
  }

  .not-found-content {
    margin-right: 0;
    margin-bottom: var(--spacing-xl);
  }

  .not-found-content h1 {
    font-size: 80px;
  }

  .not-found-image {
    max-width: 300px;
  }
}
</style>
