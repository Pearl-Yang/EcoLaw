<template>
  <view class="page">
    <!-- 顶部：白底卡片 -->
    <view class="header-card">
      <view class="header-top-row">
        <text class="banner-title">任务中心</text>
        <view class="header-points-badge">
          <LineIcon name="award" :size="24" color="#eab308" />
          <text class="points-num-sm">{{ taskStats.totalPoints }}</text>
          <text class="points-label-sm">累计积分</text>
        </view>
      </view>
      <text class="banner-desc">参与任务、完成任务获取积分，成为法律明白人</text>
    </view>

    <!-- 任务统计 -->
    <view class="stats-row">
      <view class="stat-item">
        <text class="stat-num stat-primary">{{ taskStats.pending }}</text>
        <text class="stat-label">待执行</text>
      </view>
      <view class="stat-divider" />
      <view class="stat-item">
        <text class="stat-num">{{ taskStats.inProgress }}</text>
        <text class="stat-label">进行中</text>
      </view>
      <view class="stat-divider" />
      <view class="stat-item">
        <text class="stat-num">{{ taskStats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
    </view>

    <!-- 任务标签切换 -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: activeTab === 'all' }"
        @click="switchTab('all')"
      >
        <text>全部</text>
        <view v-if="taskStats.total > 0" class="tab-badge">{{ taskStats.total }}</view>
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'pending' }"
        @click="switchTab('pending')"
      >
        <text>待执行</text>
        <view v-if="taskStats.pending > 0" class="tab-badge">{{ taskStats.pending }}</view>
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'in_progress' }"
        @click="switchTab('in_progress')"
      >
        <text>进行中</text>
        <view v-if="taskStats.inProgress > 0" class="tab-badge">{{ taskStats.inProgress }}</view>
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'completed' }"
        @click="switchTab('completed')"
      >
        <text>已完成</text>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view v-if="loading" class="loading-state">
        <text>加载中...</text>
      </view>
      <view v-else-if="filteredTasks.length === 0" class="empty-state">
        <LineIcon name="clipboard-list" :size="64" color="#d1d5db" />
        <text class="empty-text">暂无{{ getTabName() }}任务</text>
      </view>
      <view
        v-else
        v-for="task in filteredTasks"
        :key="task.id"
        class="task-card"
        @click="goTaskDetail(task)"
      >
        <!-- 左侧色彩条 -->
        <view class="task-accent-bar" :class="task.tagClass || 'default'" />

        <!-- 状态标签 -->
        <view class="task-status-bar" :class="task.statusClass">
          <text class="status-text">{{ task.statusText }}</text>
        </view>

        <!-- 任务类型标签 -->
        <view class="task-type-tag" :class="task.tagClass || task.type">
          <LineIcon :name="getTypeIcon(task.type)" :size="18" :color="getTypeColor(task.tagClass || task.type)" />
          <text class="type-text">{{ task.typeText }}</text>
        </view>

        <!-- 任务标题和描述 -->
        <text class="task-title">{{ task.title || task.name }}</text>
        <text class="task-desc">{{ task.description || task.desc || '点击查看详情' }}</text>

        <!-- 任务来源 -->
        <view class="task-source">
          <LineIcon name="globe" :size="20" color="#9ca3af" />
          <text class="source-text">来源: {{ task.source || '系统分配' }}</text>
        </view>

        <!-- 任务要求 -->
        <view v-if="task.requirements && task.requirements.length > 0" class="task-requirements">
          <text v-for="(req, i) in task.requirements" :key="i" class="req-item">{{ i + 1 }}. {{ req }}</text>
        </view>

        <!-- 任务底部信息 -->
        <view class="task-footer">
          <view class="task-deadline">
            <LineIcon name="clock" :size="20" color="#9ca3af" />
            <text>截止 {{ formatDate(task.deadline || task.endTime) }}</text>
          </view>
          <view class="task-points">
            <LineIcon name="award" :size="20" color="#eab308" />
            <text class="points-num">+{{ task.points || task.score || 0 }}</text>
          </view>
        </view>

        <!-- 进度条 -->
        <view v-if="task.status !== 'completed' && task.status !== 2" class="task-progress">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: (task.progress || task.percent || 0) + '%' }" />
          </view>
          <text class="progress-text">{{ task.progress || task.percent || 0 }}%</text>
        </view>

        <!-- 操作按钮 -->
        <view v-if="task.status === 'pending' || task.status === 0" class="task-action">
          <view class="action-btn primary" @click.stop="claimTask(task)">
            <LineIcon name="plus" :size="22" color="#ffffff" />
            <text>立即领取</text>
          </view>
        </view>
        <view v-else-if="task.status === 'in_progress' || task.status === 1" class="task-action">
          <view class="action-btn secondary" @click.stop="uploadProgress(task)">
            <LineIcon name="upload" :size="22" color="#22c55e" />
            <text>上传过程</text>
          </view>
          <view class="action-btn primary" @click.stop="completeTask(task)">
            <LineIcon name="check-circle" :size="22" color="#ffffff" />
            <text>完成打卡</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import LineIcon from '../../components/LineIcon.vue'

function getTypeIcon(type) {
  const map = {
    survey: 'clipboard-list',
    publicity: 'globe',
    education: 'book-open',
    compliance: 'shield-check',
    recycle: 'leaf',
    government: 'shield',
    enterprise: 'graduation-cap',
    law_firm: 'file-text'
  }
  return map[type] || 'clipboard-list'
}

function getTypeColor(tagClass) {
  const map = {
    survey: '#22c55e',
    publicity: '#3b82f6',
    education: '#a855f7',
    compliance: '#f59e0b',
    recycle: '#22c55e'
  }
  return map[tagClass] || '#6b7280'
}

const loading = ref(false)
const activeTab = ref('all')
const allTasks = ref(getMockTasks())

const taskStats = reactive({
  total: 0,
  pending: 0,
  inProgress: 0,
  completed: 0,
  totalPoints: 0
})

const filteredTasks = ref([])

/** 从接口多种分页结构里取出数组 */
function extractTaskList(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  if (Array.isArray(raw.list)) return raw.list
  if (Array.isArray(raw.records)) return raw.records
  if (Array.isArray(raw.rows)) return raw.rows
  return []
}

const TAG_CLASS = {
  survey: 'survey',
  publicity: 'publicity',
  education: 'education',
  compliance: 'compliance',
  recycle: 'recycle',
  government: 'compliance',
  enterprise: 'education',
  law_firm: 'survey'
}

function updateFilteredTasks() {
  const tasks = allTasks.value
  switch (activeTab.value) {
    case 'pending':
      filteredTasks.value = tasks.filter((t) => t.status === 0 || t.status === 'pending')
      break
    case 'in_progress':
      filteredTasks.value = tasks.filter((t) => t.status === 1 || t.status === 'in_progress')
      break
    case 'completed':
      filteredTasks.value = tasks.filter((t) => t.status === 2 || t.status === 'completed')
      break
    default:
      filteredTasks.value = [...tasks]
  }
}

onMounted(() => {
  applyTaskUiFields()
  updateStats()
  updateFilteredTasks()
  loadData()
})

onPullDownRefresh(() => {
  loadData().finally(() => {
    uni.stopPullDownRefresh()
  })
})

async function loadData() {
  loading.value = true
  try {
    const res = await api.task.list({ page: 1, pageSize: 50 }, { showLoading: false })
    const list = extractTaskList(res)
    allTasks.value = list.length ? list.map((t) => ({ ...t })) : []
    if (allTasks.value.length === 0) {
      allTasks.value = getMockTasks()
    }
    applyTaskUiFields()
    updateStats()
    updateFilteredTasks()
  } catch (error) {
    console.error('加载任务失败:', error)
    if (allTasks.value.length === 0) {
      allTasks.value = getMockTasks()
    }
    applyTaskUiFields()
    updateStats()
    updateFilteredTasks()
  } finally {
    loading.value = false
  }
}

/** 输入：接口或 mock 任务项；输出：就地补充展示用字段（tagClass、进度、描述等） */
function applyTaskUiFields() {
  const typeLabel = { government: '政务', enterprise: '企业', law_firm: '律所' }
  allTasks.value.forEach((task) => {
    task.tagClass = TAG_CLASS[task.type] || task.type
    if (!task.typeText) {
      task.typeText = typeLabel[task.type] || '任务'
    }
    if (task.description == null && task.content != null) {
      task.description = task.content
    }
    const tc = Number(task.targetCount) || 0
    const cc = Number(task.completedCount) || 0
    if (tc > 0) {
      task.progress = Math.min(100, Math.round((cc / tc) * 100))
    } else if (task.progress == null && task.percent != null) {
      task.progress = task.percent
    }
    task.statusClass = getStatusClass(task.status)
    task.statusText = getStatusText(task.status)
  })
}

/** 注意：避免 switch+default 被压缩器打乱顺序导致 status===1 落入 default */
function getStatusClass(status) {
  if (status === 1 || status === 'in_progress') return 'in-progress'
  if (status === 2 || status === 'completed') return 'completed'
  return 'pending'
}

function getStatusText(status) {
  if (status === 1 || status === 'in_progress') return '执行中'
  if (status === 2 || status === 'completed') return '已完成'
  if (status === 'overdue') return '已逾期'
  if (status === 'rejected') return '已驳回'
  return '待执行'
}

function updateStats() {
  const tasks = allTasks.value
  taskStats.total = tasks.length
  taskStats.pending = tasks.filter((t) => t.status === 0 || t.status === 'pending').length
  taskStats.inProgress = tasks.filter((t) => t.status === 1 || t.status === 'in_progress').length
  taskStats.completed = tasks.filter((t) => t.status === 2 || t.status === 'completed').length
  taskStats.totalPoints = tasks
    .filter((t) => t.status === 2 || t.status === 'completed')
    .reduce((sum, t) => sum + (t.points || 0), 0)
}

function getMockTasks() {
  return [
    {
      id: '1',
      title: '校园白色污染调研',
      description: '问卷发放与现场走访记录上传',
      type: 'survey',
      typeText: '调研',
      source: 'XX社区居委会',
      deadline: '2026-04-12',
      points: 50,
      progress: 60,
      status: 1,
      requirements: ['发放问卷不少于50份', '现场走访照片不少于10张', '提交调研报告']
    },
    {
      id: '2',
      title: '市集减塑宣传',
      description: '发放普法折页并拍照留痕',
      type: 'publicity',
      typeText: '宣传',
      source: 'XX市场监管局',
      deadline: '2026-04-18',
      points: 30,
      progress: 25,
      status: 1,
      requirements: ['发放宣传折页100份以上', '拍照记录宣传过程']
    },
    {
      id: '3',
      title: '社区环保讲座',
      description: '组织社区居民参与环保知识讲座',
      type: 'education',
      typeText: '教育',
      source: 'XX司法所',
      deadline: '2026-04-20',
      points: 80,
      progress: 0,
      status: 0,
      requirements: ['组织不少于30人参与', '活动时长不少于1小时', '提交活动照片和签到表']
    },
    {
      id: '4',
      title: '企业合规检查',
      description: '协助企业完成塑料污染防治合规自查',
      type: 'compliance',
      typeText: '合规',
      source: 'XX生态环境局',
      deadline: '2026-04-05',
      points: 100,
      progress: 100,
      status: 2,
      requirements: ['完成企业自查报告', '上传合规证明材料']
    },
    {
      id: '5',
      title: '农膜回收示范',
      description: '开展农膜回收示范活动',
      type: 'recycle',
      typeText: '回收',
      source: 'XX农业农村局',
      deadline: '2026-03-28',
      points: 60,
      progress: 100,
      status: 2,
      requirements: ['回收农膜不少于200公斤', '建立回收台账']
    }
  ]
}

function switchTab(tab) {
  activeTab.value = tab
  updateFilteredTasks()
}

function getTabName() {
  switch (activeTab.value) {
    case 'pending':
      return '待执行'
    case 'in_progress':
      return '进行中'
    case 'completed':
      return '已完成'
    default:
      return ''
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '待定'
  try {
    const date = new Date(dateStr)
    return `${date.getMonth() + 1}-${String(date.getDate()).padStart(2, '0')}`
  } catch {
    return dateStr
  }
}

function goTaskDetail(task) {
  if (!task.id) return
  uni.navigateTo({ url: `/pages/task/detail?id=${encodeURIComponent(task.id)}` })
}

function claimTask(task) {
  uni.showModal({
    title: '领取任务',
    content: `确定领取任务"${task.title}"吗？`,
    success: (res) => {
      if (res.confirm) {
        task.status = 1
        task.statusClass = 'in-progress'
        task.statusText = '执行中'
        updateStats()
        updateFilteredTasks()
        uni.showToast({ title: '领取成功', icon: 'success' })
      }
    }
  })
}

function uploadProgress(task) {
  uni.showToast({ title: '上传过程功能待开发', icon: 'none' })
}

function completeTask(task) {
  uni.showModal({
    title: '完成打卡',
    content: `确定完成任务"${task.title}"吗？`,
    success: (res) => {
      if (res.confirm) {
        task.status = 2
        task.progress = 100
        task.statusClass = 'completed'
        task.statusText = '已完成'
        updateStats()
        updateFilteredTasks()
        uni.showToast({ title: '任务完成！+' + task.points + '积分', icon: 'success' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
$bg: #f5f6f8;
$text-primary: #1f2937;
$text-secondary: #4b5563;
$primary: #22c55e;
$primary-dark: #15803d;
$mint: #ecfdf5;

.page {
  min-height: 100vh;
  background: $bg;
  padding: 0 24rpx 48rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
}

.header-card {
  background: #ffffff;
  border-radius: 0 0 24rpx 24rpx;
  padding: 28rpx 28rpx 28rpx;
  margin: 0 -24rpx 20rpx;
  border-bottom: 1rpx solid #e5e7eb;
}

.header-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.banner-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
}

.banner-desc {
  font-size: 24rpx;
  color: $text-secondary;
}

.header-points-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #fffbeb;
  border: 1rpx solid rgba(234, 179, 8, 0.35);
  border-radius: 999rpx;
  padding: 8rpx 16rpx;
}

.points-num-sm {
  font-size: 28rpx;
  font-weight: 700;
  color: #d97706;
}

.points-label-sm {
  font-size: 20rpx;
  color: #92400e;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.03);
}

.stat-divider {
  width: 1rpx;
  height: 40rpx;
  background: #e5e7eb;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-num {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
}

.stat-num.stat-primary {
  color: $primary;
}

.stat-label {
  font-size: 22rpx;
  color: $text-secondary;
}

.tab-bar {
  display: flex;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 18rpx 0;
  background: #fff;
  border-radius: 12rpx;
  font-size: 26rpx;
  color: $text-secondary;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.02);
}

.tab-item.active {
  background: linear-gradient(135deg, #ffffff 0%, $mint 100%);
  border-color: rgba(34, 197, 94, 0.45);
  color: $primary-dark;
  font-weight: 700;
}

.tab-badge {
  border-radius: 999rpx;
  padding: 2rpx 10rpx;
  font-size: 20rpx;
  background: $primary;
  color: #fff;
}

.tab-item.active .tab-badge {
  background: rgba(255, 255, 255, 0.9);
  color: $primary;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.loading-state,
.empty-state {
  padding: 60rpx 0;
  text-align: center;
  color: #999;
  font-size: 28rpx;
}

.empty-icon-dot {
  width: 72rpx;
  height: 72rpx;
  margin: 0 auto 16rpx;
  border-radius: 16rpx;
  background: #f0fdf4;
  border: 1rpx solid rgba(34, 197, 94, 0.2);
}

.task-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  padding-left: 44rpx;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
  position: relative;
  border: 1rpx solid #e5e7eb;
}

.task-accent-bar {
  position: absolute;
  top: 20rpx;
  bottom: 20rpx;
  left: 0;
  width: 6rpx;
  border-radius: 0 4rpx 4rpx 0;
  background: $primary;
}

.task-accent-bar.survey { background: $primary; }
.task-accent-bar.publicity { background: #3b82f6; }
.task-accent-bar.education { background: #a855f7; }
.task-accent-bar.compliance { background: #f59e0b; }
.task-accent-bar.recycle { background: #22c55e; }
.task-accent-bar.default { background: #6b7280; }

.task-status-bar {
  display: inline-block;
  padding: 6rpx 16rpx;
  border-radius: 0 16rpx 0 12rpx;
  font-size: 20rpx;
  position: absolute;
  top: 0;
  right: 0;
}

.task-status-bar.pending {
  background: #fef9c3;
  color: #854d0e;
}

.task-status-bar.in-progress {
  background: #dbeafe;
  color: #1e40af;
}

.task-status-bar.completed {
  background: #d1fae5;
  color: #14532d;
}

.task-type-tag {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  font-size: 20rpx;
  padding: 6rpx 14rpx;
  border-radius: 8rpx;
  margin-bottom: 12rpx;
  border: 1rpx solid #e5e7eb;
  background: #f9fafb;
  color: $text-secondary;
}

.task-type-tag.survey { background: #f0fdf4; border-color: rgba(34,197,94,0.2); color: $primary-dark; }
.task-type-tag.publicity { background: #eff6ff; border-color: rgba(59,130,246,0.2); color: #1d4ed8; }
.task-type-tag.education { background: #faf5ff; border-color: rgba(168,85,247,0.2); color: #6b21a8; }
.task-type-tag.compliance { background: #fffbeb; border-color: rgba(245,158,11,0.2); color: #92400e; }
.task-type-tag.recycle { background: #f0fdf4; border-color: rgba(34,197,94,0.2); color: #15803d; }

.task-title {
  font-size: 30rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
  margin-bottom: 10rpx;
}

.task-desc {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.4;
}

.task-source {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 12rpx;
}

.source-icon {
  font-size: 24rpx;
}

.source-text {
  font-size: 22rpx;
  color: #9ca3af;
}

.task-requirements {
  margin-bottom: 16rpx;
  padding: 16rpx;
  background: #f9fafb;
  border-radius: 12rpx;
  border: 1rpx solid #e5e7eb;
}

.req-item {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 6rpx;
  padding-left: 8rpx;
}

.req-item:last-child {
  margin-bottom: 0;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.task-deadline {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  color: #9ca3af;
}

.deadline-icon {
  font-size: 24rpx;
}

.task-points {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.points-num {
  font-size: 28rpx;
  font-weight: 700;
  color: #d97706;
}

.points-label {
  font-size: 22rpx;
  color: #9ca3af;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.progress-bar {
  flex: 1;
  height: 8rpx;
  background: #e5e7eb;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, $primary, #86efac);
  border-radius: 4rpx;
}

.progress-text {
  font-size: 22rpx;
  color: $primary;
  font-weight: 600;
  width: 72rpx;
  text-align: right;
}

.task-action {
  display: flex;
  gap: 16rpx;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f3f4f6;
}

.action-btn {
  flex: 1;
  text-align: center;
  padding: 18rpx;
  border-radius: 12rpx;
  font-size: 26rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.action-btn.primary {
  background: linear-gradient(135deg, $primary, $primary-dark);
  color: #fff;
  border-color: transparent;
}

.action-btn.secondary {
  background: #f9fafb;
  color: $text-secondary;
  border: 1rpx solid #e5e7eb;
}
</style>
