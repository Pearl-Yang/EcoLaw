<template>
  <view class="page">
    <!-- 顶部区域：白底卡片，不再用深绿渐变 -->
    <view class="head-card">
      <view class="h-header-row">
        <text class="h-title">学习中心</text>
        <view class="h-stats-mini">
          <view class="stat-dot">
            <view class="dot dot-green" />
            <text class="dot-label">{{ learningStats.completed }} 已完成</text>
          </view>
          <view class="stat-dot">
            <view class="dot dot-yellow" />
            <text class="dot-label">{{ learningStats.inProgress }} 进行中</text>
          </view>
        </view>
      </view>

      <!-- 学习进度 -->
      <view class="h-progress">
        <view class="progress-circle-wrap">
          <view class="progress-ring">
            <view class="progress-ring-fill" :style="{ '--pct': monthlyProgress + '%' }" />
            <text class="progress-percent">{{ monthlyProgress }}%</text>
          </view>
        </view>
        <view class="progress-info">
          <text class="progress-title">本月学习进度</text>
          <text class="progress-sub">本月已学习 {{ monthlyHours }}/{{ targetHours }} 小时</text>
          <view class="progress-bar-wrap">
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: monthlyProgress + '%' }" />
            </view>
          </view>
        </view>
      </view>

      <!-- 统计横条 -->
      <view class="h-stats">
        <view class="stat-item">
          <text class="stat-num">{{ learningStats.completed }}</text>
          <text class="stat-label">已完成</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-num">{{ learningStats.inProgress }}</text>
          <text class="stat-label">进行中</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-num">{{ learningStats.totalHours }}</text>
          <text class="stat-label">累计学时</text>
        </view>
        <view class="stat-divider" />
        <view class="stat-item">
          <text class="stat-num stat-primary">{{ learningStats.points }}</text>
          <text class="stat-label">学习积分</text>
        </view>
      </view>
    </view>

    <!-- Tab切换 -->
    <view class="tab-bar">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <text class="tab-text">{{ tab.name }}</text>
        <view v-if="tab.badge && tab.badge > 0" class="tab-badge">{{ tab.badge }}</view>
      </view>
    </view>

    <!-- 宠物养成入口 -->
    <view class="pet-breed-entry" @click="goToPetBreed">
      <view class="pet-breed-bg">
        <view class="pet-breed-icon">
          <text class="pet-emoji">🌱</text>
        </view>
        <view class="pet-breed-info">
          <text class="pet-breed-title">绿法生态岛</text>
          <text class="pet-breed-desc">养宠物·学环保·做任务</text>
        </view>
        <view class="pet-breed-arrow">
          <text class="arrow-icon">›</text>
        </view>
      </view>
      <view class="pet-breed-stats">
        <view class="pet-stat-item">
          <text class="pet-stat-num">{{ petStats.level || 1 }}</text>
          <text class="pet-stat-label">等级</text>
        </view>
        <view class="pet-stat-divider"></view>
        <view class="pet-stat-item">
          <text class="pet-stat-num">{{ petStats.exp || 0 }}</text>
          <text class="pet-stat-label">经验</text>
        </view>
        <view class="pet-stat-divider"></view>
        <view class="pet-stat-item">
          <text class="pet-stat-num">{{ petStats.tasks || 0 }}</text>
          <text class="pet-stat-label">任务</text>
        </view>
      </view>
    </view>

    <!-- 学习中 Tab -->
    <view v-if="activeTab === 'learning'" class="tab-content">
      <!-- 继续学习 -->
      <view v-if="continueLearn.length > 0" class="section">
        <view class="section-head">
          <text class="section-title">继续学习</text>
        </view>
        <view
          v-for="course in continueLearn"
          :key="course.id"
          class="course-card continue-card"
          @click="goCourseDetail(course)"
        >
          <view class="course-cover" :style="{ background: course.coverGradient }">
            <LineIcon name="book-open" :size="36" color="#ffffff" />
            <view class="continue-tag">
              <text class="continue-tag-text">继续</text>
            </view>
          </view>
          <view class="course-info">
            <text class="course-title">{{ course.title }}</text>
            <text class="course-chapter">第{{ course.currentChapter }}章 共{{ course.totalChapter }}章</text>
            <view class="course-progress">
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: course.progress + '%' }" />
              </view>
              <text class="progress-text">{{ course.progress }}%</text>
            </view>
            <view class="course-action">
              <text class="action-btn primary">继续学习</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 视频学习任务 -->
      <view v-if="videoTaskList.length > 0" class="section video-task-section">
        <view class="section-head">
          <text class="section-title">视频学习任务</text>
          <view class="section-more-btn" @click="openFeaturedVideo">
            <LineIcon name="play" :size="24" color="#22c55e" />
            <text class="section-more">播放</text>
          </view>
        </view>
        <view class="video-feature-row">
          <view class="video-main" @click="openFeaturedVideo">
            <image
              v-if="featuredVideo.coverUrl"
              class="video-main-cover"
              mode="aspectFill"
              :src="featuredVideo.coverUrl"
            />
            <view
              v-else
              class="video-main-cover video-main-fallback"
              :style="{ background: featuredVideo.coverGradient }"
            >
              <LineIcon name="video" :size="56" color="#ffffff" />
            </view>
            <view class="video-main-dim" />
            <view class="video-play-circle">
              <LineIcon name="play" :size="36" color="#ffffff" />
            </view>
            <view class="video-cover-stats">
              <view class="cover-stat-row">
                <LineIcon name="eye" :size="20" color="#ffffff" />
                <text class="cover-stat">{{ featuredVideo.views }}</text>
              </view>
              <view class="cover-stat-row">
                <LineIcon name="heart" :size="20" color="#ffffff" />
                <text class="cover-stat">{{ featuredVideo.likes }}</text>
              </view>
            </view>
          </view>
          <view class="video-thumb-grid">
            <view
              v-for="slot in sideVideos"
              :key="slot.item.id"
              class="video-thumb-cell"
              @click="selectVideoTask(slot.index)"
            >
              <view class="video-thumb-img-wrap">
                <image
                  v-if="slot.item.coverUrl"
                  class="video-thumb-img"
                  mode="aspectFill"
                  :src="slot.item.coverUrl"
                />
                <view
                  v-else
                  class="video-thumb-img video-thumb-fallback"
                  :style="{ background: slot.item.coverGradient }"
                >
                  <LineIcon name="video" :size="28" color="#ffffff" />
                </view>
                <view class="video-thumb-stats">
                  <LineIcon name="eye" :size="16" color="#ffffff" />
                  <text class="thumb-stat">{{ slot.item.views }}</text>
                </view>
              </view>
              <view class="video-thumb-title-row">
                <text class="video-thumb-title">{{ slot.item.title }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 推荐课程 -->
      <view class="section">
        <view class="section-head">
          <text class="section-title">推荐课程</text>
          <text class="section-more" @click="switchCategory('all')">更多 ›</text>
        </view>
        <scroll-view class="course-scroll" scroll-x>
          <view
            v-for="course in recommendCourses"
            :key="course.id"
            class="course-mini-card"
            @click="goCourseDetail(course)"
          >
            <view class="mini-cover" :style="{ background: course.coverGradient }">
              <LineIcon name="book-open" :size="36" color="#ffffff" />
            </view>
            <text class="mini-title">{{ course.title }}</text>
            <view class="mini-meta">
              <view class="meta-item">
                <LineIcon name="clock" :size="18" color="#9ca3af" />
                <text class="mini-duration">{{ course.duration }}分钟</text>
              </view>
              <view class="meta-item">
                <LineIcon name="star" :size="18" color="#eab308" />
                <text class="mini-rating">{{ course.rating }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 分类标签 -->
      <view class="section">
        <view class="section-head">
          <text class="section-title">全部课程</text>
        </view>
        <view class="category-tabs">
          <view
            v-for="cat in categories"
            :key="cat.id"
            class="tab-tag"
            :class="{ active: activeCategory === cat.id }"
            @click="switchCategory(cat.id)"
          >
            <text>{{ cat.name }}</text>
          </view>
        </view>
      </view>

      <!-- 课程列表 -->
      <view class="course-list">
        <view v-if="loading" class="loading-state">
          <text>加载中...</text>
        </view>
        <view v-else-if="courses.length === 0" class="empty-state">
          <LineIcon name="book-open" :size="64" color="#d1d5db" />
          <text class="empty-text">暂无相关课程</text>
        </view>
        <view
          v-else
          v-for="course in courses"
          :key="course.id"
          class="course-card"
          @click="goCourseDetail(course)"
        >
          <view class="course-cover" :style="{ background: course.coverGradient }">
            <LineIcon name="book-open" :size="36" color="#ffffff" />
          </view>
          <view class="course-info">
            <text class="course-title">{{ course.title }}</text>
            <text class="course-desc">{{ course.description }}</text>
            <view class="course-meta">
              <view class="meta-item">
                <LineIcon name="clock" :size="20" color="#9ca3af" />
                <text class="meta-text">{{ course.duration }}分钟</text>
              </view>
              <view class="meta-item">
                <LineIcon name="award" :size="20" color="#9ca3af" />
                <text class="meta-text">{{ course.points }}积分</text>
              </view>
              <view class="meta-tag" :class="course.difficulty">{{ course.difficultyText }}</view>
            </view>
            <view v-if="course.progress > 0" class="course-progress">
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: course.progress + '%' }" />
              </view>
              <text class="progress-text">{{ course.progress }}%</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 已完成 Tab -->
    <view v-if="activeTab === 'completed'" class="tab-content">
      <view class="course-list">
        <view v-if="completedCourses.length === 0" class="empty-state">
          <LineIcon name="graduation-cap" :size="64" color="#d1d5db" />
          <text class="empty-text">暂无已完成的课程</text>
        </view>
        <view
          v-else
          v-for="course in completedCourses"
          :key="course.id"
          class="course-card completed-card"
          @click="goCourseDetail(course)"
        >
          <view class="course-cover" :style="{ background: course.coverGradient }">
            <LineIcon name="book-open" :size="36" color="#ffffff" />
            <view class="completed-badge">
              <LineIcon name="check-circle" :size="24" color="#ffffff" />
            </view>
          </view>
          <view class="course-info">
            <text class="course-title">{{ course.title }}</text>
            <view class="completed-info">
              <text class="completed-date">完成于 {{ course.completedDate }}</text>
              <text class="completed-score">得分: {{ course.score }}分</text>
            </view>
            <view class="course-meta">
              <view class="meta-item">
                <LineIcon name="clock" :size="20" color="#9ca3af" />
                <text class="meta-text">{{ course.duration }}分钟</text>
              </view>
              <view class="meta-item">
                <LineIcon name="award" :size="20" color="#9ca3af" />
                <text class="meta-text">+{{ course.points }}积分</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 我的考试 Tab -->
    <view v-if="activeTab === 'exam'" class="tab-content">
      <view class="exam-list">
        <!-- 待参加考试 -->
        <view class="exam-section">
          <view class="section-head">
            <text class="section-title">待参加考试</text>
          </view>
          <view v-if="pendingExams.length === 0" class="empty-state small">
            <text class="empty-text">暂无待参加的考试</text>
          </view>
          <view
            v-else
            v-for="exam in pendingExams"
            :key="exam.id"
            class="exam-card pending"
            @click="goExam(exam)"
          >
            <view class="exam-icon-wrap">
              <LineIcon name="edit" :size="36" color="#22c55e" />
            </view>
            <view class="exam-info">
              <text class="exam-title">{{ exam.title }}</text>
              <view class="exam-meta">
                <LineIcon name="clock" :size="18" color="#9ca3af" />
                <text class="exam-time">{{ exam.examTime }}</text>
              </view>
              <view class="exam-meta">
                <LineIcon name="award" :size="18" color="#9ca3af" />
                <text class="exam-duration">{{ exam.duration }}分钟</text>
              </view>
            </view>
            <view class="exam-action">
              <text class="action-btn primary">进入考试</text>
            </view>
          </view>
        </view>

        <!-- 已完成考试 -->
        <view class="exam-section">
          <view class="section-head">
            <text class="section-title">已完成考试</text>
          </view>
          <view v-if="completedExams.length === 0" class="empty-state small">
            <text class="empty-text">暂无已完成记录</text>
          </view>
          <view
            v-else
            v-for="exam in completedExams"
            :key="exam.id"
            class="exam-card completed"
            @click="goExamDetail(exam)"
          >
            <view class="exam-icon-wrap">
              <LineIcon name="check-circle" :size="36" color="#22c55e" />
            </view>
            <view class="exam-info">
              <text class="exam-title">{{ exam.title }}</text>
              <view class="exam-result">
                <text class="exam-score">得分: {{ exam.score }}分</text>
                <text class="exam-status passed">通过</text>
              </view>
              <view class="exam-meta">
                <LineIcon name="clock" :size="18" color="#9ca3af" />
                <text class="exam-time">完成: {{ exam.completedTime }}</text>
              </view>
            </view>
            <view class="exam-action">
              <text class="action-btn">查看详情</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { onLoad, onPullDownRefresh } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'
import { videoTasks as videoTasksSeed } from '../../data/videoTasks.js'
import LineIcon from '../../components/LineIcon.vue'

const loading = ref(false)
const activeTab = ref('learning')
const activeCategory = ref('all')

// 宠物养成数据
const petStats = ref({
  level: 1,
  exp: 0,
  tasks: 0
})

const learningStats = ref({
  completed: 5,
  inProgress: 3,
  totalHours: 68,
  points: 1250
})

// 月度学习进度
const monthlyHours = ref(12)
const targetHours = ref(18)
const monthlyProgress = computed(() => Math.round((monthlyHours.value / targetHours.value) * 100))

const tabs = ref([
  { key: 'learning', name: '学习中', badge: 3 },
  { key: 'completed', name: '已完成', badge: 0 },
  { key: 'exam', name: '我的考试', badge: 2 }
])

const categories = [
  { id: 'all', name: '全部' },
  { id: 'law', name: '法规学习' },
  { id: 'compliance', name: '企业合规' },
  { id: 'science', name: '环保科普' },
  { id: 'exam', name: '在线考试' }
]

const courses = reactive([...getMockCourses()])
const continueLearn = reactive([...getMockContinueLearn()])
const videoTaskList = reactive([...videoTasksSeed])
const selectedVideoIndex = ref(0)
const featuredVideo = computed(() => {
  const list = videoTaskList
  if (!list.length) return { id: '', title: '', views: 0, likes: 0, coverUrl: '', coverGradient: '', emoji: '🎬', videoUrl: '' }
  const i = Math.min(Math.max(0, selectedVideoIndex.value), list.length - 1)
  return list[i]
})
const sideVideos = computed(() => {
  const list = videoTaskList
  const i0 = Math.min(Math.max(0, selectedVideoIndex.value), Math.max(0, list.length - 1))
  return list
    .map((item, index) => ({ item, index }))
    .filter(({ index }) => index !== i0)
    .slice(0, 6)
})
const recommendCourses = reactive([...getMockRecommendCourses()])
const completedCourses = reactive([])
const pendingExams = reactive([])
const completedExams = reactive([])

onLoad((query) => {
  if (query.courseId) {
    uni.redirectTo({
      url: `/pages/learn/course-detail?id=${encodeURIComponent(query.courseId)}`
    })
  }
})

onMounted(() => {
  loadData()
})

onPullDownRefresh(() => {
  loadData().finally(() => {
    uni.stopPullDownRefresh()
  })
})

async function loadData() {
  loading.value = true
  try {
    await Promise.all([
      loadCourses(),
      loadLearningStats(),
      loadContinueLearn(),
      loadVideoTasks(),
      loadRecommendCourses(),
      loadCompletedCourses(),
      loadExams()
    ])
  } catch (error) {
    console.error('加载数据失败:', error)
    loadMockData()
  } finally {
    loading.value = false
  }
}

function trainingListParams() {
  const cat = activeCategory.value
  const base = { page: 1, pageSize: 50 }
  if (cat === 'exam') return { ...base, type: 'exam' }
  if (cat === 'law') return { ...base, keyword: '法' }
  if (cat === 'compliance') return { ...base, keyword: '合规' }
  if (cat === 'science') return { ...base, keyword: '污染' }
  return base
}

function pickTrainingGradient(seed) {
  const gradients = [
    'linear-gradient(135deg, #ffffff 0%, #d1fae5 60%, #86efac 100%)',
    'linear-gradient(135deg, #ffffff 0%, #fef9c3 55%, #fde047 100%)',
    'linear-gradient(135deg, #ffffff 0%, #f3e8ff 50%, #d946ef 100%)',
    'linear-gradient(135deg, #ffffff 0%, #e0e7ff 50%, #818cf8 100%)',
    'linear-gradient(135deg, #ffffff 0%, #fce7f3 50%, #f472b6 100%)'
  ]
  let h = 0
  const s = String(seed || '')
  for (let i = 0; i < s.length; i++) h = (h + s.charCodeAt(i) * (i + 1)) % 997
  return gradients[h % gradients.length]
}

function normalizeTraining(t) {
  const content = t.content || ''
  return {
    id: String(t.id),
    title: t.title || '培训课程',
    description: content
      ? content.length > 80
        ? content.slice(0, 80) + '…'
        : content
      : '点击进入学习详情',
    emoji: t.type === 'exam' ? '📝' : t.type === 'document' ? '📄' : '🎬',
    coverGradient: pickTrainingGradient(t.id),
    duration: t.duration != null ? t.duration : 30,
    points: Math.min(120, Math.max(20, (t.duration || 30) * 2)),
    difficulty: 'medium',
    difficultyText: '标准',
    progress: 0,
    coverUrl: t.coverUrl || '',
    videoUrl: t.videoUrl || '',
    type: t.type || 'video'
  }
}

async function loadLearningStats() {
  try {
    const s = await api.training.statistics(undefined, { showLoading: false })
    if (s && typeof s === 'object') {
      if (s.completed != null) learningStats.value.completed = Number(s.completed) || learningStats.value.completed
      if (s.inProgress != null) learningStats.value.inProgress = Number(s.inProgress) || learningStats.value.inProgress
      if (s.total != null) {
        const t = Number(s.total)
        if (!Number.isNaN(t) && t > 0) learningStats.value.totalHours = Math.min(999, t * 2)
      }
    }
  } catch (_) {
    /* 保持默认示例数据 */
  }
}

async function loadCourses() {
  try {
    const res = await api.training.list(trainingListParams(), { showLoading: false })
    let list = []
    if (Array.isArray(res)) list = res
    else if (res && Array.isArray(res.list)) list = res.list
    courses.length = 0
    if (list.length > 0) {
      courses.push(...list.map(normalizeTraining))
    } else {
      courses.push(...getMockCourses())
    }
  } catch (error) {
    console.error('加载课程失败:', error)
    if (courses.length === 0) {
      courses.push(...getMockCourses())
    }
  }
}

async function loadContinueLearn() {
  continueLearn.length = 0
  continueLearn.push(...getMockContinueLearn())
}

async function loadVideoTasks() {
  videoTaskList.length = 0
  videoTaskList.push(...videoTasksSeed)
  if (selectedVideoIndex.value >= videoTaskList.length) {
    selectedVideoIndex.value = 0
  }
}

async function loadRecommendCourses() {
  recommendCourses.length = 0
  recommendCourses.push(...getMockRecommendCourses())
}

async function loadCompletedCourses() {
  completedCourses.length = 0
  completedCourses.push(...getMockCompletedCourses())
}

async function loadExams() {
  pendingExams.length = 0
  completedExams.length = 0
  pendingExams.push(...getMockPendingExams())
  completedExams.push(...getMockCompletedExams())
}

function loadMockData() {
  if (courses.length === 0) courses.push(...getMockCourses())
  if (continueLearn.length === 0) continueLearn.push(...getMockContinueLearn())
  if (videoTaskList.length === 0) loadVideoTasks()
  if (recommendCourses.length === 0) recommendCourses.push(...getMockRecommendCourses())
  if (completedCourses.length === 0) completedCourses.push(...getMockCompletedCourses())
  if (pendingExams.length === 0) pendingExams.push(...getMockPendingExams())
  if (completedExams.length === 0) completedExams.push(...getMockCompletedExams())
}

function getMockCourses() {
  return [
    {
      id: '1',
      title: '《固体废物污染环境防治法》要点解读',
      description: '深度解析固废法中关于塑料污染治理的核心条款',
      emoji: '📖',
      coverGradient: 'linear-gradient(135deg, #28a745, #1a4d2e)',
      duration: 45,
      points: 50,
      difficulty: 'medium',
      difficultyText: '中等',
      progress: 60,
      currentChapter: 3,
      totalChapter: 5
    },
    {
      id: '2',
      title: '企业塑料制品合规指南',
      description: '面向生产企业的塑料污染防治合规要求与实操',
      emoji: '🏭',
      coverGradient: 'linear-gradient(135deg, #0077b6, #48cae4)',
      duration: 60,
      points: 80,
      difficulty: 'hard',
      difficultyText: '进阶',
      progress: 0
    },
    {
      id: '3',
      title: '白色污染治理基础知识',
      description: '了解白色污染的来源、危害与防治措施',
      emoji: '🌿',
      coverGradient: 'linear-gradient(135deg, #40916c, #95d5b2)',
      duration: 30,
      points: 30,
      difficulty: 'easy',
      difficultyText: '入门',
      progress: 100
    }
  ]
}

function getMockContinueLearn() {
  return [
    {
      id: '1',
      title: '《固体废物污染环境防治法》要点解读',
      emoji: '📖',
      coverGradient: 'linear-gradient(135deg, #28a745, #1a4d2e)',
      currentChapter: 3,
      totalChapter: 5,
      progress: 60
    },
    {
      id: '4',
      title: '企业环保合规培训',
      emoji: '🏭',
      coverGradient: 'linear-gradient(135deg, #0077b6, #48cae4)',
      currentChapter: 1,
      totalChapter: 8,
      progress: 12
    }
  ]
}

function getMockRecommendCourses() {
  return [
    {
      id: '5',
      title: '法规解读',
      emoji: '📚',
      coverGradient: 'linear-gradient(135deg, #e76f51, #f4a261)',
      duration: 45,
      rating: 4.8
    },
    {
      id: '6',
      title: '案例分析',
      emoji: '📋',
      coverGradient: 'linear-gradient(135deg, #6c5ce7, #a29bfe)',
      duration: 30,
      rating: 4.6
    },
    {
      id: '7',
      title: '农膜回收',
      emoji: '🌾',
      coverGradient: 'linear-gradient(135deg, #d4a373, #e9c46a)',
      duration: 35,
      rating: 4.7
    },
    {
      id: '8',
      title: '塑料限制政策',
      emoji: '📜',
      coverGradient: 'linear-gradient(135deg, #00b894, #55efc4)',
      duration: 40,
      rating: 4.5
    }
  ]
}

function getMockCompletedCourses() {
  return [
    {
      id: '3',
      title: '白色污染治理基础知识',
      emoji: '🌿',
      coverGradient: 'linear-gradient(135deg, #40916c, #95d5b2)',
      duration: 30,
      points: 30,
      score: 95,
      completedDate: '2026-03-28'
    }
  ]
}

function getMockPendingExams() {
  return [
    {
      id: '1',
      title: '企业合规考核',
      examTime: '2026-04-15 14:00',
      duration: 60
    }
  ]
}

function getMockCompletedExams() {
  return [
    {
      id: '2',
      title: '普法知识测试',
      score: 85,
      completedTime: '2026-03-20 15:30',
      duration: 30
    }
  ]
}

function switchTab(key) {
  activeTab.value = key
}

function goToPetBreed() {
  uni.navigateTo({
    url: '/pages/breed/index'
  })
}

function switchCategory(id) {
  activeCategory.value = id
  loadCourses()
}

function selectVideoTask(index) {
  selectedVideoIndex.value = index
}

function openFeaturedVideo() {
  const v = featuredVideo.value
  if (!v || !v.id) return
  uni.navigateTo({ url: `/pages/learn/video-play?id=${encodeURIComponent(v.id)}` })
}

function shareVideoTitle(item) {
  uni.setClipboardData({
    data: item.title,
    success: () => uni.showToast({ title: '标题已复制', icon: 'none' })
  })
}

function goCourseDetail(course) {
  uni.navigateTo({
    url: `/pages/learn/course-detail?id=${encodeURIComponent(course.id)}`
  })
}

function goExam(exam) {
  uni.showToast({ title: '进入考试页面待开发', icon: 'none' })
}

function goExamDetail(exam) {
  uni.showToast({ title: '考试详情待开发', icon: 'none' })
}
</script>

<style lang="scss" scoped>
$bg: #f5f6f8;
$text-primary: #1f2937;
$text-secondary: #4b5563;
$primary: #22c55e;
$primary-dark: #15803d;
$mint: #ecfdf5;

.page {
  min-height: 100vh;
  background: $bg;
  padding: 0 24rpx 48rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.head-card {
  background: #ffffff;
  border-radius: 0 0 24rpx 24rpx;
  padding: 28rpx 28rpx 32rpx;
  margin: 0 -24rpx 24rpx;
  border-bottom: 1rpx solid #e5e7eb;
}

.h-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.h-title {
  font-size: 40rpx;
  font-weight: 700;
  color: $text-primary;
  display: block;
}

.h-stats-mini {
  display: flex;
  gap: 20rpx;
}

.stat-dot {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
}

.dot-green { background: $primary; }
.dot-yellow { background: #eab308; }

.dot-label {
  font-size: 22rpx;
  color: $text-secondary;
}

.h-progress {
  display: flex;
  gap: 24rpx;
  align-items: center;
  margin-bottom: 24rpx;
  padding: 20rpx;
  background: #f9fafb;
  border-radius: 16rpx;
  border: 1rpx solid #e5e7eb;
}

.progress-circle-wrap {
  flex-shrink: 0;
}

.progress-ring {
  position: relative;
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  border: 8rpx solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-ring-fill {
  position: absolute;
  top: -8rpx;
  left: -8rpx;
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  border: 8rpx solid $primary;
  clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 0% 100%, 0% 0%, 50% 0%);
  transform: rotate(-90deg);
  transform-origin: center;
}

.progress-percent {
  font-size: 28rpx;
  font-weight: 700;
  color: $primary;
  z-index: 1;
}

.progress-info {
  flex: 1;
}

.progress-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-primary;
  display: block;
  margin-bottom: 6rpx;
}

.progress-sub {
  font-size: 24rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 12rpx;
}

.progress-bar-wrap {
  width: 100%;
}

.progress-bar {
  height: 8rpx;
  background: #e5e7eb;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, $primary, #86efac);
  border-radius: 4rpx;
}

.h-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-divider {
  width: 1rpx;
  height: 40rpx;
  background: #e5e7eb;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-num {
  font-size: 32rpx;
  font-weight: 700;
  display: block;
  color: $text-primary;
}

.stat-num.stat-primary {
  color: $primary;
}

.stat-label {
  font-size: 22rpx;
  color: $text-secondary;
}

/* 宠物养成入口 */
.pet-breed-entry {
  margin: 24rpx 32rpx;
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(16, 185, 129, 0.3);
}

.pet-breed-bg {
  display: flex;
  align-items: center;
}

.pet-breed-icon {
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.pet-emoji {
  font-size: 56rpx;
}

.pet-breed-info {
  flex: 1;
}

.pet-breed-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8rpx;
}

.pet-breed-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}

.pet-breed-arrow {
  width: 48rpx;
  height: 48rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.arrow-icon {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: bold;
}

.pet-breed-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.2);
}

.pet-stat-item {
  text-align: center;
}

.pet-stat-num {
  font-size: 32rpx;
  font-weight: 700;
  color: #ffffff;
}

.pet-stat-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-left: 4rpx;
}

.pet-stat-divider {
  width: 1rpx;
  height: 40rpx;
  background: rgba(255, 255, 255, 0.2);
}

.tab-bar {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 8rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.03);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  position: relative;
}

.tab-item.active {
  background: linear-gradient(135deg, #ffffff 0%, $mint 60%);
  border: 1rpx solid rgba(34, 197, 94, 0.4);
  color: $primary-dark;
}

.tab-text {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-secondary;
}

.tab-item.active .tab-text {
  color: $primary-dark;
  font-weight: 700;
}

.tab-badge {
  position: absolute;
  top: 8rpx;
  right: 16rpx;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  background: $primary;
  color: #fff;
  font-size: 20rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-content {
  // Tab content area
}

.section {
  margin-bottom: 32rpx;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1a4d2e;
}

.section-more {
  font-size: 24rpx;
  color: $primary;
}

.video-task-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx 20rpx 28rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 14rpx rgba(0, 0, 0, 0.04);
}

.video-feature-row {
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
}

.video-main {
  position: relative;
  width: 48%;
  flex-shrink: 0;
  height: 340rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #1a1a1a;
}

.video-main-cover {
  width: 100%;
  height: 100%;
}

.video-main-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-main-emoji {
  font-size: 80rpx;
}

.video-main-dim {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 50%;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.75));
  pointer-events: none;
}

.video-play-circle {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.25);
}

.video-play-tri {
  font-size: 36rpx;
  color: #1a4d2e;
  margin-left: 6rpx;
}

.video-cover-stats {
  position: absolute;
  left: 12rpx;
  right: 12rpx;
  bottom: 12rpx;
  display: flex;
  gap: 20rpx;
  z-index: 2;
}

.cover-stat {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.95);
  text-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.5);
}

.video-thumb-grid {
  flex: 1;
  min-width: 0;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12rpx 10rpx;
}

.video-thumb-cell {
  min-width: 0;
}

.video-thumb-img-wrap {
  position: relative;
  width: 100%;
  height: 96rpx;
  border-radius: 8rpx;
  overflow: hidden;
  background: #eee;
}

.video-thumb-img {
  width: 100%;
  height: 100%;
}

.video-thumb-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-thumb-emoji {
  font-size: 40rpx;
}

.video-thumb-stats {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 6rpx 8rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.72));
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.thumb-stat {
  font-size: 18rpx;
  color: rgba(255, 255, 255, 0.95);
}

.video-thumb-title-row {
  display: flex;
  align-items: flex-start;
  gap: 6rpx;
  margin-top: 6rpx;
}

.video-thumb-title {
  flex: 1;
  min-width: 0;
  font-size: 20rpx;
  color: #333;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.video-thumb-share {
  flex-shrink: 0;
  font-size: 22rpx;
  opacity: 0.7;
  padding: 2rpx;
}

.course-scroll {
  white-space: nowrap;
  width: 100%;
}

.course-mini-card {
  display: inline-block;
  width: 240rpx;
  margin-right: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  vertical-align: top;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.mini-cover {
  height: 140rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mini-emoji {
  font-size: 56rpx;
}

.mini-title {
  font-size: 26rpx;
  font-weight: 600;
  color: #333;
  display: block;
  padding: 16rpx 16rpx 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mini-meta {
  display: flex;
  justify-content: space-between;
  padding: 0 16rpx 16rpx;
}

.mini-duration,
.mini-rating {
  font-size: 22rpx;
  color: #888;
}

.mini-rating {
  color: #f59e0b;
}

.category-tabs {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
  margin-bottom: 20rpx;
}

.tab-tag {
  padding: 12rpx 24rpx;
  background: #fff;
  border-radius: 999rpx;
  font-size: 26rpx;
  color: #666;
  border: 2rpx solid #e8f5eb;
}

.tab-tag.active {
  background: $primary;
  color: #fff;
  border-color: $primary;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.course-card {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  display: flex;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.course-cover {
  width: 180rpx;
  min-height: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.course-emoji {
  font-size: 64rpx;
}

.continue-card .course-cover {
  min-height: 180rpx;
}

.continue-tag {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  background: $primary;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.continue-tag-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: 600;
}

.completed-badge {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 48rpx;
  height: 48rpx;
  background: $primary;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.completed-icon {
  font-size: 24rpx;
  color: #fff;
}

.course-info {
  flex: 1;
  padding: 24rpx;
  min-width: 0;
}

.course-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1a4d2e;
  display: block;
  margin-bottom: 8rpx;
  line-height: 1.4;
}

.course-chapter {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 12rpx;
}

.course-desc {
  font-size: 24rpx;
  color: #666;
  display: block;
  margin-bottom: 12rpx;
  line-height: 1.4;
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 12rpx;
}

.course-action {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  padding: 12rpx 32rpx;
  background: #e8f5eb;
  color: $primary;
  font-size: 24rpx;
  font-weight: 600;
  border-radius: 999rpx;
}

.action-btn.primary {
  background: linear-gradient(135deg, $primary, $primary-dark);
  color: #fff;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 12rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.meta-icon {
  font-size: 24rpx;
}

.meta-text {
  font-size: 22rpx;
  color: #888;
}

.meta-tag {
  font-size: 20rpx;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
}

.meta-tag.easy {
  background: #e8f5e9;
  color: $primary;
}

.meta-tag.medium {
  background: #fff3e0;
  color: #e65100;
}

.meta-tag.hard {
  background: #fce4ec;
  color: #c62828;
}

.progress-text {
  font-size: 22rpx;
  color: $primary;
  width: 72rpx;
  text-align: right;
}

.completed-card {
  opacity: 0.9;
}

.completed-info {
  display: flex;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 8rpx;
}

.completed-date {
  font-size: 24rpx;
  color: #666;
}

.completed-score {
  font-size: 26rpx;
  font-weight: 600;
  color: $primary;
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.exam-section {
  // Exam section wrapper
}

.exam-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(26, 77, 46, 0.06);
}

.exam-icon {
  font-size: 48rpx;
  width: 80rpx;
  text-align: center;
}

.exam-info {
  flex: 1;
  min-width: 0;
}

.exam-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1a4d2e;
  display: block;
  margin-bottom: 8rpx;
}

.exam-result {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 6rpx;
}

.exam-score {
  font-size: 28rpx;
  font-weight: 600;
  color: $primary;
}

.exam-status {
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.exam-status.passed {
  background: #e8f5e9;
  color: $primary;
}

.exam-time,
.exam-duration {
  font-size: 24rpx;
  color: #888;
  display: block;
}

.exam-action {
  flex-shrink: 0;
}

.loading-state,
.empty-state {
  padding: 60rpx 0;
  text-align: center;
  color: #999;
  font-size: 28rpx;
}

.empty-state.small {
  padding: 40rpx 0;
}

.empty-icon {
  font-size: 64rpx;
  display: block;
  margin-bottom: 16rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>
