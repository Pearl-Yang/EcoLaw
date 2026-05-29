/**
 * 普法任务 API
 */
import request from '@/utils/request.js'

// 获取任务列表
export function getTaskList(params) {
  return request.get('/tasks', { params })
}

// 获取任务详情
export function getTask(id) {
  return request.get(`/tasks/${id}`)
}

// 创建任务
export function createTask(data) {
  return request.post('/tasks', data)
}

// 更新任务
export function updateTask(id, data) {
  return request.put(`/tasks/${id}`, data)
}

// 删除任务
export function deleteTask(id) {
  return request.delete(`/tasks/${id}`)
}

// 派发任务
export function dispatchTask(id, organizationIds) {
  return request.post(`/tasks/${id}/dispatch`, { organizationIds })
}

// 完成任务
export function completeTask(id, data) {
  return request.post(`/tasks/${id}/complete`, data)
}

// 上报进度
export function reportProgress(id, data) {
  return request.post(`/tasks/${id}/progress`, data)
}

// 获取任务统计
export function getTaskStatistics(params) {
  return request.get('/tasks/statistics', { params })
}
