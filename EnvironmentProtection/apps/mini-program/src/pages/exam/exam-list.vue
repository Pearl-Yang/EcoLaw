<template>
  <view class="exam-list-container">
    <!-- 顶部背景 -->
    <view class="header-bg">
      <view class="header-content">
        <text class="page-title">我的考试</text>
        <text class="page-subtitle">检验学习成果，提升合规能力</text>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-card" v-if="stats">
      <view class="stat-item">
        <text class="stat-value">{{ stats.total }}</text>
        <text class="stat-label">考试总数</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.avgScore }}</text>
        <text class="stat-label">平均分</text>
      </view>
    </view>

    <!-- 标签页 -->
    <view class="tabs-wrapper">
      <view class="tab-item" :class="{ active: currentTab === 'pending' }" @click="switchTab('pending')">
        <text>待完成</text>
        <view class="tab-badge" v-if="pendingList.length > 0">{{ pendingList.length }}</view>
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'completed' }" @click="switchTab('completed')">
        <text>已完成</text>
      </view>
      <view class="tab-item" :class="{ active: currentTab === 'all' }" @click="switchTab('all')">
        <text>全部</text>
      </view>
    </view>

    <!-- 考试列表 -->
    <scroll-view scroll-y class="exam-scroll" @scrolltolower="loadMore">
      <view class="exam-list">
        <!-- 待完成考试 -->
        <view v-if="currentTab === 'pending'" class="list-section">
          <view v-if="pendingList.length === 0" class="empty-state">
            <image class="empty-icon" src="/static/images/empty-exam.png" mode="aspectFit" />
            <text class="empty-text">暂无待完成的考试</text>
          </view>
          <view v-else>
            <view v-for="item in pendingList" :key="item.id" class="exam-card pending" @click="goToExam(item)">
              <view class="card-header">
                <view class="card-badge pending-badge">待完成</view>
                <text class="card-title">{{ item.paperTitle }}</text>
              </view>
              <view class="card-info">
                <view class="info-item">
                  <text class="info-icon">📋</text>
                  <text>{{ item.totalCount || 0 }} 题</text>
                </view>
                <view class="info-item">
                  <text class="info-icon">⏱️</text>
                  <text>{{ item.timeLimit || 60 }} 分钟</text>
                </view>
                <view class="info-item">
                  <text class="info-icon">🏆</text>
                  <text>{{ item.passScore || 60 }} 分及格</text>
                </view>
              </view>
              <view class="card-action">
                <button class="btn-start" :disabled="item.status === 'in_progress'">
                  {{ item.status === 'in_progress' ? '继续答题' : '开始考试' }}
                </button>
              </view>
            </view>
          </view>
        </view>

        <!-- 已完成考试 -->
        <view v-if="currentTab === 'completed'" class="list-section">
          <view v-if="completedList.length === 0" class="empty-state">
            <image class="empty-icon" src="/static/images/empty-exam.png" mode="aspectFit" />
            <text class="empty-text">暂无已完成的考试</text>
          </view>
          <view v-else>
            <view v-for="item in completedList" :key="item.id" class="exam-card completed" @click="viewResult(item)">
              <view class="card-header">
                <view class="card-badge" :class="item.isPassed ? 'pass-badge' : 'fail-badge'">
                  {{ item.isPassed ? '已及格' : '未及格' }}
                </view>
                <text class="card-title">{{ item.paperTitle }}</text>
              </view>
              <view class="card-result">
                <view class="result-score">
                  <text class="score-value">{{ item.score || 0 }}</text>
                  <text class="score-unit">分</text>
                </view>
                <view class="result-detail">
                  <view class="detail-item">
                    <text class="detail-label">正确率</text>
                    <text class="detail-value">{{ item.correctRate || 0 }}%</text>
                  </view>
                  <view class="detail-item">
                    <text class="detail-label">用时</text>
                    <text class="detail-value">{{ formatDuration(item.duration) }}</text>
                  </view>
                </view>
              </view>
              <view class="card-footer">
                <text class="card-time">完成于 {{ formatDate(item.submitTime) }}</text>
                <text class="card-view">查看详情 →</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 全部考试 -->
        <view v-if="currentTab === 'all'" class="list-section">
          <view v-if="allList.length === 0" class="empty-state">
            <image class="empty-icon" src="/static/images/empty-exam.png" mode="aspectFit" />
            <text class="empty-text">暂无考试记录</text>
          </view>
          <view v-else>
            <view v-for="item in allList" :key="item.id" class="exam-card" :class="getCardClass(item)">
              <view class="card-header">
                <view class="card-badge" :class="getBadgeClass(item)">
                  {{ getStatusText(item) }}
                </view>
                <text class="card-title">{{ item.paperTitle }}</text>
              </view>
              <view class="card-info">
                <view class="info-item">
                  <text class="info-icon">📋</text>
                  <text>{{ item.totalCount || 0 }} 题</text>
                </view>
                <view v-if="item.score !== null && item.score !== undefined" class="info-item">
                  <text class="info-icon">🏆</text>
                  <text>得分: {{ item.score }}</text>
                </view>
                <view v-if="item.correctRate !== null && item.correctRate !== undefined" class="info-item">
                  <text class="info-icon">✓</text>
                  <text>正确率: {{ item.correctRate }}%</text>
                </view>
              </view>
              <view class="card-footer">
                <text class="card-time">{{ formatDate(item.createTime) }}</text>
                <text v-if="item.status === 'not_started' || item.status === 'in_progress'"
                      class="card-action-text" @click.stop="goToExam(item)">
                  {{ item.status === 'in_progress' ? '继续' : '开始' }} →
                </text>
                <text v-else class="card-action-text" @click.stop="viewResult(item)">
                  查看详情 →
                </text>
              </view>
            </view>
          </view>
        </view>

        <!-- 加载状态 -->
        <view v-if="loading" class="loading-more">
          <text>加载中...</text>
        </view>
        <view v-if="noMore && allList.length > 0" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { api } from '@/api/index.js'

const currentTab = ref('pending')
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = 10

const allList = ref([])
const pendingList = computed(() => allList.value.filter(item => item.status === 'not_started' || item.status === 'in_progress'))
const completedList = computed(() => allList.value.filter(item => item.status === 'graded' || item.status === 'submitted'))

const stats = ref({
  total: 0,
  completed: 0,
  avgScore: 0
})

const loadExamList = async (reset = false) => {
  if (loading.value) return
  if (reset) {
    page.value = 1
    noMore.value = false
    allList.value = []
  }

  loading.value = true
  try {
    const res = await api.exam.myList({ page: page.value, pageSize })
    const list = res || []

    if (reset) {
      allList.value = list
    } else {
      allList.value.push(...list)
    }

    if (list.length < pageSize) {
      noMore.value = true
    }

    // 更新统计
    stats.value.total = allList.value.length
    stats.value.completed = completedList.value.length
    const scoredItems = completedList.value.filter(item => item.score !== null && item.score !== undefined)
    if (scoredItems.length > 0) {
      const totalScore = scoredItems.reduce((sum, item) => sum + (item.score || 0), 0)
      stats.value.avgScore = Math.round(totalScore / scoredItems.length)
    }
  } catch (e) {
    console.error('加载考试列表失败', e)
  } finally {
    loading.value = false
  }
}

const switchTab = (tab) => {
  currentTab.value = tab
}

const goToExam = (item) => {
  uni.navigateTo({
    url: `/pages/exam/exam-paper?id=${item.paperId}&recordId=${item.id}`
  })
}

const viewResult = (item) => {
  uni.navigateTo({
    url: `/pages/exam/exam-result?id=${item.id}`
  })
}

const loadMore = () => {
  if (!noMore.value && !loading.value) {
    page.value++
    loadExamList()
  }
}

const getCardClass = (item) => {
  if (item.status === 'graded') {
    return item.isPassed ? 'pass' : 'fail'
  }
  return ''
}

const getBadgeClass = (item) => {
  if (item.status === 'not_started') return 'pending-badge'
  if (item.status === 'in_progress') return 'progress-badge'
  if (item.status === 'graded') return item.isPassed ? 'pass-badge' : 'fail-badge'
  return ''
}

const getStatusText = (item) => {
  if (item.status === 'not_started') return '待开始'
  if (item.status === 'in_progress') return '进行中'
  if (item.status === 'graded') return item.isPassed ? '已及格' : '未及格'
  if (item.status === 'submitted') return '已提交'
  return ''
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}分${secs}秒`
}

onMounted(() => {
  loadExamList(true)
})
</script>

<style lang="scss" scoped>
.exam-list-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.header-bg {
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  padding: 40rpx 32rpx 80rpx;
  border-radius: 0 0 40rpx 40rpx;
}

.header-content {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #ffffff;
}

.page-subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 12rpx;
}

.stats-card {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: #ffffff;
  margin: -50rpx 32rpx 0;
  padding: 32rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(82, 183, 136, 0.15);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 700;
  color: #52b788;
}

.stat-label {
  font-size: 24rpx;
  color: #909399;
  margin-top: 8rpx;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: #e4e7ed;
}

.tabs-wrapper {
  display: flex;
  padding: 32rpx 32rpx 0;
  gap: 32rpx;
}

.tab-item {
  position: relative;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 8rpx;

  &.active {
    color: #2d6a4f;
    font-weight: 600;
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 48rpx;
      height: 6rpx;
      background: #52b788;
      border-radius: 3rpx;
    }
  }
}

.tab-badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  background: #f56c6c;
  color: #fff;
  font-size: 20rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.exam-scroll {
  height: calc(100vh - 400rpx);
  padding: 24rpx 32rpx;
}

.exam-list {
  padding-bottom: 32rpx;
}

.exam-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

  &.pass {
    border-left: 6rpx solid #67c23a;
  }

  &.fail {
    border-left: 6rpx solid #f56c6c;
  }

  &.pending {
    border-left: 6rpx solid #e6a23c;
  }
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.card-badge {
  padding: 6rpx 16rpx;
  font-size: 22rpx;
  border-radius: 8rpx;
  flex-shrink: 0;

  &.pending-badge {
    background: rgba(230, 162, 60, 0.1);
    color: #e6a23c;
  }

  &.progress-badge {
    background: rgba(64, 158, 255, 0.1);
    color: #409eff;
  }

  &.pass-badge {
    background: rgba(103, 194, 58, 0.1);
    color: #67c23a;
  }

  &.fail-badge {
    background: rgba(245, 108, 108, 0.1);
    color: #f56c6c;
  }
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.card-info {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  margin-bottom: 20rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #606266;
}

.info-icon {
  font-size: 24rpx;
}

.card-result {
  display: flex;
  align-items: center;
  gap: 32rpx;
  padding: 20rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.result-score {
  display: flex;
  align-items: baseline;
}

.score-value {
  font-size: 56rpx;
  font-weight: 700;
  color: #52b788;
}

.score-unit {
  font-size: 24rpx;
  color: #909399;
  margin-left: 4rpx;
}

.result-detail {
  display: flex;
  gap: 32rpx;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 22rpx;
  color: #909399;
}

.detail-value {
  font-size: 28rpx;
  color: #606266;
  font-weight: 500;
}

.card-action {
  display: flex;
  justify-content: flex-end;
}

.btn-start {
  padding: 16rpx 48rpx;
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  color: #fff;
  font-size: 28rpx;
  border-radius: 40rpx;
  border: none;

  &[disabled] {
    background: #c0c4cc;
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-time {
  font-size: 24rpx;
  color: #909399;
}

.card-view {
  font-size: 26rpx;
  color: #52b788;
}

.card-action-text {
  font-size: 26rpx;
  color: #52b788;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 32rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #909399;
}

.loading-more,
.no-more {
  text-align: center;
  padding: 32rpx;
  color: #909399;
  font-size: 24rpx;
}
</style>
