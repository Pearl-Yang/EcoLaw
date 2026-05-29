<template>
  <div class="org-container">
    <!-- 左侧导航 -->
    <aside class="org-sidebar">
      <div class="sidebar-header">
        <h3>组织架构</h3>
        <el-button type="primary" circle size="small" @click="handleAdd">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </el-button>
      </div>
      <div class="sidebar-search">
        <el-input v-model="searchKey" placeholder="搜索组织名称..." prefix-icon="Search" clearable />
      </div>
      <div class="org-tree-wrapper">
        <el-scrollbar>
          <el-tree
            ref="treeRef"
            :data="filteredTreeData"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <div class="tree-node-content">
                <div class="node-level-badge" :class="`level-${data.level}`">{{ getLevelName(data.level) }}</div>
                <span class="node-name">{{ data.name }}</span>
              </div>
            </template>
          </el-tree>
        </el-scrollbar>
      </div>
    </aside>

    <!-- 右侧内容 -->
    <main class="org-main">
      <!-- 顶部统计 -->
      <div class="stats-bar">
        <div class="stat-item">
          <div class="stat-icon prov"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg></div>
          <div class="stat-info">
            <span class="stat-num">{{ stats.province }}</span>
            <span class="stat-label">省级</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon city"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4"/></svg></div>
          <div class="stat-info">
            <span class="stat-num">{{ stats.city }}</span>
            <span class="stat-label">市级</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon county"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg></div>
          <div class="stat-info">
            <span class="stat-num">{{ stats.county }}</span>
            <span class="stat-label">县级</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon community"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg></div>
          <div class="stat-info">
            <span class="stat-num">{{ stats.community }}</span>
            <span class="stat-label">社区/村</span>
          </div>
        </div>
      </div>

      <!-- 选中组织详情 -->
      <div v-if="currentOrg" class="org-detail-card">
        <div class="detail-header">
          <div class="detail-title">
            <div class="org-avatar" :class="`level-${currentOrg.level}`">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path :d="getLevelIcon(currentOrg.level)"/>
              </svg>
            </div>
            <div class="org-title-info">
              <h2>{{ currentOrg.name }}</h2>
              <div class="org-badges">
                <span class="badge level-badge" :class="`level-${currentOrg.level}`">{{ getLevelName(currentOrg.level) }}</span>
                <span class="badge code-badge">{{ currentOrg.code }}</span>
              </div>
            </div>
          </div>
          <div class="detail-actions">
            <el-button type="primary" plain @click="handleAddChild(currentOrg)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              添加下级
            </el-button>
            <el-button type="primary" @click="handleEdit(currentOrg)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              编辑
            </el-button>
            <el-button type="danger" plain @click="handleDelete(currentOrg)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              删除
            </el-button>
          </div>
        </div>
        <div class="detail-body">
          <div class="detail-row">
            <span class="detail-label">组织编码</span>
            <span class="detail-value code">{{ currentOrg.code }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">组织层级</span>
            <span class="detail-value">{{ getLevelName(currentOrg.level) }}</span>
          </div>
          <div class="detail-row" v-if="currentOrg.parentName">
            <span class="detail-label">上级组织</span>
            <span class="detail-value">{{ currentOrg.parentName }}</span>
          </div>
          <div class="detail-row" v-if="currentOrg.description">
            <span class="detail-label">组织描述</span>
            <span class="detail-value">{{ currentOrg.description }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">下级组织</span>
            <span class="detail-value">{{ getChildCount(currentOrg) }} 个</span>
          </div>
        </div>

        <!-- 普法教育模块 - 社区层级专属 -->
        <div v-if="currentOrg.level >= 5" class="edu-module">
          <div class="edu-header">
            <div class="edu-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
              <span>普法教育管理</span>
            </div>
            <el-button type="primary" size="small" @click="showEduDialog = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              添加居民
            </el-button>
          </div>
          
          <!-- 普法教育统计 -->
          <div class="edu-stats">
            <div class="edu-stat-item">
              <span class="edu-stat-num">{{ eduStats.residents }}</span>
              <span class="edu-stat-label">登记居民</span>
            </div>
            <div class="edu-stat-item">
              <span class="edu-stat-num">{{ eduStats.eduEvents }}</span>
              <span class="edu-stat-label">普法活动</span>
            </div>
            <div class="edu-stat-item">
              <span class="edu-stat-num">{{ eduStats.participated }}</span>
              <span class="edu-stat-label">参与人次</span>
            </div>
            <div class="edu-stat-item">
              <span class="edu-stat-num">{{ eduStats.coverage }}%</span>
              <span class="edu-stat-label">覆盖比率</span>
            </div>
          </div>

          <!-- 居民列表 -->
          <div class="residents-section">
            <div class="residents-toolbar">
              <el-input v-model="residentSearch" placeholder="搜索居民姓名..." size="small" style="width: 200px">
                <template #prefix><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg></template>
              </el-input>
              <el-select v-model="residentFilter" placeholder="筛选" size="small" style="width: 120px">
                <el-option label="全部" :value="0" />
                <el-option label="已参与" :value="1" />
                <el-option label="未参与" :value="2" />
              </el-select>
            </div>
            <el-scrollbar max-height="240px">
              <div class="residents-list">
                <div v-for="resident in filteredResidents" :key="resident.id" class="resident-card">
                  <div class="resident-avatar">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  </div>
                  <div class="resident-info">
                    <span class="resident-name">{{ resident.name }}</span>
                    <span class="resident-meta">{{ resident.gender === 'male' ? '男' : '女' }} · {{ resident.age }}岁 · {{ resident.address }}</span>
                  </div>
                  <div class="resident-status">
                    <span class="status-tag" :class="resident.participated ? 'participated' : 'not-participated'">
                      {{ resident.participated ? '已参与' : '未参与' }}
                    </span>
                  </div>
                  <div class="resident-actions">
                    <el-button type="primary" link size="small" @click="editResident(resident)">编辑</el-button>
                    <el-button type="danger" link size="small" @click="deleteResident(resident)">删除</el-button>
                  </div>
                </div>
                <el-empty v-if="filteredResidents.length === 0" description="暂无数据" :image-size="60" />
              </div>
            </el-scrollbar>
          </div>

          <!-- 普法活动记录 -->
          <div class="edu-events-section">
            <div class="edu-events-header">
              <span class="section-title">普法活动记录</span>
              <el-button type="primary" plain size="small" @click="showEventDialog = true">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                记录活动
              </el-button>
            </div>
            <div class="edu-events-list">
              <div v-for="event in eduEvents" :key="event.id" class="edu-event-item">
                <div class="event-date">{{ event.date }}</div>
                <div class="event-content">
                  <span class="event-title">{{ event.title }}</span>
                  <span class="event-meta">{{ event.participants }}人参与</span>
                </div>
              </div>
              <el-empty v-if="eduEvents.length === 0" description="暂无活动记录" :image-size="50" />
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
        </div>
        <p>请从左侧选择一个组织查看详情</p>
      </div>

      <!-- 下级组织列表 -->
      <div v-if="currentOrg?.children?.length" class="child-orgs-section">
        <div class="section-header">
          <h3>下级组织</h3>
          <span class="child-count">{{ currentOrg.children.length }} 个</span>
        </div>
        <div class="child-orgs-grid">
          <div
            v-for="child in currentOrg.children"
            :key="child.id"
            class="child-org-card"
            @click="handleNodeClick(child)"
          >
            <div class="child-org-icon" :class="`level-${child.level}`">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path :d="getLevelIcon(child.level)"/>
              </svg>
            </div>
            <div class="child-org-info">
              <span class="child-org-name">{{ child.name }}</span>
              <span class="child-org-meta">
                <span class="badge small" :class="`level-${child.level}`">{{ getLevelName(child.level) }}</span>
                <span class="child-org-code">{{ child.code }}</span>
              </span>
            </div>
            <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
          </div>
        </div>
      </div>
    </main>

    <!-- 添加/编辑组织弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      class="org-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top">
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入组织编码">
            <template #prefix>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="组织层级" prop="level">
          <el-select v-model="form.level" placeholder="请选择" style="width: 100%">
            <el-option label="省级" :value="1" />
            <el-option label="市级" :value="2" />
            <el-option label="县级" :value="3" />
            <el-option label="乡镇级" :value="4" />
            <el-option label="村级" :value="5" />
            <el-option label="社区" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.parentId" label="上级组织">
          <el-input :model-value="form.parentName" disabled />
        </el-form-item>
        <el-form-item label="组织描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="简要描述该组织的职责范围" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑居民弹窗 -->
    <el-dialog
      v-model="showEduDialog"
      :title="residentForm.id ? '编辑居民' : '添加居民'"
      width="460px"
      class="org-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="residentFormRef" :model="residentForm" :rules="residentRules" label-position="top">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="residentForm.name" placeholder="请输入居民姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="residentForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="residentForm.age" :min="0" :max="150" placeholder="年龄" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="residentForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="居住地址" prop="address">
          <el-input v-model="residentForm.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="是否参与普法活动">
          <el-switch v-model="residentForm.participated" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEduDialog = false">取消</el-button>
        <el-button type="primary" @click="handleResidentSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加普法活动弹窗 -->
    <el-dialog
      v-model="showEventDialog"
      title="记录普法活动"
      width="460px"
      class="org-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="eventFormRef" :model="eventForm" :rules="eventRules" label-position="top">
        <el-form-item label="活动主题" prop="title">
          <el-input v-model="eventForm.title" placeholder="请输入活动主题" />
        </el-form-item>
        <el-form-item label="活动日期" prop="date">
          <el-date-picker v-model="eventForm.date" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="参与人数" prop="participants">
          <el-input-number v-model="eventForm.participants" :min="1" placeholder="参与人数" />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="eventForm.content" type="textarea" :rows="3" placeholder="简要描述活动内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEventDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEventSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const treeRef = ref()
const treeData = ref([])
const searchKey = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增组织')
const formRef = ref()
const currentOrg = ref(null)

// 居民相关
const showEduDialog = ref(false)
const showEventDialog = ref(false)
const residentFormRef = ref()
const eventFormRef = ref()
const residentSearch = ref('')
const residentFilter = ref(0)
const residents = ref([])
const eduEvents = ref([])

const residentForm = reactive({
  id: '', name: '', gender: 'male', age: 30, phone: '', address: '', participated: false
})

const residentRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入居住地址', trigger: 'blur' }]
}

const eventForm = reactive({
  title: '', date: '', participants: 0, content: ''
})

const eventRules = {
  title: [{ required: true, message: '请输入活动主题', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  participants: [{ required: true, message: '请输入参与人数', trigger: 'blur' }]
}

const form = reactive({
  id: '', name: '', code: '', level: 3, parentId: '', parentName: '', description: ''
})

const formRules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入组织编码', trigger: 'blur' }],
  level: [{ required: true, message: '请选择组织层级', trigger: 'change' }]
}

const treeProps = { children: 'children', label: 'name' }

const levelConfig = {
  1: { name: '省级', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' },
  2: { name: '市级', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
  3: { name: '县级', icon: 'M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0zM12 10a3 3 0 1 0 0-6 3 3 0 0 0 0 6z' },
  4: { name: '乡镇级', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  5: { name: '村级', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 3a4 4 0 1 0 0-8 4 4 0 0 0 0 8z' },
  6: { name: '社区', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 7a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75' }
}

const getLevelName = (level) => levelConfig[level]?.name || `第${level}级`
const getLevelIcon = (level) => levelConfig[level]?.icon || levelConfig[3].icon

const filteredTreeData = computed(() => {
  if (!searchKey.value) return treeData.value
  return filterTree(treeData.value, searchKey.value)
})

const filterTree = (data, key) => {
  return data.reduce((acc, node) => {
    const match = node.name.includes(key)
    const children = node.children ? filterTree(node.children, key) : []
    if (match || children.length) {
      acc.push({ ...node, children })
    }
    return acc
  }, [])
}

const stats = computed(() => {
  const count = { province: 0, city: 0, county: 0, community: 0 }
  const traverse = (nodes) => {
    nodes.forEach(node => {
      if (node.level === 1) count.province++
      else if (node.level === 2) count.city++
      else if (node.level === 3) count.county++
      else if (node.level >= 5) count.community++
      if (node.children) traverse(node.children)
    })
  }
  traverse(treeData.value)
  return count
})

const eduStats = computed(() => {
  const participated = residents.value.filter(r => r.participated).length
  const coverage = residents.value.length > 0 ? Math.round((participated / residents.value.length) * 100) : 0
  return {
    residents: residents.value.length,
    eduEvents: eduEvents.value.length,
    participated,
    coverage
  }
})

const filteredResidents = computed(() => {
  let result = residents.value
  if (residentSearch.value) {
    result = result.filter(r => r.name.includes(residentSearch.value))
  }
  if (residentFilter.value === 1) {
    result = result.filter(r => r.participated)
  } else if (residentFilter.value === 2) {
    result = result.filter(r => !r.participated)
  }
  return result
})

const getChildCount = (org) => org.children?.length || 0

function mockTree() {
  return [
    {
      id: '1', name: '省生态环境厅', code: 'PROV-001', level: 1, description: '省级生态环境主管部门，统筹全省环境保护工作',
      children: [
        {
          id: '2', name: '市生态环境局-A', code: 'CITY-001', level: 2, description: '市级生态环境主管部门',
          children: [
            {
              id: '3', name: '县生态环境分局-A', code: 'COUNTY-001', level: 3, description: '县级生态环境主管部门',
              children: [
                { 
                  id: '4', name: '阳光镇', code: 'TOWN-001', level: 4, description: '乡镇级执行部门',
                  children: [
                    { 
                      id: '5', name: '东湖村', code: 'VILL-001', level: 5, description: '村级组织',
                      children: [
                        {
                          id: '15', name: '东湖社区居委会', code: 'COMM-001', level: 6, description: '社区居民委员会',
                          children: []
                        }
                      ]
                    },
                    { 
                      id: '6', name: '西湖村', code: 'VILL-002', level: 5, description: '村级组织',
                      children: [
                        {
                          id: '16', name: '西湖社区居委会', code: 'COMM-002', level: 6, description: '社区居民委员会',
                          children: []
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}

function mockResidents() {
  return [
    { id: 'r1', name: '张伟', gender: 'male', age: 45, phone: '138****1234', address: '东湖社区1号楼', participated: true },
    { id: 'r2', name: '李娜', gender: 'female', age: 38, phone: '139****5678', address: '东湖社区2号楼', participated: true },
    { id: 'r3', name: '王强', gender: 'male', age: 52, phone: '137****9012', address: '东湖社区3单元', participated: false },
    { id: 'r4', name: '刘芳', gender: 'female', age: 28, phone: '136****3456', address: '东湖社区5号楼', participated: true },
    { id: 'r5', name: '陈明', gender: 'male', age: 35, phone: '135****7890', address: '东湖社区7号楼', participated: true }
  ]
}

function mockEduEvents() {
  return [
    { id: 'e1', title: '环境保护法律知识讲座', date: '2024-03-15', participants: 45, content: '讲解环境保护相关法律法规' },
    { id: 'e2', title: '垃圾分类知识宣传', date: '2024-03-08', participants: 32, content: '入户宣传垃圾分类知识' },
    { id: 'e3', title: '环保志愿者培训', date: '2024-02-28', participants: 28, content: '组织志愿者学习环保知识' }
  ]
}

const fetchTree = () => {
  treeData.value = mockTree()
  if (treeData.value.length && !currentOrg.value) {
    currentOrg.value = treeData.value[0]
    treeRef.value?.setCurrentKey(treeData.value[0].id)
  }
}

const loadResidents = () => {
  residents.value = mockResidents()
  eduEvents.value = mockEduEvents()
}

const handleNodeClick = (data) => {
  currentOrg.value = data
  treeRef.value?.setCurrentKey(data.id)
  if (data.level >= 5) {
    loadResidents()
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增组织'
  Object.assign(form, { id: '', name: '', code: '', level: 3, parentId: '', parentName: '', description: '' })
  dialogVisible.value = true
}

const handleAddChild = (data) => {
  dialogTitle.value = '添加下级组织'
  Object.assign(form, { id: '', name: '', code: '', level: Math.min(data.level + 1, 6), parentId: data.id, parentName: data.name, description: '' })
  dialogVisible.value = true
}

const handleEdit = (data) => {
  dialogTitle.value = '编辑组织'
  Object.assign(form, { ...data })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  ElMessage.success(form.id ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  fetchTree()
}

const handleDelete = async (data) => {
  try {
    await ElMessageBox.confirm(`确定要删除「${data.name}」吗？`, '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchTree()
  } catch {}
}

const editResident = (resident) => {
  Object.assign(residentForm, { ...resident })
  showEduDialog.value = true
}

const deleteResident = async (resident) => {
  try {
    await ElMessageBox.confirm(`确定要删除居民「${resident.name}」吗？`, '提示', { type: 'warning' })
    residents.value = residents.value.filter(r => r.id !== resident.id)
    ElMessage.success('删除成功')
  } catch {}
}

const handleResidentSubmit = async () => {
  const valid = await residentFormRef.value?.validate().catch(() => false)
  if (!valid) return
  
  if (residentForm.id) {
    const index = residents.value.findIndex(r => r.id === residentForm.id)
    if (index > -1) residents.value[index] = { ...residentForm }
    ElMessage.success('编辑成功')
  } else {
    residentForm.id = 'r' + Date.now()
    residents.value.push({ ...residentForm })
    ElMessage.success('添加成功')
  }
  showEduDialog.value = false
}

const handleEventSubmit = async () => {
  const valid = await eventFormRef.value?.validate().catch(() => false)
  if (!valid) return
  
  eduEvents.value.unshift({
    id: 'e' + Date.now(),
    ...eventForm
  })
  ElMessage.success('记录成功')
  showEventDialog.value = false
  Object.assign(eventForm, { title: '', date: '', participants: 0, content: '' })
}

fetchTree()
</script>

<style lang="scss" scoped>
.org-container {
  display: flex;
  height: calc(100vh - 140px);
  gap: 24px;
  padding: 0 4px;
}

// 侧边栏
.org-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 16px;
  border-bottom: 1px solid #f0f0f0;
  
  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #1a4d2e;
  }
  
  :deep(.el-button) {
    width: 32px;
    height: 32px;
    padding: 0;
    svg { width: 14px; height: 14px; }
  }
}

.sidebar-search {
  padding: 16px 16px 12px;
  :deep(.el-input__wrapper) {
    border-radius: 10px;
    box-shadow: none;
    border: 1px solid #e8e8e8;
    &:hover, &.is-focus { border-color: #52b788; }
  }
}

.org-tree-wrapper {
  flex: 1;
  overflow: hidden;
  padding: 0 12px 16px;
  
  :deep(.el-tree) {
    background: transparent;
    --el-tree-node-hover-bg-color: rgba(82, 183, 136, 0.06);
  }
  
  :deep(.el-tree-node__content) {
    padding: 6px 8px;
    border-radius: 10px;
    margin-bottom: 2px;
  }
  
  :deep(.el-tree-node.is-current > .el-tree-node__content) {
    background: rgba(82, 183, 136, 0.1) !important;
  }
}

.tree-node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.node-level-badge {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
  flex-shrink: 0;
  
  &.level-1 { background: rgba(82, 183, 136, 0.15); color: #1a4d2e; }
  &.level-2 { background: rgba(45, 106, 79, 0.12); color: #2d6a4f; }
  &.level-3 { background: rgba(64, 145, 108, 0.1); color: #40916c; }
  &.level-4 { background: rgba(149, 213, 178, 0.15); color: #52b788; }
  &.level-5 { background: rgba(149, 213, 178, 0.2); color: #40916c; }
  &.level-6 { background: rgba(82, 183, 136, 0.25); color: #1a4d2e; }
}

.node-name {
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// 主内容区
.org-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
  padding-right: 4px;
}

// 统计栏
.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg { width: 22px; height: 22px; }
  
  &.prov { background: rgba(26, 77, 46, 0.1); svg { color: #1a4d2e; } }
  &.city { background: rgba(45, 106, 79, 0.1); svg { color: #2d6a4f; } }
  &.county { background: rgba(82, 183, 136, 0.1); svg { color: #52b788; } }
  &.community { background: rgba(32, 201, 151, 0.1); svg { color: #20c997; } }
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

// 详情卡片
.org-detail-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #f5f5f5;
}

.detail-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.org-avatar {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg { width: 28px; height: 28px; }
  
  &.level-1 { background: linear-gradient(135deg, #1a4d2e, #2d6a4f); svg { color: #fff; } }
  &.level-2 { background: linear-gradient(135deg, #2d6a4f, #40916c); svg { color: #fff; } }
  &.level-3 { background: linear-gradient(135deg, #52b788, #74c69d); svg { color: #fff; } }
  &.level-4 { background: linear-gradient(135deg, #95d5b2, #b7e4c7); svg { color: #1a4d2e; } }
  &.level-5 { background: linear-gradient(135deg, #b7e4c7, #d8f3dc); svg { color: #1a4d2e; } }
  &.level-6 { background: linear-gradient(135deg, #20c997, #52b788); svg { color: #fff; } }
}

.org-title-info {
  h2 {
    margin: 0 0 8px;
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
  }
}

.org-badges {
  display: flex;
  gap: 8px;
}

.badge {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  
  &.level-badge {
    &.level-1 { background: rgba(26, 77, 46, 0.1); color: #1a4d2e; }
    &.level-2 { background: rgba(45, 106, 79, 0.1); color: #2d6a4f; }
    &.level-3 { background: rgba(82, 183, 136, 0.1); color: #52b788; }
    &.level-4 { background: rgba(149, 213, 178, 0.15); color: #40916c; }
    &.level-5 { background: rgba(149, 213, 178, 0.2); color: #40916c; }
    &.level-6 { background: rgba(32, 201, 151, 0.15); color: #20c997; }
  }
  
  &.code-badge {
    background: #f5f7fa;
    color: #666;
    font-family: monospace;
  }
}

.detail-actions {
  display: flex;
  gap: 8px;
  
  :deep(.el-button) {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    
    svg { width: 14px; height: 14px; }
  }
}

.detail-body {
  padding: 20px 24px 24px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #999;
}

.detail-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  
  &.code {
    font-family: monospace;
    color: #1a4d2e;
  }
}

// ========== 普法教育模块 ==========
.edu-module {
  border-top: 1px dashed #e8e8e8;
  padding: 24px;
  background: linear-gradient(180deg, #fafcff 0%, #ffffff 100%);
}

.edu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.edu-title {
  display: flex;
  align-items: center;
  gap: 10px;
  
  svg { width: 22px; height: 22px; color: #1a4d2e; }
  
  span {
    font-size: 16px;
    font-weight: 600;
    color: #1a4d2e;
  }
}

.edu-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.edu-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.edu-stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #1a4d2e;
  line-height: 1;
  margin-bottom: 6px;
}

.edu-stat-label {
  font-size: 12px;
  color: #999;
}

// 居民列表
.residents-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.residents-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.residents-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.resident-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fafbfc;
  border-radius: 10px;
  transition: all 0.2s;
  
  &:hover {
    background: #f0f7f2;
    
    .resident-actions { opacity: 1; }
  }
}

.resident-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52b788, #95d5b2);
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg { width: 20px; height: 20px; color: #fff; }
}

.resident-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.resident-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.resident-meta {
  font-size: 12px;
  color: #999;
}

.resident-status {
  flex-shrink: 0;
}

.status-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  
  &.participated {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
  }
  
  &.not-participated {
    background: rgba(156, 163, 175, 0.15);
    color: #9ca3af;
  }
}

.resident-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  
  :deep(.el-button) {
    font-size: 12px;
    padding: 4px 8px;
  }
}

// 普法活动记录
.edu-events-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
}

.edu-events-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.edu-events-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edu-event-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 14px;
  background: #fafbfc;
  border-radius: 8px;
  
  &:hover { background: #f0f7f2; }
}

.event-date {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  width: 80px;
}

.event-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.event-title {
  font-size: 13px;
  color: #333;
}

.event-meta {
  font-size: 12px;
  color: #52b788;
  background: rgba(82, 183, 136, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
}

// 空状态
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 20px;
  padding: 60px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f7f2, #e8f0e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  
  svg { width: 36px; height: 36px; color: #52b788; }
}

.empty-state p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

// 下级组织
.child-orgs-section {
  background: #fff;
  border-radius: 20px;
  padding: 20px 24px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  
  h3 {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: #1a1a1a;
  }
}

.child-count {
  font-size: 12px;
  color: #999;
  background: #f5f7fa;
  padding: 4px 10px;
  border-radius: 20px;
}

.child-orgs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}

.child-org-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-color: #52b788;
    background: rgba(82, 183, 136, 0.02);
    
    .arrow-icon { opacity: 1; transform: translateX(2px); }
  }
}

.child-org-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  svg { width: 18px; height: 18px; }
  
  &.level-1 { background: rgba(26, 77, 46, 0.1); svg { color: #1a4d2e; } }
  &.level-2 { background: rgba(45, 106, 79, 0.1); svg { color: #2d6a4f; } }
  &.level-3 { background: rgba(82, 183, 136, 0.1); svg { color: #52b788; } }
  &.level-4 { background: rgba(149, 213, 178, 0.15); svg { color: #40916c; } }
  &.level-5 { background: rgba(149, 213, 178, 0.2); svg { color: #40916c; } }
  &.level-6 { background: rgba(32, 201, 151, 0.15); svg { color: #20c997; } }
}

.child-org-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.child-org-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.child-org-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.child-org-code {
  font-size: 11px;
  color: #999;
  font-family: monospace;
}

.badge.small {
  font-size: 10px;
  padding: 2px 6px;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  color: #ccc;
  opacity: 0;
  transition: all 0.2s;
  flex-shrink: 0;
}

// 弹窗样式
:deep(.org-dialog) {
  .el-dialog {
    border-radius: 20px;
    overflow: hidden;
  }
  .el-dialog__header {
    padding: 20px 24px !important;
    margin: 0 !important;
    border-bottom: 1px solid #f0f0f0;
  }
  .el-dialog__title {
    font-weight: 600;
    color: #1a4d2e;
  }
  .el-dialog__body {
    padding: 24px !important;
  }
  .el-dialog__footer {
    padding: 16px 24px !important;
    border-top: 1px solid #f0f0f0;
  }
}
</style>
