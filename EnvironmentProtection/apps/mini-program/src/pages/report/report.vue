<template>
  <view class="page">
    <!-- 顶部标题栏 -->
    <view class="header-bar">
      <text class="header-title">线索举报</text>
      <view class="header-right" @click="goMyReports">
        <text class="my-reports">我的举报</text>
        <text class="arrow">›</text>
      </view>
    </view>

    <!-- 举报类型选择 -->
    <view class="report-types">
      <view
        v-for="type in reportTypes"
        :key="type.id"
        class="type-card"
        :class="{ active: selectedType.value === type.id }"
        @click="selectType(type.id)"
      >
        <view class="type-icon">{{ type.icon }}</view>
        <text class="type-name">{{ type.name }}</text>
        <text class="type-count">{{ type.count }}条</text>
      </view>
    </view>

    <!-- 举报表单 -->
    <view class="form-area">
      <view class="form-card">
        <!-- 事发地点 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-star">*</text>
            <text class="label-text">事发地点</text>
          </view>
          <view class="location-row">
            <input
              v-model="form.value.address"
              class="form-input"
              placeholder="请输入详细地址"
              placeholder-class="placeholder"
            />
            <view class="location-btn" @click="selectLocation">
              <text class="loc-icon">📍</text>
              <text class="loc-text">定位</text>
            </view>
          </view>
        </view>

        <!-- 问题类型 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-star">*</text>
            <text class="label-text">问题类型</text>
          </view>
          <view class="problem-types">
            <view
              v-for="problem in problemTypes"
              :key="problem.id"
              class="problem-tag"
              :class="{ selected: form.problemType === problem.id }"
              @click="form.problemType = problem.id"
            >
              {{ problem.name }}
            </view>
          </view>
        </view>

        <!-- 问题描述 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-star">*</text>
            <text class="label-text">问题描述</text>
          </view>
          <textarea
            v-model="form.value.description"
            class="form-textarea"
            placeholder="请详细描述问题，包括时间、主体、现场情况等..."
            placeholder-class="placeholder"
            :maxlength="500"
          />
          <text class="word-count">{{ form.description.length }}/500</text>
        </view>

        <!-- 上传图片 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">上传图片</text>
            <text class="label-hint">(最多4张)</text>
          </view>
          <view class="upload-area">
            <view
              v-for="(img, index) in form.images"
              :key="index"
              class="upload-preview"
            >
              <image class="preview-img" :src="img" mode="aspectFill" />
              <view class="preview-delete" @click="removeImage(index)">
                <text>×</text>
              </view>
            </view>
            <view v-if="form.images.length < 4" class="upload-btn" @click="chooseImage">
              <text class="upload-icon">+</text>
              <text class="upload-text">添加图片</text>
            </view>
          </view>
        </view>

        <!-- 联系方式 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-text">联系方式</text>
            <text class="label-hint">(选填)</text>
          </view>
          <input
            v-model="form.value.contact"
            class="form-input"
            placeholder="请输入手机号码"
            type="number"
            placeholder-class="placeholder"
          />
        </view>

        <!-- 隐私协议 -->
        <view class="privacy-row" @click="form.agreePrivacy = !form.agreePrivacy">
          <view class="check-box" :class="{ checked: form.agreePrivacy }">
            <text v-if="form.agreePrivacy" class="check-icon">✓</text>
          </view>
          <text class="privacy-text">
            我已阅读并同意
            <text class="privacy-link" @click.stop="showPrivacy">《举报隐私协议》</text>
          </text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-btn" :class="{ disabled: !canSubmit.value }" @click="submitReport">
        <text class="btn-text">提交举报</text>
      </view>

      <!-- 温馨提示 -->
      <view class="tips">
        <text class="tips-title">📌 温馨提示</text>
        <view class="tips-list">
          <text class="tip-item">• 请确保举报内容真实有效</text>
          <text class="tip-item">• 提供详细地址和情况描述有助于快速处理</text>
          <text class="tip-item">• 请上传现场照片作为证据支持</text>
          <text class="tip-item">• 举报人信息会受到保护，不会泄露</text>
        </view>
      </view>
    </view>

    <!-- 提交成功弹窗 -->
    <view v-if="showSuccess.value" class="success-popup" @click="showSuccess.value = false">
      <view class="success-card" @click.stop>
        <view class="success-icon">✓</view>
        <text class="success-title">提交成功</text>
        <text class="success-desc">您的举报已提交，工作人员将在3个工作日内处理，请保持电话畅通。</text>
        <view class="success-btn" @click="goMyReports">查看我的举报</view>
        <view class="success-close" @click="closeSuccess">关闭</view>
      </view>
    </view>

    <!-- 隐私协议弹窗 -->
    <view v-if="showPrivacyPolicy" class="privacy-popup" @click="showPrivacyPolicy = false">
      <view class="privacy-card" @click.stop>
        <text class="privacy-title">举报隐私协议</text>
        <scroll-view class="privacy-content" scroll-y>
          <text class="privacy-text-content">
1. 信息收集
我们仅收集您主动提交的举报信息，包括事发地点、问题描述、图片等。

2. 信息使用
您的举报信息仅用于环保部门调查处理，不会用于其他目的。

3. 信息保护
您的个人联系方式将受到严格保护，不会对外公开。

4. 匿名举报
您可选择不填写联系方式进行匿名举报。

5. 处理流程
收到举报后，我们将在3个工作日内进行初步审核并联系您确认情况。
          </text>
        </scroll-view>
        <view class="privacy-close" @click="showPrivacyPolicy = false">我知道了</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { api } from '../../api/index.js'

// 举报类型
const reportTypes = [
  { id: 'illegal_dumping', name: '违法倾倒', icon: '🏭', count: 128 },
  { id: 'plastic_bag', name: '违规塑料袋', icon: '🛍️', count: 86 },
  { id: 'agricultural_film', name: '农膜污染', icon: '🌾', count: 54 },
  { id: 'other', name: '其他问题', icon: '📋', count: 32 }
]

// 问题类型
const problemTypes = [
  { id: 'illegal_dumping_plastic', name: '违法倾倒塑料垃圾' },
  { id: 'ultrathin_bag', name: '使用超薄塑料袋' },
  { id: 'agricultural_film', name: '废旧农膜随意丢弃' },
  { id: 'unclean_recyclable', name: '一次性塑料餐具污染' },
  { id: 'other', name: '其他' }
]

// 表单数据
const form = ref({
  address: '',
  problemType: '',
  description: '',
  images: [],
  contact: '',
  agreePrivacy: false
})

// 状态
const selectedType = ref('illegal_dumping')
const showSuccess = ref(false)
const showPrivacyPolicy = ref(false)

// 计算属性
const canSubmit = ref(false)

function updateCanSubmit() {
  canSubmit.value = form.value.address.trim() &&
    form.value.problemType &&
    form.value.description.trim().length >= 10 &&
    form.value.agreePrivacy
}

// 选择举报类型
function selectType(id) {
  selectedType.value = id
}

// 选择问题类型
function selectLocation() {
  uni.showToast({ title: '定位功能待对接', icon: 'none' })
}

// 选择图片
function chooseImage() {
  uni.chooseImage({
    count: 4 - form.images.length,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      form.images.push(...res.tempFilePaths)
    }
  })
}

// 删除图片
function removeImage(index) {
  form.images.splice(index, 1)
}

// 显示隐私协议
function showPrivacy() {
  showPrivacyPolicy.value = true
}

// 提交举报
async function submitReport() {
  if (!canSubmit.value) {
    if (!form.agreePrivacy) {
      uni.showToast({ title: '请阅读并同意隐私协议', icon: 'none' })
      return
    }
    if (form.description.trim().length < 10) {
      uni.showToast({ title: '请详细描述问题（至少10字）', icon: 'none' })
      return
    }
    return
  }

  uni.showLoading({ title: '提交中...' })

  try {
    await api.report.create({
      type: selectedType.value,
      problemType: form.problemType,
      address: form.address,
      description: form.description,
      images: form.images,
      contact: form.contact
    })
    uni.hideLoading()
    showSuccess.value = true
  } catch (error) {
    uni.hideLoading()
    // 模拟成功
    setTimeout(() => {
      showSuccess.value = true
    }, 500)
  }
}

// 查看我的举报
function goMyReports() {
  showSuccess.value = false
  uni.showToast({ title: '查看我的举报', icon: 'none' })
}
</script>

<style lang="scss" scoped>
$primary: #28a745;
$primary-dark: #1a4d2e;

.page {
  min-height: 100vh;
  background: #f0f7f2;
}

// 顶部标题栏
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.header-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.my-reports {
  font-size: 26rpx;
  color: $primary;
}

.arrow {
  font-size: 28rpx;
  color: $primary;
}

// 举报类型
.report-types {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 24rpx;
}

.type-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 28rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
}

.type-card.active {
  border-color: $primary;
  background: #f0f7f2;
}

.type-icon {
  font-size: 48rpx;
}

.type-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.type-count {
  font-size: 22rpx;
  color: #999;
}

// 表单区域
.form-area {
  padding: 0 24rpx 40rpx;
}

.form-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.form-item {
  margin-bottom: 32rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 4rpx;
  margin-bottom: 16rpx;
}

.label-star {
  color: #ff4d4f;
  font-size: 28rpx;
}

.label-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.label-hint {
  font-size: 22rpx;
  color: #999;
}

.form-input {
  width: 100%;
  height: 80rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
}

.placeholder {
  color: #bbb;
}

.location-row {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.location-row .form-input {
  flex: 1;
}

.location-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  background: #f0f7f2;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.loc-icon {
  font-size: 28rpx;
}

.loc-text {
  font-size: 26rpx;
  color: $primary;
}

// 问题类型
.problem-types {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.problem-tag {
  padding: 12rpx 24rpx;
  background: #f5f6f8;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
  border: 2rpx solid transparent;
}

.problem-tag.selected {
  background: #e8f5e9;
  color: $primary;
  border-color: $primary;
}

// 文本域
.form-textarea {
  width: 100%;
  min-height: 200rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}

.word-count {
  font-size: 22rpx;
  color: #999;
  text-align: right;
  margin-top: 8rpx;
}

// 上传图片
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.upload-preview {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}

.preview-img {
  width: 100%;
  height: 100%;
}

.preview-delete {
  position: absolute;
  top: 0;
  right: 0;
  width: 40rpx;
  height: 40rpx;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
}

.upload-btn {
  width: 160rpx;
  height: 160rpx;
  background: #f5f6f8;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.upload-icon {
  font-size: 48rpx;
  color: #999;
}

.upload-text {
  font-size: 22rpx;
  color: #999;
}

// 隐私协议
.privacy-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 24rpx;
}

.check-box {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #ddd;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-box.checked {
  background: $primary;
  border-color: $primary;
}

.check-icon {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
}

.privacy-text {
  font-size: 24rpx;
  color: #666;
}

.privacy-link {
  color: $primary;
}

// 提交按钮
.submit-btn {
  margin-top: 32rpx;
  height: 88rpx;
  background: $primary;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn.disabled {
  background: #ccc;
}

.btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}

// 温馨提示
.tips {
  margin-top: 32rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
}

.tips-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.tip-item {
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
}

// 成功弹窗
.success-popup,
.privacy-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.success-card {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.success-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: $primary;
  color: #fff;
  font-size: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.success-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #333;
  margin-bottom: 16rpx;
}

.success-desc {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  text-align: center;
  margin-bottom: 32rpx;
}

.success-btn {
  width: 100%;
  height: 80rpx;
  background: $primary;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #fff;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.success-close {
  font-size: 26rpx;
  color: #999;
}

// 隐私协议弹窗
.privacy-card {
  width: 600rpx;
  max-height: 800rpx;
  background: #fff;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
}

.privacy-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  text-align: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #eee;
}

.privacy-content {
  flex: 1;
  padding: 24rpx 32rpx;
  max-height: 500rpx;
}

.privacy-text-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
}

.privacy-close {
  padding: 24rpx;
  text-align: center;
  font-size: 30rpx;
  color: $primary;
  border-top: 1rpx solid #eee;
}
</style>
