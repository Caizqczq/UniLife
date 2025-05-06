import { get, post as httpPost, put, del } from './request';

// 类型定义
export interface PostItem {
  id: number;
  title: string;
  content: string;
  userId: number;
  nickname: string;
  avatar: string;
  categoryId: number;
  categoryName: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePostParams {
  title: string;
  content: string;
  categoryId: number;
}

// API方法
export default {
  // 获取帖子列表
  getPosts(params: { page?: number; size?: number; category?: number; sort?: string }) {
    return get<{ code: number; data: { total: number; list: PostItem[]; pages: number } }>('/posts', params);
  },
  
  // 获取帖子详情
  getPostDetail(id: number) {
    return get<{ code: number; data: PostItem }>(`/posts/${id}`);
  },
  
  // 创建帖子
  createPost(data: CreatePostParams) {
    return httpPost<{ code: number; data: { postId: number } }>('/posts', data);
  },
  
  // 更新帖子
  updatePost(id: number, data: CreatePostParams) {
    return put<{ code: number; message: string }>(`/posts/${id}`, data);
  },
  
  // 删除帖子
  deletePost(id: number) {
    return del<{ code: number; message: string }>(`/posts/${id}`);
  },
  
  // 点赞/取消点赞帖子
  likePost(id: number) {
    return httpPost<{ code: number; message: string }>(`/posts/${id}/like`);
  },
  
  // 获取用户的帖子列表
  getUserPosts(userId?: number) {
    const params = userId ? { userId } : {};
    return get<{ code: number; data: { total: number; list: PostItem[]; pages: number } }>('/posts', params);
  }
};