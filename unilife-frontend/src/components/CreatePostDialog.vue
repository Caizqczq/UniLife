<template>
  <el-dialog
    v-model="visible"
    title="发布新帖子"
    width="600px"
    class="create-post-dialog"
    @closed="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="帖子标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入帖子标题"
          maxlength="100"
          show-word-limit
          size="large"
        />
      </el-form-item>

      <el-form-item label="分类" prop="categoryId">
        <el-select
          v-model="form.categoryId"
          placeholder="请选择分类"
          size="large"
          style="width: 100%"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="帖子内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="8"
          placeholder="请输入帖子内容..."
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        发布
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { createPost } from '@/api/forum'
import type { Category } from '@/types'

interface Props {
  modelValue: boolean
  categories: Category[]
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  title: '',
  content: '',
  categoryId: undefined as number | undefined
})

const rules = {
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在5到100个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度在10到2000个字符之间', trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    
    await createPost({
      title: form.title,
      content: form.content,
      categoryId: form.categoryId!
    })
    
    emit('success')
    handleClose()
  } catch (error: any) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  visible.value = false
  form.title = ''
  form.content = ''
  form.categoryId = undefined
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.create-post-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}

.create-post-dialog :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--gray-700);
}

.create-post-dialog :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 2px solid var(--gray-200);
  transition: var(--transition-base);
}

.create-post-dialog :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-300);
}

.create-post-dialog :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(184, 169, 255, 0.15);
}

.create-post-dialog :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 2px solid var(--gray-200);
  transition: var(--transition-base);
}

.create-post-dialog :deep(.el-textarea__inner:hover) {
  border-color: var(--primary-300);
}

.create-post-dialog :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-400);
  box-shadow: 0 0 0 3px rgba(184, 169, 255, 0.15);
}
</style> 