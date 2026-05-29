<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">个人中心</h2>
        <p class="page-sub">管理您的个人信息与账号设置</p>
      </div>
      <el-tag type="success" size="large">
        <span style="display: flex; align-items: center; gap: 4px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 14px; height: 14px;">
            <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z"/>
            <path d="M12 8v4M12 16h.01"/>
          </svg>
          虚拟演示模式
        </span>
      </el-tag>
    </div>

    <div class="profile-grid">
      <!-- 左侧：个人信息卡片 -->
      <div class="glass-card card-avatar">
        <div class="demo-badge">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
          演示账号
        </div>
        <div class="avatar-wrap">
          <el-avatar :size="96" class="user-avatar" :style="getAvatarStyle(userInfo.role)">
            {{ (userInfo.nickname || 'U').slice(0, 1) }}
          </el-avatar>
          <div class="avatar-info">
            <h3 class="user-name">{{ userInfo.nickname || '用户' }}</h3>
            <p class="user-role">{{ getRoleName(userInfo.role) }}</p>
            <el-tag class="org-tag" size="small" :style="getOrgTagStyle(userInfo.role)">
              {{ userInfo.organizationName || '未分配组织' }}
            </el-tag>
          </div>
        </div>
        <div class="profile-stats">
          <div class="stat-item">
            <span class="stat-num">{{ profileStats.loginCount }}</span>
            <span class="stat-lbl">登录次数</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">{{ profileStats.taskCount }}</span>
            <span class="stat-lbl">完成任务</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">{{ profileStats.trainingCount }}</span>
            <span class="stat-lbl">参与培训</span>
          </div>
        </div>
        <div class="profile-meta">
          <div class="meta-item">
            <span class="meta-label">用户ID</span>
            <span class="meta-value">{{ userInfo.id }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">注册时间</span>
            <span class="meta-value">2026-01-15</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">上次登录</span>
            <span class="meta-value">{{ new Date().toLocaleDateString() }}</span>
          </div>
        </div>
        <el-button type="primary" class="logout-btn" @click="handleLogout">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          退出登录
        </el-button>
      </div>

      <!-- 右侧：设置面板 -->
      <div class="profile-right">
        <!-- 基本信息 -->
        <div class="glass-card card-form">
          <div class="card-title-bar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            基本信息
          </div>
          <el-form :model="profileForm" label-position="top" class="profile-form">
            <div class="form-row-2">
              <el-form-item label="用户名">
                <el-input :model-value="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
            </div>
            <div class="form-row-2">
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
            </div>
            <div class="form-row-2">
              <el-form-item label="所属组织">
                <el-input :model-value="userInfo.organizationName || '未分配'" disabled />
              </el-form-item>
              <el-form-item label="角色">
                <el-input :model-value="getRoleName(userInfo.role)" disabled />
              </el-form-item>
            </div>
            <el-form-item label="个人简介">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." maxlength="200" show-word-limit />
            </el-form-item>
            <el-button type="primary" @click="handleSaveProfile" :loading="saving">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                <polyline points="17 21 17 13 7 13 7 21"/>
                <polyline points="7 3 7 8 15 8"/>
              </svg>
              保存修改
            </el-button>
          </el-form>
        </div>

        <!-- 安全设置 -->
        <div class="glass-card card-form">
          <div class="card-title-bar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            安全设置
          </div>
          <div class="security-list">
            <div class="security-item" @click="showPwdDialog = true">
              <div class="sec-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </div>
              <div class="sec-info">
                <span class="sec-title">登录密码</span>
                <span class="sec-desc">定期更换密码，保障账号安全</span>
              </div>
              <svg class="sec-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
            <div class="security-item" @click="showPhoneDialog = true">
              <div class="sec-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2"/>
                  <line x1="12" y1="18" x2="12.01" y2="18"/>
                </svg>
              </div>
              <div class="sec-info">
                <span class="sec-title">手机绑定</span>
                <span class="sec-desc">已绑定：{{ userInfo.phone || '未绑定' }}</span>
              </div>
              <svg class="sec-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
            <div class="security-item">
              <div class="sec-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                </svg>
              </div>
              <div class="sec-info">
                <span class="sec-title">双重验证</span>
                <span class="sec-desc">登录时需要手机验证码二次确认</span>
              </div>
              <el-switch v-model="securityForm.twoFactor" active-color="#52b788" />
            </div>
          </div>
        </div>

        <!-- 通知设置 -->
        <div class="glass-card card-form">
          <div class="card-title-bar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            通知偏好
          </div>
          <div class="notify-list">
            <div class="notify-item" v-for="n in notifySettings" :key="n.key">
              <div class="notify-info">
                <span class="notify-title">{{ n.title }}</span>
                <span class="notify-desc">{{ n.desc }}</span>
              </div>
              <el-switch v-model="n.enabled" active-color="#52b788" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="480px" class="glass-dialog">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-position="top">
        <el-form-item label="当前密码" prop="oldPwd">
          <el-input v-model="pwdForm.oldPwd" type="password" placeholder="请输入当前密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="pwdForm.newPwd" type="password" placeholder="8位以上，含大小写字母和数字" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input v-model="pwdForm.confirmPwd" type="password" placeholder="再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePwd">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 修改手机弹窗 -->
    <el-dialog v-model="showPhoneDialog" title="更换手机号" width="480px" class="glass-dialog">
      <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef" label-position="top">
        <el-form-item label="新手机号" prop="phone">
          <el-input v-model="phoneForm.phone" placeholder="请输入新手机号" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input-wrap">
            <el-input v-model="phoneForm.code" placeholder="请输入验证码" />
            <el-button class="code-btn" :disabled="codeCountdown > 0" @click="handleSendCode">
              {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPhoneDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePhone">确认更换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, setUserInfo, logout as authLogout } from '@/utils/auth.js'
import { getDemoUser, getRoleText, getProfileStats } from '@/utils/mockUser.js'

const router = useRouter()

const saving = ref(false)
const showPwdDialog = ref(false)
const showPhoneDialog = ref(false)
const codeCountdown = ref(0)
const pwdFormRef = ref()
const phoneFormRef = ref()

// 使用虚拟用户数据
const userInfo = computed(() => {
  const info = getUserInfo()
  if (!info || !info.id) {
    return getDemoUser()
  }
  return info
})

// 虚拟统计数据
const profileStats = reactive(getProfileStats(userInfo.value.id))

const profileForm = reactive({
  nickname: userInfo.value.nickname || '',
  phone: userInfo.value.phone || '',
  email: userInfo.value.email || '',
  bio: '环保法律爱好者，致力于推动绿色法治建设'
})

const securityForm = reactive({ twoFactor: false })

const notifySettings = reactive([
  { key: 'task', title: '任务通知', desc: '任务派发、到期提醒', enabled: true },
  { key: 'system', title: '系统公告', desc: '系统更新、功能上线通知', enabled: true },
  { key: 'training', title: '培训通知', desc: '培训报名、开课提醒', enabled: true },
  { key: 'report', title: '举报处理', desc: '线索举报处理结果通知', enabled: false }
])

const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })
const phoneForm = reactive({ phone: '', code: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPwd) callback(new Error('两次密码不一致'))
  else callback()
}

const pwdRules = {
  oldPwd: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPwd: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 8, message: '密码至少8位', trigger: 'blur' }],
  confirmPwd: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const phoneRules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 使用虚拟数据的角色名称
const roleMap = {
  super_admin: '超级管理员', government: '政府监管员', enterprise: '企业管理员',
  law_firm: '律所管理员', user: '普通用户'
}

const getRoleName = (role) => getRoleText(role) || roleMap[role] || '用户'

// 根据角色获取头像样式
const getAvatarStyle = (role) => {
  const colors = {
    super_admin: 'linear-gradient(135deg, #e74c3c, #c0392b)',
    government: 'linear-gradient(135deg, #3498db, #2980b9)',
    enterprise: 'linear-gradient(135deg, #27ae60, #229954)',
    law_firm: 'linear-gradient(135deg, #9b59b6, #8e44ad)',
    user: 'linear-gradient(135deg, #95a5a6, #7f8c8d)'
  }
  return { background: colors[role] || colors.user }
}

// 根据角色获取组织标签样式
const getOrgTagStyle = (role) => {
  const colors = {
    super_admin: 'background: rgba(231,76,60,0.1) !important; border-color: rgba(231,76,60,0.2) !important; color: #e74c3c !important;',
    government: 'background: rgba(52,152,219,0.1) !important; border-color: rgba(52,152,219,0.2) !important; color: #3498db !important;',
    enterprise: 'background: rgba(39,174,96,0.1) !important; border-color: rgba(39,174,96,0.2) !important; color: #27ae60 !important;',
    law_firm: 'background: rgba(155,89,182,0.1) !important; border-color: rgba(155,89,182,0.2) !important; color: #9b59b6 !important;',
    user: 'background: rgba(149,165,166,0.1) !important; border-color: rgba(149,165,166,0.2) !important; color: #95a5a6 !important;'
  }
  return colors[role] || colors.user
}

const handleSaveProfile = async () => {
  saving.value = true
  await new Promise(r => setTimeout(r, 800))
  ElMessage.success('个人信息已保存')
  saving.value = false
}

const handleChangePwd = async () => {
  const v = await pwdFormRef.value?.validate().catch(() => false)
  if (!v) return
  ElMessage.success('密码修改成功')
  showPwdDialog.value = false
}

const handleChangePhone = async () => {
  const v = await phoneFormRef.value?.validate().catch(() => false)
  if (!v) return
  ElMessage.success('手机号已更换')
  showPhoneDialog.value = false
}

const handleSendCode = () => {
  if (!phoneForm.phone || !/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  ElMessage.success('验证码已发送')
  codeCountdown.value = 60
  const t = setInterval(() => { codeCountdown.value--; if (codeCountdown.value <= 0) clearInterval(t) }, 1000)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    localStorage.clear()
    router.push('/login')
  } catch {}
}
</script>

<style lang="scss" scoped>
.profile-page { max-width: 1200px; margin: 0 auto; }

.page-header {
  margin-bottom: 24px;
  .page-title { margin: 0 0 4px; font-size: 24px; font-weight: 700; color: #1a4d2e; }
  .page-sub { margin: 0; font-size: 13px; color: #9ca3af; }
}

.profile-grid {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  align-items: flex-start;
}

.glass-card {
  border-radius: 20px; background: rgba(255,255,255,0.72); backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5); box-shadow: 0 4px 24px rgba(82,183,136,0.06); padding: 24px;
}

.demo-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(82,183,136,0.15), rgba(45,106,79,0.1));
  border: 1px solid rgba(82,183,136,0.25);
  font-size: 12px;
  color: #2d6a4f;
  font-weight: 600;
  margin-bottom: 16px;
  
  svg {
    width: 14px;
    height: 14px;
  }
}

.card-avatar {
  display: flex; flex-direction: column; gap: 20px;
}

.avatar-wrap { display: flex; align-items: center; gap: 16px; }

.user-avatar {
  color: #fff !important;
  font-size: 32px; font-weight: 700; flex-shrink: 0;
}

.user-name { margin: 0 0 4px; font-size: 18px; font-weight: 700; color: #1a4d2e; }
.user-role { margin: 0 0 8px; font-size: 13px; color: #9ca3af; }

.org-tag {
  background: rgba(82,183,136,0.1) !important; border-color: rgba(82,183,136,0.2) !important;
  color: #52b788 !important;
}

.profile-stats {
  display: flex; align-items: center; justify-content: space-around;
  padding: 16px 0; border-top: 1px solid rgba(82,183,136,0.1); border-bottom: 1px solid rgba(82,183,136,0.1);
}

.stat-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.stat-num { font-size: 20px; font-weight: 800; color: #1a4d2e; }
.stat-lbl { font-size: 12px; color: #9ca3af; }
.stat-divider { width: 1px; height: 30px; background: rgba(82,183,136,0.1); }

.profile-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: rgba(82,183,136,0.04);
  border-radius: 10px;
  border: 1px dashed rgba(82,183,136,0.15);
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.meta-label {
  color: #9ca3af;
}

.meta-value {
  color: #374151;
  font-weight: 500;
  font-family: 'SF Mono', 'Consolas', monospace;
}

.logout-btn {
  width: 100%; display: flex; align-items: center; justify-content: center; gap: 8px;
  background: rgba(239,68,68,0.06) !important; border-color: rgba(239,68,68,0.2) !important; color: #ef4444 !important;
  &:hover { background: rgba(239,68,68,0.1) !important; }
  svg { width: 16px; height: 16px; }
}

.profile-right { display: flex; flex-direction: column; gap: 20px; }

.card-title-bar {
  display: flex; align-items: center; gap: 10px; margin-bottom: 20px;
  font-size: 15px; font-weight: 600; color: #1a4d2e; padding-bottom: 14px;
  border-bottom: 1px solid rgba(82,183,136,0.1);
  svg { width: 18px; height: 18px; color: #52b788; }
}

.profile-form {
  .el-button {
    display: flex; align-items: center; gap: 6px; margin-top: 8px;
    svg { width: 15px; height: 15px; }
  }
}

.form-row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }

.security-list { display: flex; flex-direction: column; gap: 2px; }

.security-item {
  display: flex; align-items: center; gap: 14px; padding: 14px 16px;
  border-radius: 12px; cursor: pointer; transition: all 0.2s;
  &:hover { background: rgba(82,183,136,0.04); }
}

.sec-icon {
  width: 40px; height: 40px; border-radius: 10px;
  background: rgba(82,183,136,0.1); border: 1px solid rgba(82,183,136,0.15);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0; color: #52b788;
  svg { width: 18px; height: 18px; }
}

.sec-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 3px; }
.sec-title { font-size: 14px; font-weight: 600; color: #1a4d2e; }
.sec-desc { font-size: 12px; color: #9ca3af; }

.sec-arrow { width: 16px; height: 16px; color: #9ca3af; flex-shrink: 0; }

.notify-list { display: flex; flex-direction: column; gap: 4px; }

.notify-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; border-radius: 12px; transition: all 0.2s;
  &:hover { background: rgba(82,183,136,0.04); }
}

.notify-info { display: flex; flex-direction: column; gap: 2px; }
.notify-title { font-size: 14px; font-weight: 500; color: #374151; }
.notify-desc { font-size: 12px; color: #9ca3af; }

.code-input-wrap { display: flex; gap: 10px; }
.code-btn {
  flex-shrink: 0; padding: 0 14px !important; border-radius: 10px !important;
  background: rgba(82,183,136,0.08) !important; border-color: rgba(82,183,136,0.2) !important;
  color: #52b788 !important; font-size: 13px !important;
  &:hover:not(:disabled) { background: rgba(82,183,136,0.15) !important; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

:deep(.glass-dialog) {
  .el-dialog {
    border-radius: 20px !important; background: rgba(255,255,255,0.95) !important;
    backdrop-filter: blur(20px) !important; border: 1px solid rgba(82,183,136,0.2) !important;
  }
  .el-dialog__header { border-bottom: 1px solid rgba(82,183,136,0.1); padding: 20px 24px !important; }
  .el-dialog__title { font-weight: 700; color: #1a4d2e; font-size: 16px; }
  .el-dialog__body { padding: 24px !important; }
  .el-dialog__footer { border-top: 1px solid rgba(82,183,136,0.1); padding: 16px 24px !important; }
}

@media (max-width: 900px) { .profile-grid { grid-template-columns: 1fr; } }
</style>
