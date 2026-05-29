<script>
/**
 * 顶部轮播头图：绿色渐变模拟森林/草地/沼泽/高原/山地。
 * 若已放入实景图，将路径填入 bannerSrcs（顺序与五类景观一致），优先显示。
 */
export default {
  name: 'AuthHero',
  props: {
    bannerSrcs: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      slides: [
        { theme: 'forest',     label: '森林' },
        { theme: 'grassland',  label: '草地' },
        { theme: 'marsh',      label: '沼泽' },
        { theme: 'plateau',    label: '高原' },
        { theme: 'mountain',   label: '山地' }
      ],
      imgOk: [true, true, true, true, true]
    }
  },
  methods: {
    onImgError(i) {
      const next = [...this.imgOk]
      next[i] = false
      this.imgOk = next
    }
  }
}
</script>

<template>
  <view class="hero">
    <swiper
      class="hero-swiper"
      circular
      autoplay
      :interval="4500"
      :duration="500"
      indicator-dots
      indicator-color="rgba(255,255,255,0.35)"
      indicator-active-color="#ffffff"
    >
      <swiper-item v-for="(s, i) in slides" :key="s.theme">
        <view class="slide">
          <image
            v-if="bannerSrcs[i] && imgOk[i]"
            class="slide-img"
            :src="bannerSrcs[i]"
            mode="aspectFill"
            @error="onImgError(i)"
          />
          <view class="slide-bg" :class="'slide-bg--' + s.theme" />
          <view class="slide-vignette" />
        </view>
      </swiper-item>
    </swiper>

    <view class="hero-brand">
      <view class="logo-mark">叶</view>
      <text class="logo-title">绿法通</text>
      <text class="logo-sub">白色污染治理 · AI 智慧普法</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.hero {
  position: relative;
  height: 400rpx;
  width: 100%;
}
.hero-swiper { width: 100%; height: 100%; }
.slide {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.slide-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}
.slide-bg { position: absolute; inset: 0; z-index: 0; }
.slide-bg--forest     { background: linear-gradient(165deg,#0d3d2e 0%,#1a5c40 35%,#2d6b4f 70%,#1e4d3a 100%); }
.slide-bg--grassland  { background: linear-gradient(165deg,#3d6b3a 0%,#5a8f4e 40%,#7daf6a 75%,#4a7c48 100%); }
.slide-bg--marsh      { background: linear-gradient(165deg,#1a4d4a 0%,#2d6560 45%,#3d7a72 80%,#255550 100%); }
.slide-bg--plateau    { background: linear-gradient(165deg,#3a5c4a 0%,#5a7a62 40%,#8faa7a 72%,#4d6b52 100%); }
.slide-bg--mountain   { background: linear-gradient(165deg,#2c3d35 0%,#3d5248 38%,#5a6b5e 68%,#354239 100%); }
.slide-vignette {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  background: linear-gradient(180deg,rgba(0,0,0,0.1) 0%,transparent 40%,transparent 55%,rgba(0,0,0,0.35) 100%);
}
.hero-brand {
  position: absolute;
  left: 0; right: 0;
  bottom: 56rpx;
  z-index: 4;
  display: flex;
  flex-direction: column;
  align-items: center;
  pointer-events: none;
}
.logo-mark {
  width: 88rpx; height: 88rpx;
  border-radius: 24rpx;
  background: rgba(255,255,255,0.92);
  color: #33857a;
  font-size: 40rpx; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8rpx 28rpx rgba(0,0,0,0.12);
  margin-bottom: 12rpx;
}
.logo-title {
  font-size: 40rpx; font-weight: 700;
  color: #ffffff;
  letter-spacing: 6rpx;
  text-shadow: 0 2rpx 12rpx rgba(0,0,0,0.25);
}
.logo-sub {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: rgba(255,255,255,0.88);
  text-shadow: 0 1rpx 8rpx rgba(0,0,0,0.2);
}
</style>
