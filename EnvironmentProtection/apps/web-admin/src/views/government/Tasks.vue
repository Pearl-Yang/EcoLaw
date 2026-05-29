<template>
  <div class="tasks-container">
    <!-- 玻璃卡片：页头 -->
    <div class="glass-card page-header-card">
      <div class="header-content">
        <div class="header-left">
          <div class="title-wrapper">
            <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
              <rect x="9" y="3" width="6" height="4" rx="1"/>
              <path d="M9 12h6"/>
              <path d="M9 16h6"/>
            </svg>
            <h1 class="page-title">任务管理</h1>
          </div>
          <div class="task-count-badge">
            <span class="count-num">{{ total }}</span>
            <span class="count-label">个任务</span>
          </div>
        </div>
        <div class="header-actions">
          <button class="btn-glass btn-outline" @click="handleExportAll">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            导出台账
          </button>
          <button class="btn-glass btn-primary" @click="handleCreate">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            新建任务
          </button>
        </div>
      </div>
    </div>

    <!-- 玻璃卡片：查询表单 -->
    <div class="glass-card query-card">
      <div class="query-form">
        <div class="form-row">
          <div class="form-item">
            <label class="form-label">关键词</label>
            <input 
              v-model="queryParams.keyword" 
              type="text" 
              class="glass-input" 
              placeholder="任务名称/内容"
            />
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <select v-model="queryParams.status" class="glass-select">
              <option value="">全部</option>
              <option value="pending">待派发</option>
              <option value="in_progress">执行中</option>
              <option value="completed">已完成</option>
              <option value="overdue">已逾期</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">任务类型</label>
            <select v-model="queryParams.type" class="glass-select">
              <option value="">全部</option>
              <option value="publicity">普法宣传</option>
              <option value="compliance">合规检查</option>
              <option value="training">培训组织</option>
              <option value="recycle">回收检查</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">区域</label>
            <select v-model="queryParams.region" class="glass-select">
              <option value="">选择区域</option>
              <option value="region1-1">XX区 XX街道</option>
              <option value="region1-2">XX区 XX镇</option>
              <option value="region2-1">YY县 YY镇</option>
              <option value="region2-2">YY县 YY乡</option>
            </select>
          </div>
          <div class="form-item date-range">
            <label class="form-label">时间范围</label>
            <div class="date-inputs">
              <input 
                v-model="dateStart" 
                type="date" 
                class="glass-input glass-date"
              />
              <span class="date-separator">至</span>
              <input 
                v-model="dateEnd" 
                type="date" 
                class="glass-input glass-date"
              />
            </div>
          </div>
          <div class="form-actions">
            <button class="btn-glass btn-search" @click="handleQuery">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"/>
                <path d="M21 21l-4.35-4.35"/>
              </svg>
              查询
            </button>
            <button class="btn-glass btn-reset" @click="handleReset">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                <path d="M3 3v5h5"/>
              </svg>
              重置
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 玻璃卡片：数据表格 -->
    <div class="glass-card table-card">
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
      </div>
      <table class="glass-table">
        <thead>
          <tr>
            <th class="col-check"><input type="checkbox" /></th>
            <th class="col-title">任务名称</th>
            <th class="col-type">类型</th>
            <th class="col-region">执行区域</th>
            <th class="col-status">状态</th>
            <th class="col-progress">进度</th>
            <th class="col-count">参与人数</th>
            <th class="col-time">截止时间</th>
            <th class="col-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in tasks" :key="row.id">
            <td><input type="checkbox" /></td>
            <td class="cell-title" :title="row.title">{{ row.title }}</td>
            <td>
              <span class="tag" :class="'tag-' + row.type">{{ getTypeText(row.type) }}</span>
            </td>
            <td>{{ row.region }}</td>
            <td>
              <span class="tag" :class="'status-' + row.status">{{ getStatusText(row.status) }}</span>
            </td>
            <td>
              <div class="progress-cell">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: (row.progress || 0) + '%', background: getProgressColor(row.status) }"></div>
                </div>
                <span class="progress-text">{{ row.progress || 0 }}%</span>
              </div>
            </td>
            <td class="cell-count">
              <span class="count-highlight">{{ row.joinedCount || 0 }}</span>/{{ row.targetCount || 0 }}人
            </td>
            <td :class="{ 'overdue-text': row.status === 'overdue' }">{{ row.endTime }}</td>
            <td>
              <div class="action-btns">
                <button class="action-btn view" @click="handleView(row)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  查看
                </button>
                <button class="action-btn edit" @click="handleEdit(row)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  编辑
                </button>
                <button class="action-btn dispatch" @click="handleDispatch(row)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="22" y1="2" x2="11" y2="13"/>
                    <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                  </svg>
                  派发
                </button>
                <button class="action-btn delete" @click="handleDelete(row)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    <line x1="10" y1="11" x2="10" y2="17"/>
                    <line x1="14" y1="11" x2="14" y2="17"/>
                  </svg>
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pagination-wrapper">
        <div class="pagination-info">
          共 <span class="highlight">{{ total }}</span> 条记录
        </div>
        <div class="pagination-controls">
          <select v-model="queryParams.pageSize" class="glass-select page-size-select" @change="handleSizeChange">
            <option :value="10">10条/页</option>
            <option :value="20">20条/页</option>
            <option :value="50">50条/页</option>
            <option :value="100">100条/页</option>
          </select>
          <div class="pagination-btns">
            <button class="page-btn" :disabled="queryParams.page <= 1" @click="queryParams.page = 1">«</button>
            <button class="page-btn" :disabled="queryParams.page <= 1" @click="queryParams.page--">‹</button>
            <span class="page-indicator">{{ queryParams.page }} / {{ totalPages }}</span>
            <button class="page-btn" :disabled="queryParams.page >= totalPages" @click="queryParams.page++">›</button>
            <button class="page-btn" :disabled="queryParams.page >= totalPages" @click="queryParams.page = totalPages">»</button>
          </div>
          <input 
            type="number" 
            class="glass-input page-jump" 
            v-model.number="jumpPage" 
            min="1" 
            :max="totalPages"
            placeholder="跳转"
            @keyup.enter="handleJump"
          />
        </div>
      </div>
    </div>

    <!-- 新建/编辑任务弹窗 -->
    <div class="dialog-overlay" v-if="createDialogVisible" @click.self="createDialogVisible = false">
      <div class="glass-dialog">
        <div class="dialog-header">
          <h2 class="dialog-title">{{ isEdit ? '编辑任务' : '新建任务' }}</h2>
          <button class="dialog-close" @click="createDialogVisible = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="dialog-body">
          <div class="form-item full-width">
            <label class="form-label">任务名称 <span class="required">*</span></label>
            <input 
              v-model="taskForm.title" 
              type="text" 
              class="glass-input" 
              placeholder="请输入任务名称"
              maxlength="100"
            />
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">任务类型 <span class="required">*</span></label>
              <select v-model="taskForm.type" class="glass-select">
                <option value="">请选择</option>
                <option value="publicity">普法宣传</option>
                <option value="compliance">合规检查</option>
                <option value="training">培训组织</option>
                <option value="recycle">回收检查</option>
              </select>
            </div>
            <div class="form-item">
              <label class="form-label">执行区域</label>
              <select v-model="taskForm.region" class="glass-select">
                <option value="">选择区域</option>
                <option value="region1-1">XX区 XX街道</option>
                <option value="region1-2">XX区 XX镇</option>
                <option value="region2-1">YY县 YY镇</option>
                <option value="region2-2">YY县 YY乡</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">截止时间 <span class="required">*</span></label>
              <input 
                v-model="taskForm.endTime" 
                type="datetime-local" 
                class="glass-input"
              />
            </div>
            <div class="form-item">
              <label class="form-label">目标人数 <span class="required">*</span></label>
              <input 
                v-model.number="taskForm.targetCount" 
                type="number" 
                class="glass-input" 
                min="1" 
                max="1000"
              />
            </div>
          </div>
          <div class="form-item full-width">
            <label class="form-label">任务描述 <span class="required">*</span></label>
            <textarea 
              v-model="taskForm.description" 
              class="glass-textarea" 
              placeholder="请输入任务描述"
              rows="3"
              maxlength="500"
            ></textarea>
          </div>
          <div class="form-item full-width">
            <label class="form-label">任务要求</label>
            <div class="requirement-list">
              <div v-for="(req, index) in taskForm.requirements" :key="index" class="requirement-item">
                <input 
                  v-model="taskForm.requirements[index]" 
                  type="text" 
                  class="glass-input" 
                  placeholder="输入任务要求"
                />
                <button class="btn-icon delete" @click="removeRequirement(index)" v-if="taskForm.requirements.length > 1">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18"/>
                    <line x1="6" y1="6" x2="18" y2="18"/>
                  </svg>
                </button>
              </div>
              <button class="btn-glass btn-add-req" @click="addRequirement">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                添加要求
              </button>
            </div>
          </div>
          <div class="form-item full-width">
            <label class="form-label">上传附件</label>
            <div class="upload-area">
              <button class="btn-glass btn-upload">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="17 8 12 3 7 8"/>
                  <line x1="12" y1="3" x2="12" y2="15"/>
                </svg>
                选择文件
              </button>
              <p class="upload-tip">支持 PDF、Word、Excel、压缩包格式，单个文件不超过 20MB</p>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-glass btn-cancel" @click="createDialogVisible = false" :disabled="submitting">取消</button>
          <button class="btn-glass btn-primary" :class="{ loading: submitting }" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? '提交中...' : (isEdit ? '保存修改' : '创建任务') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 任务详情弹窗 -->
    <div class="dialog-overlay" v-if="viewDialogVisible" @click.self="viewDialogVisible = false">
      <div class="glass-dialog detail-dialog">
        <div class="dialog-header">
          <h2 class="dialog-title">任务详情</h2>
          <button class="dialog-close" @click="viewDialogVisible = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="dialog-body" v-if="currentTask">
          <div class="detail-section">
            <h3 class="detail-title">{{ currentTask.title }}</h3>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">任务类型</span>
              <span class="tag" :class="'tag-' + currentTask.type">{{ getTypeText(currentTask.type) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">执行区域</span>
              <span class="detail-value">{{ currentTask.region || '未指定' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">状态</span>
              <span class="tag" :class="'status-' + currentTask.status">{{ getStatusText(currentTask.status) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">参与人数</span>
              <span class="detail-value">{{ currentTask.joinedCount || 0 }}/{{ currentTask.targetCount || 0 }} 人</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">开始时间</span>
              <span class="detail-value">{{ currentTask.startTime || '未开始' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">截止时间</span>
              <span class="detail-value" :class="{ 'overdue-text': currentTask.status === 'overdue' }">{{ currentTask.endTime }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建人</span>
              <span class="detail-value">{{ currentTask.creatorName }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ currentTask.createdAt }}</span>
            </div>
          </div>
          <div class="detail-section" v-if="currentTask.description">
            <h4 class="section-title">任务描述</h4>
            <p class="description-text">{{ currentTask.description }}</p>
          </div>
          <div class="detail-section" v-if="currentTask.requirements?.length > 0">
            <h4 class="section-title">任务要求</h4>
            <ul class="requirements-display">
              <li v-for="(req, index) in currentTask.requirements" :key="index">{{ req }}</li>
            </ul>
          </div>
          <div class="detail-section" v-if="currentTask.attachments?.length > 0">
            <h4 class="section-title">任务附件</h4>
            <div class="attachments-display">
              <div v-for="file in currentTask.attachments" :key="file.id" class="attachment-display">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
                <span class="file-name">{{ file.name }}</span>
                <button class="btn-download">下载</button>
              </div>
            </div>
          </div>
          <div class="detail-section">
            <h4 class="section-title">执行进度</h4>
            <div class="progress-display">
              <div class="progress-bar large">
                <div class="progress-fill" :style="{ width: (currentTask.progress || 0) + '%', background: getProgressColor(currentTask.status) }"></div>
              </div>
              <span class="progress-percent">{{ currentTask.progress || 0 }}%</span>
            </div>
            <p class="progress-info">已完成 {{ currentTask.joinedCount || 0 }} 人，目标 {{ currentTask.targetCount || 0 }} 人</p>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-glass btn-cancel" @click="viewDialogVisible = false">关闭</button>
          <button class="btn-glass btn-dispatch" @click="handleDispatch(currentTask)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="22" y1="2" x2="11" y2="13"/>
              <polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
            派发
          </button>
        </div>
      </div>
    </div>

    <!-- 派发弹窗 -->
    <div class="dialog-overlay" v-if="dispatchDialogVisible" @click.self="dispatchDialogVisible = false">
      <div class="glass-dialog dispatch-dialog">
        <div class="dialog-header">
          <h2 class="dialog-title">任务派发</h2>
          <button class="dialog-close" @click="dispatchDialogVisible = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="dialog-body">
          <div class="form-item full-width">
            <label class="form-label">选择执行对象</label>
            <div class="target-groups">
              <div class="target-group">
                <h4 class="group-title">政府机构</h4>
                <label class="target-item">
                  <input type="checkbox" value="gov_1" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX区司法局</span>
                </label>
                <label class="target-item">
                  <input type="checkbox" value="gov_2" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX镇政府</span>
                </label>
              </div>
              <div class="target-group">
                <h4 class="group-title">企业</h4>
                <label class="target-item">
                  <input type="checkbox" value="ent_1" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX塑料厂</span>
                </label>
                <label class="target-item">
                  <input type="checkbox" value="ent_2" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX合作社</span>
                </label>
              </div>
              <div class="target-group">
                <h4 class="group-title">基层单位</h4>
                <label class="target-item">
                  <input type="checkbox" value="grass_1" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX社区居委会</span>
                </label>
                <label class="target-item">
                  <input type="checkbox" value="grass_2" v-model="dispatchForm.targetIds" />
                  <span class="target-name">XX村委会</span>
                </label>
              </div>
            </div>
          </div>
          <div class="form-item full-width">
            <label class="form-label">派发说明</label>
            <textarea 
              v-model="dispatchForm.remark" 
              class="glass-textarea" 
              placeholder="请输入派发说明"
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-glass btn-cancel" @click="dispatchDialogVisible = false">取消</button>
          <button class="btn-glass btn-primary" @click="handleDispatchConfirm">确认派发</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// ============ 1. 状态定义 ============
const loading = ref(false)
const submitting = ref(false)
const tasks = ref([])
const total = ref(0)
const createDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dispatchDialogVisible = ref(false)
const currentTask = ref(null)
const isEdit = ref(false)
const dateStart = ref('')
const dateEnd = ref('')
const jumpPage = ref(null)

// 任务表单引用
const taskFormRef = ref()

// ============ 2. 查询参数 ============
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  status: '',
  region: '',
  dateRange: []
})

// ============ 3. 任务表单数据 ============
const taskForm = reactive({
  title: '',
  type: '',
  region: '',
  description: '',
  endTime: '',
  targetCount: 10,
  requirements: ['']
})

// 任务表单验证规则
const taskFormRules = {
  title: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 100, message: '任务名称2-100字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入任务描述', trigger: 'blur' },
    { max: 500, message: '描述不超过500字符', trigger: 'blur' }
  ],
  endTime: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ],
  targetCount: [
    { required: true, message: '请输入目标人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '目标人数至少1人', trigger: 'blur' }
  ]
}

// ============ 4. 派发表单 ============
const dispatchForm = reactive({
  targetIds: [],
  remark: ''
})

// ============ 5. 计算属性 ============
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / queryParams.pageSize)))

// ============ 6. 工具函数 ============
const getTypeText = (type) => {
  const map = { publicity: '普法宣传', compliance: '合规检查', training: '培训组织', recycle: '回收检查' }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = { pending: '待派发', in_progress: '执行中', completed: '已完成', overdue: '已逾期', rejected: '已驳回' }
  return map[status] || status
}

const getProgressColor = (status) => {
  const map = { pending: '#909399', in_progress: '#52b788', completed: '#1a4d2e', overdue: '#ef4444' }
  return map[status] || '#52b788'
}

// ============ 7. 数据获取 ============
function getMockTasks() {
  return [
    { id: '1', title: 'XX社区禁塑宣传活动', type: 'publicity', status: 'in_progress', region: 'XX区XX街道', progress: 65, joinedCount: 8, targetCount: 10, endTime: '2026-04-20', creatorName: '张三', createdAt: '2026-04-01', description: '开展社区禁塑宣传活动', requirements: ['发放宣传资料', '拍照记录', '提交活动报告'] },
    { id: '2', title: '企业合规检查', type: 'compliance', status: 'pending', region: 'XX区', progress: 0, joinedCount: 0, targetCount: 20, endTime: '2026-04-30', creatorName: '李四', createdAt: '2026-04-02', description: '对企业进行塑料污染防治合规检查' },
    { id: '3', title: '农膜回收培训', type: 'training', status: 'completed', region: 'YY县', progress: 100, joinedCount: 50, targetCount: 50, endTime: '2026-03-15', creatorName: '王五', createdAt: '2026-03-01', description: '组织农户参加农膜回收培训' },
    { id: '4', title: '塑料制品抽查', type: 'recycle', status: 'overdue', region: 'YY县YY镇', progress: 30, joinedCount: 3, targetCount: 15, endTime: '2026-03-10', creatorName: '赵六', createdAt: '2026-02-20', description: '对辖区内塑料制品进行质量抽查' }
  ]
}

async function fetchTasks() {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    tasks.value = getMockTasks()
    total.value = tasks.value.length
  } finally {
    loading.value = false
  }
}

// ============ 8. 生命周期：弹窗状态监听 ============
watch(createDialogVisible, (val) => {
  if (val) {
    nextTick(() => taskFormRef.value?.clearValidate())
  }
})

// ============ 9. 查询操作 ============
const handleQuery = () => {
  queryParams.page = 1
  fetchTasks()
}

const handleReset = () => {
  Object.assign(queryParams, { keyword: '', type: '', status: '', region: '', page: 1 })
  dateStart.value = ''
  dateEnd.value = ''
  fetchTasks()
}

const handleSizeChange = () => {
  queryParams.page = 1
  fetchTasks()
}

const handleJump = () => {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    queryParams.page = jumpPage.value
    fetchTasks()
  }
  jumpPage.value = null
}

// ============ 10. 生命周期：表单操作 ============
const handleCreate = () => {
  isEdit.value = false
  resetTaskForm()
  createDialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(taskForm, { ...row, requirements: row.requirements?.length ? [...row.requirements] : [''] })
  createDialogVisible.value = true
}

const resetTaskForm = () => {
  Object.assign(taskForm, {
    title: '',
    type: '',
    region: '',
    description: '',
    endTime: '',
    targetCount: 10,
    requirements: ['']
  })
}

const handleCloseCreateDialog = () => {
  resetTaskForm()
  taskFormRef.value?.clearValidate()
}

// ============ 11. 动态要求项操作 ============
const addRequirement = () => {
  taskForm.requirements.push('')
}

const removeRequirement = (index) => {
  taskForm.requirements.splice(index, 1)
}

// ============ 12. 生命周期：表单提交 ============
const handleSubmit = async () => {
  try {
    const valid = await taskFormRef.value?.validate().catch(() => false)
    if (!valid) return

    submitting.value = true

    // 模拟 API 调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
    createDialogVisible.value = false
    fetchTasks()
  } catch (err) {
    if (err !== false) {
      ElMessage.error(isEdit.value ? '保存失败' : '创建失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleView = (row) => {
  currentTask.value = row
  viewDialogVisible.value = true
}

const handleDispatch = (row) => {
  currentTask.value = row
  dispatchForm.targetIds = []
  dispatchForm.remark = ''
  viewDialogVisible.value = false
  dispatchDialogVisible.value = true
}

const handleDispatchConfirm = () => {
  if (dispatchForm.targetIds.length === 0) {
    alert('请选择执行对象')
    return
  }
  alert('派发成功')
  dispatchDialogVisible.value = false
}

const handleDelete = (row) => {
  if (confirm(`确定要删除任务"${row.title}"吗？`)) {
    alert('删除成功')
    fetchTasks()
  }
}

const handleExportAll = () => {
  alert('导出功能开发中')
}

onMounted(() => {
  fetchTasks()
})
</script>

<style lang="scss" scoped>
* {
  box-sizing: border-box;
}

.tasks-container {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f4f0 0%, #e8f0e8 100%);
}

.glass-card {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(26, 77, 46, 0.08), 0 2px 8px rgba(26, 77, 46, 0.04);
  margin-bottom: 20px;
}

.page-header-card {
  padding: 24px 28px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  width: 32px;
  height: 32px;
  color: #1a4d2e;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1a4d2e;
}

.task-count-badge {
  display: flex;
  align-items: baseline;
  gap: 4px;
  padding: 6px 14px;
  background: rgba(82, 183, 136, 0.15);
  border-radius: 20px;

  .count-num {
    font-size: 18px;
    font-weight: 700;
    color: #1a4d2e;
  }

  .count-label {
    font-size: 13px;
    color: #2d6a4f;
  }
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-glass {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &.btn-primary {
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;
    box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(82, 183, 136, 0.4);
    }
  }

  &.btn-outline {
    background: rgba(255, 255, 255, 0.7);
    color: #1a4d2e;
    border: 1px solid rgba(82, 183, 136, 0.3);

    &:hover {
      background: rgba(82, 183, 136, 0.1);
      border-color: #52b788;
    }
  }

  &.btn-search {
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3);
    }
  }

  &.btn-reset {
    background: rgba(255, 255, 255, 0.7);
    color: #2d6a4f;
    border: 1px solid rgba(82, 183, 136, 0.3);

    &:hover {
      background: rgba(82, 183, 136, 0.1);
    }
  }

  &.btn-cancel {
    background: rgba(255, 255, 255, 0.7);
    color: #666;
    border: 1px solid rgba(0, 0, 0, 0.1);

    &:hover {
      background: rgba(0, 0, 0, 0.05);
    }
  }

  &.btn-upload {
    background: rgba(255, 255, 255, 0.7);
    color: #2d6a4f;
    border: 1px solid rgba(82, 183, 136, 0.3);

    &:hover {
      background: rgba(82, 183, 136, 0.1);
    }
  }

  &.btn-dispatch {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: #fff;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
    }
  }

  &.loading {
    opacity: 0.7;
    pointer-events: none;
  }
}

.query-card {
  padding: 24px 28px;
}

.query-form {
  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: flex-end;
  }
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;

  &.date-range {
    flex: 1;
    min-width: 260px;
  }

  &.full-width {
    width: 100%;
  }
}

.date-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  color: #2d6a4f;
  font-size: 14px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: #2d6a4f;

  .required {
    color: #ef4444;
  }
}

.glass-input, .glass-select, .glass-textarea {
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 10px;
  font-size: 14px;
  color: #1a4d2e;
  transition: all 0.3s ease;
  outline: none;

  &::placeholder {
    color: #95d5b2;
  }

  &:focus {
    border-color: #52b788;
    background: rgba(255, 255, 255, 0.8);
    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.1);
  }
}

.glass-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%232d6a4f' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 36px;
}

.glass-date {
  width: 140px;
}

.glass-textarea {
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.table-card {
  position: relative;
  overflow: hidden;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(82, 183, 136, 0.2);
  border-top-color: #52b788;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.glass-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;

  thead {
    background: rgba(26, 77, 46, 0.03);
  }

  th {
    padding: 14px 16px;
    text-align: left;
    font-weight: 600;
    color: #1a4d2e;
    border-bottom: 1px solid rgba(82, 183, 136, 0.15);
    white-space: nowrap;
  }

  td {
    padding: 14px 16px;
    color: #2d6a4f;
    border-bottom: 1px solid rgba(82, 183, 136, 0.08);
  }

  tbody tr {
    transition: background 0.2s ease;

    &:hover {
      background: rgba(82, 183, 136, 0.04);
    }

    &:last-child td {
      border-bottom: none;
    }
  }

  input[type="checkbox"] {
    width: 16px;
    height: 16px;
    accent-color: #52b788;
    cursor: pointer;
  }
}

.col-check { width: 50px; }
.col-title { min-width: 200px; }
.col-type { width: 100px; }
.col-region { width: 120px; }
.col-status { width: 100px; }
.col-progress { width: 160px; }
.col-count { width: 100px; }
.col-time { width: 120px; }
.col-action { width: 220px; }

.cell-title {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.cell-count {
  .count-highlight {
    color: #52b788;
    font-weight: 600;
  }
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;

  &.tag-publicity {
    background: rgba(82, 183, 136, 0.15);
    color: #1a4d2e;
  }

  &.tag-compliance {
    background: rgba(45, 106, 79, 0.15);
    color: #2d6a4f;
  }

  &.tag-training {
    background: rgba(149, 213, 178, 0.3);
    color: #2d6a4f;
  }

  &.tag-recycle {
    background: rgba(82, 183, 136, 0.1);
    color: #52b788;
  }

  &.status-pending {
    background: rgba(144, 144, 147, 0.15);
    color: #666;
  }

  &.status-in_progress {
    background: rgba(82, 183, 136, 0.15);
    color: #2d6a4f;
  }

  &.status-completed {
    background: rgba(26, 77, 46, 0.15);
    color: #1a4d2e;
  }

  &.status-overdue {
    background: rgba(239, 68, 68, 0.15);
    color: #dc2626;
  }
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: rgba(82, 183, 136, 0.15);
  border-radius: 4px;
  overflow: hidden;

  &.large {
    height: 12px;
    border-radius: 6px;
  }
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 13px;
  color: #666;
  width: 40px;
  text-align: right;
}

.overdue-text {
  color: #ef4444 !important;
  font-weight: 500;
}

.action-btns {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: transparent;

  svg {
    width: 14px;
    height: 14px;
  }

  &.view {
    color: #52b788;
    &:hover { background: rgba(82, 183, 136, 0.1); }
  }

  &.edit {
    color: #2d6a4f;
    &:hover { background: rgba(45, 106, 79, 0.1); }
  }

  &.dispatch {
    color: #f59e0b;
    &:hover { background: rgba(245, 158, 11, 0.1); }
  }

  &.delete {
    color: #ef4444;
    &:hover { background: rgba(239, 68, 68, 0.1); }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid rgba(82, 183, 136, 0.1);
}

.pagination-info {
  font-size: 14px;
  color: #2d6a4f;

  .highlight {
    color: #1a4d2e;
    font-weight: 600;
  }
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-size-select {
  padding: 6px 32px 6px 12px;
  font-size: 13px;
}

.pagination-btns {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.6);
  color: #2d6a4f;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover:not(:disabled) {
    background: rgba(82, 183, 136, 0.1);
    border-color: #52b788;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.page-indicator {
  padding: 0 12px;
  font-size: 14px;
  color: #2d6a4f;
}

.page-jump {
  width: 70px;
  padding: 6px 10px;
  font-size: 13px;
  text-align: center;
}

.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(26, 77, 46, 0.3);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.glass-dialog {
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(26, 77, 46, 0.2);

  &.detail-dialog {
    max-width: 800px;
  }

  &.dispatch-dialog {
    max-width: 600px;
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid rgba(82, 183, 136, 0.15);
}

.dialog-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1a4d2e;
}

.dialog-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.05);
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
  }
}

.dialog-body {
  padding: 24px 28px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid rgba(82, 183, 136, 0.1);
}

.requirement-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.requirement-item {
  display: flex;
  align-items: center;
  gap: 10px;

  .glass-input {
    flex: 1;
  }
}

.btn-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 10px;
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    background: rgba(239, 68, 68, 0.2);
  }
}

.btn-add-req {
  padding: 8px 16px;
  font-size: 13px;
  align-self: flex-start;

  svg {
    width: 14px;
    height: 14px;
  }
}

.upload-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.upload-tip {
  margin: 0;
  font-size: 12px;
  color: #95d5b2;
}

.detail-section {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.detail-title {
  margin: 0 0 16px;
  font-size: 22px;
  font-weight: 700;
  color: #1a4d2e;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-label {
  font-size: 13px;
  color: #95d5b2;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #2d6a4f;
}

.section-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1a4d2e;
  padding-bottom: 8px;
  border-bottom: 2px solid rgba(82, 183, 136, 0.2);
}

.description-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #2d6a4f;
}

.requirements-display {
  margin: 0;
  padding-left: 20px;

  li {
    font-size: 14px;
    line-height: 1.8;
    color: #2d6a4f;
  }
}

.attachments-display {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attachment-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(82, 183, 136, 0.05);
  border-radius: 12px;

  svg {
    width: 20px;
    height: 20px;
    color: #52b788;
    flex-shrink: 0;
  }

  .file-name {
    flex: 1;
    font-size: 14px;
    color: #2d6a4f;
  }
}

.btn-download {
  padding: 6px 14px;
  border: none;
  border-radius: 8px;
  background: rgba(82, 183, 136, 0.15);
  color: #2d6a4f;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(82, 183, 136, 0.25);
  }
}

.progress-display {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;

  .progress-bar {
    flex: 1;
  }

  .progress-percent {
    font-size: 16px;
    font-weight: 600;
    color: #1a4d2e;
    width: 50px;
    text-align: right;
  }
}

.progress-info {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.target-groups {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.target-group {
  .group-title {
    margin: 0 0 10px;
    font-size: 14px;
    font-weight: 600;
    color: #1a4d2e;
  }
}

.target-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: rgba(82, 183, 136, 0.05);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 8px;

  &:hover {
    background: rgba(82, 183, 136, 0.1);
  }

  input[type="checkbox"] {
    width: 18px;
    height: 18px;
    accent-color: #52b788;
  }

  .target-name {
    font-size: 14px;
    color: #2d6a4f;
  }
}
</style>
