<template>
  <div class="my-posts-container">
    <el-card class="my-posts-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>我的帖子</h3>
          <el-button type="primary" @click="createNewPost">发布新帖子</el-button>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="posts.length === 0" class="empty-container">
        <el-empty description="您还没有发布过帖子" />
        <el-button type="primary" @click="createNewPost">立即发布</el-button>
      </div>
      
      <div v-else class="posts-list">
        <el-table :data="posts" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="180">
            <template #default="{ row }">
              <router-link :to="`/forum/post/${row.id}`" class="post-title">
                {{ row.title }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
          <el-table-column prop="likeCount" label="点赞" width="80" align="center" />
          <el-table-column prop="commentCount" label="评论" width="80" align="center" />
          <el-table-column prop="createdAt" label="发布时间" width="150">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button
                size="small"
                type="primary"
                plain
                @click="editPost(row.id)"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                type="danger"
                plain
                @click="deletePost(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { deletePost as apiDeletePost } from '@/api/forum';
import request from '@/api/request';

// 路由
const router = useRouter();
const userStore = useUserStore();

// 数据
const loading = ref(true);
const posts = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const currentSort = ref('latest');

// 获取帖子数据
const fetchUserPosts = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  
  loading.value = true;
  try {
    // 确保用户信息已加载
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo();
    }
    
    const userId = userStore.userInfo?.id;
    if (!userId) {
      throw new Error('无法获取用户ID');
    }
    
    // 直接使用后端API路径
    const response = await request({
      url: `/posts/user/${userId}`,
      method: 'get',
      params: {
        page: currentPage.value,
        size: pageSize.value,
        sort: currentSort.value
      }
    });
    
    posts.value = response.data.list || [];
    total.value = response.data.total || 0;
  } catch (error) {
    console.error('获取用户帖子失败:', error);
    ElMessage.error('获取用户帖子列表失败');
  } finally {
    loading.value = false;
  }
};

// 编辑帖子
const editPost = (postId) => {
  router.push(`/edit-post/${postId}`);
};

// 删除帖子
const deletePost = async (postId) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？此操作不可逆。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await apiDeletePost(postId);
    ElMessage.success('删除成功');
    fetchUserPosts(); // 重新获取帖子列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error);
      ElMessage.error('删除帖子失败');
    }
  }
};

// 创建新帖子
const createNewPost = () => {
  router.push('/create-post');
};

// 分页相关
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchUserPosts();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchUserPosts();
};

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  
  const date = new Date(dateTimeStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 页面加载时获取数据
onMounted(() => {
  fetchUserPosts();
});
</script>

<style scoped>
.my-posts-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.my-posts-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-weight: 500;
}

.loading-container, .empty-container {
  padding: 40px 0;
  text-align: center;
}

.empty-container .el-button {
  margin-top: 16px;
}

.posts-list {
  margin-top: 16px;
}

.post-title {
  color: var(--el-color-primary);
  text-decoration: none;
  font-weight: 500;
}

.post-title:hover {
  text-decoration: underline;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
