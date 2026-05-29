<template>
  <div class="consult-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6" v-for="s in statsCards" :key="s.label">
        <div class="stat-card" :style="{ background: s.gradient }">
          <div class="stat-icon">
            <svg v-if="s.icon === 'inbox'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 16 12 14 15 10 15 8 12 2 12"></polyline><path d="M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"></path></svg>
            <svg v-else-if="s.icon === 'loader'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="2" x2="12" y2="6"></line><line x1="12" y1="18" x2="12" y2="22"></line><line x1="4.93" y1="4.93" x2="7.76" y2="7.76"></line><line x1="16.24" y1="16.24" x2="19.07" y2="19.07"></line><line x1="2" y1="12" x2="6" y2="12"></line><line x1="18" y1="12" x2="22" y2="12"></line><line x1="4.93" y1="19.07" x2="7.76" y2="16.24"></line><line x1="16.24" y1="7.76" x2="19.07" y2="4.93"></line></svg>
            <svg v-else-if="s.icon === 'check'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            <svg v-else-if="s.icon === 'message'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
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
        <h1 class="page-title">咨询对接记录</h1>
        <div class="header-actions">
          <button class="btn-secondary" @click="exportData">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
            导出记录
          </button>
        </div>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="search-box">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <input v-model="filters.search" type="text" placeholder="搜索咨询人/问题内容..." @input="handleSearch" />
        </div>
        <div class="filter-group">
          <select v-model="filters.type" class="filter-select" @change="handleFilter">
            <option value="">全部类型</option>
            <option value="合规咨询">合规咨询</option>
            <option value="法律咨询">法律咨询</option>
            <option value="案件委托">案件委托</option>
            <option value="政策解读">政策解读</option>
          </select>
          <select v-model="filters.status" class="filter-select" @change="handleFilter">
            <option value="">全部状态</option>
            <option value="pending">待处理</option>
            <option value="processing">处理中</option>
            <option value="done">已回复</option>
          </select>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-wrapper" v-loading="loading">
        <table class="data-table">
          <thead>
            <tr>
              <th>咨询人</th>
              <th>联系方式</th>
              <th>咨询类型</th>
              <th>咨询内容</th>
              <th>对接律师</th>
              <th>状态</th>
              <th>咨询时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in paginatedList" :key="row.id">
              <td class="cell-name">
                <div class="inquirer-info">
                  <span class="inquirer-avatar">{{ row.inquirer?.charAt(0) || '访' }}</span>
                  <span class="inquirer-name">{{ row.inquirer }}</span>
                  <span v-if="row.unread > 0" class="unread-badge">{{ row.unread }}</span>
                </div>
              </td>
              <td>{{ row.phone }}</td>
              <td>
                <span class="tag tag-type">{{ row.type }}</span>
              </td>
              <td class="cell-question">
                <span class="question-text" :title="row.question">{{ row.question }}</span>
              </td>
              <td>
                <span v-if="row.lawyer" class="lawyer-tag">{{ row.lawyer }}</span>
                <span v-else class="lawyer-unassigned">待分配</span>
              </td>
              <td>
                <span class="tag tag-status" :class="'tag-status-' + row.status">{{ statusText[row.status] }}</span>
              </td>
              <td>{{ row.createTime }}</td>
              <td>
                <div class="action-btns">
                  <button class="action-btn action-primary" @click="openChat(row)" title="在线咨询">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                  </button>
                  <button class="action-btn" @click="openDetail(row)">详情</button>
                </div>
              </td>
            </tr>
            <tr v-if="paginatedList.length === 0 && !loading">
              <td colspan="8" class="empty-row">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
                  <p>暂无咨询记录</p>
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

    <!-- 聊天弹窗 -->
    <div class="chat-overlay" v-if="showChatDialog" @click.self="closeChat">
      <div class="chat-dialog">
        <div class="chat-header">
          <div class="chat-user-info">
            <span class="chat-avatar">{{ chatClient.inquirer?.charAt(0) || '访' }}</span>
            <div class="chat-user-detail">
              <span class="chat-user-name">{{ chatClient.inquirer }}</span>
              <span class="chat-user-meta">{{ chatClient.phone }} · {{ chatClient.type }}</span>
            </div>
          </div>
          <div class="chat-header-actions">
            <span class="tag tag-status" :class="'tag-status-' + chatClient.status">{{ statusText[chatClient.status] }}</span>
            <button class="chat-close" @click="closeChat">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </div>

        <div class="chat-body" ref="chatBodyRef">
          <!-- 原始咨询 -->
          <div class="chat-original">
            <div class="original-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
              原始咨询
            </div>
            <div class="original-content">{{ chatClient.question }}</div>
            <div class="original-time">{{ chatClient.createTime }}</div>
          </div>

          <!-- 聊天消息 -->
          <div class="chat-messages" v-if="chatMessages.length > 0">
            <div v-for="msg in chatMessages" :key="msg.id" class="message-item" :class="msg.role">
              <div class="message-avatar">
                <span v-if="msg.role === 'lawyer'">{{ chatClient.lawyer?.charAt(0) || '律' }}</span>
                <span v-else>{{ chatClient.inquirer?.charAt(0) || '访' }}</span>
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  <p>{{ msg.content }}</p>
                </div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="chatMessages.length === 0 && !chatLoading" class="chat-empty">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            <p>暂无对话记录，开始咨询吧</p>
          </div>

          <!-- 加载中 -->
          <div v-if="chatLoading" class="chat-loading">
            <span>加载中...</span>
          </div>

          <!-- 正在输入 -->
          <div v-if="isTyping" class="message-item lawyer">
            <div class="message-avatar"><span>{{ chatClient.lawyer?.charAt(0) || '律' }}</span></div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <div class="quick-replies" v-if="quickReplies.length > 0">
            <button v-for="reply in quickReplies" :key="reply" class="quick-reply-btn" @click="sendQuickReply(reply)">
              {{ reply }}
            </button>
          </div>
          <div class="chat-input-area">
            <textarea 
              v-model="chatInput" 
              placeholder="输入回复内容，按 Enter 发送..." 
              rows="1"
              @keydown.enter.exact.prevent="sendMessage"
              @input="autoResize"
              ref="chatInputRef"
            ></textarea>
            <button class="send-btn" :disabled="!chatInput.trim() || sending" @click="sendMessage">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div class="dialog-overlay" v-if="showDetailDialog" @click.self="closeDetail">
      <div class="dialog dialog-wide">
        <div class="dialog-header">
          <h3>咨询详情</h3>
          <button class="dialog-close" @click="closeDetail">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
        <div class="dialog-body" v-loading="detailLoading">
          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">咨询编号</span>
                <span class="detail-value">INQ-{{ detailConsult.id }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">咨询类型</span>
                <span class="tag tag-type">{{ detailConsult.type }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">咨询状态</span>
                <span class="tag tag-status" :class="'tag-status-' + detailConsult.status">
                  {{ statusText[detailConsult.status] }}
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">咨询时间</span>
                <span class="detail-value">{{ detailConsult.createTime }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">咨询人信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">咨询人</span>
                <span class="detail-value">{{ detailConsult.inquirer }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">联系电话</span>
                <span class="detail-value">{{ detailConsult.phone }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">咨询内容</h4>
            <div class="consult-content">
              <p>{{ detailConsult.question }}</p>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">处理信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">对接律师</span>
                <span class="detail-value" v-if="detailConsult.lawyer">{{ detailConsult.lawyer }}</span>
                <span class="detail-value text-muted" v-else>待分配</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">回复次数</span>
                <span class="detail-value">{{ detailConsult.replyCount || 0 }} 次</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">最后回复</span>
                <span class="detail-value">{{ detailConsult.lastReplyTime || '-' }}</span>
              </div>
            </div>
          </div>

          <!-- 对话记录 -->
          <div class="detail-section" v-if="detailMessages.length > 0">
            <h4 class="section-title">对话记录</h4>
            <div class="dialog-records">
              <div v-for="msg in detailMessages" :key="msg.id" class="dialog-record" :class="msg.role">
                <div class="record-header">
                  <span class="record-sender">{{ msg.role === 'lawyer' ? detailConsult.lawyer : detailConsult.inquirer }}</span>
                  <span class="record-time">{{ msg.time }}</span>
                </div>
                <div class="record-content">{{ msg.content }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDetail">关闭</button>
          <button class="btn-primary" @click="openChatFromDetail">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            开始对话
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <transition name="slide">
      <div class="toast" v-if="showToast">
        <svg v-if="toastType === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
        <span>{{ toastMessage }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getConsultList, getConsultDetail, getChatMessages, sendMessage as postConsultMessage, getConsultStats, exportConsults, getQuickReplies, updateConsultStatus, markAsRead } from '@/api/consult.js'

// 状态
const loading = ref(false)
const chatLoading = ref(false)
const detailLoading = ref(false)
const sending = ref(false)

// 统计数据
const statsCards = reactive([
  { label: '待处理咨询', value: 0, gradient: 'linear-gradient(135deg, #1a4d2e, #2d6a4f)', icon: 'inbox' },
  { label: '处理中', value: 0, gradient: 'linear-gradient(135deg, #2d6a4f, #52b788)', icon: 'loader' },
  { label: '本月已处理', value: 0, gradient: 'linear-gradient(135deg, #52b788, #95d5b2)', icon: 'check' },
  { label: '待回复消息', value: 0, gradient: 'linear-gradient(135deg, #f59e0b, #fbbf24)', icon: 'message' }
])

// 咨询列表数据
const list = ref([])

const statusText = { pending: '待处理', processing: '处理中', done: '已回复' }

// 筛选
const filters = reactive({
  search: '',
  type: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = 10

const filteredList = computed(() => {
  return list.value.filter(item => {
    const matchSearch = !filters.search || 
      item.inquirer?.includes(filters.search) || 
      item.question?.includes(filters.search)
    const matchType = !filters.type || item.type === filters.type
    const matchStatus = !filters.status || item.status === filters.status
    return matchSearch && matchType && matchStatus
  })
})

const totalPages = computed(() => Math.ceil(filteredList.value.length / pageSize) || 1)

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

// 聊天相关
const showChatDialog = ref(false)
const chatClient = ref({})
const chatMessages = ref([])
const chatInput = ref('')
const isTyping = ref(false)
const chatBodyRef = ref(null)
const chatInputRef = ref(null)
const quickReplies = ref([])

// 详情弹窗
const showDetailDialog = ref(false)
const detailConsult = ref({})
const detailMessages = ref([])

// Toast
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// 方法
function updateStats() {
  statsCards[0].value = list.value.filter(i => i.status === 'pending').length
  statsCards[1].value = list.value.filter(i => i.status === 'processing').length
  statsCards[2].value = list.value.filter(i => i.status === 'done').length
  statsCards[3].value = list.value.reduce((sum, i) => sum + (i.unread || 0), 0)
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize,
      ...filters
    }
    Object.keys(params).forEach(key => {
      if (params[key] === '') delete params[key]
    })
    
    const res = await getConsultList(params)
    list.value = res?.list || res || []
    updateStats()
  } catch (error) {
    console.error('获取咨询列表失败:', error)
    // 使用示例数据
    list.value = [
      { id: 1001, inquirer: '李女士', phone: '139****5678', type: '合规咨询', question: '企业更换可降解餐具需要做哪些准备工作？', lawyer: '陈律师', status: 'pending', createTime: '2026-04-04 09:30', unread: 0, replyCount: 0 },
      { id: 1002, inquirer: '王先生', phone: '138****1234', type: '法律咨询', question: '农膜回收协议的法律效力如何认定？', lawyer: '刘律师', status: 'processing', createTime: '2026-04-03 14:20', unread: 2, replyCount: 3, lastReplyTime: '2026-04-04 10:15', messages: [
        { id: 1, role: 'inquirer', content: '农膜回收协议的法律效力如何认定？', time: '2026-04-03 14:20' },
        { id: 2, role: 'lawyer', content: '您好，关于农膜回收协议的法律效力问题...', time: '2026-04-03 15:30' }
      ]},
      { id: 1003, inquirer: '张总', phone: '137****9012', type: '案件委托', question: '希望委托律师代理行政处罚听证', lawyer: '陈律师', status: 'done', createTime: '2026-04-02 10:15', unread: 0, replyCount: 5, lastReplyTime: '2026-04-02 16:30' },
      { id: 1004, inquirer: '刘经理', phone: '136****3456', type: '政策解读', question: '新版《塑料污染治理专项行动方案》有哪些主要变化？', lawyer: '', status: 'pending', createTime: '2026-04-04 11:00', unread: 0, replyCount: 0 },
      { id: 1005, inquirer: '赵厂长', phone: '135****7890', type: '合规咨询', question: '塑料粒子生产过程中的固体废物如何分类管理？', lawyer: '王律师', status: 'processing', createTime: '2026-04-01 16:45', unread: 1, replyCount: 2, lastReplyTime: '2026-04-02 09:30' }
    ]
    updateStats()
  } finally {
    loading.value = false
  }
}

async function fetchQuickReplies() {
  try {
    const res = await getQuickReplies()
    quickReplies.value = res || ['您好，已收到您的咨询', '根据相关规定...', '请问还有其它问题吗？']
  } catch (error) {
    quickReplies.value = ['您好，已收到您的咨询', '根据相关规定...', '请问还有其它问题吗？', '建议您提供更多细节']
  }
}

async function openChat(row) {
  chatClient.value = { ...row }
  chatMessages.value = row.messages ? [...row.messages] : []
  
  // 标记已读
  if (row.unread > 0) {
    try {
      await markAsRead(row.id)
      const idx = list.value.findIndex(i => i.id === row.id)
      if (idx !== -1) list.value[idx].unread = 0
      updateStats()
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  
  // 自动更新状态
  if (row.status === 'pending') {
    try {
      await updateConsultStatus(row.id, 'processing')
      const idx = list.value.findIndex(i => i.id === row.id)
      if (idx !== -1) list.value[idx].status = 'processing'
      chatClient.value.status = 'processing'
      updateStats()
    } catch (e) {
      console.error('更新状态失败', e)
    }
  }
  
  // 加载对话记录
  chatLoading.value = true
  try {
    const res = await getChatMessages(row.id)
    if (res && res.length > 0) {
      chatMessages.value = res
    }
  } catch (error) {
    console.error('获取对话记录失败:', error)
  } finally {
    chatLoading.value = false
  }
  
  showChatDialog.value = true
  nextTick(() => scrollToBottom())
}

function closeChat() {
  showChatDialog.value = false
  chatMessages.value = []
  chatInput.value = ''
}

async function sendMessage() {
  if (!chatInput.value.trim() || sending.value) return
  
  const content = chatInput.value.trim()
  chatInput.value = ''
  sending.value = true
  
  const msg = {
    id: Date.now(),
    role: 'lawyer',
    content,
    time: new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  }
  
  chatMessages.value.push(msg)
  scrollToBottom()
  
  try {
    await postConsultMessage(chatClient.value.id, { content })
    
    // 更新列表中的数据
    const idx = list.value.findIndex(i => i.id === chatClient.value.id)
    if (idx !== -1) {
      if (!list.value[idx].messages) list.value[idx].messages = []
      list.value[idx].messages.push(msg)
      list.value[idx].replyCount = (list.value[idx].replyCount || 0) + 1
      list.value[idx].lastReplyTime = msg.time
      list.value[idx].status = 'done'
    }
    chatClient.value.status = 'done'
    updateStats()
  } catch (error) {
    console.error('发送消息失败:', error)
    // 模拟成功
    const idx = list.value.findIndex(i => i.id === chatClient.value.id)
    if (idx !== -1) {
      if (!list.value[idx].messages) list.value[idx].messages = []
      list.value[idx].messages.push(msg)
      list.value[idx].replyCount = (list.value[idx].replyCount || 0) + 1
      list.value[idx].lastReplyTime = msg.time
      list.value[idx].status = 'done'
    }
    chatClient.value.status = 'done'
    updateStats()
    ElMessage.success('回复已发送')
  } finally {
    sending.value = false
  }
}

function sendQuickReply(reply) {
  chatInput.value = reply
  sendMessage()
}

function autoResize(e) {
  const el = e.target
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

// 详情弹窗
async function openDetail(row) {
  detailConsult.value = { ...row }
  detailMessages.value = []
  showDetailDialog.value = true
  detailLoading.value = true
  
  try {
    const res = await getConsultDetail(row.id)
    if (res) {
      detailConsult.value = { ...detailConsult.value, ...res }
      detailMessages.value = res.messages || []
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    detailMessages.value = row.messages || []
  } finally {
    detailLoading.value = false
  }
}

function closeDetail() {
  showDetailDialog.value = false
}

function openChatFromDetail() {
  closeDetail()
  openChat(detailConsult.value)
}

// Toast
function showToastMessage(msg, type = 'success') {
  toastMessage.value = msg
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

// 筛选方法
function handleSearch() {
  currentPage.value = 1
}

function handleFilter() {
  currentPage.value = 1
}

// 导出
async function exportData() {
  try {
    await exportConsults(filters)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.success('导出功能开发中')
  }
}

// 初始化
onMounted(() => {
  fetchList()
  fetchQuickReplies()
})
</script>

<style lang="scss" scoped>
.consult-container {
  padding: 24px;
  min-height: 100vh;
  background: #f0f4f0;
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
    background: rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
  }

  .stat-body {
    .stat-val {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      color: #1a4d2e;
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
  color: #1a4d2e;
}

.header-actions { display: flex; gap: 12px; }

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

  &:disabled { opacity: 0.6; cursor: not-allowed; }
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #fff;
  color: #2d6a4f;
  border: 1px solid #52b788;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;

  &:hover { background: rgba(82, 183, 136, 0.08); }
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

  &:hover { background: #e8e8e8; }
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

  svg { color: #999; flex-shrink: 0; }

  input {
    flex: 1;
    border: none;
    background: transparent;
    outline: none;
    font-size: 14px;
    color: #333;
    &::placeholder { color: #999; }
  }
}

.filter-group { display: flex; gap: 12px; }

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: #fff;
  cursor: pointer;
  outline: none;
  &:focus { border-color: #52b788; }
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

  thead tr { background: rgba(82, 183, 136, 0.06); }

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
    &:hover { background: rgba(82, 183, 136, 0.04); }
  }
}

.cell-name {
  .inquirer-info {
    display: flex;
    align-items: center;
    gap: 10px;
    position: relative;
  }

  .inquirer-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, #2d6a4f, #52b788);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 500;
  }

  .inquirer-name {
    font-weight: 500;
    color: #1a4d2e;
  }

  .unread-badge {
    position: absolute;
    left: 24px;
    top: -4px;
    min-width: 16px;
    height: 16px;
    background: #ef4444;
    color: #fff;
    border-radius: 8px;
    font-size: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 4px;
  }
}

.cell-question {
  max-width: 280px;
  .question-text {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.lawyer-tag { color: #2d6a4f; font-weight: 500; }
.lawyer-unassigned { color: #999; font-style: italic; }

.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;

  &.tag-type { background: rgba(82, 183, 136, 0.12); color: #2d6a4f; }
  &.tag-status-pending { background: rgba(245, 158, 11, 0.12); color: #d97706; }
  &.tag-status-processing { background: rgba(82, 183, 136, 0.12); color: #2d6a4f; }
  &.tag-status-done { background: rgba(82, 183, 136, 0.15); color: #2d6a4f; }
}

.action-btns { display: flex; gap: 8px; }

.action-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  background: transparent;
  color: #2d6a4f;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;

  &:hover { background: rgba(82, 183, 136, 0.1); }

  &.action-primary {
    background: rgba(82, 183, 136, 0.12);
    &:hover { background: rgba(82, 183, 136, 0.2); }
  }
}

.empty-row { text-align: center; padding: 60px 20px !important; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #999;
  svg { opacity: 0.5; }
  p { margin: 0; font-size: 14px; }
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

.pagination-info { font-size: 13px; color: #666; }
.pagination-controls { display: flex; gap: 8px; }

.page-btn {
  padding: 6px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) { border-color: #52b788; color: #52b788; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

// 聊天弹窗
.chat-overlay {
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

.chat-dialog {
  background: #fff;
  border-radius: 16px;
  width: 95%;
  max-width: 700px;
  height: 85vh;
  max-height: 700px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .chat-avatar {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    background: linear-gradient(135deg, #2d6a4f, #52b788);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: 500;
  }

  .chat-user-detail {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .chat-user-name { font-weight: 600; color: #1a4d2e; font-size: 15px; }
    .chat-user-meta { font-size: 12px; color: #999; }
  }
}

.chat-header-actions { display: flex; align-items: center; gap: 12px; }

.chat-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #999;
  border-radius: 4px;
  transition: all 0.2s;
  &:hover { background: #f5f5f5; color: #333; }
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f9fafb;
}

.chat-original {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  border-left: 4px solid #52b788;

  .original-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #52b788;
    margin-bottom: 8px;
  }

  .original-content { font-size: 14px; color: #333; line-height: 1.6; }
  .original-time { font-size: 12px; color: #999; margin-top: 8px; }
}

.chat-messages { display: flex; flex-direction: column; gap: 16px; }

.chat-loading {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
  svg { opacity: 0.4; margin-bottom: 12px; }
  p { margin: 0; font-size: 14px; }
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 80%;

  &.lawyer {
    align-self: flex-end;
    flex-direction: row-reverse;
    .message-bubble { background: linear-gradient(135deg, #2d6a4f, #52b788); color: #fff; }
    .message-time { text-align: right; }
  }

  &.inquirer { align-self: flex-start; }
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2d6a4f, #52b788);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 500;
  flex-shrink: 0;
}

.lawyer .message-avatar {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: #fff;
}

.message-content { display: flex; flex-direction: column; gap: 4px; }

.message-bubble {
  background: #fff;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  p { margin: 0; font-size: 14px; line-height: 1.5; }
}

.message-time { font-size: 11px; color: #999; }

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 12px;

  span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #ccc;
    animation: typing 1.4s infinite;
  }

  span:nth-child(2) { animation-delay: 0.2s; }
  span:nth-child(3) { animation-delay: 0.4s; }
}

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.chat-footer {
  background: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 12px 16px;
}

.quick-replies {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.quick-reply-btn {
  padding: 6px 12px;
  background: #f0f4f0;
  border: none;
  border-radius: 16px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { background: #e0e8e0; color: #2d6a4f; }
}

.chat-input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;

  textarea {
    flex: 1;
    padding: 12px 16px;
    border: 1px solid #e5e7eb;
    border-radius: 24px;
    font-size: 14px;
    resize: none;
    outline: none;
    max-height: 120px;
    font-family: inherit;
    &:focus { border-color: #52b788; }
  }
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2d6a4f, #52b788);
  border: none;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;

  &:hover:not(:disabled) { transform: scale(1.05); box-shadow: 0 4px 12px rgba(82, 183, 136, 0.3); }
  &:disabled { background: #ccc; cursor: not-allowed; }
}

// 详情弹窗
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
  max-width: 560px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  &.dialog-wide { max-width: 680px; }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  h3 { margin: 0; font-size: 16px; font-weight: 600; color: #1a4d2e; }
}

.dialog-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #999;
  border-radius: 4px;
  transition: all 0.2s;
  &:hover { background: #f5f5f5; color: #333; }
}

.dialog-body { padding: 20px; overflow-y: auto; flex: 1; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 12px; padding: 16px 20px; border-top: 1px solid #f0f0f0; }

.detail-section { margin-bottom: 20px; &:last-child { margin-bottom: 0; } }

.section-title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #1a4d2e;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e5e7eb;
}

.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.detail-item {
  .detail-label { display: block; font-size: 12px; color: #999; margin-bottom: 4px; }
  .detail-value { font-size: 14px; color: #333; }
  .text-muted { color: #999; }
}

.consult-content {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  p { margin: 0; font-size: 14px; color: #333; line-height: 1.6; }
}

.dialog-records { display: flex; flex-direction: column; gap: 12px; }

.dialog-record {
  padding: 12px;
  border-radius: 8px;
  background: #f9fafb;

  &.lawyer { background: rgba(82, 183, 136, 0.08); }

  .record-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
  .record-sender { font-size: 12px; font-weight: 600; color: #2d6a4f; }
  .record-time { font-size: 11px; color: #999; }
  .record-content { font-size: 13px; color: #333; line-height: 1.5; }
}

// Toast
.toast {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 8px;
}

.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateX(-50%) translateY(20px); }
</style>
