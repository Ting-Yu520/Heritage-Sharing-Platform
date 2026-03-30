<template>
  <div class="home-container">
    <div class="hero-section">
      <h1 style="color: white; font-size: 36px; margin-bottom: 20px;">探索非物质文化遗产的魅力</h1>
      <el-input
          v-model="searchKeyword"
          placeholder="搜索遗产名称、简介或标签..."
          class="search-bar"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">🔍 搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="main-content">
      <div class="filter-section">
        <span style="font-weight: bold; margin-right: 15px;">分类导航：</span>
        <el-tag
            :effect="activeCategory === '' ? 'dark' : 'plain'"
            class="filter-tag"
            @click="selectCategory('')">
          全部 ({{ totalResources }})
        </el-tag>
        <el-tag
            v-for="(count, name) in categoryCounts"
            :key="name"
            :effect="activeCategory === name ? 'dark' : 'plain'"
            class="filter-tag"
            @click="selectCategory(name)">
          {{ name }} ({{ count }})
        </el-tag>
      </div>

      <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon style="margin-bottom: 20px;">
        <el-button size="small" type="danger" plain @click="fetchData">重新加载</el-button>
      </el-alert>

      <div class="resource-grid" v-loading="loading">
        <el-card
            v-for="item in resourceList"
            :key="item.id"
            class="resource-card"
            shadow="hover"
            @click="goToDetail(item.id)">
          <div class="card-img-wrap">
            <img :src="item.thumbnail || 'https://via.placeholder.com/400x250?text=No+Image'" class="card-img" />
            <el-tag size="small" type="warning" class="category-badge">{{ item.category }}</el-tag>
          </div>
          <div style="padding: 15px;">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-desc">{{ item.description }}</p>
            <div class="card-meta">
              <span>👤 {{ item.contributorUsername }}</span>
              <span>🕒 {{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <el-empty
          v-if="!loading && resourceList.length === 0 && !errorMsg"
          :description="searchKeyword ? 'No relevant resources found. Please try other keywords.' : 'No recommended content available at this time. Please check back soon.'"
      />

      <div class="pagination-wrap" v-if="totalResources > 0">
        <el-pagination background layout="total, prev, pager, next" :total="totalResources" :page-size="12" v-model:current-page="currentPage" @current-change="fetchResources" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')

// 数据状态
const resourceList = ref([])
const categoryCounts = ref({})
const searchKeyword = ref('')
const activeCategory = ref('')
const currentPage = ref(1)
const totalResources = ref(0)

// 获取分类及其数量统计 (PBI 3)
const fetchCategoryCounts = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/public/categories/count')
    categoryCounts.value = res.data
  } catch (e) {
    console.error('获取分类失败')
  }
}

// 获取核心资源列表 (支持搜索和过滤)
const fetchResources = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    let url = `http://localhost:8080/api/public/resources?current=${currentPage.value}&size=12`
    if (searchKeyword.value) url += `&keyword=${encodeURIComponent(searchKeyword.value)}`
    if (activeCategory.value) url += `&category=${encodeURIComponent(activeCategory.value)}`

    const res = await axios.get(url)
    resourceList.value = res.data.records
    totalResources.value = res.data.total
  } catch (e) {
    errorMsg.value = 'System crash—please try refreshing. 系统请求失败，请尝试刷新。'
  } finally {
    loading.value = false
  }
}

// 交互操作
const handleSearch = () => { currentPage.value = 1; fetchResources() }
const selectCategory = (cat) => { activeCategory.value = cat; currentPage.value = 1; fetchResources() }
const goToDetail = (id) => { router.push(`/resource/${id}`) }
const formatDate = (dateStr) => { return dateStr ? dateStr.substring(0, 10) : '未知日期' }

const fetchData = () => { fetchCategoryCounts(); fetchResources() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.hero-section { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); padding: 60px 20px; text-align: center; }
.search-bar { max-width: 600px; margin: 0 auto; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border-radius: 8px; }
:deep(.search-bar .el-input__wrapper) { padding-left: 15px; height: 50px; font-size: 16px; }
.main-content { max-width: 1200px; margin: -30px auto 40px; padding: 20px; background: white; border-radius: 12px; box-shadow: 0 5px 20px rgba(0,0,0,0.05); }
.filter-section { margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid #ebeef5; }
.filter-tag { margin-right: 10px; cursor: pointer; transition: all 0.2s; font-size: 14px; padding: 6px 12px; height: auto; }
.filter-tag:hover { transform: translateY(-2px); }
.resource-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 25px; }
.resource-card { cursor: pointer; border: none; transition: transform 0.3s, box-shadow 0.3s; border-radius: 10px; overflow: hidden; }
.resource-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.card-img-wrap { position: relative; height: 180px; overflow: hidden; }
.card-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.3s; }
.resource-card:hover .card-img { transform: scale(1.05); }
.category-badge { position: absolute; top: 10px; right: 10px; z-index: 10; opacity: 0.9; }
.card-title { margin: 0 0 10px; font-size: 18px; color: #303133; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.card-desc { font-size: 13px; color: #606266; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 15px; height: 39px; }
.card-meta { display: flex; justify-content: space-between; font-size: 12px; color: #909399; border-top: 1px solid #f0f0f0; padding-top: 10px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 40px; }
</style>