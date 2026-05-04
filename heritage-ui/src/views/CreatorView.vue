<template>
  <div class="creator-page">
    <div class="page-header">
      <h1>Creator Center</h1>
      <p>Publish your cultural discoveries and manage your submissions and drafts here.</p>
    </div>

    <el-card class="main-card" shadow="hover">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">

        <el-tab-pane label="My Submissions & Drafts" name="list">
          <el-table :data="resourceList" border stripe v-loading="loading" style="width: 100%; margin-top: 10px;">
            <el-table-column label="Cover" width="120" align="center">
              <template #default="scope">
                <el-image :src="scope.row.thumbnail || 'https://picsum.photos/id/1015/800/1000'" style="width: 80px; height: 50px; border-radius: 4px;" fit="cover" />
              </template>
            </el-table-column>

            <el-table-column prop="title" label="Submission Title" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                <strong>{{ scope.row.title || 'Untitled Draft' }}</strong>
              </template>
            </el-table-column>

            <el-table-column prop="category" label="Category" width="140" />

            <el-table-column label="Current Status" width="140" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === -1" type="info">Draft</el-tag>
                <el-tag v-else-if="scope.row.status === 0" type="warning">Under Review</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">Published</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="danger">Rejected</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">Archived</el-tag>
                <el-tag v-else-if="scope.row.status === 4" type="info">Withdrawn</el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="updatedAt" label="Last Modified" width="170" />

            <el-table-column label="Actions" width="300" align="center" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" size="small" type="warning" plain @click="withdraw(scope.row.id)">Withdraw</el-button>
                <el-button v-if="[-1, 2, 4].includes(scope.row.status)" size="small" type="primary" @click="editResource(scope.row)">Continue Editing</el-button>
                <el-button v-if="scope.row.status === 1" size="small" type="success" plain @click="router.push(`/resource/${scope.row.id}`)">View Details</el-button>
                <!-- ✨ New: delete button -->
                <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">Delete</el-button>
              </template>
            </el-table-column>

            <template #empty>
              <el-empty description="You haven't published any submissions yet" />
            </template>
          </el-table>

          <div class="pagination-wrap" v-if="total > 0">
            <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="fetchMyResources" />
          </div>
        </el-tab-pane>

        <el-tab-pane :label="isEditing ? 'Edit Submission' : 'Publish New Resource'" name="publish">
          <div class="publish-form-wrap">
            <el-alert v-if="isEditing" title="You are editing an existing submission. If you submit for review directly, it will overwrite the original content." type="info" show-icon style="margin-bottom: 20px;" />

            <el-form :model="form" :rules="rules" ref="resourceFormRef" label-width="110px" v-loading="submitting">
              <el-form-item label="Resource Title" prop="title">
                <el-input v-model="form.title" placeholder="Please enter an eye-catching title (Required)" maxlength="100" show-word-limit />
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Category" prop="category">
                    <el-select v-model="form.category" placeholder="Please select resource category" style="width: 100%;">
                      <el-option label="Intangible Cultural Heritage" value="Intangible Cultural Heritage" />
                      <el-option label="Historical Sites / Architecture" value="Historical Sites / Architecture" />
                      <el-option label="Folk Activities" value="Folk Activities" />
                      <el-option label="Traditional Crafts / Handicrafts" value="Traditional Crafts / Handicrafts" />
                      <el-option label="Oral Traditions / Myths" value="Oral Traditions / Myths" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="Location" prop="location">
                    <el-input v-model="form.location" placeholder="e.g., Pingjiang Road, Suzhou, Jiangsu Province" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="Cover Image URL" prop="thumbnail">
                <div style="display: flex; gap: 15px; width: 100%;">
                  <el-input v-model="form.thumbnail" placeholder="Please enter the network link for the cover image (Required)" clearable />
                  <el-image v-if="form.thumbnail" :src="form.thumbnail" style="width: 60px; height: 60px; border-radius: 4px;" fit="cover" />
                </div>
              </el-form-item>

              <el-form-item label="Attachments/Videos" prop="mediaUrl">
                <el-input v-model="form.mediaUrl" placeholder="If there are related videos or external materials, please enter the link (Optional)" clearable />
              </el-form-item>

              <el-form-item label="Related Tags" prop="tags">
                <el-input v-model="form.tags" placeholder="Separate multiple tags with commas, e.g., handicrafts,suzhou,mingqing" clearable />
              </el-form-item>

              <el-form-item label="Detailed Description" prop="description">
                <el-input v-model="form.description" type="textarea" :rows="8" placeholder="Describe the historical background, current status, cultural value, etc. of this cultural heritage in detail... (Required)" maxlength="5000" show-word-limit />
              </el-form-item>

              <el-form-item style="margin-top: 30px;">
                <el-button type="info" plain @click="submit(-1)" style="width: 160px;">Save to Draft</el-button>
                <el-button type="primary" @click="submit(0)" style="width: 160px;">Submit for Review</el-button>
                <el-button v-if="isEditing" @click="resetForm" style="margin-left: 20px;">Cancel Edit</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const currentUsername = localStorage.getItem('currentUser')
const activeTab = ref('list')

// List Status
const resourceList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

// Form Status
const resourceFormRef = ref(null)
const submitting = ref(false)
const isEditing = ref(false)
const editId = ref(null)

const form = ref({
  title: '', category: '', thumbnail: '', mediaUrl: '', tags: '', location: '', description: '', contributorUsername: currentUsername
})

const rules = {
  title: [{ required: true, message: 'Please enter resource title', trigger: 'blur' }],
  category: [{ required: true, message: 'Please select category', trigger: 'change' }],
  thumbnail: [{ required: true, message: 'Must provide cover image link', trigger: 'blur' }],
  description: [{ required: true, message: 'Must fill in detailed description', trigger: 'blur' }]
}

const fetchMyResources = async () => {
  if (!currentUsername) return
  loading.value = true
  try {
    const res = await axios.get(`http://116.62.165.182:8080/api/my-resources?username=${currentUsername}&current=${currentPage.value}&size=10`)
    resourceList.value = res.data.records.map(item => ({
      ...item,
      updatedAt: item.updatedAt ? item.updatedAt.substring(0, 16).replace('T', ' ') : ''
    }))
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('Failed to get submissions list')
  } finally {
    loading.value = false
  }
}

const submit = (status) => {
  resourceFormRef.value.validate(async (valid) => {
    if (!valid && status === 0) {
      ElMessage.warning('Please complete all required fields with red stars before submitting for review!')
      return
    }

    if (!form.value.title && status === -1) form.value.title = 'Untitled Draft ' + new Date().toLocaleTimeString()

    submitting.value = true
    try {
      let res
      if (isEditing.value) {
        res = await axios.put(`http://116.62.165.182:8080/api/my-resources/${editId.value}?status=${status}`, form.value)
      } else {
        res = await axios.post(`http://116.62.165.182:8080/api/my-resources/submit?status=${status}`, form.value)
      }

      if (res.data.success) {
        ElMessage.success(res.data.message)
        resetForm()
        activeTab.value = 'list'
        fetchMyResources()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (error) {
      ElMessage.error('Operation failed, network error')
    } finally {
      submitting.value = false
    }
  })
}

const withdraw = (id) => {
  ElMessageBox.confirm('Are you sure you want to withdraw this submission?', 'Withdrawal Confirmation', {
    confirmButtonText: 'Confirm', cancelButtonText: 'Cancel', type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.put(`http://116.62.165.182:8080/api/my-resources/${id}/withdraw`)
      if (res.data.success) {
        ElMessage.success(res.data.message)
        fetchMyResources()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (error) { ElMessage.error('Withdrawal failed') }
  }).catch(() => {})
}

/**
 * ✨ New: delete submission
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to permanently delete the submission "${row.title || 'Untitled Draft'}"? This action cannot be undone!`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await axios.delete(`http://116.62.165.182:8080/api/my-resources/${row.id}`)
      if (res.data.success) {
        ElMessage.success(res.data.message)
        fetchMyResources()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (error) {
      ElMessage.error('Deletion failed')
    }
  }).catch(() => {})
}

const editResource = (row) => {
  isEditing.value = true
  editId.value = row.id
  form.value = { ...row, contributorUsername: currentUsername }
  activeTab.value = 'publish'
}

const resetForm = () => {
  if (resourceFormRef.value) resourceFormRef.value.resetFields()
  form.value = { title: '', category: '', thumbnail: '', mediaUrl: '', tags: '', location: '', description: '', contributorUsername: currentUsername }
  isEditing.value = false
  editId.value = null
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'list') fetchMyResources()
}

onMounted(() => { fetchMyResources() })
</script>

<style scoped>
.creator-page {
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

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

.publish-form-wrap {
  max-width: 860px;
  margin: 0 auto;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
