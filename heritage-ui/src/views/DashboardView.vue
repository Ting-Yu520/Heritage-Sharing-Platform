<template>
  <div>
    <h2>📊 控制台大厅</h2>

    <el-card style="margin-bottom: 20px;">
      <h3>欢迎回来，{{ displayRole }}：{{ currentName }}！</h3>
      <p style="color: #666;">这是您今天的系统概览。传承文化，守护历史，感谢您的贡献。</p>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>📦 资源总数</template>
          <div class="stat-number">{{ stats.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>⏳ 待审核</template>
          <div class="stat-number" style="color: #E6A23C;">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>✅ 已发布</template>
          <div class="stat-number" style="color: #67C23A;">{{ stats.published }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <template #header>🛡️ 审计日志</template>
          <div class="stat-number" style="color: #409EFF;">{{ stats.logs }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const currentName = ref(localStorage.getItem('currentUser') || '访客')
const role = localStorage.getItem('userRole')
const displayRole = role === 'ADMIN' ? '超级管理员' : '文化贡献者'

const stats = ref({
  total: 0,
  pending: 0,
  published: 0,
  logs: 0
})

// 获取统计数据的逻辑
const fetchStats = async () => {
  try {
    // 我们可以从之前的接口里直接计算出这些数字
    const res = await axios.get('http://localhost:8080/api/resources')
    const allData = res.data
    stats.value.total = allData.length
    stats.value.pending = allData.filter(i => i.status === 0).length
    stats.value.published = allData.filter(i => i.status === 1).length

    // 获取日志数量
    const logRes = await axios.get('http://localhost:8080/api/audit-logs')
    stats.value.logs = logRes.data.length
  } catch (e) {
    console.error("加载统计数据失败")
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.stat-card { text-align: center; border-radius: 12px; }
.stat-number { font-size: 32px; font-weight: bold; padding: 10px 0; }
</style>