import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../stores';

// 布局
import MainLayout from '../layouts/MainLayout.vue';
import BaseLayout from '../layouts/BaseLayout.vue';

// 页面
import Login from '../views/Login.vue';
import Home from '../views/Home.vue';
import NotFound from '../views/NotFound.vue';

// 路由配置
const routes: Array<RouteRecordRaw> = [
  // 主应用布局 - 使用MainLayout
  {
    path: '/',
    component: MainLayout,
    children: [
      // 个人主页 - 需要登录
      {
        path: 'home',      // URL: /home
        name: 'Home',
        component: Home,
        meta: { title: '个人主页 - UniLife', requiresAuth: true }
      },
      // 论坛首页 - 无需登录
      {
        path: 'forum',     // URL: /forum  
        name: 'Forum',
        component: () => import('../views/forum/PostListView.vue'),
        meta: { title: '论坛广场 - UniLife', requiresAuth: false }
      },
      // 帖子详情 - 无需登录
      {
        path: 'post/:id',  // URL: /post/123
        name: 'PostDetail',
        component: () => import('../views/forum/PostDetailView.vue'),
        props: true,
        meta: { title: '帖子详情 - UniLife', requiresAuth: false }
      },
      // 发布帖子 - 需要登录
      {
        path: 'create-post',  // URL: /create-post
        name: 'CreatePost',
        component: () => import('../views/forum/CreatePostView.vue'),
        meta: { title: '发布帖子 - UniLife', requiresAuth: true }
      },
      // 编辑帖子 - 需要登录
      {
        path: 'edit-post/:id',  // URL: /edit-post/123
        name: 'EditPost',
        component: () => import('../views/forum/CreatePostView.vue'),
        props: true,
        meta: { title: '编辑帖子 - UniLife', requiresAuth: true }
      },
      // 我的帖子 - 需要登录
      {
        path: 'my-posts',       // URL: /my-posts
        name: 'MyPosts',
        component: () => import('../views/forum/MyPostsView.vue'),
        meta: { title: '我的帖子 - UniLife', requiresAuth: true }
      },
      // 学习资源 - 无需登录
      {
        path: 'resource',  // URL: /resource
        name: 'Resources',
        component: () => import('../views/resource/ResourceListView.vue'),
        meta: { title: '学习资源 - UniLife', requiresAuth: false }
      },
      // 资源详情 - 无需登录
      {
        path: 'resource/:id',  // URL: /resource/123
        name: 'ResourceDetail',
        component: () => import('../views/resource/ResourceDetailView.vue'),
        props: true,
        meta: { title: '资源详情 - UniLife', requiresAuth: false }
      },
      // 课程表管理 - 需要登录
      {
        path: 'course-table',   // URL: /course-table
        name: 'CourseTable',
        component: () => import('../views/schedule/CourseTableView.vue'),
        meta: { title: '课程表 - UniLife', requiresAuth: true }
      },
      // 日程管理 - 需要登录
      {
        path: 'schedule',   // URL: /schedule
        name: 'Schedule',
        component: () => import('../views/schedule/ScheduleView.vue'),
        meta: { title: '日程管理 - UniLife', requiresAuth: true }
      },
      // 搜索页面 - 无需登录
      {
        path: 'search',     // URL: /search
        name: 'Search',
        component: () => import('../views/SearchView.vue'),
        meta: { title: '搜索 - UniLife', requiresAuth: false }
      },
      // 个人资料 - 需要登录
      {
        path: 'profile',     // URL: /profile
        name: 'Profile',
        component: () => import('../views/AccountManager.vue'),
        meta: { title: '个人资料 - UniLife', requiresAuth: true }
      },
      // 消息中心 - 需要登录
      {
        path: 'messages',    // URL: /messages
        name: 'Messages',
        component: () => import('../views/MessagesView.vue'),
        meta: { title: '消息中心 - UniLife', requiresAuth: true }
      },
      // 设置页面 - 需要登录
      {
        path: 'settings',    // URL: /settings
        name: 'Settings',
        component: () => import('../views/SettingsView.vue'),
        meta: { title: '设置 - UniLife', requiresAuth: true }
      },
      // 通知页面 - 需要登录
      {
        path: 'notifications',    // URL: /notifications
        name: 'Notifications',
        component: () => import('../views/NotFound.vue'), // 临时使用NotFound页面
        meta: { title: '通知 - UniLife', requiresAuth: true }
      },
      // 根路径重定向
      {
        path: '',  // 网站根路径 /
        redirect: (to) => {
          const userStore = useUserStore();
          return userStore.isLoggedIn ? '/home' : '/forum';
        }
      }
    ]
  },

  // 认证相关页面 - 使用BaseLayout布局
  {
    path: '/',
    component: BaseLayout,
    children: [
      {
        path: 'login',      // URL: /login
        name: 'Login',
        component: Login,
        meta: { title: '登录/注册 - UniLife', requiresAuth: false }
      }
    ]
  },

  // Catch-all 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '页面不存在 - UniLife' }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

// 全局前置守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title as string || 'UniLife学生论坛';
  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn;

  // 如果路由需要认证但用户未登录
  if (to.matched.some(record => record.meta.requiresAuth) && !isLoggedIn) {
    next({
      name: 'Login',
      query: { redirect: to.fullPath } // 保存原始路径用于登录后重定向
    });
  } else if ((to.name === 'Login') && isLoggedIn) {
    // 如果用户已登录但尝试访问登录页面，重定向到首页
    next({ path: '/home' });
  } else {
    // 正常导航
    next();
  }
});

export default router;
