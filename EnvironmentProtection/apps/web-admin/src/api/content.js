/**
 * 内容管理 API - 新闻、通知、评论
 */
import request from '@/utils/request.js'

// ============ 新闻/热点 API ============

/**
 * 获取新闻列表（小程序端用，返回已发布）
 */
export function getNewsList(params) {
  return request.get('/news', { params })
}

/**
 * 获取新闻列表（管理端用，支持分页）
 */
export function getNewsPage(params) {
  return request.get('/news/page', { params })
}

/**
 * 获取新闻详情
 */
export function getNews(id) {
  return request.get(`/news/${id}`)
}

/**
 * 获取热门新闻
 */
export function getHotNews(limit = 10) {
  return request.get('/news/hot', { params: { limit } })
}

/**
 * 创建新闻
 */
export function createNews(data) {
  return request.post('/news', data)
}

/**
 * 更新新闻
 */
export function updateNews(id, data) {
  return request.put(`/news/${id}`, data)
}

/**
 * 删除新闻
 */
export function deleteNews(id) {
  return request.delete(`/news/${id}`)
}

/**
 * 点赞新闻
 */
export function likeNews(id, userId) {
  return request.post(`/news/${id}/like`, null, { params: { userId } })
}

// ============ 通知 API ============

/**
 * 获取通知列表（小程序端用）
 */
export function getNotificationList(params) {
  return request.get('/notifications', { params })
}

/**
 * 获取通知列表（管理端用，支持分页）
 */
export function getNotificationPage(params) {
  return request.get('/notifications/page', { params })
}

/**
 * 获取通知详情
 */
export function getNotification(id) {
  return request.get(`/notifications/${id}`)
}

/**
 * 获取未读数量
 */
export function getUnreadCount(userId) {
  return request.get('/notifications/unread-count', { params: { userId } })
}

/**
 * 创建通知
 */
export function createNotification(data) {
  return request.post('/notifications', data)
}

/**
 * 更新通知
 */
export function updateNotification(id, data) {
  return request.put(`/notifications/${id}`, data)
}

/**
 * 删除通知
 */
export function deleteNotification(id) {
  return request.delete(`/notifications/${id}`)
}

/**
 * 标记已读
 */
export function markAsRead(id, userId) {
  return request.put(`/notifications/${id}/read`, null, { params: { userId } })
}

/**
 * 标记全部已读
 */
export function markAllAsRead(userId) {
  return request.put('/notifications/read-all', null, { params: { userId } })
}

// ============ 评论 API ============

/**
 * 获取评论列表（小程序端用）
 */
export function getCommentList(params) {
  return request.get('/comments', { params })
}

/**
 * 获取评论列表（管理端用，支持分页）
 */
export function getCommentPage(params) {
  return request.get('/comments/page', { params })
}

/**
 * 获取评论的回复
 */
export function getCommentReplies(id) {
  return request.get(`/comments/${id}/replies`)
}

/**
 * 创建评论
 */
export function createComment(data) {
  return request.post('/comments', data)
}

/**
 * 删除评论
 */
export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}

/**
 * 点赞评论
 */
export function likeComment(id, userId) {
  return request.post(`/comments/${id}/like`, null, { params: { userId } })
}

// ============ 类型常量 ============

// 新闻类型
export const NewsType = {
  NEWS: 'news',
  HOTSPOT: 'hotspot',
  POLICY: 'policy',
  ACTIVITY: 'activity'
}

// 新闻状态
export const NewsStatus = {
  DRAFT: 'draft',
  PUBLISHED: 'published',
  ARCHIVED: 'archived'
}

// 通知类型
export const NotificationType = {
  SYSTEM: 'system',
  TASK: 'task',
  TRAINING: 'training',
  ACTIVITY: 'activity',
  REPORT: 'report'
}

// 通知优先级
export const NotificationPriority = {
  LOW: 'low',
  NORMAL: 'normal',
  HIGH: 'high',
  URGENT: 'urgent'
}

// 通知状态
export const NotificationStatus = {
  DRAFT: 'draft',
  PUBLISHED: 'published',
  CANCELLED: 'cancelled'
}

// 评论状态
export const CommentStatus = {
  PENDING: 'pending',
  PUBLISHED: 'published',
  DELETED: 'deleted'
}

// 评论目标类型
export const CommentTargetType = {
  NEWS: 'news',
  TRAINING: 'training',
  CASE_INFO: 'case_info'
}
