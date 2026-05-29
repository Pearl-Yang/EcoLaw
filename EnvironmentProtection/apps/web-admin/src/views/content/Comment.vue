<template>
  <div class="comment-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>评论管理</h2>
        <span class="subtitle">管理用户对新闻、培训等内容的评论和回复</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon total"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.total }}</span>
          <span class="stat-label">全部评论</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon news"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M19 20H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v1m2 13a2 2 0 0 1-2-2V9a2 2 0 0 0-2-2h-1"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.newsComments }}</span>
          <span class="stat-label">新闻评论</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon training"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.trainingComments }}</span>
          <span class="stat-label">培训评论</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon likes"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.totalLikes }}</span>
          <span class="stat-label">总点赞</span>
        </div>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="搜索评论内容/用户..." prefix-icon="Search" clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="query.targetType" placeholder="目标类型" clearable style="width: 140px">
        <el-option label="全部" :value="''" />
        <el-option label="新闻" value="news" />
        <el-option label="培训" value="training" />
        <el-option label="案例" value="case_info" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="全部" :value="''" />
        <el-option label="已发布" value="published" />
        <el-option label="待审核" value="pending" />
      </el-select>
      <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px" clearable />
      <el-button type="primary" plain @click="loadData">筛选</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#f8faf8', color: '#1a4d2e', fontWeight: 600 }">
        <el-table-column label="评论内容" min-width="320">
          <template #default="{ row }">
            <div class="comment-content">
              <div class="comment-user">
                <div class="user-avatar">{{ (row.userNickname || '匿名').charAt(0) }}</div>
                <div class="user-info">
                  <span class="user-name">{{ row.userNickname || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatTime(row.createTime) }}</span>
                </div>
              </div>
              <div class="comment-text">{{ row.content }}</div>
              <div class="comment-target" v-if="row.targetType">
                <span class="target-tag" :class="`target-${row.targetType}`">{{ getTargetName(row.targetType) }}</span>
                <span class="target-id" v-if="row.targetId">#{{ row.targetId.slice(0, 8) }}...</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="回复" width="80" align="center">
          <template #default="{ row }">
            <span class="reply-count">{{ row.replyCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="点赞" width="80" align="center">
          <template #default="{ row }">
            <span class="like-count">{{ row.likeCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-badge" :class="`status-${row.status}`">{{ getStatusName(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <div class="action-group">
              <el-button type="primary" link size="small" @click="handleViewReplies(row)">查看回复</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 回复列表弹窗 -->
    <el-dialog v-model="repliesDialogVisible" title="评论回复" width="680px" class="replies-dialog" destroy-on-close>
      <div class="parent-comment">
        <div class="parent-user">
          <div class="user-avatar lg">{{ (parentComment.userNickname || '匿名').charAt(0) }}</div>
          <div class="user-info">
            <span class="user-name">{{ parentComment.userNickname || '匿名用户' }}</span>
            <span class="comment-time">{{ formatTime(parentComment.createTime) }}</span>
          </div>
        </div>
        <div class="parent-text">{{ parentComment.content }}</div>
      </div>

      <div class="replies-list" v-if="replies.length > 0">
        <div class="replies-header">回复 ({{ replies.length }})</div>
        <div v-for="reply in replies" :key="reply.id" class="reply-item">
          <div class="reply-user">
            <div class="user-avatar sm">{{ (reply.userNickname || '匿名').charAt(0) }}</div>
            <div class="user-info">
              <span class="user-name">{{ reply.userNickname || '匿名用户' }}</span>
              <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
            </div>
          </div>
          <div class="reply-text">{{ reply.content }}</div>
        </div>
      </div>
      <div v-else class="no-replies">暂无回复</div>

      <template #footer>
        <el-button @click="repliesDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCommentPage, getCommentReplies, deleteComment } from '@/api/content.js'

const loading = ref(false)
const repliesDialogVisible = ref(false)
const parentComment = ref({})
const replies = ref([])

const tableData = ref([])
const query = reactive({ keyword: '', targetType: '', status: '', dateRange: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const stats = computed(() => {
  const newsComments = tableData.value.filter(t => t.targetType === 'news').length
  const trainingComments = tableData.value.filter(t => t.targetType === 'training').length
  const totalLikes = tableData.value.reduce((sum, t) => sum + (t.likeCount || 0), 0)
  return { total: tableData.value.length, newsComments, trainingComments, totalLikes }
})

const getTargetName = (type) => ({ news: '新闻', training: '培训', case_info: '案例' }[type] || type)
const getStatusName = (status) => ({ published: '已发布', pending: '待审核', deleted: '已删除' }[status] || status)

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.targetType) params.targetType = query.targetType
    if (query.status) params.status = query.status

    const res = await getCommentPage(params)
    if (res) {
      tableData.value = Array.isArray(res) ? res : (res.list || [])
      pagination.total = res.total || tableData.value.length
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { keyword: '', targetType: '', status: '', dateRange: null })
  loadData()
}

async function handleViewReplies(row) {
  parentComment.value = row
  try {
    const res = await getCommentReplies(row.id)
    replies.value = res || []
    repliesDialogVisible.value = true
  } catch (error) {
    console.error('加载回复失败:', error)
    replies.value = []
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除该评论吗？此操作不可恢复！`, '确认删除', { type: 'error', confirmButtonText: '删除' })
    await deleteComment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.comment-page {
  padding: 0 8px 24px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;

  .header-left {
    h2 { margin: 0 0 4px; font-size: 22px; font-weight: 700; color: #1a4d2e; }
    .subtitle { font-size: 13px; color: #999; }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  svg { width: 24px; height: 24px; }
  &.total { background: rgba(82, 183, 136, 0.1); svg { color: #52b788; } }
  &.news { background: rgba(99, 102, 241, 0.1); svg { color: #6366f1; } }
  &.training { background: rgba(16, 185, 129, 0.1); svg { color: #10b981; } }
  &.likes { background: rgba(239, 68, 68, 0.1); svg { color: #ef4444; } }
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-num { font-size: 26px; font-weight: 700; color: #1a1a1a; line-height: 1; }
.stat-label { font-size: 12px; color: #999; }

.filter-bar {
  background: #fff;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.comment-list {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.comment-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52b788, #95d5b2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;

  &.lg {
    width: 44px;
    height: 44px;
    font-size: 18px;
  }

  &.sm {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name { font-size: 13px; font-weight: 600; color: #333; }
.comment-time, .reply-time { font-size: 11px; color: #999; }

.comment-text {
  font-size: 13px;
  color: #555;
  line-height: 1.6;
  padding-left: 46px;
}

.comment-target {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-left: 46px;
}

.target-tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 20px;
  &.target-news { background: rgba(82, 183, 136, 0.1); color: #52b788; }
  &.target-training { background: rgba(16, 185, 129, 0.1); color: #10b981; }
  &.target-case_info { background: rgba(99, 102, 241, 0.1); color: #6366f1; }
}

.target-id {
  font-size: 11px;
  color: #999;
}

.reply-count, .like-count {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.status-badge {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  &.status-published { background: rgba(16, 185, 129, 0.1); color: #10b981; }
  &.status-pending { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
}

.action-group { display: flex; gap: 4px; }
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }

.parent-comment {
  padding: 16px;
  background: #f8faf8;
  border-radius: 12px;
  margin-bottom: 20px;
}

.parent-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.parent-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  padding-left: 56px;
}

.replies-list {
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

.replies-header {
  font-size: 14px;
  font-weight: 600;
  color: #1a4d2e;
  margin-bottom: 16px;
}

.reply-item {
  padding: 12px 0;
  border-bottom: 1px dashed #f0f0f0;
  &:last-child { border-bottom: none; }
}

.reply-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.reply-text {
  font-size: 13px;
  color: #555;
  line-height: 1.6;
  padding-left: 38px;
}

.no-replies {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}

:deep(.replies-dialog) {
  .el-dialog { border-radius: 20px; overflow: hidden; }
  .el-dialog__header { padding: 20px 24px !important; margin: 0; border-bottom: 1px solid #f0f0f0; font-weight: 600; color: #1a4d2e; }
  .el-dialog__body { padding: 24px !important; }
  .el-dialog__footer { padding: 16px 24px !important; border-top: 1px solid #f0f0f0; }
}
</style>
