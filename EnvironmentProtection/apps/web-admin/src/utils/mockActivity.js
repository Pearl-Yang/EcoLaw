/**
 * 虚拟用户操作活动数据模块
 * 用于实时监控大屏展示虚拟用户操作数据
 */

// 虚拟用户列表（复用 mockUser.js 的数据结构）
export const mockUsers = [
  { id: '1', username: 'admin', nickname: '超级管理员', role: 'super_admin', organizationName: '平台总部' },
  { id: '2', username: 'wang_jian', nickname: '王建国', role: 'government', organizationName: '省生态环境厅' },
  { id: '3', username: 'li_ming', nickname: '李明华', role: 'enterprise', organizationName: '绿科环保科技有限公司' },
  { id: '4', username: 'zhang_law', nickname: '张正信', role: 'law_firm', organizationName: '正信律师事务所' },
  { id: '5', username: 'chen_user', nickname: '陈晓燕', role: 'user', organizationName: '个人用户' },
  { id: '6', username: 'zhang_wei', nickname: '张伟', role: 'government', organizationName: '市生态环境局' },
  { id: '7', username: 'li_na', nickname: '李娜', role: 'enterprise', organizationName: '绿色包装有限公司' },
  { id: '8', username: 'liu_law', nickname: '刘律师', role: 'law_firm', organizationName: '明德律师事务所' },
  { id: '9', username: 'wang_min', nickname: '王敏', role: 'user', organizationName: '个人用户' },
  { id: '10', username: 'zhao_admin', nickname: '赵管理', role: 'enterprise', organizationName: 'ECO环保科技有限公司' }
]

// 操作类型
export const actionTypes = {
  LOGIN: { code: 'login', label: '登录', icon: '🔐', color: '#3B82F6' },
  REGISTER: { code: 'register', label: '注册', icon: '📝', color: '#10B981' },
  VIEW_PAGE: { code: 'view', label: '浏览页面', icon: '👁', color: '#8B5CF6' },
  SEARCH: { code: 'search', label: '搜索', icon: '🔍', color: '#F59E0B' },
  SUBMIT_FORM: { code: 'submit', label: '提交表单', icon: '📤', color: '#EC4899' },
  UPLOAD_FILE: { code: 'upload', label: '上传文件', icon: '📁', color: '#06B6D4' },
  DOWNLOAD: { code: 'download', label: '下载', icon: '📥', color: '#6366F1' },
  API_CALL: { code: 'api', label: 'API调用', icon: '⚡', color: '#EF4444' },
  TASK_COMPLETE: { code: 'task', label: '完成任务', icon: '✅', color: '#22C55E' },
  AI_QUERY: { code: 'ai', label: 'AI查询', icon: '🤖', color: '#A855F7' }
}

// 页面路径
const pages = [
  { path: '/dashboard/workbench', name: '工作台' },
  { path: '/government/tasks', name: '任务管理' },
  { path: '/government/publicity', name: '普法宣传' },
  { path: '/government/enterprises', name: '企业管理' },
  { path: '/enterprise/training', name: '培训管理' },
  { path: '/enterprise/compliance', name: '合规台账' },
  { path: '/law-firm/cases', name: '案例管理' },
  { path: '/law-firm/consult', name: '咨询对接' },
  { path: '/system/users', name: '用户管理' },
  { path: '/system/roles', name: '角色权限' },
  { path: '/ai-tools/material-gen', name: '素材生成' },
  { path: '/ai-tools/law-interpret', name: '法条解读' },
  { path: '/profile', name: '个人中心' },
  { path: '/big-screen', name: '数据驾驶舱' }
]

// 搜索关键词
const searchKeywords = [
  '白色污染治理', '企业合规', '农膜回收', '塑料制品禁限', '环保法规',
  '普法宣传', '任务下发', '合规报告', '案例查询', '培训课程'
]

// AI 查询内容
const aiQueries = [
  '白色污染治理最新政策解读',
  '企业环保合规检查清单',
  '塑料制品使用规范',
  '农膜回收补贴政策',
  '环境违法处罚标准'
]

// 生成随机操作记录
export function generateActivity(type = null) {
  const user = mockUsers[Math.floor(Math.random() * mockUsers.length)]
  const action = type || Object.values(actionTypes)[Math.floor(Math.random() * Object.values(actionTypes).length)]
  const page = pages[Math.floor(Math.random() * pages.length)]
  
  const now = new Date()
  const time = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
  
  let detail = ''
  switch (action.code) {
    case 'login':
      detail = `从 ${['PC浏览器', '手机浏览器', '微信小程序'][Math.floor(Math.random() * 3)]} 登录`
      break
    case 'register':
      detail = `新用户注册成为${user.organizationName || '个人用户'}`
      break
    case 'view':
      detail = `浏览了「${page.name}」页面`
      break
    case 'search':
      detail = `搜索了「${searchKeywords[Math.floor(Math.random() * searchKeywords.length)]}」`
      break
    case 'submit':
      detail = `提交了 ${['任务报告', '合规申请', '培训报名'][Math.floor(Math.random() * 3)]}`
      break
    case 'upload':
      detail = `上传了 ${['文档', '图片', 'Excel表格'][Math.floor(Math.random() * 3)]}`
      break
    case 'download':
      detail = `下载了合规报告`
      break
    case 'api':
      detail = `调用了数据接口`
      break
    case 'task':
      detail = `完成了任务节点`
      break
    case 'ai':
      detail = `查询了「${aiQueries[Math.floor(Math.random() * aiQueries.length)]}」`
      break
    default:
      detail = '执行了操作'
  }
  
  return {
    id: Date.now() + Math.random().toString(36).substr(2, 9),
    time,
    userId: user.id,
    username: user.username,
    nickname: user.nickname,
    role: user.role,
    organizationName: user.organizationName,
    actionType: action.code,
    actionLabel: action.label,
    actionIcon: action.icon,
    actionColor: action.color,
    page: page.name,
    pagePath: page.path,
    detail,
    ip: `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`,
    device: ['Windows', 'macOS', 'iOS', 'Android'][Math.floor(Math.random() * 4)]
  }
}

// 生成初始操作记录列表
export function generateInitialActivities(count = 50) {
  const activities = []
  const now = Date.now()
  
  for (let i = 0; i < count; i++) {
    const activity = generateActivity()
    // 随机分配过去的时间
    activity.time = new Date(now - Math.random() * 3600000).toLocaleTimeString('zh-CN', { hour12: false })
    activity.id = now - (count - i) * 1000
    activities.push(activity)
  }
  
  // 按时间倒序排列
  return activities.sort((a, b) => b.id - a.id)
}

// 用户统计数据
export function getUserStats() {
  // 基础用户数 - 确保不低于2060
  const baseUsers = 2060 + Math.floor(Math.random() * 200)
  const totalUsers = baseUsers
  
  // 在线用户 - 确保不低于500
  const onlineUsers = Math.max(500, Math.floor(totalUsers * 0.28) + Math.floor(Math.random() * 50))
  const todayLogins = Math.floor(totalUsers * 0.6) + Math.floor(Math.random() * 80)
  const todayRegisters = Math.floor(totalUsers * 0.03) + Math.floor(Math.random() * 20)
  
  // 角色分布 - 基于总数按比例分配，确保总和等于 totalUsers
  const roleDistribution = calculateRoleDistribution(totalUsers)
  
  // 操作统计
  const actionStats = {}
  Object.values(actionTypes).forEach(action => {
    actionStats[action.code] = {
      label: action.label,
      icon: action.icon,
      color: action.color,
      count: Math.floor(Math.random() * 500) + 100,
      trend: (Math.random() * 40 - 20).toFixed(1)
    }
  })
  
  // 今日活跃时段分布
  const hourlyStats = Array.from({ length: 24 }, (_, i) => ({
    hour: i,
    label: `${String(i).padStart(2, '0')}:00`,
    logins: Math.floor(Math.random() * 200) + 50,
    pageViews: Math.floor(Math.random() * 1000) + 200,
    apiCalls: Math.floor(Math.random() * 500) + 100
  }))
  
  // 用户增长趋势数据（线性图用）- 累计用户持续增长
  const baseTotal = totalUsers - 200
  const userTrendStats = Array.from({ length: 30 }, (_, i) => {
    // 累计用户：每天都比前一天多，最终达到 totalUsers
    const progress = i / 29  // 0 到 1
    const total = Math.floor(baseTotal + (200 * progress) + Math.random() * 10)
    // 活跃用户：不低于总数的40%
    const activeUsers = Math.floor(Math.max(total * 0.40, total * 0.45 + (Math.random() - 0.5) * 20))
    return {
      day: i + 1,
      label: `${i + 1}日`,
      totalUsers: total,
      newUsers: Math.floor(Math.random() * 15) + 3,
      activeUsers: activeUsers
    }
  })
  
  // 地区分布 - 用于中国地图（省级名称映射到ECharts地图名称）
  const regionStats = [
    { name: '北京', value: Math.floor(totalUsers * 0.12) + Math.floor(Math.random() * 30), itemStyle: { color: '#3B82F6' } },
    { name: '上海', value: Math.floor(totalUsers * 0.10) + Math.floor(Math.random() * 25), itemStyle: { color: '#10B981' } },
    { name: '广东', value: Math.floor(totalUsers * 0.15) + Math.floor(Math.random() * 35), itemStyle: { color: '#F59E0B' } },
    { name: '浙江', value: Math.floor(totalUsers * 0.08) + Math.floor(Math.random() * 20), itemStyle: { color: '#8B5CF6' } },
    { name: '江苏', value: Math.floor(totalUsers * 0.07) + Math.floor(Math.random() * 18), itemStyle: { color: '#EC4899' } },
    { name: '四川', value: Math.floor(totalUsers * 0.06) + Math.floor(Math.random() * 15), itemStyle: { color: '#06B6D4' } },
    { name: '山东', value: Math.floor(totalUsers * 0.06) + Math.floor(Math.random() * 15), itemStyle: { color: '#EF4444' } },
    { name: '河南', value: Math.floor(totalUsers * 0.05) + Math.floor(Math.random() * 12), itemStyle: { color: '#14B8A6' } },
    { name: '湖北', value: Math.floor(totalUsers * 0.04) + Math.floor(Math.random() * 10), itemStyle: { color: '#8B5CF6' } },
    { name: '湖南', value: Math.floor(totalUsers * 0.04) + Math.floor(Math.random() * 10), itemStyle: { color: '#F97316' } },
    { name: '河北', value: Math.floor(totalUsers * 0.04) + Math.floor(Math.random() * 10), itemStyle: { color: '#6366F1' } },
    { name: '福建', value: Math.floor(totalUsers * 0.03) + Math.floor(Math.random() * 8), itemStyle: { color: '#22C55E' } },
    { name: '安徽', value: Math.floor(totalUsers * 0.03) + Math.floor(Math.random() * 8), itemStyle: { color: '#A855F7' } },
    { name: '辽宁', value: Math.floor(totalUsers * 0.03) + Math.floor(Math.random() * 8), itemStyle: { color: '#EAB308' } }
  ]
  
  return {
    totalUsers,
    onlineUsers,
    todayLogins,
    todayRegisters,
    totalLogins: Math.floor(Math.random() * 10000) + 5000,
    totalPageViews: Math.floor(Math.random() * 50000) + 20000,
    totalApiCalls: Math.floor(Math.random() * 20000) + 10000,
    roleDistribution,
    actionStats,
    hourlyStats,
    userTrendStats,
    regionStats,
    // 增长趋势
    userGrowth: {
      week: (Math.random() * 20 + 5).toFixed(1),
      month: (Math.random() * 50 + 15).toFixed(1)
    }
  }
}

// 计算角色分布，确保普通用户最多，超级管理员最少
function calculateRoleDistribution(totalUsers) {
  // 普通用户占多数（约60%）
  const userCount = Math.floor(totalUsers * 0.60)
  const remaining = totalUsers - userCount
  
  // 其他角色按比例分配：企业>政府>律所>超级管理员
  // 企业 25%，政府 8%，律所 5%，超级管理员 2%
  const distribution = {
    user: userCount,
    enterprise: Math.floor(remaining * 0.50),      // 企业最多
    government: Math.floor(remaining * 0.28),     // 政府次之
    law_firm: Math.floor(remaining * 0.17),      // 律所第三
    super_admin: remaining - Math.floor(remaining * 0.50) - Math.floor(remaining * 0.28) - Math.floor(remaining * 0.17) // 超级管理员最少
  }
  
  // 确保总和等于 totalUsers
  let sum = Object.values(distribution).reduce((a, b) => a + b, 0)
  distribution.user += (totalUsers - sum) // 补偿舍入误差
  
  return distribution
}

// 角色名称映射
export function getRoleName(role) {
  const names = {
    super_admin: '超级管理员',
    government: '政府监管员',
    enterprise: '企业管理员',
    law_firm: '律所管理员',
    user: '普通用户'
  }
  return names[role] || '未知'
}

// 获取角色颜色
export function getRoleColor(role) {
  const colors = {
    super_admin: '#E74C3C',
    government: '#3498DB',
    enterprise: '#27AE60',
    law_firm: '#9B59B6',
    user: '#95A5A6'
  }
  return colors[role] || '#95A5A6'
}
