/**
 * API 请求封装
 */
import axios from 'axios'
import { ElMessage, ElNotification } from 'element-plus'
import { getToken, removeToken } from './auth.js'
import router from '@/router/index.js'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 成功响应
    if (res.code === 200 || res.success) {
      return res.data ?? res
    }
    
    // 业务错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('Response error:', error)
    
    const status = error.response?.status
    const message = error.response?.data?.message || error.message
    
    switch (status) {
      case 401:
        ElNotification.error({ title: '登录已过期', message: '请重新登录' })
        removeToken()
        router.push('/login')
        break
      case 403:
        ElMessage.error('没有权限访问该资源')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器错误，请稍后再试')
        break
      default:
        ElMessage.error(message || '网络错误')
    }
    
    return Promise.reject(error)
  }
)

// 发送请求
const request = (config) => service(config)

// 兼容 get/post/put/delete 方法
request.get = function(url, config) {
  return service({ ...config, method: 'get', url })
}

request.post = function(url, data, config) {
  return service({ ...config, method: 'post', url, data })
}

request.put = function(url, data, config) {
  return service({ ...config, method: 'put', url, data })
}

request.delete = function(url, config) {
  return service({ ...config, method: 'delete', url })
}

export default request
