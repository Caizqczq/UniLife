import api from './index'
import type { ApiResponse } from '@/types'

// 获取用户个人信息
export const getUserInfo = () => {
  return api.get<ApiResponse<{
    id: number
    username: string
    email: string
    nickname: string
    avatar: string
    bio: string
    gender: number
    studentId: string
    department: string
    major: string
    grade: string
    points: number
    role: number
    isVerified: number
  }>>('/users/info')
}

// 更新用户个人信息
export const updateUserProfile = (data: {
  username?: string
  bio?: string
  gender?: number
  department?: string
  major?: string
  grade?: string
}) => {
  return api.put<ApiResponse<null>>('/users/profile', data)
}

// 修改用户密码
export const changePassword = (data: {
  code: string
  newPassword: string
}) => {
  return api.put<ApiResponse<null>>('/users/password', data)
}

// 上传用户头像
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return api.post<ApiResponse<{
    avatarUrl: string
  }>>('/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新用户邮箱
export const updateEmail = (data: {
  email: string
  code: string
}) => {
  return api.put<ApiResponse<null>>('/users/email', data)
}

// 获取用户统计数据
export const getUserStats = () => {
  return api.get<ApiResponse<{
    postsCount: number
    commentsCount: number
    resourcesCount: number
    likesReceived: number
    coursesCount: number
    schedulesCount: number
  }>>('/users/stats')
}

// 获取用户最近帖子
export const getUserRecentPosts = (limit?: number) => {
  return api.get<ApiResponse<Array<{
    id: number
    title: string
    content: string
    categoryId: number
    createTime: string
    viewsCount: number
    likesCount: number
    commentsCount: number
  }>>>('/users/recent-posts', {
    params: { limit }
  })
}

// 获取邮箱验证码
export const sendEmailCode = (email: string) => {
  return api.post<ApiResponse<null>>('/users/code', { email })
} 