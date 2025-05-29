// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface PaginatedResponse<T> {
  total: number
  list: T[]
  pages: number
}

// 用户相关类型
export interface User {
  id: number
  username: string
  email: string
  nickname: string
  avatar: string
  bio?: string
  gender?: number
  studentId: string
  department: string
  major: string
  grade: string
  points: number
  role: number
  isVerified: number
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  nickname: string
  studentId: string
  department: string
  major: string
  grade: string
  code: string
}

export interface LoginResponse {
  token: string
  id: number
  username: string
  nickname: string
  avatar: string
  role: number
}

// 帖子相关类型
export interface Post {
  id: number
  title: string
  content?: string
  summary?: string
  userId: number
  nickname: string
  avatar: string
  categoryId: number
  categoryName: string
  viewCount: number
  likeCount: number
  commentCount: number
  isLiked?: boolean
  createdAt: string
  updatedAt?: string
}

export interface CreatePostRequest {
  title: string
  content: string
  categoryId: number
}

// 评论相关类型
export interface Comment {
  id: number
  content: string
  userId: number
  nickname: string
  avatar: string
  likeCount: number
  isLiked: boolean
  createdAt: string
  replies?: Comment[]
}

export interface CreateCommentRequest {
  postId: number
  content: string
  parentId?: number
}

// 分类相关类型
export interface Category {
  id: number
  name: string
  description: string
  icon: string
  sort: number
  status: number
  createdAt: string
  updatedAt: string
  postCount?: number       // 该分类下的帖子数量
  resourceCount?: number   // 该分类下的资源数量
}

// 资源相关类型
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

// 课程相关类型
export interface Course {
  id: number
  userId: number
  name: string
  teacher: string
  location: string
  dayOfWeek: number
  startTime: string
  endTime: string
  startWeek: number
  endWeek: number
  semester?: string
  color: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface CreateCourseRequest {
  name: string
  teacher: string
  location: string
  dayOfWeek: number
  startTime: string
  endTime: string
  startWeek: number
  endWeek: number
  semester?: string
  color: string
}

// 日程相关类型
export interface Schedule {
  id: number
  userId: number
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  isAllDay: number
  reminder?: number
  color: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface CreateScheduleRequest {
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  isAllDay: number
  reminder?: number
  color: string
} 