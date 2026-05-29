/**
 * 通知管理 API
 */
import request from '@/utils/request.js'

// 获取通知列表
export function getNotificationList(params) {
  return request.get('/notifications', { params })
}

// 获取通知详情
export function getNotification(id) {
  return request.get(`/notifications/${id}`)
}

// 创建通知
export function createNotification(data) {
  return request.post('/notifications', data)
}

// 更新通知
export function updateNotification(id, data) {
  return request.put(`/notifications/${id}`, data)
}

// 删除通知
export function deleteNotification(id) {
  return request.delete(`/notifications/${id}`)
}

// 发布通知
export function publishNotification(id) {
  return request.post(`/notifications/${id}/publish`)
}

// 撤回通知
export function revokeNotification(id) {
  return request.post(`/notifications/${id}/revoke`)
}
