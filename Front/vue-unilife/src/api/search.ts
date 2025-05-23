import { get } from './request'

// 搜索相关接口类型定义
export interface SearchParams {
  keyword: string
  type?: 'all' | 'post' | 'resource' | 'user'
  categoryId?: number
  sortBy?: 'time' | 'relevance' | 'popularity'
  page?: number
  size?: number
}

export interface SearchItem {
  id: number
  title: string
  summary: string
  type: 'post' | 'resource' | 'user'
  author: string
  avatar: string
  categoryName: string
  createdAt: string
  likeCount: number
  viewCount: number
  highlights?: string[]
}

export interface SearchResult {
  items: SearchItem[]
  total: number
  page: number
  size: number
  keyword: string
  searchTime: number
}

// 综合搜索
export const search = (params: SearchParams) => {
  return get<SearchResult>('/search', params)
}

// 搜索帖子
export const searchPosts = (params: SearchParams) => {
  return get<SearchResult>('/search/posts', params)
}

// 搜索资源
export const searchResources = (params: SearchParams) => {
  return get<SearchResult>('/search/resources', params)
}

// 搜索用户
export const searchUsers = (params: SearchParams) => {
  return get<SearchResult>('/search/users', params)
}

// 获取搜索建议
export const getSuggestions = (keyword: string) => {
  return get<string[]>('/search/suggestions', { keyword })
}

// 获取热门搜索词
export const getHotKeywords = () => {
  return get<string[]>('/search/hot-keywords')
}