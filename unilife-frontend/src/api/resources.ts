import api from './index'
import type { ApiResponse } from '@/types'

// 资源类型定义
export interface Resource {
  id: number
  title: string
  description: string
  fileUrl: string
  fileSize: number
  fileType: string
  userId: number
  nickname: string
  avatar: string
  categoryId: number
  categoryName: string
  downloadCount: number
  likeCount: number
  isLiked: boolean
  createdAt: string
  updatedAt: string
}

// 上传资源
export const uploadResource = (data: {
  file: File
  title: string
  description: string
  categoryId: number
}) => {
  const formData = new FormData()
  formData.append('file', data.file)
  formData.append('title', data.title)
  formData.append('description', data.description)
  formData.append('categoryId', data.categoryId.toString())
  
  return api.post<ApiResponse<{
    resourceId: number
  }>>('/resources', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取资源详情
export const getResourceDetail = (id: number) => {
  return api.get<ApiResponse<Resource>>(`/resources/${id}`)
}

// 获取资源列表
export const getResources = (params: {
  category?: number
  user?: number
  keyword?: string
  page?: number
  size?: number
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Resource[]
    pages: number
  }>>('/resources', { params })
}

// 更新资源
export const updateResource = (id: number, data: {
  title: string
  description: string
  categoryId: number
}) => {
  return api.put<ApiResponse<null>>(`/resources/${id}`, data)
}

// 删除资源
export const deleteResource = (id: number) => {
  return api.delete<ApiResponse<null>>(`/resources/${id}`)
}

// 下载资源
export const downloadResource = (id: number) => {
  return api.get<ApiResponse<{
    fileUrl: string
    fileName: string
    fileType: string
  }>>(`/resources/${id}/download`)
}

// 点赞/取消点赞资源
export const likeResource = (id: number) => {
  return api.post<ApiResponse<null>>(`/resources/${id}/like`)
}

// 获取用户上传的资源列表
export const getUserResources = (userId: number, params?: {
  page?: number
  size?: number
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Resource[]
    pages: number
  }>>(`/resources/user/${userId}`, { params })
}

// 获取当前用户上传的资源列表
export const getMyResources = (params?: {
  page?: number
  size?: number
}) => {
  return api.get<ApiResponse<{
    total: number
    list: Resource[]
    pages: number
  }>>('/resources/my', { params })
} 