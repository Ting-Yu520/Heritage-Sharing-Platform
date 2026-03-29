<template>
  <div>
    <h2>🛡️ 系统审计日志 (Audit Logs)</h2>
    <el-card>
      <div style="display: flex; gap: 15px; margin-bottom: 20px; align-items: center;">
        <el-input v-model="searchResourceId" placeholder="按资源 ID 搜索" style="width: 200px" clearable />
        <el-select v-model="searchAction" placeholder="按操作类型过滤" style="width: 200px" clearable>
          <el-option label="CREATE (新增)" value="CREATE" />
          <el-option label="UPDATE (修改)" value="UPDATE" />
          <el-option label="ARCHIVE (归档)" value="ARCHIVE" />
          <el-option label="APPROVE/RESTORE (发布/恢复)" value="APPROVE/RESTORE" />
          <el-option label="DELETE (删除)" value="DELETE" />
        </el-select>

        <div style="flex-grow: 1;"></div>

        <el-button type="success" @click="exportCSV">📥 导出报表 (Export CSV)</el-button>
        <el-button type="primary" plain @click="fetchLogs">刷新日志</el-button>
      </div>

      <el-table :data="filteredLogs" border style="width: 100%" stripe height="500">
        <el-table-column prop="id" label="日志 ID" width="80" align="center" />
        <el-table-column prop="userId" label="操作人" width="120" />
        <el-table-column prop="actionType" label="动作类型" width="160">
          <template #default="scope">
            <el-tag effect="dark" :type="scope.row.actionType === 'DELETE' ? 'danger' : 'info'">
              {{ scope.row.actionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resourceId" label="受影响资源 ID" width="130" align="center" />
        <el-table-column prop="changesSummary" label="变更详情" />
        <el-table-column prop="createdAt" label="操作时间" width="220" />
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

// 获取所有日志 (按时间倒序排，最新的在上面)
const fetchLogs = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/audit-logs')
    allLogs.value = response.data.reverse()
  } catch (error) {
    ElMessage.error('无法获取审计日志')
  }
}

// 魔法：多条件动态过滤计算属性
const filteredLogs = computed(() => {
  return allLogs.value.filter(log => {
    const matchId = searchResourceId.value ? String(log.resourceId).includes(searchResourceId.value) : true
    const matchAction = searchAction.value ? log.actionType === searchAction.value : true
    return matchId && matchAction
  })
})

// PBI 4: 导出 CSV 报表的核心功能
const exportCSV = () => {
  if (filteredLogs.value.length === 0) {
    ElMessage.warning('当前没有可导出的数据')
    return
  }
  // \uFEFF 是为了让 Excel 打开不乱码
  let csvContent = "data:text/csv;charset=utf-8,\uFEFF"
  csvContent += "日志ID,操作人,动作类型,受影响资源ID,变更详情,操作时间\n"

  filteredLogs.value.forEach(row => {
    const changes = `"${row.changesSummary || ''}"` // 防止内容里有逗号导致串列
    const rowData = `${row.id},${row.userId},${row.actionType},${row.resourceId},${changes},${row.createdAt}`
    csvContent += rowData + "\n"
  })

  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", "system_audit_logs.csv")
  document.body.appendChild(link)
  link.click() // 模拟点击下载
  document.body.removeChild(link)
  ElMessage.success('报表导出成功！')
}

onMounted(() => {
  fetchLogs()
})
</script>