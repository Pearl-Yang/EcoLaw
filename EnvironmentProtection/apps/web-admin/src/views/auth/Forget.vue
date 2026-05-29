<template>
  <div class="auth-page">
    <canvas ref="bgCanvas" class="bg-canvas" />
    <div class="content-layer">
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
        </div>
      </div>
      <div class="form-side">
        <div class="glass-form">
          <div class="form-header">
            <h2>找回密码</h2>
            <p>通过手机号验证，快速重置密码</p>
          </div>

          <!-- 步骤条 -->
          <div class="step-bar">
            <div class="step-item" :class="{ active: currentStep >= 1, done: currentStep > 1 }">
              <div class="step-dot">
                <svg v-if="currentStep > 1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                <span v-else>1</span>
              </div>
              <span>验证手机</span>
            </div>
            <div class="step-line" :class="{ active: currentStep > 1 }"></div>
            <div class="step-item" :class="{ active: currentStep >= 2, done: currentStep > 2 }">
              <div class="step-dot">
                <svg v-if="currentStep > 2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
                <span v-else>2</span>
              </div>
              <span>重置密码</span>
            </div>
            <div class="step-line" :class="{ active: currentStep > 2 }"></div>
            <div class="step-item" :class="{ active: currentStep >= 3 }">
              <div class="step-dot"><span>3</span></div>
              <span>完成</span>
            </div>
          </div>

          <!-- 步骤1: 验证手机 -->
          <el-form v-if="currentStep === 1" ref="form1Ref" :model="form1" :rules="rules1" label-position="top">
            <el-form-item label="手机号" prop="phone">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/>
                </svg>
                <el-input v-model="form1.phone" placeholder="输入注册时的手机号" size="large" />
              </div>
            </el-form-item>
            <el-form-item label="验证码" prop="code">
              <div class="input-wrap sms-input">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
                <el-input v-model="form1.code" placeholder="请输入验证码" size="large" />
                <el-button size="large" class="sms-btn" :disabled="countdown > 0" @click="handleSendCode">
                  {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleStep1">验证并下一步</el-button>
          </el-form>

          <!-- 步骤2: 重置密码 -->
          <el-form v-if="currentStep === 2" ref="form2Ref" :model="form2" :rules="rules2" label-position="top">
            <el-form-item label="新密码" prop="password">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <el-input v-model="form2.password" type="password" placeholder="8位以上，含大小写字母和数字" size="large" show-password />
              </div>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <el-input v-model="form2.confirmPassword" type="password" placeholder="再次输入新密码" size="large" show-password />
              </div>
            </el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleStep2">确认重置</el-button>
          </el-form>

          <!-- 步骤3: 完成 -->
          <div v-if="currentStep === 3" class="success-step">
            <div class="success-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </div>
            <h3>密码重置成功！</h3>
            <p>请使用新密码重新登录</p>
            <el-button type="primary" size="large" class="submit-btn" @click="router.push('/login')">前往登录</el-button>
          </div>

          <div class="back-link">
            <el-link type="primary" :underline="false" @click="router.push('/login')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>
              返回登录
            </el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const bgCanvas = ref()
const currentStep = ref(1)
const countdown = ref(0)
const form1Ref = ref()
const form2Ref = ref()
let animationId

const form1 = reactive({ phone: '', code: '' })
const form2 = reactive({ password: '', confirmPassword: '' })

const rules1 = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const validateConfirm = (rule, value, callback) => {
  if (value !== form2.password) callback(new Error('两次输入密码不一致'))
  else callback()
}

const rules2 = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

function initCanvas() {
  const canvas = bgCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  const bgImages = [
    'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80',
    'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1920&q=80',
    'https://images.unsplash.com/photo-1502786129293-79981df4e689?w=1920&q=80',
    'https://images.unsplash.com/photo-1510797215324-95aa89f43c33?w=1920&q=80',
    'https://images.unsplash.com/photo-1448375240586-882707db888b?w=1920&q=80',
    'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1920&q=80',
  ]
  const imgs = bgImages.map(src => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.src = src
    return img
  })
  let idx = 0, next = 1, blend = 0, blending = false
  let time = 0
  function wait() {
    if (imgs.every(i => i.complete && i.naturalWidth > 0)) { animate(); setInterval(() => { blending = true }, 6000) }
    else setTimeout(wait, 200)
  }
  wait()
  function drawBg(img, a) {
    ctx.save(); ctx.globalAlpha = a
    if (img.complete && img.naturalWidth > 0) {
      const s = Math.max(canvas.width / img.naturalWidth, canvas.height / img.naturalHeight)
      ctx.drawImage(img, (canvas.width - img.naturalWidth * s) / 2, (canvas.height - img.naturalHeight * s) / 2, img.naturalWidth * s, img.naturalHeight * s)
    }
    ctx.restore()
  }
  function overlay() {
    const g = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    g.addColorStop(0, 'rgba(20,60,40,0.3)'); g.addColorStop(0.5, 'rgba(15,50,35,0.2)'); g.addColorStop(1, 'rgba(30,80,55,0.35)')
    ctx.fillStyle = g; ctx.fillRect(0, 0, canvas.width, canvas.height)
  }
  function animate() {
    time++; ctx.clearRect(0, 0, canvas.width, canvas.height)
    drawBg(imgs[idx], 1)
    if (blending && imgs[next].complete) { drawBg(imgs[next], blend); blend += 0.0012; if (blend >= 1) { blend = 0; blending = false; idx = next; next = (next + 1) % imgs.length } }
    overlay()
    animationId = requestAnimationFrame(animate)
  }
}
onMounted(initCanvas)
onUnmounted(() => { if (animationId) cancelAnimationFrame(animationId) })

const handleSendCode = () => {
  if (!form1.phone || !/^1[3-9]\d{9}$/.test(form1.phone)) { ElMessage.warning('请输入正确的手机号'); return }
  ElMessage.success('验证码已发送'); countdown.value = 60
  const t = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(t) }, 1000)
}

const handleStep1 = async () => {
  const v = await form1Ref.value?.validate().catch(() => false)
  if (!v) return
  currentStep.value = 2
}

const handleStep2 = async () => {
  const v = await form2Ref.value?.validate().catch(() => false)
  if (!v) return
  ElMessage.success('密码重置成功')
  currentStep.value = 3
}
</script>

<style lang="scss" scoped>
.auth-page { position: fixed; inset: 0; overflow: hidden; }
.bg-canvas { position: absolute; inset: 0; width: 100%; height: 100%; }
.content-layer { position: relative; z-index: 1; display: flex; width: 100%; height: 100vh; }
.brand-side { flex: 0 0 42%; display: flex; align-items: center; justify-content: center; padding: 40px; }
.brand-content { max-width: 400px; text-align: center; }
.brand-logo { width: 72px; height: 72px; margin: 0 auto 24px; filter: drop-shadow(0 4px 16px rgba(82,183,136,0.4)); svg { width: 100%; height: 100%; } }
.brand-title { font-size: 42px; font-weight: 800; color: #fff; margin: 0 0 12px; letter-spacing: 4px; text-shadow: 0 2px 20px rgba(0,0,0,0.3); }
.brand-sub { font-size: 16px; color: rgba(255,255,255,0.75); margin: 0; letter-spacing: 1px; }
.form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px; }
.glass-form { width: 100%; max-width: 460px; padding: 40px; border-radius: 24px; background: rgba(255,255,255,0.12); backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px); border: 1px solid rgba(255,255,255,0.22); box-shadow: 0 20px 60px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.2); }
.form-header { text-align: center; margin-bottom: 28px; h2 { font-size: 26px; font-weight: 700; color: #fff; margin: 0 0 8px; text-shadow: 0 2px 8px rgba(0,0,0,0.2); } p { font-size: 14px; color: rgba(255,255,255,0.65); margin: 0; } }
// 步骤条
.step-bar { display: flex; align-items: center; justify-content: center; margin-bottom: 32px; gap: 0; }
.step-item { display: flex; flex-direction: column; align-items: center; gap: 6px; font-size: 12px; color: rgba(255,255,255,0.4); &.active { color: #fff; } &.done { color: #52b788; } }
.step-dot { width: 32px; height: 32px; border-radius: 50%; border: 2px solid rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; color: rgba(255,255,255,0.4); transition: all 0.3s; .active & { border-color: #52b788; background: rgba(82,183,136,0.2); color: #52b788; } .done & { border-color: #52b788; background: #52b788; color: #fff; } svg { width: 16px; height: 16px; } }
.step-line { flex: 1; height: 2px; background: rgba(255,255,255,0.15); min-width: 40px; margin-bottom: 18px; transition: background 0.3s; &.active { background: #52b788; } }
// 输入框
.input-wrap { display: flex; align-items: center; gap: 10px; padding: 4px 16px; border-radius: 12px; background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.18); transition: all 0.25s; &:focus-within { border-color: rgba(82,183,136,0.6); background: rgba(255,255,255,0.15); box-shadow: 0 0 0 3px rgba(82,183,136,0.12); } .input-icon { width: 18px; height: 18px; flex-shrink: 0; color: rgba(255,255,255,0.5); } :deep(.el-input__wrapper) { background: transparent !important; box-shadow: none !important; padding: 0 !important; } :deep(.el-input__inner) { color: #fff !important; font-size: 15px; &::placeholder { color: rgba(255,255,255,0.4) !important; } } }
.sms-input { :deep(.el-input__wrapper) { flex: 1; } }
.sms-btn { flex-shrink: 0; padding: 8px 14px !important; border-radius: 10px !important; font-size: 13px !important; background: rgba(82,183,136,0.25) !important; border: 1px solid rgba(82,183,136,0.4) !important; color: #fff !important; cursor: pointer; transition: all 0.2s; &:hover:not(:disabled) { background: rgba(82,183,136,0.4) !important; } &:disabled { opacity: 0.5; cursor: not-allowed; } }
.submit-btn { width: 100%; height: 50px !important; font-size: 16px !important; font-weight: 600 !important; letter-spacing: 2px; border-radius: 14px !important; background: linear-gradient(135deg, rgba(82,183,136,0.85), rgba(45,106,79,0.9)) !important; border: 1px solid rgba(82,183,136,0.4) !important; color: #fff !important; box-shadow: 0 8px 24px rgba(82,183,136,0.3) !important; transition: all 0.3s !important; display: flex !important; align-items: center !important; justify-content: center !important; margin-top: 8px; &:hover { background: linear-gradient(135deg, rgba(82,183,136,1), rgba(45,106,79,1)) !important; box-shadow: 0 12px 32px rgba(82,183,136,0.4) !important; transform: translateY(-1px); } }
.success-step { text-align: center; padding: 20px 0; .success-icon { width: 72px; height: 72px; border-radius: 50%; background: rgba(82,183,136,0.2); border: 2px solid #52b788; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; svg { width: 36px; height: 36px; color: #52b788; } } h3 { font-size: 22px; font-weight: 700; color: #fff; margin: 0 0 8px; } p { font-size: 14px; color: rgba(255,255,255,0.65); margin: 0 0 24px; } }
.back-link { text-align: center; margin-top: 20px; :deep(.el-link__inner) { color: rgba(82,183,136,0.8) !important; font-size: 14px; display: flex; align-items: center; gap: 6px; svg { width: 16px; height: 16px; } } }
@media (max-width: 768px) { .brand-side { display: none; } .form-side { padding: 20px; } .glass-form { padding: 28px 24px; } }
</style>
