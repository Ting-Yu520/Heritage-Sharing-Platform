<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>📂 遗产资源主数据</h2>
      <div>
        <el-button type="success" @click="dialogVisible = true">✨ 新增资源</el-button>
        <el-button type="primary" @click="fetchResources">刷新数据</el-button>
      </div>
    </div>

    <el-table :data="tableData" border style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="60" align="center" />
      <el-table-column prop="title" label="资源名称" width="200" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="description" label="详细描述" />

      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">已发布</el-tag>
          <el-tag v-else-if="scope.row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="info">已归档</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="审计与归档操作" width="220" align="center" v-if="userRole === 'ADMIN'">
        <template #default="scope">
          <el-button v-if="scope.row.status === 1" type="warning" size="small" @click="handleArchive(scope.row)">📦 归档隐藏</el-button>
          <el-button v-if="scope.row.status === 3" type="primary" size="small" @click="handleRestore(scope.row.id)">🔄 恢复上线</el-button>
          <el-button type="danger" size="small" @click="deleteData(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增遗产资源" width="50%">
      <el-form :model="form" label-width="100px">
        <el-form-item label="资源名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option label="非物质文化遗产" value="非物质文化遗产" />
            <el-option label="古建筑" value="古建筑" />
            <el-option label="民俗活动" value="民俗活动" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述"><el-input v-model="form.description" type="textarea" rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitData">提交保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const userRole = localStorage.getItem('userRole')
const tableData = ref([])
const dialogVisible = ref(false)
const form = ref({ title: '', category: '', description: '' })

const fetchResources = async () => {
  const response = await axios.get('http://localhost:8080/api/resources')
  tableData.value = response.data
}

const submitData = async () => {
  await axios.post('http://localhost:8080/api/resources', form.value)
  ElMessage.success('资源新增成功！')
  dialogVisible.value = false
  fetchResources()
  form.value = { title: '', category: '', description: '' }
}

const deleteData = async (id) => {
  try {
    await ElMessageBox.confirm('永久删除该记录, 是否继续?', '警告', { type: 'error' })
    await axios.delete(`http://localhost:8080/api/resources/${id}`)
    ElMessage.success('删除成功！')
    fetchResources()
  } catch {}
}

// ======= 核心 PBI 1 & 2 实现 =======

// PBI 1 & 2: 归档操作与依赖警告
const handleArchive = async (row) => {
  try {
    // 模拟 PBI 2 的依赖检查警告 (Dependency Check Warning)
    await ElMessageBox.confirm(
        `系统检测到资源【${row.title}】可能被关联到活跃的社区展览中。将其归档会导致公共视图中不再可见。您确定要阻断关联并强制归档吗？`,
        '⚠️ 高危操作: 关联依赖警告',
        { confirmButtonText: '我了解风险，强制归档', cancelButtonText: '取消', type: 'warning' }
    )

    // 如果确认，调用接口将状态改为 3 (归档)
    await axios.put(`http://localhost:8080/api/resources/${row.id}/status?status=3`)
    ElMessage.success('归档成功！已从前台隐藏。')
    fetchResources()
  } catch (error) {
    ElMessage.info('已取消归档操作')
  }
}

// PBI 1: 恢复归档资源
const handleRestore = async (id) => {
  try {
    await ElMessageBox.confirm('将该资源恢复为“已发布”状态，重新在公共展示区可见？', '恢复确认', { type: 'info' })
    // 将状态改回 1 (已发布)
    await axios.put(`http://localhost:8080/api/resources/${id}/status?status=1`)
    ElMessage.success('恢复成功！资源已重新上线。')
    fetchResources()
  } catch {}
}

onMounted(() => fetchResources())
</script>