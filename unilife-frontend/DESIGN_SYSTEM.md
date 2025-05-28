# UniLife 设计系统

## 概述

UniLife 采用现代化的紫色主题设计系统，注重用户体验和视觉一致性。设计风格融合了玻璃态（Glassmorphism）效果、流体动画和响应式布局。

## 🎨 色彩系统

### 主色调（紫色系）
```css
--primary-50: #faf5ff;    /* 最浅紫色 */
--primary-100: #f3e8ff;   /* 很浅紫色 */
--primary-200: #e9d5ff;   /* 浅紫色 */
--primary-300: #d8b4fe;   /* 中浅紫色 */
--primary-400: #c084fc;   /* 中紫色 */
--primary-500: #a855f7;   /* 标准紫色（主色） */
--primary-600: #9333ea;   /* 中深紫色 */
--primary-700: #7c3aed;   /* 深紫色 */
--primary-800: #6b21a8;   /* 很深紫色 */
--primary-900: #581c87;   /* 最深紫色 */
--primary-950: #3b0764;   /* 极深紫色 */
```

### 中性色系
```css
--gray-50: #f9fafb;     /* 背景色 */
--gray-100: #f3f4f6;    /* 浅背景 */
--gray-200: #e5e7eb;    /* 边框色 */
--gray-300: #d1d5db;    /* 分割线 */
--gray-400: #9ca3af;    /* 占位符 */
--gray-500: #6b7280;    /* 辅助文字 */
--gray-600: #4b5563;    /* 次要文字 */
--gray-700: #374151;    /* 主要文字 */
--gray-800: #1f2937;    /* 标题 */
--gray-900: #111827;    /* 黑色文字 */
```

### 语义化颜色
```css
--success: #10b981;     /* 成功绿 */
--warning: #f59e0b;     /* 警告橙 */
--error: #ef4444;       /* 错误红 */
--info: #3b82f6;        /* 信息蓝 */
```

### 渐变色
```css
--gradient-primary: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
--gradient-secondary: linear-gradient(135deg, var(--primary-200) 0%, var(--primary-400) 100%);
--gradient-bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```

## 📐 间距系统

```css
--spacing-xs: 4px;      /* 8px */
--spacing-sm: 8px;      /* 12px */
--spacing-md: 16px;     /* 16px */
--spacing-lg: 24px;     /* 24px */
--spacing-xl: 32px;     /* 32px */
--spacing-2xl: 48px;    /* 48px */
--spacing-3xl: 64px;    /* 64px */
--spacing-4xl: 80px;    /* 80px */
```

## 🔤 字体系统

### 字体族
- 主字体：系统默认字体栈
- 代码字体：'JetBrains Mono', 'Consolas', monospace

### 字体大小
```css
--text-xs: 12px;        /* 小号文字 */
--text-sm: 14px;        /* 次要文字 */
--text-base: 16px;      /* 基础文字 */
--text-lg: 18px;        /* 大号文字 */
--text-xl: 20px;        /* 标题 */
--text-2xl: 24px;       /* 大标题 */
--text-3xl: 30px;       /* 页面标题 */
--text-4xl: 36px;       /* 主标题 */
```

### 字重
```css
--font-light: 300;      /* 细体 */
--font-normal: 400;     /* 常规 */
--font-medium: 500;     /* 中等 */
--font-semibold: 600;   /* 半粗 */
--font-bold: 700;       /* 粗体 */
--font-black: 900;      /* 黑体 */
```

## 🎭 阴影系统

```css
--shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
--shadow-base: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
--shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
--shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
--shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
--shadow-purple: 0 20px 40px rgba(168, 85, 247, 0.4);
```

## 📏 圆角系统

```css
--radius-sm: 6px;       /* 小圆角 */
--radius-base: 8px;     /* 基础圆角 */
--radius-md: 12px;      /* 中等圆角 */
--radius-lg: 16px;      /* 大圆角 */
--radius-xl: 20px;      /* 超大圆角 */
--radius-2xl: 24px;     /* 特大圆角 */
--radius-full: 9999px;  /* 完全圆角 */
```

## ⚡ 动画系统

### 过渡时间
```css
--transition-fast: 150ms ease-in-out;
--transition-base: 250ms ease-in-out;
--transition-slow: 350ms ease-in-out;
```

### 关键帧动画
- `animate-fade-in`: 淡入效果
- `animate-fade-in-up`: 从下方淡入
- `animate-float`: 浮动效果
- `animate-glow`: 发光效果
- `animate-pulse`: 脉冲效果

## 🧱 组件规范

### 玻璃态效果（Glass）
```css
.glass {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
```

### 按钮组件
- **主要按钮**: 紫色渐变背景，白色文字
- **次要按钮**: 透明背景，边框，白色文字
- **文字按钮**: 无背景，仅文字
- **状态**: hover、active、disabled

### 表单组件
- **输入框**: 玻璃态背景，紫色焦点边框
- **标签**: 白色文字，适当间距
- **验证**: 错误状态使用红色，成功状态使用绿色

### 卡片组件
- **背景**: 玻璃态效果
- **阴影**: 悬停时添加紫色阴影
- **圆角**: 使用--radius-lg
- **间距**: 内边距32px

## 📱 响应式断点

```css
/* 移动设备 */
@media (max-width: 480px) { }

/* 平板设备 */
@media (max-width: 768px) { }

/* 桌面设备 */
@media (max-width: 1024px) { }

/* 大屏设备 */
@media (min-width: 1200px) { }
```

## 🎯 设计原则

### 1. 视觉层次
- 使用字体大小和颜色对比建立清晰的信息层次
- 重要内容使用紫色高亮
- 辅助信息使用较淡的颜色

### 2. 一致性
- 所有交互元素使用统一的视觉语言
- 保持间距、圆角、阴影的一致性
- 动画时长和缓动函数保持统一

### 3. 可访问性
- 确保颜色对比度符合WCAG标准
- 提供焦点状态指示
- 支持键盘导航

### 4. 现代感
- 使用玻璃态效果增强层次感
- 流畅的动画过渡
- 适当的留白和间距

## 🚀 使用指南

### 引入样式
在main.ts中引入全局样式：
```typescript
import './styles/globals.css'
```

### 使用CSS变量
在组件中使用预定义的CSS变量：
```css
.custom-button {
  background: var(--gradient-primary);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  transition: var(--transition-base);
}
```

### 玻璃态组件
添加glass类名获得玻璃态效果：
```html
<div class="glass">内容</div>
```

### 渐变文字
使用gradient-text类名：
```html
<h1 class="gradient-text">标题</h1>
```

## 📚 组件库

当前已实现的页面组件：
- **HomeView**: 首页，包含hero section、功能介绍、使用步骤
- **LoginView**: 登录页面，玻璃态表单设计
- **RegisterView**: 注册页面，多步骤表单设计
- **ForumView**: 论坛页面（基础结构）

### 下一步计划
- 完善论坛组件（帖子列表、详情页）
- 资源管理组件
- 课程表组件
- 用户个人资料组件
- 通用组件库（模态框、通知、加载状态等） 