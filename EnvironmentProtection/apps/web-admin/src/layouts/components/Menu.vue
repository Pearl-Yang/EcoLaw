<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="props.collapse"
    background-color="transparent"
    text-color="#475569"
    active-text-color="#2563EB"
    :router="true"
    class="sidebar-menu"
    :popper-class="'sidebar-popper'"
  >
    <template v-for="item in menus" :key="item.path">
      <el-sub-menu v-if="item.children?.length" :index="item.path">
        <template #title>
          <div class="menu-item-wrapper">
            <span class="menu-icon">{{ item.icon }}</span>
            <span class="menu-title">{{ item.title }}</span>
          </div>
        </template>
        <el-menu-item
          v-for="child in item.children"
          :key="child.path"
          :index="child.path"
        >
          <span class="menu-icon-small">{{ child.icon || '📄' }}</span>
          {{ child.title }}
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-else :index="item.path">
        <span class="menu-icon">{{ item.icon }}</span>
        <span class="menu-title">{{ item.title }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

defineOptions({ name: 'SidebarMenu' })

const props = defineProps({
  collapse: Boolean
})

const route = useRoute()
const userStore = useUserStore()
const role = computed(() => userStore.userRole)

// 根据角色过滤菜单
const menus = computed(() => {
  const allMenus = [
    {
      path: '/dashboard',
      title: '仪表盘',
      icon: '📊',
      children: [
        { path: '/dashboard/workbench', title: '工作台', icon: '🏠' },
        { path: '/dashboard/data-analysis', title: '数据分析', icon: '📈' }
      ]
    },
    {
      path: '/government',
      title: '政府端',
      icon: '🏛️',
      children: [
        { path: '/government/tasks', title: '任务管理', icon: '📋' },
        { path: '/government/publicity', title: '普法宣传', icon: '📢' },
        { path: '/government/enterprises', title: '企业管理', icon: '🏢' },
        { path: '/government/analytics', title: '数据分析', icon: '📊' },
        { path: '/government/reports', title: '报表导出', icon: '📥' },
        { path: '/system/employee', title: '职员管理', icon: '👤' }
      ]
    },
    {
      path: '/enterprise',
      title: '企业端',
      icon: '🏢',
      children: [
        { path: '/enterprise/training', title: '培训管理', icon: '📚' },
        { path: '/enterprise/employees', title: '员工管理', icon: '👥' },
        { path: '/system/employee', title: '职员档案', icon: '📇' },
        { path: '/enterprise/compliance', title: '合规台账', icon: '✅' }
      ]
    },
    {
      path: '/law-firm',
      title: '律所端',
      icon: '⚖️',
      children: [
        { path: '/law-firm/cases', title: '案例管理', icon: '📁' },
        { path: '/law-firm/clients', title: '客户管理', icon: '👤' },
        { path: '/law-firm/consult', title: '咨询对接', icon: '💬' }
      ]
    },
    {
      path: '/ai-tools',
      title: 'AI工具库',
      icon: '🤖',
      children: [
        { path: '/ai/smart-search', title: '智能检索', icon: '🔍' },
        { path: '/ai-tools/material-gen', title: '素材生成', icon: '🎨' },
        { path: '/ai-tools/law-interpret', title: '法条解读', icon: '📜' },
        { path: '/ai-tools/exam-generate', title: '智能组卷', icon: '📝' }
      ]
    },
    {
      path: '/content',
      title: '内容管理',
      icon: '📰',
      children: [
        { path: '/content/notification', title: '通知管理', icon: '🔔' },
        { path: '/content/article', title: '新闻管理', icon: '📝' },
        { path: '/content/comment', title: '评论管理', icon: '💬' }
      ]
    },
    {
      path: '/system',
      title: '系统管理',
      icon: '⚙️',
      children: [
        { path: '/system/organization', title: '组织架构', icon: '🏗️' },
        { path: '/system/employee', title: '职员管理', icon: '👤' },
        { path: '/system/users', title: '用户管理', icon: '👥' },
        { path: '/system/roles', title: '角色权限', icon: '🔐' },
        { path: '/system/logs', title: '操作日志', icon: '📋' }
      ]
    }
  ]

  // 如果用户未登录，只显示工作台
  if (!role.value) {
    return allMenus.filter(m => m.path === '/dashboard')
  }

  // 超级管理员可见所有菜单
  if (role.value === 'super_admin') {
    return allMenus
  }

  // 政府监管员
  if (role.value === 'government') {
    return allMenus.filter(m => ['/dashboard', '/government', '/ai-tools', '/content', '/system'].includes(m.path))
  }

  // 企业管理员
  if (role.value === 'enterprise') {
    return allMenus.filter(m => ['/dashboard', '/enterprise', '/ai-tools', '/content', '/system'].includes(m.path))
  }

  // 律所管理员
  if (role.value === 'law_firm') {
    return allMenus.filter(m => ['/dashboard', '/law-firm', '/ai-tools', '/content'].includes(m.path))
  }

  // 其他登录用户（如平台管理员）
  if (role.value === 'platform') {
    return allMenus.filter(m => ['/dashboard', '/ai-tools', '/content', '/system'].includes(m.path))
  }

  // 默认显示所有菜单
  return allMenus
})

const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar-menu {
  border-right: none;
  height: calc(100vh - 64px);
  overflow-y: auto;
  padding: 12px 8px;

  &:not(.el-menu--collapse) {
    width: 224px;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 48px;
    line-height: 48px;
    border-radius: 10px;
    margin: 2px 0;
    transition: all 0.2s ease;
    color: #475569;

    &:hover {
      background: #F1F5F9 !important;
      color: #0F172A;
    }
  }

  :deep(.el-menu-item.is-active) {
    background: #EFF6FF !important;
    border-radius: 10px;
    color: #2563EB !important;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(37, 99, 235, 0.12);

    .menu-icon,
    .menu-title {
      color: #2563EB;
    }
  }

  :deep(.el-sub-menu .el-menu-item) {
    height: 44px;
    line-height: 44px;
    padding-left: 52px !important;
    font-size: 14px;

    &:hover {
      background: #F1F5F9 !important;
    }
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #2563EB !important;
  }

  :deep(.el-sub-menu__icon-arrow) {
    color: #94A3B8;
  }
}

.menu-item-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.menu-icon-small {
  font-size: 14px;
  margin-right: 10px;
}

.menu-title {
  font-size: 14px;
  font-weight: 500;
}
</style>
