# UniLife接口文档

## 更新日志

### v1.4.0 (2025-05-30)
- **重构AI助手流式响应**: 完全重构AI聊天功能，实现真正的流式显示
- **优化接口参数格式**: AI聊天接口改用`application/x-www-form-urlencoded`格式，简化参数传递
- **改进前端实现**: 使用原生fetch API替代axios，确保流式响应的稳定性
- **参考成功案例**: 基于已验证的流式响应实现模式进行重构
- **UI体验提升**: 保持美观界面的同时，实现真正的实时流式文字显示
- **响应格式优化**: 从Server-Sent Events改为直接文本流，提高兼容性

### v1.3.0 (2025-01-27)
- **新增AI辅助学习模块**: 实现了完整的AI聊天助手功能
- **AI会话管理**: 支持创建、查询、更新、删除聊天会话
- **AI消息系统**: 支持发送消息、获取回复、查看历史记录
- **上下文感知**: AI可以根据会话历史提供连贯的对话体验
- **Markdown支持**: AI回复支持丰富的格式化内容
- **会话标题编辑**: 支持双击编辑或点击编辑按钮修改会话标题
- **数据库设计**: 新增ai_chat_sessions和ai_chat_messages表
- **多会话支持**: 用户可以同时进行多个独立的AI对话会话

### v1.2.0 (2025-01-27)
- **修复资源点赞功能**: 实现了完整的资源点赞表 (`resource_likes`)，防止重复点赞
- **优化响应数据结构**: 统一时间格式为ISO 8601格式 (`yyyy-MM-ddTHH:mm:ss`)
- **完善isLiked字段**: 资源列表和详情接口中的 `isLiked` 字段现在基于当前用户的真实点赞状态
- **增强文件存储**: 文件存储从本地上传改为阿里云OSS，支持临时访问链接
- **添加数据库设计说明**: 详细说明了资源表和点赞表的设计
- **修正API参数说明**: 更新了资源列表接口中 `user` 参数的含义，改为 `uploaderUserId`
- **优化课程表时间安排**: 学期格式统一为"2024-2025第一学期"格式，课程时间安排调整为13个标准时间段

### v1.1.0 (2025-01-26)
- 完成论坛功能模块的前后端集成
- 实现帖子、评论、点赞等核心功能
- 添加用户认证和权限管理

### v1.0.0 (2025-01-25)
- 初始版本发布
- 实现基础的用户认证功能
- 定义核心数据结构和API接口

## 目录
- [1. 基础信息](#1-基础信息)
- [2. 用户认证模块](#2-用户认证模块)
- [3. 用户信息管理模块](#3-用户信息管理模块)
- [4. 论坛功能模块](#4-论坛功能模块)
    - [4.1 帖子管理](#41-帖子管理)
    - [4.2 评论管理](#42-评论管理)
    - [4.3 分类管理](#43-分类管理)
- [5. 学习资源共享模块](#5-学习资源共享模块)
- [6. 课程表与日程管理模块](#6-课程表与日程管理模块)
- [7. AI辅助学习模块](#7-AI辅助学习模块)
- [8. 待实现模块](#8-待实现模块)

## 1. 基础信息

- **基础URL**: `http://localhost:8087`(本地测试)
- **接口格式**: RESTful API
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: JWT (JSON Web Token)

### HTTP方法
- **GET**: 获取资源
- **POST**: 创建资源
- **PUT**: 更新资源（全量更新）
- **DELETE**: 删除资源

### 响应格式
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

错误响应:
```json
{
  "code": 400,  
  "message": "参数错误",
  "data": null  
}
```

## 2. 用户认证模块

### 2.1 用户注册
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
  "grade": "2023级",
  "code": "123456"
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

### 2.2 用户密码登录
- **URL**: `/users/login`
- **方法**: POST
- **描述**: 用户登录

请求参数：
```json
{
  "email": "student@school.edu",
  "password": "Secure@Password123"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 12345,
    "username": "student123",
    "nickname": "学生昵称",
    "avatar": "https://example.com/avatar.jpg",
    "role": 0
  }
}
```

### 2.3 获取邮箱验证码
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

### 2.4 邮箱验证码登录
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

响应结果:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 12345,
    "username": "student123",
    "nickname": "学生昵称",
    "avatar": "https://example.com/avatar.jpg",
    "role": 0
  }
}
```

## 3. 用户信息管理模块

### 3.1 获取用户个人信息
- **URL**: `/users/info`
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

### 3.2 更新用户个人信息
- **URL**: `/users/profile`
- **方法**: PUT
- **描述**: 更新当前登录用户的个人资料信息
- **认证**: 需要JWT Token

请求参数：
```json
{
  "username": "newusername",
  "bio": "这是一个更新的个人简介",
  "gender": 1,
  "department": "计算机学院",
  "major": "软件工程",
  "grade": "2023级"
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

### 3.3 修改用户密码
- **URL**: `/users/password`
- **方法**: PUT
- **描述**: 修改当前登录用户的密码
- **认证**: 需要JWT Token

请求参数：
```json
{
  "code": "123456",
  "newPassword": "NewSecure@Password123"
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

### 3.4 上传用户头像
- **URL**: `/users/avatar`
- **方法**: POST
- **描述**: 上传用户头像
- **认证**: 需要JWT Token

请求参数：
- **Content-Type**: `multipart/form-data`
- **file**: 头像文件（图片格式）

响应结果：
```json
{
  "code": 200,
  "message": "头像上传成功",
  "data": {
    "avatarUrl": "https://example.com/avatars/user_12345.jpg"
  }
}
```

### 3.5 更新用户邮箱
- **URL**: `/users/email`
- **方法**: PUT
- **描述**: 更新当前登录用户的邮箱
- **认证**: 需要JWT Token

请求参数：
```json
{
  "email": "newemail@school.edu",
  "code": "123456"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "邮箱更新成功",
  "data": null
}
```

### 3.6 获取用户统计数据
- **URL**: `/users/stats`
- **方法**: GET
- **描述**: 获取当前用户的统计数据
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "postsCount": 25,
    "commentsCount": 150,
    "resourcesCount": 10,
    "likesReceived": 300,
    "coursesCount": 8,
    "schedulesCount": 15
  }
}
```

### 3.7 获取用户最近帖子
- **URL**: `/users/recent-posts`
- **方法**: GET
- **描述**: 获取当前用户最近发布的帖子
- **认证**: 需要JWT Token

请求参数：
- **limit** (query, 可选): 返回的帖子数量，默认为5

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 123,
      "title": "最新帖子标题",
      "content": "帖子内容摘要...",
      "categoryId": 1,
      "createdAt": "2023-05-01T12:00:00",
      "viewsCount": 50,
      "likesCount": 10,
      "commentsCount": 5
    }
  ]
}
```

## 4. 论坛功能模块

### 4.1 帖子管理

#### 4.1.1 获取帖子列表
- **URL**: `/posts`
- **方法**: GET
- **描述**: 获取帖子列表，支持分页和筛选

请求参数：
- **categoryId** (query, 可选): 分类ID
- **keyword** (query, 可选): 搜索关键词
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为10
- **sort** (query, 可选): 排序方式，默认为latest

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
        "createdAt": "2023-05-01T12:00:00"
      }
    ],
    "pages": 10
  }
}
```

#### 4.1.2 获取帖子详情
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
    "createdAt": "2023-05-01T12:00:00",
    "updatedAt": "2023-05-01T12:00:00"
  }
}
```

#### 4.1.3 发布帖子
- **URL**: `/posts`
- **方法**: POST
- **描述**: 发布新帖子
- **认证**: 需要JWT Token

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

#### 4.1.4 更新帖子
- **URL**: `/posts/{id}`
- **方法**: PUT
- **描述**: 更新帖子
- **认证**: 需要JWT Token

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

#### 4.1.5 删除帖子
- **URL**: `/posts/{id}`
- **方法**: DELETE
- **描述**: 删除帖子
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 4.1.6 点赞/取消点赞帖子
- **URL**: `/posts/{id}/like`
- **方法**: POST
- **描述**: 点赞或取消点赞帖子
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

#### 4.1.7 获取用户的帖子列表
- **URL**: `/posts/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户发布的帖子列表

请求参数：
- **userId** (path): 用户ID
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为10
- **sort** (query, 可选): 排序方式，默认为latest

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
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
        "createdAt": "2023-05-01T12:00:00"
      }
    ],
    "pages": 3
  }
}
```

### 4.2 评论管理

#### 4.2.1 获取评论列表
- **URL**: `/comments/post/{postId}`
- **方法**: GET
- **描述**: 获取帖子的评论列表

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
        "createdAt": "2023-05-01T12:30:00",
        "replies": [
          {
            "id": 2,
            "content": "回复内容...",
            "userId": 54321,
            "nickname": "回复者昵称",
            "avatar": "https://example.com/avatar2.jpg",
            "likeCount": 2,
            "isLiked": true,
            "createdAt": "2023-05-01T12:35:00"
          }
        ]
      }
    ]
  }
}
```

#### 4.2.2 发表评论
- **URL**: `/comments`
- **方法**: POST
- **描述**: 发表评论或回复
- **认证**: 需要JWT Token

请求参数：
```json
{
  "postId": 1,
  "content": "评论内容...",
  "parentId": null
}
```

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

#### 4.2.3 删除评论
- **URL**: `/comments/{id}`
- **方法**: DELETE
- **描述**: 删除评论
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

#### 4.2.4 点赞/取消点赞评论
- **URL**: `/comments/{id}/like`
- **方法**: POST
- **描述**: 点赞或取消点赞评论
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

### 4.3 分类管理

#### 4.3.1 获取分类列表
- **URL**: `/categories`
- **方法**: GET
- **描述**: 获取分类列表

请求参数：
- **status** (query, 可选): 分类状态，1-启用，0-禁用

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "name": "学习交流",
        "description": "讨论学习相关话题",
        "icon": "icon-study",
        "sort": 1,
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 4.3.2 获取分类详情
- **URL**: `/categories/{id}`
- **方法**: GET
- **描述**: 获取分类详情

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "学习交流",
    "description": "讨论学习相关话题",
    "icon": "icon-study",
    "sort": 1,
    "status": 1,
    "createdAt": "2023-05-01T12:00:00",
    "updatedAt": "2023-05-01T12:00:00"
  }
}
```

#### 4.3.3 创建分类
- **URL**: `/categories`
- **方法**: POST
- **描述**: 创建新分类（需要管理员权限）
- **认证**: 需要JWT Token

请求参数：
```json
{
  "name": "新分类",
  "description": "新分类描述",
  "icon": "icon-new",
  "sort": 6,
  "status": 1
}
```

响应结果：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "categoryId": 6
  }
}
```

#### 4.3.4 更新分类
- **URL**: `/categories/{id}`
- **方法**: PUT
- **描述**: 更新分类（需要管理员权限）
- **认证**: 需要JWT Token

请求参数：
```json
{
  "name": "更新后的分类",
  "description": "更新后的描述",
  "icon": "icon-updated",
  "sort": 7,
  "status": 1
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

#### 4.3.5 删除分类
- **URL**: `/categories/{id}`
- **方法**: DELETE
- **描述**: 删除分类（需要管理员权限）
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

## 5. 学习资源共享模块

### 5.1 上传资源
- **URL**: `/resources`
- **方法**: POST
- **描述**: 上传新资源
- **认证**: 需要JWT Token

请求参数：
- **Content-Type**: `multipart/form-data`
- **file**: 资源文件
- **title**: 资源标题
- **description**: 资源描述
- **categoryId**: 分类ID

响应结果：
```json
{
  "code": 200,
  "message": "资源上传成功",
  "data": {
    "resourceId": 1
  }
}
```

### 5.2 获取资源详情
- **URL**: `/resources/{id}`
- **方法**: GET
- **描述**: 获取资源详情

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "资源标题",
    "description": "资源描述",
    "fileUrl": "https://oss-example.aliyuncs.com/resources/file.pdf",
    "fileSize": 1024000,
    "fileType": "application/pdf",
    "userId": 12345,
    "nickname": "上传者昵称",
    "avatar": "https://example.com/avatar.jpg",
    "categoryId": 1,
    "categoryName": "学习资料",
    "downloadCount": 10,
    "likeCount": 5,
    "isLiked": false,
    "createdAt": "2023-05-01T12:00:00",
    "updatedAt": "2023-05-01T12:00:00"
  }
}
```

**说明**: 
- `isLiked` 字段表示当前登录用户是否已点赞该资源，未登录用户该字段为false
- `fileUrl` 为阿里云OSS存储的文件访问URL

### 5.3 获取资源列表
- **URL**: `/resources`
- **方法**: GET
- **描述**: 获取资源列表，支持分页和筛选

请求参数：
- **category** (query, 可选): 分类ID
- **user** (query, 可选): 上传者用户ID（用于筛选特定用户上传的资源）
- **keyword** (query, 可选): 搜索关键词
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为10

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
        "title": "资源标题",
        "description": "资源描述",
        "fileUrl": "https://oss-example.aliyuncs.com/resources/file.pdf",
        "fileSize": 1024000,
        "fileType": "application/pdf",
        "userId": 12345,
        "nickname": "上传者昵称",
        "avatar": "https://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "学习资料",
        "downloadCount": 10,
        "likeCount": 5,
        "isLiked": false,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ],
    "pages": 10
  }
}
```

**说明**: 
- `isLiked` 字段基于当前登录用户的点赞状态，通过查询`resource_likes`表获得
- 未登录用户获取列表时，所有资源的`isLiked`字段均为false

### 5.4 更新资源
- **URL**: `/resources/{id}`
- **方法**: PUT
- **描述**: 更新资源信息
- **认证**: 需要JWT Token

请求参数：
```json
{
  "title": "更新后的标题",
  "description": "更新后的描述",
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

### 5.5 删除资源
- **URL**: `/resources/{id}`
- **方法**: DELETE
- **描述**: 删除资源
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**说明**: 删除资源时会同时删除阿里云OSS中的文件和数据库中的相关记录（包括点赞记录）

### 5.6 下载资源
- **URL**: `/resources/{id}/download`
- **方法**: GET
- **描述**: 下载资源

响应结果：
```json
{
  "code": 200,
  "message": "获取下载链接成功",
  "data": {
    "fileUrl": "https://oss-example.aliyuncs.com/resources/file.pdf?Expires=1684737600&OSSAccessKeyId=xxx&Signature=xxx",
    "fileName": "资源标题.pdf",
    "fileType": "application/pdf"
  }
}
```

**说明**: 
- `fileUrl` 为生成的临时访问URL，有效期为1小时
- 每次下载会增加资源的下载计数

### 5.7 点赞/取消点赞资源
- **URL**: `/resources/{id}/like`
- **方法**: POST
- **描述**: 点赞或取消点赞资源
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "点赞成功",
  "data": null
}
```

或

```json
{
  "code": 200,
  "message": "取消点赞成功",
  "data": null
}
```

**实现说明**:
- 使用`resource_likes`表记录用户点赞状态，确保一个用户对同一资源只能点赞一次
- 点赞时会增加资源的`like_count`字段，取消点赞时会减少
- 通过唯一键约束防止重复点赞：`UNIQUE KEY uk_user_resource (user_id, resource_id)`

### 5.8 获取用户上传的资源列表
- **URL**: `/resources/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户上传的资源列表

请求参数：
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为10

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "list": [
      {
        "id": 1,
        "title": "资源标题",
        "description": "资源描述",
        "fileUrl": "https://oss-example.aliyuncs.com/resources/file.pdf",
        "fileSize": 1024000,
        "fileType": "application/pdf",
        "userId": 12345,
        "nickname": "上传者昵称",
        "avatar": "https://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "学习资料",
        "downloadCount": 10,
        "likeCount": 5,
        "isLiked": false,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ],
    "pages": 2
  }
}
```

### 5.9 获取当前用户上传的资源列表
- **URL**: `/resources/my`
- **方法**: GET
- **描述**: 获取当前登录用户上传的资源列表
- **认证**: 需要JWT Token

请求参数：
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为10

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "list": [
      {
        "id": 1,
        "title": "资源标题",
        "description": "资源描述",
        "fileUrl": "https://oss-example.aliyuncs.com/resources/file.pdf",
        "fileSize": 1024000,
        "fileType": "application/pdf",
        "userId": 12345,
        "nickname": "上传者昵称",
        "avatar": "https://example.com/avatar.jpg",
        "categoryId": 1,
        "categoryName": "学习资料",
        "downloadCount": 10,
        "likeCount": 5,
        "isLiked": false,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ],
    "pages": 2
  }
}
```

### 5.10 数据库设计说明

#### 资源表 (resources)
```sql
CREATE TABLE `resources` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
  `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '资源标题',
  `description` TEXT DEFAULT NULL COMMENT '资源描述',
  `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL(OSS)',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 资源点赞表 (resource_likes)
```sql
CREATE TABLE `resource_likes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_resource` (`user_id`, `resource_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`) ON DELETE CASCADE
);
```

**特性**:
- 通过唯一键约束确保用户不能重复点赞同一资源
- 级联删除保证数据一致性

## 6. 课程表与日程管理模块

### 6.1 课程管理

#### 6.1.1 创建课程
- **URL**: `/courses`
- **方法**: POST
- **描述**: 创建新课程
- **认证**: 需要JWT Token

请求参数：
```json
{
  "name": "数据结构",
  "teacher": "张教授",
  "location": "教学楼A-101",
  "dayOfWeek": 1,
  "startTime": "08:00:00",
  "endTime": "08:50:00",
  "startWeek": 1,
  "endWeek": 16,
  "semester": "2024-2025-1",
  "color": "#4CAF50"
}
```

**参数说明**:
- `dayOfWeek`: 星期几（1-7，1表示周一，7表示周日）
- `startTime`/`endTime`: 课程时间，支持13个标准时间段：
  - 第1节课: 08:00-08:50
  - 第2节课: 08:50-09:40
  - 第3节课: 09:50-10:40
  - 第4节课: 10:40-11:30
  - 第5节课: 11:30-12:20
  - 第6节课: 14:05-14:55
  - 第7节课: 14:55-15:45
  - 第8节课: 15:45-16:35
  - 第9节课: 16:40-17:30
  - 第10节课: 17:30-18:20
  - 第11节课: 18:30-19:20
  - 第12节课: 19:20-20:10
  - 第13节课: 20:10-21:00
- `semester`: 学期格式为"YYYY-YYYY-X"（如："2024-2025-1"表示2024-2025学年第一学期）

响应结果：
```json
{
  "code": 200,
  "message": "创建课程成功",
  "data": {
    "courseId": 1
  }
}
```

#### 6.1.2 获取课程详情
- **URL**: `/courses/{id}`
- **方法**: GET
- **描述**: 获取课程详情
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 12345,
    "name": "数据结构",
    "teacher": "张教授",
    "location": "教学楼A-101",
    "dayOfWeek": 1,
    "startTime": "08:00:00",
    "endTime": "08:50:00",
    "startWeek": 1,
    "endWeek": 16,
    "semester": "2024-2025-1",
    "color": "#4CAF50",
    "status": 1,
    "createdAt": "2023-05-01T12:00:00",
    "updatedAt": "2023-05-01T12:00:00"
  }
}
```

#### 6.1.3 获取用户的所有课程
- **URL**: `/courses`
- **方法**: GET
- **描述**: 获取当前用户的所有课程
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "userId": 12345,
        "name": "数据结构",
        "teacher": "张教授",
        "location": "教学楼A-101",
        "dayOfWeek": 1,
        "startTime": "08:00:00",
        "endTime": "08:50:00",
        "startWeek": 1,
        "endWeek": 16,
        "semester": "2024-2025-1",
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 6.1.4 获取用户在指定星期几的课程
- **URL**: `/courses/day/{dayOfWeek}`
- **方法**: GET
- **描述**: 获取当前用户在指定星期几的课程
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "list": [
      {
        "id": 1,
        "userId": 12345,
        "name": "数据结构",
        "teacher": "张教授",
        "location": "教学楼A-101",
        "dayOfWeek": 1,
        "startTime": "08:00:00",
        "endTime": "08:50:00",
        "startWeek": 1,
        "endWeek": 16,
        "semester": "2024-2025-1",
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 6.1.5 获取用户在指定学期的课程
- **URL**: `/courses/semester/{semester}`
- **方法**: GET
- **描述**: 获取当前用户在指定学期的课程
- **认证**: 需要JWT Token

**参数说明**: `semester`路径参数示例："2024-2025-1"

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "userId": 12345,
        "name": "数据结构",
        "teacher": "张教授",
        "location": "教学楼A-101",
        "dayOfWeek": 1,
        "startTime": "08:00:00",
        "endTime": "08:50:00",
        "startWeek": 1,
        "endWeek": 16,
        "semester": "2024-2025-1",
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 6.1.6 更新课程
- **URL**: `/courses/{id}`
- **方法**: PUT
- **描述**: 更新课程
- **认证**: 需要JWT Token

请求参数：
```json
{
  "name": "高级数据结构",
  "teacher": "李教授",
  "location": "教学楼B-202",
  "dayOfWeek": 2,
  "startTime": "09:50:00",
  "endTime": "10:40:00",
  "startWeek": 1,
  "endWeek": 16,
  "semester": "2024-2025-1",
  "color": "#2196F3"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "更新课程成功",
  "data": null
}
```

#### 6.1.7 删除课程
- **URL**: `/courses/{id}`
- **方法**: DELETE
- **描述**: 删除课程
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除课程成功",
  "data": null
}
```

#### 6.1.8 检查课程时间冲突
- **URL**: `/courses/check-conflict`
- **方法**: GET
- **描述**: 检查课程时间是否冲突
- **认证**: 需要JWT Token

请求参数：
- **dayOfWeek** (query): 星期几（1-7）
- **startTime** (query): 开始时间，格式："HH:mm:ss"
- **endTime** (query): 结束时间，格式："HH:mm:ss"
- **excludeCourseId** (query, 可选): 排除的课程ID

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": false,
    "conflictCount": 0
  }
}
```

### 6.2 日程管理

#### 6.2.1 创建日程
- **URL**: `/schedules`
- **方法**: POST
- **描述**: 创建新日程
- **认证**: 需要JWT Token

请求参数：
```json
{
  "title": "小组会议",
  "description": "讨论项目进展",
  "startTime": "2023-05-10T14:00:00",
  "endTime": "2023-05-10T16:00:00",
  "location": "图书馆讨论室",
  "isAllDay": 0,
  "reminder": 30,
  "color": "#FF5722"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "创建日程成功",
  "data": {
    "scheduleId": 1
  }
}
```

#### 6.2.2 获取日程详情
- **URL**: `/schedules/{id}`
- **方法**: GET
- **描述**: 获取日程详情
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 12345,
    "title": "小组会议",
    "description": "讨论项目进展",
    "startTime": "2023-05-10T14:00:00",
    "endTime": "2023-05-10T16:00:00",
    "location": "图书馆讨论室",
    "isAllDay": 0,
    "reminder": 30,
    "color": "#FF5722",
    "status": 1,
    "createdAt": "2023-05-01T12:00:00",
    "updatedAt": "2023-05-01T12:00:00"
  }
}
```

#### 6.2.3 获取用户的所有日程
- **URL**: `/schedules`
- **方法**: GET
- **描述**: 获取当前用户的所有日程
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "userId": 12345,
        "title": "小组会议",
        "description": "讨论项目进展",
        "startTime": "2023-05-10T14:00:00",
        "endTime": "2023-05-10T16:00:00",
        "location": "图书馆讨论室",
        "isAllDay": 0,
        "reminder": 30,
        "color": "#FF5722",
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 6.2.4 获取用户在指定时间范围内的日程
- **URL**: `/schedules/range`
- **方法**: GET
- **描述**: 获取当前用户在指定时间范围内的日程
- **认证**: 需要JWT Token

请求参数：
- **startTime** (query): 开始时间，格式："yyyy-MM-ddTHH:mm:ss"
- **endTime** (query): 结束时间，格式："yyyy-MM-ddTHH:mm:ss"

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "list": [
      {
        "id": 1,
        "userId": 12345,
        "title": "小组会议",
        "description": "讨论项目进展",
        "startTime": "2023-05-10T14:00:00",
        "endTime": "2023-05-10T16:00:00",
        "location": "图书馆讨论室",
        "isAllDay": 0,
        "reminder": 30,
        "color": "#FF5722",
        "status": 1,
        "createdAt": "2023-05-01T12:00:00",
        "updatedAt": "2023-05-01T12:00:00"
      }
    ]
  }
}
```

#### 6.2.5 更新日程
- **URL**: `/schedules/{id}`
- **方法**: PUT
- **描述**: 更新日程
- **认证**: 需要JWT Token

请求参数：
```json
{
  "title": "项目讨论会",
  "description": "讨论项目下一阶段计划",
  "startTime": "2023-05-10T15:00:00",
  "endTime": "2023-05-10T17:00:00",
  "location": "线上会议",
  "isAllDay": 0,
  "reminder": 15,
  "color": "#9C27B0"
}
```

响应结果：
```json
{
  "code": 200,
  "message": "更新日程成功",
  "data": null
}
```

#### 6.2.6 删除日程
- **URL**: `/schedules/{id}`
- **方法**: DELETE
- **描述**: 删除日程
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除日程成功",
  "data": null
}
```

#### 6.2.7 检查日程时间冲突
- **URL**: `/schedules/check-conflict`
- **方法**: GET
- **描述**: 检查日程时间是否冲突
- **认证**: 需要JWT Token

请求参数：
- **startTime** (query): 开始时间，格式："yyyy-MM-ddTHH:mm:ss"
- **endTime** (query): 结束时间，格式："yyyy-MM-ddTHH:mm:ss"
- **excludeScheduleId** (query, 可选): 排除的日程ID

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hasConflict": false,
    "conflictCount": 0
  }
}
```

#### 6.2.8 处理日程提醒
- **URL**: `/schedules/process-reminders`
- **方法**: POST
- **描述**: 处理日程提醒（通常由系统定时任务调用）

响应结果：
```json
{
  "code": 200,
  "message": "处理日程提醒完成",
  "data": {
    "total": 5,
    "success": 5
  }
}
```

### 6.3 数据库设计说明

#### 课程表 (courses)
```sql
CREATE TABLE `courses` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `teacher` VARCHAR(50) DEFAULT NULL COMMENT '教师姓名',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '上课地点',
  `day_of_week` TINYINT NOT NULL COMMENT '星期几（1-7）',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `start_week` TINYINT NOT NULL COMMENT '开始周次',
  `end_week` TINYINT NOT NULL COMMENT '结束周次',
  `semester` VARCHAR(20) DEFAULT NULL COMMENT '学期（如：2024-2025-1）',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '显示颜色',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

#### 日程表 (schedules)
```sql
CREATE TABLE `schedules` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日程ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '日程标题',
  `description` TEXT DEFAULT NULL COMMENT '日程描述',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '地点',
  `is_all_day` TINYINT DEFAULT 0 COMMENT '是否全天（0-否, 1-是）',
  `reminder` TINYINT DEFAULT NULL COMMENT '提醒时间（分钟）',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '显示颜色',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-删除, 1-正常）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

**特性**:
- 支持学期管理和多学期课程数据
- 支持13个标准课程时间段的时间安排
- 支持时间冲突检测
- 支持课程和日程的颜色标记
- 支持日程提醒功能
- 学期格式统一为"YYYY-YYYY-X"格式

**课程时间安排说明**:
系统支持13个标准课程时间段：
- 第1节课: 08:00-08:50
- 第2节课: 08:50-09:40  
- 第3节课: 09:50-10:40
- 第4节课: 10:40-11:30
- 第5节课: 11:30-12:20
- 第6节课: 14:05-14:55
- 第7节课: 14:55-15:45
- 第8节课: 15:45-16:35
- 第9节课: 16:40-17:30
- 第10节课: 17:30-18:20
- 第11节课: 18:30-19:20
- 第12节课: 19:20-20:10
- 第13节课: 20:10-21:00

## 7. AI辅助学习模块

### 7.1 核心接口

#### 7.1.1 发送消息给AI（流式响应）
- **URL**: `/ai/chat`
- **方法**: POST
- **描述**: 向AI发送消息并获取流式回复
- **认证**: 需要JWT Token
- **Content-Type**: `application/x-www-form-urlencoded`
- **响应类型**: `text/html;charset=UTF-8`（流式文本响应）

请求参数（Form Data格式）：
```
prompt=如何学好Python编程？
sessionId=session_1234567890
```

**参数说明**:
- `prompt`: 用户发送的消息内容（必填）
- `sessionId`: 会话ID，如果不提供则由后端创建新会话（可选）

**流式响应**:
接口返回原始文本流，AI的回复内容会逐字符或逐词流式返回：
```
学好Python编程需要循序渐进地学习，首先掌握基础语法，然后通过实际项目练习...
```



#### 7.1.2 获取聊天会话列表
- **URL**: `/ai/sessions`
- **方法**: GET
- **描述**: 获取当前用户的聊天会话列表
- **认证**: 需要JWT Token

请求参数：
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为20

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessions": [
      {
        "id": "session_1234567890",
        "title": "Python学习咨询",
        "createdAt": "2025-01-27T10:00:00",
        "updatedAt": "2025-01-27T15:30:00",
        "lastMessageTime": "2025-01-27T15:30:00",
        "messageCount": 8
      }
    ],
    "total": 5
  }
}
```

#### 7.1.3 获取会话消息历史
- **URL**: `/ai/sessions/{sessionId}/messages`
- **方法**: GET
- **描述**: 获取指定会话的消息历史
- **认证**: 需要JWT Token

请求参数：
- **page** (query, 可选): 页码，默认为1
- **size** (query, 可选): 每页大小，默认为50

响应结果：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "messages": [
      {
        "id": "msg_001",
        "role": "user",
        "content": "如何学好Python编程？",
        "timestamp": "2025-01-27T10:00:00"
      },
      {
        "id": "msg_002",
        "role": "assistant", 
        "content": "学好Python编程需要循序渐进...",
        "timestamp": "2025-01-27T10:00:30"
      }
    ],
    "total": 8,
    "sessionInfo": {
      "id": "session_1234567890",
      "title": "Python学习咨询",
      "createdAt": "2025-01-27T10:00:00",
      "updatedAt": "2025-01-27T15:30:00",
      "lastMessageTime": "2025-01-27T15:30:00",
      "messageCount": 8
    }
  }
}
```

#### 7.1.4 创建聊天会话
- **URL**: `/ai/sessions`
- **方法**: POST
- **描述**: 创建新的AI聊天会话（sessionId由前端生成）
- **认证**: 需要JWT Token

请求参数：
```json
{
  "sessionId": "session_1706177600_abc123",
  "title": "新对话"
}
```

**参数说明**:
- `sessionId`: 会话ID，由前端生成（格式：session_<timestamp>_<random>）
- `title` (可选): 会话标题，默认为"新对话"

响应结果：
```json
{
  "code": 200,
  "message": "创建会话成功",
  "data": {
    "sessionId": "session_1706177600_abc123",
    "title": "新对话"
  }
}
```

#### 7.1.5 更新会话标题
- **URL**: `/ai/sessions/{sessionId}`
- **方法**: PUT
- **描述**: 更新会话标题
- **认证**: 需要JWT Token

请求参数：
```json
{
  "title": "更新后的标题"
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

#### 7.1.6 清空会话消息
- **URL**: `/ai/sessions/{sessionId}/messages`
- **方法**: DELETE
- **描述**: 清空指定会话的所有消息（保留会话）
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "清空成功",
  "data": null
}
```

#### 7.1.7 删除会话
- **URL**: `/ai/sessions/{sessionId}`
- **方法**: DELETE
- **描述**: 删除指定会话及其所有消息
- **认证**: 需要JWT Token

响应结果：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7.2 数据库设计说明

#### AI聊天会话表 (ai_chat_sessions)
```sql
CREATE TABLE `ai_chat_sessions` (
  `id` VARCHAR(64) PRIMARY KEY COMMENT '会话ID（前端生成）',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL DEFAULT '新对话' COMMENT '会话标题',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);
```

#### AI聊天消息表 (ai_chat_messages)
```sql
CREATE TABLE `ai_chat_messages` (
  `id` VARCHAR(64) PRIMARY KEY COMMENT '消息ID（前端生成）',
  `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
  `role` ENUM('user', 'assistant', 'system') NOT NULL COMMENT '角色',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_session_id` (`session_id`),
  FOREIGN KEY (`session_id`) REFERENCES `ai_chat_sessions` (`id`) ON DELETE CASCADE
);
```

### 7.3 流式响应特性说明

**核心特性**:
- 支持多会话管理，每个用户可以有多个独立的对话会话
- 消息按时间顺序存储，支持完整的对话历史记录
- 级联删除保证数据一致性
- 支持用户、助手和系统三种角色的消息
- 会话ID和消息ID由前端生成，确保前端能够立即使用
- ID格式：session_<timestamp>_<random> 和 msg_<timestamp>_<random>
- **流式响应**: 使用Server-Sent Events实现实时的AI回复流式传输

**前端流式处理示例**:
```typescript
import { sendMessage } from '@/api/ai'

// 使用流式API
await sendMessage({
  message: '你好',
  sessionId: 'session_123',
  conversationHistory: []
}, (chunk: string) => {
  // 处理每个数据块
  currentMessage.content += chunk
  // 实时更新UI
  updateMessageDisplay()
})
```

**后端流式实现**:
```java
@RestController
@RequestMapping("/ai")
public class AiController {
    
    private final AiService aiService;

    @Operation(summary = "发送消息给AI")
    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> sendMessage(
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "sessionId", required = false) String sessionId) {
        log.info("发送消息给AI: {}", prompt);
        
        AiSendMessageDTO sendMessageDTO = new AiSendMessageDTO();
        sendMessageDTO.setMessage(prompt);
        sendMessageDTO.setSessionId(sessionId);
        
        return aiService.sendMessage(sendMessageDTO);
    }
}
```

**Service层实现**:
```java
@Service
public class AiServiceImpl implements AiService {
    
    @Autowired
    private ChatClient chatClient;

    @Override
    public Flux<String> sendMessage(AiSendMessageDTO sendMessageDTO) {
        log.info("发送消息给AI: {}", sendMessageDTO.getMessage());
        
        // 使用Spring AI ChatClient的流式响应
        return chatClient.prompt(sendMessageDTO.getMessage())
                .stream()
                .content();
    }
}
```

## 8. 待实现模块

以下模块尚未实现，将在后续开发中完成：

- 搜索功能模块
- 积分系统模块