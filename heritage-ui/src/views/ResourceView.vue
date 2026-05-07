<template>
  <div class="resource-page">
    <div class="page-header">
      <h1>Category Master Data Management</h1>
      <p>Manage the category dictionary for all site resources, prevent data silos, and ensure the accuracy of information retrieval.</p>
    </div>

    <el-card class="main-card" shadow="hover">
      <div class="toolbar">
        <div class="left-tools">
          <el-input
              v-model="searchKeyword"
              placeholder="Search category name or description..."
              clearable
              @input="handleSearch"
              style="width: 250px; margin-right: 15px;"
          />
          <el-select v-model="filterStatus" placeholder="All usage status" clearable @change="fetchCategories" style="width: 180px;">
            <el-option label="Show All" value="" />
            <el-option label="In Use" value="IN_USE" />
            <el-option label="Unused" value="UNUSED" />
          </el-select>
        </div>

        <el-button type="primary" size="large" @click="openAddDialog">
          Add System Category
        </el-button>
      </div>

      <el-table :data="categoryList" border stripe v-loading="loading" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="name" label="Category Name" width="200">
          <template #default="scope">
            <span style="font-weight: bold; color: #303133; font-size: 15px;">{{ scope.row.name }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="Description" min-width="250" show-overflow-tooltip />

        <el-table-column prop="usageCount" label="Usage" width="150" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.usageCount > 0 ? 'success' : 'info'" effect="dark" round>
              {{ scope.row.usageCount }} resources
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="Creation Time" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="openEditDialog(scope.row)">Edit</el-button>
            <el-button size="small" type="danger" @click="handleDeleteClick(scope.row)">Delete</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="No matching category data found, please adjust search criteria or add new categories." />
        </template>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="formDialogVisible" :title="isEdit ? 'Edit Category' : 'Add System Category'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="Category Name" prop="name">
          <el-input v-model="form.name" placeholder="Please enter unique category name (2-100 characters)" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="Category Description" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="Supplemental explanation of the category's scope (optional)" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitForm" :loading="saving">Save</el-button>
      </template>
    </el-dialog>

    <!-- Delete Confirmation Dialog -->
    <el-dialog v-model="deleteDialogVisible" title="Risk Operation Confirmation" width="450px">
      <div v-if="targetCategory">
        <div style="background-color: #fef0f0; color: #f56c6c; padding: 15px; border-radius: 4px; margin-bottom: 20px;">
          <h3 style="margin: 0 0 10px 0;">This category is currently in use!</h3>
          <p style="margin: 0;"><strong>[{{ targetCategory.name }}]</strong> is currently used by <strong>{{ targetCategory.usageCount }}</strong> resources.</p>
          <p style="margin: 5px 0 0 0; font-size: 13px;">Forced deletion may cause these resources to lose their category information and become "data silos".</p>
        </div>
        <el-checkbox v-model="confirmRisk" style="font-weight: bold; color: #E6A23C;">
          I understand the potential risks and insist on force deletion
        </el-checkbox>
      </div>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">Cancel</el-button>
        <el-button type="danger" @click="executeDelete" :disabled="!confirmRisk" :loading="deleting">
          Force Delete
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// --- Keep the logic fully consistent with the stable version you provided ---
const categoryList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
let searchTimeout = null

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await axios.get('http://116.62.165.182/api/admin/categories', {
      params: { keyword: searchKeyword.value, filterStatus: filterStatus.value }
    })
    categoryList.value = res.data
  } catch (error) {
    ElMessage.error('Failed to load categories')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => { fetchCategories() }, 300)
}

const formDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const saving = ref(false)
const form = ref({ id: null, name: '', description: '' })

const rules = {
  name: [
    { required: true, message: 'Category name cannot be empty', trigger: 'blur' },
    { min: 2, max: 100, message: 'Length must be between 2 and 100 characters', trigger: 'blur' }
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
      const res = await axios.post('http://116.62.165.182/api/admin/categories', form.value)
      if (res.data.success) {
        ElMessage.success(res.data.message)
        formDialogVisible.value = false
        fetchCategories()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (e) { ElMessage.error('Save failed') }
    finally { saving.value = false }
  })
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
}

const deleteDialogVisible = ref(false)
const targetCategory = ref(null)
const confirmRisk = ref(false)
const deleting = ref(false)

const handleDeleteClick = (row) => {
  if (row.usageCount === 0) {
    ElMessageBox.confirm(`Are you sure you want to permanently delete category [${row.name}]?`, 'Delete Confirmation', {
      confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'warning'
    }).then(() => {
      targetCategory.value = row
      executeDelete()
    }).catch(() => {})
  } else {
    targetCategory.value = row
    confirmRisk.value = false
    deleteDialogVisible.value = true
  }
}

const executeDelete = async () => {
  if (!targetCategory.value) return
  deleting.value = true
  try {
    const res = await axios.delete(`http://116.62.165.182/api/admin/categories/${targetCategory.value.id}`)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      deleteDialogVisible.value = false
      fetchCategories()
    }
  } catch (error) { ElMessage.error('Delete failed') }
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
.resource-page {
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
}

.left-tools { display: flex; align-items: center; }
</style>
