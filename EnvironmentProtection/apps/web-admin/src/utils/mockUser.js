/**
 * 虚拟用户数据模块
 * 用于本地开发/演示时显示虚拟用户数据
 */

// 虚拟用户列表
export const mockUsers = [
  {
    id: '1',
    username: 'admin',
    nickname: '超级管理员',
    role: 'super_admin',
    phone: '138****8001',
    email: 'admin@lvfat.cn',
    organizationName: '平台总部',
    organizationId: 'org-001',
    status: 'active',
    createTime: '2026-01-15 09:30:00',
    lastLoginTime: '2026-04-19 08:30:00',
    avatar: null,
    permissions: ['*']
  },
  {
    id: '2',
    username: 'wang_jian',
    nickname: '王建国',
    role: 'government',
    phone: '138****8002',
    email: 'wangjian@gov.cn',
    organizationName: '省生态环境厅',
    organizationId: 'org-002',
    status: 'active',
    createTime: '2026-02-01 10:00:00',
    lastLoginTime: '2026-04-18 16:20:00',
    avatar: null,
    permissions: ['task:*', 'publicity:*', 'enterprise:read', 'report:read']
  },
  {
    id: '3',
    username: 'li_ming',
    nickname: '李明华',
    role: 'enterprise',
    phone: '138****8003',
    email: 'liminghua@greentech.cn',
    organizationName: '绿科环保科技有限公司',
    organizationId: 'org-003',
    status: 'active',
    createTime: '2026-02-10 14:30:00',
    lastLoginTime: '2026-04-19 09:45:00',
    avatar: null,
    permissions: ['training:*', 'compliance:*', 'employee:read']
  },
  {
    id: '4',
    username: 'zhang_law',
    nickname: '张正信',
    role: 'law_firm',
    phone: '138****8004',
    email: 'zhangzhengxin@zhengxinlaw.cn',
    organizationName: '正信律师事务所',
    organizationId: 'org-004',
    status: 'active',
    createTime: '2026-02-15 08:45:00',
    lastLoginTime: '2026-04-17 11:30:00',
    avatar: null,
    permissions: ['case:*', 'client:*', 'consult:*']
  },
  {
    id: '5',
    username: 'chen_user',
    nickname: '陈晓燕',
    role: 'user',
    phone: '138****8005',
    email: 'chenxiaoyan@example.com',
    organizationName: '个人用户',
    organizationId: null,
    status: 'active',
    createTime: '2026-03-01 09:00:00',
    lastLoginTime: '2026-04-19 10:15:00',
    avatar: null,
    permissions: ['profile:*']
  },
  {
    id: '6',
    username: 'zhang_wei',
    nickname: '张伟',
    role: 'government',
    phone: '139****9001',
    email: 'zhangwei@city-env.cn',
    organizationName: '市生态环境局',
    organizationId: 'org-005',
    status: 'active',
    createTime: '2026-03-05 11:10:00',
    lastLoginTime: '2026-04-16 14:00:00',
    avatar: null,
    permissions: ['task:read', 'publicity:read', 'enterprise:read']
  },
  {
    id: '7',
    username: 'li_na',
    nickname: '李娜',
    role: 'enterprise',
    phone: '139****9002',
    email: 'lina@greenpkg.cn',
    organizationName: '绿色包装有限公司',
    organizationId: 'org-006',
    status: 'disabled',
    createTime: '2026-03-08 15:30:00',
    lastLoginTime: '2026-04-10 09:00:00',
    avatar: null,
    permissions: ['training:read']
  },
  {
    id: '8',
    username: 'liu_law',
    nickname: '刘律师',
    role: 'law_firm',
    phone: '139****9003',
    email: 'liulaw@mingdelaw.cn',
    organizationName: '明德律师事务所',
    organizationId: 'org-007',
    status: 'active',
    createTime: '2026-03-10 10:00:00',
    lastLoginTime: '2026-04-18 17:30:00',
    avatar: null,
    permissions: ['case:read', 'consult:read']
  },
  {
    id: '9',
    username: 'wang_min',
    nickname: '王敏',
    role: 'user',
    phone: '139****9004',
    email: 'wangmin@example.com',
    organizationName: '个人用户',
    organizationId: null,
    status: 'active',
    createTime: '2026-03-12 14:20:00',
    lastLoginTime: '2026-04-19 07:50:00',
    avatar: null,
    permissions: ['profile:*']
  },
  {
    id: '10',
    username: 'zhao_admin',
    nickname: '赵管理',
    role: 'enterprise',
    phone: '139****9005',
    email: 'zhaoguanli@ecotech.cn',
    organizationName: 'ECO环保科技有限公司',
    organizationId: 'org-008',
    status: 'active',
    createTime: '2026-03-15 09:00:00',
    lastLoginTime: '2026-04-19 11:20:00',
    avatar: null,
    permissions: ['training:*', 'compliance:*', 'employee:*']
  }
]

// 角色映射
export const roleMap = {
  super_admin: { text: '超级管理员', color: '#e74c3c', icon: 'M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z' },
  government: { text: '政府监管员', color: '#3498db', icon: 'M3 21h18M3 7v1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7m0 1a3 3 0 0 0 6 0V7H3l2-4h14l2 4' },
  enterprise: { text: '企业管理员', color: '#27ae60', icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z' },
  law_firm: { text: '律所管理员', color: '#9b59b6', icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5' },
  user: { text: '普通用户', color: '#95a5a6', icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }
}

// 获取角色文本
export function getRoleText(role) {
  return roleMap[role]?.text || '未知角色'
}

// 获取角色颜色
export function getRoleColor(role) {
  return roleMap[role]?.color || '#95a5a6'
}

// 获取角色图标
export function getRoleIcon(role) {
  return roleMap[role]?.icon || ''
}

// 获取用户统计
export function getUserStats() {
  const total = mockUsers.length
  const active = mockUsers.filter(u => u.status === 'active').length
  const byRole = {}
  
  mockUsers.forEach(user => {
    byRole[user.role] = (byRole[user.role] || 0) + 1
  })
  
  return {
    total,
    active,
    disabled: total - active,
    byRole,
    growth: '+12%',
    activeRate: `${Math.round((active / total) * 100)}%`
  }
}

// 根据条件过滤用户
export function filterUsers({ keyword = '', role = '', status = '', page = 1, pageSize = 10 }) {
  let filtered = [...mockUsers]
  
  if (keyword) {
    const kw = keyword.toLowerCase()
    filtered = filtered.filter(u => 
      u.username.toLowerCase().includes(kw) || 
      u.nickname.toLowerCase().includes(kw) ||
      (u.phone && u.phone.includes(keyword)) ||
      (u.email && u.email.toLowerCase().includes(kw))
    )
  }
  
  if (role) {
    filtered = filtered.filter(u => u.role === role)
  }
  
  if (status) {
    filtered = filtered.filter(u => u.status === status)
  }
  
  const total = filtered.length
  const start = (page - 1) * pageSize
  const data = filtered.slice(start, start + pageSize)
  
  return { data, total }
}

// 获取当前演示用户（用于本地展示）
export function getDemoUser() {
  return {
    id: '1',
    username: 'admin',
    nickname: '超级管理员',
    role: 'super_admin',
    phone: '138****8001',
    email: 'admin@lvfat.cn',
    organizationName: '平台总部',
    organizationId: 'org-001',
    status: 'active',
    permissions: ['*']
  }
}

// 登录模拟
export function mockLogin(username, password) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // 简单的模拟验证
      if (username === 'admin' && password === '123456') {
        resolve({
          token: 'mock-token-' + Date.now(),
          userInfo: getDemoUser()
        })
      } else if (username && password) {
        // 其他任意用户名密码组合都可以登录
        const user = mockUsers.find(u => u.username === username) || mockUsers[0]
        resolve({
          token: 'mock-token-' + Date.now(),
          userInfo: user
        })
      } else {
        reject(new Error('用户名或密码错误'))
      }
    }, 500)
  })
}

// 获取用户个人统计
export function getProfileStats(userId) {
  // 模拟统计数据
  return {
    loginCount: Math.floor(Math.random() * 100) + 20,
    taskCount: Math.floor(Math.random() * 200) + 50,
    trainingCount: Math.floor(Math.random() * 30) + 5,
    examScore: Math.floor(Math.random() * 20) + 80,
    totalHours: Math.floor(Math.random() * 100) + 20
  }
}
