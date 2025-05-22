<template>
  <div class="post-sidebar">
    <h2>分类</h2>
    <ul class="top-tags">
      <li v-for="tag in tags" :key="tag" @click="goTo(tag)">
        {{ tag }}
      </li>
    </ul>

    <el-divider />
    
    <div class="sub-menu">
      <div v-for="item in subs" :key="item" @click="goTo(item)">{{ item }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
const tags = ['计算机学院', '遥感学院', '电子信息学院']
const subs = ['综合', '最新', '热度最高', '用户']

//临时跳转函数
function goTo(tag: string) {
  // 临时跳转 NotFound
  window.location.href = '/not-found'
}
</script>

<style scoped lang="scss">
.post-sidebar {
  font-size: 16px;

  .top-tags {
    margin-top: 8px;
    padding: 0;
    li {
      cursor: pointer;
      margin: 6px 0;
      list-style-type: none;
      padding: 0;
      &:hover {
       font-size:20px;
       color:#8a63d2;
      }
    }
  }

  .sub-menu {
    margin-top: 24px;
    div {
      margin: 6px 0;
      cursor: pointer;
      &:hover {
       font-size:20px;
       color:#8a63d2;
      }
    }
  }
}
</style>
