import api from './index'
import type { ApiResponse, Post, Category } from '@/types'

// 获取帖子列表
export const getPosts = (params: {
  categoryId?: number | null
  keyword?: string
  page?: number
  size?: number
  sort?: string
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Post[]
    pages: number
  }>>('/posts', { params })
}

// 获取帖子详情
export const getPostDetail = (id: number) => {
  return api.get<ApiResponse<Post>>(`/posts/${id}`)
}

// 发布帖子
export const createPost = (data: {
  title: string
  content: string
  categoryId: number
}) => {
  return api.post<ApiResponse<{ postId: number }>>('/posts', data)
}

// 更新帖子
export const updatePost = (id: number, data: {
  title: string
  content: string
  categoryId: number
}) => {
  return api.put<ApiResponse<null>>(`/posts/${id}`, data)
}

// 删除帖子
export const deletePost = (id: number) => {
  return api.delete<ApiResponse<null>>(`/posts/${id}`)
}

// 点赞/取消点赞帖子
export const likePost = (id: number) => {
  return api.post<ApiResponse<null>>(`/posts/${id}/like`)
}

// 获取用户的帖子列表
export const getUserPosts = (userId: number, params?: {
  page?: number
  size?: number
  sort?: string
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Post[]
    pages: number
  }>>(`/posts/user/${userId}`, { params })
}

// ==================== 评论管理 ====================

// 获取评论列表
export const getComments = (postId: number) => {
  return api.get<ApiResponse<{
    total: number
    list: Array<{
      id: number
      content: string
      userId: number
      nickname: string
      avatar: string
      likeCount: number
      isLiked: boolean
      createdAt: string
      replies?: Array<{
        id: number
        content: string
        userId: number
        nickname: string
        avatar: string
        likeCount: number
        isLiked: boolean
        createdAt: string
      }>
    }>
  }>>(`/comments/post/${postId}`)
}

// 发表评论
export const createComment = (data: {
  postId: number
  content: string
  parentId?: number | null
}) => {
  return api.post<ApiResponse<{ commentId: number }>>('/comments', data)
}

// 删除评论
export const deleteComment = (id: number) => {
  return api.delete<ApiResponse<null>>(`/comments/${id}`)
}

// 点赞/取消点赞评论
export const likeComment = (id: number) => {
  return api.post<ApiResponse<null>>(`/comments/${id}/like`)
}

// 获取分类列表
export const getCategories = (params?: { status?: number }) => {
  return api.get<ApiResponse<{
    total: number
    list: Category[]
  }>>('/categories', { params })
}

// 获取分类详情
export const getCategoryDetail = (id: number) => {
  return api.get<ApiResponse<Category>>(`/categories/${id}`)
}

// 创建分类（管理员权限）
export const createCategory = (data: {
  name: string
  description: string
  icon: string
  sort: number
  status: number
}) => {
  return api.post<ApiResponse<{ categoryId: number }>>('/categories', data)
}

// 更新分类（管理员权限）
export const updateCategory = (id: number, data: {
  name: string
  description: string
  icon: string
  sort: number
  status: number
}) => {
  return api.put<ApiResponse<null>>(`/categories/${id}`, data)
}

// 删除分类（管理员权限）
export const deleteCategory = (id: number) => {
  return api.delete<ApiResponse<null>>(`/categories/${id}`)
} 