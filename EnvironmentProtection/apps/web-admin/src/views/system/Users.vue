<template>
  <div class="users-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">用户管理</h2>
        <p class="page-sub">管理平台所有用户账号与权限</p>
      </div>
      <el-button type="primary" class="add-btn" @click="handleCreate">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新增用户
      </el-button>
    </div>

    <!-- 玻璃卡片 -->
    <div class="glass-card">
      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="filter-group">
          <div class="search-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/>
              <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索用户名 / 昵称"
              clearable
              @clear="handleQuery"
              @keyup.enter="handleQuery"
            />
          </div>

          <el-select v-model="queryParams.role" placeholder="选择角色" clearable style="width: 150px">
            <el-option label="超级管理员" value="super_admin" />
            <el-option label="政府监管员" value="government" />
            <el-option label="企业管理员" value="enterprise" />
            <el-option label="律所管理员" value="law_firm" />
            <el-option label="普通用户" value="user" />
          </el-select>

          <el-select v-model="queryParams.status" placeholder="账号状态" clearable style="width: 120px">
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </div>

        <div class="action-group">
          <el-button @click="handleReset">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="1 4 1 10 7 10"/>
              <path d="M3.51 15a9 9 0 1 0 .49-3.49"/>
            </svg>
            重置
          </el-button>
          <el-button type="primary" @click="handleQuery">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/>
              <line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            查询
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="users"
        class="glass-table"
        :header-cell-style="{ background: 'rgba(82,183,136,0.06)', color: '#1a4d2e', fontWeight: '600', borderBottom: '1px solid rgba(82,183,136,0.1)' }"
        :row-style="{ borderBottom: '1px solid rgba(82,183,136,0.06)' }"
      >
        <el-table-column prop="username" label="用户名" min-width="130">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" class="user-av">
                {{ (row.nickname || row.username || 'U').slice(0, 1) }}
              </el-avatar>
              <div class="user-info">
                <span class="username">{{ row.username }}</span>
                <span class="nickname">{{ row.nickname || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="角色" width="130">
          <template #default="{ row }">
            <el-tag class="role-tag" :style="{ '--tag-color': getRoleColorFn(row.role) }">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path :d="getRoleIconFn(row.role)"/>
              </svg>
              {{ getRoleTextFn(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="organizationName" label="所属组织" min-width="160" />

        <el-table-column prop="phone" label="手机号" width="130" />

        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <div class="status-wrap">
              <span class="status-dot" :class="row.status === 'active' ? 'active' : 'disabled'"></span>
              <span>{{ row.status === 'active' ? '启用' : '禁用' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="170" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button type="primary" link @click="handleEdit(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
                编辑
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"/>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
                删除
              </el-button>
              <el-button type="warning" link @click="handleResetPwd(row)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                重置密码
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      class="glass-dialog"
      :close-on-click-modal="false"
      @close="handleClose"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top" :disabled="submitting">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入显示昵称">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择用户角色" style="width: 100%">
            <el-option label="超级管理员" value="super_admin" />
            <el-option label="政府监管员" value="government" />
            <el-option label="企业管理员" value="enterprise" />
            <el-option label="律所管理员" value="law_firm" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
                <line x1="12" y1="18" x2="12.01" y2="18"/>
              </svg>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="账号状态">
          <el-radio-group v-model="form.status">
            <el-radio value="active">启用</el-radio>
            <el-radio value="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false" :disabled="submitting">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '确认' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mockUsers, getRoleText, getRoleColor, getRoleIcon, filterUsers } from '@/utils/mockUser.js'

// ============ 1. 状态定义 ============
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref()

// 是否为编辑模式
const isEdit = computed(() => !!form.value.id)

// ============ 2. 查询参数 ============
const queryParams = reactive({
  keyword: '',
  role: '',
  status: '',
  page: 1,
  pageSize: 10
})

// ============ 3. 表单数据 ============
const form = reactive({
  id: '',
  username: '',
  nickname: '',
  role: '',
  phone: '',
  status: 'active'
})

// ============ 4. 表单验证规则 ============
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名3-20字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '仅支持字母、数字、下划线', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 30, message: '昵称2-30字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// ============ 5. 角色映射（使用虚拟数据） ============
const roleMap = {
  super_admin: { text: '超级管理员', color: '#e74c3c', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' },
  government: { text: '政府监管', color: '#3498db', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
  enterprise: { text: '企业管理员', color: '#27ae60', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  law_firm: { text: '律所管理员', color: '#9b59b6', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  user: { text: '普通用户', color: '#95a5a6', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }
}

const getRoleTextFn = (role) => getRoleText(role) || roleMap[role]?.text || role
const getRoleColorFn = (role) => getRoleColor(role) || roleMap[role]?.color || '#9ca3af'
const getRoleIconFn = (role) => getRoleIcon(role) || roleMap[role]?.icon || ''

// ============ 6. 数据定义 ============
const users = ref([])
const total = ref(0)

// ============ 7. 使用虚拟数据获取用户列表 ============
function fetchUsersFromMock() {
  const result = filterUsers({
    keyword: queryParams.keyword,
    role: queryParams.role,
    status: queryParams.status,
    page: queryParams.page,
    pageSize: queryParams.pageSize
  })
  users.value = result.data
  total.value = result.total
}

// ============ 8. 生命周期：数据获取 ============
async function fetchUsers() {
  loading.value = true
  try {
    // 模拟 API 调用延迟
    await new Promise(r => setTimeout(r, 400))
    fetchUsersFromMock()
  } finally {
    loading.value = false
  }
}

// ============ 9. 生命周期：弹窗状态监听 ============
watch(dialogVisible, (val) => {
  if (val) {
    // 打开弹窗时清理验证状态
    nextTick(() => formRef.value?.clearValidate())
  }
})

// ============ 10. 查询操作 ============
const handleQuery = () => {
  queryParams.page = 1
  fetchUsers()
}

const handleReset = () => {
  Object.assign(queryParams, { keyword: '', role: '', status: '', page: 1 })
  fetchUsers()
}

const handleSizeChange = () => {
  queryParams.page = 1
  fetchUsers()
}

const handleCurrentChange = () => {
  fetchUsers()
}

// ============ 11. 生命周期：表单操作 ============
const handleCreate = () => {
  dialogTitle.value = '新增用户'
  // 重置表单
  Object.assign(form, { id: '', username: '', nickname: '', role: '', phone: '', status: 'active' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 弹窗关闭时重置表单
const handleClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: '', username: '', nickname: '', role: '', phone: '', status: 'active' })
}

// ============ 12. 数据操作 ============
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {}
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置「${row.username}」的密码吗？`, '提示', { type: 'warning' })
    ElMessage.success(`密码已重置为：123456`)
  } catch {}
}

// ============ 13. 生命周期：表单提交 ============
const handleSubmit = async () => {
  try {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return
    
    submitting.value = true
    
    // 模拟 API 调用延迟
    await new Promise(r => setTimeout(r, 800))
    
    ElMessage.success(form.id ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchUsers()
  } catch (err) {
    if (err !== false) {
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}

// ============ 14. 初始化 ============
onMounted(fetchUsers)
</script>

<style lang="scss" scoped>
.users-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.header-left {
  .page-title {
    margin: 0 0 4px;
    font-size: 24px;
    font-weight: 700;
    color: #1a4d2e;
  }
  .page-sub {
    margin: 0;
    font-size: 13px;
    color: #9ca3af;
  }
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  svg {
    width: 15px;
    height: 15px;
  }
}

// 玻璃卡片
.glass-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 24px rgba(82, 183, 136, 0.06);
  padding: 24px;
}

// 工具栏
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-wrap {
  position: relative;
  display: flex;
  align-items: center;

  svg {
    position: absolute;
    left: 12px;
    width: 15px;
    height: 15px;
    color: #9ca3af;
    z-index: 1;
  }

  :deep(.el-input__wrapper) {
    padding-left: 36px !important;
    background: rgba(255, 255, 255, 0.8) !important;
    border: 1px solid rgba(82, 183, 136, 0.2) !important;
    box-shadow: none !important;
  }
}

.action-group {
  display: flex;
  gap: 8px;
}

// 表格
.glass-table {
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-table__row) {
    background: transparent !important;
    &:hover > td {
      background: rgba(82, 183, 136, 0.04) !important;
    }
  }

  :deep(.el-table__cell) {
    padding: 14px 12px;
    font-size: 13px;
  }
}

// 用户单元格
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-av {
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
  color: #fff !important;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;

  .username {
    font-weight: 600;
    color: #1a4d2e;
    font-size: 13px;
  }

  .nickname {
    font-size: 12px;
    color: #9ca3af;
  }
}

// 角色标签
.role-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(82, 183, 136, 0.08) !important;
  border: 1px solid rgba(82, 183, 136, 0.15) !important;
  color: var(--tag-color, #52b788) !important;

  svg {
    width: 13px;
    height: 13px;
  }
}

// 状态
.status-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;

  &.active {
    background: #52b788;
    box-shadow: 0 0 6px rgba(82, 183, 136, 0.5);
  }

  &.disabled {
    background: #d1d5db;
  }
}

// 操作按钮
.action-btns {
  display: flex;
  gap: 4px;
  align-items: center;

  .el-button {
    padding: 4px 8px !important;
    font-size: 12px !important;
    display: inline-flex;
    align-items: center;
    gap: 4px;

    svg {
      width: 13px;
      height: 13px;
    }
  }
}

// 分页
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(82, 183, 136, 0.08);

  :deep(.el-pagination) {
    .el-pager li {
      border-radius: 8px;
      &.is-active {
        background: #52b788 !important;
        color: #fff !important;
      }
    }
  }
}

// Dialog
:deep(.glass-dialog) {
  .el-dialog {
    border-radius: 20px !important;
    background: rgba(255, 255, 255, 0.95) !important;
    backdrop-filter: blur(20px) !important;
    border: 1px solid rgba(82, 183, 136, 0.2) !important;
  }

  .el-dialog__header {
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    padding: 20px 24px !important;
  }

  .el-dialog__title {
    font-weight: 700;
    color: #1a4d2e;
    font-size: 16px;
  }

  .el-dialog__body {
    padding: 24px !important;
  }

  .el-dialog__footer {
    border-top: 1px solid rgba(82, 183, 136, 0.1);
    padding: 16px 24px !important;
  }
}
</style>
