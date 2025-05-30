<template>
  <div class="ai-assistant-container">
    <!-- 使用通用顶部导航栏组件 -->
    <TopNavbar />

    <!-- 主要内容区域 -->
    <div class="ai-main">
      <div class="ai-content">
        <!-- 会话管理侧边栏 -->
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
              @click="loadChat(chat.id)"
            >
              <div class="chat-title">{{ chat.title }}</div>
              <div class="chat-time">{{ formatTime(chat.updatedAt) }}</div>
            </div>
            
            <div v-if="chatHistory.length === 0" class="empty-history">
              <el-icon class="empty-icon"><ChatDotRound /></el-icon>
              <p>开启智能学习之旅</p>
            </div>
          </div>
        </aside>

        <!-- 主聊天区域 -->
        <main class="chat-area">
          <!-- 聊天消息区域 -->
          <div class="messages-container" ref="messagesContainer">
            <!-- 欢迎界面 -->
            <div v-if="currentMessages.length === 0" class="welcome-section">
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
            <div class="messages-list" v-if="currentMessages.length > 0">
              <div 
                v-for="(message, index) in currentMessages" 
                :key="index"
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
                  <div v-if="message.role === 'user'" class="message-bubble user-bubble">
                    <div class="message-text">{{ message.content }}</div>
                  </div>
                  <div v-else class="message-bubble ai-bubble">
                    <div v-if="isStreaming && index === currentMessages.length - 1" class="streaming-content">
                      <div class="message-text-streaming">
                        {{ message.content }}<span class="cursor-blink">|</span>
                      </div>
                    </div>
                    <div v-else class="ai-response">
                      <MdPreview 
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

          <!-- 输入区域 -->
          <div class="input-section">
            <!-- 主输入区域 -->
            <div class="input-container">
              <div class="input-wrapper" :class="{ 'input-focused': inputFocused, 'has-content': userInput.trim() }">
                <!-- 输入框 -->
                <div class="input-field">
                  <el-input
                    v-model="userInput"
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

                <!-- 发送按钮 -->
                <div class="input-actions">
                  <el-button 
                    type="primary"
                    circle
                    class="send-btn"
                    :class="{ 'btn-ready': canSend, 'btn-loading': isStreaming }"
                    :disabled="!canSend || isStreaming"
                    @click="sendMessage"
                  >
                    <el-icon v-if="!isStreaming">
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
                  <span class="char-count" :class="{ 'near-limit': userInput.length > 1500 }">
                    {{ userInput.length }}/2000
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
import { ref, onMounted, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Plus,
  ChatDotRound,
  Search,
  Document,
  TrendCharts,
  Right,
  ArrowLeft,
  Loading,
  Promotion
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import TopNavbar from '@/components/TopNavbar.vue'
import { 
  getChatHistory, 
  getChatMessages, 
  createChatSession,
  sendMessage as sendAIMessage,
  updateSessionTitle,
  deleteChatSession
} from '@/api/ai'
import type { ChatSession, ChatMessage } from '@/api/ai'

const userStore = useUserStore()

// 响应式数据
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref<string | null>(null)
const messagesContainer = ref()
const inputFocused = ref(false)
const sidebarCollapsed = ref(false)

// 聊天数据
const chatHistory = ref<ChatSession[]>([])
const currentMessages = ref<ChatMessage[]>([])

// 快速操作选项
const quickActions = ref([
  { text: '学习计划', prompt: '帮我制定一个学期的学习计划', icon: Document },
  { text: '课程推荐', prompt: '推荐一些适合我专业的课程', icon: TrendCharts },
  { text: '学习资源', prompt: '推荐一些高质量的学习资源', icon: Search },
  { text: '学习方法', prompt: '分享一些高效的学习方法', icon: ChatDotRound }
])

// 计算属性
const canSend = computed(() => {
  return userInput.value.trim() && !isStreaming.value
})

// 方法
const startNewChat = async () => {
  try {
    // 生成新的会话ID
    const newSessionId = `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    await createChatSession(newSessionId, '新对话')
    
    currentChatId.value = newSessionId
    currentMessages.value = []
    
    // 刷新会话列表
    await loadChatHistory()
  } catch (error: any) {
    console.error('创建新会话失败:', error)
    ElMessage.error('创建新会话失败')
  }
}

const loadChat = async (chatId: string) => {
  try {
    currentChatId.value = chatId
    
    // 加载历史消息
    const response = await getChatMessages(chatId, 1, 50)
    if (response.data.code === 200) {
      currentMessages.value = response.data.data.messages || []
    } else {
      ElMessage.error('加载聊天记录失败')
    }
  } catch (error: any) {
    console.error('加载聊天记录失败:', error)
    ElMessage.error('加载聊天记录失败')
  }
}

const loadChatHistory = async () => {
  try {
    const response = await getChatHistory(1, 20)
    if (response.data.code === 200) {
      chatHistory.value = response.data.data.sessions || []
    } else {
      console.error('加载聊天历史失败:', response.data.message)
    }
  } catch (error: any) {
    console.error('加载聊天历史失败:', error)
  }
}

const sendExampleQuestion = (prompt: string) => {
  userInput.value = prompt
  sendMessage()
}

// 核心发送消息方法 - 基于参考项目实现
const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return
  
  const messageContent = userInput.value.trim()
  
  // 如果没有当前会话ID，创建新的会话
  if (!currentChatId.value) {
    const newSessionId = `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    currentChatId.value = newSessionId
    
    try {
      await createChatSession(newSessionId, '新对话')
      // 刷新会话列表
      await loadChatHistory()
    } catch (error: any) {
      // 即使创建会话失败，也继续发送消息，让后端处理
    }
  }
  
  
  // 添加用户消息
  const userMessage: ChatMessage = {
    id: `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    role: 'user',
    content: messageContent,
    timestamp: new Date().toISOString()
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入
  userInput.value = ''
  await scrollToBottom()
  
  // 添加助手消息占位
  const assistantMessage: ChatMessage = {
    id: `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    role: 'assistant',
    content: '',
    timestamp: new Date().toISOString()
  }
  currentMessages.value.push(assistantMessage)
  isStreaming.value = true
  
  try {
    // 使用AI API模块进行流式请求，现在确保有sessionId
    const reader = await sendAIMessage(messageContent, currentChatId.value)
    const decoder = new TextDecoder('utf-8')
    let accumulatedContent = ''  // 累积内容变量
    
    while (true) {
      try {
        const { value, done } = await reader.read()
        if (done) break
        
        // 累积新内容
        accumulatedContent += decoder.decode(value)
        
        await nextTick(() => {
          // 更新消息内容，使用累积的内容
          const updatedMessage = {
            ...assistantMessage,
            content: accumulatedContent
          }
          const lastIndex = currentMessages.value.length - 1
          currentMessages.value.splice(lastIndex, 1, updatedMessage)
        })
        await scrollToBottom()
      } catch (readError) {
        console.error('读取流错误:', readError)
        break
      }
    }
  } catch (error: any) {
    console.error('发送消息失败:', error)
    assistantMessage.content = '抱歉，发生了错误，请稍后重试。'
    ElMessage.error('发送失败：' + (error.message || '网络错误'))
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    if (canSend.value) {
      sendMessage()
    }
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
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

const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleString()
}

onMounted(() => {
  console.log('AI助手页面加载完成')
  loadChatHistory()
})
</script>

<style scoped>
.ai-assistant-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 主要内容区域 */
.ai-main {
  padding: 32px 24px;
  min-height: calc(100vh - 88px);
}

.ai-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  height: calc(100vh - 152px);
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
  padding: 24px;
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

.user-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.ai-bubble {
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
  padding: 16px 32px 20px;
  background: white;
  border-top: 1px solid #e5e7eb;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  transition: all 0.3s ease;
}

.input-wrapper {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 20px;
  padding: 12px 16px;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.input-wrapper.input-focused {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1), 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.input-wrapper.has-content {
  background: white;
  border-color: #cbd5e1;
}

.input-field {
  flex: 1;
  min-width: 0;
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
  min-height: 20px !important;
}

.message-input :deep(.el-textarea__inner):focus {
  box-shadow: none !important;
}

.message-input :deep(.el-textarea__inner)::placeholder {
  color: #94a3b8 !important;
  font-weight: 400 !important;
}

.input-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.send-btn {
  background: #e2e8f0 !important;
  border: none !important;
  color: #94a3b8 !important;
  width: 40px !important;
  height: 40px !important;
  transition: all 0.3s ease !important;
  flex-shrink: 0 !important;
}

.send-btn.btn-ready {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3) !important;
}

.send-btn.btn-ready:hover {
  background: linear-gradient(135deg, #5a67d8, #6b46c1) !important;
  transform: scale(1.08);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4) !important;
}

.send-btn.btn-loading {
  background: #f1f5f9 !important;
  color: #64748b !important;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-footer {
  margin-top: 12px;
  padding: 0 4px;
}

.input-hints {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #64748b;
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.hint-item kbd {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 4px 8px;
  font-size: 11px;
  font-family: inherit;
  color: #475569;
  font-weight: 500;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.char-count {
  font-weight: 500;
  font-family: 'Monaco', 'Menlo', monospace;
}

.char-count.near-limit {
  color: #f59e0b;
  font-weight: 600;
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