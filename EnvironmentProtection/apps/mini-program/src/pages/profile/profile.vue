<template>
  <view class="page">
    <!-- 顶部用户信息区域 -->
    <view class="user-header">
      <view class="user-bg"></view>
      <view class="user-info">
        <view class="avatar-wrap" @click="editProfile">
          <image v-if="userInfo.avatar" class="avatar" :src="userInfo.avatar" mode="aspectFill" />
          <view v-else class="avatar-text">{{ initial }}</view>
          <view class="avatar-edit">
            <LineIcon name="camera" :size="24" color="#6b7280" />
          </view>
        </view>
        <view class="user-detail">
          <text class="user-name">{{ userInfo.nickname || '访客用户' }}</text>
          <view class="user-role">
            <text class="role-tag" :class="userInfo.role || 'guest'">{{ getRoleText(userInfo.role) }}</text>
            <text class="level-tag" v-if="userInfo.level">{{ userInfo.level }}级</text>
          </view>
          <text class="user-org" v-if="userInfo.organization">{{ userInfo.organization }}</text>
        </view>
        <view class="edit-btn" @click="editProfile">
          <LineIcon name="edit" :size="22" color="#22c55e" />
          <text>编辑</text>
        </view>
      </view>
    </view>

    <!-- 积分区域 -->
    <view class="points-card" @click="goRanking">
      <view class="points-left">
        <view class="points-icon-wrap">
          <LineIcon name="award" :size="36" color="#eab308" />
        </view>
        <view class="points-info">
          <text class="points-label">我的积分</text>
          <text class="points-num">{{ userInfo.points ?? 0 }}</text>
        </view>
      </view>
      <view class="points-right">
        <text class="ranking-btn">我的排行</text>
        <LineIcon name="chevron-right" :size="24" color="#9ca3af" />
      </view>
    </view>

    <!-- 用户统计 -->
    <view class="stats-row">
      <view class="stat-item" @click="goMyLearn">
        <LineIcon name="clock" :size="28" color="#22c55e" />
        <text class="stat-num">{{ userStats.learningHours || 68 }}</text>
        <text class="stat-label">学习时长(h)</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @click="goMyTasks">
        <LineIcon name="check-circle" :size="28" color="#22c55e" />
        <text class="stat-num">{{ userStats.completedTasks || 23 }}</text>
        <text class="stat-label">完成任务</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item" @click="goMyPoints">
        <LineIcon name="star" :size="28" color="#eab308" />
        <text class="stat-num stat-gold">{{ userStats.totalPoints || 1250 }}</text>
        <text class="stat-label">累计积分</text>
      </view>
    </view>

    <!-- 会员卡区域 -->
    <view class="member-card" @click="goMemberCenter">
      <view class="member-left">
        <view class="member-icon-wrap">
          <LineIcon name="leaf" :size="32" color="#22c55e" />
        </view>
        <view class="member-info">
          <text class="member-title">绿法通会员</text>
          <text class="member-desc">专享更多权益与积分加倍</text>
        </view>
      </view>
      <view class="member-right">
        <text class="member-level">{{ userInfo.memberLevel || '普通会员' }}</text>
        <LineIcon name="chevron-right" :size="24" color="#9ca3af" />
      </view>
    </view>

    <!-- 角色专属功能入口 -->
    <view class="role-services" v-if="roleServices.length > 0">
      <view class="role-section-title">
        <text>{{ getRoleServiceTitle() }}</text>
      </view>
      <view class="role-service-grid">
        <view
          v-for="service in roleServices"
          :key="service.id"
          class="role-service-item"
          @click="goRoleService(service)"
        >
          <view class="role-service-icon" :style="{ background: service.bgColor }">
            <LineIcon :name="service.icon" :size="32" :color="service.iconColor || '#22c55e'" />
          </view>
          <text class="role-service-name">{{ service.name }}</text>
        </view>
      </view>
    </view>

    <!-- 功能入口区域 -->
    <view class="service-section">
      <view class="section-title">
        <text class="title-text">常用服务</text>
      </view>
      <view class="service-grid">
        <view
          v-for="service in commonServices"
          :key="service.id"
          class="service-item"
          @click="goService(service)"
        >
          <view class="service-icon" :style="{ background: service.bgColor }">
            <LineIcon :name="service.icon" :size="32" :color="service.iconColor || '#22c55e'" />
          </view>
          <text class="service-name">{{ service.name }}</text>
          <view v-if="service.badge" class="service-badge">{{ service.badge }}</view>
        </view>
      </view>
    </view>

    <!-- 我的收藏 -->
    <view class="my-collection">
      <view class="collection-item" v-for="item in myCollections" :key="item.id" @click="goCollection(item)">
        <view class="coll-icon-wrap">
          <LineIcon :name="item.icon" :size="28" color="#22c55e" />
        </view>
        <text class="collection-name">{{ item.name }}</text>
        <view class="coll-right">
          <text class="collection-count">{{ item.count }}</text>
          <LineIcon name="chevron-right" :size="20" color="#d1d5db" />
        </view>
      </view>
    </view>

    <!-- 工具卡片 -->
    <view class="tools-section">
      <view class="tools-card">
        <view
          v-for="tool in tools"
          :key="tool.id"
          class="tool-item"
          @click="goTool(tool)"
        >
          <view class="tool-icon">
            <LineIcon :name="tool.icon" :size="28" color="#6b7280" />
          </view>
          <text class="tool-name">{{ tool.name }}</text>
          <LineIcon name="chevron-right" :size="24" color="#d1d5db" />
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">
      <LineIcon name="log-out" :size="28" color="#ef4444" />
      <text>退出登录</text>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>V1.0.0 绿法通 · 白色污染治理AI智慧普法平台</text>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { userStore } from '../../store/user.js'
import { api } from '../../api/index.js'

// 用户信息
const userInfo = ref({
  avatar: '',
  nickname: '张三',
  role: 'grass_roots',
  level: 3,
  points: 1250,
  tasks: 15,
  certificates: 2,
  memberLevel: '银卡会员',
  organization: 'XX社区居委会'
})

const userStats = ref({
  learningHours: 68,
  completedTasks: 23,
  totalPoints: 1250
})

// 角色专属服务
const roleServices = computed(() => {
  const role = userInfo.value.role
  const services = []
  
  if (role === 'government') {
    services.push(
      { id: 'g1', name: '数据看板', icon: 'bar-chart-2', path: '/pages/government/dashboard', bgColor: '#eff6ff', iconColor: '#3b82f6' },
      { id: 'g2', name: '进度督导', icon: 'clipboard-list', path: '/pages/government/supervision', bgColor: '#f0fdf4', iconColor: '#22c55e' },
      { id: 'g3', name: '考核管理', icon: 'edit', path: '/pages/government/exam', bgColor: '#fffbeb', iconColor: '#f59e0b' }
    )
  } else if (role === 'enterprise' || role === 'enterprise_employee') {
    services.push(
      { id: 'e1', name: '企业培训', icon: 'graduation-cap', path: '/pages/enterprise/training', bgColor: '#f0fdf4', iconColor: '#22c55e' },
      { id: 'e2', name: '员工管理', icon: 'users', path: '/pages/enterprise/employees', bgColor: '#eff6ff', iconColor: '#3b82f6' },
      { id: 'e3', name: '合规台账', icon: 'shield-check', path: '/pages/enterprise/compliance', bgColor: '#fffbeb', iconColor: '#f59e0b' },
      { id: 'e4', name: 'ESG报告', icon: 'file-text', path: '/pages/enterprise/esg', bgColor: '#faf5ff', iconColor: '#a855f7' }
    )
  } else if (role === 'grass_roots') {
    services.push(
      { id: 'b1', name: '任务执行', icon: 'clipboard-list', path: '/pages/task/task', bgColor: '#f0fdf4', iconColor: '#22c55e' },
      { id: 'b2', name: '积分激励', icon: 'award', path: '/pages/points/points', bgColor: '#fffbeb', iconColor: '#eab308' },
      { id: 'b3', name: '证书荣誉', icon: 'star', path: '/pages/certificate/certificate', bgColor: '#fff8e1', iconColor: '#d97706' }
    )
  } else if (role === 'law_firm') {
    services.push(
      { id: 'l1', name: '案例管理', icon: 'bookmark', path: '/pages/law-firm/cases', bgColor: '#eff6ff', iconColor: '#3b82f6' },
      { id: 'l2', name: '客户服务', icon: 'users', path: '/pages/law-firm/clients', bgColor: '#f0fdf4', iconColor: '#22c55e' },
      { id: 'l3', name: '咨询对接', icon: 'message-circle', path: '/pages/law-firm/consult', bgColor: '#fffbeb', iconColor: '#f59e0b' }
    )
  }
  
  return services
})

function syncUserFromStore() {
  userStore.init()
  const u = userStore.userInfo
  if (u) {
    if (u.nickname || u.username) userInfo.value.nickname = u.nickname || u.username
    if (u.role) userInfo.value.role = u.role
    if (u.avatar) userInfo.value.avatar = u.avatar
    if (u.points != null) userInfo.value.points = u.points
  }
}

/** 有 token 时拉取服务端资料并写回 store（静默失败则保留本地/默认展示） */
async function refreshUserFromServer() {
  userStore.init()
  if (!userStore.token) return
  try {
    const u = await api.auth.getUserInfo({ showLoading: false })
    if (u && typeof u === 'object') {
      const merged = { ...userStore.userInfo, ...u }
      userStore.setUserInfo(merged)
      syncUserFromStore()
    }
  } catch {
    syncUserFromStore()
  }
}

onShow(() => {
  syncUserFromStore()
  refreshUserFromServer()
})

// 计算属性
const initial = computed(() => {
  const name = userInfo.value.nickname || '访客'
  return name.slice(0, 1)
})

// 常用服务
const commonServices = [
  { id: '1', name: '学习档案', icon: 'book-open', bgColor: '#f0fdf4', iconColor: '#22c55e', badge: '' },
  { id: '2', name: '积分商城', icon: 'award', bgColor: '#fffbeb', iconColor: '#eab308', badge: 'new' },
  { id: '3', name: '勋章墙', icon: 'star', bgColor: '#fff8e1', iconColor: '#d97706', badge: '' },
  { id: '4', name: '消息通知', icon: 'bell', bgColor: '#eff6ff', iconColor: '#3b82f6', badge: '3' },
  { id: '5', name: '我的举报', icon: 'map-pin', bgColor: '#fef2f2', iconColor: '#ef4444', badge: '' },
  { id: '6', name: '证书下载', icon: 'download', bgColor: '#f0f9ff', iconColor: '#0284c7', badge: '' }
]

// 我的收藏
const myCollections = [
  { id: '1', name: '收藏法规', icon: 'bookmark', count: 12 },
  { id: '2', name: '收藏课程', icon: 'heart', count: 8 },
  { id: '3', name: '浏览历史', icon: 'clock', count: 36 }
]

// 工具
const tools = [
  { id: '1', name: '账号与安全', icon: 'shield', path: '/pages/settings/security' },
  { id: '2', name: '隐私设置', icon: 'lock', path: '/pages/settings/privacy' },
  { id: '3', name: '消息推送', icon: 'bell', path: '/pages/settings/notification' },
  { id: '4', name: '关于我们', icon: 'info', path: '/pages/settings/about' }
]

// 获取角色文本
function getRoleText(role) {
  switch (role) {
    case 'government': return '政府监管端'
    case 'grass_roots': return '法律明白人'
    case 'enterprise_employee': return '企业员工'
    case 'law_firm': return '律所服务端'
    case 'enterprise': return '企业合规端'
    case 'general_user': return '普通用户'
    case 'legal_person': return '法律明白人'
    case 'lawyer': return '律师'
    default: return '普通用户'
  }
}

// 获取角色专属服务标题
function getRoleServiceTitle() {
  switch (userInfo.value.role) {
    case 'government': return '政府监管端'
    case 'enterprise':
    case 'enterprise_employee': return '企业端'
    case 'grass_roots': return '法律明白人'
    case 'law_firm': return '律所端'
    default: return '专属服务'
  }
}

// 编辑个人资料
function editProfile() {
  uni.showToast({ title: '编辑资料功能待开发', icon: 'none' })
}

// 进入排行榜
function goRanking() {
  uni.navigateTo({ url: '/pages/ranking/ranking' })
}

// 进入会员中心
function goMemberCenter() {
  uni.showToast({ title: '会员中心功能待开发', icon: 'none' })
}

// 进入服务
function goService(service) {
  uni.showToast({ title: service.name, icon: 'none' })
}

// 进入收藏
function goCollection(item) {
  uni.showToast({ title: item.name + '功能待开发', icon: 'none' })
}

// 进入工具
function goTool(tool) {
  uni.showToast({ title: tool.name, icon: 'none' })
}

// 角色专属服务
function goRoleService(service) {
  uni.navigateTo({ url: service.path })
}

// 统计跳转
function goMyLearn() {
  uni.switchTab({ url: '/pages/learn/learn' })
}

function goMyTasks() {
  uni.switchTab({ url: '/pages/task/task' })
}

function goMyPoints() {
  goRanking()
}

// 退出登录
function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出当前账号吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.clear()
        uni.clearStorageSync()
        uni.reLaunch({ url: '/pages/login/login' })
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
  padding-bottom: 140rpx;
}

// 用户头部
.user-header {
  position: relative;
  padding: 0 24rpx 0;
}

.user-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 240rpx;
  background: #ffffff;
  border-bottom: 1rpx solid #e5e7eb;
}

.user-info {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 32rpx;
  padding-bottom: 20rpx;
}

.avatar-wrap {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  margin-bottom: 16rpx;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 4rpx solid #e5e7eb;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.avatar-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #d1fae5, #86efac);
  text-align: center;
  line-height: 140rpx;
  font-size: 56rpx;
  font-weight: 700;
  color: #15803d;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e5e7eb;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.user-detail {
  text-align: center;
  margin-bottom: 16rpx;
}

.user-name {
  font-size: 36rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
  margin-bottom: 12rpx;
}

.user-role {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.role-tag,
.level-tag {
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  background: #f0fdf4;
  color: $primary-dark;
  border: 1rpx solid rgba(34, 197, 94, 0.25);
}

.user-org {
  font-size: 22rpx;
  color: $text-secondary;
}

.role-tag.legal_person { background: #fffbeb; color: #92400e; border-color: rgba(234,179,8,0.3); }
.role-tag.enterprise { background: #eff6ff; color: #1d4ed8; border-color: rgba(59,130,246,0.3); }
.role-tag.government { background: #f0fdf4; color: #15803d; border-color: rgba(34,197,94,0.3); }
.role-tag.grass_roots { background: #faf5ff; color: #6b21a8; border-color: rgba(168,85,247,0.3); }
.role-tag.enterprise_employee { background: #eff6ff; color: #1d4ed8; border-color: rgba(59,130,246,0.3); }
.role-tag.law_firm { background: #faf5ff; color: #7c3aed; border-color: rgba(124,58,237,0.3); }
.role-tag.general_user { background: #f0fdf4; color: #15803d; border-color: rgba(34,197,94,0.3); }
.level-tag { background: #f9fafb; color: #4b5563; border-color: #e5e7eb; }

.edit-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 10rpx 24rpx;
  background: #f0fdf4;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: $primary;
  border: 1rpx solid rgba(34, 197, 94, 0.3);
}

// 积分卡片
.points-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -32rpx 24rpx 20rpx;
  padding: 24rpx 28rpx;
  background: #ffffff;
  border-radius: 20rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.06);
}

.points-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.points-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  background: #fffbeb;
  border: 1rpx solid rgba(234,179,8,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}

.points-info {
  display: flex;
  flex-direction: column;
}

.points-label {
  font-size: 22rpx;
  color: $text-secondary;
}

.points-num {
  font-size: 48rpx;
  font-weight: 700;
  color: #d97706;
}

.ranking-btn {
  font-size: 24rpx;
  color: $text-secondary;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

// 用户统计
.stats-row {
  display: flex;
  align-items: center;
  background: #fff;
  margin: 0 24rpx 20rpx;
  padding: 24rpx 40rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: $primary;
  display: block;
}

.stat-num.stat-gold {
  color: #f59e0b;
}

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-divider {
  width: 1rpx;
  height: 48rpx;
  background: #eee;
}

// 角色专属服务
.role-services {
  margin: 0 24rpx 20rpx;
  padding: 24rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.role-section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $primary-dark;
  margin-bottom: 20rpx;
}

.role-service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}

.role-service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.role-service-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.role-service-name {
  font-size: 22rpx;
  color: #333;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-num {
  font-size: 36rpx;
  font-weight: 700;
  color: $primary;
  display: block;
}

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-divider {
  width: 1rpx;
  height: 48rpx;
  background: #eee;
}

// 会员卡
.member-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 24rpx 24rpx;
  padding: 24rpx 28rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f0fdf4 50%, #d1fae5 100%);
  border-radius: 20rpx;
  border: 1rpx solid rgba(34,197,94,0.25);
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
}

.member-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.member-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  background: #ffffff;
  border: 1rpx solid rgba(34,197,94,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.member-info {
  display: flex;
  flex-direction: column;
}

.member-title {
  font-size: 30rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
  margin-bottom: 6rpx;
}

.member-desc {
  font-size: 22rpx;
  color: $text-secondary;
}

.member-right {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.member-level {
  font-size: 26rpx;
  color: $text-secondary;
}

// 服务区域
.service-section {
  padding: 0 24rpx 24rpx;
}

.section-title {
  margin-bottom: 20rpx;
}

.title-text {
  font-size: 32rpx;
  font-weight: 700;
  color: $text-primary;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.service-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.service-name {
  font-size: 24rpx;
  color: $text-primary;
}

.service-badge {
  position: absolute;
  top: 0;
  right: 16rpx;
  min-width: 32rpx;
  height: 32rpx;
  background: $primary;
  color: #fff;
  font-size: 20rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}

// 我的收藏
.my-collection {
  display: flex;
  justify-content: space-around;
  background: #fff;
  margin: 0 24rpx 24rpx;
  padding: 28rpx;
  border-radius: 20rpx;
}

.collection-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.coll-icon-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  background: #f0fdf4;
  border: 1rpx solid rgba(34,197,94,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.coll-right {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.collection-name {
  font-size: 24rpx;
  color: $text-primary;
}

.collection-count {
  font-size: 20rpx;
  color: $text-secondary;
}

// 工具卡片
.tools-section {
  padding: 0 24rpx;
}

.tools-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.tool-item {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f3f4f6;
}

.tool-item:last-child {
  border-bottom: none;
}

.tool-icon {
  margin-right: 20rpx;
  width: 56rpx;
  height: 56rpx;
  border-radius: 14rpx;
  background: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tool-name {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
}

// 退出登录
.logout-btn {
  margin: 32rpx 24rpx;
  padding: 28rpx;
  background: #ffffff;
  border-radius: 16rpx;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
}

.logout-btn text {
  font-size: 28rpx;
  color: #ef4444;
}

// 版本信息
.version-info {
  text-align: center;
  padding-bottom: 60rpx;
}

.version-info text {
  font-size: 22rpx;
  color: #d1d5db;
}
</style>