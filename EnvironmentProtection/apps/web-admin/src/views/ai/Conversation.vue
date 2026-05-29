<template>
  <div class="ai-conversation-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">AI 对话管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalConversations || 0 }}</div>
              <div class="stat-label">总对话数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon law">
              <el-icon><ScaleToOriginal /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.lawConversations || 0 }}</div>
              <div class="stat-label">法律问答</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon messages">
              <el-icon><ChatLineRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalMessages || 0 }}</div>
              <div class="stat-label">消息总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon tokens">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(statistics.totalTokens || 0) }}</div>
              <div class="stat-label">Token消耗</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="会话类型">
          <el-select v-model="filterForm.sessionType" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="法律问答" value="law" />
            <el-option label="素材生成" value="material" />
            <el-option label="案例分析" value="case" />
            <el-option label="智能组卷" value="exam" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="filterForm.userId" placeholder="请输入用户ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 对话列表 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">对话记录</span>
        </div>
      </template>

      <el-table :data="conversationList" v-loading="loading" stripe>
        <el-table-column prop="id" label="会话ID" width="200" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="sessionType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.sessionType)">
              {{ getTypeLabel(row.sessionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="会话标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="messageCount" label="消息数" width="80" align="center" />
        <el-table-column prop="lastMessageTime" label="最后消息" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.lastMessageTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '进行中' : '已归档' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
    </el-card>

    <!-- 对话详情抽屉 -->
    <el-drawer v-model="detailDrawer" title="对话详情" size="60%" direction="rtl">
      <div class="conversation-detail" v-if="currentConversation">
        <div class="detail-header">
          <div class="detail-info">
            <el-tag :type="getTypeTagType(currentConversation.sessionType)">
              {{ getTypeLabel(currentConversation.sessionType) }}
            </el-tag>
            <span class="detail-id">ID: {{ currentConversation.id }}</span>
          </div>
          <div class="detail-meta">
            <span>用户ID: {{ currentConversation.userId }}</span>
            <span>消息数: {{ currentConversation.messageCount }}</span>
            <span>创建时间: {{ formatDateTime(currentConversation.createTime) }}</span>
          </div>
        </div>

        <div class="messages-container">
          <div
            v-for="msg in currentMessages"
            :key="msg.id"
            class="message-item"
            :class="msg.role"
          >
            <div class="message-avatar">
              <el-avatar :size="36" :icon="msg.role === 'user' ? 'User' : 'ChatDotSquare'" />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-role">{{ msg.role === 'user' ? '用户' : 'AI助手' }}</span>
                <span class="message-time">{{ formatDateTime(msg.createTime) }}</span>
              </div>
              <div class="message-bubble">
                <pre class="message-text">{{ msg.content }}</pre>
              </div>
              <div v-if="msg.lawReferences" class="message-references">
                <div class="ref-title">
                  <el-icon><Document /></el-icon>
                  引用法规
                </div>
                <div class="ref-list">
                  <div
                    v-for="ref in JSON.parse(msg.lawReferences)"
                    :key="ref.id"
                    class="ref-item"
                  >
                    <el-icon><Link /></el-icon>
                    {{ ref.title }}
                  </div>
                </div>
              </div>
              <div v-if="msg.role === 'assistant'" class="message-feedback">
                <el-button
                  :type="msg.feedback === 'like' ? 'success' : 'default'"
                  size="small"
                  @click="handleFeedback(msg.id, 'like')"
                >
                  <el-icon><Select /></el-icon>
                  满意
                </el-button>
                <el-button
                  :type="msg.feedback === 'dislike' ? 'danger' : 'default'"
                  size="small"
                  @click="handleFeedback(msg.id, 'dislike')"
                >
                  <el-icon><CloseBold /></el-icon>
                  不满意
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Search,
  RefreshLeft,
  View,
  Delete,
  ChatDotRound,
  ChatLineRound,
  ChatDotSquare,
  ScaleToOriginal,
  Coin,
  Document,
  Link,
  Select,
  CloseBold
} from '@element-plus/icons-vue'
import { getAIConversationList, deleteAIConversation, getAIConversationDetail, feedbackAIMessage } from '@/api/ai.js'

const loading = ref(false)
const conversationList = ref([])
const detailDrawer = ref(false)
const currentConversation = ref(null)
const currentMessages = ref([])

// 筛选表单
const filterForm = reactive({
  sessionType: '',
  userId: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 统计数据
const statistics = ref({
  totalConversations: 0,
  lawConversations: 0,
  totalMessages: 0,
  totalTokens: 0
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filterForm.sessionType) {
      params.sessionType = filterForm.sessionType
    }
    if (filterForm.userId) {
      params.userId = filterForm.userId
    }

    const res = await getAIConversationList(params)
    conversationList.value = Array.isArray(res) ? res : (res?.list || [])

    // 计算统计数据
    statistics.value.totalConversations = conversationList.value.length
    statistics.value.lawConversations = conversationList.value.filter(c => c.sessionType === 'law').length
    statistics.value.totalMessages = conversationList.value.reduce((sum, c) => sum + (c.messageCount || 0), 0)
    statistics.value.totalTokens = 0

    ElMessage.success('加载成功')
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 筛选
function handleFilter() {
  pagination.page = 1
  loadData()
}

// 重置
function handleReset() {
  filterForm.sessionType = ''
  filterForm.userId = ''
  filterForm.dateRange = []
  pagination.page = 1
  loadData()
}

// 分页
function handleSizeChange(val) {
  pagination.pageSize = val
  loadData()
}

function handlePageChange(val) {
  pagination.page = val
  loadData()
}

// 查看详情
async function viewDetail(row) {
  try {
    const res = await getAIConversationDetail(row.id)
    currentConversation.value = res?.conversation || row
    currentMessages.value = res?.messages || []
    detailDrawer.value = true
  } catch (error) {
    console.error('加载详情失败:', error)
    ElMessage.error('加载详情失败')
  }
}

// 删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这条对话记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteAIConversation(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 反馈
async function handleFeedback(messageId, feedback) {
  try {
    await feedbackAIMessage(messageId, feedback, '')
    ElMessage.success('反馈成功')

    // 更新本地状态
    const msg = currentMessages.value.find(m => m.id === messageId)
    if (msg) {
      msg.feedback = feedback
    }
  } catch (error) {
    console.error('反馈失败:', error)
    ElMessage.error('反馈失败')
  }
}

// 类型标签
function getTypeTagType(type) {
  const types = {
    law: '',
    material: 'warning',
    case: 'success',
    exam: 'info'
  }
  return types[type] || ''
}

function getTypeLabel(type) {
  const labels = {
    law: '法律问答',
    material: '素材生成',
    case: '案例分析',
    exam: '智能组卷'
  }
  return labels[type] || type
}

// 格式化
function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.ai-conversation-container {
  padding: 20px;
  background: #ffffff;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .page-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #fff;

          &.total { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
          &.law { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
          &.messages { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); }
          &.tokens { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        }

        .stat-info {
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .filter-card {
    margin-bottom: 20px;
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        font-weight: 600;
      }
    }

    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .conversation-detail {
    .detail-header {
      padding-bottom: 16px;
      border-bottom: 1px solid #eee;
      margin-bottom: 16px;

      .detail-info {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;

        .detail-id {
          color: #909399;
          font-size: 12px;
        }
      }

      .detail-meta {
        display: flex;
        gap: 24px;
        color: #606266;
        font-size: 13px;
      }
    }

    .messages-container {
      max-height: calc(100vh - 300px);
      overflow-y: auto;
      padding: 16px 0;

      .message-item {
        display: flex;
        gap: 12px;
        margin-bottom: 20px;

        &.user {
          flex-direction: row-reverse;

          .message-bubble {
            background: #28a745;
            color: #fff;

            .message-text {
              color: #fff;
            }
          }
        }

        .message-content {
          flex: 1;
          max-width: 80%;

          .message-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 4px;

            .message-role {
              font-weight: 600;
              color: #303133;
            }

            .message-time {
              font-size: 12px;
              color: #909399;
            }
          }

          .message-bubble {
            background: #f5f7fa;
            border-radius: 8px;
            padding: 12px 16px;

            .message-text {
              margin: 0;
              white-space: pre-wrap;
              word-break: break-word;
              font-family: inherit;
              line-height: 1.6;
            }
          }

          .message-references {
            margin-top: 8px;
            padding: 8px 12px;
            background: #f0f9f4;
            border-radius: 6px;

            .ref-title {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
              color: #28a745;
              margin-bottom: 6px;
            }

            .ref-list {
              display: flex;
              flex-direction: column;
              gap: 4px;

              .ref-item {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: #606266;
              }
            }
          }

          .message-feedback {
            margin-top: 8px;
            display: flex;
            gap: 8px;
          }
        }
      }
    }
  }
}
</style>
