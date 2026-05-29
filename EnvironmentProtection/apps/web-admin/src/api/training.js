/**
 * 培训管理 API
 */
import request from '@/utils/request.js'

// 获取培训列表
export function getTrainingList(params) {
  return request.get('/trainings', { params })
}

// 获取培训详情
export function getTraining(id) {
  return request.get(`/trainings/${id}`)
}

// 创建培训
export function createTraining(data) {
  return request.post('/trainings', data)
}

// 更新培训
export function updateTraining(id, data) {
  return request.put(`/trainings/${id}`, data)
}

// 删除培训
export function deleteTraining(id) {
  return request.delete(`/trainings/${id}`)
}

// 派发培训
export function dispatchTraining(id, organizationIds) {
  return request.post(`/trainings/${id}/dispatch`, { organizationIds })
}

// 获取培训统计
export function getTrainingStatistics(params) {
  return request.get('/trainings/statistics', { params })
}

// 获取培训记录
export function getTrainingRecords(params) {
  return request.get('/trainings/records', { params })
}
