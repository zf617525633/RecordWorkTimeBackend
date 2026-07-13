import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import BasicLayout from '../layout/BasicLayout.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { title: '后台登录', noAuth: true }
  },
  {
    path: '/',
    component: BasicLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'withdraw',
        name: 'WithdrawAudit',
        component: () => import('../views/withdraw/WithdrawAudit.vue'),
        meta: { title: '提现审核' }
      },
      {
        path: 'app',
        name: 'AppManage',
        component: () => import('../views/app/AppManage.vue'),
        meta: { title: '租户应用管理', superAdminOnly: true }
      },
      {
        path: 'admin',
        name: 'AdminManage',
        component: () => import('../views/admin/AdminManage.vue'),
        meta: { title: '管理员管理', superAdminOnly: true }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token');
  const role = localStorage.getItem('admin_role');

  if (to.meta.title) {
    document.title = (to.meta.title as string) + ' - 裂变系统';
  }

  if (!to.meta.noAuth && !token) {
    next('/login');
  } else if (to.meta.superAdminOnly && role !== '1') {
    next('/dashboard'); // 没权限的普通管理员强制回看板
  } else {
    next();
  }
});

export default router;
