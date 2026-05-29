<template>
  <div class="bs-root">
    <div class="bs-bg-pattern" aria-hidden="true" />
    <div class="bs-accent-glow" aria-hidden="true" />

    <header class="bs-header">
      <div class="bs-header-side bs-time">
        <svg class="bs-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        <span>{{ dateText }}</span>
        <span class="bs-divider"></span>
        <span>{{ timeText }}</span>
      </div>
      <div class="bs-title-wrap">
        <h1 class="bs-title">绿法通 · 白色污染治理数据驾驶舱</h1>
        <p class="bs-sub">普法任务 · 企业合规 · 舆情与执法联动监测（演示数据）</p>
      </div>
      <div class="bs-header-side bs-actions">
        <button v-if="showBack" type="button" class="bs-btn" @click="goAdmin">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15 18-6-6 6-6"/></svg>
          返回管理后台
        </button>
        <button type="button" class="bs-btn bs-btn-outline" @click="toggleFullScreen">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 3H5a2 2 0 0 0-2 2v3m18 0V5a2 2 0 0 0-2-2h-3m0 18h3a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2h3"/></svg>
          全屏
        </button>
        <span class="bs-kb-hint">Esc 退出 | F 全屏 | 1-9 快捷跳转</span>
      </div>
    </header>

    <section class="bs-kpi">
      <div v-for="k in kpis" :key="k.label" class="bs-kpi-card" :class="'accent-' + k.accent">
        <div class="bs-kpi-inner">
          <span class="bs-kpi-label">{{ k.label }}</span>
          <span class="bs-kpi-value">{{ k.value }}</span>
          <span class="bs-kpi-trend" :class="k.up ? 'up' : 'down'">{{ k.trend }}</span>
        </div>
        <div class="bs-kpi-decoration" aria-hidden="true"></div>
      </div>
    </section>

    <div class="bs-body">
      <aside class="bs-col bs-col-left">
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon green"></span><span class="bs-panel-title">任务完成趋势</span></div>
          <div ref="elLine" class="bs-chart" />
        </div>
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon blue"></span><span class="bs-panel-title">区域普法完成排名</span></div>
          <div ref="elBarH" class="bs-chart" />
        </div>
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon purple"></span><span class="bs-panel-title">合规状态占比</span></div>
          <div ref="elPie" class="bs-chart" />
        </div>
      </aside>

      <main class="bs-col bs-col-center">
        <div class="bs-panel bs-panel-hero">
          <div class="bs-panel-hd"><span class="bs-panel-icon teal"></span><span class="bs-panel-title">重点地区监测强度</span></div>
          <div ref="elMapBar" class="bs-chart bs-chart-tall" />
        </div>
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon amber"></span><span class="bs-panel-title">月度案件与咨询走势</span></div>
          <div ref="elMix" class="bs-chart bs-chart-mid" />
        </div>
      </main>

      <aside class="bs-col bs-col-right">
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon red"></span><span class="bs-panel-title">实时告警趋势</span></div>
          <div ref="elAlertLine" class="bs-chart" />
        </div>
        <div class="bs-panel">
          <div class="bs-panel-hd"><span class="bs-panel-icon cyan"></span><span class="bs-panel-title">渠道来源分布</span></div>
          <div ref="elRose" class="bs-chart" />
        </div>
        <div class="bs-panel bs-panel-table">
          <div class="bs-panel-hd"><span class="bs-panel-icon indigo"></span><span class="bs-panel-title">最新动态</span></div>
          <div class="bs-table-wrap">
            <table class="bs-table">
              <thead>
                <tr>
                  <th>时间</th>
                  <th>类型</th>
                  <th>摘要</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in feed" :key="row.id">
                  <td>{{ row.t }}</td>
                  <td><span class="bs-type-tag">{{ row.type }}</span></td>
                  <td>{{ row.text }}</td>
                  <td><span class="bs-tag" :class="row.level">{{ row.status }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')
import screenfull from 'screenfull'
import { getToken } from '@/utils/auth'

defineOptions({ name: 'BigScreenIndex' })

const router = useRouter()
const showBack = computed(() => !!getToken())

const dateText = ref('')
const timeText = ref('')
let clockTimer = null

const kpis = ref([
  { label: '累计普法触达', value: '1,286,400', trend: '↑ 12.4%', up: true, accent: 'green' },
  { label: '企业合规巡检', value: '8,932', trend: '↑ 3.1%', up: true, accent: 'blue' },
  { label: '待处置预警', value: '36', trend: '↓ 8 条', up: true, accent: 'amber' },
  { label: '律所对接案件', value: '1,204', trend: '↑ 6.0%', up: true, accent: 'purple' }
])

const feed = ref([
  { id: 1, t: '18:32:01', type: '预警', text: '某园区固废台账异常波动', status: '跟进中', level: 'warn' },
  { id: 2, t: '18:28:44', type: '任务', text: '县域普法周任务已闭环', status: '已完成', level: 'ok' },
  { id: 3, t: '18:21:09', type: '咨询', text: '餐饮企业包装物合规问询', status: '已分配', level: 'info' },
  { id: 4, t: '18:05:33', type: '执法', text: '农贸市场抽查记录同步', status: '已归档', level: 'ok' },
  { id: 5, t: '17:58:12', type: '舆情', text: '社区群热议塑料袋新规', status: '监测中', level: 'info' }
])

const elLine = ref(null)
const elBarH = ref(null)
const elPie = ref(null)
const elMapBar = ref(null)
const elMix = ref(null)
const elAlertLine = ref(null)
const elRose = ref(null)

const charts = []

const axisLine = { lineStyle: { color: '#CBD5E1' } }
const axisLabel = { color: '#64748B', fontSize: 11 }
const splitLine = { lineStyle: { color: '#F1F5F9' } }

function baseChartOptions() {
  return {
    backgroundColor: 'transparent',
    textStyle: { fontFamily: 'inherit' }
  }
}

function initLine() {
  if (!elLine.value) return
  const c = echarts.init(elLine.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    grid: { left: 48, right: 16, top: 28, bottom: 28 },
    tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    legend: {
      data: ['已完成', '进行中'],
      textStyle: { color: '#64748B', fontSize: 11 },
      top: 0
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine,
      axisLabel,
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine,
      axisLine: { show: false },
      axisLabel
    },
    series: [
      {
        name: '已完成',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#10B981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.2)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' }
          ])
        },
        data: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: '进行中',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#3B82F6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.18)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' }
          ])
        },
        data: [80, 102, 121, 104, 130, 150, 160]
      }
    ]
  })
}

function initBarH() {
  if (!elBarH.value) return
  const c = echarts.init(elBarH.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    grid: { left: 72, right: 24, top: 16, bottom: 16 },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    xAxis: {
      type: 'value',
      splitLine,
      axisLabel,
      axisLine: { show: false }
    },
    yAxis: {
      type: 'category',
      data: ['经开区', '高新区', '县域北', '县域南', '中心城区'],
      axisLine,
      axisLabel,
      axisTick: { show: false }
    },
    series: [
      {
        type: 'bar',
        data: [820, 760, 690, 640, 920],
        barWidth: 14,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#D1FAE5' },
            { offset: 1, color: '#10B981' }
          ])
        }
      }
    ]
  })
}

function initPie() {
  if (!elPie.value) return
  const c = echarts.init(elPie.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'item', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    legend: {
      bottom: 0,
      textStyle: { color: '#64748B', fontSize: 11 }
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '46%'],
        label: { color: '#475569', fontSize: 11 },
        data: [
          { value: 48, name: '合规良好', itemStyle: { color: '#10B981' } },
          { value: 29, name: '待整改', itemStyle: { color: '#3B82F6' } },
          { value: 15, name: '重点盯防', itemStyle: { color: '#F59E0B' } },
          { value: 8, name: '其他', itemStyle: { color: '#94A3B8' } }
        ]
      }
    ]
  })
}

function initMapBar() {
  if (!elMapBar.value) return
  const c = echarts.init(elMapBar.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    grid: { left: 48, right: 16, top: 24, bottom: 40 },
    tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    xAxis: {
      type: 'category',
      data: ['A 片区', 'B 片区', 'C 片区', 'D 片区', 'E 片区', 'F 片区'],
      axisLine,
      axisLabel: { ...axisLabel, rotate: 24 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine,
      axisLabel,
      axisLine: { show: false }
    },
    series: [
      {
        name: '监测强度',
        type: 'bar',
        data: [320, 280, 410, 360, 300, 390],
        barWidth: 20,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
            { offset: 0, color: '#D1FAE5' },
            { offset: 1, color: '#10B981' }
          ]),
          borderRadius: [6, 6, 0, 0]
        }
      },
      {
        name: '预警密度',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { color: '#F59E0B', width: 2.5 },
        itemStyle: { color: '#F59E0B' },
        data: [120, 102, 180, 145, 90, 160]
      }
    ]
  })
}

function initMix() {
  if (!elMix.value) return
  const c = echarts.init(elMix.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    grid: { left: 48, right: 16, top: 36, bottom: 28 },
    tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    legend: {
      data: ['咨询量', '案件量'],
      textStyle: { color: '#64748B', fontSize: 11 },
      top: 0
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLine,
      axisLabel,
      axisTick: { show: false }
    },
    yAxis: [
      {
        type: 'value',
        splitLine,
        axisLabel,
        axisLine: { show: false }
      },
      {
        type: 'value',
        splitLine: { show: false },
        axisLabel,
        axisLine: { show: false }
      }
    ],
    series: [
      {
        name: '咨询量',
        type: 'bar',
        data: [2200, 2600, 2100, 2800, 3000, 3200],
        barWidth: 16,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [
            { offset: 0, color: '#DBEAFE' },
            { offset: 1, color: '#3B82F6' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: '案件量',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        lineStyle: { width: 2.5, color: '#10B981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.2)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' }
          ])
        },
        data: [420, 512, 401, 534, 590, 630]
      }
    ]
  })
}

function initAlertLine() {
  if (!elAlertLine.value) return
  const c = echarts.init(elAlertLine.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    grid: { left: 40, right: 12, top: 16, bottom: 24 },
    tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
      axisLine,
      axisLabel,
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine,
      axisLabel,
      axisLine: { show: false }
    },
    series: [
      {
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 2.5, color: '#EF4444' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(239, 68, 68, 0.15)' },
            { offset: 1, color: 'rgba(239, 68, 68, 0)' }
          ])
        },
        data: [12, 18, 9, 22, 16, 14]
      }
    ]
  })
}

function initRose() {
  if (!elRose.value) return
  const c = echarts.init(elRose.value)
  charts.push(c)
  c.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'item', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#1E293B' } },
    series: [
      {
        type: 'pie',
        radius: [16, 96],
        center: ['50%', '52%'],
        roseType: 'area',
        label: { color: '#475569', fontSize: 11 },
        data: [
          { value: 32, name: '小程序', itemStyle: { color: '#10B981' } },
          { value: 28, name: '政务端', itemStyle: { color: '#3B82F6' } },
          { value: 22, name: '线下活动', itemStyle: { color: '#8B5CF6' } },
          { value: 18, name: '媒体报道', itemStyle: { color: '#F59E0B' } }
        ]
      }
    ]
  })
}

function resizeCharts() {
  charts.forEach(ch => ch.resize())
}

function goAdmin() {
  router.push('/dashboard/workbench')
}

function toggleFullScreen() {
  if (!screenfull.isEnabled) return
  screenfull.toggle(document.documentElement)
}

function onKeydown(e) {
  if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return
  if (e.key === 'Escape') {
    router.push('/dashboard/workbench')
    return
  }
  if (e.key === 'f' || e.key === 'F') {
    toggleFullScreen()
    return
  }
  const QUICK_KEYS = {
    '1': '/dashboard/workbench',
    '2': '/government/tasks',
    '3': '/government/publicity',
    '4': '/enterprise/training',
    '5': '/law-firm/consult',
    '6': '/ai-tools/material-gen',
    '7': '/ai-tools/law-interpret',
    '8': '/content/notification',
    '9': '/profile'
  }
  if (QUICK_KEYS[e.key]) {
    router.push(QUICK_KEYS[e.key])
  }
}

function tickClock() {
  const now = dayjs()
  dateText.value = now.format('YYYY年MM月DD日 dddd')
  timeText.value = now.format('HH:mm:ss')
}

onMounted(() => {
  tickClock()
  clockTimer = window.setInterval(tickClock, 1000)
  requestAnimationFrame(() => {
    initLine()
    initBarH()
    initPie()
    initMapBar()
    initMix()
    initAlertLine()
    initRose()
    resizeCharts()
  })
  window.addEventListener('resize', resizeCharts)
  window.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  if (clockTimer) window.clearInterval(clockTimer)
  window.removeEventListener('resize', resizeCharts)
  window.removeEventListener('keydown', onKeydown)
  charts.forEach(ch => ch.dispose())
  charts.length = 0
})
</script>

<style lang="scss" scoped>
.bs-root {
  position: relative;
  min-height: 100vh;
  padding: 16px 20px 24px;
  background: linear-gradient(145deg, #F8FAFC 0%, #F1F5F9 50%, #E2E8F0 100%);
  color: #1E293B;
  font-family: 'Segoe UI', system-ui, sans-serif;
  box-sizing: border-box;
}

.bs-bg-pattern {
  pointer-events: none;
  position: fixed;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(16, 185, 129, 0.06) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(139, 92, 246, 0.03) 0%, transparent 60%);
  z-index: 0;
}

.bs-accent-glow {
  pointer-events: none;
  position: fixed;
  top: -10%;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 40%;
  background: radial-gradient(ellipse at center, rgba(16, 185, 129, 0.08) 0%, transparent 60%);
  z-index: 0;
}

.bs-header,
.bs-kpi,
.bs-body {
  position: relative;
  z-index: 1;
}

.bs-header {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 16px 24px;
  border-radius: 16px;
  background: linear-gradient(135deg, #FFFFFF 0%, #F8FAFC 100%);
  border: 1px solid #E2E8F0;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
}

.bs-header-side {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #64748B;
}

.bs-time {
  justify-self: start;
}

.bs-icon {
  width: 16px;
  height: 16px;
  color: #10B981;
}

.bs-divider {
  width: 1px;
  height: 14px;
  background: #CBD5E1;
  margin: 0 4px;
}

.bs-actions {
  justify-self: end;
  gap: 8px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.bs-kb-hint {
  font-size: 11px;
  color: #94A3B8;
  letter-spacing: 0.02em;
  white-space: nowrap;
  padding: 0 4px;
}

.bs-title-wrap {
  text-align: center;
}

.bs-title {
  margin: 0;
  font-size: clamp(20px, 2.2vw, 28px);
  font-weight: 700;
  letter-spacing: 0.04em;
  background: linear-gradient(90deg, #059669, #10B981, #34D399);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.bs-sub {
  margin: 6px 0 0;
  font-size: 12px;
  color: #94A3B8;
  letter-spacing: 0.04em;
}

.bs-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  border: 1px solid #E2E8F0;
  background: #FFFFFF;
  color: #475569;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 12px;
  transition: all 0.2s ease;
  
  svg {
    width: 14px;
    height: 14px;
  }
}

.bs-btn:hover {
  background: #F8FAFC;
  border-color: #10B981;
  color: #059669;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.bs-btn-outline {
  background: transparent;
  border-color: #10B981;
  color: #10B981;
}

.bs-kpi {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

@media (max-width: 1100px) {
  .bs-kpi {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.bs-kpi-card {
  position: relative;
  padding: 20px 20px 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, #FFFFFF 0%, #F8FAFC 100%);
  border: 1px solid #E2E8F0;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  }
  
  &.accent-green .bs-kpi-decoration { background: linear-gradient(135deg, #10B981, #34D399); }
  &.accent-blue .bs-kpi-decoration { background: linear-gradient(135deg, #3B82F6, #60A5FA); }
  &.accent-amber .bs-kpi-decoration { background: linear-gradient(135deg, #F59E0B, #FBBF24); }
  &.accent-purple .bs-kpi-decoration { background: linear-gradient(135deg, #8B5CF6, #A78BFA); }
}

.bs-kpi-inner {
  position: relative;
  z-index: 1;
}

.bs-kpi-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.bs-kpi-label {
  display: block;
  font-size: 13px;
  color: #64748B;
  margin-bottom: 8px;
  font-weight: 500;
}

.bs-kpi-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: #1E293B;
}

.bs-kpi-trend {
  display: inline-block;
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 4px;
}

.bs-kpi-trend.up {
  color: #059669;
  background: rgba(16, 185, 129, 0.1);
}

.bs-kpi-trend.down {
  color: #DC2626;
  background: rgba(239, 68, 68, 0.1);
}

.bs-body {
  display: grid;
  grid-template-columns: 1fr 1.35fr 1fr;
  gap: 16px;
  align-items: stretch;
}

@media (max-width: 1200px) {
  .bs-body {
    grid-template-columns: 1fr;
  }

  .bs-header {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .bs-header-side {
    justify-self: center;
  }

  .bs-actions {
    justify-self: center;
    flex-wrap: wrap;
    justify-content: center;
  }
}

.bs-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.bs-panel {
  position: relative;
  flex: 1;
  min-height: 200px;
  padding: 16px;
  border-radius: 16px;
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.bs-panel-hd {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #F1F5F9;
}

.bs-panel-icon {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  
  &.green { background: #10B981; }
  &.blue { background: #3B82F6; }
  &.purple { background: #8B5CF6; }
  &.teal { background: #14B8A6; }
  &.amber { background: #F59E0B; }
  &.red { background: #EF4444; }
  &.cyan { background: #06B6D4; }
  &.indigo { background: #6366F1; }
}

.bs-panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #1E293B;
}

.bs-panel-hero {
  flex: 1.2;
  min-height: 280px;
}

.bs-panel-table {
  flex: 1;
  min-height: 220px;
  display: flex;
  flex-direction: column;
}

.bs-chart {
  width: 100%;
  height: 220px;
}

.bs-chart-tall {
  height: min(42vh, 360px);
}

.bs-chart-mid {
  height: 240px;
}

.bs-table-wrap {
  flex: 1;
  overflow: auto;
  border: 1px solid #F1F5F9;
  border-radius: 10px;
}

.bs-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.bs-table th,
.bs-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #F1F5F9;
}

.bs-table th {
  color: #64748B;
  font-weight: 600;
  background: linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%);
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.bs-table td {
  color: #475569;
}

.bs-table tbody tr:nth-child(even) {
  background: #F8FAFC;
}

.bs-table tbody tr:hover {
  background: rgba(16, 185, 129, 0.04);
}

.bs-type-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
  font-weight: 500;
}

.bs-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid transparent;
}

.bs-tag.ok {
  color: #059669;
  border-color: rgba(16, 185, 129, 0.3);
  background: rgba(16, 185, 129, 0.08);
}

.bs-tag.warn {
  color: #DC2626;
  border-color: rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.08);
}

.bs-tag.info {
  color: #0284C7;
  border-color: rgba(2, 132, 199, 0.3);
  background: rgba(2, 132, 199, 0.08);
}
</style>
