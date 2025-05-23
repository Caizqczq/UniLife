# UniLife技术要点与开发指南

## 📋 目录
- [一、项目架构概览](#一项目架构概览)
- [二、开发环境配置](#二开发环境配置)
- [三、核心技术要点](#三核心技术要点)
- [四、已实现功能详解](#四已实现功能详解)
- [五、待开发功能规划](#五待开发功能规划)
- [六、开发规范与最佳实践](#六开发规范与最佳实践)
- [七、常见问题与解决方案](#七常见问题与解决方案)

---

## 一、项目架构概览

### 🏗️ 整体架构
```
UniLife项目
├── 前端 (Vue 3 + TypeScript)
│   ├── 用户界面层
│   ├── 业务逻辑层
│   └── 数据访问层
├── 后端 (Spring Boot 3)
│   ├── 控制器层 (Controller)
│   ├── 服务层 (Service)
│   ├── 数据访问层 (Mapper)
│   └── 实体层 (Entity/DTO/VO)
└── 数据库 (MySQL + Redis)
    ├── 主数据库 (MySQL)
    └── 缓存 (Redis)
```

### 🔄 数据流向
```
用户 → 前端页面 → API请求 → 后端控制器 → 服务层 → 数据库 → 返回结果
```

---

## 二、开发环境配置

### 🔧 后端环境要求
- **JDK**: 17或以上
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **IDE**: IntelliJ IDEA 推荐

### 🎨 前端环境要求
- **Node.js**: 18.0+
- **npm**: 8.0+ 或 pnpm 8.0+
- **IDE**: VS Code 推荐

### 📦 依赖安装

#### 后端依赖
```bash
cd unilife-server
mvn clean install
```

#### 前端依赖
```bash
cd Front/vue-unilife
npm install
# 或
pnpm install
```

---

## 三、核心技术要点

### 🔐 认证与安全
- **JWT认证机制**：用户登录后颁发JWT Token
- **拦截器配置**：自动验证Token有效性
- **权限控制**：基于角色的访问控制

```java
// JWT配置示例
@Component
public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        // Token验证逻辑
    }
}
```

### 📊 数据库设计
- **主键策略**：统一使用BIGINT自增主键
- **时间字段**：created_at, updated_at 自动维护
- **软删除**：使用status字段标记删除状态
- **外键关系**：合理设计表间关系

### 🔄 API设计规范
- **RESTful风格**：GET/POST/PUT/DELETE语义化
- **统一响应格式**：Result<T>包装返回数据
- **分页查询**：使用PageHelper插件
- **异常处理**：全局异常处理器

```java
// 统一响应格式
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
}
```

### 🎯 前端架构设计
- **组件化开发**：可复用组件设计
- **路由管理**：嵌套路由与权限控制
- **状态管理**：Pinia集中状态管理
- **API封装**：统一HTTP请求处理

---

## 四、已实现功能详解

### 👤 用户认证系统
- **注册流程**：邮箱验证 → 用户信息填写 → 账号创建
- **登录方式**：用户名密码登录 + 邮箱验证码登录
- **信息管理**：个人资料修改、密码修改、头像上传

### 💬 论坛功能模块
- **帖子管理**：发布、编辑、删除、查看
- **评论系统**：嵌套回复、点赞功能
- **分类系统**：多级分类管理

### 📚 资源共享模块
- **文件上传**：支持多种文件格式
- **存储方案**：本地存储 + 阿里云OSS
- **下载统计**：下载次数自动统计

### 📅 课程表与日程
- **课程管理**：添加、编辑、删除课程
- **日程管理**：个人日程安排
- **冲突检测**：时间冲突自动检测
- **邮件提醒**：日程提醒邮件发送

### 🔍 搜索功能
- **综合搜索**：跨模块内容搜索
- **分类搜索**：按类型筛选结果
- **搜索建议**：智能关键词建议
- **热门搜索**：热门关键词统计

---

## 五、待开发功能规划

### 🤖 AI辅助学习模块
#### 技术选型建议
- **AI服务**：百度文心一言、阿里通义千问
- **集成方式**：HTTP API调用
- **功能设计**：
  - 学习计划生成
  - 智能问答
  - 学习进度分析

#### 数据库设计
```sql
-- 学习计划表
CREATE TABLE study_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    ai_generated TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- AI对话记录
CREATE TABLE ai_conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 🎯 积分系统模块
#### 积分规则设计
```yaml
积分获取规则:
  发布帖子: +10分
  发布评论: +5分
  上传资源: +20分
  资源被下载: +2分
  帖子被点赞: +1分
  日常签到: +5分
  完成学习计划: +15分

积分消费规则:
  下载高级资源: -10分
  使用AI问答: -5分
  置顶帖子: -50分
```

#### 数据库设计
```sql
-- 积分记录表
CREATE TABLE points_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    related_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 成就表
CREATE TABLE achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255),
    condition_type VARCHAR(50),
    condition_value INT,
    points_reward INT DEFAULT 0
);
```

### 🔔 实时通知系统
#### 技术方案
- **WebSocket**：实时双向通信
- **消息队列**：RabbitMQ处理大量消息
- **通知类型**：系统通知、点赞通知、评论通知、私信

#### 实现步骤
1. 添加WebSocket依赖
2. 配置WebSocket端点
3. 实现消息推送逻辑
4. 前端WebSocket客户端连接

---

## 六、开发规范与最佳实践

### 📝 代码规范
#### 后端规范
- **命名规范**：驼峰命名法
- **注释规范**：重要方法必须添加JavaDoc
- **异常处理**：统一异常处理机制
- **日志规范**：使用SLF4J记录关键操作

#### 前端规范
- **组件命名**：PascalCase
- **变量命名**：camelCase
- **文件命名**：kebab-case
- **代码格式**：使用Prettier统一格式

### 🔧 开发流程
1. **需求分析** → 确定功能需求
2. **技术设计** → 设计技术方案
3. **数据库设计** → 设计表结构
4. **API设计** → 定义接口规范
5. **编码实现** → 实现具体功能
6. **测试验证** → 功能测试
7. **文档更新** → 更新相关文档

### 📊 性能优化建议
- **数据库优化**：合理使用索引
- **缓存机制**：Redis缓存热点数据
- **懒加载**：前端组件按需加载
- **图片优化**：图片压缩和CDN加速

---

## 七、常见问题与解决方案

### 🐛 常见问题

#### 1. 跨域问题
**问题**：前端调用后端API出现跨域错误

**解决方案**：
```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {
    // 控制器方法
}
```

#### 2. JWT Token过期
**问题**：用户Token过期后无法访问

**解决方案**：
- 实现Token自动刷新机制
- 前端拦截401响应，引导用户重新登录

#### 3. 文件上传失败
**问题**：大文件上传超时或失败

**解决方案**：
- 增加文件上传大小限制配置
- 实现分片上传功能
- 添加上传进度显示

#### 4. 数据库连接异常
**问题**：数据库连接池耗尽

**解决方案**：
- 优化数据库连接池配置
- 检查慢SQL查询
- 合理使用事务

### 🔄 调试技巧
- **后端调试**：使用IDE断点调试
- **前端调试**：浏览器开发者工具
- **API测试**：使用Postman或Knife4j
- **日志分析**：查看应用日志定位问题

---

## 📞 技术支持

如果在开发过程中遇到问题，可以：
1. 查阅项目文档
2. 查看相关技术官方文档
3. 在项目群中讨论
4. 提交Issue到项目仓库

---

*最后更新：2024年12月*
*维护团队：UniLife开发组* 