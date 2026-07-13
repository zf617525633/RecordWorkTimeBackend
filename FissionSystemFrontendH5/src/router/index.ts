import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/auth',
    name: 'Auth',
    // 隐式鉴权页，用于接收App传过来的ticket并换取token
    component: () => import('../views/home/Auth.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/home/Home.vue')
  },
  {
    path: '/withdraw',
    name: 'Withdraw',
    component: () => import('../views/withdraw/Withdraw.vue')
  },
  {
    path: '/share',
    name: 'Share',
    component: () => import('../views/share/Share.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
