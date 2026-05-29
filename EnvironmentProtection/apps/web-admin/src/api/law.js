/**
 * 法规查询 API
 */
import request from '@/utils/request.js'

// 搜索法规
export function searchLaws(params) {
  return request.get('/laws', { params })
}

// 获取法规详情
export function getLaw(id) {
  return request.get(`/laws/${id}`)
}

// 获取法规章节
export function getLawChapters(lawId) {
  return request.get(`/laws/${lawId}/chapters`)
}

// 获取章节内容
export function getChapterContent(chapterId) {
  return request.get(`/laws/chapters/${chapterId}`)
}

// 获取法规分类
export function getLawCategories() {
  return request.get('/laws/categories')
}

// AI法规解读
export function explainLaw(lawId, question) {
  return request.post(`/laws/${lawId}/explain`, { question })
}
