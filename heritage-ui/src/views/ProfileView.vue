<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>Personal Profile</h1>
      <p>Manage your account information, favorites and notification preferences.</p>
    </div>

    <el-card class="main-card" shadow="hover">

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">

        <!-- ========== Personal profile settings ========== -->
        <el-tab-pane label="Personal Profile Settings" name="info">
          <el-form :model="profileForm" label-width="120px" v-loading="loading" style="margin-top: 20px;">
            <el-form-item label="My Avatar">
              <div style="display: flex; align-items: center; gap: 20px; width: 100%;">
                <el-avatar :size="80" :src="profileForm.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                <el-input v-model="profileForm.avatar" placeholder="Please enter a network image URL as avatar" clearable />
              </div>
            </el-form-item>

            <el-form-item label="Login Username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="Registration Email">
              <el-input v-model="profileForm.email" disabled />
            </el-form-item>

            <el-form-item label="Current Role">
              <el-tag :type="profileForm.role === 'ADMIN' ? 'danger' : (profileForm.role === 'CONTRIBUTOR' ? 'success' : 'info')">
                {{ profileForm.role }}
              </el-tag>
              <el-button v-if="profileForm.role === 'VIEWER'" type="primary" link style="margin-left: 15px; font-weight: bold;" @click="applyDialogVisible = true">
                Apply to be Contributor
              </el-button>
            </el-form-item>

            <el-form-item label="Personal Nickname">
              <el-input v-model="profileForm.nickname" placeholder="Please enter your display nickname" clearable />
            </el-form-item>

            <!-- ✨ Birthday (read-only display) -->
            <el-form-item label="Birthday">
              <el-input :value="profileForm.birthday || 'Not set'" disabled />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">Save Changes</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- ========== My favorites ========== -->
        <el-tab-pane label="My Favorites" name="favorites">
          <div v-loading="favLoading" style="min-height: 200px; padding-top: 10px;">
            <el-empty v-if="favList.length === 0" description="You haven't favorited any resources yet, go to the hall and explore!" />
            <div v-else class="fav-list">
              <div v-for="item in favList" :key="item.id" class="fav-item" @click="goToDetail(item.id)">
                <img :src="item.thumbnail || 'https://picsum.photos/id/1015/800/1000'" class="fav-img" />
                <div class="fav-content">
                  <h4>{{ item.title }}</h4>
                  <el-tag size="small" type="warning">{{ item.category }}</el-tag>
                  <p class="fav-desc">{{ item.description }}</p>
                </div>
                <el-button type="primary" plain size="small">View Details</el-button>
              </div>
            </div>
            <div class="pagination-wrap" v-if="favTotal > 0">
              <el-pagination background layout="total, prev, pager, next" :total="favTotal" :page-size="6" v-model:current-page="favPage" @current-change="fetchFavorites" />
            </div>
          </div>
        </el-tab-pane>

        <!-- ========== Notification preferences ========== -->
        <el-tab-pane label="Notification Preferences" name="notifications">
          <div style="padding: 10px 20px; min-height: 200px;">
            <el-alert
              title="You can freely choose which types of system notifications to receive. Once turned off, the system will no longer bother you."
              type="info"
              show-icon
              style="margin-bottom: 30px;"
            />

            <!-- Each item is a separate block: toggle on top, description below -->
            <div class="preference-item">
              <div class="preference-row">
                <span class="preference-label">Receive resource review notifications</span>
                <el-switch v-model="profileForm.notifyReview" :active-value="1" :inactive-value="0" @change="savePreferences" />
              </div>
              <p class="preference-desc">Notify you when your submission is approved or rejected</p>
            </div>

            <div class="preference-item">
              <div class="preference-row">
                <span class="preference-label">Receive comment and reply notifications</span>
                <el-switch v-model="profileForm.notifyComment" :active-value="1" :inactive-value="0" @change="savePreferences" />
              </div>
              <p class="preference-desc">Notify you when users comment on your submissions or reply to you</p>
            </div>

            <div class="preference-item">
              <div class="preference-row">
                <span class="preference-label">Receive system security notifications</span>
                <el-switch v-model="profileForm.notifySystem" :active-value="1" :inactive-value="0" @change="savePreferences" />
              </div>
              <p class="preference-desc">System important announcements and risk control warnings, recommended to turn on</p>
            </div>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <!-- Apply to become a contributor dialog -->
    <el-dialog v-model="applyDialogVisible" title="Apply to be Cultural Contributor" width="500px">
      <p style="color: #666; font-size: 14px; margin-bottom: 15px;">After becoming a contributor, you will gain the permission to publish intangible cultural heritage resources.</p>
      <el-input v-model="applyReason" type="textarea" :rows="4" placeholder="Example: I am a folk culture researcher..." />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitApplication" :loading="applying">Submit Application</el-button>
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

// --- Personal profile and preference status ---
const loading = ref(false)
const saving = ref(false)
const profileForm = ref({
  username: '', email: '', nickname: '', avatar: '', role: '',
  notifyReview: 1, notifyComment: 1, notifySystem: 1,
  birthday: ''
})

const fetchProfile = async () => {
  if (!currentUsername) return
  loading.value = true
  try {
    const res = await axios.get(`http://116.62.165.182/api/users/profile?username=${currentUsername}`)
    if (res.data) profileForm.value = Object.assign({}, profileForm.value, res.data)
  } catch (error) {
    ElMessage.error('Failed to get personal profile')
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    const res = await axios.put('http://116.62.165.182/api/users/profile', profileForm.value)
    if (res.data.success) {
      ElMessage.success(res.data.message)
      setTimeout(() => { window.location.reload() }, 1000)
    } else { ElMessage.error('Update failed') }
  } catch (error) { ElMessage.error('Network error') }
  finally { saving.value = false }
}

const savePreferences = async () => {
  try {
    const res = await axios.put(`http://116.62.165.182/api/users/preferences?username=${currentUsername}`, profileForm.value)
    if (res.data.success) ElMessage.success(res.data.message)
  } catch (error) { ElMessage.error('Failed to save preferences') }
}

// --- Promotion application logic ---
const applyDialogVisible = ref(false)
const applyReason = ref('')
const applying = ref(false)

const submitApplication = async () => {
  if (!applyReason.value.trim()) return ElMessage.warning('Please fill in the application reason!')
  applying.value = true
  try {
    const res = await axios.post('http://116.62.165.182/api/users/apply-role', { username: currentUsername, reason: applyReason.value })
    if (res.data.success) {
      ElMessage.success(res.data.message)
      applyDialogVisible.value = false
      applyReason.value = ''
    } else { ElMessage.error(res.data.message) }
  } catch (error) { ElMessage.error('Application submission failed') }
  finally { applying.value = false }
}

// --- My favorites logic ---
const favLoading = ref(false)
const favList = ref([])
const favTotal = ref(0)
const favPage = ref(1)

const fetchFavorites = async () => {
  if (!currentUsername) return
  favLoading.value = true
  try {
    const res = await axios.get(`http://116.62.165.182/api/resources/favorites?username=${currentUsername}&current=${favPage.value}&size=6`)
    favList.value = res.data.records
    favTotal.value = res.data.total
  } catch (error) { ElMessage.error('Failed to get favorites list') }
  finally { favLoading.value = false }
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'favorites') fetchFavorites()
}

const goToDetail = (id) => { router.push(`/resource/${id}`) }

onMounted(() => { fetchProfile() })
</script>

<style scoped>
.profile-page {
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

/* ========== Notification preferences: card layout ========== */
.preference-item {
  background: #f9fafc;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 18px 22px;
  margin-bottom: 16px;
}

.preference-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.preference-label {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.preference-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.5;
}

/* ========== Favorites list ========== */
.fav-list { display: flex; flex-direction: column; gap: 15px; }
.fav-item { display: flex; align-items: center; padding: 15px; border: 1px solid #ebeef5; border-radius: 8px; cursor: pointer; transition: all 0.3s; }
.fav-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); transform: translateX(5px); border-color: #c6e2ff; }
.fav-img { width: 100px; height: 75px; object-fit: cover; border-radius: 6px; margin-right: 20px; flex-shrink: 0; }
.fav-content { flex: 1; overflow: hidden; }
.fav-desc { font-size: 13px; color: #909399; margin: 8px 0 0 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 25px; }
</style>
