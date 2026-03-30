<template>
  <div class="category-container">
    <div class="header-banner">
      <h2>📂 分类主数据管理 (Master Data)</h2>
      <p>管理全站资源的分类字典，防止数据孤岛，保障信息检索的准确性。</p>
    </div>

    <el-card class="main-card" shadow="hover">
      <div class="toolbar">
        <div class="left-tools">
          <el-input
              v-model="searchKeyword"
              placeholder="🔍 搜索分类名称或描述..."
              clearable
              @input="handleSearch"
              style="width: 250px; margin-right: 15px;"
          />
          <el-select v-model="filterStatus" placeholder="所有使用状态" clearable @change="fetchCategories" style="width: 180px;">
            <el-option label="显示全部" value="" />
            <el-option label="✅ 使用中 (In Use)" value="IN_USE" />
            <el-option label="💤 未使用 (Unused)" value="UNUSED" />
          </el-select>
        </div>

        <el-button type="primary" size="large" @click="openAddDialog">
          <el-icon style="margin-right: 5px;"><Plus /></el-icon> 新增系统分类
        </el-button>
      </div>

      <el-table :data="categoryList" border stripe v-loading="loading" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="name" label="分类名称 (Category Name)" width="200">
          <template #default="scope">
            <span style="font-weight: bold; color: #303133; font-size: 15px;">{{ scope.row.name }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="分类描述 (Description)" min-width="250" show-overflow-tooltip />

        <el-table-column prop="usageCount" label="资源使用量 (Usage)" width="150" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.usageCount > 0 ? 'success' : 'info'" effect="dark" round>
              {{ scope.row.usageCount }} 个资源
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作 (Actions)" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteClick(scope.row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无符合条件的分类数据，请调整搜索条件或新增分类。" />
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="formDialogVisible" :title="isEdit ? '✏️ 编辑分类' : '✨ 新增系统分类'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入唯一分类名称 (2-100字符)" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="补充说明该分类的适用范围（选填）" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消 (Cancel)</el-button>
        <el-button type="primary" @click="submitForm" :loading="saving">保存 (Save)</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deleteDialogVisible" title="⚠️ 风险操作确认 (Risk Alert)" width="450px">
      <div v-if="targetCategory">
        <div style="background-color: #fef0f0; color: #f56c6c; padding: 15px; border-radius: 4px; margin-bottom: 20px;">
          <h3 style="margin: 0 0 10px 0;">该分类正在被使用！</h3>
          <p style="margin: 0;"><strong>[{{ targetCategory.name }}]</strong> 目前正被 <strong>{{ targetCategory.usageCount }}</strong> 个资源使用。</p>
          <p style="margin: 5px 0 0 0; font-size: 13px;">强制删除可能导致这些资源的分类信息丢失，沦为“信息孤岛”。</p>
        </div>
        <el-checkbox v-model="confirmRisk" style="font-weight: bold; color: #E6A23C;">
          我已了解潜在风险，执意要强制删除 (Force Delete)
        </el-checkbox>
      </div>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="executeDelete" :disabled="!confirmRisk" :loading="deleting">
          强制删除
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// --- 大盘状态 ---
const categoryList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
let searchTimeout = null

// --- 获取数据 (PBI 1 & 5) ---
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://localhost:8080/api/admin/categories', {
      params: { keyword: searchKeyword.value, filterStatus: filterStatus.value }
    })
    categoryList.value = res.data
  } catch (error) {
    ElMessage.error('加载分类数据失败 (Failed to load categories)')
  } finally {
    loading.value = false
  }
}

// 实时搜索防抖
const handleSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => { fetchCategories() }, 300)
}

// --- 表单状态 (PBI 2 & 3) ---
const formDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const saving = ref(false)
const form = ref({ id: null, name: '', description: '' })

const rules = {
  name: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, name: '', description: '' }
  formDialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { id: row.id, name: row.name, description: row.description }
  formDialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = await axios.post('http://localhost:8080/api/admin/categories', form.value)
      if (res.data.success) {
        ElMessage.success(res.data.message)
        formDialogVisible.value = false
        fetchCategories()
      } else {
        ElMessage.error(res.data.message) // 名称重复的报错
      }
    } catch (e) { ElMessage.error('保存失败') }
    finally { saving.value = false }
  })
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
}

// --- 高级删除逻辑 (PBI 4) ---
const deleteDialogVisible = ref(false)
const targetCategory = ref(null)
const confirmRisk = ref(false)
const deleting = ref(false)

const handleDeleteClick = (row) => {
  // Scenario 1: Unused Category -> 直接弹标准确认框
  if (row.usageCount === 0) {
    ElMessageBox.confirm(`确定要永久删除分类 [${row.name}] 吗？`, '删除确认', {
      confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning'
    }).then(() => {
      targetCategory.value = row
      executeDelete()
    }).catch(() => {})
  }
  // Scenario 2: Used Category -> 弹高级风险警告框
  else {
    targetCategory.value = row
    confirmRisk.value = false // 必须重新勾选
    deleteDialogVisible.value = true
  }
}

const executeDelete = async () => {
  if (!targetCategory.value) return
  deleting.value = true
  try {
    const res = await axios.delete(`http://localhost:8080/api/admin/categories/${targetCategory.value.id}`)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      deleteDialogVisible.value = false
      fetchCategories()
    }
  } catch (error) { ElMessage.error('删除失败') }
  finally { deleting.value = false }
}

const formatDate = (dateStr) => {
  return dateStr ? dateStr.replace('T', ' ').substring(0, 16) : ''
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-container { padding: 10px 20px; max-width: 1200px; margin: 0 auto; }
.header-banner { background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%); padding: 25px; border-radius: 8px; margin-bottom: 20px; border-left: 6px solid #409EFF; }
.header-banner h2 { margin: 0 0 10px 0; color: #303133; }
.header-banner p { margin: 0; color: #606266; font-size: 14px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; background: #f5f7fa; padding: 15px; border-radius: 8px; }
.left-tools { display: flex; align-items: center; }
</style>