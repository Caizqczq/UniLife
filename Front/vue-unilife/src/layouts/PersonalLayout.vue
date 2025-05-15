<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../stores';
import { HomeFilled, User, Document, Message, Setting, ArrowLeft } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 侧边栏菜单项 - 个人中心相关
const menuItems = [
  { name: 'PersonalHome', title: '个人主页', icon: HomeFilled, path: '/personal/home' },
  { name: 'AccountManager', title: '账号管理', icon: User, path: '/personal/account' },
  { name: 'MyPosts', title: '我的帖子', icon: Document, path: '/personal/posts' },
  { name: 'Messages', title: '消息中心', icon: Message, path: '/personal/messages' },
  { name: 'Settings', title: '设置', icon: Setting, path: '/personal/settings' }
];

// 当前激活的菜单项
const activeIndex = ref(0);

// 设置激活的菜单项
const setActive = (index: number) => {
  activeIndex.value = index;
  router.push(menuItems[index].path);
};

// 返回论坛首页
const backToForum = () => {
  router.push('/');
};

// 根据当前路由设置激活的菜单项
onMounted(() => {
  const currentPath = route.path;
  const index = menuItems.findIndex(item => currentPath.includes(item.path));
  if (index !== -1) {
    activeIndex.value = index;
  }
  
  // 如果已登录，获取用户信息
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo();
  } else {
    // 未登录则跳转到登录页，并保存当前路径用于登录后跳转回来
    router.push({ path: '/login', query: { redirect: route.fullPath } });
  }
});
</script>

<template>
  <div class="personal-layout">
    <!-- 返回论坛按钮 -->
    <div class="back-bar">
      <button class="back-btn" @click="backToForum">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回论坛</span>
      </button>
      <h1 class="page-title">个人中心</h1>
    </div>
    
    <div class="layout-container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <div class="avatar">
            <img :src="userStore.userInfo?.avatar || '/images/默认头像.jpg'" alt="用户头像">
          </div>
          <div class="user-info">
            <div class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</div>
            <div class="username">{{ userStore.userInfo?.username }}</div>
          </div>
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
      </div>
      
      <!-- 主内容区 -->
      <div class="main-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
.personal-layout {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100vh;
  background-color: var(--bg-color);
}

.back-bar {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  background-color: var(--secondary-color);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.back-btn {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background-color: transparent;
  border: none;
  border-radius: var(--border-radius-sm);
  color: var(--primary-color);
  font-weight: 500;
  cursor: pointer;
  transition: background-color var(--transition-normal);
}

.back-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.back-btn span {
  margin-left: 8px;
}

.page-title {
  margin-left: 16px;
  font-size: 1.2rem;
  color: var(--text-primary);
}

.layout-container {
  display: flex;
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  gap: 24px;
}

.sidebar {
  width: 260px;
  background-color: white;
  border-radius: var(--border-radius-md);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-full);
  overflow: hidden;
  margin-bottom: 12px;
  border: 3px solid var(--primary-light);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  text-align: center;
}

.nickname {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 4px;
}

.username {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 12px;
}

.menu li {
  position: relative;
  margin-bottom: 8px;
  list-style: none;
  cursor: pointer;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
}

.menu-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
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
  margin-right: 12px;
}

.title {
  white-space: nowrap;
  font-weight: 500;
}

.main-content {
  flex: 1;
  background-color: white;
  border-radius: var(--border-radius-md);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  overflow-y: auto;
}

@media screen and (max-width: 768px) {
  .layout-container {
    flex-direction: column;
    padding: 16px;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .sidebar-header {
    flex-direction: row;
    align-items: center;
    text-align: left;
  }
  
  .avatar {
    width: 60px;
    height: 60px;
    margin-bottom: 0;
    margin-right: 16px;
  }
  
  .user-info {
    text-align: left;
  }
}
</style>
