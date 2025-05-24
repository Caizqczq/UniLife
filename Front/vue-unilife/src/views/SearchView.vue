<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <div class="search-input-container">
        <el-input
          v-model="searchKeyword"
          size="large"
          placeholder="搜索帖子、资源..."
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
          <el-radio-button label="post">帖子</el-radio-button>
          <el-radio-button label="resource">资源</el-radio-button>
        </el-radio-group>
        
        <el-select v-model="sortBy" placeholder="排序方式" @change="handleSearch" style="width: 120px; margin-left: 10px;">
          <el-option label="最新" value="latest" />
          <el-option label="热门" value="hot" />
          <el-option label="点赞" value="likes" />
        </el-select>
        
        <el-select v-model="categoryId" placeholder="选择分类" @change="handleSearch" style="width: 150px; margin-left: 10px;" clearable>
          <el-option 
            v-for="category in categories" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id" 
          />
        </el-select>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-content" v-loading="loading">
      <!-- 搜索统计 -->
      <div class="search-stats" v-if="searchResult">
        <span>找到 {{ searchResult.total || 0 }} 个{{ searchType === 'post' ? '帖子' : '资源' }}</span>
        <span v-if="searchResult.total" class="search-time">共{{ searchResult.pages || 1 }}页</span>
      </div>

      <!-- 帖子搜索结果 -->
      <div class="search-results" v-if="searchType === 'post' && searchResult && searchResult.list && searchResult.list.length > 0">
        <div 
          v-for="item in searchResult.list" 
          :key="item.id"
          class="search-item post-item"
          @click="handlePostClick(item)"
        >
          <div class="item-header">
            <el-tag type="primary" size="small">帖子</el-tag>
            <span class="item-title">{{ item.title }}</span>
          </div>
          
          <div class="item-content">
            <p class="item-summary">{{ item.summary }}</p>
          </div>
          
          <div class="item-meta">
            <div class="author-info">
              <el-avatar :src="item.avatar" :size="20" />
              <span class="author-name">{{ item.nickname }}</span>
            </div>
            
            <div class="meta-info">
              <span class="category">{{ item.categoryName }}</span>
              <span class="create-time">{{ formatTime(item.createdAt) }}</span>
              <span class="stats">
                <el-icon><StarFilled /></el-icon>{{ item.likeCount }}
                <el-icon style="margin-left: 10px;"><View /></el-icon>{{ item.viewCount }}
                <el-icon style="margin-left: 10px;"><ChatDotSquare /></el-icon>{{ item.commentCount }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 资源搜索结果 -->
      <div class="search-results" v-else-if="searchType === 'resource' && searchResult && searchResult.list && searchResult.list.length > 0">
        <div 
          v-for="item in searchResult.list" 
          :key="item.id"
          class="search-item resource-item"
          @click="handleResourceClick(item)"
        >
          <div class="item-header">
            <el-tag type="success" size="small">资源</el-tag>
            <span class="item-title">{{ item.title }}</span>
          </div>
          
          <div class="item-content">
            <p class="item-summary">{{ item.description }}</p>
          </div>
          
          <div class="item-meta">
            <div class="author-info">
              <el-avatar :src="item.avatar" :size="20" />
              <span class="author-name">{{ item.nickname }}</span>
            </div>
            
            <div class="meta-info">
              <span class="category">{{ item.categoryName }}</span>
              <span class="file-info">{{ formatFileSize(item.fileSize) }} · {{ item.fileType }}</span>
              <span class="create-time">{{ formatTime(item.createdAt) }}</span>
              <span class="stats">
                <el-icon><StarFilled /></el-icon>{{ item.likeCount }}
                <el-icon style="margin-left: 10px;"><Download /></el-icon>{{ item.downloadCount }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="searchResult && (!searchResult.list || searchResult.list.length === 0)" 
        :description="`没有找到相关${searchType === 'post' ? '帖子' : '资源'}`"
      />

      <!-- 分页 -->
      <div class="pagination" v-if="searchResult && (searchResult.total || 0) > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="searchResult.total || 0"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, StarFilled, View, ChatDotSquare, Download } from '@element-plus/icons-vue'
import { searchPosts, getCategories } from '@/api/forum'
import resourceApi from '@/api/resource'

const route = useRoute()
const router = useRouter()

// 搜索状态
const loading = ref(false)
const searchKeyword = ref('')
const searchType = ref<'post' | 'resource'>('post')
const sortBy = ref<'latest' | 'hot' | 'likes'>('latest')
const categoryId = ref<number>()
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索结果
const searchResult = ref<any>()
const categories = ref<any[]>([])

// 初始化
onMounted(async () => {
  // 从URL参数获取搜索关键词
  const keyword = route.query.keyword as string
  if (keyword) {
    searchKeyword.value = keyword
    handleSearch()
  }
  
  // 获取分类列表
  await loadCategories()
})

// 监听路由变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = newKeyword as string
    handleSearch()
  }
})

// 加载分类
const loadCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response?.data?.list || response || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 搜索处理
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  currentPage.value = 1

  try {
    let response
    if (searchType.value === 'post') {
      response = await searchPosts(
        searchKeyword.value,
        currentPage.value,
        pageSize.value,
        categoryId.value,
        sortBy.value
      )
    } else {
      response = await resourceApi.getResources({
        page: currentPage.value,
        size: pageSize.value,
        categoryId: categoryId.value,
        keyword: searchKeyword.value
      })
    }

    // 调试日志
    console.log('API响应:', response)
    console.log('响应数据类型:', typeof response)
    console.log('response.data:', response.data)

    // 统一处理响应数据格式
    searchResult.value = response.data || response
    
    console.log('最终搜索结果:', searchResult.value)
    console.log('结果列表:', searchResult.value?.list)

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

// 处理帖子点击
const handlePostClick = (post: any) => {
  router.push(`/forum/post/${post.id}`)
}

// 处理资源点击
const handleResourceClick = (resource: any) => {
  router.push(`/resources/${resource.id}`)
}

// 时间格式化
const formatTime = (time: string) => {
  return new Date(time).toLocaleString()
}

// 文件大小格式化
const formatFileSize = (size: number) => {
  if (size < 1024) return size + 'B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + 'MB'
  return (size / (1024 * 1024 * 1024)).toFixed(1) + 'GB'
}
</script>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-input-container {
  margin-bottom: 15px;
}

.search-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.search-stats {
  padding: 20px 20px 10px;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

.search-results {
  padding: 20px;
}

.search-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.item-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  margin-left: 10px;
  color: #333;
}

.item-content {
  margin-bottom: 15px;
}

.item-summary {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #999;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stats {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  padding: 20px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .search-page {
    padding: 10px;
  }
  
  .search-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .item-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>