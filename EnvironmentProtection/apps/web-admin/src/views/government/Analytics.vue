<template>
  <div class="analytics-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
    </div>

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="title-wrapper">
            <div class="title-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
              </svg>
            </div>
            <div>
              <h1 class="page-title">数据分析</h1>
              <p class="page-subtitle">环境法治工作全景数据概览</p>
            </div>
          </div>
        </div>
        <div class="header-right">
          <div class="time-selector">
            <el-radio-group v-model="timeRange" size="default">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="quarter">本季度</el-radio-button>
              <el-radio-button label="year">本年</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="kpi-grid">
      <div v-for="(s, index) in kpis" :key="s.label" class="kpi-card" :style="{ '--delay': index * 0.1 + 's' }">
        <div class="kpi-card-inner">
          <div class="kpi-icon-wrap" :style="{ background: s.iconBg }">
            <svg class="kpi-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <template v-if="s.icon === 'tickets'">
                <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v2z"/>
                <path d="M13 5v2"/>
                <path d="M13 17v2"/>
                <path d="M13 11v2"/>
              </template>
              <template v-else-if="s.icon === 'check'">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </template>
              <template v-else-if="s.icon === 'book'">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
              </template>
              <template v-else-if="s.icon === 'document'">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <polyline points="10 9 9 9 8 9"/>
              </template>
            </svg>
          </div>
          <div class="kpi-content">
            <div class="kpi-value">{{ s.value }}</div>
            <div class="kpi-label">{{ s.label }}</div>
          </div>
          <div class="kpi-trend" :class="s.up ? 'up' : 'down'">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline v-if="s.up" points="18 15 12 9 6 15"/>
              <polyline v-else points="6 9 12 15 18 9"/>
            </svg>
            {{ s.change }}%
          </div>
        </div>
        <div class="kpi-progress">
          <div class="progress-track">
            <div class="progress-fill" :style="{ 
              width: s.progress + '%', 
              background: s.progressGradient 
            }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="glass-card chart-card chart-main">
        <div class="card-header">
          <div class="card-title-wrap">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
            </svg>
            <h3 class="card-title">普法触达趋势</h3>
          </div>
          <div class="card-actions">
            <el-button size="small" circle>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="14" height="14">
                <circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/><circle cx="5" cy="12" r="1"/>
              </svg>
            </el-button>
          </div>
        </div>
        <div ref="trendRef" class="chart-container"></div>
      </div>
      <div class="glass-card chart-card chart-side">
        <div class="card-header">
          <div class="card-title-wrap">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21.21 15.89A10 10 0 1 1 8 2.83"/>
              <path d="M22 12A10 10 0 0 0 12 2v10z"/>
            </svg>
            <h3 class="card-title">任务类型占比</h3>
          </div>
        </div>
        <div ref="pieRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 底栏 -->
    <div class="bottom-row">
      <div class="glass-card table-card">
        <div class="card-header">
          <div class="card-title-wrap">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="20" x2="18" y2="10"/>
              <line x1="12" y1="20" x2="12" y2="4"/>
              <line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
            <h3 class="card-title">乡镇任务完成排行</h3>
          </div>
          <div class="card-actions">
            <el-button type="primary" size="small" plain>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="14" height="14">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
              导出
            </el-button>
          </div>
        </div>
        <div class="rank-table">
          <div class="rank-header">
            <span class="rank-col rank-num">排名</span>
            <span class="rank-col rank-name">乡镇</span>
            <span class="rank-col rank-done">已完成</span>
            <span class="rank-col rank-total">总数</span>
            <span class="rank-col rank-rate">完成率</span>
          </div>
          <div class="rank-body">
            <div class="rank-row" v-for="row in rankList" :key="row.rank">
              <span class="rank-col rank-num">
                <span class="rank-badge" :class="getRankClass(row.rank)">{{ row.rank }}</span>
              </span>
              <span class="rank-col rank-name">{{ row.name }}</span>
              <span class="rank-col rank-done">{{ row.done }}</span>
              <span class="rank-col rank-total">{{ row.total }}</span>
              <span class="rank-col rank-rate">
                <div class="rate-bar">
                  <div class="rate-fill" :style="{ width: row.rate + '%', background: getRateGradient(row.rate) }"></div>
                </div>
                <span class="rate-value" :style="{ color: getRateColor(row.rate) }">{{ row.rate }}%</span>
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="glass-card chart-card chart-bar">
        <div class="card-header">
          <div class="card-title-wrap">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <line x1="3" y1="9" x2="21" y2="9"/>
              <line x1="9" y1="21" x2="9" y2="9"/>
            </svg>
            <h3 class="card-title">企业合规率分布</h3>
          </div>
        </div>
        <div ref="barRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const timeRange = ref('month')

const kpis = reactive([
  { 
    label: '普法覆盖人次', 
    value: '12.8 万', 
    change: '18', 
    up: true, 
    progress: 85,
    progressGradient: 'linear-gradient(90deg, #1a4d2e, #52b788)',
    iconBg: 'linear-gradient(135deg, rgba(26,77,46,0.15), rgba(82,183,136,0.25))', 
    icon: 'tickets' 
  },
  { 
    label: '任务完成率', 
    value: '86.3%', 
    change: '5', 
    up: true, 
    progress: 86,
    progressGradient: 'linear-gradient(90deg, #2d6a4f, #52b788)',
    iconBg: 'linear-gradient(135deg, rgba(45,106,79,0.15), rgba(82,183,136,0.25))', 
    icon: 'check' 
  },
  { 
    label: '培训参与人数', 
    value: '4,560', 
    change: '12', 
    up: true, 
    progress: 92,
    progressGradient: 'linear-gradient(90deg, #40916c, #95d5b2)',
    iconBg: 'linear-gradient(135deg, rgba(82,183,136,0.15), rgba(149,213,178,0.25))', 
    icon: 'book' 
  },
  { 
    label: '合规企业数', 
    value: '328', 
    change: '2', 
    up: false, 
    progress: 78,
    progressGradient: 'linear-gradient(90deg, #6b7280, #9ca3af)',
    iconBg: 'linear-gradient(135deg, rgba(107,114,128,0.12), rgba(156,163,175,0.2))', 
    icon: 'document' 
  }
])

const rankList = reactive([
  { rank: 1, name: '龙泉镇', done: 48, total: 52, rate: 92 },
  { rank: 2, name: '凤鸣乡', done: 45, total: 50, rate: 90 },
  { rank: 3, name: '清溪村', done: 40, total: 48, rate: 83 },
  { rank: 4, name: '东华街道', done: 35, total: 46, rate: 76 },
  { rank: 5, name: '西岭镇', done: 28, total: 44, rate: 64 }
])

let trendChart = null
let pieChart = null
let barChart = null
const trendRef = ref()
const pieRef = ref()
const barRef = ref()

const eco = '#52b788'
const ecoDark = '#1a4d2e'
const ecoMid = '#2d6a4f'
const ecoLight = '#95d5b2'

const getRankClass = (rank) => {
  if (rank === 1) return 'gold'
  if (rank === 2) return 'silver'
  if (rank === 3) return 'bronze'
  return ''
}

const getRateColor = (rate) => {
  if (rate >= 80) return ecoDark
  if (rate >= 50) return '#f59e0b'
  return '#ef4444'
}

const getRateGradient = (rate) => {
  if (rate >= 80) return `linear-gradient(90deg, ${ecoDark}, ${eco})`
  if (rate >= 50) return 'linear-gradient(90deg, #f59e0b, #fbbf24)'
  return 'linear-gradient(90deg, #ef4444, #f87171)'
}

const initCharts = () => {
  if (trendRef.value) {
    trendChart = echarts.init(trendRef.value)
    trendChart.setOption({
      tooltip: { 
        trigger: 'axis',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(82,183,136,0.3)',
        borderWidth: 1,
        textStyle: { color: ecoDark },
        axisPointer: { type: 'shadow' }
      },
      legend: { 
        data: ['普法触达', '任务完成'], 
        bottom: 0,
        textStyle: { color: '#666' },
        itemWidth: 20,
        itemHeight: 8
      },
      grid: { left: '3%', right: '4%', bottom: '18%', top: '8%', containLabel: true },
      xAxis: { 
        type: 'category', 
        data: ['1月', '2月', '3月', '4月', '5月', '6月'],
        axisLine: { lineStyle: { color: 'rgba(82,183,136,0.2)' } },
        axisLabel: { color: '#666' }
      },
      yAxis: { 
        type: 'value', 
        splitLine: { lineStyle: { color: 'rgba(82,183,136,0.1)' } },
        axisLabel: { color: '#666' }
      },
      series: [
        { 
          name: '普法触达', 
          type: 'line', 
          smooth: true, 
          data: [6200, 7800, 9100, 10500, 12800, 14200], 
          areaStyle: { 
            color: { 
              type: 'linear', x: 0, y: 0, x2: 0, y2: 1, 
              colorStops: [{ offset: 0, color: eco + '55' }, { offset: 1, color: eco + '08' }] 
            } 
          }, 
          lineStyle: { color: eco, width: 3 }, 
          itemStyle: { color: eco },
          symbol: 'circle',
          symbolSize: 6
        },
        { 
          name: '任务完成', 
          type: 'line', 
          smooth: true, 
          data: [3800, 4600, 5200, 6100, 7200, 8100], 
          lineStyle: { color: ecoMid, width: 3 }, 
          itemStyle: { color: ecoMid },
          symbol: 'circle',
          symbolSize: 6
        }
      ]
    })
  }

  if (pieRef.value) {
    pieChart = echarts.init(pieRef.value)
    pieChart.setOption({
      tooltip: { 
        trigger: 'item',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(82,183,136,0.3)',
        borderWidth: 1,
        textStyle: { color: ecoDark }
      },
      legend: { 
        orient: 'vertical', 
        right: 8, 
        top: 'center', 
        itemWidth: 10, 
        itemHeight: 10,
        textStyle: { color: '#666', fontSize: 12 }
      },
      series: [{
        type: 'pie', 
        radius: ['48%', '72%'], 
        center: ['38%', '50%'],
        label: { show: true, formatter: '{b}\n{d}%', fontSize: 12, color: '#666' },
        labelLine: { lineStyle: { color: 'rgba(82,183,136,0.3)' } },
        data: [
          { value: 48, name: '政府任务', itemStyle: { color: ecoDark } },
          { value: 28, name: '企业培训', itemStyle: { color: eco } },
          { value: 15, name: '律所服务', itemStyle: { color: ecoLight } },
          { value: 9, name: '其他', itemStyle: { color: '#d8f3dc' } }
        ]
      }]
    })
  }

  if (barRef.value) {
    barChart = echarts.init(barRef.value)
    barChart.setOption({
      tooltip: { 
        trigger: 'axis',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(82,183,136,0.3)',
        borderWidth: 1,
        textStyle: { color: ecoDark },
        axisPointer: { type: 'shadow' }
      },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
      xAxis: { 
        type: 'category', 
        data: ['塑料制造', '农业合作社', '餐饮商超', '快递物流', '医疗机构'],
        axisLine: { lineStyle: { color: 'rgba(82,183,136,0.2)' } },
        axisLabel: { color: '#666', fontSize: 11 }
      },
      yAxis: { 
        type: 'value', 
        max: 100, 
        axisLabel: { formatter: '{value}%', color: '#666' },
        splitLine: { lineStyle: { color: 'rgba(82,183,136,0.1)' } }
      },
      series: [{
        type: 'bar', 
        barWidth: '40%',
        data: [92, 78, 65, 83, 70].map(v => ({
          value: v,
          itemStyle: { 
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: v >= 80 ? eco : v >= 60 ? '#f59e0b' : '#ef4444' },
              { offset: 1, color: v >= 80 ? ecoDark : v >= 60 ? '#d97706' : '#dc2626' }
            ]),
            borderRadius: [6, 6, 0, 0] 
          }
        }))
      }]
    })
  }
}

const handleResize = () => { 
  trendChart?.resize(); 
  pieChart?.resize(); 
  barChart?.resize() 
}

onMounted(() => { 
  initCharts(); 
  window.addEventListener('resize', handleResize) 
})
onUnmounted(() => { 
  window.removeEventListener('resize', handleResize); 
  trendChart?.dispose(); 
  pieChart?.dispose(); 
  barChart?.dispose() 
})
</script>

<style lang="scss" scoped>
$primary: #1a4d2e;
$secondary: #2d6a4f;
$accent: #52b788;
$light: #95d5b2;
$gray-100: #f7f7f7;
$gray-200: #eeeeee;
$gray-300: #e0e0e0;
$gray-400: #bdbdbd;
$gray-500: #9e9e9e;
$gray-600: #757575;
$gray-700: #616161;
$gray-800: #424242;
$gray-900: #212121;

* {
  box-sizing: border-box;
}

.analytics-container {
  padding: 24px 32px;
  min-height: 100vh;
  background: linear-gradient(180deg, #fafafa 0%, #f0f0f0 50%, #e8f0e8 100%);
  position: relative;
  overflow-x: hidden;
}

// 背景装饰
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
  
  &.orb-1 {
    width: 500px;
    height: 500px;
    background: linear-gradient(135deg, rgba(26,77,46,0.12), rgba(82,183,136,0.08));
    top: -150px;
    right: 5%;
    animation: float 25s ease-in-out infinite;
  }
  
  &.orb-2 {
    width: 350px;
    height: 350px;
    background: linear-gradient(135deg, rgba(82,183,136,0.1), rgba(149,213,178,0.06));
    bottom: 10%;
    left: -100px;
    animation: float 30s ease-in-out infinite reverse;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -40px) scale(1.05); }
  66% { transform: translate(-30px, 30px) scale(0.95); }
}

// 页面头部
.page-header {
  position: relative;
  z-index: 1;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(26,77,46,0.1), rgba(82,183,136,0.2));
  border: 1px solid rgba(82,183,136,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 26px;
    height: 26px;
    color: $primary;
  }
}

.page-title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: $gray-900;
  letter-spacing: -0.5px;
}

.page-subtitle {
  margin: 4px 0 0;
  font-size: 14px;
  color: $gray-500;
}

.time-selector {
  :deep(.el-radio-button__inner) {
    background: rgba(255,255,255,0.8);
    border-color: rgba(82,183,136,0.2);
    color: $gray-600;
    
    &:hover {
      color: $primary;
    }
  }
  
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background: linear-gradient(135deg, $primary, $secondary);
    border-color: $primary;
    color: white;
    box-shadow: 0 2px 8px rgba(26,77,46,0.25);
  }
}

// 核心指标卡片
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.kpi-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease-out var(--delay) both;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(26,77,46,0.12);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.kpi-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}

.kpi-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-icon {
  width: 28px;
  height: 28px;
  color: $primary;
}

.kpi-content {
  flex: 1;
  min-width: 0;
}

.kpi-value {
  font-size: 26px;
  font-weight: 800;
  color: $gray-900;
  line-height: 1.2;
}

.kpi-label {
  font-size: 13px;
  color: $gray-500;
  margin-top: 4px;
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  padding: 6px 10px;
  border-radius: 20px;

  svg {
    width: 14px;
    height: 14px;
  }

  &.up {
    background: rgba(82, 183, 136, 0.1);
    color: $accent;
  }

  &.down {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
  }
}

.kpi-progress {
  margin-top: 16px;
}

.progress-track {
  height: 6px;
  background: rgba(82, 183, 136, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
}

// 图表卡片
.glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.06);
}

.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.chart-card {
  .card-header {
    padding: 18px 24px;
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &.chart-main .chart-container {
    height: 280px;
    padding: 16px;
  }

  &.chart-side .chart-container {
    height: 280px;
    padding: 16px;
  }

  &.chart-bar .chart-container {
    height: 260px;
    padding: 16px;
  }
}

.table-card {
  .card-header {
    padding: 18px 24px;
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
  }
}

.card-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-icon {
  width: 22px;
  height: 22px;
  color: $accent;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: $gray-800;
}

.card-actions {
  :deep(.el-button) {
    background: rgba(255,255,255,0.8);
    border-color: rgba(82,183,136,0.2);
    color: $gray-600;
    
    &:hover {
      background: rgba(82,183,136,0.08);
      border-color: rgba(82,183,136,0.3);
      color: $primary;
    }
  }
}

// 排行表格
.rank-table {
  padding: 8px 16px 16px;
}

.rank-header {
  display: grid;
  grid-template-columns: 60px 1fr 70px 70px 120px;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(26, 77, 46, 0.03);
  border-radius: 10px;
  margin-bottom: 8px;
}

.rank-col {
  font-size: 13px;
  font-weight: 600;
  color: $gray-700;
  display: flex;
  align-items: center;
}

.rank-body {
  .rank-row {
    display: grid;
    grid-template-columns: 60px 1fr 70px 70px 120px;
    gap: 12px;
    padding: 14px 16px;
    border-radius: 10px;
    transition: background 0.2s ease;
    align-items: center;

    &:hover {
      background: rgba(82, 183, 136, 0.04);
    }

    &:not(:last-child) {
      border-bottom: 1px solid rgba(82, 183, 136, 0.06);
    }
  }
}

.rank-num {
  justify-content: center;
}

.rank-name {
  font-weight: 500;
  color: $gray-800;
}

.rank-done, .rank-total {
  justify-content: center;
  color: $gray-600;
  font-size: 13px;
}

.rank-rate {
  gap: 10px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;

  &.gold {
    background: linear-gradient(135deg, #f59e0b, #fbbf24);
    color: #fff;
  }

  &.silver {
    background: linear-gradient(135deg, #9ca3af, #d1d5db);
    color: #fff;
  }

  &.bronze {
    background: linear-gradient(135deg, #b45309, #d97706);
    color: #fff;
  }
}

.rate-bar {
  flex: 1;
  height: 8px;
  background: rgba(82, 183, 136, 0.12);
  border-radius: 4px;
  overflow: hidden;
}

.rate-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.rate-value {
  font-size: 12px;
  font-weight: 600;
  min-width: 40px;
  text-align: right;
}

// 响应式
@media (max-width: 1400px) {
  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1200px) {
  .charts-row {
    grid-template-columns: 1fr;
  }

  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .analytics-container {
    padding: 16px;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
