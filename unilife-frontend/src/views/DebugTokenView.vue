<template>
  <div class="debug-container">
    <h2>JWT Token 调试页面</h2>
    
    <div class="debug-section">
      <h3>当前Token信息</h3>
      <div class="token-info">
        <p><strong>Token存在:</strong> {{ !!currentToken }}</p>
        <p><strong>Token值:</strong> {{ currentToken ? currentToken.substring(0, 50) + '...' : '无' }}</p>
        <p><strong>Token过期:</strong> {{ tokenExpired }}</p>
        <p><strong>剩余时间:</strong> {{ remainingTime }}ms ({{ Math.floor(remainingTime / 1000) }}秒)</p>
      </div>
    </div>

    <div class="debug-section">
      <h3>Token解析信息</h3>
      <div class="token-payload" v-if="tokenPayload">
        <pre>{{ JSON.stringify(tokenPayload, null, 2) }}</pre>
      </div>
      <p v-else>无法解析Token</p>
    </div>

    <div class="debug-section">
      <h3>用户Store状态</h3>
      <div class="store-info">
        <p><strong>isLoggedIn:</strong> {{ userStore.isLoggedIn }}</p>
        <p><strong>用户ID:</strong> {{ userStore.user?.id || '无' }}</p>
        <p><strong>用户名:</strong> {{ userStore.user?.username || '无' }}</p>
      </div>
    </div>

    <div class="debug-section">
      <h3>操作</h3>
      <div class="actions">
        <el-button @click="refreshTokenInfo">刷新Token信息</el-button>
        <el-button @click="checkTokenValidity">检查Token有效性</el-button>
        <el-button @click="testApiCall">测试API调用</el-button>
        <el-button @click="clearToken" type="danger">清除Token</el-button>
      </div>
    </div>

    <div class="debug-section">
      <h3>调试日志</h3>
      <div class="debug-logs">
        <div v-for="(log, index) in debugLogs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { parseJwtToken, isTokenExpired, getTokenRemainingTime } from '@/utils/jwt'
import { getUserInfo } from '@/api/user'

const userStore = useUserStore()

const currentToken = ref<string>('')
const tokenPayload = ref<any>(null)
const debugLogs = ref<Array<{time: string, message: string}>>([])

const tokenExpired = computed(() => {
  return currentToken.value ? isTokenExpired(currentToken.value) : true
})

const remainingTime = computed(() => {
  return currentToken.value ? getTokenRemainingTime(currentToken.value) : 0
})

let interval: number | null = null

const addLog = (message: string) => {
  const time = new Date().toLocaleTimeString()
  debugLogs.value.unshift({ time, message })
  if (debugLogs.value.length > 20) {
    debugLogs.value = debugLogs.value.slice(0, 20)
  }
  console.log(`[${time}] ${message}`)
}

const refreshTokenInfo = () => {
  currentToken.value = localStorage.getItem('token') || ''
  if (currentToken.value) {
    tokenPayload.value = parseJwtToken(currentToken.value)
    addLog('Token信息已刷新')
  } else {
    tokenPayload.value = null
    addLog('未找到Token')
  }
}

const checkTokenValidity = () => {
  const isValid = userStore.checkTokenValidity()
  addLog(`Token有效性检查结果: ${isValid}`)
}

const testApiCall = async () => {
  try {
    addLog('开始测试API调用...')
    const response = await getUserInfo()
    addLog('API调用成功: ' + JSON.stringify(response).substring(0, 100))
  } catch (error: any) {
    addLog('API调用失败: ' + error.message)
  }
}

const clearToken = () => {
  localStorage.removeItem('token')
  userStore.logout()
  refreshTokenInfo()
  addLog('Token已清除')
}

onMounted(() => {
  refreshTokenInfo()
  addLog('调试页面已加载')
  
  // 每秒更新一次信息
  interval = setInterval(() => {
    refreshTokenInfo()
  }, 1000)
})

onUnmounted(() => {
  if (interval) {
    clearInterval(interval)
  }
})
</script>

<style scoped>
.debug-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.debug-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.debug-section h3 {
  margin-top: 0;
  color: #333;
}

.token-info p,
.store-info p {
  margin: 10px 0;
}

.token-payload {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 5px;
  font-family: monospace;
  font-size: 12px;
  overflow: auto;
}

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.debug-logs {
  max-height: 300px;
  overflow-y: auto;
  background: #f8f8f8;
  padding: 10px;
  border-radius: 5px;
}

.log-item {
  display: block;
  margin-bottom: 5px;
  font-family: monospace;
  font-size: 12px;
}

.log-time {
  color: #666;
  margin-right: 10px;
}

.log-message {
  color: #333;
}
</style> 