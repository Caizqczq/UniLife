<script setup lang="ts">
const images = [
  new URL('@/assets/logo-carousel/1.jpeg', import.meta.url).href,
  new URL('@/assets/logo-carousel/2.png', import.meta.url).href,
  new URL('@/assets/logo-carousel/3.jpg', import.meta.url).href
]
</script>

<template>
  <el-carousel :interval="3000" height="300px" arrow="hover">
    <el-carousel-item v-for="(img, i) in images" :key="i">
      <img :src="img" class="carousel-img" />
    </el-carousel-item>
  </el-carousel>
</template>


<style scoped>
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}
</style>
