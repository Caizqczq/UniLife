<template>
  <div class="messages-view">
    <div class="page-header">
      <h1>消息中心</h1>
      <p>查看你的所有通知和私信</p>
    </div>
    
    <!-- 消息分类 -->
    <div class="message-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="系统通知" name="system">
          <div class="message-list">
            <el-skeleton :loading="loading" :rows="3" animated />
            
            <el-empty v-if="!loading && systemMessages.length === 0" description="暂无系统通知">
              <el-button type="primary" @click="refreshMessages">刷新</el-button>
            </el-empty>
            
            <div v-if="!loading && systemMessages.length > 0">
              <div v-for="message in systemMessages" :key="message.id" class="message-item">
                <div class="message-header">
                  <div class="message-title">{{ message.title }}</div>
                  <div class="message-time">{{ formatTime(message.createdAt) }}</div>
                </div>
                <div class="message-content">{{ message.content }}</div>
                <div class="message-actions" v-if="!message.isRead">
                  <el-button size="small" @click="markAsRead(message.id)">标记已读</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="评论回复" name="comments">
          <div class="message-list">
            <el-skeleton :loading="loading" :rows="3" animated />
            
            <el-empty v-if="!loading && commentMessages.length === 0" description="暂无评论回复">
              <el-button type="primary" @click="refreshMessages">刷新</el-button>
            </el-empty>
            
            <div v-if="!loading && commentMessages.length > 0">
              <div v-for="message in commentMessages" :key="message.id" class="message-item">
                <div class="message-header">
                  <div class="message-title">{{ message.nickname }} 回复了你的帖子</div>
                  <div class="message-time">{{ formatTime(message.createdAt) }}</div>
                </div>
                <div class="message-content">{{ message.content }}</div>
                <div class="message-actions">
                  <el-button size="small" type="primary" @click="goToPost(message.postId)">查看帖子</el-button>
                  <el-button size="small" @click="markAsRead(message.id)" v-if="!message.isRead">标记已读</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="点赞通知" name="likes">
          <div class="message-list">
            <el-skeleton :loading="loading" :rows="3" animated />
            
            <el-empty v-if="!loading && likeMessages.length === 0" description="暂无点赞通知">
              <el-button type="primary" @click="refreshMessages">刷新</el-button>
            </el-empty>
            
            <div v-if="!loading && likeMessages.length > 0">
              <div v-for="message in likeMessages" :key="message.id" class="message-item">
                <div class="message-header">
                  <div class="message-title">{{ message.nickname }} 点赞了你的帖子</div>
                  <div class="message-time">{{ formatTime(message.createdAt) }}</div>
                </div>
                <div class="message-content">{{ message.postTitle }}</div>
                <div class="message-actions">
                  <el-button size="small" type="primary" @click="goToPost(message.postId)">查看帖子</el-button>
                  <el-button size="small" @click="markAsRead(message.id)" v-if="!message.isRead">标记已读</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 操作按钮 -->
    <div class="message-actions-bar">
      <el-button @click="markAllAsRead">全部标记已读</el-button>
      <el-button @click="clearReadMessages">清除已读消息</el-button>
      <el-button type="primary" @click="refreshMessages">刷新</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElTabs, ElTabPane, ElSkeleton, ElEmpty, ElButton } from 'element-plus';

const router = useRouter();

// 当前活跃的标签页
const activeTab = ref('system');
const loading = ref(true);

// 消息数据
const systemMessages = ref([
  {
    id: 1,
    title: '欢迎使用UniLife',
    content: '欢迎来到UniLife！这里是你的大学生活助手。',
    createdAt: '2023-12-01T10:00:00Z',
    isRead: false
  },
  {
    id: 2,
    title: '系统维护通知',
    content: '系统将于今晚进行维护，预计持续2小时。',
    createdAt: '2023-12-02T14:30:00Z',
    isRead: true
  }
]);

const commentMessages = ref([
  {
    id: 3,
    nickname: '张同学',
    content: '很有用的分享，谢谢！',
    postId: 1,
    postTitle: '大学生活经验分享',
    createdAt: '2023-12-01T15:20:00Z',
    isRead: false
  }
]);

const likeMessages = ref([
  {
    id: 4,
    nickname: '李同学',
    postId: 1,
    postTitle: '大学生活经验分享',
    createdAt: '2023-12-01T12:10:00Z',
    isRead: false
  }
]);

// 格式化时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
  
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};

// 切换标签页
const handleTabClick = (tab: any) => {
  console.log('切换到标签页:', tab.props.name);
  // 这里可以根据不同标签页加载不同的数据
};

// 标记消息为已读
const markAsRead = (messageId: number) => {
  // 在对应的消息列表中找到消息并标记为已读
  const allMessages = [...systemMessages.value, ...commentMessages.value, ...likeMessages.value];
  const message = allMessages.find(msg => msg.id === messageId);
  if (message) {
    message.isRead = true;
    ElMessage.success('已标记为已读');
  }
};

// 全部标记已读
const markAllAsRead = () => {
  systemMessages.value.forEach(msg => msg.isRead = true);
  commentMessages.value.forEach(msg => msg.isRead = true);
  likeMessages.value.forEach(msg => msg.isRead = true);
  ElMessage.success('已全部标记为已读');
};

// 清除已读消息
const clearReadMessages = () => {
  systemMessages.value = systemMessages.value.filter(msg => !msg.isRead);
  commentMessages.value = commentMessages.value.filter(msg => !msg.isRead);
  likeMessages.value = likeMessages.value.filter(msg => !msg.isRead);
  ElMessage.success('已清除已读消息');
};

// 刷新消息
const refreshMessages = () => {
  loading.value = true;
  // 模拟API调用
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('刷新成功');
  }, 1000);
};

// 跳转到帖子
const goToPost = (postId: number) => {
  router.push(`/post/${postId}`);
};

onMounted(() => {
  // 模拟加载数据
  setTimeout(() => {
    loading.value = false;
  }, 1000);
});
</script>

<style scoped>
.messages-view {
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

.message-tabs {
  margin-bottom: 30px;
}

.message-list {
  min-height: 300px;
}

.message-item {
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  margin-bottom: 12px;
  background: var(--el-bg-color);
  transition: box-shadow 0.2s;
}

.message-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.message-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.message-content {
  color: var(--el-text-color-regular);
  line-height: 1.6;
  margin-bottom: 12px;
}

.message-actions {
  display: flex;
  gap: 8px;
}

.message-actions-bar {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-view {
    padding: 10px;
  }
  
  .message-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .message-time {
    margin-top: 4px;
  }
  
  .message-actions-bar {
    flex-direction: column;
    align-items: center;
  }
}
</style> 