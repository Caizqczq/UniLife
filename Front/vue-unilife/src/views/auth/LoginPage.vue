<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>Login</span>
        </div>
      </template>
      <el-form ref="loginForm" :model="loginData" :rules="loginRules" @submit.prevent="handleLogin">
        <el-form-item label="Email" prop="email">
          <el-input v-model="loginData.email" placeholder="Enter your email"></el-input>
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="loginData.password" type="password" placeholder="Enter your password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">Login</el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/register">Create an account</router-link>
        <router-link to="/forgot-password">Forgot password?</router-link>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'LoginPage',
  setup() {
    const router = useRouter();
    const loginForm = ref(null);
    const loginData = ref({
      email: '',
      password: '',
    });
    const loading = ref(false);

    const loginRules = {
      email: [
        { required: true, message: 'Please input email address', trigger: 'blur' },
        { type: 'email', message: 'Please input correct email address', trigger: ['blur', 'change'] },
      ],
      password: [
        { required: true, message: 'Please input password', trigger: 'blur' },
      ],
    };

    const handleLogin = async () => {
      if (!loginForm.value) return;
      // @ts-ignore
      loginForm.value.validate(async (valid: boolean) => {
        if (valid) {
          loading.value = true;
          try {
            const response = await axios.post('/api/auth/login', {
              email: loginData.value.email,
              password: loginData.value.password,
            });
            localStorage.setItem('token', response.data.token);
            ElMessage.success('Login successful');
            router.push('/'); // Redirect to homepage
          } catch (error) {
            ElMessage.error('Login failed. Please check your credentials.');
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
      loginForm,
      loginData,
      loginRules,
      handleLogin,
      loading,
    };
  },
});
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
  font-size: 20px;
}
.links {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}
</style>
