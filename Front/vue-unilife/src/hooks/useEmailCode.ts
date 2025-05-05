import { ref } from 'vue';
import { userApi } from '../api';
import { ElMessage } from 'element-plus';

export function useEmailCode() {
  const isSending = ref(false);
  const countdown = ref(0);
  let timer: number | null = null;

  // 发送邮箱验证码
  const sendEmailCode = async (email: string) => {
    if (isSending.value) return;
    
    if (!email) {
      ElMessage.warning('请输入邮箱地址');
      return;
    }
    
    try {
      isSending.value = true;
      const res = await userApi.getEmailCode({ email });
      
      if (res.code === 200) {
        ElMessage.success('验证码已发送，请查收邮件');
        startCountdown();
      }
      
      return res;
    } catch (error) {
      console.error('发送验证码失败:', error);
      ElMessage.error('发送验证码失败，请稍后重试');
    } finally {
      isSending.value = false;
    }
  };

  // 验证邮箱验证码
  const verifyEmailCode = async (email: string, code: string) => {
    if (!email || !code) {
      ElMessage.warning('请输入邮箱和验证码');
      return;
    }
    
    try {
      const res = await userApi.verifyEmailCode({ email, code });
      return res;
    } catch (error) {
      console.error('验证码验证失败:', error);
      throw error;
    }
  };

  // 开始倒计时
  const startCountdown = () => {
    countdown.value = 60;
    
    if (timer) {
      clearInterval(timer);
    }
    
    timer = window.setInterval(() => {
      if (countdown.value > 0) {
        countdown.value--;
      } else {
        if (timer) {
          clearInterval(timer);
          timer = null;
        }
      }
    }, 1000);
  };

  return {
    isSending,
    countdown,
    sendEmailCode,
    verifyEmailCode
  };
}
