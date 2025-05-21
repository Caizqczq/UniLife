import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useUserStore } from '../stores';

// 布局
import BaseLayout from '../layouts/BaseLayout.vue';
import PersonalLayout from '../layouts/PersonalLayout.vue';
import PublicLayout from '../layouts/PublicLayout.vue';

// Auth Pages
import LoginPage from '../views/auth/LoginPage.vue';
import RegisterPage from '../views/auth/RegisterPage.vue';
import ForgotPasswordPage from '../views/auth/ForgotPasswordPage.vue';

// 页面
// import Login from '../views/Login.vue'; // Replaced by LoginPage
import Home from '../views/Home.vue';
// import AccountManager from '../views/AccountManager.vue'; // Will be replaced by ProfilePage/SettingsPage
import NotFound from '../views/NotFound.vue';

// User Feature Pages
import ProfilePage from '../views/user/ProfilePage.vue';
import SettingsPage from '../views/user/SettingsPage.vue';

// Forum Feature Pages
import PostListPage from '../views/forum/PostListPage.vue';
import PostDetailPage from '../views/forum/PostDetailPage.vue';
import PostEditPage from '../views/forum/PostEditPage.vue';

// Resource Sharing Feature Pages
import ResourceListPage from '../views/resources/ResourceListPage.vue';
import ResourceDetailPage from '../views/resources/ResourceDetailPage.vue';
import ResourceUploadPage from '../views/resources/ResourceUploadPage.vue';

// Schedule and Course Management Feature Pages
import TimetablePage from '../views/schedule/TimetablePage.vue';
import CourseEditPage from '../views/schedule/CourseEditPage.vue';
import ScheduleManagementPage from '../views/schedule/ScheduleManagementPage.vue';

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
        name: 'Forum', // Keep name 'Forum' for consistency if it's referenced elsewhere
        component: PostListPage, // Use newly created PostListPage
        meta: { title: 'UniLife 论坛', requiresAuth: false }
      },
      // 帖子详情 - 无需登录
      {
        path: 'post/:postId',  // Changed :id to :postId for consistency with new components
        name: 'PostDetail',
        component: PostDetailPage, // Use newly created PostDetailPage
        props: true,
        meta: { title: '帖子详情 - UniLife', requiresAuth: false }
      },
      // 发布帖子 - 需要登录
      {
        path: 'post/create',  // Changed path for clarity
        name: 'PostCreate', // Changed name for clarity
        component: PostEditPage, // Use newly created PostEditPage
        meta: { title: '发布新帖 - UniLife', requiresAuth: true }
      },
      // 编辑帖子 - 需要登录
      {
        path: 'post/edit/:postId',  // Changed :id to :postId and path for clarity
        name: 'PostEdit', // Changed name for clarity
        component: PostEditPage, // Use newly created PostEditPage for editing as well
        props: true,
        meta: { title: '编辑帖子 - UniLife', requiresAuth: true }
      },
      // 学习资源 - 无需登录
      {
        path: 'resources',  // URL: /resources
        name: 'ResourceList', // Changed name for clarity
        component: ResourceListPage, // Use newly created ResourceListPage
        meta: { title: '学习资源 - UniLife', requiresAuth: false }
      },
      // 资源详情 - 无需登录
      {
        path: 'resources/:resourceId',  // Changed path for clarity and consistency
        name: 'ResourceDetail',    // Kept name, or could be UserResourceDetail
        component: ResourceDetailPage, // Use newly created ResourceDetailPage
        props: true,
        meta: { title: '资源详情 - UniLife', requiresAuth: false }
      },
      // 上传资源 - 需要登录
      {
        path: 'resources/upload',
        name: 'ResourceUpload',
        component: ResourceUploadPage, // Use newly created ResourceUploadPage
        meta: { title: '上传资源 - UniLife', requiresAuth: true }
      },
      // 编辑资源 - 需要登录 (Editing metadata, not the file itself)
      {
        path: 'resources/edit/:resourceId',
        name: 'ResourceEdit',
        component: ResourceUploadPage, // Re-use ResourceUploadPage for editing metadata
        props: true,
        meta: { title: '编辑资源 - UniLife', requiresAuth: true }
      },
      // Timetable Page - Requires Login
      {
        path: 'schedule/timetable', 
        name: 'TimetablePage',    // Renamed from 'Courses'
        component: TimetablePage, // Use newly created TimetablePage
        meta: { title: '我的课表 - UniLife', requiresAuth: true } // Usually requires auth
      },
      // Add/Edit Course Page - Requires Login
      {
        path: 'schedule/course/create',
        name: 'CourseCreate',
        component: CourseEditPage,
        meta: { title: '添加课程 - UniLife', requiresAuth: true }
      },
      {
        path: 'schedule/course/edit/:courseId',
        name: 'CourseEdit',
        component: CourseEditPage,
        props: true,
        meta: { title: '编辑课程 - UniLife', requiresAuth: true }
      },
      // Schedule Management Page - Requires Login
      {
        path: 'schedule/manage',   // Renamed from 'schedule' path
        name: 'ScheduleManagePage', // Renamed from 'Schedule'
        component: ScheduleManagementPage, // Use newly created ScheduleManagementPage
        meta: { title: '日程管理 - UniLife', requiresAuth: true }
      }
      // Note: The old /courses and /schedule routes are effectively replaced.
      // Any existing links to them would need to be updated if paths/names changed significantly.
    ]
  },

  // 认证相关页面 - 使用BaseLayout布局
  {
    path: '/',
    component: BaseLayout, // Assuming BaseLayout is suitable for auth pages
    children: [
      {
        path: 'login',
        name: 'Login',
        component: LoginPage,
        meta: { title: 'Login - UniLife', requiresAuth: false }
      },
      {
        path: 'register',
        name: 'Register',
        component: RegisterPage,
        meta: { title: 'Register - UniLife', requiresAuth: false }
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: ForgotPasswordPage,
        meta: { title: 'Forgot Password - UniLife', requiresAuth: false }
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
        path: 'home',        // URL: /personal/home - This can be a dashboard or landing for personal section
        name: 'PersonalHome',
        component: Home, // Assuming Home.vue is a general dashboard page
        meta: { title: '个人中心 - UniLife' }
      },
      {
        path: 'profile',     // URL: /personal/profile (current user's own profile)
        name: 'CurrentUserProfile',
        component: ProfilePage,
        meta: { title: '我的主页 - UniLife' }
      },
      {
        path: 'profile/:userId', // URL: /personal/profile/123 (another user's public profile)
        name: 'UserPublicProfile',
        component: ProfilePage,
        props: true, // Passes route.params.userId as a prop to ProfilePage
        meta: { title: '用户主页 - UniLife' }
      },
      {
        path: 'settings',    // URL: /personal/settings
        name: 'UserSettings',
        component: SettingsPage,
        meta: { title: '个人设置 - UniLife' }
      },
      {
        path: 'posts',       // URL: /personal/posts
        name: 'MyPosts', // This route seems to be for a user's own posts, keep as is.
        component: () => import('../views/forum/MyPostsView.vue'), // Assuming MyPostsView is a separate feature.
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
      // Default redirect for /personal itself
      // If /personal/home is the intended landing, this can redirect there,
      // or to /personal/profile if that's preferred.
      {
        path: '',
        redirect: '/personal/profile' // Defaulting to the user's own profile page
      }
    ]
  },

  // Catch-all 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '页面不存在 - UniLife', requiresAuth: false } // Ensure 404 is always accessible
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // always scroll to top when navigating to new routes
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

// 全局前置守卫
import { ElMessage } from 'element-plus'; // Import ElMessage for use in guard

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title as string) || 'UniLife 学生论坛';
  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn;

  if (to.matched.some(record => record.meta.requiresAuth) && !isLoggedIn) {
    ElMessage({
      message: '请先登录以访问此页面。',
      type: 'warning',
      duration: 3000,
    });
    next({
      name: 'Login',
      query: { redirect: to.fullPath }
    });
  } else if ((to.name === 'Login' || to.name === 'Register' || to.name === 'ForgotPassword') && isLoggedIn) {
    // If user is logged in and tries to access Login, Register, or ForgotPassword page, redirect to forum home
    next({ name: 'Forum' });
  } else {
    next();
  }
});

export default router;
