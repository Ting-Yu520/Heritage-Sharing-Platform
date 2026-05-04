<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h1>Dashboard</h1>
      <p>Welcome back, {{ displayRole }}: {{ currentName }} • Overview of the platform</p>
    </div>

    <!-- Stat cards (styles only, structure unchanged) -->
    <el-row :gutter="24">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>Total Resources</template>
          <div class="stat-number">{{ stats.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>Pending Review</template>
          <div class="stat-number" style="color: #E6A23C;">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>Published</template>
          <div class="stat-number" style="color: #67C23A;">{{ stats.published }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>Audit Logs</template>
          <div class="stat-number" style="color: #409EFF;">{{ stats.logs }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const currentName = ref(localStorage.getItem('currentUser') || 'Guest')
const role = localStorage.getItem('userRole')
const displayRole = role === 'ADMIN' ? 'Super Admin' : 'Cultural Contributor'

const stats = ref({
  total: 0,
  pending: 0,
  published: 0,
  logs: 0
})

// Keep the original data loading logic unchanged
const fetchStats = async () => {
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/resources')
    const allData = res.data
    stats.value.total = allData.length
    stats.value.pending = allData.filter(i => i.status === 0).length
    stats.value.published = allData.filter(i => i.status === 1).length

    const logRes = await axios.get('http://116.62.165.182:8080/api/audit-logs')
    stats.value.logs = logRes.data.length
  } catch (e) {
    console.error("Failed to load statistical data")
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard-page {
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

.stat-card {
  border: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  transition: transform 0.3s ease;
  text-align: center;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card :deep(.el-card__header) {
  font-size: 1.1rem;
  font-weight: 500;
  color: #111;
  padding-bottom: 12px;
}

.stat-number {
  font-size: 2.6rem;
  font-weight: 500;
  padding: 12px 0;
  color: #111;
}
</style>
