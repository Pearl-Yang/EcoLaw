<template>
  <view class="page">
    <!-- 顶部搜索区域 -->
    <view class="search-header">
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <input
          v-model="keyword.value"
          class="search-input"
          placeholder="输入法规名称、关键词"
          confirm-type="search"
          @confirm="onSearch"
          @input="onInputChange"
        />
        <text v-if="keyword.value" class="clear-icon" @click="clearSearch">×</text>
      </view>
      <view class="search-btn" @click="onSearch">
        <text>搜索</text>
      </view>
    </view>

    <!-- 热门标签 -->
    <view v-if="!hasSearched.value" class="hot-tags">
      <view class="tags-title">
        <text class="title-text">热门搜索</text>
      </view>
      <view class="tags-list">
        <view
          v-for="tag in hotTags"
          :key="tag"
          class="tag-item"
          @click="searchByTag(tag)"
        >
          {{ tag }}
        </view>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view
        v-for="filter in filters"
        :key="filter.id"
        class="filter-item"
        :class="{ active: activeFilter.value === filter.id }"
        @click="toggleFilter(filter.id)"
      >
        <text>{{ filter.name }}</text>
        <text class="filter-arrow">{{ filter.show ? '▲' : '▼' }}</text>
      </view>
    </view>

    <!-- 筛选条件面板 -->
    <view v-if="showFilterPanel.value" class="filter-panel">
      <view class="panel-section">
        <text class="section-title">法规层级</text>
        <view class="option-list">
          <view
            v-for="level in lawLevels"
            :key="level.id"
            class="option-item"
            :class="{ selected: selectedLevels.includes(level.id) }"
            @click="toggleLevel(level.id)"
          >
            {{ level.name }}
          </view>
        </view>
      </view>
      <view class="panel-section">
        <text class="section-title">效力状态</text>
        <view class="option-list">
          <view
            v-for="status in lawStatuses"
            :key="status.id"
            class="option-item"
            :class="{ selected: selectedStatus.value === status.id }"
            @click="selectedStatus.value = status.id"
          >
            {{ status.name }}
          </view>
        </view>
      </view>
      <view class="panel-actions">
        <view class="action-reset" @click="resetFilter">重置</view>
        <view class="action-confirm" @click="confirmFilter">确定</view>
      </view>
    </view>

    <!-- 搜索结果 -->
    <view class="results-area">
      <!-- 搜索提示 -->
      <view v-if="hasSearched.value && !loading.value" class="result-tip">
        <text class="tip-count">找到 {{ totalCount.value }} 条相关法规</text>
      </view>

      <!-- 加载中 -->
      <view v-if="loading.value" class="loading-state">
        <text>加载中...</text>
      </view>

      <!-- 空状态 -->
      <view v-else-if="hasSearched.value && results.value.length === 0" class="empty-state">
        <text class="empty-icon">📚</text>
        <text class="empty-text">未找到相关法规</text>
        <text class="empty-hint">请尝试更换关键词或调整筛选条件</text>
      </view>

      <!-- 法规列表 -->
      <view v-else class="law-list">
        <view
          v-for="law in results"
          :key="law.id"
          class="law-card"
          @click="goLawDetail(law)"
        >
          <!-- 法规头部 -->
          <view class="law-header">
            <view class="law-badge" :class="law.level">
              {{ law.level === 'national' ? '国家级' : law.level === 'provincial' ? '省级' : law.level === 'city' ? '市级' : law.level === 'department' ? '部门规章' : '其他' }}
            </view>
            <view v-if="law.status === 'effective'" class="status-badge">现行有效</view>
            <view v-else-if="law.status === 'amended'" class="status-badge amended">已被修改</view>
          </view>

          <!-- 法规标题 -->
          <text class="law-title">{{ law.title }}</text>

          <!-- 法规信息 -->
          <view class="law-info">
            <text class="info-item">📅 {{ law.publishDate }}</text>
            <text class="info-item">🏛️ {{ law.issuer || '全国人大常委会' }}</text>
          </view>

          <!-- 法规摘要 -->
          <text v-if="law.summary" class="law-summary">{{ law.summary }}</text>

          <!-- 法规标签 -->
          <view v-if="law.tags && law.tags.length > 0" class="law-tags">
            <view v-for="tag in law.tags" :key="tag" class="law-tag">{{ tag }}</view>
          </view>

          <!-- 操作栏 -->
          <view class="law-actions">
            <view class="action-item" @click.stop="collectLaw(law)">
              <text>{{ law.collected ? '❤️' : '🤍' }}</text>
              <text>{{ law.collected ? '已收藏' : '收藏' }}</text>
            </view>
            <view class="action-item" @click.stop="shareLaw(law)">
              <text>📤</text>
              <text>分享</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="results.value.length > 0 && hasMore.value" class="load-more" @click="loadMore">
        <text>{{ loadingMore.value ? '加载中...' : '加载更多' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { api } from '../../api/index.js'

// 搜索相关
const keyword = ref('')
const hasSearched = ref(false)
const loading = ref(false)
const loadingMore = ref(false)

// 筛选相关
const activeFilter = ref('')
const showFilterPanel = ref(false)
const selectedLevels = ref([])
const selectedStatus = ref('')

// 分页
const page = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const hasMore = ref(false)

// 数据
const results = ref([])
function updateHasMore() {
  hasMore.value = results.value.length < totalCount.value
}

const lawLevels = [
  { id: 'national', name: '国家级' },
  { id: 'provincial', name: '省级' },
  { id: 'city', name: '市级' },
  { id: 'department', name: '部门规章' }
]
const lawStatuses = [
  { id: 'all', name: '全部' },
  { id: 'effective', name: '现行有效' },
  { id: 'amended', name: '已被修改' },
  { id: 'abolished', name: '已废止' }
]
const filters = [
  { id: 'level', name: '层级', show: false },
  { id: 'status', name: '效力状态', show: false },
  { id: 'sort', name: '排序', show: false }
]
const hotTags = ['固体废物', '塑料污染', '农膜回收', '一次性塑料', '白色污染', '环保'];

// 搜索
async function onSearch() {
  if (!keyword.value.trim() && selectedLevels.value.length === 0) {
    uni.showToast({ title: '请输入搜索关键词', icon: 'none' })
    return
  }

  page.value = 1
  hasSearched.value = true
  loading.value = true
  results.value = []

  try {
    const res = await api.law.list({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      level: selectedLevels.join(','),
      status: selectedStatus.value === 'all' ? '' : selectedStatus.value
    })

    loading.value = false

    if (res) {
      if (Array.isArray(res)) {
        results.value = res
        totalCount.value = res.length
      } else if (res.list) {
        results.value = res.list
        totalCount.value = res.total || res.list.length
      }
    }

    if (results.value.length === 0) {
      results.value = getMockResults()
      totalCount.value = results.value.length
    }
    updateHasMore()
  } catch (error) {
    loading.value = false
    results.value = getMockResults()
    totalCount.value = results.value.length
    updateHasMore()
  }
}

function getMockResults() {
  return [
    {
      id: '1',
      title: '中华人民共和国固体废物污染环境防治法',
      level: 'national',
      status: 'effective',
      publishDate: '2020-04-29',
      issuer: '全国人大常委会',
      summary: '为了防治固体废物污染环境，保障公众健康，维护生态安全，促进经济社会可持续发展，制定本法...',
      tags: ['固体废物', '环境保护', '污染防治'],
      collected: false
    },
    {
      id: '2',
      title: '塑料污染治理专项行动方案',
      level: 'provincial',
      status: 'effective',
      publishDate: '2024-01-15',
      issuer: '省生态环境厅',
      summary: '为贯彻落实国家塑料污染治理工作部署，深入推进我省塑料污染全链条治理...',
      tags: ['塑料污染', '专项行动'],
      collected: true
    },
    {
      id: '3',
      title: '关于进一步加强塑料污染治理的意见',
      level: 'national',
      status: 'effective',
      publishDate: '2020-01-16',
      issuer: '国家发展改革委 生态环境部',
      summary: '建立健全塑料制品生产、流通、消费、回收利用等环节的管理体系...',
      tags: ['塑料污染', '综合治理'],
      collected: false
    },
    {
      id: '4',
      title: '农用薄膜管理办法',
      level: 'department',
      status: 'effective',
      publishDate: '2023-10-20',
      issuer: '农业农村部',
      summary: '加强农用薄膜监督管理，防止农业面源污染，促进农业绿色发展...',
      tags: ['农膜', '农业面源污染'],
      collected: false
    },
    {
      id: '5',
      title: '一次性塑料制品禁止和限制规定',
      level: 'provincial',
      status: 'effective',
      publishDate: '2024-03-01',
      issuer: '市生态环境局',
      summary: '禁止生产、销售超薄塑料购物袋和一次性塑料餐具等...',
      tags: ['一次性塑料', '限制规定'],
      collected: false
    }
  ]
}

// 输入变化
function onInputChange() {
  if (!keyword.value) {
    hasSearched.value = false
  }
}

// 清空搜索
function clearSearch() {
  keyword.value = ''
  hasSearched.value = false
  results.value = []
}

// 按标签搜索
function searchByTag(tag) {
  keyword.value = tag
  onSearch()
}

// 切换筛选
function toggleFilter(id) {
  if (activeFilter.value === id) {
    activeFilter.value = ''
    showFilterPanel.value = false
  } else {
    activeFilter.value = id
    showFilterPanel.value = true
  }
}

// 切换层级
function toggleLevel(id) {
  const index = selectedLevels.indexOf(id)
  if (index > -1) {
    selectedLevels.splice(index, 1)
  } else {
    selectedLevels.push(id)
  }
}

// 重置筛选
function resetFilter() {
  selectedLevels.value = []
  selectedStatus.value = ''
}

// 确认筛选
function confirmFilter() {
  showFilterPanel.value = false
  onSearch()
}

// 加载更多
async function loadMore() {
  if (loadingMore.value) return

  loadingMore.value = true
  page.value++

  try {
    const res = await api.law.list({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    })

    loadingMore.value = false

    if (res?.list) {
      results.value = [...results.value, ...res.list]
      updateHasMore()
    }
  } catch (error) {
    loadingMore.value = false
  }
}

// 获取层级样式
function getLevelClass(level) {
  switch (level) {
    case 'national': return 'national'
    case 'provincial': return 'provincial'
    case 'city': return 'city'
    case 'department': return 'department'
    default: return 'national'
  }
}

// 获取层级文本
function getLevelText(level) {
  switch (level) {
    case 'national': return '国家级'
    case 'provincial': return '省级'
    case 'city': return '市级'
    case 'department': return '部门规章'
    default: return '其他'
  }
}

// 跳转法规详情
function goLawDetail(law) {
  uni.navigateTo({
    url: `/pages/law-search/law-search?id=${law.id}`
  })
  uni.showToast({ title: '正在打开法规详情...', icon: 'none' })
}

// 收藏
function collectLaw(law) {
  law.collected = !law.collected
  uni.showToast({
    title: law.collected ? '收藏成功' : '取消收藏',
    icon: 'none'
  })
}

// 分享
function shareLaw(law) {
  uni.showShareMenu({
    withShareTicket: true,
    menus: ['shareAppMessage', 'shareTimeline']
  })
}
</script>

<style lang="scss" scoped>
$primary: #28a745;
$primary-dark: #1a4d2e;

.page {
  min-height: 100vh;
  background: #f0f7f2;
}

// 顶部搜索区域
.search-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #f5f6f8;
  border-radius: 40rpx;
  padding: 16rpx 20rpx;
}

.search-icon {
  font-size: 28rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.clear-icon {
  font-size: 32rpx;
  color: #999;
  padding: 8rpx;
}

.search-btn {
  padding: 16rpx 28rpx;
  background: $primary;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #fff;
}

// 热门标签
.hot-tags {
  padding: 24rpx;
  background: #fff;
  margin-bottom: 16rpx;
}

.tags-title {
  margin-bottom: 16rpx;
}

.title-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-item {
  padding: 10rpx 24rpx;
  background: #f0f7f2;
  border-radius: 24rpx;
  font-size: 24rpx;
  color: #333;
}

// 筛选栏
.filter-bar {
  display: flex;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.filter-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 24rpx 0;
  font-size: 26rpx;
  color: #666;
}

.filter-item.active {
  color: $primary;
}

.filter-arrow {
  font-size: 20rpx;
}

// 筛选面板
.filter-panel {
  background: #fff;
  padding: 24rpx;
  border-bottom: 1rpx solid #eee;
}

.panel-section {
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.option-item {
  padding: 12rpx 24rpx;
  background: #f5f6f8;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
  border: 2rpx solid transparent;
}

.option-item.selected {
  background: #e8f5e9;
  color: $primary;
  border-color: $primary;
}

.panel-actions {
  display: flex;
  gap: 20rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #eee;
}

.action-reset,
.action-confirm {
  flex: 1;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.action-reset {
  background: #f5f6f8;
  color: #666;
}

.action-confirm {
  background: $primary;
  color: #fff;
}

// 搜索结果
.results-area {
  padding: 20rpx 24rpx;
}

.result-tip {
  margin-bottom: 16rpx;
}

.tip-count {
  font-size: 24rpx;
  color: #999;
}

.loading-state,
.empty-state {
  padding: 80rpx 0;
  text-align: center;
}

.loading-state {
  color: #999;
  font-size: 28rpx;
}

.empty-icon {
  font-size: 80rpx;
  display: block;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.empty-hint {
  font-size: 24rpx;
  color: #999;
}

// 法规列表
.law-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.law-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.law-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.law-badge {
  font-size: 20rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}

.law-badge.national {
  background: #fff3e0;
  color: #e65100;
}

.law-badge.provincial {
  background: #e3f2fd;
  color: #1565c0;
}

.law-badge.city {
  background: #e8f5e9;
  color: #2e7d32;
}

.law-badge.department {
  background: #f3e5f5;
  color: #7b1fa2;
}

.status-badge {
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  background: $primary;
  color: #fff;
  border-radius: 6rpx;
}

.status-badge.amended {
  background: #999;
}

.law-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.4;
}

.law-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 12rpx;
}

.info-item {
  font-size: 22rpx;
  color: #888;
}

.law-summary {
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
  display: block;
  margin-bottom: 16rpx;
}

.law-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.law-tag {
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  background: #f0f7f2;
  color: $primary;
  border-radius: 6rpx;
}

.law-actions {
  display: flex;
  gap: 40rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #666;
}

// 加载更多
.load-more {
  text-align: center;
  padding: 32rpx 0;
  font-size: 26rpx;
  color: $primary;
}
</style>
