<template>
  <div class="law-test-container">
    <div class="page-header">
      <h1>法规API接口测试</h1>
      <div class="connection-status">
        <span :class="['status-dot', isConnected ? 'connected' : 'disconnected']"></span>
        <span>{{ isConnected ? '已连接' : '未连接' }}</span>
      </div>
    </div>

    <div class="config-card">
      <h3>API 配置信息</h3>
      <div class="config-info">
        <div class="info-item">
          <span class="label">接口地址：</span>
          <span class="value">{{ apiUrl }}</span>
        </div>
        <div class="info-item">
          <span class="label">App ID：</span>
          <span class="value">{{ appId }}</span>
        </div>
        <div class="info-item">
          <span class="label">App Secret：</span>
          <span class="value masked">********{{ appSecret.slice(-8) }}</span>
        </div>
      </div>
    </div>

    <div class="test-card">
      <h3>接口测试</h3>
      <div class="test-form">
        <el-input v-model="testKeyword" placeholder="输入法规关键词" style="width: 300px" />
        <el-button type="primary" :loading="testing" @click="handleTest">
          测试搜索接口
        </el-button>
      </div>

      <div v-if="testResult" class="test-result">
        <h4>返回结果：</h4>
        <pre>{{ JSON.stringify(testResult, null, 2) }}</pre>
      </div>

      <div v-if="testError" class="test-error">
        <h4>错误信息：</h4>
        <pre>{{ testError }}</pre>
      </div>
    </div>

    <div class="quick-test">
      <h3>快速测试</h3>
      <div class="test-buttons">
        <el-button @click="testCategories">获取分类</el-button>
        <el-button @click="testLatest">最新法规</el-button>
        <el-button @click="testHot">热门法规</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  searchLaws,
  getLawCategories,
  getLatestLaws,
  getHotLaws
} from '@/api/lawExternal.js'

const apiUrl = ref(import.meta.env.VITE_LAW_API_URL || 'https://api.law-star.com')
const appId = ref(import.meta.env.VITE_LAW_APP_ID || '未配置')
const appSecret = ref(import.meta.env.VITE_LAW_APP_SECRET || '未配置')

const testKeyword = ref('环境保护')
const testing = ref(false)
const testResult = ref(null)
const testError = ref(null)
const isConnected = ref(false)

const handleTest = async () => {
  testing.value = true
  testResult.value = null
  testError.value = null

  try {
    const res = await searchLaws({
      keyword: testKeyword.value,
      page: 1,
      pageSize: 10
    })
    testResult.value = res
    isConnected.value = true
    ElMessage.success('接口调用成功')
  } catch (error) {
    testError.value = error.message || '接口调用失败'
    isConnected.value = false
    ElMessage.error('接口调用失败: ' + (error.message || '未知错误'))
  } finally {
    testing.value = false
  }
}

const testCategories = async () => {
  try {
    const res = await getLawCategories()
    testResult.value = res
    isConnected.value = true
    ElMessage.success('获取分类成功')
  } catch (error) {
    testError.value = error.message
    isConnected.value = false
  }
}

const testLatest = async () => {
  try {
    const res = await getLatestLaws(5)
    testResult.value = res
    isConnected.value = true
    ElMessage.success('获取最新法规成功')
  } catch (error) {
    testError.value = error.message
    isConnected.value = false
  }
}

const testHot = async () => {
  try {
    const res = await getHotLaws(5)
    testResult.value = res
    isConnected.value = true
    ElMessage.success('获取热门法规成功')
  } catch (error) {
    testError.value = error.message
    isConnected.value = false
  }
}

onMounted(() => {
  // 自动测试连接
  testLatest()
})
</script>

<style lang="scss" scoped>
.law-test-container {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h1 {
      margin: 0;
      font-size: 24px;
      color: #333;
    }

    .connection-status {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #666;

      .status-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;

        &.connected {
          background: #52b788;
          box-shadow: 0 0 8px rgba(82, 183, 136, 0.5);
        }

        &.disconnected {
          background: #dc3545;
        }
      }
    }
  }

  .config-card, .test-card, .quick-test {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    h3 {
      margin: 0 0 16px;
      font-size: 16px;
      color: #333;
    }
  }

  .config-info {
    .info-item {
      margin-bottom: 12px;
      font-size: 14px;

      .label {
        color: #666;
      }

      .value {
        color: #333;
        font-family: monospace;

        &.masked {
          color: #999;
        }
      }
    }
  }

  .test-form {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
  }

  .test-result, .test-error {
    margin-top: 16px;

    h4 {
      margin: 0 0 8px;
      font-size: 14px;
      color: #666;
    }

    pre {
      background: #f5f5f5;
      padding: 16px;
      border-radius: 8px;
      font-size: 12px;
      max-height: 400px;
      overflow: auto;
    }
  }

  .test-error pre {
    background: #fff2f2;
    color: #dc3545;
  }

  .test-buttons {
    display: flex;
    gap: 12px;
  }
}
</style>
