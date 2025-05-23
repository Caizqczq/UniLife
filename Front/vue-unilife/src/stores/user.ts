import { defineStore } from 'pinia';
import { ref } from 'vue';
import userApi from '../api/user';
import type { UserInfo, UpdatePasswordParams } from '../api/user';
import { ElMessage } from 'element-plus';

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<UserInfo | null>(null);
  const isLoggedIn = ref<boolean>(!!token.value);
  const loading = ref<boolean>(false);

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
    isLoggedIn.value = true;
  };

  // 清除token
  const clearToken = () => {
    token.value = '';
    localStorage.removeItem('token');
    isLoggedIn.value = false;
  };

  // 登录
  const login = async (email: string, password: string) => {
    try {
      loading.value = true;
      const res = await userApi.login({ email, password });

      if (res.code === 200 && res.data.token) {
        setToken(res.data.token);
        ElMessage.success('登录成功');
        return true;
      }

      return false;
    } catch (error) {
      console.error('登录失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 通过验证码登录
  const loginWithCode = async (email: string, code: string) => {
    try {
      loading.value = true;
      const res = await userApi.verifyEmailCode({ email, code });

      if (res.code === 200 && res.data.token) {
        setToken(res.data.token);
        ElMessage.success('登录成功');
        return true;
      }

      return false;
    } catch (error) {
      console.error('登录失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 注册
  const register = async (email: string, password: string, code: string) => {
    try {
      loading.value = true;

      const res = await userApi.register({ email, password, code });

      if (res.code === 200 && res.data.token) {
        setToken(res.data.token);
        ElMessage.success('注册成功');
        return true;
      }

      return false;
    } catch (error) {
      console.error('注册失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 登出
  const logout = () => {
    clearToken();
    userInfo.value = null;
    ElMessage.success('已退出登录');
  };

  // 获取用户信息
  const fetchUserInfo = async () => {
    if (!token.value) return null;

    try {
      loading.value = true;
      const res = await userApi.getUserInfo();

      if (res.code === 200) {
        userInfo.value = res.data;
        return res.data;
      }

      return null;
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 更新用户资料
  const updateProfile = async (data: {
    username?: string;
    bio?: string;
    gender?: number;
    department?: string;
    major?: string;
    grade?: string;
  }) => {
    try {
      loading.value = true;
      const params: any = {};
      if (data.username !== undefined) params.username = data.username;
      if (data.bio !== undefined) params.bio = data.bio;
      if (data.gender !== undefined) params.gender = data.gender;
      if (data.department !== undefined) params.department = data.department;
      if (data.major !== undefined) params.major = data.major;
      if (data.grade !== undefined) params.grade = data.grade;

      const res = await userApi.updateProfile(params);

      if (res.code === 200) {
        // 更新本地用户信息
        if (userInfo.value) {
          if (data.username !== undefined) userInfo.value.username = data.username;
          if (data.bio !== undefined) userInfo.value.bio = data.bio;
          if (data.gender !== undefined) userInfo.value.gender = data.gender;
          if (data.department !== undefined) userInfo.value.department = data.department;
          if (data.major !== undefined) userInfo.value.major = data.major;
          if (data.grade !== undefined) userInfo.value.grade = data.grade;
        }

        ElMessage.success('个人资料更新成功');
        return true;
      }

      return false;
    } catch (error) {
      console.error('更新个人资料失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 更新用户密码
  const updatePassword = async (data: {
    newPassword?: string;
    code?: string;
  }) => {
    try {
      loading.value = true;
      const params: Partial<UpdatePasswordParams> = {};
      if (data.newPassword) params.newPassword = data.newPassword;
      if (data.code) params.code = data.code;

      const res = await userApi.updatePassword(params as UpdatePasswordParams);

      if (res.code === 200) {
        ElMessage.success('密码修改成功');
        return true;
      }

      return false;
    } catch (error) {
      console.error('修改密码失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  return {
    token,
    userInfo,
    isLoggedIn,
    loading,
    login,
    loginWithCode,
    register,
    logout,
    fetchUserInfo,
    updateProfile,
    updatePassword
  };
});
