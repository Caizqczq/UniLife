<template>
  <div class="public-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <router-link to="/">
            <img src="/images/默认头像.jpg" alt="UniLife Logo" class="logo-image" />
            <span class="logo-text">UniLife</span>
          </router-link>
        </div>
        
        <nav class="main-nav">
          <router-link to="/" class="nav-item">Forum</router-link> <!-- English consistency -->
          <router-link to="/resources" class="nav-item">Resources</router-link>
          <router-link to="/schedule/timetable" class="nav-item">Timetable</router-link>
          <router-link to="/schedule/manage" class="nav-item">Schedules</router-link>
        </nav>
        
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="user-dropdown-link">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar || '/images/默认头像.jpg'" />
                <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/personal/profile')"> <!-- Changed from /personal/home -->
                    <el-icon><User /></el-icon>My Profile
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/personal/settings')"> <!-- Changed from /personal/account -->
                    <el-icon><Setting /></el-icon>Settings
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/personal/posts')">
                    <el-icon><Document /></el-icon>My Posts
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/personal/messages')"> <!-- Placeholder, link is fine -->
                    <el-icon><Message /></el-icon>Messages 
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>Logout
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="login-btn">
              <el-button type="primary" size="small">登录 / 注册</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </header>
    
    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>
    
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <p>&copy; {{ new Date().getFullYear() }} UniLife - 有你生活，优你生活</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores';
import { User, Setting, Document, Message, SwitchButton, ArrowDown } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 在组件挂载时尝试获取用户信息（如果已登录）
onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo();
  }
});

// 退出登录
const logout = () => {
  userStore.logout();
  router.push('/');
};
</script>

<style scoped>
.public-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: var(--secondary-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--primary-color);
}

.logo-image {
  height: 32px;
  margin-right: 8px;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: bold;
}

.main-nav {
  display: flex;
  gap: 32px;
}

.nav-item {
  text-decoration: none;
  color: var(--text-primary);
  font-weight: 500;
  padding: 8px 0;
  position: relative;
}

.nav-item:hover {
  color: var(--primary-color);
}

.nav-item.router-link-active {
  color: var(--primary-color);
}

.nav-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--primary-color);
}

.user-area {
  display: flex;
  align-items: center;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.user-dropdown-link:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.username {
  margin: 0 8px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-btn {
  text-decoration: none;
}

.main-content {
  flex: 1;
  background-color: var(--bg-color);
  padding: 24px 0;
}

.footer {
  background-color: var(--secondary-color);
  padding: 16px 0;
  text-align: center;
  color: var(--text-secondary);
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
