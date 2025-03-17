## 一、API规范

### 1.1 基础信息



- **基础URL**: `http://localhost:8080`(本地测试)

- **接口格式**: RESTful API

- **数据格式**: JSON

- **字符编码**: UTF-8

- **认证方式**: JWT (JSON Web Token)

  

- **GET**: 获取资源
- **POST**: 创建资源
- **PUT**: 更新资源（全量更新）
- **PATCH**: 部分更新资源
- **DELETE**: 删除资源

### 1.2 响应规范

#### 响应状态码

- **200**: 成功
- **400**: 请求参数错误
- **401**: 未授权
- **403**: 禁止访问
- **404**: 资源不存在
- **500**: 服务器内部错误

#### 后端相应格式

成功响应:

```json
{
  "code": 200,  // 200表示成功
  "message": "success",
  "data": {   // 实际返回数据
    // ...
  }
}
```

错误相应:

```json
{
  "code": 400,  
  "message": "参数错误",  // 错误信息
  "data": null  
}
```

## 二、 数据库设计

###  2.1用户表设计 (users)

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

#### 建表语句

```sql
CREATE TABLE `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址（学校邮箱）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio` TEXT DEFAULT NULL COMMENT '个人简介',
  `gender` TINYINT DEFAULT 0 COMMENT '性别（0-未知, 1-男, 2-女）',
  `student_id` VARCHAR(20) UNIQUE DEFAULT NULL COMMENT '学号',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '院系',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
  `points` INT DEFAULT 0 COMMENT '积分',
  `role` TINYINT DEFAULT 0 COMMENT '角色（0-普通用户, 1-版主, 2-管理员）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用, 1-启用）',
  `is_verified` TINYINT DEFAULT 0 COMMENT '是否验证（0-未验证, 1-已验证）',
  `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最近登录IP',
  `login_time` DATETIME DEFAULT NULL COMMENT '最近登录时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_email` (`email`),
  INDEX `idx_username` (`username`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_role` (`role`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```



## 三、Api功能实现

### 3.1用户认证模块

#### 3.1.1 用户注册

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


#### 3.1.2 用户密码登录

- **URL**: `/users/login`
- **方法**: POST
- **描述**: 用户登录

请求参数：

```json
{
  "username": "student123",  // 用户名或邮箱
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

#### 3.1.3 获取邮箱验证码

- **URL**: `/users/code`

- **方法**: POST

- **描述**: 向指定邮箱发送登录验证码

  请求参数

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

#### 3.1.4 邮箱验证码登录

- **URL**: `/users/login/code`
- **方法**: POST
- **描述**: 使用邮箱和验证码进行登录

请求参数

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

