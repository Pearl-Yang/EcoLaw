<template>
  <div class="workbench">
    <section class="hero-carousel" aria-label="平台动态与推荐">
      <el-carousel
        height="300px"
        :interval="6000"
        indicator-position="outside"
        class="wb-carousel"
      >
        <el-carousel-item v-for="slide in roleNames" :key="slide.id">
          <div
            class="slide-inner"
            :style="{ backgroundImage: `linear-gradient(105deg, rgba(15, 60, 45, 0.88) 0%, rgba(15, 60, 45, 0.35) 48%, rgba(20, 50, 40, 0.2) 100%), url(${slide.image})` }"
          >
            <div class="slide-content">
              <span class="slide-tag">{{ slide.tag }}</span>
              <h2 class="slide-title">{{ slide.title }}</h2>
              <p class="slide-desc">{{ slide.desc }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="stat-row" aria-label="数据摘要">
      <div v-for="s in statCards" :key="s.key" class="stat-card">
        <div class="stat-icon" :class="s.tone">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path :d="s.icon" />
          </svg>
        </div>
        <div class="stat-body">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </section>

    <section class="quick-section">
      <div class="section-head">
        <h2>快捷入口</h2>
        <p>按身份为您展示常用功能，点击即可跳转</p>
      </div>
      <div class="quick-grid">
        <router-link
          v-for="item in quickEntries"
          :key="item.path + item.label"
          :to="item.path"
          class="quick-card"
        >
          <span class="quick-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path :d="item.icon" />
            </svg>
          </span>
          <span class="quick-label">{{ item.label }}</span>
          <span class="quick-hint">{{ item.hint }}</span>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getUserInfo, getUserRole } from '@/utils/auth'
import { getDemoUser, getRoleText } from '@/utils/mockUser'

defineOptions({ name: 'DashboardWorkbench' })

// 使用虚拟用户数据
const userInfo = computed(() => {
  const info = getUserInfo()
  if (!info || !info.id) {
    return getDemoUser()
  }
  return info
})

const userRole = computed(() => userInfo.value?.role || 'super_admin')

// 使用虚拟数据的角色标签
const roleLabel = computed(() => getRoleText(userRole.value) || '用户')

// 欢迎信息
const displayName = computed(() => {
  return userInfo.value?.nickname || userInfo.value?.username || '用户'
})

const roleNames = [
  {
    id: 1,
    tag: '政策聚焦',
    title: '白色污染治理专项行动',
    desc: '协同监管、企业合规与法律服务，一站式推进减塑与回收',
    image: 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=1600&q=80'
  },
  {
    id: 2,
    tag: 'AI 普法',
    title: '智能素材与法条解读',
    desc: '素材生成、智能组卷与对话能力，提升普法与培训效率',
    image: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1600&q=80'
  },
  {
    id: 3,
    tag: '数据看板',
    title: '任务与企业全链路可视',
    desc: '从任务下发到执行反馈，数据驱动决策与考核',
    image: 'https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=1600&q=80'
  },
  {
    id: 4,
    tag: '合规共建',
    title: '政企律协同治理',
    desc: '连接政府端、企业端与律所端，共建绿色法治生态',
    image: 'https://images.unsplash.com/photo-1497435334941-0c3be9f9d4b8?w=1600&q=80'
  }
]

const statCards = [
  {
    key: 'tasks',
    label: '进行中任务',
    value: '128',
    tone: 'emerald',
    icon: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2 M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2'
  },
  {
    key: 'ents',
    label: '纳管企业',
    value: '2.4k',
    tone: 'teal',
    icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10'
  },
  {
    key: 'compliance',
    label: '合规率',
    value: '94.2%',
    tone: 'amber',
    icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11'
  },
  {
    key: 'ai',
    label: '本月 AI 调用',
    value: '15.6万',
    tone: 'violet',
    icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01'
  }
]

const ALL_ROLES = ['government', 'enterprise', 'law_firm', 'platform', 'super_admin']

const ALL_QUICK = [
  { label: '数据概览', path: '/dashboard/data-analysis', hint: '图表与 KPI', icon: 'M18 20V10M12 20V4M6 20v-6', roles: ALL_ROLES },
  { label: '智能检索', path: '/ai/smart-search', hint: '法规语义搜索', icon: 'M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z', roles: ALL_ROLES },
  { label: '任务管理', path: '/government/tasks', hint: '下发与跟踪', icon: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2 M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2', roles: ['government', 'platform', 'super_admin'] },
  { label: '普法宣传', path: '/government/publicity', hint: '内容与活动', icon: 'M19 20H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v1m2 13a2 2 0 0 1-2-2V7m2 13a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-2', roles: ['government', 'platform', 'super_admin'] },
  { label: '企业管理', path: '/government/enterprises', hint: '名录与档案', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10', roles: ['government', 'platform', 'super_admin'] },
  { label: '政府分析', path: '/government/analytics', hint: '指标与图表', icon: 'M3 3v18h18 M7 16l4-4 4 4 6-6', roles: ['government', 'platform', 'super_admin'] },
  { label: '职员管理', path: '/system/employee', hint: '查询与导出台账', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z', roles: ['government', 'enterprise', 'platform', 'super_admin'] },
  { label: '培训管理', path: '/enterprise/training', hint: '课程与考核', icon: 'M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z', roles: ['enterprise', 'platform', 'super_admin'] },
  { label: '合规台账', path: '/enterprise/compliance', hint: '记录与备查', icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11', roles: ['enterprise', 'platform', 'super_admin'] },
  { label: '案例管理', path: '/law-firm/cases', hint: '案例库', icon: 'M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z', roles: ['law_firm', 'platform', 'super_admin'] },
  { label: '咨询对接', path: '/law-firm/consult', hint: '在线咨询', icon: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z', roles: ['law_firm', 'platform', 'super_admin'] },
  { label: '数据驾驶舱', path: '/big-screen', hint: '数据大屏', icon: 'M3 3h7v7H3z M14 3h7v7h-7z M14 14h7v7h-7z M3 14h7v7H3z', roles: ALL_ROLES },
  { label: 'AI 工具', path: '/ai-tools/material-gen', hint: 'AI 能力', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01', roles: ALL_ROLES },
  { label: '用户管理', path: '/system/users', hint: '系统', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75', roles: ['platform', 'super_admin'] }
]

const quickEntries = computed(() => {
  const r = userRole.value
  if (!r) return ALL_QUICK.filter(q => q.roles.includes('government'))
  return ALL_QUICK.filter(q => q.roles.includes(r))
})
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

$primary: #2563EB;
$text: #0F172A;
$muted: #64748B;
$border: #E2E8F0;
$bg-gray: #F1F5F9;

.workbench {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: $text;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 32px;
}

.hero-carousel {
  margin: -4px -8px 24px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(0, 0, 0, 0.04);
}

:deep(.wb-carousel.el-carousel--horizontal) {
  .el-carousel__container {
    border-radius: 16px;
  }
  .el-carousel__indicators--outside {
    margin-top: 12px;
  }
  .el-carousel__indicator button {
    background: rgba($primary, 0.3);
    width: 8px;
    height: 8px;
    border-radius: 4px;
  }
  .el-carousel__indicator.is-active button {
    background: $primary;
    width: 24px;
  }
}

.slide-inner {
  height: 100%;
  min-height: 220px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: flex-end;
  padding: 28px 32px;
  box-sizing: border-box;
}

.slide-content {
  max-width: 520px;
}

.slide-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: rgba($primary, 0.85);
  backdrop-filter: blur(8px);
  margin-bottom: 10px;
}

.slide-title {
  margin: 0 0 8px;
  font-size: clamp(1.25rem, 2.8vw, 1.65rem);
  font-weight: 700;
  color: #fff;
  line-height: 1.35;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
}

.slide-desc {
  margin: 0;
  font-size: 14px;
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.92);
  text-shadow: 0 1px 8px rgba(0, 0, 0, 0.25);
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid $border;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 22px;
    height: 22px;
  }

  &.emerald {
    background: rgba($primary, 0.1);
    color: $primary;
  }
  &.teal {
    background: rgba(16, 185, 129, 0.1);
    color: #10B981;
  }
  &.amber {
    background: rgba(245, 158, 11, 0.1);
    color: #D97706;
  }
  &.violet {
    background: rgba(139, 92, 246, 0.1);
    color: #7C3AED;
  }
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: $text;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 13px;
  color: $muted;
}

.quick-section {
  margin-bottom: 28px;
}

.section-head {
  margin-bottom: 16px;

  h2 {
    margin: 0 0 4px;
    font-size: 16px;
    font-weight: 600;
    color: $text;
  }

  p {
    margin: 0;
    font-size: 13px;
    color: $muted;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(148px, 1fr));
  gap: 12px;
}

.quick-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid $border;
  text-decoration: none;
  color: inherit;
  transition: border-color 0.2s, box-shadow 0.2s, transform 0.2s;

  &:hover {
    border-color: rgba($primary, 0.3);
    box-shadow: 0 4px 14px rgba($primary, 0.08);
    transform: translateY(-2px);
  }
}

.quick-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba($primary, 0.08);
  color: $primary;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;

  svg {
    width: 18px;
    height: 18px;
  }
}

.quick-label {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  color: $text;
}

.quick-hint {
  font-size: 12px;
  color: $muted;
}

@media (max-width: 900px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 520px) {
  .stat-row {
    grid-template-columns: 1fr;
  }
  .slide-inner {
    padding: 20px;
  }
}
</style>
