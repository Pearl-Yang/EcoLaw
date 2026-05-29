<template>
  <div class="ai-tools-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：素材生成 -->
      <el-col :span="16">
        <div class="page-header">
          <div class="header-title">
            <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09Z"/>
              <path d="m11.25 11.25.041-.02a.75.75 0 0 1 .763.692l-.115.184a6.25 6.25 0 0 0-1.632 1.568l-.187.327"/>
              <path d="M13.5 12a1.5 1.5 0 1 0-3 0v3.75"/>
              <path d="M19.5 12a7.5 7.5 0 1 1-15 0 7.5 7.5 0 0 1 15 0Z"/>
            </svg>
            <h1>AI 素材生成</h1>
          </div>
          <div class="header-badge">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09Z"/>
            </svg>
            基于大语言模型
          </div>
        </div>

        <div class="glass-card generate-card">
          <el-form :inline="true" class="generate-form">
            <el-form-item label="素材类型">
              <el-select v-model="form.type" placeholder="请选择" style="width: 140px">
                <el-option label="宣传文案" value="text" />
                <el-option label="视频脚本" value="video" />
                <el-option label="宣传海报" value="poster" />
                <el-option label="H5页面" value="h5" />
                <el-option label="音频文案" value="audio" />
              </el-select>
            </el-form-item>
            <el-form-item label="模板风格">
              <el-select v-model="form.template" placeholder="选择模板" style="width: 160px">
                <el-option label="正式严肃" value="formal" />
                <el-option label="轻松活泼" value="casual" />
                <el-option label="简约现代" value="modern" />
                <el-option label="传统典雅" value="traditional" />
              </el-select>
            </el-form-item>
            <el-form-item label="目标受众">
              <el-select v-model="form.targetAudience" placeholder="请选择" style="width: 150px">
                <el-option label="政府监管人员" value="government" />
                <el-option label="企业管理人员" value="enterprise" />
                <el-option label="基层执行人员" value="grassroots" />
                <el-option label="普通群众" value="public" />
              </el-select>
            </el-form-item>
          </el-form>

          <el-form class="topic-form">
            <el-form-item label="生成主题">
              <el-input
                v-model="form.topic"
                type="textarea"
                :rows="3"
                placeholder="请详细描述您需要的素材内容，如：生成一篇关于白色污染治理的宣传文案，面向社区居民，呼吁减少使用一次性塑料制品..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="关键词">
              <el-select v-model="form.keywords" multiple placeholder="选择或输入关键词" style="width: 100%" allow-create filterable default-first-option>
                <el-option label="禁塑" value="禁塑" />
                <el-option label="白色污染" value="白色污染" />
                <el-option label="绿色生活" value="绿色生活" />
                <el-option label="环保科普" value="环保科普" />
                <el-option label="法规宣传" value="法规宣传" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" size="large" class="btn-primary" @click="handleGenerate">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09Z"/>
                  <path d="m11.25 11.25.041-.02a.75.75 0 0 1 .763.692l-.115.184a6.25 6.25 0 0 0-1.632 1.568l-.187.327"/>
                </svg>
                一键生成
              </el-button>
              <el-button size="large" class="btn-reset" @click="handleReset">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"/>
                </svg>
                重置
              </el-button>
            </el-form-item>
          </el-form>

          <div class="section-divider">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 12h.01M15 12h.01M10 16c.667.333 1.333.5 2 .5s1.333-.167 2-.5"/>
              <circle cx="12" cy="12" r="10"/>
            </svg>
            <span>生成结果</span>
          </div>

          <div class="result-area" v-if="result || generatingText">
            <div v-if="generatingText" class="generating">
              <svg class="loading-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 12m-1 0a1 1 0 1 0 2 0a1 1 0 1 0-2 0"/>
                <path d="M12 7a5 5 0 1 0 5 5"/>
                <path d="M13 3.055A9 9 0 1 0 20.945 12"/>
                <path d="M16 3l-4 4-4-4"/>
                <path d="M20.5 8.5a2.5 2.5 0 0 1-5 0v0a2.5 2.5 0 0 1 5 0Z"/>
              </svg>
              <span>AI 正在生成中，请稍候...</span>
            </div>
            <div v-else-if="result">
              <el-input
                v-model="result"
                type="textarea"
                :rows="12"
                placeholder="生成的内容将显示在这里"
              />
              <div class="result-meta">
                <span class="meta-info">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                  </svg>
                  字数：{{ result.length }} 字
                </span>
                <span class="meta-info">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                  预计阅读时间：{{ Math.ceil(result.length / 400) }} 分钟
                </span>
              </div>
              <div class="actions">
                <el-button type="primary" class="btn-copy" @click="handleCopy">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <rect width="14" height="14" x="8" y="8" rx="2" ry="2"/>
                    <path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/>
                  </svg>
                  复制全文
                </el-button>
                <el-button type="success" class="btn-download" @click="handleDownload">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                    <polyline points="7 10 12 15 17 10"/>
                    <line x1="12" x2="12" y1="15" y2="3"/>
                  </svg>
                  下载文档
                </el-button>
                <el-button type="warning" class="btn-save" @click="handleSave">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
                    <line x1="12" x2="12" y1="11" y2="17"/>
                    <line x1="9" x2="15" y1="14" y2="14"/>
                  </svg>
                  保存到素材库
                </el-button>
                <el-button class="btn-regenerate" @click="handleRegenerate" :loading="loading">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"/>
                  </svg>
                  重新生成
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-else description="填写上方表单，点击「一键生成」按钮开始生成AI素材" />
        </div>
      </el-col>

      <!-- 右侧：素材库和模板 -->
      <el-col :span="8">
        <!-- 快捷模板 -->
        <div class="glass-card template-card">
          <div class="card-header">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect width="18" height="18" x="3" y="3" rx="2"/>
              <path d="M3 9h18"/>
              <path d="M9 21V9"/>
            </svg>
            <span>快捷模板</span>
          </div>
          <div class="template-list">
            <div v-for="tpl in templates" :key="tpl.id" class="template-item" @click="useTemplate(tpl)">
              <div class="tpl-icon">{{ tpl.icon }}</div>
              <div class="tpl-info">
                <span class="tpl-name">{{ tpl.name }}</span>
                <span class="tpl-desc">{{ tpl.desc }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 历史记录 -->
        <div class="glass-card history-card">
          <div class="card-header card-header-row">
            <div class="header-left">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              <span>历史生成</span>
            </div>
            <el-button type="primary" link size="small" class="link-btn">查看全部</el-button>
          </div>
          <div class="history-list">
            <div v-for="item in history" :key="item.id" class="history-item" @click="loadHistory(item)">
              <div class="history-icon">{{ item.typeIcon }}</div>
              <div class="history-info">
                <span class="history-title">{{ item.title }}</span>
                <span class="history-time">{{ item.createTime }}</span>
              </div>
              <div class="history-actions">
                <el-button type="primary" link size="small" class="link-btn" @click.stop="reuseHistory(item)">复用</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 素材库 -->
        <div class="glass-card material-card">
          <div class="card-header card-header-row">
            <div class="header-left">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z"/>
              </svg>
              <span>我的素材库</span>
            </div>
            <el-button type="primary" link size="small" class="link-btn">管理</el-button>
          </div>
          <div class="material-tabs">
            <el-tag v-for="tag in materialTags" :key="tag" :type="activeTag === tag ? 'success' : 'info'" @click="activeTag = tag" class="material-tag">
              {{ tag }}
            </el-tag>
          </div>
          <div class="material-list">
            <div v-for="m in filteredMaterials" :key="m.id" class="material-item">
              <span class="material-name">{{ m.name }}</span>
              <el-button type="primary" link size="small" class="link-btn">使用</el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 生成进度提示 -->
    <el-dialog v-model="showProgress" :show-close="false" :close-on-click-modal="false" class="glass-dialog">
      <template #header>
        <div class="dialog-header">
          <svg class="dialog-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09Z"/>
            <path d="m11.25 11.25.041-.02a.75.75 0 0 1 .763.692l-.115.184a6.25 6.25 0 0 0-1.632 1.568l-.187.327"/>
          </svg>
          <span>AI 生成中</span>
        </div>
      </template>
      <div class="progress-content">
        <el-progress :percentage="progressPercent" :stroke-width="20" :color="progressColor" />
        <p class="progress-text">{{ progressText }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { generateMaterial } from '@/api/ai'

const loading = ref(false)
const generatingText = ref('')
const result = ref('')
const showProgress = ref(false)
const progressPercent = ref(0)
const progressText = ref('正在连接 AI 服务...')
const progressColor = ref('#52b788')
const activeTag = ref('全部')

const form = reactive({
  type: 'text',
  template: 'casual',
  topic: '',
  targetAudience: 'public',
  keywords: []
})

const templates = [
  { id: 1, icon: '📋', name: '活动通知', desc: '社区活动公告模板' },
  { id: 2, icon: '📜', name: '政策解读', desc: '法规政策宣传模板' },
  { id: 3, icon: '📹', name: '视频脚本', desc: '短视频文案模板' },
  { id: 4, icon: '🏢', name: '企业合规', desc: '企业合规指南模板' },
  { id: 5, icon: '🌱', name: '环保科普', desc: '绿色生活宣传模板' }
]

const history = ref([
  { id: '1', title: '塑料污染防治宣传文案', type: 'text', typeIcon: '📝', createTime: '2026-04-04 10:30' },
  { id: '2', title: '企业合规指南海报', type: 'poster', typeIcon: '🖼', createTime: '2026-04-03 15:20' },
  { id: '3', title: '社区宣传活动视频脚本', type: 'video', typeIcon: '📹', createTime: '2026-04-02 09:15' },
  { id: '4', title: '禁塑倡议书', type: 'text', typeIcon: '📝', createTime: '2026-04-01 14:00' }
])

const materialTags = ['全部', '宣传文案', '视频脚本', '海报模板', 'H5页面']

const materials = ref([
  { id: 1, name: '白色污染宣传文案集', tag: '宣传文案' },
  { id: 2, name: '企业合规指南.pdf', tag: '宣传文案' },
  { id: 3, name: '禁塑倡议书模板', tag: '宣传文案' },
  { id: 4, name: '环保科普视频脚本', tag: '视频脚本' }
])

const filteredMaterials = computed(() => {
  if (activeTag.value === '全部') return materials.value
  return materials.value.filter(m => m.tag === activeTag.value)
})

const handleGenerate = async () => {
  if (!form.topic) {
    ElMessage.warning('请输入生成主题')
    return
  }

  loading.value = true
  generatingText.value = '正在生成中...'
  showProgress.value = true
  progressPercent.value = 0

  const steps = ['连接 AI 服务', '分析主题', '生成内容', '优化文案']
  let stepIndex = 0

  const progressTimer = setInterval(() => {
    if (stepIndex < steps.length) {
      progressText.value = steps[stepIndex] + '...'
      progressPercent.value = Math.min(95, progressPercent.value + 25)
      stepIndex++
    }
  }, 600)

  try {
    await new Promise(resolve => setTimeout(resolve, 2500))
    clearInterval(progressTimer)
    progressPercent.value = 100
    progressText.value = '生成完成！'

    const mockResults = {
      text: `【白色污染治理宣传文案】

📢 倡议书

广大市民朋友们：

塑料制品在为我们带来便利的同时，也给生态环境带来了严重污染。为建设美丽家园，我们倡议：

一、减少使用一次性塑料制品
二、自觉养成绿色生活习惯  
三、积极宣传环保理念

让我们共同行动，为子孙后代留下一片碧水蓝天！

XX市生态环境局
2026年4月`,
      video: `【视频脚本】白色污染治理宣传

时长：60秒

🎬 开场（5秒）
画面：蓝天白云、绿水青山
旁白：当我们享受美好环境时...

🎬 问题展示（15秒）
画面：塑料垃圾堆积、污染场景
旁白：每年有800万吨塑料排入海洋...

🎬 行动呼吁（25秒）
画面：市民使用环保袋、减塑行动
旁白：减塑行动，从我做起...

🎬 结尾（15秒）
画面：美丽家园、志愿者活动
旁白：共建清洁城乡，共享美好生活！`,
      poster: `【宣传海报文案】

主标题：减塑行动 绿色生活

副标题：减少一次性塑料 共建清洁家园

正文：
- 拒绝塑料吸管
- 使用环保购物袋  
- 选择可降解餐具
- 做好垃圾分类

落款：XX市生态环境局宣`
    }

    result.value = mockResults[form.type] || mockResults.text
    generatingText.value = ''
    ElMessage.success('生成成功！')

    setTimeout(() => {
      showProgress.value = false
    }, 500)

  } catch {
    ElMessage.error('生成失败，请重试')
  } finally {
    loading.value = false
    generatingText.value = ''
  }
}

const handleCopy = () => {
  navigator.clipboard.writeText(result.value)
  ElMessage.success('已复制到剪贴板')
}

const handleDownload = () => {
  ElMessage.info('文档下载功能开发中')
}

const handleSave = () => {
  ElMessage.success('已保存到素材库')
}

const handleRegenerate = () => {
  handleGenerate()
}

const handleReset = () => {
  form.type = 'text'
  form.template = 'casual'
  form.topic = ''
  form.keywords = []
  result.value = ''
  generatingText.value = ''
}

const useTemplate = (tpl) => {
  form.type = tpl.id === 3 ? 'video' : tpl.id === 2 ? 'text' : 'text'
  ElMessage.success(`已选择模板：${tpl.name}`)
}

const loadHistory = (item) => {
  result.value = '历史记录内容加载中...'
  ElMessage.info('加载历史记录')
}

const reuseHistory = (item) => {
  form.type = item.type === 'poster' ? 'poster' : item.type
  handleGenerate()
}
</script>

<style lang="scss" scoped>
.ai-tools-container {
  padding: 24px;
  min-height: 100vh;
  background: #ffffff;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(82,183,136,0.08), rgba(149,213,178,0.04));
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  bottom: 10%;
  left: -80px;
}

.bg-circle-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  right: 20%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;

  .header-icon {
    width: 40px;
    height: 40px;
    color: #52b788;
  }

  h1 {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
    color: #333333;
    letter-spacing: -0.5px;
  }
}

.header-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(82,183,136,0.1);
  border: 1px solid rgba(82,183,136,0.2);
  border-radius: 20px;
  font-size: 13px;
  color: #2d6a4f;

  svg {
    width: 16px;
    height: 16px;
  }
}

.glass-card {
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(82,183,136,0.06);
  padding: 24px;
  position: relative;
  z-index: 1;
}

.generate-card {
  padding: 28px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20px;

  svg {
    width: 20px;
    height: 20px;
    color: #52b788;
  }
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 16px;
    font-weight: 600;
    color: #333333;

    svg {
      width: 20px;
      height: 20px;
      color: #52b788;
    }
  }
}

.generate-form {
  margin-bottom: 16px;

  :deep(.el-form-item__label) {
    color: #333333;
    font-weight: 500;
  }
}

.topic-form {
  :deep(.el-textarea) {
    .el-textarea__inner {
      font-size: 14px;
      line-height: 1.8;
      border-color: rgba(82,183,136,0.3);
      background: rgba(255,255,255,0.8);

      &:focus {
        border-color: #52b788;
        background: #fff;
      }
    }
  }

  :deep(.el-select) {
    .el-input__wrapper {
      background: rgba(255,255,255,0.8);
      box-shadow: 0 0 0 1px rgba(82,183,136,0.3);

      &:hover, &.is-focus {
        box-shadow: 0 0 0 1px #52b788;
      }
    }
  }
}

.section-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 28px 0 20px;
  color: #2d6a4f;
  font-weight: 600;
  font-size: 15px;

  svg {
    width: 20px;
    height: 20px;
  }

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(82,183,136,0.3), transparent);
  }
}

.result-area {
  min-height: 300px;
}

.generating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #52b788;
  font-size: 14px;

  .loading-icon {
    width: 24px;
    height: 24px;
    animation: spin 1.5s linear infinite;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.result-meta {
  display: flex;
  gap: 20px;
  margin: 12px 0;
  padding: 12px;
  background: rgba(82,183,136,0.06);
  border-radius: 12px;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #2d6a4f;

  svg {
    width: 16px;
    height: 16px;
  }
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 16px;
}

.btn-primary {
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
  border: none !important;
  color: #fff !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px;
  padding: 12px 28px !important;
  border-radius: 12px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(82,183,136,0.3);
  }
}

.btn-reset {
  background: rgba(255,255,255,0.8) !important;
  border: 1px solid rgba(82,183,136,0.3) !important;
  color: #2d6a4f !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px;
  padding: 12px 24px !important;
  border-radius: 12px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: rgba(82,183,136,0.1) !important;
    border-color: #52b788 !important;
  }
}

.btn-copy, .btn-download, .btn-save, .btn-regenerate {
  display: flex !important;
  align-items: center !important;
  gap: 6px;
  padding: 10px 18px !important;
  border-radius: 10px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 16px;
    height: 16px;
  }
}

.btn-copy {
  background: rgba(82,183,136,0.1) !important;
  border: 1px solid rgba(82,183,136,0.2) !important;
  color: #2d6a4f !important;

  &:hover {
    background: rgba(82,183,136,0.2) !important;
  }
}

.btn-download {
  background: rgba(82,183,136,0.15) !important;
  border: 1px solid rgba(82,183,136,0.25) !important;
  color: #1a4d2e !important;

  &:hover {
    background: rgba(82,183,136,0.25) !important;
  }
}

.btn-save {
  background: rgba(255,183,76,0.15) !important;
  border: 1px solid rgba(255,183,76,0.25) !important;
  color: #8B6914 !important;

  &:hover {
    background: rgba(255,183,76,0.25) !important;
  }
}

.btn-regenerate {
  background: rgba(255,255,255,0.8) !important;
  border: 1px solid rgba(82,183,136,0.2) !important;
  color: #2d6a4f !important;

  &:hover {
    background: rgba(82,183,136,0.1) !important;
  }
}

.template-card {
  margin-bottom: 20px;
  padding: 20px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.template-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: rgba(82,183,136,0.04);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(82,183,136,0.1);
    transform: translateX(4px);
  }
}

.tpl-icon {
  font-size: 28px;
}

.tpl-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tpl-name {
  font-size: 14px;
  font-weight: 600;
  color: #333333;
}

.tpl-desc {
  font-size: 12px;
  color: #666666;
}

.history-card {
  margin-bottom: 20px;
  padding: 20px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(82,183,136,0.03);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(82,183,136,0.06);
  }
}

.history-icon {
  font-size: 24px;
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.history-title {
  font-size: 13px;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-time {
  font-size: 11px;
  color: #9ca3af;
}

.material-card {
  padding: 20px;
}

.material-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.material-tag {
  cursor: pointer;
  border-radius: 8px !important;
  transition: all 0.2s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.material-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.material-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(82,183,136,0.04);
  border-radius: 10px;
}

.material-name {
  font-size: 13px;
  color: #333333;
}

.link-btn {
  font-size: 12px;
  color: #52b788 !important;

  &:hover {
    color: #2d6a4f !important;
  }
}

.glass-dialog {
  :deep(.el-dialog) {
    background: rgba(255,255,255,0.92);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255,255,255,0.5);
    border-radius: 24px;
    box-shadow: 0 20px 60px rgba(26,77,46,0.15);
  }

  :deep(.el-dialog__header) {
    padding: 24px 24px 0;
    margin: 0;
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333333;

  .dialog-icon {
    width: 28px;
    height: 28px;
    color: #52b788;
    animation: pulse 1.5s ease-in-out infinite;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

.progress-content {
  padding: 20px 0;
}

.progress-text {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #2d6a4f;
}

:deep(.el-empty__description) {
  color: #666666;
}

:deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.8);
  box-shadow: 0 0 0 1px rgba(82,183,136,0.3);
  border-radius: 12px;

  &:hover, &.is-focus {
    box-shadow: 0 0 0 1px #52b788;
  }
}

:deep(.el-select__wrapper) {
  background: rgba(255,255,255,0.8);
  box-shadow: 0 0 0 1px rgba(82,183,136,0.3);
  border-radius: 12px;

  &:hover, &.is-focused {
    box-shadow: 0 0 0 1px #52b788;
  }
}

:deep(.el-tag) {
  border-radius: 8px;
}

:deep(.el-progress-bar__outer) {
  background: rgba(82,183,136,0.1);
  border-radius: 10px;
}
</style>
