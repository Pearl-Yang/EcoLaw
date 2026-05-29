/**
 * AI相关 API
 */
import request from '@/utils/request.js'

// ============ AI 对话管理 ============

// 获取用户对话列表
export function getAIConversationList(params) {
  return request.get('/ai/conversation/list', { params })
}

// 获取对话详情（含消息）
export function getAIConversationDetail(id) {
  return request.get(`/ai/conversation/${id}`)
}

// 创建新对话
export function createAIConversation(params) {
  return request.post('/ai/conversation', null, { params })
}

// 删除对话
export function deleteAIConversation(id) {
  return request.delete(`/ai/conversation/${id}`)
}

// 归档对话
export function archiveAIConversation(id) {
  return request.put(`/ai/conversation/${id}/archive`)
}

// 获取对话消息列表
export function getAIMessages(conversationId) {
  return request.get(`/ai/conversation/${conversationId}/messages`)
}

// 消息反馈
export function feedbackAIMessage(messageId, feedback, feedbackContent) {
  return request.post(`/ai/conversation/messages/${messageId}/feedback`, null, {
    params: { feedback, feedbackContent }
  })
}

// 获取用户AI使用统计
export function getAIStatistics(params) {
  return request.get('/ai/conversation/statistics', { params })
}

// ============ AI 功能 ============

// 法律智能问答（带对话历史、综合检索）
export function lawChat(data) {
  return request.post('/ai/chat', data)
}

// 解析 AI 响应，提取相关法规、新闻、评论
export function parseLawChatResponse(res) {
  if (!res) return null
  
  return {
    answer: res.answer || '',
    summary: res.summary || '',
    conversationId: res.conversationId || '',
    messageId: res.messageId || '',
    latencyMs: res.latencyMs || 0,
    isFallback: res.isFallback || false,
    relatedLaws: res.relatedLaws || [],
    relatedNews: res.relatedNews || [],
    relatedComments: res.relatedComments || [],
    suggestions: res.suggestions || []
  }
}

// 生成普法素材
export function generateMaterial(data) {
  return request.post('/ai/material/generate', data)
}

// 生成法条解读
export function explainLawText(data) {
  return request.post('/ai/law/explain', data)
}

// 生成案例分析
export function analyzeCase(data) {
  return request.post('/ai/case/analyze', data)
}

// 生成考试题目
export function generateExam(data) {
  return request.post('/ai/exam/generate', data)
}

// 生成工作报告
export function generateReport(data) {
  return request.post('/ai/report/generate', data)
}

// ============ 素材管理 ============

// 获取素材列表
export function getMaterialList(params) {
  return request.get('/ai/materials', { params })
}

// 获取素材详情
export function getMaterial(id) {
  return request.get(`/ai/materials/${id}`)
}

// 删除素材
export function deleteMaterial(id) {
  return request.delete(`/ai/materials/${id}`)
}
