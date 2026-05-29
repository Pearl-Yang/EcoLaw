/**
 * 咨询对接 API
 */
import request from '@/utils/request.js'

// 获取咨询列表
export function getConsultList(params) {
  return request.get('/law/consults', { params })
}

// 获取咨询详情
export function getConsultDetail(id) {
  return request.get(`/law/consults/${id}`)
}

// 创建咨询
export function createConsult(data) {
  return request.post('/law/consults', data)
}

// 更新咨询状态
export function updateConsultStatus(id, status) {
  return request.put(`/law/consults/${id}/status`, { status })
}

// 分配律师
export function assignLawyer(id, lawyerId) {
  return request.put(`/law/consults/${id}/assign`, { lawyerId })
}

// 获取对话记录
export function getChatMessages(consultId) {
  return request.get(`/law/consults/${consultId}/messages`)
}

// 发送消息
export function sendMessage(consultId, data) {
  return request.post(`/law/consults/${consultId}/messages`, data)
}

// 获取咨询统计
export function getConsultStats() {
  return request.get('/law/consults/stats')
}

// 导出咨询记录
export function exportConsults(params) {
  return request.get('/law/consults/export', { params, responseType: 'blob' })
}

// 获取律师列表
export function getLawyerList() {
  return request.get('/law/lawyers')
}

// 标记已读
export function markAsRead(id) {
  return request.put(`/law/consults/${id}/read`)
}

// 获取快捷回复
export function getQuickReplies() {
  return request.get('/law/quick-replies')
}
