<template>
  <view class="pet-canvas-container">
    <!-- 3D Canvas -->
    <canvas
      v-if="use3D"
      id="petCanvas"
      class="pet-canvas"
      type="webgl"
      :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
      @click="onCanvasClick"
    ></canvas>
    
    <!-- 2D Fallback Canvas -->
    <canvas
      v-else
      id="petCanvas2D"
      class="pet-canvas"
      :style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
      @click="onCanvasClick"
    ></canvas>
    
    <!-- 加载指示器 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 宠物状态信息 -->
    <view class="pet-status-bar">
      <view class="status-item">
        <text class="status-icon">Lv.{{ currentLevel }}</text>
      </view>
      <view class="status-item">
        <text class="status-icon">{{ levelTitle }}</text>
      </view>
    </view>
    
    <!-- 宠物精灵图（2D模式） -->
    <view 
      v-if="!use3D"
      class="pet-sprite"
      :style="petSpriteStyle"
    >
      <image class="pet-image" src="/static/images/pet_default.png" mode="aspectFit"></image>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

const props = defineProps({
  petModelUrl: {
    type: String,
    default: '/static/models/pet.glb'
  },
  sceneType: {
    type: String,
    default: 'garden'
  },
  initialPosition: {
    type: Object,
    default: () => ({ x: 0, y: 0, z: 0 })
  },
  enableJoystick: {
    type: Boolean,
    default: true
  },
  enableClickMove: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits([
  'ready',
  'positionChange',
  'actionStart',
  'actionEnd',
  'groundClick'
])

// 状态
const canvasWidth = ref(375)
const canvasHeight = ref(500)
const loading = ref(true)
const currentLevel = ref(1)
const levelTitle = ref('新手')
const use3D = ref(false)

// 3D相关（动态导入）
let THREE = null
let scene = null
let camera = null
let renderer = null
let pet = null
let ground = null
let animationId = null

// 2D相关
let ctx2D = null
let pet2D = { x: 200, y: 250 }

// 宠物位置
const petPosition = ref({ ...props.initialPosition })

// 目标位置
let targetPosition = null
let isMoving = false

// 宠物精灵图样式
const petSpriteStyle = computed(() => ({
  left: pet2D.x + 'px',
  top: pet2D.y + 'px',
  transform: `translate(-50%, -100%) rotate(${currentRotation.value}deg)`
}))

const currentRotation = ref(0)

// 动态导入Three.js
async function loadThreeJS() {
  try {
    // 尝试动态导入three
    THREE = await import('three')
    use3D.value = true
    return true
  } catch (e) {
    console.warn('Three.js not available, using 2D fallback:', e)
    use3D.value = false
    return false
  }
}

// 初始化3D场景
async function init3DScene() {
  const canvas = document.getElementById('petCanvas')
  if (!canvas) return false
  
  try {
    const { GLTFLoader } = await import('three/examples/jsm/loaders/GLTFLoader.js')
    
    // 创建场景
    scene = new THREE.Scene()
    scene.background = new THREE.Color(0x87CEEB)
    scene.fog = new THREE.Fog(0x87CEEB, 20, 100)
    
    // 创建相机
    camera = new THREE.PerspectiveCamera(60, canvasWidth.value / canvasHeight.value, 0.1, 1000)
    camera.position.set(0, 8, 12)
    
    // 创建渲染器
    renderer = new THREE.WebGLRenderer({ canvas, antialias: true })
    renderer.setSize(canvasWidth.value, canvasHeight.value)
    renderer.shadowMap.enabled = true
    
    // 创建光照
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5)
    scene.add(ambientLight)
    
    const mainLight = new THREE.DirectionalLight(0xffffff, 1.2)
    mainLight.position.set(10, 20, 10)
    mainLight.castShadow = true
    scene.add(mainLight)
    
    // 创建地面
    const groundGeometry = new THREE.PlaneGeometry(60, 60)
    const groundMaterial = new THREE.MeshStandardMaterial({ 
      color: getSceneColor(props.sceneType),
      roughness: 0.8
    })
    ground = new THREE.Mesh(groundGeometry, groundMaterial)
    ground.rotation.x = -Math.PI / 2
    ground.receiveShadow = true
    ground.name = 'ground'
    scene.add(ground)
    
    // 加载宠物模型
    await loadPetModel3D(GLTFLoader)
    
    // 开始渲染循环
    animate3D()
    
    return true
  } catch (e) {
    console.error('3D init failed:', e)
    use3D.value = false
    return false
  }
}

// 获取场景颜色
function getSceneColor(sceneType) {
  const colors = {
    garden: 0x228B22,
    forest: 0x2D5016,
    beach: 0xF4E4BA,
    mountain: 0x808080,
    snowland: 0xFFFAFA,
    night: 0x1a1a2e
  }
  return colors[sceneType] || 0x228B22
}

// 加载3D宠物模型
async function loadPetModel3D(Loader) {
  const loader = new Loader()
  
  try {
    const gltf = await new Promise((resolve, reject) => {
      loader.load(
        props.petModelUrl,
        resolve,
        null,
        reject
      )
    })
    
    pet = gltf.scene
    pet.position.set(props.initialPosition.x, props.initialPosition.y, props.initialPosition.z)
    pet.scale.set(1, 1, 1)
    pet.traverse((child) => {
      if (child.isMesh) child.castShadow = true
    })
    scene.add(pet)
  } catch (e) {
    console.warn('Failed to load 3D model, creating fallback:', e)
    createFallbackPet3D()
  }
}

// 创建备用宠物
function createFallbackPet3D() {
  const geometry = new THREE.CapsuleGeometry(0.5, 1, 8, 16)
  const material = new THREE.MeshStandardMaterial({ 
    color: 0x90EE90,
    roughness: 0.5
  })
  
  pet = new THREE.Mesh(geometry, material)
  pet.position.set(props.initialPosition.x, 1, props.initialPosition.z)
  pet.castShadow = true
  scene.add(pet)
}

// 3D渲染循环
function animate3D() {
  animationId = requestAnimationFrame(animate3D)
  
  if (use3D.value && renderer && scene && camera) {
    // 更新宠物移动
    if (isMoving && pet && targetPosition) {
      update3DMovement()
    }
    
    // 更新相机
    updateCamera3D()
    
    renderer.render(scene, camera)
  }
}

// 更新3D宠物移动
function update3DMovement() {
  if (!pet || !targetPosition) return
  
  const dx = targetPosition.x - pet.position.x
  const dz = targetPosition.z - pet.position.z
  const distance = Math.sqrt(dx * dx + dz * dz)
  
  if (distance < 0.1) {
    isMoving = false
    targetPosition = null
    emit('actionEnd', { action: 'idle' })
    return
  }
  
  const speed = 0.05
  pet.position.x += (dx / distance) * speed
  pet.position.z += (dz / distance) * speed
  
  // 更新朝向
  const angle = Math.atan2(dx, dz)
  pet.rotation.y = angle
  
  emit('positionChange', {
    x: pet.position.x,
    y: pet.position.y,
    z: pet.position.z
  })
}

// 更新3D相机
function updateCamera3D() {
  if (!pet || !camera) return
  
  const targetPos = {
    x: pet.position.x,
    y: pet.position.y + 8,
    z: pet.position.z + 12
  }
  
  camera.position.x += (targetPos.x - camera.position.x) * 0.05
  camera.position.y += (targetPos.y - camera.position.y) * 0.05
  camera.position.z += (targetPos.z - camera.position.z) * 0.05
  
  camera.lookAt(pet.position.x, pet.position.y + 1, pet.position.z)
}

// 初始化2D场景
function init2DScene() {
  const canvas = document.getElementById('petCanvas2D')
  if (!canvas) return
  
  ctx2D = canvas.getContext('2d')
  
  // 绘制初始场景
  draw2DScene()
}

// 绘制2D场景
function draw2DScene() {
  if (!ctx2D) return
  
  const canvas = ctx2D.canvas
  const w = canvas.width
  const h = canvas.height
  
  // 清除画布
  ctx2D.clearRect(0, 0, w, h)
  
  // 绘制背景
  const gradient = ctx2D.createLinearGradient(0, 0, 0, h)
  gradient.addColorStop(0, '#87CEEB') // 天空
  gradient.addColorStop(0.6, '#87CEEB')
  gradient.addColorStop(0.6, getSceneColorHex(props.sceneType)) // 地面
  gradient.addColorStop(1, getSceneColorHex(props.sceneType))
  ctx2D.fillStyle = gradient
  ctx2D.fillRect(0, 0, w, h)
  
  // 绘制地面
  ctx2D.fillStyle = getSceneColorHex(props.sceneType)
  ctx2D.fillRect(0, h * 0.6, w, h * 0.4)
  
  // 绘制一些装饰
  draw2DDecorations()
  
  // 绘制宠物
  draw2DPet()
  
  // 继续动画
  if (!use3D.value) {
    requestAnimationFrame(draw2DScene)
  }
}

// 获取场景颜色Hex
function getSceneColorHex(sceneType) {
  const colors = {
    garden: '#228B22',
    forest: '#2D5016',
    beach: '#F4E4BA',
    mountain: '#808080',
    snowland: '#FFFAFA',
    night: '#1a1a2e'
  }
  return colors[sceneType] || '#228B22'
}

// 绘制装饰物
function draw2DDecorations() {
  if (!ctx2D) return
  
  const canvas = ctx2D.canvas
  const h = canvas.height
  
  // 绘制云朵
  ctx2D.fillStyle = 'rgba(255, 255, 255, 0.8)'
  drawCloud(50, 50, 30)
  drawCloud(150, 80, 25)
  drawCloud(280, 40, 35)
  
  // 绘制小草（花园场景）
  if (props.sceneType === 'garden') {
    ctx2D.fillStyle = '#32CD32'
    for (let i = 0; i < 10; i++) {
      const x = Math.random() * canvas.width
      const y = h * 0.65 + Math.random() * (h * 0.3)
      drawGrass(x, y)
    }
  }
}

// 绘制云朵
function drawCloud(x, y, size) {
  ctx2D.beginPath()
  ctx2D.arc(x, y, size, 0, Math.PI * 2)
  ctx2D.arc(x + size * 0.8, y - size * 0.2, size * 0.7, 0, Math.PI * 2)
  ctx2D.arc(x + size * 1.5, y, size * 0.8, 0, Math.PI * 2)
  ctx2D.fill()
}

// 绘制小草
function drawGrass(x, y) {
  ctx2D.fillRect(x - 2, y, 4, 15)
  ctx2D.beginPath()
  ctx2D.moveTo(x, y)
  ctx2D.lineTo(x - 5, y - 10)
  ctx2D.lineTo(x + 5, y - 10)
  ctx2D.closePath()
  ctx2D.fill()
}

// 绘制2D宠物
function draw2DPet() {
  if (!ctx2D) return
  
  ctx2D.save()
  ctx2D.translate(pet2D.x, pet2D.y)
  ctx2D.rotate(currentRotation.value * Math.PI / 180)
  
  // 宠物身体
  ctx2D.fillStyle = '#90EE90'
  ctx2D.beginPath()
  ctx2D.ellipse(0, 0, 30, 40, 0, 0, Math.PI * 2)
  ctx2D.fill()
  
  // 宠物眼睛
  ctx2D.fillStyle = '#000'
  ctx2D.beginPath()
  ctx2D.arc(-10, -15, 5, 0, Math.PI * 2)
  ctx2D.arc(10, -15, 5, 0, Math.PI * 2)
  ctx2D.fill()
  
  // 宠物眼睛高光
  ctx2D.fillStyle = '#fff'
  ctx2D.beginPath()
  ctx2D.arc(-8, -17, 2, 0, Math.PI * 2)
  ctx2D.arc(12, -17, 2, 0, Math.PI * 2)
  ctx2D.fill()
  
  // 宠物嘴巴
  ctx2D.strokeStyle = '#228B22'
  ctx2D.lineWidth = 2
  ctx2D.beginPath()
  ctx2D.arc(0, -5, 8, 0.2, Math.PI - 0.2)
  ctx2D.stroke()
  
  ctx2D.restore()
}

// 更新2D宠物位置
function update2DMovement() {
  if (!isMoving || !targetPosition) return
  
  const targetX = targetPosition.x * 10 + canvasWidth.value / 2
  const targetY = targetPosition.z * 10 + canvasHeight.value * 0.6
  
  const dx = targetX - pet2D.x
  const dy = targetY - pet2D.y
  const distance = Math.sqrt(dx * dx + dy * dy)
  
  if (distance < 5) {
    isMoving = false
    targetPosition = null
    emit('actionEnd', { action: 'idle' })
    return
  }
  
  const speed = 3
  pet2D.x += (dx / distance) * speed
  pet2D.y += (dy / distance) * speed
  
  // 更新朝向
  currentRotation.value = Math.atan2(dx, dy) * 180 / Math.PI - 90
  
  emit('positionChange', {
    x: (pet2D.x - canvasWidth.value / 2) / 10,
    y: 0,
    z: (pet2D.y - canvasHeight.value * 0.6) / 10
  })
}

// 移动宠物到目标位置
function movePetTo(target) {
  targetPosition = { x: target.x, y: target.y, z: target.z }
  isMoving = true
  
  emit('actionStart', { action: 'walk' })
}

// 停止移动
function stopMove() {
  isMoving = false
  targetPosition = null
  emit('actionEnd', { action: 'idle' })
}

// 执行互动动作
function doInteraction(actionType) {
  emit('actionStart', { action: actionType })
  
  // 模拟动画效果
  setTimeout(() => {
    emit('actionEnd', { action: 'idle' })
  }, 2000)
}

// 更新宠物位置
function updatePetPosition(x, y, z) {
  petPosition.value = { x, y, z }
  
  if (use3D.value && pet) {
    pet.position.set(x, y, z)
  } else {
    pet2D.x = x * 10 + canvasWidth.value / 2
    pet2D.y = z * 10 + canvasHeight.value * 0.6
  }
}

// 更新宠物等级信息
function updatePetLevel(level, title) {
  currentLevel.value = level
  levelTitle.value = title
}

// 处理画布点击
function onCanvasClick(event) {
  if (!props.enableClickMove) return
  
  // 获取点击位置（简化处理）
  const rect = event.currentTarget.getBoundingClientRect()
  const x = event.detail.x || event.clientX - rect.left
  const y = event.detail.y || event.clientY - rect.top
  
  // 转换为世界坐标
  const worldX = (x - canvasWidth.value / 2) / 10
  const worldZ = (y - canvasHeight.value * 0.6) / 10
  
  movePetTo({ x: worldX, y: 0, z: worldZ })
  emit('groundClick', { x: worldX, y: 0, z: worldZ })
}

// 调整画布大小
function resizeCanvas() {
  const container = document.querySelector('.pet-canvas-container')
  if (container) {
    canvasWidth.value = container.clientWidth
    canvasHeight.value = container.clientHeight
  }
}

// 初始化
onMounted(async () => {
  loading.value = true
  
  // 调整画布大小
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)
  
  // 尝试加载3D
  const has3D = await loadThreeJS()
  
  if (has3D) {
    const success = await init3DScene()
    if (!success) {
      use3D.value = false
      init2DScene()
    }
  } else {
    init2DScene()
  }
  
  loading.value = false
  emit('ready')
})

// 清理
onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', resizeCanvas)
})

// 监听场景类型变化
watch(() => props.sceneType, () => {
  if (!use3D.value) {
    // 重新绘制2D场景
  }
})

// 暴露方法
defineExpose({
  movePetTo,
  stopMove,
  doInteraction,
  updatePetPosition,
  updatePetLevel,
  getPetPosition: () => use3D.value && pet ? {
    x: pet.position.x,
    y: pet.position.y,
    z: pet.position.z
  } : {
    x: (pet2D.x - canvasWidth.value / 2) / 10,
    y: 0,
    z: (pet2D.y - canvasHeight.value * 0.6) / 10
  }
})
</script>

<style scoped>
.pet-canvas-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(180deg, #87CEEB 0%, #E0F6FF 100%);
}

.pet-canvas {
  display: block;
}

.pet-sprite {
  position: absolute;
  transition: left 0.1s linear, top 0.1s linear;
  z-index: 10;
}

.pet-image {
  width: 80px;
  height: 80px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  z-index: 10;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #e0e0e0;
  border-top-color: #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 20px;
  font-size: 28rpx;
  color: #666;
}

.pet-status-bar {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 10rpx;
  z-index: 5;
}

.status-item {
  display: flex;
  align-items: center;
  padding: 8rpx 16rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.status-icon {
  font-size: 24rpx;
  color: #333;
  font-weight: 500;
}
</style>
