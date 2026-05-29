<template>
  <view class="page">
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="!task" class="state">任务不存在或已删除</view>
    <view v-else class="card">
      <view class="row-top">
        <text class="badge" :class="statusClass">{{ statusText }}</text>
        <text class="type-tag">{{ typeText }}</text>
      </view>
      <text class="title">{{ task.title }}</text>
      <text class="content">{{ task.content || task.description || '暂无说明' }}</text>
      <view class="meta">
        <text class="meta-line">开始：{{ formatTime(task.startTime) }}</text>
        <text class="meta-line">截止：{{ formatTime(task.endTime) }}</text>
        <text v-if="task.targetCount != null" class="meta-line">
          进度：{{ task.completedCount || 0 }} / {{ task.targetCount }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'

const loading = ref(true)
const task = ref(null)
const statusText = ref('')
const statusClass = ref('pending')
const typeText = ref('任务')

function mapType(t) {
  const m = { government: '政务', enterprise: '企业', law_firm: '律所' }
  return m[t] || t || '任务'
}

function mapStatus(s) {
  const map = {
    pending: ['pending', '待执行'],
    in_progress: ['in-progress', '执行中'],
    completed: ['completed', '已完成'],
    overdue: ['pending', '已逾期'],
    rejected: ['pending', '已驳回']
  }
  const x = map[s] || ['pending', '待执行']
  return { cls: x[0], text: x[1] }
}

function formatTime(v) {
  if (!v) return '待定'
  const d = typeof v === 'string' ? v.replace('T', ' ').slice(0, 16) : String(v)
  return d
}

onLoad((q) => {
  const id = q.id ? decodeURIComponent(q.id) : ''
  if (!id) {
    loading.value = false
    return
  }
  api.task
    .get(id, { showLoading: false })
    .then((res) => {
      task.value = res
      typeText.value = mapType(res.type)
      const st = mapStatus(res.status)
      statusClass.value = st.cls
      statusText.value = st.text
    })
    .catch(() => {
      task.value = null
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f7f2;
  padding: 24rpx;
}

.state {
  text-align: center;
  color: #888;
  padding: 80rpx 24rpx;
  font-size: 28rpx;
}

.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.row-top {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.badge {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.badge.pending {
  background: #fff3e0;
  color: #e65100;
}

.badge.in-progress {
  background: #e3f2fd;
  color: #1565c0;
}

.badge.completed {
  background: #e8f5e9;
  color: #2e7d32;
}

.type-tag {
  font-size: 22rpx;
  color: #666;
}

.title {
  font-size: 34rpx;
  font-weight: 700;
  color: #222;
  display: block;
  margin-bottom: 16rpx;
}

.content {
  font-size: 26rpx;
  color: #555;
  line-height: 1.5;
  display: block;
  margin-bottom: 24rpx;
}

.meta-line {
  display: block;
  font-size: 24rpx;
  color: #888;
  margin-bottom: 8rpx;
}
</style>
