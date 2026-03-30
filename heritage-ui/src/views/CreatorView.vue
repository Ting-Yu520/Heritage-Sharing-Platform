<template>
  <div class="creator-container">
    <div class="header-banner">
      <h2>🎨 创作者服务中心</h2>
      <p>在这里发布您的文化发现，管理您的稿件与草稿。</p>
    </div>

    <el-card class="main-card" shadow="hover">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">

        <el-tab-pane label="🗂️ 我的稿件与草稿" name="list">
          <el-table :data="resourceList" border stripe v-loading="loading" style="width: 100%; margin-top: 10px;">
            <el-table-column label="封面" width="120" align="center">
              <template #default="scope">
                <el-image :src="scope.row.thumbnail || 'https://via.placeholder.com/100'" style="width: 80px; height: 50px; border-radius: 4px;" fit="cover" />
              </template>
            </el-table-column>

            <el-table-column prop="title" label="稿件标题" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                <strong>{{ scope.row.title || '无标题草稿' }}</strong>
              </template>
            </el-table-column>

            <el-table-column prop="category" label="分类" width="120" />

            <el-table-column label="当前状态" width="120" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === -1" type="info">📝 草稿箱</el-tag>
                <el-tag v-else-if="scope.row.status === 0" type="warning">⏳ 审核中</el-tag>
                <el-tag v-else-if="scope.row.status === 1" type="success">✅ 已发布</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="danger">❌ 被驳回</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info" effect="dark">📦 已归档</el-tag>
                <el-tag v-else-if="scope.row.status === 4" type="info" effect="plain">↩️ 已撤回</el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="updatedAt" label="最后修改时间" width="170" />

            <el-table-column label="操作" width="220" align="center" fixed="right">
              <template #default="scope">
                <el-button v-if="scope.row.status === 0" size="small" type="warning" plain @click="withdraw(scope.row.id)">撤回稿件</el-button>

                <el-button v-if="[-1, 2, 4].includes(scope.row.status)" size="small" type="primary" @click="editResource(scope.row)">继续编辑</el-button>

                <el-button v-if="scope.row.status === 1" size="small" type="success" plain @click="router.push(`/resource/${scope.row.id}`)">查看详情</el-button>
              </template>
            </el-table-column>

            <template #empty>
              <el-empty description="您还没有发布过任何稿件哦，快去投稿吧！" />
            </template>
          </el-table>

          <div class="pagination-wrap" v-if="total > 0">
            <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="fetchMyResources" />
          </div>
        </el-tab-pane>

        <el-tab-pane :label="isEditing ? '✏️ 编辑稿件' : '🚀 发布新资源'" name="publish">
          <div class="publish-form-wrap">
            <el-alert v-if="isEditing" title="您正在编辑已有的稿件。如果直接提交审核，将会覆盖原有内容。" type="info" show-icon style="margin-bottom: 20px;" />

            <el-form :model="form" :rules="rules" ref="resourceFormRef" label-width="100px" v-loading="submitting">
              <el-form-item label="资源标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入引人注目的标题 (必填)" maxlength="100" show-word-limit />
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="所属分类" prop="category">
                    <el-select v-model="form.category" placeholder="请选择资源分类" style="width: 100%;">
                      <el-option label="非物质文化遗产" value="非物质文化遗产" />
                      <el-option label="历史古迹/建筑" value="历史古迹/建筑" />
                      <el-option label="民俗活动" value="民俗活动" />
                      <el-option label="传统技艺/手工艺" value="传统技艺/手工艺" />
                      <el-option label="口头传统/神话" value="口头传统/神话" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="地理位置" prop="location">
                    <el-input v-model="form.location" placeholder="例如：江苏省苏州市平江路" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="封面图 URL" prop="thumbnail">
                <div style="display: flex; gap: 15px; width: 100%;">
                  <el-input v-model="form.thumbnail" placeholder="请输入封面图片的网络链接 (必填)" clearable />
                  <el-image v-if="form.thumbnail" :src="form.thumbnail" style="width: 60px; height: 60px; border-radius: 4px;" fit="cover" />
                </div>
              </el-form-item>

              <el-form-item label="附件/视频" prop="mediaUrl">
                <el-input v-model="form.mediaUrl" placeholder="如有相关视频或外部资料，请输入链接 (选填)" clearable />
              </el-form-item>

              <el-form-item label="相关标签" prop="tags">
                <el-input v-model="form.tags" placeholder="多个标签请用英文逗号 (,) 隔开，例如：手工艺,苏州,明清" clearable />
              </el-form-item>

              <el-form-item label="详细描述" prop="description">
                <el-input v-model="form.description" type="textarea" :rows="8" placeholder="详细描述该文化遗产的历史背景、现状、文化价值等... (必填)" maxlength="5000" show-word-limit />
              </el-form-item>

              <el-form-item style="margin-top: 30px;">
                <el-button type="info" plain @click="submit( -1 )" style="width: 150px;">💾 保存到草稿箱</el-button>
                <el-button type="primary" @click="submit( 0 )" style="width: 150px;">🚀 提交管理员审核</el-button>
                <el-button v-if="isEditing" @click="resetForm" style="margin-left: 20px;">取消编辑</el-button>
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

// --- 列表状态 ---
const resourceList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

// --- 表单状态 ---
const resourceFormRef = ref(null)
const submitting = ref(false)
const isEditing = ref(false)
const editId = ref(null)

const form = ref({
  title: '', category: '', thumbnail: '', mediaUrl: '', tags: '', location: '', description: '', contributorUsername: currentUsername
})

// 表单验证规则 (PBI 1 验收标准: 必须有强制字段)
const rules = {
  title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  thumbnail: [{ required: true, message: '必须提供封面图链接', trigger: 'blur' }],
  description: [{ required: true, message: '必须填写详细描述', trigger: 'blur' }]
}

// 1. 获取我的稿件列表
const fetchMyResources = async () => {
  if (!currentUsername) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/my-resources?username=${currentUsername}&current=${currentPage.value}&size=10`)
    resourceList.value = res.data.records.map(item => ({
      ...item, updatedAt: item.updatedAt ? item.updatedAt.substring(0, 16).replace('T', ' ') : ''
    }))
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取稿件列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 提交表单 (草稿 status=-1, 待审 status=0)
const submit = (status) => {
  resourceFormRef.value.validate(async (valid) => {
    if (!valid && status === 0) {
      ElMessage.warning('请将带红星的必填项填写完整后再提交审核！')
      return
    }

    // 如果是存草稿，可以不校验直接发，只需给个默认标题
    if (!form.value.title && status === -1) form.value.title = '未命名草稿 ' + new Date().toLocaleTimeString()

    submitting.value = true
    try {
      let res;
      if (isEditing.value) {
        // 修改已有稿件
        res = await axios.put(`http://localhost:8080/api/my-resources/${editId.value}?status=${status}`, form.value)
      } else {
        // 新增稿件
        res = await axios.post(`http://localhost:8080/api/my-resources/submit?status=${status}`, form.value)
      }

      if (res.data.success) {
        ElMessage.success(res.data.message)
        resetForm()
        activeTab.value = 'list' // 切回列表页
        fetchMyResources() // 刷新列表
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (error) {
      ElMessage.error('操作失败，网络错误')
    } finally {
      submitting.value = false
    }
  })
}

// 3. 撤回正在审核的稿件 (PBI 4)
const withdraw = (id) => {
  ElMessageBox.confirm('确定要撤回这篇稿件吗？撤回后将移出审核队列，变为“已撤回”状态。', '撤回确认', {
    confirmButtonText: '确定撤回', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await axios.put(`http://localhost:8080/api/my-resources/${id}/withdraw`)
      if (res.data.success) {
        ElMessage.success(res.data.message)
        fetchMyResources()
      } else {
        ElMessage.error(res.data.message)
      }
    } catch (error) { ElMessage.error('撤回失败') }
  }).catch(() => {})
}

// 4. 继续编辑 (PBI 3 & 5)
const editResource = (row) => {
  isEditing.value = true
  editId.value = row.id
  form.value = {
    title: row.title, category: row.category, thumbnail: row.thumbnail, mediaUrl: row.mediaUrl,
    tags: row.tags, location: row.location, description: row.description, contributorUsername: currentUsername
  }
  activeTab.value = 'publish' // 自动切换到编辑标签页
}

// 5. 重置表单
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
.creator-container { padding: 10px 20px; max-width: 1200px; margin: 0 auto; }
.header-banner { background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%); padding: 30px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border-left: 6px solid #409EFF; }
.header-banner h2 { margin: 0 0 10px 0; color: #303133; }
.header-banner p { margin: 0; color: #606266; font-size: 14px; }
.main-card { min-height: 600px; }
:deep(.el-tabs__item) { font-size: 16px; font-weight: bold; }
.publish-form-wrap { max-width: 800px; margin: 20px auto 0; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>