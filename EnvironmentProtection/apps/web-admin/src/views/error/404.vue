<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;

  .content {
    text-align: center;

    h1 {
      font-size: 120px;
      color: #10B981;
      margin: 0;
      line-height: 1;
    }

    p {
      font-size: 20px;
      color: #666;
      margin: 20px 0 40px;
    }
  }
}
</style>