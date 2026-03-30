<template>
  <div class="audit-container">
    <h2 style="margin-bottom: 20px;">🛡️ 资源审核与状态管理</h2>

    <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">

      <el-tab-pane label="⏳ 待审核队列" name="pending">

        <el-card shadow="never" class="filter-card">
          <el-form :inline="true" :model="filters" class="filter-form">
            <el-form-item label="资源分类">
              <el-select v-model="filters.category" placeholder="全部" clearable style="width: 150px">
                <el-option label="传统技艺" value="传统技艺" />
                <el-option label="民俗活动" value="民俗活动" />
                <el-option label="口头文学" value="口头文学" />
                <el-option label="传统医药" value="传统医药" />
              </el-select>
            </el-form-item>
            <el-form-item label="提交时间">
              <el-date-picker
                  v-model="filters.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 250px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="applyFilters">筛选</el-button>
              <el-button @click="clearFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-table :data="pendingList" border stripe v-loading="loading">
          <el-table-column prop="title" label="资源标题" width="180" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="description" label="内容预览" show-overflow-tooltip />
          <el-table-column prop="createdAt" label="提交时间" width="180" sortable />
          <el-table-column label="审核操作" width="220" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="success" @click="approve(scope.row.id)">通过</el-button>
              <el-button size="small" type="danger" @click="openReject(scope.row.id)">驳回</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="太棒了！当前没有待处理的审核任务。" />
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

      <el-tab-pane label="📦 发布与归档管理" name="manage">
        <el-table :data="manageList" border stripe>
          <el-table-column prop="title" label="资源标题" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="status" label="当前状态" width="120">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status === 1">已发布</el-tag>
              <el-tag type="info" v-if="scope.row.status === 3">已归档</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态控制" width="150" align="center">
            <template #default="scope">
              <el-button v-if="scope.row.status === 1" size="small" type="warning" @click="toggleStatus(scope.row.id, 3, '归档')">隐藏归档</el-button>
              <el-button v-if="scope.row.status === 3" size="small" type="primary" @click="toggleStatus(scope.row.id, 1, '恢复')">重新发布</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

    </el-tabs>

    <el-dialog v-model="rejectDialog.visible" title="填写驳回意见" width="500px">
      <div style="margin-bottom: 10px;">
        <span style="font-size: 13px; color: #666;">快捷模板：</span>
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
          placeholder="请详细说明驳回原因（至少10个字符），以便贡献者修改..."
      />
      <div style="margin-top: 10px;">
        <el-checkbox v-model="rejectDialog.saveTemplate">将此反馈保存为快捷模板</el-checkbox>
      </div>

      <template #footer>
        <el-button @click="rejectDialog.visible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 状态控制 ---
const activeTab = ref('pending')
const loading = ref(false)

// --- PBI 1 & 4: 待审核队列与过滤 ---
const pendingList = ref([])
const total = ref(0)
const currentPage = ref(1)

// 从 localStorage 加载过滤器记忆 (PBI 4 Requirement 6)
const savedFilters = JSON.parse(localStorage.getItem('auditFilters') || '{}')
const filters = ref({
  category: savedFilters.category || '',
  dateRange: savedFilters.dateRange || null
})

const fetchPending = async () => {
  loading.value = true
  try {
    let url = `http://localhost:8080/api/resources/pending?current=${currentPage.value}&size=20`
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
  localStorage.setItem('auditFilters', JSON.stringify(filters.value)) // 持久化
  fetchPending()
}

const clearFilters = () => {
  filters.value = { category: '', dateRange: null }
  localStorage.removeItem('auditFilters')
  applyFilters()
}

// --- PBI 2: 一键通过与二次确认 ---
const approve = (id) => {
  ElMessageBox.confirm('确定要通过该资源的发布申请吗？', '确认通过', {
    confirmButtonText: '通过发布', cancelButtonText: '取消', type: 'success'
  }).then(async () => {
    await axios.put(`http://localhost:8080/api/resources/${id}/status?status=1`)
    ElMessage.success('资源已成功发布，并已通知贡献者！')
    fetchPending()
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// --- PBI 3: 驳回与模板机制 ---
const rejectDialog = ref({ visible: false, id: null, feedback: '', saveTemplate: false })
const rejectTemplates = ref(JSON.parse(localStorage.getItem('rejectTemplates') || '["内容不符合非遗主题，请重新核对。", "缺少必要的图片或历史背景描述。"]'))

const openReject = (id) => {
  rejectDialog.value = { visible: true, id, feedback: '', saveTemplate: false }
}

const useTemplate = (text) => {
  rejectDialog.value.feedback = text
}

const submitReject = async () => {
  const feedbackText = rejectDialog.value.feedback.trim()
  if (feedbackText.length < 10) {
    ElMessage.warning('驳回意见不能少于10个字符！')
    return
  }

  try {
    const res = await axios.put(`http://localhost:8080/api/resources/${rejectDialog.value.id}/status?status=2&feedback=${encodeURIComponent(feedbackText)}`)
    if (res.data.includes('ERROR')) {
      ElMessage.error(res.data)
      return
    }

    // 保存为模板
    if (rejectDialog.value.saveTemplate && !rejectTemplates.value.includes(feedbackText)) {
      rejectTemplates.value.push(feedbackText)
      localStorage.setItem('rejectTemplates', JSON.stringify(rejectTemplates.value))
    }

    ElMessage.success('已驳回，反馈意见已发送给贡献者')
    rejectDialog.value.visible = false
    fetchPending()
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

// --- PBI 5: 发布与归档管理 ---
const manageList = ref([])

const fetchManageList = async () => {
  const res = await axios.get('http://localhost:8080/api/resources')
  // 只显示 已发布(1) 和 已归档(3)
  manageList.value = res.data.filter(r => r.status === 1 || r.status === 3)
}

const toggleStatus = (id, targetStatus, actionName) => {
  ElMessageBox.confirm(`确定要将此资源【${actionName}】吗？`, '警告', { type: 'warning' }).then(async () => {
    await axios.put(`http://localhost:8080/api/resources/${id}/status?status=${targetStatus}`)
    ElMessage.success(`${actionName}成功！`)
    fetchManageList()
  })
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'pending') fetchPending()
  if (tab.paneName === 'manage') fetchManageList()
}

// 初始化
onMounted(() => {
  fetchPending()
})
</script>

<style scoped>
.audit-container { padding: 10px; }
.filter-card { margin-bottom: 20px; background-color: #f8f9fa; border: none; }
.filter-form .el-form-item { margin-bottom: 0; }
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>