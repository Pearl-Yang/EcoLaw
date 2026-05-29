<template>
  <div class="layout-wrapper">
    <!-- 顶部导航栏 -->
    <header class="glass-nav">
      <div class="nav-left">
        <div class="nav-logo">
          <svg viewBox="0 0 32 32" fill="none">
            <circle cx="16" cy="16" r="14" fill="rgba(82,183,136,0.15)" stroke="rgba(82,183,136,0.4)" stroke-width="1"/>
            <path d="M10 20 C10 14 12 10 16 10 C20 10 22 14 22 20 C22 24 20 26 18 27 C17 27.5 16.5 28 16 28 C15.5 28 15 27.5 14 27 C12 26 10 24 10 20Z" fill="#52b788"/>
            <path d="M14 18 C15 16 17 16 18 18" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/>
            <circle cx="16" cy="14" r="2" fill="#fff" opacity="0.6"/>
          </svg>
          <span class="logo-text">绿法通</span>
        </div>
      </div>

      <div class="nav-center">
        <SearchBox
          v-model="searchValue"
          :hot-searches="['白色污染治理', '企业合规', '农膜回收', '塑料制品禁限', '普法宣传']"
          :categories="[
            { label: '全部', value: 'all' },
            { label: '任务', value: 'task' },
            { label: '企业', value: 'enterprise' },
            { label: '法规', value: 'law' },
            { label: '知识库', value: 'knowledge' }
          ]"
          placeholder="搜索任务、企业、法规..."
          show-category
          @search="handleSearch"
        />
        <div class="nav-menu">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: isActive(item.path) }"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path :d="item.icon"/>
            </svg>
            <span>{{ item.label }}</span>
          </router-link>
        </div>
      </div>

      <div class="nav-right">
        <div class="role-badge">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
          <span>{{ currentRoleName }}</span>
        </div>

        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-menu">
            <el-avatar :size="32" class="user-avatar">
              {{ (userInfo?.nickname || '用户').slice(0, 1) }}
            </el-avatar>
            <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 侧边栏 + 主内容 -->
    <div class="body-wrapper">
      <!-- 侧边栏 -->
      <aside class="glass-sidebar" :class="{ collapsed: isCollapse }">
        <div class="sidebar-toggle" @click="toggleCollapse">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="3" y1="12" x2="21" y2="12"/>
            <line x1="3" y1="6" x2="21" y2="6"/>
            <line x1="3" y1="18" x2="21" y2="18"/>
          </svg>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <template v-for="group in menuGroups" :key="group.title">
            <div v-if="!isCollapse" class="menu-group-title">{{ group.title }}</div>
            <el-menu-item
              v-for="item in group.items"
              :key="item.path"
              :index="item.path"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path :d="item.icon"/>
              </svg>
              <span>{{ item.label }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </aside>

      <!-- 主内容区 -->
      <main class="main-content">
        <div class="page-container">
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getUserInfo, getUserRole, logout } from '@/utils/auth'
import { getDemoUser, getRoleText } from '@/utils/mockUser'
import SearchBox from '@/components/SearchBox.vue'

const route = useRoute()
const router = useRouter()

// 初始化虚拟用户数据
const initDemoUser = () => {
  const currentUser = getUserInfo()
  if (!currentUser || !currentUser.id) {
    const demoUser = getDemoUser()
    localStorage.setItem('lvfat_user', JSON.stringify(demoUser))
    localStorage.setItem('lvfat_token', 'mock-token-' + Date.now())
  }
}
initDemoUser()

const searchValue = ref('')
const handleSearch = (val) => {
  if (val?.trim()) {
    ElMessage.success(`搜索: ${val}`)
  }
}

const isCollapse = ref(false)

// 确保获取到用户信息（使用虚拟数据）
const userInfo = computed(() => {
  const info = getUserInfo()
  if (!info || !info.id) {
    return getDemoUser()
  }
  return info
})

const userRole = computed(() => userInfo.value?.role || 'super_admin')

const roleConfig = {
  government: { name: '政府监管', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
  law_firm: { name: '律所服务', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  enterprise: { name: '企业合规', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  platform: { name: '平台管理', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' },
  super_admin: { name: '超级管理员', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' }
}

// 使用虚拟数据的角色名称
const currentRoleName = computed(() => getRoleText(userInfo.value?.role) || roleConfig[userInfo.value?.role]?.name || '用户')

const navItems = computed(() => {
  const basePath = getUserRole() || 'government'

  // 政府端用户
  if (basePath === 'government') {
    return [
      { label: '工作台', path: '/dashboard/workbench', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: '政府端', path: '/government', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
      { label: 'AI工具', path: '/ai-tools', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' }
    ]
  }

  // 企业端用户
  if (basePath === 'enterprise') {
    return [
      { label: '工作台', path: '/dashboard/workbench', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: '企业端', path: '/enterprise', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: 'AI工具', path: '/ai-tools', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' }
    ]
  }

  // 律所端用户
  if (basePath === 'law_firm') {
    return [
      { label: '工作台', path: '/dashboard/workbench', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: '律所端', path: '/law-firm', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
      { label: 'AI工具', path: '/ai-tools', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' }
    ]
  }

  // 超级管理员和平台管理员可以看到所有
  return [
    { label: '工作台', path: '/dashboard/workbench', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
    { label: '政府端', path: '/government', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
    { label: '企业端', path: '/enterprise', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
    { label: '律所端', path: '/law-firm', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
    { label: 'AI工具', path: '/ai-tools', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' },
    { label: '系统管理', path: '/system', icon: 'M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6z' }
  ]
})

const menuGroups = computed(() => {
  const basePath = userRole.value || 'government'

  const baseMenu = {
    title: '仪表盘',
    items: [
      { label: '工作台', path: '/dashboard/workbench', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: '数据分析', path: '/dashboard/data-analysis', icon: 'M18 20V10M12 20V4M6 20v-6' }
    ]
  }

  // 政府端菜单
  const governmentMenu = {
    title: '政府端',
    items: [
      { label: '任务管理', path: '/government/tasks', icon: 'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2 M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2' },
      { label: '普法宣传', path: '/government/publicity', icon: 'M19 20H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v1m2 13a2 2 0 0 1-2-2V7m2 13a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-2' },
      { label: '企业管理', path: '/government/enterprises', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10' },
      { label: '数据分析', path: '/government/analytics', icon: 'M18 20V10M12 20V4M6 20v-6' },
      { label: '报表导出', path: '/government/reports', icon: 'M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4 M7 10l5 5 5-5 M12 15V3' },
      { label: '职员管理', path: '/system/employee', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z' }
    ]
  }

  // 企业端菜单
  const enterpriseMenu = {
    title: '企业端',
    items: [
      { label: '培训管理', path: '/enterprise/training', icon: 'M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z' },
      { label: '员工管理', path: '/enterprise/employees', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2' },
      { label: '职员档案', path: '/system/employee', icon: 'M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2 M9 12h6M9 16h6' },
      { label: '合规台账', path: '/enterprise/compliance', icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11' }
    ]
  }

  // 律所端菜单
  const lawFirmMenu = {
    title: '律所端',
    items: [
      { label: '案例管理', path: '/law-firm/cases', icon: 'M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z' },
      { label: '客户管理', path: '/law-firm/clients', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' },
      { label: '咨询对接', path: '/law-firm/consult', icon: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' }
    ]
  }

  // AI工具菜单
  const aiToolsMenu = {
    title: 'AI工具库',
    items: [
      { label: '素材生成', path: '/ai-tools/material-gen', icon: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 8v4M12 16h.01' },
      { label: '法条解读', path: '/ai-tools/law-interpret', icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' },
      { label: '智能组卷', path: '/ai-tools/exam-generate', icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11' }
    ]
  }

  // 内容管理菜单
  const contentMenu = {
    title: '内容管理',
    items: [
      { label: '通知管理', path: '/content/notification', icon: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9 M13.73 21a2 2 0 0 1-3.46 0' },
      { label: '新闻管理', path: '/content/article', icon: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6 M16 13H8 M16 17H8 M10 9H8' },
      { label: '评论管理', path: '/content/comment', icon: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' }
    ]
  }

  // 系统管理菜单
  const systemMenu = {
    title: '系统管理',
    items: [
      { label: '组织架构', path: '/system/organization', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75' },
      { label: '职员管理', path: '/system/employee', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z' },
      { label: '用户管理', path: '/system/users', icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2' },
      { label: '角色权限', path: '/system/roles', icon: 'M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4' },
      { label: '用户监控', path: '/monitor/user', icon: 'M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z' },
      { label: '操作日志', path: '/system/logs', icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' }
    ]
  }

  // 个人菜单
  const profileMenu = {
    title: '个人',
    items: [
      { label: '个人中心', path: '/profile', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z' }
    ]
  }

  // 政府端用户
  if (basePath === 'government') {
    return [baseMenu, governmentMenu, aiToolsMenu, contentMenu, profileMenu]
  }

  // 企业端用户
  if (basePath === 'enterprise') {
    return [baseMenu, enterpriseMenu, aiToolsMenu, contentMenu, profileMenu]
  }

  // 律所端用户
  if (basePath === 'law_firm') {
    return [baseMenu, lawFirmMenu, aiToolsMenu, contentMenu, profileMenu]
  }

  // 超级管理员和平台管理员可以看到所有
  return [baseMenu, governmentMenu, enterpriseMenu, lawFirmMenu, aiToolsMenu, contentMenu, systemMenu, profileMenu]
})

const activeMenu = computed(() => route.path)

const isActive = (path) => {
  if (path === '/dashboard/workbench') {
    return route.path === '/dashboard/workbench' || route.path === '/dashboard'
  }
  return route.path === path || route.path.startsWith(`${path}/`)
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        localStorage.clear()
        router.push('/login')
      } catch {}
      break
  }
}
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.layout-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #F8FAFC;
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
}

// ============ 顶部导航栏 ============
.glass-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background: #FFFFFF;
  border-bottom: 1px solid #E2E8F0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;

  svg {
    width: 32px;
    height: 32px;
  }

  .logo-text {
    font-size: 18px;
    font-weight: 700;
    color: #0F172A;
    letter-spacing: 1px;
  }
}

.platform-tag {
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(37, 99, 235, 0.08);
  border: 1px solid rgba(37, 99, 235, 0.2);
  font-size: 12px;
  color: #2563EB;
  font-weight: 500;
  white-space: nowrap;
}

.nav-center {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  padding: 0 24px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  text-decoration: none;
  transition: all 0.2s ease;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    background: #F1F5F9;
    color: #0F172A;
  }

  &.active {
    background: #EFF6FF;
    color: #2563EB;
    font-weight: 600;
  }
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  background: #F1F5F9;
  border: 1px solid #E2E8F0;
  font-size: 13px;
  color: #475569;
  font-weight: 500;

  svg {
    width: 14px;
    height: 14px;
  }
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: #F1F5F9;
  }
}

.user-avatar {
  background: linear-gradient(135deg, #2563EB, #1D4ED8) !important;
  color: #fff !important;
  font-weight: 600;
  font-size: 14px;
}

.chevron {
  width: 14px;
  height: 14px;
  color: #64748B;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #475569;

  svg {
    width: 15px;
    height: 15px;
  }

  &:hover {
    background: #EFF6FF;
    color: #2563EB;
  }
}

// ============ 页面主体 ============
.body-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
  background: #F8FAFC;
}

// ============ 侧边栏 ============
.glass-sidebar {
  position: sticky;
  top: 60px;
  height: calc(100vh - 60px);
  flex-shrink: 0;
  width: 240px;
  background: #FFFFFF;
  border-right: 1px solid #E2E8F0;
  overflow-y: auto;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;

  &.collapsed {
    width: 64px;
  }
}

.sidebar-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  cursor: pointer;
  color: #94A3B8;
  border-bottom: 1px solid #F1F5F9;
  transition: all 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: #F1F5F9;
    color: #2563EB;
  }
}

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
  flex: 1;
  padding: 12px 8px;

  &:not(.el-menu--collapse) {
    width: 240px;
  }

  .menu-group-title {
    padding: 16px 20px 6px;
    font-size: 11px;
    font-weight: 600;
    color: #94A3B8;
    letter-spacing: 0.5px;
    text-transform: uppercase;
  }

  :deep(.el-menu-item) {
    height: 44px;
    line-height: 44px;
    margin: 2px 8px;
    border-radius: 10px;
    color: #475569;
    font-size: 14px;
    transition: all 0.2s ease;

    svg {
      width: 16px;
      height: 16px;
      margin-right: 10px;
      flex-shrink: 0;
    }

    &:hover {
      background: #F1F5F9 !important;
      color: #0F172A;
    }

    &.is-active {
      background: #EFF6FF !important;
      color: #2563EB !important;
      font-weight: 600;
      border-right: 3px solid #2563EB;
    }
  }
}

// ============ 主内容 ============
.main-content {
  flex: 1;
  overflow-y: auto;
  min-width: 0;
  background: #F8FAFC;
}

.page-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

// ============ 页面过渡动画 ============
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

// ============ 滚动条美化 ============
.glass-sidebar::-webkit-scrollbar,
.main-content::-webkit-scrollbar {
  width: 6px;
}

.glass-sidebar::-webkit-scrollbar-track,
.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.glass-sidebar::-webkit-scrollbar-thumb,
.main-content::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 3px;

  &:hover {
    background: #94A3B8;
  }
}

// ============ 响应式 ============
@media (max-width: 1024px) {
  .nav-center {
    display: none;
  }
}

@media (max-width: 768px) {
  .glass-sidebar {
    position: fixed;
    left: -240px;
    z-index: 99;
    transition: left 0.3s ease;

    &.open {
      left: 0;
    }
  }

  .role-badge {
    display: none;
  }
}
</style>
