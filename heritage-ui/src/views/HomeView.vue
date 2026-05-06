<template>
  <div class="home-page">
    <!-- Hero section -->
    <div class="hero">
      <h1>DISCOVER OUR<br>SHARED HERITAGE</h1>
      <p class="subtitle">Stories, traditions and treasures from communities around the world</p>

      <!-- Search bar -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="Search heritage names, descriptions or tags..."
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">SEARCH</el-button>
      </div>
    </div>

    <div class="main-content">
      <!-- Category navigation -->
      <div class="categories-bar">
        <span class="label">CATEGORY NAVIGATION</span>
        <div class="tags">
          <el-tag :effect="activeCategory === '' ? 'dark' : 'plain'" @click="selectCategory('')">ALL</el-tag>
          <el-tag
            v-for="(count, name) in categoryCounts"
            :key="name"
            :effect="activeCategory === name ? 'dark' : 'plain'"
            @click="selectCategory(name)">
            {{ name }}
          </el-tag>
        </div>
      </div>

      <el-alert v-if="errorMsg" type="error" show-icon closable>{{ errorMsg }}</el-alert>

      <!-- Resource card grid -->
      <div class="resource-grid" v-loading="loading">
        <el-card v-for="item in resourceList" :key="item.id" class="resource-card" @click="goToDetail(item.id)">
          <img :src="item.thumbnail || 'https://picsum.photos/id/1015/800/1000'" class="card-img" />
          <div class="card-info">
            <el-tag size="small">{{ item.category }}</el-tag>
            <h3>{{ item.title }}</h3>
            <!-- Only key metadata: location and contributor -->
            <div class="meta">
              <span><el-icon><Location /></el-icon> {{ item.location || 'Unrecorded' }}</span>
              <span><el-icon><User /></el-icon> {{ item.contributorUsername }}</span>
            </div>
            <div class="meta time">
              <span>{{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <div class="pagination-wrap" v-if="totalResources > 0">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="totalResources"
          :page-size="12"
          v-model:current-page="currentPage"
          @current-change="fetchResources"
        />
      </div>

      <el-empty v-if="!loading && resourceList.length === 0" description="No resources found" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { Location, User } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const resourceList = ref([])
const categoryCounts = ref({})
const searchKeyword = ref('')
const activeCategory = ref('')
const currentPage = ref(1)
const totalResources = ref(0)

// Fetch category counts from the server
const fetchCategoryCounts = async () => {
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/public/categories/count')
    categoryCounts.value = res.data || {}
  } catch (e) {}
}

// Fetch resources with optional search keyword and category filter
const fetchResources = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    let url = `http://116.62.165.182:8080/api/public/resources?current=${currentPage.value}&size=12`
    if (searchKeyword.value) url += `&keyword=${encodeURIComponent(searchKeyword.value)}`
    if (activeCategory.value) url += `&category=${encodeURIComponent(activeCategory.value)}`
    const res = await axios.get(url)
    resourceList.value = res.data.records || []
    totalResources.value = res.data.total || 0
  } catch (e) {
    errorMsg.value = 'Failed to load resources'
  } finally {
    loading.value = false
  }
}

// Trigger search
const handleSearch = () => {
  currentPage.value = 1
  fetchResources()
}

// Select a category filter
const selectCategory = (cat) => {
  activeCategory.value = cat
  currentPage.value = 1
  fetchResources()
}

// Navigate to resource detail page
const goToDetail = (id) => router.push(`/resource/${id}`)

// Format date string (e.g., 2026-05-06T12:34:56 -> 2026-05-06)
const formatDate = (dateStr) => dateStr ? dateStr.substring(0, 10) : ''

// Initial data loading
onMounted(() => {
  fetchCategoryCounts()
  fetchResources()
})
</script>

<style scoped>
.home-page { min-height: 100vh; background: #fff; color: #111; }

.hero {
  background: linear-gradient(rgba(15,23,42,0.75), rgba(15,23,42,0.75)), url('https://picsum.photos/id/1015/2000/1200') center/cover no-repeat;
  padding: 160px 40px 120px;
  text-align: center;
  color: #fff;
}

.hero h1 {
  font-size: 5.2rem;
  font-weight: 300;
  letter-spacing: -6px;
  line-height: 1.05;
  margin-bottom: 24px;
}

.subtitle {
  font-size: 1.4rem;
  max-width: 720px;
  margin: 0 auto 60px;
  opacity: 0.95;
}

/* Search bar */
.search-bar {
  max-width: 780px;
  margin: 40px auto 0;
  background: #fff;
  border: 2px solid #111;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  box-shadow: 0 15px 50px rgba(0,0,0,0.18);
}

.search-bar :deep(.el-input__inner) {
  border: none;
  font-size: 17px;
  padding: 18px 24px;
  height: 58px;
  box-shadow: none;
  flex: 1;
}

.search-bar :deep(.el-button) {
  height: 58px;
  padding: 0 48px;
  font-size: 17px;
  font-weight: 300;
  letter-spacing: 3px;
  background-color: #1e3a5f !important;
  border: none;
  color: #fff;
  border-radius: 0;
}

/* Category navigation bar */
.categories-bar { display: flex; align-items: center; padding: 40px 0 20px; border-bottom: 1px solid #eee; }
.label { font-size: 13px; letter-spacing: 3px; font-weight: 500; margin-right: 32px; color: #555; }
.tags { display: flex; gap: 14px; }

/* Resource card grid */
.resource-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 32px; padding: 40px 0; }
.resource-card { border: none; box-shadow: 0 4px 20px rgba(0,0,0,0.08); transition: transform 0.4s ease; }
.resource-card:hover { transform: translateY(-8px); }
.card-img { width: 100%; height: 260px; object-fit: cover; }
.card-info { padding: 24px; }
.card-info h3 { font-size: 1.3rem; font-weight: 500; margin: 10px 0 8px; }

/* Metadata rows */
.meta { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-top: 8px; font-size: 0.85rem; color: #777; }
.meta.time { margin-top: 4px; font-size: 0.8rem; }

/* Hide original description if present */
.desc { display: none; }

/* Pagination */
.pagination-wrap { display: flex; justify-content: center; margin-top: 60px; }
</style>