<template>
  <div class="forgot-password-page">
    <el-card class="forgot-password-card">
      <template #header>
        <div class="card-header">
          <span>Forgot Password</span>
        </div>
      </template>
      <el-form ref="forgotPasswordForm" :model="formData" :rules="formRules" @submit.prevent="handleForgotPassword">
        <el-form-item label="Email" prop="email">
          <el-input v-model="formData.email" placeholder="Enter your email" :disabled="isCodeSent && !isCodeVerified"></el-input>
        </el-form-item>

        <div v-if="!isCodeSent || isCodeVerified">
            <el-form-item  v-if="!isCodeVerified" class="action-button">
                <el-button type="primary" @click="sendResetCode" :loading="codeLoading" :disabled="!isEmailValid">
                    Send Reset Code
                </el-button>
            </el-form-item>
        </div>


        <div v-if="isCodeSent && !isCodeVerified">
          <el-form-item label="Verification Code" prop="code">
            <el-input v-model="formData.code" placeholder="Enter code from email">
               <template #append>
                 <el-button @click="sendResetCode" :disabled="isResendDisabled">
                    {{ isResendDisabled ? `Resend (${countdown})` : 'Resend Code' }}
                  </el-button>
                </template>
            </el-input>
          </el-form-item>
           <el-form-item class="action-button">
             <el-button type="info" @click="verifyCode" :loading="verifyLoading">Verify Code</el-button>
           </el-form-item>
        </div>


        <div v-if="isCodeVerified">
          <el-form-item label="New Password" prop="newPassword">
            <el-input v-model="formData.newPassword" type="password" placeholder="Enter new password"></el-input>
          </el-form-item>
          <el-form-item label="Confirm New Password" prop="confirmNewPassword">
            <el-input v-model="formData.confirmNewPassword" type="password" placeholder="Confirm new password"></el-input>
          </el-form-item>
          <el-form-item class="action-button">
            <el-button type="primary" native-type="submit" :loading="resetLoading">Reset Password</el-button>
          </el-form-item>
        </div>
      </el-form>
      <div class="links">
        <router-link to="/login">Back to Login</router-link>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ForgotPasswordPage',
  setup() {
    const router = useRouter();
    const forgotPasswordForm = ref(null);
    const formData = ref({
      email: '',
      code: '',
      newPassword: '',
      confirmNewPassword: '',
    });

    const codeLoading = ref(false);
    const verifyLoading = ref(false);
    const resetLoading = ref(false);
    const isCodeSent = ref(false);
    const isCodeVerified = ref(false); // New state to track if code has been verified
    const countdown = ref(60);
    const isResendDisabled = ref(false);


    const validatePassConfirm = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('Please confirm your new password'));
      } else if (value !== formData.value.newPassword) {
        callback(new Error("Passwords don't match!"));
      } else {
        callback();
      }
    };

    const formRules = ref({
      email: [
        { required: true, message: 'Please input email address', trigger: 'blur' },
        { type: 'email', message: 'Please input correct email address', trigger: ['blur', 'change'] },
      ],
      code: [
        { required: true, message: 'Please input verification code', trigger: 'blur' },
      ],
      newPassword: [
        { required: true, message: 'Please input new password', trigger: 'blur' },
        { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' },
      ],
      confirmNewPassword: [
        { required: true, validator: validatePassConfirm, trigger: 'blur' },
      ],
    });

    const isEmailValid = computed(() => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(formData.value.email);
    });

    const startCountdown = () => {
      isResendDisabled.value = true;
      countdown.value = 60;
      const timer = setInterval(() => {
        if (countdown.value > 0) {
          countdown.value--;
        } else {
          clearInterval(timer);
          isResendDisabled.value = false;
        }
      }, 1000);
    };

    const sendResetCode = async () => {
      // @ts-ignore
      forgotPasswordForm.value.validateField('email', async (isValid: boolean) => {
        if (isValid) {
          codeLoading.value = true;
          try {
            await axios.post('/api/user/reset-password/code', { email: formData.value.email });
            ElMessage.success('Reset code sent to your email.');
            isCodeSent.value = true;
            isCodeVerified.value = false; // Reset verification status
            startCountdown();
          } catch (error) {
            ElMessage.error('Failed to send reset code.');
            console.error(error);
          } finally {
            codeLoading.value = false;
          }
        } else {
            ElMessage.error('Please enter a valid email address.');
        }
      });
    };

    const verifyCode = async () => {
        // @ts-ignore
        forgotPasswordForm.value.validateField(['email', 'code'], async (isValid: boolean) => {
        if (isValid) {
          verifyLoading.value = true;
          try {
            // This is a placeholder. The backend docs don't specify a separate verify code endpoint for password reset.
            // We assume that if the code is correct, the reset password call will succeed.
            // For a better UX, a dedicated verification endpoint would be ideal.
            // Simulating verification for now.
            // In a real scenario, you might not have a separate verify step before setting new password.
            // The PUT /api/user/reset-password itself verifies the code.
            // So, this step could be just a UI state change.
            ElMessage.success('Code appears valid. Please set your new password.');
            isCodeVerified.value = true;
          } catch (error) {
            ElMessage.error('Invalid verification code.');
            console.error(error);
          } finally {
            verifyLoading.value = false;
          }
        } else {
            ElMessage.error('Please enter your email and a valid code.');
        }
      });
    };


    const handleForgotPassword = async () => {
      if (!forgotPasswordForm.value) return;
      // @ts-ignore
      forgotPasswordForm.value.validate(async (valid: boolean) => {
        if (valid) {
          if (!isCodeVerified.value) {
            ElMessage.error('Please verify your code first.');
            return;
          }
          resetLoading.value = true;
          try {
            await axios.put('/api/user/reset-password', {
              email: formData.value.email,
              code: formData.value.code,
              newPassword: formData.value.newPassword,
            });
            ElMessage.success('Password reset successful! Please login with your new password.');
            router.push('/login');
          } catch (error) {
            ElMessage.error('Password reset failed. Please try again.');
            console.error(error);
          } finally {
            resetLoading.value = false;
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };

    return {
      forgotPasswordForm,
      formData,
      formRules,
      handleForgotPassword,
      sendResetCode,
      verifyCode,
      codeLoading,
      verifyLoading,
      resetLoading,
      isCodeSent,
      isCodeVerified,
      isEmailValid,
      countdown,
      isResendDisabled,
    };
  },
});
</script>

<style scoped>
.forgot-password-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.forgot-password-card {
  width: 450px;
}
.card-header {
  text-align: center;
  font-size: 20px;
}
.links {
  margin-top: 10px;
  text-align: center;
}
.action-button {
  display: flex;
  justify-content: center;
  width: 100%;
}
.action-button .el-button {
    width: 100%;
}
</style>
