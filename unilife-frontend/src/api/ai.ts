import api from './index'
import type { ApiResponse } from '@/types'

// AI聊天相关接口
export interface ChatMessage {
  id: string
  role: 'user' | 'assistant' | 'system'
  content: string
  timestamp: string
}

export interface ChatSession {
  id: string
  title: string
  createdAt: string
  updatedAt: string
  lastMessageTime: string
  messageCount: number
}

export interface SendMessageRequest {
  message: string
  sessionId?: string
  conversationHistory?: ChatMessage[]
}

export interface ChatHistoryResponse {
  sessions: ChatSession[]
  total: number
}

export interface ChatMessagesResponse {
  messages: ChatMessage[]
  total: number
  sessionInfo: ChatSession
}

// 发送消息给AI（流式响应）
export const sendMessage = async (
  prompt: string,
  sessionId?: string | null
): Promise<ReadableStreamDefaultReader<Uint8Array>> => {
  const token = localStorage.getItem('token')
  
  // 使用URLSearchParams构建请求参数
  const params = new URLSearchParams()
  params.append('prompt', prompt)
  if (sessionId && sessionId.trim()) {
    params.append('sessionId', sessionId.trim())
    console.log('API请求包含sessionId:', sessionId.trim())
  } else {
    console.log('API请求不包含sessionId')
  }
  
  console.log('发送到后端的参数:', params.toString())

  const response = await fetch(`${api.defaults.baseURL}/ai/chat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: params
  })

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  if (!response.body) {
    throw new Error('Response body is not readable')
  }

  return response.body.getReader()
}

// 获取聊天历史列表
export const getChatHistory = (page = 1, size = 20) => {
  return api.get<ApiResponse<ChatHistoryResponse>>('/ai/sessions', { 
    params: { page, size } 
  })
}

// 获取指定会话的消息历史
export const getChatMessages = (sessionId: string, page = 1, size = 50) => {
  return api.get<ApiResponse<ChatMessagesResponse>>(`/ai/sessions/${sessionId}/messages`, { 
    params: { page, size } 
  })
}

// 创建新的聊天会话
export const createChatSession = (sessionId: string, title?: string) => {
  return api.post<ApiResponse<{ sessionId: string; title: string }>>('/ai/sessions', { 
    sessionId,
    title 
  })
}

// 删除聊天会话
export const deleteChatSession = (sessionId: string) => {
  return api.delete<ApiResponse>(`/ai/sessions/${sessionId}`)
}

// 更新会话标题
export const updateSessionTitle = (sessionId: string, title: string) => {
  return api.put<ApiResponse>(`/ai/sessions/${sessionId}`, { title })
}

// 清空会话消息
export const clearSessionMessages = (sessionId: string) => {
  return api.delete<ApiResponse>(`/ai/sessions/${sessionId}/messages`)
} 