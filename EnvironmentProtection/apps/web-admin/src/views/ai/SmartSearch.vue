<template>
  <div class="smart-search-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-header">
        <div class="header-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
        </div>
        <h1 class="header-title">智能法律语义检索</h1>
        <p class="header-subtitle">输入问题或关键词，AI 帮您找到相关法规、新闻和讨论</p>
      </div>

      <!-- 搜索框 -->
      <div class="search-box-wrapper">
        <div class="search-box" :class="{ 'is-focused': isFocused, 'is-loading': loading }">
          <!-- 搜索类型选择 -->
          <el-dropdown trigger="click" @visible-change="categoryOpen = $event">
            <div class="search-category">
              <span>{{ currentCategory.label }}</span>
              <svg viewBox="0 0 16 16" fill="none">
                <path d="M4 6l4 4 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="cat in categories"
                  :key="cat.value"
                  @click="selectCategory(cat)"
                >
                  <span :class="{ active: currentCategory.value === cat.value }">{{ cat.label }}</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 输入框 -->
          <div class="search-input-wrapper">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
              <path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <input
              ref="inputRef"
              v-model="searchQuery"
              type="text"
              class="search-input"
              placeholder="输入问题或关键词，如：企业违规倾倒废塑料怎么处理？"
              @focus="isFocused = true"
              @blur="isFocused = false"
              @keydown.enter="handleSearch"
            />
            <div v-if="searchQuery" class="clear-btn" @click="clearSearch">
              <svg viewBox="0 0 16 16" fill="none">
                <path d="M12 4L4 12M4 4l8 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
            </div>
          </div>

          <!-- 搜索按钮 -->
          <button class="search-btn" :class="{ loading }" :disabled="loading" @click="handleSearch">
            <span v-if="!loading">智能检索</span>
            <span v-else class="loading-text">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </span>
          </button>
        </div>

        <!-- 热门标签 -->
        <div class="hot-tags">
          <span class="hot-label">热门搜索：</span>
          <span
            v-for="tag in hotTags"
            :key="tag"
            class="hot-tag"
            @click="quickSearch(tag)"
          >
            {{ tag }}
          </span>
        </div>
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div v-if="searchResult || loading" class="results-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">
          <div class="spinner"></div>
        </div>
        <p class="loading-text">AI 正在检索分析，请稍候...</p>
      </div>

      <!-- 搜索结果 -->
      <div v-else-if="searchResult" class="results-content">
        <!-- 搜索摘要 -->
        <div class="result-summary">
          <div class="summary-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div class="summary-text">
            <h3>检索摘要</h3>
            <p>{{ searchResult.summary || '为您找到相关内容' }}</p>
          </div>
          <div class="summary-stats">
            <div class="stat-item">
              <span class="stat-num">{{ searchResult.relatedLaws?.length || 0 }}</span>
              <span class="stat-label">法规</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ searchResult.relatedNews?.length || 0 }}</span>
              <span class="stat-label">资讯</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ searchResult.relatedComments?.length || 0 }}</span>
              <span class="stat-label">讨论</span>
            </div>
          </div>
        </div>

        <!-- AI 回答 -->
        <div class="result-card ai-answer">
          <div class="card-header">
            <div class="card-icon ai">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
              </svg>
            </div>
            <h3>AI 智能回答</h3>
          </div>
          <div class="card-body">
            <div class="answer-content" v-html="formatAnswer(searchResult.answer)"></div>
          </div>
          <div class="card-actions">
            <button class="action-btn" @click="copyAnswer">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="9" y="9" width="13" height="13" rx="2"/>
                <path d="M5 15H4a2 2 0 01-2-2V4a2 2 0 012-2h9a2 2 0 012 2v1"/>
              </svg>
              复制回答
            </button>
            <button class="action-btn primary" @click="askMore">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
              </svg>
              继续追问
            </button>
          </div>
        </div>

        <!-- 追问建议 -->
        <div v-if="searchResult.suggestions?.length" class="suggestions-bar">
          <span class="suggestions-label">您可能想了解：</span>
          <div class="suggestions-list">
            <span
              v-for="(sug, idx) in searchResult.suggestions"
              :key="idx"
              class="suggestion-item"
              @click="followUpSearch(sug)"
            >
              {{ sug }}
            </span>
          </div>
        </div>

        <!-- 相关法规 -->
        <div v-if="searchResult.relatedLaws?.length" class="result-card">
          <div class="card-header">
            <div class="card-icon law">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
              </svg>
            </div>
            <h3>相关法规</h3>
            <span class="card-count">{{ searchResult.relatedLaws.length }} 部</span>
          </div>
          <div class="card-body">
            <div class="law-list">
              <div
                v-for="law in searchResult.relatedLaws"
                :key="law.id"
                class="law-item"
                @click="viewLawDetail(law)"
              >
                <div class="law-info">
                  <h4 class="law-title">{{ law.title }}</h4>
                  <div class="law-meta">
                    <el-tag size="small" :type="getLevelType(law.level)">{{ law.level_text || law.level }}</el-tag>
                    <span v-if="law.category" class="law-category">{{ law.category }}</span>
                    <span class="law-relevance">相关度 {{ Math.round((law.relevance || 0.5) * 100) }}%</span>
                  </div>
                </div>
                <div class="law-arrow">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M9 5l7 7-7 7"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 相关资讯 -->
        <div v-if="searchResult.relatedNews?.length" class="result-card">
          <div class="card-header">
            <div class="card-icon news">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 12h10"/>
              </svg>
            </div>
            <h3>相关资讯</h3>
            <span class="card-count">{{ searchResult.relatedNews.length }} 篇</span>
          </div>
          <div class="card-body">
            <div class="news-list">
              <div
                v-for="news in searchResult.relatedNews"
                :key="news.id"
                class="news-item"
                @click="viewNewsDetail(news)"
              >
                <div class="news-info">
                  <h4 class="news-title">{{ news.title }}</h4>
                  <p v-if="news.summary" class="news-summary">{{ news.summary }}</p>
                  <div class="news-meta">
                    <span v-if="news.source" class="news-source">{{ news.source }}</span>
                    <span v-if="news.type" class="news-type">{{ getNewsTypeLabel(news.type) }}</span>
                    <span class="news-views">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                        <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                      </svg>
                      {{ news.view_count || 0 }}
                    </span>
                  </div>
                </div>
                <div class="news-arrow">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M9 5l7 7-7 7"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 网友讨论 -->
        <div v-if="searchResult.relatedComments?.length" class="result-card">
          <div class="card-header">
            <div class="card-icon comment">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
              </svg>
            </div>
            <h3>网友讨论</h3>
            <span class="card-count">{{ searchResult.relatedComments.length }} 条</span>
          </div>
          <div class="card-body">
            <div class="comment-list">
              <div
                v-for="comment in searchResult.relatedComments"
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-avatar">
                  {{ (comment.user_nickname || '网友')[0] }}
                </div>
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.user_nickname || '匿名网友' }}</span>
                    <span v-if="comment.target_title" class="comment-target">
                      回复：{{ comment.target_title }}
                    </span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-footer">
                    <span v-if="comment.like_count" class="comment-likes">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3zM7 22H4a2 2 0 01-2-2v-7a2 2 0 012-2h3"/>
                      </svg>
                      {{ comment.like_count }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
          <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
        </svg>
      </div>
      <h3>开始您的智能检索</h3>
      <p>输入问题或关键词，AI 将帮您检索相关法规、资讯和讨论</p>
      <div class="empty-tips">
        <div class="tip-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <span>支持关键词检索</span>
        </div>
        <div class="tip-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <span>支持大白话提问</span>
        </div>
        <div class="tip-item">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          <span>智能关联相关内容</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { lawChat } from '@/api/ai.js'

const router = useRouter()

// 搜索相关状态
const searchQuery = ref('')
const isFocused = ref(false)
const categoryOpen = ref(false)
const loading = ref(false)
const searchResult = ref(null)

// 分类选项
const categories = ref([
  { label: '全部', value: 'all' },
  { label: '法规', value: 'law' },
  { label: '资讯', value: 'news' },
  { label: '讨论', value: 'comment' }
])

const currentCategory = ref(categories.value[0])

// 热门标签
const hotTags = ref([
  '白色污染治理',
  '企业合规',
  '农膜回收',
  '塑料制品禁限',
  '违规倾倒处罚'
])

// 选择分类
function selectCategory(cat) {
  currentCategory.value = cat
}

// 搜索
async function handleSearch() {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索内容')
    return
  }

  loading.value = true
  searchResult.value = null

  try {
    const res = await lawChat({
      question: searchQuery.value,
      role: 'common',
      top_k: 5,
      enable_rag: true
    })

    searchResult.value = res

    // 保存搜索历史
    saveSearchHistory(searchQuery.value)

    ElMessage.success('检索完成')
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('检索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 快速搜索
function quickSearch(tag) {
  searchQuery.value = tag
  handleSearch()
}

// 清除搜索
function clearSearch() {
  searchQuery.value = ''
  searchResult.value = null
}

// 复制回答
function copyAnswer() {
  if (searchResult.value?.answer) {
    navigator.clipboard.writeText(searchResult.value.answer)
    ElMessage.success('已复制到剪贴板')
  }
}

// 继续追问
function askMore() {
  if (searchResult.value?.suggestions?.length) {
    const sug = searchResult.value.suggestions[0]
    searchQuery.value = sug
    handleSearch()
  }
}

// 追问
function followUpSearch(sug) {
  searchQuery.value = sug
  handleSearch()
}

// 查看法规详情
function viewLawDetail(law) {
  router.push(`/law/${law.id}`)
}

// 查看资讯详情
function viewNewsDetail(news) {
  router.push(`/news/${news.id}`)
}

// 格式化回答（简单转换换行）
function formatAnswer(text) {
  if (!text) return ''
  return text.replace(/\n/g, '<br>')
}

// 获取法规层级类型
function getLevelType(level) {
  const types = {
    'national': '',
    'provincial': 'success',
    'city': 'warning'
  }
  return types[level] || 'info'
}

// 获取资讯类型标签
function getNewsTypeLabel(type) {
  const labels = {
    'news': '新闻',
    'hotspot': '热点',
    'policy': '政策解读',
    'activity': '活动'
  }
  return labels[type] || type
}

// 保存搜索历史
function saveSearchHistory(query) {
  try {
    const history = JSON.parse(localStorage.getItem('ai_search_history') || '[]')
    const filtered = history.filter(h => h !== query)
    filtered.unshift(query)
    localStorage.setItem('ai_search_history', JSON.stringify(filtered.slice(0, 10)))
  } catch (e) {
    // 忽略
  }
}

// 页面加载时检查 URL 参数
onMounted(() => {
  const query = router.currentRoute.value.query.q
  if (query) {
    searchQuery.value = query
    handleSearch()
  }
})
</script>

<style lang="scss" scoped>
.smart-search-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8faf9 0%, #ecf5f0 100%);
  position: relative;
  overflow: hidden;
}

// 背景装饰
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
  background: linear-gradient(135deg, rgba(82,183,136,0.1), rgba(149,213,178,0.05));
}

.bg-circle-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
}

.bg-circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
}

.bg-circle-3 {
  width: 300px;
  height: 300px;
  top: 40%;
  left: 60%;
}

// 搜索区域
.search-section {
  position: relative;
  z-index: 1;
  padding: 60px 20px 40px;
  text-align: center;
}

.search-header {
  margin-bottom: 40px;

  .header-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto 20px;
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 36px;
      height: 36px;
      color: #fff;
    }
  }

  .header-title {
    font-size: 32px;
    font-weight: 700;
    color: #1a1a1a;
    margin: 0 0 12px;
  }

  .header-subtitle {
    font-size: 16px;
    color: #666;
    margin: 0;
  }
}

// 搜索框
.search-box-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.search-box {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 50px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  padding: 8px;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 6px 30px rgba(82,183,136,0.15);
  }

  &.is-focused {
    box-shadow: 0 6px 30px rgba(82,183,136,0.25);
  }

  &.is-loading {
    opacity: 0.9;
  }
}

.search-category {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  border-right: 1px solid #eee;
  white-space: nowrap;

  svg {
    width: 12px;
    height: 12px;
  }

  &:hover {
    color: #52b788;
  }
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 0 12px;
  gap: 10px;
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #999;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: #333;
  background: transparent;

  &::placeholder {
    color: #aaa;
  }
}

.clear-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ddd;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;

  svg {
    width: 12px;
    height: 12px;
    color: #fff;
  }

  &:hover {
    background: #ccc;
  }
}

.search-btn {
  padding: 12px 28px;
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  border: none;
  border-radius: 30px;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(82,183,136,0.4);
  }

  &:disabled {
    opacity: 0.8;
    cursor: not-allowed;
  }

  &.loading {
    padding: 12px 20px;
  }
}

.loading-text {
  display: flex;
  gap: 4px;

  .dot {
    width: 6px;
    height: 6px;
    background: #fff;
    border-radius: 50%;
    animation: bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

// 热门标签
.hot-tags {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
  flex-wrap: wrap;

  .hot-label {
    font-size: 13px;
    color: #999;
  }

  .hot-tag {
    padding: 6px 14px;
    background: rgba(82,183,136,0.1);
    border-radius: 20px;
    font-size: 13px;
    color: #52b788;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: rgba(82,183,136,0.2);
      transform: translateY(-1px);
    }
  }
}

// 结果区域
.results-section {
  position: relative;
  z-index: 1;
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

.loading-state {
  text-align: center;
  padding: 60px 0;

  .loading-spinner {
    margin-bottom: 20px;

    .spinner {
      width: 48px;
      height: 48px;
      margin: 0 auto;
      border: 3px solid rgba(82,183,136,0.2);
      border-top-color: #52b788;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }

  .loading-text {
    color: #666;
    font-size: 14px;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.results-content {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

// 搜索摘要
.result-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(82,183,136,0.1), rgba(82,183,136,0.05));
  border-radius: 16px;
  margin-bottom: 24px;

  .summary-icon {
    width: 48px;
    height: 48px;
    background: #52b788;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    svg {
      width: 24px;
      height: 24px;
      color: #fff;
    }
  }

  .summary-text {
    flex: 1;

    h3 {
      font-size: 14px;
      color: #52b788;
      margin: 0 0 4px;
      font-weight: 500;
    }

    p {
      font-size: 15px;
      color: #333;
      margin: 0;
    }
  }

  .summary-stats {
    display: flex;
    gap: 20px;
  }

  .stat-item {
    text-align: center;

    .stat-num {
      display: block;
      font-size: 24px;
      font-weight: 700;
      color: #52b788;
    }

    .stat-label {
      font-size: 12px;
      color: #999;
    }
  }
}

// 结果卡片
.result-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);

  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;

    .card-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;

      svg {
        width: 20px;
        height: 20px;
        color: #fff;
      }

      &.ai { background: linear-gradient(135deg, #667eea, #764ba2); }
      &.law { background: linear-gradient(135deg, #11998e, #38ef7d); }
      &.news { background: linear-gradient(135deg, #fc4a1a, #f7b733); }
      &.comment { background: linear-gradient(135deg, #4facfe, #00f2fe); }
    }

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin: 0;
    }

    .card-count {
      margin-left: auto;
      padding: 4px 10px;
      background: #f5f5f5;
      border-radius: 20px;
      font-size: 12px;
      color: #666;
    }
  }

  .card-body {
    color: #555;
    line-height: 1.8;
    font-size: 14px;
  }
}

// AI 回答卡片
.ai-answer {
  border: 2px solid rgba(82,183,136,0.2);

  .card-actions {
    display: flex;
    gap: 12px;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: #f5f5f5;
    border: none;
    border-radius: 8px;
    font-size: 13px;
    color: #666;
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: #eee;
    }

    &.primary {
      background: linear-gradient(135deg, #52b788, #2d6a4f);
      color: #fff;

      &:hover {
        opacity: 0.9;
      }
    }
  }
}

.answer-content {
  white-space: pre-wrap;
  word-break: break-word;
}

// 追问建议
.suggestions-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: #f8faf9;
  border-radius: 12px;
  margin-bottom: 20px;

  .suggestions-label {
    font-size: 13px;
    color: #999;
    white-space: nowrap;
  }

  .suggestions-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .suggestion-item {
    padding: 6px 14px;
    background: #fff;
    border: 1px solid #e8e8e8;
    border-radius: 20px;
    font-size: 13px;
    color: #666;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #52b788;
      color: #52b788;
      background: rgba(82,183,136,0.05);
    }
  }
}

// 法规列表
.law-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.law-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f0f5f3;
    transform: translateX(4px);
  }

  .law-info {
    flex: 1;
  }

  .law-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin: 0 0 8px;
  }

  .law-meta {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 12px;
    color: #999;

    .law-category {
      color: #666;
    }

    .law-relevance {
      color: #52b788;
    }
  }

  .law-arrow {
    svg {
      width: 20px;
      height: 20px;
      color: #ccc;
    }
  }
}

// 资讯列表
.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f0f5f3;
    transform: translateX(4px);
  }

  .news-info {
    flex: 1;
  }

  .news-title {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin: 0 0 6px;
  }

  .news-summary {
    font-size: 13px;
    color: #888;
    margin: 0 0 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .news-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    color: #999;

    .news-views {
      display: flex;
      align-items: center;
      gap: 4px;

      svg {
        width: 14px;
        height: 14px;
      }
    }
  }

  .news-arrow svg {
    width: 20px;
    height: 20px;
    color: #ccc;
  }
}

// 评论列表
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;

  .comment-avatar {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    flex-shrink: 0;
  }

  .comment-content {
    flex: 1;
    background: #f9fafb;
    border-radius: 12px;
    padding: 12px 16px;
  }

  .comment-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;

    .comment-author {
      font-size: 13px;
      font-weight: 500;
      color: #333;
    }

    .comment-target {
      font-size: 12px;
      color: #52b788;
    }
  }

  .comment-text {
    font-size: 13px;
    color: #555;
    margin: 0;
    line-height: 1.6;
  }

  .comment-footer {
    margin-top: 8px;

    .comment-likes {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #999;

      svg {
        width: 14px;
        height: 14px;
      }
    }
  }
}

// 空状态
.empty-state {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 80px 20px;

  .empty-icon {
    width: 100px;
    height: 100px;
    margin: 0 auto 24px;
    background: linear-gradient(135deg, rgba(82,183,136,0.1), rgba(82,183,136,0.05));
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 48px;
      height: 48px;
      color: #52b788;
      opacity: 0.5;
    }
  }

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: #999;
    margin: 0 0 32px;
  }

  .empty-tips {
    display: flex;
    justify-content: center;
    gap: 32px;
    flex-wrap: wrap;
  }

  .tip-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #666;

    svg {
      width: 18px;
      height: 18px;
      color: #52b788;
    }
  }
}
</style>
