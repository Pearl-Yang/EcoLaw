/**
 * 报表管理 API
 */

// 报表类型
export const ReportType = {
  TASK: 'task',
  COMPLIANCE: 'compliance',
  REPORT: 'report'
}

// 报表状态
export const ReportStatus = {
  PENDING: 'pending',
  PROCESSING: 'processing',
  COMPLETED: 'completed',
  FAILED: 'failed'
}

// 导出格式
export const ExportFormat = {
  EXCEL: 'xlsx',
  PDF: 'pdf',
  CSV: 'csv'
}

import request from '@/utils/request.js'

// ============ 报表导出 API ============

/**
 * 获取报表列表
 * @param {Object} params - 查询参数
 */
export function getReportList(params) {
  return request.get('/reports', { params })
}

/**
 * 获取报表详情
 * @param {string} id - 报表ID
 */
export function getReportDetail(id) {
  return request.get(`/reports/${id}`)
}

/**
 * 生成报表
 * @param {Object} data - 报表配置
 */
export function generateReport(data) {
  return request.post('/reports/generate', data)
}

/**
 * 导出报表
 * @param {string} type - 报表类型
 * @param {string} format - 导出格式
 * @param {Object} params - 查询参数
 */
export function exportReport(type, format, params) {
  return request.get(`/reports/export/${type}`, {
    params: { ...params, format },
    responseType: 'blob'
  })
}

/**
 * 导出全部报表
 * @param {string} format - 导出格式
 * @param {Object} params - 查询参数
 */
export function exportAllReports(format, params) {
  return request.get('/reports/export/all', {
    params: { format },
    responseType: 'blob'
  })
}

/**
 * 获取导出记录
 * @param {Object} params - 查询参数
 */
export function getExportHistory(params) {
  return request.get('/reports/export/history', { params })
}

/**
 * 下载报表
 * @param {string} id - 报表ID
 */
export function downloadReport(id) {
  return request.get(`/reports/download/${id}`, { responseType: 'blob' })
}

/**
 * 删除报表
 * @param {string} id - 报表ID
 */
export function deleteReport(id) {
  return request.delete(`/reports/${id}`)
}

// ============ 举报管理 API ============

// 举报类型
export const ReportType2 = {
  ILLEGAL_PRODUCTION: 'illegal_production',
  ILLEGAL_SALE: 'illegal_sale',
  ILLEGAL_USE: 'illegal_use',
  ENVIRONMENTAL_POLLUTION: 'environmental_pollution',
  OTHER: 'other'
}

// 举报状态
export const ReportStatus2 = {
  PENDING: 'pending',
  PROCESSING: 'processing',
  RESOLVED: 'resolved',
  REJECTED: 'rejected'
}

// 获取举报列表
export function getReportList(params) {
  return request.get('/reports', { params })
}

// 获取举报详情
export function getReport(id) {
  return request.get(`/reports/${id}`)
}

// 提交举报
export function submitReport(data) {
  return request.post('/reports', data)
}

// 处理举报
export function handleReport(id, data) {
  return request.post(`/reports/${id}/handle`, data)
}

// 获取举报统计
export function getReportStatistics(params) {
  return request.get('/reports/statistics', { params })
}
