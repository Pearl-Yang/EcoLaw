/**
 * 权限状态管理 - 动态路由
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRoleList } from '@/api/user.js'

export const usePermissionStore = defineStore('permission', () => {
  // 状态
  const routes = ref([])
  const addRoutes = ref([])
  const menuList = ref([])
  const isMenuLoaded = ref(false)

  // 角色列表
  const roleList = ref([])

  // 根据角色过滤路由
  function filterRoutes(routes, roles) {
    return routes.filter(route => {
      if (route.meta?.roles) {
        return route.meta.roles.some(role => roles.includes(role))
      }
      return true
    })
  }

  // 生成菜单
  function generateMenu(routes) {
    const menus = []

    routes.forEach(route => {
      if (route.meta?.hidden) return

      const menu = {
        id: route.name,
        name: route.name,
        path: route.path,
        component: route.component,
        meta: route.meta,
        children: []
      }

      if (route.children && route.children.length > 0) {
        menu.children = generateMenu(route.children)
      }

      menus.push(menu)
    })

    return menus
  }

  // 设置路由
  function setRoutes(newRoutes) {
    routes.value = [...routes.value, ...newRoutes]
    addRoutes.value = newRoutes
  }

  // 设置菜单
  function setMenu(newMenu) {
    menuList.value = newMenu
  }

  // 重置权限状态
  function resetPermission() {
    routes.value = []
    addRoutes.value = []
    menuList.value = []
    isMenuLoaded.value = false
  }

  // 加载角色列表
  async function loadRoles() {
    try {
      const res = await getRoleList()
      roleList.value = res
    } catch {
      // 使用默认角色
      roleList.value = [
        { id: '1', name: '超级管理员', code: 'super_admin' },
        { id: '2', name: '政府监管员', code: 'government' },
        { id: '3', name: '企业管理员', code: 'enterprise' },
        { id: '4', name: '律所管理员', code: 'law_firm' }
      ]
    }
  }

  // 获取角色名称
  function getRoleName(code) {
    const role = roleList.value.find(r => r.code === code)
    return role?.name || code
  }

  return {
    // 状态
    routes,
    addRoutes,
    menuList,
    isMenuLoaded,
    roleList,
    // 方法
    filterRoutes,
    generateMenu,
    setRoutes,
    setMenu,
    resetPermission,
    loadRoles,
    getRoleName
  }
})
