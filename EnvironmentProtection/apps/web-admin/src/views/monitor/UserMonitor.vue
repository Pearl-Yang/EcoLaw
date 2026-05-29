<template>
  <div class="user-monitor">
    <!-- Canvas 动态背景 -->
    <canvas ref="bgCanvas" class="bg-canvas" />
    
    <!-- 顶部标题栏 -->
    <header class="monitor-header">
      <div class="header-left">
        <div class="live-indicator">
          <span class="live-dot"></span>
          <span class="live-text">实时监控</span>
        </div>
        <div class="time-display">
          <span>{{ currentTime }}</span>
          <span class="date">{{ currentDate }}</span>
        </div>
      </div>
      
      <div class="header-center">
        <h1 class="main-title">用户行为实时监控大屏</h1>
        <p class="sub-title">用户操作 · 注册登录 · 功能使用 全链路可视化监测</p>
      </div>
      
      <div class="header-right">
        <div class="stats-summary">
          <div class="stat-item">
            <span class="stat-value">{{ stats.onlineUsers }}</span>
            <span class="stat-label">在线</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.todayLogins }}</span>
            <span class="stat-label">今日登录</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.todayRegisters }}</span>
            <span class="stat-label">今日注册</span>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn" @click="toggleFullScreen">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M8 3H5a2 2 0 0 0-2 2v3m18 0V5a2 2 0 0 0-2-2h-3m0 18h3a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2h3"/>
            </svg>
          </button>
          <button class="action-btn" @click="goBack">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="m15 18-6-6 6-6"/>
            </svg>
          </button>
        </div>
      </div>
    </header>

    <!-- KPI 统计卡片 -->
    <section class="kpi-section">
      <div class="kpi-grid">
        <div class="kpi-card kpi-primary">
          <div class="kpi-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">平台用户总数</span>
            <span class="kpi-value">{{ formatNumber(stats.totalUsers) }}</span>
            <span class="kpi-trend up">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="18 15 12 9 6 15"/>
              </svg>
              ↑ {{ stats.userGrowth.month }}%
            </span>
          </div>
        </div>
        
        <div class="kpi-card kpi-success">
          <div class="kpi-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">当前在线用户</span>
            <span class="kpi-value online">{{ stats.onlineUsers }}</span>
            <span class="kpi-trend">
              <span class="pulse-dot"></span>
              实时更新
            </span>
          </div>
        </div>
        
        <div class="kpi-card kpi-info">
          <div class="kpi-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
            </svg>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">总访问量</span>
            <span class="kpi-value">{{ formatNumber(stats.totalPageViews) }}</span>
            <span class="kpi-trend up">↑ {{ stats.totalPageViews - 20000 }}</span>
          </div>
        </div>
        
        <div class="kpi-card kpi-warning">
          <div class="kpi-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">API 调用次数</span>
            <span class="kpi-value">{{ formatNumber(stats.totalApiCalls) }}</span>
            <span class="kpi-trend up">↑ 活跃</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：实时活动流 -->
      <aside class="panel-left">
        <div class="panel live-feed-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon live"></span>
              <span>实时活动流</span>
            </div>
            <span class="activity-count">每3秒更新</span>
          </div>
          <div class="live-feed" ref="feedContainer">
            <div class="feed-list">
              <TransitionGroup name="feed">
                <div 
                  v-for="activity in activities" 
                  :key="activity.id" 
                  class="feed-item"
                  :style="{ '--accent-color': activity.actionColor }"
                >
                  <div class="feed-avatar" :style="{ background: getRoleGradient(activity.role) }">
                    {{ activity.nickname?.slice(0, 1) || 'U' }}
                  </div>
                  <div class="feed-content">
                    <div class="feed-header">
                      <span class="feed-user">{{ activity.nickname }}</span>
                      <span class="feed-role" :style="{ color: getRoleColor(activity.role) }">
                        {{ getRoleName(activity.role) }}
                      </span>
                      <span class="feed-action">{{ activity.actionIcon }}</span>
                    </div>
                    <div class="feed-detail">{{ activity.detail }}</div>
                    <div class="feed-meta">
                      <span class="feed-time">{{ activity.time }}</span>
                      <span class="feed-page">{{ activity.page }}</span>
                      <span class="feed-device">{{ activity.device }}</span>
                    </div>
                  </div>
                </div>
              </TransitionGroup>
            </div>
          </div>
        </div>
        
        <!-- 操作类型分布 -->
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon blue"></span>
              <span>操作类型分布</span>
            </div>
          </div>
          <div ref="actionChartRef" class="chart-container"></div>
        </div>
      </aside>

      <!-- 中间：主监控区域 -->
      <main class="panel-center">
        <!-- 角色分布 -->
        <div class="panel role-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon green"></span>
              <span>用户角色分布</span>
            </div>
            <span class="total-hint">总计: {{ stats.totalUsers }} 人</span>
          </div>
          <div class="role-grid">
            <div 
              v-for="(count, role) in sortedRoleDistribution" 
              :key="role"
              class="role-item"
              :style="{ '--role-color': getRoleColor(role) }"
            >
              <div class="role-bar" :style="{ width: `${(count / stats.totalUsers) * 100}%` }"></div>
              <div class="role-info">
                <span class="role-name">{{ getRoleName(role) }}</span>
                <span class="role-percent">{{ ((count / stats.totalUsers) * 100).toFixed(1) }}%</span>
                <span class="role-count">{{ count }} 人</span>
              </div>
            </div>
          </div>
          <div ref="roleChartRef" class="chart-container"></div>
        </div>
        
        <!-- 24小时活跃趋势 -->
        <div class="panel trend-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon purple"></span>
              <span>24小时活跃趋势</span>
            </div>
            <div class="trend-legend">
              <span class="legend-item">
                <span class="legend-dot" style="background: #10B981"></span>
                登录量
              </span>
              <span class="legend-item">
                <span class="legend-dot" style="background: #3B82F6"></span>
                页面浏览
              </span>
            </div>
          </div>
          <div ref="trendChartRef" class="chart-container tall"></div>
        </div>

        <!-- 用户增长趋势 - 线性图表 -->
        <div class="panel user-trend-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon green"></span>
              <span>近30天用户增长趋势</span>
            </div>
            <div class="trend-legend">
              <span class="legend-item">
                <span class="legend-dot" style="background: #10B981"></span>
                累计用户
              </span>
              <span class="legend-item">
                <span class="legend-dot" style="background: #3B82F6"></span>
                新增用户
              </span>
              <span class="legend-item">
                <span class="legend-dot" style="background: #F59E0B"></span>
                活跃用户
              </span>
            </div>
          </div>
          <div ref="userTrendChartRef" class="chart-container tall"></div>
        </div>
      </main>

      <!-- 右侧：区域分布和统计 -->
      <aside class="panel-right">
        <!-- 地区分布 -->
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon amber"></span>
              <span>用户地区分布</span>
            </div>
          </div>
          <div ref="chinaMapChartRef" class="chart-container map-container"></div>
        </div>
        
        <!-- 热门操作排行 -->
        <div class="panel ranking-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon red"></span>
              <span>热门操作趋势</span>
            </div>
          </div>
          <div ref="actionTrendChartRef" class="chart-container"></div>
        </div>
        
        <!-- 最新注册用户 -->
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="panel-icon teal"></span>
              <span>最新注册用户</span>
            </div>
          </div>
          <div class="new-users">
            <div 
              v-for="user in latestUsers" 
              :key="user.id"
              class="new-user-item"
            >
              <div class="user-avatar-sm" :style="{ background: getRoleGradient(user.role) }">
                {{ user.nickname?.slice(0, 1) || 'U' }}
              </div>
              <div class="user-info">
                <span class="user-name">{{ user.nickname }}</span>
                <span class="user-org">{{ user.organizationName }}</span>
              </div>
              <span class="user-time">{{ user.registerTime }}</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { 
  generateActivity, 
  generateInitialActivities, 
  getUserStats, 
  getRoleName, 
  getRoleColor,
  mockUsers,
  actionTypes 
} from '@/utils/mockActivity.js'
import screenfull from 'screenfull'

defineOptions({ name: 'UserMonitor' })

const router = useRouter()

// Canvas 背景
const bgCanvas = ref(null)

// 引用
const feedContainer = ref(null)
const actionChartRef = ref(null)
const roleChartRef = ref(null)
const trendChartRef = ref(null)
const userTrendChartRef = ref(null)
const actionTrendChartRef = ref(null)
const chinaMapChartRef = ref(null)

// Charts
let actionChart = null
let roleChart = null
let trendChart = null
let userTrendChart = null
let actionTrendChart = null
let chinaMapChart = null

// 时间和活动
const currentTime = ref('')
const currentDate = ref('')
let clockTimer = null

// 活动流
const activities = ref([])
let activityTimer = null

// 统计数据
const stats = reactive({
  totalUsers: 0,
  onlineUsers: 0,
  todayLogins: 0,
  todayRegisters: 0,
  totalLogins: 0,
  totalPageViews: 0,
  totalApiCalls: 0,
  roleDistribution: {},
  userGrowth: { week: 0, month: 0 }
})

// 最新用户
const latestUsers = ref([])

// 排序后的角色分布（普通用户最多放前面）
const sortedRoleDistribution = computed(() => {
  const roleOrder = ['user', 'enterprise', 'government', 'law_firm', 'super_admin']
  const distribution = {}
  roleOrder.forEach(role => {
    if (stats.roleDistribution[role] !== undefined) {
      distribution[role] = stats.roleDistribution[role]
    }
  })
  return distribution
})

// 格式化数字
function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num?.toLocaleString() || '0'
}

// 获取角色渐变
function getRoleGradient(role) {
  const gradients = {
    super_admin: 'linear-gradient(135deg, #e74c3c, #c0392b)',
    government: 'linear-gradient(135deg, #3498db, #2980b9)',
    enterprise: 'linear-gradient(135deg, #27ae60, #229954)',
    law_firm: 'linear-gradient(135deg, #9b59b6, #8e44ad)',
    user: 'linear-gradient(135deg, #95a5a6, #7f8c8d)'
  }
  return gradients[role] || gradients.user
}

// 更新时间
function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

// 添加新活动
function addActivity() {
  const newActivity = generateActivity()
  activities.value.unshift(newActivity)
  
  // 保持最多显示30条
  if (activities.value.length > 30) {
    activities.value.pop()
  }
  
  // 更新在线人数
  stats.onlineUsers = Math.floor(stats.totalUsers * 0.3) + Math.floor(Math.random() * 10)
}

// 初始化数据
function initData() {
  const userStats = getUserStats()
  Object.assign(stats, userStats)
  
  // 初始化活动
  activities.value = generateInitialActivities(20)
  
  // 生成最新用户
  latestUsers.value = mockUsers.slice(0, 5).map((user, index) => ({
    ...user,
    registerTime: `${Math.floor(Math.random() * 23)}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`
  }))
}

// 初始化图表
function initCharts() {
  nextTick(() => {
    initActionChart()
    initRoleChart()
    initTrendChart()
    initUserTrendChart()
    initActionTrendChart()
    initChinaMapChart()
  })
}

// 操作类型饼图
function initActionChart() {
  if (!actionChartRef.value) return
  actionChart = echarts.init(actionChartRef.value)
  
  const actionData = Object.values(getUserStats().actionStats).map(a => ({
    name: a.label,
    value: a.count,
    itemStyle: { color: a.color }
  }))
  
  actionChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontWeight: 'bold' }
      },
      data: actionData
    }]
  })
}

// 角色饼图
function initRoleChart() {
  if (!roleChartRef.value) return
  roleChart = echarts.init(roleChartRef.value)
  
  const roleData = Object.entries(stats.roleDistribution).map(([role, count]) => ({
    name: getRoleName(role),
    value: count,
    itemStyle: { color: getRoleColor(role) }
  }))
  
  roleChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' }
    },
    series: [{
      type: 'pie',
      radius: ['30%', '60%'],
      center: ['50%', '50%'],
      itemStyle: { borderRadius: 8, borderColor: '#1e293b', borderWidth: 2 },
      label: { 
        show: true,
        formatter: '{b}\n{d}%',
        color: '#94a3b8',
        fontSize: 11
      },
      labelLine: {
        lineStyle: { color: '#64748b' }
      },
      data: roleData
    }]
  })
}

// 24小时趋势图
function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  
  const hourlyData = getUserStats().hourlyStats
  
  trendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' }
    },
    legend: {
      data: ['登录量', '页面浏览'],
      bottom: 0,
      textStyle: { color: '#64748b', fontSize: 11 }
    },
    grid: { left: 40, right: 16, top: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: hourlyData.map(h => h.label),
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b', fontSize: 10, interval: 3 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b', fontSize: 10 }
    },
    series: [
      {
        name: '登录量',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 2, color: '#10B981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.2)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' }
          ])
        },
        data: hourlyData.map(h => h.logins)
      },
      {
        name: '页面浏览',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 2, color: '#3B82F6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.15)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' }
          ])
        },
        data: hourlyData.map(h => h.pageViews)
      }
    ]
  })
}

// 用户增长趋势线性图
function initUserTrendChart() {
  if (!userTrendChartRef.value) return
  userTrendChart = echarts.init(userTrendChartRef.value)
  
  const trendData = getUserStats().userTrendStats
  const maxTotal = Math.max(...trendData.map(t => t.totalUsers))
  const minActive = Math.min(...trendData.map(t => t.activeUsers))
  
  userTrendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' }
    },
    legend: {
      data: ['累计用户', '活跃用户', '新增用户'],
      bottom: 0,
      textStyle: { color: '#64748b', fontSize: 10 }
    },
    grid: { 
      left: 15, 
      right: 15, 
      top: 10, 
      bottom: 38,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.map(t => t.label),
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94a3b8', fontSize: 9, interval: 4 },
      boundaryGap: false
    },
    yAxis: [
      {
        type: 'value',
        name: '用户数',
        nameTextStyle: { color: '#64748b', fontSize: 9 },
        splitLine: { lineStyle: { color: '#334155' } },
        axisLabel: { color: '#94a3b8', fontSize: 9 },
        min: minActive - 50,
        max: maxTotal + 100
      }
    ],
    series: [
      {
        name: '累计用户',
        type: 'line',
        yAxisIndex: 0,
        smooth: 0.4,
        symbol: 'none',
        lineStyle: { width: 3, color: '#10B981' },
        itemStyle: { color: '#10B981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' }
          ])
        },
        data: trendData.map(t => t.totalUsers)
      },
      {
        name: '活跃用户',
        type: 'line',
        yAxisIndex: 0,
        smooth: 0.4,
        symbol: 'none',
        lineStyle: { width: 2, color: '#F59E0B' },
        itemStyle: { color: '#F59E0B' },
        data: trendData.map(t => t.activeUsers)
      },
      {
        name: '新增用户',
        type: 'line',
        yAxisIndex: 0,
        smooth: 0.4,
        symbol: 'none',
        lineStyle: { width: 2, color: '#3B82F6' },
        itemStyle: { color: '#3B82F6' },
        data: trendData.map(t => t.newUsers)
      }
    ]
  })
}

// 热门操作趋势图
function initActionTrendChart() {
  if (!actionTrendChartRef.value) return
  actionTrendChart = echarts.init(actionTrendChartRef.value)
  
  const actionStats = getUserStats().actionStats
  const topActions = Object.values(actionStats)
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)
  
  // 生成7天的模拟趋势数据
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const seriesData = topActions.map(action => ({
    name: action.label,
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 4,
    lineStyle: { width: 2, color: action.color },
    itemStyle: { color: action.color },
    data: days.map(() => Math.floor(action.count * (0.7 + Math.random() * 0.6)))
  }))
  
  actionTrendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b' }
    },
    legend: {
      data: topActions.map(a => a.label),
      bottom: 0,
      textStyle: { color: '#64748b', fontSize: 10 }
    },
    grid: { left: 40, right: 10, top: 10, bottom: 40 },
    xAxis: {
      type: 'category',
      data: days,
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#64748b', fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94a3b8', fontSize: 10 }
    },
    series: seriesData
  })
}

// 中国地图
function initChinaMapChart() {
  if (!chinaMapChartRef.value) return
  chinaMapChart = echarts.init(chinaMapChartRef.value)
  
  const regionData = getUserStats().regionStats
  
  // 使用 fetch 加载中国地图 GeoJSON
  fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
    .then(response => response.json())
    .then(chinaJson => {
      echarts.registerMap('china', chinaJson)
      chinaMapChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} 人',
          backgroundColor: 'rgba(30, 41, 59, 0.95)',
          borderColor: '#475569',
          textStyle: { color: '#e2e8f0' }
        },
        visualMap: {
          min: 0,
          max: Math.max(...regionData.map(r => r.value)),
          left: 'left',
          top: 'bottom',
          text: ['高', '低'],
          textStyle: { color: '#94a3b8' },
          inRange: { color: ['#1e3a5f', '#10B981'] },
          calculable: true
        },
        geo: {
          map: 'china',
          roam: true,
          zoom: 1.2,
          label: {
            show: false
          },
          itemStyle: {
            borderColor: '#475569',
            borderWidth: 1,
            areaColor: '#1e293b'
          },
          emphasis: {
            label: { show: true, color: '#fff' },
            itemStyle: { areaColor: '#334155' }
          }
        },
        series: [{
          name: '用户分布',
          type: 'map',
          map: 'china',
          geoIndex: 0,
          data: regionData,
          emphasis: {
            itemStyle: {
              areaColor: '#334155'
            }
          }
        }]
      })
    })
    .catch(err => {
      // 如果加载失败，使用备用柱状图
      chinaMapChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          backgroundColor: 'rgba(30, 41, 59, 0.95)',
          textStyle: { color: '#e2e8f0' }
        },
        grid: { left: 50, right: 10, top: 10, bottom: 10 },
        xAxis: {
          type: 'value',
          splitLine: { lineStyle: { color: '#334155' } },
          axisLabel: { show: false }
        },
        yAxis: {
          type: 'category',
          data: regionData.map(r => r.name).reverse(),
          axisLine: { show: false },
          axisLabel: { color: '#94a3b8', fontSize: 10 }
        },
        series: [{
          type: 'bar',
          data: regionData.map(r => ({
            value: r.value,
            itemStyle: { color: r.itemStyle.color }
          })).reverse(),
          barWidth: 12
        }]
      })
    })
}

// 调整图表大小
function resizeCharts() {
  actionChart?.resize()
  roleChart?.resize()
  trendChart?.resize()
  userTrendChart?.resize()
  actionTrendChart?.resize()
  chinaMapChart?.resize()
}

// 全屏切换
function toggleFullScreen() {
  if (screenfull.isEnabled) {
    screenfull.toggle(document.documentElement)
  }
}

// 返回
function goBack() {
  router.push('/dashboard/workbench')
}

onMounted(() => {
  // 初始化时间
  updateTime()
  clockTimer = setInterval(updateTime, 1000)
  
  // 初始化数据
  initData()
  
  // 初始化图表
  initCharts()
  
  // 开始活动流更新
  activityTimer = setInterval(addActivity, 3000)
  
  // 每30秒更新统计
  setInterval(() => {
    const userStats = getUserStats()
    stats.todayLogins = userStats.todayLogins
    stats.todayRegisters = userStats.todayRegisters
  }, 30000)
  
  // 监听窗口大小变化
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
  if (activityTimer) clearInterval(activityTimer)
  window.removeEventListener('resize', resizeCharts)
  
  actionChart?.dispose()
  roleChart?.dispose()
  trendChart?.dispose()
  userTrendChart?.dispose()
  actionTrendChart?.dispose()
  chinaMapChart?.dispose()
})
</script>

<style lang="scss" scoped>
.user-monitor {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  color: #e2e8f0;
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  padding: 16px 20px 24px;
  overflow-x: hidden;
}

.bg-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

// ============ 头部 ============
.monitor-header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(148, 163, 184, 0.1);
  border-radius: 16px;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.live-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 20px;
}

.live-dot {
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

.live-text {
  font-size: 13px;
  font-weight: 600;
  color: #ef4444;
}

.time-display {
  display: flex;
  flex-direction: column;
  font-size: 18px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  color: #f8fafc;
  
  .date {
    font-size: 12px;
    color: #94a3b8;
    font-weight: 400;
  }
}

.header-center {
  text-align: center;
}

.main-title {
  margin: 0;
  font-size: clamp(18px, 2.5vw, 26px);
  font-weight: 700;
  background: linear-gradient(90deg, #10B981, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 2px;
}

.sub-title {
  margin: 6px 0 0;
  font-size: 12px;
  color: #64748b;
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stats-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 16px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #10B981;
  font-variant-numeric: tabular-nums;
}

.stat-label {
  font-size: 11px;
  color: #64748b;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: rgba(148, 163, 184, 0.2);
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 8px;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
  
  svg { width: 16px; height: 16px; }
  
  &:hover {
    background: rgba(148, 163, 184, 0.2);
    color: #e2e8f0;
  }
}

// ============ KPI 区域 ============
.kpi-section {
  position: relative;
  z-index: 10;
  margin-bottom: 16px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(148, 163, 184, 0.1);
  border-radius: 16px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    border-color: rgba(148, 163, 184, 0.2);
  }
}

.kpi-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  svg { width: 26px; height: 26px; }
}

.kpi-primary .kpi-icon {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.kpi-success .kpi-icon {
  background: rgba(16, 185, 129, 0.15);
  color: #10B981;
}

.kpi-info .kpi-icon {
  background: rgba(59, 130, 246, 0.15);
  color: #3B82F6;
}

.kpi-warning .kpi-icon {
  background: rgba(245, 158, 11, 0.15);
  color: #F59E0B;
}

.kpi-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.kpi-label {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  color: #f8fafc;
  font-variant-numeric: tabular-nums;
  
  &.online {
    color: #10B981;
    text-shadow: 0 0 20px rgba(16, 185, 129, 0.5);
  }
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
  
  svg { width: 14px; height: 14px; }
  
  &.up {
    color: #10B981;
  }
  
  .pulse-dot {
    width: 6px;
    height: 6px;
    background: #10B981;
    border-radius: 50%;
    animation: pulse 1.5s ease-in-out infinite;
  }
}

// ============ 主要内容 ============
.main-content {
  position: relative;
  z-index: 10;
  display: grid;
  grid-template-columns: 320px 1fr 300px;
  gap: 16px;
  align-items: start;
}

.panel-left,
.panel-center,
.panel-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel {
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(148, 163, 184, 0.1);
  border-radius: 16px;
  padding: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
}

.panel-icon {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  
  &.live { background: #ef4444; animation: pulse 1.5s infinite; }
  &.green { background: #10B981; }
  &.blue { background: #3B82F6; }
  &.purple { background: #8B5CF6; }
  &.amber { background: #F59E0B; }
  &.red { background: #ef4444; }
  &.teal { background: #14B8A6; }
}

.activity-count {
  font-size: 11px;
  color: #64748b;
}

.total-hint {
  font-size: 12px;
  color: #10B981;
  font-weight: 600;
}

// ============ 实时活动流 ============
.live-feed-panel {
  max-height: 1000px;
  overflow: hidden;
}

.live-feed {
  max-height: 560px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(148, 163, 184, 0.3);
    border-radius: 2px;
  }
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feed-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: rgba(51, 65, 85, 0.4);
  border-radius: 12px;
  border-left: 3px solid var(--accent-color);
  transition: all 0.3s;
  
  &:hover {
    background: rgba(51, 65, 85, 0.6);
    transform: translateX(4px);
  }
}

.feed-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
}

.feed-content {
  flex: 1;
  min-width: 0;
}

.feed-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.feed-user {
  font-size: 13px;
  font-weight: 600;
  color: #f8fafc;
}

.feed-role {
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(148, 163, 184, 0.15);
  border-radius: 4px;
}

.feed-action {
  font-size: 14px;
  margin-left: auto;
}

.feed-detail {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 6px;
  line-height: 1.4;
}

.feed-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: #64748b;
}

.feed-time {
  font-variant-numeric: tabular-nums;
}

// Transition
.feed-enter-active {
  transition: all 0.5s ease;
}

.feed-leave-active {
  transition: all 0.3s ease;
  position: absolute;
}

.feed-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.feed-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// ============ 图表容器 ============
.chart-container {
  width: 100%;
  flex: 1;
  min-height: 200px;
  
  &.tall {
    min-height: 260px;
  }
  
  &.map-container {
    height: 280px;
  }
}

// ============ 角色分布 ============
.role-panel {
  flex: 1;
}

.user-trend-panel {
  flex: 1;
  min-height: 280px;
  display: flex;
  flex-direction: column;
}

.role-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.role-item {
  position: relative;
  height: 36px;
  background: rgba(51, 65, 85, 0.5);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.role-bar {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: var(--role-color);
  opacity: 0.3;
  transition: width 1s ease;
}

.role-info {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 12px;
  height: 100%;
}

.role-name {
  font-size: 12px;
  color: #e2e8f0;
  min-width: 70px;
}

.role-percent {
  font-size: 11px;
  color: #64748b;
  min-width: 45px;
  text-align: center;
}

.role-count {
  font-size: 13px;
  font-weight: 600;
  color: #f8fafc;
  min-width: 50px;
  text-align: right;
}

// ============ 操作排行 ============
.action-ranking {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ranking-index {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  background: rgba(148, 163, 184, 0.2);
  color: #94a3b8;
  
  &.gold { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: #fff; }
  &.silver { background: linear-gradient(135deg, #94a3b8, #64748b); color: #fff; }
  &.bronze { background: linear-gradient(135deg, #d97706, #b45309); color: #fff; }
}

.ranking-icon {
  font-size: 14px;
}

.ranking-label {
  font-size: 12px;
  color: #94a3b8;
  min-width: 60px;
}

.ranking-bar {
  flex: 1;
  height: 6px;
  background: rgba(148, 163, 184, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.ranking-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.5s ease;
}

.ranking-count {
  font-size: 12px;
  font-weight: 600;
  color: #f8fafc;
  min-width: 50px;
  text-align: right;
  font-variant-numeric: tabular-nums;
}

// ============ 最新用户 ============
.new-users {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.new-user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: rgba(51, 65, 85, 0.3);
  border-radius: 8px;
}

.user-avatar-sm {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #f8fafc;
}

.user-org {
  display: block;
  font-size: 11px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-time {
  font-size: 11px;
  color: #64748b;
  font-variant-numeric: tabular-nums;
}

// ============ 趋势图例 ============
.trend-legend {
  display: flex;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #64748b;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

// ============ 响应式 ============
@media (max-width: 1400px) {
  .main-content {
    grid-template-columns: 280px 1fr 260px;
  }
}

@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .monitor-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-center {
    order: -1;
  }
  
  .kpi-grid {
    grid-template-columns: 1fr;
  }
}
</style>
