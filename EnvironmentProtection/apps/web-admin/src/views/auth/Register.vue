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
          <div class="feature-list">
            <div class="feature-row">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4M5 21V10.85M19 21V10.85M9 21v-4a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4"/></svg>
              <span>政府监管</span>
            </div>
            <div class="feature-row">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
              <span>律所服务</span>
            </div>
            <div class="feature-row">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
              <span>企业合规</span>
            </div>
            <div class="feature-row">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01"/></svg>
              <span>智能普法</span>
            </div>
          </div>
        </div>
      </div>
      <div class="form-side">
        <div class="glass-form">
          <div class="form-header">
            <h2>账号注册</h2>
            <p>加入绿法通，共建绿色家园</p>
          </div>
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <div class="form-row-2">
              <el-form-item label="用户名" prop="username">
                <div class="input-wrap">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  <el-input v-model="form.username" placeholder="6-20位字母或数字" size="large" />
                </div>
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <div class="input-wrap">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  <el-input v-model="form.nickname" placeholder="显示名称" size="large" />
                </div>
              </el-form-item>
            </div>

            <el-form-item label="手机号" prop="phone">
              <div class="input-wrap">
                <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
                <el-input v-model="form.phone" placeholder="用于接收任务通知" size="large" />
              </div>
            </el-form-item>

            <div class="form-row-2">
              <el-form-item label="密码" prop="password">
                <div class="input-wrap">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  <el-input v-model="form.password" type="password" placeholder="8位以上，含大小写" size="large" show-password />
                </div>
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <div class="input-wrap">
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" size="large" show-password />
                </div>
              </el-form-item>
            </div>

            <el-form-item label="注册角色" prop="role">
              <div class="role-grid">
                <button v-for="r in roleOptions" :key="r.value" class="role-card" :class="{ active: form.role === r.value }" type="button" @click="form.role = r.value">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path :d="r.icon"/></svg>
                  <span>{{ r.label }}</span>
                </button>
              </div>
            </el-form-item>

            <div class="agree-row">
              <el-checkbox v-model="form.agree">
                我已阅读并同意 <el-link type="primary" :underline="false">《用户协议》</el-link> 和 <el-link type="primary" :underline="false">《隐私政策》</el-link>
              </el-checkbox>
            </div>

            <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="handleRegister">立即注册</el-button>

            <div class="login-tip">
              已有账号？<el-link type="primary" :underline="false" @click="router.push('/login')">前往登录</el-link>
            </div>
          </el-form>
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
const formRef = ref()
const loading = ref(false)
let animationId

const form = reactive({
  username: '', nickname: '', phone: '', password: '', confirmPassword: '', role: 'government', agree: false
})

const roleOptions = [
  { label: '政府监管员', value: 'government', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
  { label: '企业管理员', value: 'enterprise', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  { label: '律所管理员', value: 'law_firm', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  { label: '普通用户', value: 'user', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }
]

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 8, message: '密码至少8位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
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
  const imgs = bgImages.map(src => { const img = new Image(); img.crossOrigin = 'anonymous'; img.src = src; return img })

  // 备用渐变背景（图片加载失败时使用）
  function drawFallbackBg() {
    const g = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
    g.addColorStop(0, '#1a5c3a')
    g.addColorStop(0.5, '#2d7a50')
    g.addColorStop(1, '#0f3d28')
    ctx.fillStyle = g
    ctx.fillRect(0, 0, canvas.width, canvas.height)
  }

  let idx = 0, next = 1, blend = 0, blending = false
  let allLoaded = false

  // 预加载（带超时）
  function wait() {
    // 3秒超时
    const timeout = setTimeout(() => {
      if (!allLoaded) {
        allLoaded = true
        drawFallbackBg()
        animate()
        setInterval(() => { blending = true }, 6000)
      }
    }, 3000)

    if (imgs.every(i => i.complete && i.naturalWidth > 0)) {
      clearTimeout(timeout)
      allLoaded = true
      animate()
      setInterval(() => { blending = true }, 6000)
    } else setTimeout(wait, 200)
  }
  wait()

  function drawBg(img, a) {
    ctx.save()
    ctx.globalAlpha = a
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
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    if (!allLoaded) {
      drawFallbackBg()
      animationId = requestAnimationFrame(animate)
      return
    }

    drawBg(imgs[idx], 1)
    if (blending && imgs[next].complete) { drawBg(imgs[next], blend); blend += 0.0012; if (blend >= 1) { blend = 0; blending = false; idx = next; next = (next + 1) % imgs.length } }
    overlay()
    animationId = requestAnimationFrame(animate)
  }
}
onMounted(initCanvas)
onUnmounted(() => { if (animationId) cancelAnimationFrame(animationId) })

const handleRegister = async () => {
  if (!form.agree) { ElMessage.warning('请阅读并同意用户协议'); return }
  const v = await formRef.value?.validate().catch(() => false)
  if (!v) return
  loading.value = true
  await new Promise(r => setTimeout(r, 1500))
  ElMessage.success('注册成功，请登录！')
  loading.value = false
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.auth-page { position: fixed; inset: 0; overflow: hidden; }
.bg-canvas { position: absolute; inset: 0; width: 100%; height: 100%; }
.content-layer { position: relative; z-index: 1; display: flex; width: 100%; height: 100vh; }
.brand-side { flex: 0 0 42%; display: flex; align-items: center; justify-content: center; padding: 40px; }
.brand-content { max-width: 400px; }
.brand-logo { width: 72px; height: 72px; margin-bottom: 24px; filter: drop-shadow(0 4px 16px rgba(82,183,136,0.4)); svg { width: 100%; height: 100%; } }
.brand-title { font-size: 42px; font-weight: 800; color: #fff; margin: 0 0 12px; letter-spacing: 4px; text-shadow: 0 2px 20px rgba(0,0,0,0.3); }
.brand-sub { font-size: 16px; color: rgba(255,255,255,0.75); margin: 0 0 40px; }
.feature-list { display: flex; flex-direction: column; gap: 12px; }
.feature-row { display: flex; align-items: center; gap: 14px; color: rgba(255,255,255,0.9); font-size: 15px; font-weight: 500; padding: 12px 18px; border-radius: 12px; background: rgba(255,255,255,0.08); backdrop-filter: blur(8px); border: 1px solid rgba(255,255,255,0.12); transition: all 0.3s; &:hover { background: rgba(255,255,255,0.14); transform: translateX(6px); } svg { width: 18px; height: 18px; flex-shrink: 0; } }
.form-side { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px; overflow-y: auto; }
.glass-form { width: 100%; max-width: 540px; padding: 36px; border-radius: 24px; background: rgba(255,255,255,0.12); backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px); border: 1px solid rgba(255,255,255,0.22); box-shadow: 0 20px 60px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.2); }
.form-header { text-align: center; margin-bottom: 24px; h2 { font-size: 26px; font-weight: 700; color: #fff; margin: 0 0 8px; text-shadow: 0 2px 8px rgba(0,0,0,0.2); } p { font-size: 14px; color: rgba(255,255,255,0.65); margin: 0; } }
.form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.input-wrap { display: flex; align-items: center; gap: 10px; padding: 4px 14px; border-radius: 12px; background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.18); transition: all 0.25s; &:focus-within { border-color: rgba(82,183,136,0.6); background: rgba(255,255,255,0.15); box-shadow: 0 0 0 3px rgba(82,183,136,0.12); } .input-icon { width: 16px; height: 16px; flex-shrink: 0; color: rgba(255,255,255,0.5); } :deep(.el-input__wrapper) { background: transparent !important; box-shadow: none !important; padding: 0 !important; } :deep(.el-input__inner) { color: #fff !important; font-size: 14px; &::placeholder { color: rgba(255,255,255,0.4) !important; } } }
.role-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 8px; }
.role-card { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 12px 6px; border-radius: 12px; border: 1.5px solid rgba(255,255,255,0.15); background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.6); cursor: pointer; transition: all 0.25s; font-size: 12px; svg { width: 20px; height: 20px; } &:hover { border-color: rgba(82,183,136,0.5); color: #fff; background: rgba(82,183,136,0.1); } &.active { border-color: #52b788; background: rgba(82,183,136,0.18); color: #fff; } }
.agree-row { margin-bottom: 16px; :deep(.el-checkbox__label) { color: rgba(255,255,255,0.6) !important; font-size: 13px; } }
.submit-btn { width: 100%; height: 50px !important; font-size: 16px !important; font-weight: 600 !important; letter-spacing: 2px; border-radius: 14px !important; background: linear-gradient(135deg, rgba(82,183,136,0.85), rgba(45,106,79,0.9)) !important; border: 1px solid rgba(82,183,136,0.4) !important; color: #fff !important; box-shadow: 0 8px 24px rgba(82,183,136,0.3) !important; transition: all 0.3s !important; display: flex !important; align-items: center !important; justify-content: center !important; margin-bottom: 16px; &:hover { background: linear-gradient(135deg, rgba(82,183,136,1), rgba(45,106,79,1)) !important; transform: translateY(-1px); } }
.login-tip { text-align: center; font-size: 14px; color: rgba(255,255,255,0.5); :deep(.el-link__inner) { color: rgba(82,183,136,0.9) !important; font-weight: 600; } }
@media (max-width: 768px) { .brand-side { display: none; } .form-side { padding: 20px; } .glass-form { padding: 24px 20px; } .form-row-2 { grid-template-columns: 1fr; } .role-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
