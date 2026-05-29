<template>
  <div class="notification-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>通知管理</h2>
        <span class="subtitle">创建、编辑并向用户推送环保通知</span>
      </div>
      <el-button type="primary" @click="handleCreate">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        发布通知
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon total"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.total }}</span>
          <span class="stat-label">全部通知</span>
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
        <div class="stat-icon draft"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M11 5H6a2 2 0 0 0-2 2v11a2 2 0 0 0 2 2h11a2 2 0 0 0 2-2v-5m-1.414-9.414a2 2 0 1 1 2.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.draft }}</span>
          <span class="stat-label">草稿</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon views"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/><path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg></div>
        <div class="stat-info">
          <span class="stat-num">{{ stats.totalViews }}</span>
          <span class="stat-label">总阅读</span>
        </div>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="搜索通知标题..." prefix-icon="Search" clearable style="width: 260px" @keyup.enter="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px">
        <el-option label="全部" :value="''" />
        <el-option label="已发布" value="published" />
        <el-option label="草稿" value="draft" />
      </el-select>
      <el-select v-model="query.type" placeholder="类型" clearable style="width: 130px">
        <el-option label="全部" :value="''" />
        <el-option label="系统通知" value="system" />
        <el-option label="环保政策" value="policy" />
        <el-option label="活动公告" value="activity" />
      </el-select>
      <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 260px" clearable />
      <el-button type="primary" plain @click="loadData">筛选</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <!-- 通知列表 -->
    <div class="notification-list">
      <el-table :data="tableData" v-loading="loading" stripe :header-cell-style="{ background: '#f8faf8', color: '#1a4d2e', fontWeight: 600 }">
        <el-table-column label="通知信息" min-width="260">
          <template #default="{ row }">
            <div class="notification-info">
              <div class="notif-cover" :style="{ background: row.coverGradient || '#52b788' }">
                <span class="notif-emoji">{{ row.emoji || '📢' }}</span>
              </div>
              <div class="notif-detail">
                <span class="notif-title">{{ row.title }}</span>
                <span class="notif-desc">{{ row.summary || '暂无摘要' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <span class="type-tag" :class="`type-${row.type}`">{{ getTypeName(row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-badge" :class="`status-${row.status}`">{{ getStatusName(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="目标范围" width="120">
          <template #default="{ row }">
            <span class="target-text">{{ getTargetName(row.targetType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数据" width="160">
          <template #default="{ row }">
            <div class="data-cells">
              <span class="data-item"><span class="data-icon">👁</span> {{ row.views || 0 }}</span>
              <span class="data-item"><span class="data-icon">👍</span> {{ row.likes || 0 }}</span>
              <span class="data-item"><span class="data-icon">💬</span> {{ row.comments || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="120">
          <template #default="{ row }">
            <span class="time-text">{{ row.publishTime || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-group">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 'draft'" type="success" link size="small" @click="handlePublish(row)">发布</el-button>
              <el-button v-else type="warning" link size="small" @click="handleRevoke(row)">撤回</el-button>
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

    <!-- 创建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" class="notif-dialog" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
        <div class="form-row">
          <el-form-item label="通知标题" prop="title" style="flex: 1">
            <el-input v-model="form.title" placeholder="请输入通知标题（最多50字）" maxlength="50" show-word-limit />
          </el-form-item>
          <el-form-item label="类型" prop="type" style="width: 150px">
            <el-select v-model="form.type" placeholder="选择类型">
              <el-option label="系统通知" value="system" />
              <el-option label="环保政策" value="policy" />
              <el-option label="活动公告" value="activity" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="封面图标" prop="emoji">
          <div class="emoji-picker">
            <div v-for="e in emojiOptions" :key="e" class="emoji-cell" :class="{ selected: form.emoji === e }" @click="form.emoji = e">
              <span>{{ e }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="通知摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简要描述通知内容（最多200字）" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="通知正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入通知的详细内容，支持富文本..." />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="目标范围" prop="target" style="flex: 1">
            <el-select v-model="form.target" placeholder="选择推送范围" style="width: 100%">
              <el-option label="全部用户" value="全部用户" />
              <el-option label="政府用户" value="政府用户" />
              <el-option label="企业用户" value="企业用户" />
              <el-option label="律师用户" value="律师用户" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布状态" style="width: 160px">
            <el-switch v-model="form.immediate" active-text="立即发布" inactive-text="存草稿" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ form.immediate ? '立即发布' : '保存草稿' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNotificationPage, getNotification, createNotification, updateNotification, deleteNotification } from '@/api/content.js'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('发布通知')
const formRef = ref()

const tableData = ref([])
const query = reactive({ keyword: '', status: '', type: '', dateRange: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const form = reactive({
  id: '', title: '', type: 'system', emoji: '📢', summary: '', content: '',
  target: 'all', immediate: false, targetType: 'all'
})

const formRules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入通知正文', trigger: 'blur' }]
}

const emojiOptions = ['📢', '🏛️', '🌿', '📋', '⚠️', '✅', '📣', '🔔', '📜', '🏢', '🌍', '💡']

const stats = computed(() => {
  const published = tableData.value.filter(t => t.status === 'published').length
  const draft = tableData.value.filter(t => t.status === 'draft').length
  const totalViews = tableData.value.reduce((sum, t) => sum + (t.views || 0), 0)
  return { total: tableData.value.length, published, draft, totalViews }
})

const getTypeName = (type) => ({ system: '系统通知', policy: '环保政策', activity: '活动公告', task: '任务通知', training: '培训通知', report: '举报通知' }[type] || type)
const getStatusName = (status) => ({ published: '已发布', draft: '草稿', cancelled: '已取消' }[status] || status)
const getTargetName = (targetType) => ({ all: '全部用户', user: '指定用户', role: '指定角色', organization: '指定组织' }[targetType] || '全部用户')

async function loadData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.status) params.status = query.status
    if (query.type) params.type = query.type

    const res = await getNotificationPage(params)
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
  Object.assign(query, { keyword: '', status: '', type: '', dateRange: null })
  pagination.page = 1
  loadData()
}

function handleCreate() {
  dialogTitle.value = '发布通知'
  Object.assign(form, { id: '', title: '', type: 'system', emoji: '📢', summary: '', content: '', target: 'all', immediate: false, targetType: 'all' })
  dialogVisible.value = true
}

async function handleEdit(row) {
  dialogTitle.value = '编辑通知'
  try {
    const res = await getNotification(row.id)
    Object.assign(form, { ...res, immediate: res.status === 'published' })
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
    data.status = form.immediate ? 'published' : 'draft'
    if (form.id) {
      await updateNotification(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createNotification(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

async function handlePublish(row) {
  try {
    await ElMessageBox.confirm(`确定要发布「${row.title}」吗？`, '确认发布', { type: 'info' })
    await updateNotification(row.id, { status: 'published' })
    ElMessage.success('通知已发布')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('发布失败')
  }
}

async function handleRevoke(row) {
  try {
    await ElMessageBox.confirm(`确定要撤回「${row.title}」吗？`, '确认撤回', { type: 'warning' })
    await updateNotification(row.id, { status: 'draft' })
    ElMessage.success('通知已撤回')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('撤回失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除「${row.title}」吗？此操作不可恢复！`, '确认删除', { type: 'error', confirmButtonText: '删除' })
    await deleteNotification(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.notification-page {
  padding: 0 8px 24px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;

  .header-left {
    h2 {
      margin: 0 0 4px;
      font-size: 22px;
      font-weight: 700;
      color: #1a4d2e;
    }
    .subtitle {
      font-size: 13px;
      color: #999;
    }
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
  &.draft { background: rgba(245, 158, 11, 0.1); svg { color: #f59e0b; } }
  &.views { background: rgba(99, 102, 241, 0.1); svg { color: #6366f1; } }
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-num {
  font-size: 26px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

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

.notification-list {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.notification-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.notif-cover {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notif-emoji {
  font-size: 26px;
}

.notif-detail {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.notif-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notif-desc {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.type-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  &.type-system { background: rgba(99, 102, 241, 0.1); color: #6366f1; }
  &.type-policy { background: rgba(16, 185, 129, 0.1); color: #10b981; }
  &.type-activity { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
}

.status-badge {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  &.status-published { background: rgba(16, 185, 129, 0.1); color: #10b981; }
  &.status-draft { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
}

.target-text {
  font-size: 12px;
  color: #666;
}

.data-cells {
  display: flex;
  gap: 12px;
}

.data-item {
  font-size: 12px;
  color: #888;
  display: flex;
  align-items: center;
  gap: 3px;
}

.data-icon {
  font-size: 11px;
}

.time-text {
  font-size: 12px;
  color: #999;
}

.action-group {
  display: flex;
  gap: 4px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-row {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.emoji-picker {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 8px;
  padding: 12px;
  background: #f8faf8;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.emoji-cell {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  font-size: 22px;
  transition: all 0.2s;
  &:hover { background: #f0f0f0; transform: scale(1.1); }
  &.selected { background: #e8f5eb; border: 2px solid #52b788; }
}

:deep(.notif-dialog) {
  .el-dialog {
    border-radius: 20px;
    overflow: hidden;
  }
  .el-dialog__header {
    padding: 20px 24px !important;
    margin: 0;
    border-bottom: 1px solid #f0f0f0;
    font-weight: 600;
    color: #1a4d2e;
  }
  .el-dialog__body {
    padding: 24px !important;
  }
  .el-dialog__footer {
    padding: 16px 24px !important;
    border-top: 1px solid #f0f0f0;
  }
}
</style>
