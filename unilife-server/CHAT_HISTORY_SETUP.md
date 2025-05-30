# AI聊天会话历史功能设置指南

## 概述

本文档详细说明如何在您的UniLife项目中设置和使用AI聊天会话历史功能。该功能结合了Spring AI的ChatMemory（会话记忆）和MySQL数据库存储，实现了完整的会话历史管理。

## 功能特性

### 🔥 核心功能
- **双重存储架构**: Spring AI ChatMemory（短期记忆）+ MySQL（长期历史）
- **自动同步**: ChatMemory消息自动同步到MySQL历史表
- **会话管理**: 支持会话列表、标题管理、删除等操作
- **匿名支持**: 支持匿名会话和用户会话
- **性能优化**: 历史表查询优化，支持分页
- **兼容性**: 完全兼容现有的Spring AI ChatMemory功能

### 📊 数据库表结构

1. **SPRING_AI_CHAT_MEMORY** - Spring AI原生记忆表
2. **ai_chat_sessions** - 会话元数据管理表
3. **ai_chat_messages_history** - 消息历史详情表

## 设置步骤

### 1. 数据库初始化

运行以下SQL脚本来创建必要的表结构：

```sql
-- Spring AI Chat Memory表（自动创建）
CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(36) NOT NULL COMMENT '会话ID',
    content TEXT NOT NULL COMMENT '消息内容',
    type VARCHAR(10) NOT NULL COMMENT '消息类型：USER、ASSISTANT、SYSTEM、TOOL',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间戳',
    INDEX idx_conversation_id_timestamp (conversation_id, `timestamp`),
    CONSTRAINT chk_message_type CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 会话历史管理表
CREATE TABLE IF NOT EXISTS ai_chat_sessions (
    id VARCHAR(64) PRIMARY KEY COMMENT '会话ID（前端生成）',
    user_id BIGINT NULL COMMENT '用户ID（可选，支持匿名会话）',
    title VARCHAR(200) NOT NULL DEFAULT '新对话' COMMENT '会话标题',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_message_time TIMESTAMP NULL COMMENT '最后消息时间',
    message_count INT NOT NULL DEFAULT 0 COMMENT '消息总数',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 会话消息历史详情表
CREATE TABLE IF NOT EXISTS ai_chat_messages_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息历史ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    conversation_id VARCHAR(36) NOT NULL COMMENT 'Spring AI会话ID',
    role ENUM('user', 'assistant', 'system', 'tool') NOT NULL COMMENT '消息角色',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (session_id) REFERENCES ai_chat_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 2. 配置验证

确保`application.yml`中包含以下配置：

```yaml
spring:
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always
            schema: classpath:schema-mysql.sql
  datasource:
    url: jdbc:mysql://localhost:3306/UniLife?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. Maven依赖检查

确保`pom.xml`中包含Spring AI JDBC ChatMemory依赖：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
</dependency>
```

## 使用指南

### API接口说明

#### 1. 发送消息（带会话记忆）
```http
GET /ai/chat?prompt=你好&sessionId=session_001
```

#### 2. 获取会话列表
```http
GET /ai/sessions?page=1&size=20
```

#### 3. 获取会话消息历史
```http
GET /ai/sessions/{sessionId}/messages?page=1&size=50
```

#### 4. 创建会话
```http
POST /ai/sessions
{
    "sessionId": "session_123456789",
    "title": "关于Spring Boot的讨论"
}
```

#### 5. 更新会话标题
```http
PUT /ai/sessions/{sessionId}
{
    "title": "新的会话标题"
}
```

#### 6. 删除会话
```http
DELETE /ai/sessions/{sessionId}
```

### 测试接口

系统提供了专门的测试接口来验证功能：

#### 1. 测试会话记忆
```http
GET /ai/test/memory?conversationId=test-001&message=我的名字是张三
GET /ai/test/memory?conversationId=test-001&message=我的名字是什么？
```

#### 2. 查看会话历史
```http
GET /ai/test/history/test-001
```

#### 3. 查看Spring AI原始记忆
```http
GET /ai/test/raw-memory/test-001
```

#### 4. 同步记忆到历史表
```http
POST /ai/test/sync/test-001
```

## 架构说明

### 数据流程

1. **用户发送消息** → ChatClient处理 → Spring AI ChatMemory自动存储
2. **消息处理完成** → 自动同步到MySQL历史表
3. **查询历史** → 优先从历史表查询（性能更好）
4. **会话管理** → 独立的会话元数据管理

### 关键组件

- **AiChatSessionHistoryService**: 会话历史管理服务
- **AiChatSessionMapper**: 会话数据访问层
- **AiChatMessageHistoryMapper**: 消息历史数据访问层
- **AiServiceImpl**: 整合Spring AI和历史管理的主服务

## 最佳实践

### 1. 会话ID管理
- 使用格式：`session_${timestamp}_${random}`
- 前端生成，确保唯一性
- 示例：`session_1703827200000_abc123`

### 2. 性能优化
- 定期清理过期会话数据
- 监控`SPRING_AI_CHAT_MEMORY`表大小
- 使用分页查询大量历史数据

### 3. 错误处理
- 数据库连接失败时ChatMemory会降级为内存模式
- 同步失败不影响主要聊天功能
- 提供手动同步接口

## 监控和维护

### 1. 数据库监控
```sql
-- 查看会话统计
SELECT COUNT(*) as total_sessions FROM ai_chat_sessions;

-- 查看消息统计
SELECT COUNT(*) as total_messages FROM ai_chat_messages_history;

-- 查看Spring AI记忆表大小
SELECT COUNT(*) as memory_records FROM SPRING_AI_CHAT_MEMORY;
```

### 2. 清理过期数据
```sql
-- 删除30天前的会话
DELETE FROM ai_chat_sessions 
WHERE updated_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

## 故障排除

### 常见问题

1. **表不存在错误**
   - 检查`schema-mysql.sql`是否正确
   - 确认`initialize-schema: always`配置

2. **同步失败**
   - 检查数据库连接
   - 查看日志中的错误信息

3. **会话列表为空**
   - 确认会话已创建：`POST /ai/test/session`
   - 检查数据库中`ai_chat_sessions`表

### 调试命令

```bash
# 查看应用日志
tail -f logs/unilife.log | grep -i "chat\|memory\|session"

# 检查数据库连接
mysql -u root -p -e "USE UniLife; SHOW TABLES LIKE '%chat%';"
```

## 总结

该实现提供了一个完整的AI聊天会话历史管理解决方案，结合了Spring AI的强大记忆功能和MySQL的持久化存储。通过这种双重存储架构，您既享受了Spring AI的智能记忆管理，又获得了完整的会话历史功能。

启动应用后，访问Swagger UI（`http://localhost:8087/doc.html`）查看所有可用的API接口并进行测试。 