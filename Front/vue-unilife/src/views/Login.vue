<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores';
import { useEmailCode } from '../hooks/useEmailCode';

const router = useRouter();
const userStore = useUserStore();
const { sendEmailCode, countdown } = useEmailCode();

// 表单模式
const formMode = ref<'login-password' | 'login-code' | 'register'>('login-password');

const loginEmail = ref('');
const loginPassword = ref('');
const loginCodeEmail = ref('');
const loginCode = ref('');
const registerEmail = ref('');
const registerPassword = ref('');
const confirmPassword = ref('');
const registerCode = ref('');

// 切换表单模式
const switchMode = (mode: 'login-password' | 'login-code' | 'register') => {
  formMode.value = mode;
  resetForms(); // 切换时重置表单
};

// 重置所有表单 - 修改为重置 ref
const resetForms = () => {
  loginEmail.value = '';
  loginPassword.value = '';
  loginCodeEmail.value = '';
  loginCode.value = '';
  registerEmail.value = '';
  registerPassword.value = '';
  confirmPassword.value = '';
  registerCode.value = '';
};


// 处理密码登录
const handlePasswordLogin = async () => {
  try {
    // 获取 store action 的执行结果 (true/false)
    const success = await userStore.login(loginEmail.value, loginPassword.value);

    if (success) {
      // 只有当 store 返回 true 时才视为成功
      ElMessage.success('登录成功'); // 统一在组件中提示
      // 获取重定向URL，如果有的话跳转到这个URL，否则跳转到论坛首页
      const redirectUrl = router.currentRoute.value.query.redirect as string || '/';
      router.push(redirectUrl);
    } else {
    }
  } catch (error: any) {
    console.error("登录组件捕获异常:", error);
    // 错误消息也应由拦截器处理，这里是最后防线
    ElMessage.error(error.message || '登录请求处理异常');
  }
};

// 处理验证码登录
const handleCodeLogin = async () => {
  try {
    // 获取 store action 的执行结果 (true/false)
    const success = await userStore.loginWithCode(loginCodeEmail.value, loginCode.value);

    if (success) {
      ElMessage.success('登录成功'); // 统一在组件中提示
      // 获取重定向URL，如果有的话跳转到这个URL，否则跳转到论坛首页
      const redirectUrl = router.currentRoute.value.query.redirect as string || '/';
      router.push(redirectUrl);
    } else {
      // 依赖拦截器或 store 显示具体失败信息
      // ElMessage.error('验证码错误或登录失败');
    }
  } catch (error: any) {
    console.error("验证码登录组件捕获异常:", error);
    ElMessage.error(error.message || '验证码登录处理异常');
  }
};

// 处理注册
const handleRegister = async () => {
  try {
    // 获取 store action 的执行结果 (true/false)
    const success = await userStore.register(
        registerEmail.value,
        registerPassword.value,
        registerCode.value
    );

    if (success) {
      ElMessage.success('注册成功，已自动登录'); // 统一在组件中提示
      // 注册成功后跳转到论坛首页
      router.push('/');
    } else {
    }
  } catch (error: any) {
    console.error("注册组件捕获异常:", error);
    ElMessage.error(error.message || '注册请求处理异常');
  }
};

// 发送验证码 - 修改参数类型为 string，因为模板中传递 .value
const handleSendCode = async (email: string) => {
  if (!email) { // 仍然保留基本的空值检查
    ElMessage.warning('请先输入邮箱');
    return;
  }

  try {
    await sendEmailCode(email);
    // ElMessage.success('验证码已发送'); // 成功提示可能由拦截器或 sendEmailCode 内部处理
  } catch (error: any) {
    console.error("发送验证码失败:", error);
  }
};

const isLoginPasswordActive = computed(() => formMode.value === 'login-password');
const isLoginCodeActive = computed(() => formMode.value === 'login-code');
const isRegisterActive = computed(() => formMode.value === 'register');

const codeButtonText = computed(() => {
  return countdown.value > 0 ? `${countdown.value}秒后重新获取` : '获取验证码';
});
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="form-container">
        <div class="form-header">
          <h1>{{ isRegisterActive ? '注册账号' : '欢迎回来' }}</h1>
          <p>{{ isRegisterActive ? '创建你的UniLife账号' : '登录你的UniLife账号' }}</p>
        </div>

        <form v-if="isLoginPasswordActive" @submit.prevent="handlePasswordLogin" class="login-form">
          <div class="form-group">
            <label for="email">邮箱</label>
            <input
                id="email"
                type="email"
                v-model="loginEmail" placeholder="请输入邮箱"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input
                id="password"
                type="password"
                v-model="loginPassword" placeholder="请输入密码"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary">登录</button>
            <div class="form-links">
              <a href="#" @click.prevent="switchMode('login-code')">验证码登录</a>
              <a href="#" @click.prevent="switchMode('register')">注册账号</a>
            </div>
          </div>
        </form>

        <form v-if="isLoginCodeActive" @submit.prevent="handleCodeLogin" class="login-form">
          <div class="form-group">
            <label for="codeEmail">邮箱</label>
            <input
                id="codeEmail"
                type="email"
                v-model="loginCodeEmail" placeholder="请输入邮箱"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-group">
            <label for="code">验证码</label>
            <div class="code-input-group">
              <input
                  id="code"
                  type="text"
                  v-model="loginCode" placeholder="请输入验证码"
                  class="form-input"
              >
              <button
                  type="button"
                  class="code-btn"
                  @click="handleSendCode(loginCodeEmail)" :disabled="countdown > 0"
              >
                {{ codeButtonText }}
              </button>
            </div>
            <div class="error-message"></div> </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary">登录</button>
            <div class="form-links">
              <a href="#" @click.prevent="switchMode('login-password')">密码登录</a>
              <a href="#" @click.prevent="switchMode('register')">注册账号</a>
            </div>
          </div>
        </form>

        <form v-if="isRegisterActive" @submit.prevent="handleRegister" class="login-form">
          <div class="form-group">
            <label for="registerEmail">邮箱</label>
            <input
                id="registerEmail"
                type="email"
                v-model="registerEmail" placeholder="请输入邮箱"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-group">
            <label for="registerPassword">密码</label>
            <input
                id="registerPassword"
                type="password"
                v-model="registerPassword" placeholder="请输入密码"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input
                id="confirmPassword"
                type="password"
                v-model="confirmPassword" placeholder="请再次输入密码"
                class="form-input"
            >
            <div class="error-message"></div> </div>

          <div class="form-group">
            <label for="registerCode">验证码</label>
            <div class="code-input-group">
              <input
                  id="registerCode"
                  type="text"
                  v-model="registerCode" placeholder="请输入验证码"
                  class="form-input"
              >
              <button
                  type="button"
                  class="code-btn"
                  @click="handleSendCode(registerEmail)" :disabled="countdown > 0"
              >
                {{ codeButtonText }}
              </button>
            </div>
            <div class="error-message"></div> </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary">注册</button>
            <div class="form-links">
              <a href="#" @click.prevent="switchMode('login-password')">已有账号？去登录</a>
            </div>
          </div>
        </form>
      </div>

      <div class="info-container">
        <div class="info-content">
          <h2>欢迎来到 <span>UniLife</span> 学生论坛</h2>
          <p>这是一个专属于大学生的论坛，你可以在这里发表自己的观点，也可以在这里找到志同道合的朋友。</p>
          <div class="info-image">
            <img src="/images/LogPage1.jpg" alt="UniLife">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--bg-gradient); /* 保留 CSS 变量 */
  padding: var(--spacing-lg); /* 保留 CSS 变量 */
}

.login-container {
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  display: flex;
  border-radius: var(--border-radius-lg); /* 保留 CSS 变量 */
  overflow: hidden;
  box-shadow: var(--shadow-lg); /* 保留 CSS 变量 */
  background-color: var(--bg-primary); /* 保留 CSS 变量 */
}

.form-container {
  flex: 1;
  padding: var(--spacing-xl); /* 保留 CSS 变量 */
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: var(--spacing-xl); /* 保留 CSS 变量 */
  text-align: center;
}

.form-header h1 {
  color: var(--primary-color); /* 保留 CSS 变量 */
  font-size: var(--font-size-xxl); /* 保留 CSS 变量 */
  margin-bottom: var(--spacing-sm); /* 保留 CSS 变量 */
}

.form-header p {
  color: var(--text-secondary); /* 保留 CSS 变量 */
}

.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: var(--spacing-lg); /* 保留 CSS 变量 */
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm); /* 保留 CSS 变量 */
  color: var(--text-secondary); /* 保留 CSS 变量 */
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: var(--spacing-md); /* 保留 CSS 变量 */
  border: 2px solid var(--border-color); /* 保留 CSS 变量 */
  border-radius: var(--border-radius-md); /* 保留 CSS 变量 */
  font-size: var(--font-size-md); /* 保留 CSS 变量 */
  transition: border-color var(--transition-normal); /* 保留 CSS 变量 */
  box-sizing: border-box; /* 添加这个防止 padding 影响总宽度 */
}

.form-input:focus {
  border-color: var(--primary-light); /* 保留 CSS 变量 */
  outline: none;
}

.code-input-group {
  display: flex;
  gap: var(--spacing-md); /* 保留 CSS 变量 */
}

.code-input-group .form-input {
  flex: 1;
}

.code-btn {
  padding: var(--spacing-sm) var(--spacing-md); /* 保留 CSS 变量 */
  background-color: var(--primary-color); /* 保留 CSS 变量 */
  color: white;
  border: none;
  border-radius: var(--border-radius-md); /* 保留 CSS 变量 */
  cursor: pointer;
  transition: background-color var(--transition-normal); /* 保留 CSS 变量 */
  white-space: nowrap;
}

.code-btn:hover:not(:disabled) {
  background-color: var(--primary-dark); /* 保留 CSS 变量 */
}

.code-btn:disabled {
  background-color: var(--text-light); /* 保留 CSS 变量 */
  cursor: not-allowed;
}

.error-message {
  /* color: #ff4d4f; */ /* 不再需要特定颜色 */
  font-size: var(--font-size-sm); /* 保留 CSS 变量 */
  margin-top: var(--spacing-xs); /* 保留 CSS 变量 */
  min-height: 20px; /* 保留最小高度防止布局跳动 */
}

.form-actions {
  margin-top: var(--spacing-lg); /* 保留 CSS 变量 */
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-actions .btn {
  width: 100%;
  padding: var(--spacing-md); /* 保留 CSS 变量 */
  font-size: var(--font-size-lg); /* 保留 CSS 变量 */
  /* 假设按钮样式由 .btn 和 .btn-primary 类定义 */
}
/* 你可能需要确保 .btn 和 .btn-primary 有定义，例如：*/
.btn {
  cursor: pointer;
  border-radius: var(--border-radius-md);
  transition: background-color var(--transition-normal), color var(--transition-normal), border-color var(--transition-normal);
  border: 1px solid transparent;
}
.btn-primary {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
.btn-primary:hover {
  background-color: var(--primary-dark);
  border-color: var(--primary-dark);
}


.form-links {
  margin-top: var(--spacing-lg); /* 保留 CSS 变量 */
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.form-links a {
  color: var(--primary-color); /* 保留 CSS 变量 */
  text-decoration: none;
  transition: color var(--transition-normal); /* 保留 CSS 变量 */
}

.form-links a:hover {
  color: var(--primary-dark); /* 保留 CSS 变量 */
  text-decoration: underline;
}

.info-container {
  flex: 1;
  background-color: var(--primary-light); /* 保留 CSS 变量 */
  padding: var(--spacing-xl); /* 保留 CSS 变量 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.info-content {
  text-align: center;
  z-index: 1;
}

.info-content h2 {
  font-size: var(--font-size-xl); /* 保留 CSS 变量 */
  margin-bottom: var(--spacing-lg); /* 保留 CSS 变量 */
}

.info-content span {
  font-weight: 700;
}

.info-content p {
  margin-bottom: var(--spacing-xl); /* 保留 CSS 变量 */
  line-height: 1.6;
}

.info-image {
  width: 80%;
  max-width: 300px;
  margin: 0 auto;
  border-radius: var(--border-radius-lg); /* 保留 CSS 变量 */
  overflow: hidden;
  box-shadow: var(--shadow-md); /* 保留 CSS 变量 */
}

.info-image img {
  width: 100%;
  height: auto;
  display: block; /* 移除图片底部空隙 */
  object-fit: cover;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    min-height: auto; /* 调整高度 */
  }

  .info-container {
    display: none; /* 在小屏幕上隐藏右侧信息 */
  }
  .form-container {
    padding: var(--spacing-lg); /* 使用变量 */
  }
  .form-header h1 {
    font-size: var(--font-size-xl); /* 使用变量 */
  }
}
</style>