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
      </view>
    </view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索资讯..."
        @confirm="onSearch"
      />
      <view class="search-btn" @click="onSearch">
        <text class="search-icon">🔍</text>
      </view>
    </view>

    <!-- 热门标签 -->
    <view class="hot-tags" v-if="!keyword">
      <text
        v-for="tag in hotTags"
        :key="tag"
        class="hot-tag"
        :class="{ active: selectedTag === tag }"
        @click="selectTag(tag)"
      >
        {{ tag }}
      </text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 空状态 -->
    <view v-else-if="newsList.length === 0" class="empty-state">
      <text class="empty-icon">📰</text>
      <text class="empty-text">暂无相关资讯</text>
    </view>

    <!-- 新闻列表 -->
    <scroll-view
      v-else
      class="news-list"
      scroll-y
      @scrolltolower="loadMore"
    >
      <!-- 置顶新闻 -->
      <view
        v-for="item in topNews"
        :key="item.id"
        class="news-item top-news"
        @click="goDetail(item.id)"
      >
        <view class="news-badge">
          <text class="badge-text">置顶</text>
        </view>
        <view class="news-main">
          <text class="news-title">{{ item.title }}</text>
          <text class="news-summary">{{ item.summary }}</text>
          <view class="news-meta">
            <text class="meta-source">{{ item.source || '官方' }}</text>
            <text class="meta-time">{{ formatDate(item.publishTime) }}</text>
            <text class="meta-views">👁 {{ item.viewCount || 0 }}</text>
          </view>
        </view>
      </view>

      <!-- 普通新闻 -->
      <view
        v-for="item in normalNews"
        :key="item.id"
        class="news-item"
        @click="goDetail(item.id)"
      >
        <image v-if="item.coverUrl" class="news-cover" :src="item.coverUrl" mode="aspectFill" />
        <view class="news-body">
          <text class="news-title">{{ item.title }}</text>
          <text class="news-summary">{{ item.summary }}</text>
          <view class="news-meta">
            <text class="news-tag" :class="'tag-' + item.type">{{ getTypeName(item.type) }}</text>
            <text class="meta-time">{{ formatDate(item.publishTime) }}</text>
            <text class="meta-views">👁 {{ item.viewCount || 0 }}</text>
          </view>
        </view>
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
import { ref, computed, onMounted } from 'vue'
import { api } from '../../api/index.js'
import { userStore } from '../../store/user.js'

const currentTab = ref('')
const keyword = ref('')
const selectedTag = ref('')
const loading = ref(false)
const newsList = ref([])
const page = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)

const tabs = [
  { key: '', name: '全部', icon: '📰' },
  { key: 'hotspot', name: '热点', icon: '🔥' },
  { key: 'policy', name: '政策', icon: '📋' },
  { key: 'news', name: '新闻', icon: '📢' },
  { key: 'activity', name: '活动', icon: '🎉' }
]

const hotTags = ['禁塑', '固废法', '环保', '白色污染', '可降解']

const topNews = computed(() => newsList.value.filter(item => item.isTop === 1))
const normalNews = computed(() => newsList.value.filter(item => item.isTop !== 1))

onMounted(() => {
  loadNews()
})

function switchTab(key) {
  currentTab.value = key
  page.value = 1
  hasMore.value = true
  newsList.value = []
  loadNews()
}

function selectTag(tag) {
  selectedTag.value = selectedTag.value === tag ? '' : tag
  keyword.value = selectedTag.value
  page.value = 1
  hasMore.value = true
  newsList.value = []
  loadNews()
}

function onSearch() {
  page.value = 1
  hasMore.value = true
  newsList.value = []
  loadNews()
}

async function loadNews() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (currentTab.value) {
      params.type = currentTab.value
    }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    const res = await api.news.list(params)
    if (res) {
      if (page.value === 1) {
        newsList.value = res
      } else {
        newsList.value = [...newsList.value, ...res]
      }
      hasMore.value = res.length >= pageSize.value
    }
  } catch (error) {
    console.error('加载资讯失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (!hasMore.value || loading.value) return
  page.value++
  await loadNews()
}

function goDetail(id) {
  uni.navigateTo({
    url: `/pages/news/news-detail?id=${id}`
  })
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    return `${date.getMonth() + 1}-${date.getDate()}`
  } catch {
    return dateStr
  }
}

function getTypeName(type) {
  const map = {
    hotspot: '热点',
    policy: '政策',
    news: '新闻',
    activity: '活动'
  }
  return map[type] || '资讯'
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

.search-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #fff;
  border-radius: 999rpx;
  padding: 16rpx 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.search-btn {
  width: 64rpx;
  height: 64rpx;
  background: linear-gradient(135deg, #28a745, #1a4d2e);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-icon {
  font-size: 28rpx;
}

.hot-tags {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
  flex-wrap: wrap;
}

.hot-tag {
  padding: 10rpx 20rpx;
  background: #fff;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666;
  border: 1rpx solid #e8f5eb;
}

.hot-tag.active {
  background: #fff8e1;
  color: #e65100;
  border-color: #ffcc02;
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

.news-list {
  height: calc(100vh - 400rpx);
}

.news-item {
  display: flex;
  gap: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.05);
}

.top-news {
  flex-direction: column;
  border: 2rpx solid #ffec99;
  background: linear-gradient(135deg, #fffde7, #fff8e1);
}

.news-badge {
  margin-bottom: 12rpx;
}

.badge-text {
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: #fff;
  border-radius: 4rpx;
}

.news-cover {
  width: 200rpx;
  height: 140rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
  background: #e8f5eb;
}

.news-main,
.news-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.news-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #222;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-summary {
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 22rpx;
  color: #999;
}

.news-tag {
  padding: 4rpx 12rpx;
  border-radius: 6rpx;
  font-size: 20rpx;
  background: #e8f5eb;
  color: #28a745;
}

.tag-hotspot {
  background: #fff3e0;
  color: #e65100;
}

.tag-policy {
  background: #e3f2fd;
  color: #1565c0;
}

.tag-activity {
  background: #f3e5f5;
  color: #7b1fa2;
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
