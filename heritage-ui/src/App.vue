<template>
  <div v-if="$route.path === '/login' || $route.path === '/'">
    <router-view />
  </div>

  <div class="common-layout" v-else>
    <el-container style="height: 100vh;">

      <el-aside width="240px" style="background-color: #2c3e50;">
        <div style="color: white; text-align: center; padding: 20px 0; font-size: 20px; font-weight: bold;">
          遗产策展后台
        </div>
        <el-menu
            router
            active-text-color="#409EFF"
            background-color="#2c3e50"
            text-color="#fff"
            :default-active="$route.path"
            style="border-right: none;"
        >
          <el-menu-item index="/admin"><span>📊 控制台大厅</span></el-menu-item>
          <el-menu-item index="/resources"><span>📂 资源主数据管理</span></el-menu-item>

          <el-menu-item index="/audit" v-if="currentUserRole === 'ADMIN'">
            <span>🛡️ 资源审核与状态</span>
          </el-menu-item>
          <el-menu-item index="/audit-logs" v-if="currentUserRole === 'ADMIN'">
            <span>📝 系统审计日志大屏</span>
          </el-menu-item>
          <el-menu-item index="/users" v-if="currentUserRole === 'ADMIN'">
            <span>👥 用户与权限管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between;">
          <span style="font-size: 18px; font-weight: bold;">社区遗产资源共享平台</span>

          <div style="display: flex; align-items: center;">
            <el-popover placement="bottom" :width="320" trigger="click" @show="fetchNotes">
              <template #reference>
                <el-badge :is-dot="hasNewNote" style="margin-right: 25px; cursor: pointer;">
                  <el-icon :size="20"><Bell /></el-icon>
                </el-badge>
              </template>

              <div class="notification-list">
                <div
                    v-for="n in notes"
                    :key="n.id"
                    @click="markRead(n)"
                    class="note-item"
                    :style="{ backgroundColor: n.isRead === 0 ? '#f0f9ff' : '#fff' }"
                >
                  <p style="margin: 0 0 5px 0; font-size: 14px; line-height: 1.4;">
                    <el-badge is-dot v-if="n.isRead === 0" style="margin-right: 8px;" />
                    {{ n.content }}
                  </p>
                  <span style="font-size: 12px; color: #999;">{{ n.createdAt }}</span>
                </div>
                <div v-if="notes.length === 0" style="text-align: center; color: #999; padding: 20px;">
                  暂无系统通知
                </div>
              </div>
            </el-popover>

            <span style="margin-right: 15px; font-weight: bold; color: #409EFF;">
              {{ currentUsername }} ({{ currentUserRole }})
            </span>
            <el-button type="danger" size="small" plain @click="logout">退出登录</el-button>
          </div>
        </el-header>

        <el-main style="background-color: #f3f4f7; padding: 20px;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

// --- 用户状态属性 ---
const currentUsername = ref(localStorage.getItem('currentUser') || '未登录')
const currentUserRole = ref(localStorage.getItem('userRole') || '')

// --- 消息通知属性 ---
const notes = ref([])

// 计算属性：只要有一条消息未读，铃铛就显示红点
const hasNewNote = computed(() => {
  return notes.value.some(note => note.isRead === 0)
})

// --- 核心逻辑函数 ---

// 获取通知列表
const fetchNotes = async () => {
  if (currentUsername.value === '未登录') return
  try {
    const res = await axios.get(`http://localhost:8080/api/notifications?username=${currentUsername.value}`)
    notes.value = res.data
  } catch (err) {
    console.error("获取通知失败")
  }
}

// 标记单条通知为已读
const markRead = async (note) => {
  if (note.isRead === 0) {
    try {
      await axios.put(`http://localhost:8080/api/notifications/${note.id}/read`)
      fetchNotes() // 刷新列表，红点会自动消失
    } catch (err) {
      console.error("标记已读失败")
    }
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
  localStorage.removeItem('userRole')
  currentUsername.value = '未登录'
  currentUserRole.value = ''
  router.push('/login')
}

// 监听路由变化，实时更新用户信息（解决登录后状态不刷新的问题）
watch(() => route.path, () => {
  currentUsername.value = localStorage.getItem('currentUser') || '未登录'
  currentUserRole.value = localStorage.getItem('userRole') || ''
  fetchNotes()
})

// 每隔 15 秒轮询一次新消息
onMounted(() => {
  fetchNotes()
  setInterval(fetchNotes, 15000)
})
</script>

<style>
/* 全局样式清理 */
html, body { margin: 0; padding: 0; width: 100%; height: 100%; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; }
#app { max-width: none !important; width: 100% !important; height: 100% !important; padding: 0 !important; margin: 0 !important; }

/* 消息列表样式 */
.note-item {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}
.note-item:hover {
  background-color: #f5f7fa !important;
}
.note-item:last-child {
  border-bottom: none;
}
.notification-list {
  max-height: 400px;
  overflow-y: auto;
}
</style>