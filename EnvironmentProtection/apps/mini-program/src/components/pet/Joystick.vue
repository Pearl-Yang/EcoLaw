<template>
  <view class="joystick-container" :style="containerStyle">
    <!-- 摇杆底座 -->
    <view
      class="joystick-base"
      :style="baseStyle"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
      @touchcancel="handleTouchEnd"
    >
      <!-- 摇杆杆 -->
      <view class="joystick-stick" :style="stickStyle"></view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  // 摇杆大小
  size: {
    type: Number,
    default: 120
  },
  // 摇杆杆大小
  stickSize: {
    type: Number,
    default: 50
  },
  // 摇杆颜色
  color: {
    type: String,
    default: '#28A745'
  },
  // 背景色
  bgColor: {
    type: String,
    default: 'rgba(255, 255, 255, 0.8)'
  },
  // 是否启用
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['move', 'stop'])

// 摇杆状态
const isActive = ref(false)
const offsetX = ref(0)
const offsetY = ref(0)
const maxDistance = computed(() => (props.size - props.stickSize) / 2 - 5)

// 样式
const containerStyle = computed(() => ({
  width: props.size + 'px',
  height: props.size + 'px'
}))

const baseStyle = computed(() => ({
  width: props.size + 'px',
  height: props.size + 'px',
  backgroundColor: props.bgColor,
  borderColor: props.color
}))

const stickStyle = computed(() => ({
  width: props.stickSize + 'px',
  height: props.stickSize + 'px',
  backgroundColor: props.color,
  transform: `translate(${offsetX.value}px, ${offsetY.value}px)`
}))

// 计算方向和距离
const calculateDirection = (x, y) => {
  const distance = Math.sqrt(x * x + y * y)
  const normalizedDistance = Math.min(distance / maxDistance.value, 1)

  let angle = Math.atan2(-y, x) * (180 / Math.PI)
  if (angle < 0) angle += 360

  let direction = 'center'
  if (distance > 10) {
    if (angle >= 315 || angle < 45) {
      direction = 'right'
    } else if (angle >= 45 && angle < 135) {
      direction = 'up'
    } else if (angle >= 135 && angle < 225) {
      direction = 'left'
    } else {
      direction = 'down'
    }
  }

  return {
    direction,
    angle: angle,
    distance: distance,
    normalizedDistance: normalizedDistance,
    offsetX: x,
    offsetY: y
  }
}

// 触摸开始
const handleTouchStart = (e) => {
  if (props.disabled) return
  e.preventDefault()
  isActive.value = true

  const touch = e.touches[0]
  const baseRect = e.currentTarget.getBoundingClientRect()
  const centerX = baseRect.left + baseRect.width / 2
  const centerY = baseRect.top + baseRect.height / 2

  let deltaX = touch.clientX - centerX
  let deltaY = touch.clientY - centerY

  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  if (distance > maxDistance.value) {
    deltaX = (deltaX / distance) * maxDistance.value
    deltaY = (deltaY / distance) * maxDistance.value
  }

  offsetX.value = deltaX
  offsetY.value = deltaY

  emit('move', calculateDirection(deltaX, deltaY))
}

// 触摸移动
const handleTouchMove = (e) => {
  if (props.disabled || !isActive.value) return
  e.preventDefault()

  const touch = e.touches[0]
  const baseRect = e.currentTarget.getBoundingClientRect()
  const centerX = baseRect.left + baseRect.width / 2
  const centerY = baseRect.top + baseRect.height / 2

  let deltaX = touch.clientX - centerX
  let deltaY = touch.clientY - centerY

  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  if (distance > maxDistance.value) {
    deltaX = (deltaX / distance) * maxDistance.value
    deltaY = (deltaY / distance) * maxDistance.value
  }

  offsetX.value = deltaX
  offsetY.value = deltaY

  emit('move', calculateDirection(deltaX, deltaY))
}

// 触摸结束
const handleTouchEnd = (e) => {
  if (props.disabled) return
  isActive.value = false
  offsetX.value = 0
  offsetY.value = 0
  emit('stop')
}

// 暴露方法给父组件
defineExpose({
  reset: () => {
    isActive.value = false
    offsetX.value = 0
    offsetY.value = 0
  }
})
</script>

<style scoped>
.joystick-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.joystick-base {
  border-radius: 50%;
  border: 3px solid;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.joystick-stick {
  border-radius: 50%;
  transition: transform 0.05s ease-out;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}
</style>
