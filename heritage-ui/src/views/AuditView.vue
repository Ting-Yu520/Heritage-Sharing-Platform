<template>
  <div class="audit-page">
    <div class="page-header">
      <h1>Resource Review & Status Management</h1>
      <p>Review pending resources, manage publication status and archive operations.</p>
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">

      <el-tab-pane label="Pending Review Queue" name="pending">

        <el-card shadow="never" class="filter-card">
          <el-form :inline="true" :model="filters" class="filter-form">
            <el-form-item label="Resource Category">
              <el-select v-model="filters.category" placeholder="All" clearable style="width: 150px">
                <el-option label="Intangible Cultural Heritage" value="Intangible Cultural Heritage" />
                <el-option label="Historical Sites / Architecture" value="Historical Sites / Architecture" />
                <el-option label="Folk Activities" value="Folk Activities" />
                <el-option label="Traditional Crafts / Handicrafts" value="Traditional Crafts / Handicrafts" />
                <el-option label="Oral Traditions / Myths" value="Oral Traditions / Myths" />
              </el-select>
            </el-form-item>
            <el-form-item label="Submission Time">
              <el-date-picker
                  v-model="filters.dateRange"
                  type="daterange"
                  range-separator="to"
                  start-placeholder="Start Date"
                  end-placeholder="End Date"
                  value-format="YYYY-MM-DD"
                  style="width: 250px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="applyFilters">Filter</el-button>
              <el-button @click="clearFilters">Reset</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="pendingList" border stripe v-loading="loading">
          <el-table-column prop="title" label="Resource Title" width="180" />
          <el-table-column prop="category" label="Category" width="120" />
          <el-table-column prop="description" label="Content Preview" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="Submission Time" width="180" sortable />
          <el-table-column label="Review Actions" width="220" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="success" @click="approve(scope.row.id)">Approve</el-button>
              <el-button size="small" type="danger" @click="openReject(scope.row.id)">Reject</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="Great! No pending review tasks at the moment." />
          </template>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination
              background
              layout="total, prev, pager, next"
              :total="total"
              :page-size="20"
              v-model:current-page="currentPage"
              @current-change="fetchPending"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="Publish & Archive Management" name="manage">
        <el-table :data="manageList" border stripe>
          <el-table-column prop="title" label="Resource Title" />
          <el-table-column prop="category" label="Category" width="120" />
          <el-table-column prop="status" label="Current Status" width="120">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status === 1">Published</el-tag>
              <el-tag type="info" v-if="scope.row.status === 3">Archived</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Status Control" width="300" align="center">
            <template #default="scope">
              <el-button v-if="scope.row.status === 1" size="small" type="warning" @click="toggleStatus(scope.row.id, 3, 'Archive')">Archive</el-button>
              <el-button v-if="scope.row.status === 3" size="small" type="primary" @click="toggleStatus(scope.row.id, 1, 'Restore')">Restore</el-button>
              <!-- ✨ New: delete button -->
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id, scope.row.title)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

    </el-tabs>

    <el-dialog v-model="rejectDialog.visible" title="Provide Rejection Reason" width="500px">
      <div style="margin-bottom: 10px;">
        <span style="font-size: 13px; color: #666;">Quick Templates:</span>
        <el-tag
            v-for="(tpl, idx) in rejectTemplates"
            :key="idx"
            style="margin-right: 5px; cursor: pointer;"
            @click="useTemplate(tpl)">
          {{ tpl.substring(0, 5) }}...
        </el-tag>
      </div>

      <el-input
          v-model="rejectDialog.feedback"
          type="textarea"
          :rows="4"
          placeholder="Please provide detailed rejection reasons (at least 10 characters) to help contributors modify..."
      />
      <div style="margin-top: 10px;">
        <el-checkbox v-model="rejectDialog.saveTemplate">Save this feedback as a quick template</el-checkbox>
      </div>

      <template #footer>
        <el-button @click="rejectDialog.visible = false">Cancel</el-button>
        <el-button type="danger" @click="submitReject">Confirm Rejection</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('pending')
const loading = ref(false)

const pendingList = ref([])
const total = ref(0)
const currentPage = ref(1)

const savedFilters = JSON.parse(localStorage.getItem('auditFilters') || '{}')
const filters = ref({
  category: savedFilters.category || '',
  dateRange: savedFilters.dateRange || null
})

const fetchPending = async () => {
  loading.value = true
  try {
    let url = `http://116.62.165.182/api/resources/pending?current=${currentPage.value}&size=20`
    if (filters.value.category) url += `&category=${filters.value.category}`
    if (filters.value.dateRange && filters.value.dateRange.length === 2) {
      url += `&startDate=${filters.value.dateRange[0]}&endDate=${filters.value.dateRange[1]}`
    }
    const res = await axios.get(url)
    pendingList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  currentPage.value = 1
  localStorage.setItem('auditFilters', JSON.stringify(filters.value))
  fetchPending()
}

const clearFilters = () => {
  filters.value = { category: '', dateRange: null }
  localStorage.removeItem('auditFilters')
  applyFilters()
}

const approve = (id) => {
  ElMessageBox.confirm('Are you sure you want to approve this resource for publication?', 'Confirm Approval', {
    confirmButtonText: 'Approve', cancelButtonText: 'Cancel', type: 'success'
  }).then(async () => {
    await axios.put(`http://116.62.165.182/api/resources/${id}/status?status=1`)
    ElMessage.success('Resource has been successfully published, and the contributor has been notified!')
    fetchPending()
  }).catch(() => {
    ElMessage.info('Operation cancelled')
  })
}

const rejectDialog = ref({ visible: false, id: null, feedback: '', saveTemplate: false })
const rejectTemplates = ref(JSON.parse(localStorage.getItem('rejectTemplates') || '["Content does not match intangible heritage theme, please recheck.", "Missing necessary images or historical background description."]'))

const openReject = (id) => {
  rejectDialog.value = { visible: true, id, feedback: '', saveTemplate: false }
}

const useTemplate = (text) => {
  rejectDialog.value.feedback = text
}

const submitReject = async () => {
  const feedbackText = rejectDialog.value.feedback.trim()
  if (feedbackText.length < 10) {
    ElMessage.warning('Rejection reason must be at least 10 characters!')
    return
  }

  try {
    const res = await axios.put(`http://116.62.165.182/api/resources/${rejectDialog.value.id}/status?status=2&feedback=${encodeURIComponent(feedbackText)}`)
    if (res.data.includes('ERROR')) {
      ElMessage.error(res.data)
      return
    }

    if (rejectDialog.value.saveTemplate && !rejectTemplates.value.includes(feedbackText)) {
      rejectTemplates.value.push(feedbackText)
      localStorage.setItem('rejectTemplates', JSON.stringify(rejectTemplates.value))
    }

    ElMessage.success('Rejected, feedback has been sent to the contributor')
    rejectDialog.value.visible = false
    fetchPending()
  } catch (e) {
    ElMessage.error('Network error')
  }
}

const manageList = ref([])

const fetchManageList = async () => {
  const res = await axios.get('http://116.62.165.182/api/resources')
  manageList.value = res.data.filter(r => r.status === 1 || r.status === 3)
}

const toggleStatus = (id, targetStatus, actionName) => {
  ElMessageBox.confirm(`Are you sure you want to ${actionName} this resource?`, 'Warning', { type: 'warning' }).then(async () => {
    await axios.put(`http://116.62.165.182/api/resources/${id}/status?status=${targetStatus}`)
    ElMessage.success(`${actionName} successful!`)
    fetchManageList()
  })
}

/**
 * ✨ New: admin deletes resource
 */
const handleDelete = (id, title) => {
  ElMessageBox.confirm(
    `Are you sure you want to permanently delete the resource "${title}"? This action cannot be undone!`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await axios.delete(`http://116.62.165.182/api/resources/${id}`)
      ElMessage.success('Resource deleted successfully!')
      fetchManageList()
    } catch (error) {
      ElMessage.error('Deletion failed')
    }
  }).catch(() => {})
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'pending') fetchPending()
  if (tab.paneName === 'manage') fetchManageList()
}

onMounted(() => {
  fetchPending()
})
</script>

<style scoped>
.audit-page {
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

.filter-card {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  border: none;
}

.filter-form .el-form-item {
  margin-bottom: 0;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
