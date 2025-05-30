<template>
  <div class="ai-assistant-container">
    <!-- 使用通用顶部导航栏组件 -->
    <TopNavbar />

    <!-- 主要内容区域 - 新的单栏布局 -->
    <div class="ai-main">
      <div class="ai-content">
        <!-- 会话管理侧边栏 - 可折叠 -->
        <aside class="chat-sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
          <div class="sidebar-header">
            <div class="sidebar-title" v-if="!sidebarCollapsed">
              <h3>对话历史</h3>
              <el-button 
                @click="startNewChat" 
                type="primary" 
                size="small" 
                class="new-chat-btn"
              >
                <el-icon><Plus /></el-icon>
                新对话
              </el-button>
            </div>
            <el-button 
              @click="toggleSidebar"
              text
              class="sidebar-toggle"
            >
              <el-icon v-if="sidebarCollapsed"><Right /></el-icon>
              <el-icon v-else><ArrowLeft /></el-icon>
            </el-button>
          </div>
          
          <div class="chat-history" v-if="!sidebarCollapsed">
            <div 
              v-for="chat in chatHistory" 
              :key="chat.id"
              class="chat-item"
              :class="{ active: currentChatId === chat.id }"
              @click="selectChat(chat.id)"
            >
              <div class="chat-title">{{ chat.title }}</div>
              <div class="chat-time">{{ chat.lastMessageTime }}</div>
            </div>
            
            <div v-if="chatHistory.length === 0" class="empty-history">
              <el-icon class="empty-icon"><ChatDotRound /></el-icon>
              <p>开启智能学习之旅</p>
            </div>
          </div>
        </aside>

        <!-- 主聊天区域 - 全新布局 -->
        <main class="chat-area">
          <!-- 聊天消息区域 -->
          <div class="messages-container" ref="messagesContainer">
            <!-- 欢迎界面 -->
            <div v-if="messages.length === 0" class="welcome-section">
              <div class="welcome-content">
                <!-- 标题区域 -->
                <div class="welcome-header">
                <div class="ai-logo">
                  <el-icon><TrendCharts /></el-icon>
                </div>
                  <h1 class="welcome-title">UniLife AI 学习助手</h1>
                <p class="welcome-subtitle">基于智能检索的个性化学习伙伴</p>
                </div>
                
                <!-- 功能特色 -->
                <div class="features-showcase">
                  <div class="feature-item">
                    <div class="feature-icon">
                    <el-icon><Search /></el-icon>
                    </div>
                    <span class="feature-text">智能资源搜索</span>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">
                    <el-icon><TrendCharts /></el-icon>
                    </div>
                    <span class="feature-text">个性化推荐</span>
                  </div>
                  <div class="feature-item">
                    <div class="feature-icon">
                    <el-icon><Document /></el-icon>
                    </div>
                    <span class="feature-text">学习路径规划</span>
                  </div>
                </div>

                <!-- 快速开始选项 -->
                <div class="quick-start">
                  <h3 class="quick-start-title">试试这些功能</h3>
                  <div class="quick-actions">
                    <button 
                      v-for="action in quickActions" 
                      :key="action.text"
                      class="quick-action-btn"
                      @click="sendExampleQuestion(action.prompt)"
                    >
                      <div class="action-icon">
                      <el-icon>
                        <component :is="action.icon" />
                      </el-icon>
                      </div>
                      <span class="action-text">{{ action.text }}</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 消息列表 -->
            <div class="messages-list" v-if="messages.length > 0">
              <div 
                v-for="message in messages" 
                :key="message.id"
                class="message-item"
                :class="{ 'user-message': message.role === 'user', 'ai-message': message.role === 'assistant' }"
              >
                <div class="message-avatar">
                  <el-avatar v-if="message.role === 'user'" :size="32" :src="userStore.user?.avatar">
                    {{ userStore.user?.nickname?.charAt(0) }}
                  </el-avatar>
                  <div v-else class="ai-avatar">
                    <el-icon><TrendCharts /></el-icon>
                  </div>
                </div>
                
                <div class="message-content">
                  <div class="message-bubble">
                    <div v-if="message.role === 'user'" class="message-text">
                      {{ message.content }}
                    </div>
                    <div v-else class="ai-response">
                      <!-- AI思考中 -->
                      <div v-if="message.typing" class="typing-indicator">
                        <div class="typing-animation">
                          <span></span>
                          <span></span>
                          <span></span>
                        </div>
                        <span class="typing-text">AI正在思考...</span>
                      </div>
                      
                      <!-- 流式显示中 -->
                      <div v-else-if="message.streaming" class="streaming-content">
                        <div class="message-text-streaming">
                          {{ message.content }}<span class="cursor-blink">|</span>
                        </div>
                      </div>
                      
                      <!-- 完成显示 -->
                      <MdPreview 
                        v-else
                        :model-value="message.content"
                        preview-theme="default"
                        code-theme="atom"
                        class="ai-markdown"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 全新的输入区域 -->
          <div class="input-section">
            <!-- 快速操作栏 -->
            <div class="quick-toolbar" v-if="messages.length > 0">
              <el-button 
                text 
                size="small"
                @click="clearCurrentChat"
                class="toolbar-btn"
              >
                <el-icon><Delete /></el-icon>
                清空对话
              </el-button>
              <el-button 
                text 
                size="small"
                @click="exportChat"
                class="toolbar-btn"
              >
                <el-icon><Download /></el-icon>
                导出对话
              </el-button>
            </div>

            <!-- 附件显示区域 -->
            <div v-if="attachments.length > 0" class="attachments-preview">
              <div 
                v-for="(attachment, index) in attachments" 
                :key="index"
                class="attachment-item"
              >
                <!-- 图片预览 -->
                <div v-if="attachment.type === 'image'" class="image-preview">
                  <img :src="attachment.url" :alt="attachment.name" />
                  <div class="attachment-info">
                    <span class="attachment-name">{{ attachment.name }}</span>
                    <span class="attachment-size">{{ formatFileSize(attachment.size) }}</span>
                  </div>
                  <el-button 
                    circle 
                    size="small" 
                    class="remove-btn"
                    @click="removeAttachment(index)"
                  >
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
                
                <!-- 文件预览 -->
                <div v-else class="file-preview">
                  <div class="file-icon">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="attachment-info">
                    <span class="attachment-name">{{ attachment.name }}</span>
                    <span class="attachment-size">{{ formatFileSize(attachment.size) }}</span>
                  </div>
                  <el-button 
                    circle 
                    size="small" 
                    class="remove-btn"
                    @click="removeAttachment(index)"
                  >
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 主输入区域 -->
            <div 
              class="input-container"
              @drop="handleDrop"
              @dragover="handleDragOver"
              @dragenter="handleDragEnter"
              @dragleave="handleDragLeave"
              :class="{ 'drag-over': isDragOver }"
            >
              <div class="input-wrapper" :class="{ 'input-focused': inputFocused, 'has-content': inputMessage.trim() || attachments.length > 0 }">
                <!-- 拖拽提示 -->
                <div v-if="isDragOver" class="drag-overlay">
                  <div class="drag-content">
                    <el-icon class="drag-icon"><Upload /></el-icon>
                    <p>拖拽文件到这里上传</p>
                    <span>支持图片、文档等格式</span>
                  </div>
                </div>

                <!-- 输入框 -->
                <div class="input-field">
                  <el-input
                    v-model="inputMessage"
                    type="textarea"
                    :rows="1"
                    :autosize="{ minRows: 1, maxRows: 5 }"
                    resize="none"
                    placeholder="询问任何关于学习的问题..."
                    class="message-input"
                    @keydown="handleKeyDown"
                    @focus="onInputFocus"
                    @blur="onInputBlur"
                  />
                </div>

                <!-- 操作按钮组 -->
                <div class="input-actions">
                  <!-- 附件上传按钮组 -->
                  <div class="attachment-actions">
                    <!-- 图片上传 -->
                    <el-upload
                      ref="imageUploadRef"
                      :auto-upload="false"
                      :show-file-list="false"
                      accept="image/*"
                      :on-change="handleImageUpload"
                      class="upload-component"
                    >
                      <el-button 
                        text 
                        circle 
                        class="action-btn image-btn"
                        title="上传图片"
                      >
                        <el-icon><Picture /></el-icon>
                      </el-button>
                    </el-upload>

                    <!-- 文件上传 -->
                    <el-upload
                      ref="fileUploadRef"
                      :auto-upload="false"
                      :show-file-list="false"
                      accept=".pdf,.doc,.docx,.txt,.md,.ppt,.pptx,.xls,.xlsx"
                      :on-change="handleFileUpload"
                      class="upload-component"
                    >
                  <el-button 
                    text 
                    circle 
                    class="action-btn attachment-btn"
                    title="上传文件"
                  >
                    <el-icon><Document /></el-icon>
                  </el-button>
                    </el-upload>
                  </div>

                  <!-- 发送按钮 -->
                  <el-button 
                    type="primary"
                    circle
                    class="send-btn"
                    :class="{ 'btn-ready': canSend, 'btn-loading': isLoading }"
                    :disabled="!canSend || isLoading"
                    @click="sendMessage"
                  >
                    <el-icon v-if="!isLoading">
                      <Promotion />
                    </el-icon>
                    <div v-else class="loading-spinner">
                      <el-icon class="is-loading"><Loading /></el-icon>
                    </div>
                  </el-button>
                </div>
              </div>

              <!-- 输入提示信息 -->
              <div class="input-footer">
                <div class="input-hints">
                  <span class="hint-item">
                    <kbd>Enter</kbd> 发送消息
                  </span>
                  <span class="hint-item">
                    支持拖拽上传图片和文件
                  </span>
                  <span class="char-count" :class="{ 'near-limit': inputMessage.length > 1500 }">
                    {{ inputMessage.length }}/2000
                  </span>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps, UploadUserFile } from 'element-plus'
import { 
  Plus,
  ChatDotRound,
  User,
  Delete,
  Promotion,
  Edit,
  Search,
  Document,
  TrendCharts,
  Right,
  ArrowLeft,
  Loading,
  Download,
  Close,
  Upload,
  Picture
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import TopNavbar from '@/components/TopNavbar.vue'
import { 
  sendMessage as sendMessageAPI, 
  getChatHistory as getChatHistoryAPI,
  getChatMessages as getChatMessagesAPI,
  createChatSession as createChatSessionAPI,
  deleteChatSession as deleteChatSessionAPI,
  clearSessionMessages as clearSessionMessagesAPI,
  updateSessionTitle as updateChatTitleAPI,
  type ChatMessage as BaseChatMessage,
  type ChatSession,
  type ChatHistoryResponse,
  type ChatMessagesResponse
} from '@/api/ai'
import type { ApiResponse } from '@/types'
import { generateSessionId, generateMessageId } from '@/utils'

const router = useRouter()
const userStore = useUserStore()

// 扩展ChatMessage类型以支持typing状态
interface ChatMessage extends BaseChatMessage {
  typing?: boolean
  streaming?: boolean  // 新增：是否正在流式显示
}

// 响应式数据
const inputMessage = ref('')
const isLoading = ref(false)
const currentChatId = ref<string | null>(null)
const messagesContainer = ref()
const inputFocused = ref(false)
const sidebarCollapsed = ref(false)
const isDragOver = ref(false)

// 聊天数据
const chatHistory = ref<ChatSession[]>([])
const messages = ref<ChatMessage[]>([])

// 附件管理
const attachments = ref<Array<{
  type: 'image' | 'file'
  name: string
  size: number
  url: string
  file: File
}>>([])

// 示例问题
const exampleQuestions = [
  '帮我搜索关于Python编程的学习资源',
  '根据我的学习兴趣推荐学习路线',
  '找找数据结构相关的课件和资料',
  '基于我点赞的内容，推荐相关学习材料',
  '搜索机器学习的实战项目资源',
  '为我制定个性化的前端开发学习计划'
]

// 快速操作选项
const quickActions = ref([
  { text: '学习计划', prompt: '帮我制定一个学期的学习计划', icon: Document },
  { text: '课程推荐', prompt: '推荐一些适合我专业的课程', icon: TrendCharts },
  { text: '学习资源', prompt: '推荐一些高质量的学习资源', icon: Search },
  { text: '学习方法', prompt: '分享一些高效的学习方法', icon: ChatDotRound }
])

// 编辑相关
const editingChatId = ref<string | null>(null)
const editingTitle = ref('')
const editTitleInput = ref()

// 计算属性
const tokenCountClass = computed(() => {
  const length = inputMessage.value.length
  if (length >= 1800) return 'at-limit'
  if (length >= 1500) return 'near-limit'
  return ''
})

const canSend = computed(() => {
  return inputMessage.value.trim() || attachments.value.length > 0
})

// 方法
const startNewChat = () => {
  currentChatId.value = null
  messages.value = []
}

const selectChat = async (chatId: string) => {
  if (currentChatId.value === chatId) return
  
  currentChatId.value = chatId
  await loadChatMessages(chatId)
}

const sendExampleQuestion = (prompt: string) => {
  inputMessage.value = prompt
  sendMessage()
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage: ChatMessage = {
    id: generateMessageId(),
    role: 'user' as const,
    content: inputMessage.value.trim(),
    timestamp: new Date().toISOString()
  }

  messages.value.push(userMessage)
  
  const messageContent = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    isLoading.value = true
    
    // 添加AI消息框，准备接收流式内容
    const aiMessage: ChatMessage = {
      id: generateMessageId(),
      role: 'assistant' as const,
      content: '',
      timestamp: new Date().toISOString(),
      typing: true
    }
    messages.value.push(aiMessage)
    
    await nextTick()
    scrollToBottom()

    // 调用AI接口（流式）
    await callAIAPIStream(messageContent, aiMessage)
    
    // 完成后清理所有中间状态
    aiMessage.typing = false
    aiMessage.streaming = false
    
  } catch (error: any) {
    ElMessage.error('发送失败：' + (error.message || '网络错误'))
    messages.value.pop() // 移除失败的AI消息
  } finally {
    isLoading.value = false
  }
}

const callAIAPIStream = async (message: string, aiMessage: ChatMessage): Promise<void> => {
  try {
    // 如果没有当前会话ID，创建一个新会话
    if (!currentChatId.value) {
      const newSessionId = generateSessionId()
      try {
        const createResponse = await createChatSessionAPI(newSessionId) as any as ApiResponse<{ sessionId: string; title: string }>
        if (createResponse.code === 200) {
          currentChatId.value = newSessionId
          // 重新加载聊天历史以包含新会话
          await loadChatHistory()
        }
      } catch (createError) {
        console.warn('创建会话失败，使用临时会话ID:', createError)
        currentChatId.value = newSessionId
      }
    }

    // 获取流式读取器
    const reader = await sendMessageAPI({
      message: message,
      sessionId: currentChatId.value!,
      conversationHistory: messages.value
        .filter(m => m.role !== 'assistant' || m.content)
        .map(m => ({
          id: m.id,
          role: m.role,
          content: m.content,
          timestamp: m.timestamp
        }))
    })

    const decoder = new TextDecoder('utf-8')
    let accumulatedContent = ''  // 累积内容

    // 第一次开始接收数据时切换状态
    aiMessage.typing = false
    aiMessage.streaming = true

    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)
        
        await nextTick(() => {
          // 更新消息内容
          aiMessage.content = accumulatedContent
        })
        
        // 滚动到底部
        await scrollToBottom()
      } catch (readError) {
        console.error('读取流错误:', readError)
        break
      }
    }
    
  } catch (error: any) {
    console.error('AI API调用失败:', error)
    throw new Error(error.message || '网络请求失败')
  }
}

const clearCurrentChat = async () => {
  if (!currentChatId.value) {
    messages.value = []
    return
  }

  try {
    await ElMessageBox.confirm('确定要清空当前对话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await clearSessionMessagesAPI(currentChatId.value)
    messages.value = []
    ElMessage.success('对话已清空')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

const loadChatMessages = async (chatId: string) => {
  try {
    isLoading.value = true
    const response = await getChatMessagesAPI(chatId) as any as ApiResponse<ChatMessagesResponse>
    if (response.code === 200) {
      messages.value = response.data.messages
      await nextTick()
      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载消息失败')
    console.error(error)
  } finally {
    isLoading.value = false
  }
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    if (canSend.value && !isLoading.value) {
    sendMessage()
    }
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

onMounted(() => {
  console.log('AI助手页面加载完成')
  // 加载聊天历史
  loadChatHistory()
})

const loadChatHistory = async () => {
  try {
    const response = await getChatHistoryAPI() as any as ApiResponse<ChatHistoryResponse>
    
    if (response.code === 200) {
      chatHistory.value = response.data.sessions
    } else {
      console.error('加载聊天历史失败:', response.message)
    }
  } catch (error: any) {
    console.error('加载聊天历史失败:', error)
  }
}

const startEditTitle = (chatId: string, title: string) => {
  editingChatId.value = chatId
  editingTitle.value = title
  nextTick(() => {
    editTitleInput.value.focus()
  })
}

const saveTitle = async (chatId: string) => {
  if (!editingTitle.value.trim()) {
    cancelEdit()
    return
  }

  try {
    const response = await updateChatTitleAPI(chatId, editingTitle.value) as any as ApiResponse<null>
    
    if (response.code === 200) {
      // 更新聊天历史
      const index = chatHistory.value.findIndex(chat => chat.id === chatId)
      if (index !== -1) {
        chatHistory.value[index].title = editingTitle.value
      }
      
      ElMessage.success('聊天标题已更新')
      cancelEdit()
    } else {
      ElMessage.error('更新聊天标题失败：' + response.message)
      cancelEdit()
    }
  } catch (error: any) {
    console.error('更新聊天标题失败:', error)
    ElMessage.error('更新聊天标题失败：' + (error.response?.data?.message || '网络错误'))
    cancelEdit()
  }
}

const cancelEdit = () => {
  editingChatId.value = null
  editingTitle.value = ''
}

const onInputFocus = () => {
  inputFocused.value = true
}

const onInputBlur = () => {
  inputFocused.value = false
}

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const exportChat = () => {
  // Implementation for exporting chat
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
  const files = event.dataTransfer?.files
  if (files && files.length > 0) {
    const file = files[0]
    processFile(file)
  }
}

const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragEnter = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  if (event.relatedTarget === null) {
    isDragOver.value = false
  }
}

const handleImageUpload: UploadProps['onChange'] = (uploadFile) => {
  if (uploadFile.raw) {
    processFile(uploadFile.raw, 'image')
  }
}

const handleFileUpload: UploadProps['onChange'] = (uploadFile) => {
  if (uploadFile.raw) {
    processFile(uploadFile.raw, 'file')
  }
}

const processFile = (file: File, forceType?: 'image' | 'file') => {
  // 检查文件大小限制 (10MB)
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  // 检查附件数量限制
  if (attachments.value.length >= 5) {
    ElMessage.error('最多只能上传5个附件')
    return
  }

  const fileType = forceType || (file.type.startsWith('image/') ? 'image' : 'file')
  
  // 创建文件URL用于预览
  const url = URL.createObjectURL(file)
  
  const attachment = {
    type: fileType,
    name: file.name,
    size: file.size,
    url,
    file
  }
  
  attachments.value.push(attachment)
  ElMessage.success(`已添加${fileType === 'image' ? '图片' : '文件'}：${file.name}`)
}

const removeAttachment = (index: number) => {
  const attachment = attachments.value[index]
  // 释放URL对象
  URL.revokeObjectURL(attachment.url)
  attachments.value.splice(index, 1)
  ElMessage.success('已移除附件')
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.ai-assistant-container {
  height: 100vh;
  background: var(--gradient-bg);
  overflow: hidden;
}

/* 主要内容区域 */
.ai-main {
  padding: 0;
  height: calc(100vh - 88px);
  overflow: hidden;
}

.ai-content {
  display: flex;
  height: 100%;
  overflow: hidden;
}

/* 侧边栏 */
.chat-sidebar {
  width: 300px;
  background: #f8fafc;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.chat-sidebar.sidebar-collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sidebar-title {
  flex: 1;
}

.sidebar-title h3 {
  margin: 0 0 12px 0;
  color: #374151;
  font-size: 16px;
  font-weight: 700;
}

.new-chat-btn {
  font-size: 12px;
  height: 32px;
  border-radius: 8px;
}

.sidebar-toggle {
  padding: 8px !important;
  color: #6b7280 !important;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.chat-item {
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 8px;
}

.chat-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.chat-item.active {
  background: rgba(102, 126, 234, 0.1);
  border-left: 3px solid #667eea;
}

.chat-title {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-time {
  color: #9ca3af;
  font-size: 12px;
}

.empty-history {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #d1d5db;
}

/* 主聊天区域 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background: white;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  background: #f8fafc;
}

/* 欢迎界面 */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  overflow-y: auto;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
  width: 100%;
}

.welcome-header {
  margin-bottom: 48px;
}

.ai-logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 36px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.welcome-title {
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.welcome-subtitle {
  font-size: 18px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

.features-showcase {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 48px;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 16px;
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  min-width: 140px;
}

.feature-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #667eea;
}

.feature-text {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  text-align: center;
}

.quick-start {
  margin-top: 32px;
}

.quick-start-title {
  font-size: 20px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 24px 0;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  max-width: 500px;
  margin: 0 auto;
}

.quick-action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
  width: 100%;
}

.quick-action-btn:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
}

.action-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #667eea;
  flex-shrink: 0;
}

.action-text {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  flex: 1;
}

/* 消息列表 */
.messages-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message-item.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 16px 20px;
  border-radius: 18px;
  position: relative;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.ai-message .message-bubble {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 6px;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.ai-markdown {
  font-size: 14px !important;
  line-height: 1.6 !important;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.typing-animation {
  display: flex;
  gap: 4px;
}

.typing-animation span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-animation span:nth-child(1) { animation-delay: -0.32s; }
.typing-animation span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.typing-text {
  color: #6b7280;
  font-size: 14px;
}

/* 流式显示样式 */
.streaming-content {
  position: relative;
}

.message-text-streaming {
  line-height: 1.6;
  word-wrap: break-word;
  color: #374151;
  font-size: 14px;
  white-space: pre-wrap;
}

.cursor-blink {
  animation: blink 1s infinite;
  color: #667eea;
  font-weight: bold;
  margin-left: 2px;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

/* 输入区域 */
.input-section {
  padding: 20px 24px 24px;
  background: white;
  border-top: 1px solid #f3f4f6;
}

.quick-toolbar {
  padding: 12px 24px;
  display: flex;
  gap: 12px;
  border-bottom: 1px solid #f3f4f6;
  background: #fafbfc;
}

.toolbar-btn {
  font-size: 12px !important;
  color: #6b7280 !important;
  background: transparent !important;
  border: none !important;
  transition: all 0.2s ease !important;
}

.toolbar-btn:hover {
  color: #374151 !important;
  background: #f3f4f6 !important;
}

.input-container {
  position: relative;
  transition: all 0.3s ease;
}

.input-wrapper {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 20px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.input-wrapper.input-focused {
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.input-wrapper.has-content {
  background: white;
  border-color: #d1d5db;
}

.input-field {
  flex: 1;
  margin-right: 12px;
}

.message-input {
  border: none !important;
  background: transparent !important;
  font-size: 16px !important;
  line-height: 1.5 !important;
  resize: none !important;
}

.message-input :deep(.el-textarea__inner) {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
  padding: 0 !important;
  color: #374151 !important;
  font-size: 16px !important;
  line-height: 1.5 !important;
  resize: none !important;
}

.message-input :deep(.el-textarea__inner):focus {
  box-shadow: none !important;
}

.message-input :deep(.el-textarea__inner)::placeholder {
  color: #9ca3af !important;
  font-weight: 400 !important;
}

.input-actions {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.attachment-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.upload-component {
  display: inline-block;
}

.action-btn {
  background: transparent !important;
  border: none !important;
  color: #6b7280 !important;
  width: 36px !important;
  height: 36px !important;
  transition: all 0.2s ease !important;
}

.action-btn:hover {
  background: #f3f4f6 !important;
  color: #374151 !important;
  transform: scale(1.1);
}

.image-btn:hover {
  color: #059669 !important;
}

.attachment-btn:hover {
  color: #7c3aed !important;
}

.send-btn {
  background: #e5e7eb !important;
  border: none !important;
  color: #9ca3af !important;
  width: 40px !important;
  height: 40px !important;
  transition: all 0.3s ease !important;
}

.send-btn.btn-ready {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8) !important;
  color: white !important;
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3) !important;
}

.send-btn.btn-ready:hover {
  background: linear-gradient(135deg, #1d4ed8, #1e40af) !important;
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4) !important;
}

.send-btn.btn-loading {
  background: #f3f4f6 !important;
  color: #6b7280 !important;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-footer {
  margin-top: 12px;
}

.input-hints {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #9ca3af;
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.hint-item kbd {
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 10px;
  font-family: inherit;
  color: #6b7280;
}

.char-count {
  font-weight: 500;
}

.char-count.near-limit {
  color: #f59e0b;
  font-weight: 600;
}

/* 附件预览区域 */
.attachments-preview {
  padding: 12px 20px 0;
  max-height: 200px;
  overflow-y: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.attachment-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.attachment-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.image-preview {
  display: flex;
  align-items: center;
  padding: 8px;
  max-width: 200px;
}

.image-preview img {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}

.file-preview {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  min-width: 150px;
}

.file-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: white;
  font-size: 16px;
}

.attachment-info {
  flex: 1;
  min-width: 0;
}

.attachment-name {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
}

.attachment-size {
  font-size: 10px;
  color: #9ca3af;
}

.remove-btn {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ef4444 !important;
  border: 2px solid white !important;
  color: white !important;
  width: 20px !important;
  height: 20px !important;
  font-size: 12px !important;
}

/* 拖拽上传样式 */
.input-container.drag-over {
  transform: scale(1.02);
}

.input-container.drag-over .input-wrapper {
  border-color: #3b82f6 !important;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), rgba(99, 102, 241, 0.05)) !important;
}

.drag-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(59, 130, 246, 0.1);
  backdrop-filter: blur(4px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.drag-content {
  text-align: center;
  color: #3b82f6;
}

.drag-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.drag-content p {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.drag-content span {
  font-size: 12px;
  opacity: 0.8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .ai-content {
    flex-direction: column;
    height: auto;
    min-height: calc(100vh - 120px);
  }
  
  .chat-sidebar {
    width: 100%;
    height: auto;
    order: 2;
  }
  
  .chat-area {
    order: 1;
  }
  
  .features-showcase {
    flex-direction: column;
    gap: 16px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .ai-main {
    padding: 16px;
  }
  
  .ai-content {
    border-radius: 16px;
  }
  
  .welcome-content h1 {
    font-size: 28px;
  }
  
  .ai-logo {
    width: 80px;
    height: 80px;
    font-size: 32px;
  }
  
  .features-showcase {
    gap: 12px;
  }
  
  .feature-item {
    padding: 16px 12px;
    min-width: 100px;
  }
  
  .input-wrapper {
    padding: 12px 16px;
  }
  
  .message-content {
    max-width: 90%;
  }
}
</style>