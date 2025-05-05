import axios from 'axios';
import type { AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8087',
  timeout: 10000
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');

    // 如果存在token，则添加到请求头
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log("请求已附加token");
    }

    return config;
  },
  (error) => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    // 如果接口返回的状态码不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      });

      // 处理特定错误码
      if (res.code === 401) {
        // 未授权，清除token并重定向到登录页
        localStorage.removeItem('token');
        window.location.href = '/login';
      }

      return Promise.reject(new Error(res.message || '请求失败'));
    } else {
      return res;
    }
  },
  (error) => {
    console.error('响应错误:', error);

    // 显示错误信息
    ElMessage({
      message: error.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    });

    return Promise.reject(error);
  }
);

// 封装GET请求
export function get<T>(url: string, params?: any): Promise<T> {
  return service.get(url, { params });
}

// 封装POST请求
export function post<T>(url: string, data?: any): Promise<T> {
  return service.post(url, data);
}

// 封装PUT请求
export function put<T>(url: string, data?: any): Promise<T> {
  return service.put(url, data);
}

// 封装DELETE请求
export function del<T>(url: string, params?: any): Promise<T> {
  return service.delete(url, { params });
}

export default service;
