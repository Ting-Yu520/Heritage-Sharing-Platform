<template>
  <div class="audit-log-page">
    <div class="page-header">
      <h1>System Audit Logs</h1>
      <p>View all system operation records and track resource changes.</p>
    </div>

    <el-card class="main-card" shadow="hover">
      <div class="toolbar">
        <div class="left-tools">
          <el-input
              v-model="searchResourceId"
              placeholder="Search by Resource ID"
              style="width: 200px"
              clearable
          />
          <el-select v-model="searchAction" placeholder="Filter by Action Type" style="width: 200px" clearable>
            <el-option label="CREATE (Add)" value="CREATE" />
            <el-option label="UPDATE (Modify)" value="UPDATE" />
            <el-option label="ARCHIVE (Archive)" value="ARCHIVE" />
            <el-option label="APPROVE/RESTORE (Publish/Restore)" value="APPROVE/RESTORE" />
            <el-option label="DELETE (Delete)" value="DELETE" />
          </el-select>
        </div>

        <div>
          <el-button type="success" @click="exportCSV">Export Report (CSV)</el-button>
          <el-button type="primary" plain @click="fetchLogs">Refresh Logs</el-button>
        </div>
      </div>

      <el-table :data="filteredLogs" border style="width: 100%" stripe height="500">
        <el-table-column prop="id" label="Log ID" width="80" align="center" />
        <el-table-column prop="userId" label="Operator" width="120" />
        <el-table-column prop="actionType" label="Action Type" width="160">
          <template #default="scope">
            <el-tag effect="dark" :type="scope.row.actionType === 'DELETE' ? 'danger' : 'info'">
              {{ scope.row.actionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceId" label="Affected Resource ID" width="130" align="center" />
        <el-table-column prop="changesSummary" label="Change Details" />
        <el-table-column prop="createdAt" label="Operation Time" width="220" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const allLogs = ref([])
const searchResourceId = ref('')
const searchAction = ref('')

// Get all logs (sorted by time in reverse order, newest first)
const fetchLogs = async () => {
  try {
    const response = await axios.get('http://116.62.165.182:8080/api/audit-logs')
    allLogs.value = response.data.reverse()
  } catch (error) {
    ElMessage.error('Failed to get audit logs')
  }
}

// Magic: Multi-condition dynamic filtering computed property
const filteredLogs = computed(() => {
  return allLogs.value.filter(log => {
    const matchId = searchResourceId.value ? String(log.resourceId).includes(searchResourceId.value) : true
    const matchAction = searchAction.value ? log.actionType === searchAction.value : true
    return matchId && matchAction
  })
})

// PBI 4: Core functionality for exporting CSV report
const exportCSV = () => {
  if (filteredLogs.value.length === 0) {
    ElMessage.warning('No data available for export')
    return
  }
  let csvContent = "data:text/csv;charset=utf-8,\uFEFF"
  csvContent += "Log ID,Operator,Action Type,Affected Resource ID,Change Details,Operation Time\n"

  filteredLogs.value.forEach(row => {
    const changes = `"${row.changesSummary || ''}"`
    const rowData = `${row.id},${row.userId},${row.actionType},${row.resourceId},${changes},${row.createdAt}`
    csvContent += rowData + "\n"
  })

  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", "system_audit_logs.csv")
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('Report exported successfully!')
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.audit-log-page {
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

.main-card {
  border: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.left-tools { display: flex; align-items: center; gap: 15px; }
</style>