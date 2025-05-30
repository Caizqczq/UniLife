<template>
  <nav class="top-navbar">
    <div class="nav-container">
      <div class="nav-brand">
        <router-link to="/" class="brand-link">
          <div class="logo-circle">
            <i class="el-icon-star-filled"></i>
          </div>
          <span class="brand-name">UniLife</span>
        </router-link>
      </div>
      
      <div class="nav-menu">
        <router-link to="/forum" class="nav-item">论坛</router-link>
        <router-link to="/resources" class="nav-item">资源</router-link>
        <router-link to="/schedule" class="nav-item">课程表</router-link>
        <router-link to="/tasks" class="nav-item">日程管理</router-link>
        <router-link to="/ai-assistant" class="nav-item">AI助手</router-link>
      </div>
      
      <div class="nav-actions">
        <div class="user-info">
          <el-avatar :size="36" :src="userStore.user?.avatar">
            {{ userStore.user?.nickname?.charAt(0) }}
          </el-avatar>
          <span class="username">{{ userStore.user?.nickname || '用户' }}</span>
        </div>
        <el-dropdown @command="handleCommand">
          <el-button circle>
            <el-icon><Setting /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.top-navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo-circle {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-item {
  text-decoration: none;
  color: #6b7280;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #374151;
  background: rgba(107, 114, 128, 0.1);
}

.nav-item.router-link-active {
  color: #8b5cf6;
  background: rgba(139, 92, 246, 0.1);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: #374151;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .nav-container {
    padding: 0 16px;
  }
  
  .brand-name {
    font-size: 20px;
  }
  
  .logo-circle {
    width: 36px;
    height: 36px;
    font-size: 18px;
  }
}
</style> 