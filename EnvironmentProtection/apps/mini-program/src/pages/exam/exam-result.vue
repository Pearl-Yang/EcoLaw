<template>
  <view class="result-container">
    <!-- 顶部结果卡片 -->
    <view class="result-header">
      <view class="result-card">
        <view class="result-icon" :class="isPassed ? 'pass' : 'fail'">
          <text>{{ isPassed ? '🎉' : '😢' }}</text>
        </view>
        <view class="result-status">
          <text class="status-text">{{ isPassed ? '恭喜及格' : '未达及格线' }}</text>
          <text class="paper-title">{{ record.paperTitle }}</text>
        </view>
      </view>
    </view>

    <!-- 成绩统计 -->
    <view class="stats-section">
      <view class="score-display">
        <view class="main-score">
          <text class="score-value">{{ record.score || 0 }}</text>
          <text class="score-unit">分</text>
        </view>
        <view class="score-info">
          <text class="total-score">满分 {{ paper.totalScore || 100 }}</text>
          <text class="pass-line">及格线 {{ record.paperId ? 60 : 60 }}</text>
        </view>
      </view>

      <view class="stats-grid">
        <view class="stat-card">
          <view class="stat-value correct">{{ record.correctCount || 0 }}</view>
          <view class="stat-label">答对题数</view>
        </view>
        <view class="stat-card">
          <view class="stat-value total">{{ record.totalCount || 0 }}</view>
          <view class="stat-label">总题数</view>
        </view>
        <view class="stat-card">
          <view class="stat-value rate">{{ record.correctRate || 0 }}%</view>
          <view class="stat-label">正确率</view>
        </view>
        <view class="stat-card">
          <view class="stat-value time">{{ formatDuration(record.duration) }}</view>
          <view class="stat-label">用时</view>
        </view>
      </view>
    </view>

    <!-- 答题详情 -->
    <view class="detail-section">
      <view class="section-header">
        <text class="section-title">答题详情</text>
      </view>

      <view class="question-list">
        <view
          v-for="(item, index) in answerDetails"
          :key="item.questionId"
          class="question-item"
          :class="{ correct: item.isCorrect, wrong: !item.isCorrect }"
        >
          <view class="question-header">
            <view class="question-number">{{ index + 1 }}</view>
            <view class="question-type">{{ getTypeName(item.questionType) }}</view>
            <view class="question-result" :class="item.isCorrect ? 'correct' : 'wrong'">
              {{ item.isCorrect ? '✓ 正确' : '✗ 错误' }}
            </view>
          </view>

          <view class="question-content">
            <text>{{ item.content }}</text>
          </view>

          <view class="answer-row">
            <view class="answer-label">你的答案:</view>
            <view class="answer-value user" :class="item.isCorrect ? 'correct' : 'wrong'">
              {{ formatAnswer(item.userAnswer) }}
            </view>
          </view>

          <view v-if="!item.isCorrect" class="answer-row">
            <view class="answer-label">正确答案:</view>
            <view class="answer-value correct-answer">{{ formatAnswer(item.correctAnswer) }}</view>
          </view>

          <view v-if="item.analysis" class="answer-analysis">
            <view class="analysis-label">解析:</view>
            <view class="analysis-content">{{ item.analysis }}</view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作 -->
    <view class="bottom-actions">
      <button class="btn-back" @click="goBack">返回列表</button>
      <button class="btn-again" @click="doAgain">再做一次</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '@/api/index.js'

const recordId = ref('')
const record = ref({})
const paper = ref({})
const answerDetails = ref([])

const isPassed = computed(() => record.value.isPassed === 1 || record.value.isPassed === true)

const loadResult = async () => {
  uni.showLoading({ title: '加载中...' })

  try {
    // 获取考试记录
    const recordRes = await api.request.get(`/api/exam/records/${recordId.value}`)
    record.value = recordRes || {}

    // 获取试卷信息
    if (record.value.paperId) {
      const paperRes = await api.exam.paperDetail(record.value.paperId)
      paper.value = paperRes || {}
    }

    // 获取答题详情
    const detailRes = await api.exam.answerDetail(recordId.value)
    answerDetails.value = detailRes || []
  } catch (e) {
    console.error('加载结果失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const getTypeName = (type) => {
  const map = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    judge: '判断题',
    essay: '简答题'
  }
  return map[type] || type
}

const formatAnswer = (answer) => {
  if (!answer) return '未作答'
  try {
    const list = JSON.parse(answer)
    return Array.isArray(list) ? list.join('、') : answer
  } catch {
    return answer
  }
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}分${secs}秒`
}

const goBack = () => {
  uni.navigateBack()
}

const doAgain = () => {
  if (record.value.paperId) {
    uni.redirectTo({
      url: `/pages/exam/exam-paper?id=${record.value.paperId}`
    })
  }
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || currentPage.$page?.options || {}

  recordId.value = options.id || ''

  if (recordId.value) {
    loadResult()
  }
})
</script>

<style lang="scss" scoped>
.result-container {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 140rpx;
}

.result-header {
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  padding: 60rpx 32rpx 100rpx;
}

.result-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.result-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;

  text {
    font-size: 60rpx;
  }

  &.pass {
    background: rgba(103, 194, 58, 0.2);
  }

  &.fail {
    background: rgba(245, 108, 108, 0.2);
  }
}

.result-status {
  .status-text {
    display: block;
    font-size: 40rpx;
    font-weight: 700;
    color: #fff;
    margin-bottom: 12rpx;
  }

  .paper-title {
    display: block;
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.stats-section {
  margin: -60rpx 32rpx 0;
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
}

.score-display {
  text-align: center;
  padding-bottom: 32rpx;
  border-bottom: 1rpx solid #e4e7ed;
  margin-bottom: 32rpx;
}

.main-score {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.score-value {
  font-size: 96rpx;
  font-weight: 700;
  color: #52b788;
}

.score-unit {
  font-size: 36rpx;
  color: #909399;
  margin-left: 8rpx;
}

.score-info {
  display: flex;
  justify-content: center;
  gap: 32rpx;
  margin-top: 12rpx;

  text {
    font-size: 26rpx;
    color: #909399;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.stat-card {
  text-align: center;
  padding: 20rpx 8rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 8rpx;

  &.correct {
    color: #67c23a;
  }

  &.total {
    color: #409eff;
  }

  &.rate {
    color: #e6a23c;
  }

  &.time {
    color: #909399;
    font-size: 28rpx;
  }
}

.stat-label {
  font-size: 22rpx;
  color: #909399;
}

.detail-section {
  margin: 24rpx 32rpx;
}

.section-header {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #303133;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.question-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  border-left: 6rpx solid #67c23a;

  &.wrong {
    border-left-color: #f56c6c;
  }
}

.question-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.question-number {
  width: 40rpx;
  height: 40rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  color: #606266;
}

.question-type {
  font-size: 22rpx;
  color: #909399;
  flex: 1;
}

.question-result {
  font-size: 24rpx;
  font-weight: 600;
  padding: 4rpx 12rpx;
  border-radius: 6rpx;

  &.correct {
    background: rgba(103, 194, 58, 0.1);
    color: #67c23a;
  }

  &.wrong {
    background: rgba(245, 108, 108, 0.1);
    color: #f56c6c;
  }
}

.question-content {
  font-size: 28rpx;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 16rpx;
  padding: 16rpx;
  background: #f9fafb;
  border-radius: 8rpx;
}

.answer-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.answer-label {
  font-size: 26rpx;
  color: #909399;
  width: 140rpx;
  flex-shrink: 0;
}

.answer-value {
  font-size: 26rpx;
  flex: 1;

  &.user.correct {
    color: #67c23a;
  }

  &.user.wrong {
    color: #f56c6c;
  }

  &.correct-answer {
    color: #67c23a;
    font-weight: 500;
  }
}

.answer-analysis {
  margin-top: 16rpx;
  padding: 16rpx;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 8rpx;
}

.analysis-label {
  font-size: 24rpx;
  color: #409eff;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.analysis-content {
  font-size: 26rpx;
  color: #606266;
  line-height: 1.6;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 24rpx;
  padding: 24rpx 32rpx;
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);

  button {
    flex: 1;
    padding: 24rpx;
    font-size: 30rpx;
    border-radius: 44rpx;
    border: none;
  }

  .btn-back {
    background: #f5f7fa;
    color: #606266;
  }

  .btn-again {
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;
  }
}
</style>
