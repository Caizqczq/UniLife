# Spring AI ChatMemory MySQL 集成说明

## 概述

本项目已成功集成Spring AI的ChatMemory功能，使用MySQL作为持久化存储，实现了真正的AI会话记忆功能。

## 主要改进

### 1. 删除的错误实现
- ❌ 删除了 `AiChatSession.java` - 自定义会话实体（与Spring AI不兼容）
- ❌ 删除了 `AiChatMessage.java` - 自定义消息实体（与Spring AI不兼容）
- ❌ 删除了基于TODO注释的伪实现

### 2. 新增的正确实现
- ✅ 添加了 `spring-ai-starter-model-chat-memory-repository-jdbc` 依赖
- ✅ 配置了基于MySQL的JdbcChatMemoryRepository
- ✅ 集成了MessageChatMemoryAdvisor，实现自动会话记忆
- ✅ 使用Spring AI标准的ChatMemory接口

## 核心功能

### 会话记忆机制
- **自动记忆**: 每次对话自动保存到MySQL数据库
- **上下文保持**: 支持最多20条消息的上下文窗口
- **会话隔离**: 不同conversationId的会话完全隔离
- **持久化存储**: 重启服务后会话记忆不丢失

### 数据库表结构
```sql
CREATE TABLE SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(36) NOT NULL,    -- 会话ID
    content TEXT NOT NULL,                   -- 消息内容
    type VARCHAR(10) NOT NULL,               -- 消息类型：USER/ASSISTANT/SYSTEM/TOOL
    timestamp TIMESTAMP NOT NULL,           -- 时间戳
    INDEX idx_conversation_id_timestamp (conversation_id, timestamp)
);
```

## API使用说明

### 1. 发送消息（带会话记忆）
```http
POST /ai/chat?prompt=你好&sessionId=my-session-001
```
- 自动将用户消息和AI回复保存到数据库
- 后续对话会自动加载历史上下文

### 2. 测试记忆功能
```http
GET /ai/test/memory?conversationId=test-session&message=我的名字是张三
GET /ai/test/memory?conversationId=test-session&message=我的名字是什么？
```
- 第二次询问AI会记住之前说过的名字

### 3. 查看会话历史
```http
GET /ai/test/history/test-session
```

### 4. 清空会话记忆
```http
DELETE /ai/test/memory/test-session
```

## 配置说明

### application.yml 关键配置
```yaml
spring:
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always  # 自动初始化表结构
            schema: classpath:schema-mysql.sql
```

### AiConfig.java 关键配置
```java
@Bean
public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(20)  // 保留最近20条消息
            .build();
}

@Bean
public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
    return ChatClient.builder(model)
            .defaultAdvisors(
                new SimpleLoggerAdvisor(),
                MessageChatMemoryAdvisor.builder(chatMemory).build()  // 自动记忆
            )
            .build();
}
```

## 使用最佳实践

### 1. 会话ID管理
- 前端生成唯一的sessionId（如：`session_${timestamp}_${random}`）
- 同一会话的所有消息使用相同的sessionId
- 新建会话时生成新的sessionId

### 2. 会话记忆策略
- 系统自动保留最近20条消息作为上下文
- 超过20条消息时，旧消息会被自动清理（仅从内存中，数据库保留完整历史）
- 可根据需要调整`maxMessages`参数

### 3. 数据库维护
- 定期清理过期的会话数据
- 监控`SPRING_AI_CHAT_MEMORY`表的大小
- 考虑为长期存储添加分区策略

## 注意事项

1. **会话列表功能限制**: Spring AI ChatMemory专注于消息存储，不提供会话元数据管理。如需要会话列表、标题管理等功能，建议维护单独的会话管理表。

2. **性能优化**: 对于高并发场景，考虑：
   - 数据库连接池配置
   - 消息内容压缩
   - 定期清理策略

3. **错误处理**: 当数据库连接失败时，ChatMemory会降级为内存模式，重启后会话记忆将丢失。

## 测试验证

启动应用后，可以通过以下步骤验证功能：

1. 访问 Swagger UI: `http://localhost:8087/doc.html`
2. 找到 "AI测试接口" 分组
3. 使用测试接口验证会话记忆功能
4. 检查MySQL数据库中的`SPRING_AI_CHAT_MEMORY`表

这样就完成了Spring AI ChatMemory与MySQL的完整集成，实现了真正的AI会话记忆功能！ 