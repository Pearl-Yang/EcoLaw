<template>
  <view class="page">
    <!-- 顶部标签 -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab"
        :class="{ active: currentTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <text class="tab-icon">{{ tab.icon }}</text>
        <text class="tab-name">{{ tab.name }}</text>
        <text v-if="tab.key === '' && unreadCount > 0" class="tab-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</text>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 空状态 -->
    <view v-else-if="notifications.length === 0" class="empty-state">
      <text class="empty-icon">🔔</text>
      <text class="empty-text">暂无消息通知</text>
    </view>

    <!-- 通知列表 -->
    <scroll-view
      v-else
      class="notification-list"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view
        v-for="item in notifications"
        :key="item.id"
        class="notification-item"
        :class="{ unread: item.isRead !== 1 }"
        @click="handleClick(item)"
      >
        <view class="notification-icon" :class="'icon-' + item.type">
          <text class="icon-text">{{ getTypeIcon(item.type) }}</text>
        </view>
        <view class="notification-body">
          <view class="notification-header">
            <text class="notification-title">{{ item.title }}</text>
            <text v-if="item.isRead !== 1" class="unread-dot"></text>
          </view>
          <text class="notification-content">{{ item.content }}</text>
          <view class="notification-footer">
            <text class="notification-type">{{ getTypeName(item.type) }}</text>
            <text class="notification-time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
        <view class="notification-arrow">›</view>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore" class="load-more" @click="loadMore">
        <text class="load-more-text">加载更多</text>
      </view>
      <view v-else class="no-more">
        <text class="no-more-text">— 没有更多了 —</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../../api/index.js'
import { userStore } from '../../store/user.js'

const currentTab = ref('')
const loading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)
const page = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

const tabs = [
  { key: '', name: '全部', icon: '📋' },
  { key: 'system', name: '系统', icon: '⚙️' },
  { key: 'task', name: '任务', icon: '📝' },
  { key: 'training', name: '培训', icon: '🎓' },
  { key: 'activity', name: '活动', icon: '🎉' }
]

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})

function switchTab(key) {
  currentTab.value = key
  page.value = 1
  hasMore.value = true
  notifications.value = []
  loadNotifications()
}

async function loadNotifications() {
  const userId = userStore.userInfo?.id
  if (!userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const params = {
      userId: userId,
      page: page.value,
      size: pageSize.value
    }
    if (currentTab.value) {
      params.type = currentTab.value
    }
    const res = await api.notification.list(params)
    if (res) {
      if (page.value === 1) {
        notifications.value = res
      } else {
        notifications.value = [...notifications.value, ...res]
      }
      hasMore.value = res.length >= pageSize.value
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  const userId = userStore.userInfo?.id
  if (!userId) return
  try {
    const res = await api.notification.unreadCount(userId)
    unreadCount.value = res || 0
  } catch (error) {
    console.error('获取未读数量失败:', error)
  }
}

async function loadMore() {
  if (!hasMore.value || loading.value) return
  page.value++
  await loadNotifications()
}

async function handleClick(item) {
  if (item.isRead !== 1) {
    await markAsRead(item.id)
  }
  if (item.actionUrl) {
    uni.navigateTo({ url: item.actionUrl })
  }
}

async function markAsRead(id) {
  const userId = userStore.userInfo?.id
  if (!userId) return
  try {
    await api.notification.markAsRead(id, userId)
    const index = notifications.value.findIndex(n => n.id === id)
    if (index !== -1) {
      notifications.value[index].isRead = 1
    }
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
    if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
    return `${date.getMonth() + 1}-${date.getDate()}`
  } catch {
    return dateStr
  }
}

function getTypeIcon(type) {
  const map = {
    system: '⚙️',
    task: '📝',
    training: '🎓',
    activity: '🎉',
    report: '📍'
  }
  return map[type] || '🔔'
}

function getTypeName(type) {
  const map = {
    system: '系统通知',
    task: '任务通知',
    training: '培训通知',
    activity: '活动通知',
    report: '举报通知'
  }
  return map[type] || '通知'
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f7f2;
  padding: 0 24rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
}

.tabs {
  display: flex;
  gap: 16rpx;
  padding: 24rpx 0;
  flex-wrap: wrap;
}

.tab {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  background: #fff;
  border-radius: 999rpx;
  border: 2rpx solid #e8f5eb;
  transition: all 0.2s;
}

.tab.active {
  background: linear-gradient(135deg, #28a745, #1a4d2e);
  border-color: transparent;
  color: #fff;
}

.tab-icon {
  font-size: 24rpx;
}

.tab-name {
  font-size: 26rpx;
  font-weight: 600;
}

.tab-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  background: #ef4444;
  color: #fff;
  font-size: 20rpx;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-state,
.empty-state {
  padding: 120rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}

.empty-icon {
  font-size: 80rpx;
  display: block;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.notification-list {
  height: calc(100vh - 200rpx);
}

.notification-item {
  display: flex;
  gap: 20rpx;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.05);
  transition: all 0.2s;
}

.notification-item:active {
  background: #f5f5f5;
}

.notification-item.unread {
  background: linear-gradient(135deg, #e8f5eb, #f0f7f2);
  border-left: 6rpx solid #28a745;
}

.notification-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #e8f5eb;
}

.icon-system {
  background: #e3f2fd;
}

.icon-task {
  background: #fff3e0;
}

.icon-training {
  background: #f3e5f5;
}

.icon-activity {
  background: #e0f7fa;
}

.icon-report {
  background: #ffebee;
}

.icon-text {
  font-size: 40rpx;
}

.notification-body {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.notification-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-dot {
  width: 12rpx;
  height: 12rpx;
  background: #ef4444;
  border-radius: 50%;
  flex-shrink: 0;
}

.notification-content {
  font-size: 24rpx;
  color: #666;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: #999;
}

.notification-type {
  color: #28a745;
}

.notification-arrow {
  font-size: 40rpx;
  color: #ccc;
  flex-shrink: 0;
}

.load-more {
  padding: 32rpx 0;
  text-align: center;
}

.load-more-text {
  font-size: 26rpx;
  color: #28a745;
}

.no-more {
  padding: 32rpx 0;
  text-align: center;
}

.no-more-text {
  font-size: 24rpx;
  color: #999;
}
</style>
