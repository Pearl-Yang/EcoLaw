<template>
  <view 
    class="joystick-container"
    :class="{ 'joystick-active': isActive }"
    @touchstart="onTouchStart"
    @touchmove="onTouchMove"
    @touchend="onTouchEnd"
    @touchcancel="onTouchEnd"
  >
    <!-- 摇杆底座 -->
    <view class="joystick-base">
      <!-- 方向指示 -->
      <view class="direction-indicator">
        <view class="direction-arrow direction-up"></view>
        <view class="direction-arrow direction-down"></view>
        <view class="direction-arrow direction-left"></view>
        <view class="direction-arrow direction-right"></view>
      </view>
      
      <!-- 可移动区域 -->
      <view class="joystick-area">
        <!-- 摇杆球 -->
        <view 
          class="joystick-knob"
          :style="knobStyle"
        >
          <view class="knob-inner"></view>
        </view>
      </view>
    </view>
    
    <!-- 当前方向显示 -->
    <view v-if="showDirection" class="direction-display">
      <text class="direction-text">{{ directionText }}</text>
      <text class="power-text">{{ Math.round(power * 100) }}%</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  // 摇杆半径（像素）
  radius: {
    type: Number,
    default: 80
  },
  // 摇杆球半径（像素）
  knobRadius: {
    type: Number,
    default: 30
  },
  // 是否显示方向文字
  showDirection: {
    type: Boolean,
    default: true
  },
  // 是否启用
  enabled: {
    type: Boolean,
    default: true
  },
  // 灵敏度
  sensitivity: {
    type: Number,
    default: 1.0
  },
  // 死区（0-1）
  deadZone: {
    type: Number,
    default: 0.1
  }
})

const emit = defineEmits([
  'move',
  'start',
  'end',
  'directionChange'
])

// 状态
const isActive = ref(false)
const knobX = ref(0)
const knobY = ref(0)
const angle = ref(0)
const power = ref(0)
const directionText = ref('停止')

// 计算摇杆球样式
const knobStyle = computed(() => {
  const maxOffset = props.radius - props.knobRadius
  return {
    transform: `translate(${knobX.value}px, ${knobY.value}px)`,
    transition: isActive.value ? 'none' : 'transform 0.2s ease-out'
  }
})

// 触摸事件处理
let touchId = null
let containerRect = null

function getContainerRect() {
  const query = uni.createSelectorQuery()
  return new Promise((resolve) => {
    query.select('.joystick-container').boundingClientRect((rect) => {
      resolve(rect)
    }).exec()
  })
}

async function onTouchStart(event) {
  if (!props.enabled) return
  
  const touch = event.touches[0]
  touchId = touch.identifier
  isActive.value = true
  
  // 获取容器位置
  if (!containerRect) {
    containerRect = await getContainerRect()
  }
  
  // 计算触摸点相对于容器中心的位置
  const centerX = containerRect.width / 2
  const centerY = containerRect.height / 2
  const touchX = touch.clientX - containerRect.left - centerX
  const touchY = touch.clientY - containerRect.top - centerY
  
  updateKnobPosition(touchX, touchY)
  
  emit('start', { angle: angle.value, power: power.value })
}

function onTouchMove(event) {
  if (!props.enabled || touchId === null) return
  
  // 找到对应的触摸点
  const touch = Array.from(event.touches).find(t => t.identifier === touchId)
  if (!touch) return
  
  // 计算触摸点相对于容器中心的位置
  const centerX = containerRect.width / 2
  const centerY = containerRect.height / 2
  const touchX = touch.clientX - containerRect.left - centerX
  const touchY = touch.clientY - containerRect.top - centerY
  
  updateKnobPosition(touchX, touchY)
}

function onTouchEnd(event) {
  if (!props.enabled) return
  
  // 检查是否是我们追踪的触摸点
  if (event.touches.length === 0) {
    touchId = null
    isActive.value = false
    resetKnob()
    emit('end', { angle: angle.value, power: power.value })
  } else {
    // 还有其他触摸点
    const touch = Array.from(event.touches).find(t => t.identifier === touchId)
    if (!touch) {
      touchId = null
      isActive.value = false
      resetKnob()
      emit('end', { angle: angle.value, power: power.value })
    }
  }
}

function updateKnobPosition(x, y) {
  // 计算距离和角度
  const distance = Math.sqrt(x * x + y * y)
  const maxDistance = props.radius - props.knobRadius
  
  // 应用灵敏度
  const adjustedDistance = distance * props.sensitivity
  
  // 限制在最大范围内
  let limitedX, limitedY, limitedDistance
  if (adjustedDistance > maxDistance) {
    const ratio = maxDistance / adjustedDistance
    limitedX = x * ratio
    limitedY = y * ratio
    limitedDistance = maxDistance
  } else {
    limitedX = x
    limitedY = y
    limitedDistance = adjustedDistance
  }
  
  // 计算标准化后的值（考虑死区）
  const normalizedDistance = limitedDistance / maxDistance
  const effectivePower = normalizedDistance < props.deadZone 
    ? 0 
    : (normalizedDistance - props.deadZone) / (1 - props.deadZone)
  
  // 更新状态
  knobX.value = limitedX
  knobY.value = limitedY
  power.value = Math.min(1, Math.max(0, effectivePower))
  
  // 计算角度（弧度转角度，0度向上）
  if (limitedDistance > 0) {
    angle.value = Math.atan2(-limitedX, -limitedY) * 180 / Math.PI
    directionText.value = getDirectionText(angle.value)
  } else {
    angle.value = 0
    power.value = 0
    directionText.value = '停止'
  }
  
  // 发送移动事件
  emit('move', {
    angle: angle.value,
    power: power.value,
    x: limitedDistance > 0 ? limitedX / maxDistance : 0,
    y: limitedDistance > 0 ? limitedY / maxDistance : 0
  })
  
  // 发送方向变化事件
  emit('directionChange', {
    angle: angle.value,
    power: power.value
  })
}

function resetKnob() {
  knobX.value = 0
  knobY.value = 0
  angle.value = 0
  power.value = 0
  directionText.value = '停止'
  
  emit('move', { angle: 0, power: 0, x: 0, y: 0 })
}

function getDirectionText(angleDeg) {
  // 将角度转换为方向
  const normalizedAngle = ((angleDeg % 360) + 360) % 360
  
  if (normalizedAngle >= 337.5 || normalizedAngle < 22.5) {
    return '上'
  } else if (normalizedAngle >= 22.5 && normalizedAngle < 67.5) {
    return '右上'
  } else if (normalizedAngle >= 67.5 && normalizedAngle < 112.5) {
    return '右'
  } else if (normalizedAngle >= 112.5 && normalizedAngle < 157.5) {
    return '右下'
  } else if (normalizedAngle >= 157.5 && normalizedAngle < 202.5) {
    return '下'
  } else if (normalizedAngle >= 202.5 && normalizedAngle < 247.5) {
    return '左下'
  } else if (normalizedAngle >= 247.5 && normalizedAngle < 292.5) {
    return '左'
  } else {
    return '左上'
  }
}

// 生命周期
onMounted(() => {
  // 初始化
})

onUnmounted(() => {
  touchId = null
})

// 暴露方法
defineExpose({
  // 手动设置摇杆值
  setValue: (newAngle, newPower) => {
    const maxDistance = props.radius - props.knobRadius
    const radians = newAngle * Math.PI / 180
    const limitedX = Math.sin(radians) * maxDistance * newPower
    const limitedY = -Math.cos(radians) * maxDistance * newPower
    
    knobX.value = limitedX
    knobY.value = limitedY
    angle.value = newAngle
    power.value = newPower
    directionText.value = getDirectionText(newAngle)
  },
  
  // 重置摇杆
  reset: () => {
    resetKnob()
  },
  
  // 获取当前值
  getValue: () => ({
    angle: angle.value,
    power: power.value
  })
})
</script>

<style scoped>
.joystick-container {
  position: relative;
  width: 200px;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  touch-action: none;
  user-select: none;
}

.joystick-base {
  position: relative;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.3), rgba(0, 0, 0, 0.2));
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.3),
    inset 0 2px 4px rgba(255, 255, 255, 0.2),
    inset 0 -2px 4px rgba(0, 0, 0, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.joystick-container.joystick-active .joystick-base {
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 
    0 4px 16px rgba(76, 175, 80, 0.4),
    inset 0 2px 4px rgba(255, 255, 255, 0.3),
    inset 0 -2px 4px rgba(0, 0, 0, 0.2);
}

.direction-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.direction-arrow {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0.2;
}

.direction-arrow.direction-up {
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 10px solid rgba(255, 255, 255, 0.5);
}

.direction-arrow.direction-down {
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 10px solid rgba(255, 255, 255, 0.5);
}

.direction-arrow.direction-left {
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 10px solid rgba(255, 255, 255, 0.5);
}

.direction-arrow.direction-right {
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 10px solid rgba(255, 255, 255, 0.5);
}

.joystick-area {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle at 50% 40%, rgba(100, 100, 100, 0.1), rgba(0, 0, 0, 0.2));
}

.joystick-knob {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  margin-left: -30px;
  margin-top: -30px;
  border-radius: 50%;
  background: linear-gradient(145deg, #4CAF50, #388E3C);
  box-shadow: 
    0 4px 8px rgba(0, 0, 0, 0.3),
    0 2px 4px rgba(0, 0, 0, 0.2),
    inset 0 2px 4px rgba(255, 255, 255, 0.3),
    inset 0 -2px 4px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  z-index: 10;
}

.joystick-container.joystick-active .joystick-knob {
  background: linear-gradient(145deg, #66BB6A, #43A047);
  box-shadow: 
    0 4px 12px rgba(76, 175, 80, 0.5),
    0 2px 4px rgba(0, 0, 0, 0.2),
    inset 0 2px 4px rgba(255, 255, 255, 0.4),
    inset 0 -2px 4px rgba(0, 0, 0, 0.2);
}

.knob-inner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.4), transparent);
}

.direction-display {
  position: absolute;
  bottom: -40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.direction-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.power-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}
</style>
