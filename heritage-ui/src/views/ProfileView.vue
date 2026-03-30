<template>
  <div class="profile-container">
    <el-card class="box-card" shadow="hover">

      <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="profile-tabs">

        <el-tab-pane label="🧑‍💻 个人资料设置" name="info">
          <el-form :model="profileForm" label-width="100px" v-loading="loading" style="margin-top: 20px;">
            <el-form-item label="我的头像">
              <div style="display: flex; align-items: center; gap: 20px; width: 100%;">
                <el-avatar :size="80" :src="profileForm.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                <el-input v-model="profileForm.avatar" placeholder="请输入网络图片的 URL 链接作为头像" clearable />
              </div>
            </el-form-item>

            <el-form-item label="登录账号">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="注册邮箱">
              <el-input v-model="profileForm.email" disabled />
            </el-form-item>

            <el-form-item label="当前角色">
              <el-tag :type="profileForm.role === 'ADMIN' ? 'danger' : (profileForm.role === 'CONTRIBUTOR' ? 'success' : 'info')">
                {{ profileForm.role }}
              </el-tag>
              <el-button v-if="profileForm.role === 'VIEWER'" type="primary" link style="margin-left: 15px; font-weight: bold;" @click="applyDialogVisible = true">
                🚀 申请成为贡献者 (Contributor)
              </el-button>
            </el-form-item>

            <el-form-item label="个人昵称">
              <el-input v-model="profileForm.nickname" placeholder="请输入您想展示的昵称" clearable />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="⭐ 我的收藏夹" name="favorites">
          <div v-loading="favLoading" style="min-height: 200px; padding-top: 10px;">
            <el-empty v-if="favList.length === 0" description="您还没有收藏任何资源哦，快去大厅逛逛吧！" />
            <div v-else class="fav-list">
              <div v-for="item in favList" :key="item.id" class="fav-item" @click="goToDetail(item.id)">
                <img :src="item.thumbnail || 'https://via.placeholder.com/150?text=No+Image'" class="fav-img" />
                <div class="fav-content">
                  <h4 style="margin: 0 0 10px 0; color: #303133;">{{ item.title }}</h4>
                  <el-tag size="small" type="warning">{{ item.category }}</el-tag>
                  <p class="fav-desc">{{ item.description }}</p>
                </div>
                <el-button type="primary" plain size="small">查看详情</el-button>
              </div>
            </div>
            <div class="pagination-wrap" v-if="favTotal > 0">
              <el-pagination background layout="total, prev, pager, next" :total="favTotal" :page-size="6" v-model:current-page="favPage" @current-change="fetchFavorites" />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="🔔 通知偏好设置" name="notifications">
          <div style="padding: 10px 20px; min-height: 200px;">
            <el-alert title="您可以自由选择接收哪些类型的系统通知。关闭后系统将不再打扰您。" type="info" show-icon style="margin-bottom: 25px;" />

            <el-form label-width="180px" label-position="left">
              <el-form-item label="接收资源审核通知">
                <el-switch v-model="profileForm.notifyReview" :active-value="1" :inactive-value="0" @change="savePreferences" />
                <span style="margin-left: 15px; color: #909399; font-size: 13px;">(当您的稿件被通过或驳回时提醒您)</span>
              </el-form-item>

              <el-form-item label="接收评论与回复通知">
                <el-switch v-model="profileForm.notifyComment" :active-value="1" :inactive-value="0" @change="savePreferences" />
                <span style="margin-left: 15px; color: #909399; font-size: 13px;">(当有用户评论您的稿件或回复您时提醒)</span>
              </el-form-item>

              <el-form-item label="接收系统安全通知">
                <el-switch v-model="profileForm.notifySystem" :active-value="1" :inactive-value="0" @change="savePreferences" />
                <span style="margin-left: 15px; color: #909399; font-size: 13px;">(系统重要公告与风控警告，建议开启)</span>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <el-dialog v-model="applyDialogVisible" title="申请成为文化贡献者" width="500px">
      <p style="color: #666; font-size: 14px; margin-bottom: 15px;">成为贡献者后，您将获得发布非遗文化资源的权限。</p>
      <el-input v-model="applyReason" type="textarea" :rows="4" placeholder="例如：我是一名民俗文化研究者..." />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApplication" :loading="applying">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentUsername = localStorage.getItem('currentUser')
const activeTab = ref('info')

// --- 个人资料与偏好状态 ---
const loading = ref(false)
const saving = ref(false)
// ✨ 补充了通知开关字段
const profileForm = ref({
  username: '', email: '', nickname: '', avatar: '', role: '',
  notifyReview: 1, notifyComment: 1, notifySystem: 1
})

const fetchProfile = async () => {
  if (!currentUsername) return
  loading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/users/profile?username=${currentUsername}`)
    if (res.data) profileForm.value = Object.assign({}, profileForm.value, res.data)
  } catch (error) {
    ElMessage.error('获取个人资料失败')
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    const res = await axios.put('http://localhost:8080/api/users/profile', profileForm.value)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      setTimeout(() => { window.location.reload() }, 1000)
    } else { ElMessage.error('更新失败') }
  } catch (error) { ElMessage.error('网络错误') }
  finally { saving.value = false }
}

// ✨ PBI 5: 保存偏好设置
const savePreferences = async () => {
  try {
    const res = await axios.put(`http://localhost:8080/api/users/preferences?username=${currentUsername}`, profileForm.value)
    if (res.data.success) ElMessage.success(res.data.message)
  } catch (error) { ElMessage.error('偏好设置保存失败') }
}

// --- 晋升申请逻辑 ---
const applyDialogVisible = ref(false)
const applyReason = ref('')
const applying = ref(false)

const submitApplication = async () => {
  if (!applyReason.value.trim()) return ElMessage.warning('请填写申请理由！')
  applying.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/users/apply-role', { username: currentUsername, reason: applyReason.value })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      applyDialogVisible.value = false
      applyReason.value = ''
    } else { ElMessage.error(res.data.message) }
  } catch (error) { ElMessage.error('申请提交失败') }
  finally { applying.value = false }
}

// --- 我的收藏逻辑 ---
const favLoading = ref(false)
const favList = ref([])
const favTotal = ref(0)
const favPage = ref(1)

const fetchFavorites = async () => {
  if (!currentUsername) return
  favLoading.value = true
  try {
    const res = await axios.get(`http://localhost:8080/api/resources/favorites?username=${currentUsername}&current=${favPage.value}&size=6`)
    favList.value = res.data.records
    favTotal.value = res.data.total
  } catch (error) { ElMessage.error('获取收藏列表失败') }
  finally { favLoading.value = false }
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'favorites') fetchFavorites()
}

const goToDetail = (id) => { router.push(`/resource/${id}`) }

onMounted(() => { fetchProfile() })
</script>

<style scoped>
.profile-container { max-width: 800px; margin: 30px auto; }
:deep(.el-tabs__item) { font-size: 16px; font-weight: bold; }
.fav-list { display: flex; flex-direction: column; gap: 15px; }
.fav-item { display: flex; align-items: center; padding: 15px; border: 1px solid #ebeef5; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.fav-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); transform: translateX(5px); border-color: #c6e2ff; }
.fav-img { width: 100px; height: 75px; object-fit: cover; border-radius: 6px; margin-right: 20px; }
.fav-content { flex: 1; overflow: hidden; }
.fav-desc { font-size: 13px; color: #909399; margin: 8px 0 0 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 25px; }
</style>