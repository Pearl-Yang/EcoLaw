import { useUserStore } from './user.js'

export function setupStore(app) {
  // 无需 pinia，store 已自动初始化
  app.config.globalProperties.$userStore = useUserStore
}

export { useUserStore }
