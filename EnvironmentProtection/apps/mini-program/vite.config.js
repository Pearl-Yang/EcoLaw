import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'
import { copyFileSync, mkdirSync, existsSync } from 'fs'

function copyStaticPlugin() {
  return {
    name: 'copy-static',
    closeBundle() {
      const srcTabbar = path.resolve(__dirname, 'static', 'tabbar')
      if (!existsSync(srcTabbar)) return
      const files = [
        'home.png', 'home-active.png', 'learn.png', 'learn-active.png',
        'task.png', 'task-active.png', 'chat.png', 'chat-active.png',
        'profile.png', 'profile-active.png'
      ]
      const destRoots = [
        path.resolve(__dirname, 'dist/dev/mp-weixin/static'),
        path.resolve(__dirname, 'dist/build/mp-weixin/static')
      ]
      for (const root of destRoots) {
        const destTabbar = path.join(root, 'tabbar')
        if (!existsSync(destTabbar)) mkdirSync(destTabbar, { recursive: true })
        for (const f of files) {
          try {
            copyFileSync(path.join(srcTabbar, f), path.join(destTabbar, f))
          } catch {
            /* 源文件缺失时跳过 */
          }
        }
      }
    }
  }
}

export default defineConfig({
  plugins: [uni(), copyStaticPlugin()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ai-api': {
        target: 'http://localhost:8000',
        changeOrigin: true
      }
    }
  }
})
