<template>
  <div class="gallery-page">
    <div class="public-nav">
      <div class="logo">🏛️ Community Heritage Resource Sharing Platform</div>
      <div class="nav-actions">
        <el-input v-model="searchKeyword" placeholder="Search heritage resources..." prefix-icon="Search" style="width: 250px; margin-right: 20px" clearable />
        <el-button type="primary" plain @click="$router.push('/login')">Contributor/Admin Login</el-button>
      </div>
    </div>

    <div class="hero-banner">
      <h1>Explore, Inherit, Share Our Cultural Treasures</h1>
      <p>Discover intangible cultural heritage and historical imprints around you</p>
    </div>

    <div class="gallery-container">
      <el-empty v-if="filteredResources.length === 0" description="No publicly available resources yet, stay tuned!" />

      <el-row :gutter="24" v-else>
        <el-col :xs="24" :sm="12" :md="8" v-for="item in filteredResources" :key="item.id" style="margin-bottom: 24px;">
          <el-card class="resource-card" shadow="hover" :body-style="{ padding: '0px' }">
            <img :src="`https://source.unsplash.com/800x600/?heritage,culture,${item.id}`" class="card-image" alt="Cover image" />

            <div style="padding: 20px;">
              <div class="card-header">
                <span class="card-title">{{ item.title }}</span>
                <el-tag size="small" effect="dark" color="#2c3e50" style="border:none">{{ item.category }}</el-tag>
              </div>
              <p class="card-desc">{{ item.description }}</p>
              <div class="card-footer">
                <span class="publish-time">ID: #{{ item.id }}</span>
                <div class="card-footer">
                  <el-button type="text" class="detail-btn" @click="openDetail(item)">Learn more >></el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-dialog v-model="detailVisible" :title="currentResource.title" width="60%">
        <div style="display: flex; gap: 20px; margin-bottom: 20px;">
          <img :src="`https://source.unsplash.com/800x600/?heritage,culture,${currentResource.id}`" style="width: 50%; border-radius: 8px; object-fit: cover;" />
          <div>
            <el-tag size="large">{{ currentResource.category }}</el-tag>
            <p style="margin-top: 20px; line-height: 1.8; color: #666;">{{ currentResource.description }}</p>
          </div>
        </div>

        <el-divider>💬 Visitor Message Board</el-divider>

        <div style="margin-bottom: 20px; display: flex; gap: 10px;">
          <el-input v-model="newCommentText" placeholder="Share your insights or leave a message..." type="textarea" :rows="2" />
          <el-button type="primary" style="height: auto;" @click="submitComment">Post<br>Comment</el-button>
        </div>

        <div v-if="comments.length === 0" style="text-align: center; color: #999; padding: 20px 0;">
          No messages yet, be the first to comment!
        </div>

        <div v-for="c in comments" :key="c.id" style="background: #f9fafc; padding: 15px; border-radius: 8px; margin-bottom: 10px;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <strong style="color: #409eff;">{{ c.username }}</strong>
            <span style="color: #999; font-size: 12px;">{{ c.createdAt }}</span>
          </div>
          <div style="color: #333;">{{ c.content }}</div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { Search } from '@element-plus/icons-vue'

const publicResources = ref([])
const searchKeyword = ref('')

// Below the script setup imports, add these reactive variables:
import { ElMessage } from 'element-plus'

const detailVisible = ref(false)
const currentResource = ref({})
const comments = ref([])
const newCommentText = ref('')

// Function triggered when clicking "Learn more"
const openDetail = async (item) => {
  currentResource.value = item
  detailVisible.value = true
  fetchComments(item.id) // Fetch comments for this resource
}

// Fetch comments from backend
const fetchComments = async (resourceId) => {
  try {
    const response = await axios.get(`http://116.62.165.182:8080/api/public/resources/${resourceId}/comments`)
    comments.value = response.data
  } catch (error) {
    console.error('Failed to get comments')
  }
}

// Function to submit comment
const submitComment = async () => {
  if (!newCommentText.value.trim()) {
    ElMessage.warning('Comment content cannot be empty!')
    return
  }

  try {
    // Try to get current logged-in username, null if not logged in
    const currentUser = localStorage.getItem('currentUser')

    await axios.post('http://116.62.165.182:8080/api/public/comments', {
      resourceId: currentResource.value.id,
      username: currentUser, // Backend will automatically become "Anonymous Cultural Enthusiast" if null
      content: newCommentText.value
    })

    ElMessage.success('Comment posted successfully!')
    newCommentText.value = '' // Clear input box
    fetchComments(currentResource.value.id) // Refresh comment list!
  } catch (error) {
    ElMessage.error('Failed to post comment')
  }
}

// Request data from Java's new public interface
const fetchPublicData = async () => {
  try {
    const response = await axios.get('http://116.62.165.182:8080/api/public/resources')
    publicResources.value = response.data
  } catch (error) {
    console.error('Failed to get public resources')
  }
}

// Pure frontend implementation of second-level search filtering functionality
const filteredResources = computed(() => {
  if (!searchKeyword.value) return publicResources.value
  return publicResources.value.filter(item =>
      item.title.includes(searchKeyword.value) ||
      item.description.includes(searchKeyword.value)
  )
})

onMounted(() => {
  fetchPublicData()
})
</script>

<style scoped>
.gallery-page { min-height: 100vh; background-color: #f8f9fa; }
.public-nav {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 40px; height: 70px; background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 100;
}
.logo { font-size: 22px; font-weight: bold; color: #2c3e50; }
.hero-banner {
  height: 300px; background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  color: white; display: flex; flex-dirction: column; justify-content: center; align-items: center; flex-direction: column; text-align: center;
}
.hero-banner h1 { font-size: 42px; margin-bottom: 10px; letter-spacing: 2px; }
.hero-banner p { font-size: 18px; opacity: 0.9; }
.gallery-container { max-width: 1200px; margin: -40px auto 40px; padding: 0 20px; position: relative; z-index: 10;}
.resource-card { border-radius: 12px; overflow: hidden; border: none; transition: transform 0.3s ease, box-shadow 0.3s ease; }
.resource-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.1) !important; }
.card-image { width: 100%; height: 220px; object-fit: cover; border-bottom: 1px solid #eee; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.card-title { font-size: 18px; font-weight: bold; color: #303133; }
.card-desc { font-size: 14px; color: #606266; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 15px; height: 60px; line-height: 1.5; }
.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #ebeef5; padding-top: 15px; }
.publish-time { font-size: 12px; color: #909399; }
.detail-btn { font-weight: bold; }
</style>