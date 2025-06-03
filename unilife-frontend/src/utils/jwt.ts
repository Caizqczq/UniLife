/**
 * JWT工具函数
 */

/**
 * 解析JWT token
 * @param token JWT token
 * @returns 解析后的payload或null
 */
export function parseJwtToken(token: string): any {
  try {
    const parts = token.split('.')
    if (parts.length !== 3) {
      return null
    }
    
    const payload = parts[1]
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
    return JSON.parse(decoded)
  } catch (error) {
    console.error('解析JWT token失败:', error)
    return null
  }
}

/**
 * 检查JWT token是否过期
 * @param token JWT token
 * @returns true表示过期，false表示未过期
 */
export function isTokenExpired(token: string): boolean {
  if (!token) {
    return true
  }
  
  const payload = parseJwtToken(token)
  if (!payload || !payload.exp) {
    return true
  }
  
  // JWT的exp是秒级时间戳，需要转换为毫秒
  const expirationTime = payload.exp * 1000
  const currentTime = Date.now()
  
  // 提前30秒判断过期，避免边界情况
  return currentTime >= (expirationTime - 30000)
}

/**
 * 获取token的剩余有效时间（毫秒）
 * @param token JWT token
 * @returns 剩余有效时间，如果已过期返回0
 */
export function getTokenRemainingTime(token: string): number {
  if (!token) {
    return 0
  }
  
  const payload = parseJwtToken(token)
  if (!payload || !payload.exp) {
    return 0
  }
  
  const expirationTime = payload.exp * 1000
  const currentTime = Date.now()
  const remainingTime = expirationTime - currentTime
  
  return Math.max(0, remainingTime)
} 