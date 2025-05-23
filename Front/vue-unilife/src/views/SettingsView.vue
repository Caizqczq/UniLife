<template>
  <div class="settings-view">
    <div class="page-header">
      <h1>设置</h1>
      <p>管理你的账户偏好和应用设置</p>
    </div>
    
    <div class="settings-content">
      <!-- 通知设置 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <h3>通知设置</h3>
        </template>
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">邮件通知</div>
            <div class="setting-desc">接收新评论和点赞的邮件通知</div>
          </div>
          <el-switch v-model="notificationSettings.email" />
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">浏览器通知</div>
            <div class="setting-desc">在浏览器中显示实时通知</div>
          </div>
          <el-switch v-model="notificationSettings.browser" />
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">私信通知</div>
            <div class="setting-desc">接收新私信的通知</div>
          </div>
          <el-switch v-model="notificationSettings.message" />
        </div>
      </el-card>
      
      <!-- 隐私设置 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <h3>隐私设置</h3>
        </template>
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">个人资料可见性</div>
            <div class="setting-desc">其他用户是否可以查看你的个人资料</div>
          </div>
          <el-select v-model="privacySettings.profileVisibility" style="width: 140px;">
            <el-option label="公开" value="public" />
            <el-option label="仅好友" value="friends" />
            <el-option label="私密" value="private" />
          </el-select>
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">在线状态</div>
            <div class="setting-desc">是否显示你的在线状态</div>
          </div>
          <el-switch v-model="privacySettings.showOnlineStatus" />
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">活动历史</div>
            <div class="setting-desc">是否保存你的活动历史记录</div>
          </div>
          <el-switch v-model="privacySettings.saveActivityHistory" />
        </div>
      </el-card>
      
      <!-- 界面设置 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <h3>界面设置</h3>
        </template>
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">主题模式</div>
            <div class="setting-desc">选择你喜欢的界面主题</div>
          </div>
          <el-select v-model="interfaceSettings.theme" style="width: 120px;">
            <el-option label="自动" value="auto" />
            <el-option label="浅色" value="light" />
            <el-option label="深色" value="dark" />
          </el-select>
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">语言</div>
            <div class="setting-desc">选择界面显示语言</div>
          </div>
          <el-select v-model="interfaceSettings.language" style="width: 120px;">
            <el-option label="中文" value="zh-CN" />
            <el-option label="English" value="en-US" />
          </el-select>
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">页面大小</div>
            <div class="setting-desc">每页显示的内容数量</div>
          </div>
          <el-select v-model="interfaceSettings.pageSize" style="width: 100px;">
            <el-option label="10" :value="10" />
            <el-option label="20" :value="20" />
            <el-option label="50" :value="50" />
          </el-select>
        </div>
      </el-card>
      
      <!-- 账户安全 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <h3>账户安全</h3>
        </template>
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">两步验证</div>
            <div class="setting-desc">为你的账户添加额外的安全保护</div>
          </div>
          <el-button size="small" @click="setupTwoFactor">
            {{ securitySettings.twoFactorEnabled ? '已启用' : '启用' }}
          </el-button>
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">登录通知</div>
            <div class="setting-desc">新设备登录时发送通知</div>
          </div>
          <el-switch v-model="securitySettings.loginNotification" />
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">会话管理</div>
            <div class="setting-desc">查看和管理你的登录会话</div>
          </div>
          <el-button size="small" @click="manageSessions">管理会话</el-button>
        </div>
      </el-card>
      
      <!-- 数据管理 -->
      <el-card class="settings-card" shadow="hover">
        <template #header>
          <h3>数据管理</h3>
        </template>
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">数据导出</div>
            <div class="setting-desc">下载你的所有数据</div>
          </div>
          <el-button size="small" @click="exportData">导出数据</el-button>
        </div>
        <el-divider />
        <div class="setting-item">
          <div class="setting-info">
            <div class="setting-title">清除缓存</div>
            <div class="setting-desc">清除本地缓存数据</div>
          </div>
          <el-button size="small" @click="clearCache">清除缓存</el-button>
        </div>
        <el-divider />
        <div class="setting-item danger">
          <div class="setting-info">
            <div class="setting-title">删除账户</div>
            <div class="setting-desc">永久删除你的账户和所有数据</div>
          </div>
          <el-button size="small" type="danger" @click="deleteAccount">删除账户</el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 保存按钮 -->
    <div class="save-actions">
      <el-button @click="resetSettings">重置</el-button>
      <el-button type="primary" @click="saveSettings" :loading="saving">保存设置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox, ElCard, ElSwitch, ElSelect, ElOption, ElButton, ElDivider } from 'element-plus';

// 设置数据
const notificationSettings = ref({
  email: true,
  browser: false,
  message: true
});

const privacySettings = ref({
  profileVisibility: 'public',
  showOnlineStatus: true,
  saveActivityHistory: true
});

const interfaceSettings = ref({
  theme: 'auto',
  language: 'zh-CN',
  pageSize: 20
});

const securitySettings = ref({
  twoFactorEnabled: false,
  loginNotification: true
});

const saving = ref(false);

// 保存设置
const saveSettings = async () => {
  saving.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 这里应该调用真实的API来保存设置
    // await settingsApi.saveSettings({
    //   notification: notificationSettings.value,
    //   privacy: privacySettings.value,
    //   interface: interfaceSettings.value,
    //   security: securitySettings.value
    // });
    
    ElMessage.success('设置保存成功');
  } catch (error) {
    ElMessage.error('设置保存失败');
  } finally {
    saving.value = false;
  }
};

// 重置设置
const resetSettings = () => {
  notificationSettings.value = {
    email: true,
    browser: false,
    message: true
  };
  
  privacySettings.value = {
    profileVisibility: 'public',
    showOnlineStatus: true,
    saveActivityHistory: true
  };
  
  interfaceSettings.value = {
    theme: 'auto',
    language: 'zh-CN',
    pageSize: 20
  };
  
  securitySettings.value = {
    twoFactorEnabled: false,
    loginNotification: true
  };
  
  ElMessage.success('设置已重置');
};

// 启用两步验证
const setupTwoFactor = () => {
  ElMessage.info('两步验证功能开发中');
};

// 管理会话
const manageSessions = () => {
  ElMessage.info('会话管理功能开发中');
};

// 导出数据
const exportData = () => {
  ElMessage.info('数据导出功能开发中');
};

// 清除缓存
const clearCache = () => {
  localStorage.clear();
  sessionStorage.clear();
  ElMessage.success('缓存已清除');
};

// 删除账户
const deleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除账户吗？此操作不可逆，所有数据将永久丢失。',
      '删除账户',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    );
    
    ElMessage.info('账户删除功能开发中');
  } catch {
    // 用户取消
  }
};

onMounted(() => {
  // 这里可以加载用户的设置数据
  // loadUserSettings();
});
</script>

<style scoped>
.settings-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.page-header p {
  color: var(--el-text-color-secondary);
}

.settings-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-card {
  overflow: visible;
}

.settings-card :deep(.el-card__header) {
  padding: 18px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.settings-card h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.setting-item.danger {
  border-left: 3px solid var(--el-color-danger);
  padding-left: 16px;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.setting-desc {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}

.save-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-view {
    padding: 10px;
  }
  
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .save-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style> 