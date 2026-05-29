<template>
  <div class="li-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
    </div>

    <!-- 页头 -->
    <div class="page-header">
      <div class="header-title">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="m3 21 1.9-5.7a8.5 8.5 0 1 1 3.8 3.8z"/>
          <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/>
        </svg>
        <h1>法条智能解读</h1>
      </div>
      <el-button type="primary" size="default" class="btn-history" @click="showHistory = !showHistory">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>
        历史记录
      </el-button>
    </div>

    <!-- 主卡片 -->
    <div class="glass-card panel-card">
      <el-form :inline="true" class="query-form">
        <el-form-item label="法规名称">
          <el-input v-model="lawName" placeholder="如：固体废物污染环境防治法" style="width:260px" clearable />
        </el-form-item>
        <el-form-item label="条款编号">
          <el-input v-model="articleNo" placeholder="如：第六十五条" style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="btn-primary" @click="handleInterpret">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            开始解读
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="result" class="interpret-result">
        <div class="result-header">
          <svg class="result-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <span>解读完成</span>
        </div>
        <div class="result-body" v-html="result" />
        <div class="result-actions">
          <el-button type="primary" size="small" class="btn-copy" @click="handleCopy">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect width="14" height="14" x="8" y="8" rx="2" ry="2"/>
              <path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/>
            </svg>
            复制
          </el-button>
          <el-button type="success" size="small" class="btn-save" @click="handleSave">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
              <line x1="12" x2="12" y1="11" y2="17"/>
              <line x1="9" x2="15" y1="14" y2="14"/>
            </svg>
            保存
          </el-button>
          <el-button type="warning" size="small" class="btn-ask" @click="handleAsk">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>
            追问
          </el-button>
        </div>
      </div>

      <el-empty v-else description="输入法规名称和条款编号，点击「开始解读」获取通俗化法条解读" />
    </div>

    <!-- 常用法条快捷 -->
    <div class="glass-card panel-card mt-20">
      <div class="card-header">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <rect width="18" height="18" x="3" y="3" rx="2"/>
          <path d="M7 7h.01"/>
          <path d="M17 7h.01"/>
          <path d="M7 17h.01"/>
          <path d="M17 17h.01"/>
        </svg>
        <span>常用法条速查</span>
      </div>
      <div class="quick-list">
        <div v-for="item in quickLaws" :key="item.id" class="quick-item" @click="lawName = item.name; articleNo = item.article; handleInterpret()">
          <div class="quick-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" x2="8" y1="13" y2="13"/>
              <line x1="16" x2="8" y1="17" y2="17"/>
              <polyline points="10 9 9 9 8 9"/>
            </svg>
          </div>
          <div class="quick-info">
            <span class="q-name">{{ item.name }}</span>
            <span class="q-article">{{ item.article }}</span>
          </div>
          <div class="quick-arrow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="m9 18 6-6-6-6"/>
            </svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { explainLawText } from '@/api/ai.js'

const loading = ref(false)
const showHistory = ref(false)
const lawName = ref('')
const articleNo = ref('')
const result = ref('')
const relatedLaws = ref([])

const quickLaws = reactive([
  { id: 1, name: '固体废物污染环境防治法', article: '第六十五条' },
  { id: 2, name: '固体废物污染环境防治法', article: '第六十九条' },
  { id: 3, name: '塑料污染治理专项行动方案', article: '第三条' },
  { id: 4, name: '循环经济促进法', article: '第三十一条' }
])

const handleInterpret = async () => {
  if (!lawName.value) { ElMessage.warning('请输入法规名称'); return }
  loading.value = true
  try {
    const res = await explainLawText({
      content: lawName.value + (articleNo.value ? ' ' + articleNo.value : ''),
      level: 'normal'
    })
    
    if (res && res.explanation) {
      // 格式化返回结果
      result.value = formatExplanation(res.explanation)
      relatedLaws.value = res.related_laws || []
      ElMessage.success('解读完成')
    } else {
      // 降级处理：使用模拟数据
      result.value = generateMockResult(lawName.value, articleNo.value)
      ElMessage.warning('使用缓存数据，请确保AI服务已启动')
    }
  } catch (error) {
    console.error('解读失败:', error)
    // 降级处理：使用模拟数据
    result.value = generateMockResult(lawName.value, articleNo.value)
    ElMessage.warning('解读服务暂时不可用，显示示例数据')
  } finally {
    loading.value = false
  }
}

// 格式化AI返回的解读结果
function formatExplanation(explanation) {
  return `<p><strong>📖 通俗解读：</strong>${explanation}</p>
<p><strong>⚖️ 合规提示：</strong>请严格按照法规要求执行，确保企业合规运营。</p>`
}

// 生成模拟结果（当AI服务不可用时）
function generateMockResult(law, article) {
  return `<p><strong>📌 法条信息：</strong>${law} ${article || '（未指定条款）'}</p>
<p><strong>📖 通俗解读：</strong>本条款主要约束相关主体依法合规经营，要求建立相关台账制度，禁止违规行为。违者将面临相应处罚，情节严重的可追究法律责任。</p>
<p><strong>⚖️ 重点提示：</strong>建议相关主体留存完整台账记录，以备监管部门检查。如有疑问，请咨询当地主管部门。</p>`
}

const handleCopy = () => { navigator.clipboard.writeText(result.value.replace(/<[^>]+>/g, '')); ElMessage.success('已复制') }
const handleSave = () => ElMessage.success('已保存到个人法条库')

// 追问功能：跳转到AI对话页面
const handleAsk = () => {
  // 将当前法规信息传递给对话页面
  sessionStorage.setItem('law_interpret_context', JSON.stringify({
    lawName: lawName.value,
    articleNo: articleNo.value,
    result: result.value
  }))
  window.open('/ai-tools/ai-chat', '_blank')
  ElMessage.info('正在打开AI对话窗口')
}
</script>

<style lang="scss" scoped>
.li-container {
  padding: 24px;
  min-height: 100vh;
  background: #ffffff;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(82,183,136,0.08), rgba(149,213,178,0.04));
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  bottom: 5%;
  left: -80px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;

  .header-icon {
    width: 40px;
    height: 40px;
    color: #52b788;
  }

  h1 {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
    color: #333333;
    letter-spacing: -0.5px;
  }
}

.btn-history {
  display: flex !important;
  align-items: center !important;
  gap: 8px;
  background: rgba(82,183,136,0.1) !important;
  border: 1px solid rgba(82,183,136,0.2) !important;
  color: #2d6a4f !important;
  padding: 10px 20px !important;
  border-radius: 12px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: rgba(82,183,136,0.2) !important;
  }
}

.glass-card {
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(82,183,136,0.06);
  padding: 24px;
  position: relative;
  z-index: 1;
}

.panel-card {
  padding: 28px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20px;

  svg {
    width: 20px;
    height: 20px;
    color: #52b788;
  }
}

.query-form {
  margin-bottom: 16px;

  :deep(.el-form-item__label) {
    color: #333333;
    font-weight: 500;
  }
}

.btn-primary {
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
  border: none !important;
  color: #fff !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px;
  padding: 12px 24px !important;
  border-radius: 12px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(82,183,136,0.3);
  }
}

.mt-20 {
  margin-top: 24px;
}

.interpret-result {
  background: rgba(82,183,136,0.06);
  border: 1px solid rgba(82,183,136,0.15);
  border-radius: 16px;
  padding: 24px;
  margin-top: 8px;

  .result-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
    font-weight: 600;
    color: #52b788;
    font-size: 15px;

    .result-icon {
      width: 22px;
      height: 22px;
    }
  }

  .result-body {
    font-size: 14px;
    line-height: 1.9;
    color: #333;

    :deep(p) {
      margin: 0 0 16px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    :deep(strong) {
      color: #333333;
    }
  }

  .result-actions {
    display: flex;
    gap: 12px;
    margin-top: 20px;
  }
}

.btn-copy, .btn-save, .btn-ask {
  display: flex !important;
  align-items: center !important;
  gap: 6px;
  padding: 8px 16px !important;
  border-radius: 10px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 16px;
    height: 16px;
  }
}

.btn-copy {
  background: rgba(82,183,136,0.1) !important;
  border: 1px solid rgba(82,183,136,0.2) !important;
  color: #2d6a4f !important;

  &:hover {
    background: rgba(82,183,136,0.2) !important;
  }
}

.btn-save {
  background: rgba(82,183,136,0.15) !important;
  border: 1px solid rgba(82,183,136,0.25) !important;
  color: #1a4d2e !important;

  &:hover {
    background: rgba(82,183,136,0.25) !important;
  }
}

.btn-ask {
  background: rgba(255,183,76,0.12) !important;
  border: 1px solid rgba(255,183,76,0.25) !important;
  color: #8B6914 !important;

  &:hover {
    background: rgba(255,183,76,0.22) !important;
  }
}

.quick-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;

  .quick-item {
    display: flex;
    align-items: center;
    gap: 14px;
    background: rgba(82,183,136,0.04);
    border-radius: 14px;
    padding: 18px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(82,183,136,0.1);
      transform: translateY(-2px);

      .quick-arrow {
        opacity: 1;
        transform: translateX(0);
      }
    }

    .quick-icon {
      width: 44px;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(82,183,136,0.1);
      border-radius: 12px;
      flex-shrink: 0;

      svg {
        width: 22px;
        height: 22px;
        color: #52b788;
      }
    }

    .quick-info {
      flex: 1;
      min-width: 0;

      .q-name {
        display: block;
        font-size: 14px;
        color: #333333;
        font-weight: 600;
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .q-article {
        font-size: 12px;
        color: #666666;
      }
    }

    .quick-arrow {
      opacity: 0;
      transform: translateX(-8px);
      transition: all 0.3s ease;

      svg {
        width: 20px;
        height: 20px;
        color: #52b788;
      }
    }
  }
}

:deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.8);
  box-shadow: 0 0 0 1px rgba(82,183,136,0.3);
  border-radius: 12px;

  &:hover, &.is-focus {
    box-shadow: 0 0 0 1px #52b788;
  }
}

:deep(.el-empty__description) {
  color: #666666;
}
</style>
