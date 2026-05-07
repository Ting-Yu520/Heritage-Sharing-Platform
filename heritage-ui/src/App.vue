<template>
  <div v-if="$route.path === '/login'">
    <router-view />
  </div>

  <div class="common-layout" v-else>
    <el-container style="height: 100vh;">

      <!-- Top Header -->
      <el-header
        style="
          background-color: #ffffff;
          border-bottom: 1px solid #e5e5e5;
          display: flex;
          align-items: center;
          justify-content: space-between;
          z-index: 10;
          box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        "
      >
        <!-- Left: collapse button + brand -->
        <div style="display: flex; align-items: center;">
          <el-icon
            v-if="isBackendRoute"
            @click="isCollapse = !isCollapse"
            style="font-size: 22px; cursor: pointer; margin-right: 20px; color: #111;"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>

          <div style="cursor: pointer;" @click="router.push('/')">
            <span style="font-size: 24px; font-weight: 700; letter-spacing: 4px; color: #111;">
              HERITAGE
            </span>
          </div>
        </div>

        <!-- Right: user area -->
        <div style="display: flex; align-items: center;">
          <template v-if="currentUsername === 'Not logged in'">
            <el-button type="primary" plain size="small" @click="router.push('/login')">
              Login / Register
            </el-button>
          </template>

          <template v-else>
            <!-- Notification bell -->
            <el-popover placement="bottom" :width="350" trigger="click" @show="fetchNotes">
              <template #reference>
                <el-badge :is-dot="hasNewNote" style="margin-right: 25px; cursor: pointer;">
                  <el-icon :size="20"><Bell /></el-icon>
                </el-badge>
              </template>

              <div
                style="
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  border-bottom: 1px solid #ebeef5;
                  padding-bottom: 10px;
                  margin-bottom: 10px;
                "
              >
                <span style="font-weight: bold; font-size: 15px; color: #111;">Notifications</span>
                <el-button v-if="hasNewNote" size="small" type="primary" link @click="markAllRead">
                  Mark All as Read
                </el-button>
              </div>

              <div class="notification-list">
                <div
                  v-for="n in notes"
                  :key="n.id"
                  @click="markRead(n)"
                  class="note-item"
                  :style="{ backgroundColor: n.isRead === 0 ? '#f0f9ff' : '#fff' }"
                >
                  <p style="margin: 0 0 5px 0; font-size: 13px; line-height: 1.5; color: #606266;">
                    <el-badge is-dot v-if="n.isRead === 0" style="margin-right: 8px;" />
                    {{ n.content }}
                  </p>
                  <span style="font-size: 12px; color: #909399;">
                    {{ n.createdAt ? n.createdAt.replace('T', ' ').substring(0, 16) : '' }}
                  </span>
                </div>
                <div v-if="notes.length === 0" style="text-align: center; color: #999; padding: 20px;">
                  No system notifications
                </div>
              </div>
            </el-popover>

            <!-- User nickname -->
            <span style="margin-right: 15px; font-weight: 500; color: #111; font-size: 15px;">
              {{ displayName }}
            </span>

            <!-- Action buttons (backend entry restricted to ADMIN/CONTRIBUTOR) -->
            <el-button class="plain-btn" @click="router.push('/admin')" v-if="(currentUserRole === 'ADMIN' || currentUserRole === 'CONTRIBUTOR') && !isBackendRoute">
              Enter Backend
            </el-button>
            <el-button class="plain-btn" @click="router.push('/profile')">Profile</el-button>
            <el-button class="plain-btn danger" @click="logout">Logout</el-button>
          </template>
        </div>
      </el-header>

      <!-- Main content area -->
      <el-container style="height: calc(100vh - 60px);">
        <!-- Sidebar (only shown on backend routes) -->
        <el-aside
          v-if="isBackendRoute"
          :width="isCollapse ? '64px' : '300px'"
          style="
            background-color: #ffffff;
            transition: width 0.3s ease-in-out;
            overflow-x: hidden;
            border-right: 1px solid #e5e5e5;
          "
        >
          <el-menu
            :collapse="isCollapse"
            :collapse-transition="false"
            router
            active-text-color="#1e3a2f"
            background-color="#ffffff"
            text-color="#1f2937"
            :default-active="$route.path"
            style="border-right: none;"
          >
            <!-- Dashboard (both ADMIN and CONTRIBUTOR) -->
            <el-menu-item index="/admin">
              <el-icon><Odometer /></el-icon>
              <template #title>Dashboard</template>
            </el-menu-item>

            <!-- Creator Center (ADMIN/CONTRIBUTOR) -->
            <el-menu-item index="/creator" v-if="currentUserRole === 'ADMIN' || currentUserRole === 'CONTRIBUTOR'">
              <el-icon><Edit /></el-icon>
              <template #title>My Creator Center</template>
            </el-menu-item>

            <!-- Resource Master Data Management (ADMIN only) -->
            <el-menu-item index="/resources" v-if="currentUserRole === 'ADMIN'">
              <el-icon><FolderOpened /></el-icon>
              <template #title>Resource Master Data Management</template>
            </el-menu-item>

            <!-- Resource Review & Status (ADMIN only) -->
            <el-menu-item index="/audit" v-if="currentUserRole === 'ADMIN'">
              <el-icon><Checked /></el-icon>
              <template #title>Resource Review & Status</template>
            </el-menu-item>

            <!-- System Audit Log (ADMIN only) -->
            <el-menu-item index="/audit-logs" v-if="currentUserRole === 'ADMIN'">
              <el-icon><Document /></el-icon>
              <template #title>System Audit Log Dashboard</template>
            </el-menu-item>

            <!-- User & Permission Management (ADMIN only) -->
            <el-menu-item index="/users" v-if="currentUserRole === 'ADMIN'">
              <el-icon><UserFilled /></el-icon>
              <template #title>User & Permission Management</template>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- Main content -->
        <el-main style="background-color: #ffffff; padding: 0;">
          <router-view
            :key="$route.fullPath"
            style="padding: 20px; min-height: 100%; box-sizing: border-box;"
          />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Bell, Fold, Expand, Odometer, Edit, FolderOpened, Checked, Document, UserFilled
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

const currentUsername = ref(localStorage.getItem('currentUser') || 'Not logged in')
const currentUserRole = ref(localStorage.getItem('userRole') || '')
const displayName = ref(currentUsername.value)

const notes = ref([])
const isCollapse = ref(false)

const hasNewNote = computed(() => notes.value.some(note => note.isRead === 0))

// Backend route determination
const isBackendRoute = computed(() => {
  const backendPaths = ['/admin', '/resources', '/audit', '/audit-logs', '/users', '/profile', '/creator']
  // VIEWERs are not allowed to see the sidebar, even if the URL matches
  if (currentUserRole.value === 'VIEWER' || !currentUserRole.value) return false
  return backendPaths.includes(route.path)
})

const updateUserInfo = () => {
  currentUsername.value = localStorage.getItem('currentUser') || 'Not logged in'
  currentUserRole.value = localStorage.getItem('userRole') || ''
  displayName.value = currentUsername.value
}

const fetchNickname = async () => {
  if (currentUsername.value === 'Not logged in') return
  try {
    const res = await axios.get(`http://116.62.165.182/api/users/profile?username=${currentUsername.value}`)
    if (res.data && res.data.nickname) {
      displayName.value = res.data.nickname
    }
  } catch (e) {}
}

const fetchNotes = async () => {
  if (currentUsername.value === 'Not logged in') return
  try {
    const res = await axios.get(`http://116.62.165.182/api/notifications?username=${currentUsername.value}`)
    notes.value = res.data
  } catch (err) {}
}

const markRead = async (note) => {
  if (note.isRead === 0) {
    try {
      await axios.put(`http://116.62.165.182/api/notifications/${note.id}/read`)
      fetchNotes()
    } catch (err) {}
  }
}

const markAllRead = async () => {
  if (currentUsername.value === 'Not logged in') return
  try {
    await axios.put(`http://116.62.165.182/api/notifications/mark-all-read?username=${currentUsername.value}`)
    fetchNotes()
  } catch (err) {}
}

const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('userRole')
  updateUserInfo()
  router.push('/login')
}

watch(() => route.path, () => {
  updateUserInfo()
  nextTick(() => {
    fetchNickname()
    fetchNotes()
  })
})

onMounted(() => {
  updateUserInfo()
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