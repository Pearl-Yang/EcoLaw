<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AuthHero from '../../components/auth/AuthHero.vue'
import SvgIcon from '../../components/auth/SvgIcon.vue'
import { registerRequest, applyDemoRegister, DEMO_SMS_CODE } from '../../services/auth.js'
import { api } from '../../api/index.js'

const ROLE_OPTIONS = [
  { id: 'grass_roots', label: '基层用户' },
  { id: 'enterprise_employee', label: '企业员工' }
]

const selectedRole = ref('grass_roots')
const formPhone = ref('')
const formCode = ref('')
const formPassword = ref('')
const formPassword2 = ref('')
const formAgree = ref(false)
const codeSentFlag = ref(false)
const countdownVal = ref(60)
const showPassword = ref(false)
const showPassword2 = ref(false)
let timer = null

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
    await api.auth.sendSmsCode({ phone: formPhone.value, scene: 'register' })
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

function togglePassword(field) {
  if (field === 1) showPassword.value = !showPassword.value
  else showPassword2.value = !showPassword2.value
}

function onAgreeChange(e) {
  formAgree.value = e.detail.value.includes('agree')
}

function toTerms(type) {
  uni.showToast({ title: `《${type === 'user' ? '用户协议' : '隐私政策'}》待对接`, icon: 'none' })
}

async function onRegister() {
  if (!formPhone.value || formPhone.value.length < 11) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  if (!codeSentFlag.value) {
    uni.showToast({ title: '请先获取验证码', icon: 'none' })
    return
  }
  if (!/^\d{6}$/.test(formCode.value || '')) {
    uni.showToast({ title: '请输入6位验证码', icon: 'none' })
    return
  }
  if (!formPassword.value || formPassword.value.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  if (formPassword.value !== formPassword2.value) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  if (!formAgree.value) {
    uni.showToast({ title: '请先同意用户协议与隐私政策', icon: 'none' })
    return
  }

  uni.showLoading({ title: '提交中', mask: true })
  const { ok } = await registerRequest({
    phone: formPhone.value,
    code: formCode.value,
    password: formPassword.value,
    role: selectedRole.value
  })
  uni.hideLoading()

  if (ok) {
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 600)
    return
  }

  if (applyDemoRegister({
    phone: formPhone.value,
    code: formCode.value,
    smsRequested: codeSentFlag.value
  })) {
    setTimeout(() => uni.navigateBack(), 800)
    return
  }

  uni.showToast({ title: '注册失败，请检查验证码或网络', icon: 'none' })
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
      formPassword2,
      formAgree,
      codeSentFlag,
      countdownVal,
      showPassword,
      showPassword2
    }
  },

  methods: {
    goLogin() {
      uni.redirectTo({ url: '/pages/login/login' })
    },
    selectRole,
    onSendCode,
    togglePassword,
    onAgreeChange,
    toTerms,
    onRegister
  }
}
</script>

<template>
  <view class="page">
    <AuthHero />

    <view class="sheet">
      <!-- 登录 / 注册 切换 -->
      <view class="auth-tabs">
        <view class="auth-tab auth-tab--left auth-tab--ghost" @click="goLogin">登录</view>
        <view class="auth-tab auth-tab--right" :class="{ 'auth-tab--on': true }">注册</view>
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

      <!-- 验证码 -->
      <view class="field-card">
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

      <!-- 密码 -->
      <view class="field-card">
        <SvgIcon name="lock" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          :password="!showPassword"
          placeholder="设置密码（至少6位）"
          :value="formPassword"
          @input="formPassword = $event.detail.value"
        />
        <view class="field-card__eye" @click="togglePassword(1)">
          <SvgIcon :name="showPassword ? 'eyeOff' : 'eye'" :size="40" color="#9a9a9a" />
        </view>
      </view>

      <!-- 确认密码 -->
      <view class="field-card">
        <SvgIcon name="lock" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          :password="!showPassword2"
          placeholder="再次输入密码"
          :value="formPassword2"
          @input="formPassword2 = $event.detail.value"
        />
        <view class="field-card__eye" @click="togglePassword(2)">
          <SvgIcon :name="showPassword2 ? 'eyeOff' : 'eye'" :size="40" color="#9a9a9a" />
        </view>
      </view>

      <!-- 注册按钮 -->
      <view class="btn-primary" @click="onRegister">
        <text>注册</text>
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

.agree-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  font-size: 24rpx;
  margin-top: 40rpx;
}

.ck-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.ck-text {
  font-size: 26rpx;
  color: $muted;
}

.muted-txt { color: $muted; }
.link { color: $teal; }

.btn-primary {
  width: 100%;
  text-align: center;
  padding: 30rpx 0;
  border-radius: 999rpx;
  background: $teal;
  box-shadow: 0 12rpx 36rpx rgba(51, 133, 122, 0.30);
  margin-top: 16rpx;

  text {
    font-size: 34rpx;
    font-weight: 700;
    color: #ffffff;
    letter-spacing: 6rpx;
  }
}
</style>
