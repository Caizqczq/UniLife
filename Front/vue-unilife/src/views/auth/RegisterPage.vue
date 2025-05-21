<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>Register</span>
        </div>
      </template>
      <el-form ref="registerForm" :model="registerData" :rules="registerRules" @submit.prevent="handleRegister">
        <el-form-item label="Username" prop="username">
          <el-input v-model="registerData.username" placeholder="Enter your username"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="registerData.email" placeholder="Enter your email"></el-input>
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="registerData.password" type="password" placeholder="Enter your password"></el-input>
        </el-form-item>
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input v-model="registerData.confirmPassword" type="password" placeholder="Confirm your password"></el-input>
        </el-form-item>
        <el-form-item label="Verification Code" prop="verificationCode">
          <el-input v-model="registerData.verificationCode" placeholder="Enter code from email">
            <template #append>
              <el-button @click="sendVerificationCode" :disabled="isCodeSent || !isEmailValid">
                {{ isCodeSent ? `Resend (${countdown})` : 'Get Code' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">Register</el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/login">Already have an account? Login</router-link>
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
  name: 'RegisterPage',
  setup() {
    const router = useRouter();
    const registerForm = ref(null);
    const registerData = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      verificationCode: '',
    });
    const loading = ref(false);
    const isCodeSent = ref(false);
    const countdown = ref(60);

    const validatePassConfirm = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error('Please confirm your password'));
      } else if (value !== registerData.value.password) {
        callback(new Error("Passwords don't match!"));
      } else {
        callback();
      }
    };

    const registerRules = {
      username: [
        { required: true, message: 'Please input username', trigger: 'blur' },
      ],
      email: [
        { required: true, message: 'Please input email address', trigger: 'blur' },
        { type: 'email', message: 'Please input correct email address', trigger: ['blur', 'change'] },
      ],
      password: [
        { required: true, message: 'Please input password', trigger: 'blur' },
        { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' },
      ],
      confirmPassword: [
        { required: true, validator: validatePassConfirm, trigger: 'blur' },
      ],
      verificationCode: [
        { required: true, message: 'Please input verification code', trigger: 'blur' },
      ],
    };

    const isEmailValid = computed(() => {
      // Basic email regex for enabling "Get Code" button
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(registerData.value.email);
    });

    const startCountdown = () => {
      isCodeSent.value = true;
      countdown.value = 60;
      const timer = setInterval(() => {
        if (countdown.value > 0) {
          countdown.value--;
        } else {
          clearInterval(timer);
          isCodeSent.value = false;
        }
      }, 1000);
    };

    const sendVerificationCode = async () => {
      if (!isEmailValid.value) {
        ElMessage.error('Please enter a valid email address.');
        return;
      }
      try {
        loading.value = true;
        await axios.post('/api/auth/register/code', { email: registerData.value.email });
        ElMessage.success('Verification code sent to your email.');
        startCountdown();
      } catch (error) {
        ElMessage.error('Failed to send verification code.');
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    const handleRegister = async () => {
      if (!registerForm.value) return;
      // @ts-ignore
      registerForm.value.validate(async (valid: boolean) => {
        if (valid) {
          loading.value = true;
          try {
            await axios.post('/api/auth/register', {
              username: registerData.value.username,
              email: registerData.value.email,
              password: registerData.value.password,
              verificationCode: registerData.value.verificationCode,
            });
            ElMessage.success('Registration successful! Please login.');
            router.push('/login');
          } catch (error) {
            ElMessage.error('Registration failed. Please try again.');
            console.error(error);
          } finally {
            loading.value = false;
          }
        } else {
          ElMessage.error('Please correct the errors in the form.');
          return false;
        }
      });
    };

    return {
      registerForm,
      registerData,
      registerRules,
      handleRegister,
      loading,
      sendVerificationCode,
      isCodeSent,
      countdown,
      isEmailValid,
    };
  },
});
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.register-card {
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
</style>
