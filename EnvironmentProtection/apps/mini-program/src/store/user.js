// 用户状态管理 - 简单实现，无需 pinia

const TOKEN_KEY = 'lvfat_token'
const USER_INFO_KEY = 'lvfat_user'

// 状态
const state = {
  token: '',
  userInfo: null
}

// 简单的事件通知机制
const listeners = []

export const useUserStore = {
  // 获取 token
  get token() {
    return state.token
  },

  // 获取用户信息
  get userInfo() {
    return state.userInfo
  },

  // 计算属性
  get isLoggedIn() {
    return !!state.token
  },

  get userName() {
    return state.userInfo?.nickname || state.userInfo?.username || ''
  },

  get userId() {
    return state.userInfo?.id || ''
  },

  get userRole() {
    return state.userInfo?.role || ''
  },

  get isLegalExpert() {
    return state.userInfo?.role === 'legal_expert'
  },

  get isEnterprise() {
    return state.userInfo?.role === 'enterprise'
  },

  get isGovernment() {
    return state.userInfo?.role === 'government'
  },

  // 方法
  setToken(newToken) {
    state.token = newToken
    uni.setStorageSync(TOKEN_KEY, newToken)
    this.notify()
  },

  setUserInfo(info) {
    state.userInfo = info
    if (info) {
      uni.setStorageSync(USER_INFO_KEY, JSON.stringify(info))
    } else {
      uni.removeStorageSync(USER_INFO_KEY)
    }
    this.notify()
  },

  login(data) {
    this.setToken(data.token || data.tokenValue || '')
    this.setUserInfo(data.userInfo || data)
  },

  logout() {
    state.token = ''
    state.userInfo = null
    uni.removeStorageSync(TOKEN_KEY)
    uni.removeStorageSync(USER_INFO_KEY)
    this.notify()
  },

  // 初始化 - 从本地存储恢复
  init() {
    const storedToken = uni.getStorageSync(TOKEN_KEY)
    const storedUserInfo = uni.getStorageSync(USER_INFO_KEY)
    if (storedToken) {
      state.token = storedToken
    }
    if (storedUserInfo) {
      try {
        state.userInfo = JSON.parse(storedUserInfo)
      } catch {
        state.userInfo = null
      }
    }
    this.notify()
  },

  // 监听变化
  onChange(callback) {
    listeners.push(callback)
    return () => {
      const index = listeners.indexOf(callback)
      if (index > -1) listeners.splice(index, 1)
    }
  },

  notify() {
    listeners.forEach(fn => fn(state))
  }
}

/** 与页面中 `import { userStore }` 保持一致 */
export const userStore = useUserStore

// 初始化
useUserStore.init()
