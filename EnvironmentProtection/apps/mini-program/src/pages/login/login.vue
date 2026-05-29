<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AuthHero from '../../components/auth/AuthHero.vue'
import SvgIcon from '../../components/auth/SvgIcon.vue'
import {
  loginRequest,
  applyDemoLogin,
  DEMO_SMS_CODE,
  DEMO_PASSWORD
} from '../../services/auth.js'
import { api } from '../../api/index.js'

const ROLE_OPTIONS = [
  { id: 'grass_roots', label: '基层用户' },
  { id: 'enterprise_employee', label: '企业员工' }
]

const selectedRole = ref('grass_roots')
const formPhone = ref('')
const formCode = ref('')
const formPassword = ref('')
const formAgree = ref(false)
const codeSentFlag = ref(false)
const countdownVal = ref(60)
const rememberPhone = ref(true)
const loginMode = ref('password')
const showPassword = ref(false)
let timer = null

const isCodeMode = computed(() => loginMode.value === 'code')

onMounted(() => {
  const saved = uni.getStorageSync('rememberedPhone')
  if (saved) formPhone.value = saved
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function selectRole(id) {
  selectedRole.value = id
}

async function onSendCode() {
  if (!formPhone.value || formPhone.value.length < 11) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  if (codeSentFlag.value) return
  try {
    await api.auth.sendSmsCode({ phone: formPhone.value, scene: 'login' })
  } catch {
    /* 忽略 */
  }
  codeSentFlag.value = true
  countdownVal.value = 60
  uni.showToast({
    title: `验证码已发送（演示可填 ${DEMO_SMS_CODE}）`,
    icon: 'success',
    duration: 2600
  })
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    countdownVal.value--
    if (countdownVal.value <= 0) {
      codeSentFlag.value = false
      if (timer) { clearInterval(timer); timer = null }
    }
  }, 1000)
}

function toggleLoginMode() {
  loginMode.value = loginMode.value === 'code' ? 'password' : 'code'
  formCode.value = ''
}

function togglePassword() {
  showPassword.value = !showPassword.value
}

function onAgreeChange(e) {
  formAgree.value = e.detail.value.includes('agree')
}

function onRememberChange(e) {
  rememberPhone.value = e.detail.value.includes('remember')
}

async function onLogin() {
  if (!formPhone.value || formPhone.value.length < 11) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  if (isCodeMode.value) {
    if (!/^\d{6}$/.test(formCode.value || '')) {
      uni.showToast({ title: '请输入6位验证码', icon: 'none' })
      return
    }
  } else if (!formPassword.value || formPassword.value.length < 6) {
    uni.showToast({ title: '请输入至少6位密码', icon: 'none' })
    return
  }
  if (!formAgree.value) {
    uni.showToast({ title: '请先同意用户协议与隐私政策', icon: 'none' })
    return
  }

  if (rememberPhone.value) {
    uni.setStorageSync('rememberedPhone', formPhone.value)
  } else {
    uni.removeStorageSync('rememberedPhone')
  }

  uni.showLoading({ title: '登录中', mask: true })
  const { ok } = await loginRequest({
    phone: formPhone.value,
    code: formCode.value,
    password: formPassword.value,
    role: selectedRole.value,
    loginMode: loginMode.value
  })
  uni.hideLoading()

  if (ok) {
    uni.switchTab({ url: '/pages/index/index' })
    return
  }

  const demoOk = applyDemoLogin({
    phone: formPhone.value,
    role: selectedRole.value,
    code: formCode.value,
    password: formPassword.value,
    loginMode: loginMode.value,
    smsRequested: codeSentFlag.value
  })
  if (demoOk) {
    uni.switchTab({ url: '/pages/index/index' })
    return
  }

  if (isCodeMode.value) {
    uni.showToast({ title: '验证码错误或服务不可用', icon: 'none' })
  } else {
    uni.showToast({
      title: `账号或密码错误（演示：${DEMO_PASSWORD}）`,
      icon: 'none',
      duration: 3000
    })
  }
}

function onWechatLogin() {
  // #ifdef MP-WEIXIN
  wx.getUserProfile({
    desc: '用于完善会员资料',
    success: () => { uni.showToast({ title: '微信登录待对接', icon: 'none' }) },
    fail: () => { uni.showToast({ title: '已取消授权', icon: 'none' }) }
  })
  // #endif
  // #ifndef MP-WEIXIN
  uni.showToast({ title: '请在微信小程序中使用', icon: 'none' })
  // #endif
}

function toForgotPassword() {
  uni.navigateTo({ url: '/pages/forget/forget' })
}

function toRegister() {
  uni.navigateTo({ url: '/pages/register/register' })
}

function toTerms(type) {
  uni.showToast({ title: `《${type === 'user' ? '用户协议' : '隐私政策'}》待对接`, icon: 'none' })
}

export default {
  components: { AuthHero, SvgIcon },

  data() {
    return {
      ROLE_OPTIONS,
      selectedRole,
      formPhone,
      formCode,
      formPassword,
      formAgree,
      codeSentFlag,
      countdownVal,
      rememberPhone,
      loginMode,
      showPassword,
      isCodeMode
    }
  },

  methods: {
    selectRole,
    onSendCode,
    toggleLoginMode,
    togglePassword,
    onAgreeChange,
    onRememberChange,
    onLogin,
    onWechatLogin,
    toForgotPassword,
    toRegister,
    toTerms
  }
}
</script>

<template>
  <view class="page">
    <AuthHero />

    <view class="sheet">
      <!-- 登录 / 注册 切换 -->
      <view class="auth-tabs">
        <view class="auth-tab auth-tab--left" :class="{ 'auth-tab--on': true }">登录</view>
        <view class="auth-tab auth-tab--right auth-tab--ghost" @click="toRegister">注册</view>
      </view>

      <!-- 身份选择 -->
      <view class="role-row">
        <view
          v-for="r in ROLE_OPTIONS"
          :key="r.id"
          class="role-chip"
          :class="{ 'role-chip--on': selectedRole === r.id }"
          @click="selectRole(r.id)"
        >
          <text class="role-chip__dot" />
          <text class="role-chip__label">{{ r.label }}</text>
        </view>
      </view>

      <!-- 手机号 -->
      <view class="field-card">
        <SvgIcon name="phone" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          type="number"
          maxlength="11"
          placeholder="手机号"
          :value="formPhone"
          @input="formPhone = $event.detail.value"
        />
      </view>

      <!-- 密码登录 -->
      <view v-if="!isCodeMode" class="field-card">
        <SvgIcon name="lock" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          :password="!showPassword"
          placeholder="密码"
          :value="formPassword"
          @input="formPassword = $event.detail.value"
        />
        <view class="field-card__eye" @click="togglePassword">
          <SvgIcon :name="showPassword ? 'eyeOff' : 'eye'" :size="40" color="#9a9a9a" />
        </view>
      </view>

      <!-- 验证码登录 -->
      <view v-else class="field-card">
        <SvgIcon name="code" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          type="number"
          maxlength="6"
          placeholder="验证码"
          :value="formCode"
          @input="formCode = $event.detail.value"
        />
        <view class="code-btn" @click="onSendCode">
          <text :class="{ muted: codeSentFlag }">
            {{ codeSentFlag ? countdownVal + 's' : '获取验证码' }}
          </text>
        </view>
      </view>

      <!-- 密码/验证码切换行 -->
      <view class="switch-row">
        <view class="switch-btn" @click="toggleLoginMode">
          <text>{{ isCodeMode ? '切换密码登录' : '切换验证码登录' }}</text>
        </view>
        <view v-if="!isCodeMode" class="forgot-btn" @click="toForgotPassword">
          <text>忘记密码？</text>
        </view>
      </view>

      <!-- 记住手机号 -->
      <view class="remember-row">
        <checkbox-group @change="onRememberChange">
          <label class="ck-row">
            <checkbox value="remember" :checked="rememberPhone" color="#33857a" />
            <text class="ck-text">记住手机号</text>
          </label>
        </checkbox-group>
      </view>

      <!-- 登录按钮 -->
      <view class="btn-primary" @click="onLogin">
        <text>登录</text>
      </view>

      <!-- 微信登录 -->
      <view class="wechat-btn" @click="onWechatLogin">
        <view class="wechat-icon">
          <SvgIcon name="wechat" :size="36" color="#ffffff" />
        </view>
        <text class="wechat-txt">使用微信账号登录</text>
      </view>

      <!-- 协议 -->
      <view class="agree-row">
        <checkbox-group @change="onAgreeChange">
          <label class="ck-row">
            <checkbox value="agree" :checked="formAgree" color="#33857a" />
            <text class="ck-text">我已阅读并同意</text>
          </label>
        </checkbox-group>
        <text class="link" @click="toTerms('user')">《用户协议》</text>
        <text class="muted-txt">与</text>
        <text class="link" @click="toTerms('privacy')">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
$teal: #33857a;
$ink: #1a1a1a;
$muted: #9a9a9a;
$card-bg: #f7f7f7;
$border: #e4e4e4;

.page {
  min-height: 100vh;
  background: #ffffff;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

.sheet {
  margin-top: -40rpx;
  position: relative;
  z-index: 2;
  background: #ffffff;
  border-radius: 40rpx 40rpx 0 0;
  padding: 52rpx 48rpx 48rpx;
  box-shadow: 0 -10rpx 40rpx rgba(0, 0, 0, 0.06);
}

.auth-tabs {
  display: flex;
  margin-bottom: 52rpx;
  border-radius: 20rpx;
  overflow: hidden;
  border: 2rpx solid $border;
}

.auth-tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 30rpx;
  font-weight: 600;
  background: $card-bg;
  color: $muted;
  transition: all 0.2s;
}

.auth-tab--on { background: $teal; color: #ffffff; }
.auth-tab--left { border-radius: 18rpx 0 0 18rpx; }
.auth-tab--right { border-radius: 0 18rpx 18rpx 0; }
.auth-tab--ghost { color: $teal; }

.role-row {
  display: flex;
  gap: 20rpx;
  margin-bottom: 48rpx;
}

.role-chip {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  padding: 20rpx 16rpx;
  border-radius: 16rpx;
  background: $card-bg;
  border: 2rpx solid $border;

  &__dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background: $border;
    flex-shrink: 0;
  }

  &__label {
    font-size: 26rpx;
    color: $muted;
    font-weight: 500;
  }

  &--on {
    border-color: $teal;
    background: rgba(51, 133, 122, 0.07);

    .role-chip__dot { background: $teal; }
    .role-chip__label { color: $teal; font-weight: 600; }
  }
}

.field-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: $card-bg;
  border-radius: 20rpx;
  padding: 0 28rpx;
  height: 100rpx;
  margin-bottom: 24rpx;
  border: 2rpx solid transparent;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: $teal;
    background: #f0faf8;
  }

  &__input {
    flex: 1;
    font-size: 30rpx;
    color: $ink;
    height: 100%;
  }

  &__eye {
    padding: 8rpx;
  }
}

.code-btn {
  flex-shrink: 0;
  padding: 14rpx 24rpx;
  border-radius: 999rpx;
  border: 2rpx solid $teal;
  font-size: 24rpx;
  color: $teal;
  font-weight: 600;
  white-space: nowrap;

  .muted { color: $muted; font-weight: 400; }
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 36rpx;
}

.switch-btn text { font-size: 26rpx; color: $teal; }
.forgot-btn text { font-size: 26rpx; color: $muted; }

.remember-row { margin-bottom: 32rpx; }

.ck-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.ck-text { font-size: 26rpx; color: $muted; }

.btn-primary {
  width: 100%;
  text-align: center;
  padding: 30rpx 0;
  border-radius: 999rpx;
  background: $teal;
  box-shadow: 0 12rpx 36rpx rgba(51, 133, 122, 0.30);
  margin-bottom: 32rpx;

  text {
    font-size: 34rpx;
    font-weight: 700;
    color: #ffffff;
    letter-spacing: 6rpx;
  }
}

.wechat-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 24rpx 0;
  border-radius: 999rpx;
  border: 2rpx solid $border;
  background: $card-bg;
  margin-bottom: 40rpx;
}

.wechat-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 10rpx;
  background: #07c160;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.wechat-txt {
  font-size: 28rpx;
  color: $ink;
  font-weight: 500;
}

.agree-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  font-size: 24rpx;
}

.muted-txt { color: $muted; }
.link { color: $teal; }
</style>
