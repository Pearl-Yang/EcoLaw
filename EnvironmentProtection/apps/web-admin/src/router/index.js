import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getToken, getUserRole } from '@/utils/auth'

const routes = [
  {
    path: '/welcome',
    name: 'Welcome',
    component: () => import('@/views/landing/Index.vue'),
    meta: { title: '绿法通', hidden: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', hidden: true }
  },
  {
    path: '/forget',
    name: 'Forget',
    component: () => import('@/views/auth/Forget.vue'),
    meta: { title: '忘记密码', hidden: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard/workbench',
    children: [
      {
        path: 'dashboard',
        redirect: '/dashboard/workbench'
      },
      {
        path: 'dashboard/workbench',
        name: 'DashboardWorkbench',
        component: () => import('@/views/dashboard/Workbench.vue'),
        meta: { title: '工作台', icon: 'Odometer', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'dashboard/data-analysis',
        name: 'DashboardDataAnalysis',
        component: () => import('@/views/dashboard/DataAnalysis.vue'),
        meta: { title: '数据分析', icon: 'DataAnalysis', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'big-screen',
        name: 'BigScreen',
        component: () => import('@/views/big-screen/Index.vue'),
        meta: { title: '数据驾驶舱', icon: 'Odometer', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'monitor/user',
        name: 'UserMonitor',
        component: () => import('@/views/monitor/UserMonitor.vue'),
        meta: { title: '用户监控', icon: 'Monitor', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'government/tasks',
        name: 'GovernmentTasks',
        component: () => import('@/views/government/Tasks.vue'),
        meta: { title: '任务管理', icon: 'Coordinate', allowedRoles: ['government', 'platform', 'super_admin'] }
      },
      {
        path: 'government/publicity',
        name: 'GovernmentPublicity',
        component: () => import('@/views/government/Publicity.vue'),
        meta: { title: '普法宣传', icon: 'Microphone', allowedRoles: ['government', 'platform', 'super_admin'] }
      },
      {
        path: 'government/enterprises',
        name: 'GovernmentEnterprises',
        component: () => import('@/views/government/Enterprises.vue'),
        meta: { title: '企业管理', icon: 'OfficeBuilding', allowedRoles: ['government', 'platform', 'super_admin'] }
      },
      {
        path: 'government/analytics',
        name: 'GovernmentAnalytics',
        component: () => import('@/views/government/Analytics.vue'),
        meta: { title: '数据分析', icon: 'DataAnalysis', allowedRoles: ['government', 'platform', 'super_admin'] }
      },
      {
        path: 'government/reports',
        name: 'GovernmentReports',
        component: () => import('@/views/government/Reports.vue'),
        meta: { title: '报表导出', icon: 'Document', allowedRoles: ['government', 'platform', 'super_admin'] }
      },
      {
        path: 'enterprise/training',
        name: 'EnterpriseTraining',
        component: () => import('@/views/enterprise/Training.vue'),
        meta: { title: '培训管理', icon: 'Reading', allowedRoles: ['enterprise', 'platform', 'super_admin'] }
      },
      {
        path: 'enterprise/employees',
        name: 'EnterpriseEmployees',
        component: () => import('@/views/enterprise/Employees.vue'),
        meta: { title: '员工管理', icon: 'User', allowedRoles: ['enterprise', 'platform', 'super_admin'] }
      },
      {
        path: 'enterprise/compliance',
        name: 'EnterpriseCompliance',
        component: () => import('@/views/enterprise/Compliance.vue'),
        meta: { title: '合规台账', icon: 'DocumentChecked', allowedRoles: ['enterprise', 'platform', 'super_admin'] }
      },
      {
        path: 'law-firm/cases',
        name: 'LawFirmCases',
        component: () => import('@/views/law-firm/Cases.vue'),
        meta: { title: '案例管理', icon: 'Suitcase', allowedRoles: ['law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'law-firm/clients',
        name: 'LawFirmClients',
        component: () => import('@/views/law-firm/Clients.vue'),
        meta: { title: '客户管理', icon: 'OfficeBuilding', allowedRoles: ['law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'law-firm/consult',
        name: 'LawFirmConsult',
        component: () => import('@/views/law-firm/Consult.vue'),
        meta: { title: '咨询对接', icon: 'ChatDotRound', allowedRoles: ['law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'ai-tools/material-gen',
        name: 'AiMaterialGen',
        component: () => import('@/views/ai-tools/MaterialGen.vue'),
        meta: { title: '素材生成', icon: 'MagicStick', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'], isCommon: true }
      },
      {
        path: 'ai-tools/law-interpret',
        name: 'AiLawInterpret',
        component: () => import('@/views/ai-tools/LawInterpret.vue'),
        meta: { title: '法条解读', icon: 'ScaleToOriginal', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'], isCommon: true }
      },
      {
        path: 'ai/smart-search',
        name: 'SmartSearch',
        component: () => import('@/views/ai/SmartSearch.vue'),
        meta: { title: '智能检索', icon: 'Search', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'], isCommon: true }
      },
      {
        path: 'ai-tools/exam-generate',
        name: 'AiExamGenerate',
        component: () => import('@/views/ai-tools/ExamGenerate.vue'),
        meta: { title: '智能组卷', icon: 'DocumentCopy', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'], isCommon: true }
      },
      {
        path: 'ai-tools/conversation',
        name: 'AIConversation',
        component: () => import('@/views/ai/Conversation.vue'),
        meta: { title: '对话管理', icon: 'ChatDotSquare', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'system/organization',
        name: 'SystemOrganization',
        component: () => import('@/views/system/Organization.vue'),
        meta: { title: '组织架构', icon: 'Grid', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'system/employee',
        name: 'SystemEmployee',
        component: () => import('@/views/system/Employee.vue'),
        meta: { title: '职员管理', icon: 'UserFilled', allowedRoles: ['platform', 'super_admin', 'government', 'enterprise'] }
      },
      {
        path: 'system/task-dispatch',
        name: 'SystemTaskDispatch',
        component: () => import('@/views/system/TaskDispatch.vue'),
        meta: { title: '任务下发', icon: 'Promotion', allowedRoles: ['platform', 'super_admin', 'government', 'enterprise'] }
      },
      {
        path: 'system/users',
        name: 'SystemUsers',
        component: () => import('@/views/system/Users.vue'),
        meta: { title: '用户管理', icon: 'User', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'system/roles',
        name: 'SystemRoles',
        component: () => import('@/views/system/Roles.vue'),
        meta: { title: '角色权限', icon: 'Key', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'system/logs',
        name: 'SystemLogs',
        component: () => import('@/views/system/Logs.vue'),
        meta: { title: '操作日志', icon: 'Clock', allowedRoles: ['platform', 'super_admin'] }
      },
      {
        path: 'content/notification',
        name: 'ContentNotification',
        component: () => import('@/views/content/Notification.vue'),
        meta: { title: '通知管理', icon: 'Bell', allowedRoles: ['platform', 'super_admin', 'government', 'enterprise'] }
      },
      {
        path: 'content/article',
        name: 'ContentArticle',
        component: () => import('@/views/content/Article.vue'),
        meta: { title: '新闻管理', icon: 'Document', allowedRoles: ['platform', 'super_admin', 'government', 'enterprise'] }
      },
      {
        path: 'content/comment',
        name: 'ContentComment',
        component: () => import('@/views/content/Comment.vue'),
        meta: { title: '评论管理', icon: 'ChatDotSquare', allowedRoles: ['platform', 'super_admin', 'government', 'enterprise'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Index.vue'),
        meta: { title: '个人中心', icon: 'User', allowedRoles: ['government', 'enterprise', 'law_firm', 'platform', 'super_admin'] }
      },
      {
        path: 'test/law-api',
        name: 'LawApiTest',
        component: () => import('@/views/test/LawApiTest.vue'),
        meta: { title: '法规API测试', icon: 'Connection', allowedRoles: ['platform', 'super_admin'], hidden: false }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 路由守卫 - 阻止跨端访问
router.beforeEach((to, from, next) => {
  // 公开路由直接放行
  if (to.meta.hidden || to.path === '/login' || to.path === '/register' || to.path === '/forget') {
    return next()
  }

  // 检查是否已登录
  if (!getToken()) {
    return next('/login')
  }

  // 检查角色权限
  const userRole = getUserRole()
  const allowedRoles = to.meta.allowedRoles

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    ElMessage.error('您没有权限访问该页面')
    return next('/dashboard/workbench')
  }

  next()
})

export default router
