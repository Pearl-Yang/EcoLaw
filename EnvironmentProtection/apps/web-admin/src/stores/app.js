/**
 * 应用全局状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebarOpened = ref(true)
  const sidebarCollapsed = ref(false)
  const device = ref('desktop')
  const language = ref('zh-CN')
  const size = ref('default')
  const loading = ref(false)
  const pageLoading = ref(false)

  // 计算属性
  const isDesktop = computed(() => device.value === 'desktop')
  const isMobile = computed(() => device.value === 'mobile')

  // 切换侧边栏
  function toggleSidebar() {
    sidebarOpened.value = !sidebarOpened.value
    if (sidebarOpened.value) {
      sidebarCollapsed.value = false
    }
  }

  // 折叠侧边栏
  function collapseSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 关闭侧边栏
  function closeSidebar() {
    sidebarOpened.value = false
  }

  // 打开侧边栏
  function openSidebar() {
    sidebarOpened.value = true
  }

  // 切换设备
  function toggleDevice(newDevice) {
    device.value = newDevice
    if (newDevice === 'mobile') {
      closeSidebar()
    }
  }

  // 设置语言
  function setLanguage(newLanguage) {
    language.value = newLanguage
    localStorage.setItem('language', newLanguage)
  }

  // 设置尺寸
  function setSize(newSize) {
    size.value = newSize
    localStorage.setItem('size', newSize)
  }

  // 全局加载
  function setLoading(value) {
    loading.value = value
  }

  // 页面加载
  function setPageLoading(value) {
    pageLoading.value = value
  }

  // 初始化应用状态
  function initAppState() {
    // 从本地存储恢复状态
    const savedLanguage = localStorage.getItem('language')
    if (savedLanguage) {
      language.value = savedLanguage
    }

    const savedSize = localStorage.getItem('size')
    if (savedSize) {
      size.value = savedSize
    }

    // 检测设备类型
    const isMobileDevice = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      navigator.userAgent
    )
    if (isMobileDevice) {
      device.value = 'mobile'
      closeSidebar()
    }
  }

  return {
    // 状态
    sidebarOpened,
    sidebarCollapsed,
    device,
    language,
    size,
    loading,
    pageLoading,
    // 计算属性
    isDesktop,
    isMobile,
    // 方法
    toggleSidebar,
    collapseSidebar,
    closeSidebar,
    openSidebar,
    toggleDevice,
    setLanguage,
    setSize,
    setLoading,
    setPageLoading,
    initAppState
  }
})
