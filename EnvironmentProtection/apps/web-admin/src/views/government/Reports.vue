<template>
  <div class="rep-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="title-wrapper">
            <div class="title-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9 17H5a2 2 0 0 0-2 2 2 2 0 0 0 2 2h2a2 2 0 0 0 2-2zm12-2h-4a2 2 0 0 0-2 2 2 2 0 0 0 2 2h2a2 2 0 0 0 2-2 2 2 0 0 0-2-2z"/>
                <path d="M5 17V7a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2z"/>
              </svg>
            </div>
            <div>
              <h1 class="page-title">数据报表</h1>
              <p class="page-subtitle">多维度统计分析与导出管理</p>
            </div>
          </div>
        </div>
        
        <div class="header-right">
          <div class="export-dropdown">
            <el-dropdown trigger="click" @command="handleExportCommand">
              <el-button type="primary" class="export-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
                导出报表
                <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="excel">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                      <path d="M14 2v6h6"/>
                    </svg>
                    导出 Excel
                  </el-dropdown-item>
                  <el-dropdown-item command="pdf">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                      <polyline points="14 2 14 8 20 8"/>
                    </svg>
                    导出 PDF
                  </el-dropdown-item>
                  <el-dropdown-item command="all" divided>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                      <line x1="3" y1="9" x2="21" y2="9"/>
                      <line x1="9" y1="21" x2="9" y2="9"/>
                    </svg>
                    导出全部
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计概览卡片 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div v-for="(stat, index) in summary" :key="stat.label" 
             class="stat-card" 
             :style="{ '--delay': index * 0.1 + 's' }">
          <div class="stat-card-inner">
            <div class="stat-icon" :style="{ background: stat.iconBg }">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path :d="stat.icon"/>
              </svg>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
            <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline v-if="stat.trend > 0" points="18 15 12 9 6 15"/>
                <polyline v-else points="6 9 12 15 18 9"/>
              </svg>
              {{ Math.abs(stat.trend) }}%
            </div>
          </div>
          <div class="stat-progress">
            <div class="progress-bar" :style="{ 
              width: stat.progress + '%', 
              background: stat.progressColor 
            }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 导出记录面板 -->
    <div class="export-history-panel" v-if="exportHistory.length > 0">
      <div class="panel-header">
        <h3 class="panel-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
          导出记录
        </h3>
        <el-button link size="small" @click="clearHistory">清除全部</el-button>
      </div>
      <div class="export-list">
        <div v-for="item in exportHistory" :key="item.id" class="export-item">
          <div class="export-item-icon" :class="item.format">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path v-if="item.format === 'excel'" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6"/>
              <path v-else d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6"/>
            </svg>
          </div>
          <div class="export-item-info">
            <div class="export-item-name">{{ item.name }}</div>
            <div class="export-item-meta">
              <span class="format-badge">{{ item.format.toUpperCase() }}</span>
              <span class="export-time">{{ item.time }}</span>
            </div>
          </div>
          <div class="export-item-actions">
            <el-progress v-if="item.progress < 100" type="circle" :percentage="item.progress" :width="36" />
            <el-button v-else type="success" circle size="small" @click="reDownload(item)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧：数据表格 -->
      <div class="content-left">
        <div class="glass-card">
          <!-- 标签切换 -->
          <div class="tab-bar">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="tab-btn"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path :d="tab.icon"/>
              </svg>
              <span>{{ tab.label }}</span>
            </button>
          </div>

          <!-- 筛选栏 -->
          <div class="filter-bar">
            <div class="filter-left">
              <span class="filter-label">筛选：</span>
              <el-select v-model="filterStatus" placeholder="状态" clearable size="default">
                <el-option label="全部" value="" />
                <el-option label="已完成" value="done" />
                <el-option label="进行中" value="processing" />
                <el-option label="待处理" value="pending" />
              </el-select>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="default"
              />
            </div>
            <div class="filter-right">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索关键词..."
                prefix-icon="Search"
                clearable
                size="default"
              />
            </div>
          </div>

          <!-- 普法任务报表 -->
          <div v-if="activeTab === 'task'" class="table-section">
            <div class="section-header">
              <span class="section-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
                  <rect x="9" y="3" width="6" height="4" rx="1"/>
                </svg>
                普法任务报表
              </span>
              <div class="section-actions">
                <el-button type="primary" plain @click="handleExport('task')">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                  </svg>
                  导出
                </el-button>
              </div>
            </div>
            <el-table 
              :data="filteredTaskData" 
              class="glass-table"
              :header-cell-style="headerCellStyle"
              :row-class-name="tableRowClassName"
            >
              <el-table-column type="index" width="60" label="序号" align="center" />
              <el-table-column prop="org" label="组织" min-width="160" show-overflow-tooltip />
              <el-table-column prop="total" label="任务总数" width="100" align="center">
                <template #default="{ row }">
                  <span class="num-tag">{{ row.total }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="done" label="已完成" width="100" align="center">
                <template #default="{ row }">
                  <span class="num-tag success">{{ row.done }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="rate" label="完成率" width="140">
                <template #default="{ row }">
                  <div class="rate-wrapper">
                    <div class="rate-bar">
                      <div class="rate-fill" :style="{ 
                        width: row.rate + '%', 
                        background: getRateGradient(row.rate) 
                      }"></div>
                    </div>
                    <span class="rate-value" :style="{ color: getRateColor(row.rate) }">{{ row.rate }}%</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="cover" label="覆盖人次" width="110" align="center">
                <template #default="{ row }">
                  <span class="num-value">{{ formatNumber(row.cover) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="viewDetail('task', row)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 企业合规台账 -->
          <div v-if="activeTab === 'compliance'" class="table-section">
            <div class="section-header">
              <span class="section-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                  <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
                企业合规台账
              </span>
              <div class="section-actions">
                <el-button type="primary" plain @click="handleExport('compliance')">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                  </svg>
                  导出
                </el-button>
              </div>
            </div>
            <el-table 
              :data="filteredCompData" 
              class="glass-table"
              :header-cell-style="headerCellStyle"
            >
              <el-table-column type="index" width="60" label="序号" align="center" />
              <el-table-column prop="enterprise" label="企业名称" min-width="180" show-overflow-tooltip />
              <el-table-column prop="industry" label="行业" width="130" />
              <el-table-column prop="status" label="合规状态" width="110">
                <template #default="{ row }">
                  <span class="status-badge" :class="getStatusClass(row.status)">
                    <span class="status-dot"></span>
                    {{ row.status }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="lastCheck" label="最近检查" width="120" />
              <el-table-column label="ESG报告" width="100" align="center">
                <template #default>
                  <el-button type="success" link>导出</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 线索举报统计 -->
          <div v-if="activeTab === 'report'" class="table-section">
            <div class="section-header">
              <span class="section-title">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                  <line x1="12" y1="9" x2="12" y2="13"/>
                  <line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
                线索举报统计
              </span>
              <div class="section-actions">
                <el-button type="primary" plain @click="handleExport('report')">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" y1="15" x2="12" y2="3"/>
                  </svg>
                  导出
                </el-button>
              </div>
            </div>
            <el-table 
              :data="filteredReportData" 
              class="glass-table"
              :header-cell-style="headerCellStyle"
            >
              <el-table-column type="index" width="60" label="序号" align="center" />
              <el-table-column prop="area" label="区域" min-width="160" show-overflow-tooltip />
              <el-table-column prop="total" label="举报总数" width="100" align="center">
                <template #default="{ row }">
                  <span class="num-tag warning">{{ row.total }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="pending" label="待处置" width="100" align="center">
                <template #default="{ row }">
                  <span class="num-tag danger">{{ row.pending }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="processed" label="已处置" width="100" align="center">
                <template #default="{ row }">
                  <span class="num-tag success">{{ row.processed }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="avgTime" label="平均处置时长" width="120" align="center">
                <template #default="{ row }">
                  <span class="avg-time">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <circle cx="12" cy="12" r="10"/>
                      <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    {{ row.avgTime }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>

      <!-- 右侧：图表概览 -->
      <div class="content-right">
        <!-- 完成率分布 -->
        <div class="glass-card chart-card">
          <div class="card-header">
            <h3 class="card-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21.21 15.89A10 10 0 1 1 8 2.83"/>
                <path d="M22 12A10 10 0 0 0 12 2v10z"/>
              </svg>
              完成率分布
            </h3>
          </div>
          <div ref="pieChartRef" class="chart-container"></div>
        </div>

        <!-- 趋势图 -->
        <div class="glass-card chart-card">
          <div class="card-header">
            <h3 class="card-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
              </svg>
              举报趋势
            </h3>
          </div>
          <div ref="lineChartRef" class="chart-container"></div>
        </div>
      </div>
    </div>

    <!-- 导出进度对话框 -->
    <el-dialog
      v-model="exportDialogVisible"
      title="导出报表"
      width="400px"
      :close-on-click-modal="false"
      class="export-dialog"
    >
      <div class="export-progress-content">
        <div class="export-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <path d="M14 2v6h6"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>
        </div>
        <div class="export-info">
          <div class="export-name">{{ currentExport.name }}</div>
          <div class="export-format">{{ currentExport.format.toUpperCase() }} 格式</div>
        </div>
        <el-progress 
          :percentage="currentExport.progress" 
          :stroke-width="8"
          :color="progressColor"
        />
        <div class="export-status">
          <span v-if="currentExport.progress < 100">正在生成报表，请稍候...</span>
          <span v-else class="success">导出完成！</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="cancelExport" v-if="currentExport.progress < 100">取消</el-button>
        <el-button type="primary" @click="downloadExport" v-else>立即下载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  exportToExcel, 
  exportToPDF, 
  exportTaskReport, 
  exportComplianceReport,
  exportReportData as exportReportDataUtil,
  exportAllReports,
  generateExportFilename,
  ExportColors
} from '@/utils/export.js'

// 状态
const activeTab = ref('task')
const filterStatus = ref('')
const dateRange = ref([])
const searchKeyword = ref('')
const exportDialogVisible = ref(false)
const exportHistory = ref([])

// 图表引用
const pieChartRef = ref(null)
const lineChartRef = ref(null)
let pieChart = null
let lineChart = null

// 导出状态
const currentExport = reactive({
  name: '',
  format: 'excel',
  progress: 0,
  data: null
})

// 标签页配置
const tabs = [
  { 
    key: 'task', 
    label: '普法任务报表', 
    icon: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2 M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2'
  },
  { 
    key: 'compliance', 
    label: '企业合规台账', 
    icon: 'M22 11.08V12a10 10 0 1 1-5.93-9.14 M22 4L12 14.01l-3-3'
  },
  { 
    key: 'report', 
    label: '线索举报统计', 
    icon: 'M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z M12 9v4 M12 17h.01'
  }
]

// 统计概览
const summary = reactive([
  { 
    label: '累计任务数', 
    value: '1,286', 
    trend: 12, 
    progress: 86,
    progressColor: 'linear-gradient(90deg, #1a4d2e, #52b788)',
    iconBg: 'linear-gradient(135deg, rgba(26,77,46,0.15), rgba(82,183,136,0.25))',
    icon: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2 M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2'
  },
  { 
    label: '普法覆盖人次', 
    value: '12.8 万', 
    trend: 18,
    progress: 92,
    progressColor: 'linear-gradient(90deg, #2d6a4f, #95d5b2)',
    iconBg: 'linear-gradient(135deg, rgba(82,183,136,0.15), rgba(149,213,178,0.3))',
    icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75'
  },
  { 
    label: '合规企业数', 
    value: '328 家', 
    trend: 5,
    progress: 78,
    progressColor: 'linear-gradient(90deg, #40916c, #52b788)',
    iconBg: 'linear-gradient(135deg, rgba(64,145,108,0.12), rgba(82,183,136,0.22))',
    icon: 'M22 11.08V12a10 10 0 1 1-5.93-9.14 M22 4L12 14.01l-3-3'
  },
  { 
    label: '处置举报线索', 
    value: '156 条', 
    trend: -3,
    progress: 65,
    progressColor: 'linear-gradient(90deg, #6b7280, #9ca3af)',
    iconBg: 'linear-gradient(135deg, rgba(107,114,128,0.12), rgba(156,163,175,0.22))',
    icon: 'M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z M12 9v4 M12 17h.01'
  }
])

// 表格数据
const taskData = reactive([
  { org: '龙泉镇司法所', total: 52, done: 48, rate: 92, cover: 1280, status: 'done' },
  { org: '凤鸣乡司法所', total: 50, done: 45, rate: 90, cover: 960, status: 'done' },
  { org: '清溪村居委会', total: 48, done: 40, rate: 83, cover: 720, status: 'done' },
  { org: '东华街道办', total: 46, done: 35, rate: 76, cover: 560, status: 'processing' },
  { org: '西岭镇政府', total: 44, done: 28, rate: 64, cover: 420, status: 'processing' },
  { org: '南山区工业园', total: 38, done: 32, rate: 84, cover: 380, status: 'done' }
])

const compData = reactive([
  { enterprise: '环保科技有限公司', industry: '塑料制造', status: '合规', lastCheck: '2026-03-28' },
  { enterprise: '绿野农业合作社', industry: '农业', status: '合规', lastCheck: '2026-03-20' },
  { enterprise: '清新餐饮管理公司', industry: '餐饮', status: '待整改', lastCheck: '2026-03-15' },
  { enterprise: '光明乳业有限公司', industry: '食品加工', status: '合规', lastCheck: '2026-03-25' },
  { enterprise: '鑫源化工有限公司', industry: '化工', status: '待整改', lastCheck: '2026-03-10' }
])

const reportData = reactive([
  { area: '龙泉镇', total: 48, pending: 5, processed: 43, avgTime: '3.2 天' },
  { area: '凤鸣乡', total: 35, pending: 3, processed: 32, avgTime: '2.8 天' },
  { area: '清溪村', total: 28, pending: 8, processed: 20, avgTime: '4.1 天' },
  { area: '东华街道', total: 22, pending: 2, processed: 20, avgTime: '2.5 天' }
])

// 筛选后的数据
const filteredTaskData = computed(() => {
  return taskData.filter(item => {
    if (filterStatus.value && item.status !== filterStatus.value) return false
    if (searchKeyword.value && !item.org.includes(searchKeyword.value)) return false
    return true
  })
})

const filteredCompData = computed(() => {
  return compData.filter(item => {
    if (filterStatus.value && item.status !== filterStatus.value) return false
    if (searchKeyword.value && !item.enterprise.includes(searchKeyword.value)) return false
    return true
  })
})

const filteredReportData = computed(() => {
  return reportData.filter(item => {
    if (filterStatus.value && item.status !== filterStatus.value) return false
    if (searchKeyword.value && !item.area.includes(searchKeyword.value)) return false
    return true
  })
})

// 表格样式
const headerCellStyle = {
  background: 'rgba(26,77,46,0.04)',
  color: '#1a4d2e',
  fontWeight: '600',
  borderBottom: '1px solid rgba(82,183,136,0.1)'
}

const tableRowClassName = ({ row }) => {
  return row.status === 'pending' ? 'pending-row' : ''
}

// 工具函数
const formatNumber = (num) => {
  return num.toLocaleString()
}

const getRateColor = (rate) => {
  if (rate >= 80) return '#1a4d2e'
  if (rate >= 50) return '#f59e0b'
  return '#ef4444'
}

const getRateGradient = (rate) => {
  if (rate >= 80) return 'linear-gradient(90deg, #1a4d2e, #52b788)'
  if (rate >= 50) return 'linear-gradient(90deg, #f59e0b, #fbbf24)'
  return 'linear-gradient(90deg, #ef4444, #f87171)'
}

const getStatusClass = (status) => {
  if (status === '合规') return 'success'
  if (status === '待整改') return 'warning'
  return 'danger'
}

const progressColor = computed(() => {
  if (currentExport.progress < 30) return '#ef4444'
  if (currentExport.progress < 70) return '#f59e0b'
  if (currentExport.progress < 100) return '#52b788'
  return 'linear-gradient(90deg, #1a4d2e, #52b788)'
})

// 导出处理
const handleExportCommand = async (command) => {
  if (command === 'all') {
    await handleExportAll()
  } else {
    await handleExport(activeTab.value, command)
  }
}

const handleExport = async (type, format = 'excel') => {
  let data, name
  switch (type) {
    case 'task':
      data = filteredTaskData.value
      name = '普法任务报表'
      break
    case 'compliance':
      data = filteredCompData.value
      name = '企业合规台账'
      break
    case 'report':
      data = filteredReportData.value
      name = '线索举报统计'
      break
  }
  
  currentExport.name = name
  currentExport.format = format
  currentExport.progress = 0
  currentExport.data = { type, data }
  exportDialogVisible.value = true
  
  // 模拟导出进度
  simulateExport(type, data, format, name)
}

const handleExportAll = async () => {
  currentExport.name = '综合数据报表'
  currentExport.format = 'excel'
  currentExport.progress = 0
  exportDialogVisible.value = true
  
  simulateExportAll()
}

const simulateExport = async (type, data, format, name) => {
  for (let i = 0; i <= 100; i += 10) {
    await new Promise(resolve => setTimeout(resolve, 100))
    currentExport.progress = i
  }
  
  // 实际导出
  const filename = generateExportFilename(name, format)
  
  if (format === 'excel') {
    if (type === 'task') {
      await exportTaskReport(data, filename)
    } else if (type === 'compliance') {
      await exportComplianceReport(data, filename)
    } else {
      await exportReportDataUtil(data, filename)
    }
  } else {
    ElMessage.info('PDF导出功能开发中')
    exportDialogVisible.value = false
    return
  }
  
  // 添加到历史记录
  addToHistory({
    name: filename,
    format: format,
    time: new Date().toLocaleTimeString()
  })
  
  ElMessage.success(`${name}导出成功`)
}

const simulateExportAll = async () => {
  for (let i = 0; i <= 100; i += 5) {
    await new Promise(resolve => setTimeout(resolve, 80))
    currentExport.progress = i
  }
  
  const filename = generateExportFilename('综合数据报表', 'xlsx')
  await exportAllReports(filteredTaskData.value, filteredCompData.value, filteredReportData.value, filename)
  
  addToHistory({
    name: filename,
    format: 'xlsx',
    time: new Date().toLocaleTimeString()
  })
  
  ElMessage.success('全部报表导出成功')
}

const cancelExport = () => {
  exportDialogVisible.value = false
  currentExport.progress = 0
}

const downloadExport = () => {
  exportDialogVisible.value = false
  ElMessage.success('文件已下载')
}

const addToHistory = (item) => {
  exportHistory.value.unshift({
    id: Date.now(),
    progress: 100,
    ...item
  })
  
  // 只保留最近10条
  if (exportHistory.value.length > 10) {
    exportHistory.value.pop()
  }
}

const clearHistory = () => {
  exportHistory.value = []
}

const reDownload = (item) => {
  ElMessage.info('重新下载: ' + item.name)
}

const viewDetail = (type, row) => {
  ElMessage.info(`查看 ${type} 明细: ${row.org || row.enterprise || row.area}`)
}

// 初始化图表
const initCharts = () => {
  // 饼图
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(82,183,136,0.3)',
        textStyle: { color: '#1a4d2e' }
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        itemWidth: 10,
        itemHeight: 10,
        textStyle: { color: '#666', fontSize: 12 }
      },
      series: [{
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['35%', '50%'],
        label: { show: true, formatter: '{b}\n{d}%', fontSize: 12, color: '#666' },
        labelLine: { lineStyle: { color: 'rgba(82,183,136,0.3)' } },
        data: [
          { value: 48, name: '已完成', itemStyle: { color: '#1a4d2e' } },
          { value: 28, name: '进行中', itemStyle: { color: '#52b788' } },
          { value: 24, name: '待处理', itemStyle: { color: '#9ca3af' } }
        ]
      }]
    })
  }
  
  // 折线图
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    lineChart.setOption({
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(82,183,136,0.3)',
        textStyle: { color: '#1a4d2e' }
      },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
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
      series: [{
        type: 'line',
        smooth: true,
        data: [32, 45, 38, 52, 48, 62],
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(82,183,136,0.3)' },
              { offset: 1, color: 'rgba(82,183,136,0.05)' }
            ]
          }
        },
        lineStyle: { color: '#52b788', width: 2 },
        itemStyle: { color: '#52b788' },
        symbol: 'circle',
        symbolSize: 6
      }]
    })
  }
}

const handleResize = () => {
  pieChart?.resize()
  lineChart?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  lineChart?.dispose()
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

.rep-page {
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
  opacity: 0.4;
  
  &.orb-1 {
    width: 400px;
    height: 400px;
    background: linear-gradient(135deg, rgba(26,77,46,0.15), rgba(82,183,136,0.1));
    top: -100px;
    right: 10%;
    animation: float 20s ease-in-out infinite;
  }
  
  &.orb-2 {
    width: 300px;
    height: 300px;
    background: linear-gradient(135deg, rgba(82,183,136,0.12), rgba(149,213,178,0.08));
    bottom: 20%;
    left: -50px;
    animation: float 25s ease-in-out infinite reverse;
  }
  
  &.orb-3 {
    width: 200px;
    height: 200px;
    background: linear-gradient(135deg, rgba(45,106,79,0.1), rgba(82,183,136,0.15));
    top: 40%;
    right: 5%;
    animation: float 18s ease-in-out infinite;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
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

.export-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, $primary 0%, $secondary 100%);
  border: none;
  border-radius: 10px;
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(26,77,46,0.25);
  transition: all 0.3s ease;
  
  svg {
    width: 18px;
    height: 18px;
  }
  
  .arrow-icon {
    width: 14px;
    height: 14px;
    margin-left: 4px;
    transition: transform 0.2s;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(26,77,46,0.35);
    
    .arrow-icon {
      transform: rotate(180deg);
    }
  }
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  
  svg {
    width: 16px;
    height: 16px;
  }
}

// 统计概览
.stats-section {
  position: relative;
  z-index: 1;
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.6);
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

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  svg {
    width: 22px;
    height: 22px;
    color: $primary;
  }
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 800;
  color: $gray-900;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: $gray-500;
  margin-top: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 20px;
  
  svg {
    width: 12px;
    height: 12px;
  }
  
  &.up {
    background: rgba(82,183,136,0.1);
    color: $accent;
  }
  
  &.down {
    background: rgba(239,68,68,0.1);
    color: #ef4444;
  }
}

.stat-progress {
  margin-top: 12px;
  height: 4px;
  background: rgba(82,183,136,0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 2px;
  transition: width 0.5s ease;
}

// 导出记录面板
.export-history-panel {
  position: relative;
  z-index: 1;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 14px;
  padding: 16px 20px;
  margin-bottom: 24px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: $gray-700;
  
  svg {
    width: 16px;
    height: 16px;
    color: $accent;
  }
}

.export-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.export-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: rgba(255,255,255,0.8);
  border: 1px solid rgba(82,183,136,0.1);
  border-radius: 10px;
  flex-shrink: 0;
}

.export-item-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 16px;
    height: 16px;
  }
  
  &.excel {
    background: rgba(82,183,136,0.1);
    color: $accent;
  }
  
  &.pdf {
    background: rgba(239,68,68,0.1);
    color: #ef4444;
  }
}

.export-item-info {
  flex: 1;
  min-width: 0;
}

.export-item-name {
  font-size: 13px;
  font-weight: 500;
  color: $gray-800;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.export-item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.format-badge {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  background: rgba(26,77,46,0.08);
  color: $primary;
  border-radius: 4px;
}

.export-time {
  font-size: 11px;
  color: $gray-500;
}

.export-item-actions {
  flex-shrink: 0;
}

// 主内容区
.main-content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
}

.glass-card {
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.6);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.06);
}

// 标签栏
.tab-bar {
  display: flex;
  gap: 6px;
  padding: 6px;
  background: rgba(82,183,136,0.04);
  border-radius: 14px;
  margin-bottom: 20px;
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 14px;
  border: none;
  border-radius: 10px;
  background: transparent;
  font-size: 13px;
  font-weight: 500;
  color: $gray-500;
  cursor: pointer;
  transition: all 0.25s ease;
  
  svg {
    width: 16px;
    height: 16px;
  }
  
  &:hover {
    color: $primary;
    background: rgba(255,255,255,0.5);
  }
  
  &.active {
    background: white;
    color: $primary;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(82,183,136,0.12);
  }
}

// 筛选栏
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: rgba(82,183,136,0.03);
  border-radius: 12px;
  margin-bottom: 16px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 13px;
  color: $gray-600;
}

.filter-right {
  :deep(.el-input__wrapper) {
    background: white;
    border-radius: 8px;
  }
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: $gray-800;
  
  svg {
    width: 20px;
    height: 20px;
    color: $accent;
  }
}

.section-actions {
  :deep(.el-button) {
    display: flex;
    align-items: center;
    gap: 6px;
    
    svg {
      width: 14px;
      height: 14px;
    }
  }
}

.glass-table {
  :deep(.el-table__row) {
    background: transparent !important;
    transition: background 0.2s;
    
    &:hover > td {
      background: rgba(82,183,136,0.04) !important;
    }
    
    &.pending-row {
      background: rgba(245,158,11,0.03) !important;
    }
  }
  
  :deep(.el-table__cell) {
    padding: 14px 12px;
    font-size: 13px;
    border-bottom: 1px solid rgba(82,183,136,0.06) !important;
  }
}

.num-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(82,183,136,0.08);
  color: $secondary;
  
  &.success {
    background: rgba(82,183,136,0.12);
    color: $accent;
  }
  
  &.warning {
    background: rgba(245,158,11,0.1);
    color: #f59e0b;
  }
  
  &.danger {
    background: rgba(239,68,68,0.08);
    color: #ef4444;
  }
}

.num-value {
  font-weight: 600;
  color: $gray-800;
}

.rate-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rate-bar {
  flex: 1;
  height: 6px;
  background: rgba(82,183,136,0.1);
  border-radius: 3px;
  overflow: hidden;
  max-width: 70px;
}

.rate-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.rate-value {
  font-size: 13px;
  font-weight: 600;
  min-width: 36px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(82,183,136,0.08);
  border: 1px solid rgba(82,183,136,0.15);
  
  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
  }
  
  &.success {
    color: $accent;
    .status-dot { background: $accent; }
  }
  
  &.warning {
    color: #f59e0b;
    background: rgba(245,158,11,0.08);
    border-color: rgba(245,158,11,0.2);
    .status-dot { background: #f59e0b; }
  }
  
  &.danger {
    color: #ef4444;
    background: rgba(239,68,68,0.08);
    border-color: rgba(239,68,68,0.15);
    .status-dot { background: #ef4444; }
  }
}

.avg-time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: $gray-600;
  font-size: 13px;
  
  svg {
    width: 14px;
    height: 14px;
    color: $gray-400;
  }
}

// 右侧图表卡片
.content-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card {
  .card-header {
    padding: 16px 20px;
    border-bottom: 1px solid rgba(82,183,136,0.08);
  }
  
  .chart-container {
    height: 180px;
    padding: 12px;
  }
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: $gray-800;
  
  svg {
    width: 18px;
    height: 18px;
    color: $accent;
  }
}

// 导出对话框
.export-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
  }
  
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $primary 0%, $secondary 100%);
    color: white;
    padding: 16px 20px;
    margin: 0;
  }
  
  :deep(.el-dialog__title) {
    color: white;
    font-weight: 600;
  }
  
  :deep(.el-dialog__close) {
    color: rgba(255,255,255,0.8);
    
    &:hover {
      color: white;
    }
  }
}

.export-progress-content {
  padding: 20px;
  text-align: center;
}

.export-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(26,77,46,0.1), rgba(82,183,136,0.15));
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 32px;
    height: 32px;
    color: $primary;
  }
}

.export-info {
  margin-bottom: 20px;
}

.export-name {
  font-size: 16px;
  font-weight: 600;
  color: $gray-800;
}

.export-format {
  font-size: 13px;
  color: $gray-500;
  margin-top: 4px;
}

.export-status {
  margin-top: 12px;
  font-size: 13px;
  color: $gray-600;
  
  .success {
    color: $accent;
    font-weight: 500;
  }
}

// 响应式
@media (max-width: 1400px) {
  .main-content {
    grid-template-columns: 1fr 300px;
  }
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .content-right {
    flex-direction: row;
    
    .chart-card {
      flex: 1;
    }
  }
}

@media (max-width: 768px) {
  .rep-page {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .content-right {
    flex-direction: column;
  }
}
</style>
