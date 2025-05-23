<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElButton, ElInput, ElForm, ElFormItem, ElIcon, ElDivider, ElCard } from 'element-plus';
import { User, Lock, View, Hide, UserFilled, Message } from '@element-plus/icons-vue';
import { useUserStore } from '../stores';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 表单状态
const isLogin = ref(true);
const showPassword = ref(false);
const loading = ref(false);

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
});

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: ''
});

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
};

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 10, message: '昵称长度为2-10个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
    } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

// 表单引用
const loginFormRef = ref();
const registerFormRef = ref();

// 计算属性
const formTitle = computed(() => isLogin.value ? '欢迎回来' : '加入我们');
const submitText = computed(() => isLogin.value ? '登录' : '注册');
const switchText = computed(() => isLogin.value ? '还没有账号？点击注册' : '已有账号？点击登录');

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value;
  // 清空表单
  if (loginFormRef.value) loginFormRef.value.resetFields();
  if (registerFormRef.value) registerFormRef.value.resetFields();
};

// 切换密码显示
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  const valid = await loginFormRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    await userStore.login(loginForm.username, loginForm.password);
    ElMessage.success('登录成功！');
    
    // 重定向到原来要访问的页面或默认页面
    const redirect = route.query.redirect as string;
    router.push(redirect || '/home');
  } catch (error: any) {
    ElMessage.error(error?.message || '登录失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return;
  
  const valid = await registerFormRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    // 由于当前register方法只接受email、password、code三个参数，先使用email作为用户名
    await userStore.register(
      registerForm.email,
      registerForm.password,
      '' // 验证码参数，暂时留空
    );
    ElMessage.success('注册成功！');
    
    // 注册成功后自动切换到登录模式
    isLogin.value = true;
    loginForm.username = registerForm.username;
  } catch (error: any) {
    ElMessage.error(error?.message || '注册失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 处理表单提交
const handleSubmit = () => {
  if (isLogin.value) {
    handleLogin();
  } else {
    handleRegister();
  }
};

// 快速登录演示
const quickLogin = () => {
  loginForm.username = 'demo';
  loginForm.password = '123456';
  ElMessage.info('已填入演示账号，点击登录即可体验');
};

onMounted(() => {
  // 如果已经登录，直接跳转
  if (userStore.isLoggedIn) {
    router.push('/home');
  }
});
</script>

<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="decoration-circle decoration-circle--1"></div>
      <div class="decoration-circle decoration-circle--2"></div>
      <div class="decoration-circle decoration-circle--3"></div>
      <div class="decoration-grid"></div>
    </div>

    <!-- 主内容 -->
    <div class="login-container">
      <!-- 左侧信息区 -->
      <div class="info-section">
        <div class="info-content">
          <div class="logo">
            <div class="logo-icon">
              <span class="logo-text">UL</span>
            </div>
            <h1 class="brand-name">UniLife</h1>
          </div>
          
          <div class="info-text">
            <h2 class="info-title">连接校园，分享生活</h2>
            <p class="info-description">
              加入UniLife社区，与同学们一起交流学习经验，分享校园生活，共同成长进步。
            </p>
          </div>
          
          <div class="features">
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="feature-text">
                <h3>学术交流</h3>
                <p>与同学讨论学术问题，分享学习心得</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="feature-text">
                <h3>校园动态</h3>
                <p>获取最新的校园资讯和活动信息</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-section">
        <el-card class="form-card" shadow="hover">
          <!-- 表单头部 -->
          <div class="form-header">
            <h2 class="form-title">{{ formTitle }}</h2>
            <p class="form-subtitle">
              {{ isLogin ? '登录您的账号继续使用' : '创建新账号开始您的校园之旅' }}
            </p>
          </div>

          <!-- 登录表单 -->
          <el-form
            v-if="isLogin"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="auth-form"
            size="large"
            @submit.prevent="handleSubmit"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                clearable
                class="form-input"
                @keyup.enter="handleSubmit"
              >
                <template #suffix>
                  <el-icon @click="togglePasswordVisibility" class="password-toggle">
                    <View v-if="showPassword" />
                    <Hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-button
              type="primary"
              :loading="loading"
              @click="handleSubmit"
              class="submit-btn"
              size="large"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-else
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="auth-form"
            size="large"
            @submit.prevent="handleSubmit"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                :prefix-icon="UserFilled"
                clearable
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                :prefix-icon="Message"
                clearable
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                clearable
                  class="form-input"
              >
                <template #suffix>
                  <el-icon @click="togglePasswordVisibility" class="password-toggle">
                    <View v-if="showPassword" />
                    <Hide v-else />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                clearable
                class="form-input"
                @keyup.enter="handleSubmit"
              />
            </el-form-item>

            <el-button
              type="primary"
              :loading="loading"
              @click="handleSubmit"
              class="submit-btn"
              size="large"
            >
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form>

          <!-- 分割线 -->
          <el-divider class="form-divider">
            <span class="divider-text">或者</span>
          </el-divider>

          <!-- 快速登录/切换模式 -->
          <div class="form-footer">
            <el-button
              v-if="isLogin"
              @click="quickLogin"
              class="quick-login-btn"
              size="large"
              plain
            >
              演示账号快速登录
            </el-button>
            
            <el-button
              @click="toggleMode"
              class="switch-mode-btn"
              type="primary"
              link
              size="large"
            >
              {{ switchText }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-50) 0%, var(--primary-100) 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  background: linear-gradient(135deg, var(--primary-300), var(--primary-200));
  border-radius: var(--radius-full);
  opacity: 0.3;
}

.decoration-circle--1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation: float 8s ease-in-out infinite;
}

.decoration-circle--2 {
  width: 150px;
  height: 150px;
  bottom: 15%;
  right: 15%;
  animation: float 6s ease-in-out infinite reverse;
}

.decoration-circle--3 {
  width: 100px;
  height: 100px;
  top: 60%;
  left: 5%;
  animation: float 10s ease-in-out infinite;
}

.decoration-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(139, 77, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 77, 255, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

/* 主容器 */
.login-container {
  display: flex;
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
  padding: var(--space-6);
  gap: var(--space-8);
  position: relative;
  z-index: 2;
}

/* 信息区域 */
.info-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: var(--radius-2xl);
  margin-right: var(--space-4);
  box-shadow: var(--shadow-lg);
}

.info-content {
  max-width: 400px;
  color: var(--text-primary);
  padding: var(--space-8);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-8);
}

.logo-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-400));
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-xl);
}

.logo-text {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: white;
}

.brand-name {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0;
  background: linear-gradient(135deg, var(--primary-600), var(--primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.info-text {
  margin-bottom: var(--space-8);
}

.info-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--space-4) 0;
  color: var(--text-primary);
}

.info-description {
  font-size: var(--font-size-lg);
  line-height: var(--line-height-relaxed);
  margin: 0;
  color: var(--text-secondary);
}

.features {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  background: var(--primary-50);
  transition: all var(--duration-200) var(--ease-out);
}

.feature-item:hover {
  background: var(--primary-100);
  transform: translateX(4px);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-400));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-xl);
  color: white;
  box-shadow: var(--shadow-sm);
}

.feature-text h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin: 0 0 var(--space-1) 0;
  color: var(--text-primary);
}

.feature-text p {
  font-size: var(--font-size-sm);
  margin: 0;
  color: var(--text-secondary);
}

/* 表单区域 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-card {
  width: 100%;
  max-width: 400px;
  border-radius: var(--radius-2xl);
  border: none;
  box-shadow: var(--shadow-2xl);
  overflow: hidden;
}

.form-card :deep(.el-card__body) {
  padding: var(--space-8);
}

.form-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.form-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.form-subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: var(--font-size-sm);
}

/* 表单样式 */
.auth-form {
  margin-bottom: var(--space-6);
}

.form-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-lg);
  padding: var(--space-3) var(--space-4);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: all var(--duration-150) var(--ease-out);
}

.form-input :deep(.el-input__wrapper:hover) {
  box-shadow: var(--shadow-md);
  border-color: var(--primary-300);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(139, 77, 255, 0.1), var(--shadow-md);
  border-color: var(--primary-500);
}

.password-toggle {
  cursor: pointer;
  color: var(--text-light);
  transition: color var(--duration-150) var(--ease-out);
}

.password-toggle:hover {
  color: var(--primary-500);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-lg);
  font-weight: var(--font-weight-medium);
  margin-top: var(--space-4);
  box-shadow: var(--shadow-primary);
  transition: all var(--duration-150) var(--ease-out);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-primary-lg);
}

/* 分割线 */
.form-divider {
  margin: var(--space-6) 0;
}

.divider-text {
  color: var(--text-light);
  font-size: var(--font-size-sm);
  padding: 0 var(--space-3);
  background: var(--bg-elevated);
}

/* 表单底部 */
.form-footer {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  align-items: center;
}

.quick-login-btn {
  width: 100%;
  height: 44px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  transition: all var(--duration-150) var(--ease-out);
}

.quick-login-btn:hover {
  border-color: var(--primary-300);
  color: var(--primary-600);
  background: var(--primary-50);
}

.switch-mode-btn {
  font-size: var(--font-size-sm);
  transition: all var(--duration-150) var(--ease-out);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    padding: var(--space-4);
    gap: var(--space-6);
  }
  
  .info-section {
    margin-right: 0;
    margin-bottom: var(--space-4);
  }
  
  .info-content {
    padding: var(--space-6);
    text-align: center;
  }
  
  .logo {
    justify-content: center;
  }
  
  .features {
    gap: var(--space-4);
  }
  
  .feature-item {
    padding: var(--space-3);
  }
  
  .form-card {
    max-width: none;
  }
  
  .form-card :deep(.el-card__body) {
    padding: var(--space-6);
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: var(--space-4);
  }
  
  .info-content {
    padding: var(--space-4);
  }
  
  .features {
    flex-direction: column;
    gap: var(--space-3);
  }
  
  .feature-item {
    flex-direction: column;
    text-align: center;
    gap: var(--space-2);
  }
  
  .form-card :deep(.el-card__body) {
    padding: var(--space-4);
  }
  
  .form-header {
    margin-bottom: var(--space-6);
  }
  
  .form-title {
    font-size: var(--font-size-xl);
  }
}
</style>