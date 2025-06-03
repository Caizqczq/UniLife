import request from '@/utils/request'
import type { ApiResponse } from '@/types'

export const adminApi = {
  // 获取系统统计数据
  getSystemStats(): Promise<ApiResponse> {
    return request.get('/admin/stats')
  },

  // 获取用户列表
  getUserList(params: {
    page?: number
    size?: number
    keyword?: string
    role?: number | null
    status?: number
  }): Promise<ApiResponse> {
    return request.get('/admin/users', { params })
  },

  // 更新用户状态
  updateUserStatus(userId: number, data: { status: number }): Promise<ApiResponse> {
    return request.put(`/admin/users/${userId}/status`, data)
  },

  // 更新用户角色
  updateUserRole(userId: number, data: { role: number }): Promise<ApiResponse> {
    return request.put(`/admin/users/${userId}/role`, data)
  },

  // 删除用户
  deleteUser(userId: number): Promise<ApiResponse> {
    return request.delete(`/admin/users/${userId}`)
  },

  // 获取帖子列表
  getPostList(params: {
    page?: number
    size?: number
    keyword?: string
    categoryId?: number
    status?: number
  }): Promise<ApiResponse> {
    return request.get('/admin/posts', { params })
  },

  // 更新帖子状态
  updatePostStatus(postId: number, data: { status: number }): Promise<ApiResponse> {
    return request.put(`/admin/posts/${postId}/status`, data)
  },

  // 删除帖子
  deletePost(postId: number): Promise<ApiResponse> {
    return request.delete(`/admin/posts/${postId}`)
  },

  // 永久删除帖子
  permanentDeletePost(postId: number): Promise<ApiResponse> {
    return request.delete(`/admin/posts/${postId}/permanent`)
  },

  // 获取评论列表
  getCommentList(params: {
    page?: number
    size?: number
    keyword?: string
    postId?: number
    status?: number
  }): Promise<ApiResponse> {
    return request.get('/admin/comments', { params })
  },

  // 删除评论
  deleteComment(commentId: number): Promise<ApiResponse> {
    return request.delete(`/admin/comments/${commentId}`)
  },

  // 获取分类列表
  getCategoryList(): Promise<ApiResponse> {
    return request.get('/admin/categories')
  },

  // 创建分类
  createCategory(data: {
    name: string
    description?: string
    icon?: string
    sort?: number
    status?: number
  }): Promise<ApiResponse> {
    return request.post('/admin/categories', data)
  },

  // 更新分类
  updateCategory(categoryId: number, data: {
    name: string
    description?: string
    icon?: string
    sort?: number
    status?: number
  }): Promise<ApiResponse> {
    return request.put(`/admin/categories/${categoryId}`, data)
  },

  // 删除分类
  deleteCategory(categoryId: number): Promise<ApiResponse> {
    return request.delete(`/admin/categories/${categoryId}`)
  },

  // 获取资源列表
  getResourceList(params: {
    page?: number
    size?: number
    keyword?: string
    categoryId?: number
    status?: number
  }): Promise<ApiResponse> {
    return request.get('/admin/resources', { params })
  },

  // 删除资源
  deleteResource(resourceId: number): Promise<ApiResponse> {
    return request.delete(`/admin/resources/${resourceId}`)
  },

    // 获取系统状态
  getSystemStatus(): Promise<ApiResponse> {
    return request.get('/admin/monitor/status')
  },

  // ========== 课表管理相关接口 ==========

  // 获取用户课表
  getUserSchedule(userId: number, semester?: string): Promise<ApiResponse> {
    const params = semester ? { semester } : {}
    return request.get(`/admin/users/${userId}/schedule`, { params })
  },

  // 删除课程
  deleteCourse(courseId: number): Promise<ApiResponse> {
    return request.delete(`/admin/courses/${courseId}`)
  }
} 