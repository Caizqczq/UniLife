<template>
  <div class="admin-dashboard">
    <!-- 顶部导航 -->
    <div class="admin-header">
      <div class="header-left">
        <h1 class="admin-title">UniLife 管理后台</h1>
      </div>
      <div class="header-right">
        <span class="admin-user">{{ userStore.user?.nickname }}</span>
        <el-button @click="logout" type="danger" size="small">退出登录</el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="admin-content">
      <!-- 侧边栏 -->
      <div class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="posts">
            <el-icon><Document /></el-icon>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="comments">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论管理</span>
          </el-menu-item>
          <el-menu-item index="categories">
            <el-icon><FolderOpened /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="resources">
            <el-icon><Files /></el-icon>
            <span>资源管理</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区 -->
      <div class="admin-main">
        <!-- 数据概览 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <h2>系统数据概览</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalUsers || 0 }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon post-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalPosts || 0 }}</div>
                <div class="stat-label">总帖子数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon comment-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalComments || 0 }}</div>
                <div class="stat-label">总评论数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon resource-icon">
                <el-icon><Files /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalResources || 0 }}</div>
                <div class="stat-label">总资源数</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users-content">
          <h2>用户管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="userSearch"
              placeholder="搜索用户..."
              style="width: 300px"
              @input="searchUsers"
            />
            <el-select v-model="userRoleFilter" placeholder="角色筛选" @change="searchUsers">
              <el-option label="全部" :value="null" />
              <el-option label="普通用户" :value="0" />
              <el-option label="版主" :value="1" />
              <el-option label="管理员" :value="2" />
            </el-select>
          </div>
          <el-table :data="users" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="role" label="角色" width="100">
              <template #default="scope">
                <el-tag :type="getRoleType(scope.row.role)">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="注册时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button
                  size="small"
                  :type="scope.row.status === 1 ? 'warning' : 'success'"
                  @click="toggleUserStatus(scope.row)"
                >
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteUser(scope.row)"
                  :disabled="scope.row.role === 2"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="userPage"
            v-model:page-size="userPageSize"
            :total="userTotal"
            @current-change="loadUsers"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 其他管理模块的占位符 -->
        <div v-if="activeMenu === 'posts'" class="posts-content">
          <h2>帖子管理</h2>
          <p>帖子管理功能开发中...</p>
        </div>

        <div v-if="activeMenu === 'comments'" class="comments-content">
          <h2>评论管理</h2>
          <p>评论管理功能开发中...</p>
        </div>

        <div v-if="activeMenu === 'categories'" class="categories-content">
          <h2>分类管理</h2>
          <p>分类管理功能开发中...</p>
        </div>

        <div v-if="activeMenu === 'resources'" class="resources-content">
          <h2>资源管理</h2>
          <p>资源管理功能开发中...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataBoard,
  User,
  Document,
  ChatDotRound,
  FolderOpened,
  Files
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { adminApi } from '@/api/admin'

// 定义统计数据接口
interface SystemStats {
  totalUsers?: number
  totalPosts?: number
  totalComments?: number
  totalResources?: number
}

const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = ref('dashboard')

// 统计数据
const stats = ref<SystemStats>({})

// 用户管理相关
const users = ref([])
const userSearch = ref('')
const userRoleFilter = ref(null)
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  if (index === 'dashboard') {
    loadStats()
  } else if (index === 'users') {
    loadUsers()
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await adminApi.getSystemStats()
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const response = await adminApi.getUserList({
      page: userPage.value,
      size: userPageSize.value,
      keyword: userSearch.value || undefined,
      role: userRoleFilter.value
    })
    if (response.code === 200) {
      users.value = response.data.list
      userTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 搜索用户
const searchUsers = () => {
  userPage.value = 1
  loadUsers()
}

// 切换用户状态
const toggleUserStatus = async (user: any) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const response = await adminApi.updateUserStatus(user.id, { status: newStatus })
    if (response.code === 200) {
      user.status = newStatus
      ElMessage.success('用户状态更新成功')
    }
  } catch (error) {
    ElMessage.error('更新用户状态失败')
  }
}

// 删除用户
const deleteUser = async (user: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteUser(user.id)
    if (response.code === 200) {
      ElMessage.success('用户删除成功')
      loadUsers()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除用户失败')
    }
  }
}

// 获取角色类型
const getRoleType = (role: number) => {
  switch (role) {
    case 0: return ''
    case 1: return 'warning'
    case 2: return 'danger'
    default: return ''
  }
}

// 获取角色文本
const getRoleText = (role: number) => {
  switch (role) {
    case 0: return '普通用户'
    case 1: return '版主'
    case 2: return '管理员'
    default: return '未知'
  }
}

// 退出登录
const logout = () => {
  userStore.logout()
  router.push('/login')
}

// 页面加载时初始化
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-header {
  background: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.admin-title {
  margin: 0;
  color: #409EFF;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.admin-user {
  color: #666;
}

.admin-content {
  display: flex;
  height: calc(100vh - 60px);
}

.admin-sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e6e6e6;
}

.admin-menu {
  border: none;
  height: 100%;
}

.admin-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.user-icon { background: #409EFF; }
.post-icon { background: #67C23A; }
.comment-icon { background: #E6A23C; }
.resource-icon { background: #F56C6C; }

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.table-toolbar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.users-content,
.posts-content,
.comments-content,
.categories-content,
.resources-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
</style> 