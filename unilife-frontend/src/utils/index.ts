/**
 * 生成唯一的会话ID
 * 格式：session_<timestamp>_<random>
 */
export const generateSessionId = (): string => {
  const timestamp = Date.now()
  const random = Math.random().toString(36).substring(2, 8)
  return `session_${timestamp}_${random}`
}

/**
 * 生成唯一的消息ID
 * 格式：msg_<timestamp>_<random>
 */
export const generateMessageId = (): string => {
  const timestamp = Date.now()
  const random = Math.random().toString(36).substring(2, 8)
  return `msg_${timestamp}_${random}`
} 