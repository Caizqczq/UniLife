import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue')
    },
    {
      path: '/forum',
      name: 'forum',
      component: () => import('@/views/forum/ForumView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/forum/post/:id',
      name: 'post-detail',
      component: () => import('@/views/forum/PostDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/resources',
      name: 'resources',
      component: () => import('@/views/resources/ResourcesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/resources/:id',
      name: 'resource-detail',
      component: () => import('@/views/resources/ResourceDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/schedule',
      name: 'schedule',
      component: () => import('@/views/schedule/ScheduleView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/tasks',
      name: 'tasks',
      component: () => import('@/views/schedule/TaskView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/ai-assistant',
      name: 'ai-assistant',
      component: () => import('@/views/ai/AIAssistantView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/profile/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/admin/AdminDashboard.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    }
    // {
    //   path: '/courses',
    //   name: 'courses',
    //   component: () => import('@/views/schedule/CoursesView.vue'),
    //   meta: { requiresAuth: true }
    // }
  ]
})

// 路由守卫
router.beforeEach(async (to) => {
  const userStore = useUserStore()
  
  // 如果需要认证但未登录，跳转到登录页
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return '/login'
  }
  
  // 如果已登录但用户信息为空，先获取用户信息
  if (userStore.isLoggedIn && !userStore.user) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      // 如果获取用户信息失败，可能token已过期，清除登录状态
      userStore.logout()
      return '/login'
    }
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin && userStore.user?.role !== 2) {
    return '/forum'
  }
  
  // 只限制已登录用户访问登录页面，允许访问注册页面
  if (to.name === 'login' && userStore.isLoggedIn) {
    // 如果是管理员，跳转到管理后台
    if (userStore.user?.role === 2) {
      return '/admin'
    }
    // 普通用户跳转到论坛
    return '/forum'
  }
})

export default router
