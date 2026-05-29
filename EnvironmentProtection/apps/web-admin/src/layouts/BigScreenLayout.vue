<template>
  <div class="big-screen-layout">
    <router-view />
  </div>
</template>

<script setup>
defineOptions({ name: 'BigScreenLayout' })
</script>

<style scoped>
.big-screen-layout {
  width: 100%;
  min-height: 100vh;
  margin: 0;
  overflow: auto;
  background: #020814;
}
</style>
