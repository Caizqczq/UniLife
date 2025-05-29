# AI助手后端接口文档

## 重要提示 - 分类API更新需求

前端已修复分类数量显示问题，需要后端配合更新以下API：

### 论坛分类API (`/api/categories`)
返回的分类数据需要包含 `postCount` 字段：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "学术交流",
        "description": "学术讨论与交流",
        "postCount": 15,  // ← 新增：该分类下的帖子数量
        "status": 1,
        "createdAt": "2024-12-21T10:00:00Z"
      }
    ]
  }
}
```

### 资源分类API (`/api/categories`) 
当用于资源模块时，需要包含 `resourceCount` 字段：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "学术资料",
        "description": "学术相关资源",
        "resourceCount": 8,  // ← 新增：该分类下的资源数量
        "status": 1,
        "createdAt": "2024-12-21T10:00:00Z"
      }
    ]
  }
}
```

---

## 概述

AI助手功能为UniLife平台提供智能问答服务，支持多轮对话、会话管理、历史记录等功能。

## 基础信息

- **基础URL**: `/api/ai`
- **认证方式**: JWT Token (Header: `Authorization: Bearer <token>`)
- **响应格式**: JSON

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": "2024-12-21T10:30:00Z"
}
```

## 数据模型

### ChatMessage 聊天消息

```json
{
  "id": "string",
  "sessionId": "string", 
  "role": "user|assistant|system",
  "content": "string",
  "timestamp": "2024-12-21T10:30:00Z",
  "userId": "number"
}
```

### ChatSession 聊天会话

```json
{
  "id": "string",
  "userId": "number",
  "title": "string",
  "createdAt": "2024-12-21T10:30:00Z",
  "updatedAt": "2024-12-21T10:30:00Z", 
  "lastMessageTime": "2024-12-21T10:30:00Z",
  "messageCount": "number"
}
```

## API接口

### 1. 发送消息 (核心接口)

**POST** `/ai/chat`

向AI发送消息并获取回复。

**请求体:**
```json
{
  "message": "如何学习Java编程?",
  "sessionId": "session_123", // 可选，不传则创建新会话
  "conversationHistory": [    // 可选，用于上下文
    {
      "role": "user",
      "content": "之前的问题",
      "timestamp": "2024-12-21T10:25:00Z"
    }
  ]
}
```

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "messageId": "msg_456",
    "content": "学习Java编程可以从以下几个方面开始:\n\n1. **基础语法**\n   - 变量和数据类型\n   - 控制流程\n   - 面向对象\n\n2. **实践项目**\n   - 简单的控制台程序\n   - Web应用开发\n\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n```\n\n希望这个建议对你有帮助！",
    "sessionId": "session_123",
    "timestamp": "2024-12-21T10:30:00Z"
  }
}
```

### 2. 获取聊天会话列表

**GET** `/ai/sessions`

获取用户的所有聊天会话。

**查询参数:**
- `page`: number (默认: 1)
- `size`: number (默认: 20)

**响应:**
```json
{
  "code": 200,
  "data": {
    "sessions": [
      {
        "id": "session_123",
        "userId": 1,
        "title": "Java学习咨询",
        "createdAt": "2024-12-21T10:00:00Z",
        "updatedAt": "2024-12-21T10:30:00Z",
        "lastMessageTime": "2024-12-21T10:30:00Z",
        "messageCount": 5
      }
    ],
    "total": 10
  }
}
```

### 3. 获取会话消息历史

**GET** `/ai/sessions/{sessionId}/messages`

获取指定会话的消息历史。

**路径参数:**
- `sessionId`: string - 会话ID

**查询参数:**
- `page`: number (默认: 1) 
- `size`: number (默认: 50)

**响应:**
```json
{
  "code": 200,
  "data": {
    "messages": [
      {
        "id": "msg_001",
        "sessionId": "session_123",
        "role": "user",
        "content": "如何学习Java编程?",
        "timestamp": "2024-12-21T10:25:00Z",
        "userId": 1
      },
      {
        "id": "msg_002", 
        "sessionId": "session_123",
        "role": "assistant",
        "content": "学习Java编程可以从基础语法开始...",
        "timestamp": "2024-12-21T10:30:00Z",
        "userId": null
      }
    ],
    "total": 5,
    "sessionInfo": {
      "id": "session_123",
      "title": "Java学习咨询",
      "createdAt": "2024-12-21T10:00:00Z"
    }
  }
}
```

### 4. 创建新的聊天会话

**POST** `/ai/sessions`

创建一个新的聊天会话。

**请求体:**
```json
{
  "title": "新的聊天" // 可选，不传则自动生成
}
```

**响应:**
```json
{
  "code": 200,
  "data": {
    "sessionId": "session_456",
    "title": "新的聊天"
  }
}
```

### 5. 更新会话标题

**PUT** `/ai/sessions/{sessionId}`

更新指定会话的标题。

**路径参数:**
- `sessionId`: string

**请求体:**
```json
{
  "title": "Java学习讨论"
}
```

**响应:**
```json
{
  "code": 200,
  "message": "会话标题更新成功"
}
```

### 6. 删除聊天会话

**DELETE** `/ai/sessions/{sessionId}`

删除指定的聊天会话及其所有消息。

**路径参数:**
- `sessionId`: string

**响应:**
```json
{
  "code": 200,
  "message": "会话删除成功"
}
```

### 7. 清空会话消息

**DELETE** `/ai/sessions/{sessionId}/messages`

清空指定会话的所有消息，但保留会话。

**路径参数:**
- `sessionId`: string

**响应:**
```json
{
  "code": 200,
  "message": "会话消息已清空"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 无权限访问 |
| 404 | 会话或消息不存在 |
| 429 | 请求频率过高 |
| 500 | AI服务异常 |
| 503 | AI服务暂时不可用 |

## 实现建议

### 1. 数据库设计

**ai_sessions表:**
```sql
CREATE TABLE ai_sessions (
    id VARCHAR(64) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_message_time TIMESTAMP,
    message_count INT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_updated_at (updated_at)
);
```

**ai_messages表:**
```sql
CREATE TABLE ai_messages (
    id VARCHAR(64) PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    user_id BIGINT,
    role ENUM('user', 'assistant', 'system') NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (session_id) REFERENCES ai_sessions(id) ON DELETE CASCADE
);
```

### 2. AI服务集成

建议集成以下AI服务之一：
- **OpenAI GPT API** - 功能强大，支持多种模型
- **百度文心一言** - 中文优化，国内访问稳定  
- **阿里通义千问** - 企业级服务，安全可靠
- **智谱GLM** - 性价比高，中文效果好

### 3. 关键功能

1. **上下文管理**: 维护对话历史，提供连贯的多轮对话
2. **流式输出**: 支持Server-Sent Events实现打字机效果
3. **内容安全**: 过滤敏感内容，确保回复安全合规
4. **速率限制**: 防止滥用，控制每用户请求频率
5. **缓存机制**: 相似问题缓存回复，提高响应速度

### 4. 安全考虑

- 所有接口需要JWT认证
- 验证用户对会话的权限
- 过滤和审核用户输入
- 监控和记录异常使用行为
- 设置合理的内容长度限制

### 5. 性能优化

- 使用连接池管理AI服务连接
- 实现请求排队和负载均衡
- 添加Redis缓存热门问答
- 定期清理过期会话数据
- 监控API调用频率和成本

## 前端配合说明

前端已实现：
- ✅ 现代化聊天界面设计
- ✅ Markdown内容渲染
- ✅ 打字机效果动画
- ✅ 会话历史管理
- ✅ 响应式设计
- ✅ 示例问题快速发送
- ✅ 键盘快捷键支持

需要后端配合：
- 🔄 实现上述所有API接口
- 🔄 选择并集成AI服务
- 🔄 建立数据库表结构
- 🔄 添加安全和性能优化

## 测试建议

1. **单元测试**: 测试各API接口的基本功能
2. **集成测试**: 测试AI服务的集成和响应
3. **压力测试**: 测试并发请求的处理能力
4. **安全测试**: 验证权限控制和内容安全
5. **用户体验测试**: 测试实际使用场景和响应时间 