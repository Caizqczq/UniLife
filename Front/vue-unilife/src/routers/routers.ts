import type { RouteRecord, RouteRecordRaw } from 'vue-router';
import { createWebHashHistory, createRouter,createWebHistory } from 'vue-router';
import LogPage from '../views/LogPage.vue';
import Personal from '@/components/Personal.vue';
import Manager from '@/views/AcountManager.vue';
import PersonalHome from '@/views/Home.vue';
import ForumHome from '@/views/ForumHome.vue';

const routes:Array<RouteRecordRaw> = [
    {
        path: '/',
        redirect: '/log',
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/404.vue'),
    },
    {
        path:'/log',
        name: 'LogPage',
        component: LogPage
    },
    {
        path:'/personal',
        name: 'Personal',
        component: Personal,
        children: [
            {
                path:'',
                name:'Home',
                component:PersonalHome,
            },
            {
                path:'manager',
                name: 'Manager',
                component:Manager,
            },
            {
                path:'ai',
                redirect: '/personal',
            },
        ]
    },
    {
        path:'/uniLifeHome',
        name: 'ForumHome',
        component: ForumHome,
    },
    {
        path:'/post/:id',
        name:'PostDetail',
        component: () => import('@/views/PostDetailPage.vue'),
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
