<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore, useUIStore } from '@/stores'
import { ElMessage, ElDropdown, ElDropdownMenu, ElDropdownItem, ElButton, ElIcon, ElBadge, ElAvatar } from 'element-plus'
import { 
  HomeFilled as Home, 
  ChatDotRound, 
  Document, 
  Calendar, 
  Clock,
  User, 
  Setting, 
  Search,
  Bell,
  Moon,
  Sunny,
  Menu,
  Close,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const uiStore = useUIStore()

// 响应式状态
const isMobileMenuOpen = ref(false)
const isScrolled = ref(false)
const unreadNotifications = ref(3) // 模拟未读通知数

// 导航菜单项
const navigationItems = [
  { name: '首页', path: '/home', icon: Home },
  { name: '论坛', path: '/forum', icon: ChatDotRound },
  { name: '课程表', path: '/course-table', icon: Document },
  { name: '日程', path: '/schedule', icon: Clock },
  { name: '资源', path: '/resource', icon: Calendar },
]

// 计算属性
const currentPath = computed(() => route.path)
const isDarkMode = computed(() => uiStore.isDarkMode)

// 处理滚动
const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

// 切换移动端菜单
const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

// 关闭移动端菜单
const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

// 导航到页面
const navigateTo = (path: string) => {
  router.push(path)
  closeMobileMenu()
}

// 用户操作
const handleProfileClick = () => {
  router.push('/profile')
  closeMobileMenu()
}

const handleSettingsClick = () => {
  router.push('/settings')
  closeMobileMenu()
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error('退出失败')
  }
  closeMobileMenu()
}

// 切换主题
const toggleTheme = () => {
  uiStore.toggleDarkMode()
}

// 生命周期
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="main-layout">
    <!-- 导航栏 -->
    <header 
      class="navbar"
      :class="{ 'navbar--scrolled': isScrolled }"
    >
      <div class="navbar__container">
        <!-- Logo -->
        <div class="navbar__brand" @click="navigateTo('/home')">
          <div class="brand-logo">
            <div class="logo-icon">
              <span class="logo-text">UL</span>
            </div>
            <span class="brand-name">UniLife</span>
          </div>
        </div>

        <!-- 桌面端导航 -->
        <nav class="navbar__nav">
          <div class="nav-items">
            <a
              v-for="item in navigationItems"
              :key="item.path"
              class="nav-item"
              :class="{ 'nav-item--active': currentPath.startsWith(item.path) }"
              @click="navigateTo(item.path)"
            >
              <el-icon class="nav-item__icon">
                <component :is="item.icon" />
              </el-icon>
              <span class="nav-item__text">{{ item.name }}</span>
            </a>
          </div>
        </nav>

        <!-- 右侧操作区 -->
        <div class="navbar__actions">
          <!-- 搜索按钮 -->
          <el-button
            class="action-btn"
            circle
            @click="navigateTo('/search')"
          >
            <el-icon><Search /></el-icon>
          </el-button>

          <!-- 通知按钮 -->
          <el-badge :value="unreadNotifications" :hidden="unreadNotifications === 0">
            <el-button
              class="action-btn"
              circle
              @click="navigateTo('/notifications')"
            >
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>

          <!-- 主题切换 -->
          <el-button
            class="action-btn"
            circle
            @click="toggleTheme"
          >
            <el-icon>
              <Sunny v-if="isDarkMode" />
              <Moon v-else />
            </el-icon>
          </el-button>

          <!-- 用户菜单 -->
          <el-dropdown v-if="userStore.isLoggedIn" trigger="click" placement="bottom-end">
            <div class="user-menu">
              <el-avatar 
                :size="36"
                :src="userStore.userInfo?.avatar"
                class="user-avatar"
              >
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfileClick">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item @click="handleSettingsClick">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 登录按钮 -->
          <el-button
            v-else
            type="primary"
            @click="navigateTo('/login')"
          >
            登录
          </el-button>

          <!-- 移动端菜单按钮 -->
          <el-button
            class="mobile-menu-btn"
            circle
            @click="toggleMobileMenu"
          >
            <el-icon>
              <Close v-if="isMobileMenuOpen" />
              <Menu v-else />
            </el-icon>
          </el-button>
        </div>
      </div>

      <!-- 移动端菜单 -->
      <transition name="mobile-menu">
        <div v-if="isMobileMenuOpen" class="mobile-menu">
          <div class="mobile-menu__content">
            <!-- 导航项 -->
            <div class="mobile-nav">
              <a
                v-for="item in navigationItems"
                :key="item.path"
                class="mobile-nav-item"
                :class="{ 'mobile-nav-item--active': currentPath.startsWith(item.path) }"
                @click="navigateTo(item.path)"
              >
                <el-icon class="mobile-nav-item__icon">
                  <component :is="item.icon" />
                </el-icon>
                <span class="mobile-nav-item__text">{{ item.name }}</span>
              </a>
            </div>

            <!-- 用户操作 -->
            <div v-if="userStore.isLoggedIn" class="mobile-user-actions">
              <div class="mobile-user-info">
                <el-avatar 
                  :size="48"
                  :src="userStore.userInfo?.avatar"
                >
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-details">
                  <span class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</span>
                  <span class="user-email">{{ userStore.userInfo?.email || '' }}</span>
                </div>
              </div>
              
              <div class="mobile-action-buttons">
                <el-button @click="handleProfileClick" style="width: 100%;">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-button>
                <el-button @click="handleSettingsClick" style="width: 100%;">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-button>
                <el-button type="danger" @click="handleLogout" style="width: 100%;">
                  退出登录
                </el-button>
              </div>
            </div>

            <div v-else class="mobile-auth-actions">
              <el-button type="primary" @click="navigateTo('/login')" style="width: 100%;">
                登录
              </el-button>
              <el-button @click="navigateTo('/register')" style="width: 100%;">
                注册
              </el-button>
            </div>
          </div>
        </div>
      </transition>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部导航（移动端） -->
    <nav class="bottom-nav">
      <a
        v-for="item in navigationItems"
        :key="item.path"
        class="bottom-nav-item"
        :class="{ 'bottom-nav-item--active': currentPath.startsWith(item.path) }"
        @click="navigateTo(item.path)"
      >
        <el-icon class="bottom-nav-item__icon">
          <component :is="item.icon" />
        </el-icon>
        <span class="bottom-nav-item__text">{{ item.name }}</span>
      </a>
    </nav>
  </div>
</template>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 导航栏样式 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: var(--z-fixed);
  background: var(--bg-overlay);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-light);
  transition: all var(--duration-200) var(--ease-out);
}

.navbar--scrolled {
  box-shadow: var(--shadow-lg);
  background: var(--bg-elevated);
}

.navbar__container {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 var(--space-6);
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo和品牌 */
.navbar__brand {
  cursor: pointer;
  user-select: none;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-500));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: var(--font-weight-bold);
  box-shadow: var(--shadow-primary);
}

.logo-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
}

.brand-name {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 桌面端导航 */
.navbar__nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-items {
  display: flex;
  gap: var(--space-2);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: all var(--duration-150) var(--ease-out);
  cursor: pointer;
  position: relative;
}

.nav-item::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--primary-500);
  border-radius: var(--radius-full);
  transition: all var(--duration-200) var(--ease-out);
  transform: translateX(-50%);
}

.nav-item:hover {
  color: var(--text-primary);
  background: var(--neutral-100);
}

.nav-item--active {
  color: var(--primary-600);
  background: var(--primary-50);
}

.nav-item--active::before {
  width: 24px;
}

.nav-item__icon {
  font-size: var(--font-size-lg);
}

/* 右侧操作区 */
.navbar__actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.action-btn {
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-light);
  background: var(--bg-elevated);
  color: var(--text-secondary);
  transition: all var(--duration-150) var(--ease-out);
}

.action-btn:hover {
  background: var(--neutral-100);
  color: var(--text-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

/* 用户菜单 */
.user-menu {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--duration-150) var(--ease-out);
}

.user-menu:hover {
  background: var(--neutral-100);
}

.user-avatar {
  box-shadow: var(--shadow-sm);
}

.user-name {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.dropdown-arrow {
  color: var(--text-light);
  transition: transform var(--duration-150) var(--ease-out);
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
}

/* 移动端菜单 */
.mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-light);
  box-shadow: var(--shadow-lg);
}

.mobile-menu__content {
  padding: var(--space-6);
}

.mobile-nav {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  margin-bottom: var(--space-6);
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: all var(--duration-150) var(--ease-out);
  cursor: pointer;
}

.mobile-nav-item:hover {
  background: var(--neutral-100);
  color: var(--text-primary);
}

.mobile-nav-item--active {
  background: var(--primary-50);
  color: var(--primary-600);
}

.mobile-nav-item__icon {
  font-size: var(--font-size-xl);
}

/* 移动端用户操作 */
.mobile-user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  background: var(--neutral-50);
  margin-bottom: var(--space-4);
}

.user-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-email {
  font-size: var(--font-size-sm);
  color: var(--text-light);
}

.mobile-action-buttons {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.mobile-auth-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

/* 主内容区 */
.main-content {
  flex: 1;
  margin-top: 72px;
  margin-bottom: 70px;
  min-height: calc(100vh - 142px);
}

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: var(--z-fixed);
  background: var(--bg-elevated);
  border-top: 1px solid var(--border-light);
  display: none;
  padding: var(--space-2) var(--space-4);
  box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.1);
}

.bottom-nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2);
  border-radius: var(--radius-md);
  color: var(--text-light);
  text-decoration: none;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  transition: all var(--duration-150) var(--ease-out);
  cursor: pointer;
}

.bottom-nav-item:hover {
  color: var(--text-secondary);
}

.bottom-nav-item--active {
  color: var(--primary-600);
  background: var(--primary-50);
}

.bottom-nav-item__icon {
  font-size: var(--font-size-lg);
}

/* 动画 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all var(--duration-200) var(--ease-out);
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .navbar__container {
    padding: 0 var(--space-4);
  }
}

@media (max-width: 768px) {
  .navbar__nav {
    display: none;
  }
  
  .mobile-menu-btn {
    display: flex;
  }
  
  .bottom-nav {
    display: flex;
  }
  
  .main-content {
    margin-bottom: 70px;
  }
  
  .user-name {
    display: none;
  }
}

@media (max-width: 480px) {
  .navbar__container {
    padding: 0 var(--space-3);
  }
  
  .brand-name {
    display: none;
  }
  
  .navbar__actions {
    gap: var(--space-2);
  }
}
</style> 