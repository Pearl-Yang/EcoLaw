/**
 * 用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo as getUserInfoApi } from '@/api/auth.js'
import { setToken, removeToken, setUserInfo } from '@/utils/auth.js'
import router from '@/router/index.js'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref('')
  const userInfo = ref(null)
  const roles = ref([])
  const permissions = ref([])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')
  const userId = computed(() => userInfo.value?.id || '')
  const userRole = computed(() => userInfo.value?.role || '')
  const isSuperAdmin = computed(() => userRole.value === 'super_admin')
  const isGovernment = computed(() => userRole.value === 'government')
  const isEnterprise = computed(() => userRole.value === 'enterprise')
  const isLawFirm = computed(() => userRole.value === 'law_firm')

  // 登录
  async function login(loginForm) {
    try {
      const res = await loginApi(loginForm)
      token.value = res.token
      userInfo.value = res.userInfo
      roles.value = [res.userInfo.role]
      permissions.value = res.userInfo.permissions || []
      setToken(res.token)
      setUserInfo(res.userInfo)
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取用户信息
  async function getUserInfo() {
    try {
      const res = await getUserInfoApi()
      userInfo.value = res
      roles.value = [res.role]
      permissions.value = res.permissions || []
      setUserInfo(res)
      return res
    } catch (error) {
      throw error
    }
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 忽略登出API错误
    } finally {
      resetState()
      router.push('/login')
    }
  }

  // 重置状态
  function resetState() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    removeToken()
  }

  // 检查权限
  function hasPermission(permission) {
    if (!permission || permission.length === 0) return true
    if (isSuperAdmin.value) return true

    const permissionList = Array.isArray(permission) ? permission : [permission]
    return permissionList.some(p => permissions.value.includes(p))
  }

  // 检查角色
  function hasRole(role) {
    if (!role || role.length === 0) return true
    if (isSuperAdmin.value) return true

    const roleList = Array.isArray(role) ? role : [role]
    return roleList.some(r => roles.value.includes(r))
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    permissions,
    // 计算属性
    isLoggedIn,
    userName,
    userId,
    userRole,
    isSuperAdmin,
    isGovernment,
    isEnterprise,
    isLawFirm,
    // 方法
    login,
    getUserInfo,
    logout,
    resetState,
    hasPermission,
    hasRole
  }
})
