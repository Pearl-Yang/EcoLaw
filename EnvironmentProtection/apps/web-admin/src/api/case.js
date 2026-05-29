/**
 * 案例管理 API
 */
import request from '@/utils/request.js'

// 获取案例列表
export function getCaseList(params) {
  return request.get('/cases', { params })
}

// 获取案例详情
export function getCase(id) {
  return request.get(`/cases/${id}`)
}

// 创建案例
export function createCase(data) {
  return request.post('/cases', data)
}

// 更新案例
export function updateCase(id, data) {
  return request.put(`/cases/${id}`, data)
}

// 删除案例
export function deleteCase(id) {
  return request.delete(`/cases/${id}`)
}

// 审核案例
export function reviewCase(id, data) {
  return request.post(`/cases/${id}/review`, data)
}

// 获取案例分类
export function getCaseCategories() {
  return request.get('/cases/categories')
}
