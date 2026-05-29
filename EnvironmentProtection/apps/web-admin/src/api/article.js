/**
 * 文章管理 API
 */
import request from '@/utils/request.js'

// 获取文章列表
export function getArticleList(params) {
  return request.get('/articles', { params })
}

// 获取文章详情
export function getArticle(id) {
  return request.get(`/articles/${id}`)
}

// 创建文章
export function createArticle(data) {
  return request.post('/articles', data)
}

// 更新文章
export function updateArticle(id, data) {
  return request.put(`/articles/${id}`, data)
}

// 删除文章
export function deleteArticle(id) {
  return request.delete(`/articles/${id}`)
}

// 审核文章
export function auditArticle(id, data) {
  return request.post(`/articles/${id}/audit`, data)
}

// 获取文章评论列表
export function getArticleComments(articleId, params) {
  return request.get(`/articles/${articleId}/comments`, { params })
}

// 删除文章评论
export function deleteArticleComment(articleId, commentId) {
  return request.delete(`/articles/${articleId}/comments/${commentId}`)
}
