<template>
  <view class="exam-paper-container">
    <!-- 顶部信息栏 -->
    <view class="exam-header">
      <view class="header-left">
        <text class="exam-title">{{ paper.title }}</text>
      </view>
      <view class="header-right">
        <view class="timer">
          <text class="timer-icon">⏱️</text>
          <text class="timer-value">{{ formatTime(remainingTime) }}</text>
        </view>
      </view>
    </view>

    <!-- 题目进度 -->
    <view class="progress-bar">
      <view class="progress-info">
        <text>第 {{ currentIndex + 1 }} / {{ questions.length }} 题</text>
        <text>已答 {{ answeredCount }} 题</text>
      </view>
      <view class="progress-track">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
    </view>

    <!-- 题目导航 -->
    <scroll-view scroll-x class="question-nav">
      <view class="nav-items">
        <view
          v-for="(q, index) in questions"
          :key="q.id"
          class="nav-item"
          :class="{
            'current': index === currentIndex,
            'answered': userAnswers[q.id],
            'correct': submitted && answers[q.id]?.isCorrect,
            'wrong': submitted && answers[q.id]?.isCorrect === false
          }"
          @click="goToQuestion(index)"
        >
          {{ index + 1 }}
        </view>
      </view>
    </scroll-view>

    <!-- 题目内容 -->
    <scroll-view scroll-y class="question-content">
      <view class="question-card">
        <!-- 题目头部 -->
        <view class="question-header">
          <view class="question-type-tag">
            <text>{{ getTypeName(qCurrent.type) }}</text>
          </view>
          <view class="question-score">
            <text>{{ qCurrent.score }}分</text>
          </view>
        </view>

        <!-- 题目内容 -->
        <view class="question-text">
          <text>{{ qCurrent.content }}</text>
        </view>

        <!-- 选项 -->
        <view v-if="qCurrent.options" class="question-options">
          <view
            v-for="(opt, optIndex) in parseOptions(qCurrent.options)"
            :key="optIndex"
            class="option-item"
            :class="{
              'selected': isOptionSelected(qCurrent, opt.key),
              'correct': submitted && opt.key === getCorrectAnswer(qCurrent),
              'wrong': submitted && isOptionSelected(qCurrent, opt.key) && opt.key !== getCorrectAnswer(qCurrent)
            }"
            @click="selectOption(qCurrent, opt.key)"
          >
            <view class="option-key">
              <text>{{ opt.key }}</text>
            </view>
            <view class="option-content">
              <text>{{ opt.value }}</text>
            </view>
            <view v-if="submitted" class="option-result">
              <text v-if="opt.key === getCorrectAnswer(qCurrent)" class="result-icon correct">✓</text>
              <text v-else-if="isOptionSelected(qCurrent, opt.key)" class="result-icon wrong">✗</text>
            </view>
          </view>
        </view>

        <!-- 判断题 -->
        <view v-else-if="qCurrent.type === 'judge'" class="judge-options">
          <view
            class="judge-item"
            :class="{ 'selected': userAnswers[qCurrent.id] === 'A' }"
            @click="selectOption(qCurrent, 'A')"
          >
            <text class="judge-icon">✓</text>
            <text>正确</text>
          </view>
          <view
            class="judge-item"
            :class="{ 'selected': userAnswers[qCurrent.id] === 'B' }"
            @click="selectOption(qCurrent, 'B')"
          >
            <text class="judge-icon">✗</text>
            <text>错误</text>
          </view>
        </view>

        <!-- 简答题 -->
        <view v-else-if="qCurrent.type === 'essay'" class="essay-input">
          <textarea
            v-model="userAnswers[qCurrent.id]"
            placeholder="请输入答案..."
            class="essay-textarea"
            :disabled="submitted"
          ></textarea>
        </view>

        <!-- 答案解析 -->
        <view v-if="submitted && qCurrent.analysis" class="analysis-section">
          <view class="analysis-header">
            <text>答案解析</text>
          </view>
          <view class="analysis-content">
            <text>{{ qCurrent.analysis }}</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-left">
        <button class="btn-nav" @click="prevQuestion" :disabled="currentIndex === 0">
          <text>上一题</text>
        </button>
      </view>
      <view class="bar-center">
        <button v-if="!submitted" class="btn-submit" @click="confirmSubmit">
          <text>提交答案</text>
        </button>
        <button v-else-if="currentIndex < questions.length - 1" class="btn-next" @click="nextQuestion">
          <text>下一题</text>
        </button>
        <button v-else class="btn-finish" @click="finishExam">
          <text>完成考试</text>
        </button>
      </view>
      <view class="bar-right">
        <button class="btn-nav" @click="nextQuestion" :disabled="currentIndex === questions.length - 1">
          <text>下一题</text>
        </button>
      </view>
    </view>

    <!-- 确认提交弹窗 -->
    <view v-if="showConfirm" class="confirm-overlay" @click="showConfirm = false">
      <view class="confirm-dialog" @click.stop>
        <view class="confirm-header">
          <text>确认提交</text>
        </view>
        <view class="confirm-body">
          <text>您已答 {{ answeredCount }} / {{ questions.length }} 题</text>
          <text v-if="answeredCount < questions.length" class="confirm-warning">
            仍有 {{ questions.length - answeredCount }} 题未答
          </text>
        </view>
        <view class="confirm-footer">
          <button class="btn-cancel" @click="showConfirm = false">取消</button>
          <button class="btn-confirm" @click="submitAnswer">确认提交</button>
        </view>
      </view>
    </view>

    <!-- 交卷确认弹窗 -->
    <view v-if="showFinishConfirm" class="confirm-overlay" @click="showFinishConfirm = false">
      <view class="confirm-dialog" @click.stop>
        <view class="confirm-header">
          <text>确认交卷</text>
        </view>
        <view class="confirm-body">
          <text>您已完成所有答题，确认提交试卷吗？</text>
          <view class="finish-stats">
            <view class="stat-item">
              <text class="stat-label">已答</text>
              <text class="stat-value">{{ answeredCount }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">未答</text>
              <text class="stat-value">{{ questions.length - answeredCount }}</text>
            </view>
            <view class="stat-item">
              <text class="stat-label">用时</text>
              <text class="stat-value">{{ formatTime(elapsedTime) }}</text>
            </view>
          </view>
        </view>
        <view class="confirm-footer">
          <button class="btn-cancel" @click="showFinishConfirm = false">继续答题</button>
          <button class="btn-confirm" @click="submitExam">确认交卷</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { api } from '@/api/index.js'

const paperId = ref('')
const recordId = ref('')
const paper = ref({})
const questions = ref([])
const currentIndex = ref(0)
const userAnswers = ref({})
const answers = ref({})
const submitted = ref(false)
const showConfirm = ref(false)
const showFinishConfirm = ref(false)
const remainingTime = ref(0)
const elapsedTime = ref(0)
let timer = null

const qCurrent = computed(() => questions.value[currentIndex.value] || {})

const answeredCount = computed(() => {
  return Object.keys(userAnswers.value).filter(key => {
    const val = userAnswers.value[key]
    return val !== null && val !== undefined && val !== ''
  }).length
})

const progressPercent = computed(() => {
  if (questions.value.length === 0) return 0
  return (answeredCount.value / questions.value.length) * 100
})

const loadPaperDetail = async () => {
  try {
    const res = await api.exam.paperDetail(paperId.value)
    paper.value = res || {}
    questions.value = res.questions || []
  } catch (e) {
    console.error('加载试卷失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

const loadExamRecord = async () => {
  if (!recordId.value) return
  try {
    const res = await api.exam.answerDetail(recordId.value)
    const answerList = res || []
    answerList.forEach(a => {
      userAnswers.value[a.questionId] = a.userAnswer
      answers.value[a.questionId] = a
    })
    if (answerList.length > 0) {
      submitted.value = true
    }
  } catch (e) {
    console.error('加载答题记录失败', e)
  }
}

const startExam = async () => {
  try {
    const res = await api.exam.start(paperId.value)
    recordId.value = res.recordId
    // 开始计时
    startTimer()
  } catch (e) {
    console.error('开始考试失败', e)
    uni.showToast({ title: '开始考试失败', icon: 'none' })
  }
}

const startTimer = () => {
  const limit = paper.value.timeLimit || 60
  remainingTime.value = limit * 60
  timer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
      elapsedTime.value++
    } else {
      clearInterval(timer)
      uni.showToast({ title: '时间到，自动交卷', icon: 'none' })
      submitExam()
    }
  }, 1000)
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

const parseOptions = (optionsStr) => {
  try {
    return JSON.parse(optionsStr) || []
  } catch {
    return []
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

const isOptionSelected = (question, key) => {
  const answer = userAnswers.value[question.id]
  if (!answer) return false
  try {
    const answerList = JSON.parse(answer)
    return answerList.includes(key)
  } catch {
    return answer === key
  }
}

const selectOption = (question, key) => {
  if (submitted.value) return

  if (question.type === 'multiple_choice') {
    const current = userAnswers.value[question.id] || '[]'
    let list = []
    try {
      list = JSON.parse(current)
    } catch {}
    const index = list.indexOf(key)
    if (index > -1) {
      list.splice(index, 1)
    } else {
      list.push(key)
    }
    userAnswers.value[question.id] = JSON.stringify(list)
  } else {
    userAnswers.value[question.id] = key
  }
}

const getCorrectAnswer = (question) => {
  if (!question.answer) return ''
  try {
    const answerList = JSON.parse(question.answer)
    return question.type === 'multiple_choice' ? answerList : answerList[0]
  } catch {
    return question.answer
  }
}

const goToQuestion = (index) => {
  currentIndex.value = index
}

const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
  }
}

const confirmSubmit = () => {
  showConfirm.value = true
}

const submitAnswer = async () => {
  showConfirm.value = false
  submitted.value = true

  // 显示当前题目的正确答案
  const q = qCurrent.value
  answers.value[q.id] = {
    questionId: q.id,
    userAnswer: userAnswers.value[q.id],
    isCorrect: checkAnswer(q)
  }
}

const checkAnswer = (question) => {
  const userAnswer = userAnswers.value[question.id]
  const correctAnswer = question.answer

  if (!userAnswer || !correctAnswer) return false

  try {
    const userList = JSON.parse(userAnswer)
    const correctList = JSON.parse(correctAnswer)

    if (question.type === 'multiple_choice') {
      userList.sort()
      correctList.sort()
    }
    return JSON.stringify(userList) === JSON.stringify(correctList)
  } catch {
    return userAnswer === correctAnswer.replace(/[\[\]"]/g, '')
  }
}

const finishExam = () => {
  showFinishConfirm.value = true
}

const submitExam = async () => {
  showFinishConfirm.value = false
  clearInterval(timer)

  uni.showLoading({ title: '提交中...' })

  try {
    // 构建答题数据
    const answerList = Object.keys(userAnswers.value).map((qId, index) => {
      const answer = userAnswers.value[qId]
      return {
        questionId: qId,
        userAnswer: answer,
        sortOrder: index + 1,
        answerTime: 0
      }
    })

    await api.exam.submit({
      examRecordId: recordId.value,
      answers: JSON.stringify(answerList)
    })

    uni.hideLoading()
    uni.showToast({ title: '提交成功', icon: 'success' })

    // 跳转到结果页
    setTimeout(() => {
      uni.redirectTo({
        url: `/pages/exam/exam-result?id=${recordId.value}`
      })
    }, 1500)
  } catch (e) {
    uni.hideLoading()
    console.error('提交失败', e)
    uni.showToast({ title: '提交失败', icon: 'none' })
  }
}

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || currentPage.$page?.options || {}

  paperId.value = options.id || ''
  recordId.value = options.recordId || ''

  await loadPaperDetail()
  await loadExamRecord()

  if (!recordId.value && questions.value.length > 0) {
    await startExam()
  } else if (recordId.value) {
    startTimer()
  }
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style lang="scss" scoped>
.exam-paper-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  color: #fff;
}

.exam-title {
  font-size: 30rpx;
  font-weight: 600;
  max-width: 400rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(255, 255, 255, 0.2);
  padding: 12rpx 24rpx;
  border-radius: 30rpx;
}

.timer-value {
  font-size: 32rpx;
  font-weight: 700;
  font-family: monospace;
}

.progress-bar {
  padding: 24rpx 32rpx;
  background: #fff;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #606266;
  margin-bottom: 12rpx;
}

.progress-track {
  height: 8rpx;
  background: #e4e7ed;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #52b788, #2d6a4f);
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.question-nav {
  background: #fff;
  padding: 16rpx 32rpx;
  white-space: nowrap;

  .nav-items {
    display: flex;
    gap: 16rpx;
  }

  .nav-item {
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    border-radius: 12rpx;
    background: #f5f7fa;
    color: #909399;
    flex-shrink: 0;

    &.current {
      background: linear-gradient(135deg, #52b788, #2d6a4f);
      color: #fff;
    }

    &.answered {
      background: rgba(82, 183, 136, 0.15);
      color: #52b788;
    }

    &.correct {
      background: rgba(103, 194, 58, 0.15);
      color: #67c23a;
    }

    &.wrong {
      background: rgba(245, 108, 108, 0.15);
      color: #f56c6c;
    }
  }
}

.question-content {
  flex: 1;
  padding: 24rpx 32rpx;
  padding-bottom: 140rpx;
}

.question-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.question-type-tag {
  padding: 6rpx 16rpx;
  background: rgba(82, 183, 136, 0.1);
  color: #52b788;
  font-size: 24rpx;
  border-radius: 8rpx;
}

.question-score {
  font-size: 26rpx;
  color: #909399;
}

.question-text {
  font-size: 32rpx;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 32rpx;
}

.question-options {
  .option-item {
    display: flex;
    align-items: center;
    padding: 24rpx;
    background: #f5f7fa;
    border-radius: 16rpx;
    margin-bottom: 16rpx;
    border: 2rpx solid transparent;

    &.selected {
      background: rgba(82, 183, 136, 0.1);
      border-color: #52b788;
    }

    &.correct {
      background: rgba(103, 194, 58, 0.1);
      border-color: #67c23a;
    }

    &.wrong {
      background: rgba(245, 108, 108, 0.1);
      border-color: #f56c6c;
    }
  }

  .option-key {
    width: 48rpx;
    height: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    border-radius: 50%;
    font-size: 26rpx;
    font-weight: 600;
    color: #606266;
    margin-right: 20rpx;
    flex-shrink: 0;

    .selected & {
      background: #52b788;
      color: #fff;
    }

    .correct & {
      background: #67c23a;
      color: #fff;
    }

    .wrong & {
      background: #f56c6c;
      color: #fff;
    }
  }

  .option-content {
    flex: 1;
    font-size: 28rpx;
    color: #303133;
  }

  .option-result {
    margin-left: 16rpx;
  }

  .result-icon {
    font-size: 32rpx;
    font-weight: 700;

    &.correct {
      color: #67c23a;
    }

    &.wrong {
      color: #f56c6c;
    }
  }
}

.judge-options {
  display: flex;
  gap: 24rpx;

  .judge-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40rpx;
    background: #f5f7fa;
    border-radius: 16rpx;
    border: 2rpx solid transparent;

    &.selected {
      background: rgba(82, 183, 136, 0.1);
      border-color: #52b788;
    }

    .judge-icon {
      font-size: 48rpx;
      margin-bottom: 12rpx;
    }

    text {
      font-size: 28rpx;
      color: #303133;
    }
  }
}

.essay-input {
  .essay-textarea {
    width: 100%;
    min-height: 200rpx;
    padding: 24rpx;
    background: #f5f7fa;
    border-radius: 16rpx;
    font-size: 28rpx;
    color: #303133;
    box-sizing: border-box;
  }
}

.analysis-section {
  margin-top: 32rpx;
  padding: 24rpx;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 12rpx;
  border-left: 4rpx solid #409eff;

  .analysis-header {
    font-size: 28rpx;
    font-weight: 600;
    color: #409eff;
    margin-bottom: 12rpx;
  }

  .analysis-content {
    font-size: 26rpx;
    color: #606266;
    line-height: 1.6;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);

  .btn-nav {
    padding: 16rpx 32rpx;
    background: #f5f7fa;
    color: #606266;
    font-size: 28rpx;
    border-radius: 40rpx;
    border: none;

    &[disabled] {
      opacity: 0.5;
    }
  }

  .btn-submit {
    padding: 20rpx 48rpx;
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;
    font-size: 30rpx;
    border-radius: 40rpx;
    border: none;
  }

  .btn-next {
    padding: 20rpx 48rpx;
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;
    font-size: 30rpx;
    border-radius: 40rpx;
    border: none;
  }

  .btn-finish {
    padding: 20rpx 48rpx;
    background: linear-gradient(135deg, #e6a23c, #f56c6c);
    color: #fff;
    font-size: 30rpx;
    border-radius: 40rpx;
    border: none;
  }
}

.confirm-overlay {
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

.confirm-dialog {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.confirm-header {
  padding: 32rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  color: #303133;
  border-bottom: 1rpx solid #e4e7ed;
}

.confirm-body {
  padding: 32rpx;
  text-align: center;

  text {
    display: block;
    font-size: 28rpx;
    color: #606266;
  }

  .confirm-warning {
    color: #f56c6c;
    margin-top: 12rpx;
  }
}

.finish-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #e4e7ed;

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .stat-label {
    font-size: 24rpx;
    color: #909399;
  }

  .stat-value {
    font-size: 36rpx;
    font-weight: 700;
    color: #52b788;
    margin-top: 8rpx;
  }
}

.confirm-footer {
  display: flex;
  padding: 24rpx 32rpx;
  gap: 24rpx;

  button {
    flex: 1;
    padding: 24rpx;
    font-size: 30rpx;
    border-radius: 12rpx;
    border: none;
  }

  .btn-cancel {
    background: #f5f7fa;
    color: #606266;
  }

  .btn-confirm {
    background: linear-gradient(135deg, #52b788, #2d6a4f);
    color: #fff;
  }
}
</style>
