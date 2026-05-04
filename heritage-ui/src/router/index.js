import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import GalleryView from '../views/GalleryView.vue'
import LoginView from '../views/LoginView.vue'
import DetailView from '../views/DetailView.vue'
import DashboardView from '../views/DashboardView.vue'
import CreatorView from '../views/CreatorView.vue'
import ResourceView from '../views/ResourceView.vue'
import AuditView from '../views/AuditView.vue'
import AuditLogView from '../views/AuditLogView.vue'
import UsersView from '../views/UsersView.vue'
import ProfileView from '../views/ProfileView.vue'


const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/gallery', name: 'gallery', component: GalleryView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/resource/:id', name: 'detail', component: DetailView },
  {
    path: '/admin',
    name: 'dashboard',
    component: DashboardView,
    meta: { roles: ['ADMIN', 'CONTRIBUTOR'] }
  },
  {
    path: '/creator',
    name: 'creator',
    component: CreatorView,
    meta: { roles: ['ADMIN', 'CONTRIBUTOR'] }
  },
  {
    path: '/resources',
    name: 'resources',
    component: ResourceView,
    meta: { roles: ['ADMIN', 'CONTRIBUTOR'] }
  },
  {
    path: '/audit',
    name: 'audit',
    component: AuditView,
    meta: { roles: ['ADMIN'] }
  },
  {
    path: '/audit-logs',
    name: 'audit-logs',
    component: AuditLogView,
    meta: { roles: ['ADMIN'] }
  },
  {
    path: '/users',
    name: 'users',
    component: UsersView,
    meta: { roles: ['ADMIN'] }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { roles: ['ADMIN', 'CONTRIBUTOR', 'VIEWER'] }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Global navigation guard
router.beforeEach((to, from, next) => {
  const userRole = localStorage.getItem('userRole') || ''
  const requiredRoles = to.meta.roles

  if (requiredRoles && Array.isArray(requiredRoles)) {
    if (!requiredRoles.includes(userRole)) {
      // No permission, redirect to home
      next('/')
      return
    }
  }
  next()
})

export default router
