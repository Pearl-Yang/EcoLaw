/**
 * 法规查询 API (law-star.com)
 * 独立于后端API的外部法规服务
 */
import axios from 'axios'

// 创建 axios 实例
const lawService = axios.create({
  baseURL: import.meta.env.VITE_LAW_API_URL || 'https://api.law-star.com',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加签名认证
lawService.interceptors.request.use(
  (config) => {
    const timestamp = Date.now()
    const appId = import.meta.env.VITE_LAW_APP_ID
    const appSecret = import.meta.env.VITE_LAW_APP_SECRET
    
    // 添加认证头
    config.headers['X-App-Id'] = appId
    config.headers['X-Timestamp'] = timestamp
    config.headers['X-Sign'] = generateSign(appId, appSecret, timestamp)
    
    return config
  },
  (error) => {
    console.error('Law API Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
lawService.interceptors.response.use(
  (response) => {
    const res = response.data
    return res
  },
  (error) => {
    console.error('Law API Response error:', error)
    return Promise.reject(error)
  }
)

/**
 * 生成签名
 * @param {string} appId
 * @param {string} appSecret
 * @param {number} timestamp
 * @returns {string}
 */
function generateSign(appId, appSecret, timestamp) {
  // 简单的签名生成算法，根据实际API要求调整
  const str = `${appId}${appSecret}${timestamp}`
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash
  }
  return Math.abs(hash).toString(16)
}

// ========== API 方法 ==========

/**
 * 搜索法规
 * @param {Object} params - 搜索参数
 * @param {string} params.keyword - 关键词
 * @param {string} params.category - 分类
 * @param {string} params.region - 地区
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 */
export function searchLaws(params) {
  return lawService.get('/api/laws/search', { params })
}

/**
 * 获取法规详情
 * @param {string} id - 法规ID
 */
export function getLawDetail(id) {
  return lawService.get(`/api/laws/${id}`)
}

/**
 * 获取法规全文
 * @param {string} id - 法规ID
 */
export function getLawContent(id) {
  return lawService.get(`/api/laws/${id}/content`)
}

/**
 * 获取法规章节列表
 * @param {string} id - 法规ID
 */
export function getLawChapters(id) {
  return lawService.get(`/api/laws/${id}/chapters`)
}

/**
 * 获取章节内容
 * @param {string} chapterId - 章节ID
 */
export function getChapterContent(chapterId) {
  return lawService.get(`/api/chapters/${chapterId}/content`)
}

/**
 * 获取法规分类
 */
export function getLawCategories() {
  return lawService.get('/api/laws/categories')
}

/**
 * 获取法规列表
 * @param {Object} params - 查询参数
 */
export function getLawList(params) {
  return lawService.get('/api/laws', { params })
}

/**
 * 获取最新法规
 * @param {number} limit - 返回数量
 */
export function getLatestLaws(limit = 10) {
  return lawService.get('/api/laws/latest', { params: { limit } })
}

/**
 * 获取热门法规
 * @param {number} limit - 返回数量
 */
export function getHotLaws(limit = 10) {
  return lawService.get('/api/laws/hot', { params: { limit } })
}

/**
 * AI法规解读/问答
 * @param {string} lawId - 法规ID
 * @param {string} question - 问题
 */
export function explainLaw(lawId, question) {
  return lawService.post(`/api/laws/${lawId}/explain`, { question })
}

// 导出 axios 实例（用于特殊情况）
export { lawService }
