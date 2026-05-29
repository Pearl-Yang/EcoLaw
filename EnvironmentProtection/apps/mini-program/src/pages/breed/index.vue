<template>
  <view class="breed-page">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <text class="nav-icon">&lt;</text>
      </view>
      <text class="nav-title">{{ pageTitle }}</text>
      <view class="nav-right">
        <view class="settings-btn" @click="showSettings">
          <text class="settings-icon">...</text>
        </view>
      </view>
    </view>

    <!-- 3D场景容器 -->
    <view class="scene-container">
      <PetCanvas
        ref="petCanvasRef"
        :pet-model-url="petModelUrl"
        :scene-type="currentScene"
        :initial-position="petPosition"
        :enable-joystick="true"
        :enable-click-move="false"
        @ready="onSceneReady"
        @positionChange="onPetPositionChange"
        @actionStart="onActionStart"
        @actionEnd="onActionEnd"
      />
      
      <!-- 地面点击控制器 -->
      <GroundController
        ref="groundControllerRef"
        :bounds="sceneBounds"
        @groundClick="onGroundClick"
        @moveStart="onMoveStart"
        @moveEnd="onMoveEnd"
      />
      
      <!-- 宠物状态HUD -->
      <view class="pet-hud">
        <!-- 宠物信息 -->
        <view class="pet-info-card">
          <view class="pet-avatar">
            <image class="avatar-img" :src="petInfo.petImage || '/static/images/pet_default.png'" mode="aspectFill"></image>
            <view v-if="petInfo.level" class="level-badge">Lv.{{ petInfo.level }}</view>
          </view>
          <view class="pet-details">
            <text class="pet-name">{{ petInfo.petName || '我的宠物' }}</text>
            <text class="pet-title">{{ petInfo.levelTitle || '新手' }}</text>
          </view>
        </view>
        
        <!-- 经验条 -->
        <view class="exp-bar-container">
          <view class="exp-bar">
            <view class="exp-fill" :style="{ width: expProgress + '%' }"></view>
          </view>
          <text class="exp-text">{{ petInfo.exp || 0 }} / {{ petInfo.nextLevelExp || 100 }}</text>
        </view>
      </view>
      
      <!-- 场景切换按钮 -->
      <view class="scene-switcher">
        <scroll-view class="scene-list" scroll-x>
          <view 
            v-for="scene in availableScenes" 
            :key="scene.sceneCode"
            class="scene-item"
            :class="{ 'is-active': currentScene === scene.sceneCode, 'is-locked': !scene.isUnlocked }"
            @click="switchScene(scene)"
          >
            <image class="scene-thumb" :src="scene.thumbnailUrl" mode="aspectFill"></image>
            <text class="scene-name">{{ scene.sceneName }}</text>
            <view v-if="!scene.isUnlocked" class="lock-icon">*</view>
          </view>
        </scroll-view>
      </view>
      
      <!-- 互动任务面板 -->
      <TaskPanel
        ref="taskPanelRef"
        :tasks="taskList"
        :pet-level="petInfo.level || 1"
        :pet-energy="petInfo.energy || 100"
        :today-stats="todayStats"
        @taskClick="onTaskClick"
        @taskExecute="onTaskExecute"
        @taskComplete="onTaskComplete"
        @refresh="loadTasks"
      />
      
      <!-- 虚拟摇杆 -->
      <view class="joystick-container">
        <VirtualJoystick
          ref="joystickRef"
          :enabled="!isInteracting"
          :radius="100"
          :knob-radius="35"
          :dead-zone="0.15"
          :sensitivity="1.2"
          @move="onJoystickMove"
          @start="onJoystickStart"
          @end="onJoystickEnd"
        />
      </view>
    </view>
    
    <!-- 设置弹窗 -->
    <view v-if="showSettingsModal" class="modal-overlay" @click="closeSettings">
      <view class="settings-modal" @click.stop>
        <text class="modal-title">设置</text>
        
        <view class="setting-item">
          <text class="setting-label">宠物模型</text>
          <picker mode="selector" :range="petModels" range-key="name" @change="onPetModelChange">
            <view class="picker-value">
              <text>{{ currentPetModel.name }}</text>
              <text class="picker-arrow">&gt;</text>
            </view>
          </picker>
        </view>
        
        <view class="setting-item">
          <text class="setting-label">背景音乐</text>
          <switch :checked="bgMusicEnabled" @change="toggleBgMusic" color="#4CAF50"/>
        </view>
        
        <view class="setting-item">
          <text class="setting-label">音效</text>
          <switch :checked="soundEnabled" @change="toggleSound" color="#4CAF50"/>
        </view>
        
        <view class="setting-item">
          <text class="setting-label">摇杆灵敏度</text>
          <slider 
            :value="joystickSensitivity" 
            min="0.5" 
            max="2" 
            step="0.1" 
            show-value
            @change="onSensitivityChange"
          />
        </view>
        
        <view class="modal-buttons">
          <button class="modal-btn cancel-btn" @click="closeSettings">取消</button>
          <button class="modal-btn confirm-btn" @click="saveSettings">保存</button>
        </view>
      </view>
    </view>
    
    <!-- 任务详情弹窗 -->
    <view v-if="showTaskDetail" class="modal-overlay" @click="closeTaskDetail">
      <view class="task-detail-modal" @click.stop>
        <view class="modal-header">
          <view class="task-icon-large" :class="'icon-' + selectedTask.taskType">
            <text>{{ getTaskIcon(selectedTask.taskType) }}</text>
          </view>
          <text class="task-title">{{ selectedTask.taskName }}</text>
        </view>
        
        <view class="modal-body">
          <text class="task-desc">{{ selectedTask.description }}</text>
          
          <view class="reward-section">
            <text class="section-title">任务奖励</text>
            <view class="reward-list">
              <view class="reward-item">
                <text class="reward-icon">*</text>
                <text class="reward-name">经验</text>
                <text class="reward-value exp-color">+{{ selectedTask.expReward }}</text>
              </view>
              <view class="reward-item">
                <text class="reward-icon">$</text>
                <text class="reward-name">金币</text>
                <text class="reward-value coin-color">+{{ selectedTask.coinReward }}</text>
              </view>
            </view>
          </view>
          
          <view class="info-section">
            <view class="info-row">
              <text class="info-label">冷却时间</text>
              <text class="info-value">{{ selectedTask.cooldownSeconds }}秒</text>
            </view>
            <view class="info-row">
              <text class="info-label">体力消耗</text>
              <text class="info-value">{{ selectedTask.energyCost || 0 }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">等级要求</text>
              <text class="info-value">Lv.{{ selectedTask.minPetLevel }}</text>
            </view>
          </view>
        </view>
        
        <view class="modal-buttons">
          <button class="modal-btn cancel-btn" @click="closeTaskDetail">关闭</button>
          <button 
            class="modal-btn confirm-btn" 
            :disabled="!canExecuteTask"
            @click="startTaskExecution"
          >
            {{ canExecuteTask ? '开始互动' : '等级不足' }}
          </button>
        </view>
      </view>
    </view>
    
    <!-- 升级提示弹窗 -->
    <view v-if="showLevelUp" class="level-up-overlay">
      <view class="level-up-content">
        <view class="level-up-anim">
          <text class="level-up-icon">^</text>
        </view>
        <text class="level-up-title">升级!</text>
        <text class="level-up-level">Lv.{{ newLevel }}</text>
        <text class="level-up-subtitle">{{ newLevelTitle }}</text>
        <view class="level-up-rewards">
          <text class="reward-hint">获得奖励：</text>
          <text class="reward-text">+{{ levelUpExp }} 经验</text>
        </view>
        <button class="level-up-btn" @click="closeLevelUp">太棒了!</button>
      </view>
    </view>
    
    <!-- 成就解锁提示 -->
    <view v-if="showAchievement" class="achievement-overlay">
      <view class="achievement-content">
        <text class="achievement-title">成就解锁</text>
        <view class="achievement-icon">
          <text>*</text>
        </view>
        <text class="achievement-name">{{ unlockedAchievement.achievementName }}</text>
        <text class="achievement-desc">{{ unlockedAchievement.description }}</text>
        <button class="achievement-btn" @click="closeAchievement">确定</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import { api } from '@/api'
import PetCanvas from './PetCanvas.vue'
import VirtualJoystick from './VirtualJoystick.vue'
import GroundController from './GroundController.vue'
import TaskPanel from './TaskPanel.vue'

// 页面状态
const pageTitle = ref('宠物养成')
const isLoading = ref(true)
const isInteracting = ref(false)
const currentScene = ref('garden')

// 宠物信息
const petInfo = reactive({
  petId: null,
  petName: '我的宠物',
  petType: 'leaf_balance',
  petImage: null,
  level: 1,
  exp: 0,
  nextLevelExp: 100,
  levelTitle: '新手',
  energy: 100,
  emotion: 100
})

// 宠物位置
const petPosition = reactive({ x: 0, y: 0, z: 0 })

// 场景信息
const availableScenes = ref([])
const sceneBounds = reactive({
  minX: -30,
  maxX: 30,
  minZ: -30,
  maxZ: 30
})

// 经验进度
const expProgress = computed(() => {
  if (!petInfo.nextLevelExp || petInfo.nextLevelExp === 0) return 0
  return Math.min(100, (petInfo.exp / petInfo.nextLevelExp) * 100)
})

// 今日统计
const todayStats = reactive({
  completedCount: 0,
  expGained: 0
})

// 任务列表
const taskList = ref([])

// 摇杆控制
let joystickInterval = null
let currentJoystickInput = null

// 宠物模型选项
const petModels = ref([
  { name: '默认宠物', url: '/static/models/pet.glb' },
  { name: '叶子天平', url: '/static/models/leaf_balance.glb' },
  { name: '能源叶子', url: '/static/models/energy_leaf.glb' },
  { name: '法律之树', url: '/static/models/law_tree.glb' }
])
const currentPetModel = ref(petModels.value[0])
const petModelUrl = computed(() => currentPetModel.value.url)

// 设置相关
const showSettingsModal = ref(false)
const bgMusicEnabled = ref(true)
const soundEnabled = ref(true)
const joystickSensitivity = ref(1.0)

// 任务详情弹窗
const showTaskDetail = ref(false)
const selectedTask = ref({})

// 升级提示
const showLevelUp = ref(false)
const newLevel = ref(0)
const newLevelTitle = ref('')
const levelUpExp = ref(0)

// 成就提示
const showAchievement = ref(false)
const unlockedAchievement = ref({})

// Refs
const petCanvasRef = ref(null)
const joystickRef = ref(null)
const groundControllerRef = ref(null)
const taskPanelRef = ref(null)

// 用户ID（从全局获取）
const userId = ref(uni.getStorageSync('userId') || 1)

// 生命周期
onMounted(async () => {
  await initPage()
})

onUnmounted(() => {
  stopJoystickControl()
  // 停止背景音乐等清理工作
})

// 初始化页面
async function initPage() {
  isLoading.value = true
  
  try {
    // 加载宠物信息
    await loadPetInfo()
    
    // 加载场景信息
    await loadScenes()
    
    // 加载任务列表
    await loadTasks()
    
    // 加载今日统计
    await loadTodayStats()
    
    isLoading.value = false
  } catch (error) {
    console.error('初始化页面失败:', error)
    isLoading.value = false
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

// 加载宠物信息
async function loadPetInfo() {
  try {
    const res = await api.pet.getInfo(userId.value)
    if (res.code === 200 && res.data) {
      const data = res.data
      petInfo.petId = data.petId
      petInfo.petName = data.petName || '我的宠物'
      petInfo.petType = data.petType
      petInfo.petImage = data.petThumbnailUrl
      petInfo.level = data.level || 1
      petInfo.exp = data.exp || 0
      petInfo.nextLevelExp = data.nextLevelExp || 100
      petInfo.levelTitle = data.levelTitle || '新手'
      petInfo.energy = data.energy || 100
      petInfo.emotion = data.emotion || 100
      
      // 更新PetCanvas的等级显示
      if (petCanvasRef.value) {
        petCanvasRef.value.updatePetLevel(petInfo.level, petInfo.levelTitle)
      }
    }
  } catch (error) {
    console.error('加载宠物信息失败:', error)
  }
}

// 加载场景列表
async function loadScenes() {
  try {
    const res = await request.get('/pet/breed/scenes')
    if (res) {
      availableScenes.value = res.map(scene => ({
        ...scene,
        isUnlocked: scene.isUnlocked !== false
      }))
      
      // 更新当前场景
      if (availableScenes.value.length > 0) {
        const defaultScene = availableScenes.value.find(s => s.isDefault) || availableScenes.value[0]
        currentScene.value = defaultScene.sceneCode
        updateSceneBounds(defaultScene)
      }
    }
  } catch (error) {
    console.error('加载场景列表失败:', error)
    // 使用默认场景
    availableScenes.value = [
      { sceneCode: 'garden', sceneName: '阳光花园', isDefault: true, isUnlocked: true }
    ]
  }
}

// 加载任务列表
async function loadTasks() {
  try {
    const res = await request.get('/pet/breed/tasks', { userId: userId.value, petId: petInfo.petId })
    if (res) {
      taskList.value = res.map(task => ({
        ...task,
        available: task.minPetLevel <= petInfo.level,
        unavailableReason: task.minPetLevel > petInfo.level ? '等级不足' : null
      }))
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
    // 使用示例任务
    taskList.value = getSampleTasks()
  }
}

// 加载今日统计
async function loadTodayStats() {
  try {
    const res = await request.get('/pet/breed/stats/today', { userId: userId.value, petId: petInfo.petId })
    if (res) {
      todayStats.completedCount = res.todayInteractionCount || 0
      todayStats.expGained = res.todayExpGained || 0
    }
  } catch (error) {
    console.error('加载今日统计失败:', error)
  }
}

// 获取示例任务
function getSampleTasks() {
  return [
    {
      taskId: 1,
      taskCode: 'feed_01',
      taskName: '喂食小零食',
      taskType: 'feed',
      description: '给宠物喂一颗美味的小零食',
      expReward: 5,
      coinReward: 2,
      cooldownSeconds: 60,
      minPetLevel: 1,
      energyCost: 0,
      durationSeconds: 3,
      available: true
    },
    {
      taskId: 2,
      taskCode: 'pet_01',
      taskName: '轻轻抚摸',
      taskType: 'pet',
      description: '温柔地抚摸宠物的头部',
      expReward: 3,
      coinReward: 1,
      cooldownSeconds: 30,
      minPetLevel: 1,
      energyCost: 0,
      durationSeconds: 2,
      available: true
    },
    {
      taskId: 3,
      taskCode: 'clean_01',
      taskName: '梳理毛发',
      taskType: 'clean',
      description: '用梳子仔细梳理宠物的毛发',
      expReward: 5,
      coinReward: 2,
      cooldownSeconds: 120,
      minPetLevel: 1,
      energyCost: 0,
      durationSeconds: 4,
      available: true
    },
    {
      taskId: 4,
      taskCode: 'train_01',
      taskName: '基础训练',
      taskType: 'train',
      description: '教宠物一些简单的基础动作',
      expReward: 8,
      coinReward: 3,
      cooldownSeconds: 180,
      minPetLevel: 2,
      energyCost: 3,
      durationSeconds: 5,
      available: petInfo.level >= 2
    },
    {
      taskId: 5,
      taskCode: 'play_01',
      taskName: '追逐游戏',
      taskType: 'play',
      description: '和宠物玩追逐游戏',
      expReward: 6,
      coinReward: 2,
      cooldownSeconds: 120,
      minPetLevel: 1,
      energyCost: 2,
      durationSeconds: 4,
      available: true
    }
  ]
}

// 更新场景边界
function updateSceneBounds(scene) {
  if (scene.mapBounds) {
    sceneBounds.minX = scene.mapBounds.xMin || -30
    sceneBounds.maxX = scene.mapBounds.xMax || 30
    sceneBounds.minZ = scene.mapBounds.zMin || -30
    sceneBounds.maxZ = scene.mapBounds.zMax || 30
  }
}

// 返回
function goBack() {
  uni.navigateBack()
}

// 场景切换
async function switchScene(scene) {
  if (!scene.isUnlocked) {
    uni.showToast({
      title: '场景未解锁',
      icon: 'none'
    })
    return
  }
  
  if (scene.sceneCode === currentScene.value) return
  
  try {
    const res = await request.post('/pet/breed/scene/switch', null, { params: { 
      userId: userId.value, 
      petId: petInfo.petId, 
      sceneCode: scene.sceneCode 
    }})
    if (res) {
      currentScene.value = scene.sceneCode
      updateSceneBounds(scene)
      
      // 重置宠物位置到新场景出生点
      if (res.positionX !== undefined) {
        petPosition.x = res.positionX
        petPosition.z = res.positionZ
      }
    }
  } catch (error) {
    console.error('切换场景失败:', error)
    currentScene.value = scene.sceneCode
  }
}

// 场景就绪
function onSceneReady() {
  console.log('场景准备就绪')
}

// 宠物位置变化
function onPetPositionChange(position) {
  petPosition.x = position.x
  petPosition.y = position.y
  petPosition.z = position.z
  
  // 更新地面控制器
  if (groundControllerRef.value) {
    groundControllerRef.value.updatePetPosition(position.x, position.z)
  }
  
  // 同步位置到服务器
  debounceSyncPosition(position)
}

// 防抖同步位置
let syncPositionTimer = null
function debounceSyncPosition(position) {
  if (syncPositionTimer) clearTimeout(syncPositionTimer)
  syncPositionTimer = setTimeout(() => {
    syncPositionToServer(position)
  }, 2000)
}

// 同步位置到服务器
async function syncPositionToServer(position) {
  try {
    await request.post('/pet/breed/move', {
      userId: userId.value,
      petId: petInfo.petId,
      targetX: position.x,
      targetY: position.y,
      targetZ: position.z,
      moveType: 'click'
    })
  } catch (error) {
    console.error('同步位置失败:', error)
  }
}

// 动作开始
function onActionStart(data) {
  isInteracting.value = true
  stopJoystickControl()
}

// 动作结束
function onActionEnd(data) {
  isInteracting.value = false
}

// 地面点击
function onGroundClick(data) {
  console.log('地面点击:', data)
  // 宠物移动到点击位置
  if (petCanvasRef.value) {
    petCanvasRef.value.movePetTo({
      x: data.worldX,
      y: data.worldY || 0,
      z: data.worldZ
    })
  }
}

// 移动开始
function onMoveStart(data) {
  console.log('移动开始:', data)
}

// 移动结束
function onMoveEnd() {
  console.log('移动结束')
}

// 摇杆移动
function onJoystickMove(data) {
  if (!petCanvasRef.value || isInteracting.value) return
  
  currentJoystickInput = data
  
  // 启动摇杆控制循环
  if (!joystickInterval) {
    startJoystickControl()
  }
}

// 摇杆开始
function onJoystickStart(data) {
  console.log('摇杆开始:', data)
}

// 摇杆结束
function onJoystickEnd(data) {
  console.log('摇杆结束:', data)
  stopJoystickControl()
  
  // 宠物停止移动
  if (petCanvasRef.value) {
    petCanvasRef.value.stopMove()
  }
}

// 启动摇杆控制循环
function startJoystickControl() {
  if (joystickInterval) return
  
  joystickInterval = setInterval(() => {
    if (!currentJoystickInput || !petCanvasRef.value || isInteracting.value) {
      return
    }
    
    const { angle, power } = currentJoystickInput
    
    // 计算移动方向
    const radians = angle * Math.PI / 180
    const moveX = Math.sin(radians) * power * joystickSensitivity.value * 0.1
    const moveZ = -Math.cos(radians) * power * joystickSensitivity.value * 0.1
    
    // 更新宠物位置
    const newX = Math.max(sceneBounds.minX, Math.min(sceneBounds.maxX, petPosition.x + moveX))
    const newZ = Math.max(sceneBounds.minZ, Math.min(sceneBounds.maxZ, petPosition.z + moveZ))
    
    petCanvasRef.value.updatePetPosition(newX, 0, newZ)
  }, 16) // 约60fps
}

// 停止摇杆控制
function stopJoystickControl() {
  if (joystickInterval) {
    clearInterval(joystickInterval)
    joystickInterval = null
  }
  currentJoystickInput = null
}

// 任务点击
function onTaskClick(task) {
  selectedTask.value = task
  showTaskDetail.value = true
}

// 任务执行
function onTaskExecute(task) {
  console.log('开始执行任务:', task)
  
  // 播放宠物动作
  if (petCanvasRef.value) {
    petCanvasRef.value.doInteraction(task.animationType || task.taskType)
  }
}

// 任务完成
async function onTaskComplete(result) {
  console.log('任务完成:', result)
  
  // 更新今日统计
  todayStats.completedCount++
  todayStats.expGained += result.expGained
  
  // 更新宠物经验
  petInfo.exp += result.expGained
  
  // 检查升级
  if (result.levelUp) {
    newLevel.value = result.newLevel
    newLevelTitle.value = result.levelTitle
    levelUpExp.value = result.expGained
    showLevelUp.value = true
    petInfo.level = result.newLevel
  }
  
  // 检查成就解锁
  if (result.unlockedAchievements && result.unlockedAchievements.length > 0) {
    unlockedAchievement.value = result.unlockedAchievements[0]
    showAchievement.value = true
  }
  
  // 重新加载任务列表（更新冷却状态）
  await loadTasks()
  
  // 同步到服务器
  try {
    await request.post('/pet/breed/complete-task', {
      userId: userId.value,
      petId: petInfo.petId,
      taskCode: result.taskCode,
      durationMs: (taskList.value.find(t => t.taskCode === result.taskCode)?.durationSeconds || 5) * 1000
    })
  } catch (error) {
    console.error('同步任务完成失败:', error)
  }
}

// 能否执行任务
const canExecuteTask = computed(() => {
  if (!selectedTask.value) return false
  return selectedTask.value.minPetLevel <= petInfo.level
})

// 获取任务图标
function getTaskIcon(taskType) {
  const icons = {
    feed: '@',
    pet: '#',
    clean: '$',
    train: '%',
    play: '&'
  }
  return icons[taskType] || '?'
}

// 关闭任务详情
function closeTaskDetail() {
  showTaskDetail.value = false
}

// 开始任务执行
function startTaskExecution() {
  if (!canExecuteTask.value) return
  
  closeTaskDetail()
  onTaskExecute(selectedTask.value)
}

// 显示设置
function showSettings() {
  showSettingsModal.value = true
}

// 关闭设置
function closeSettings() {
  showSettingsModal.value = false
}

// 保存设置
function saveSettings() {
  closeSettings()
  uni.showToast({
    title: '设置已保存',
    icon: 'success'
  })
}

// 宠物模型变更
function onPetModelChange(e) {
  const index = e.detail.value
  currentPetModel.value = petModels.value[index]
}

// 切换背景音乐
function toggleBgMusic(e) {
  bgMusicEnabled.value = e.detail.value
  // 实现背景音乐切换逻辑
}

// 切换音效
function toggleSound(e) {
  soundEnabled.value = e.detail.value
  // 实现音效切换逻辑
}

// 灵敏度变更
function onSensitivityChange(e) {
  joystickSensitivity.value = e.detail.value
  if (joystickRef.value) {
    joystickRef.value.setSensitivity?.(e.detail.value)
  }
}

// 关闭升级提示
function closeLevelUp() {
  showLevelUp.value = false
}

// 关闭成就提示
function closeAchievement() {
  showAchievement.value = false
}
</script>

<style scoped>
.breed-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 88rpx;
  padding: 0 30rpx;
  background: linear-gradient(135deg, #4CAF50, #388E3C);
  color: #fff;
  z-index: 100;
}

.nav-left, .nav-right {
  width: 80rpx;
}

.nav-icon {
  font-size: 40rpx;
  font-weight: bold;
}

.nav-title {
  font-size: 34rpx;
  font-weight: bold;
}

.settings-btn {
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-icon {
  font-size: 40rpx;
  transform: rotate(90deg);
}

.scene-container {
  position: relative;
  flex: 1;
  overflow: hidden;
}

.pet-hud {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  right: 120rpx;
  z-index: 50;
}

.pet-info-card {
  display: flex;
  align-items: center;
  padding: 16rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.pet-avatar {
  position: relative;
  width: 80rpx;
  height: 80rpx;
  margin-right: 16rpx;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #f0f0f0;
}

.level-badge {
  position: absolute;
  bottom: -8rpx;
  left: 50%;
  transform: translateX(-50%);
  padding: 4rpx 12rpx;
  background: #FF9800;
  border-radius: 20rpx;
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
  white-space: nowrap;
}

.pet-details {
  flex: 1;
}

.pet-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.pet-title {
  font-size: 24rpx;
  color: #4CAF50;
  margin-top: 4rpx;
}

.exp-bar-container {
  margin-top: 12rpx;
  padding: 12rpx 16rpx;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.exp-bar {
  height: 12rpx;
  background: #e0e0e0;
  border-radius: 6rpx;
  overflow: hidden;
}

.exp-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF9800, #FFC107);
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.exp-text {
  font-size: 22rpx;
  color: #666;
  margin-top: 8rpx;
  display: block;
  text-align: right;
}

.scene-switcher {
  position: absolute;
  bottom: 300rpx;
  left: 0;
  right: 0;
  z-index: 50;
}

.scene-list {
  display: flex;
  padding: 0 20rpx;
  white-space: nowrap;
}

.scene-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  width: 120rpx;
  margin-right: 20rpx;
  opacity: 0.6;
  transition: all 0.2s ease;
}

.scene-item.is-active {
  opacity: 1;
  transform: scale(1.1);
}

.scene-item.is-locked {
  opacity: 0.3;
}

.scene-thumb {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  background: #e0e0e0;
}

.scene-name {
  font-size: 22rpx;
  color: #fff;
  margin-top: 8rpx;
  text-shadow: 0 1px 3rpx rgba(0, 0, 0, 0.5);
  white-space: nowrap;
}

.lock-icon {
  position: absolute;
  top: 30rpx;
  font-size: 40rpx;
  color: #fff;
}

.joystick-container {
  position: absolute;
  bottom: 60rpx;
  left: 40rpx;
  z-index: 60;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.settings-modal, .task-detail-modal {
  width: 600rpx;
  padding: 40rpx;
  background: #fff;
  border-radius: 24rpx;
}

.modal-title, .modal-header .task-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  display: block;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-label {
  font-size: 28rpx;
  color: #333;
}

.picker-value {
  display: flex;
  align-items: center;
  color: #666;
}

.picker-arrow {
  margin-left: 8rpx;
}

.modal-buttons {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.modal-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 30rpx;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background: #4CAF50;
  color: #fff;
}

.confirm-btn[disabled] {
  background: #ccc;
}

/* 任务详情弹窗 */
.modal-header {
  text-align: center;
  padding-bottom: 30rpx;
  border-bottom: 1px solid #f0f0f0;
}

.task-icon-large {
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 20rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  color: #fff;
}

.icon-feed { background: linear-gradient(135deg, #FF9800, #FF5722); }
.icon-pet { background: linear-gradient(135deg, #E91E63, #C2185B); }
.icon-clean { background: linear-gradient(135deg, #03A9F4, #2196F3); }
.icon-train { background: linear-gradient(135deg, #9C27B0, #7B1FA2); }
.icon-play { background: linear-gradient(135deg, #4CAF50, #8BC34A); }

.modal-body {
  padding: 30rpx 0;
}

.task-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: block;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.reward-section, .info-section {
  margin-top: 30rpx;
}

.reward-list {
  display: flex;
  gap: 30rpx;
}

.reward-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.reward-icon {
  font-size: 28rpx;
}

.reward-name {
  font-size: 26rpx;
  color: #666;
}

.reward-value {
  font-size: 28rpx;
  font-weight: bold;
}

.exp-color { color: #FF9800; }
.coin-color { color: #FFC107; }

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
}

.info-label {
  font-size: 26rpx;
  color: #666;
}

.info-value {
  font-size: 26rpx;
  color: #333;
}

/* 升级弹窗 */
.level-up-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.level-up-content {
  text-align: center;
  color: #fff;
}

.level-up-anim {
  animation: bounce 1s infinite;
}

.level-up-icon {
  font-size: 120rpx;
  color: #FF9800;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.level-up-title {
  font-size: 60rpx;
  font-weight: bold;
  color: #FF9800;
  margin-top: 20rpx;
  display: block;
}

.level-up-level {
  font-size: 100rpx;
  font-weight: bold;
  display: block;
  margin-top: 10rpx;
}

.level-up-subtitle {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-top: 10rpx;
}

.level-up-rewards {
  margin-top: 40rpx;
}

.reward-hint {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.6);
}

.reward-text {
  font-size: 28rpx;
  color: #FF9800;
  margin-left: 10rpx;
}

.level-up-btn {
  margin-top: 50rpx;
  padding: 20rpx 60rpx;
  background: #FF9800;
  border-radius: 40rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}

/* 成就弹窗 */
.achievement-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.achievement-content {
  text-align: center;
  color: #fff;
  padding: 60rpx;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  border-radius: 24rpx;
}

.achievement-title {
  font-size: 32rpx;
  color: rgba(0, 0, 0, 0.6);
}

.achievement-icon {
  width: 150rpx;
  height: 150rpx;
  margin: 30rpx auto;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
}

.achievement-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.achievement-desc {
  font-size: 26rpx;
  color: rgba(0, 0, 0, 0.7);
  display: block;
}

.achievement-btn {
  margin-top: 40rpx;
  padding: 20rpx 80rpx;
  background: #333;
  border-radius: 40rpx;
  color: #fff;
  font-size: 30rpx;
}
</style>
