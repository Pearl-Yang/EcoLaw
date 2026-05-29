/**
 * 任务下发 API
 */
import request from '@/utils/request.js'

// 获取任务下发链路
export function getDispatchChain(taskId) {
  return request.get(`/task-dispatches/chain/${taskId}`)
}

// 获取下发记录的子记录
export function getDispatchChildren(dispatchId) {
  return request.get(`/task-dispatches/${dispatchId}/children`)
}

// 获取下发记录详情
export function getDispatchDetail(dispatchId) {
  return request.get(`/task-dispatches/${dispatchId}`)
}

// 下发任务到组织
export function dispatchToOrganizations(taskId, organizationIds) {
  return request.post('/task-dispatches/dispatch-to-orgs', organizationIds, {
    params: { taskId }
  })
}

// 向下级组织转发任务
export function delegateTask(dispatchId, organizationIds) {
  return request.post(`/task-dispatches/${dispatchId}/delegate`, organizationIds)
}

// 向指定职员下发任务
export function delegateToEmployees(dispatchId, employeeIds) {
  return request.post(`/task-dispatches/${dispatchId}/delegate-to-employees`, employeeIds)
}

// 接收任务
export function acceptDispatch(dispatchId) {
  return request.post(`/task-dispatches/${dispatchId}/accept`)
}

// 拒绝任务
export function rejectDispatch(dispatchId, reason) {
  return request.post(`/task-dispatches/${dispatchId}/reject`, null, {
    params: { reason }
  })
}

// 上报进度
export function reportProgress(dispatchId, completedCount, notes) {
  return request.post(`/task-dispatches/${dispatchId}/progress`, null, {
    params: { completedCount, notes }
  })
}

// 完成任务
export function completeDispatch(dispatchId) {
  return request.post(`/task-dispatches/${dispatchId}/complete`)
}

// 获取下发统计
export function getDispatchStatistics(organizationId, taskId) {
  return request.get('/task-dispatches/statistics', {
    params: { organizationId, taskId }
  })
}

// 获取待处理任务
export function getPendingDispatches(organizationId, userId) {
  return request.get('/task-dispatches/pending', {
    params: { organizationId, userId }
  })
}

// 获取可下发目标组织列表
export function getDispatchableTargets(organizationId) {
  return request.get('/task-dispatches/targets', {
    params: { organizationId }
  })
}

// 更新下发记录
export function updateDispatch(dispatchId, data) {
  return request.put(`/task-dispatches/${dispatchId}`, data)
}

// 删除下发记录
export function deleteDispatch(dispatchId) {
  return request.delete(`/task-dispatches/${dispatchId}`)
}

// 获取我下发的任务列表
export function getMyDispatchedTasks(organizationId) {
  return request.get('/task-dispatches/dispatched', {
    params: { organizationId }
  })
}

// 获取下发给我的任务列表
export function getMyReceivedTasks(organizationId, userId) {
  return request.get('/task-dispatches/received', {
    params: { organizationId, userId }
  })
}
