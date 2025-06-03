import axios from 'axios'
import { isTokenExpired } from './jwt'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8087',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      // 检查token是否过期
      if (isTokenExpired(token)) {
        console.warn('Token已过期，清除本地存储并跳转登录页')
        localStorage.removeItem('token')
        // 清除用户store状态
        import('@/stores/user').then(({ useUserStore }) => {
          const userStore = useUserStore()
          userStore.logout()
        })
        // 如果不是登录页面，则跳转到登录页
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
        return Promise.reject(new Error('Token已过期'))
      }
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request 