<template>
  <div>
    <h2>👥 用户与权限管理中心</h2>
    <el-card>
      <el-table :data="userList" border style="width: 100%" stripe>
        <el-table-column prop="id" label="用户 ID" width="80" align="center" />
        <el-table-column prop="username" label="登录账号" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="role" label="当前角色">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'info'">{{ scope.row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限操作" width="250">
          <template #default="scope">
            <el-button
              size="small"
              type="warning"
              @click="promote(scope.row)"
              v-if="scope.row.role !== 'ADMIN'">提拔管理员</el-button>
            <el-button
              size="small"
              type="danger"
              @click="removeUser(scope.row.id)"
              v-if="scope.row.username !== 'admin'">强制注销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])

const fetchUsers = async () => {
  const res = await axios.get('http://localhost:8080/api/users')
  userList.value = res.data
}

const promote = async (user) => {
  await axios.put(`http://localhost:8080/api/users/${user.id}/role?role=ADMIN`)
  ElMessage.success(`${user.username} 已升级为管理员`)
  fetchUsers()
}

const removeUser = (id) => {
  ElMessageBox.confirm('确定要注销该账号吗？此操作不可逆！', '警告', { type: 'warning' }).then(async () => {
    await axios.delete(`http://localhost:8080/api/users/${id}`)
    ElMessage.success('账号已从系统抹除')
    fetchUsers()
  })
}

onMounted(fetchUsers)
</script>