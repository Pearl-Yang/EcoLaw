/**
 * API 请求封装 - 适配 UniApp
 */
import { userStore } from '../store/user.js'

const BASE_URL = 'http://localhost:8080/api'

// 创建请求实例
class Request {
  constructor(baseURL) {
    this.baseURL = baseURL
    this.defaultHeader = {
      'Content-Type': 'application/json'
    }
  }

  getToken() {
    return userStore.token || uni.getStorageSync('lvfat_token') || null
  }

  buildHeaders(options) {
    const token = this.getToken()
    const headers = {
      ...this.defaultHeader,
      ...options.header
    }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    return headers
  }

  handleError(statusCode, message) {
    switch (statusCode) {
      case 401:
        uni.removeStorageSync('lvfat_token')
        uni.removeStorageSync('lvfat_user')
        uni.reLaunch({ url: '/pages/login/login' })
        uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
        break
      case 403:
        uni.showToast({ title: '没有权限访问该资源', icon: 'none' })
        break
      case 404:
        uni.showToast({ title: '请求的资源不存在', icon: 'none' })
        break
      case 500:
        uni.showToast({ title: '服务器错误，请稍后再试', icon: 'none' })
        break
      default:
        uni.showToast({ title: message || '网络错误', icon: 'none' })
    }
    throw new Error(message || 'Network Error')
  }

  async request(options) {
    const { url, method = 'GET', data, showLoading = true } = options

    if (showLoading) {
      uni.showLoading({ title: '加载中...', mask: true })
    }

    try {
      const headers = this.buildHeaders(options)
      const fullUrl = url.startsWith('http') ? url : this.baseURL + url

      const response = await new Promise((resolve, reject) => {
        uni.request({
          url: fullUrl,
          method,
          data,
          header: headers,
          success: resolve,
          fail: reject
        })
      })

      if (showLoading) {
        uni.hideLoading()
      }

      if (response.statusCode === 200) {
        const result = response.data
        if (result.code === 200 || result.code === undefined) {
          return result.data ?? result
        } else {
          uni.showToast({ title: result.message || '请求失败', icon: 'none' })
          throw new Error(result.message)
        }
      } else {
        this.handleError(response.statusCode)
      }
    } catch (error) {
      if (showLoading) {
        uni.hideLoading()
      }
      if (error.errMsg) {
        uni.showToast({ title: '请求失败', icon: 'none' })
      }
      throw error
    }

    return null
  }

  get(url, data, options) {
    return this.request({ url, method: 'GET', data, ...options })
  }

  post(url, data, options) {
    return this.request({ url, method: 'POST', data, ...options })
  }

  put(url, data, options) {
    return this.request({ url, method: 'PUT', data, ...options })
  }

  delete(url, data, options) {
    return this.request({ url, method: 'DELETE', data, ...options })
  }
}

const request = new Request(BASE_URL)

// API 方法
export const api = {
  // ============ 认证相关 ============
  auth: {
    login: (data, options) => request.post('/auth/login', data, options),
    register: (data, options) => request.post('/auth/register', data, options),
    sendSmsCode: (data, options) =>
      request.post('/auth/sms/send', data, { showLoading: false, ...options }),
    logout: () => request.post('/auth/logout'),
    getUserInfo: (options) => request.get('/auth/userinfo', undefined, options),
    getCaptcha: () => request.get('/auth/captcha'),
    refreshToken: () => request.post('/auth/refresh'),
    changePassword: (data) => request.post('/auth/change-password', data),
    /** 忘记密码：手机+验证码+新密码（后端未实现时页面内会提示） */
    resetPasswordBySms: (data, options) =>
      request.post('/auth/password/reset', data, { showLoading: true, ...options })
  },

  // ============ 用户相关 ============
  user: {
    list: (params) => request.get('/users', params),
    get: (id) => request.get(`/users/${id}`),
    create: (data) => request.post('/users', data),
    update: (id, data) => request.put(`/users/${id}`, data),
    delete: (id) => request.delete(`/users/${id}`),
    resetPassword: (id) => request.post(`/users/${id}/reset-password`)
  },

  // ============ 法规相关 ============
  law: {
    list: (params) => request.get('/laws', params),
    get: (id) => request.get(`/laws/${id}`),
    create: (data) => request.post('/laws', data),
    update: (id, data) => request.put(`/laws/${id}`, data),
    delete: (id) => request.delete(`/laws/${id}`),
    categories: () => request.get('/laws/categories')
  },

  // ============ 任务相关 ============
  task: {
    list: (params, options) => request.get('/tasks', params, options),
    get: (id, options) => request.get(`/tasks/${id}`, undefined, options),
    create: (data) => request.post('/tasks', data),
    update: (id, data) => request.put(`/tasks/${id}`, data),
    delete: (id) => request.delete(`/tasks/${id}`),
    complete: (id) => request.post(`/tasks/${id}/complete`),
    claim: (id) => request.post(`/tasks/${id}/claim`)
  },

  // ============ 培训相关 ============
  training: {
    list: (params, options) => request.get('/trainings', params, options),
    get: (id, options) => request.get(`/trainings/${id}`, undefined, options),
    create: (data) => request.post('/trainings', data),
    update: (id, data) => request.put(`/trainings/${id}`, data),
    delete: (id) => request.delete(`/trainings/${id}`),
    finish: (id, data) => request.post(`/trainings/${id}/finish`, data),
    statistics: (params, options) => request.get('/trainings/statistics', params, options)
  },

  // ============ 举报相关 ============
  report: {
    list: (params) => request.get('/reports', params),
    get: (id) => request.get(`/reports/${id}`),
    create: (data) => request.post('/reports', data),
    handle: (id, data) => request.post(`/reports/${id}/handle`, data),
    statistics: () => request.get('/reports/statistics')
  },

  // ============ AI 相关 ============
  ai: {
    // 对话功能
    chat: (data) => request.post('/ai/chat', data),
    conversations: {
      list: (params) => request.get('/ai/conversation/list', params),
      get: (id) => request.get(`/ai/conversation/${id}`),
      create: (params) => request.post('/ai/conversation', null, { params }),
      delete: (id) => request.delete(`/ai/conversation/${id}`),
      archive: (id) => request.put(`/ai/conversation/${id}/archive`),
      messages: (id) => request.get(`/ai/conversation/${id}/messages`),
      feedback: (messageId, feedback, feedbackContent) =>
        request.post(`/ai/conversation/messages/${messageId}/feedback`, null, {
          params: { feedback, feedbackContent }
        })
    },
    // AI 生成功能
    generateMaterial: (data) => request.post('/ai/material/generate', data),
    explainLaw: (data) => request.post('/ai/law/explain', data),
    analyzeCase: (data) => request.post('/ai/case/analyze', data),
    generateExam: (data) => request.post('/ai/exam/generate', data),
    generateReport: (data) => request.post('/ai/report/generate', data)
  },

  // ============ 案例相关 ============
  case: {
    list: (params) => request.get('/cases', params),
    get: (id) => request.get(`/cases/${id}`),
    create: (data) => request.post('/cases', data),
    update: (id, data) => request.put(`/cases/${id}`, data),
    delete: (id) => request.delete(`/cases/${id}`)
  },

  // ============ 组织相关 ============
  organization: {
    tree: () => request.get('/organizations/tree'),
    list: (params) => request.get('/organizations', params),
    get: (id) => request.get(`/organizations/${id}`),
    create: (data) => request.post('/organizations', data),
    update: (id, data) => request.put(`/organizations/${id}`, data),
    delete: (id) => request.delete(`/organizations/${id}`)
  },

  // ============ 资讯 ============
  news: {
    list: (params) => request.get('/news', params),
    get: (id) => request.get(`/news/${id}`),
    hot: (limit) => request.get('/news/hot', { limit }),
    like: (id, userId) => request.post(`/news/${id}/like`, null, { params: { userId } }),
    isLiked: (id, userId) => request.get(`/news/${id}/like/status`, { userId })
  },

  // ============ 通知 ============
  notification: {
    list: (params) => request.get('/notifications', params),
    get: (id) => request.get(`/notifications/${id}`),
    unreadCount: (userId) => request.get('/notifications/unread-count', { userId }),
    markAsRead: (id, userId) => request.put(`/notifications/${id}/read`, null, { params: { userId } }),
    markAllAsRead: (userId) => request.put('/notifications/read-all', null, { params: { userId } })
  },

  // ============ 评论 ============
  comment: {
    list: (targetType, targetId) => request.get('/comments', { targetType, targetId }),
    getReplies: (id) => request.get(`/comments/${id}/replies`),
    create: (data) => request.post('/comments', data),
    delete: (id) => request.delete(`/comments/${id}`),
    like: (id, userId) => request.post(`/comments/${id}/like`, null, { params: { userId } }),
    isLiked: (id, userId) => request.get(`/comments/${id}/like/status`, { userId })
  },

  // ============ 数据看板 ============
  dashboard: {
    overview: (params) => request.get('/dashboard/overview', params),
    trends: (params) => request.get('/dashboard/trends', params),
    pendingCount: () => request.get('/dashboard/pending-count'),
    news: () => request.get('/dashboard/news')
  },

  // ============ 宠物养成 ============
  pet: {
    // 获取宠物信息
    getInfo: (userId) => request.get(`/pet/info/${userId}`),
    // 创建宠物（首次进入）
    create: (data) => request.post('/pet/create', data),
    // 更新宠物位置
    updatePosition: (data) => request.post('/pet/position', data),
    // 完成互动
    completeInteraction: (data) => request.post('/pet/interaction', data),
    // 获取场景信息
    getScene: (sceneId) => request.get(`/pet/scene/${sceneId}`),
    // 获取排行榜
    getRanking: (params) => request.get('/pet/ranking', params)
  },

  // ============ 宠物养成空间（Breed） ============
  petBreed: {
    // 获取宠物养成空间状态
    getSpace: (userId, petId) => request.get(`/pet/breed/space/${userId}/${petId}`),
    // 创建或获取养成空间
    createSpace: (userId, petId) => request.post('/pet/breed/space/create', null, { params: { userId, petId } }),
    // 更新宠物位置
    move: (data) => request.post('/pet/breed/move', data),
    // 获取可用互动任务列表
    getTasks: (userId, petId) => request.get('/pet/breed/tasks', { userId, petId }),
    // 获取任务详情
    getTaskDetail: (taskCode) => request.get(`/pet/breed/task/${taskCode}`),
    // 完成互动任务
    completeTask: (data) => request.post('/pet/breed/complete-task', data),
    // 获取任务完成历史
    getTaskHistory: (userId, petId, page = 1, size = 20) =>
      request.get(`/pet/breed/task-history/${userId}/${petId}`, { page, size }),
    // 获取宠物等级信息
    getLevelInfo: (petId) => request.get(`/pet/breed/level-info/${petId}`),
    // 获取宠物信息
    getPetInfo: (userId, petId) => request.get('/pet/breed/pet-info', { userId, petId }),
    // 获取所有场景列表
    getAllScenes: () => request.get('/pet/breed/scenes'),
    // 获取场景详情
    getSceneDetail: (sceneCode) => request.get(`/pet/breed/scene/${sceneCode}`),
    // 解锁场景
    unlockScene: (userId, petId, sceneCode) =>
      request.post('/pet/breed/scene/unlock', null, { params: { userId, petId, sceneCode } }),
    // 切换场景
    switchScene: (userId, petId, sceneCode) =>
      request.post('/pet/breed/scene/switch', null, { params: { userId, petId, sceneCode } }),
    // 获取今日统计数据
    getTodayStats: (userId, petId) => request.get('/pet/breed/stats/today', { userId, petId }),
    // 获取累计统计数据
    getTotalStats: (userId, petId) => request.get('/pet/breed/stats/total', { userId, petId })
  }
}

export { request }
export default request
