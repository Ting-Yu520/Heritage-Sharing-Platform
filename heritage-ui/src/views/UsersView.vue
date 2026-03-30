<template>
  <div class="users-container">
    <h2 style="margin-bottom: 20px;">👥 用户与权限管理中心</h2>

    <el-tabs v-model="activeTab" type="border-card">

      <el-tab-pane label="📫 贡献者申请审批" name="applications">
        <el-table :data="applicationList" border stripe v-loading="loadingApps">
          <el-table-column prop="username" label="申请人账号" width="150" />
          <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="提交时间" width="180" />
          <el-table-column label="审批操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="success" @click="processApp(scope.row.id, 1)">通过</el-button>
              <el-button size="small" type="danger" @click="processApp(scope.row.id, 2)">驳回</el-button>
            </template>
          </el-table-column>
          <template #empty><el-empty description="当前没有待处理的晋升申请。" /></template>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="🎛️ 用户角色大盘" name="allUsers">
        <el-table :data="userList" border stripe v-loading="loadingUsers">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="登录账号" width="150" />
          <el-table-column prop="email" label="注册邮箱" width="200" />
          <el-table-column prop="realName" label="姓名/昵称" width="150" />
          <el-table-column label="角色权限控制" min-width="200">
            <template #default="scope">
              <el-select
                  v-model="scope.row.role"
                  @change="(newRole) => handleRoleChange(scope.row.id, scope.row.username, newRole)"
                  :disabled="scope.row.role === 'ADMIN'" style="width: 150px;">
                <el-option label="VIEWER (浏览者)" value="VIEWER" />
                <el-option label="CONTRIBUTOR (贡献者)" value="CONTRIBUTOR" />
                <el-option label="ADMIN (管理员)" value="ADMIN" disabled />
              </el-select>
              <el-tag v-if="scope.row.role === 'ADMIN'" type="danger" style="margin-left: 10px;">超级管理员</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="🛡️ 评论风控中心" name="commentReports">
        <el-alert title="管理员可在此处理用户对违规评论的举报。核实违规后点击「清理」将直接删除该评论并警告发布者。" type="warning" show-icon style="margin-bottom: 15px;" />

        <el-table :data="reportList" border stripe v-loading="loadingReports">
          <el-table-column prop="commentId" label="被举报评论ID" width="120" align="center" />
          <el-table-column prop="reporterUsername" label="举报人" width="150" />
          <el-table-column prop="reason" label="违规类型" width="150">
            <template #default="scope"><el-tag type="danger">{{ scope.row.reason }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="details" label="详细说明" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="举报时间" width="180" />

          <el-table-column label="风控操作" width="220" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="danger" @click="processReport(scope.row.id, 1)">违规清理</el-button>
              <el-button size="small" type="info" plain @click="processReport(scope.row.id, 2)">忽略驳回</el-button>
            </template>
          </el-table-column>
          <template #empty><el-empty description="目前风平浪静，没有收到任何举报。" /></template>
        </el-table>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('applications')

// --- 原有逻辑保留 ---
const applicationList = ref([])
const loadingApps = ref(false)
const userList = ref([])
const loadingUsers = ref(false)

const fetchApplications = async () => { /* 保持原样 */
  loadingApps.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/admin/role-applications')
    applicationList.value = res.data.map(item => ({ ...item, createdAt: item.createdAt ? item.createdAt.replace('T', ' ').substring(0, 19) : '' }))
  } catch (e) {} finally { loadingApps.value = false }
}
const processApp = (id, status) => { /* 保持原样 */
  const actionName = status === 1 ? '通过' : '驳回'
  ElMessageBox.confirm(`确定要 ${actionName} 该用户的晋升申请吗？`, '审批确认', { type: status === 1 ? 'success' : 'warning' }).then(async () => {
    try {
      const res = await axios.put(`http://localhost:8080/api/admin/role-applications/${id}?status=${status}`)
      if (res.data.success) { ElMessage.success(`已${actionName}！`); fetchApplications(); fetchUsers(); }
    } catch (e) {}
  }).catch(() => {})
}
const fetchUsers = async () => { /* 保持原样 */
  loadingUsers.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/users')
    userList.value = res.data
  } catch (e) {} finally { loadingUsers.value = false }
}
const handleRoleChange = (userId, username, newRole) => { /* 保持原样 */
  ElMessageBox.confirm(`确定要将用户 [${username}] 的角色修改为 ${newRole} 吗？`, '高危操作确认', { confirmButtonText: '确定修改', cancelButtonText: '取消', type: 'warning' }).then(async () => {
    try { await axios.put(`http://localhost:8080/api/users/${userId}/role?role=${newRole}`); ElMessage.success('权限更新成功！'); fetchUsers() } catch (e) { fetchUsers() }
  }).catch(() => { fetchUsers() })
}

// --- ✨ Sprint 5: 举报审核大盘逻辑 ---
const reportList = ref([])
const loadingReports = ref(false)

const fetchReports = async () => {
  loadingReports.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/admin/comment-reports')
    reportList.value = res.data.map(item => ({ ...item, createdAt: item.createdAt ? item.createdAt.replace('T', ' ').substring(0, 19) : '' }))
  } catch (e) { ElMessage.error('获取举报列表失败') } finally { loadingReports.value = false }
}

const processReport = (id, status) => {
  const actionName = status === 1 ? '强制清理该评论' : '驳回该举报'
  ElMessageBox.confirm(`确定要 ${actionName} 吗？处理后系统将自动发送通知。`, '风控操作', {
    type: status === 1 ? 'danger' : 'info'
  }).then(async () => {
    try {
      const res = await axios.put(`http://localhost:8080/api/admin/comment-reports/${id}?status=${status}`)
      if (res.data.success) {
        ElMessage.success('处理成功！通知已下发。')
        fetchReports() // 刷新风控列表
      }
    } catch (e) { ElMessage.error('处理失败') }
  }).catch(() => {})
}

onMounted(() => {
  fetchApplications()
  fetchUsers()
  fetchReports() // 加载举报列表
})
</script>

<style scoped>
.users-container { padding: 10px; }
</style>