<template>
  <div class="home-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
      <div class="geometric-bg"></div>
    </div>
    
    <!-- 导航栏 -->
    <nav class="navbar glass-light">
      <div class="nav-container">
        <div class="nav-brand">
          <div class="logo-circle">
            <i class="el-icon-star-filled gradient-text"></i>
          </div>
          <span class="brand-name gradient-text">UniLife</span>
        </div>
        <div class="nav-actions">
          <router-link to="/login" class="nav-button login-btn">登录</router-link>
          <router-link to="/register" class="nav-button register-btn">注册</router-link>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-badge animate-fade-in">
          <span class="badge-text">🎓 为大学生打造的生活平台</span>
        </div>
        
        <h1 class="hero-title animate-fade-in-up delay-200">
          <span class="title-line">连接每一个</span>
          <span class="title-line gradient-text">精彩的大学时光</span>
        </h1>
        
        <p class="hero-description animate-fade-in-up delay-300">
          UniLife 是专为大学生设计的综合性平台，集成论坛交流、资源分享、课程管理于一体，
          让你的大学生活更加充实精彩。加入我们，开启你的数字化大学之旅。
        </p>
        
        <div class="hero-actions animate-fade-in-up delay-400">
          <router-link to="/register" class="cta-button primary animate-glow">
            <el-icon><Star /></el-icon>
            立即开始
          </router-link>
          <a href="#features" class="cta-button secondary">
            <el-icon><ArrowDown /></el-icon>
            了解更多
          </a>
        </div>
        
        <!-- 用户统计 -->
        <div class="stats-container animate-fade-in-up delay-500">
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">活跃用户</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">500+</div>
            <div class="stat-label">讨论话题</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-number">200+</div>
            <div class="stat-label">学习资源</div>
          </div>
        </div>
      </div>
      
      <!-- Hero图片 -->
      <div class="hero-visual">
        <div class="visual-container animate-float">
          <div class="mockup-browser glass-purple">
            <div class="browser-header">
              <div class="browser-dots">
                <span class="dot red"></span>
                <span class="dot yellow"></span>
                <span class="dot green"></span>
              </div>
              <div class="browser-url">unilife.com</div>
            </div>
            <div class="browser-content">
              <div class="mock-forum">
                <div class="forum-post" v-for="i in 3" :key="i">
                  <div class="post-avatar"></div>
                  <div class="post-content">
                    <div class="post-title"></div>
                    <div class="post-meta"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能特色 -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title gradient-text">强大功能，一应俱全</h2>
          <p class="section-description">为大学生活的每个方面提供完美解决方案</p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card card-light animate-fade-in-up" v-for="(feature, index) in features" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="feature-icon">
              <el-icon><component :is="feature.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 如何使用 -->
    <section class="how-it-works-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title gradient-text">简单三步，开启精彩</h2>
          <p class="section-description">快速上手，立即享受便捷的大学生活</p>
        </div>
        
        <div class="steps-container">
          <div class="step-item animate-fade-in-up" v-for="(step, index) in steps" :key="index" :style="{ animationDelay: `${index * 0.2}s` }">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <h3 class="step-title">{{ step.title }}</h3>
              <p class="step-description">{{ step.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区域 -->
    <section class="cta-section">
      <div class="cta-container glass-purple">
        <div class="cta-content">
          <h2 class="cta-title gradient-text">准备好开始了吗？</h2>
          <p class="cta-description">加入数千名大学生，一起探索更精彩的校园生活</p>
          <div class="cta-actions">
            <router-link to="/register" class="cta-button primary large animate-glow">
              <el-icon><Star /></el-icon>
              免费注册
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="logo-circle">
              <i class="el-icon-star-filled gradient-text"></i>
            </div>
            <span class="brand-name gradient-text">UniLife</span>
          </div>
          <p class="footer-description">为大学生打造的综合性生活平台</p>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4>产品</h4>
            <a href="#">论坛</a>
            <a href="#">资源库</a>
            <a href="#">课程表</a>
            <a href="#">日程管理</a>
          </div>
          <div class="link-group">
            <h4>支持</h4>
            <a href="#">帮助中心</a>
            <a href="#">用户协议</a>
            <a href="#">隐私政策</a>
          </div>
          <div class="link-group">
            <h4>联系我们</h4>
            <a href="#">反馈建议</a>
            <a href="#">商务合作</a>
            <a href="#">关于我们</a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 UniLife. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { 
  Star, 
  ArrowDown, 
  ChatDotRound, 
  FolderOpened, 
  Calendar, 
  Clock,
  UserFilled,
  TrendCharts 
} from '@element-plus/icons-vue'

// 功能特色数据
const features = ref([
  {
    icon: ChatDotRound,
    title: '活跃论坛',
    description: '与同学们自由讨论学习、生活话题，分享经验与见解，建立有意义的连接'
  },
  {
    icon: FolderOpened,
    title: '资源共享',
    description: '上传和下载学习资料、课件、笔记等，让知识在同学之间自由流转'
  },
  {
    icon: Calendar,
    title: '课程管理',
    description: '智能课程表管理，清晰展示课程安排，再也不用担心忘记上课时间'
  },
  {
    icon: Clock,
    title: '日程管理',
    description: '个人日程安排，重要事项提醒，让你的大学生活井井有条'
  },
  {
    icon: UserFilled,
    title: '个人中心',
    description: '个性化用户资料，记录学习轨迹，展示你的大学成长历程'
  },
  {
    icon: TrendCharts,
    title: '数据统计',
    description: '学习数据可视化，了解自己的学习习惯，制定更好的学习计划'
  }
])

// 使用步骤数据
const steps = ref([
  {
    title: '注册账号',
    description: '使用学校邮箱快速注册，验证学生身份'
  },
  {
    title: '完善资料',
    description: '填写个人信息，加入院系和班级'
  },
  {
    title: '开始使用',
    description: '探索论坛、分享资源、管理课程'
  }
])
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: var(--gradient-bg);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  background: linear-gradient(135deg, var(--primary-200), var(--primary-300));
  border-radius: 50%;
  animation: float 12s ease-in-out infinite;
  opacity: 0.6;
}

.shape-1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation-delay: 4s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 8s;
}

.shape-4 {
  width: 60px;
  height: 60px;
  top: 30%;
  right: 40%;
  animation-delay: 2s;
}

.geometric-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(184, 169, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(231, 227, 255, 0.15) 0%, transparent 50%);
  opacity: 0.8;
}

/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 0;
  border-bottom: 1px solid var(--gray-200);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-circle {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: var(--shadow-light);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav-actions {
  display: flex;
  gap: 12px;
}

.nav-button {
  padding: 10px 20px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: var(--transition-base);
  font-size: 14px;
}

.login-btn {
  color: var(--gray-700);
  background: white;
  border: 1px solid var(--gray-200);
}

.login-btn:hover {
  background: var(--primary-50);
  border-color: var(--primary-300);
  color: var(--primary-600);
}

.register-btn {
  color: white;
  background: var(--gradient-primary);
  box-shadow: var(--shadow-light);
}

.register-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-purple);
}

/* Hero Section */
.hero-section {
  min-height: 100vh;
  display: flex;
  align-items: center;
  padding: 120px 24px 80px;
  position: relative;
  z-index: 10;
}

.hero-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid var(--primary-200);
  border-radius: 50px;
  padding: 8px 16px;
  margin-bottom: 32px;
  backdrop-filter: blur(10px);
  box-shadow: var(--shadow-sm);
}

.badge-text {
  color: var(--gray-700);
  font-size: 14px;
  font-weight: 600;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1.1;
  margin-bottom: 24px;
  letter-spacing: -0.02em;
}

.title-line {
  display: block;
  color: var(--gray-800);
}

.hero-description {
  font-size: 18px;
  line-height: 1.7;
  color: var(--gray-600);
  margin-bottom: 40px;
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 60px;
}

.cta-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 16px 32px;
  border-radius: 16px;
  text-decoration: none;
  font-weight: 600;
  font-size: 16px;
  transition: var(--transition-base);
}

.cta-button.primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-light);
}

.cta-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-purple);
}

.cta-button.secondary {
  background: white;
  color: var(--primary-600);
  border: 2px solid var(--primary-300);
  box-shadow: var(--shadow-sm);
}

.cta-button.secondary:hover {
  background: var(--primary-50);
  border-color: var(--primary-400);
  transform: translateY(-1px);
}

.cta-button.large {
  padding: 20px 40px;
  font-size: 18px;
}

/* 统计数据 */
.stats-container {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  backdrop-filter: blur(20px);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: var(--primary-600);
  margin-bottom: 4px;
}

.stat-label {
  color: var(--gray-600);
  font-size: 14px;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--gray-200);
}

/* Hero Visual */
.hero-visual {
  position: relative;
}

.visual-container {
  position: relative;
  max-width: 500px;
  margin: 0 auto;
}

.mockup-browser {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-xl);
}

.browser-header {
  padding: 16px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  gap: 16px;
}

.browser-dots {
  display: flex;
  gap: 8px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.dot.red { background: #ff5f57; }
.dot.yellow { background: #ffbd2e; }
.dot.green { background: #28ca42; }

.browser-url {
  background: var(--gray-100);
  border-radius: 8px;
  padding: 6px 12px;
  color: var(--gray-600);
  font-size: 12px;
  flex: 1;
  text-align: center;
}

.browser-content {
  padding: 24px;
  height: 300px;
  background: rgba(255, 255, 255, 0.95);
}

.mock-forum {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.forum-post {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--gray-50);
  border-radius: 12px;
  border: 1px solid var(--gray-200);
}

.post-avatar {
  width: 40px;
  height: 40px;
  background: var(--gradient-primary);
  border-radius: 50%;
}

.post-content {
  flex: 1;
}

.post-title {
  height: 14px;
  background: var(--gray-300);
  border-radius: 4px;
  margin-bottom: 8px;
  width: 85%;
}

.post-meta {
  height: 10px;
  background: var(--gray-200);
  border-radius: 4px;
  width: 60%;
}

/* 功能特色 */
.features-section {
  padding: 120px 24px;
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.5);
}

.section-container {
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 80px;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.section-description {
  font-size: 18px;
  color: var(--gray-600);
  max-width: 600px;
  margin: 0 auto;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 32px;
}

.feature-card {
  text-align: center;
  transition: var(--transition-base);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  font-size: 32px;
  color: white;
  box-shadow: var(--shadow-light);
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 16px;
}

.feature-description {
  color: var(--gray-600);
  line-height: 1.6;
}

/* 使用步骤 */
.how-it-works-section {
  padding: 120px 24px;
  background: var(--gradient-purple-light);
}

.steps-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 48px;
  max-width: 1000px;
  margin: 0 auto;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  text-align: left;
}

.step-number {
  width: 60px;
  height: 60px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
  box-shadow: var(--shadow-light);
}

.step-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--gray-800);
  margin-bottom: 8px;
}

.step-description {
  color: var(--gray-600);
  line-height: 1.6;
}

/* CTA区域 */
.cta-section {
  padding: 120px 24px;
}

.cta-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 80px 60px;
  border-radius: 32px;
  text-align: center;
}

.cta-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.cta-description {
  font-size: 18px;
  color: var(--gray-600);
  margin-bottom: 40px;
}

/* 页脚 */
.footer {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-top: 1px solid var(--gray-200);
  padding: 80px 24px 40px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 60px;
  margin-bottom: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.footer-description {
  color: var(--gray-600);
  line-height: 1.6;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.link-group h4 {
  color: var(--gray-800);
  font-weight: 700;
  margin-bottom: 16px;
}

.link-group a {
  display: block;
  color: var(--gray-600);
  text-decoration: none;
  margin-bottom: 8px;
  transition: var(--transition-fast);
}

.link-group a:hover {
  color: var(--primary-600);
}

.footer-bottom {
  border-top: 1px solid var(--gray-200);
  padding-top: 40px;
  text-align: center;
  color: var(--gray-500);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .hero-title {
    font-size: 3rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .cta-title {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .nav-container {
    padding: 0 16px;
  }
  
  .hero-section {
    padding: 100px 16px 60px;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .stats-container {
    flex-direction: column;
    gap: 20px;
  }
  
  .stat-divider {
    width: 80%;
    height: 1px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .footer-container {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .cta-container {
    padding: 60px 40px;
  }
  
  .steps-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .brand-name {
    display: none;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 1.75rem;
  }
  
  .cta-title {
    font-size: 1.75rem;
  }
  
  .cta-container {
    padding: 40px 24px;
  }
}
</style> 