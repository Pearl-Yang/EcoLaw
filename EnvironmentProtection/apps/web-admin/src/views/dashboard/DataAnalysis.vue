<template>
  <div class="data-analysis">
    <header class="page-head">
      <div>
        <h1 class="page-title">数据分析</h1>
        <p class="page-desc">平台访问、业务转化与动态一览（示例数据）</p>
      </div>
      <el-radio-group v-model="range" size="default">
        <el-radio-button label="24h">近 24 小时</el-radio-button>
        <el-radio-button label="7d">近 7 天</el-radio-button>
        <el-radio-button label="30d">近 30 天</el-radio-button>
      </el-radio-group>
    </header>

    <div class="row-charts">
      <el-card class="card chart-card" shadow="hover">
        <template #header>
          <div class="card-head">
            <span>仪表盘 · 近期概览</span>
            <div class="head-stats">
              <span class="mini-stat"><em>触达</em>{{ headline.reach }}</span>
              <span class="mini-stat"><em>转化</em>{{ headline.convert }}</span>
            </div>
          </div>
        </template>
        <div ref="lineRef" class="echart-box" />
      </el-card>
      <el-card class="card donut-card" shadow="hover">
        <template #header>
          <div class="card-head">
            <span>时间分配</span>
            <el-button size="small" circle text type="primary">
              <el-icon><Setting /></el-icon>
            </el-button>
          </div>
        </template>
        <div ref="donutRef" class="echart-box donut" />
      </el-card>
    </div>

    <div class="row-kpi">
      <div v-for="k in kpis" :key="k.label" class="kpi-tile" :style="{ '--kpi': k.color }">
        <div class="kpi-icon">
          <el-icon :size="26"><component :is="k.icon" /></el-icon>
        </div>
        <div class="kpi-text">
          <span class="kpi-value">{{ k.value }}</span>
          <span class="kpi-label">{{ k.label }}</span>
        </div>
      </div>
    </div>

    <div class="row-bottom">
      <el-card class="card" shadow="hover">
        <template #header>
          <span>时间线</span>
        </template>
        <el-timeline class="da-timeline">
          <el-timeline-item
            v-for="(ev, i) in timeline"
            :key="i"
            :timestamp="ev.time"
            :type="ev.type"
            placement="top"
          >
            <p class="tl-title">{{ ev.title }}</p>
            <p class="tl-desc">{{ ev.desc }}</p>
          </el-timeline-item>
        </el-timeline>
      </el-card>
      <el-card class="card" shadow="hover">
        <template #header>
          <span>表格</span>
        </template>
        <el-table :data="tableRows" stripe style="width: 100%">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="age" label="年龄" width="72" />
          <el-table-column prop="address" label="地址" min-width="140" show-overflow-tooltip />
          <el-table-column label="标签" min-width="160">
            <template #default="{ row }">
              <el-tag
                v-for="tag in row.tags"
                :key="tag"
                size="small"
                class="tag-cell"
                :type="tagType(tag)"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Setting, View, Wallet, Download, ShoppingCart } from '@element-plus/icons-vue'

defineOptions({ name: 'DashboardDataAnalysis' })

const range = ref('24h')
const lineRef = ref(null)
const donutRef = ref(null)
let lineChart = null
let donutChart = null

const headline = ref({ reach: '12.8万', convert: '3,402' })

const kpis = [
  { label: '访问量', value: '1,000,000', color: '#ec4899', icon: View },
  { label: '成交额', value: '¥234,568', color: '#8b5cf6', icon: Wallet },
  { label: '下载数', value: '666,666', color: '#38bdf8', icon: Download },
  { label: '成交数', value: '999,999', color: '#fb923c', icon: ShoppingCart }
]

const timeline = [
  { type: 'success', time: '2026-04-09 09:12', title: '同步成功', desc: '监管任务数据已同步至大屏。' },
  { type: 'danger', time: '2026-04-08 16:40', title: '接口异常', desc: '第三方合规校验接口短暂超时，已自动重试。' },
  { type: 'warning', time: '2026-04-08 11:05', title: '待处理', desc: '3 家企业台账待复核。' },
  { type: 'primary', time: '2026-04-07 20:46', title: '提示', desc: '新版本「数据分析」页已上线。' }
]

const tableRows = [
  { name: '胡一', age: 32, address: '浙江省杭州市西湖区文三路', tags: ['环保', 'developer'] },
  { name: '陈二', age: 28, address: '江苏省南京市鼓楼区中山路', tags: ['合规', 'wow'] },
  { name: '张三', age: 45, address: '上海市浦东新区世纪大道', tags: ['律所', 'nice'] },
  { name: '李四', age: 26, address: '广东省深圳市南山区科技园', tags: ['企业', 'developer', 'nice'] }
]

function tagType(tag) {
  const map = { nice: 'success', developer: 'info', wow: 'warning', 环保: '', 合规: 'success', 律所: 'danger', 企业: 'info' }
  return map[tag] || ''
}

function hoursLabels() {
  return Array.from({ length: 24 }, (_, i) => `${String(i).padStart(2, '0')}:00`)
}

function buildLineOption() {
  const hours = hoursLabels()
  const downloads = hours.map((_, i) => Math.round(40 + 35 * Math.sin(i / 3) + i * 2 + Math.random() * 15))
  const regs = hours.map((_, i) => Math.round(25 + 28 * Math.cos(i / 4) + i * 1.2 + Math.random() * 12))
  return {
    color: ['#3b82f6', '#22c55e'],
    tooltip: { trigger: 'axis' },
    legend: { data: ['访问量', '新注册'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '14%', top: '12%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: hours,
      axisLabel: { fontSize: 11, color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } },
      axisLabel: { fontSize: 11, color: '#64748b' }
    },
    series: [
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.35)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.02)' }
          ])
        },
        data: downloads
      },
      {
        name: '新注册',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(34, 197, 94, 0.35)' },
            { offset: 1, color: 'rgba(34, 197, 94, 0.02)' }
          ])
        },
        data: regs
      }
    ]
  }
}

function buildDonutOption() {
  return {
    color: ['#6366f1', '#22c55e', '#f97316', '#94a3b8', '#ec4899'],
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, icon: 'circle', textStyle: { fontSize: 11 } },
    series: [
      {
        type: 'pie',
        radius: ['42%', '68%'],
        center: ['50%', '46%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}\n{d}%' },
        data: [
          { value: 335, name: '工作' },
          { value: 234, name: '学习' },
          { value: 154, name: '娱乐' },
          { value: 120, name: '休息' },
          { value: 87, name: '其他' }
        ]
      }
    ]
  }
}

function initCharts() {
  if (lineRef.value && !lineChart) {
    lineChart = echarts.init(lineRef.value)
    lineChart.setOption(buildLineOption())
  }
  if (donutRef.value && !donutChart) {
    donutChart = echarts.init(donutRef.value)
    donutChart.setOption(buildDonutOption())
  }
}

function resizeCharts() {
  lineChart?.resize()
  donutChart?.resize()
}

watch(range, (v) => {
  if (v === '7d') headline.value = { reach: '86.2万', convert: '21.5k' }
  else if (v === '30d') headline.value = { reach: '312万', convert: '78.9k' }
  else headline.value = { reach: '12.8万', convert: '3,402' }
  lineChart?.setOption(buildLineOption(), { notMerge: true })
})

onMounted(() => {
  nextTick(() => {
    initCharts()
    window.addEventListener('resize', resizeCharts)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  lineChart?.dispose()
  donutChart?.dispose()
  lineChart = null
  donutChart = null
})
</script>

<style lang="scss" scoped>
.data-analysis {
  max-width: 1280px;
  margin: 0 auto;
  padding-bottom: 24px;
}

.page-head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.page-title {
  margin: 0 0 4px;
  font-size: 1.35rem;
  font-weight: 700;
  color: #1a2e24;
}

.page-desc {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.row-charts {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.card {
  border-radius: 12px;
  border: 1px solid rgba(45, 106, 79, 0.08);

  :deep(.el-card__header) {
    padding: 12px 16px;
    border-bottom: 1px solid #f1f5f9;
  }

  :deep(.el-card__body) {
    padding: 12px 16px 16px;
  }
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.head-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #64748b;
}

.mini-stat em {
  font-style: normal;
  color: #94a3af;
  margin-right: 6px;
  font-size: 12px;
}

.echart-box {
  width: 100%;
  height: 300px;
}

.echart-box.donut {
  height: 280px;
}

.row-kpi {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 16px;
}

.kpi-tile {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  }
}

.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: var(--kpi);
  flex-shrink: 0;
}

.kpi-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.kpi-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.kpi-label {
  font-size: 12px;
  color: #64748b;
}

.row-bottom {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 16px;
  align-items: start;
}

.da-timeline {
  padding-left: 4px;

  :deep(.el-timeline-item__timestamp) {
    font-size: 12px;
    color: #94a3b8;
  }
}

.tl-title {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.tl-desc {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.tag-cell {
  margin-right: 6px;
  margin-bottom: 4px;
}

@media (max-width: 1024px) {
  .row-charts {
    grid-template-columns: 1fr;
  }
  .row-kpi {
    grid-template-columns: repeat(2, 1fr);
  }
  .row-bottom {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 520px) {
  .row-kpi {
    grid-template-columns: 1fr;
  }
  .echart-box {
    height: 260px;
  }
}
</style>
