<template>
  <div v-if="$route.path === '/login'">
    <router-view />
  </div>

  <div class="common-layout" v-else>
    <el-container style="height: 100vh;">

      <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; z-index: 10; box-shadow: 0 2px 10px rgba(0,0,0,0.02);">
        <div style="display: flex; align-items: center;">
          <el-icon
              v-if="isBackendRoute"
              @click="isCollapse = !isCollapse"
              style="font-size: 22px; cursor: pointer; margin-right: 20px; color: #606266; transition: color 0.3s;"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>

          <div style="cursor: pointer; display: flex; align-items: center;" @click="router.push('/')">
            <span style="font-size: 18px; font-weight: bold; color: #2c3e50; letter-spacing: 1px;">🏛️ 社区遗产资源共享平台</span>
          </div>
        </div>

        <div style="display: flex; align-items: center;">
          <template v-if="currentUsername === '未登录'">
            <el-button type="primary" plain size="small" @click="router.push('/login')">登录 / 注册</el-button>
          </template>

          <template v-else>
            <el-popover placement="bottom" :width="350" trigger="click" @show="fetchNotes">
              <template #reference>
                <el-badge :is-dot="hasNewNote" style="margin-right: 25px; cursor: pointer;">
                  <el-icon :size="20"><Bell /></el-icon>
                </el-badge>
              </template>

              <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #ebeef5; padding-bottom: 10px; margin-bottom: 10px;">
                <span style="font-weight: bold; font-size: 15px; color: #303133;">📬 消息通知</span>
                <el-button v-if="hasNewNote" size="small" type="primary" link @click="markAllRead">一键标为已读</el-button>
              </div>

              <div class="notification-list">
                <div v-for="n in notes" :key="n.id" @click="markRead(n)" class="note-item" :style="{ backgroundColor: n.isRead === 0 ? '#f0f9ff' : '#fff' }">
                  <p style="margin: 0 0 5px 0; font-size: 13px; line-height: 1.5; color: #606266;">
                    <el-badge is-dot v-if="n.isRead === 0" style="margin-right: 8px;" />
                    {{ n.content }}
                  </p>
                  <span style="font-size: 12px; color: #909399;">{{ n.createdAt ? n.createdAt.replace('T', ' ').substring(0, 16) : '' }}</span>
                </div>
                <div v-if="notes.length === 0" style="text-align: center; color: #999; padding: 20px;">暂无系统通知</div>
              </div>
            </el-popover>

            <span style="margin-right: 15px; font-weight: bold; color: #409EFF;">{{ displayName }}</span>
            <el-button type="primary" size="small" plain @click="router.push('/admin')" v-if="currentUserRole && !isBackendRoute">进入后台</el-button>
            <el-button type="success" size="small" plain @click="router.push('/profile')">个人中心</el-button>
            <el-button type="danger" size="small" plain @click="logout">退出登录</el-button>
          </template>
        </div>
      </el-header>

      <el-container style="height: calc(100vh - 60px);">
        <el-aside
            v-if="isBackendRoute"
            :width="isCollapse ? '64px' : '240px'"
            style="background-color: #2c3e50; transition: width 0.3s ease-in-out; overflow-x: hidden;"
        >
          <div style="color: white; text-align: center; padding: 20px 0; font-weight: bold; white-space: nowrap;">
            <span v-if="!isCollapse" style="font-size: 20px;">遗产策展后台</span>
            <span v-else style="font-size: 14px;">后台</span>
          </div>

          <el-menu
              :collapse="isCollapse"
              :collapse-transition="false"
              router
              active-text-color="#409EFF"
              background-color="#2c3e50"
              text-color="#fff"
              :default-active="$route.path"
              style="border-right: none;"
          >
            <el-menu-item index="/">
              <el-icon><i style="font-style: normal; font-size: 18px;">🏠</i></el-icon>
              <template #title><span>前台展览大厅</span></template>
            </el-menu-item>

            <el-menu-item index="/admin">
              <el-icon><i style="font-style: normal; font-size: 18px;">📊</i></el-icon>
              <template #title><span>控制台大厅</span></template>
            </el-menu-item>

            <el-menu-item index="/creator" v-if="currentUserRole === 'ADMIN' || currentUserRole === 'CONTRIBUTOR'">
              <el-icon><i style="font-style: normal; font-size: 18px;">🎨</i></el-icon>
              <template #title><span>我的创作者中心</span></template>
            </el-menu-item>

            <el-menu-item index="/resources">
              <el-icon><i style="font-style: normal; font-size: 18px;">📂</i></el-icon>
              <template #title><span>资源主数据管理</span></template>
            </el-menu-item>

            <el-menu-item index="/audit" v-if="currentUserRole === 'ADMIN'">
              <el-icon><i style="font-style: normal; font-size: 18px;">🛡️</i></el-icon>
              <template #title><span>资源审核与状态</span></template>
            </el-menu-item>

            <el-menu-item index="/audit-logs" v-if="currentUserRole === 'ADMIN'">
              <el-icon><i style="font-style: normal; font-size: 18px;">📝</i></el-icon>
              <template #title><span>系统审计日志大屏</span></template>
            </el-menu-item>

            <el-menu-item index="/users" v-if="currentUserRole === 'ADMIN'">
              <el-icon><i style="font-style: normal; font-size: 18px;">👥</i></el-icon>
              <template #title><span>用户与权限管理</span></template>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-main style="background-color: #f3f4f7; padding: 0;">
          <router-view :key="$route.fullPath" style="padding: 20px; min-height: 100%; box-sizing: border-box;" />
        </el-main>
      </el-container>

    </el-container>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell, Fold, Expand } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

const currentUsername = ref(localStorage.getItem('currentUser') || '未登录')
const currentUserRole = ref(localStorage.getItem('userRole') || '')
const displayName = ref(currentUsername.value)
const notes = ref([])
const isCollapse = ref(false)

const hasNewNote = computed(() => notes.value.some(note => note.isRead === 0))

const isBackendRoute = computed(() => {
  const backendPaths = ['/admin', '/resources', '/audit', '/audit-logs', '/users', '/profile', '/creator']
  return backendPaths.includes(route.path)
})

const fetchNickname = async () => {
  if (currentUsername.value && currentUsername.value !== '未登录') {
    try {
      const res = await axios.get(`http://localhost:8080/api/users/profile?username=${currentUsername.value}`)
      if (res.data && res.data.nickname) { displayName.value = res.data.nickname }
      else { displayName.value = currentUsername.value }
    } catch (error) { displayName.value = currentUsername.value }
  } else { displayName.value = '未登录' }
}

const fetchNotes = async () => {
  if (currentUsername.value === '未登录') return
  try {
    const res = await axios.get(`http://localhost:8080/api/notifications?username=${currentUsername.value}`)
    notes.value = res.data
  } catch (err) {}
}

const markRead = async (note) => {
  if (note.isRead === 0) {
    try {
      await axios.put(`http://localhost:8080/api/notifications/${note.id}/read`)
      fetchNotes()
    } catch (err) {}
  }
}

// ✨ PBI 4: 一键全部标为已读
const markAllRead = async () => {
  if (currentUsername.value === '未登录') return
  try {
    await axios.put(`http://localhost:8080/api/notifications/mark-all-read?username=${currentUsername.value}`)
    fetchNotes()
  } catch (err) {}
}

const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('userRole')
  currentUsername.value = '未登录'
  currentUserRole.value = ''
  displayName.value = '未登录'
  router.push('/')
}

watch(() => route.path, () => {
  currentUsername.value = localStorage.getItem('currentUser') || '未登录'
  currentUserRole.value = localStorage.getItem('userRole') || ''
  fetchNickname()
  fetchNotes()
})

onMounted(() => {
  fetchNickname()
  fetchNotes()
  setInterval(fetchNotes, 15000)
})
</script>

<style>
html, body { margin: 0; padding: 0; width: 100%; height: 100%; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; }
#app { max-width: none !important; width: 100% !important; height: 100% !important; padding: 0 !important; margin: 0 !important; }

.note-item { padding: 12px 15px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.2s; }
.note-item:hover { background-color: #f5f7fa !important; }
.note-item:last-child { border-bottom: none; }
.notification-list { max-height: 400px; overflow-y: auto; }
</style>