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
router.beforeEach((to) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return '/login'
  }
  
  if (to.name === 'login' && userStore.isLoggedIn) {
    return '/forum'
  }
})

export default router
