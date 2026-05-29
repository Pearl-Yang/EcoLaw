<template>
  <div class="aliyun-search" :class="{ 'is-focused': isFocused, 'has-value': searchValue }">
    <!-- 搜索类型选择器 -->
    <el-dropdown
      v-if="showCategory"
      trigger="click"
      @visible-change="(v) => (categoryOpen = v)"
    >
      <div class="search-category" :class="{ active: categoryOpen }">
        <span class="category-text">{{ currentCategory.label }}</span>
        <svg class="category-arrow" viewBox="0 0 16 16" fill="none">
          <path d="M4 6l4 4 4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <template #dropdown>
        <el-dropdown-menu class="search-category-menu">
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

    <!-- 搜索图标 -->
    <div class="search-icon">
      <svg viewBox="0 0 20 20" fill="none">
        <path
          d="M17.5 17.5L12.5 12.5M14.1667 8.33333C14.1667 11.555 11.555 14.1667 8.33333 14.1667C5.11167 14.1667 2.5 11.555 2.5 8.33333C2.5 5.11167 5.11167 2.5 8.33333 2.5C11.555 2.5 14.1667 5.11167 14.1667 8.33333Z"
          stroke="currentColor"
          stroke-width="1.75"
          stroke-linecap="round"
        />
      </svg>
    </div>

    <!-- 输入框 -->
    <input
      ref="inputRef"
      v-model="searchValue"
      type="text"
      class="search-input"
      :placeholder="placeholder"
      @focus="handleFocus"
      @blur="handleBlur"
      @input="handleInput"
      @keydown.enter="handleSearch"
      @keydown.up.prevent="navigateSuggestion(-1)"
      @keydown.down.prevent="navigateSuggestion(1)"
      @keydown.esc="handleEsc"
    />

    <!-- 清除按钮 -->
    <transition name="fade">
      <div v-if="searchValue" class="search-clear" @click="clearSearch">
        <svg viewBox="0 0 16 16" fill="none">
          <path d="M12 4L4 12M4 4l8 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </div>
    </transition>

    <!-- 搜索按钮 -->
    <div class="search-btn" @click="handleSearch">
      <span>搜索</span>
    </div>

    <!-- 热门搜索建议浮层 -->
    <transition name="dropdown-fade">
      <div
        v-if="showSuggestions && !categoryOpen"
        class="search-suggestions"
        :style="suggestionsStyle"
      >
        <!-- 热门搜索 -->
        <div v-if="hotSearches.length" class="suggestion-section">
          <div class="suggestion-header">
            <span class="suggestion-title">
              <svg viewBox="0 0 16 16" fill="none">
                <path d="M8 1l2.163 4.382 4.837.703-3.5 3.412.826 4.813L8 12.085l-4.326 2.225.826-4.813-3.5-3.412 4.837-.703L8 1z" fill="currentColor"/>
              </svg>
              热门搜索
            </span>
          </div>
          <div class="suggestion-tags">
            <span
              v-for="(item, index) in hotSearches"
              :key="index"
              class="suggestion-tag"
              :class="{ active: activeSuggestion === index }"
              @click="selectSuggestion(item)"
              @mouseenter="activeSuggestion = index"
            >
              {{ item }}
            </span>
          </div>
        </div>

        <!-- 搜索历史 -->
        <div v-if="searchHistory.length && !searchValue" class="suggestion-section">
          <div class="suggestion-header">
            <span class="suggestion-title">
              <svg viewBox="0 0 16 16" fill="none">
                <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1.5"/>
                <path d="M8 5v3.5l2 2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              搜索历史
            </span>
            <span class="suggestion-action" @click="clearHistory">清除</span>
          </div>
          <div class="suggestion-tags">
            <span
              v-for="(item, index) in searchHistory"
              :key="index"
              class="suggestion-tag history"
              :class="{ active: activeSuggestion === hotSearches.length + index }"
              @click="selectSuggestion(item)"
              @mouseenter="activeSuggestion = hotSearches.length + index"
            >
              {{ item }}
            </span>
          </div>
        </div>

        <!-- 实时搜索建议 -->
        <div v-if="searchValue && suggestions.length" class="suggestion-section">
          <div class="suggestion-header">
            <span class="suggestion-title">
              <svg viewBox="0 0 16 16" fill="none">
                <path d="M14 14L10 10M6.667 4.333A3.333 3.333 0 1 0 6.667 11 3.333 3.333 0 1 0 6.667 4.333Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              相关搜索
            </span>
          </div>
          <div class="suggestion-list">
            <div
              v-for="(item, index) in suggestions"
              :key="index"
              class="suggestion-item"
              :class="{ active: activeSuggestion === index }"
              @click="selectSuggestion(item.text || item)"
              @mouseenter="activeSuggestion = index"
            >
              <span v-html="highlightKeyword(item.text || item, searchValue)" />
              <span v-if="item.count" class="suggestion-count">{{ item.count }} 条结果</span>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '搜索任务、企业、法规...'
  },
  showCategory: {
    type: Boolean,
    default: false
  },
  categories: {
    type: Array,
    default: () => [
      { label: '全部', value: 'all' },
      { label: '任务', value: 'task' },
      { label: '企业', value: 'enterprise' },
      { label: '法规', value: 'law' },
      { label: '知识库', value: 'knowledge' }
    ]
  },
  hotSearches: {
    type: Array,
    default: () => ['白色污染治理', '企业合规', '农膜回收', '塑料制品禁限']
  },
  suggestions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'search', 'focus', 'blur', 'select'])

// 状态
const isFocused = ref(false)
const searchValue = ref(props.modelValue)
const inputRef = ref(null)
const categoryOpen = ref(false)
const activeSuggestion = ref(-1)
const showSuggestions = ref(false)
const searchHistory = ref([])

// 计算属性
const currentCategory = computed(() => {
  return props.categories.find(c => c.value === categoryValue.value) || props.categories[0]
})

const categoryValue = ref('all')

const suggestionsStyle = computed(() => {
  return {
    width: '480px'
  }
})

// 监听外部值变化
watch(() => props.modelValue, (val) => {
  searchValue.value = val
})

watch(searchValue, (val) => {
  emit('update:modelValue', val)
  if (val) {
    showSuggestions.value = true
    activeSuggestion.value = -1
  }
})

// 加载搜索历史
onMounted(() => {
  try {
    const history = localStorage.getItem('search_history')
    if (history) {
      searchHistory.value = JSON.parse(history)
    }
  } catch (e) {
    searchHistory.value = []
  }

  // 点击外部关闭建议
  document.addEventListener('click', handleOutsideClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick)
})

function handleOutsideClick(e) {
  const searchEl = document.querySelector('.aliyun-search')
  if (searchEl && !searchEl.contains(e.target)) {
    showSuggestions.value = false
    categoryOpen.value = false
  }
}

// 方法
function handleFocus() {
  isFocused.value = true
  showSuggestions.value = true
  emit('focus')
}

function handleBlur() {
  isFocused.value = false
  emit('blur')
}

function handleInput() {
  activeSuggestion.value = -1
}

function handleSearch() {
  if (activeSuggestion.value >= 0) {
    const allSuggestions = [...props.hotSearches, ...searchHistory.value, ...props.suggestions.map(s => s.text || s)]
    if (allSuggestions[activeSuggestion.value]) {
      searchValue.value = allSuggestions[activeSuggestion.value]
    }
  }
  
  if (searchValue.value.trim()) {
    addToHistory(searchValue.value.trim())
  }
  
  showSuggestions.value = false
  emit('search', searchValue.value)
}

function selectSuggestion(text) {
  searchValue.value = text
  addToHistory(text)
  showSuggestions.value = false
  emit('select', text)
  emit('search', text)
}

function clearSearch() {
  searchValue.value = ''
  showSuggestions.value = false
  inputRef.value?.focus()
}

function selectCategory(cat) {
  categoryValue.value = cat.value
  inputRef.value?.focus()
}

function navigateSuggestion(direction) {
  const total = props.hotSearches.length + searchHistory.value.length + props.suggestions.length
  if (total === 0) return

  if (direction === 1) {
    activeSuggestion.value = activeSuggestion.value < total - 1 ? activeSuggestion.value + 1 : 0
  } else {
    activeSuggestion.value = activeSuggestion.value > 0 ? activeSuggestion.value - 1 : total - 1
  }
}

function handleEsc() {
  showSuggestions.value = false
  inputRef.value?.blur()
}

function addToHistory(keyword) {
  const history = searchHistory.value.filter(h => h !== keyword)
  history.unshift(keyword)
  searchHistory.value = history.slice(0, 10)
  localStorage.setItem('search_history', JSON.stringify(searchHistory.value))
}

function clearHistory() {
  searchHistory.value = []
  localStorage.removeItem('search_history')
}

function highlightKeyword(text, keyword) {
  if (!keyword) return text
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<em>$1</em>')
}

// 暴露方法
defineExpose({
  focus: () => inputRef.value?.focus(),
  blur: () => inputRef.value?.blur(),
  clear: clearSearch
})
</script>

<style lang="scss" scoped>
// 主容器
.aliyun-search {
  position: relative;
  display: flex;
  align-items: center;
  height: 38px;
  background: #f5f5f5;
  border: 1.5px solid transparent;
  border-radius: 20px;
  transition: all 0.25s ease;
  overflow: visible;

  // 默认状态
  &:hover {
    background: #f0f0f0;
    border-color: #d9d9d9;

    .search-icon {
      color: #999;
    }
  }

  // 聚焦状态
  &.is-focused {
    background: #fff;
    border-color: #52b788;
    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.12);
    border-radius: 20px 20px 0 0;

    .search-icon {
      color: #52b788;
    }

    .search-btn {
      background: #52b788;
      color: #fff;
    }
  }

  // 有内容时的样式
  &.has-value {
    .search-clear {
      opacity: 1;
      transform: scale(1);
    }
  }
}

// 搜索分类
.search-category {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 12px 0 14px;
  height: 100%;
  border-right: 1px solid #e8e8e8;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
  flex-shrink: 0;

  .category-text {
    font-size: 13px;
    color: #666;
    white-space: nowrap;
  }

  .category-arrow {
    width: 12px;
    height: 12px;
    color: #999;
    transition: transform 0.2s;
  }

  &:hover,
  &.active {
    background: rgba(0, 0, 0, 0.03);

    .category-text {
      color: #333;
    }

    .category-arrow {
      color: #52b788;
      transform: rotate(180deg);
    }
  }
}

// 搜索图标
.search-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10px 0 12px;
  color: #bfbfbf;
  transition: color 0.2s;
  flex-shrink: 0;

  svg {
    width: 16px;
    height: 16px;
  }
}

// 输入框
.search-input {
  flex: 1;
  height: 100%;
  padding: 0 8px;
  border: none;
  background: transparent;
  font-size: 14px;
  color: #333;
  outline: none;
  min-width: 120px;

  &::placeholder {
    color: #999;
  }
}

// 清除按钮
.search-clear {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  margin-right: 6px;
  border-radius: 50%;
  background: #ccc;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
  opacity: 0;
  transform: scale(0.5);
  flex-shrink: 0;

  svg {
    width: 10px;
    height: 10px;
  }

  &:hover {
    background: #999;
    transform: scale(1.1);
  }
}

// 搜索按钮
.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 18px;
  background: #e8e8e8;
  border-radius: 0 20px 20px 0;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;

  &:hover {
    background: #52b788;
    color: #fff;
  }
}

// 建议浮层
.search-suggestions {
  position: absolute;
  top: calc(100% + 2px);
  left: -1.5px;
  right: -1.5px;
  background: #fff;
  border: 1.5px solid #52b788;
  border-top: none;
  border-radius: 0 0 16px 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  z-index: 100;
  overflow: hidden;
}

// 建议区块
.suggestion-section {
  padding: 16px;

  &:not(:last-child) {
    border-bottom: 1px solid #f0f0f0;
  }
}

.suggestion-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.suggestion-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #666;

  svg {
    width: 14px;
    height: 14px;
    color: #52b788;
  }
}

.suggestion-action {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #52b788;
  }
}

// 标签
.suggestion-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-tag {
  padding: 5px 12px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;

  &:hover,
  &.active {
    background: #e8f5ed;
    color: #52b788;
  }

  &.history {
    background: transparent;
    border: 1px solid #e8e8e8;

    &:hover,
    &.active {
      border-color: #52b788;
      background: #e8f5ed;
    }
  }
}

// 建议列表
.suggestion-list {
  display: flex;
  flex-direction: column;
}

.suggestion-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;

  &:hover,
  &.active {
    background: #f5f9f6;
  }

  :deep(em) {
    font-style: normal;
    color: #52b788;
    font-weight: 600;
  }

  .suggestion-count {
    font-size: 12px;
    color: #999;
  }
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: all 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scale(0.5);
}

.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.2s ease;
}

.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

// 分类下拉菜单样式
:deep(.search-category-menu) {
  padding: 8px 0;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  .el-dropdown-menu__item {
    padding: 8px 16px;
    font-size: 13px;
    color: #666;

    span {
      transition: color 0.2s;
    }

    &:hover {
      background: #e8f5ed;
      
      span {
        color: #52b788;
      }
    }

    span.active {
      color: #52b788;
      font-weight: 600;
    }
  }
}
</style>
