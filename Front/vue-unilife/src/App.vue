<template>
  <HeaderBar/>
  <router-view/>
</template>

<script setup lang="ts">
import HeaderBar from './components/HeaderBar.vue'
</script>

<style>

/* 设置 body 背景渐变，清除异常布局设置 */
body{
        height: 100%;
        width:100%;
    
        /*弹性布局，水平垂直居中*/
        justify-content: center;
        align-items: center;
        /*渐变背景*/
        background: linear-gradient(200deg, #f3e7e9, #e3eeff);
}
</style>
