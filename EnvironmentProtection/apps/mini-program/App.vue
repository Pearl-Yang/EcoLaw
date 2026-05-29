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
  --primary-color: #28a745;
  --success-color: #28a745;
  --warning-color: #F59E0B;
  --danger-color: #EF4444;
  --info-color: #6B7280;
  
  font-size: 28rpx;
  color: #333;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

view, scroll-view, swiper-item {
  box-sizing: border-box;
}
</style>