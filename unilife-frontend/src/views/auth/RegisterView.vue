<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <!-- 注册卡片 -->
    <div class="register-card glass animate-fade-in">
      <!-- Logo和标题 -->
      <div class="register-header">
        <div class="logo-container animate-float">
          <div class="logo-circle">
            <i class="el-icon-star-filled gradient-text"></i>
          </div>
        </div>
        <h1 class="title gradient-text">加入 UniLife</h1>
        <p class="subtitle">开启您的精彩大学生活</p>
      </div>
      
      <!-- 注册表单 -->
      <div class="register-form">
        <el-form 
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          @submit.prevent="handleRegister"
          class="form-container"
        >
          <!-- 昵称输入 -->
          <div class="input-group animate-fade-in-up delay-100">
            <label class="input-label">昵称</label>
            <el-form-item prop="nickname">
              <el-input 
                v-model="registerForm.nickname"
                placeholder="请输入您的昵称"
                size="large"
                class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <!-- 用户名输入 -->
          <div class="input-group animate-fade-in-up delay-150">
            <label class="input-label">用户名</label>
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <!-- 邮箱输入 -->
          <div class="input-group animate-fade-in-up delay-200">
            <label class="input-label">邮箱地址</label>
            <el-form-item prop="email">
              <el-input 
                v-model="registerForm.email"
                placeholder="请输入您的邮箱地址"
                type="email"
                size="large"
                class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>
          
          <!-- 密码输入 -->
          <div class="input-group animate-fade-in-up delay-250">
            <label class="input-label">密码</label>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password"
                placeholder="请输入密码（至少6位）"
                type="password"
                size="large"
                show-password
                class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <!-- 确认密码 -->
          <div class="input-group animate-fade-in-up delay-300">
            <label class="input-label">确认密码</label>
                         <el-form-item prop="confirmPassword">
               <el-input 
                 v-model="registerForm.confirmPassword"
                placeholder="请再次输入密码"
                type="password"
                size="large"
                show-password
                class="modern-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <!-- 学生信息 -->
          <div class="student-info animate-fade-in-up delay-350">
            <div class="input-row">
              <div class="input-group half">
                <label class="input-label">学号</label>
                <el-form-item prop="studentId">
                  <el-input 
                    v-model="registerForm.studentId"
                    placeholder="学号"
                    size="large"
                    class="modern-input"
                  />
                </el-form-item>
              </div>
              <div class="input-group half">
                <label class="input-label">年级</label>
                <el-form-item prop="grade">
                  <el-input 
                    v-model="registerForm.grade"
                    placeholder="如：2024级"
                    size="large"
                    class="modern-input"
                  />
                </el-form-item>
              </div>
            </div>
            
            <div class="input-row">
              <div class="input-group half">
                <label class="input-label">学院</label>
                <el-form-item prop="department">
                  <el-input 
                    v-model="registerForm.department"
                    placeholder="学院名称"
                    size="large"
                    class="modern-input"
                  />
                </el-form-item>
              </div>
              <div class="input-group half">
                <label class="input-label">专业</label>
                <el-form-item prop="major">
                  <el-input 
                    v-model="registerForm.major"
                    placeholder="专业名称"
                    size="large"
                    class="modern-input"
                  />
                </el-form-item>
              </div>
            </div>
          </div>

          <!-- 验证码 -->
          <div class="input-group animate-fade-in-up delay-400">
            <label class="input-label">邮箱验证码</label>
            <div class="verification-container">
              <el-form-item prop="code" style="flex: 1;">
                <el-input 
                  v-model="registerForm.code"
                  placeholder="请输入验证码"
                  size="large"
                  class="modern-input"
                >
                  <template #prefix>
                    <el-icon class="input-icon"><Key /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-button 
                type="primary" 
                size="large"
                :disabled="codeLoading || countdown > 0"
                @click="sendCode"
                class="code-button"
              >
                {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
              </el-button>
            </div>
          </div>
          
          <!-- 用户协议 -->
          <div class="agreement animate-fade-in-up delay-450">
            <el-checkbox v-model="agreeTerms" class="agreement-checkbox">
              我已阅读并同意
              <a href="#" class="agreement-link">用户协议</a>
              和
              <a href="#" class="agreement-link">隐私政策</a>
            </el-checkbox>
          </div>
          
          <!-- 注册按钮 -->
          <div class="register-actions animate-fade-in-up delay-500">
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              :disabled="!agreeTerms"
              @click="handleRegister"
              class="register-button animate-glow"
            >
              <template v-if="!loading">
                <el-icon><Plus /></el-icon>
                立即注册
              </template>
              <template v-else>
                注册中...
              </template>
            </el-button>
          </div>
        </el-form>
        
        <!-- 登录链接 -->
        <div class="login-link animate-fade-in-up delay-600">
          <span>已有账号？</span>
          <router-link to="/login" class="login-button">立即登录</router-link>
        </div>
      </div>
    </div>
    
    <!-- 装饰元素 -->
    <div class="decoration-elements">
      <div class="grid-pattern"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { User, UserFilled, Message, Lock, Key, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getEmailCode } from '@/api/auth'
import type { RegisterRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const codeLoading = ref(false)
const countdown = ref(0)
const agreeTerms = ref(false)

const registerForm = reactive<RegisterRequest & { confirmPassword: string }>({
  username: '',
  email: '',
  password: '',
  nickname: '',
  studentId: '',
  department: '',
  major: '',
  grade: '',
  code: '',
  confirmPassword: ''
})

// 自定义验证规则
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2到20个字符之间', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入学院', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 发送验证码
const sendCode = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }

  try {
    codeLoading.value = true
    await getEmailCode(registerForm.email)
    ElMessage.success('验证码已发送，请查收邮件')
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    ElMessage.error(error.message || '发送验证码失败')
  } finally {
    codeLoading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    if (!agreeTerms.value) {
      ElMessage.warning('请先同意用户协议和隐私政策')
      return
    }
    
    loading.value = true
    
    // 构造注册请求数据，排除confirmPassword字段
    const registerData: RegisterRequest = {
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      nickname: registerForm.nickname,
      studentId: registerForm.studentId,
      department: registerForm.department,
      major: registerForm.major,
      grade: registerForm.grade,
      code: registerForm.code
    }
    
    await userStore.userRegister(registerData)
    ElMessage.success('注册成功！欢迎加入UniLife 🎉')
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败，请检查信息后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: var(--gradient-bg);
  padding: 20px;
}

/* 背景装饰 - 与登录页面一致 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  background: linear-gradient(135deg, var(--primary-200), var(--primary-300));
  border-radius: 50%;
  animation: float 12s ease-in-out infinite;
  opacity: 0.6;
}

.shape-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation-delay: 4s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 8s;
}

.shape-4 {
  width: 60px;
  height: 60px;
  top: 30%;
  right: 40%;
  animation-delay: 2s;
}

/* 注册卡片 - 使用淡紫色主题 */
.register-card {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 480px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: var(--shadow-xl);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid var(--gray-200);
  max-height: 90vh;
  overflow-y: auto;
}

/* 注册头部 */
.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-container {
  margin-bottom: 20px;
}

.logo-circle {
  width: 80px;
  height: 80px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 36px;
  color: white;
  box-shadow: var(--shadow-purple);
}

.title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: -0.02em;
}

.subtitle {
  color: var(--gray-600);
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

/* 表单样式 */
.form-container {
  width: 100%;
}

.input-group {
  margin-bottom: 20px;
}

.input-group.half {
  flex: 1;
  margin-bottom: 20px;
}

.input-row {
  display: flex;
  gap: 16px;
}

.input-label {
  display: block;
  color: var(--gray-700);
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.input-icon {
  color: var(--gray-400);
}

/* 学生信息区域 */
.student-info {
  background: var(--primary-50);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  border: 1px solid var(--primary-200);
}

/* 验证码容器 */
.verification-container {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.code-button {
  min-width: 120px;
  height: 40px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  background: var(--gradient-primary);
  color: white;
  border: none;
  box-shadow: var(--shadow-light);
  transition: var(--transition-base);
}

.code-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: var(--shadow-purple);
}

.code-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 用户协议 */
.agreement {
  margin-bottom: 24px;
  text-align: center;
}

.agreement-checkbox {
  color: var(--gray-600);
  font-size: 14px;
}

.agreement-link {
  color: var(--primary-600);
  text-decoration: none;
  font-weight: 600;
  transition: var(--transition-fast);
}

.agreement-link:hover {
  color: var(--primary-700);
  text-decoration: underline;
}

/* 注册按钮 */
.register-actions {
  margin-bottom: 32px;
}

.register-button {
  width: 100%;
  height: 52px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: var(--transition-base);
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 登录链接 */
.login-link {
  text-align: center;
  color: var(--gray-600);
  font-size: 14px;
}

.login-button {
  color: var(--primary-600);
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: var(--transition-fast);
}

.login-button:hover {
  color: var(--primary-700);
  text-decoration: underline;
}

/* 装饰元素 */
.decoration-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 2;
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 1px 1px, rgba(255, 255, 255, 0.15) 1px, transparent 0);
  background-size: 40px 40px;
  opacity: 0.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    max-width: 100%;
    padding: 32px 24px;
    margin: 16px;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .input-row {
    flex-direction: column;
    gap: 0;
  }
  
  .verification-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .code-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .student-info {
    padding: 16px;
  }
}

/* Element Plus组件样式覆盖 */
:deep(.el-form-item__content) {
  flex-direction: column;
  align-items: stretch;
}

:deep(.el-form-item__error) {
  color: var(--error);
  font-size: 12px;
  margin-top: 4px;
}

:deep(.el-checkbox__label) {
  color: var(--gray-600) !important;
  line-height: 1.4;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--primary-500) !important;
  border-color: var(--primary-500) !important;
}

/* 滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-track {
  background: var(--gray-100);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb {
  background: var(--primary-400);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb:hover {
  background: var(--primary-500);
}
</style> 