import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/common/LoginView.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      component: () => import('@/views/common/LayoutView.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        // 管理员
        { path: 'dashboard', component: () => import('@/views/admin/DashboardView.vue'), meta: { roles: [2] } },
        { path: 'admin/orders', component: () => import('@/views/admin/OrderManageView.vue'), meta: { roles: [2] } },
        { path: 'admin/users', component: () => import('@/views/admin/UserManageView.vue'), meta: { roles: [2] } },
        { path: 'admin/notices', component: () => import('@/views/admin/NoticeManageView.vue'), meta: { roles: [2] } },
        // 学生
        { path: 'student/submit', component: () => import('@/views/student/SubmitOrderView.vue'), meta: { roles: [0] } },
        { path: 'student/orders', component: () => import('@/views/student/MyOrdersView.vue'), meta: { roles: [0] } },
        // 维修工
        { path: 'worker/orders', component: () => import('@/views/worker/WorkerOrdersView.vue'), meta: { roles: [1] } },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
    return
  }
  if (to.meta.guest && authStore.isLoggedIn) {
    next('/')
    return
  }
  // 角色权限控制
  const roles = to.meta.roles as number[] | undefined
  if (roles && authStore.isLoggedIn) {
    const userRole = authStore.user?.role
    if (!roles.includes(userRole)) {
      // 重定向到该用户角色对应的默认首页
      const defaultPath = userRole === 0 ? '/student/orders'
        : userRole === 1 ? '/worker/orders'
        : '/'
      next(defaultPath)
      return
    }
  }
  next()
})

export default router
