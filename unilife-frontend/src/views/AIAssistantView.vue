<template>
  <div class="ai-assistant-container">
    <!-- 顶部导航栏 -->
    <nav class="ai-navbar glass-light">
      <div class="nav-container">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">
            <div class="logo-circle">
              <i class="el-icon-star-filled"></i>
            </div>
            <span class="brand-name gradient-text">UniLife</span>
          </router-link>
        </div>
        
        <div class="nav-menu">
          <router-link to="/forum" class="nav-item">论坛</router-link>
          <router-link to="/resources" class="nav-item">资源</router-link>
          <router-link to="/schedule" class="nav-item">课程表</router-link>
          <router-link to="/tasks" class="nav-item">日程管理</router-link>
          <router-link to="/ai-assistant" class="nav-item active">AI助手</router-link>
        </div>
        
        <div class="nav-actions">
          <div class="user-info">
            <el-avatar :size="36" :src="userStore.user?.avatar">
              {{ userStore.user?.nickname?.charAt(0) }}
            </el-avatar>
            <span class="username">{{ userStore.user?.nickname }}</span>
          </div>
          <el-dropdown @command="handleCommand">
            <el-button circle>
              <el-icon><Setting /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="ai-main">
      <div class="ai-content">
        <!-- 侧边栏 - 聊天历史 -->
        <aside class="chat-sidebar card-light">
          <div class="sidebar-header">
            <h3>聊天记录</h3>
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
          
          <div class="chat-history">
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
            
            <!-- 空状态 -->
            <div v-if="chatHistory.length === 0" class="empty-history">
              <el-icon class="empty-icon"><ChatDotRound /></el-icon>
              <p>暂无聊天记录</p>
            </div>
          </div>
        </aside>

        <!-- 主聊天区域 -->
        <main class="chat-area">
          <!-- 聊天头部 -->
          <div class="chat-header card-light">
            <div class="chat-info">
              <div class="ai-avatar">
                <el-icon><User /></el-icon>
              </div>
              <div class="ai-details">
                <h3>UniLife AI助手</h3>
                <span class="ai-status">
                  <span class="status-dot"></span>
                  在线
                </span>
              </div>
            </div>
            
            <div class="chat-actions">
              <el-button text @click="clearCurrentChat">
                <el-icon><Delete /></el-icon>
                清空对话
              </el-button>
            </div>
          </div>

          <!-- 消息列表 -->
          <div class="messages-container" ref="messagesContainer">
            <!-- 欢迎消息 -->
            <div v-if="messages.length === 0" class="welcome-section">
              <div class="welcome-avatar">
                <el-icon><User /></el-icon>
              </div>
              <h2>你好！我是UniLife AI助手</h2>
              <p>我可以帮助你解答学习、生活、技术等各方面的问题</p>
              
              <!-- 示例问题 -->
              <div class="example-questions">
                <h4>你可以这样问我：</h4>
                <div class="questions-grid">
                  <div 
                    v-for="example in exampleQuestions" 
                    :key="example"
                    class="example-item"
                    @click="sendExampleQuestion(example)"
                  >
                    {{ example }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 消息列表 -->
            <div class="messages-list">
              <div 
                v-for="message in messages" 
                :key="message.id"
                class="message-item"
                :class="{ 'user-message': message.role === 'user', 'ai-message': message.role === 'assistant' }"
              >
                <div class="message-avatar">
                  <el-avatar v-if="message.role === 'user'" :size="36" :src="userStore.user?.avatar">
                    {{ userStore.user?.nickname?.charAt(0) }}
                  </el-avatar>
                  <div v-else class="ai-avatar-small">
                    <el-icon><User /></el-icon>
                  </div>
                </div>
                
                <div class="message-content">
                  <div class="message-bubble">
                    <div v-if="message.role === 'user'" class="message-text">
                      {{ message.content }}
                    </div>
                    <div v-else class="ai-response">
                      <MdPreview 
                        v-if="!message.typing"
                        :model-value="message.content"
                        preview-theme="default"
                        code-theme="atom"
                        class="ai-markdown"
                      />
                      <div v-else class="typing-indicator">
                        <span class="typing-dot"></span>
                        <span class="typing-dot"></span>
                        <span class="typing-dot"></span>
                      </div>
                    </div>
                  </div>
                  <div class="message-time">{{ message.timestamp }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="input-area card-light">
            <div class="input-container">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="1"
                resize="none"
                placeholder="请输入您的问题..."
                class="message-input"
                @keydown="handleKeyDown"
              />
              <el-button 
                type="primary" 
                class="send-btn"
                :loading="isLoading"
                :disabled="!inputMessage.trim()"
                @click="sendMessage"
              >
                <el-icon v-if="!isLoading"><Promotion /></el-icon>
                {{ isLoading ? '发送中...' : '发送' }}
              </el-button>
            </div>
            
            <div class="input-footer">
              <span class="input-tip">按 Ctrl + Enter 发送消息</span>
              <span class="token-count">{{ inputMessage.length }}/2000</span>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Setting,
  Plus,
  ChatDotRound,
  User,
  Delete,
  Promotion
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { 
  sendMessage as sendMessageAPI, 
  getChatHistory as getChatHistoryAPI,
  getChatMessages as getChatMessagesAPI,
  createChatSession as createChatSessionAPI,
  deleteChatSession as deleteChatSessionAPI,
  clearSessionMessages as clearSessionMessagesAPI,
  type ChatMessage,
  type ChatSession
} from '@/api/ai'
import type { ApiResponse } from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const inputMessage = ref('')
const isLoading = ref(false)
const currentChatId = ref<string | null>(null)
const messagesContainer = ref()

// 聊天数据
const chatHistory = ref<any[]>([])
const messages = ref<any[]>([])

// 示例问题
const exampleQuestions = [
  '如何有效地学习编程？',
  '大学生活有什么建议？',
  '如何准备考研？',
  '写一个Python快速排序算法',
  '推荐一些提高效率的工具',
  '如何保持学习动力？'
]

// 方法
const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

const startNewChat = () => {
  currentChatId.value = null
  messages.value = []
}

const selectChat = async (chatId: string) => {
  currentChatId.value = chatId
  // 加载聊天消息
  await loadChatMessages(chatId)
}

const sendExampleQuestion = (question: string) => {
  inputMessage.value = question
  sendMessage()
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: inputMessage.value.trim(),
    timestamp: new Date().toLocaleTimeString()
  }

  messages.value.push(userMessage)
  
  const messageContent = inputMessage.value.trim()
  inputMessage.value = ''
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()

  try {
    isLoading.value = true
    
    // 添加AI typing状态
    const aiMessage = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: '',
      typing: true,
      timestamp: new Date().toLocaleTimeString()
    }
    messages.value.push(aiMessage)
    
    await nextTick()
    scrollToBottom()

    // 调用AI接口
    const response = await callAIAPI(messageContent)
    
    // 移除typing状态，显示回复
    aiMessage.typing = false
    aiMessage.content = response
    
    await nextTick()
    scrollToBottom()

  } catch (error: any) {
    ElMessage.error('发送失败：' + (error.message || '网络错误'))
    messages.value.pop() // 移除失败的AI消息
  } finally {
    isLoading.value = false
  }
}

const callAIAPI = async (message: string): Promise<string> => {
  // 这里应该调用真实的AI API
  return `这是对"${message}"的AI回复示例。\n\n**功能说明：**\n- 支持Markdown格式\n- 支持代码高亮\n- 支持数学公式\n\n\`\`\`javascript\nconsole.log('Hello, UniLife!');\n\`\`\`\n\n希望这个回复对您有帮助！`
}

const clearCurrentChat = () => {
  messages.value = []
  ElMessage.success('对话已清空')
}

const loadChatMessages = async (chatId: string) => {
  // 这里应该调用真实的AI API来加载聊天消息
  messages.value = []
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.ctrlKey && event.key === 'Enter') {
    event.preventDefault()
    sendMessage()
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
  // 这里应该调用真实的AI API来加载聊天历史
  chatHistory.value = []
}
</script>

<style scoped>
.ai-assistant-container {
  min-height: 100vh;
  background: var(--gradient-bg);
}

/* 导航栏样式 */
.ai-navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 16px 0;
  border-bottom: 1px solid var(--gray-200);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.logo-circle {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: var(--shadow-light);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-item {
  text-decoration: none;
  color: var(--gray-600);
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 12px;
  transition: var(--transition-base);
}

.nav-item:hover,
.nav-item.active {
  color: var(--primary-600);
  background: var(--primary-50);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: var(--gray-700);
}

/* 主要内容区域 */
.ai-main {
  padding: 24px;
  height: calc(100vh - 88px);
}

.ai-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

/* 侧边栏样式 */
.chat-sidebar {
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--gray-200);
}

.sidebar-header h3 {
  color: var(--gray-800);
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}

.new-chat-btn {
  font-size: 12px;
  padding: 8px 12px;
  border-radius: 8px;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-item {
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: var(--transition-base);
  border: 1px solid transparent;
}

.chat-item:hover {
  background: var(--gray-50);
  border-color: var(--gray-200);
}

.chat-item.active {
  background: var(--primary-50);
  border-color: var(--primary-200);
}

.chat-title {
  font-weight: 600;
  color: var(--gray-800);
  font-size: 14px;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-time {
  color: var(--gray-500);
  font-size: 12px;
}

.empty-history {
  text-align: center;
  padding: 40px 20px;
  color: var(--gray-500);
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
  color: var(--gray-400);
}

/* 主聊天区域 */
.chat-area {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.chat-header {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--gray-200);
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ai-avatar {
  width: 48px;
  height: 48px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.ai-details h3 {
  color: var(--gray-800);
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--gray-600);
  font-size: 14px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
}

/* 消息区域 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.welcome-section {
  text-align: center;
  padding: 60px 20px;
}

.welcome-avatar {
  width: 80px;
  height: 80px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
  margin: 0 auto 24px;
}

.welcome-section h2 {
  color: var(--gray-800);
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
}

.welcome-section p {
  color: var(--gray-600);
  font-size: 16px;
  margin-bottom: 40px;
}

.example-questions h4 {
  color: var(--gray-700);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.example-item {
  padding: 12px 16px;
  background: white;
  border: 2px solid var(--gray-200);
  border-radius: 12px;
  cursor: pointer;
  transition: var(--transition-base);
  font-size: 14px;
  color: var(--gray-700);
}

.example-item:hover {
  border-color: var(--primary-300);
  background: var(--primary-50);
  transform: translateY(-2px);
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message-item {
  display: flex;
  gap: 12px;
}

.message-item.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar-small {
  width: 36px;
  height: 36px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 16px 20px;
  border-radius: 16px;
  margin-bottom: 4px;
}

.user-message .message-bubble {
  background: var(--gradient-primary);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: white;
  border: 1px solid var(--gray-200);
  border-bottom-left-radius: 4px;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
}

.ai-markdown {
  font-size: 14px !important;
  line-height: 1.6 !important;
}

.message-time {
  font-size: 12px;
  color: var(--gray-500);
}

.user-message .message-time {
  text-align: right;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-dot {
  width: 8px;
  height: 8px;
  background: var(--gray-400);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

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

/* 输入区域 */
.input-area {
  padding: 20px 24px;
  border-top: 1px solid var(--gray-200);
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.send-btn {
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 600;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: var(--gray-500);
}

.token-count {
  color: var(--gray-400);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .ai-content {
    grid-template-columns: 1fr;
  }
  
  .chat-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .ai-main {
    padding: 16px;
  }
  
  .messages-container {
    padding: 16px;
  }
  
  .message-content {
    max-width: 85%;
  }
}
</style> 