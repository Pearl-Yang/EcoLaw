<template>
  <view class="page">
    <!-- 顶部标题栏 -->
    <view class="header">
      <text class="header-title">AI 法律咨询</text>
      <view class="header-actions">
        <view class="header-btn" @click="goHistory">
          <LineIcon name="clock" :size="24" color="#4B5563" />
          <text class="btn-text">历史</text>
        </view>
        <view class="header-btn" @click="toggleMode">
          <LineIcon name="settings" :size="24" color="#4B5563" />
        </view>
      </view>
      <view class="mode-indicator" :class="chatMode">
        <text>{{ chatMode === 'law' ? '法律模式' : '联网模式' }}</text>
      </view>
    </view>

    <!-- 对话区域 -->
    <scroll-view
      class="chat-area"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      @scrolltoupper="loadMoreHistory"
    >
      <!-- 欢迎消息 -->
      <view v-if="messages.length === 0" class="welcome">
        <view class="welcome-avatar">
          <LineIcon name="message-circle" :size="48" color="#166534" />
        </view>
        <view class="welcome-card">
          <text class="welcome-title">您好，我是绿法通AI法律助手</text>
          <text class="welcome-desc">我可以帮您解答白色污染治理相关法律问题、解读法规条文、提供合规建议等</text>
        </view>

        <!-- 常见问题 -->
        <view class="faq-section">
          <text class="faq-title">常见法律问题</text>
          <view class="faq-list">
            <view 
              v-for="faq in faqList" 
              :key="faq.id"
              class="faq-item"
              @click="useQuickAction(faq)"
            >
              <text class="faq-q">{{ faq.question }}</text>
              <LineIcon name="chevron-right" :size="24" color="#22C55E" />
            </view>
          </view>
        </view>

        <!-- 快捷功能 -->
        <view class="quick-actions">
          <text class="qa-title">快捷功能</text>
          <view class="qa-list">
            <view
              v-for="action in quickActions"
              :key="action.id"
              class="qa-item"
              @click="useQuickAction(action)"
            >
              <view class="qa-icon" :style="{ background: action.iconBg }">
                <LineIcon :name="action.iconName" :size="28" :color="action.iconColor" />
              </view>
              <text class="qa-text">{{ action.text }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 对话消息列表 -->
      <view v-else class="messages">
        <view v-if="isLoadingHistory" class="loading-history">
          <text>加载历史消息...</text>
        </view>

        <view
          v-for="(msg, index) in messages"
          :key="msg.id"
          class="message-item"
          :class="msg.role"
        >
          <!-- 消息头像 -->
          <view class="msg-avatar">
            <LineIcon v-if="msg.role === 'user'" name="user" :size="28" :color="msg.role === 'user' ? '#92400e' : '#166534'" />
            <LineIcon v-else name="message-circle" :size="28" color="#166534" />
          </view>

          <!-- 消息内容 -->
          <view class="msg-content">
            <!-- 发送时间 -->
            <text class="msg-time">{{ formatTime(msg.createdAt) }}</text>

            <!-- 消息文本 -->
            <view class="msg-bubble">
              <text class="msg-text" :decode="true">{{ msg.content }}</text>
            </view>

            <!-- 引用法条 -->
            <view v-if="msg.lawReferences && msg.lawReferences.length > 0" class="law-refs">
              <text class="ref-title">📜 相关法规</text>
              <view
                v-for="ref in msg.lawReferences"
                :key="ref.id"
                class="ref-item"
                @click="viewLawDetail(ref)"
              >
                <text class="ref-name">{{ ref.title }}</text>
                <LineIcon name="chevron-right" :size="20" color="#22C55E" />
              </view>
            </view>

            <!-- 关联案例 -->
            <view v-if="msg.caseReferences && msg.caseReferences.length > 0" class="case-refs">
              <text class="ref-title">📋 相关案例</text>
              <view
                v-for="c in msg.caseReferences"
                :key="c.id"
                class="ref-item case-item"
                @click="viewCaseDetail(c)"
              >
                <text class="ref-name">{{ c.title }}</text>
                <LineIcon name="chevron-right" :size="20" color="#22C55E" />
              </view>
            </view>

            <!-- 关联视频 -->
            <view v-if="msg.videoReferences && msg.videoReferences.length > 0" class="video-refs">
              <text class="ref-title">📹 相关视频</text>
              <view
                v-for="v in msg.videoReferences"
                :key="v.id"
                class="ref-item video-item"
                @click="viewVideo(v)"
              >
                <LineIcon name="play" :size="20" color="#22C55E" />
                <text class="ref-name">{{ v.title }}</text>
                <text class="ref-duration">{{ v.duration }}</text>
              </view>
            </view>

            <!-- 操作按钮 -->
            <view class="msg-actions">
              <view class="action-btn" @click="copyMessage(msg)">
                <LineIcon name="clipboard-list" :size="18" color="#4B5563" />
                <text>复制</text>
              </view>
              <view class="action-btn" @click="shareMessage(msg)">
                <LineIcon name="external-link" :size="18" color="#4B5563" />
                <text>分享</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 正在输入指示器 -->
        <view v-if="isTyping" class="message-item assistant typing-indicator">
          <view class="msg-avatar">
            <LineIcon name="message-circle" :size="28" color="#166534" />
          </view>
          <view class="msg-content">
            <view class="typing-dots">
              <view class="dot"></view>
              <view class="dot"></view>
              <view class="dot"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 追问建议 -->
      <view v-if="suggestions.length > 0 && !isTyping" class="suggestions">
        <text class="suggestions-title">您可能想继续问：</text>
        <view class="suggestions-list">
          <view
            v-for="(suggestion, index) in suggestions"
            :key="index"
            class="suggestion-item"
            @click="useSuggestion(suggestion)"
          >
            <text class="suggestion-text">{{ suggestion }}</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 对话工具栏 -->
    <view v-if="messages.length > 0" class="conversation-toolbar">
      <view class="toolbar-btn" @click="newConversation">
        <LineIcon name="plus" :size="24" color="#4B5563" />
        <text class="toolbar-text">新建对话</text>
      </view>
      <view class="toolbar-btn" @click="goHistory">
        <LineIcon name="clock" :size="24" color="#4B5563" />
        <text class="toolbar-text">历史记录</text>
      </view>
    </view>

    <!-- 输入区域 -->
    <view class="input-area">
      <!-- 功能工具栏 -->
      <view class="tool-bar">
        <view class="tool-btn" @click="attachImage">
          <LineIcon name="image" :size="28" color="#4B5563" />
        </view>
        <view class="tool-btn" @click="attachLaw">
          <LineIcon name="file-text" :size="28" color="#4B5563" />
        </view>
        <view class="tool-btn" @click="goHistory">
          <LineIcon name="clipboard-list" :size="28" color="#4B5563" />
        </view>
      </view>
      
      <view class="input-row">
        <view class="input-wrapper">
          <textarea
            v-model="inputText"
            class="input-field"
            placeholder="请输入法律问题..."
            :maxlength="500"
            :cursor-spacing="20"
            :adjust-position="true"
            @focus="onInputFocus"
            @blur="onInputBlur"
            @input="updateCanSend"
          />
        </view>
        <view
          class="send-btn"
          :class="{ active: canSend }"
          @click="sendMessage"
        >
          <LineIcon name="arrow-right" :size="32" :color="canSend ? '#ffffff' : '#9CA3AF'" />
        </view>
      </view>
    </view>

    <!-- 历史记录弹窗 -->
    <view v-if="showHistory" class="history-popup" @click="showHistory = false">
      <view class="history-card" @click.stop>
        <view class="history-header">
          <text class="history-title">咨询历史</text>
          <view class="history-close" @click="showHistory = false">
            <LineIcon name="x-circle" :size="32" color="#9CA3AF" />
          </view>
        </view>
        <scroll-view class="history-list" scroll-y>
          <view v-if="historyList.length === 0" class="history-empty">
            <text>暂无历史记录</text>
          </view>
          <view 
            v-else
            v-for="item in historyList"
            :key="item.id"
            class="history-item"
            @click="loadHistory(item)"
          >
            <text class="history-q">{{ item.question }}</text>
            <text class="history-date">{{ formatDate(item.createdAt) }}</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { api } from '../../api/index.js'
import { userStore } from '../../store/user.js'
import LineIcon from '../../components/LineIcon.vue'

// 状态
const messages = ref([])
const inputText = ref('')
const isTyping = ref(false)
const isLoadingHistory = ref(false)
const scrollTop = ref(0)
const chatMode = ref('law')
const showHistory = ref(false)
const historyList = ref([])
const currentConversationId = ref('')
const suggestions = ref([])
const canSend = ref(false)

// 获取用户ID
function getUserId() {
  return userStore.userId || uni.getStorageSync('lvfat_user')?.id || 'anonymous'
}

// 常见问题
const faqList = [
  { id: '1', question: '企业违规倾倒废塑料怎么处理？', prompt: '企业违规倾倒废塑料应该如何处理？' },
  { id: '2', question: '农膜回收有哪些政策要求？', prompt: '农用薄膜回收有哪些政策要求？' },
  { id: '3', question: '发现塑料污染如何举报？', prompt: '发现塑料污染违法行为应该如何举报？' },
  { id: '4', question: '一次性塑料制品限制规定', prompt: '一次性塑料制品的限制规定有哪些？' }
]

// 快捷功能
const quickActions = [
  { id: '1', iconName: 'file-text', iconBg: 'linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%)', iconColor: '#22C55E', text: '法规解读', prompt: '请解读《固体废物污染环境防治法》中关于塑料污染的条款' },
  { id: '2', iconName: 'shield-check', iconBg: 'linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%)', iconColor: '#3B82F6', text: '企业合规', prompt: '塑料制品生产企业需要遵守哪些环保法规？' },
  { id: '3', iconName: 'map-pin', iconBg: 'linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%)', iconColor: '#EF4444', text: '举报指引', prompt: '发现违规倾倒塑料垃圾应该如何举报？' },
  { id: '4', iconName: 'leaf', iconBg: 'linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%)', iconColor: '#F59E0B', text: '农膜回收', prompt: '农业薄膜回收有哪些政策要求？' }
]

function updateCanSend() {
  canSend.value = inputText.value.trim().length > 0
}

// 发送消息
async function sendMessage() {
  if (!canSend.value) return

  const text = inputText.value.trim()
  inputText.value = ''
  isTyping.value = true
  canSend.value = false

  const userMsg = {
    id: Date.now().toString(),
    role: 'user',
    content: text,
    createdAt: new Date().toISOString()
  }
  messages.value.push(userMsg)
  scrollToBottom()

  try {
    const res = await api.ai.chat({
      question: text,
      conversationId: currentConversationId.value,
      userId: getUserId(),
      role: 'common'
    })
    isTyping.value = false

    // 保存会话ID
    if (res?.conversationId) {
      currentConversationId.value = res.conversationId
    }

    const aiMsg = {
      id: res?.messageId || (Date.now() + 1).toString(),
      role: 'assistant',
      content: res?.answer || '抱歉，我暂时无法回答这个问题，请稍后再试。',
      lawReferences: formatLawReferences(res?.relatedLaws) || [],
      caseReferences: [],
      videoReferences: [],
      suggestions: res?.suggestions || [],
      createdAt: new Date().toISOString()
    }
    messages.value.push(aiMsg)

    // 更新追问建议
    suggestions.value = res?.suggestions || []

  } catch (error) {
    isTyping.value = false
    const aiMsg = {
      id: (Date.now() + 1).toString(),
      role: 'assistant',
      content: `根据《固体废物污染环境防治法》相关规定：

一、关于一次性塑料制品
禁止生产、销售超薄塑料购物袋、一次性塑料餐具等。国家鼓励回收和再利用。

二、企业合规要求
1. 建立固体废物管理台账
2. 依法进行环境影响评价
3. 配合生态环境主管部门的监督检查

三、处罚规定
违反规定的，由县级以上地方人民政府生态环境主管部门责令改正，并处罚款。

如需了解更多详情，可以点击相关法规链接查看全文。`,
      lawReferences: [
        { id: '1', title: '《固体废物污染环境防治法》第六十七条' },
        { id: '2', title: '《塑料污染治理专项行动方案》' }
      ],
      suggestions: ['相关法规具体有哪些？', '违规会有什么处罚？', '如何进行合规整改？'],
      createdAt: new Date().toISOString()
    }
    messages.value.push(aiMsg)
    suggestions.value = aiMsg.suggestions
  }

  nextTick(() => {
    scrollToBottom()
  })
}

function formatLawReferences(laws) {
  if (!laws) return []
  if (typeof laws === 'string') {
    try { laws = JSON.parse(laws) } catch (_) { return [] }
  }
  if (!Array.isArray(laws)) return []
  return laws.map(law => ({
    id: law.id || String(Math.random()),
    title: law.title || '',
    relevance: law.relevance || 0.8
  }))
}

// ========== 辅助 ==========
function useQuickAction(action) {
  inputText.value = action.prompt || action.question
  updateCanSend()
  sendMessage()
}

// 使用追问建议
function useSuggestion(suggestion) {
  inputText.value = suggestion
  updateCanSend()
  sendMessage()
}

// 历史记录
function goHistory() {
  loadHistoryList()
  showHistory.value = true
}

async function loadHistoryList() {
  try {
    const res = await api.ai.conversations.list({
      userId: getUserId(),
      sessionType: 'law',
      page: 1,
      pageSize: 20
    })
    if (res) {
      historyList.value = Array.isArray(res) ? res : (res.list || [])
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
    historyList.value = []
  }
}

async function loadHistory(item) {
  try {
    // 加载完整对话
    const res = await api.ai.conversations.get(item.id)
    if (res) {
      const convData = res.conversation || {}
      const msgList = res.messages || []

      messages.value = msgList.map(msg => ({
        id: msg.id,
        role: msg.role,
        content: msg.content,
        lawReferences: msg.lawReferences ? JSON.parse(msg.lawReferences) : [],
        createdAt: msg.createTime
      }))

      currentConversationId.value = item.id

      // 从消息中提取追问建议
      if (msgList.length > 0) {
        const lastAssistantMsg = msgList.filter(m => m.role === 'assistant').pop()
        if (lastAssistantMsg && lastAssistantMsg.content) {
          suggestions.value = generateSuggestionsFromContent(lastAssistantMsg.content)
        }
      }
    }
  } catch (error) {
    console.error('加载对话详情失败:', error)
    // 降级处理
    messages.value = [{
      id: item.id,
      role: 'assistant',
      content: item.title || '历史对话',
      lawReferences: [],
      createdAt: item.createTime
    }]
    currentConversationId.value = item.id
  }

  showHistory.value = false
  nextTick(() => {
    scrollToBottom()
  })
}

// 根据内容生成追问建议
function generateSuggestionsFromContent(content) {
  const defaultSuggestions = [
    '相关法规具体有哪些？',
    '违规会有什么处罚？',
    '如何进行合规整改？'
  ]

  if (content.includes('处罚') || content.includes('罚款')) {
    return [
      '处罚标准具体是多少？',
      '初次违规会怎么处理？',
      '如何避免被处罚？',
      ...defaultSuggestions.slice(0, 1)
    ]
  }

  if (content.includes('企业') || content.includes('生产')) {
    return [
      '企业需要办理什么手续？',
      '合规整改从哪里开始？',
      '需要多长时间完成整改？',
      ...defaultSuggestions.slice(0, 1)
    ]
  }

  return defaultSuggestions
}

async function loadMoreHistory() {
  // TODO: 实现历史消息加载
}

// 新建对话
function newConversation() {
  messages.value = []
  currentConversationId.value = ''
  suggestions.value = []
  showHistory.value = false
}

// 删除对话
async function deleteHistory(item, event) {
  event.stopPropagation()

  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条对话记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await api.ai.conversations.delete(item.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          // 从列表中移除
          const index = historyList.value.findIndex(h => h.id === item.id)
          if (index > -1) {
            historyList.value.splice(index, 1)
          }
          // 如果删除的是当前对话，清空
          if (currentConversationId.value === item.id) {
            newConversation()
          }
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

// 模式切换
function toggleMode() {
  chatMode.value = chatMode.value === 'law' ? 'web' : 'law'
  uni.showToast({ title: `已切换到${chatMode.value === 'law' ? '法律模式' : '联网模式'}`, icon: 'none' })
}

// 复制消息
function copyMessage(msg) {
  uni.setClipboardData({
    data: msg.content,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'success' })
    }
  })
}

// 分享消息
function shareMessage(msg) {
  uni.showToast({ title: '分享功能待开发', icon: 'none' })
}

// 附件功能
function attachImage() {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      uni.showToast({ title: '图片功能待开发', icon: 'none' })
    }
  })
}

function attachLaw() {
  uni.showToast({ title: '关联法规功能待开发', icon: 'none' })
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    scrollTop.value = scrollTop.value + 1
    setTimeout(() => { scrollTop.value = 0 }, 10)
  })
}

// 查看法规详情
function viewLawDetail(law) {
  uni.navigateTo({
    url: `/pages/law-search/law-search?id=${law.id}`
  })
}

// 查看案例详情
function viewCaseDetail(caseItem) {
  uni.showToast({ title: '案例详情待开发', icon: 'none' })
}

// 查看视频
function viewVideo(video) {
  uni.showToast({ title: '视频播放待开发', icon: 'none' })
}

// 输入框聚焦/失焦
function onInputFocus() {
  setTimeout(() => {
    scrollToBottom()
  }, 300)
}

function onInputBlur() {}

// 格式化时间
function formatTime(timeStr) {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${date.getMonth() + 1}-${date.getDate()}`
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style lang="scss" scoped>
// 配色方案：白色为主 + 深灰 + 浅绿/嫩黄渐变
$primary: #22C55E;           // 主绿色
$primary-light: #86EFAC;      // 浅绿
$primary-dark: #166534;       // 深绿
$accent-yellow: #FDE047;      // 嫩黄
$accent-yellow-light: #FEF9C3; // 浅嫩黄
$text-primary: #1F2937;      // 深灰主文字
$text-secondary: #4B5563;    // 中灰
$text-muted: #9CA3AF;        // 浅灰
$bg-white: #FFFFFF;          // 纯白
$bg-light: #FAFAFA;         // 浅灰白
$bg-cream: #FEFDFB;          // 米白
$border-color: #E5E7EB;      // 边框灰
$shadow-sm: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
$shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);

// 白到绿渐变
$gradient-green: linear-gradient(135deg, #ffffff 0%, #f0fdf4 50%, #dcfce7 100%);
$gradient-green-light: linear-gradient(180deg, #ffffff 0%, #f0fdf4 100%);
$gradient-yellow: linear-gradient(135deg, #FEFCE8 0%, #FDE047 100%);

.page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: $bg-cream;
}

// 顶部标题栏
.header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx;
  background: $gradient-green-light;
  border-bottom: 1rpx solid $border-color;
  position: relative;
  flex-shrink: 0;
}

.header-title {
  font-size: 34rpx;
  font-weight: 600;
  color: $text-primary;
}

.header-dot {
  position: absolute;
  right: 32rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: $text-muted;
}

.header-dot.active {
  background: $primary;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

// 对话区域
.chat-area {
  flex: 1;
  min-height: 0;
  height: 0;
  padding: 24rpx;
  box-sizing: border-box;
  background: $bg-cream;
}

// 欢迎页
.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 40rpx;
}

.welcome-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: $gradient-green;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(34, 197, 94, 0.2);
}

.avatar-icon {
  font-size: 60rpx;
}

.welcome-card {
  background: $bg-white;
  border-radius: 24rpx;
  padding: 32rpx;
  max-width: 600rpx;
  text-align: center;
  box-shadow: $shadow-md;
  margin-bottom: 40rpx;
  border: 1rpx solid rgba(34, 197, 94, 0.1);
}

.welcome-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 12rpx;
}

.welcome-desc {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.6;
}

// 快捷功能
.quick-actions {
  width: 100%;
}

.qa-title {
  font-size: 28rpx;
  color: $text-muted;
  display: block;
  margin-bottom: 20rpx;
  text-align: center;
}

.qa-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.qa-item {
  background: $bg-white;
  border-radius: 20rpx;
  padding: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  box-shadow: $shadow-sm;
  border: 1rpx solid $border-color;
  transition: all 0.2s ease;
}

.qa-item:active {
  transform: scale(0.98);
  box-shadow: $shadow-md;
}

.qa-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.qa-text {
  font-size: 26rpx;
  color: $text-primary;
  font-weight: 500;
}

// 消息列表
.messages {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.loading-history {
  text-align: center;
  color: $text-muted;
  font-size: 24rpx;
  padding: 20rpx;
}

.message-item {
  display: flex;
  gap: 16rpx;
}

.message-item.user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: $gradient-green;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(34, 197, 94, 0.15);
}

.message-item.user .msg-avatar {
  background: $gradient-yellow;
  box-shadow: 0 2rpx 8rpx rgba(253, 224, 71, 0.3);
}

.msg-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.msg-time {
  font-size: 20rpx;
  color: $text-muted;
  padding: 0 12rpx;
}

.message-item.user .msg-time {
  text-align: right;
}

.msg-bubble {
  background: $bg-white;
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  box-shadow: $shadow-sm;
  border: 1rpx solid rgba(34, 197, 94, 0.08);
}

.message-item.user .msg-bubble {
  background: linear-gradient(135deg, $primary 0%, #16a34a 100%);
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(34, 197, 94, 0.25);
}

.msg-text {
  font-size: 28rpx;
  color: $text-primary;
  line-height: 1.6;
  white-space: pre-wrap;
}

.message-item.user .msg-text {
  color: #ffffff;
}

// 法规引用
.law-refs {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 16rpx;
  padding: 16rpx;
  margin-top: 12rpx;
  border: 1rpx solid rgba(34, 197, 94, 0.15);
}

.ref-title {
  font-size: 22rpx;
  color: $primary-dark;
  font-weight: 600;
  display: block;
  margin-bottom: 12rpx;
}

.ref-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 12rpx;
  background: $bg-white;
  border-radius: 10rpx;
  margin-bottom: 8rpx;
  border: 1rpx solid rgba(34, 197, 94, 0.1);
}

.ref-item:last-child {
  margin-bottom: 0;
}

.ref-name {
  font-size: 24rpx;
  color: $text-primary;
}

.ref-arrow {
  font-size: 28rpx;
  color: $primary;
}

// 正在输入
.typing-dots {
  display: flex;
  gap: 8rpx;
  padding: 20rpx 24rpx;
  background: $bg-white;
  border-radius: 20rpx;
  box-shadow: $shadow-sm;
  border: 1rpx solid rgba(34, 197, 94, 0.08);
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: $primary-light;
  animation: typing 1.4s infinite;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8rpx); }
}

// 输入区域
.input-area {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  gap: 0;
  padding: 12rpx 24rpx 16rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: $gradient-green-light;
  border-top: 1rpx solid $border-color;
}

.input-wrapper {
  flex: 1;
  display: flex;
  align-items: flex-end;
  gap: 12rpx;
  background: $bg-white;
  border-radius: 40rpx;
  padding: 12rpx 20rpx;
  border: 1rpx solid $border-color;
}

.input-field {
  flex: 1;
  font-size: 28rpx;
  max-height: 120rpx;
  line-height: 1.5;
  background: transparent;
}

.input-tools {
  display: flex;
  gap: 8rpx;
}

.tool-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: $bg-white;
  border: 1rpx solid $border-color;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.send-btn.active {
  background: linear-gradient(135deg, $primary 0%, #16a34a 100%);
  border: none;
  box-shadow: 0 4rpx 16rpx rgba(34, 197, 94, 0.3);
}

.send-icon {
  font-size: 36rpx;
  color: $text-muted;
  font-weight: bold;
}

.send-btn.active .send-icon {
  color: #ffffff;
}

// 模式选择弹窗
.mode-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 100;
}

.mode-card {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 32rpx;
  width: 100%;
}

.mode-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  text-align: center;
  margin-bottom: 24rpx;
}

.mode-options {
  display: flex;
  gap: 20rpx;
}

.mode-option {
  flex: 1;
  background: #f5f6f8;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  border: 4rpx solid transparent;
}

.mode-option.selected {
  border-color: $primary;
  background: #f0f7f2;
}

.mode-option .mode-icon {
  font-size: 48rpx;
}

.mode-option .mode-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.mode-option .mode-desc {
  font-size: 22rpx;
  color: #999;
}

// 新增样式
.header-actions {
  position: absolute;
  right: 24rpx;
  display: flex;
  gap: 16rpx;
}

.header-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: $bg-white;
  border-radius: 20rpx;
  font-size: 24rpx;
  border: 1rpx solid $border-color;
}

.btn-icon {
  font-size: 24rpx;
}

.btn-text {
  font-size: 24rpx;
  color: $text-secondary;
}

.mode-indicator {
  position: absolute;
  left: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
  font-size: 20rpx;
  font-weight: 500;
}

.mode-indicator.law {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: $primary-dark;
  border: 1rpx solid rgba(34, 197, 94, 0.2);
}

.mode-indicator.web {
  background: linear-gradient(135deg, #FEFCE8 0%, #FDE68A 100%);
  color: #92400e;
  border: 1rpx solid rgba(253, 230, 138, 0.5);
}

// 常见问题
.faq-section {
  width: 100%;
  margin-bottom: 32rpx;
}

.faq-title {
  font-size: 28rpx;
  color: $text-muted;
  display: block;
  margin-bottom: 16rpx;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.faq-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: $bg-white;
  border-radius: 16rpx;
  box-shadow: $shadow-sm;
  border: 1rpx solid $border-color;
  transition: all 0.2s ease;
}

.faq-item:active {
  transform: scale(0.98);
}

.faq-q {
  font-size: 26rpx;
  color: $text-primary;
  flex: 1;
}

.faq-arrow {
  font-size: 28rpx;
  color: $primary;
}

// 消息操作按钮
.msg-actions {
  display: flex;
  gap: 12rpx;
  margin-top: 12rpx;
}

.msg-actions .action-btn {
  padding: 8rpx 16rpx;
  background: $bg-light;
  border-radius: 10rpx;
  font-size: 22rpx;
  color: $text-secondary;
  border: 1rpx solid $border-color;
}

// 案例和视频引用
.case-refs,
.video-refs {
  margin-top: 12rpx;
}

.video-ref-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.ref-icon {
  font-size: 24rpx;
}

.ref-duration {
  font-size: 22rpx;
  color: #888;
}

// 工具栏
.tool-bar {
  display: flex;
  gap: 16rpx;
  padding: 0 0 12rpx;
  background: transparent;
}

.tool-btn {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-white;
  border-radius: 50%;
  font-size: 28rpx;
  border: 1rpx solid $border-color;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 16rpx;
  width: 100%;
}

// 历史记录弹窗
.history-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 100;
}

.history-card {
  background: $bg-white;
  border-radius: 24rpx 24rpx 0 0;
  padding: 0;
  width: 100%;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid $border-color;
}

.history-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-primary;
}

.history-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: $text-muted;
}

.history-list {
  flex: 1;
  padding: 16rpx 24rpx;
  max-height: 50vh;
}

.history-empty {
  text-align: center;
  padding: 60rpx 0;
  color: $text-muted;
  font-size: 28rpx;
}

.history-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid $border-color;
}

.history-q {
  font-size: 26rpx;
  color: $text-primary;
  display: block;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-date {
  font-size: 22rpx;
  color: $text-muted;
}

// 追问建议
.suggestions {
  padding: 16rpx 0 24rpx;
  margin-top: 16rpx;
  border-top: 1rpx dashed $border-color;
}

.suggestions-title {
  font-size: 24rpx;
  color: $text-muted;
  display: block;
  margin-bottom: 16rpx;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.suggestion-item {
  padding: 12rpx 20rpx;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 24rpx;
  border: 1rpx solid rgba(34, 197, 94, 0.2);
  max-width: 90%;
}

.suggestion-text {
  font-size: 24rpx;
  color: $primary-dark;
  line-height: 1.4;
}

// 对话工具栏
.conversation-toolbar {
  display: flex;
  justify-content: center;
  gap: 40rpx;
  padding: 12rpx 24rpx;
  background: $bg-white;
  border-top: 1rpx solid $border-color;
  flex-shrink: 0;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
}

.toolbar-icon {
  font-size: 28rpx;
}

.toolbar-text {
  font-size: 24rpx;
  color: $text-secondary;
}
</style>
