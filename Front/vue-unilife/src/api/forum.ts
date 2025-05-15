import request from './request';

/**
 * 获取帖子列表
 * @param page 页码
 * @param size 每页数量
 * @param categoryId 分类ID，可选
 * @param sort 排序方式（latest, hot, likes, comments），默认latest
 * @param userId 用户ID，可选，获取指定用户的帖子
 */
export function getPosts(page = 1, size = 10, categoryId?: number | null, sort = 'latest', userId?: number | null) {
  if (userId) {
    // 获取指定用户的帖子
    return request({
      url: `/posts/user/${userId}`,
      method: 'get',
      params: { page, size, sort }
    });
  } else {
    // 获取所有帖子或者按分类筛选
    return request({
      url: '/posts',
      method: 'get',
      params: { page, size, categoryId, sort }
    });
  }
}

/**
 * 获取帖子详情
 * @param id 帖子ID
 */
export function getPostDetail(id: number) {
  return request({
    url: `/posts/${id}`,
    method: 'get'
  });
}

/**
 * 创建帖子
 * @param data 帖子数据
 */
export function createPost(data: any) {
  return request({
    url: '/posts',
    method: 'post',
    data
  });
}

/**
 * 更新帖子
 * @param id 帖子ID
 * @param data 更新数据
 */
export function updatePost(id: number, data: any) {
  return request({
    url: `/posts/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除帖子
 * @param id 帖子ID
 */
export function deletePost(id: number) {
  return request({
    url: `/posts/${id}`,
    method: 'delete'
  });
}

/**
 * 点赞或取消点赞帖子
 * @param id 帖子ID
 */
export function likePost(id: number) {
  return request({
    url: `/posts/${id}/like`,
    method: 'post'
  });
}

/**
 * 获取所有分类
 */
export function getCategories() {
  return request({
    url: '/categories',
    method: 'get'
  });
}
