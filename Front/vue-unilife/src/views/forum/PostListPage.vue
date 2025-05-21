<template>
  <div class="post-list-page">
    <el-container>
      <el-aside width="250px" class="sidebar">
        <h3>Categories</h3>
        <el-menu
          :default-active="selectedCategoryId || 'all'"
          class="category-menu"
          @select="handleCategorySelect"
        >
          <el-menu-item index="all">
            <el-icon><Collection /></el-icon>
            <span>All Posts</span>
          </el-menu-item>
          <el-menu-item
            v-for="category in categories"
            :key="category.id"
            :index="category.id.toString()"
          >
            <el-icon><FolderOpened /></el-icon>
            <span>{{ category.name }}</span>
          </el-menu-item>
        </el-menu>
        <el-divider />
        <h3>Sort By</h3>
         <el-radio-group v-model="sortBy" @change="handleSortChange" class="sort-options">
          <el-radio-button label="latest">Latest</el-radio-button>
          <el-radio-button label="views">Most Viewed</el-radio-button>
          <el-radio-button label="likes">Most Liked</el-radio-button>
        </el-radio-group>
      </el-aside>

      <el-main>
        <div class="toolbar">
          <h2>{{ pageTitle }}</h2>
          <router-link to="/forum/post/create">
            <el-button type="primary" :icon="EditPen">Create Post</el-button>
          </router-link>
        </div>

        <el-skeleton :rows="10" animated v-if="loading" />

        <div v-if="!loading && posts.length === 0" class="empty-state">
          <el-empty description="No posts found in this category or matching your criteria." />
        </div>

        <div v-if="!loading && posts.length > 0" class="post-list">
          <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover">
            <template #header>
              <div class="post-header">
                <router-link :to="`/forum/post/${post.id}`" class="post-title-link">
                  <h3 class="post-title">{{ post.title }}</h3>
                </router-link>
                <span class="post-author">by {{ post.user?.nickname || post.user?.username || 'Unknown Author' }}</span>
              </div>
            </template>
            <div class="post-content-snippet" v-html="truncateText(post.content, 150)"></div>
            <div class="post-meta">
              <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }} views</span>
              <span><el-icon><Pointer /></el-icon> {{ post.likeCount || 0 }} likes</span>
              <span class="post-date"><el-icon><Calendar /></el-icon> {{ formatDate(post.createdAt) }}</span>
            </div>
          </el-card>
        </div>

        <el-pagination
          v-if="!loading && totalPosts > pageSize"
          class="pagination"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalPosts"
          layout="prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import { ElMessage, ElButton, ElCard, ElContainer, ElAside, ElMain, ElMenu, ElMenuItem, ElIcon, ElPagination, ElSkeleton, ElEmpty, ElDivider, ElRadioGroup, ElRadioButton } from 'element-plus';
import { Collection, FolderOpened, EditPen, View, Pointer, Calendar } from '@element-plus/icons-vue';
import { useRouter, useRoute } from 'vue-router';
import DOMPurify from 'dompurify';

interface PostUser {
  id: number;
  username: string;
  nickname?: string;
}
interface Post {
  id: number;
  title: string;
  content: string;
  categoryId: number;
  categoryName?: string; // Assuming category name might come with post data
  userId: number;
  user?: PostUser;
  viewCount: number;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
}

interface Category {
  id: number;
  name: string;
  description?: string;
}

export default defineComponent({
  name: 'PostListPage',
  components: {
    ElButton, ElCard, ElContainer, ElAside, ElMain, ElMenu, ElMenuItem, ElIcon, ElPagination, ElSkeleton, ElEmpty, ElDivider, ElRadioGroup, ElRadioButton,
    Collection, FolderOpened, EditPen, View, Pointer, Calendar,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();

    const posts = ref<Post[]>([]);
    const categories = ref<Category[]>([]);
    const loading = ref(true);
    const currentPage = ref(1);
    const pageSize = ref(10); // Default page size
    const totalPosts = ref(0);
    const selectedCategoryId = ref<string | null>(null); // Can be 'all' or category ID
    const sortBy = ref('latest'); // 'latest', 'views', 'likes'

    const pageTitle = computed(() => {
      if (selectedCategoryId.value && selectedCategoryId.value !== 'all') {
        const category = categories.value.find(c => c.id.toString() === selectedCategoryId.value);
        return category ? `${category.name} Posts` : 'Posts';
      }
      return 'All Posts';
    });

    const fetchCategories = async () => {
      try {
        const response = await axios.get('/api/categories');
        if (response.data && response.data.code === 200) {
          categories.value = response.data.data || [];
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch categories.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching categories.');
        console.error("Fetch categories error:", error);
      }
    };

    const fetchPosts = async () => {
      loading.value = true;
      let url = '/api/posts';
      const params: any = {
        page: currentPage.value,
        size: pageSize.value,
        sort: sortBy.value, // Add sort parameter
      };

      if (selectedCategoryId.value && selectedCategoryId.value !== 'all') {
        url = `/api/posts/category/${selectedCategoryId.value}`;
      }
      
      // The backend API doc GET /api/posts doesn't explicitly mention a 'sort' query param.
      // Assuming it might be supported or will be ignored gracefully if not.
      // If sorting is purely client-side after fetching all (not recommended for large datasets), this logic would change.
      // For now, passing it as a query param.

      try {
        const response = await axios.get(url, { params });
        if (response.data && response.data.code === 200 && response.data.data) {
          posts.value = response.data.data.records || response.data.data || []; // Adapt based on actual API response structure for pagination
          totalPosts.value = response.data.data.total || posts.value.length; // Adapt for pagination
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch posts.');
          posts.value = [];
          totalPosts.value = 0;
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching posts.');
        console.error("Fetch posts error:", error);
        posts.value = [];
        totalPosts.value = 0;
      } finally {
        loading.value = false;
      }
    };

    const handleCategorySelect = (index: string) => {
      selectedCategoryId.value = index === 'all' ? null : index;
      currentPage.value = 1; // Reset to first page
      router.push({ query: { ...route.query, category: index === 'all' ? undefined : index, page: undefined } });
      // Fetch posts will be triggered by watcher or can be called directly
    };

    const handleSortChange = (newSortBy: string | number | boolean) => {
        sortBy.value = newSortBy as string;
        currentPage.value = 1;
        router.push({ query: { ...route.query, sort: sortBy.value, page: undefined } });
        // fetchPosts(); // Watcher will also trigger this
    };

    const handlePageChange = (page: number) => {
      currentPage.value = page;
      router.push({ query: { ...route.query, page: page === 1 ? undefined : page } });
      // fetchPosts(); // Watcher will also trigger this
    };

    const formatDate = (dateString: string) => {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    const truncateText = (htmlContent: string, maxLength: number) => {
      // First, create a temporary DOM element to parse the HTML and extract text
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = DOMPurify.sanitize(htmlContent, { USE_PROFILES: { html: true } }); // Sanitize before parsing
      
      // Get text content
      let textContent = tempDiv.textContent || tempDiv.innerText || "";
      
      if (textContent.length > maxLength) {
        return textContent.substring(0, maxLength) + '...';
      }
      return textContent; // If shorter than max, return as is (text only)
                          // Or, if you want to return truncated HTML:
                          // return DOMPurify.sanitize(htmlContent.substring(0, someCalculatedHtmlLength) + '...');
                          // For simplicity, returning truncated text.
    };
    
    onMounted(() => {
      // Set initial state from route query params
      selectedCategoryId.value = route.query.category as string || null;
      sortBy.value = route.query.sort as string || 'latest';
      currentPage.value = parseInt(route.query.page as string) || 1;

      fetchCategories();
      fetchPosts();
    });

    // Watch for route query changes to re-fetch data
    watch(
      () => route.query,
      (newQuery, oldQuery) => {
        const newPage = parseInt(newQuery.page as string) || 1;
        const newCategory = newQuery.category as string || null;
        const newSort = newQuery.sort as string || 'latest';

        let needsFetch = false;
        if (newPage !== currentPage.value) {
            currentPage.value = newPage;
            needsFetch = true;
        }
        if (newCategory !== selectedCategoryId.value) {
            selectedCategoryId.value = newCategory;
            if(currentPage.value !== 1) currentPage.value =1; // Reset page if category changes unless already 1
            else needsFetch = true; // If page is 1 but cat changes, still fetch
        }
         if (newSort !== sortBy.value) {
            sortBy.value = newSort;
            if(currentPage.value !== 1) currentPage.value =1;
            else needsFetch = true;
        }

        if (needsFetch || (!newQuery.page && !newQuery.category && !newQuery.sort)) { // Initial load or clear
             fetchPosts();
        }
      },
      { deep: true } // Not strictly necessary for query object itself but good practice
    );


    return {
      posts,
      categories,
      loading,
      currentPage,
      pageSize,
      totalPosts,
      selectedCategoryId,
      sortBy,
      pageTitle,
      handleCategorySelect,
      handleSortChange,
      handlePageChange,
      formatDate,
      truncateText,
      EditPen, // Icon
    };
  },
});
</script>

<style scoped>
.post-list-page {
  padding: 20px;
}
.sidebar {
  padding-right: 20px;
  border-right: 1px solid #e4e7ed;
}
.sidebar h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
  color: #303133;
}
.category-menu .el-menu-item {
  height: 40px;
  line-height: 40px;
}
.category-menu .el-menu-item.is-active {
  font-weight: bold;
  color: var(--el-color-primary);
}
.sort-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start; /* Align radio buttons to the left */
}
.sort-options .el-radio-button {
    margin-right: 0; /* Remove default right margin if any */
}
.sort-options .el-radio-button :deep(.el-radio-button__inner) { /* Deep selector for nested element */
    width: 100%;
    text-align: left;
    border-radius: 4px !important; /* Override default button group styling */
    border: 1px solid #dcdfe6 !important;
    margin-bottom: -1px; /* Adjust for border collapse effect */
}
.sort-options .el-radio-button.is-active :deep(.el-radio-button__inner) {
    background-color: var(--el-color-primary-light-9);
    border-color: var(--el-color-primary) !important;
    color: var(--el-color-primary);
    box-shadow: -1px 0 0 0 var(--el-color-primary) !important;
}


.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar h2 {
  margin: 0;
}

.post-card {
  margin-bottom: 20px;
  transition: box-shadow 0.3s ease-in-out;
}
.post-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.post-title-link {
  text-decoration: none;
  color: inherit;
}
.post-title {
  margin: 0;
  font-size: 1.5em;
  color: #303133;
}
.post-title-link:hover .post-title {
  color: var(--el-color-primary);
}
.post-author {
  font-size: 0.9em;
  color: #606266;
}

.post-content-snippet {
  color: #606266;
  font-size: 1em;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9em;
  color: #909399;
}
.post-meta span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.post-meta .post-date {
  margin-left: auto; /* Pushes date to the right if it's the last item */
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.empty-state {
  margin-top: 40px;
  text-align: center;
}
</style>
