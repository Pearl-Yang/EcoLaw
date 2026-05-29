/**
 * 职员管理 API
 */
import request from '@/utils/request.js'

// 获取职员列表
export function getEmployeeList(params) {
  return request.get('/employees', { params })
}

// 获取职员详情
export function getEmployee(id) {
  return request.get(`/employees/${id}`)
}

// 获取职员树
export function getEmployeeTree(id) {
  return request.get(`/employees/${id}/tree`)
}

// 获取组织所有职员
export function getOrganizationEmployees(organizationId, params) {
  return request.get(`/employees/organization/${organizationId}`, { params })
}

// 获取可下发目标列表
export function getDispatchTargets(organizationId) {
  return request.get('/employees/dispatch-targets', { params: { organizationId } })
}

// 创建职员
export function createEmployee(data) {
  return request.post('/employees', data)
}

// 批量创建职员
export function batchCreateEmployees(organizationId, data) {
  return request.post(`/employees/batch?organizationId=${organizationId}`, data)
}

// 更新职员
export function updateEmployee(id, data) {
  return request.put(`/employees/${id}`, data)
}

// 删除职员
export function deleteEmployee(id) {
  return request.delete(`/employees/${id}`)
}

// 调岗
export function transferEmployee(id, targetOrganizationId, newPosition) {
  return request.post(`/employees/${id}/transfer`, null, {
    params: { targetOrganizationId, newPosition }
  })
}

// 获取汇报链
export function getReportingChain(id) {
  return request.get(`/employees/${id}/reporting-chain`)
}

// 根据用户ID获取职员
export function getEmployeeByUser(userId) {
  return request.get(`/employees/user/${userId}`)
}
