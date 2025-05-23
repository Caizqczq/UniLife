<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <div class="search-input-container">
        <el-input
          v-model="searchKeyword"
          size="large"
          placeholder="搜索帖子、资源、用户..."
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <!-- 搜索过滤器 -->
      <div class="search-filters">
        <el-radio-group v-model="searchType" @change="handleSearch">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="post">帖子</el-radio-button>
          <el-radio-button label="resource">资源</el-radio-button>
          <el-radio-button label="user">用户</el-radio-button>
        </el-radio-group>
        
        <el-select v-model="sortBy" placeholder="排序方式" @change="handleSearch" style="width: 120px; margin-left: 10px;">
          <el-option label="相关性" value="relevance" />
          <el-option label="时间" value="time" />
          <el-option label="热门度" value="popularity" />
        </el-select>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-content" v-loading="loading">
      <!-- 搜索统计 -->
      <div class="search-stats" v-if="searchResult">
        <span>找到 {{ searchResult.total }} 个结果</span>
        <span class="search-time">耗时 {{ searchResult.searchTime }}ms</span>
      </div>

      <!-- 搜索结果列表 -->
      <div class="search-results" v-if="searchResult && searchResult.items.length > 0">
        <div 
          v-for="item in searchResult.items" 
          :key="`${item.type}-${item.id}`"
          class="search-item"
          @click="handleItemClick(item)"
        >
          <div class="item-header">
            <el-tag :type="getTypeTagType(item.type)" size="small">
              {{ getTypeText(item.type) }}
            </el-tag>
            <span class="item-title">{{ item.title }}</span>
          </div>
          
          <div class="item-content">
            <p class="item-summary">{{ item.summary }}</p>
          </div>
          
          <div class="item-meta">
            <div class="author-info">
              <el-avatar :src="item.avatar" :size="20" />
              <span class="author-name">{{ item.author }}</span>
            </div>
            
            <div class="meta-info">
              <span v-if="item.categoryName" class="category">{{ item.categoryName }}</span>
              <span class="create-time">{{ formatTime(item.createdAt) }}</span>
              <span class="stats">
                <el-icon><StarFilled /></el-icon>{{ item.likeCount }}
                <el-icon style="margin-left: 10px;"><View /></el-icon>{{ item.viewCount }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="searchResult && searchResult.items.length === 0" 
        description="没有找到相关内容"
      />

      <!-- 分页 -->
      <div class="pagination" v-if="searchResult && searchResult.total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="searchResult.total"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 热门搜索词 -->
    <div class="hot-keywords" v-if="!searchResult">
      <h3>热门搜索</h3>
      <div class="keyword-tags">
        <el-tag 
          v-for="keyword in hotKeywords" 
          :key="keyword"
          @click="handleHotKeywordClick(keyword)"
          style="margin: 5px; cursor: pointer;"
        >
          {{ keyword }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, StarFilled, View } from '@element-plus/icons-vue'
import { search, searchPosts, searchResources, searchUsers, getHotKeywords } from '@/api/search'
import type { SearchParams, SearchResult, SearchItem } from '@/api/search'

const route = useRoute()
const router = useRouter()

// 搜索状态
const loading = ref(false)
const searchKeyword = ref('')
const searchType = ref<'all' | 'post' | 'resource' | 'user'>('all')
const sortBy = ref<'time' | 'relevance' | 'popularity'>('relevance')
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索结果
const searchResult = ref<SearchResult>()
const hotKeywords = ref<string[]>([])

// 初始化
onMounted(async () => {
  // 从URL参数获取搜索关键词
  const keyword = route.query.keyword as string
  if (keyword) {
    searchKeyword.value = keyword
    handleSearch()
  }
  
  // 获取热门搜索词
  await loadHotKeywords()
})

// 监听路由变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = newKeyword as string
    handleSearch()
  }
})

// 搜索处理
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  currentPage.value = 1

  try {
    const params: SearchParams = {
      keyword: searchKeyword.value,
      type: searchType.value,
      sortBy: sortBy.value,
      page: currentPage.value,
      size: pageSize.value
    }

    let result
    if (searchType.value === 'post') {
      result = await searchPosts(params)
    } else if (searchType.value === 'resource') {
      result = await searchResources(params)
    } else if (searchType.value === 'user') {
      result = await searchUsers(params)
    } else {
      result = await search(params)
    }

    searchResult.value = result

    // 更新URL
    router.replace({
      query: { keyword: searchKeyword.value }
    })
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  currentPage.value = page
  handleSearch()
}

// 点击搜索结果项
const handleItemClick = (item: SearchItem) => {
  if (item.type === 'post') {
    router.push(`/post/${item.id}`)
  } else if (item.type === 'resource') {
    router.push(`/resource/${item.id}`)
  } else if (item.type === 'user') {
    // TODO: 跳转到用户主页
    ElMessage.info('用户主页功能待开发')
  }
}

// 点击热门关键词
const handleHotKeywordClick = (keyword: string) => {
  searchKeyword.value = keyword
  handleSearch()
}

// 获取热门搜索词
const loadHotKeywords = async () => {
  try {
    const result = await getHotKeywords()
    hotKeywords.value = result
  } catch (error) {
    console.error('获取热门搜索词失败:', error)
  }
}

// 获取类型标签类型
const getTypeTagType = (type: string) => {
  switch (type) {
    case 'post': return 'primary'
    case 'resource': return 'success'
    case 'user': return 'warning'
    default: return 'info'
  }
}

// 获取类型文本
const getTypeText = (type: string) => {
  switch (type) {
    case 'post': return '帖子'
    case 'resource': return '资源'
    case 'user': return '用户'
    default: return '未知'
  }
}

// 格式化时间
const formatTime = (time: string) => {
  return new Date(time).toLocaleDateString()
}
</script>

<style scoped>
.search-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  margin-bottom: 20px;
}

.search-input-container {
  margin-bottom: 15px;
}

.search-filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.search-time {
  color: #999;
}

.search-results {
  margin-bottom: 20px;
}

.search-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.item-content {
  margin-bottom: 15px;
}

.item-summary {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  color: #409eff;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.category {
  background: #f4f4f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.stats {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.hot-keywords {
  margin-top: 40px;
}

.hot-keywords h3 {
  margin-bottom: 15px;
  color: #303133;
}
</style>