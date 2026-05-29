// 权限控制
import { getToken } from '@/utils/auth.js'

const whiteList = ['/login', '/register', '/forget', '/welcome']

export default function permission(router, hasToken) {
  router.beforeEach(async (to, from, next) => {
    const hasAuth = hasToken()
    const isWhite = whiteList.includes(to.path)

    if (hasAuth) {
      if (to.path === '/login' || to.path === '/welcome') {
        next({ path: '/' })
      } else {
        next()
      }
    } else {
      if (isWhite) {
        next()
      } else if (to.path === '/') {
        next('/welcome')
      } else {
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      }
    }
  })

  router.afterEach(() => {
  })
}
