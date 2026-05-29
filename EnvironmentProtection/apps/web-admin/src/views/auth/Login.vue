<template>
  <div class="login-page">
    <!-- Canvas 动态背景 -->
    <canvas ref="bgCanvas" class="bg-canvas" />

    <!-- 内容层 -->
    <div class="content-layer">
      <!-- 左侧品牌区 -->
      <div class="brand-side">
        <div class="brand-content">
          <div class="brand-logo">
            <svg viewBox="0 0 48 48" fill="none">
              <circle cx="24" cy="24" r="22" stroke="rgba(255,255,255,0.6)" stroke-width="1.5"/>
              <path d="M24 8 C18 8 14 14 14 20 C14 26 16 30 20 33 C22 34.5 23 36 24 38 C25 36 26 34.5 28 33 C32 30 34 26 34 20 C34 14 30 8 24 8Z" fill="rgba(255,255,255,0.9)" opacity="0.95"/>
              <path d="M24 14 L24 30 M20 22 C22 20 26 20 28 22" stroke="rgba(45,106,79,1)" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <h1 class="brand-title">绿法通</h1>
          <p class="brand-sub">白色污染治理 · AI 智慧普法平台</p>

          <div class="feature-list">
            <div class="feature-row" v-for="f in features" :key="f.label">
              <div class="feature-icon-wrap">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path :d="f.icon"/>
                </svg>
              </div>
              <span>{{ f.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-side">
        <div class="glass-form">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请选择登录身份，开始您的智慧普法之旅</p>
          </div>

          <!-- 角色选择 -->
          <div class="role-grid">
            <button
              v-for="role in ROLE_OPTIONS"
              :key="role.id"
              class="role-card"
              :class="{ active: form.role === role.id }"
              @click="form.role = role.id"
            >
              <div class="role-icon-wrap">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path :d="role.icon"/>
                </svg>
              </div>
              <span class="role-name">{{ role.name }}</span>
              <span class="role-desc">{{ role.desc }}</span>
            </button>
          </div>

          <!-- 登录标签 -->
          <div class="tab-bar">
            <button
              v-for="tab in loginTabs"
              :key="tab.key"
              class="tab-btn"
              :class="{ active: activeTab === tab.key }"
              @click="activeTab = tab.key"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path :d="tab.icon"/>
              </svg>
              {{ tab.name }}
            </button>
          </div>

          <!-- 密码登录 -->
          <el-form v-show="activeTab === 'password'" ref="pwdFormRef" :model="form" :rules="pwdRules" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"/>
                </svg>
                <el-input v-model="form.username" placeholder="用户名 / 手机号" size="large" />
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
              </div>
            </el-form-item>

            <div class="form-row">
              <el-checkbox v-model="form.remember">记住密码</el-checkbox>
              <el-link type="primary" :underline="false" @click="router.push('/forget')">忘记密码？</el-link>
            </div>

            <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleLogin">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4 M10 17l5-5-5-5 M15 12H3"/>
              </svg>
              登 录
            </el-button>
          </el-form>

          <!-- 验证码登录 -->
          <el-form v-show="activeTab === 'sms'" ref="smsFormRef" :model="smsForm" :rules="smsRules">
            <el-form-item prop="phone">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
                  <line x1="12" y1="18" x2="12.01" y2="18"/>
                </svg>
                <el-input v-model="smsForm.phone" placeholder="手机号" size="large" />
              </div>
            </el-form-item>
            <el-form-item prop="code">
              <div class="input-wrap sms-input">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
                <el-input v-model="smsForm.code" placeholder="验证码" size="large" />
                <el-button size="large" class="sms-btn" :disabled="countdown > 0" @click="handleSendCode">
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleSmsLogin">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4 M10 17l5-5-5-5 M15 12H3"/>
              </svg>
              登 录
            </el-button>
          </el-form>

          <!-- 演示提示 -->
          <div class="demo-tip">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <span>演示账号：任意用户名 + <strong>123456</strong></span>
          </div>

          <div class="register-tip">
            还没有账号？<el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { mockLogin } from '@/utils/mockUser.js'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const bgCanvas = ref()
let ctx, animationId, particles = []
let canvasTime = 0

const pwdFormRef = ref()
const smsFormRef = ref()
const loading = ref(false)
const activeTab = ref('password')
const countdown = ref(0)

const features = [
  { label: '政府监管', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4M5 21V10.85M19 21V10.85M9 21v-4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4' },
  { label: '律所服务', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  { label: '企业合规', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
  { label: '智能普法', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' }
]

const loginTabs = [
  { key: 'password', name: '密码登录', icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' },
  { key: 'sms', name: '验证码登录', icon: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' }
]

const ROLE_OPTIONS = [
  { id: 'government', name: '政府', desc: '监管普法', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4M5 21V10.85M19 21V10.85M9 21v-4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4' },
  { id: 'law_firm', name: '律所', desc: '法务支持', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  { id: 'enterprise', name: '企业', desc: '合规管理', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
  { id: 'platform', name: '平台', desc: '系统运维', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' }
]

const form = reactive({ username: '', password: '', role: 'government', remember: false })
const smsForm = reactive({ phone: '', code: '' })

const pwdRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const smsRules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// Canvas 动画：多张环保背景图渐变 + 粒子
function initCanvas() {
  const canvas = bgCanvas.value
  if (!canvas) return
  ctx = canvas.getContext('2d')

  function resize() {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  // 环境图片列表（使用高质免费图片）
  const bgImages = [
    'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80', // 高山湖泊
    'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1920&q=80', // 森林
    'https://images.unsplash.com/photo-1502786129293-79981df4e689?w=1920&q=80', // 高原草原
    'https://images.unsplash.com/photo-1510797215324-95aa89f43c33?w=1920&q=80', // 湿地沼泽
    'https://images.unsplash.com/photo-1448375240586-882707db888b?w=1920&q=80', // 森林小路
    'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1920&q=80', // 山脉日出
  ]

  const imgs = bgImages.map(src => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.src = src
    return img
  })

  // 备用渐变背景（图片加载失败时使用）
  function drawFallbackBg() {
    const grad = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    grad.addColorStop(0, '#1a5c3a')
    grad.addColorStop(0.5, '#2d7a50')
    grad.addColorStop(1, '#0f3d28')
    ctx.fillStyle = grad
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  let currentIdx = 0
  let nextIdx = 1
  let blendFactor = 0
  let blending = false
  let blendSpeed = 0.0012
  let allLoaded = false

  // 预加载（带超时）
  function waitForImgs() {
    // 3秒超时
    const timeout = setTimeout(() => {
      if (!allLoaded) {
        allLoaded = true
        drawFallbackBg()
        animate()
        setInterval(() => { blending = true }, 6000)
      }
    }, 3000)

    const loaded = imgs.every(img => img.complete && img.naturalWidth > 0)
    if (loaded) {
      clearTimeout(timeout)
      allLoaded = true
      animate()
      setInterval(() => { blending = true }, 6000)
    } else {
      setTimeout(waitForImgs, 200)
    }
  }
  waitForImgs()

  // 粒子系统
  for (let i = 0; i < 60; i++) {
    particles.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      r: Math.random() * 2 + 0.5,
      vx: (Math.random() - 0.5) * 0.3,
      vy: -Math.random() * 0.5 - 0.1,
      alpha: Math.random() * 0.5 + 0.1,
      color: Math.random() > 0.6 ? '59, 130, 246' : Math.random() > 0.5 ? '100, 180, 255' : '200, 220, 250'
    })
  }

  function drawBg(img, alpha) {
    ctx.save()
    ctx.globalAlpha = alpha
    if (img.complete && img.naturalWidth > 0) {
      const scale = Math.max(canvas.width / img.naturalWidth, canvas.height / img.naturalHeight)
      const w = img.naturalWidth * scale
      const h = img.naturalHeight * scale
      const x = (canvas.width - w) / 2
      const y = (canvas.height - h) / 2
      ctx.drawImage(img, x, y, w, h)
    }
    ctx.restore()
  }

  function drawOverlay() {
    // 渐变叠层（蓝灰调）
    const grad = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    grad.addColorStop(0, 'rgba(15, 23, 42, 0.45)')
    grad.addColorStop(0.5, 'rgba(30, 41, 59, 0.3)')
    grad.addColorStop(1, 'rgba(51, 65, 85, 0.5)')
    ctx.fillStyle = grad
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    // 底部渐变
    const bGrad = ctx.createLinearGradient(0, canvas.height - 200, 0, canvas.height)
    bGrad.addColorStop(0, 'rgba(15, 23, 42, 0)')
    bGrad.addColorStop(1, 'rgba(15, 23, 42, 0.6)')
    ctx.fillStyle = bGrad
    ctx.fillRect(0, canvas.height - 200, canvas.width, 200)
  }

  function drawParticles() {
    particles.forEach(p => {
      ctx.save()
      ctx.globalAlpha = p.alpha
      ctx.fillStyle = `rgba(${p.color}, 1)`
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
      ctx.fill()
      ctx.restore()

      p.x += p.vx + Math.sin(canvasTime * 0.001 + p.y * 0.01) * 0.2
      p.y += p.vy
      p.alpha += (Math.random() - 0.5) * 0.005
      p.alpha = Math.max(0.05, Math.min(0.6, p.alpha))

      if (p.y < -10) { p.y = canvas.height + 10; p.x = Math.random() * canvas.width }
      if (p.x < -10) p.x = canvas.width + 10
      if (p.x > canvas.width + 10) p.x = -10
    })
  }

  function drawTimeDecorations() {
    const t = canvasTime * 0.0003
    // 漂浮装饰
    for (let i = 0; i < 8; i++) {
      const angle = t * (0.3 + i * 0.2) + i * Math.PI / 4
      const radius = 150 + i * 60
      const x = canvas.width * 0.5 + Math.cos(angle) * radius
      const y = canvas.height * 0.5 + Math.sin(angle * 0.7) * radius * 0.5
      ctx.save()
      ctx.translate(x, y)
      ctx.rotate(t + i)
      ctx.globalAlpha = 0.06 + Math.sin(t * 2 + i) * 0.03
      ctx.fillStyle = '#3B82F6'
      ctx.beginPath()
      ctx.ellipse(0, 0, 12 + i * 2, 6 + i, 0, 0, Math.PI * 2)
      ctx.fill()
      ctx.restore()
    }
  }

  function animate() {
    canvasTime++
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    // 如果图片未加载完成，先显示渐变背景
    if (!allLoaded) {
      drawFallbackBg()
      animationId = requestAnimationFrame(animate)
      return
    }

    const currImg = imgs[currentIdx]
    const nextImg = imgs[nextIdx]

    drawBg(currImg, 1)

    if (blending && nextImg.complete) {
      drawBg(nextImg, blendFactor)
      blendFactor += blendSpeed
      if (blendFactor >= 1) {
        blendFactor = 0
        blending = false
        currentIdx = nextIdx
        nextIdx = (nextIdx + 1) % imgs.length
      }
    }

    drawOverlay()
    drawParticles()
    drawTimeDecorations()

    animationId = requestAnimationFrame(animate)
  }
}

const handleSendCode = () => {
  if (!smsForm.phone || !/^1[3-9]\d{9}$/.test(smsForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  ElMessage.success('验证码已发送')
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const handleLogin = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // 使用虚拟登录
    const result = await mockLogin(form.username, form.password)
    // 存储用户信息
    localStorage.setItem('lvfat_token', result.token)
    localStorage.setItem('lvfat_user', JSON.stringify(result.userInfo))
    ElMessage.success('登录成功，欢迎回来！')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

const handleSmsLogin = async () => {
  ElMessage.success('登录成功！')
  router.push('/')
}

onMounted(() => {
  initCanvas()
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
})
</script>

<style lang="scss" scoped>
.login-page {
  position: fixed;
  inset: 0;
  overflow: hidden;
}

.bg-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.content-layer {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  height: 100vh;
}

// 左侧品牌区
.brand-side {
  flex: 0 0 42%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.brand-content {
  max-width: 400px;
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.brand-logo {
  width: 72px;
  height: 72px;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 16px rgba(37, 99, 235, 0.4));

  svg {
    width: 100%;
    height: 100%;
  }
}

.brand-title {
  font-size: 42px;
  font-weight: 800;
  color: #fff;
  margin: 0 0 12px;
  letter-spacing: 4px;
  text-shadow: 0 2px 20px rgba(0,0,0,0.3);
}

.brand-sub {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.75);
  margin: 0 0 48px;
  letter-spacing: 1px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-row {
  display: flex;
  align-items: center;
  gap: 16px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  font-weight: 500;
  padding: 14px 20px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.14);
    transform: translateX(6px);
  }
}

.feature-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.3);
  border: 1px solid rgba(37, 99, 235, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  svg {
    width: 18px;
    height: 18px;
  }
}

// 右侧表单区
.form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.glass-form {
  width: 100%;
  max-width: 460px;
  padding: 40px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.22);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255,255,255,0.2);
  animation: fadeIn 0.8s ease-out 0.2s both;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.96) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.form-header {
  text-align: center;
  margin-bottom: 28px;

  h2 {
    font-size: 26px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 8px;
    text-shadow: 0 2px 8px rgba(0,0,0,0.2);
  }

  p {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.65);
    margin: 0;
  }
}

// 角色选择网格
.role-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 24px;
}

.role-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 8px;
  border-radius: 14px;
  border: 1.5px solid rgba(255, 255, 255, 0.18);
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: all 0.25s;
  color: rgba(255, 255, 255, 0.7);

  &:hover {
    border-color: rgba(37, 99, 235, 0.6);
    background: rgba(37, 99, 235, 0.12);
    color: #fff;
    transform: translateY(-2px);
  }

  &.active {
    border-color: #3B82F6;
    background: rgba(37, 99, 235, 0.2);
    color: #fff;
    box-shadow: 0 4px 16px rgba(37, 99, 235, 0.2);
  }

  .role-icon-wrap {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    background: rgba(37, 99, 235, 0.2);
    border: 1px solid rgba(37, 99, 235, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 18px;
      height: 18px;
    }
  }

  .role-name {
    font-size: 13px;
    font-weight: 600;
  }

  .role-desc {
    font-size: 11px;
    opacity: 0.6;
  }
}

// 登录标签
.tab-bar {
  display: flex;
  gap: 6px;
  padding: 4px;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 12px;
  margin-bottom: 20px;
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border: none;
  border-radius: 10px;
  background: transparent;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s;

  svg {
    width: 14px;
    height: 14px;
  }

  &:hover {
    color: rgba(255, 255, 255, 0.8);
  }

  &.active {
    background: rgba(37, 99, 235, 0.3);
    color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

// 输入框包装
.input-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.25s;

  &:focus-within {
    border-color: rgba(82, 183, 136, 0.6);
    background: rgba(255, 255, 255, 0.15);
    box-shadow: 0 0 0 3px rgba(82, 183, 136, 0.12);
  }

  .input-icon {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
    color: rgba(255, 255, 255, 0.5);
  }

  :deep(.el-input__wrapper) {
    background: transparent !important;
    box-shadow: none !important;
    padding: 0 !important;
  }

  :deep(.el-input__inner) {
    color: #fff !important;
    font-size: 15px;

    &::placeholder {
      color: rgba(255, 255, 255, 0.4) !important;
    }
  }
}

.sms-input {
  :deep(.el-input__wrapper) {
    flex: 1;
  }
}

.sms-btn {
  flex-shrink: 0;
  padding: 8px 14px !important;
  border-radius: 10px !important;
  font-size: 13px !important;
  background: rgba(82, 183, 136, 0.25) !important;
  border: 1px solid rgba(82, 183, 136, 0.4) !important;
  color: #fff !important;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: rgba(82, 183, 136, 0.4) !important;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  :deep(.el-checkbox__label) {
    color: rgba(255, 255, 255, 0.7) !important;
    font-size: 13px;
  }

  :deep(.el-link__inner) {
    color: rgba(255, 255, 255, 0.7) !important;
    font-size: 13px;
  }
}

.submit-btn {
  width: 100%;
  height: 50px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  letter-spacing: 2px;
  border-radius: 14px !important;
  background: linear-gradient(135deg, rgba(82, 183, 136, 0.85), rgba(45, 106, 79, 0.9)) !important;
  border: 1px solid rgba(82, 183, 136, 0.4) !important;
  color: #fff !important;
  box-shadow: 0 8px 24px rgba(82, 183, 136, 0.3) !important;
  transition: all 0.3s !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: linear-gradient(135deg, rgba(82, 183, 136, 1), rgba(45, 106, 79, 1)) !important;
    box-shadow: 0 12px 32px rgba(82, 183, 136, 0.4) !important;
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
}

.demo-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 12px;
  background: rgba(82, 183, 136, 0.12);
  border: 1px solid rgba(82, 183, 136, 0.25);
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);

  svg {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
    color: rgba(82, 183, 136, 0.8);
  }

  strong {
    color: #fff;
    font-weight: 600;
  }
}

.register-tip {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);

  :deep(.el-link__inner) {
    color: rgba(82, 183, 136, 0.9) !important;
    font-weight: 600;
  }
}

@media (max-width: 768px) {
  .brand-side { display: none; }
  .form-side { padding: 20px; }
  .glass-form { padding: 28px 24px; }
  .role-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
