<template>
  <div class="embed-wrap">
    <div class="toolbar">
      <div class="toolbar-text">
        <span class="label">数据大屏预览</span>
        <span class="hint">与公开地址同源，便于在后台核对投屏内容</span>
      </div>
      <div class="toolbar-actions">
        <el-button type="primary" plain @click="openPublic">新窗口打开</el-button>
        <el-button @click="toggleFrameFullScreen">iframe 全屏</el-button>
      </div>
    </div>
    <div ref="frameHost" class="iframe-host">
      <iframe
        title="数据大屏"
        class="screen-iframe"
        :src="publicUrl"
        referrerpolicy="no-referrer-when-downgrade"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import screenfull from 'screenfull'

defineOptions({ name: 'BigScreenEmbed' })

const frameHost = ref(null)

const publicUrl = computed(() => {
  const base = import.meta.env.BASE_URL || '/'
  const path = base.endsWith('/') ? `${base}big-screen` : `${base}/big-screen`
  if (typeof window !== 'undefined') {
    return `${window.location.origin}${path}`
  }
  return path.startsWith('/') ? path : `/${path}`
})

function openPublic() {
  window.open(publicUrl.value, '_blank', 'noopener,noreferrer')
}

function toggleFrameFullScreen() {
  const el = frameHost.value
  if (!el || !screenfull.isEnabled) return
  screenfull.toggle(el)
}
</script>

<style lang="scss" scoped>
.embed-wrap {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  min-height: 480px;
  gap: 12px;
}

.toolbar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #ffffff, #f0fdf4);
  border: 1px solid rgba(82, 183, 136, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 14px rgba(45, 106, 79, 0.06);
}

.toolbar-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.label {
  font-size: 15px;
  font-weight: 600;
  color: #1a4d2e;
}

.hint {
  font-size: 12px;
  color: #6b7280;
}

.toolbar-actions {
  flex-shrink: 0;
  display: flex;
  gap: 8px;
}

.iframe-host {
  flex: 1;
  min-height: 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(82, 183, 136, 0.15);
  background: #020814;
}

.screen-iframe {
  display: block;
  width: 100%;
  height: 100%;
  border: none;
}
</style>
