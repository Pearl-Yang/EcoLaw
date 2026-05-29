<template>
  <div class="roles-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
    </div>

    <!-- 页头 -->
    <div class="page-header">
      <div class="header-title">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
        <h1>角色权限配置</h1>
      </div>
      <el-button type="primary" size="default" class="btn-primary">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M5 12h14"/>
          <path d="M12 5v14"/>
        </svg>
        新增角色
      </el-button>
    </div>

    <!-- 表格卡片 -->
    <div class="glass-card panel-card">
      <el-table :data="list" stripe class="custom-table">
        <el-table-column prop="name" label="角色名称" width="140">
          <template #default="{ row }">
            <div class="role-name">
              <div class="role-avatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="角色标识" width="160">
          <template #default="{ row }">
            <code class="role-code">{{ row.code }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="描述" min-width="200" />
        <el-table-column prop="userCount" label="用户数" width="100" align="center">
          <template #default="{ row }">
            <span class="user-count">{{ row.userCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'active' ? 'success' : 'info'" class="status-tag">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" link size="small" class="action-btn edit">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/>
                  <path d="m15 5 4 4"/>
                </svg>
                编辑
              </el-button>
              <el-button type="warning" link size="small" class="action-btn permission">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                权限配置
              </el-button>
              <el-button type="danger" link size="small" class="action-btn delete">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M3 6h18"/>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                  <line x1="10" x2="10" y1="11" y2="17"/>
                  <line x1="14" x2="14" y1="11" y2="17"/>
                </svg>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const list = reactive([
  { id: 1, name: '超级管理员', code: 'super_admin', desc: '系统最高权限，可管理所有模块', userCount: 2, status: 'active' },
  { id: 2, name: '政府监管员', code: 'government', desc: '县/区级司法局、生态环境局使用', userCount: 8, status: 'active' },
  { id: 3, name: '企业管理员', code: 'enterprise', desc: '企业管理合规培训与员工', userCount: 15, status: 'active' },
  { id: 4, name: '律所管理员', code: 'law_firm', desc: '律所案例管理与客户对接', userCount: 6, status: 'active' },
  { id: 5, name: '普通用户', code: 'user', desc: '基层群众，可参与学习与举报', userCount: 1234, status: 'active' }
])
</script>

<style lang="scss" scoped>
.roles-container {
  padding: 24px;
  min-height: 100vh;
  background: #f0f4f0;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(82,183,136,0.08), rgba(149,213,178,0.04));
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  bottom: 5%;
  left: -80px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;

  .header-icon {
    width: 40px;
    height: 40px;
    color: #52b788;
  }

  h1 {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
    color: #1a4d2e;
    letter-spacing: -0.5px;
  }
}

.btn-primary {
  display: flex !important;
  align-items: center !important;
  gap: 8px;
  background: linear-gradient(135deg, #52b788, #2d6a4f) !important;
  border: none !important;
  color: #fff !important;
  padding: 12px 24px !important;
  border-radius: 12px !important;
  font-weight: 500;
  transition: all 0.3s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(82,183,136,0.3);
  }
}

.glass-card {
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(82,183,136,0.06);
  padding: 24px;
  position: relative;
  z-index: 1;
}

.panel-card {
  padding: 28px;
}

.custom-table {
  :deep(.el-table__header-wrapper) {
    th {
      background: rgba(82,183,136,0.06) !important;
      color: #1a4d2e;
      font-weight: 600;
      font-size: 14px;
      border-bottom: 1px solid rgba(82,183,136,0.15) !important;
      padding: 16px 12px !important;
    }
  }

  :deep(.el-table__body-wrapper) {
    tr {
      &:hover > td {
        background: rgba(82,183,136,0.04) !important;
      }

      td {
        padding: 16px 12px !important;
        border-bottom: 1px solid rgba(82,183,136,0.08) !important;
      }
    }
  }
}

.role-name {
  display: flex;
  align-items: center;
  gap: 10px;

  .role-avatar {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(82,183,136,0.1);
    border-radius: 10px;

    svg {
      width: 18px;
      height: 18px;
      color: #52b788;
    }
  }

  span {
    font-weight: 500;
    color: #1a4d2e;
  }
}

.role-code {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 12px;
  padding: 4px 8px;
  background: rgba(82,183,136,0.08);
  border-radius: 6px;
  color: #2d6a4f;
}

.user-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  padding: 4px 10px;
  background: rgba(82,183,136,0.08);
  border-radius: 8px;
  font-weight: 500;
  color: #2d6a4f;
  font-size: 13px;
}

.status-tag {
  border-radius: 8px !important;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-btn {
  display: flex !important;
  align-items: center !important;
  gap: 4px;
  font-size: 13px;
  padding: 6px 10px !important;
  border-radius: 8px !important;
  transition: all 0.2s ease;

  svg {
    width: 14px;
    height: 14px;
  }

  &.edit {
    color: #52b788 !important;

    &:hover {
      background: rgba(82,183,136,0.1) !important;
    }
  }

  &.permission {
    color: #f59e0b !important;

    &:hover {
      background: rgba(245,158,11,0.1) !important;
    }
  }

  &.delete {
    color: #ef4444 !important;

    &:hover {
      background: rgba(239,68,68,0.1) !important;
    }
  }
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: rgba(82,183,136,0.02);
}
</style>
