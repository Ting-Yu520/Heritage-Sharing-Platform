<template>
  <div class="gallery-page">
    <div class="public-nav">
      <div class="logo">🏛️ 社区遗产资源共享平台</div>
      <div class="nav-actions">
        <el-input v-model="searchKeyword" placeholder="搜索遗产资源..." prefix-icon="Search" style="width: 250px; margin-right: 20px" clearable />
        <el-button type="primary" plain @click="$router.push('/login')">贡献者/管理员登录</el-button>
      </div>
    </div>

    <div class="hero-banner">
      <h1>探索、传承、共享我们的文化瑰宝</h1>
      <p>发现身边的非物质文化遗产与历史印记</p>
    </div>

    <div class="gallery-container">
      <el-empty v-if="filteredResources.length === 0" description="暂无公开展出的资源，敬请期待！" />

      <el-row :gutter="24" v-else>
        <el-col :xs="24" :sm="12" :md="8" v-for="item in filteredResources" :key="item.id" style="margin-bottom: 24px;">
          <el-card class="resource-card" shadow="hover" :body-style="{ padding: '0px' }">
            <img :src="`https://source.unsplash.com/800x600/?heritage,culture,${item.id}`" class="card-image" alt="封面图" />

            <div style="padding: 20px;">
              <div class="card-header">
                <span class="card-title">{{ item.title }}</span>
                <el-tag size="small" effect="dark" color="#2c3e50" style="border:none">{{ item.category }}</el-tag>
              </div>
              <p class="card-desc">{{ item.description }}</p>
              <div class="card-footer">
                <span class="publish-time">编号: #{{ item.id }}</span>
                <div class="card-footer">
                  <el-button type="text" class="detail-btn" @click="openDetail(item)">了解详情 >></el-button>
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

        <el-divider>💬 游客留言板</el-divider>

        <div style="margin-bottom: 20px; display: flex; gap: 10px;">
          <el-input v-model="newCommentText" placeholder="写下你的感悟或留言..." type="textarea" :rows="2" />
          <el-button type="primary" style="height: auto;" @click="submitComment">发表<br>评论</el-button>
        </div>

        <div v-if="comments.length === 0" style="text-align: center; color: #999; padding: 20px 0;">
          暂无留言，快来抢沙发吧！
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

// 在 script setup 引入的下方，补充这些响应式变量：
import { ElMessage } from 'element-plus'

const detailVisible = ref(false)
const currentResource = ref({})
const comments = ref([])
const newCommentText = ref('')

// 点击“了解详情”触发的函数
const openDetail = async (item) => {
  currentResource.value = item
  detailVisible.value = true
  fetchComments(item.id) // 拉取这篇资源的评论
}

// 去后端拉取评论
const fetchComments = async (resourceId) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/public/resources/${resourceId}/comments`)
    comments.value = response.data
  } catch (error) {
    console.error('获取评论失败')
  }
}

// 提交评论的函数
const submitComment = async () => {
  if (!newCommentText.value.trim()) {
    ElMessage.warning('留言内容不能为空哦！')
    return
  }

  try {
    // 尝试获取当前登录的用户名，如果没登录就是 null
    const currentUser = localStorage.getItem('currentUser')

    await axios.post('http://localhost:8080/api/public/comments', {
      resourceId: currentResource.value.id,
      username: currentUser, // 后端如果收到 null 会自动变成“匿名文化爱好者”
      content: newCommentText.value
    })

    ElMessage.success('评论发表成功！')
    newCommentText.value = '' // 清空输入框
    fetchComments(currentResource.value.id) // 刷新评论列表！
  } catch (error) {
    ElMessage.error('评论发表失败')
  }
}

// 向 Java 的新公开接口请求数据
const fetchPublicData = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/public/resources')
    publicResources.value = response.data
  } catch (error) {
    console.error('获取公开资源失败')
  }
}

// 纯前端实现秒级搜索过滤功能
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