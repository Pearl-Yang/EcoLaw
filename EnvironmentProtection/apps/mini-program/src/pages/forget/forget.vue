<script>
import { ref, onUnmounted } from 'vue'
import AuthHero from '../../components/auth/AuthHero.vue'
import SvgIcon from '../../components/auth/SvgIcon.vue'
import { DEMO_SMS_CODE } from '../../services/auth.js'
import { api } from '../../api/index.js'

const state = ref({
  formPhone: '',
  formCode: '',
  formPassword: '',
  formPassword2: '',
  codeSentFlag: false,
  countdownVal: 60,
  smsRequested: false,
  showPassword: false,
  showPassword2: false
})
let timer = null

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

export default {
  components: { AuthHero, SvgIcon },

  methods: {
    goBack() {
      uni.navigateBack()
    },

    togglePassword(field) {
      if (field === 1) state.value.showPassword = !state.value.showPassword
      else state.value.showPassword2 = !state.value.showPassword2
    },

    async onSendCode() {
      if (!state.value.formPhone || state.value.formPhone.length < 11) {
        uni.showToast({ title: '请输入正确手机号', icon: 'none' })
        return
      }
      if (state.value.codeSentFlag) return
      try {
        await api.auth.sendSmsCode({ phone: state.value.formPhone, scene: 'reset' })
      } catch {
        /* 忽略 */
      }
      state.value.smsRequested = true
      state.value.codeSentFlag = true
      state.value.countdownVal = 60
      uni.showToast({
        title: `验证码已发送（演示可填 ${DEMO_SMS_CODE}）`,
        icon: 'success',
        duration: 2600
      })
      if (timer) clearInterval(timer)
      timer = setInterval(() => {
        state.value.countdownVal--
        if (state.value.countdownVal <= 0) {
          state.value.codeSentFlag = false
          if (timer) { clearInterval(timer); timer = null }
        }
      }, 1000)
    },

    async onSubmit() {
      if (!state.value.formPhone || state.value.formPhone.length < 11) {
        uni.showToast({ title: '请输入手机号', icon: 'none' })
        return
      }
      if (!state.value.smsRequested) {
        uni.showToast({ title: '请先获取验证码', icon: 'none' })
        return
      }
      if (!/^\d{6}$/.test(state.value.formCode || '')) {
        uni.showToast({ title: '请输入6位验证码', icon: 'none' })
        return
      }
      if (!state.value.formPassword || state.value.formPassword.length < 6) {
        uni.showToast({ title: '新密码至少6位', icon: 'none' })
        return
      }
      if (state.value.formPassword !== state.value.formPassword2) {
        uni.showToast({ title: '两次密码不一致', icon: 'none' })
        return
      }

      try {
        await api.auth.resetPasswordBySms({
          phone: state.value.formPhone,
          code: state.value.formCode,
          newPassword: state.value.formPassword
        })
        uni.showToast({ title: '密码已重置，请登录', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 800)
      } catch {
        if (state.value.formCode === DEMO_SMS_CODE) {
          uni.showToast({
            title: '演示环境已接受重置（正式环境需后端接口）',
            icon: 'none',
            duration: 2800
          })
          setTimeout(() => uni.navigateBack(), 900)
        } else {
          uni.showToast({ title: '重置失败，请检查验证码或联系管理员', icon: 'none' })
        }
      }
    }
  }
}
</script>

<template>
  <view class="page">
    <AuthHero />

    <view class="sheet">
      <view class="nav" @click="goBack">
        <SvgIcon name="arrow-left" :size="36" color="#33857a" />
        <text class="nav-txt">返回登录</text>
      </view>

      <text class="title">找回密码</text>
      <text class="sub">通过手机验证码设置新密码</text>

      <!-- 手机号 -->
      <view class="field-card">
        <SvgIcon name="phone" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          type="number"
          maxlength="11"
          placeholder="手机号"
          :value="state.formPhone"
          @input="state.formPhone = $event.detail.value"
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
          :value="state.formCode"
          @input="state.formCode = $event.detail.value"
        />
        <view class="code-btn" @click="onSendCode">
          <text :class="{ muted: state.codeSentFlag }">
            {{ state.codeSentFlag ? state.countdownVal + 's' : '获取验证码' }}
          </text>
        </view>
      </view>

      <!-- 新密码 -->
      <view class="field-card">
        <SvgIcon name="lock" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          :password="!state.showPassword"
          placeholder="新密码（至少6位）"
          :value="state.formPassword"
          @input="state.formPassword = $event.detail.value"
        />
        <view class="field-card__eye" @click="togglePassword(1)">
          <SvgIcon :name="state.showPassword ? 'eyeOff' : 'eye'" :size="40" color="#9a9a9a" />
        </view>
      </view>

      <!-- 确认新密码 -->
      <view class="field-card">
        <SvgIcon name="lock" :size="40" color="#33857a" />
        <input
          class="field-card__input"
          :password="!state.showPassword2"
          placeholder="再次输入密码"
          :value="state.formPassword2"
          @input="state.formPassword2 = $event.detail.value"
        />
        <view class="field-card__eye" @click="togglePassword(2)">
          <SvgIcon :name="state.showPassword2 ? 'eyeOff' : 'eye'" :size="40" color="#9a9a9a" />
        </view>
      </view>

      <!-- 确认按钮 -->
      <view class="btn-primary" @click="onSubmit">
        <text>确认重置</text>
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

.nav {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 48rpx;
}

.nav-txt {
  font-size: 28rpx;
  color: $teal;
  font-weight: 600;
}

.title {
  display: block;
  font-size: 44rpx;
  font-weight: 700;
  color: $ink;
  margin-bottom: 12rpx;
}

.sub {
  display: block;
  font-size: 26rpx;
  color: $muted;
  margin-bottom: 48rpx;
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
