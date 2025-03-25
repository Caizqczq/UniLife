import type { RouteRecord, RouteRecordRaw } from 'vue-router';
import { createWebHashHistory, createRouter,createWebHistory } from 'vue-router';
import LogPage from './components/LogPage.vue';
import Personal from './components/Personal.vue';

const routes:Array<RouteRecordRaw> = [
    {
        path:'/log',
        name: 'LogPage',
        component: LogPage
    },
    {
        path:'/personal',
        name: 'Personal',
        component: Personal
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
