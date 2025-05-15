<template>
  <div class="account-manager-container">
    <el-row :gutter="24">
      <el-col :xs="24" :sm="24" :md="10" :lg="10" :xl="10">
        <el-card class="profile-section" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>个人资料</span>
              <el-button 
                type="primary" 
                link 
                @click="toggleProfileEdit"
              >
                {{ isProfileEditable ? '取消' : '编辑' }}
              </el-button>
            </div>
          </template>
          <div class="avatar-section">
            <div 
              class="avatar-container"
              @mouseenter="handleAvatarHover(true)"
              @mouseleave="handleAvatarHover(false)"
              @click="openAvatarDialog"
            >
              <img :src="avatarUrl" alt="User Avatar" class="avatar-image">
              <div v-if="isAvatarHovered" class="avatar-overlay">
                <el-icon><Camera /></el-icon>
                <span>修改头像</span>
              </div>
            </div>
          </div>

          <el-form 
            :model="profileForm" 
            ref="profileFormRef" 
            label-width="80px" 
            :disabled="!isProfileEditable"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" :disabled="!isProfileEditable" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio v-for="option in genderOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="学院" prop="department">
              <el-input v-model="profileForm.department" />
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="profileForm.major" />
            </el-form-item>
            <el-form-item label="年级" prop="grade">
              <el-input v-model="profileForm.grade" />
            </el-form-item>
          </el-form>
          <div class="profile-actions" v-if="isProfileEditable">
            <el-button type="primary" @click="submitProfile">保存修改</el-button>
            <el-button @click="toggleProfileEdit">取消</el-button>
          </div>
          <div v-else>
            <el-button type="primary" @click="toggleProfileEdit">编辑资料</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="14" :lg="14" :xl="14">
        <el-card class="account-info-section" shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">
              <span>账号信息</span>
            </div>
          </template>
          <div v-if="userStore.userInfo" class="account-info-details">
            <p><strong>用户ID:</strong> {{ userStore.userInfo.id }}</p>
            <p><strong>当前用户名:</strong> {{ userStore.userInfo.username }}</p>
            <p><strong>邮箱:</strong> {{ userStore.userInfo.email }}</p>
            <p><strong>学号:</strong> {{ userStore.userInfo.studentId || '未设置' }}</p>
            <p><strong>学院:</strong> {{ userStore.userInfo.department || '未设置' }}</p>
            <p><strong>专业:</strong> {{ userStore.userInfo.major || '未设置' }}</p>
            <p><strong>年级:</strong> {{ userStore.userInfo.grade || '未设置' }}</p>
            <p><strong>积分:</strong> {{ userStore.userInfo.points ?? 'N/A' }}</p>
            <p><strong>角色:</strong> {{ userStore.userInfo.role === 1 ? '管理员' : '普通用户' }}</p>
            <p><strong>邮箱已验证:</strong> {{ userStore.userInfo.isVerified === 1 ? '是' : '否' }}</p>
          </div>
        </el-card>

        <el-card class="password-section" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>密码修改</span>
              <el-button 
                type="primary" 
                link 
                @click="togglePasswordEdit"
              >
                {{ isPasswordEditable ? '取消' : '修改密码' }}
              </el-button>
            </div>
          </template>
          <el-form :model="passwordForm" label-width="80px" :disabled="!isPasswordEditable">
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" />
            </el-form-item>
            <el-form-item label="验证码" prop="code">
              <el-input v-model="passwordForm.code" />
              <el-button 
                type="primary" 
                @click="handleSendCode" 
                :disabled="countdown > 0"
              >
                {{ codeButtonText }}
              </el-button>
            </el-form-item>
          </el-form>
          <div class="password-actions" v-if="isPasswordEditable">
            <el-button type="primary" @click="submitPassword">修改密码</el-button>
            <el-button @click="togglePasswordEdit">取消</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
            <el-icon><Plus /></el-icon>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useForm } from 'vee-validate';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores';
import { useEmailCode } from '../hooks/useEmailCode';
import { Camera, Plus } from '@element-plus/icons-vue';

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
  department: '', 
  major: '',      
  grade: '',      
});

// 密码表单
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: '',
  code: '',
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
const { handleSubmit: handleProfileSubmit, setValues: setProfileValues } = useForm({
  initialValues: profileForm
});

const { handleSubmit: handlePasswordSubmit } = useForm({
  initialValues: passwordForm
});

// 获取用户信息
const fetchUserInfo = async () => {
  const userInfo = await userStore.fetchUserInfo();
  
  if (userInfo) {
    profileForm.username = userInfo.username || '';
    profileForm.gender = userInfo.gender || 0;
    profileForm.bio = userInfo.bio || '';
    profileForm.department = userInfo.department || ''; 
    profileForm.major = userInfo.major || '';          
    profileForm.grade = userInfo.grade || '';          
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
      profileForm.department = userInfo.department || ''; 
      profileForm.major = userInfo.major || '';          
      profileForm.grade = userInfo.grade || '';          
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
  console.log('Submitting profile with values:', values);
  try {
    const dataToSubmit = {
      username: profileForm.username,
      bio: profileForm.bio,
      gender: profileForm.gender,
      department: profileForm.department,
      major: profileForm.major,
      grade: profileForm.grade,
    };

    const success = await userStore.updateProfile(dataToSubmit);
    
    if (success) {
      isProfileEditable.value = false;
      // ElMessage.success('个人资料更新成功'); // Success message is now handled in the store
    } else {
      // General error message is handled in the store, but we might want specific handling here if needed
      // ElMessage.error('个人资料更新失败，请稍后再试');
    }
  } catch (error: any) {
    if (error.response && error.response.status === 409) {
      ElMessage.error(error.response.data.message || '用户名已被占用，请选择其他用户名');
    } else if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error('更新失败: ' + error.response.data.message);
    } else {
      ElMessage.error('个人资料更新失败，请稍后再试');
    }
    console.error('Error updating profile:', error);
  }
});

// 提交密码修改
const submitPassword = handlePasswordSubmit(async (values) => {
  console.log('Submitting password change with passwordForm state:', passwordForm);
  try {
    const dataToSubmit = {
      newPassword: passwordForm.newPassword,
      code: passwordForm.code
    };

    const success = await userStore.updatePassword(dataToSubmit);

    if (success) {
      isPasswordEditable.value = false;
      passwordForm.newPassword = '';
      passwordForm.confirmPassword = '';
      passwordForm.code = '';
      // ElMessage.success('密码更新成功'); // Handled in store
    } else {
      // ElMessage.error('密码更新失败，请检查您的输入'); // General error, store might provide specifics
    }
  } catch (error: any) {
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error('更新失败: ' + error.response.data.message);
    } else {
      ElMessage.error('密码更新失败，请稍后再试');
    }
    console.error('Error updating password:', error);
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

<style scoped>
.account-manager-container {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.1rem;
  font-weight: bold;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-container {
  width: 120px;
  height: 120px;
  border-radius: var(--border-radius-full);
  overflow: hidden;
  position: relative;
  cursor: pointer;
  border: 3px solid var(--primary-light);
}

.avatar-image {
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

.profile-actions,
.password-actions {
  margin-top: 20px;
  text-align: right;
}

.account-info-details p {
  margin-bottom: 10px;
  font-size: 0.95rem;
}

.account-info-details strong {
  margin-right: 8px;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .el-col {
    margin-bottom: 20px;
  }
  .profile-actions,
  .password-actions {
    text-align: center;
  }
  .el-form-item {
    margin-bottom: 15px;
  }
  .el-card {
    margin-bottom: 20px;
  }
  .el-col:last-child .el-card:last-child {
    margin-bottom: 0; /* Remove margin from the very last card on mobile */
  }
}
</style>
