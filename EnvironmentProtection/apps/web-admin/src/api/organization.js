/**
 * 组织管理 API
 */
import request from '@/utils/request.js'

// 获取组织树
export function getOrganizationTree() {
  return request.get('/system/organizations/tree')
}

// 获取组织列表
export function getOrganizationList(params) {
  return request.get('/system/organizations', { params })
}

// 获取组织详情
export function getOrganization(id) {
  return request.get(`/system/organizations/${id}`)
}

// 创建组织
export function createOrganization(data) {
  return request.post('/system/organizations', data)
}

// 更新组织
export function updateOrganization(id, data) {
  return request.put(`/system/organizations/${id}`, data)
}

// 删除组织
export function deleteOrganization(id) {
  return request.delete(`/system/organizations/${id}`)
}

// 获取组织用户
export function getOrganizationUsers(id, params) {
  return request.get(`/system/organizations/${id}/users`, { params })
}
