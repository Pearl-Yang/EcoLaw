<template>
  <div class="emp-container">
    <!-- 主内容卡片 -->
    <div class="glass-card">
      <div class="page-header">
        <h1 class="page-title">员工管理</h1>
        <button class="btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
          导入员工
        </button>
      </div>

      <!-- 查询表单 -->
      <div class="query-form">
        <div class="form-row">
          <div class="form-item">
            <label>关键词</label>
            <input v-model="q.keyword" placeholder="姓名/工号" class="form-input" />
          </div>
          <div class="form-item">
            <label>培训状态</label>
            <select v-model="q.trainingStatus" class="form-select">
              <option value="">全部</option>
              <option value="completed">已完成</option>
              <option value="in_progress">进行中</option>
              <option value="pending">未开始</option>
            </select>
          </div>
          <div class="form-actions">
            <button class="btn-primary" @click="handleQuery">查询</button>
            <button class="btn-secondary" @click="handleReset">重置</button>
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper">
        <table class="data-table" v-loading="loading">
          <thead>
            <tr>
              <th>姓名</th>
              <th>工号</th>
              <th>部门</th>
              <th>岗位</th>
              <th>手机号</th>
              <th>参加培训</th>
              <th>参加考试</th>
              <th>平均成绩</th>
              <th>合规状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in list" :key="row.empNo">
              <td class="cell-name">{{ row.name }}</td>
              <td>{{ row.empNo }}</td>
              <td>{{ row.department }}</td>
              <td>{{ row.position }}</td>
              <td>{{ row.phone }}</td>
              <td>{{ row.trainingCount }}</td>
              <td>{{ row.examCount }}</td>
              <td>
                <span :class="{ 'score-high': row.score >= 60, 'score-low': row.score < 60 }">
                  {{ row.score || '-' }}
                </span>
              </td>
              <td>
                <span class="tag" :class="'tag-status-' + row.status">
                  {{ row.status === 'compliant' ? '合规' : row.status === 'pending' ? '待补训' : '不合格' }}
                </span>
              </td>
              <td>
                <div class="action-btns">
                  <button class="action-btn">详情</button>
                  <button class="action-btn action-warning">指派培训</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination-row">
        <span class="pagination-info">共 {{ total }} 条</span>
        <div class="pagination-controls">
          <button :disabled="q.page <= 1" @click="q.page--; load()">上一页</button>
          <span class="page-num">{{ q.page }} / {{ Math.ceil(total / q.pageSize) || 1 }}</span>
          <button :disabled="q.page >= Math.ceil(total / q.pageSize)" @click="q.page++; load()">下一页</button>
          <select v-model="q.pageSize" @change="load()" class="page-size-select">
            <option :value="10">10条/页</option>
            <option :value="20">20条/页</option>
            <option :value="50">50条/页</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

const loading = ref(false)
const total = ref(4)
const q = reactive({ page: 1, pageSize: 10, keyword: '', trainingStatus: '' })

const list = reactive([
  { name: '张三', empNo: 'E001', department: '生产车间', position: '操作工', phone: '138****1201', trainingCount: 3, examCount: 2, score: 85, status: 'compliant' },
  { name: '李四', empNo: 'E002', department: '仓储物流', position: '仓管员', phone: '139****2202', trainingCount: 3, examCount: 2, score: 72, status: 'compliant' },
  { name: '王五', empNo: 'E003', department: '餐饮服务', position: '服务员', phone: '137****3303', trainingCount: 2, examCount: 1, score: 45, status: 'violated' },
  { name: '赵六', empNo: 'E004', department: '快递配送', position: '配送员', phone: '136****4404', trainingCount: 3, examCount: 2, score: 91, status: 'compliant' }
])

const load = () => {
  loading.value = true
  setTimeout(() => { loading.value = false }, 500)
}

const handleQuery = () => {
  q.page = 1
  load()
}

const handleReset = () => {
  q.keyword = ''
  q.trainingStatus = ''
  handleQuery()
}

onMounted(load)
</script>

<style lang="scss" scoped>
.emp-container {
  padding: 24px;
  min-height: 100vh;
  background: #ffffff;
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

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #2d6a4f, #52b788);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: linear-gradient(135deg, #52b788, #95d5b2);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
  }
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.9);
  color: #2d6a4f;
  border: 1px solid rgba(82, 183, 136, 0.3);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: rgba(82, 183, 136, 0.1);
    border-color: #52b788;
  }
}

.query-form {
  background: rgba(82, 183, 136, 0.04);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  
  label {
    font-size: 13px;
    color: #666;
    font-weight: 500;
  }
}

.form-input, .form-select {
  padding: 10px 14px;
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 8px;
  font-size: 14px;
  background: #fff;
  transition: all 0.3s;
  color: #333;
  min-width: 150px;
  
  &:focus {
    outline: none;
    border-color: #52b788;
    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
  }
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
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

.cell-name {
  font-weight: 500;
  color: #333333;
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  
  &.tag-status-compliant {
    background: rgba(82, 183, 136, 0.15);
    color: #2d6a4f;
  }
  &.tag-status-pending {
    background: rgba(245, 158, 11, 0.15);
    color: #d97706;
  }
  &.tag-status-violated {
    background: rgba(239, 68, 68, 0.15);
    color: #dc2626;
  }
}

.score-high {
  color: #52b788;
  font-weight: 600;
}

.score-low {
  color: #ef4444;
  font-weight: 600;
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

.pagination-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(82, 183, 136, 0.08);
}

.pagination-info {
  font-size: 13px;
  color: #666;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  
  button {
    padding: 8px 14px;
    border: 1px solid rgba(82, 183, 136, 0.2);
    border-radius: 8px;
    background: #fff;
    color: #2d6a4f;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover:not(:disabled) {
      background: rgba(82, 183, 136, 0.1);
      border-color: #52b788;
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
  
  .page-num {
    font-size: 13px;
    color: #666;
  }
}

.page-size-select {
  padding: 8px 12px;
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 8px;
  font-size: 13px;
  background: #fff;
  color: #2d6a4f;
  cursor: pointer;
}
</style>
