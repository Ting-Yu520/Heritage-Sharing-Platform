<template>
  <div class="users-page">
    <div class="page-header">
      <h1>User & Permission Management Center</h1>
      <p>Manage contributor applications, user roles and comment risk control.</p>
    </div>

    <el-tabs v-model="activeTab" type="border-card">

      <!-- ========== Contributor application approval ========== -->
      <el-tab-pane label="Contributor Application Approval" name="applications">
        <el-table :data="applicationList" border stripe v-loading="loadingApps">
          <el-table-column prop="username" label="Applicant Username" width="150" />
          <el-table-column prop="reason" label="Application Reason" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="Submission Time" width="180" />
          <el-table-column label="Approval Actions" width="220" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="success" @click="processApp(scope.row.id, 1)">Approve</el-button>
              <el-button size="small" type="danger" @click="processApp(scope.row.id, 2)">Reject</el-button>
            </template>
          </el-table-column>
          <template #empty><el-empty description="No pending promotion applications at the moment." /></template>
        </el-table>
      </el-tab-pane>

      <!-- ========== User role overview ========== -->
      <el-tab-pane label="User Role Overview" name="allUsers">
        <el-table :data="userList" border stripe v-loading="loadingUsers">
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="username" label="Login Username" width="150" />
          <el-table-column prop="email" label="Registration Email" width="200" />
          <el-table-column prop="realName" label="Name/Nickname" width="150" />
          <el-table-column label="Role Permission Control" min-width="200">
            <template #default="scope">
              <el-select
                  v-model="scope.row.role"
                  @change="(newRole) => handleRoleChange(scope.row.id, scope.row.username, newRole)"
                  :disabled="scope.row.role === 'ADMIN'" style="width: 150px;">
                <el-option label="VIEWER (Viewer)" value="VIEWER" />
                <el-option label="CONTRIBUTOR (Contributor)" value="CONTRIBUTOR" />
                <el-option label="ADMIN (Admin)" value="ADMIN" disabled />
              </el-select>
              <el-tag v-if="scope.row.role === 'ADMIN'" type="danger" style="margin-left: 10px;">Super Admin</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ========== Comment risk control center ========== -->
      <el-tab-pane label="Comment Risk Control Center" name="commentReports">
        <el-alert
          title="Administrators can handle user reports of violating comments here. After verifying the violation, click 'Clean Up' to directly delete the comment and warn the publisher."
          type="warning"
          show-icon
          style="margin-bottom: 15px;"
        />

        <el-table :data="reportList" border stripe v-loading="loadingReports">
          <el-table-column prop="commentId" label="Reported Comment ID" width="140" align="center" />
          <el-table-column prop="reporterUsername" label="Reporter" width="150" />
          <el-table-column prop="reason" label="Violation Type" width="130">
            <template #default="scope"><el-tag type="danger">{{ scope.row.reason }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="details" label="Detailed Description" show-overflow-tooltip min-width="150" />
          <el-table-column prop="createdAt" label="Report Time" width="180" />

          <!-- Widen action column to avoid button overlap -->
          <el-table-column label="Risk Control Actions" width="280" align="center" fixed="right">
            <template #default="scope">
              <div style="display: flex; gap: 8px; justify-content: center;">
                <el-button size="small" type="danger" @click="processReport(scope.row.id, 1)">Clean Up Violation</el-button>
                <el-button size="small" type="info" plain @click="processReport(scope.row.id, 2)">Ignore & Reject</el-button>
              </div>
            </template>
          </el-table-column>
          <template #empty><el-empty description="Everything is calm, no reports received." /></template>
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

const applicationList = ref([])
const loadingApps = ref(false)
const userList = ref([])
const loadingUsers = ref(false)

const fetchApplications = async () => {
  loadingApps.value = true
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/admin/role-applications')
    applicationList.value = res.data.map(item => ({ ...item, createdAt: item.createdAt ? item.createdAt.replace('T', ' ').substring(0, 19) : '' }))
  } catch (e) {} finally { loadingApps.value = false }
}

const processApp = (id, status) => {
  const actionName = status === 1 ? 'approve' : 'reject'
  ElMessageBox.confirm(`Are you sure you want to ${actionName} this user's promotion application?`, 'Approval Confirmation', { type: status === 1 ? 'success' : 'warning' }).then(async () => {
    try {
      const res = await axios.put(`http://116.62.165.182:8080/api/admin/role-applications/${id}?status=${status}`)
      if (res.data.success) { ElMessage.success(`Application ${actionName}ed!`); fetchApplications(); fetchUsers(); }
    } catch (e) {}
  }).catch(() => {})
}

const fetchUsers = async () => {
  loadingUsers.value = true
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/users')
    userList.value = res.data
  } catch (e) {} finally { loadingUsers.value = false }
}

const handleRoleChange = (userId, username, newRole) => {
  ElMessageBox.confirm(`Are you sure you want to change user [${username}]'s role to ${newRole}?`, 'High-Risk Operation Confirmation', { confirmButtonText: 'Confirm Change', cancelButtonText: 'Cancel', type: 'warning' }).then(async () => {
    try { await axios.put(`http://116.62.165.182:8080/api/users/${userId}/role?role=${newRole}`); ElMessage.success('Permission updated successfully!'); fetchUsers() } catch (e) { fetchUsers() }
  }).catch(() => { fetchUsers() })
}

const reportList = ref([])
const loadingReports = ref(false)

const fetchReports = async () => {
  loadingReports.value = true
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/admin/comment-reports')
    reportList.value = res.data.map(item => ({ ...item, createdAt: item.createdAt ? item.createdAt.replace('T', ' ').substring(0, 19) : '' }))
  } catch (e) { ElMessage.error('Failed to get report list') } finally { loadingReports.value = false }
}

const processReport = (id, status) => {
  const actionName = status === 1 ? 'force clean up this comment' : 'reject this report'
  ElMessageBox.confirm(`Are you sure you want to ${actionName}? The system will automatically send a notification after processing.`, 'Risk Control Operation', {
    type: status === 1 ? 'danger' : 'info'
  }).then(async () => {
    try {
      const res = await axios.put(`http://116.62.165.182:8080/api/admin/comment-reports/${id}?status=${status}`)
      if (res.data.success) {
        ElMessage.success('Processing successful! Notification has been sent.')
        fetchReports()
      }
    } catch (e) { ElMessage.error('Processing failed') }
  }).catch(() => {})
}

onMounted(() => {
  fetchApplications()
  fetchUsers()
  fetchReports()
})
</script>

<style scoped>
.users-page {
  padding: 40px;
  max-width: 1280px;
  margin: 0 auto;
  background: #fff;
}

.page-header h1 {
  font-size: 2.8rem;
  font-weight: 300;
  letter-spacing: -2px;
  color: #111;
  margin-bottom: 8px;
}

.page-header p {
  font-size: 1.1rem;
  color: #666;
}
</style>
