<script set lang="ts">
import { defineComponent, ref } from 'vue';

export default defineComponent({
  name: 'Manager',
  setup() {
    const email = ref('test@example.com');
    const username = ref('测试员');
    return{
      email,
      username
    }
  }
});
</script>

<template>
    <div class = "box">
      <el-container class = "box-container">
        <el-main class = "profile">
          <el-container class = "personal">
            <el-header>
              <h2>个人信息</h2>
            </el-header>
            <el-main>
              <ul>
                <li><label>昵称</label>
                  <input v-model = "username">
                </li>
                <li><label>昵称</label>
                  <input v-model = "username">
                </li>
                <li>
                  <input type = "checkbox">
                </li>
              </ul>
            </el-main>
          </el-container>
        </el-main>
        <el-aside class = "layout">
          <el-avatar src = "../../../public/images/默认头像.jpg"
                     size = "large"
                     fit = 'fill'/>
          <label class = "showemail">{{ email }}</label>
          <el-input
                    class = "self-introduce"
                    maxlength = "30"
                    placeholder="这是一个个人简介"
                    size = "large"
                    type = "textarea"/>
        </el-aside>
      </el-container>
    </div>
</template>

<style scoped>
  .box
  {
    position:absolute;  
    background-color:transparent;
    left:0;
    top:2%;
    width:100%;
    height: 96%;
  }

  .box-container
  {
    position:relative;
    width:94%;
    left:104px;
    right:20px;
    height:100%;
    gap:20px;
  }

  .profile,
  .layout
  {
    text-align:center;
    border-radius:20px;
    box-shadow:0px 4px 10px rgba(0,0,0,0.1);
  }

  /*密码，昵称，性别，生日修改区域 */
  .profile
  {
    background-color: #fff;
    padding-left:70px;
    padding-top:50px;
    display:flex;
    align-items:right;
  }

  .profile .personal h2
  {
    display:flex;
    align-items:right;
  }

  .profile ul
  {
    list-style: none;
    display:block;
    align-items:right;
    margin:0;
  }

  .profile li
  {
    display:flex;
    align-self:right;
    font-size:22px;
    padding-top:20px;
  }

  .profile label
  {
    padding-right:20px;
  }

  .profile input
  {
    background-color: transparent;
    width:200px;
    height:30px;
    color:black;
    /* 下边框样式 */
    border-bottom:1px solid black;
    padding-left:10px;
    text-indent:10px;
    font-size:19px;
    letter-spacing:2px;
  }

  .profile input::placeholder
  {
    font-size:20px;
  }


  /*头像和个人简介修改区域 */
  .layout
  {
    background-color: #ead1fb;
    width:30%;
    height:100%;
    padding-top:150px;
    padding-left:80px;
    padding-right:80px;
    align-items: center;
  }

  .showemail
  {
    position:relative;
    top:20px;
    font-size:24px;
  }

  .self-introduce:deep(.el-textarea__inner)
  {
    border:2px solid black;
  }

  .self-introduce
  {
    position: relative;
    top:50px;
    left:10px;
    width:400px;
    min-height:90px;
    font-size:22px;
    align-items: center;
  }


  .el-avatar
  {
    width:200px;
    height:200px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  }

  .layout-description
  {
    width:100%;
  }

</style>