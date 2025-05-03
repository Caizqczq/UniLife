# UniLife开发文档（更新版）

[TOC]

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

## 二、当前进度

### 2.1 已完成功能

#### 前端
- 基础环境搭建（Vue 3 + TypeScript + Vite）
- 基础组件库集成（Element Plus）
- HTTP请求封装（Axios）
- 页面原型设计

#### 后端
- 基础环境搭建（Spring Boot 3）
- 数据库设计（用户表）
- 用户认证模块
  - 用户注册
  - 用户登录
  - 邮箱验证码获取
  - 邮箱验证码登录
- IP地址定位服务

### 2.2 待实现功能

#### 前端
- 所有页面的具体实现
- 状态管理
- 路由配置
- 表单验证
- 用户认证与授权
- 响应式设计

#### 后端
- 用户信息管理模块
  - 获取用户信息
  - 更新用户信息
  - 修改密码
  - 上传头像
- 论坛功能模块
  - 帖子发布/编辑/删除
  - 评论/回复
  - 点赞/收藏
- 学习资源共享模块
  - 资源上传/下载
  - 资源分类/搜索
- 课程表与日程管理模块
  - 课程导入/编辑
  - 日程安排/提醒
- AI辅助学习模块
  - 学习计划制定
  - 智能提醒
- 搜索功能模块
  - 全文搜索
  - 分类搜索
- 积分系统模块
  - 积分获取/消费
  - 积分规则管理

## 三、API规范

### 3.1 基础信息

- **基础URL**: `http://localhost:8087`(本地测试)
- **接口格式**: RESTful API
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT (JSON Web Token)

#### HTTP方法
- **GET**: 获取资源
- **POST**: 创建资源
- **PUT**: 更新资源（全量更新）
- **PATCH**: 部分更新资源
- **DELETE**: 删除资源

### 3.2 响应规范

#### 响应状态码
- **200**: 成功
- **400**: 请求参数错误
- **401**: 未授权
- **403**: 禁止访问
- **404**: 资源不存在
- **500**: 服务器内部错误

#### 响应格式

成功响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "key": "value"
  }
}
```

注：
- code: 200表示成功
- data: 包含实际返回数据

错误响应:
```json
{
  "code": 400,  
  "message": "参数错误",
  "data": null  
}
```

注：message字段包含错误信息

## 四、数据库设计

### 4.1 用户表设计 (users)

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

### 4.2 帖子表设计 (posts)

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

### 4.3 评论表设计 (comments)

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

### 4.4 分类表设计 (categories)

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

### 4.5 资源表设计 (resources)

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

### 4.6 课程表设计 (courses)

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

### 4.7 日程表设计 (schedules)

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

## 五、API功能实现

### 5.1 用户认证模块

#### 5.1.1 用户注册

- **URL**: `/users/register`
- **方法**: POST
- **描述**: 创建新用户账号

请求参数：
```json
{
  "username": "student123",
  "email": "student@school.edu",
  "password": "Secure@Password123",
  "nickname": "学生昵称",
  "studentId": "20220101001",
  "department": "计算机学院",
  "major": "软件工程",
  "grade": "2023级"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 12345,
    "username": "student123",
    "nickname": "学生昵称"
  }
}
```

#### 5.1.2 用户密码登录

- **URL**: `/users/login`
- **方法**: POST
- **描述**: 用户登录

请求参数：
```json
{
  "username": "student123",
  "password": "Secure@Password123"
}
```

注：username字段可以是用户名或邮箱

响应结果：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 12345,
      "username": "student123",
      "nickname": "学生昵称",
      "avatar": "https://example.com/avatar.jpg",
      "role": 0,
      "isVerified": true,
      "status": 1
    }
  }
}
```

#### 5.1.3 获取邮箱验证码

- **URL**: `/users/code`
- **方法**: POST
- **描述**: 向指定邮箱发送登录验证码

请求参数：
```json
{
  "email": "student@school.edu"
}
```

响应结果:
```json
{
  "code": 200,
  "message": "验证码已发送",
  "data": null
}
```

#### 5.1.4 邮箱验证码登录

- **URL**: `/users/login/code`
- **方法**: POST
- **描述**: 使用邮箱和验证码进行登录

请求参数：
```json
{
  "email": "student@school.edu",
  "code": "123456"
}
```

成功响应:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 12345,
      "username": "student123",
      "nickname": "学生昵称",
      "avatar": "https://example.com/avatar.jpg",
      "role": 0
    }
  }
}
```

### 5.2 用户信息管理模块（待实现）

#### 5.2.1 获取用户个人信息

- **URL**: `/users/profile`
- **方法**: GET
- **描述**: 获取当前登录用户的个人资料信息

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "username": "student123",
    "email": "student@school.edu",
    "nickname": "测试员", 
    "avatar": "https://example.com/avatars/default.png",
    "bio": "这是一个个人简介",
    "gender": 2,
    "studentId": "20220101001",
    "department": "计算机学院",
    "major": "软件工程",
    "grade": "2023级",
    "points": 100,
    "role": 0,
    "isVerified": 1
  }
}
```

#### 5.2.2 更新用户个人信息

- **URL**: `/users/profile`
- **方法**: PUT
- **描述**: 更新当前登录用户的个人资料

请求参数：
```json
{
  "nickname": "新昵称",
  "bio": "这是更新后的个人简介",
  "gender": 2
}
```

响应结果：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

#### 5.2.3 修改用户密码

- **URL**: `/users/password`
- **方法**: PUT
- **描述**: 修改当前登录用户的密码

请求参数：
```json
{
  "code": "验证码",
  "newPassword": "新密码"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

#### 5.2.4 上传用户头像

- **URL**: `/users/avatar`
- **方法**: POST
- **描述**: 上传或更新用户头像

请求参数：
```
file: [图片文件]
```

响应结果：
```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": {
    "avatar": "https://example.com/avatars/user_123456.jpg"
  }
}
```

#### 5.2.5 更新用户邮箱

- **URL**: `/users/email`
- **方法**: PUT
- **描述**: 更新用户邮箱地址

请求参数：
```json
{
  "email": "new_email@school.edu",
  "code": "123456"
}
```

注：code字段为邮箱验证码

响应结果：
```json
{
  "code": 200,
  "message": "邮箱更新成功",
  "data": null
}
```

### 5.3 论坛功能模块（待实现）

#### 5.3.1 获取帖子列表

- **URL**: `/posts`
- **方法**: GET
- **描述**: 获取帖子列表，支持分页和筛选

请求参数：
```
page: 1
size: 10
category: 1
sort: latest
```

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "帖子标题",
        "summary": "帖子摘要...",
        "userId": 12345,
        "nickname": "发布者昵称",
        "avatar": "https://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "学习交流",
        "viewCount": 100,
        "likeCount": 20,
        "commentCount": 5,
        "createdAt": "2023-05-01 12:00:00"
      }
    ]
  }
}
```

#### 5.3.2 获取帖子详情

- **URL**: `/posts/{id}`
- **方法**: GET
- **描述**: 获取帖子详情

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "帖子标题",
    "content": "帖子内容...",
    "userId": 12345,
    "nickname": "发布者昵称",
    "avatar": "https://example.com/avatar.jpg",
    "categoryId": 1,
    "categoryName": "学习交流",
    "viewCount": 100,
    "likeCount": 20,
    "commentCount": 5,
    "isLiked": true,
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
  }
}
```

#### 5.3.3 发布帖子

- **URL**: `/posts`
- **方法**: POST
- **描述**: 发布新帖子

请求参数：
```json
{
  "title": "帖子标题",
  "content": "帖子内容...",
  "categoryId": 1
}
```

响应结果：
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "postId": 1
  }
}
```

#### 5.3.4 更新帖子

- **URL**: `/posts/{id}`
- **方法**: PUT
- **描述**: 更新帖子

请求参数：
```json
{
  "title": "更新后的标题",
  "content": "更新后的内容...",
  "categoryId": 2
}
```

响应结果：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

#### 5.3.5 删除帖子

- **URL**: `/posts/{id}`
- **方法**: DELETE
- **描述**: 删除帖子

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 5.3.6 获取评论列表

- **URL**: `/posts/{postId}/comments`
- **方法**: GET
- **描述**: 获取帖子的评论列表

请求参数：
```
page: 1
size: 20
```

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "content": "评论内容...",
        "userId": 12345,
        "nickname": "评论者昵称",
        "avatar": "https://example.com/avatar.jpg",
        "likeCount": 5,
        "isLiked": false,
        "createdAt": "2023-05-01 12:30:00",
        "replies": [
          {
            "id": 2,
            "content": "回复内容...",
            "userId": 54321,
            "nickname": "回复者昵称",
            "avatar": "https://example.com/avatar2.jpg",
            "likeCount": 2,
            "isLiked": true,
            "createdAt": "2023-05-01 12:35:00"
          }
        ]
      }
    ]
  }
}
```

#### 5.3.7 发表评论

- **URL**: `/comments`
- **方法**: POST
- **描述**: 发表评论或回复

请求参数：
```json
{
  "postId": 1,
  "content": "评论内容...",
  "parentId": null
}
```

注：parentId字段用于回复某条评论，回复时设置为父评论ID，发表新评论时设为null

响应结果：
```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "commentId": 3
  }
}
```

#### 5.3.8 点赞/取消点赞

- **URL**: `/posts/{id}/like`
- **方法**: POST
- **描述**: 点赞
