/**
 * 用户认证相关工具函数
 */
import { getDemoUser } from './mockUser.js'

const TOKEN_KEY = 'lvfat_token'
const USER_INFO_KEY = 'lvfat_user'

/**
 * 获取Token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置Token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除Token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const info = localStorage.getItem(USER_INFO_KEY)
  if (info) {
    try {
      return JSON.parse(info)
    } catch {
      // 解析失败，使用默认虚拟用户
    }
  }
  // 返回演示虚拟用户
  const demoUser = getDemoUser()
  setUserInfo(demoUser)
  return demoUser
}

/**
 * 设置用户信息
 */
export function setUserInfo(info) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
}

/**
 * 用户登录（模拟）
 */
export async function login(data) {
  // 模拟登录延迟
  await new Promise(r => setTimeout(r, 800))
  
  // 简单验证
  if (!data.username || !data.password) {
    throw new Error('请输入用户名和密码')
  }
  
  // 模拟登录成功
  const mockUser = getDemoUser()
  const token = 'mock-token-' + Date.now()
  
  setToken(token)
  setUserInfo(mockUser)
  
  return { token, userInfo: mockUser }
}

/**
 * 用户登出
 */
export async function logout() {
  removeToken()
}

/**
 * 获取当前用户角色
 */
export function getUserRole() {
  const info = getUserInfo()
  return info?.role || null
}

/**
 * 判断是否有指定角色
 */
export function hasRole(role) {
  const userRole = getUserRole()
  if (!userRole) return false
  
  if (Array.isArray(role)) {
    return role.includes(userRole)
  }
  return userRole === role
}
