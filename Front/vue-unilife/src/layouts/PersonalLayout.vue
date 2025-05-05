<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../stores';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 侧边栏菜单项
const menuItems = [
  { name: 'Home', title: '个人主页', icon: 'home' },
  { name: 'AccountManager', title: '账号管理', icon: 'user' },
  { name: 'Posts', title: '我的帖子', icon: 'document' },
  { name: 'Messages', title: '消息中心', icon: 'message' },
  { name: 'Settings', title: '设置', icon: 'setting' }
];

// 当前激活的菜单项
const activeIndex = ref(0);

// 设置激活的菜单项
const setActive = (index: number) => {
  activeIndex.value = index;
  router.push({ name: menuItems[index].name });
};

// 根据当前路由设置激活的菜单项
onMounted(() => {
  const currentRouteName = route.name as string;
  const index = menuItems.findIndex(item => item.name === currentRouteName);
  if (index !== -1) {
    activeIndex.value = index;
  }
  
  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo();
  } else {
    // 未登录则跳转到登录页
    router.push('/login');
  }
});

// 退出登录
const logout = () => {
  userStore.logout();
  router.push('/login');
};
</script>

<template>
  <div class="personal-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <div class="avatar">
          <img :src="userStore.userInfo?.avatar || '/images/默认头像.jpg'" alt="用户头像">
        </div>
        <div class="username">{{ userStore.userInfo?.username || '用户' }}</div>
      </div>
      
      <ul class="menu">
        <li 
          v-for="(item, index) in menuItems" 
          :key="index"
          :class="{ active: activeIndex === index }"
          @click="setActive(index)"
        >
          <div class="menu-item">
            <div class="icon">
              <el-icon>
                <component :is="item.icon"></component>
              </el-icon>
            </div>
            <div class="title">{{ item.title }}</div>
          </div>
        </li>
      </ul>
      
      <div class="sidebar-footer">
        <button class="logout-btn" @click="logout">
          <el-icon><switch-button /></el-icon>
          <span>退出登录</span>
        </button>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.personal-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: var(--sidebar-width);
  height: 100%;
  background-color: var(--secondary-color);
  transition: width var(--transition-slow);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.sidebar:hover {
  width: var(--sidebar-width-expanded);
}

.sidebar-header {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: var(--border-radius-full);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
  border: 3px solid var(--primary-light);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  color: var(--text-primary);
  font-weight: 500;
  white-space: nowrap;
}

.menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 var(--spacing-sm);
}

.menu li {
  position: relative;
  margin-bottom: var(--spacing-md);
  cursor: pointer;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: background-color var(--transition-normal);
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.5);
}

.menu li.active .menu-item {
  background-color: var(--primary-color);
  color: white;
}

.icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
  margin-right: var(--spacing-md);
}

.title {
  white-space: nowrap;
}

.sidebar-footer {
  padding: var(--spacing-lg);
  display: flex;
  justify-content: center;
}

.logout-btn {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  background-color: rgba(255, 255, 255, 0.5);
  color: var(--text-primary);
  transition: background-color var(--transition-normal);
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.8);
}

.logout-btn .el-icon {
  margin-right: var(--spacing-sm);
}

.main-content {
  flex: 1;
  padding: var(--spacing-xl);
  overflow-y: auto;
  background-color: var(--bg-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personal-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    flex-direction: row;
    padding: var(--spacing-sm);
  }
  
  .sidebar:hover {
    width: 100%;
  }
  
  .sidebar-header {
    margin-bottom: 0;
    padding: var(--spacing-sm);
  }
  
  .menu {
    flex-direction: row;
    padding: 0;
    overflow-x: auto;
  }
  
  .menu li {
    margin-right: var(--spacing-md);
    margin-bottom: 0;
  }
  
  .sidebar-footer {
    padding: var(--spacing-sm);
  }
  
  .main-content {
    padding: var(--spacing-md);
  }
}
</style>
