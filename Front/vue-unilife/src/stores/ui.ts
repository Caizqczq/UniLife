import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUIStore = defineStore('ui', () => {
  // 状态
  const isMobileView = ref(false);
  const isSidebarCollapsed = ref(false);
  const isDarkMode = ref(false);
  const isLoading = ref(false);
  const loadingText = ref('加载中...');
  
  // 检测是否为移动视图
  const checkMobileView = () => {
    isMobileView.value = window.innerWidth < 768;
  };
  
  // 切换侧边栏状态
  const toggleSidebar = () => {
    isSidebarCollapsed.value = !isSidebarCollapsed.value;
  };
  
  // 切换暗黑模式
  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value;
    
    // 应用暗黑模式到文档
    if (isDarkMode.value) {
      document.documentElement.classList.add('dark-mode');
    } else {
      document.documentElement.classList.remove('dark-mode');
    }
  };
  
  // 设置加载状态
  const setLoading = (loading: boolean, text?: string) => {
    isLoading.value = loading;
    if (text) {
      loadingText.value = text;
    }
  };
  
  // 初始化
  const initialize = () => {
    // 检测移动视图
    checkMobileView();
    window.addEventListener('resize', checkMobileView);
    
    // 检测系统暗黑模式偏好
    const prefersDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if (prefersDarkMode) {
      toggleDarkMode();
    }
  };
  
  return {
    isMobileView,
    isSidebarCollapsed,
    isDarkMode,
    isLoading,
    loadingText,
    toggleSidebar,
    toggleDarkMode,
    setLoading,
    initialize
  };
});
