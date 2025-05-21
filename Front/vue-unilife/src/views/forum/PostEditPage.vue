<template>
  <div class="post-edit-page">
    <el-card class="edit-card">
      <template #header>
        <h2>{{ isEditMode ? 'Edit Post' : 'Create New Post' }}</h2>
      </template>

      <el-form
        ref="postFormRef"
        :model="postData"
        :rules="postRules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="Title" prop="title">
          <el-input v-model="postData.title" placeholder="Enter post title" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="Category" prop="categoryId">
          <el-select v-model="postData.categoryId" placeholder="Select a category" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Content" prop="content">
          <!-- Using el-input textarea for now. Replace with rich text editor if needed. -->
          <el-input
            v-model="postData.content"
            type="textarea"
            :rows="15"
            placeholder="Write your post content here..."
            maxlength="20000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item class="form-actions">
          <el-button type="primary" native-type="submit" :loading="submitting">
            {{ isEditMode ? 'Save Changes' : 'Publish Post' }}
          </el-button>
          <el-button @click="handleCancel">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

interface Category {
  id: number;
  name: string;
  description?: string;
}

interface PostFormData {
  title: string;
  content: string;
  categoryId: number | null;
}

export default defineComponent({
  name: 'PostEditPage',
  components: {
    ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const postFormRef = ref<FormInstance>();
    const postData = ref<PostFormData>({
      title: '',
      content: '',
      categoryId: null,
    });
    const categories = ref<Category[]>([]);
    const submitting = ref(false);
    const loadingPostData = ref(false); // For edit mode

    const postId = computed(() => route.params.postId ? Number(route.params.postId as string) : null);
    const isEditMode = computed(() => !!postId.value);

    const postRules: FormRules = {
      title: [
        { required: true, message: 'Please enter a title', trigger: 'blur' },
        { min: 3, max: 100, message: 'Title must be between 3 and 100 characters', trigger: 'blur' },
      ],
      categoryId: [
        { required: true, message: 'Please select a category', trigger: 'change' },
      ],
      content: [
        { required: true, message: 'Please enter content for your post', trigger: 'blur' },
        { min: 10, message: 'Content must be at least 10 characters long', trigger: 'blur' },
         { max: 20000, message: 'Content cannot exceed 20000 characters', trigger: 'blur' },
      ],
    };

    const fetchCategories = async () => {
      try {
        const response = await axios.get('/api/categories');
        if (response.data && response.data.code === 200) {
          categories.value = response.data.data || [];
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch categories.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching categories.');
        console.error("Fetch categories error:", error);
      }
    };

    const fetchPostDataForEdit = async () => {
      if (!isEditMode.value || !postId.value) return;
      loadingPostData.value = true;
      try {
        const response = await axios.get(`/api/posts/${postId.value}`);
        if (response.data && response.data.code === 200 && response.data.data) {
          const fetchedPost = response.data.data;
          postData.value = {
            title: fetchedPost.title,
            content: fetchedPost.content,
            categoryId: fetchedPost.categoryId,
          };
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch post data for editing.');
          router.push({ name: 'PostList' }); // Redirect if post not found or error
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching post data.');
        console.error("Fetch post for edit error:", error);
        router.push({ name: 'PostList' });
      } finally {
        loadingPostData.value = false;
      }
    };

    const handleSubmit = async () => {
      if (!postFormRef.value) return;
      await postFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          try {
            let response;
            const payload = {
              title: postData.value.title,
              content: postData.value.content,
              categoryId: postData.value.categoryId,
            };

            if (isEditMode.value && postId.value) {
              response = await axios.put(`/api/posts/${postId.value}`, payload);
            } else {
              response = await axios.post('/api/posts', payload);
            }

            if (response.data && response.data.code === 200) {
              ElMessage.success(isEditMode.value ? 'Post updated successfully!' : 'Post created successfully!');
              // Assuming the response.data.data contains the created/updated post,
              // and it has an 'id' field.
              const newOrUpdatedPostId = response.data.data?.id || postId.value;
              if (newOrUpdatedPostId) {
                router.push({ name: 'PostDetail', params: { postId: newOrUpdatedPostId } });
              } else {
                 router.push({ name: 'PostList' }); // Fallback if ID not available
              }
            } else {
              ElMessage.error(response.data.message || `Failed to ${isEditMode.value ? 'update' : 'create'} post.`);
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || `Error ${isEditMode.value ? 'updating' : 'creating'} post.`);
            console.error("Submit post error:", error);
          } finally {
            submitting.value = false;
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };

    const handleCancel = () => {
      // router.back(); // Or navigate to a specific page like PostList or PostDetail if editing
      if (isEditMode.value && postId.value) {
        router.push({ name: 'PostDetail', params: { postId: postId.value } });
      } else {
        router.push({ name: 'PostList' });
      }
    };

    onMounted(async () => {
      await fetchCategories(); // Fetch categories for both create and edit
      if (isEditMode.value) {
        await fetchPostDataForEdit();
      }
    });

    return {
      postFormRef,
      postData,
      categories,
      submitting,
      loadingPostData,
      isEditMode,
      postRules,
      handleSubmit,
      handleCancel,
    };
  },
});
</script>

<style scoped>
.post-edit-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.edit-card {
  border-radius: 8px;
}

.edit-card h2 {
  margin: 0;
  font-size: 1.8em;
  color: #303133;
}

.el-form-item {
  margin-bottom: 25px;
}

.el-form-item label {
  font-weight: 600;
}

/* Consider specific styling for the textarea if it's used long-term
   or for the rich text editor component when integrated. */
.el-textarea :deep(textarea) { /* Target inner textarea */
  min-height: 250px !important; /* Ensure enough height */
  font-family: Arial, sans-serif; /* Consistent font */
  line-height: 1.6;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end; /* Align buttons to the right */
}
.form-actions .el-button {
  margin-left: 10px;
}
</style>
