<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="Profile Settings" name="profile">
        <el-row :gutter="20">
          <el-col :span="8" :xs="24" style="text-align: center; margin-bottom: 20px;">
            <h3>Avatar</h3>
            <AvatarUpload :initial-avatar-url="profileData.avatar" @avatar-updated="handleAvatarUpdate" />
          </el-col>
          <el-col :span="16" :xs="24">
            <h3>Edit Profile</h3>
            <el-form ref="profileFormRef" :model="profileData" :rules="profileRules" label-width="120px">
              <el-form-item label="Nickname" prop="nickname">
                <el-input v-model="profileData.nickname" placeholder="Enter your nickname"></el-input>
              </el-form-item>
              <el-form-item label="Bio" prop="bio">
                <el-input v-model="profileData.bio" type="textarea" placeholder="Tell us about yourself"></el-input>
              </el-form-item>
              <el-form-item label="Gender" prop="gender">
                <el-select v-model="profileData.gender" placeholder="Select your gender">
                  <el-option label="Male" :value="1"></el-option>
                  <el-option label="Female" :value="2"></el-option>
                  <el-option label="Other" :value="3"></el-option>
                  <el-option label="Prefer not to say" :value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Student ID" prop="studentId">
                <el-input v-model="profileData.studentId" placeholder="Enter your student ID"></el-input>
              </el-form-item>
              <el-form-item label="Department" prop="department">
                <el-input v-model="profileData.department" placeholder="e.g., Computer Science"></el-input>
              </el-form-item>
              <el-form-item label="Major" prop="major">
                <el-input v-model="profileData.major" placeholder="e.g., Software Engineering"></el-input>
              </el-form-item>
              <el-form-item label="Grade" prop="grade">
                <el-input v-model="profileData.grade" placeholder="e.g., Year 3, 2022"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitProfileForm" :loading="profileLoading">Save Profile</el-button>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="Account Settings" name="account">
        <h4>Change Email</h4>
        <el-form ref="emailFormRef" :model="emailData" :rules="emailRules" label-width="120px" style="max-width: 500px;">
          <el-form-item label="Current Email">
            <el-input :value="currentUserEmail" disabled></el-input>
          </el-form-item>
          <el-form-item label="New Email" prop="email">
            <el-input v-model="emailData.email" placeholder="Enter new email"></el-input>
          </el-form-item>
          <el-form-item label="Verification Code" prop="code">
            <el-input v-model="emailData.code" placeholder="Enter code">
              <template #append>
                <el-button @click="sendEmailCode" :disabled="!isNewEmailValid || emailCodeSentLoading">
                  {{ emailCodeButtonText }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitEmailForm" :loading="emailLoading">Update Email</el-button>
          </el-form-item>
        </el-form>

        <el-divider />

        <h4>Change Password</h4>
        <el-form ref="passwordFormRef" :model="passwordData" :rules="passwordRules" label-width="120px" style="max-width: 500px;">
          <el-form-item label="Old Password" prop="oldPassword">
            <el-input v-model="passwordData.oldPassword" type="password" placeholder="Enter old password"></el-input>
          </el-form-item>
          <el-form-item label="New Password" prop="newPassword">
            <el-input v-model="passwordData.newPassword" type="password" placeholder="Enter new password"></el-input>
          </el-form-item>
          <el-form-item label="Confirm New" prop="confirmNewPassword">
            <el-input v-model="passwordData.confirmNewPassword" type="password" placeholder="Confirm new password"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitPasswordForm" :loading="passwordLoading">Change Password</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import axios from 'axios';
import AvatarUpload from '../../components/user/AvatarUpload.vue';
import { useUserStore } from '../../stores/user';

interface ProfileData {
  id: number | null;
  username: string;
  nickname: string;
  email: string;
  avatar: string;
  bio: string;
  gender: number | null; // 0: prefer not to say, 1: male, 2: female, 3: other
  studentId: string;
  department: string;
  major: string;
  grade: string;
}

export default defineComponent({
  name: 'SettingsPage',
  components: { AvatarUpload },
  setup() {
    const userStore = useUserStore();
    const activeTab = ref('profile');

    const profileFormRef = ref<FormInstance>();
    const emailFormRef = ref<FormInstance>();
    const passwordFormRef = ref<FormInstance>();

    const profileLoading = ref(false);
    const emailLoading = ref(false);
    const passwordLoading = ref(false);
    const emailCodeSentLoading = ref(false);
    const emailCodeButtonText = ref('Send Code');
    let emailCodeCountdownTimer: number;

    const profileData = ref<ProfileData>({
      id: null,
      username: '',
      nickname: '',
      email: '',
      avatar: '',
      bio: '',
      gender: null,
      studentId: '',
      department: '',
      major: '',
      grade: '',
    });

    const emailData = ref({
      email: '',
      code: '',
    });

    const passwordData = ref({
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    });

    const currentUserEmail = computed(() => profileData.value.email); // To display in disabled field

    const fetchUserProfile = async () => {
      try {
        const response = await axios.get('/api/user/profile');
        if (response.data && response.data.code === 200 && response.data.data) {
          const userData = response.data.data;
          profileData.value = { ...profileData.value, ...userData };
          // Ensure userStore is updated if this is the source of truth
          userStore.setUser(userData);
        } else {
          ElMessage.error(response.data.message || 'Failed to fetch profile data.');
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.message || 'Error fetching profile. Please try again.');
        console.error("Fetch profile error:", error);
      }
    };

    onMounted(() => {
      fetchUserProfile();
    });

    const profileRules: FormRules = {
      nickname: [{ max: 50, message: 'Nickname cannot exceed 50 characters', trigger: 'blur' }],
      bio: [{ max: 200, message: 'Bio cannot exceed 200 characters', trigger: 'blur' }],
      studentId: [{ max: 20, message: 'Student ID cannot exceed 20 characters', trigger: 'blur' }],
      department: [{ max: 50, message: 'Department cannot exceed 50 characters', trigger: 'blur' }],
      major: [{ max: 50, message: 'Major cannot exceed 50 characters', trigger: 'blur' }],
      grade: [{ max: 20, message: 'Grade cannot exceed 20 characters', trigger: 'blur' }],
    };

    const emailRules: FormRules = {
      email: [
        { required: true, message: 'Please enter new email', trigger: 'blur' },
        { type: 'email', message: 'Please enter a valid email address', trigger: ['blur', 'change'] },
      ],
      code: [{ required: true, message: 'Please enter verification code', trigger: 'blur' }],
    };

    const validatePassConfirm = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('Please confirm your new password'));
      } else if (value !== passwordData.value.newPassword) {
        callback(new Error("New passwords don't match!"));
      } else {
        callback();
      }
    };

    const passwordRules: FormRules = {
      oldPassword: [{ required: true, message: 'Please enter old password', trigger: 'blur' }],
      newPassword: [
        { required: true, message: 'Please enter new password', trigger: 'blur' },
        { min: 6, message: 'New password must be at least 6 characters', trigger: 'blur' },
      ],
      confirmNewPassword: [
        { required: true, validator: validatePassConfirm, trigger: 'blur' },
      ],
    };

    const handleAvatarUpdate = (newAvatarUrl: string) => {
      profileData.value.avatar = newAvatarUrl;
      userStore.updateAvatar(newAvatarUrl); // Update in store
      ElMessage.success('Avatar displayed is updated!');
    };

    const submitProfileForm = async () => {
      if (!profileFormRef.value) return;
      await profileFormRef.value.validate(async (valid) => {
        if (valid) {
          profileLoading.value = true;
          try {
            const payload = {
              nickname: profileData.value.nickname,
              bio: profileData.value.bio,
              gender: profileData.value.gender,
              studentId: profileData.value.studentId,
              department: profileData.value.department,
              major: profileData.value.major,
              grade: profileData.value.grade,
            };
            const response = await axios.put('/api/user/profile', payload);
            if (response.data && response.data.code === 200) {
              ElMessage.success('Profile updated successfully!');
              userStore.setUser({ ...userStore.user, ...payload }); // Update store
              fetchUserProfile(); // Re-fetch to ensure consistency
            } else {
              ElMessage.error(response.data.message || 'Failed to update profile.');
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error updating profile. Please try again.');
          } finally {
            profileLoading.value = false;
          }
        }
      });
    };

    const isNewEmailValid = computed(() => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(emailData.value.email) && emailData.value.email !== currentUserEmail.value;
    });

    const startEmailCodeCountdown = () => {
      emailCodeSentLoading.value = true;
      let count = 60;
      emailCodeButtonText.value = `Resend (${count}s)`;
      clearInterval(emailCodeCountdownTimer); // Clear existing timer if any
      emailCodeCountdownTimer = window.setInterval(() => {
        count--;
        if (count <= 0) {
          clearInterval(emailCodeCountdownTimer);
          emailCodeButtonText.value = 'Send Code';
          emailCodeSentLoading.value = false;
        } else {
          emailCodeButtonText.value = `Resend (${count}s)`;
        }
      }, 1000);
    };

    const sendEmailCode = async () => {
      if (!emailFormRef.value) return;
       // @ts-ignore
      emailFormRef.value.validateField('email', async (isValid: boolean) => {
        if (isValid && isNewEmailValid.value) {
          emailCodeSentLoading.value = true; // Keep button disabled during API call
          emailCodeButtonText.value = 'Sending...';
          try {
            const response = await axios.post('/api/user/email/code', { email: emailData.value.email });
            if (response.data && response.data.code === 200) {
              ElMessage.success('Verification code sent to your new email address.');
              startEmailCodeCountdown();
            } else {
              ElMessage.error(response.data.message || 'Failed to send verification code.');
              emailCodeSentLoading.value = false; // Re-enable button on failure
              emailCodeButtonText.value = 'Send Code';
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error sending code. Please try again.');
            emailCodeSentLoading.value = false; // Re-enable button on error
            emailCodeButtonText.value = 'Send Code';
          }
        } else if (!isNewEmailValid.value && emailData.value.email === currentUserEmail.value) {
            ElMessage.warning('New email cannot be the same as the current email.');
        } else {
            ElMessage.warning('Please enter a valid new email address.');
        }
      });
    };

    const submitEmailForm = async () => {
      if (!emailFormRef.value) return;
      await emailFormRef.value.validate(async (valid) => {
        if (valid) {
          emailLoading.value = true;
          try {
            const response = await axios.put('/api/user/email', {
              email: emailData.value.email,
              code: emailData.value.code,
            });
            if (response.data && response.data.code === 200) {
              ElMessage.success('Email updated successfully! Please re-login if session is affected.');
              // Update local profile data and store
              profileData.value.email = emailData.value.email;
              userStore.updateEmail(emailData.value.email);
              emailData.value.email = ''; // Clear form
              emailData.value.code = '';
              clearInterval(emailCodeCountdownTimer); // Stop countdown
              emailCodeButtonText.value = 'Send Code';
              emailCodeSentLoading.value = false;
               ElMessageBox.confirm(
                'Your email has been updated. For security reasons, you might be logged out. Please login again with your new email if needed.',
                'Email Updated',
                {
                  confirmButtonText: 'OK',
                  type: 'info',
                }
              ).catch(() => {}); // Catch if user clicks away
            } else {
              ElMessage.error(response.data.message || 'Failed to update email.');
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error updating email. Please try again.');
          } finally {
            emailLoading.value = false;
          }
        }
      });
    };

    const submitPasswordForm = async () => {
      if (!passwordFormRef.value) return;
      await passwordFormRef.value.validate(async (valid) => {
        if (valid) {
          passwordLoading.value = true;
          try {
            const response = await axios.put('/api/user/password', {
              oldPassword: passwordData.value.oldPassword,
              newPassword: passwordData.value.newPassword,
            });
            if (response.data && response.data.code === 200) {
              ElMessage.success('Password changed successfully! Please use your new password to login next time.');
              passwordData.value = { oldPassword: '', newPassword: '', confirmNewPassword: '' }; // Clear form
              // Optionally, force re-login
              // userStore.logout(); router.push('/login');
            } else {
              ElMessage.error(response.data.message || 'Failed to change password.');
            }
          } catch (error: any) {
            ElMessage.error(error.response?.data?.message || 'Error changing password. Please try again.');
          } finally {
            passwordLoading.value = false;
          }
        }
      });
    };
    
    // Make sure to clear timer on component unmount
    onMounted(() => {
        fetchUserProfile();
    });

    // Clear interval when component is unmounted
    import { onBeforeUnmount } from 'vue';
    onBeforeUnmount(() => {
        clearInterval(emailCodeCountdownTimer);
    });


    return {
      activeTab,
      profileFormRef,
      emailFormRef,
      passwordFormRef,
      profileData,
      emailData,
      passwordData,
      profileRules,
      emailRules,
      passwordRules,
      profileLoading,
      emailLoading,
      passwordLoading,
      submitProfileForm,
      submitEmailForm,
      submitPasswordForm,
      handleAvatarUpdate,
      currentUserEmail,
      sendEmailCode,
      isNewEmailValid,
      emailCodeSentLoading,
      emailCodeButtonText,
    };
  },
});
</script>

<style scoped>
.settings-page {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
}
.el-tab-pane {
  padding: 20px;
}
h3, h4 {
  margin-bottom: 20px;
  color: #303133;
}
.el-form-item {
  margin-bottom: 22px;
}
.el-divider {
  margin: 30px 0;
}
/* Ensure AvatarUpload component is centered and has some space */
.el-col > h3 + .avatar-uploader-container {
    margin-top: 10px;
    margin-bottom: 20px;
}
</style>
