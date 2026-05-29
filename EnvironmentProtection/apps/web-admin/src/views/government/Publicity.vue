<template>
  <div class="pub-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">普法宣传管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="showGen = true">
          <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2L12 6M12 18L12 22M4.93 4.93L7.76 7.76M16.24 16.24L19.07 19.07M2 12L6 12M18 12L22 12M4.93 19.07L7.76 16.24M16.24 7.76L19.07 4.93"/>
          </svg>
          AI 生成素材
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6" v-for="s in stats" :key="s.label">
        <div class="stat-card" :style="{ background: s.gradient }">
          <span class="stat-num">{{ s.value }}</span>
          <span class="stat-lbl">{{ s.label }}</span>
          <span class="stat-trend" :class="s.up ? 'up' : 'dn'">
            <svg class="trend-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path v-if="s.up" d="M12 19V5M5 12l7-7 7 7"/>
              <path v-else d="M12 5v14M5 12l7 7 7-7"/>
            </svg>
            {{ s.change }}%
          </span>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mb-20">
      <!-- 宣传素材列表 -->
      <el-col :span="16">
        <div class="glass-card">
          <div class="card-header-row">
            <span class="panel-title">AI 宣传素材库</span>
          </div>
          <el-table :data="materials" stripe :row-class-name="tableRowClassName">
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="typeMap[row.type]">{{ typeText[row.type] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="target" label="受众" width="110" />
            <el-table-column prop="createdAt" label="生成时间" width="160" />
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
                <el-button type="success" link size="small" @click="handleDownload(row)">下载</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-row">
            <el-pagination layout="prev, pager, next" :total="50" />
          </div>
        </div>
      </el-col>

      <!-- 发布渠道 -->
      <el-col :span="8">
        <div class="glass-card">
          <div class="card-header-row">
            <span class="panel-title">发布渠道</span>
          </div>
          <div class="channel-list">
            <div v-for="ch in channels" :key="ch.name" class="channel-item">
              <span class="ch-name">{{ ch.name }}</span>
              <span class="ch-count">{{ ch.count }} 条</span>
              <el-switch v-model="ch.on" size="small" />
            </div>
          </div>
          <div class="divider-line"></div>
          <div class="publish-btn-wrap">
            <el-button type="primary" style="width: 100%" :disabled="!channels.some(c => c.on)" @click="handlePublish">
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
              </svg>
              一键发布到已选渠道
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- AI 生成工作台：主区 + 右侧可折叠历史（豆包式） -->
    <Teleport to="body">
      <Transition name="ai-gen-fade">
        <div v-if="showGen" class="ai-gen-overlay" @click.self="showGen = false">
          <div class="ai-gen-shell" @click.stop>
            <div class="ai-gen-main">
              <div class="ai-gen-main-head">
                <div>
                  <h2 class="ai-gen-title">AI 宣传素材生成</h2>
                  <p class="ai-gen-sub">填写需求后生成内容，历史记录固定在右侧，可随时切换回顾</p>
                </div>
                <el-button text type="primary" class="ai-gen-close" @click="showGen = false">
                  <span class="ai-gen-close-x">×</span> 关闭
                </el-button>
              </div>
              <el-form :model="genForm" label-width="100px" class="ai-gen-form">
                <el-form-item label="素材类型">
                  <el-select v-model="genForm.type" style="width: 100%">
                    <el-option label="宣传海报（图片）" value="image" />
                    <el-option label="宣传文案（文本）" value="text" />
                    <el-option label="视频脚本" value="video" />
                    <el-option label="H5 页面" value="h5" />
                  </el-select>
                </el-form-item>
                <el-form-item label="宣传主题">
                  <el-input v-model="genForm.topic" placeholder="如：农膜回收、禁塑令、餐饮商户减塑" />
                </el-form-item>
                <el-form-item label="目标受众">
                  <el-select v-model="genForm.target" style="width: 100%">
                    <el-option label="普通群众" value="public" />
                    <el-option label="企业管理人员" value="enterprise" />
                    <el-option label="政府监管人员" value="government" />
                    <el-option label="学校师生" value="school" />
                  </el-select>
                </el-form-item>
                <el-form-item label="生成数量">
                  <el-input-number v-model="genForm.count" :min="1" :max="10" />
                </el-form-item>
              </el-form>
              <div class="ai-gen-actions">
                <el-button type="primary" size="large" :loading="genLoading" @click="handleGenerate">
                  <svg v-if="!genLoading" class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 2L12 6M12 18L12 22M4.93 4.93L7.76 7.76M16.24 16.24L19.07 19.07M2 12L6 12M18 12L22 12M4.93 19.07L7.76 16.24M16.24 7.76L19.07 4.93"/>
                  </svg>
                  生成
                </el-button>
              </div>
              <div v-if="lastGeneratedContent" class="ai-gen-result">
                <div class="ai-gen-result-hd">本次生成结果</div>
                <div class="ai-gen-result-bd">{{ lastGeneratedContent }}</div>
              </div>
            </div>

            <!-- 右侧：窄条手柄 + 历史列表 -->
            <aside class="ai-gen-history-aside">
              <button
                type="button"
                class="ai-gen-rail"
                :title="historyExpanded ? '收起历史' : '展开历史'"
                @click="historyExpanded = !historyExpanded"
              >
                <svg class="rail-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                  <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <span class="rail-chevron" :class="{ 'flip': historyExpanded }">›</span>
                <span class="rail-caption">历史</span>
              </button>
              <div v-show="historyExpanded" class="ai-gen-history-panel">
                <div class="ai-gen-history-panel-hd">
                  <span>历史生成</span>
                  <span class="ai-gen-history-count">{{ genHistory.length }} 条</span>
                </div>
                <div class="ai-gen-history-scroll">
                  <button
                    v-for="item in genHistory"
                    :key="item.id"
                    type="button"
                    class="ai-gen-history-item"
                    :class="{ active: activeHistoryId === item.id }"
                    @click="applyHistory(item)"
                  >
                    <span class="hi-title">{{ item.title }}</span>
                    <span class="hi-meta">
                      <el-tag size="small" :type="typeMap[item.type]">{{ typeText[item.type] }}</el-tag>
                      {{ item.createdAt }}
                    </span>
                  </button>
                  <p v-if="!genHistory.length" class="ai-gen-history-empty">暂无历史，生成后会出现在这里</p>
                </div>
              </div>
            </aside>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 素材预览 -->
    <el-dialog v-model="showPreview" title="素材预览" width="700px" class="glass-dialog">
      <div class="preview-body">
        <h4>{{ previewItem.title }}</h4>
        <p class="preview-meta">类型：{{ typeText[previewItem.type] }} | 受众：{{ previewItem.target }} | {{ previewItem.createdAt }}</p>
        <div class="divider-line"></div>
        <div class="preview-content">{{ previewItem.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const stats = reactive([
  { label: '本月生成素材', value: '86', change: '22', up: true, gradient: 'linear-gradient(135deg, #1a4d2e, #2d6a4f)' },
  { label: '已发布宣传', value: '1,240', change: '18', up: true, gradient: 'linear-gradient(135deg, #2d6a4f, #52b788)' },
  { label: '覆盖受众', value: '3.6 万', change: '12', up: true, gradient: 'linear-gradient(135deg, #52b788, #95d5b2)' },
  { label: '举报线索', value: '37', change: '5', up: false, gradient: 'linear-gradient(135deg, #2d6a4f, #1a4d2e)' }
])

const typeMap = { image: 'success', text: 'primary', video: 'warning', h5: 'info' }
const typeText = { image: '图片', text: '文案', video: '视频', h5: 'H5' }

const materials = reactive([
  { id: 1, title: '农村农膜回收宣传折页', type: 'image', target: '村民', createdAt: '2026-04-04 10:30', content: '【农膜回收】请将使用后的农膜收集送至指定回收点，严禁焚烧或随意丢弃。支持农业绿色发展，共建美丽乡村。' },
  { id: 2, title: '餐饮商户禁塑告知书', type: 'text', target: '餐饮商户', createdAt: '2026-04-03 15:20', content: '依据《固体废物污染环境防治法》，禁止生产、销售不可降解塑料购物袋、塑料餐具…' },
  { id: 3, title: '快递包装减量宣传视频脚本', type: 'video', target: '快递企业', createdAt: '2026-04-02 09:15', content: '第一幕：快递员送货场景；第二幕：过度包装问题展示…' }
])

const channels = reactive([
  { name: '微信公众号', count: 128, on: true },
  { name: '村居公告栏', count: 86, on: true },
  { name: '企业微信群', count: 54, on: false },
  { name: '抖音/短视频', count: 32, on: false }
])

const targetLabelMap = { public: '普通群众', enterprise: '企业管理人员', government: '政府监管人员', school: '学校师生' }

const showGen = ref(false)
const showPreview = ref(false)
const genLoading = ref(false)
const historyExpanded = ref(true)
const activeHistoryId = ref(null)
const lastGeneratedContent = ref('')
const genForm = reactive({ type: 'text', topic: '', target: 'public', count: 1 })
const previewItem = reactive({})

/** 侧边栏历史：与豆包「历史对话」同级交互 */
const genHistory = ref(
  materials.map((m) => ({
    id: m.id,
    title: m.title,
    topic: m.title,
    type: m.type,
    target: m.target,
    createdAt: m.createdAt,
    content: m.content
  }))
)

const tableRowClassName = () => 'hover-highlight'

function buildMockContent () {
  const t = genForm.topic
  const aud = targetLabelMap[genForm.target] || '受众'
  const map = {
    text: `【${t} · 宣传文案】\n\n面向${aud}：\n一、背景说明\n二、法规要点与行动倡议\n三、联系方式与监督渠道\n\n（请结合实务复核后再对外发布。）`,
    image: `【${t} · 海报要点】\n主标题、副标题、核心图标（减塑/回收）、二维码落版、主色建议 #2d6a4f。`,
    video: `【${t} · 视频脚本】\n开场钩子 → 问题呈现 → 政策一句带过 → 行动号召 → 结尾标语。`,
    h5: `【${t} · H5 结构】\n封面页 / 知识卡片×3 / 互动问答 / 分享页。`
  }
  return map[genForm.type] || map.text
}

function padTime (n) {
  return String(n).padStart(2, '0')
}
function nowStamp () {
  const d = new Date()
  return `${d.getFullYear()}-${padTime(d.getMonth() + 1)}-${padTime(d.getDate())} ${padTime(d.getHours())}:${padTime(d.getMinutes())}`
}

const applyHistory = (item) => {
  activeHistoryId.value = item.id
  genForm.type = item.type
  genForm.topic = item.topic || item.title
  const tgt = Object.entries(targetLabelMap).find(([, v]) => v === item.target)
  genForm.target = tgt ? tgt[0] : 'public'
  lastGeneratedContent.value = item.content || ''
}

const handleGenerate = async () => {
  if (!genForm.topic) { ElMessage.warning('请输入宣传主题'); return }
  genLoading.value = true
  try {
    await new Promise((r) => setTimeout(r, 2000))
    const content = buildMockContent()
    const titleShort = genForm.topic.length > 22 ? `${genForm.topic.slice(0, 22)}…` : genForm.topic
    const newId = Date.now()
    const row = {
      id: newId,
      title: titleShort,
      type: genForm.type,
      target: targetLabelMap[genForm.target],
      createdAt: nowStamp(),
      content
    }
    materials.unshift(row)
    genHistory.value.unshift({
      id: newId,
      title: titleShort,
      topic: genForm.topic,
      type: genForm.type,
      target: row.target,
      createdAt: row.createdAt,
      content
    })
    activeHistoryId.value = newId
    lastGeneratedContent.value = content
    ElMessage.success('生成成功！素材已入库，并记录在右侧历史')
  } catch { ElMessage.error('生成失败') } finally { genLoading.value = false }
}

const handleView = (row) => { Object.assign(previewItem, row); showPreview.value = true }
const handleDownload = (row) => ElMessage.info('下载功能待对接')
const handleDelete = (row) => {
  const i = materials.findIndex(m => m.id === row.id)
  if (i !== -1) materials.splice(i, 1)
  const hi = genHistory.value.findIndex(h => h.id === row.id)
  if (hi !== -1) genHistory.value.splice(hi, 1)
  if (activeHistoryId.value === row.id) {
    activeHistoryId.value = null
    lastGeneratedContent.value = ''
  }
  ElMessage.success('已删除')
}
const handlePublish = () => ElMessage.success('已发布到已选渠道')
</script>

<style lang="scss" scoped>
.mb-20 { margin-bottom: 20px; }

.pub-container {
  min-height: 100%;
  background: #f0f4f0;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 4px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1a4d2e;
  letter-spacing: 0.5px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
  vertical-align: middle;
}

.stat-card {
  border-radius: 16px;
  padding: 20px 20px 16px;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: rgba(255, 255, 255, 0.7) !important;
  color: #1a4d2e !important;
  box-shadow: 0 4px 24px rgba(82, 183, 136, 0.06);

  .stat-num { font-size: 26px; font-weight: 700; }
  .stat-lbl { font-size: 13px; color: #2d6a4f; }
  .stat-trend { font-size: 12px; display: flex; align-items: center; gap: 4px;
    &.up { color: #52b788; } &.dn { color: #e76f51; }
  }
  .trend-icon { width: 14px; height: 14px; }
}

.glass-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 24px rgba(82, 183, 136, 0.06);
  padding: 20px;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(82, 183, 136, 0.1);
}

.panel-title {
  font-weight: 600;
  color: #1a4d2e;
  font-size: 15px;
}

.channel-list {
  .channel-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px solid rgba(82, 183, 136, 0.08);
    &:last-child { border-bottom: none; }
    .ch-name { flex: 1; font-size: 14px; color: #2d6a4f; }
    .ch-count { font-size: 12px; color: #52b788; }
  }
}

.divider-line {
  height: 1px;
  background: rgba(82, 183, 136, 0.1);
  margin: 16px 0;
}

.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }

.preview-body {
  h4 { margin: 0 0 8px; color: #1a4d2e; font-size: 18px; font-weight: 600; }
  .preview-meta { font-size: 12px; color: #52b788; margin-bottom: 12px; }
  .preview-content { font-size: 14px; line-height: 1.7; color: #2d6a4f; white-space: pre-wrap; }
}

:deep(.el-table) {
  --el-table-border-color: rgba(82, 183, 136, 0.08);
  --el-table-header-bg-color: rgba(82, 183, 136, 0.04);
  
  th.el-table__cell {
    background-color: rgba(82, 183, 136, 0.04) !important;
    color: #1a4d2e;
    font-weight: 600;
  }
  
  .hover-highlight:hover > td {
    background-color: rgba(82, 183, 136, 0.04) !important;
  }
}

:deep(.el-tag) {
  border-radius: 6px;
}

:deep(.glass-dialog) {
  .el-dialog {
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 8px 32px rgba(26, 77, 46, 0.15);
  }
  .el-dialog__header {
    border-bottom: 1px solid rgba(82, 183, 136, 0.1);
    .el-dialog__title { color: #1a4d2e; font-weight: 600; }
  }
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #52b788, #2d6a4f);
  border-color: #52b788;
  &:hover { background: linear-gradient(135deg, #2d6a4f, #1a4d2e); border-color: #2d6a4f; }
}

:deep(.el-input__wrapper), :deep(.el-select__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(82, 183, 136, 0.2);
  &:hover { box-shadow: 0 0 0 1px rgba(82, 183, 136, 0.4); }
  &.is-focus { box-shadow: 0 0 0 2px rgba(82, 183, 136, 0.3); }
}

:deep(.el-pagination) {
  .el-pager li { border-radius: 8px; &.is-active { background: #52b788; } }
}

/* —— AI 生成工作台 + 右侧历史边栏 —— */
.ai-gen-fade-enter-active,
.ai-gen-fade-leave-active {
  transition: opacity 0.2s ease;
}
.ai-gen-fade-enter-from,
.ai-gen-fade-leave-to {
  opacity: 0;
}

.ai-gen-overlay {
  position: fixed;
  inset: 0;
  z-index: 3000;
  background: rgba(26, 77, 46, 0.35);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: stretch;
  justify-content: flex-end;
}

.ai-gen-shell {
  display: flex;
  width: min(1120px, 100%);
  max-width: 100%;
  height: 100%;
  background: linear-gradient(165deg, #f7faf7 0%, #eef5f0 45%, #e8f0eb 100%);
  box-shadow: -12px 0 48px rgba(26, 77, 46, 0.18);
  border-radius: 20px 0 0 20px;
  overflow: hidden;
}

.ai-gen-main {
  flex: 1;
  min-width: 0;
  padding: 28px 32px 32px;
  overflow-y: auto;
}

.ai-gen-main-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.ai-gen-title {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 700;
  color: #1a4d2e;
  letter-spacing: 0.3px;
}

.ai-gen-sub {
  margin: 0;
  font-size: 13px;
  color: #52b788;
  line-height: 1.5;
  max-width: 520px;
}

.ai-gen-close {
  flex-shrink: 0;
  font-size: 14px;
}

.ai-gen-close-x {
  font-size: 20px;
  line-height: 1;
  margin-right: 4px;
  vertical-align: middle;
}

.ai-gen-form {
  max-width: 560px;
}

.ai-gen-actions {
  margin-top: 8px;
  margin-bottom: 24px;
}

.ai-gen-result {
  max-width: 640px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(82, 183, 136, 0.2);
  padding: 16px 18px;
  box-shadow: 0 4px 20px rgba(82, 183, 136, 0.08);
}

.ai-gen-result-hd {
  font-size: 12px;
  font-weight: 600;
  color: #52b788;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.ai-gen-result-bd {
  font-size: 14px;
  line-height: 1.75;
  color: #2d6a4f;
  white-space: pre-wrap;
}

.ai-gen-history-aside {
  display: flex;
  flex-direction: row;
  flex-shrink: 0;
  background: linear-gradient(180deg, #153d28 0%, #0f2e1e 100%);
  border-radius: 20px 0 0 20px;
  box-shadow: inset 1px 0 0 rgba(255, 255, 255, 0.06);
}

.ai-gen-rail {
  width: 52px;
  min-height: 100%;
  border: none;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  background: transparent;
  color: rgba(255, 255, 255, 0.92);
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 20px 0 0 20px;

  &:hover {
    background: rgba(255, 255, 255, 0.06);
  }
}

.rail-icon {
  width: 22px;
  height: 22px;
  opacity: 0.9;
}

.rail-chevron {
  font-size: 22px;
  font-weight: 300;
  line-height: 1;
  transition: transform 0.25s ease;
  opacity: 0.95;

  &.flip {
    transform: scaleX(-1);
  }
}

.rail-caption {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  font-size: 12px;
  letter-spacing: 0.2em;
  opacity: 0.85;
  margin-top: 8px;
}

.ai-gen-history-panel {
  width: 280px;
  border-left: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  min-height: 100%;
  animation: ai-history-in 0.22s ease;
}

@keyframes ai-history-in {
  from {
    opacity: 0;
    transform: translateX(8px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.ai-gen-history-panel-hd {
  padding: 18px 16px 12px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.ai-gen-history-count {
  font-size: 12px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.55);
}

.ai-gen-history-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 10px 10px 20px;
}

.ai-gen-history-item {
  width: 100%;
  text-align: left;
  border: none;
  border-radius: 12px;
  padding: 12px 12px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.92);
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  &.active {
    background: rgba(82, 183, 136, 0.35);
    box-shadow: 0 0 0 1px rgba(149, 213, 178, 0.35);
  }
}

.hi-title {
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.hi-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

.ai-gen-history-empty {
  margin: 24px 12px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  line-height: 1.6;
  text-align: center;
}

.ai-gen-history-scroll :deep(.el-tag) {
  --el-tag-border-color: transparent;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.9);
}
</style>
