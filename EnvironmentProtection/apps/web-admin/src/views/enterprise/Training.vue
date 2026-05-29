<template>
  <div class="train-container">
    <!-- 统计卡 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :xs="12" :sm="6" v-for="s in stats" :key="s.label">
        <div class="stat-card" :style="{ background: s.gradient }">
          <div class="stat-icon">
            <svg v-if="s.icon === 'video'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
            <svg v-else-if="s.icon === 'users'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            <svg v-else-if="s.icon === 'check'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            <svg v-else-if="s.icon === 'clock'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
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
        <h1 class="page-title">企业合规培训管理</h1>
        <div class="header-actions">
          <button class="btn-secondary" @click="handleExportAll">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
            导出台账
          </button>
          <button class="btn-primary" @click="showCreate = true">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            新建培训
          </button>
        </div>
      </div>

      <!-- 查询表单 -->
      <div class="query-form">
        <div class="form-row">
          <div class="form-item">
            <label>关键词</label>
            <input v-model="q.keyword" placeholder="培训名称" class="form-input" />
          </div>
          <div class="form-item">
            <label>企业</label>
            <select v-model="q.enterprise" class="form-select">
              <option value="">全部企业</option>
              <option value="ent1">XX塑料厂</option>
              <option value="ent2">XX合作社</option>
              <option value="ent3">XX超市</option>
            </select>
          </div>
          <div class="form-item">
            <label>类型</label>
            <select v-model="q.type" class="form-select">
              <option value="">全部</option>
              <option value="video">视频课程</option>
              <option value="document">文档学习</option>
              <option value="exam">在线考试</option>
            </select>
          </div>
          <div class="form-item">
            <label>状态</label>
            <select v-model="q.status" class="form-select">
              <option value="">全部</option>
              <option value="in_progress">进行中</option>
              <option value="completed">已完成</option>
              <option value="pending">未开始</option>
            </select>
          </div>
          <div class="form-item">
            <label>时间</label>
            <input type="date" v-model="q.startDate" class="form-input" /> -
            <input type="date" v-model="q.endDate" class="form-input" />
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
              <th><input type="checkbox" /></th>
              <th>培训名称</th>
              <th>所属企业</th>
              <th>类型</th>
              <th>时长</th>
              <th>参与人数</th>
              <th>完成率</th>
              <th>状态</th>
              <th>截止日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in list" :key="row.id">
              <td><input type="checkbox" /></td>
              <td class="cell-title">{{ row.title }}</td>
              <td>{{ row.enterprise }}</td>
              <td><span class="tag" :class="'tag-' + row.type">{{ typeText[row.type] }}</span></td>
              <td>{{ row.duration }}</td>
              <td><span class="enrolled-count">{{ row.completed }}/{{ row.enrolled }}人</span></td>
              <td>
                <div class="rate-cell">
                  <div class="progress-bar"><div class="progress-fill" :style="{ width: row.completionRate + '%', background: getRateColor(row.completionRate) }"></div></div>
                  <span class="rate-text">{{ row.completionRate }}%</span>
                </div>
              </td>
              <td><span class="tag" :class="'tag-status-' + row.status">{{ statusText[row.status] }}</span></td>
              <td>{{ row.deadline }}</td>
              <td>
                <div class="action-btns">
                  <button class="action-btn" @click="handleView(row)">查看</button>
                  <button class="action-btn action-success" @click="handleRemind(row)">提醒</button>
                  <button class="action-btn action-warning" @click="handleAssign(row)">指派</button>
                  <button class="action-btn action-info" @click="handleExport(row)">导出</button>
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

    <!-- 培训详情弹窗 -->
    <el-dialog v-model="showDetail" title="培训详情" width="900px" class="glass-dialog">
      <div v-if="currentTraining" class="training-detail">
        <div class="detail-grid">
          <div class="detail-item">
            <label>培训名称</label>
            <span>{{ currentTraining.title }}</span>
          </div>
          <div class="detail-item">
            <label>所属企业</label>
            <span>{{ currentTraining.enterprise }}</span>
          </div>
          <div class="detail-item">
            <label>培训类型</label>
            <span class="tag" :class="'tag-' + currentTraining.type">{{ typeText[currentTraining.type] }}</span>
          </div>
          <div class="detail-item">
            <label>课程时长</label>
            <span>{{ currentTraining.duration }}</span>
          </div>
          <div class="detail-item">
            <label>参与人数</label>
            <span>{{ currentTraining.completed }}/{{ currentTraining.enrolled }}人</span>
          </div>
          <div class="detail-item">
            <label>完成率</label>
            <span>{{ currentTraining.completionRate }}%</span>
          </div>
          <div class="detail-item full-width">
            <label>截止日期</label>
            <span>{{ currentTraining.deadline }}</span>
          </div>
          <div class="detail-item full-width">
            <label>培训说明</label>
            <span>{{ currentTraining.description || '暂无说明' }}</span>
          </div>
        </div>

        <div class="section">
          <h4>培训课程</h4>
          <div class="course-list">
            <div v-for="(course, index) in currentTraining.courses" :key="index" class="course-item">
              <div class="course-check">
                <svg v-if="course.completed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#52b788" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#c0c4cc" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle></svg>
              </div>
              <div class="course-info">
                <span class="course-name">{{ course.name }}</span>
                <span class="course-duration">{{ course.duration }}分钟</span>
              </div>
            </div>
          </div>
        </div>

        <div class="section">
          <h4>参与人员 ({{ currentTraining.participants?.length || 0 }})</h4>
          <div class="table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th>姓名</th>
                  <th>部门</th>
                  <th>学习进度</th>
                  <th>考试得分</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in currentTraining.participants || []" :key="row.name">
                  <td>{{ row.name }}</td>
                  <td>{{ row.department }}</td>
                  <td>
                    <div class="rate-cell">
                      <div class="progress-bar small"><div class="progress-fill" :style="{ width: row.progress + '%' }"></div></div>
                      <span class="rate-text">{{ row.progress }}%</span>
                    </div>
                  </td>
                  <td :class="{ 'score-pass': row.score >= 60 }">{{ row.score ? row.score + '分' : '-' }}</td>
                  <td><span class="tag" :class="'tag-status-' + row.status">{{ row.status === 'completed' ? '已完成' : row.status === 'in_progress' ? '进行中' : '未开始' }}</span></td>
                  <td><button class="action-btn" @click="handleRemindUser(row)">提醒</button></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <template #footer>
        <button class="btn-secondary" @click="showDetail = false">关闭</button>
        <button class="btn-warning" @click="handleAssign(currentTraining)">重新指派</button>
      </template>
    </el-dialog>

    <!-- 创建培训弹窗 -->
    <el-dialog v-model="showCreate" :title="isEdit ? '编辑培训' : '新建合规培训'" width="650px" class="glass-dialog">
      <el-form :model="form" :rules="formRules" ref="formRef" class="dialog-form">
        <div class="form-item">
          <label>培训名称</label>
          <input v-model="form.title" placeholder="如：白色污染治理合规培训" maxlength="100" class="form-input" />
        </div>
        <div class="form-row-2">
          <div class="form-item">
            <label>培训类型</label>
            <select v-model="form.type" class="form-select">
              <option value="video">视频课程</option>
              <option value="document">文档学习</option>
              <option value="exam">在线考试</option>
            </select>
          </div>
          <div class="form-item">
            <label>所属企业</label>
            <select v-model="form.enterprise" class="form-select">
              <option value="">请选择</option>
              <option value="ent1">XX塑料厂</option>
              <option value="ent2">XX合作社</option>
              <option value="ent3">XX超市</option>
            </select>
          </div>
        </div>
        <div class="form-item">
          <label>截止日期</label>
          <input type="datetime-local" v-model="form.deadline" class="form-input" />
        </div>
        <div class="form-item">
          <label>培训说明</label>
          <textarea v-model="form.description" placeholder="培训背景与要求…" maxlength="500" class="form-textarea" rows="3"></textarea>
        </div>
        <div class="form-item">
          <label>选择课程</label>
          <div class="checkbox-group">
            <label class="checkbox-item"><input type="checkbox" value="course1" v-model="form.courseIds" /> 塑料污染防控知识 (60分钟)</label>
            <label class="checkbox-item"><input type="checkbox" value="course2" v-model="form.courseIds" /> 固废法解读 (45分钟)</label>
            <label class="checkbox-item"><input type="checkbox" value="course3" v-model="form.courseIds" /> 农膜管理办法 (30分钟)</label>
          </div>
        </div>
      </el-form>
      <template #footer>
        <button class="btn-secondary" @click="showCreate = false">取消</button>
        <button class="btn-primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '创建培训' }}</button>
      </template>
    </el-dialog>

    <!-- ESG报告弹窗 -->
    <el-dialog v-model="showESG" title="ESG合规报告" width="800px" class="glass-dialog">
      <div class="esg-report">
        <div class="detail-grid">
          <div class="detail-item">
            <label>企业名称</label>
            <span>XX塑料厂</span>
          </div>
          <div class="detail-item">
            <label>报告周期</label>
            <span>2026年第一季度</span>
          </div>
          <div class="detail-item">
            <label>培训完成率</label>
            <span>85%</span>
          </div>
          <div class="detail-item">
            <label>合规检查</label>
            <span class="text-success">通过</span>
          </div>
        </div>
        <div class="esg-chart-placeholder">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#95d5b2" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line></svg>
          <p>ESG数据图表区域</p>
        </div>
      </div>
      <template #footer>
        <button class="btn-secondary" @click="showESG = false">关闭</button>
        <button class="btn-primary">下载报告</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const showCreate = ref(false)
const showDetail = ref(false)
const showESG = ref(false)
const isEdit = ref(false)
const formRef = ref()
const total = ref(3)
const currentTraining = ref(null)

const q = reactive({ page: 1, pageSize: 10, keyword: '', type: '', status: '', enterprise: '', dateRange: [], startDate: '', endDate: '' })

const typeMap = { video: 'primary', document: 'success', exam: 'warning' }
const typeText = { video: '视频', document: '文档', exam: '考试' }
const statusMap = { pending: 'info', in_progress: 'warning', completed: 'success' }
const statusText = { pending: '未开始', in_progress: '进行中', completed: '已完成' }

const stats = reactive([
  { label: '本月培训数', value: '12', gradient: 'linear-gradient(135deg, #1a4d2e, #2d6a4f)', icon: 'video' },
  { label: '培训参与人次', value: '456', gradient: 'linear-gradient(135deg, #2d6a4f, #52b788)', icon: 'users' },
  { label: '完成率', value: '78%', gradient: 'linear-gradient(135deg, #2d6a4f, #95d5b2)', icon: 'check' },
  { label: '待培训员工', value: '38', gradient: 'linear-gradient(135deg, #52b788, #95d5b2)', icon: 'clock' }
])

const list = reactive([
  { id: 1, title: '白色污染治理合规培训', enterprise: 'XX塑料厂', type: 'video', duration: '45 分钟', enrolled: 80, completed: 62, completionRate: 78, status: 'in_progress', deadline: '2026-04-20', description: '塑料污染防治基础知识与合规要求', courses: [
    { name: '塑料污染概述', duration: 20, completed: true },
    { name: '固废法解读', duration: 15, completed: true },
    { name: '合规操作指南', duration: 10, completed: false }
  ], participants: [
    { name: '张三', department: '生产部', progress: 100, score: 85, status: 'completed' },
    { name: '李四', department: '质检部', progress: 66, status: 'in_progress' },
    { name: '王五', department: '仓储部', progress: 0, status: 'pending' }
  ]},
  { id: 2, title: '农膜回收政策解读', enterprise: 'XX合作社', type: 'document', duration: '20 分钟', enrolled: 45, completed: 45, completionRate: 100, status: 'completed', deadline: '2026-03-30', description: '农膜回收相关政策与操作规范', participants: [] },
  { id: 3, title: '第一季度合规考试', enterprise: 'XX超市', type: 'exam', duration: '60 分钟', enrolled: 38, completed: 0, completionRate: 0, status: 'pending', deadline: '2026-04-30', description: '企业合规知识考核', participants: [] }
])

const form = reactive({ title: '', type: 'video', enterprise: '', deadline: '', targetCount: 10, description: '', courseIds: [] })

const formRules = {
  title: [{ required: true, message: '请输入培训名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择培训类型', trigger: 'change' }],
  enterprise: [{ required: true, message: '请选择所属企业', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

const getRateColor = (rate) => {
  if (rate >= 80) return '#52b788'
  if (rate >= 50) return '#f59e0b'
  return '#ef4444'
}

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
  q.type = ''
  q.status = ''
  q.enterprise = ''
  q.startDate = ''
  q.endDate = ''
  handleQuery()
}

const handleView = (row) => {
  currentTraining.value = row
  showDetail.value = true
}

const handleAssign = (row) => {
  ElMessage.info('指派员工功能开发中')
}

const handleRemind = (row) => {
  ElMessage.success(`已向 ${row.enrolled} 名员工发送提醒`)
}

const handleRemindUser = (user) => {
  ElMessage.success(`已向 ${user.name} 发送学习提醒`)
}

const handleExport = (row) => {
  ElMessage.info('导出功能开发中')
}

const handleExportAll = () => {
  ElMessage.info('批量导出台账功能开发中')
}

const handleSubmit = () => {
  showCreate.value = false
  ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
  load()
}

onMounted(load)
</script>

<style lang="scss" scoped>
.train-container {
  padding: 24px;
  min-height: 100vh;
  background: #ffffff;
}

.mb-20 { margin-bottom: 20px; }

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

.header-actions {
  display: flex;
  gap: 12px;
}

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

.btn-warning {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  
  &:hover {
    background: linear-gradient(135deg, #fbbf24, #fcd34d);
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

.form-row-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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
  
  &.full-width {
    grid-column: 1 / -1;
  }
}

.form-input, .form-select, .form-textarea {
  padding: 10px 14px;
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 8px;
  font-size: 14px;
  background: #fff;
  transition: all 0.3s;
  color: #333;
  
  &:focus {
    outline: none;
    border-color: #52b788;
    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
  }
}

.form-select {
  min-width: 120px;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  
  input[type="checkbox"] {
    width: 16px;
    height: 16px;
    accent-color: #52b788;
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
  
  input[type="checkbox"] {
    width: 16px;
    height: 16px;
    accent-color: #52b788;
  }
}

.cell-title {
  font-weight: 500;
  color: #333333;
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  
  &.tag-video {
    background: rgba(82, 183, 136, 0.15);
    color: #2d6a4f;
  }
  &.tag-document {
    background: rgba(82, 183, 136, 0.1);
    color: #52b788;
  }
  &.tag-exam {
    background: rgba(245, 158, 11, 0.15);
    color: #d97706;
  }
  &.tag-status-pending {
    background: rgba(156, 163, 175, 0.15);
    color: #6b7280;
  }
  &.tag-status-in_progress {
    background: rgba(245, 158, 11, 0.15);
    color: #d97706;
  }
  &.tag-status-completed {
    background: rgba(82, 183, 136, 0.15);
    color: #2d6a4f;
  }
}

.enrolled-count {
  color: #666;
  font-size: 13px;
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
  
  &.small {
    width: 100px;
    height: 4px;
  }
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #52b788, #95d5b2);
  border-radius: 3px;
  transition: width 0.3s;
}

.rate-text {
  font-size: 12px;
  color: #666;
  min-width: 36px;
}

.score-pass {
  color: #52b788;
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
  
  &.action-success {
    color: #52b788;
    &:hover { background: rgba(82, 183, 136, 0.15); }
  }
  &.action-warning {
    color: #d97706;
    &:hover { background: rgba(245, 158, 11, 0.1); }
  }
  &.action-info {
    color: #6b7280;
    &:hover { background: rgba(156, 163, 175, 0.1); }
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

.training-detail {
  .section {
    margin-top: 24px;
    
    h4 {
      font-size: 15px;
      font-weight: 600;
      color: #333333;
      margin: 0 0 12px;
      padding-bottom: 8px;
      border-bottom: 2px solid rgba(82, 183, 136, 0.2);
    }
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  
  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 6px;
    
    &.full-width {
      grid-column: 1 / -1;
    }
    
    label {
      font-size: 12px;
      color: #999;
      font-weight: 500;
    }
    
    span {
      font-size: 14px;
      color: #333;
    }
  }
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  background: rgba(82, 183, 136, 0.04);
  border-radius: 10px;
  transition: background 0.2s;
  
  &:hover {
    background: rgba(82, 183, 136, 0.08);
  }
}

.course-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.course-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.course-duration {
  font-size: 12px;
  color: #999;
}

.text-success {
  color: #52b788;
  font-weight: 500;
}

.esg-chart-placeholder {
  height: 200px;
  background: linear-gradient(135deg, rgba(82, 183, 136, 0.05), rgba(149, 213, 178, 0.1));
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  color: #95d5b2;
  font-size: 14px;
}

.dialog-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

// Dialog 样式
:deep(.glass-dialog) {
  .el-dialog {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 8px 32px rgba(82, 183, 136, 0.15);
  }
  
  .el-dialog__header {
    padding: 20px 24px;
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #333333;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid rgba(82, 183, 136, 0.1);
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style>
