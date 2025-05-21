<template>
  <div class="avatar-uploader-container">
    <el-upload
      class="avatar-uploader"
      action="/api/user/avatar"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeAvatarUpload"
      :headers="uploadHeaders"
      method="post"
      name="avatarfile" 
      with-credentials
    >
      <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="User Avatar"/>
      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>
    <div class="el-upload__tip">
      Click to upload. JPG/PNG files with a size less than 2MB are recommended.
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user'; // Assuming a user store exists

export default defineComponent({
  name: 'AvatarUpload',
  components: { Plus },
  props: {
    initialAvatarUrl: {
      type: String,
      default: '',
    },
  },
  emits: ['avatar-updated'],
  setup(props, { emit }) {
    const userStore = useUserStore();
    const imageUrl = ref('');

    const uploadHeaders = ref({
      Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
    });

    const handleAvatarSuccess = (response: any, uploadFile: any) => {
      if (response && response.data && response.data.avatarUrl) {
        imageUrl.value = response.data.avatarUrl;
        ElMessage.success('Avatar uploaded successfully!');
        // Update user store or emit event if needed
        if (userStore) {
          userStore.updateAvatar(response.data.avatarUrl);
        }
        emit('avatar-updated', response.data.avatarUrl);
      } else if (response && response.message) {
        // Handling cases where backend returns a success status code but a custom error message
        ElMessage.error(response.message || 'Upload failed with unexpected response format.');
        imageUrl.value = props.initialAvatarUrl; // Revert to initial on backend custom error
      } else {
        ElMessage.error('Avatar upload failed. Unexpected response format.');
        imageUrl.value = props.initialAvatarUrl; // Revert to initial
      }
    };

    const beforeAvatarUpload = (rawFile: any) => {
      const isJPGorPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
      const isLt2M = rawFile.size / 1024 / 1024 < 2;

      if (!isJPGorPNG) {
        ElMessage.error('Avatar picture must be JPG or PNG format!');
        return false;
      }
      if (!isLt2M) {
        ElMessage.error('Avatar picture size can not exceed 2MB!');
        return false;
      }
      // Re-fetch token before each upload to ensure it's current
      uploadHeaders.value.Authorization = `Bearer ${localStorage.getItem('token') || ''}`;
      return true;
    };

    onMounted(() => {
      imageUrl.value = props.initialAvatarUrl || (userStore.avatarUrl as string) || '';
    });

    return {
      imageUrl,
      handleAvatarSuccess,
      beforeAvatarUpload,
      uploadHeaders,
    };
  },
});
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%; /* Make the upload trigger area circular */
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px; /* Ensure uploader icon area matches avatar size */
  height: 120px; /* Ensure uploader icon area matches avatar size */
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}

.avatar-uploader-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.el-upload__tip {
  font-size: 12px;
  color: #999;
  text-align: center;
}
</style>
