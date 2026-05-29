<template>
  <view class="ground-controller-wrapper">
    <!-- 地面点击区域遮罩 -->
    <view 
      class="ground-touch-area"
      @click="onGroundClick"
      @touchstart="onTouchStart"
      @touchmove="onTouchMove"
      @touchend="onTouchEnd"
    >
      <!-- 点击涟漪效果 -->
      <view 
        v-for="ripple in ripples" 
        :key="ripple.id"
        class="click-ripple"
        :style="{
          left: ripple.x + 'px',
          top: ripple.y + 'px',
          animation: 'ripple 0.6s ease-out forwards'
        }"
      ></view>
      
      <!-- 路径指示线（移动时显示） -->
      <canvas 
        v-if="showPath && isMoving"
        ref="pathCanvas"
        class="path-canvas"
        :width="canvasWidth"
        :height="canvasHeight"
      ></canvas>
    </view>
    
    <!-- 目标点标记 -->
    <view 
      v-if="showTarget && targetPos"
      class="target-marker"
      :style="{
        left: targetPos.screenX + 'px',
        top: targetPos.screenY + 'px'
      }"
    >
      <view class="target-ring"></view>
      <view class="target-dot"></view>
    </view>
    
    <!-- 距离提示 -->
    <view 
      v-if="showDistance && distance !== null"
      class="distance-hint"
      :style="{
        left: distancePos.screenX + 'px',
        top: (distancePos.screenY - 30) + 'px'
      }"
    >
      <text class="distance-text">{{ distance.toFixed(1) }}m</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'

const props = defineProps({
  // 是否启用点击移动
  enabled: {
    type: Boolean,
    default: true
  },
  // 是否显示路径
  showPath: {
    type: Boolean,
    default: true
  },
  // 是否显示目标标记
  showTarget: {
    type: Boolean,
    default: true
  },
  // 是否显示距离
  showDistance: {
    type: Boolean,
    default: true
  },
  // 地图边界
  bounds: {
    type: Object,
    default: () => ({
      minX: -30,
      maxX: 30,
      minZ: -30,
      maxZ: 30
    })
  },
  // 点击最小间隔（毫秒）
  clickInterval: {
    type: Number,
    default: 100
  }
})

const emit = defineEmits([
  'groundClick',
  'moveStart',
  'moveEnd',
  'pathUpdate'
])

// 状态
const canvasWidth = ref(375)
const canvasHeight = ref(500)
const ripples = ref([])
const isMoving = ref(false)
const targetPos = ref(null)
const distance = ref(null)
const distancePos = ref({ screenX: 0, screenY: 0 })

// 路径画布
const pathCanvas = ref(null)
let pathCtx = null
let pathPoints = []

// 宠物位置（相对于3D场景）
const petWorldPos = ref({ x: 0, z: 0 })

// 点击涟漪ID
let rippleId = 0

// 上次点击时间
let lastClickTime = 0

// 初始化
onMounted(() => {
  // 获取画布尺寸
  updateCanvasSize()
  
  // 监听窗口大小变化
  uni.onWindowResize?.(() => {
    updateCanvasSize()
  })
})

// 清理
onUnmounted(() => {
  ripples.value = []
  pathPoints = []
})

// 更新画布尺寸
function updateCanvasSize() {
  const systemInfo = uni.getSystemInfoSync()
  canvasWidth.value = systemInfo.windowWidth
  canvasHeight.value = systemInfo.windowHeight
}

// 处理地面点击
async function onGroundClick(event) {
  if (!props.enabled) return
  
  // 节流处理
  const now = Date.now()
  if (now - lastClickTime < props.clickInterval) {
    return
  }
  lastClickTime = now
  
  // 获取点击位置
  const { x, y } = getClickPosition(event)
  
  // 添加涟漪效果
  addRipple(x, y)
  
  // 计算世界坐标（这里需要结合相机参数）
  const worldPos = screenToWorld(x, y)
  
  // 检查边界
  if (!isWithinBounds(worldPos.x, worldPos.z)) {
    console.log('Click outside bounds')
    return
  }
  
  // 设置目标位置
  setTargetPosition(worldPos, x, y)
  
  // 发射点击事件
  emit('groundClick', {
    screenX: x,
    screenY: y,
    worldX: worldPos.x,
    worldY: 0,
    worldZ: worldPos.z,
    distance: calculateDistance(petWorldPos.value, worldPos)
  })
}

// 获取点击位置
function getClickPosition(event) {
  if (event.detail) {
    return {
      x: event.detail.x || event.detail.clientX || 0,
      y: event.detail.y || event.detail.clientY || 0
    }
  }
  return { x: 0, y: 0 }
}

// 触摸开始
function onTouchStart(event) {
  if (!props.enabled) return
  
  // 阻止默认行为
  event.stopPropagation?.()
}

// 触摸移动
function onTouchMove(event) {
  if (!props.enabled) return
  
  // 可以实时更新位置预览
  const touch = event.touches?.[0]
  if (touch) {
    const worldPos = screenToWorld(touch.clientX, touch.clientY)
    
    // 更新距离显示
    distance.value = calculateDistance(petWorldPos.value, worldPos)
    distancePos.value = { screenX: touch.clientX, screenY: touch.clientY }
  }
}

// 触摸结束
function onTouchEnd(event) {
  if (!props.enabled) return
  
  // 清除距离显示
  distance.value = null
}

// 添加涟漪效果
function addRipple(x, y) {
  const id = rippleId++
  ripples.value.push({ id, x, y })
  
  // 动画结束后移除
  setTimeout(() => {
    ripples.value = ripples.value.filter(r => r.id !== id)
  }, 600)
}

// 设置目标位置
function setTargetPosition(worldPos, screenX, screenY) {
  isMoving.value = true
  targetPos.value = {
    worldX: worldPos.x,
    worldY: 0,
    worldZ: worldPos.z,
    screenX: screenX,
    screenY: screenY
  }
  distance.value = calculateDistance(petWorldPos.value, worldPos)
  distancePos.value = { screenX, screenY }
  
  // 添加路径点
  addPathPoint(worldPos)
  
  emit('moveStart', {
    target: worldPos,
    distance: distance.value
  })
}

// 添加路径点
function addPathPoint(worldPos) {
  pathPoints.push({
    ...worldPos,
    timestamp: Date.now()
  })
  
  // 限制路径点数量
  if (pathPoints.length > 100) {
    pathPoints.shift()
  }
  
  // 更新路径显示
  updatePathDisplay()
  
  emit('pathUpdate', { points: [...pathPoints] })
}

// 更新路径显示
async function updatePathDisplay() {
  await nextTick()
  
  if (!pathCanvas.value) {
    const query = uni.createSelectorQuery().in(getCurrentInstance?.())
    if (query) {
      query.select('.path-canvas').fields({ node: true, size: true }, (res) => {
        if (res && res.node) {
          pathCtx = res.node.getContext('2d')
          drawPath()
        }
      }).exec()
    }
    return
  }
  
  drawPath()
}

// 绘制路径
function drawPath() {
  if (!pathCtx || pathPoints.length < 2) return
  
  const canvas = pathCanvas.value
  if (!canvas) return
  
  // 清除画布
  pathCtx.clearRect(0, 0, canvas.width, canvas.height)
  
  // 设置路径样式
  pathCtx.strokeStyle = 'rgba(76, 175, 80, 0.6)'
  pathCtx.lineWidth = 3
  pathCtx.lineCap = 'round'
  pathCtx.lineJoin = 'round'
  pathCtx.setLineDash([5, 5])
  
  // 开始绘制
  pathCtx.beginPath()
  
  // 转换世界坐标到屏幕坐标
  const startScreen = worldToScreen(pathPoints[0].x, pathPoints[0].z)
  pathCtx.moveTo(startScreen.x, startScreen.y)
  
  for (let i = 1; i < pathPoints.length; i++) {
    const screen = worldToScreen(pathPoints[i].x, pathPoints[i].z)
    pathCtx.lineTo(screen.x, screen.y)
  }
  
  pathCtx.stroke()
}

// 世界坐标转屏幕坐标（简化版本，需要结合相机参数）
function worldToScreen(worldX, worldZ) {
  // 这里需要根据实际的相机参数计算
  // 简化处理：假设地面在视口中心，Y轴朝上
  const centerX = canvasWidth.value / 2
  const centerY = canvasHeight.value * 0.6 // 假设地面在视口下半部分
  
  // 简化的正交投影
  const scale = 10 // 每单位世界坐标对应的像素数
  const screenX = centerX + worldX * scale
  const screenY = centerY - worldZ * scale
  
  return { x: screenX, y: screenY }
}

// 屏幕坐标转世界坐标
function screenToWorld(screenX, screenY) {
  // 简化的逆投影
  const centerX = canvasWidth.value / 2
  const centerY = canvasHeight.value * 0.6
  
  const scale = 10
  const worldX = (screenX - centerX) / scale
  const worldZ = -(screenY - centerY) / scale
  
  return { x: worldX, y: 0, z: worldZ }
}

// 计算距离
function calculateDistance(from, to) {
  const dx = to.x - from.x
  const dz = to.z - from.z
  return Math.sqrt(dx * dx + dz * dz)
}

// 检查是否在边界内
function isWithinBounds(x, z) {
  return x >= props.bounds.minX && 
         x <= props.bounds.maxX && 
         z >= props.bounds.minZ && 
         z <= props.bounds.maxZ
}

// 更新宠物世界位置（由父组件调用）
function updatePetPosition(x, z) {
  petWorldPos.value = { x, z }
  
  // 如果宠物到达目标位置，停止移动
  if (targetPos.value && distance.value !== null && distance.value < 0.5) {
    stopMove()
  }
}

// 停止移动
function stopMove() {
  isMoving.value = false
  targetPos.value = null
  distance.value = null
  pathPoints = []
  
  // 清除路径
  if (pathCtx && pathCanvas.value) {
    pathCtx.clearRect(0, 0, pathCanvas.value.width, pathCanvas.value.height)
  }
  
  emit('moveEnd')
}

// 清除所有效果
function clear() {
  ripples.value = []
  pathPoints = []
  targetPos.value = null
  distance.value = null
  isMoving.value = false
  
  if (pathCtx && pathCanvas.value) {
    pathCtx.clearRect(0, 0, pathCanvas.value.width, pathCanvas.value.height)
  }
}

// 暴露方法
defineExpose({
  updatePetPosition,
  stopMove,
  clear,
  setTargetPosition
})
</script>

<style scoped>
.ground-controller-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.ground-touch-area {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: auto;
}

.click-ripple {
  position: absolute;
  width: 40px;
  height: 40px;
  margin-left: -20px;
  margin-top: -20px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.4) 0%, transparent 70%);
  pointer-events: none;
}

@keyframes ripple {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(3);
    opacity: 0;
  }
}

.path-canvas {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}

.target-marker {
  position: absolute;
  width: 40px;
  height: 40px;
  margin-left: -20px;
  margin-top: -20px;
  pointer-events: none;
  transform-origin: center center;
}

.target-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 30px;
  height: 30px;
  margin-left: -15px;
  margin-top: -15px;
  border: 3px solid #4CAF50;
  border-radius: 50%;
  animation: targetPulse 1.5s ease-in-out infinite;
}

.target-dot {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 8px;
  height: 8px;
  margin-left: -4px;
  margin-top: -4px;
  background: #4CAF50;
  border-radius: 50%;
}

@keyframes targetPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.6;
  }
}

.distance-hint {
  position: absolute;
  transform: translateX(-50%);
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  pointer-events: none;
}

.distance-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
}
</style>
