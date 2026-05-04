<template>
  <div class="home-page">
    <!-- Hero section (unchanged) -->
    <div class="hero">
      <h1>DISCOVER OUR<br>SHARED HERITAGE</h1>
      <p class="subtitle">Stories, traditions and treasures from communities around the world</p>
      
      <!-- [Change only here] Search bar - match the latest mock exactly; keep button on the same row -->
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
      <!-- Category Navigation (unchanged) -->
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

      <!-- Everything below is unchanged (card grid restored) -->
      <el-alert v-if="errorMsg" type="error" show-icon closable>{{ errorMsg }}</el-alert>

      <div class="resource-grid" v-loading="loading">
        <el-card v-for="item in resourceList" :key="item.id" class="resource-card" @click="goToDetail(item.id)">
          <img :src="item.thumbnail || 'https://picsum.photos/id/1015/800/1000'" class="card-img" />
          <div class="card-info">
            <el-tag size="small">{{ item.category }}</el-tag>
            <h3>{{ item.title }}</h3>
            <p class="desc">{{ item.description }}</p>
            <div class="meta">
              <span>by {{ item.contributorUsername }}</span>
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

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const resourceList = ref([])
const categoryCounts = ref({})
const searchKeyword = ref('')
const activeCategory = ref('')
const currentPage = ref(1)
const totalResources = ref(0)

const fetchCategoryCounts = async () => {
  try {
    const res = await axios.get('http://116.62.165.182:8080/api/public/categories/count')
    categoryCounts.value = res.data || {}
  } catch (e) {}
}

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

const handleSearch = () => {
  currentPage.value = 1
  fetchResources()
}

const selectCategory = (cat) => {
  activeCategory.value = cat
  currentPage.value = 1
  fetchResources()
}

const goToDetail = (id) => router.push(`/resource/${id}`)

const formatDate = (dateStr) => dateStr ? dateStr.substring(0, 10) : ''

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

/* [Change only here] Search bar - compact + force single row */
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

/* Everything below is unchanged */
.categories-bar { display: flex; align-items: center; padding: 40px 0 20px; border-bottom: 1px solid #eee; }
.label { font-size: 13px; letter-spacing: 3px; font-weight: 500; margin-right: 32px; color: #555; }
.tags { display: flex; gap: 14px; }
.resource-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 32px; padding: 40px 0; }
.resource-card { border: none; box-shadow: 0 4px 20px rgba(0,0,0,0.08); transition: transform 0.4s ease; }
.resource-card:hover { transform: translateY(-8px); }
.card-img { width: 100%; height: 260px; object-fit: cover; }
.card-info { padding: 24px; }
.card-info h3 { font-size: 1.4rem; font-weight: 500; margin: 12px 0 8px; }
.desc { color: #555; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; overflow: hidden; }
.meta { margin-top: 16px; font-size: 0.9rem; color: #777; display: flex; justify-content: space-between; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 60px; }
</style>
