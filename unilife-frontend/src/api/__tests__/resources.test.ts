import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import * as resourcesApi from '../resources'

// Mock axios
vi.mock('axios')
const mockedAxios = vi.mocked(axios)

describe('Resources API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getResources', () => {
    it('should fetch resources successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: {
            total: 5,
            list: [
              {
                id: 1,
                title: '测试资源',
                description: '测试资源描述',
                fileName: 'test.pdf',
                fileUrl: 'http://example.com/test.pdf',
                fileSize: 1024,
                fileType: 'pdf',
                categoryId: 1,
                userId: 1,
                downloadCount: 10,
                likeCount: 5,
                createdAt: '2024-01-01T10:00:00'
              }
            ],
            pages: 1
          }
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getResources({
        category: 1,
        user: 1,
        keyword: '测试',
        page: 1,
        size: 10
      })

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources', {
        params: {
          category: 1,
          user: 1,
          keyword: '测试',
          page: 1,
          size: 10
        }
      })
      expect(result.data.data.list).toHaveLength(1)
      expect(result.data.data.list[0].title).toBe('测试资源')
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

      await resourcesApi.getResources({})

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources', {
        params: {}
      })
    })
  })

  describe('getResourceDetail', () => {
    it('should fetch resource detail successfully', async () => {
      const mockResource = {
        id: 1,
        title: '测试资源详情',
        description: '详细描述',
        fileName: 'test.pdf',
        fileUrl: 'http://example.com/test.pdf',
        fileSize: 2048,
        fileType: 'pdf',
        categoryId: 1,
        userId: 1,
        author: {
          id: 1,
          nickname: '作者',
          avatar: 'avatar.jpg'
        },
        category: {
          id: 1,
          name: '学习资料'
        },
        downloadCount: 20,
        likeCount: 10,
        isLiked: false,
        createdAt: '2024-01-01T10:00:00'
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: mockResource
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getResourceDetail(1)

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources/1')
      expect(result.data.data.title).toBe('测试资源详情')
      expect(result.data.data.author.nickname).toBe('作者')
    })

    it('should handle resource not found', async () => {
      const mockResponse = {
        data: {
          code: 404,
          success: false,
          message: '资源不存在'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getResourceDetail(999)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('资源不存在')
    })
  })

  describe('uploadResource', () => {
    it('should upload resource successfully', async () => {
      const file = new File(['test content'], 'test.pdf', { type: 'application/pdf' })
      const resourceData = {
        title: '新资源',
        description: '新资源描述',
        categoryId: 1
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: { resourceId: 123 },
          message: '资源上传成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await resourcesApi.uploadResource(file, resourceData)

      expect(mockedAxios.post).toHaveBeenCalledWith(
        '/resources',
        expect.any(FormData),
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      )
      expect(result.data.data.resourceId).toBe(123)
      expect(result.data.message).toBe('资源上传成功')
    })

    it('should handle file validation errors', async () => {
      const file = new File([''], 'empty.pdf', { type: 'application/pdf' })
      const resourceData = {
        title: '新资源',
        description: '新资源描述',
        categoryId: 1
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '文件不能为空'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await resourcesApi.uploadResource(file, resourceData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('文件不能为空')
    })

    it('should handle unsupported file type', async () => {
      const file = new File(['test content'], 'test.exe', { type: 'application/octet-stream' })
      const resourceData = {
        title: '新资源',
        description: '新资源描述',
        categoryId: 1
      }

      const mockResponse = {
        data: {
          code: 400,
          success: false,
          message: '不支持的文件类型'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await resourcesApi.uploadResource(file, resourceData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('不支持的文件类型')
    })
  })

  describe('updateResource', () => {
    it('should update resource successfully', async () => {
      const updateData = {
        title: '更新后的标题',
        description: '更新后的描述',
        categoryId: 2
      }

      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '资源更新成功'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await resourcesApi.updateResource(1, updateData)

      expect(mockedAxios.put).toHaveBeenCalledWith('/resources/1', updateData)
      expect(result.data.message).toBe('资源更新成功')
    })

    it('should handle unauthorized update', async () => {
      const updateData = {
        title: '更新后的标题',
        description: '更新后的描述',
        categoryId: 2
      }

      const mockResponse = {
        data: {
          code: 403,
          success: false,
          message: '无权限修改此资源'
        }
      }

      mockedAxios.put.mockResolvedValue(mockResponse)

      const result = await resourcesApi.updateResource(1, updateData)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('无权限修改此资源')
    })
  })

  describe('deleteResource', () => {
    it('should delete resource successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '资源删除成功'
        }
      }

      mockedAxios.delete.mockResolvedValue(mockResponse)

      const result = await resourcesApi.deleteResource(1)

      expect(mockedAxios.delete).toHaveBeenCalledWith('/resources/1')
      expect(result.data.message).toBe('资源删除成功')
    })
  })

  describe('downloadResource', () => {
    it('should download resource successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: {
            downloadUrl: 'http://example.com/download/test.pdf'
          },
          message: '获取下载链接成功'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.downloadResource(1)

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources/1/download')
      expect(result.data.data.downloadUrl).toBe('http://example.com/download/test.pdf')
    })

    it('should handle download permission denied', async () => {
      const mockResponse = {
        data: {
          code: 403,
          success: false,
          message: '无权限下载此资源'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.downloadResource(1)

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('无权限下载此资源')
    })
  })

  describe('likeResource', () => {
    it('should like resource successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '点赞成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await resourcesApi.likeResource(1)

      expect(mockedAxios.post).toHaveBeenCalledWith('/resources/1/like')
      expect(result.data.message).toBe('点赞成功')
    })

    it('should unlike resource successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          message: '取消点赞成功'
        }
      }

      mockedAxios.post.mockResolvedValue(mockResponse)

      const result = await resourcesApi.likeResource(1)

      expect(result.data.message).toBe('取消点赞成功')
    })
  })

  describe('getUserResources', () => {
    it('should fetch user resources successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: {
            total: 3,
            list: [
              {
                id: 1,
                title: '用户资源1',
                description: '描述1',
                fileName: 'file1.pdf',
                fileType: 'pdf',
                downloadCount: 5,
                likeCount: 2,
                createdAt: '2024-01-01T10:00:00'
              },
              {
                id: 2,
                title: '用户资源2',
                description: '描述2',
                fileName: 'file2.docx',
                fileType: 'docx',
                downloadCount: 8,
                likeCount: 3,
                createdAt: '2024-01-02T10:00:00'
              }
            ],
            pages: 1
          }
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getUserResources(1, {
        page: 1,
        size: 10
      })

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources/user/1', {
        params: {
          page: 1,
          size: 10
        }
      })
      expect(result.data.data.list).toHaveLength(2)
      expect(result.data.data.list[0].title).toBe('用户资源1')
    })
  })

  describe('getMyResources', () => {
    it('should fetch current user resources successfully', async () => {
      const mockResponse = {
        data: {
          code: 200,
          success: true,
          data: {
            total: 2,
            list: [
              {
                id: 1,
                title: '我的资源1',
                description: '我的描述1',
                fileName: 'myfile1.pdf',
                fileType: 'pdf',
                downloadCount: 3,
                likeCount: 1,
                createdAt: '2024-01-01T10:00:00'
              }
            ],
            pages: 1
          }
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getMyResources({
        page: 1,
        size: 10
      })

      expect(mockedAxios.get).toHaveBeenCalledWith('/resources/my', {
        params: {
          page: 1,
          size: 10
        }
      })
      expect(result.data.data.list).toHaveLength(1)
      expect(result.data.data.list[0].title).toBe('我的资源1')
    })

    it('should handle unauthorized access', async () => {
      const mockResponse = {
        data: {
          code: 401,
          success: false,
          message: '未登录'
        }
      }

      mockedAxios.get.mockResolvedValue(mockResponse)

      const result = await resourcesApi.getMyResources({})

      expect(result.data.success).toBe(false)
      expect(result.data.message).toBe('未登录')
    })
  })
}) 