-- Spring AI Chat Memory MySQL Schema
-- 此表用于存储AI聊天会话的消息记忆

CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(36) NOT NULL COMMENT '会话ID',
    content TEXT NOT NULL COMMENT '消息内容',
    type VARCHAR(10) NOT NULL COMMENT '消息类型：USER、ASSISTANT、SYSTEM、TOOL',
    `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间戳',
    INDEX idx_conversation_id_timestamp (conversation_id, `timestamp`),
    CONSTRAINT chk_message_type CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Spring AI聊天记忆表';

-- 会话元数据管理表（补充Spring AI ChatMemory功能）
-- 只管理会话的元数据信息，消息存储由Spring AI ChatMemory处理
CREATE TABLE IF NOT EXISTS ai_chat_sessions (
    id VARCHAR(64) PRIMARY KEY COMMENT '会话ID（前端生成）',
    user_id BIGINT NULL COMMENT '用户ID（可选，支持匿名会话）',
    title VARCHAR(200) NOT NULL DEFAULT '新对话' COMMENT '会话标题',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天会话元数据表'; 