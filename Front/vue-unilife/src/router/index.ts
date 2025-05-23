import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../stores';

// 布局
import BaseLayout from '../layouts/BaseLayout.vue';
import PersonalLayout from '../layouts/PersonalLayout.vue';
import PublicLayout from '../layouts/PublicLayout.vue';

// 页面
import Login from '../views/Login.vue';
import Home from '../views/Home.vue';
import AccountManager from '../views/AccountManager.vue';
import NotFound from '../views/NotFound.vue';

// 路由配置
const routes: Array<RouteRecordRaw> = [
  // 公共页面 - 使用PublicLayout布局
  {
    path: '/',
    component: PublicLayout,
    children: [
      // 论坛首页 - 无需登录
      {
        path: '',  // 网站根路径 /
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
      // 学习资源 - 无需登录
      {
        path: 'resources',  // URL: /resources
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
      // 课程表 - 无需登录
      {
        path: 'courses',    // URL: /courses
        name: 'Courses',
        component: () => import('../views/schedule/CourseTableView.vue'),
        meta: { title: '课程表 - UniLife', requiresAuth: false }
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

  // 个人中心页面 - 使用PersonalLayout布局
  {
    path: '/personal',
    component: PersonalLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',        // URL: /personal/home
        name: 'PersonalHome',
        component: Home,
        meta: { title: '个人主页 - UniLife' }
      },
      {
        path: 'account',     // URL: /personal/account
        name: 'AccountManager',
        component: AccountManager,
        meta: { title: '账号管理 - UniLife' }
      },
      {
        path: 'posts',       // URL: /personal/posts
        name: 'MyPosts',
        component: () => import('../views/forum/MyPostsView.vue'),
        meta: { title: '我的帖子 - UniLife' }
      },
      {
        path: 'resources',   // URL: /personal/resources
        name: 'MyResources',
        component: () => import('../views/resource/MyResourcesView.vue'),
        meta: { title: '我的资源 - UniLife' }
      },
      {
        path: 'messages',    // URL: /personal/messages
        name: 'Messages',
        component: () => import('../views/NotFound.vue'), // 占位符
        meta: { title: '消息中心 - UniLife' }
      },
      {
        path: 'settings',    // URL: /personal/settings
        name: 'Settings',
        component: () => import('../views/NotFound.vue'), // 占位符
        meta: { title: '设置 - UniLife' }
      },
      // 默认重定向到个人主页
      {
        path: '',
        redirect: '/personal/home'
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
    // 如果用户已登录但尝试访问登录页面，重定向到论坛首页
    next({ name: 'Forum' });
  } else {
    // 正常导航
    next();
  }
});

export default router;
