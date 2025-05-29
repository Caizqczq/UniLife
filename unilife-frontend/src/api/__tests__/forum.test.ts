import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import * as forumApi from '../forum'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

describe('Forum API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getPosts', () => {
    it('should fetch posts successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: {
            total: 10,
            list: [
              {
                id: 1,
                title: '测试帖子',
                content: '测试内容',
                categoryId: 1,
                userId: 1,
                likeCount: 5,
                viewCount: 100,
                commentCount: 3,
                createdAt: '2024-01-01T10:00:00'
              }
            ],
            pages: 1
          }
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await forumApi.getPosts({
        categoryId: 1,
        keyword: '测试',
        page: 1,
        size: 10,
        sort: 'latest'
      })

      expect(mockedAxios.get).toHaveBeenCalledWith('/posts', {
        params: {
          categoryId: 1,
          keyword: '测试',
          page: 1,
          size: 10,
          sort: 'latest'
        }
      })
      expect(result.data.data.list).toHaveLength(1)
      expect(result.data.data.list[0].title).toBe('测试帖子')
    })

    it('should handle empty parameters', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { total: 0, list: [], pages: 0 }
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      await forumApi.getPosts({})

      expect(mockedAxios.get).toHaveBeenCalledWith('/posts', {
        params: {}
      })
    })
  })

  describe('getPostDetail', () => {
    it('should fetch post detail successfully', async () => {
      const mockPost = {
        id: 1,
        title: '测试帖子详情',
        content: '详细内容',
        categoryId: 1,
        userId: 1,
        author: {
          id: 1,
          nickname: '作者',
          avatar: 'avatar.jpg'
        },
        category: {
          id: 1,
          name: '学习讨论'
        },
        likeCount: 10,
        viewCount: 200,
        commentCount: 5,
        isLiked: false,
        createdAt: '2024-01-01T10:00:00'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: mockPost
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await forumApi.getPostDetail(1)

      expect(mockedAxios.get).toHaveBeenCalledWith('/posts/1')
      expect(result.data.data.title).toBe('测试帖子详情')
      expect(result.data.data.author.nickname).toBe('作者')
    })

    it('should handle post not found', async () => {
      const mockResponse = {
        data: {
          code: 404,
          success: false,
          message: '帖子不存在'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await forumApi.getPostDetail(999)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('帖子不存在')
    })
  })

  describe('createPost', () => {
    it('should create post successfully', async () => {
      const postData = {
        title: '新帖子',
        content: '新帖子内容',
        categoryId: 1
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { postId: 123 },
          message: '帖子发布成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await forumApi.createPost(postData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/posts', postData)
      expect(result.data.data.postId).toBe(123)
      expect(result.data.message).toBe('帖子发布成功')
    })

    it('should handle validation errors', async () => {
      const invalidPostData = {
        title: '',
        content: '内容',
        categoryId: 1
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '标题不能为空'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await forumApi.createPost(invalidPostData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('标题不能为空')
    })
  })

  describe('updatePost', () => {
    it('should update post successfully', async () => {
      const updateData = {
        title: '更新后的标题',
        content: '更新后的内容',
        categoryId: 2
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '帖子更新成功'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await forumApi.updatePost(1, updateData)

      expect(mockedAxios.put).toHaveBeenCalledWith('/posts/1', updateData)
      expect(result.data.message).toBe('帖子更新成功')
    })
  })

  describe('deletePost', () => {
    it('should delete post successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '帖子删除成功'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await forumApi.deletePost(1)

      expect(mockedAxios.delete).toHaveBeenCalledWith('/posts/1')
      expect(result.data.message).toBe('帖子删除成功')
    })
  })

  describe('likePost', () => {
    it('should like post successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '点赞成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await forumApi.likePost(1)

      expect(mockedAxios.post).toHaveBeenCalledWith('/posts/1/like')
      expect(result.data.message).toBe('点赞成功')
    })
  })

  describe('getComments', () => {
    it('should fetch comments successfully', async () => {
      const mockComments = {
        total: 2,
        list: [
          {
            id: 1,
            content: '评论内容1',
            userId: 1,
            nickname: '用户1',
            avatar: 'avatar1.jpg',
            likeCount: 3,
            isLiked: false,
            createdAt: '2024-01-01T10:00:00',
            replies: []
          },
          {
            id: 2,
            content: '评论内容2',
            userId: 2,
            nickname: '用户2',
            avatar: 'avatar2.jpg',
            likeCount: 1,
            isLiked: true,
            createdAt: '2024-01-01T11:00:00',
            replies: [
              {
                id: 3,
                content: '回复内容',
                userId: 1,
                nickname: '用户1',
                avatar: 'avatar1.jpg',
                likeCount: 0,
                isLiked: false,
                createdAt: '2024-01-01T12:00:00'
              }
            ]
          }
        ]
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: mockComments
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await forumApi.getComments(1)

      expect(mockedAxios.get).toHaveBeenCalledWith('/comments/post/1')
      expect(result.data.data.list).toHaveLength(2)
      expect(result.data.data.list[1].replies).toHaveLength(1)
    })
  })

  describe('createComment', () => {
    it('should create comment successfully', async () => {
      const commentData = {
        postId: 1,
        content: '新评论内容',
        parentId: null
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { commentId: 456 },
          message: '评论发布成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await forumApi.createComment(commentData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/comments', commentData)
      expect(result.data.data.commentId).toBe(456)
    })

    it('should create reply successfully', async () => {
      const replyData = {
        postId: 1,
        content: '回复内容',
        parentId: 123
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { commentId: 789 },
          message: '回复发布成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await forumApi.createComment(replyData)

      expect(mockedAxios.post).toHaveBeenCalledWith('/comments', replyData)
      expect(result.data.data.commentId).toBe(789)
    })
  })

  describe('getCategories', () => {
    it('should fetch categories successfully', async () => {
      const mockCategories = {
        total: 3,
        list: [
          { id: 1, name: '学习讨论', description: '学习相关话题', icon: 'book', sort: 1, status: 1 },
          { id: 2, name: '生活分享', description: '生活相关话题', icon: 'heart', sort: 2, status: 1 },
          { id: 3, name: '技术交流', description: '技术相关话题', icon: 'code', sort: 3, status: 1 }
        ]
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: mockCategories
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await forumApi.getCategories({ status: 1 })

      expect(mockedAxios.get).toHaveBeenCalledWith('/categories', {
        params: { status: 1 }
      })
      expect(result.data.data.list).toHaveLength(3)
      expect(result.data.data.list[0].name).toBe('学习讨论')
    })
  })
}) 