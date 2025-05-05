import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../stores';

// 布局
import BaseLayout from '../layouts/BaseLayout.vue';
import PersonalLayout from '../layouts/PersonalLayout.vue';

// 页面
import Login from '../views/Login.vue';
import Home from '../views/Home.vue';
import AccountManager from '../views/AccountManager.vue';
import NotFound from '../views/NotFound.vue';

// 路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: BaseLayout,
    children: [
      {
        path: '',
        redirect: '/login'
      },
      {
        path: 'login',
        name: 'Login',
        component: Login,
        meta: {
          title: '登录 - UniLife学生论坛',
          requiresAuth: false
        }
      }
    ]
  },
  {
    path: '/personal',
    component: PersonalLayout,
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Home',
        component: Home,
        meta: {
          title: '个人主页 - UniLife学生论坛',
          requiresAuth: true
        }
      },
      {
        path: 'account',
        name: 'AccountManager',
        component: AccountManager,
        meta: {
          title: '账号管理 - UniLife学生论坛',
          requiresAuth: true
        }
      },
      // 其他个人中心页面可以在这里添加
      {
        path: 'posts',
        name: 'Posts',
        component: () => import('../views/NotFound.vue'), // 暂时使用NotFound页面
        meta: {
          title: '我的帖子 - UniLife学生论坛',
          requiresAuth: true
        }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('../views/NotFound.vue'), // 暂时使用NotFound页面
        meta: {
          title: '消息中心 - UniLife学生论坛',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/NotFound.vue'), // 暂时使用NotFound页面
        meta: {
          title: '设置 - UniLife学生论坛',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '页面不存在 - UniLife学生论坛'
    }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title as string || 'UniLife学生论坛';

  // 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userStore = useUserStore();

    // 如果需要登录但用户未登录，重定向到登录页
    if (!userStore.isLoggedIn) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
