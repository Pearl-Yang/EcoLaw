/**
 * 用户管理 API
 */
import request from '@/utils/request.js'

// 用户列表
export function getUserList(params) {
  return request.get('/system/users', { params })
}

// 用户详情
export function getUser(id) {
  return request.get(`/system/users/${id}`)
}

// 创建用户
export function createUser(data) {
  return request.post('/system/users', data)
}

// 更新用户
export function updateUser(id, data) {
  return request.put(`/system/users/${id}`, data)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/system/users/${id}`)
}

// 重置密码
export function resetPassword(id, password) {
  return request.post(`/system/users/${id}/reset-password`, { password })
}

// 修改用户状态
export function changeUserStatus(id, status) {
  return request.post(`/system/users/${id}/status`, { status })
}

// 获取用户角色列表
export function getRoleList() {
  return request.get('/system/roles')
}

// 分配用户角色
export function assignRoles(userId, roleIds) {
  return request.post(`/system/users/${userId}/roles`, { roleIds })
}
