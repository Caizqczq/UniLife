import { get, post as httpPost, del } from './request';

// 评论类型定义
export interface CommentItem {
  id: number;
  postId: number;
  userId: number;
  nickname: string;
  avatar: string;
  content: string;
  parentId?: number;
  likeCount: number;
  isLiked: boolean;
  createdAt: string;
  replies: CommentItem[];
}

export interface CreateCommentParams {
  postId: number;
  content: string;
  parentId?: number;
}

// 评论API方法
export default {
  // 获取帖子评论列表
  getCommentsByPostId(postId: number) {
    return get<{ 
      code: number; 
      data: { 
        total: number; 
        list: CommentItem[] 
      } 
    }>(`/comments/post/${postId}`);
  },
  
  // 创建评论
  createComment(data: CreateCommentParams) {
    return httpPost<{ 
      code: number; 
      message: string; 
      data: { commentId: number } 
    }>('/comments', data);
  },
  
  // 删除评论
  deleteComment(id: number) {
    return del<{ 
      code: number; 
      message: string 
    }>(`/comments/${id}`);
  },
  
  // 点赞/取消点赞评论
  likeComment(id: number) {
    return httpPost<{ 
      code: number; 
      message: string 
    }>(`/comments/${id}/like`);
  }
}; 