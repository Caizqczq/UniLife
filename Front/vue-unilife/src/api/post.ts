import { get, post as httpPost, put, del } from './request';

// 类型定义
export interface PostItem {
  id: number;
  title: string;
  summary?: string; // Added: for post list item summary
  content: string;   // Existing: for post detail content
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
  isLiked?: boolean; // Added: for post detail, if current user liked it
}

export interface CategoryItem {
  id: number;
  name: string;
  description?: string;
}

export interface CreatePostParams {
  title: string;
  content: string;
  categoryId: number;
}

// API方法
export default {
  // 获取帖子列表
  getPosts(params: { pageNum?: number; pageSize?: number; categoryId?: number; sort?: string }) {
    // 将前端参数名转换为后端参数名
    const serverParams: any = {
      page: params.pageNum,
      size: params.pageSize,
      sort: params.sort
    };
    
    // 保留categoryId参数
    if (params.categoryId !== undefined) {
      serverParams.categoryId = params.categoryId;
    }
    
    return get<{ code: number; data: { total: number; list: PostItem[]; pages: number; pageNum: number; pageSize: number } }>('/posts', serverParams);
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
  },
  
  // 获取所有帖子分类
  getCategories() {
    return get<{ code: number; message: string; data: { list: CategoryItem[], total: number } }>('/categories');
  }
};