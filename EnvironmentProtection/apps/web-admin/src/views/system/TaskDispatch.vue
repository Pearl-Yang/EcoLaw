<template>
  <div class="dispatch-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">任务下发管理</h2>
        <p class="page-sub">管理任务下发链路、进度追踪与下发统计</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.totalDispatches || 0 }}</span>
          <span class="stat-label">下发总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.pendingCount || 0 }}</span>
          <span class="stat-label">待处理</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.inProgressCount || 0 }}</span>
          <span class="stat-label">进行中</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.completedCount || 0 }}</span>
          <span class="stat-label">已完成</span>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="glass-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我下发的" name="dispatched">
          <template #label>
            <span class="tab-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <line x1="22" y1="2" x2="11" y2="13"/>
                <polygon points="22 2 15 22 11 13 2 9 22 2"/>
              </svg>
              我下发的
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="下发给我的" name="received">
          <template #label>
            <span class="tab-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <polyline points="9 11 12 14 22 4"/>
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
              </svg>
              下发给我的
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="下发链路" name="chain">
          <template #label>
            <span class="tab-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="18" cy="18" r="3"/>
                <circle cx="6" cy="6" r="3"/>
                <path d="M6 21V9a9 9 0 0 0 9 9"/>
              </svg>
              下发链路
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <!-- 我下发的列表 -->
      <div v-if="activeTab === 'dispatched'">
        <el-table :data="dispatchedList" stripe v-loading="loading" :row-class-name="getRowClassName">
          <el-table-column prop="taskTitle" label="任务名称" min-width="180" />
          <el-table-column prop="targetOrgName" label="下发至" min-width="150" />
          <el-table-column prop="dispatchLevel" label="层级" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">第{{ row.dispatchLevel }}级</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetCount" label="目标数量" width="100" align="center" />
          <el-table-column prop="completedCount" label="已完成" width="100" align="center" />
          <el-table-column prop="progress" label="进度" width="150">
            <template #default="{ row }">
              <el-progress :percentage="row.progress || 0" :status="getProgressStatus(row.progress)" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deadline" label="截止时间" width="160" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleViewChain(row)">链路</el-button>
              <el-button v-if="row.status === 'accepted' || row.status === 'in_progress'" type="primary" link size="small" @click="handleDelegate(row)">转发</el-button>
              <el-button type="primary" link size="small" @click="handleViewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 下发给我的列表 -->
      <div v-if="activeTab === 'received'">
        <el-table :data="receivedList" stripe v-loading="loading">
          <el-table-column prop="sourceOrgName" label="下发方" min-width="150" />
          <el-table-column prop="taskTitle" label="任务名称" min-width="180" />
          <el-table-column prop="targetCount" label="目标数量" width="100" align="center" />
          <el-table-column prop="completedCount" label="已完成" width="100" align="center" />
          <el-table-column prop="progress" label="进度" width="150">
            <template #default="{ row }">
              <el-progress :percentage="row.progress || 0" :status="getProgressStatus(row.progress)" />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deadline" label="截止时间" width="160" />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 'pending'" type="success" link size="small" @click="handleAccept(row)">接收</el-button>
              <el-button v-if="row.status === 'pending'" type="danger" link size="small" @click="handleReject(row)">拒绝</el-button>
              <el-button v-if="row.status === 'accepted' || row.status === 'in_progress'" type="primary" link size="small" @click="handleReport(row)">上报进度</el-button>
              <el-button v-if="row.status === 'accepted' || row.status === 'in_progress'" type="success" link size="small" @click="handleComplete(row)">完成</el-button>
              <el-button type="primary" link size="small" @click="handleViewDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 下发链路 -->
      <div v-if="activeTab === 'chain'" class="chain-view">
        <div class="chain-header">
          <el-select v-model="selectedTaskId" placeholder="选择任务" filterable @change="fetchDispatchChain">
            <el-option v-for="task in taskOptions" :key="task.id" :label="task.title" :value="task.id" />
          </el-select>
        </div>
        <div v-if="dispatchChain.length > 0" class="chain-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in flatChain"
              :key="item.id"
              :color="getTimelineColor(item.status)"
              :hollow="item.status === 'pending'"
            >
              <div class="timeline-item">
                <div class="timeline-header">
                  <span class="org-name">{{ item.sourceOrgName || '系统' }}</span>
                  <span class="arrow">→</span>
                  <span class="org-name target">{{ item.targetOrgName }}</span>
                </div>
                <div class="timeline-meta">
                  <el-tag size="small" :type="getStatusType(item.status)">
                    {{ getStatusText(item.status) }}
                  </el-tag>
                  <span class="progress-text">进度: {{ item.completedCount || 0 }}/{{ item.targetCount || 0 }}</span>
                  <span class="time">{{ item.dispatchTime }}</span>
                </div>
                <div class="timeline-progress">
                  <el-progress :percentage="item.progress || 0" :status="getProgressStatus(item.progress)" :show-text="false" />
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
        <el-empty v-else description="请选择任务查看下发链路" />
      </div>
    </div>

    <!-- 下发详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="下发详情" width="700px" class="glass-dialog">
      <el-descriptions :column="2" border v-if="currentDispatch">
        <el-descriptions-item label="任务名称">{{ currentDispatch.taskTitle }}</el-descriptions-item>
        <el-descriptions-item label="下发方">{{ currentDispatch.sourceOrgName }}</el-descriptions-item>
        <el-descriptions-item label="接收方">{{ currentDispatch.targetOrgName }}</el-descriptions-item>
        <el-descriptions-item label="下发层级">第{{ currentDispatch.dispatchLevel }}级</el-descriptions-item>
        <el-descriptions-item label="目标数量">{{ currentDispatch.targetCount }}</el-descriptions-item>
        <el-descriptions-item label="已完成数量">{{ currentDispatch.completedCount }}</el-descriptions-item>
        <el-descriptions-item label="完成进度">{{ currentDispatch.progress }}%</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDispatch.status)">
            {{ getStatusText(currentDispatch.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ currentDispatch.deadline }}</el-descriptions-item>
        <el-descriptions-item label="下发时间">{{ currentDispatch.dispatchTime }}</el-descriptions-item>
        <el-descriptions-item label="接收时间" :span="2">{{ currentDispatch.acceptTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentDispatch.notes || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 转发弹窗 -->
    <el-dialog v-model="delegateDialogVisible" title="向下级转发任务" width="600px" class="glass-dialog">
      <el-form :model="delegateForm" label-width="100px">
        <el-form-item label="选择下级组织">
          <el-tree
            ref="orgTreeRef"
            :data="orgTreeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            :expand-on-click-node="false"
            show-checkbox
            check-strictly
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="delegateForm.notes" type="textarea" :rows="3" placeholder="可选备注说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDelegateSubmit">确认转发</el-button>
      </template>
    </el-dialog>

    <!-- 进度上报弹窗 -->
    <el-dialog v-model="progressDialogVisible" title="上报进度" width="500px" class="glass-dialog">
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="已完成数量">
          <el-input-number v-model="progressForm.completedCount" :min="0" :max="currentDispatch?.targetCount || 100" />
        </el-form-item>
        <el-form-item label="进度">
          <el-progress :percentage="progressPercentage" :status="getProgressStatus(progressPercentage)" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="progressForm.notes" type="textarea" :rows="3" placeholder="可选备注说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProgressSubmit">确认上报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyDispatchedTasks, getMyReceivedTasks, getDispatchChain, getDispatchDetail, delegateTask, acceptDispatch, rejectDispatch, reportProgress, completeDispatch, getDispatchStatistics, getDispatchableTargets } from '@/api/dispatch.js'
import { getOrganizationTree } from '@/api/organization.js'
import { getTaskList } from '@/api/task.js'

const activeTab = ref('dispatched')
const loading = ref(false)
const dispatchedList = ref([])
const receivedList = ref([])
const dispatchChain = ref([])
const taskOptions = ref([])
const selectedTaskId = ref('')
const currentDispatch = ref(null)
const orgTreeData = ref([])

const statistics = reactive({
  totalDispatches: 0,
  pendingCount: 0,
  inProgressCount: 0,
  completedCount: 0
})

const detailDialogVisible = ref(false)
const delegateDialogVisible = ref(false)
const progressDialogVisible = ref(false)
const orgTreeRef = ref(null)

const delegateForm = reactive({
  notes: ''
})

const progressForm = reactive({
  completedCount: 0,
  notes: ''
})

const progressPercentage = computed(() => {
  if (!currentDispatch.value || !currentDispatch.value.targetCount) return 0
  return Math.round((progressForm.completedCount / currentDispatch.value.targetCount) * 100)
})

const flatChain = computed(() => {
  const result = []
  function flatten(items, level = 0) {
    items.forEach(item => {
      result.push(item)
      if (item.children && item.children.length > 0) {
        flatten(item.children, level + 1)
      }
    })
  }
  flatten(dispatchChain.value)
  return result
})

onMounted(() => {
  fetchOrgTree()
  fetchTasks()
  fetchData()
  fetchStatistics()
})

async function fetchOrgTree() {
  try {
    const res = await getOrganizationTree()
    orgTreeData.value = res.data || []
  } catch (error) {
    console.error('获取组织树失败:', error)
  }
}

async function fetchTasks() {
  try {
    const res = await getTaskList({ page: 1, pageSize: 100 })
    taskOptions.value = res.data || []
  } catch (error) {
    console.error('获取任务列表失败:', error)
  }
}

async function fetchData() {
  loading.value = true
  try {
    if (activeTab.value === 'dispatched') {
      const res = await getMyDispatchedTasks('')
      dispatchedList.value = res.data || []
    } else if (activeTab.value === 'received') {
      const res = await getMyReceivedTasks('', '')
      receivedList.value = res.data || []
    }
  } catch (error) {
    console.error('获取下发列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchStatistics() {
  try {
    const res = await getDispatchStatistics('', '')
    Object.assign(statistics, res.data || {})
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

async function fetchDispatchChain(taskId) {
  if (!taskId) return
  try {
    const res = await getDispatchChain(taskId)
    dispatchChain.value = res.data || []
  } catch (error) {
    console.error('获取下发链路失败:', error)
  }
}

function handleTabChange() {
  fetchData()
}

function handleViewDetail(row) {
  currentDispatch.value = row
  detailDialogVisible.value = true
}

function handleViewChain(row) {
  activeTab.value = 'chain'
  selectedTaskId.value = row.taskId
  fetchDispatchChain(row.taskId)
}

function handleAccept(row) {
  ElMessageBox.confirm('确认接收该任务？', '提示').then(async () => {
    try {
      await acceptDispatch(row.id)
      ElMessage.success('任务已接收')
      fetchData()
      fetchStatistics()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

function handleReject(row) {
  ElMessageBox.prompt('请输入拒绝原因', '拒绝任务', {
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      await rejectDispatch(row.id, value)
      ElMessage.success('任务已拒绝')
      fetchData()
      fetchStatistics()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

function handleReport(row) {
  currentDispatch.value = row
  progressForm.completedCount = row.completedCount || 0
  progressForm.notes = ''
  progressDialogVisible.value = true
}

async function handleProgressSubmit() {
  try {
    await reportProgress(currentDispatch.value.id, progressForm.completedCount, progressForm.notes)
    ElMessage.success('进度上报成功')
    progressDialogVisible.value = false
    fetchData()
    fetchStatistics()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

function handleComplete(row) {
  ElMessageBox.confirm('确认完成该任务？', '提示').then(async () => {
    try {
      await completeDispatch(row.id)
      ElMessage.success('任务已完成')
      fetchData()
      fetchStatistics()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  })
}

function handleDelegate(row) {
  currentDispatch.value = row
  delegateForm.notes = ''
  delegateDialogVisible.value = true
}

async function handleDelegateSubmit() {
  const checkedNodes = orgTreeRef.value?.getCheckedNodes() || []
  const orgIds = checkedNodes.map(n => n.id)
  if (orgIds.length === 0) {
    ElMessage.warning('请选择至少一个下级组织')
    return
  }
  try {
    await delegateTask(currentDispatch.value.id, orgIds)
    ElMessage.success('任务转发成功')
    delegateDialogVisible.value = false
    fetchData()
    fetchStatistics()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

function getStatusText(status) {
  const map = {
    pending: '待接收',
    accepted: '已接收',
    rejected: '已拒绝',
    in_progress: '进行中',
    completed: '已完成',
    delegated: '已转发',
    overdue: '已逾期'
  }
  return map[status] || status
}

function getStatusType(status) {
  const map = {
    pending: 'warning',
    accepted: 'primary',
    rejected: 'danger',
    in_progress: 'info',
    completed: 'success',
    delegated: 'info',
    overdue: 'danger'
  }
  return map[status] || 'info'
}

function getProgressStatus(progress) {
  if (progress >= 100) return 'success'
  if (progress >= 80) return ''
  if (progress >= 50) return 'warning'
  return 'exception'
}

function getTimelineColor(status) {
  const map = {
    pending: '#909399',
    accepted: '#409EFF',
    rejected: '#F56C6C',
    in_progress: '#E6A23C',
    completed: '#67C23A',
    delegated: '#909399',
    overdue: '#F56C6C'
  }
  return map[status] || '#909399'
}

function getRowClassName({ row }) {
  if (row.status === 'overdue') return 'overdue-row'
  return ''
}
</script>

<style scoped>
.dispatch-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.page-sub {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
  color: white;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-label svg {
  width: 16px;
  height: 16px;
}

.chain-view {
  padding: 20px 0;
}

.chain-header {
  margin-bottom: 20px;
}

.chain-timeline {
  max-height: 500px;
  overflow-y: auto;
}

.timeline-item {
  padding: 8px 0;
}

.timeline-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.org-name {
  font-weight: 500;
  color: var(--text-primary);
}

.org-name.target {
  color: #409EFF;
}

.arrow {
  color: var(--text-secondary);
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.progress-text {
  font-size: 13px;
  color: var(--text-secondary);
}

.time {
  font-size: 13px;
  color: var(--text-secondary);
}

.timeline-progress {
  width: 300px;
}

:deep(.overdue-row) {
  background-color: rgba(245, 108, 108, 0.1);
}

:deep(.el-button svg) {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}
</style>
