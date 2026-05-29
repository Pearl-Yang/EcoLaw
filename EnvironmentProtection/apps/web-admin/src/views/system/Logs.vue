<template>
  <div class="logs-container">
    <el-card class="panel-card">
      <template #header>
        <div class="card-header-row">
          <span class="panel-title">操作日志</span>
          <el-button type="success" size="small"><el-icon><Download /></el-icon> 导出日志</el-button>
        </div>
      </template>

      <el-form :inline="true" class="query-form">
        <el-form-item label="关键词"><el-input v-model="q.keyword" placeholder="操作人/操作内容" clearable /></el-form-item>
        <el-form-item label="操作模块">
          <el-select v-model="q.module" clearable style="width:130px">
            <el-option label="用户管理" value="user" />
            <el-option label="任务管理" value="task" />
            <el-option label="培训管理" value="training" />
            <el-option label="系统配置" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="q.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width:240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small">查询</el-button>
          <el-button size="small">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="createTime" label="操作时间" width="170" />
        <el-table-column prop="user" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }"><el-tag size="small" type="primary">{{ moduleText[row.module] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="action" label="操作类型" width="120" />
        <el-table-column prop="detail" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP 地址" width="140" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }"><el-button type="primary" link size="small">详情</el-button></template>
        </el-table-column>
      </el-table>

      <div class="pagination-row">
        <el-pagination v-model:current-page="q.page" v-model:page-size="q.pageSize" :total="total"
          :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next"
          @size-change="load" @current-change="load" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Download } from '@element-plus/icons-vue'

const loading = ref(false)
const total = ref(5)
const q = reactive({ page: 1, pageSize: 10, keyword: '', module: '', dateRange: [] })
const moduleText = { user: '用户管理', task: '任务管理', training: '培训管理', system: '系统配置' }

const list = reactive([
  { id: 1, createTime: '2026-04-04 10:30:25', user: '管理员', module: 'user', action: '新增用户', detail: '新增用户「李四」，角色：政府监管员', ip: '192.168.1.100' },
  { id: 2, createTime: '2026-04-04 09:15:10', user: '管理员', module: 'task', action: '创建任务', detail: '创建普法任务「农村农膜回收宣传」', ip: '192.168.1.100' },
  { id: 3, createTime: '2026-04-03 16:40:33', user: '陈律师', module: 'training', action: '上传素材', detail: '上传案例「塑料厂违规倾倒固废行政处罚案」', ip: '10.0.0.58' },
  { id: 4, createTime: '2026-04-03 14:22:08', user: '管理员', module: 'system', action: '系统配置', detail: '修改 AI 服务超时配置：30s → 60s', ip: '192.168.1.100' },
  { id: 5, createTime: '2026-04-02 11:05:42', user: '管理员', module: 'user', action: '重置密码', detail: '重置用户「王五」密码', ip: '192.168.1.100' }
])

const load = () => { /* TODO */ }
</script>

<style lang="scss" scoped>
.panel-card { border-radius: 14px; }
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.panel-title { font-weight: 600; color: #1a4d2e; font-size: 15px; }
.query-form { margin-bottom: 16px; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
