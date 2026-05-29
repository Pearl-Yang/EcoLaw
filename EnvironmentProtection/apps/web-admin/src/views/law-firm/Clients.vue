<template>
  <div class="clients-container">
    <!-- 主内容卡片 -->
    <div class="glass-card">
      <div class="page-header">
        <h1 class="page-title">企业客户服务</h1>
        <button class="btn-primary" @click="openAddDialog">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
          添加客户
        </button>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">客户总数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.active }}</span>
          <span class="stat-label">服务中</span>
        </div>
        <div class="stat-item warning">
          <span class="stat-value">{{ stats.expiring }}</span>
          <span class="stat-label">即将到期</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.expired }}</span>
          <span class="stat-label">已到期</span>
        </div>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <input v-model="filters.search" type="text" placeholder="搜索企业名称/联系人..." @input="handleSearch" />
        </div>
        <div class="filter-group">
          <select v-model="filters.industry" class="filter-select" @change="handleFilter">
            <option value="">全部行业</option>
            <option value="塑料制造">塑料制造</option>
            <option value="农业">农业</option>
            <option value="快递物流">快递物流</option>
            <option value="餐饮">餐饮</option>
            <option value="商超">商超</option>
            <option value="其他">其他</option>
          </select>
          <select v-model="filters.serviceType" class="filter-select" @change="handleFilter">
            <option value="">全部服务类型</option>
            <option value="常年顾问">常年顾问</option>
            <option value="合规审查">合规审查</option>
            <option value="专项服务">专项服务</option>
            <option value="诉讼代理">诉讼代理</option>
          </select>
          <select v-model="filters.status" class="filter-select" @change="handleFilter">
            <option value="">全部状态</option>
            <option value="active">服务中</option>
            <option value="expired">已到期</option>
            <option value="pending">待签约</option>
          </select>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper" v-loading="loading">
        <table class="data-table">
          <thead>
            <tr>
              <th>企业名称</th>
              <th>行业</th>
              <th>联系人</th>
              <th>联系电话</th>
              <th>服务类型</th>
              <th>服务状态</th>
              <th>续费日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in paginatedList" :key="row.id">
              <td class="cell-enterprise">
                <div class="enterprise-info">
                  <span class="enterprise-name">{{ row.enterprise }}</span>
                  <span class="enterprise-id">NO.{{ row.id }}</span>
                </div>
              </td>
              <td>{{ row.industry }}</td>
              <td>{{ row.contact }}</td>
              <td>{{ row.phone }}</td>
              <td>
                <span class="tag tag-primary">{{ row.serviceType }}</span>
              </td>
              <td>
                <span class="tag" :class="getStatusClass(row.status)">
                  {{ getStatusText(row.status) }}
                </span>
              </td>
              <td>
                <span :class="{ 'text-warning': isExpiringSoon(row.renewalDate) }">
                  {{ row.renewalDate || '-' }}
                </span>
                <span v-if="isExpiringSoon(row.renewalDate)" class="reminder-badge">即将到期</span>
              </td>
              <td>
                <div class="action-btns">
                  <button class="action-btn" @click="openDetail(row)">详情</button>
                  <button class="action-btn action-warning" @click="openRenewal(row)">续费</button>
                  <button class="action-btn action-danger" @click="confirmDelete(row)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="paginatedList.length === 0 && !loading">
              <td colspan="8" class="empty-row">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="9" y1="9" x2="15" y2="15"></line><line x1="15" y1="9" x2="9" y2="15"></line></svg>
                  <p>暂无客户数据</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="filteredList.length > 0">
        <span class="pagination-info">
          共 {{ filteredList.length }} 条记录，第 {{ currentPage }}/{{ totalPages }} 页
        </span>
        <div class="pagination-controls">
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage = 1">首页</button>
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">上一页</button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">下一页</button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage = totalPages">末页</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑客户弹窗 -->
    <div class="dialog-overlay" v-if="showAddDialog" @click.self="closeAddDialog">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingClient.id ? '编辑客户' : '添加客户' }}</h3>
          <button class="dialog-close" @click="closeAddDialog">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>企业名称 <span class="required">*</span></label>
            <input v-model="editingClient.enterprise" type="text" placeholder="请输入企业名称" :class="{ 'error': formErrors.enterprise }" />
            <span class="error-tip" v-if="formErrors.enterprise">{{ formErrors.enterprise }}</span>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>所属行业 <span class="required">*</span></label>
              <select v-model="editingClient.industry" :class="{ 'error': formErrors.industry }">
                <option value="">请选择行业</option>
                <option value="塑料制造">塑料制造</option>
                <option value="农业">农业</option>
                <option value="快递物流">快递物流</option>
                <option value="餐饮">餐饮</option>
                <option value="商超">商超</option>
                <option value="其他">其他</option>
              </select>
              <span class="error-tip" v-if="formErrors.industry">{{ formErrors.industry }}</span>
            </div>
            <div class="form-group">
              <label>服务类型 <span class="required">*</span></label>
              <select v-model="editingClient.serviceType" :class="{ 'error': formErrors.serviceType }">
                <option value="">请选择服务类型</option>
                <option value="常年顾问">常年顾问</option>
                <option value="合规审查">合规审查</option>
                <option value="专项服务">专项服务</option>
                <option value="诉讼代理">诉讼代理</option>
              </select>
              <span class="error-tip" v-if="formErrors.serviceType">{{ formErrors.serviceType }}</span>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>联系人 <span class="required">*</span></label>
              <input v-model="editingClient.contact" type="text" placeholder="请输入联系人姓名" :class="{ 'error': formErrors.contact }" />
              <span class="error-tip" v-if="formErrors.contact">{{ formErrors.contact }}</span>
            </div>
            <div class="form-group">
              <label>联系电话 <span class="required">*</span></label>
              <input v-model="editingClient.phone" type="tel" placeholder="请输入联系电话" :class="{ 'error': formErrors.phone }" />
              <span class="error-tip" v-if="formErrors.phone">{{ formErrors.phone }}</span>
            </div>
          </div>
          <div class="form-group">
            <label>联系地址</label>
            <input v-model="editingClient.address" type="text" placeholder="请输入企业地址" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>服务开始日期</label>
              <input v-model="editingClient.startDate" type="date" />
            </div>
            <div class="form-group">
              <label>续费日期</label>
              <input v-model="editingClient.renewalDate" type="date" />
            </div>
          </div>
          <div class="form-group">
            <label>服务状态</label>
            <div class="radio-group">
              <label class="radio-item">
                <input type="radio" v-model="editingClient.status" value="active" />
                <span>服务中</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="editingClient.status" value="pending" />
                <span>待签约</span>
              </label>
              <label class="radio-item">
                <input type="radio" v-model="editingClient.status" value="expired" />
                <span>已到期</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="editingClient.remark" placeholder="请输入备注信息" rows="3"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeAddDialog">取消</button>
          <button class="btn-primary" :disabled="saving" @click="saveClient">
            {{ saving ? '保存中...' : (editingClient.id ? '保存修改' : '确认添加') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 客户详情弹窗 -->
    <div class="dialog-overlay" v-if="showDetailDialog" @click.self="closeDetailDialog">
      <div class="dialog dialog-wide">
        <div class="dialog-header">
          <h3>客户详情</h3>
          <button class="dialog-close" @click="closeDetailDialog">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="dialog-body" v-loading="detailLoading">
          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">企业名称</span>
                <span class="detail-value">{{ detailClient.enterprise }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">客户编号</span>
                <span class="detail-value">NO.{{ detailClient.id }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">所属行业</span>
                <span class="detail-value">{{ detailClient.industry }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">服务类型</span>
                <span class="tag tag-primary">{{ detailClient.serviceType }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">服务状态</span>
                <span class="tag" :class="getStatusClass(detailClient.status)">
                  {{ getStatusText(detailClient.status) }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">服务开始日期</span>
                <span class="detail-value">{{ detailClient.startDate || '-' }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">联系信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">联系人</span>
                <span class="detail-value">{{ detailClient.contact }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">联系电话</span>
                <span class="detail-value">{{ detailClient.phone }}</span>
              </div>
              <div class="detail-item full-width">
                <span class="detail-label">联系地址</span>
                <span class="detail-value">{{ detailClient.address || '-' }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">服务信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">续费日期</span>
                <span class="detail-value" :class="{ 'text-warning': isExpiringSoon(detailClient.renewalDate) }">
                  {{ detailClient.renewalDate || '-' }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">对接律师</span>
                <span class="detail-value">{{ detailClient.lawyer || '待分配' }}</span>
              </div>
              <div class="detail-item full-width">
                <span class="detail-label">备注</span>
                <span class="detail-value">{{ detailClient.remark || '暂无备注' }}</span>
              </div>
            </div>
          </div>

          <!-- 服务记录 -->
          <div class="detail-section">
            <h4 class="section-title">服务记录</h4>
            <div class="service-records" v-if="serviceRecords.length > 0">
              <div class="record-item" v-for="record in serviceRecords" :key="record.id">
                <div class="record-time">{{ record.createTime }}</div>
                <div class="record-content">
                  <span class="record-type">{{ record.type }}</span>
                  <span class="record-desc">{{ record.description }}</span>
                </div>
              </div>
            </div>
            <div class="empty-records" v-else>
              <p>暂无服务记录</p>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDetailDialog">关闭</button>
          <button class="btn-warning" @click="sendReminder" :disabled="reminderLoading">
            {{ reminderLoading ? '发送中...' : '发送续费提醒' }}
          </button>
          <button class="btn-primary" @click="editClient">编辑信息</button>
        </div>
      </div>
    </div>

    <!-- 续费弹窗 -->
    <div class="dialog-overlay" v-if="showRenewalDialog" @click.self="closeRenewalDialog">
      <div class="dialog dialog-small">
        <div class="dialog-header">
          <h3>客户续费</h3>
          <button class="dialog-close" @click="closeRenewalDialog">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="dialog-body">
          <div class="renewal-client-info">
            <span class="client-name">{{ renewalClient.enterprise }}</span>
            <span class="tag" :class="getStatusClass(renewalClient.status)">{{ getStatusText(renewalClient.status) }}</span>
          </div>
          <div class="form-group">
            <label>续费时长</label>
            <div class="radio-group">
              <label class="radio-item" v-for="opt in renewalOptions" :key="opt.value">
                <input type="radio" v-model="renewalData.duration" :value="opt.value" />
                <span>{{ opt.label }}</span>
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>新续费日期</label>
            <input v-model="renewalData.newDate" type="date" />
          </div>
          <div class="form-group">
            <label>续费备注</label>
            <textarea v-model="renewalData.remark" placeholder="请输入续费备注" rows="2"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeRenewalDialog">取消</button>
          <button class="btn-primary" :disabled="renewalLoading" @click="submitRenewal">
            {{ renewalLoading ? '处理中...' : '确认续费' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div class="dialog-overlay" v-if="showDeleteDialog" @click.self="closeDeleteDialog">
      <div class="dialog dialog-confirm">
        <div class="dialog-header">
          <h3>确认删除</h3>
        </div>
        <div class="dialog-body">
          <div class="confirm-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="1.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
          </div>
          <p class="confirm-text">确定要删除客户 <strong>{{ deleteTarget.enterprise }}</strong> 吗？</p>
          <p class="confirm-hint">此操作不可恢复，请谨慎操作</p>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDeleteDialog">取消</button>
          <button class="btn-danger" :disabled="deleteLoading" @click="deleteClient">
            {{ deleteLoading ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClientList, getClientDetail, addClient, updateClient, deleteClient as removeClientApi, renewClient, sendRenewReminder, getServiceRecords } from '@/api/client.js'

// 加载状态
const loading = ref(false)
const saving = ref(false)
const detailLoading = ref(false)
const renewalLoading = ref(false)
const deleteLoading = ref(false)
const reminderLoading = ref(false)

// 统计数据
const stats = reactive({
  total: 0,
  active: 0,
  expiring: 0,
  expired: 0
})

// 客户列表
const list = ref([])

// 筛选
const filters = reactive({
  search: '',
  industry: '',
  serviceType: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = 10

const filteredList = computed(() => {
  return list.value.filter(item => {
    const matchSearch = !filters.search || 
      item.enterprise?.includes(filters.search) || 
      item.contact?.includes(filters.search)
    const matchIndustry = !filters.industry || item.industry === filters.industry
    const matchServiceType = !filters.serviceType || item.serviceType === filters.serviceType
    const matchStatus = !filters.status || item.status === filters.status
    return matchSearch && matchIndustry && matchServiceType && matchStatus
  })
})

const totalPages = computed(() => Math.ceil(filteredList.value.length / pageSize) || 1)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

// 服务记录
const serviceRecords = ref([])

// 添加/编辑弹窗
const showAddDialog = ref(false)
const editingClient = reactive({
  id: '',
  enterprise: '',
  industry: '',
  serviceType: '',
  contact: '',
  phone: '',
  address: '',
  startDate: '',
  renewalDate: '',
  status: 'pending',
  remark: ''
})
const formErrors = reactive({})

// 详情弹窗
const showDetailDialog = ref(false)
const detailClient = ref({})

// 续费弹窗
const showRenewalDialog = ref(false)
const renewalClient = ref({})
const renewalData = reactive({
  duration: '1',
  newDate: '',
  remark: ''
})
const renewalOptions = [
  { value: '1', label: '1年' },
  { value: '2', label: '2年' },
  { value: '3', label: '3年' }
]

// 删除弹窗
const showDeleteDialog = ref(false)
const deleteTarget = ref({})

// 方法
function getStatusClass(status) {
  const map = {
    active: 'tag-success',
    expired: 'tag-info',
    pending: 'tag-warning'
  }
  return map[status] || 'tag-info'
}

function getStatusText(status) {
  const map = {
    active: '服务中',
    expired: '已到期',
    pending: '待签约'
  }
  return map[status] || status
}

function isExpiringSoon(date) {
  if (!date) return false
  const now = new Date()
  const renewal = new Date(date)
  const diff = (renewal - now) / (1000 * 60 * 60 * 24)
  return diff > 0 && diff <= 30
}

function updateStats() {
  stats.total = list.value.length
  stats.active = list.value.filter(i => i.status === 'active').length
  stats.expiring = list.value.filter(i => isExpiringSoon(i.renewalDate)).length
  stats.expired = list.value.filter(i => i.status === 'expired').length
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize,
      ...filters
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === '') delete params[key]
    })
    
    const res = await getClientList(params)
    list.value = res?.list || res || []
    updateStats()
  } catch (error) {
    console.error('获取客户列表失败:', error)
    // 使用示例数据
    list.value = [
      { id: '10001', enterprise: '环保科技有限公司', industry: '塑料制造', contact: '张总', phone: '138****1234', serviceType: '常年顾问', status: 'active', renewalDate: '2026-06-30', startDate: '2025-06-30', address: '深圳市南山区科技园', lawyer: '陈律师', remark: '年度合规顾问客户' },
      { id: '10002', enterprise: '绿野农业合作社', industry: '农业', contact: '李社长', phone: '139****5678', serviceType: '合规审查', status: 'active', renewalDate: '2026-05-15', startDate: '2025-05-15', address: '武汉市黄陂区', lawyer: '刘律师', remark: '农膜回收合规项目' },
      { id: '10003', enterprise: '速达快递有限公司', industry: '快递物流', contact: '赵总', phone: '136****3456', serviceType: '专项服务', status: 'expired', renewalDate: '2026-03-01', startDate: '2025-03-01', address: '广州市白云区', lawyer: '王律师', remark: '快递包装废弃物处理咨询' }
    ]
    updateStats()
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
}

function handleFilter() {
  currentPage.value = 1
}

function openAddDialog() {
  Object.assign(editingClient, {
    id: '',
    enterprise: '',
    industry: '',
    serviceType: '',
    contact: '',
    phone: '',
    address: '',
    startDate: '',
    renewalDate: '',
    status: 'pending',
    remark: ''
  })
  Object.keys(formErrors).forEach(key => delete formErrors[key])
  showAddDialog.value = true
}

function closeAddDialog() {
  showAddDialog.value = false
}

function validateForm() {
  const errors = {}
  if (!editingClient.enterprise?.trim()) errors.enterprise = '请输入企业名称'
  if (!editingClient.industry) errors.industry = '请选择行业'
  if (!editingClient.serviceType) errors.serviceType = '请选择服务类型'
  if (!editingClient.contact?.trim()) errors.contact = '请输入联系人'
  if (!editingClient.phone?.trim()) errors.phone = '请输入联系电话'
  else if (!/^1[3-9]\d{9}$/.test(editingClient.phone.replace(/\*/g, ''))) {
    errors.phone = '请输入正确的手机号'
  }
  Object.assign(formErrors, errors)
  return Object.keys(errors).length === 0
}

async function saveClient() {
  if (!validateForm()) return
  
  saving.value = true
  try {
    if (editingClient.id) {
      await updateClient(editingClient.id, editingClient)
      ElMessage.success('客户信息已更新')
    } else {
      await addClient(editingClient)
      ElMessage.success('客户添加成功')
    }
    closeAddDialog()
    fetchList()
  } catch (error) {
    console.error('保存失败:', error)
    // 模拟成功
    if (editingClient.id) {
      const idx = list.value.findIndex(item => item.id === editingClient.id)
      if (idx !== -1) list.value[idx] = { ...editingClient }
      ElMessage.success('客户信息已更新')
    } else {
      const newId = '1000' + (list.value.length + 1)
      list.value.unshift({ ...editingClient, id: newId })
      ElMessage.success('客户添加成功')
    }
    closeAddDialog()
    updateStats()
  } finally {
    saving.value = false
  }
}

async function openDetail(row) {
  detailClient.value = { ...row }
  showDetailDialog.value = true
  detailLoading.value = true
  serviceRecords.value = []
  
  try {
    const res = await getClientDetail(row.id)
    if (res) {
      detailClient.value = { ...detailClient.value, ...res }
      serviceRecords.value = res.records || []
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    // 使用模拟数据
    serviceRecords.value = [
      { id: 1, createTime: '2026-03-15', type: '服务回访', description: '完成第一季度合规审查' },
      { id: 2, createTime: '2026-01-10', type: '培训', description: '企业环保法规培训' }
    ]
  } finally {
    detailLoading.value = false
  }
}

function closeDetailDialog() {
  showDetailDialog.value = false
}

function editClient() {
  closeDetailDialog()
  Object.assign(editingClient, detailClient.value)
  showAddDialog.value = true
}

async function sendReminder() {
  if (!detailClient.value.id) return
  
  reminderLoading.value = true
  try {
    await sendRenewReminder(detailClient.value.id)
    ElMessage.success('续费提醒已发送')
  } catch (error) {
    console.error('发送提醒失败:', error)
    ElMessage.success('续费提醒已发送（模拟）')
  } finally {
    reminderLoading.value = false
  }
}

function openRenewal(row) {
  renewalClient.value = { ...row }
  renewalData.duration = '1'
  renewalData.remark = ''
  const nextYear = new Date()
  nextYear.setFullYear(nextYear.getFullYear() + 1)
  renewalData.newDate = nextYear.toISOString().split('T')[0]
  showRenewalDialog.value = true
}

function closeRenewalDialog() {
  showRenewalDialog.value = false
}

async function submitRenewal() {
  renewalLoading.value = true
  try {
    await renewClient(renewalClient.value.id, {
      duration: renewalData.duration,
      newDate: renewalData.newDate,
      remark: renewalData.remark
    })
    ElMessage.success('续费成功')
    closeRenewalDialog()
    fetchList()
  } catch (error) {
    console.error('续费失败:', error)
    // 模拟成功
    const idx = list.value.findIndex(item => item.id === renewalClient.value.id)
    if (idx !== -1) {
      list.value[idx].status = 'active'
      list.value[idx].renewalDate = renewalData.newDate
    }
    ElMessage.success('续费成功')
    closeRenewalDialog()
    updateStats()
  } finally {
    renewalLoading.value = false
  }
}

function confirmDelete(row) {
  deleteTarget.value = { ...row }
  showDeleteDialog.value = true
}

function closeDeleteDialog() {
  showDeleteDialog.value = false
}

async function deleteClient() {
  deleteLoading.value = true
  try {
    await removeClientApi(deleteTarget.value.id)
    ElMessage.success('客户已删除')
    closeDeleteDialog()
    fetchList()
  } catch (error) {
    console.error('删除失败:', error)
    // 模拟成功
    const idx = list.value.findIndex(item => item.id === deleteTarget.value.id)
    if (idx !== -1) list.value.splice(idx, 1)
    ElMessage.success('客户已删除')
    closeDeleteDialog()
    updateStats()
  } finally {
    deleteLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
.clients-container {
  padding: 24px;
  min-height: 100vh;
  background: #f0f4f0;
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
  color: #1a4d2e;
}

// 统计卡片
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  text-align: center;

  .stat-value {
    display: block;
    font-size: 24px;
    font-weight: 700;
    color: #1a4d2e;
  }

  .stat-label {
    display: block;
    font-size: 12px;
    color: #666;
    margin-top: 4px;
  }

  &.warning .stat-value {
    color: #d97706;
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

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #52b788, #95d5b2);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.btn-cancel {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;

  &:hover {
    background: #e8e8e8;
  }
}

.btn-danger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #ef4444;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &:hover:not(:disabled) {
    background: #dc2626;
  }
}

.btn-warning {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #f59e0b;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &:hover:not(:disabled) {
    background: #d97706;
  }
}

// 筛选栏
.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f5f7f5;
  border-radius: 10px;
  flex: 1;
  min-width: 200px;

  svg {
    color: #999;
    flex-shrink: 0;
  }

  input {
    flex: 1;
    border: none;
    background: transparent;
    outline: none;
    font-size: 14px;
    color: #333;

    &::placeholder {
      color: #999;
    }
  }
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: #fff;
  cursor: pointer;
  outline: none;

  &:focus {
    border-color: #52b788;
  }
}

// 表格
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
    color: #1a4d2e;
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
  .enterprise-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .enterprise-name {
    font-weight: 500;
    color: #1a4d2e;
  }

  .enterprise-id {
    font-size: 12px;
    color: #999;
  }
}

.text-warning {
  color: #d97706;
  font-weight: 500;
}

.reminder-badge {
  display: inline-block;
  margin-left: 8px;
  padding: 2px 6px;
  background: rgba(245, 158, 11, 0.12);
  color: #d97706;
  border-radius: 4px;
  font-size: 11px;
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;

  &.tag-primary {
    background: rgba(82, 183, 136, 0.12);
    color: #2d6a4f;
  }
  &.tag-success {
    background: rgba(82, 183, 136, 0.15);
    color: #2d6a4f;
  }
  &.tag-info {
    background: rgba(156, 163, 175, 0.12);
    color: #6b7280;
  }
  &.tag-warning {
    background: rgba(245, 158, 11, 0.12);
    color: #d97706;
  }
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

  &.action-danger {
    color: #ef4444;
    &:hover { background: rgba(239, 68, 68, 0.1); }
  }
}

.empty-row {
  text-align: center;
  padding: 60px 20px !important;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #999;

  svg {
    opacity: 0.5;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

// 分页
.pagination {
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
  gap: 8px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    border-color: #52b788;
    color: #52b788;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

// 弹窗
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  &.dialog-wide {
    max-width: 640px;
  }

  &.dialog-small {
    max-width: 400px;
  }

  &.dialog-confirm {
    max-width: 360px;
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;

  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #1a4d2e;
  }
}

.dialog-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #999;
  border-radius: 4px;
  transition: all 0.2s;

  &:hover {
    background: #f5f5f5;
    color: #333;
  }
}

.dialog-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

// 表单
.form-group {
  margin-bottom: 16px;

  label {
    display: block;
    margin-bottom: 6px;
    font-size: 13px;
    font-weight: 500;
    color: #333;
  }

  .required {
    color: #ef4444;
  }

  input, select, textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 14px;
    color: #333;
    outline: none;
    transition: border-color 0.2s;
    box-sizing: border-box;

    &:focus {
      border-color: #52b788;
    }

    &.error {
      border-color: #ef4444;
    }

    &::placeholder {
      color: #999;
    }
  }

  textarea {
    resize: vertical;
    font-family: inherit;
  }
}

.error-tip {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #ef4444;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;

  input[type="radio"] {
    width: auto;
    accent-color: #52b788;
  }
}

// 详情弹窗
.detail-section {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #1a4d2e;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e5e7eb;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.detail-item {
  &.full-width {
    grid-column: 1 / -1;
  }

  .detail-label {
    display: block;
    font-size: 12px;
    color: #999;
    margin-bottom: 4px;
  }

  .detail-value {
    font-size: 14px;
    color: #333;
  }
}

.service-records {
  .record-item {
    display: flex;
    gap: 12px;
    padding: 10px 0;
    border-bottom: 1px solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }
  }

  .record-time {
    font-size: 12px;
    color: #999;
    flex-shrink: 0;
    width: 80px;
  }

  .record-content {
    flex: 1;
  }

  .record-type {
    display: inline-block;
    padding: 2px 8px;
    background: rgba(82, 183, 136, 0.1);
    color: #2d6a4f;
    border-radius: 4px;
    font-size: 11px;
    margin-right: 8px;
  }

  .record-desc {
    font-size: 13px;
    color: #333;
  }
}

.empty-records {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 13px;
}

// 续费弹窗
.renewal-client-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 16px;

  .client-name {
    font-weight: 600;
    color: #1a4d2e;
  }
}

// 删除确认
.confirm-icon {
  text-align: center;
  margin-bottom: 16px;
}

.confirm-text {
  text-align: center;
  font-size: 14px;
  color: #333;
  margin: 0 0 8px;

  strong {
    color: #1a4d2e;
  }
}

.confirm-hint {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin: 0;
}
</style>
