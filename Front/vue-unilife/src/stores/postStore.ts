import { defineStore } from 'pinia';
import postApi from '@/api/post'; 
import type { PostItem, CategoryItem } from '@/api/post'; 
import { ElMessage } from 'element-plus';

export interface PostState {
  posts: PostItem[];
  currentPost: PostItem | null;
  loading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  totalPosts: number;
  totalPages: number;

  categories: CategoryItem[];
  loadingCategories: boolean;
  errorCategories: string | null;
  selectedCategoryId: number | null; 
}

export const usePostStore = defineStore('post', {
  state: (): PostState => ({
    posts: [],
    currentPost: null,
    loading: false,
    error: null,
    currentPage: 1,
    pageSize: 10, 
    totalPosts: 0,
    totalPages: 0,

    categories: [],
    loadingCategories: false,
    errorCategories: null,
    selectedCategoryId: null,
  }),
  actions: {
    async fetchPosts(params: { pageNum?: number; pageSize?: number } = {}) {
      this.loading = true;
      this.error = null;
      try {
        const pageNum = params.pageNum || this.currentPage;
        const pageSize = params.pageSize || this.pageSize;
        const categoryId = this.selectedCategoryId; // Use the stored selectedCategoryId

        const apiParams: { pageNum: number; pageSize: number; categoryId?: number } = { pageNum, pageSize };
        if (categoryId !== null) {
          apiParams.categoryId = categoryId;
        }

        const response = await postApi.getPosts(apiParams);

        if (response && response.data && Array.isArray(response.data.list)) {
          this.posts = response.data.list;
          this.totalPosts = response.data.total;
          this.totalPages = response.data.pages;
          this.currentPage = response.data.pageNum; // Ensure backend returns this
          this.pageSize = response.data.pageSize;   // Ensure backend returns this
        } else {
          console.error('Unexpected response structure for posts:', response);
          this.posts = [];
          this.totalPosts = 0;
          this.totalPages = 0;
          // Do not reset currentPage and pageSize here unless intended, 
          // as they might be needed for subsequent fetches if only list is malformed.
          throw new Error('帖子数据格式不正确');
        }
      } catch (error: any) {
        this.error = error.message || '获取帖子列表失败';
        // Potentially keep existing posts if fetch fails, or clear them:
        // this.posts = []; 
        // this.totalPosts = 0;
        // this.totalPages = 0;
      } finally {
        this.loading = false;
      }
    },

    async fetchPostDetail(id: number) {
      this.loading = true;
      this.error = null;
      this.currentPost = null;
      try {
        const response = await postApi.getPostDetail(id);
        // The API returns code 200 for success, and post data is directly in response.data
        if (response && response.code === 200 && response.data) {
          this.currentPost = response.data;
        } else {
          // Construct a more informative error or use a default
          const errorMessage = response?.data?.toString() ? `错误: ${response.data.toString()}` : '获取帖子详情失败';
          console.error('Failed to fetch post detail:', response);
          throw new Error(errorMessage);
        }
      } catch (error: any) {
        // 检查是否是未登录错误
        if (error.response && error.response.status === 401) {
          this.error = '您需要登录后才能查看帖子详情';
          ElMessage.warning('您需要登录后才能查看帖子详情');
          // 将用户重定向到登录页面，并记录需要返回的帖子详情页面
          const currentPath = `/post/${id}`;
          window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`;
          return;
        } else {
          // 处理其他错误
          const errorMsg = error.message || '加载帖子详情时发生未知错误';
          this.error = errorMsg;
          ElMessage.error(errorMsg);
        }
      } finally {
        this.loading = false;
      }
    },

    async fetchCategories() {
      this.loadingCategories = true;
      this.errorCategories = null;
      try {
        const response = await postApi.getCategories();
        // response.data is an object like { total: number, list: CategoryItem[] }
        // We need to access the 'list' property for the actual categories array.
        if (response && response.data && Array.isArray(response.data.list)) {
          this.categories = response.data.list;
        } else {
          // Handle cases where the structure is not as expected, though API seems to return it correctly.
          console.error('Unexpected response structure for categories:', response);
          this.categories = []; // Default to empty array to prevent further errors
          throw new Error('分类数据格式不正确');
        }
        this.loadingCategories = false;
      } catch (error: any) {
        this.errorCategories = error.message || '获取分类失败';
      } finally {
        this.loadingCategories = false;
      }
    },

    async selectCategory(categoryId: number | null) {
      if (this.selectedCategoryId !== categoryId) {
        this.selectedCategoryId = categoryId;
        this.currentPage = 1; 
        await this.fetchPosts(); 
      }
    },

    async likePost(postId: number) {
      try {
        const response = await postApi.likePost(postId);
        if (response && response.code === 200) {
          // 更新当前帖子的点赞状态
          if (this.currentPost && this.currentPost.id === postId) {
            if (this.currentPost.isLiked) {
              this.currentPost.likeCount -= 1;
              this.currentPost.isLiked = false;
              ElMessage.success('取消点赞成功');
            } else {
              this.currentPost.likeCount += 1;
              this.currentPost.isLiked = true;
              ElMessage.success('点赞成功');
            }
          }
          
          // 更新帖子列表中的对应帖子
          const postInList = this.posts.find(post => post.id === postId);
          if (postInList) {
            if (postInList.isLiked) {
              postInList.likeCount -= 1;
              postInList.isLiked = false;
            } else {
              postInList.likeCount += 1;
              postInList.isLiked = true;
            }
          }
        } else {
          throw new Error('操作失败');
        }
      } catch (error: any) {
        if (error.response && error.response.status === 401) {
          ElMessage.warning('请先登录');
          // 可以在这里处理登录跳转
        } else {
          ElMessage.error(error.message || '操作失败，请稍后重试');
        }
      }
    }
  }
});
