/**
 * 律所客户管理 API
 */
import request from '@/utils/request.js'

// 获取客户列表
export function getClientList(params) {
  return request.get('/law/clients', { params })
}

// 获取客户详情
export function getClientDetail(id) {
  return request.get(`/law/clients/${id}`)
}

// 添加客户
export function addClient(data) {
  return request.post('/law/clients', data)
}

// 更新客户
export function updateClient(id, data) {
  return request.put(`/law/clients/${id}`, data)
}

// 删除客户
export function deleteClient(id) {
  return request.delete(`/law/clients/${id}`)
}

// 客户续费
export function renewClient(id, data) {
  return request.post(`/law/clients/${id}/renew`, data)
}

// 发送续费提醒
export function sendRenewReminder(id) {
  return request.post(`/law/clients/${id}/reminder`)
}

// 获取服务记录
export function getServiceRecords(clientId) {
  return request.get(`/law/clients/${clientId}/records`)
}

// 添加服务记录
export function addServiceRecord(clientId, data) {
  return request.post(`/law/clients/${clientId}/records`, data)
}

// 导出客户数据
export function exportClients(params) {
  return request.get('/law/clients/export', { params, responseType: 'blob' })
}

// 获取客户统计
export function getClientStats() {
  return request.get('/law/clients/stats')
}
