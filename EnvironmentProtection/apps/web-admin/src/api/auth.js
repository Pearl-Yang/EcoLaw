/**
 * 认证模块 API
 */
import request from '@/utils/request.js'

// 登录
export function login(data) {
  return request.post('/auth/login', data)
}

// 获取验证码
export function getCaptcha() {
  return request.get('/auth/captcha')
}

// 刷新Token
export function refreshToken() {
  return request.post('/auth/refresh')
}

// 登出
export function logout() {
  return request.post('/auth/logout')
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/auth/userinfo')
}

// 修改密码
export function changePassword(data) {
  return request.post('/auth/change-password', data)
}
