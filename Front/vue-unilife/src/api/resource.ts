import { get, post, put, del } from './request';

// 资源类型定义
export interface ResourceItem {
  id: number;
  title: string;
  description?: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
  userId: number;
  nickname: string;
  avatar?: string;
  categoryId: number;
  categoryName: string;
  downloadCount: number;
  likeCount: number;
  isLiked?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ResourceCategory {
  id: number;
  name: string;
  description?: string;
  icon?: string;
}

export interface ResourceParams {
  page?: number;
  size?: number;
  categoryId?: number;
  keyword?: string;
  userId?: number;
}

export interface UploadResourceParams {
  title: string;
  description?: string;
  categoryId: number;
  file: File;
}

export interface UpdateResourceParams {
  title?: string;
  description?: string;
  categoryId?: number;
}

// 资源API
export default {
  // 获取资源列表
  getResources(params: ResourceParams = {}) {
    return get<{ code: number; data: { total: number; list: ResourceItem[]; pages: number } }>('/resources', params);
  },

  // 获取资源详情
  getResourceDetail(id: number) {
    return get<{ code: number; data: ResourceItem }>(`/resources/${id}`);
  },

  // 上传资源
  uploadResource(data: FormData) {
    return post<{ code: number; data: { resourceId: number } }>('/resources', data);
  },

  // 更新资源信息
  updateResource(id: number, data: UpdateResourceParams) {
    return put<{ code: number; message: string }>(`/resources/${id}`, data);
  },

  // 删除资源
  deleteResource(id: number) {
    return del<{ code: number; message: string }>(`/resources/${id}`);
  },

  // 下载资源
  downloadResource(id: number) {
    return get<{ code: number; data: { fileUrl: string; fileName: string; fileType: string } }>(`/resources/${id}/download`);
  },

  // 点赞/取消点赞资源
  likeResource(id: number) {
    return post<{ code: number; message: string }>(`/resources/${id}/like`);
  },

  // 获取用户上传的资源列表
  getUserResources(userId: number) {
    return get<{ code: number; data: { total: number; list: ResourceItem[]; pages: number } }>(`/resources/user/${userId}`);
  },

  // 获取当前用户上传的资源列表
  getMyResources(params: { page?: number; size?: number } = {}) {
    return get<{ code: number; data: { total: number; list: ResourceItem[]; pages: number } }>('/resources/my', params);
  },

  // 获取资源分类
  getResourceCategories() {
    return get<{ code: number; data: { list: ResourceCategory[]; total: number } }>('/categories');
  }
};
