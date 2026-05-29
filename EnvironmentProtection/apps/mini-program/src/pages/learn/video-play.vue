<template>
  <view class="page">
    <video
      v-if="src"
      class="player"
      :src="src"
      controls
      show-center-play-btn
      enable-play-gesture
    />
    <view v-else class="no-video">
      <text class="no-video-text">该任务暂未配置可播放视频，请联系管理员上传</text>
    </view>
    <view class="meta">
      <text class="title">{{ title }}</text>
      <view v-if="views != null" class="stats">
        <text class="stat">▶ {{ views }} 播放</text>
        <text class="stat">👍 {{ likes }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getVideoTaskById } from '../../data/videoTasks.js'

const src = ref('')
const title = ref('视频学习')
const views = ref(null)
const likes = ref(null)

onLoad((query) => {
  const id = query.id
  if (id) {
    const item = getVideoTaskById(id)
    if (item) {
      title.value = item.title
      src.value = item.videoUrl || ''
      views.value = item.views
      likes.value = item.likes
      return
    }
  }
  title.value = query.title ? decodeURIComponent(query.title) : '视频学习'
  src.value = query.src ? decodeURIComponent(query.src) : ''
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f172a;
  padding-bottom: env(safe-area-inset-bottom);
}

.player {
  width: 100%;
  height: 422rpx;
  background: #000;
}

.no-video {
  width: 100%;
  height: 422rpx;
  background: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48rpx;
  box-sizing: border-box;
}

.no-video-text {
  font-size: 28rpx;
  color: #94a3b8;
  text-align: center;
  line-height: 1.6;
}

.meta {
  padding: 32rpx 28rpx;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  margin-top: -16rpx;
  position: relative;
}

.title {
  font-size: 34rpx;
  font-weight: 700;
  color: #1a4d2e;
  line-height: 1.45;
  display: block;
}

.stats {
  display: flex;
  gap: 32rpx;
  margin-top: 20rpx;
}

.stat {
  font-size: 24rpx;
  color: #64748b;
}
</style>
