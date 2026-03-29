<template>
  <div>
    <h2>🛡️ 资源审核大厅</h2>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>待审核的任务列表</span>
          <el-button type="primary" plain @click="fetchPendingResources">刷新任务</el-button>
        </div>
      </template>

      <el-table :data="pendingData" border style="width: 100%">
        <el-table-column prop="title" label="申请资源名称" width="200" />
        <el-table-column prop="category" label="分类" width="150" />
        <el-table-column prop="description" label="详细描述" />

        <el-table-column label="管理员审批" width="200" align="center">
          <template #default="scope">
            <el-button type="success" size="small" @click="handleAudit(scope.row.id, 1)">✅ 通过</el-button>
            <el-button type="danger" size="small" @click="handleAudit(scope.row.id, 2)">❌ 驳回</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="太棒了！当前没有需要审核的资源" />
        </template>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const pendingData = ref([])

// 获取待审核数据
const fetchPendingResources = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/resources')
    // 魔法：我们只把 status 为 0 的数据挑出来展示在审核大厅！
    pendingData.value = response.data.filter(item => item.status === 0)
  } catch (error) {
    ElMessage.error('获取审核列表失败')
  }
}

// 审批操作的魔法函数
const handleAudit = async (id, newStatus) => {
  try {
    // 呼叫 Java 的 PUT 接口，修改状态
    await axios.put(`http://localhost:8080/api/resources/${id}/status?status=${newStatus}`)

    // 弹出提示
    if(newStatus === 1) {
      ElMessage.success('资源已通过并发布！')
    } else {
      ElMessage.warning('资源已被驳回！')
    }

    // 重新刷新审核列表（刚刚审核过的数据会消失）
    fetchPendingResources()
  } catch (error) {
    ElMessage.error('审批操作失败')
  }
}

onMounted(() => {
  fetchPendingResources()
})
</script>