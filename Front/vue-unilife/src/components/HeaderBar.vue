<script setup>
  import { House, Cloudy, User, Cpu, Message, HomeFilled, MessageBox, Calendar } from '@element-plus/icons-vue'
  import {useRoute} from 'vue-router'

  const route = useRoute();

</script>

<template>
    <header class ="header-bar" >
      <!-- 左侧图标组 -->
      <div class="left-icons">
        <router-link to="/unilifeHome" class="icon-btn" title="首页">
          <el-icon  class="icon-btn" :size="24">
            <HomeFilled />
          </el-icon>
        </router-link>
        <router-link to="/cloud" class="icon-btn" title="资料分享">
          <el-icon  class="icon-btn" :size="24">
              <MessageBox />
          </el-icon>
        </router-link>
        <router-link to="/self" class="icon-btn" title="日程">
          <el-icon class = "icon-btn" :size="24">
            <Calendar />
          </el-icon>
        </router-link>
        <router-link to="/assistant" class="icon-btn" title="AI助手">
            <el-icon class = "icon-btn" :size="24">
              <Cpu />
            </el-icon>
        </router-link>
      </div>
  
      <!-- 右侧部分 -->
      <div class="right-section">
        <router-link to="/message" class="icon-btn" title="消息">
          <Message size="24" />
        </router-link>
        <router-link to="/personal" class="user-entry" title="个人主页">
          <span>个人主页</span>
        </router-link>
        <router-link to="/log" class="icon-btn" title="登录">
          <el-icon class = "icon-btn" :size="24">
            <User />
          </el-icon>
        </router-link>
      </div>
    </header>
  </template>
  
  
  
  <style scoped>
  .header-bar {
    height: 70px;
    width: 100%;
    background: #ead1fb;
    position:fixed;
    top: 0;
    left:0;
    padding:0;
    margin:0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    
    z-index: 10000;
  }

.header-bar--personal {
  background: linear-gradient(to top, #c9e4ff, #fad0c4);
}
  
  .left-icons,
  .right-section {
    padding:50px;
    display: flex;
    align-items: center;
  }
  
  .icon-btn {
    margin: 0 10px;
    color: #606266;
    cursor: pointer;
    transition: transform 0.2s;
  }
  
  .icon-btn:hover {
    transform: scale(1.1);
    color: #409EFF;
  }
  
  .user-entry {
    margin-left: 12px;
    font-weight: 600;
    color: #303133;
    cursor: pointer;
  }
  </style>
