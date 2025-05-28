import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, LoginRequest, RegisterRequest, ApiResponse, LoginResponse } from '@/types'
import { login, register, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')
  const isLoggedIn = ref<boolean>(!!token.value)

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
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = ''
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  // 初始化时如果有token则获取用户信息
  const init = async () => {
    if (token.value && !user.value) {
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
    logout,
    init
  }
}) 