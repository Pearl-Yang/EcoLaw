<template>
  <div class="ent-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">企业管理</h2>
        <p class="page-sub">管理企业合规档案与状态跟踪</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleExport">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4 M7 10l5 5 5-5 M12 15V3"/>
          </svg>
          导出台账
        </el-button>
        <el-button type="primary" @click="handleCreate">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          添加企业
        </el-button>
      </div>
    </div>

    <!-- 统计行 -->
    <div class="stats-row">
      <div v-for="s in stats" :key="s.label" class="glass-stat" :style="{ '--c': s.color }">
        <div class="stat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path :d="s.icon"/>
          </svg>
        </div>
        <div class="stat-body">
          <div class="stat-val">{{ s.value }}</div>
          <div class="stat-lbl">{{ s.label }}</div>
        </div>
        <div class="stat-rate" :class="s.up ? 'up' : 'dn'">
          {{ s.up ? '↑' : '↓' }}{{ s.change }}%
        </div>
      </div>
    </div>

    <!-- 玻璃卡片 -->
    <div class="glass-card">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="filter-group">
          <div class="search-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <el-input v-model="q.keyword" placeholder="搜索企业名称 / 行业" clearable />
          </div>
          <el-select v-model="q.industry" placeholder="行业分类" clearable style="width: 150px">
            <el-option v-for="i in industries" :key="i" :label="i" :value="i" />
          </el-select>
          <el-select v-model="q.status" placeholder="合规状态" clearable style="width: 120px">
            <el-option label="合规" value="compliant" />
            <el-option label="待整改" value="pending" />
            <el-option label="已违规" value="violated" />
          </el-select>
        </div>
        <div class="action-group">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleQuery">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            查询
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" class="glass-table"
        :header-cell-style="{ background: 'rgba(82,183,136,0.06)', color: '#1a4d2e', fontWeight: '600' }">
        <el-table-column prop="name" label="企业名称" min-width="200">
          <template #default="{ row }">
            <div class="ent-cell">
              <div class="ent-logo" :style="{ background: getEntBg(row.industry) }">
                {{ row.name.slice(0, 1) }}
              </div>
              <div class="ent-info">
                <span class="ent-name">{{ row.name }}</span>
                <span class="ent-industry">{{ row.industry }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="legalPerson" label="负责人" width="100" />
        <el-table-column prop="contact" label="联系方式" width="130" />
        <el-table-column prop="employeeCount" label="员工数" width="80" align="center" />
        <el-table-column prop="status" label="合规状态" width="100">
          <template #default="{ row }">
            <el-tag class="status-tag" :style="{ '--tc': getStatusColor(row.status) }">
              <span class="dot"></span>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCheck" label="最近检查" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button type="primary" link @click="handleView(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                查看
              </el-button>
              <el-button type="warning" link @click="handleRectify(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                整改
              </el-button>
              <el-button type="success" link @click="handleArchive(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                档案
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="q.page" v-model:page-size="q.pageSize"
          :total="total" :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="load" @current-change="load"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(4)
const industries = ['塑料制品制造', '农业合作社', '餐饮商超', '快递物流', '医疗机构']
const q = reactive({ page: 1, pageSize: 10, keyword: '', industry: '', status: '' })

const stats = reactive([
  { label: '注册企业', value: '328', change: '12', up: true, color: '#52b788', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
  { label: '合规企业', value: '286', change: '8', up: true, color: '#2d6a4f', icon: 'M22 11.08V12a10 10 0 1 1-5.93-9.14 M22 4L12 14.01l-3-3' },
  { label: '待整改', value: '32', change: '5', up: false, color: '#f59e0b', icon: 'M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z M12 9v4 M12 17h.01' },
  { label: '违规企业', value: '10', change: '2', up: false, color: '#ef4444', icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' }
])

const list = reactive([
  { name: '环保科技有限公司', industry: '塑料制品制造', legalPerson: '张总', contact: '138****1234', employeeCount: 86, status: 'compliant', lastCheck: '2026-03-28' },
  { name: '绿野农业合作社', industry: '农业合作社', legalPerson: '李社长', contact: '139****5678', employeeCount: 32, status: 'compliant', lastCheck: '2026-03-20' },
  { name: '清新餐饮管理有限公司', industry: '餐饮商超', legalPerson: '王经理', contact: '137****9012', employeeCount: 15, status: 'pending', lastCheck: '2026-03-15' },
  { name: '速达快递有限公司', industry: '快递物流', legalPerson: '赵总', contact: '136****3456', employeeCount: 120, status: 'compliant', lastCheck: '2026-03-25' }
])

const getEntBg = (ind) => {
  const map = { '塑料制品制造': 'rgba(82,183,136,0.15)', '农业合作社': 'rgba(45,106,79,0.15)', '餐饮商超': 'rgba(149,213,178,0.15)', '快递物流': 'rgba(64,145,108,0.15)' }
  return map[ind] || 'rgba(82,183,136,0.1)'
}

const getStatusColor = (s) => ({ compliant: '#52b788', pending: '#f59e0b', violated: '#ef4444' }[s] || '#9ca3af')
const getStatusText = (s) => ({ compliant: '合规', pending: '待整改', violated: '已违规' }[s] || s)

const handleQuery = () => { load() }
const handleReset = () => { Object.assign(q, { keyword: '', industry: '', status: '' }); load() }
const load = () => {}
const handleCreate = () => { ElMessage.info('添加企业功能') }
const handleView = (r) => { ElMessage.info(`查看 ${r.name} 详情`) }
const handleRectify = (r) => { ElMessage.info(`下发整改通知给 ${r.name}`) }
const handleArchive = (r) => { ElMessage.info(`查看 ${r.name} 合规档案`) }
const handleExport = () => { ElMessage.info('导出企业台账') }
</script>

<style lang="scss" scoped>
.ent-page { max-width: 1400px; margin: 0 auto; }

.page-header {
  display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;
  .header-left {
    .page-title { margin: 0 0 4px; font-size: 24px; font-weight: 700; color: #1a4d2e; }
    .page-sub { margin: 0; font-size: 13px; color: #9ca3af; }
  }
  .header-actions { display: flex; gap: 10px; }
}

.header-actions .el-button {
  display: flex; align-items: center; gap: 6px;
  svg { width: 15px; height: 15px; }
}

.stats-row {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 20px;
}

.glass-stat {
  padding: 18px 20px; border-radius: 16px; background: rgba(255,255,255,0.7);
  backdrop-filter: blur(16px); border: 1px solid rgba(255,255,255,0.5);
  box-shadow: 0 4px 20px rgba(82,183,136,0.08); display: flex; align-items: center; gap: 14px;
  transition: all 0.25s;
  &:hover { transform: translateY(-2px); box-shadow: 0 8px 30px rgba(82,183,136,0.14); }
}

.stat-icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: rgba(82,183,136,0.1); border: 1px solid rgba(82,183,136,0.15);
  display: flex; align-items: center; justify-content: center; color: var(--c, #52b788); flex-shrink: 0;
  svg { width: 20px; height: 20px; }
}

.stat-body { flex: 1; min-width: 0; }
.stat-val { font-size: 22px; font-weight: 800; color: #1a4d2e; }
.stat-lbl { font-size: 12px; color: #9ca3af; margin-top: 2px; }

.stat-rate {
  font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 20px;
  &.up { color: #52b788; background: rgba(82,183,136,0.1); }
  &.dn { color: #ef4444; background: rgba(239,68,68,0.08); }
}

.glass-card {
  border-radius: 20px; background: rgba(255,255,255,0.72); backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5); box-shadow: 0 4px 24px rgba(82,183,136,0.06); padding: 24px;
}

.toolbar { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px; margin-bottom: 20px; }
.filter-group { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }

.search-wrap {
  position: relative; display: flex; align-items: center;
  svg { position: absolute; left: 12px; width: 15px; height: 15px; color: #9ca3af; z-index: 1; }
  :deep(.el-input__wrapper) { padding-left: 36px !important; background: rgba(255,255,255,0.8) !important; border: 1px solid rgba(82,183,136,0.2) !important; box-shadow: none !important; }
}

.action-group { display: flex; gap: 8px; }

.glass-table {
  :deep(.el-table__row) {
    background: transparent !important;
    &:hover > td { background: rgba(82,183,136,0.04) !important; }
  }
  :deep(.el-table__cell) { padding: 14px 12px; font-size: 13px; border-bottom: 1px solid rgba(82,183,136,0.06) !important; }
}

.ent-cell { display: flex; align-items: center; gap: 12px; }
.ent-logo {
  width: 36px; height: 36px; border-radius: 10px; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 700; color: #2d6a4f; flex-shrink: 0;
}
.ent-info { display: flex; flex-direction: column; min-width: 0; }
.ent-name { font-weight: 600; color: #1a4d2e; font-size: 13px; }
.ent-industry { font-size: 12px; color: #9ca3af; }

.status-tag {
  display: inline-flex; align-items: center; gap: 6px; padding: 3px 10px; border-radius: 20px;
  font-size: 12px; font-weight: 500; background: rgba(82,183,136,0.08) !important;
  border: 1px solid rgba(82,183,136,0.15) !important; color: var(--tc, #52b788) !important;
  .dot { width: 6px; height: 6px; border-radius: 50%; background: var(--tc, #52b788); }
}

.action-btns { display: flex; gap: 4px; align-items: center; }
.action-btns .el-button { padding: 4px 8px !important; font-size: 12px !important; display: inline-flex; align-items: center; gap: 4px; svg { width: 13px; height: 13px; } }

.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 16px; border-top: 1px solid rgba(82,183,136,0.08); }

@media (max-width: 1000px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }
</style>
