import type { RouteRecord, RouteRecordRaw } from 'vue-router';
import { createWebHashHistory, createRouter,createWebHistory } from 'vue-router';
import LogPage from './components/LogPage.vue';
import Personal from './components/Personal/Personal.vue'
import Manager from './components/Personal/AcountManager.vue';
import PersonalLayout from './components/PersonLayout.vue'
import PersonalHome from './components/Personal/Home.vue'

const routes:Array<RouteRecordRaw> = [
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
        ]
    },
    {
        path:"/personalLayout",
        name:'Personallayout',
        component:PersonalLayout,
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
