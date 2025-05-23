# UniLife项目文档

## 目录
- [一、项目概述](#一项目概述)
    - [1.1 项目简介](#11-项目简介)
    - [1.2 技术栈](#12-技术栈)
    - [1.3 项目结构](#13-项目结构)
- [二、开发状态](#二开发状态)
    - [2.1 已完成功能](#21-已完成功能)
    - [2.2 待实现功能](#22-待实现功能)
- [三、开发计划](#三开发计划)
    - [3.1 课程表与日程管理模块](#31-课程表与日程管理模块)
    - [3.2 搜索功能模块](#32-搜索功能模块)
    - [3.3 前端页面实现](#33-前端页面实现)
    - [3.4 AI辅助学习模块](#34-ai辅助学习模块)
    - [3.5 积分系统模块](#35-积分系统模块)
- [四、技术难点与解决方案](#四技术难点与解决方案)
    - [4.1 文件上传与存储](#41-文件上传与存储)
    - [4.2 课程表与日程管理](#42-课程表与日程管理)
    - [4.3 搜索功能实现](#43-搜索功能实现)
    - [4.4 用户认证与安全](#44-用户认证与安全)
    - [4.5 实时通知](#45-实时通知)
- [五、数据库设计](#五数据库设计)
    - [5.1 用户表 (users)](#51-用户表-users)
    - [5.2 帖子表 (posts)](#52-帖子表-posts)
    - [5.3 评论表 (comments)](#53-评论表-comments)
    - [5.4 分类表 (categories)](#54-分类表-categories)
    - [5.5 资源表 (resources)](#55-资源表-resources)
    - [5.6 课程表 (courses)](#56-课程表-courses)
    - [5.7 日程表 (schedules)](#57-日程表-schedules)
- [六、开发规范](#六开发规范)
    - [6.1 代码规范](#61-代码规范)
    - [6.2 提交规范](#62-提交规范)
    - [6.3 文档规范](#63-文档规范)
- [七、后续优化建议](#七后续优化建议)
    - [7.1 性能优化](#71-性能优化)
    - [7.2 安全性优化](#72-安全性优化)
    - [7.3 用户体验优化](#73-用户体验优化)
    - [7.4 代码质量优化](#74-代码质量优化)

## 一、项目概述

### 1.1 项目简介

UniLife 是一款面向学生的在线论坛，致力于提升校园生活体验。其口号为 "有你生活，优你生活"，核心功能包括论坛交流、学习资源共享、课程管理、AI 学习辅助等。项目初期以网站形式开发，后续可能扩展为移动端应用。

### 1.2 技术栈

#### 前端技术栈
- 框架：Vue 3 + TypeScript
- 构建工具：Vite
- UI组件库：Element Plus
- HTTP客户端：Axios
- 路由：Vue Router
- 表单验证：VeeValidate + Yup

#### 后端技术栈
- 框架：Spring Boot 3
- 数据库：MySQL
- ORM框架：MyBatis
- 缓存：Redis
- 认证：JWT
- 邮件服务：Spring Mail
- 工具库：Hutool
- API文档：Knife4j

### 1.3 项目结构

#### 前端结构
```
Front/vue-unilife/
├── public/             # 静态资源
├── src/
│   ├── assets/         # 资源文件
│   ├── components/     # 组件
│   ├── utils/          # 工具类
│   ├── views/          # 页面
│   ├── router/         # 路由
│   ├── store/          # 状态管理
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
└── package.json        # 项目配置
```

#### 后端结构
```
unilife-server/
├── src/main/java/com/unilife/
│   ├── common/         # 通用类
│   ├── config/         # 配置类
│   ├── controller/     # 控制器
│   ├── interceptor/    # 拦截器
│   ├── mapper/         # 数据访问层
│   ├── model/          # 数据模型
│   │   ├── dto/        # 数据传输对象
│   │   ├── entity/     # 实体类
│   │   └── vo/         # 视图对象
│   ├── service/        # 服务层
│   │   └── impl/       # 服务实现
│   └── utils/          # 工具类
└── src/main/resources/
    ├── mappers/        # MyBatis映射文件
    └── application.yml # 应用配置
```

## 二、开发状态

### 2.1 已完成功能

#### 前端
- ✅ 基础环境搭建（Vue 3 + TypeScript + Vite）
- ✅ 基础组件库集成（Element Plus）
- ✅ HTTP请求封装（Axios）
- ✅ 页面原型设计

#### 后端
- ✅ 用户认证模块（注册、登录、验证码）
- ✅ 用户信息管理模块（个人信息、密码修改、头像上传）
- ✅ 论坛功能模块（帖子、评论、点赞、分类）
- ✅ 学习资源共享模块（资源上传、下载、点赞、分类）- ✅ 课程表与日程管理模块（课程管理、日程管理、冲突检测、提醒功能）- ✅ 搜索功能模块（综合搜索、分类搜索、搜索建议、热门关键词）- ✅ 项目文档（接口文档、开发进度、开发计划）

### 2.2 待实现功能- ✅ 课程表与日程管理模块（已完成）- ✅ 搜索功能模块（已完成）- ✅ 前端页面实现（已完成）- ❌ AI辅助学习模块- ❌ 积分系统模块- ❌ 实时通知系统

## 三、开发计划

### 3.1 课程表与日程管理模块

**优先级**：高

**任务**：
1. 设计并实现课程和日程相关的实体类、DTO和VO
    - Course实体类
    - Schedule实体类
    - 相关的DTO和VO
2. 设计并实现CourseMapper和ScheduleMapper接口及XML映射文件
    - 课程的CRUD操作
    - 日程的CRUD操作
    - 按时间和用户查询
3. 设计并实现CourseService和ScheduleService接口及实现类
    - 课程导入逻辑
    - 日程管理逻辑
    - 日程提醒逻辑
4. 设计并实现CourseController和ScheduleController
    - 课程管理API
    - 日程管理API
    - 日程提醒API
5. 实现课程导入功能
    - 手动添加课程
    - 批量导入课程
    - 课程冲突检测
6. 实现日程提醒功能
    - 定时提醒
    - 邮件通知
    - 提醒设置

### 3.2 搜索功能模块

**优先级**：中

**任务**：
1. 设计并实现搜索相关的DTO和VO
    - SearchDTO
    - SearchResultVO
2. 设计并实现SearchService接口和实现类
    - 全文搜索逻辑
    - 分类搜索逻辑
    - 搜索结果排序
3. 设计并实现SearchController
    - 全局搜索API
    - 分类搜索API
    - 搜索建议API
4. 实现全文搜索功能
    - 帖子内容搜索
    - 资源内容搜索
    - 用户信息搜索
5. 实现分类搜索功能
    - 按分类筛选
    - 按标签筛选
    - 高级筛选条件

### 3.3 前端页面实现

**优先级**：高

**任务**：
1. 实现用户认证相关页面
    - 登录页面
    - 注册页面
    - 找回密码页面
2. 实现用户信息管理相关页面
    - 个人主页
    - 个人设置页面
    - 头像上传组件
3. 实现论坛功能相关页面
    - 帖子列表页面
    - 帖子详情页面
    - 帖子发布/编辑页面
    - 评论组件
4. 实现资源共享相关页面
    - 资源列表页面
    - 资源详情页面
    - 资源上传页面
5. 实现课程表与日程相关页面
    - 课程表页面
    - 日程管理页面
    - 日程添加/编辑页面

### 3.4 AI辅助学习模块

**优先级**：低

**任务**：
1. 调研并选择合适的AI服务
2. 完成学习计划制定API
3. 完成智能提醒API
4. 实现前端AI助手页面

### 3.5 积分系统模块

**优先级**：低

**任务**：
1. 完成积分规则设计
2. 完成积分获取、消费API
3. 完成积分排行榜API
4. 实现前端积分展示功能
5. 实现前端积分排行榜页面

## 四、技术难点与解决方案

### 4.1 文件上传与存储

**难点**：处理大文件上传，确保存储安全，提供高效的下载体验。

**解决方案**：
1. 实现分片上传，将大文件分成多个小块上传，减轻服务器压力
2. 使用对象存储服务（如阿里云OSS、七牛云等）存储文件，提高可靠性和访问速度
3. 设置文件类型和大小限制，防止恶意文件上传
4. 实现文件MD5校验，避免重复上传相同文件
5. 使用CDN加速文件下载

### 4.2 课程表与日程管理

**难点**：课程表数据结构设计，日程冲突检测，提醒功能实现。

**解决方案**：
1. 设计灵活的课程表数据结构，支持周期性课程和特殊课程
2. 使用算法检测日程冲突，提供冲突解决建议
3. 使用定时任务实现日程提醒功能
4. 提供多种提醒方式（站内通知、邮件等）
5. 实现日程导入/导出功能，支持与其他日历系统同步

### 4.3 搜索功能实现

**难点**：实现高效的全文搜索，处理大量数据的搜索性能问题。

**解决方案**：
1. 使用Elasticsearch或MySQL全文索引实现搜索功能
2. 对搜索结果进行缓存，减少重复搜索的开销
3. 实现搜索结果分页和懒加载，提高用户体验
4. 使用分词技术提高中文搜索的准确性
5. 实现搜索结果排序和过滤功能

### 4.4 用户认证与安全

**难点**：确保用户数据安全，防止未授权访问。

**解决方案**：
1. 使用JWT进行认证，实现权限控制
2. 加密敏感数据，如密码
3. 实现角色和权限管理
4. 防范常见安全威胁，如XSS和CSRF攻击
5. 实现登录异常检测和防护

### 4.5 实时通知

**难点**：实现实时消息推送。

**解决方案**：
1. 使用WebSocket或服务器发送事件(SSE)
2. 实现消息队列处理大量通知
3. 设计通知优先级和分类
4. 提供通知设置和管理功能
5. 实现离线通知存储和同步

## 五、数据库设计

### 5.1 用户表 (users)

| 字段名      | 类型         | 约束                                                  | 说明                                 |
| ----------- | ------------ | ----------------------------------------------------- | ------------------------------------ |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 用户ID                               |
| username    | VARCHAR(50)  | NOT NULL, UNIQUE                                      | 用户名                               |
| email       | VARCHAR(100) | NOT NULL, UNIQUE                                      | 邮箱地址（学校邮箱）                 |
| password    | VARCHAR(255) | NOT NULL                                              | 密码（加密存储）                     |
| nickname    | VARCHAR(50)  | NOT NULL                                              | 昵称                                 |
| avatar      | VARCHAR(255) |                                                       | 头像URL                              |
| bio         | TEXT         |                                                       | 个人简介                             |
| gender      | TINYINT      |                                                       | 性别（0-未知, 1-男, 2-女）           |
| student_id  | VARCHAR(20)  | UNIQUE                                                | 学号                                 |
| department  | VARCHAR(100) |                                                       | 院系                                 |
| major       | VARCHAR(100) |                                                       | 专业                                 |
| grade       | VARCHAR(20)  |                                                       | 年级                                 |
| points      | INT          | DEFAULT 0                                             | 积分                                 |
| role        | TINYINT      | DEFAULT 0                                             | 角色（0-普通用户, 1-版主, 2-管理员） |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-禁用, 1-启用）               |
| is_verified | TINYINT      | DEFAULT 0                                             | 是否验证（0-未验证, 1-已验证）       |
| login_ip    | VARCHAR(50)  |                                                       | 最近登录IP                           |
| login_time  | DATETIME     |                                                       | 最近登录时间                         |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间                             |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间                             |

### 5.2 帖子表 (posts)

| 字段名      | 类型         | 约束                                                  | 说明                           |
| ----------- | ------------ | ----------------------------------------------------- | ------------------------------ |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 帖子ID                         |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 发布用户ID                     |
| title       | VARCHAR(100) | NOT NULL                                              | 帖子标题                       |
| content     | TEXT         | NOT NULL                                              | 帖子内容                       |
| category_id | BIGINT       | NOT NULL, FOREIGN KEY                                 | 分类ID                         |
| view_count  | INT          | DEFAULT 0                                             | 浏览次数                       |
| like_count  | INT          | DEFAULT 0                                             | 点赞次数                       |
| comment_count | INT        | DEFAULT 0                                             | 评论次数                       |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-删除, 1-正常, 2-置顶） |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间                       |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间                       |

### 5.3 评论表 (comments)

| 字段名      | 类型         | 约束                                                  | 说明                     |
| ----------- | ------------ | ----------------------------------------------------- | ------------------------ |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 评论ID                   |
| post_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 帖子ID                   |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 评论用户ID               |
| content     | TEXT         | NOT NULL                                              | 评论内容                 |
| parent_id   | BIGINT       |                                                       | 父评论ID（回复某条评论） |
| like_count  | INT          | DEFAULT 0                                             | 点赞次数                 |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-删除, 1-正常）   |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间                 |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间                 |

### 5.4 分类表 (categories)

| 字段名      | 类型         | 约束                                                  | 说明                   |
| ----------- | ------------ | ----------------------------------------------------- | ---------------------- |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 分类ID                 |
| name        | VARCHAR(50)  | NOT NULL, UNIQUE                                      | 分类名称               |
| description | VARCHAR(255) |                                                       | 分类描述               |
| icon        | VARCHAR(255) |                                                       | 分类图标               |
| sort        | INT          | DEFAULT 0                                             | 排序                   |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-禁用, 1-启用） |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间               |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间               |

### 5.5 资源表 (resources)

| 字段名      | 类型         | 约束                                                  | 说明                   |
| ----------- | ------------ | ----------------------------------------------------- | ---------------------- |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 资源ID                 |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 上传用户ID             |
| title       | VARCHAR(100) | NOT NULL                                              | 资源标题               |
| description | TEXT         |                                                       | 资源描述               |
| file_url    | VARCHAR(255) | NOT NULL                                              | 文件URL                |
| file_size   | BIGINT       | NOT NULL                                              | 文件大小（字节）       |
| file_type   | VARCHAR(50)  | NOT NULL                                              | 文件类型               |
| category_id | BIGINT       | NOT NULL, FOREIGN KEY                                 | 分类ID                 |
| download_count | INT       | DEFAULT 0                                             | 下载次数               |
| like_count  | INT          | DEFAULT 0                                             | 点赞次数               |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-删除, 1-正常） |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间               |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间               |

### 5.6 课程表 (courses)

| 字段名      | 类型         | 约束                                                  | 说明                   |
| ----------- | ------------ | ----------------------------------------------------- | ---------------------- |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 课程ID                 |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 用户ID                 |
| name        | VARCHAR(100) | NOT NULL                                              | 课程名称               |
| teacher     | VARCHAR(50)  |                                                       | 教师姓名               |
| location    | VARCHAR(100) |                                                       | 上课地点               |
| day_of_week | TINYINT      | NOT NULL                                              | 星期几（1-7）          |
| start_time  | TIME         | NOT NULL                                              | 开始时间               |
| end_time    | TIME         | NOT NULL                                              | 结束时间               |
| start_week  | TINYINT      | NOT NULL                                              | 开始周次               |
| end_week    | TINYINT      | NOT NULL                                              | 结束周次               |
| color       | VARCHAR(20)  |                                                       | 显示颜色               |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-删除, 1-正常） |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间               |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间               |

### 5.7 日程表 (schedules)

| 字段名      | 类型         | 约束                                                  | 说明                   |
| ----------- | ------------ | ----------------------------------------------------- | ---------------------- |
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT                           | 日程ID                 |
| user_id     | BIGINT       | NOT NULL, FOREIGN KEY                                 | 用户ID                 |
| title       | VARCHAR(100) | NOT NULL                                              | 日程标题               |
| description | TEXT         |                                                       | 日程描述               |
| start_time  | DATETIME     | NOT NULL                                              | 开始时间               |
| end_time    | DATETIME     | NOT NULL                                              | 结束时间               |
| location    | VARCHAR(100) |                                                       | 地点                   |
| is_all_day  | TINYINT      | DEFAULT 0                                             | 是否全天（0-否, 1-是） |
| reminder    | TINYINT      |                                                       | 提醒时间（分钟）       |
| color       | VARCHAR(20)  |                                                       | 显示颜色               |
| status      | TINYINT      | DEFAULT 1                                             | 状态（0-删除, 1-正常） |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP                             | 创建时间               |
| updated_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间               |

## 六、开发规范

### 6.1 代码规范

- **前端**：
    - 遵循ESLint配置，使用Prettier格式化
    - 组件命名采用PascalCase
    - 变量命名采用camelCase
    - 使用TypeScript类型定义
    - 组件按功能模块组织

- **后端**：
    - 遵循阿里巴巴Java开发手册
    - 类名采用PascalCase
    - 方法名和变量名采用camelCase
    - 常量名采用全大写下划线分隔
    - 包名全小写
    - 接口和实现类命名规范

### 6.2 提交规范

- 使用语义化的提交信息
    - feat: 新功能
    - fix: 修复bug
    - docs: 文档更新
    - style: 代码格式调整
    - refactor: 代码重构
    - test: 测试相关
    - chore: 构建过程或辅助工具的变动
- 每个功能或修复在单独的分支上开发
- 提交前进行代码审查
- 定期合并主分支到开发分支

### 6.3 文档规范

- API文档使用Swagger/Knife4j生成
- 重要功能编写设计文档
- 定期更新开发文档
- 代码中添加必要的注释
- 复杂算法和业务逻辑需要详细注释

## 七、后续优化建议

### 7.1 性能优化

1. 添加缓存机制，减少数据库查询
    - 使用Redis缓存热门数据
    - 实现本地缓存减轻数据库压力
2. 优化SQL查询，添加必要的索引
    - 分析慢查询日志
    - 优化复杂查询
3. 实现分页查询，减少数据传输量
4. 使用异步处理耗时操作
    - 文件处理
    - 邮件发送
    - 数据统计

### 7.2 安全性优化

1. 完善权限控制系统
    - 基于角色的访问控制
    - 资源级别的权限控制
2. 加强输入验证和过滤
    - 防止SQL注入
    - 防止XSS攻击
3. 实现敏感操作的二次验证
4. 加强密码安全性
    - 密码强度检测
    - 定期修改密码提醒

### 7.3 用户体验优化

1. 实现实时通知功能
    - WebSocket实现即时消息
    - 消息推送机制
2. 优化页面加载速度
    - 资源懒加载
    - 组件按需加载
3. 添加更多交互反馈
    - 操作成功/失败提示
    - 加载状态指示
4. 实现个性化推荐
    - 基于用户行为的内容推荐
    - 热门内容推荐

### 7.4 代码质量优化

1. 增加单元测试和集成测试
    - 提高测试覆盖率
    - 自动化测试流程
2. 规范代码风格
    - 使用代码格式化工具
    - 制定统一的命名规范
3. 完善异常处理机制
    - 全局异常处理
    - 详细的错误日志
4. 重构复杂代码
    - 提取公共方法
    - 优化代码结构
