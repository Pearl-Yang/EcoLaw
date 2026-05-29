import { createSSRApp } from 'vue'
import App from './App.vue'
import { useUserStore } from './src/store/user.js'

export function createApp() {
  const app = createSSRApp(App)
  app.config.globalProperties.$userStore = useUserStore
  return { app }
}
