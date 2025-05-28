# UniLife Frontend

## 🎓 项目简介

UniLife 是一个专为大学生设计的综合性生活平台，提供论坛交流、资源分享、课程管理等功能。本项目是 UniLife 的前端部分，采用现代化的技术栈和紫色主题设计。

## ✨ 特性

- 🎨 **现代化UI设计**: 紫色主题 + 玻璃态效果
- 📱 **响应式布局**: 完美适配桌面端和移动端
- 🚀 **高性能**: Vue 3 + Vite 构建，极快的开发体验
- 🔒 **类型安全**: 完整的TypeScript支持
- 🎯 **用户体验**: 流畅的动画和交互效果
- 🌈 **设计系统**: 统一的设计规范和组件库

## 🛠️ 技术栈

- **前端框架**: Vue 3.5
- **构建工具**: Vite 6.3
- **语言**: TypeScript 5.6
- **状态管理**: Pinia 2.2
- **路由**: Vue Router 4.4
- **UI组件**: Element Plus 2.9
- **HTTP客户端**: Axios 1.7
- **样式**: CSS3 + CSS变量
- **工具**: ESLint + Prettier

## 🚀 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装依赖

```bash
cd unilife-frontend
npm install
```

### 开发服务器

```bash
npm run dev
```

访问 http://localhost:5173 查看应用

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 📁 项目结构

```
unilife-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── index.ts       # Axios配置
│   │   └── auth.ts        # 认证相关API
│   ├── components/        # 公共组件
│   ├── layouts/           # 布局组件
│   ├── router/            # 路由配置
│   │   └── index.ts       # 主路由文件
│   ├── stores/            # 状态管理
│   │   └── user.ts        # 用户状态
│   ├── styles/            # 全局样式
│   │   └── globals.css    # 设计系统CSS变量
│   ├── types/             # TypeScript类型定义
│   │   └── index.ts       # 全局类型
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   │   ├── auth/          # 认证页面
│   │   │   ├── LoginView.vue
│   │   │   └── RegisterView.vue
│   │   ├── forum/         # 论坛页面
│   │   ├── resources/     # 资源页面
│   │   ├── schedule/      # 课程表页面
│   │   ├── profile/       # 个人资料页面
│   │   └── HomeView.vue   # 首页
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── DESIGN_SYSTEM.md       # 设计系统文档
└── README.md              # 项目说明
```

## 🎨 设计系统

我们为 UniLife 创建了完整的设计系统，包括：

- **色彩系统**: 紫色主题色彩规范
- **间距系统**: 统一的间距标准
- **字体系统**: 层次化的字体规范
- **阴影系统**: 深度感的阴影效果
- **动画系统**: 流畅的过渡动画
- **组件规范**: 可复用的UI组件

详细信息请查看 [设计系统文档](./DESIGN_SYSTEM.md)

## 📄 页面说明

### 🏠 首页 (HomeView)
- Hero区域展示平台价值主张
- 功能特色介绍（6个核心功能）
- 使用步骤说明（3步上手）
- CTA区域引导用户注册
- 响应式设计，完美适配各种设备

### 🔐 登录页面 (LoginView)
- 玻璃态设计风格
- 用户名/邮箱登录支持
- 表单验证和错误提示
- 动画效果增强用户体验
- 记住密码功能

### ✍️ 注册页面 (RegisterView)
- 完整的用户信息收集
- 邮箱验证码功能
- 实时表单验证
- 密码强度检查
- 学生身份验证

### 💬 论坛页面 (ForumView)
- 基础结构已搭建
- 待后续功能完善

## 🔌 API集成

项目已配置完整的API客户端：

- **请求拦截器**: 自动添加认证token
- **响应拦截器**: 统一错误处理
- **类型安全**: 完整的TypeScript类型定义
- **错误处理**: 用户友好的错误提示

## 🔐 状态管理

使用 Pinia 进行状态管理：

- **用户状态**: 登录状态、用户信息管理
- **持久化**: localStorage自动同步
- **类型安全**: 完整的TypeScript支持

## 🛣️ 路由配置

- **路由守卫**: 自动认证检查
- **懒加载**: 按需加载页面组件
- **重定向**: 智能路由重定向
- **类型安全**: 路由参数类型检查

## 🎯 下一步计划

### 短期目标
- [ ] 完善论坛功能（帖子列表、详情、发布）
- [ ] 实现资源分享功能
- [ ] 开发课程表管理
- [ ] 个人资料页面

### 长期目标
- [ ] 消息通知系统
- [ ] 搜索功能优化
- [ ] 移动端App开发
- [ ] PWA支持

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📜 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系我们

- 项目地址: [GitHub](https://github.com/your-username/unilife-frontend)
- 问题反馈: [Issues](https://github.com/your-username/unilife-frontend/issues)
- 文档: [Wiki](https://github.com/your-username/unilife-frontend/wiki)

---

🎓 **让大学生活更精彩！**
