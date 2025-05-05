<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useForm } from 'vee-validate';
import * as yup from 'yup';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores';
import { useEmailCode } from '../hooks/useEmailCode';

const userStore = useUserStore();
const { sendEmailCode, countdown } = useEmailCode();

// 表单状态
const isProfileEditable = ref(false);
const isPasswordEditable = ref(false);

// 用户资料表单
const profileForm = reactive({
  username: '',
  gender: 0,
  bio: '',
  birthday: ''
});

// 密码表单
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: '',
  code: ''
});

// 头像相关
const avatarUrl = ref('');
const isAvatarHovered = ref(false);
const isAvatarDialogVisible = ref(false);
const avatarPreviewUrl = ref('');
const selectedAvatarFile = ref<File | null>(null);

// 性别选项
const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 },
  { label: '保密', value: 0 }
];

// 表单验证
const profileValidationSchema = yup.object({
  username: yup.string().required('用户名不能为空'),
  bio: yup.string()
});

const passwordValidationSchema = yup.object({
  newPassword: yup.string().required('新密码不能为空').min(6, '密码至少6位'),
  confirmPassword: yup.string()
    .required('确认密码不能为空')
    .oneOf([yup.ref('newPassword')], '两次密码不一致'),
  code: yup.string().required('验证码不能为空')
});

const { handleSubmit: handleProfileSubmit } = useForm({
  validationSchema: profileValidationSchema,
  initialValues: profileForm
});

const { handleSubmit: handlePasswordSubmit } = useForm({
  validationSchema: passwordValidationSchema,
  initialValues: passwordForm
});

// 获取用户信息
const fetchUserInfo = async () => {
  const userInfo = await userStore.fetchUserInfo();
  
  if (userInfo) {
    profileForm.username = userInfo.username || '';
    profileForm.gender = userInfo.gender || 0;
    profileForm.bio = userInfo.bio || '';
    profileForm.birthday = userInfo.birthday || '';
    avatarUrl.value = userInfo.avatar || '/images/默认头像.jpg';
  }
};

// 切换个人资料编辑状态
const toggleProfileEdit = () => {
  if (isProfileEditable.value) {
    // 取消编辑，恢复原始数据
    const userInfo = userStore.userInfo;
    if (userInfo) {
      profileForm.username = userInfo.username || '';
      profileForm.gender = userInfo.gender || 0;
      profileForm.bio = userInfo.bio || '';
      profileForm.birthday = userInfo.birthday || '';
    }
  }
  
  isProfileEditable.value = !isProfileEditable.value;
};

// 切换密码编辑状态
const togglePasswordEdit = () => {
  if (isPasswordEditable.value) {
    // 取消编辑，清空表单
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    passwordForm.code = '';
  }
  
  isPasswordEditable.value = !isPasswordEditable.value;
};

// 提交个人资料
const submitProfile = handleProfileSubmit(async (values) => {
  const success = await userStore.updateProfile({
    username: values.username,
    bio: values.bio,
    gender: values.gender,
    birthday: values.birthday
  });
  
  if (success) {
    isProfileEditable.value = false;
  }
});

// 提交密码修改
const submitPassword = handlePasswordSubmit(async (values) => {
  const success = await userStore.updatePassword(
    values.code,
    values.newPassword
  );
  
  if (success) {
    isPasswordEditable.value = false;
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    passwordForm.code = '';
  }
});

// 发送验证码
const handleSendCode = async () => {
  if (!userStore.userInfo?.email) {
    ElMessage.warning('无法获取邮箱地址');
    return;
  }
  
  await sendEmailCode(userStore.userInfo.email);
};

// 头像相关方法
const handleAvatarHover = (hovered: boolean) => {
  isAvatarHovered.value = hovered;
};

const openAvatarDialog = () => {
  isAvatarDialogVisible.value = true;
};

const handleAvatarChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    selectedAvatarFile.value = file;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      avatarPreviewUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const uploadAvatar = async () => {
  if (!selectedAvatarFile.value) {
    ElMessage.warning('请先选择头像');
    return;
  }
  
  try {
    const formData = new FormData();
    formData.append('avatar', selectedAvatarFile.value);
    
    // 这里应该调用上传头像的API
    // const res = await userApi.uploadAvatar(formData);
    
    // 模拟上传成功
    ElMessage.success('头像上传成功');
    avatarUrl.value = avatarPreviewUrl.value;
    isAvatarDialogVisible.value = false;
    selectedAvatarFile.value = null;
    avatarPreviewUrl.value = '';
  } catch (error) {
    console.error('上传头像失败:', error);
    ElMessage.error('上传头像失败');
  }
};

// 计算验证码按钮文本
const codeButtonText = computed(() => {
  return countdown.value > 0 ? `${countdown.value}秒后重新获取` : '获取验证码';
});

onMounted(() => {
  fetchUserInfo();
});
</script>

<template>
  <div class="account-manager">
    <div class="page-header">
      <h1>账号管理</h1>
      <p>管理你的个人资料和账号信息</p>
    </div>
    
    <div class="account-container">
      <!-- 左侧个人资料 -->
      <div class="card profile-card">
        <div class="card-header">
          <h2>个人资料</h2>
          <button 
            class="btn" 
            :class="isProfileEditable ? 'btn-secondary' : 'btn-primary'"
            @click="toggleProfileEdit"
          >
            {{ isProfileEditable ? '取消' : '编辑' }}
          </button>
        </div>
        
        <div class="avatar-container">
          <div 
            class="avatar"
            @mouseenter="handleAvatarHover(true)"
            @mouseleave="handleAvatarHover(false)"
            @click="openAvatarDialog"
          >
            <img :src="avatarUrl" alt="用户头像">
            <div class="avatar-overlay" v-if="isAvatarHovered">
              <el-icon><upload-filled /></el-icon>
              <span>更换头像</span>
            </div>
          </div>
        </div>
        
        <form @submit.prevent="submitProfile">
          <div class="form-group">
            <label for="username">用户名</label>
            <input 
              id="username"
              type="text" 
              v-model="profileForm.username"
              :readonly="!isProfileEditable"
              class="form-input"
            >
          </div>
          
          <div class="form-group">
            <label>性别</label>
            <div class="radio-group">
              <label v-for="option in genderOptions" :key="option.value" class="radio-label">
                <input 
                  type="radio" 
                  :value="option.value" 
                  v-model="profileForm.gender"
                  :disabled="!isProfileEditable"
                >
                <span>{{ option.label }}</span>
              </label>
            </div>
          </div>
          
          <div class="form-group">
            <label for="birthday">生日</label>
            <input 
              id="birthday"
              type="date" 
              v-model="profileForm.birthday"
              :readonly="!isProfileEditable"
              class="form-input"
            >
          </div>
          
          <div class="form-group">
            <label for="bio">个人简介</label>
            <textarea 
              id="bio"
              v-model="profileForm.bio"
              :readonly="!isProfileEditable"
              class="form-input textarea"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-actions" v-if="isProfileEditable">
            <button type="submit" class="btn btn-primary">保存修改</button>
          </div>
        </form>
      </div>
      
      <!-- 右侧账号信息 -->
      <div class="card account-card">
        <div class="card-header">
          <h2>账号信息</h2>
        </div>
        
        <div class="form-group">
          <label>邮箱</label>
          <input 
            type="email" 
            :value="userStore.userInfo?.email || ''"
            readonly
            class="form-input"
          >
          <p class="form-hint">邮箱地址不可修改</p>
        </div>
        
        <div class="card-header password-header">
          <h3>密码设置</h3>
          <button 
            class="btn" 
            :class="isPasswordEditable ? 'btn-secondary' : 'btn-primary'"
            @click="togglePasswordEdit"
          >
            {{ isPasswordEditable ? '取消' : '修改密码' }}
          </button>
        </div>
        
        <form v-if="isPasswordEditable" @submit.prevent="submitPassword">
          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input 
              id="newPassword"
              type="password" 
              v-model="passwordForm.newPassword"
              class="form-input"
              placeholder="请输入新密码"
            >
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input 
              id="confirmPassword"
              type="password" 
              v-model="passwordForm.confirmPassword"
              class="form-input"
              placeholder="请再次输入新密码"
            >
          </div>
          
          <div class="form-group">
            <label for="code">验证码</label>
            <div class="code-input-group">
              <input 
                id="code"
                type="text" 
                v-model="passwordForm.code"
                class="form-input"
                placeholder="请输入验证码"
              >
              <button 
                type="button" 
                class="code-btn"
                @click="handleSendCode"
                :disabled="countdown > 0"
              >
                {{ codeButtonText }}
              </button>
            </div>
            <p class="form-hint">验证码将发送到您的邮箱</p>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">确认修改</button>
          </div>
        </form>
        
        <div class="account-security">
          <h3>账号安全</h3>
          <p>最后登录时间: 2023-06-15 14:30:25</p>
          <p>最后登录IP: 192.168.1.1</p>
        </div>
      </div>
    </div>
    
    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="isAvatarDialogVisible"
      title="更换头像"
      width="400px"
    >
      <div class="avatar-upload-container">
        <div class="avatar-preview-container">
          <img 
            v-if="avatarPreviewUrl" 
            :src="avatarPreviewUrl" 
            class="avatar-preview"
          >
          <div v-else class="avatar-placeholder">
            <el-icon><plus /></el-icon>
            <span>选择图片</span>
          </div>
        </div>
        
        <input 
          type="file" 
          accept="image/*" 
          @change="handleAvatarChange"
          class="avatar-input"
          id="avatar-input"
        >
        <label for="avatar-input" class="btn btn-primary">选择图片</label>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <button class="btn btn-secondary" @click="isAvatarDialogVisible = false">取消</button>
          <button class="btn btn-primary" @click="uploadAvatar" :disabled="!avatarPreviewUrl">上传</button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 装饰元素 -->
    <div class="decoration-element star-1">✨</div>
    <div class="decoration-element star-2">✨</div>
    <div class="decoration-element heart">💜</div>
    <div class="decoration-element cat">🐱</div>
  </div>
</template>

<style scoped>
.account-manager {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
}

.page-header {
  margin-bottom: var(--spacing-xl);
}

.page-header h1 {
  font-size: var(--font-size-xxl);
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
}

.page-header p {
  color: var(--text-secondary);
}

.account-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
}

.card {
  background-color: var(--bg-primary);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.card-header h2 {
  font-size: var(--font-size-xl);
  color: var(--primary-color);
  margin: 0;
}

.card-header h3 {
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  margin: 0;
}

.password-header {
  margin-top: var(--spacing-xl);
}

.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: var(--border-radius-full);
  overflow: hidden;
  position: relative;
  cursor: pointer;
  border: 3px solid var(--primary-light);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
}

.avatar-overlay .el-icon {
  font-size: 24px;
  margin-bottom: var(--spacing-xs);
}

.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  color: var(--text-secondary);
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-md);
  transition: border-color var(--transition-normal);
}

.form-input:focus {
  border-color: var(--primary-light);
  outline: none;
}

.form-input:read-only {
  background-color: var(--secondary-color);
  cursor: not-allowed;
}

.textarea {
  resize: vertical;
  min-height: 100px;
}

.radio-group {
  display: flex;
  gap: var(--spacing-lg);
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.radio-label input {
  margin-right: var(--spacing-xs);
}

.form-hint {
  font-size: var(--font-size-sm);
  color: var(--text-light);
  margin-top: var(--spacing-xs);
}

.code-input-group {
  display: flex;
  gap: var(--spacing-md);
}

.code-input-group .form-input {
  flex: 1;
}

.code-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: background-color var(--transition-normal);
  white-space: nowrap;
}

.code-btn:hover:not(:disabled) {
  background-color: var(--primary-dark);
}

.code-btn:disabled {
  background-color: var(--text-light);
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-lg);
}

.account-security {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.account-security h3 {
  font-size: var(--font-size-lg);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.account-security p {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-preview-container {
  width: 200px;
  height: 200px;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
  border: 2px dashed var(--border-color);
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--text-light);
}

.avatar-placeholder .el-icon {
  font-size: 40px;
  margin-bottom: var(--spacing-sm);
}

.avatar-input {
  display: none;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

/* 装饰元素 */
.decoration-element {
  position: absolute;
  z-index: -1;
  font-size: 24px;
  animation: float 5s ease-in-out infinite;
}

.star-1 {
  top: 50px;
  right: 50px;
  animation-delay: 0s;
}

.star-2 {
  bottom: 100px;
  left: 50px;
  animation-delay: 1s;
}

.heart {
  top: 200px;
  left: 100px;
  animation-delay: 2s;
}

.cat {
  bottom: 50px;
  right: 100px;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .account-container {
    grid-template-columns: 1fr;
  }
  
  .decoration-element {
    display: none;
  }
}
</style>
