<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
page {
  --primary-color: #22c55e;
  --success-color: #22c55e;
  --warning-color: #EAB308;
  --danger-color: #EF4444;
  --info-color: #6B7280;
  --accent-purple: #a855f7;
  
  font-size: 28rpx;
  color: #1f2937;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

view, scroll-view, swiper-item {
  box-sizing: border-box;
}
</style>