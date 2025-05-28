import api from './index'
import type { LoginRequest, RegisterRequest, LoginResponse, User, ApiResponse } from '@/types'

// 用户注册
export const register = (data: RegisterRequest) => {
  return api.post<ApiResponse<{ userId: number; username: string; nickname: string }>>('/users/register', data)
}

// 用户登录
export const login = (data: LoginRequest) => {
  return api.post<ApiResponse<LoginResponse>>('/users/login', data)
}

// 获取邮箱验证码
export const getEmailCode = (email: string) => {
  return api.post<ApiResponse>('/users/code', { email })
}

// 邮箱验证码登录
export const loginWithCode = (email: string, code: string) => {
  return api.post<ApiResponse<LoginResponse>>('/users/login/code', { email, code })
}

// 获取用户信息
export const getUserInfo = () => {
  return api.get<ApiResponse<User>>('/users/info')
}

// 更新用户信息
export const updateProfile = (data: Partial<User>) => {
  return api.put<ApiResponse>('/users/profile', data)
}

// 修改密码
export const updatePassword = (code: string, newPassword: string) => {
  return api.put<ApiResponse>('/users/password', { code, newPassword })
}

// 上传头像
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return api.post<ApiResponse<{ avatarUrl: string }>>('/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新邮箱
export const updateEmail = (email: string, code: string) => {
  return api.put<ApiResponse>('/users/email', { email, code })
} 