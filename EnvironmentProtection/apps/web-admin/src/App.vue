<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

/* Element Plus 主色 - 蓝色系专业主题 */
html {
  --el-color-primary: #2563EB;
  --el-color-primary-light-3: #60A5FA;
  --el-color-primary-light-5: #93C5FD;
  --el-color-primary-light-7: #BFDBFE;
  --el-color-primary-light-8: #DBEAFE;
  --el-color-primary-light-9: #EFF6FF;
  --el-color-primary-dark-2: #1D4ED8;
  --el-font-family: 'Inter', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, 'Microsoft YaHei', sans-serif;
}
</style>