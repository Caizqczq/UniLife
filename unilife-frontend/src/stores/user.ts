import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, LoginRequest, RegisterRequest, ApiResponse, LoginResponse } from '@/types'
import { login, register, getUserInfo } from '@/api/auth'
import { isTokenExpired } from '@/utils/jwt'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')
  const isLoggedIn = ref<boolean>(!!token.value && !isTokenExpired(token.value))

  // 登录
  const userLogin = async (loginData: LoginRequest) => {
    try {
      const response = await login(loginData) as any as ApiResponse<LoginResponse>
      if (response.code === 200) {
        token.value = response.data.token
        localStorage.setItem('token', token.value)
        isLoggedIn.value = true
        
        // 获取用户详细信息
        await fetchUserInfo()
        return response
      }
      throw new Error(response.message)
    } catch (error) {
      throw error
    }
  }

  // 注册
  const userRegister = async (registerData: RegisterRequest) => {
    try {
      const response = await register(registerData)
      return response
    } catch (error) {
      throw error
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await getUserInfo() as any as ApiResponse<User>
      if (response.code === 200) {
        user.value = response.data
      }
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，可能是token过期，清除登录状态
      if (error?.response?.status === 401) {
        logout()
      }
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = ''
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  // 检查token是否有效
  const checkTokenValidity = () => {
    if (token.value && isTokenExpired(token.value)) {
      console.log('Token已过期，自动登出')
      logout()
      return false
    }
    return !!token.value
  }

  // 初始化时检查token并获取用户信息
  const init = async () => {
    if (checkTokenValidity() && !user.value) {
      await fetchUserInfo()
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    userLogin,
    userRegister,
    fetchUserInfo,
    checkTokenValidity,
    logout,
    init
  }
}) 