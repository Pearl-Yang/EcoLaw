<template>
  <div class="comp-container">
    <!-- 统计卡 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6" v-for="s in stats" :key="s.label">
        <div class="stat-card" :style="{ background: s.gradient }">
          <div class="stat-icon">
            <svg v-if="s.icon === 'building'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="2" width="16" height="20" rx="2" ry="2"></rect><path d="M9 22v-4h6v4"></path><line x1="9" y1="6" x2="9" y2="6"></line><line x1="15" y1="6" x2="15" y2="6"></line><line x1="9" y1="10" x2="9" y2="10"></line><line x1="15" y1="10" x2="15" y2="10"></line><line x1="9" y1="14" x2="9" y2="14"></line><line x1="15" y1="14" x2="15" y2="14"></line></svg>
            <svg v-else-if="s.icon === 'check-circle'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            <svg v-else-if="s.icon === 'alert-triangle'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
            <svg v-else-if="s.icon === 'percent'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="5" x2="5" y2="19"></line><circle cx="6.5" cy="6.5" r="2.5"></circle><circle cx="17.5" cy="17.5" r="2.5"></circle></svg>
          </div>
          <div class="stat-body">
            <p class="stat-val">{{ s.value }}</p>
            <p class="stat-lbl">{{ s.label }}</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主内容卡片 -->
    <div class="glass-card">
      <div class="page-header">
        <h1 class="page-title">合规台账</h1>
        <button class="btn-success">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
          导出 ESG 报告
        </button>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>企业名称</th>
              <th>所属行业</th>
              <th>合规项</th>
              <th>违规项</th>
              <th>合规率</th>
              <th>最近检查</th>
              <th>下次检查</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in list" :key="row.enterprise">
              <td class="cell-enterprise">{{ row.enterprise }}</td>
              <td>{{ row.industry }}</td>
              <td>{{ row.compliantCount }}</td>
              <td>
                <span :class="{ 'violated-count': row.violatedCount > 0, 'safe-count': row.violatedCount === 0 }">
                  {{ row.violatedCount }}
                </span>
              </td>
              <td>
                <div class="rate-cell">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: row.complianceRate + '%', background: row.complianceRate >= 80 ? '#52b788' : '#f59e0b' }"></div>
                  </div>
                  <span class="rate-text">{{ row.complianceRate }}%</span>
                </div>
              </td>
              <td>{{ row.lastCheck }}</td>
              <td>{{ row.nextCheck }}</td>
              <td>
                <div class="action-btns">
                  <button class="action-btn">详情</button>
                  <button class="action-btn action-warning">整改通知</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const stats = reactive([
  { label: '监管企业总数', value: '328', gradient: 'linear-gradient(135deg, #1a4d2e, #2d6a4f)', icon: 'building' },
  { label: '合规企业', value: '268', gradient: 'linear-gradient(135deg, #2d6a4f, #52b788)', icon: 'check-circle' },
  { label: '待整改企业', value: '42', gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)', icon: 'alert-triangle' },
  { label: '整体合规率', value: '81.7%', gradient: 'linear-gradient(135deg, #52b788, #95d5b2)', icon: 'percent' }
])

const list = reactive([
  { enterprise: '环保科技有限公司', industry: '塑料制造', compliantCount: 12, violatedCount: 1, complianceRate: 92, lastCheck: '2026-03-28', nextCheck: '2026-06-28' },
  { enterprise: '绿野农业合作社', industry: '农业', compliantCount: 8, violatedCount: 0, complianceRate: 100, lastCheck: '2026-03-20', nextCheck: '2026-06-20' },
  { enterprise: '清新餐饮管理公司', industry: '餐饮', compliantCount: 6, violatedCount: 2, complianceRate: 75, lastCheck: '2026-03-15', nextCheck: '2026-04-15' },
  { enterprise: '速达快递有限公司', industry: '快递', compliantCount: 10, violatedCount: 1, complianceRate: 91, lastCheck: '2026-03-25', nextCheck: '2026-06-25' }
])
</script>

<style lang="scss" scoped>
.comp-container {
  padding: 24px;
  min-height: 100vh;
  background: #ffffff;
}

.mb-20 { margin-bottom: 20px; }

.stat-card {
  border-radius: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 24px rgba(82, 183, 136, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.6);
  
  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: rgba(82, 183, 136, 0.15);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #2d6a4f;
  }
  
  .stat-body {
    .stat-val {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      color: #333333;
    }
    .stat-lbl {
      margin: 4px 0 0;
      font-size: 13px;
      color: #666;
    }
  }
}

.glass-card {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(82, 183, 136, 0.06);
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333333;
}

.btn-success {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: linear-gradient(135deg, #2d6a4f, #1a4d2e);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
  }
}

.table-wrapper {
  overflow-x: auto;
  margin: 0 -8px;
  padding: 0 8px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  
  thead tr {
    background: rgba(82, 183, 136, 0.06);
  }
  
  th, td {
    padding: 14px 12px;
    text-align: left;
    border-bottom: 1px solid rgba(82, 183, 136, 0.08);
    white-space: nowrap;
  }
  
  th {
    font-weight: 600;
    color: #333333;
    font-size: 13px;
  }
  
  tbody tr {
    transition: background 0.2s;
    
    &:hover {
      background: rgba(82, 183, 136, 0.04);
    }
  }
}

.cell-enterprise {
  font-weight: 500;
  color: #333333;
}

.violated-count {
  color: #ef4444;
  font-weight: 600;
}

.safe-count {
  color: #52b788;
  font-weight: 600;
}

.rate-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  width: 80px;
  height: 6px;
  background: rgba(82, 183, 136, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s;
}

.rate-text {
  font-size: 12px;
  color: #666;
  min-width: 36px;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  background: transparent;
  color: #2d6a4f;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(82, 183, 136, 0.1);
  }
  
  &.action-warning {
    color: #d97706;
    &:hover { background: rgba(245, 158, 11, 0.1); }
  }
}
</style>
