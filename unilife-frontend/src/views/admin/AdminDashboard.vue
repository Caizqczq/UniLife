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

        <!-- 帖子管理 -->
        <div v-if="activeMenu === 'posts'" class="posts-content">
          <h2>帖子管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="postSearch"
              placeholder="搜索帖子..."
              style="width: 300px"
              @input="searchPosts"
            />
            <el-select v-model="postCategoryFilter" placeholder="分类筛选" @change="searchPosts">
              <el-option label="全部" :value="null" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-select v-model="postStatusFilter" placeholder="状态筛选" @change="searchPosts">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="置顶" :value="2" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="posts" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
            <el-table-column prop="nickname" label="作者" width="120" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="viewCount" label="浏览" width="80" />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="commentCount" label="评论" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getPostStatusType(scope.row.status)">
                  {{ getPostStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="发布时间" width="180" />
            <el-table-column label="操作" width="250">
              <template #default="scope">
                <!-- 已删除的帖子显示恢复按钮 -->
                <template v-if="scope.row.status === 0">
                  <el-button
                    size="small"
                    type="success"
                    @click="restorePost(scope.row)"
                  >
                    恢复
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="permanentDeletePost(scope.row)"
                  >
                    永久删除
                  </el-button>
                </template>
                <!-- 正常和置顶的帖子显示置顶和删除按钮 -->
                <template v-else>
                  <el-button
                    size="small"
                    :type="scope.row.status === 2 ? 'warning' : 'primary'"
                    @click="togglePostTop(scope.row)"
                  >
                    {{ scope.row.status === 2 ? '取消置顶' : '置顶' }}
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="deletePost(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="postPage"
            v-model:page-size="postPageSize"
            :total="postTotal"
            @current-change="loadPosts"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 评论管理 -->
        <div v-if="activeMenu === 'comments'" class="comments-content">
          <h2>评论管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="commentSearch"
              placeholder="搜索评论..."
              style="width: 300px"
              @input="searchComments"
            />
            <el-select v-model="commentStatusFilter" placeholder="状态筛选" @change="searchComments">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="comments" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="content" label="内容" width="300" show-overflow-tooltip />
            <el-table-column prop="nickname" label="评论者" width="120" />
            <el-table-column prop="postTitle" label="所属帖子" width="200" show-overflow-tooltip />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="发布时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteComment(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="commentPage"
            v-model:page-size="commentPageSize"
            :total="commentTotal"
            @current-change="loadComments"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 分类管理 -->
        <div v-if="activeMenu === 'categories'" class="categories-content">
          <h2>分类管理</h2>
          <div class="table-toolbar">
            <el-button type="primary" @click="showCreateCategoryDialog">
              <el-icon><Plus /></el-icon>
              新增分类
            </el-button>
          </div>
          <el-table :data="categories" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="分类名称" width="150" />
            <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
            <el-table-column prop="icon" label="图标" width="80" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button
                  size="small"
                  type="primary"
                  @click="editCategory(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteCategory(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 资源管理 -->
        <div v-if="activeMenu === 'resources'" class="resources-content">
          <h2>资源管理</h2>
          <div class="table-toolbar">
            <el-input
              v-model="resourceSearch"
              placeholder="搜索资源..."
              style="width: 300px"
              @input="searchResources"
            />
            <el-select v-model="resourceCategoryFilter" placeholder="分类筛选" @change="searchResources">
              <el-option label="全部" :value="null" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-select v-model="resourceStatusFilter" placeholder="状态筛选" @change="searchResources">
              <el-option label="全部" :value="null" />
              <el-option label="正常" :value="1" />
              <el-option label="已删除" :value="0" />
            </el-select>
          </div>
          <el-table :data="resources" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
            <el-table-column prop="nickname" label="上传者" width="120" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="fileType" label="文件类型" width="120" />
            <el-table-column prop="fileSize" label="文件大小" width="100">
              <template #default="scope">
                {{ formatFileSize(scope.row.fileSize) }}
              </template>
            </el-table-column>
            <el-table-column prop="downloadCount" label="下载" width="80" />
            <el-table-column prop="likeCount" label="点赞" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '正常' : '已删除' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="上传时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteResource(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="resourcePage"
            v-model:page-size="resourcePageSize"
            :total="resourceTotal"
            @current-change="loadResources"
            layout="total, prev, pager, next"
          />
        </div>

        <!-- 分类编辑对话框 -->
        <el-dialog
          v-model="categoryDialogVisible"
          :title="isEditingCategory ? '编辑分类' : '新增分类'"
          width="500px"
        >
          <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="categoryForm.description" type="textarea" placeholder="请输入分类描述" />
            </el-form-item>
            <el-form-item label="图标" prop="icon">
              <el-input v-model="categoryForm.icon" placeholder="请输入图标（如：📚）" />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="categoryForm.sort" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="categoryForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="categoryDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="saveCategoryForm" :loading="isSavingCategory">
                {{ isEditingCategory ? '更新' : '创建' }}
              </el-button>
            </span>
          </template>
        </el-dialog>
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
  Files,
  Plus
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

// 定义分类接口
interface Category {
  id: number
  name: string
  description?: string
  icon?: string
  sort: number
  status: number
  createdAt?: string
  updatedAt?: string
}

// 定义分类表单接口
interface CategoryForm {
  id: number | null
  name: string
  description: string
  icon: string
  sort: number
  status: number
}

const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = ref('dashboard')

// 统计数据
const stats = ref<SystemStats>({})

// 用户管理相关
const users = ref<any[]>([])
const userSearch = ref('')
const userRoleFilter = ref(null)
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 帖子管理相关
const posts = ref<any[]>([])
const postSearch = ref('')
const postCategoryFilter = ref<number | null>(null)
const postStatusFilter = ref<number | null>(null)
const postPage = ref(1)
const postPageSize = ref(10)
const postTotal = ref(0)

// 评论管理相关
const comments = ref<any[]>([])
const commentSearch = ref('')
const commentStatusFilter = ref<number | null>(null)
const commentPage = ref(1)
const commentPageSize = ref(10)
const commentTotal = ref(0)

// 分类管理相关
const categories = ref<Category[]>([])
const categoryDialogVisible = ref(false)
const isEditingCategory = ref(false)
const categoryForm = ref<CategoryForm>({
  id: null,
  name: '',
  description: '',
  icon: '',
  sort: 1,
  status: 1
})
const categoryRules = ref({
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
})
const categoryFormRef = ref(null)
const isSavingCategory = ref(false)

// 资源管理相关
const resources = ref<any[]>([])
const resourceSearch = ref('')
const resourceCategoryFilter = ref<number | null>(null)
const resourceStatusFilter = ref<number | null>(null)
const resourcePage = ref(1)
const resourcePageSize = ref(10)
const resourceTotal = ref(0)

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  if (index === 'dashboard') {
    loadStats()
  } else if (index === 'users') {
    loadUsers()
  } else if (index === 'posts') {
    loadPosts()
  } else if (index === 'comments') {
    loadComments()
  } else if (index === 'categories') {
    loadCategories()
  } else if (index === 'resources') {
    loadResources()
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

// 加载帖子列表
const loadPosts = async () => {
  try {
    const response = await adminApi.getPostList({
      page: postPage.value,
      size: postPageSize.value,
      keyword: postSearch.value || undefined,
      categoryId: postCategoryFilter.value || undefined,
      status: postStatusFilter.value || undefined
    })
    if (response.code === 200) {
      posts.value = response.data.list
      postTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载帖子列表失败')
  }
}

// 加载评论列表
const loadComments = async () => {
  try {
    const response = await adminApi.getCommentList({
      page: commentPage.value,
      size: commentPageSize.value,
      keyword: commentSearch.value || undefined,
      status: commentStatusFilter.value || undefined
    })
    if (response.code === 200) {
      comments.value = response.data.list
      commentTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载评论列表失败')
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await adminApi.getCategoryList()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载分类列表失败')
  }
}

// 加载资源列表
const loadResources = async () => {
  try {
    const response = await adminApi.getResourceList({
      page: resourcePage.value,
      size: resourcePageSize.value,
      keyword: resourceSearch.value || undefined,
      categoryId: resourceCategoryFilter.value || undefined,
      status: resourceStatusFilter.value || undefined
    })
    if (response.code === 200) {
      resources.value = response.data.list
      resourceTotal.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载资源列表失败')
  }
}

// 搜索用户
const searchUsers = () => {
  userPage.value = 1
  loadUsers()
}

// 搜索帖子
const searchPosts = () => {
  postPage.value = 1
  loadPosts()
}

// 搜索评论
const searchComments = () => {
  commentPage.value = 1
  loadComments()
}

// 搜索资源
const searchResources = () => {
  resourcePage.value = 1
  loadResources()
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

// 切换帖子状态
const togglePostTop = async (post: any) => {
  try {
    const newStatus = post.status === 2 ? 1 : 2
    const response = await adminApi.updatePostStatus(post.id, { status: newStatus })
    if (response.code === 200) {
      post.status = newStatus
      ElMessage.success('帖子状态更新成功')
    }
  } catch (error) {
    ElMessage.error('更新帖子状态失败')
  }
}

// 删除帖子
const deletePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该帖子吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deletePost(post.id)
    if (response.code === 200) {
      ElMessage.success('帖子删除成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除帖子失败')
    }
  }
}

// 恢复帖子
const restorePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要恢复该帖子吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const response = await adminApi.updatePostStatus(post.id, { status: 1 })
    if (response.code === 200) {
      ElMessage.success('帖子恢复成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复帖子失败')
    }
  }
}

// 永久删除帖子
const permanentDeletePost = async (post: any) => {
  try {
    await ElMessageBox.confirm('确定要永久删除该帖子吗？此操作不可恢复！', '危险操作', {
      confirmButtonText: '永久删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger'
    })
    
    const response = await adminApi.permanentDeletePost(post.id)
    if (response.code === 200) {
      ElMessage.success('帖子永久删除成功')
      loadPosts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('永久删除帖子失败')
    }
  }
}

// 删除评论
const deleteComment = async (comment: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteComment(comment.id)
    if (response.code === 200) {
      ElMessage.success('评论删除成功')
      loadComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除评论失败')
    }
  }
}

// 显示创建分类对话框
const showCreateCategoryDialog = () => {
  isEditingCategory.value = false
  categoryForm.value = {
    id: null,
    name: '',
    description: '',
    icon: '',
    sort: 1,
    status: 1
  }
  categoryDialogVisible.value = true
}

// 编辑分类
const editCategory = (category: any) => {
  isEditingCategory.value = true
  categoryForm.value = {
    id: category.id,
    name: category.name,
    description: category.description,
    icon: category.icon,
    sort: category.sort,
    status: category.status
  }
  categoryDialogVisible.value = true
}

// 保存分类
const saveCategoryForm = async () => {
  try {
    isSavingCategory.value = true
    let response
    if (isEditingCategory.value && categoryForm.value.id) {
      response = await adminApi.updateCategory(categoryForm.value.id, categoryForm.value)
    } else {
      response = await adminApi.createCategory(categoryForm.value)
    }
    if (response.code === 200) {
      ElMessage.success('分类保存成功')
      categoryDialogVisible.value = false
      loadCategories()
    }
  } catch (error) {
    ElMessage.error('保存分类失败')
  } finally {
    isSavingCategory.value = false
  }
}

// 删除分类
const deleteCategory = async (category: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteCategory(category.id)
    if (response.code === 200) {
      ElMessage.success('分类删除成功')
      loadCategories()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除分类失败')
    }
  }
}

// 删除资源
const deleteResource = async (resource: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该资源吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await adminApi.deleteResource(resource.id)
    if (response.code === 200) {
      ElMessage.success('资源删除成功')
      loadResources()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除资源失败')
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

// 获取帖子状态类型
const getPostStatusType = (status: number) => {
  switch (status) {
    case 1: return ''
    case 2: return 'warning'
    case 0: return 'danger'
    default: return ''
  }
}

// 获取帖子状态文本
const getPostStatusText = (status: number) => {
  switch (status) {
    case 1: return '正常'
    case 2: return '置顶'
    case 0: return '已删除'
    default: return '未知'
  }
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
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