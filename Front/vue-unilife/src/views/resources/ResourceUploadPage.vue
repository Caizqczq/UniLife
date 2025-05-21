<template>
  <div class="resource-upload-page">
    <el-card class="upload-card">
      <template #header>
        <h2>{{ isEditMode ? 'Edit Resource Details' : 'Upload New Resource' }}</h2>
      </template>

      <el-form
        ref="uploadFormRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="Title" prop="title">
          <el-input v-model="formData.title" placeholder="Enter resource title" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="Description" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="Enter a brief description of the resource"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="Category" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="Select a category" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Resource File" prop="file" v-if="!isEditMode">
          <el-upload
            ref="uploadComponentRef"
            class="resource-uploader"
            action="#" 
            :http-request="customUploadRequest"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            :limit="1"
            :auto-upload="false" 
            drag
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              Drop file here or <em>click to upload</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                Max file size: 50MB. Allowed types: common document, image, and archive formats.
              </div>
            </template>
          </el-upload>
        </el-form-item>
         <el-form-item v-if="isEditMode && currentFileName" label="Current File">
            <el-input :value="currentFileName" disabled>
                 <template #append>
                    <el-tag type="info">File cannot be changed during edit</el-tag>
                 </template>
            </el-input>
        </el-form-item>


        <el-form-item class="form-actions">
          <el-button type="primary" native-type="submit" :loading="submitting">
            {{ isEditMode ? 'Save Changes' : 'Upload Resource' }}
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
import { ElMessage, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard, ElUpload, ElIcon, ElTag } from 'element-plus';
import type { FormInstance, FormRules, UploadInstance, UploadFile, UploadFiles, UploadRequestHandler } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

interface Category {
  id: number;
  name: string;
}

interface ResourceFormData {
  title: string;
  description: string;
  categoryId: number | null;
  file: File | null; // For storing the actual file object
}

export default defineComponent({
  name: 'ResourceUploadPage',
  components: {
    ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElCard, ElUpload, ElIcon, ElTag,
    UploadFilled,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();

    const uploadFormRef = ref<FormInstance>();
    const uploadComponentRef = ref<UploadInstance>();
    const formData = ref<ResourceFormData>({
      title: '',
      description: '',
      categoryId: null,
      file: null,
    });
    const fileList = ref<UploadFiles>([]); // For el-upload display
    const categories = ref<Category[]>([]);
    const submitting = ref(false);
    const currentFileName = ref<string|null>(null); // For edit mode

    const resourceId = computed(() => route.params.resourceId ? Number(route.params.resourceId as string) : null);
    const isEditMode = computed(() => !!resourceId.value);

    const formRules: FormRules = {
      title: [
        { required: true, message: 'Please enter a title', trigger: 'blur' },
        { min: 3, max: 100, message: 'Title must be between 3 and 100 characters', trigger: 'blur' },
      ],
      description: [
        { required: true, message: 'Please enter a description', trigger: 'blur' },
        { max: 500, message: 'Description cannot exceed 500 characters', trigger: 'blur' },
      ],
      categoryId: [
        { required: true, message: 'Please select a category', trigger: 'change' },
      ],
      file: [ // This rule is for the formData.file, not directly validated by el-form-item in UI for el-upload
        { required: !isEditMode.value, message: 'Please select a file to upload', trigger: 'change' },
      ],
    };

    const fetchCategories = async () => {
      try {
        const response = await axios.get('/api/categories'); // Assuming same categories for resources
        if (response.data && response.data.code === 200) {
          categories.value = response.data.data || [];
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch categories.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching categories.');
      }
    };

    const fetchResourceDataForEdit = async () => {
        if (!isEditMode.value || !resourceId.value) return;
        submitting.value = true; // Use submitting as loading indicator
        try {
            const response = await axios.get(`/api/resources/${resourceId.value}`);
            if (response.data && response.data.code === 200 && response.data.data) {
                const fetchedResource = response.data.data;
                formData.value.title = fetchedResource.title;
                formData.value.description = fetchedResource.description;
                formData.value.categoryId = fetchedResource.categoryId;
                currentFileName.value = fetchedResource.fileName;
                // File cannot be edited, so formData.file remains null
            } else {
                ElMessage.error(response.data.message || 'Failed to fetch resource data for editing.');
                router.push({ name: 'ResourceList' });
            }
        } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error fetching resource data.');
            router.push({ name: 'ResourceList' });
        } finally {
            submitting.value = false;
        }
    };


    const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
      // Basic file validation (client-side)
      const isLt50M = uploadFile.size! / 1024 / 1024 < 50;
      if (!isLt50M) {
        ElMessage.error('File size cannot exceed 50MB!');
        uploadFiles.pop(); // Remove the invalid file
        fileList.value = [...uploadFiles];
        formData.file = null;
        return;
      }
      // You could add file type validation here if needed
      // const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'application/zip'];
      // if (!allowedTypes.includes(uploadFile.raw!.type)) { ... }

      formData.file = uploadFile.raw!; // Store the raw file object
      fileList.value = [uploadFile]; // Keep only one file in the list
    };

    const handleFileRemove = () => {
      formData.file = null;
      fileList.value = [];
    };
    
    // This function is required by el-upload when auto-upload is false or for custom logic.
    // Since we submit via a form button, this can be a no-op or handle direct upload if desired.
    // For this form, we'll prepare FormData in handleSubmit.
    const customUploadRequest: UploadRequestHandler = async (options) => {
        // This function is called when el-upload's http-request is triggered.
        // In our case, with auto-upload="false", it won't be called automatically.
        // If we were to use el-upload's own submit mechanism, this is where the actual AJAX call would go.
        // For now, it can be a placeholder.
        console.log("customUploadRequest triggered, file:", options.file);
        // options.onSuccess({}, options.file); // Simulate success if needed for UI
    };


    const handleSubmit = async () => {
      if (!uploadFormRef.value) return;
      
      // Manually validate file for create mode
      if (!isEditMode.value && !formData.value.file) {
          ElMessage.error('Please select a file to upload.');
          return;
      }

      await uploadFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          const data = new FormData();
          data.append('title', formData.value.title);
          data.append('description', formData.value.description);
          if (formData.value.categoryId !== null) {
            data.append('categoryId', formData.value.categoryId.toString());
          }

          if (isEditMode.value && resourceId.value) {
            // PUT request to update metadata (file cannot be changed)
            try {
                const response = await axios.put(`/api/resources/${resourceId.value}`, {
                    title: formData.value.title,
                    description: formData.value.description,
                    categoryId: formData.value.categoryId,
                });
                 if (response.data && response.data.code === 200) {
                    ElMessage.success('Resource details updated successfully!');
                    router.push({ name: 'ResourceDetail', params: { resourceId: resourceId.value } });
                } else {
                    ElMessage.error(response.data.message || 'Failed to update resource details.');
                }
            } catch (error: any) {
                 ElMessage.error(error.response?.data?.message || 'Error updating resource details.');
            } finally {
                submitting.value = false;
            }
          } else {
            // POST request for new resource with file
            if (formData.value.file) {
              data.append('file', formData.value.file);
            } else {
                ElMessage.error('File is missing for upload.'); // Should be caught by earlier check
                submitting.value = false;
                return;
            }
            try {
                const response = await axios.post('/api/resources/upload', data, {
                    headers: { 'Content-Type': 'multipart/form-data' },
                });
                 if (response.data && response.data.code === 200 && response.data.data) {
                    ElMessage.success('Resource uploaded successfully!');
                    router.push({ name: 'ResourceDetail', params: { resourceId: response.data.data.id } });
                } else {
                    ElMessage.error(response.data.message || 'Failed to upload resource.');
                }
            } catch (error: any) {
                ElMessage.error(error.response?.data?.message || 'Error uploading resource.');
            } finally {
                submitting.value = false;
            }
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };

    const handleCancel = () => {
      if (isEditMode.value && resourceId.value) {
        router.push({ name: 'ResourceDetail', params: { resourceId: resourceId.value } });
      } else {
        router.push({ name: 'ResourceList' });
      }
    };

    onMounted(async () => {
      await fetchCategories();
      if (isEditMode.value) {
        await fetchResourceDataForEdit();
      }
    });

    return {
      uploadFormRef, uploadComponentRef,
      formData, fileList, categories, submitting, isEditMode, currentFileName,
      formRules, handleSubmit, handleCancel, handleFileChange, handleFileRemove, customUploadRequest,
      UploadFilled,
    };
  },
});
</script>

<style scoped>
.resource-upload-page {
  max-width: 700px;
  margin: 20px auto;
  padding: 20px;
}
.upload-card {
  border-radius: 8px;
}
.upload-card h2 {
  margin: 0;
  font-size: 1.8em;
  color: #303133;
}
.el-form-item {
  margin-bottom: 22px;
}
.el-form-item label {
  font-weight: 600;
}
.resource-uploader {
  width: 100%;
}
.resource-uploader :deep(.el-upload-dragger) { /* Style the drag area */
    width: 100%;
    padding: 40px 20px;
}
.el-upload__tip {
  line-height: 1.2;
  font-size: 0.85rem;
  color: #909399;
  margin-top: 7px;
}
.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}
.form-actions .el-button {
  margin-left: 10px;
}
</style>
