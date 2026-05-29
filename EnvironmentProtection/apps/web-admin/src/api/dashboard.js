/**
 * 数据看板 API
 */
import request from '@/utils/request.js'

// 仪表盘概览
export function getDashboardOverview(params) {
  return request.get('/dashboard/overview', { params })
}

// 趋势数据
export function getDashboardTrends(params) {
  return request.get('/dashboard/trends', { params })
}

// 待办数量
export function getPendingCount() {
  return request.get('/dashboard/pending-count')
}

// 最新动态
export function getDashboardNews() {
  return request.get('/dashboard/news')
}

// 完整仪表盘数据
export function getDashboardData() {
  return request.get('/dashboard')
}
