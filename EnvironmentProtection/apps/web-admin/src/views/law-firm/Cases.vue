<template>
  <div class="cases-container">
    <div class="page-header">
      <h1 class="page-title">典型案例库</h1>
      <el-button type="primary" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        上传案例
      </el-button>
    </div>

    <!-- 查询表单 -->
    <div class="glass-card filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="案例名称/关键词" clearable @keyup.enter="handleFilter" />
        </el-form-item>
        <el-form-item label="案例类型">
          <el-select v-model="filterForm.type" placeholder="选择类型" clearable @change="handleFilter">
            <el-option v-for="(text, key) in typeText" :key="key" :label="text" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="涉及行业">
          <el-select v-model="filterForm.industry" placeholder="选择行业" clearable @change="handleFilter">
            <el-option v-for="ind in industryList" :key="ind" :label="ind" :value="ind" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable @change="handleFilter">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="glass-card">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="title" label="案例标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <span class="tag" :class="'tag-' + row.type">{{ typeText[row.type] }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="涉及行业" width="120" />
        <el-table-column prop="lawyer" label="主办律师" width="100" />
        <el-table-column prop="lawFirm" label="所属律所" width="140" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishDate" label="发布日期" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <!-- 上传/编辑案例弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" class="glass-dialog" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="案例标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入案例标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="案例类型" prop="type">
              <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
                <el-option v-for="(text, key) in typeText" :key="key" :label="text" :value="key" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="涉及行业" prop="industry">
              <el-select v-model="form.industry" placeholder="选择行业" style="width: 100%">
                <el-option v-for="ind in industryList" :key="ind" :label="ind" :value="ind" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主办律师" prop="lawyer">
              <el-input v-model="form.lawyer" placeholder="请输入主办律师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属律所" prop="lawFirm">
              <el-input v-model="form.lawFirm" placeholder="请输入律所名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="案例概述" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="4" placeholder="请输入案例概述（200字以内）" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="案件详情" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入详细的案件详情" />
        </el-form-item>
        <el-form-item label="典型意义" prop="significance">
          <el-input v-model="form.significance" type="textarea" :rows="3" placeholder="请输入案例的典型意义和法律价值" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看案例详情弹窗 -->
    <el-dialog v-model="viewDialogVisible" title="案例详情" width="700px" class="glass-dialog">
      <div class="case-detail" v-if="currentRow">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentRow.title }}</h3>
          <div class="detail-meta">
            <span class="tag" :class="'tag-' + currentRow.type">{{ typeText[currentRow.type] }}</span>
            <el-tag :type="getStatusType(currentRow.status)" size="small">{{ getStatusText(currentRow.status) }}</el-tag>
          </div>
        </div>
        <el-descriptions :column="2" border class="detail-descriptions">
          <el-descriptions-item label="涉及行业">{{ currentRow.industry }}</el-descriptions-item>
          <el-descriptions-item label="主办律师">{{ currentRow.lawyer }}</el-descriptions-item>
          <el-descriptions-item label="所属律所">{{ currentRow.lawFirm }}</el-descriptions-item>
          <el-descriptions-item label="发布日期">{{ currentRow.publishDate }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-section">
          <h4>案例概述</h4>
          <p>{{ currentRow.summary || '暂无概述' }}</p>
        </div>
        <div class="detail-section">
          <h4>案件详情</h4>
          <p class="content-text">{{ currentRow.content || '暂无详情' }}</p>
        </div>
        <div class="detail-section" v-if="currentRow.significance">
          <h4>典型意义</h4>
          <p>{{ currentRow.significance }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleCite(currentRow)">引用案例</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCaseList, createCase, updateCase, deleteCase } from '@/api/case.js'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('上传案例')
const currentRow = ref(null)
const isEdit = ref(false)
const formRef = ref(null)

const filterForm = reactive({
  keyword: '',
  type: '',
  industry: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: '',
  title: '',
  type: '',
  industry: '',
  lawyer: '',
  lawFirm: '',
  summary: '',
  content: '',
  significance: ''
})

const formRules = {
  title: [{ required: true, message: '请输入案例标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择案例类型', trigger: 'change' }],
  industry: [{ required: true, message: '请选择涉及行业', trigger: 'change' }],
  lawyer: [{ required: true, message: '请输入主办律师', trigger: 'blur' }],
  lawFirm: [{ required: true, message: '请输入所属律所', trigger: 'blur' }]
}

const typeMap = { penalty: 'danger', civil: 'warning', criminal: 'info', public: 'success' }
const typeText = { penalty: '行政处罚', civil: '民事纠纷', criminal: '刑事案件', public: '公益诉讼' }
const industryList = ref(['塑料制造', '农业', '餐饮商超', '快递物流', '化工', '纺织', '电子制造', '其他'])

onMounted(() => {
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...filterForm
    }
    Object.keys(params).forEach(key => {
      if (params[key] === '') delete params[key]
    })
    const res = await getCaseList(params)
    tableData.value = res.data || []
    pagination.total = res.total || tableData.value.length
  } catch (error) {
    console.error('获取案例列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  pagination.page = 1
  fetchData()
}

function handleAdd() {
  dialogTitle.value = '上传案例'
  isEdit.value = false
  Object.keys(form).forEach(key => {
    form[key] = ''
  })
  form.type = ''
  form.industry = ''
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑案例'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleView(row) {
  currentRow.value = row
  viewDialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateCase(form.id, form)
      ElMessage.success('案例更新成功')
    } else {
      await createCase(form)
      ElMessage.success('案例上传成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除案例「${row.title}」吗？此操作不可恢复。`, '提示', { type: 'warning' })
    await deleteCase(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

function handleCite(row) {
  ElMessage.info(`案例「${row.title}」已加入引用区`)
}

function getStatusType(status) {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = { pending: '待审核', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}
</script>

<style lang="scss" scoped>
.cases-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
    color: var(--text-primary);
  }
}

.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

  &.filter-card {
    :deep(.el-form) {
      margin-bottom: 0;
    }
  }
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;

  &.tag-penalty {
    background: rgba(239, 68, 68, 0.12);
    color: #dc2626;
  }
  &.tag-civil {
    background: rgba(245, 158, 11, 0.12);
    color: #d97706;
  }
  &.tag-criminal {
    background: rgba(107, 114, 128, 0.12);
    color: #6b7280;
  }
  &.tag-public {
    background: rgba(82, 183, 136, 0.12);
    color: #2d6a4f;
  }
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

:deep(.glass-dialog) {
  .el-dialog__header {
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    padding-bottom: 12px;
  }
  .el-dialog__title {
    font-weight: 600;
    color: #1a4d2e;
  }
}

.case-detail {
  .detail-header {
    margin-bottom: 20px;
  }
  .detail-title {
    font-size: 18px;
    font-weight: 600;
    color: #1a4d2e;
    margin: 0 0 12px;
  }
  .detail-meta {
    display: flex;
    gap: 8px;
    align-items: center;
  }
  .detail-descriptions {
    margin-bottom: 20px;
  }
  .detail-section {
    margin-bottom: 16px;
    h4 {
      font-size: 14px;
      font-weight: 600;
      color: #1a4d2e;
      margin: 0 0 8px;
    }
    p {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      margin: 0;
    }
    .content-text {
      white-space: pre-wrap;
    }
  }
}
</style>
