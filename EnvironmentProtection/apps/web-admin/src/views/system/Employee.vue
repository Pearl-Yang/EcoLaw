<template>
  <div class="employee-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">职员管理</h2>
        <p class="page-sub">政府、企业、平台可按组织维度管理下属职员，支持条件查询与台账导出</p>
      </div>
      <div class="header-actions">
        <el-button @click="exportSelected" :disabled="!multipleSelection.length">
          导出所选
        </el-button>
        <el-button type="success" @click="exportFiltered" :disabled="!fullEmployeeList.length">
          导出当前筛选
        </el-button>
        <el-button type="primary" @click="handleImport">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17 8 12 3 7 8"/>
            <line x1="12" y1="3" x2="12" y2="15"/>
          </svg>
          批量导入
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          新增职员
        </el-button>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="glass-card filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="组织">
          <el-cascader
            v-model="filterForm.organizationId"
            :options="orgTreeData"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"
            placeholder="选择组织"
            clearable
            @change="handleFilter"
          />
        </el-form-item>
        <el-form-item label="主体类型">
          <el-select v-model="filterForm.organizationType" placeholder="政府/企业/平台" clearable style="width: 140px" @change="handleFilter">
            <el-option label="政府" value="government" />
            <el-option label="企业" value="enterprise" />
            <el-option label="平台" value="platform" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="filterForm.keyword"
            placeholder="成员姓名、工号、手机号"
            clearable
            style="width: 220px"
            @keyup.enter="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="filterForm.position" placeholder="选择职位" clearable @change="handleFilter">
            <el-option v-for="pos in positionList" :key="pos" :label="pos" :value="pos" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable @change="handleFilter">
            <el-option label="在职" value="active" />
            <el-option label="离职" value="leave" />
            <el-option label="调离" value="transfer" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 职员列表 -->
    <div class="glass-card">
      <el-table
        :data="tableData"
        stripe
        v-loading="loading"
        row-key="id"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="48" reserve-selection />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="name" label="成员姓名" min-width="120">
          <template #default="{ row }">
            <div class="name-cell">
              <el-avatar :size="32" :style="{ background: getAvatarBg(row.name) }">
                {{ row.name?.charAt(0) }}
              </el-avatar>
              <div class="name-info">
                <span class="name">{{ row.name }}</span>
                <span class="gender">{{ row.gender === 'male' ? '男' : '女' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="年龄" width="72">
          <template #default="{ row }">
            <span>{{ formatAge(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="organizationName" label="所属组织" min-width="150" />
        <el-table-column label="主体类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ orgTypeLabel(row.organizationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="positionLevel" label="职级" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : row.status === 'leave' ? 'danger' : 'warning'" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleTransfer(row)">调岗</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 职员表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" class="glass-dialog" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职级">
              <el-input v-model="form.positionLevel" placeholder="请输入职级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称">
              <el-input v-model="form.title" placeholder="请输入职称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker v-model="form.hireDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上级职员">
              <el-select v-model="form.parentEmployeeId" placeholder="选择上级" clearable filterable style="width: 100%">
                <el-option v-for="emp in employeeOptions" :key="emp.id" :label="`${emp.name} - ${emp.position}`" :value="emp.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="在职" value="active" />
                <el-option label="离职" value="leave" />
                <el-option label="调离" value="transfer" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 职员详情弹窗 -->
    <el-dialog v-model="viewDialogVisible" title="职员详情" width="600px" class="glass-dialog">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="工号">{{ currentRow.employeeNo }}</el-descriptions-item>
        <el-descriptions-item label="成员姓名">{{ currentRow.name }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ formatAge(currentRow) }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentRow.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="主体类型">{{ orgTypeLabel(currentRow.organizationType) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentRow.phone }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ currentRow.position }}</el-descriptions-item>
        <el-descriptions-item label="职级">{{ currentRow.positionLevel }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentRow.title }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentRow.email }}</el-descriptions-item>
        <el-descriptions-item label="所属组织">{{ currentRow.organizationName }}</el-descriptions-item>
        <el-descriptions-item label="上级">{{ currentRow.parentEmployeeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ currentRow.hireDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow.status === 'active' ? 'success' : currentRow.status === 'leave' ? 'danger' : 'warning'" size="small">
            {{ getStatusText(currentRow.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 调岗弹窗 -->
    <el-dialog v-model="transferDialogVisible" title="职员调岗" width="500px" class="glass-dialog">
      <el-form :model="transferForm" label-width="80px">
        <el-form-item label="目标组织">
          <el-cascader
            v-model="transferForm.targetOrganizationId"
            :options="orgTreeData"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"
            placeholder="选择目标组织"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="新职位">
          <el-input v-model="transferForm.newPosition" placeholder="请输入新职位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTransferSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import * as XLSX from 'xlsx'
import { getEmployeeList, createEmployee, updateEmployee, deleteEmployee, transferEmployee, getOrganizationEmployees } from '@/api/employee.js'
import { getOrganizationTree } from '@/api/organization.js'

const loading = ref(false)
const tableData = ref([])
const fullEmployeeList = ref([])
const multipleSelection = ref([])
const orgTreeData = ref([])
const employeeOptions = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const transferDialogVisible = ref(false)
const dialogTitle = ref('新增职员')
const currentRow = ref(null)
const isEdit = ref(false)

const filterForm = reactive({
  organizationId: '',
  organizationType: '',
  keyword: '',
  position: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

const form = reactive({
  id: '',
  name: '',
  employeeNo: '',
  gender: 'male',
  phone: '',
  position: '',
  positionLevel: '',
  title: '',
  email: '',
  idCard: '',
  hireDate: '',
  parentEmployeeId: '',
  status: 'active',
  organizationId: ''
})

const transferForm = reactive({
  targetOrganizationId: '',
  newPosition: ''
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

const positionList = ref(['处长', '副处长', '科长', '副科长', '主任', '副主任', '经理', '副经理', '主管', '专员', '员工'])

onMounted(() => {
  fetchOrgTree()
  fetchData()
})

async function fetchOrgTree() {
  try {
    const res = await getOrganizationTree()
    orgTreeData.value = res.data || []
  } catch (error) {
    console.error('获取组织树失败:', error)
  }
}

function normalizeEmployeeList(res) {
  if (Array.isArray(res)) return res
  if (res && Array.isArray(res.data)) return res.data
  if (res && Array.isArray(res.list)) return res.list
  if (res && Array.isArray(res.records)) return res.records
  return []
}

function applyPageSlice() {
  const start = (pagination.page - 1) * pagination.pageSize
  tableData.value = fullEmployeeList.value.slice(start, start + pagination.pageSize)
  pagination.total = fullEmployeeList.value.length
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      keyword: filterForm.keyword || undefined,
      position: filterForm.position || undefined,
      status: filterForm.status || undefined,
      organizationType: filterForm.organizationType || undefined
    }
    if (filterForm.organizationId && filterForm.organizationId.length > 0) {
      params.organizationId = filterForm.organizationId[filterForm.organizationId.length - 1]
    }
    const res = await getEmployeeList(params)
    fullEmployeeList.value = normalizeEmployeeList(res)
    applyPageSlice()
  } catch (error) {
    console.error('获取职员列表失败:', error)
  } finally {
    loading.value = false
  }
}

function onSelectionChange(rows) {
  multipleSelection.value = rows
}

function handlePageChange() {
  applyPageSlice()
}

function handleSizeChange() {
  pagination.page = 1
  applyPageSlice()
}

function handleFilter() {
  pagination.page = 1
  fetchData()
}

function handleAdd() {
  dialogTitle.value = '新增职员'
  isEdit.value = false
  Object.keys(form).forEach(key => {
    if (key !== 'gender' && key !== 'status') {
      form[key] = ''
    }
  })
  form.gender = 'male'
  form.status = 'active'
  dialogVisible.value = true
  loadEmployeeOptions()
}

function handleEdit(row) {
  dialogTitle.value = '编辑职员'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
  loadEmployeeOptions()
}

function handleView(row) {
  currentRow.value = row
  viewDialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      await updateEmployee(form.id, form)
      ElMessage.success('职员更新成功')
    } else {
      await createEmployee(form)
      ElMessage.success('职员创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除职员「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteEmployee(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

function handleTransfer(row) {
  currentRow.value = row
  transferForm.targetOrganizationId = ''
  transferForm.newPosition = row.position
  transferDialogVisible.value = true
}

async function handleTransferSubmit() {
  try {
    const targetOrgId = Array.isArray(transferForm.targetOrganizationId) 
      ? transferForm.targetOrganizationId[transferForm.targetOrganizationId.length - 1] 
      : transferForm.targetOrganizationId
    await transferEmployee(currentRow.value.id, targetOrgId, transferForm.newPosition)
    ElMessage.success('调岗成功')
    transferDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.message || '调岗失败')
  }
}

async function handleImport() {
  ElMessage.info('批量导入功能开发中')
}

async function loadEmployeeOptions() {
  try {
    const res = await getOrganizationEmployees(form.organizationId || '', {})
    employeeOptions.value = res.data || []
  } catch (error) {
    console.error('获取职员选项失败:', error)
  }
}

function getStatusText(status) {
  const map = { active: '在职', leave: '离职', transfer: '调离' }
  return map[status] || status
}

function getAvatarBg(name) {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  const index = (name?.charCodeAt(0) || 0) % colors.length
  return colors[index]
}

/** 从身份证号推算年龄（18 位或 15 位） */
function ageFromIdCard(idCard) {
  if (!idCard || typeof idCard !== 'string') return null
  const s = idCard.trim()
  let y
  let m
  let d
  if (s.length === 18 && /^\d{17}[\dXx]$/.test(s)) {
    y = Number(s.slice(6, 10))
    m = Number(s.slice(10, 12))
    d = Number(s.slice(12, 14))
  } else if (s.length === 15 && /^\d{15}$/.test(s)) {
    y = Number('19' + s.slice(6, 8))
    m = Number(s.slice(8, 10))
    d = Number(s.slice(10, 12))
  } else {
    return null
  }
  const birth = dayjs(`${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`)
  if (!birth.isValid()) return null
  let age = dayjs().diff(birth, 'year')
  if (dayjs().isBefore(birth.add(age, 'year'))) age -= 1
  return age
}

function formatAge(row) {
  const n = ageFromIdCard(row?.idCard)
  return n == null ? '—' : String(n)
}

function orgTypeLabel(t) {
  const map = {
    government: '政府',
    enterprise: '企业',
    platform: '平台',
    education: '教育',
    law_firm: '律所'
  }
  return map[t] || t || '—'
}

function maskIdCard(idCard) {
  if (!idCard || typeof idCard !== 'string') return ''
  const s = idCard.trim()
  if (s.length === 18) return `${s.slice(0, 6)}********${s.slice(14)}`
  if (s.length === 15) return `${s.slice(0, 6)}******${s.slice(12)}`
  return s
}

function rowsToExportSheet(rows) {
  return rows.map((row) => {
    const ageStr = formatAge(row)
    return {
    成员姓名: row.name || '',
    性别: row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '',
    年龄: ageStr === '—' ? '' : ageStr,
    工号: row.employeeNo || '',
    所属组织: row.organizationName || '',
    主体类型: orgTypeLabel(row.organizationType),
    职位: row.position || '',
    职级: row.positionLevel || '',
    职称: row.title || '',
    手机号: row.phone || '',
    邮箱: row.email || '',
    入职日期: row.hireDate || '',
    状态: getStatusText(row.status),
    证件号脱敏: maskIdCard(row.idCard)
    }
  })
}

function downloadXlsx(rows, filePrefix) {
  if (!rows.length) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  const sheet = XLSX.utils.json_to_sheet(rowsToExportSheet(rows))
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, sheet, '职员台账')
  XLSX.writeFile(wb, `${filePrefix}_${dayjs().format('YYYY-MM-DD_HHmm')}.xlsx`)
}

function exportFiltered() {
  downloadXlsx(fullEmployeeList.value, '职员管理台账')
}

function exportSelected() {
  if (!multipleSelection.value.length) {
    ElMessage.warning('请先勾选要导出的职员')
    return
  }
  downloadXlsx(multipleSelection.value, '职员管理台账_所选')
}
</script>

<style scoped>
.employee-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left .page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: var(--text-primary);
}

.header-left .page-sub {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 4px 0 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.filter-card :deep(.el-form) {
  margin-bottom: 0;
}

.name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name-info {
  display: flex;
  flex-direction: column;
}

.name-info .name {
  font-weight: 500;
}

.name-info .gender {
  font-size: 12px;
  color: var(--text-secondary);
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-button svg) {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}
</style>
