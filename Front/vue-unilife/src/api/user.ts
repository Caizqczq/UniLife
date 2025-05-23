import { get, post, put } from './request';

// 用户接口类型定义
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  nickname?: string;
  avatar?: string;
  bio?: string;
  gender?: number;
  birthday?: string;
  studentId?: string;
  department?: string;
  major?: string;
  grade?: string;
  points?: number;
  role?: number;
  isVerified?: number;
}

export interface LoginParams {
  email: string;
  password: string;
}

export interface RegisterParams {
  email: string;
  password: string;
  username?: string;
  studentId?: string;
  department?: string;
  major?: string;
  grade?: string;
  code: string;
}

export interface EmailCodeParams {
  email: string;
}

export interface VerifyCodeParams {
  email: string;
  code: string;
}

export interface UpdateProfileParams {
  username?: string;
  bio?: string;
  gender?: number;
  department?: string;
  major?: string;
  grade?: string;
}

export interface UpdatePasswordParams {
  code: string;
  newPassword: string;
}

export interface UserStats {
  totalPosts: number;
  totalLikes: number;
  totalComments: number;
  totalViews: number;
}

// 用户API
export default {
  // 登录
  login(data: LoginParams) {
    return post<{code: number; data: {token: string}}>(
      '/users/login', 
      data
    );
  },
  
  // 注册
  register(data: RegisterParams) {
    return post<{code: number; data: {token: string}}>(
      '/users/register',
      data
    );
  },
  
  // 获取邮箱验证码
  getEmailCode(data: EmailCodeParams) {
    return post<{code: number; message: string}>(
      '/users/code', 
      data
    );
  },
  
  // 验证邮箱验证码
  verifyEmailCode(data: VerifyCodeParams) {
    return post<{code: number; data: {token: string}}>(
      '/users/login/code', 
      data
    );
  },
  
  // 获取用户信息
  getUserInfo() {
    return get<{code: number; data: UserInfo}>(
      '/users/info'
    );
  },
  
  // 更新用户资料
  updateProfile(data: UpdateProfileParams) {
    return put<{code: number; message: string}>(
      '/users/profile', 
      data
    );
  },
  
  // 更新用户密码
  updatePassword(data: UpdatePasswordParams) {
    return put<{code: number; message: string}>(
      '/users/password', 
      data
    );
  },
  
  // 获取用户统计数据
  getUserStats() {
    return get<{code: number; data: UserStats}>(
      '/users/stats'
    );
  },

  // 获取用户最近帖子
  getUserRecentPosts(limit: number = 5) {
    return get<{code: number; data: {list: any[]}}>(
      `/users/recent-posts?limit=${limit}`
    );
  },
  
  // 上传头像
  uploadAvatar(formData: FormData) {
    return post<{code: number; data: {avatarUrl: string}}>(
      '/users/avatar', 
      formData
    );
  }
};
