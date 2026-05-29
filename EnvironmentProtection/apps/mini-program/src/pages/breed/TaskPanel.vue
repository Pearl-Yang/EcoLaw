<template>
  <view class="task-panel-container">
    <!-- 展开/收起按钮 -->
    <view 
      class="toggle-button"
      :class="{ 'is-expanded': isExpanded }"
      @click="togglePanel"
    >
      <text class="toggle-icon">{{ isExpanded ? '>' : '<' }}</text>
      <text class="toggle-text">{{ isExpanded ? '收起' : '任务' }}</text>
      <!-- 任务数量角标 -->
      <view v-if="availableTaskCount > 0" class="task-badge">
        <text class="badge-text">{{ availableTaskCount }}</text>
      </view>
    </view>
    
    <!-- 任务面板 -->
    <view 
      class="task-panel"
      :class="{ 'is-expanded': isExpanded }"
    >
      <!-- 面板头部 -->
      <view class="panel-header">
        <text class="panel-title">互动任务</text>
        <view class="header-stats">
          <view class="stat-item">
            <text class="stat-label">今日</text>
            <text class="stat-value">{{ todayStats.completedCount || 0 }}</text>
          </view>
          <view class="stat-item">
            <text class="stat-label">EXP</text>
            <text class="stat-value exp-color">+{{ todayStats.expGained || 0 }}</text>
          </view>
        </view>
      </view>
      
      <!-- 任务分类标签 -->
      <scroll-view 
        class="category-tabs"
        scroll-x
        enhanced
        :show-scrollbar="false"
      >
        <view 
          v-for="category in categories"
          :key="category.type"
          class="category-tab"
          :class="{ 'is-active': selectedCategory === category.type }"
          @click="selectCategory(category.type)"
        >
          <text class="category-icon">{{ category.icon }}</text>
          <text class="category-name">{{ category.name }}</text>
        </view>
      </scroll-view>
      
      <!-- 任务列表 -->
      <scroll-view 
        class="task-list"
        scroll-y
        refresher-enabled="true"
        @refresherrefresh="onRefresh"
        :refresher-triggered="refreshing"
      >
        <view 
          v-for="task in filteredTasks"
          :key="task.taskCode"
          class="task-card"
          :class="{ 
            'is-cooldown': task.inCooldown,
            'is-disabled': !task.available 
          }"
          @click="onTaskClick(task)"
        >
          <!-- 任务图标 -->
          <view class="task-icon" :class="'icon-' + task.taskType">
            <text class="icon-text">{{ getTaskIcon(task.taskType) }}</text>
          </view>
          
          <!-- 任务信息 -->
          <view class="task-info">
            <view class="task-header">
              <text class="task-name">{{ task.taskName }}</text>
              <text v-if="task.minPetLevel > 1" class="level-requirement">
                Lv.{{ task.minPetLevel }}
              </text>
            </view>
            <text class="task-desc">{{ task.description }}</text>
            
            <!-- 奖励信息 -->
            <view class="task-rewards">
              <view class="reward-item">
                <text class="reward-icon">*</text>
                <text class="reward-value exp-color">+{{ task.expReward }}</text>
              </view>
              <view class="reward-item">
                <text class="reward-icon">$</text>
                <text class="reward-value coin-color">+{{ task.coinReward }}</text>
              </view>
              <view v-if="task.energyCost > 0" class="reward-item">
                <text class="reward-icon">!</text>
                <text class="reward-value energy-color">-{{ task.energyCost }}</text>
              </view>
            </view>
          </view>
          
          <!-- 冷却/执行状态 -->
          <view class="task-action">
            <view v-if="task.inCooldown" class="cooldown-overlay">
              <text class="cooldown-time">{{ formatCooldown(task.cooldownRemaining) }}</text>
            </view>
            <view v-else-if="!task.available" class="locked-overlay">
              <text class="locked-text">{{ task.unavailableReason || '不可用' }}</text>
            </view>
            <view v-else class="action-button" @click.stop="executeTask(task)">
              <text class="action-text">执行</text>
            </view>
          </view>
        </view>
        
        <!-- 空状态 -->
        <view v-if="filteredTasks.length === 0" class="empty-state">
          <text class="empty-icon">?</text>
          <text class="empty-text">暂无可用任务</text>
        </view>
      </scroll-view>
      
      <!-- 任务执行中动画 -->
      <view v-if="executingTask" class="execution-overlay">
        <view class="execution-content">
          <view class="execution-icon">
            <text>{{ getTaskIcon(executingTask.taskType) }}</text>
          </view>
          <text class="execution-name">{{ executingTask.taskName }}</text>
          <view class="progress-bar">
            <view 
              class="progress-fill"
              :style="{ width: executionProgress + '%' }"
            ></view>
          </view>
          <text class="execution-time">{{ executionTime }}s</text>
        </view>
      </view>
    </view>
    
    <!-- 任务完成提示 -->
    <view 
      v-if="showCompletion" 
      class="completion-toast"
      :class="{ 'show': showCompletion }"
    >
      <view class="toast-content">
        <text class="toast-icon">!</text>
        <view class="toast-info">
          <text class="toast-title">{{ completionResult.taskName }}</text>
          <text class="toast-reward">+{{ completionResult.expGained }} 经验</text>
        </view>
        <view v-if="completionResult.levelUp" class="level-up-badge">
          <text class="level-up-text">升级!</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  // 任务列表
  tasks: {
    type: Array,
    default: () => []
  },
  // 宠物等级
  petLevel: {
    type: Number,
    default: 1
  },
  // 宠物能量
  petEnergy: {
    type: Number,
    default: 100
  },
  // 今日统计数据
  todayStats: {
    type: Object,
    default: () => ({
      completedCount: 0,
      expGained: 0
    })
  }
})

const emit = defineEmits([
  'taskClick',
  'taskExecute',
  'taskComplete',
  'refresh'
])

// 状态
const isExpanded = ref(false)
const selectedCategory = ref('all')
const refreshing = ref(false)
const executingTask = ref(null)
const executionProgress = ref(0)
const executionTime = ref(0)
const showCompletion = ref(false)
const completionResult = ref(null)

// 分类定义
const categories = [
  { type: 'all', name: '全部', icon: '?' },
  { type: 'feed', name: '喂食', icon: '@' },
  { type: 'pet', name: '抚摸', icon: '#' },
  { type: 'clean', name: '清洁', icon: '$' },
  { type: 'train', name: '训练', icon: '%' },
  { type: 'play', name: '玩耍', icon: '&' }
]

// 可用任务数量
const availableTaskCount = computed(() => {
  return props.tasks.filter(t => t.available && !t.inCooldown).length
})

// 过滤后的任务列表
const filteredTasks = computed(() => {
  if (selectedCategory.value === 'all') {
    return props.tasks
  }
  return props.tasks.filter(t => t.taskType === selectedCategory.value)
})

// 切换面板
function togglePanel() {
  isExpanded.value = !isExpanded.value
}

// 选择分类
function selectCategory(type) {
  selectedCategory.value = type
}

// 获取任务图标
function getTaskIcon(taskType) {
  const icons = {
    feed: '@',
    pet: '#',
    clean: '$',
    train: '%',
    play: '&'
  }
  return icons[taskType] || '?'
}

// 格式化冷却时间
function formatCooldown(seconds) {
  if (seconds < 60) {
    return `${seconds}s`
  }
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return secs > 0 ? `${minutes}m${secs}s` : `${minutes}m`
}

// 任务点击
function onTaskClick(task) {
  if (task.inCooldown) {
    uni.showToast({
      title: `冷却中 ${formatCooldown(task.cooldownRemaining)}`,
      icon: 'none'
    })
    return
  }
  
  if (!task.available) {
    uni.showToast({
      title: task.unavailableReason || '等级不足',
      icon: 'none'
    })
    return
  }
  
  emit('taskClick', task)
}

// 执行任务
function executeTask(task) {
  if (executingTask.value) return
  
  emit('taskExecute', task)
  
  // 开始执行动画
  executingTask.value = task
  executionProgress.value = 0
  executionTime.value = task.durationSeconds || 5
  
  const startTime = Date.now()
  const duration = (task.durationSeconds || 5) * 1000
  
  const progressTimer = setInterval(() => {
    const elapsed = Date.now() - startTime
    executionProgress.value = Math.min(100, (elapsed / duration) * 100)
    executionTime.value = Math.max(0, Math.ceil((duration - elapsed) / 1000))
    
    if (elapsed >= duration) {
      clearInterval(progressTimer)
      completeTask(task)
    }
  }, 50)
}

// 完成任务
function completeTask(task) {
  // 隐藏执行动画
  executingTask.value = null
  executionProgress.value = 0
  
  // 显示完成提示
  const result = {
    taskCode: task.taskCode,
    taskName: task.taskName,
    taskType: task.taskType,
    expGained: task.expReward,
    coinGained: task.coinReward,
    levelUp: false
  }
  
  completionResult.value = result
  showCompletion.value = true
  
  // 3秒后隐藏提示
  setTimeout(() => {
    showCompletion.value = false
  }, 3000)
  
  emit('taskComplete', result)
}

// 下拉刷新
async function onRefresh() {
  refreshing.value = true
  emit('refresh')
  
  // 模拟延迟
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  refreshing.value = false
}

// 生命周期
let cooldownTimer = null

onMounted(() => {
  // 启动冷却倒计时
  cooldownTimer = setInterval(() => {
    // 更新冷却状态
    props.tasks.forEach(task => {
      if (task.inCooldown && task.cooldownRemaining > 0) {
        task.cooldownRemaining--
        if (task.cooldownRemaining <= 0) {
          task.inCooldown = false
        }
      }
    })
  }, 1000)
})

onUnmounted(() => {
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
  }
})

// 暴露方法
defineExpose({
  showTaskResult: (result) => {
    completionResult.value = result
    showCompletion.value = true
    
    setTimeout(() => {
      showCompletion.value = false
    }, 3000)
  }
})
</script>

<style scoped>
.task-panel-container {
  position: absolute;
  top: 0;
  right: 0;
  height: 100%;
  z-index: 100;
  display: flex;
  flex-direction: row-reverse;
}

.toggle-button {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80rpx;
  height: 160rpx;
  margin-top: 200rpx;
  background: linear-gradient(135deg, #4CAF50, #388E3C);
  border-radius: 20rpx 0 0 20rpx;
  box-shadow: -4rpx 0 12rpx rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.toggle-button.is-expanded {
  width: 60rpx;
  border-radius: 20rpx 0 0 20rpx;
}

.toggle-icon {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.toggle-text {
  font-size: 24rpx;
  color: #fff;
  margin-top: 8rpx;
  writing-mode: vertical-rl;
}

.task-badge {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 8rpx;
  background: #FF5722;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: bold;
}

.task-panel {
  width: 0;
  height: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20rpx 0 0 20rpx;
  overflow: hidden;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.task-panel.is-expanded {
  width: 560rpx;
}

.panel-header {
  padding: 30rpx;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.header-stats {
  display: flex;
  gap: 20rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.exp-color {
  color: #FF9800;
}

.coin-color {
  color: #FFC107;
}

.energy-color {
  color: #03A9F4;
}

.category-tabs {
  padding: 20rpx 30rpx;
  white-space: nowrap;
  border-bottom: 1px solid #eee;
  height: auto;
}

.category-tab {
  display: inline-flex;
  align-items: center;
  padding: 12rpx 24rpx;
  margin-right: 16rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  transition: all 0.2s ease;
}

.category-tab.is-active {
  background: #4CAF50;
}

.category-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.category-name {
  font-size: 26rpx;
  color: #666;
}

.category-tab.is-active .category-name {
  color: #fff;
}

.task-list {
  flex: 1;
  padding: 20rpx;
  height: 0; /* 重要：允许flex布局 */
}

.task-card {
  display: flex;
  padding: 24rpx;
  margin-bottom: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.task-card:active {
  transform: scale(0.98);
}

.task-card.is-cooldown {
  opacity: 0.7;
}

.task-card.is-disabled {
  opacity: 0.5;
}

.task-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.icon-feed {
  background: linear-gradient(135deg, #FF9800, #FF5722);
}

.icon-pet {
  background: linear-gradient(135deg, #E91E63, #C2185B);
}

.icon-clean {
  background: linear-gradient(135deg, #03A9F4, #2196F3);
}

.icon-train {
  background: linear-gradient(135deg, #9C27B0, #7B1FA2);
}

.icon-play {
  background: linear-gradient(135deg, #4CAF50, #8BC34A);
}

.icon-text {
  font-size: 36rpx;
  color: #fff;
}

.task-info {
  flex: 1;
  min-width: 0;
}

.task-header {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.task-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.level-requirement {
  font-size: 22rpx;
  color: #999;
  margin-left: 12rpx;
  padding: 4rpx 8rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
}

.task-desc {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-rewards {
  display: flex;
  gap: 16rpx;
}

.reward-item {
  display: flex;
  align-items: center;
}

.reward-icon {
  font-size: 20rpx;
  margin-right: 4rpx;
}

.reward-value {
  font-size: 24rpx;
  font-weight: 500;
}

.task-action {
  width: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-button {
  padding: 12rpx 20rpx;
  background: #4CAF50;
  border-radius: 30rpx;
}

.action-text {
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;
}

.cooldown-overlay,
.locked-overlay {
  padding: 8rpx 12rpx;
  background: #e0e0e0;
  border-radius: 8rpx;
}

.cooldown-time {
  font-size: 24rpx;
  color: #666;
  font-weight: 500;
}

.locked-text {
  font-size: 22rpx;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  color: #ddd;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.execution-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.execution-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.execution-icon {
  width: 120rpx;
  height: 120rpx;
  background: #4CAF50;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  font-size: 60rpx;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.execution-name {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 30rpx;
}

.progress-bar {
  width: 300rpx;
  height: 12rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #8BC34A);
  border-radius: 6rpx;
  transition: width 0.1s linear;
}

.execution-time {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 20rpx;
}

.completion-toast {
  position: absolute;
  top: 200rpx;
  right: 80rpx;
  padding: 20rpx 30rpx;
  background: linear-gradient(135deg, #4CAF50, #388E3C);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.3);
  opacity: 0;
  transform: translateX(100%);
  transition: all 0.3s ease;
  z-index: 300;
}

.completion-toast.show {
  opacity: 1;
  transform: translateX(0);
}

.toast-content {
  display: flex;
  align-items: center;
}

.toast-icon {
  width: 60rpx;
  height: 60rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #4CAF50;
  margin-right: 16rpx;
}

.toast-info {
  display: flex;
  flex-direction: column;
}

.toast-title {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
}

.toast-reward {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

.level-up-badge {
  margin-left: 16rpx;
  padding: 8rpx 16rpx;
  background: #FF9800;
  border-radius: 20rpx;
}

.level-up-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
}
</style>
