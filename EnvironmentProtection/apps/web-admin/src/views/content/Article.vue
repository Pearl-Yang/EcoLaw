<template>
  <div class="article-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>新闻/热点管理</h2>
        <span class="subtitle">管理平台新闻、热点资讯、政策解读和活动通知</span>
      </div>
      <el-button type="primary" @click="handleCreate">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        创建新闻
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon total"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M19 20H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v1m2 13a2 2 0 0 1-2-2V9a2 2 0 0 0-2-2h-1"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.total }}</span>
          <span class="stat-label">全部新闻</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon published"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 12l2 2 4-4m6 2a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.published }}</span>
          <span class="stat-label">已发布</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon hot"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17.657 18.657A8 8 0 0 1 12 5.5a8 8 0 0 1 5.657 13.157m-5.657-6.657a5 5 0 0 0-3.536-1.536"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.hot }}</span>
          <span class="stat-label">热门</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon comments"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.totalComments }}</span>
          <span class="stat-label">总评论</span>
        </div>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="搜索标题/来源..." prefix-icon="Search" clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="query.type" placeholder="类型" clearable style="width: 130px">
        <el-option label="全部" :value="''" />
        <el-option label="新闻" value="news" />
        <el-option label="热点" value="hotspot" />
        <el-option label="政策" value="policy" />
        <el-option label="活动" value="activity" />
      </el-select>
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="全部" :value="''" />
        <el-option label="已发布" value="published" />
        <el-option label="草稿" value="draft" />
      </el-select>
      <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px" clearable />
      <el-button type="primary" plain @click="loadData">筛选</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 新闻列表 -->
    <div class="article-list">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#f8faf8', color: '#1a4d2e', fontWeight: 600 }">
        <el-table-column label="新闻信息" min-width="300">
          <template #default="{ row }">
            <div class="article-info">
              <div class="article-cover" :style="{ background: row.coverUrl ? `url(${row.coverUrl}) center/cover` : '#52b788' }">
                <span v-if="!row.coverUrl" class="article-emoji">📰</span>
                <span v-if="row.isTop === 1" class="top-badge">置顶</span>
              </div>
              <div class="article-detail">
                <span class="article-title">{{ row.title }}</span>
                <span class="article-source">来源：{{ row.source || '官方' }}</span>
                <div class="article-tags">
                  <span class="type-tag" :class="`type-${row.type}`">{{ getTypeName(row.type) }}</span>
                  <span v-if="row.isHot === 1" class="hot-tag">🔥 热门</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="摘要" min-width="160">
          <template #default="{ row }">
            <span class="article-summary">{{ row.summary || '暂无摘要' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-badge" :class="`status-${row.status}`">{{ getStatusName(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="互动数据" width="160">
          <template #default="{ row }">
            <div class="data-cells">
              <span class="data-item"><span class="data-icon">👁</span> {{ row.viewCount || 0 }}</span>
              <span class="data-item"><span class="data-icon">👍</span> {{ row.likeCount || 0 }}</span>
              <span class="data-item"><span class="data-icon">💬</span> {{ row.commentCount || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="120">
          <template #default="{ row }">
            <span class="time-text">{{ row.publishTime || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-group">
              <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
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

    <!-- 创建/编辑新闻弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" class="article-dialog" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入新闻标题（最多100字）" maxlength="100" show-word-limit />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="类型" prop="type" style="flex: 1">
            <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
              <el-option label="新闻" value="news" />
              <el-option label="热点" value="hotspot" />
              <el-option label="政策解读" value="policy" />
              <el-option label="活动" value="activity" />
            </el-select>
          </el-form-item>
          <el-form-item label="来源" style="flex: 1">
            <el-input v-model="form.source" placeholder="请输入来源" />
          </el-form-item>
        </div>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简要描述新闻内容（最多300字）" maxlength="300" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图片URL">
          <el-input v-model="form.coverUrl" placeholder="请输入封面图片URL" />
        </el-form-item>
        <el-form-item label="正文内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入新闻正文内容..." />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="标签（逗号分隔）">
            <el-input v-model="form.tags" placeholder="如：禁塑,环保,政策" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 150px">
              <el-option label="草稿" value="draft" />
              <el-option label="已发布" value="published" />
            </el-select>
          </el-form-item>
          <el-form-item label="置顶">
            <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
          </el-form-item>
          <el-form-item label="热门">
            <el-switch v-model="form.isHot" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog v-model="viewDialogVisible" title="新闻详情" width="720px" class="article-dialog" destroy-on-close>
      <div class="article-viewer" v-if="viewArticle">
        <div class="viewer-header">
          <h3>{{ viewArticle.title }}</h3>
          <div class="viewer-meta">
            <span class="type-tag" :class="`type-${viewArticle.type}`">{{ getTypeName(viewArticle.type) }}</span>
            <span v-if="viewArticle.isHot === 1" class="hot-tag">🔥 热门</span>
            <span class="viewer-source">来源：{{ viewArticle.source || '官方' }}</span>
            <span class="viewer-time">{{ viewArticle.publishTime }}</span>
          </div>
          <div class="viewer-stats">
            <span>👁 {{ viewArticle.viewCount || 0 }}</span>
            <span>👍 {{ viewArticle.likeCount || 0 }}</span>
            <span>💬 {{ viewArticle.commentCount || 0 }}</span>
          </div>
        </div>
        <image v-if="viewArticle.coverUrl" :src="viewArticle.coverUrl" class="viewer-cover" mode="widthFix" />
        <div class="viewer-summary" v-if="viewArticle.summary">{{ viewArticle.summary }}</div>
        <div class="viewer-content">{{ viewArticle.content }}</div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNewsPage, getNews, createNews, updateNews, deleteNews } from '@/api/content.js'

const loading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('创建新闻')
const formRef = ref()
const viewArticle = ref(null)

const tableData = ref([])
const query = reactive({ keyword: '', type: '', status: '', dateRange: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: '', title: '', type: 'news', source: '', summary: '', coverUrl: '',
  content: '', tags: '', status: 'draft', isTop: 0, isHot: 0, publishTime: null
})

const formRules = {
  title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入正文内容', trigger: 'blur' }]
}

const stats = computed(() => {
  const published = tableData.value.filter(t => t.status === 'published').length
  const hot = tableData.value.filter(t => t.isHot === 1).length
  const totalComments = tableData.value.reduce((sum, t) => sum + (t.commentCount || 0), 0)
  return { total: tableData.value.length, published, hot, totalComments }
})

const getTypeName = (type) => ({ news: '新闻', hotspot: '热点', policy: '政策', activity: '活动' }[type] || type)
const getStatusName = (status) => ({ published: '已发布', draft: '草稿', archived: '已归档' }[status] || status)

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.type) params.type = query.type
    if (query.status) params.status = query.status

    const res = await getNewsPage(params)
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
  Object.assign(query, { keyword: '', type: '', status: '', dateRange: null })
  loadData()
}

function handleCreate() {
  dialogTitle.value = '创建新闻'
  Object.assign(form, {
    id: '', title: '', type: 'news', source: '', summary: '', coverUrl: '',
    content: '', tags: '', status: 'draft', isTop: 0, isHot: 0, publishTime: null
  })
  dialogVisible.value = true
}

async function handleEdit(row) {
  dialogTitle.value = '编辑新闻'
  try {
    const res = await getNews(row.id)
    Object.assign(form, { ...res })
    form.tags = res.tags ? (Array.isArray(res.tags) ? res.tags.join(',') : res.tags) : ''
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    const data = { ...form }
    if (data.tags && typeof data.tags === 'string') {
      data.tags = JSON.stringify(data.tags.split(',').filter(t => t.trim()))
    }
    if (form.id) {
      await updateNews(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createNews(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

async function handleView(row) {
  try {
    const res = await getNews(row.id)
    viewArticle.value = res
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.title}」吗？此操作不可恢复！`, '确认删除', { type: 'error', confirmButtonText: '删除' })
    await deleteNews(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.article-page {
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
  &.published { background: rgba(16, 185, 129, 0.1); svg { color: #10b981; } }
  &.hot { background: rgba(239, 68, 68, 0.1); svg { color: #ef4444; } }
  &.comments { background: rgba(99, 102, 241, 0.1); svg { color: #6366f1; } }
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

.article-list {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.article-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.article-cover {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  background: linear-gradient(135deg, #52b788, #95d5b2) !important;
}

.article-emoji { font-size: 26px; }

.top-badge {
  position: absolute;
  top: -4px;
  left: -4px;
  font-size: 10px;
  padding: 2px 6px;
  background: #ff9800;
  color: #fff;
  border-radius: 4px;
}

.article-detail {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.article-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-source { font-size: 12px; color: #888; }

.article-tags {
  display: flex;
  gap: 6px;
}

.type-tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 20px;
  font-weight: 500;
  &.type-news { background: rgba(82, 183, 136, 0.1); color: #52b788; }
  &.type-hotspot { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
  &.type-policy { background: rgba(99, 102, 241, 0.1); color: #6366f1; }
  &.type-activity { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
}

.hot-tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 20px;
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.article-summary {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.status-badge {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  &.status-published { background: rgba(16, 185, 129, 0.1); color: #10b981; }
  &.status-draft { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
}

.data-cells { display: flex; gap: 10px; }
.data-item { font-size: 12px; color: #888; display: flex; align-items: center; gap: 2px; }
.time-text { font-size: 12px; color: #999; }
.action-group { display: flex; gap: 4px; }
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }
.form-row { display: flex; gap: 16px; align-items: flex-start; }

.article-viewer {
  .viewer-header {
    margin-bottom: 16px;
    h3 { margin: 0 0 12px; font-size: 20px; font-weight: 600; color: #1a4d2e; }
  }

  .viewer-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
    flex-wrap: wrap;
  }

  .viewer-source, .viewer-time { font-size: 12px; color: #888; }

  .viewer-stats {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: #999;
  }
}

.viewer-cover {
  width: 100%;
  margin-bottom: 16px;
  border-radius: 12px;
}

.viewer-summary {
  padding: 14px;
  background: #f8faf8;
  border-radius: 10px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
  border-left: 3px solid #52b788;
}

.viewer-content {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
  min-height: 120px;
}

:deep(.article-dialog) {
  .el-dialog { border-radius: 20px; overflow: hidden; }
  .el-dialog__header { padding: 20px 24px !important; margin: 0; border-bottom: 1px solid #f0f0f0; font-weight: 600; color: #1a4d2e; }
  .el-dialog__body { padding: 24px !important; }
  .el-dialog__footer { padding: 16px 24px !important; border-top: 1px solid #f0f0f0; }
}
</style>
