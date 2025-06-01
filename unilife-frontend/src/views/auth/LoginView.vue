<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="geometric-pattern"></div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="login-card glass-light animate-fade-in">
      <!-- Logo和标题 -->
      <div class="login-header">
        <div class="logo-container animate-float">
          <div class="logo-circle">
            <i class="el-icon-star-filled"></i>
          </div>
        </div>
        <h1 class="title gradient-text">UniLife</h1>
        <p class="subtitle">欢迎回到你的大学生活平台</p>
      </div>
      
      <!-- 登录表单 -->
      <div class="login-form">
        <el-form 
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          @submit.prevent="handleLogin"
          class="form-container"
        >
          <!-- 账号输入 -->
          <div class="input-group animate-fade-in-up delay-100">
            <label class="input-label">账号</label>
            <el-form-item prop="account">
              <el-input 
                v-model="loginForm.account"
                placeholder="请输入用户名或邮箱地址"
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
          <div class="input-group animate-fade-in-up delay-200">
            <label class="input-label">密码</label>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password"
                placeholder="请输入您的密码"
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
          
          <!-- 记住我和忘记密码 -->
          <div class="form-options animate-fade-in-up delay-300">
            <el-checkbox v-model="rememberMe" class="remember-me">
              记住登录状态
            </el-checkbox>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>
          
          <!-- 登录按钮 -->
          <div class="login-actions animate-fade-in-up delay-400">
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading"
              @click="handleLogin"
              class="login-button animate-glow"
            >
              <template v-if="!loading">
                <el-icon><Right /></el-icon>
                立即登录
              </template>
              <template v-else>
                登录中...
              </template>
            </el-button>
          </div>
        </el-form>
        
        <!-- 分隔线 -->
        <div class="divider animate-fade-in-up delay-500">
          <span class="divider-text">或者</span>
        </div>
        
        <!-- 第三方登录 -->
        <div class="social-login animate-fade-in-up delay-600">
          <button class="social-button github">
            <i class="fab fa-github"></i>
            GitHub
          </button>
          <button class="social-button google">
            <i class="fab fa-google"></i>
            Google
          </button>
        </div>
        
        <!-- 注册链接 -->
        <div class="signup-link animate-fade-in-up delay-700">
          <span>还没有账号？</span>
          <router-link to="/register" class="signup-button">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { Message, Lock, Right } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import type { LoginRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive<LoginRequest>({
  account: '',
  password: ''
})

const loginRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, message: '账号长度不能少于3位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    await userStore.userLogin(loginForm)
    ElMessage.success('登录成功！欢迎回来 🎉')
    router.push('/forum')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败，请检查账号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: var(--gradient-bg);
  padding: 20px;
}

/* 背景装饰 */
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
  animation: float 10s ease-in-out infinite;
  opacity: 0.6;
}

.shape-1 {
  width: 150px;
  height: 150px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 100px;
  height: 100px;
  top: 60%;
  right: 15%;
  animation-delay: 3s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 6s;
}

.geometric-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 25% 25%, rgba(184, 169, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(231, 227, 255, 0.1) 0%, transparent 50%);
  opacity: 0.8;
}

/* 登录卡片 */
.login-card {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--gray-200);
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
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
  margin-bottom: 24px;
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.remember-me {
  color: var(--gray-600);
}

.forgot-password {
  color: var(--primary-600);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: var(--transition-fast);
}

.forgot-password:hover {
  color: var(--primary-700);
  text-decoration: underline;
}

/* 登录按钮 */
.login-actions {
  margin-bottom: 32px;
}

.login-button {
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

.login-button:hover {
  transform: translateY(-2px);
}

/* 分隔线 */
.divider {
  position: relative;
  text-align: center;
  margin: 32px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: var(--gray-200);
}

.divider-text {
  background: white;
  color: var(--gray-500);
  padding: 0 16px;
  font-size: 14px;
  position: relative;
  z-index: 1;
}

/* 第三方登录 */
.social-login {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.social-button {
  flex: 1;
  height: 48px;
  background: white;
  border: 2px solid var(--gray-200);
  border-radius: 12px;
  color: var(--gray-700);
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: var(--transition-base);
  box-shadow: var(--shadow-sm);
}

.social-button:hover {
  background: var(--gray-50);
  border-color: var(--primary-300);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.social-button.github:hover {
  border-color: #333;
  color: #333;
}

.social-button.google:hover {
  border-color: #4285f4;
  color: #4285f4;
}

/* 注册链接 */
.signup-link {
  text-align: center;
  color: var(--gray-600);
  font-size: 14px;
}

.signup-button {
  color: var(--primary-600);
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: var(--transition-fast);
}

.signup-button:hover {
  color: var(--primary-700);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
    margin: 16px;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .social-login {
    flex-direction: column;
  }
  
  .form-options {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
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
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--primary-500) !important;
  border-color: var(--primary-500) !important;
}
</style> 