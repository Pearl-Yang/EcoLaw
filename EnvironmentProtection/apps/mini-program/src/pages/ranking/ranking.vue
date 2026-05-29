<template>
  <view class="page">
    <text class="title">积分排行</text>
    <text class="sub">与学习中心、任务完成联动（示例数据）</text>
    <view v-for="(row, i) in rows" :key="row.id" class="row">
      <text class="rank" :class="{ top: i < 3 }">{{ i + 1 }}</text>
      <text class="name">{{ row.name }}</text>
      <text class="pts">{{ row.points }} 分</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const rows = ref([
  { id: 1, name: '法律明白人 · 张三', points: 1280 },
  { id: 2, name: '社区志愿者 · 李四', points: 960 },
  { id: 3, name: '商户代表 · 王五', points: 720 },
  { id: 4, name: '我', points: 120 }
])
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f7f2;
  padding: 32rpx 24rpx;
}
.title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1a4d2e;
  display: block;
  margin-bottom: 8rpx;
}
.sub {
  font-size: 24rpx;
  color: #888;
  display: block;
  margin-bottom: 28rpx;
}
.row {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 14rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
}
.rank {
  width: 48rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: #999;
}
.rank.top {
  color: #28a745;
}
.name {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  margin-left: 16rpx;
}
.pts {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a4d2e;
}
</style>
