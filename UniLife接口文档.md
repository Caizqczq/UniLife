# UniLife接口文档

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
- [7. 待实现模块](#7-待实现模块)

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
      "createTime": "2024-01-15T10:30:00",
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
        "createdAt": "2023-05-01 12:00:00"
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
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
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
        "createdAt": "2023-05-01 12:00:00"
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
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
    "fileUrl": "uploads/resources/file.pdf",
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
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
  }
}
```

### 5.3 获取资源列表
- **URL**: `/resources`
- **方法**: GET
- **描述**: 获取资源列表，支持分页和筛选

请求参数：
- **category** (query, 可选): 分类ID
- **user** (query, 可选): 用户ID
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
        "fileUrl": "uploads/resources/file.pdf",
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
      }
    ],
    "pages": 10
  }
}
```

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
    "fileUrl": "uploads/resources/file.pdf",
    "fileName": "资源标题",
    "fileType": "application/pdf"
  }
}
```

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
        "fileUrl": "uploads/resources/file.pdf",
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
        "fileUrl": "uploads/resources/file.pdf",
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
      }
    ],
    "pages": 2
  }
}
```

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
  "endTime": "09:40:00",
  "startWeek": 1,
  "endWeek": 16,
  "semester": "2023-1",
  "color": "#4CAF50"
}
```

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
    "endTime": "09:40:00",
    "startWeek": 1,
    "endWeek": 16,
    "color": "#4CAF50",
    "status": 1,
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
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
        "endTime": "09:40:00",
        "startWeek": 1,
        "endWeek": 16,
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
        "endTime": "09:40:00",
        "startWeek": 1,
        "endWeek": 16,
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
        "endTime": "09:40:00",
        "startWeek": 1,
        "endWeek": 16,
        "semester": "2023-1",
        "color": "#4CAF50",
        "status": 1,
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
  "startTime": "10:00:00",
  "endTime": "11:40:00",
  "startWeek": 1,
  "endWeek": 16,
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
    "createdAt": "2023-05-01 12:00:00",
    "updatedAt": "2023-05-01 12:00:00"
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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
        "createdAt": "2023-05-01 12:00:00",
        "updatedAt": "2023-05-01 12:00:00"
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

## 7. 待实现模块

以下模块尚未实现，将在后续开发中完成：

- 搜索功能模块
- AI辅助学习模块
- 积分系统模块
