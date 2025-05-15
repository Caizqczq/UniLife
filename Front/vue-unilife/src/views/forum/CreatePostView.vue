<template>
  <div class="create-post-view">
    <div class="page-header">
      <h1 class="page-title">{{ isEditing ? '编辑帖子' : '发布新帖子' }}</h1>
      <el-button @click="goBack" class="back-button" :icon="ArrowLeft">返回</el-button>
    </div>

    <el-card class="post-form-card">
      <el-form 
        ref="postFormRef" 
        :model="postForm" 
        :rules="postRules" 
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="postForm.title" 
            placeholder="请输入帖子标题（5-50字）" 
            maxlength="50" 
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select 
            v-model="postForm.categoryId" 
            placeholder="请选择帖子分类" 
            style="width: 100%"
            :loading="loadingCategories"
          >
            <el-option 
              v-for="category in categories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="postForm.content" 
            type="textarea" 
            placeholder="请输入帖子内容（10-5000字）" 
            :rows="12"
            maxlength="5000" 
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit" 
            :loading="submitting" 
            style="width: 120px"
          >
            {{ isEditing ? '保存更改' : '发布帖子' }}
          </el-button>
          <el-button @click="goBack" style="margin-left: 16px">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElForm } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import postApi from '@/api/post';
import type { CategoryItem, CreatePostParams } from '@/api/post';
import { useUserStore } from '@/stores';

// 路由和用户状态
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 判断是否为编辑模式
const isEditing = computed(() => !!route.params.id);
const postId = computed(() => route.params.id ? Number(route.params.id) : null);

// 表单及验证规则
const postFormRef = ref<InstanceType<typeof ElForm> | null>(null);
const postForm = reactive<CreatePostParams>({
  title: '',
  content: '',
  categoryId: null as unknown as number // 初始为null，缺失后端处理的值
});

const postRules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度应在5到50个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择帖子分类', trigger: 'change' },
    { type: 'number', min: 1, message: '请选择有效的分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, max: 5000, message: '内容长度应在10到5000个字符之间', trigger: 'blur' }
  ]
};

// 表单提交状态
const submitting = ref(false);

// 分类数据
const categories = ref<CategoryItem[]>([]);
const loadingCategories = ref(false);

// 获取分类列表
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    const res = await postApi.getCategories();
    if (res.code === 200 && res.data.list) {
      categories.value = res.data.list;
    } else {
      ElMessage.error('获取分类失败');
    }
  } catch (error) {
    console.error('获取分类出错:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    loadingCategories.value = false;
  }
};

// 如果是编辑模式，获取帖子详情
const fetchPostDetail = async () => {
  if (!isEditing.value || !postId.value) return;
  
  try {
    const res = await postApi.getPostDetail(postId.value);
    if (res.code === 200 && res.data) {
      // 填充表单数据
      postForm.title = res.data.title;
      postForm.content = res.data.content;
      postForm.categoryId = res.data.categoryId;
    } else {
      ElMessage.error('获取帖子详情失败');
      // 获取失败返回列表页
      router.push('/');
    }
  } catch (error) {
    console.error('获取帖子详情出错:', error);
    ElMessage.error('网络错误，请稍后重试');
    router.push('/');
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 提交表单
const handleSubmit = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  
  // 在提交前额外检查分类是否选择
  if (!postForm.categoryId || postForm.categoryId <= 0) {
    ElMessage.warning('请选择有效的帖子分类');
    return;
  }

  await postFormRef.value?.validate(async (valid, fields) => {
    if (!valid) {
      console.log('表单验证失败:', fields);
      return;
    }

    submitting.value = true;
    try {
      let res;
      if (isEditing.value && postId.value) {
        // 更新帖子
        res = await postApi.updatePost(postId.value, postForm);
        if (res.code === 200) {
          ElMessage.success('帖子更新成功');
          router.push(`/post/${postId.value}`);
        } else {
          ElMessage.error(res.message || '帖子更新失败');
        }
      } else {
        // 创建新帖子
        res = await postApi.createPost(postForm);
        if (res.code === 200 && res.data.postId) {
          ElMessage.success('帖子发布成功');
          router.push(`/post/${res.data.postId}`);
        } else {
          ElMessage.error(res.message || '帖子发布失败');
        }
      }
    } catch (error: any) {
      console.error('提交帖子出错:', error);
      ElMessage.error(error.message || '网络错误，请稍后重试');
    } finally {
      submitting.value = false;
    }
  });
};

// 页面初始化
onMounted(async () => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录');
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }

  // 获取分类列表
  await fetchCategories();
  
  // 如果是编辑模式，获取帖子详情
  if (isEditing.value) {
    await fetchPostDetail();
  }
});
</script>

<style scoped>
.create-post-view {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.5rem;
  margin: 0;
  color: var(--el-text-color-primary);
}

.post-form-card {
  margin-bottom: 40px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .create-post-view {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .back-button {
    align-self: flex-start;
  }
}
</style>
