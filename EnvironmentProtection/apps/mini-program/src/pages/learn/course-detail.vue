<template>
  <view class="page">
    <view v-if="loading" class="state">
      <text>加载中...</text>
    </view>
    <view v-else-if="!course" class="state">
      <text class="icon">📚</text>
      <text>未找到该课程</text>
      <view class="back-btn" @click="goBack">
        <text>返回学习中心</text>
      </view>
    </view>
    <scroll-view v-else scroll-y class="scroll" :show-scrollbar="false">
      <view class="cover" :style="coverStyle">
        <text v-if="!course.coverUrl" class="emoji">{{ course.emoji }}</text>
      </view>
      <view class="body">
        <text class="title">{{ course.title }}</text>
        <view class="tags">
          <text class="tag" :class="course.type">{{ typeLabel(course.type) }}</text>
          <text class="tag muted">⏱ {{ course.duration }} 分钟</text>
          <text v-if="course.points" class="tag points">📊 {{ course.points }} 积分</text>
        </view>
        <text class="desc">{{ course.description }}</text>

        <view class="actions">
          <view
            v-if="course.videoUrl"
            class="btn primary"
            @click="playVideo"
          >
            <text>播放视频</text>
          </view>
          <view
            v-if="course.attachmentUrl"
            class="btn outline"
            @click="openDoc"
          >
            <text>查看附件</text>
          </view>
          <view class="btn outline" @click="markProgress">
            <text>记录学习进度（示例）</text>
          </view>
        </view>

        <view class="section">
          <text class="section-title">内容简介</text>
          <text class="section-text">{{ course.fullContent || course.description }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/index.js'

const loading = ref(true)
const courseId = ref('')
const course = ref(null)

const coverStyle = computed(() => {
  const c = course.value
  if (!c) return {}
  if (c.coverUrl) {
    return {
      backgroundImage: `url(${c.coverUrl})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  return { background: c.coverGradient || 'linear-gradient(135deg, #28a745, #1a4d2e)' }
})

onLoad((query) => {
  courseId.value = query.id || ''
  if (!courseId.value) {
    loading.value = false
    return
  }
  loadCourse()
})

function typeLabel(type) {
  const m = { video: '视频', document: '文档', exam: '考试' }
  return m[type] || '课程'
}

function normalizeFromApi(t) {
  const content = t.content || ''
  return {
    id: t.id,
    title: t.title || '培训课程',
    description: content
      ? content.length > 120
        ? content.slice(0, 120) + '…'
        : content
      : '环保普法培训课程，点击播放或查看详情。',
    fullContent: content || '暂无更多正文，可在管理端维护培训内容。',
    emoji: typeEmoji(t.type),
    coverGradient: pickGradient(t.id),
    duration: t.duration != null ? t.duration : 30,
    points: Math.min(120, Math.max(20, (t.duration || 30) * 2)),
    progress: 0,
    coverUrl: t.coverUrl || '',
    videoUrl: t.videoUrl || '',
    attachmentUrl: t.attachmentUrl || '',
    type: t.type || 'video',
    difficulty: 'medium',
    difficultyText: '标准'
  }
}

function typeEmoji(type) {
  if (type === 'exam') return '📝'
  if (type === 'document') return '📄'
  return '🎬'
}

function pickGradient(seed) {
  const gradients = [
    'linear-gradient(135deg, #28a745, #1a4d2e)',
    'linear-gradient(135deg, #0077b6, #48cae4)',
    'linear-gradient(135deg, #40916c, #95d5b2)',
    'linear-gradient(135deg, #6c5ce7, #a29bfe)',
    'linear-gradient(135deg, #e76f51, #f4a261)'
  ]
  let h = 0
  const s = String(seed || '')
  for (let i = 0; i < s.length; i++) h = (h + s.charCodeAt(i) * (i + 1)) % 997
  return gradients[h % gradients.length]
}

function getMockById(id) {
  const mocks = [
    {
      id: '1',
      title: '《固体废物污染环境防治法》要点解读',
      description: '深度解析固废法中关于塑料污染治理的核心条款',
      fullContent:
        '本课程围绕固废法修订要点，结合企业常见场景，讲解贮存、运输、处置环节的合规要求与违法风险，适合环保岗位人员学习。',
      emoji: '📖',
      coverGradient: 'linear-gradient(135deg, #28a745, #1a4d2e)',
      duration: 45,
      points: 50,
      type: 'video',
      videoUrl:
        'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4',
      attachmentUrl: ''
    },
    {
      id: '2',
      title: '企业塑料制品合规指南',
      description: '面向生产企业的塑料污染防治合规要求与实操',
      fullContent:
        '从原料采购、产品标识、回收利用三方面梳理合规清单，并给出内控台账与自查表示例。',
      emoji: '🏭',
      coverGradient: 'linear-gradient(135deg, #0077b6, #48cae4)',
      duration: 60,
      points: 80,
      type: 'document',
      videoUrl: '',
      attachmentUrl: ''
    },
    {
      id: '3',
      title: '白色污染治理基础知识',
      description: '了解白色污染的来源、危害与防治措施',
      fullContent:
        '科普向入门课，介绍一次性塑料制品的环境影响及减量、替代、回收路径。',
      emoji: '🌿',
      coverGradient: 'linear-gradient(135deg, #40916c, #95d5b2)',
      duration: 30,
      points: 30,
      type: 'video',
      videoUrl:
        'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4',
      attachmentUrl: ''
    }
  ]
  return mocks.find((m) => m.id === id) || null
}

async function loadCourse() {
  loading.value = true
  try {
    const raw = await api.training.get(courseId.value, { showLoading: false })
    if (raw && raw.id) {
      course.value = normalizeFromApi(raw)
    } else {
      course.value = getMockById(courseId.value)
    }
  } catch (e) {
    console.warn('培训详情接口不可用，使用本地示例', e)
    course.value = getMockById(courseId.value)
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.switchTab({ url: '/pages/learn/learn' })
}

function playVideo() {
  const c = course.value
  if (!c?.videoUrl) return
  uni.navigateTo({
    url: `/pages/learn/video-play?title=${encodeURIComponent(c.title)}&src=${encodeURIComponent(c.videoUrl)}`
  })
}

function openDoc() {
  const url = course.value?.attachmentUrl
  if (!url) {
    uni.showToast({ title: '暂无附件链接', icon: 'none' })
    return
  }
  uni.showToast({ title: '请配置业务域名后使用 web-view 打开', icon: 'none' })
}

function markProgress() {
  uni.showToast({ title: '已记录（示例，待对接学时上报）', icon: 'none' })
}
</script>

<style lang="scss" scoped>
$primary: #28a745;
$primary-dark: #1a4d2e;

.page {
  min-height: 100vh;
  background: #f0f7f2;
}

.state {
  padding: 120rpx 48rpx;
  text-align: center;
  color: #64748b;
  font-size: 28rpx;
}

.state .icon {
  display: block;
  font-size: 72rpx;
  margin-bottom: 24rpx;
}

.back-btn {
  margin-top: 40rpx;
  display: inline-block;
  padding: 16rpx 40rpx;
  background: $primary;
  color: #fff;
  border-radius: 999rpx;
  font-size: 26rpx;
}

.scroll {
  height: 100vh;
  box-sizing: border-box;
}

.cover {
  height: 360rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji {
  font-size: 120rpx;
}

.body {
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  margin-top: -24rpx;
  padding: 36rpx 28rpx 48rpx;
  padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  position: relative;
}

.title {
  font-size: 36rpx;
  font-weight: 700;
  color: $primary-dark;
  line-height: 1.4;
  display: block;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin: 20rpx 0 24rpx;
}

.tag {
  font-size: 22rpx;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: #e8f5eb;
  color: $primary-dark;
}

.tag.muted {
  background: #f1f5f9;
  color: #64748b;
}

.tag.points {
  background: #fff7ed;
  color: #c2410c;
}

.tag.video {
  background: #e0f2fe;
  color: #0369a1;
}

.tag.document {
  background: #f3e8ff;
  color: #7c3aed;
}

.tag.exam {
  background: #fef3c7;
  color: #b45309;
}

.desc {
  font-size: 28rpx;
  color: #475569;
  line-height: 1.55;
  display: block;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin: 36rpx 0;
}

.btn {
  text-align: center;
  padding: 24rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
  font-weight: 600;
}

.btn.primary {
  background: linear-gradient(135deg, $primary, $primary-dark);
  color: #fff;
}

.btn.outline {
  background: #fff;
  color: $primary;
  border: 2rpx solid rgba(40, 167, 69, 0.45);
}

.section {
  margin-top: 16rpx;
  padding-top: 28rpx;
  border-top: 1rpx solid #e8f5eb;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: $primary-dark;
  display: block;
  margin-bottom: 16rpx;
}

.section-text {
  font-size: 26rpx;
  color: #64748b;
  line-height: 1.6;
  display: block;
  white-space: pre-wrap;
}
</style>
