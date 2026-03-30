import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import ResourceView from '../views/ResourceView.vue'
import AuditView from '../views/AuditView.vue'
import AuditLogView from '../views/AuditLogView.vue'
import LoginView from '../views/LoginView.vue'
import HomeView from "@/views/HomeView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/admin', name: 'dashboard', component: DashboardView },
    { path: '/resources', name: 'resources', component: ResourceView },
    { path: '/audit', name: 'audit', component: AuditView },
    { path: '/audit-logs', name: 'auditLogs', component: AuditLogView }, // 👈 我在这里补上了逗号
    { path: '/users', name: 'users', component: () => import('../views/UsersView.vue') },
    { path: '/profile', name: 'profile', component: () => import('../views/ProfileView.vue') },
    { path: '/resource/:id', name: 'resourceDetail', component: () => import('../views/DetailView.vue') },
    { path: '/creator', name: 'creatorHub', component: () => import('../views/CreatorView.vue') }
  ]
})

export default router