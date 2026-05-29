/**
 * 登录/注册：优先请求后端；失败时仅在「已获取验证码」前提下接受演示码（避免随意输入即可进入）。
 */
import { api } from '../api/index.js'
import { userStore } from '../store/user.js'

/** 后端未就绪时：须先点「获取验证码」，再输入此码 */
export const DEMO_SMS_CODE = '888888'

/** 密码登录演示（后端未就绪时）；正式环境对接后应删除或改为 false */
export const DEMO_PASSWORD = 'demo123456'

function buildUserInfo(phone, role, data) {
  const fromApi = data?.userInfo || data?.user || {}
  return {
    id: fromApi.id || data?.userId || phone,
    nickname: fromApi.nickname || fromApi.name || `用户${String(phone).slice(-4)}`,
    phone: fromApi.phone || phone,
    role: fromApi.role || role,
    ...fromApi
  }
}

/**
 * @returns {Promise<{ ok: boolean }>}
 */
export async function loginRequest({ phone, code, password, role, loginMode }) {
  const body =
    loginMode === 'code'
      ? { phone, code, role, loginType: 'sms' }
      : { phone, password, role, loginType: 'password' }

  try {
    const data = await api.auth.login(body)
    const token = data?.token || data?.tokenValue || data?.accessToken
    if (!token) {
      uni.showToast({ title: '登录响应异常', icon: 'none' })
      return { ok: false }
    }
    userStore.setToken(token)
    userStore.setUserInfo(buildUserInfo(phone, role, data))
    return { ok: true }
  } catch {
    return { ok: false }
  }
}

/**
 * 演示兜底：验证码须已请求；密码模式须为演示密码
 */
export function applyDemoLogin({ phone, role, code, password, loginMode, smsRequested }) {
  if (loginMode === 'code') {
    if (!smsRequested || code !== DEMO_SMS_CODE) return false
    userStore.setToken(`demo_${Date.now()}`)
    userStore.setUserInfo(buildUserInfo(phone, role, { user: { nickname: `用户${String(phone).slice(-4)}` } }))
    uni.showToast({
      title: '演示环境已登录，正式环境将校验真实验证码',
      icon: 'none',
      duration: 2600
    })
    return true
  }
  if (password === DEMO_PASSWORD) {
    userStore.setToken(`demo_${Date.now()}`)
    userStore.setUserInfo(buildUserInfo(phone, role, { user: { nickname: `用户${String(phone).slice(-4)}` } }))
    uni.showToast({ title: '演示环境已登录（密码演示账号）', icon: 'none', duration: 2200 })
    return true
  }
  return false
}

/**
 * @returns {Promise<{ ok: boolean }>}
 */
export async function registerRequest({ phone, code, password, role }) {
  try {
    await api.auth.register({ phone, code, password, role })
    return { ok: true }
  } catch {
    return { ok: false }
  }
}

export function applyDemoRegister({ phone, code, smsRequested }) {
  if (!smsRequested || code !== DEMO_SMS_CODE) return false
  uni.showToast({
    title: '演示环境注册成功，请使用同一验证码登录',
    icon: 'none',
    duration: 2600
  })
  return true
}
